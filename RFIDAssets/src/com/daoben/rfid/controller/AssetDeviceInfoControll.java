package com.daoben.rfid.controller;

import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.daoben.rfid.model.AssetDeviceInfo;
import com.daoben.rfid.reader.ConnReadermpl;
import com.daoben.rfid.reader.ReaderFunction;
import com.daoben.rfid.service.AssetDeviceInfoService;
import com.daoben.rfid.utils.AcquireTimeStamp;
import com.daoben.rfid.utils.ResponsePWFactory;

@Controller
public class AssetDeviceInfoControll {

	private Logger log = Logger.getLogger(this.getClass());

	@Resource
	private AssetDeviceInfoService as;

	@Resource
	private ResponsePWFactory responsePWFactory;

//	@Resource
	private ConnReadermpl readerTI;// 读写器读取

//	@Resource
	private ReaderFunction readerFunction;

	@Resource
	private AcquireTimeStamp ats;

	/**
	 * @author wxp
	 * @Title: leon.chen
	 * @Description: 查询所有读写器信息
	 * @param :null
	 * @return String 返回类型
	 * @date 2017年2月17日 上午9:23:31
	 */
	@ResponseBody
	@RequestMapping(value = "/AssetDeviceInfo/QueryAllDeviceInfo", produces = "text/json;charset=UTF-8")
	public String selectAllTagInfo() {
		String responseMap = "";
		try {
			List<AssetDeviceInfo> aTagInfos = as.selectAllDeviceInfo();
			responseMap = responsePWFactory.responseMap("true", "RFID读写器查询成功", aTagInfos, null);
			responseMap = responsePWFactory.responseMap("true", "RFID读写器查询成功", aTagInfos, null);
		} catch (Exception e) {
			responseMap = responsePWFactory.responseMap("false", "RFID读写器查询失败", null, null);
		}
		log.info(responseMap);
		return responseMap;
	}

	/**
	 * @author wxp 更改读写器连接状态
	 *         url:http://192.168.0.160:8080/RFIDAssets/AssetDeviceInfo/
	 *         updateByDeviceState?rfid_Ip="192.168.0.1"&device_State=1
	 */
	@ResponseBody
	@RequestMapping(value = "/AssetDeviceInfo/updateByDeviceState", produces = "text/json;charset=UTF-8")
	public String updateByDeviceState(String rfid_Ip, int device_State) {
		String responseMap = "";
		try {
			AssetDeviceInfo aInfo = new AssetDeviceInfo();
			aInfo.setRfid_Ip(rfid_Ip);
			aInfo.setDevice_State(device_State);
			aInfo.setRegister_Time(ats.getTimeStamp());
			int goBack = as.updateByDeviceState(aInfo);// 获取数据库更改的状态
			// readerTI.OptionReaderConn(aInfo.getDevice_State(),
			// aInfo.getRfid_Ip(), aInfo.getDevice_Port());// 获取读写器选择更改
			responseMap = responsePWFactory.responseMap("true", "RFID读写器状态更改成功", device_State, null);
		} catch (Exception e) {
			responseMap = responsePWFactory.responseMap("false", "RFID读写器状态更改失败", null, null);
		}
		log.info(responseMap);
		return responseMap;
	}
}
