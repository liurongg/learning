package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Chengshi;
import com.xiaoshu.entity.Fenxiao;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Xuexiao;
import com.xiaoshu.service.FenxiaoService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.xmlrpc.base.Data;

@Controller
@RequestMapping("fenxiao")
public class FenxiaoController extends LogController{
	static Logger logger = Logger.getLogger(FenxiaoController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	@Autowired
	private FenxiaoService fenxiaoService;
	
	@RequestMapping("fenxiaoIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "fenxiao";
	}
	
	
	@RequestMapping(value="userList",method=RequestMethod.POST)
	public void userList(HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Xuexiao user = new Xuexiao();
			String schoolname = request.getParameter("schoolname");
			/*String roleid = request.getParameter("roleid");
			String usertype = request.getParameter("usertype");*/
			String order = request.getParameter("order");
			String ordername = request.getParameter("ordername");
			if (StringUtil.isNotEmpty(schoolname)) {
				user.setSchoolname(schoolname);
			}
			/*if (StringUtil.isNotEmpty(roleid) && !"0".equals(roleid)) {
				user.setRoleid(Integer.parseInt(roleid));
			}
			if (StringUtil.isNotEmpty(usertype)) {
				user.setUsertype(usertype.getBytes()[0]);
			}*/
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Xuexiao> userList= fenxiaoService.findUserPage(user,pageNum,pageSize,ordername,order);
			
			request.setAttribute("schoolname", schoolname);
			/*request.setAttribute("roleid", roleid);
			request.setAttribute("usertype", usertype);*/
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(HttpServletRequest request,Xuexiao xuexiao,HttpServletResponse response){
		Integer userId = xuexiao.getId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				 Xuexiao userName = fenxiaoService.existUserWithUserName(xuexiao.getSchoolname());
				if(userName != null && userName.getId().compareTo(userId)==0||userName==null){
					xuexiao.setId(userId);
					fenxiaoService.updateUser(xuexiao);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(fenxiaoService.existUserWithUserName(xuexiao.getSchoolname())==null){  // 没有重复可以添加
					String phone = xuexiao.getPhone();					
					if(phone.length()==11){
						xuexiao.setCreatetime(new Date());
						fenxiaoService.addUser(xuexiao);
						result.put("success", true);
					}else{
						result.put("success", true);
						result.put("errorMsg", "不够11位");
					}
				
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	//导出
		@RequestMapping("exportItemcats")
		public void exportItemcats(HttpServletRequest request,HttpServletResponse response){
			JSONObject result = new JSONObject();
			try {
				String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
			    String excelName = "手动备份"+time;
				//Log log = new Log();
			    Xuexiao itemcat = new Xuexiao();
				//List<Log> list = logService.findLog(log);
			    List<Xuexiao> list = fenxiaoService.getAll();
				String[] handers = {"编号","分校名称","所在城市","联系方式","详细地址","分校状态","创建时间"};
				// 1导入硬盘
				ExportExcelToDisk(request,handers,list, excelName);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("", "对不起，备份失败");
			}
			WriterUtil.write(response, result.toString());
		}
		
		
		
		// 导出到硬盘
		@SuppressWarnings("resource")
		private void ExportExcelToDisk(HttpServletRequest request,
				String[] handers, List<Xuexiao> list, String excleName) throws Exception {
			
			try {
				HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
				HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
				HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
				rowFirst.setHeight((short) 500);
				for (int i = 0; i < handers.length; i++) {
					sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
				}
				//写标题了
				for (int i = 0; i < handers.length; i++) {
				    //获取第一行的每一个单元格
				    HSSFCell cell = rowFirst.createCell(i);
				    //往单元格里面写入值
				    cell.setCellValue(handers[i]);
				}
				for (int i = 0;i < list.size(); i++) {
				    //获取list里面存在是数据集对象
					Xuexiao itemcat = list.get(i);
				    //Log log = list.get(i);
				    //创建数据行
				    HSSFRow row = sheet.createRow(i+1);
				    //设置对应单元格的值
				    row.setHeight((short)400);   // 设置每行的高度
				    //"序号","操作人","IP地址","操作时间","操作模块","操作类型","详情"
				    row.createCell(0).setCellValue(itemcat.getId());
				    row.createCell(1).setCellValue(itemcat.getSchoolname());
				   
				    	String areaid = "上海";
				    	if(itemcat.getAreaid()==2){
				    		areaid = "北京";
				    	}
				    	if(itemcat.getAreaid()==3){
				    		areaid = "河南";
				    	}
				    row.createCell(2).setCellValue(areaid);	
				    row.createCell(3).setCellValue(itemcat.getPhone());
				    String phone = itemcat.getPhone();
				    String substring = phone.substring(0, 3);
				    Integer ll = Integer.valueOf(substring);
				    if(ll>=135&&ll<=135){
				    	 row.createCell(3).setCellValue("中国移动");
				    }else if(ll>=131&&ll<=133){
				    	 row.createCell(3).setCellValue("中国联通");
				    }
				    row.createCell(4).setCellValue(itemcat.getAddress());
				    row.createCell(5).setCellValue(itemcat.getStatus());
				    row.createCell(6).setCellValue(TimeUtil.formatTime(itemcat.getCreatetime(), "yyyy-MM-dd"));
				}
				//写出文件（path为文件路径含文件名）
					OutputStream os;
					File file = new File(request.getSession().getServletContext().getRealPath("/")+"logs"+File.separator+"backup"+File.separator+excleName+".xls");
					
					if (!file.exists()){//若此目录不存在，则创建之  
						file.createNewFile();  
						logger.debug("创建文件夹路径为："+ file.getPath());  
		            } 
					os = new FileOutputStream(file);
					wb.write(os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
		}
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				fenxiaoService.deleteUser(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("getcheng")
	public void getcheng(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
		
				List<Chengshi> list=fenxiaoService.findChengshi();
			
			result.put("success", true);
			result.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
