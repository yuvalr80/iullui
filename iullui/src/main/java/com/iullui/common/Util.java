package com.iullui.common;

/**
 * Utilities class
 * @author Yuval
 */
public class Util {

	public static final String APP_ENV = "PARAM1";
	public static final String APP_TOKEN = "PARAM2";
	
	public static final String ENV_DEV = "dev";
	
	public static boolean isEmpty(String str) { 
		return (str == null || str.trim().isEmpty()); 
	}

}
