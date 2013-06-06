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
	} 
}