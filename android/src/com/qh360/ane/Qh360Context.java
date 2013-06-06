package com.qh360.ane;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.qh360.ane.func.Qh360AntiAddiction;
import com.qh360.ane.func.Qh360Destroy;
import com.qh360.ane.func.Qh360EnterBBS;
import com.qh360.ane.func.Qh360ExitGame;
import com.qh360.ane.func.Qh360Init;
import com.qh360.ane.func.Qh360Login;
import com.qh360.ane.func.Qh360Pay;
import com.qh360.ane.func.Qh360QuickPlay;
import com.qh360.ane.func.Qh360RealNameReg;
import com.qh360.ane.func.Qh360SwitchAccount;
import com.qh360.ane.func.Qh360TryAccountReg;
import com.qh360.ane.func.Qh360TryPlay;

/**
 * @author Rect
 * @version  Time：2013-5-8 
 */
public class Qh360Context extends FREContext {
	/**
	 * INIT sdk
	 */
	public static final String QH360_FUNCTION_INIT = "qh360_function_init";
	/**
	 * 登录Key
	 */
	public static final String QH360_FUNCTION_LOGIN = "qh360_function_login";
	/**
	 * 付费Key
	 */
	public static final String QH360_FUNCTION_PAY = "qh360_function_pay";
	/**
	 * 退出Key
	 */
	public static final String QH360_FUNCTION_EXIT = "qh360_function_exit";
	/**
	 * 实名注册
	 */
	public static final String QH360_FUNCTION_REAL_NAME_REG = "qh360_function_real_name_reg";
	/**
	 * 试玩注册
	 */
	public static final String QH360_FUNCTION_TRY_ACCOUNT_REG = "qh360_function_try_Account_reg";
	/**
	 * 切换帐号
	 */
	public static final String QH360_FUNCTION_SWITCH_ACCOUNT = "qh360_function_switch_account";
	/**
	 * 防沉迷查询
	 */
	public static final String QH360_FUNCTION_ANTI_ADDICTION = "qh360_function_anti_addiction";
	/**
	 * 快速游戏
	 */
	public static final String QH360_FUNCTION_QUICK_PLAY = "qh360_function_quick_play";
	/**
	 * 试玩
	 */
	public static final String QH360_FUNCTION_TRY_PLAY = "qh360_function_try_play";
	/**
	 * 退出游戏
	 */
	public static final String QH360_FUNCTION_EXITGAME = "qh360_function_exitgame";
	/**
	 * BBS
	 */
	public static final String QH360_FUNCTION_BBS = "qh360_function_bbs";
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		// TODO Auto-generated method stub
		Map<String, FREFunction> map = new HashMap<String, FREFunction>();
//	       //映射
		   map.put(QH360_FUNCTION_INIT, new Qh360Init());
	       map.put(QH360_FUNCTION_LOGIN, new Qh360Login());
	       map.put(QH360_FUNCTION_PAY, new Qh360Pay());
	       map.put(QH360_FUNCTION_EXIT, new Qh360Destroy());
	       
	       map.put(QH360_FUNCTION_REAL_NAME_REG, new Qh360RealNameReg());
	       map.put(QH360_FUNCTION_TRY_ACCOUNT_REG, new Qh360TryAccountReg());
	       map.put(QH360_FUNCTION_SWITCH_ACCOUNT, new Qh360SwitchAccount());
	       map.put(QH360_FUNCTION_ANTI_ADDICTION, new Qh360AntiAddiction());
	       map.put(QH360_FUNCTION_QUICK_PLAY, new Qh360QuickPlay());
	       map.put(QH360_FUNCTION_TRY_PLAY, new Qh360TryPlay());
	       map.put(QH360_FUNCTION_EXITGAME, new Qh360ExitGame());
	       map.put(QH360_FUNCTION_BBS, new Qh360EnterBBS());
	       return map;
	}

}
