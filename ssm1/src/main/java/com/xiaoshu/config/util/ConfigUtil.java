package com.xiaoshu.config.util;

public class ConfigUtil {
	
	private static Integer pageSize;
	
	private static String mainTitle;

	public static Integer getPageSize() {
		return pageSize;
	}

	public static void setPageSize(Integer pageSize) {
		ConfigUtil.pageSize = pageSize != null ?pageSize : 10;
	}

	public static String getMainTitle() {
		return mainTitle;
	}

	public static void setMainTitle(String mainTitle) {
		ConfigUtil.mainTitle = mainTitle;
	}
	
	
	
	

}
