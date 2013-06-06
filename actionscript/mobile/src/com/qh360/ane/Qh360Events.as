package com.qh360.ane 
{ 
	/**
	 * 
	 * @author Qh360  2013-5-6 
	 * 
	 */
	public class Qh360Events 
	{ 
		public function Qh360Events()
		{
		} 
		/**************************平台通知************************************/
		/**
		 *init 
		 */		
		public static const QH360_SDK_STATUS:String = "Qh360Init";
		/**
		 * 用户登录
		 */
		public static const QH360_LOGIN_STATUS : String = "Qh360Login";
		
		/**
		 * 用户注销
		 */
		public static const QH360_LOGOUT_STATUS : String = "Qh360Exit";
		
		/**
		 * 充值
		 */
		public static const QH360_PAY_STATUS : String = "Qh360Pay";
		/**
		 *防沉迷 
		 */		
		public static const  QH360_ANTI_STATUS:String = "Qh360AntiAddiction";
		/**
		 *论坛 
		 */		
		public static const  QH360_BBS_STATUS:String = "Qh360EnterBBS";
		/**
		 *退出游戏 
		 */		
		public static const  QH360_EXITGAME_STATUS:String = "Qh360ExitGame";
		/**
		 *快速游戏 
		 */		
		public static const  QH360_QUICKPLAY_STATUS:String = "Qh360QuickPlay";
		/**
		 *实名注册 
		 */		
		public static const  QH360_REALNAME_STATUS:String = "Qh360RealNameReg";
		/**
		 *切换账号
		 */		
		public static const  QH360_SWITCH_STATUS:String = "Qh360SwitchAccount";
		/**
		 *试玩注册 
		 */		
		public static const  QH360_TRYACCOUNT_STATUS:String = "Qh360TryAccountReg";
		/**
		 *试玩
		 */		
		public static const  QH360_TRYPLAY_STATUS:String = "Qh360TryPlay";
		
		
	} 
}