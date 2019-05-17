package com.xiaoshu.controller;



import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xiaoshu.entity.Fenxiao;
import com.xiaoshu.entity.Xuexiao;
import com.xiaoshu.service.FenxiaoService;
@Component
public class myMessageListener implements MessageListener{
	@Autowired
	FenxiaoService fenxiaoService;
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		TextMessage mg=(TextMessage) message;
		try {
			String id = mg.getText();
			Xuexiao xue=fenxiaoService.getXuexiao(Integer.parseInt(id));
			String schoolname = xue.getSchoolname();
			Fenxiao fenxiao=new Fenxiao();
			fenxiao.setName(schoolname);
			fenxiao.setNum(1);
			fenxiaoService.addfenxiao(fenxiao);
			System.out.println("添加的信息");
			System.out.println(xue);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
