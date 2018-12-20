package com.daoben.rfid.reader;

import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import com.daoben.rfid.common.push.JPushManager;
import com.daoben.rfid.model.AssetDeviceInfo;
import com.daoben.rfid.service.AssetDeviceInfoService;
import com.daoben.rfid.service.ReadeIoTimeService;

/**
 * @author Administrator
 */
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration({ "/applicationContext.xml" })
@Service
public class ConnReadermpl implements ApplicationContextAware {

	@Resource
	private AssetDeviceInfoService aInfoService;

	@Resource
	private ReaderFunction readerFunction;

	@Resource
	private ReadeIoTimeService readeIoTimeService;

	@Resource
	private TcpRfidTag tcpRfidTag;

	@Resource
	private NewRfidTag newRfidTag;

	static int flagStart = 0;

	RFIDinterface rf = RFIDinterface.getInstance();// 实例化dll文件

	private Logger log = Logger.getLogger(this.getClass());

	private volatile Thread Reader;// 盘点读写器
	private volatile Thread ReaderOut;// 出库读写器
	private volatile Thread ReaderIn;// 出库读写器
	private volatile Thread MonitorWarnA;// 监控报警信息
	private volatile Thread MonitorWarnB;// 监控报警信息
	private volatile Thread MonitorRfidOutIn;// 监视盘点

