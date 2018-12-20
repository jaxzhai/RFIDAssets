package com.daoben.rfid.test;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.daoben.rfid.mapper.TUserMapper;
import com.daoben.rfid.model.TUser;
import com.daoben.rfid.service.TUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext.xml" })
public class TUserTest {

	@Autowired
	private TUserMapper tm;

	@Autowired
	private TUserService ts;

	@Test
	public void findAll() {
		System.out.println(ts.findAll());
	}


	@Test
	public void deleteUserInfo() throws Exception {
		
		updateUserInfo();
		
		throw new Exception("数据更新操作异常。");

	}

	/**
	 * @author wxp 添加用户信息
	 */
	@Test
	public void registeruser() {
		int result = 0;
		try {
			TUser tUser = new TUser();
			tUser.setAccount("12345");
			tUser.setUser_Name("张张");
			tUser.setPassword("wxp123");
			tUser.setRole(1);
			tUser.setDepartment("自动化");
			tUser.setPhone("123465");
			tUser.setMail("123456@qq.com");
			result = ts.AddUserInfo(tUser);
			System.out.println("success");
			System.out.println(result);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			System.out.println("fail");
		}
	}

	/**
	 * @author wxp 更改用户信息
	 */
	@Test
	public void updateUserInfo() {
		try {
			TUser tUser = new TUser();
			tUser.setAccount("12345");
			tUser.setUser_Name("陈1456");
			// tUser.setPassword(password);
			tUser.setRole(1);
			// tUser.setDepartment(department);
			// tUser.setPhone(phone);
			// tUser.setMail(mail);
			ts.updateUserInfo(tUser);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

}
