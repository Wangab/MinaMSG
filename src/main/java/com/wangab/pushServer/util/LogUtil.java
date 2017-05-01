package com.wangab.pushServer.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {
	static{
		String path = LogUtil.class.getClass().getResource("/")
				.getPath()
				+ "log4j.properties";
		PropertyConfigurator.configureAndWatch(path, 1000);
	}
	public static Logger getLogger(){
		return Logger.getLogger("log");
	}
	
}
