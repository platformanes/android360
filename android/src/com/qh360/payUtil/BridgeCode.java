package com.qh360.payUtil;

import com.qh360.appserver.QihooUserInfo;
import com.qh360.appserver.TokenInfo;
import com.qh360.common.TryAccount;


/**
 * 全局变量
 * @author Rect
 * @version  Time：2013-5-27 
 */
public class BridgeCode {
	public static String PLATFORM_ACCESSTOKEN = null;
	public static String PLATFORM_QIHOOUSERID = null;
	public static String GAME_USER_NAME = null;
	public static String GAME_PAY_APP_USER_ID = null;
	public static String GAME_OTHER_KEY = null;
	
	public static Boolean mIsLandscape = false;
	
	public static TokenInfo mTokenInfo;
	public static QihooUserInfo mQihooUserInfo;
	public static TryAccount mTryAccount = new TryAccount();
}