	/**
	 * 读取RFID标签休眠时间调整
	 */
	public void delay_ms(int time) {
		try {
			Thread.sleep(time);// ms
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询RFID设备所有信息
	 */
	@Test
	public void selectAllDeviceInfo() {
		List<AssetDeviceInfo> list = aInfoService.selectAllDeviceInfo();
		for (AssetDeviceInfo e : list) {
			System.out.println(e.toString());
		}
	}

	/**
	 * 查询RFID设备，一次性连接所有读写器
	 */
	@Test
	public void AllReaderConn() {
		List<AssetDeviceInfo> list = aInfoService.selectAllDeviceInfo();
		System.out.println(list);
		for (AssetDeviceInfo e : list) {
			switch (e.getRfid_Ip()) {// 通过IP获取相应的连接
			case "192.168.0.215":// 盘点网线
				ChoiceThread(e.getRfid_Ip(), e.getDevice_Port(), e.getDevice_State(), e.getDevice_Purpose());
				break;
			// case "192.168.0.217":// 盘点wifi
			// ChoiceThread(e.getRfid_Ip(), e.getDevice_Port(),
			// e.getDevice_State(), e.getDevice_Purpose());
			// break;
			// case "192.168.0.216"://出库网线
			// ChoiceThread(e.getDevice_State(), e.getRfid_Ip(),
			// e.getDevice_Port(), e.getDevice_Purpose());
			// break;
			case "192.168.0.218":// 出库wifi
				ChoiceThreadOut(e.getRfid_Ip(), e.getDevice_Port(), e.getDevice_State(), e.getDevice_Purpose());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 查询RFID设备，并且选择连接
	 */
	public void OptionReaderConn(final String ip, final String port, int state, String purpose) throws Exception {
		switch (ip) {// 通过IP获取相应的连接
		case "192.168.0.215":// 网线盘点
			ChoiceThread(ip, port, state, purpose);
			break;
		case "192.168.0.216":// 网线出库
			ChoiceThreadOut(ip, port, state, purpose);
			break;
		case "192.168.0.217":// wifi盘点
			ChoiceThread(ip, port, state, purpose);
			break;
		case "192.168.0.218":// wifi出库
			ChoiceThreadOut(ip, port, state, purpose);
			break;
		default:
			break;
		}
	}

	public void getRfidInOut(int serverPort) {
		MonitorWarnA = new Thread(new Runnable() {
			int count = 0;

			@Override
			public void run() {
				log.info("TCP启动进出库线程");// run的方法体代表要执行的任务
				while (true) {
					tcpRfidTag.getRfidInOut(serverPort);// 8082
					delay_ms(10000);// 10s休眠时间
				}
			}
		});
		if (MonitorWarnA.isAlive() == false) {// 判断线程是否启动
			MonitorWarnA.setDaemon(true);//
			MonitorWarnA.start();//
		}

	}

	public void getMonitorRfidOutIn() {
		MonitorRfidOutIn = new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("TCP启动进出库线程");// run的方法体代表要执行的任务
				while (true) {
					tcpRfidTag.updateRfidWarnInfo();// 监视盘点所有信息
					delay_ms(10000);// 10s休眠时间
				}
			}
		});
		if (MonitorRfidOutIn.isAlive() == false) {// 判断线程是否启动
			MonitorRfidOutIn.setDaemon(true);//
			MonitorRfidOutIn.start();//
		}

	}

	public void getRfidMonitor(int serverPort) {
		MonitorWarnB = new Thread(new Runnable() {
			int count = 0;

			@Override
			public void run() {
				log.info("TCP启动盘点线程");// run的方法体代表要执行的任务
				while (true) {
					// tcpRfidTag.getRfidMonitor(serverPort);// 8083
					tcpRfidTag.getRfidMonitor(serverPort);// 8083
					delay_ms(10000);// 10s休眠时间
				}
			}
		});
		if (MonitorWarnB.isAlive() == false) {// 判断线程是否启动
			MonitorWarnB.setDaemon(true);//
			MonitorWarnB.start();//
		}

	}

	/**
	 * @author wxp
	 *
	 */
	public void ChoiceThread(final String ip, final String port, final int state, final String purpose) {
		boolean flag = false;// 判断线程是否打开
		if (state == 1) {
			flag = true;
			Reader = new Thread(new Runnable() {// run的方法体代表要执行的任务
				@Override
				public void run() {// 线程体
					System.out.println("打开盘点线程");
					int hcon = rf.OpenReaderExe(ip, port);// 打开读写器返回句柄
					try {
						AssetDeviceInfo aInfo = new AssetDeviceInfo();
						aInfo.setRfid_Ip(ip);
						aInfo.setDevice_State(state);// 默认打开读写器
						aInfo.setDevice_Handle(hcon);
						aInfoService.updatePartDeviceState(aInfo);// 更新读写器

					} catch (Exception e) {
						System.out.println("盘点读写器更新失败");
					}

					while (true) {
						if (hcon != 0) {
							readerFunction.ReaderTagId(hcon);// 读取出库读写器标签
						} else {
							// readerFunction.MonitorTagIdLose();// 查询丢失报警
							System.out.println("盘点读写器打开失败");
						}
						delay_ms(900);// 休眠时间
					}
				}
			});
			if (Reader.isAlive() == false) {// 判断线程是否启动
				Reader.setDaemon(true);//
				Reader.start();// 启动线程
			}
		} else if (state == 0) {
			if (flag) {
				try {
					// readerFunction.selectCloseRfid(ip);//关闭读写器
					// Reader.stop();//停止线程
				} catch (Exception e) {
					System.err.println("读写器关闭失败");
				}
			}
		}
	}

	public void ChoiceThreadOut(final String ip, final String port, final int state, final String purpose) {
		boolean flag = false;// 判断线程是否打开\
		if (state == 1) {
			flag = true;
			ReaderOut = new Thread(new Runnable() {
				// run的方法体代表要执行的任务
				int count = 0;

				@Override
				public void run() {
					log.info("打开出库线程");
					int hcon = rf.OpenReaderExe(ip, port);// 打开读写器返回句柄
					try {
						AssetDeviceInfo aInfo = new AssetDeviceInfo();
						aInfo.setRfid_Ip(ip);
						aInfo.setDevice_Handle(hcon);
						aInfo.setDevice_State(state);// 默认打开读写器
						// aInfoService.updatePartDeviceState(aInfo);// 更新读写器
					} catch (Exception e) {
						log.info("出库读写器更新失败");
					}
					while (true) {
						if (hcon != 0) {
							readerFunction.ReaderOutTagId(ip, hcon, purpose);// 读取出库读写器标签
							// count++;
							// if (count == 500) {
							// int readestate = rf.GetReaderStateExe(hcon);//
							// 获取读写器状态
							// log.info("读写器状态值：" + readestate);
							// count = 0;
							// if (readestate == 0) {
							// JPushManager.warnMsgSendPushOnly("wxp",
							// "出库读写器连接失败，请断开电源重连！！！");
							// }
							// }
						} else {
							log.info("出库读写器打开失败");
							JPushManager.warnMsgSendPushOnly("wxp", "出库读写器连接失败，请断开服务器重连！！！");
							delay_ms(2000);// 休眠时间
						}
						delay_ms(400);// 休眠时间
					}
				}
			});
			if (ReaderOut.isAlive() == false) {// 判断线程是否启动
				ReaderOut.setDaemon(true);//
				ReaderOut.start();//
			}
		} else if (state == 0) {
			if (flag) {
				System.out.println("关闭读写器");
				try {
					// readerFunction.selectCloseRfid(ip);//关闭读写器
					// ReaderOut.stop();//停止线程
				} catch (Exception e) {
					System.out.println("读写器关闭失败");
				}
				flag = false;
			}
		}
	}

	public void ChoiceThreadIn(final String ip, final String port, final int state, final String purpose) {
		boolean flag = false;// 判断线程是否打开\
		if (state == 1) {
			flag = true;
			ReaderIn = new Thread(new Runnable() {
				// run的方法体代表要执行的任务
				int count = 0;

				@Override
				public void run() {
					log.info("打开出库线程");
					int hcon = rf.OpenReaderExe(ip, port);// 打开读写器返回句柄
					try {
						AssetDeviceInfo aInfo = new AssetDeviceInfo();
						aInfo.setRfid_Ip(ip);
						aInfo.setDevice_Handle(hcon);
						aInfo.setDevice_State(state);// 默认打开读写器
						aInfoService.updatePartDeviceState(aInfo);// 更新读写器
					} catch (Exception e) {
						log.info("出库读写器更新失败");
					}
					while (true) {
						if (hcon != 0) {
							readerFunction.ReaderInTagId(ip, hcon, purpose);// 读取出库读写器标签
							// count++;
							// if (count == 500) {
							// int readestate = rf.GetReaderStateExe(hcon);//
							// 获取读写器状态
							// log.info("读写器状态值：" + readestate);
							// count = 0;
							// if (readestate == 0) {
							// JPushManager.warnMsgSendPushOnly("wxp",
							// "出库读写器连接失败，请断开电源重连！！！");
							// }
							// }
						} else {
							log.info("出库读写器打开失败");
							JPushManager.warnMsgSendPushOnly("wxp", "出库读写器连接失败，请断开服务器重连！！！");
							delay_ms(2000);// 休眠时间
						}
						delay_ms(400);// 休眠时间
					}
				}
			});
			if (ReaderIn.isAlive() == false) {// 判断线程是否启动
				ReaderIn.setDaemon(true);//
				ReaderIn.start();//
			}
		} else if (state == 0) {
			if (flag) {
				System.out.println("关闭读写器");
				try {
					// readerFunction.selectCloseRfid(ip);//关闭读写器
					// ReaderOut.stop();//停止线程
				} catch (Exception e) {
					System.out.println("读写器关闭失败");
				}
				flag = false;
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext app) throws BeansException {
		//log.info("开启读写器读标签");
		if (flagStart == 0) {
			// AllReaderConn();
			/****** 使用服务器 ******/
			// ChoiceThread("192.168.0.215", "4001", 1, "monitor");//
			// ChoiceThreadOut("192.168.0.216", "4001", 1, "out");// 进库
			// RfidMonitorOutputA(8085);// 监视进出库:8085
			// RfidMonitorOutputB(8086);// 监视进出库:8086
			// ServiceSendWarnInfo(8087);// 监视报警:8087

			/****** 直接发送 ******/
			// getRfidInOut(8082);// 监视盘点线程:8082
			// getRfidMonitor(8083);// 监视盘点线程8083
			// getMonitorRfidOutIn();// 盘点读写器list进出库

			flagStart = 1;// 防止再次启动
		}
	}
}
