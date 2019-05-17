package com.xiaoshu.service;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.ChengshiMapper;
import com.xiaoshu.dao.FenxiaoMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.dao.XuexiaoMapper;
import com.xiaoshu.entity.Chengshi;
import com.xiaoshu.entity.Fenxiao;
import com.xiaoshu.entity.Xuexiao;
import com.xiaoshu.entity.XuexiaoExample;
import com.xiaoshu.entity.XuexiaoExample.Criteria;

@Service
public class FenxiaoService {

	@Autowired
	UserMapper userMapper;
	@Autowired
	XuexiaoMapper xuexiaoMapper;
	@Autowired
	ChengshiMapper chengshiMapper;
	@Autowired
	Destination queueTextDestination;
	@Autowired
	JmsTemplate jmsTemplate;
	@Autowired
	FenxiaoMapper fenxiaoMapper;
	/*// 查询所有
	public List<User> findUser(User t) throws Exception {
		return userMapper.select(t);
	};

	// 数量
	public int countUser(User t) throws Exception {
		return userMapper.selectCount(t);
	};

	// 通过ID查询
	public User findOneUser(Integer id) throws Exception {
		return userMapper.selectByPrimaryKey(id);
	};

	

	

	// 登录
	public User loginUser(User user) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andPasswordEqualTo(user.getPassword()).andUsernameEqualTo(user.getUsername());
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	

	// 通过角色判断是否存在
	public User existUserWithRoleId(Integer roleId) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleidEqualTo(roleId);
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	}
*/
	// 删除
		public void deleteUser(Integer id) throws Exception {
			xuexiaoMapper.deleteByPrimaryKey(id);
		};
	// 新增
		public void addUser(Xuexiao t) throws Exception {
			xuexiaoMapper.getMQ(t);
			final Integer id = t.getId();
			jmsTemplate.send(queueTextDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					TextMessage message = session.createTextMessage(JSONObject.toJSONString(id));
					return message;
				}
			});
		};
		
		// 修改
		public void updateUser(Xuexiao t) throws Exception {
			xuexiaoMapper.updateByPrimaryKeySelective(t);
		};
	// 通过用户名判断是否存在，（新增时不能重名）
		public Xuexiao existUserWithUserName(String schoolname) throws Exception {
			XuexiaoExample example = new XuexiaoExample();
			Criteria criteria = example.createCriteria();
			criteria.andSchoolnameEqualTo(schoolname);
			List<Xuexiao> userList = xuexiaoMapper.selectByExample(example);
			return userList.isEmpty()?null:userList.get(0);
		};
	public PageInfo<Xuexiao> findUserPage(Xuexiao user, int pageNum, int pageSize, String ordername, String order) {
		PageHelper.startPage(pageNum, pageSize);
		ordername = StringUtil.isNotEmpty(ordername)?ordername:"id";
		order = StringUtil.isNotEmpty(order)?order:"desc";
		XuexiaoExample example = new XuexiaoExample();
		example.setOrderByClause(ordername+" "+order);
		Criteria criteria = example.createCriteria();
		if(StringUtil.isNotEmpty(user.getSchoolname())){
			criteria.andSchoolnameLike("%"+user.getSchoolname()+"%");
		}
		/*if(user.getUsertype() != null){
			criteria.andUsertypeEqualTo(user.getUsertype());
		}
		if(user.getRoleid() != null){
			criteria.andRoleidEqualTo(user.getRoleid());
		}*/
		List<Xuexiao> userList = xuexiaoMapper.selectByXuexiao(example);
		PageInfo<Xuexiao> pageInfo = new PageInfo<Xuexiao>(userList);
		return pageInfo;
	}
	public List<Chengshi> findChengshi() {
		// TODO Auto-generated method stub
		List<Chengshi> list = chengshiMapper.selectAll();
		return list;
	}
	public List<Xuexiao> getAll() {
		// TODO Auto-generated method stub
		List<Xuexiao> list = xuexiaoMapper.selectAll();
		return list;
	}
	public Xuexiao getXuexiao(int id) {
		Xuexiao xuexiao = xuexiaoMapper.selectByPrimaryKey(id);
		return xuexiao;
	}
	public void addfenxiao(Fenxiao fenxiao) {
		// TODO Auto-generated method stub
		fenxiaoMapper.insert(fenxiao);
		System.out.println("-------"+fenxiao);
		
	}


}
