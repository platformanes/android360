package com.qh360.ane 
{ 
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	
	/**
	 * 
	 * @author Rect  2013-5-6 
	 * 
	 */
	public class Qh360Extension extends EventDispatcher 
	{ 
		public static const QH360_FUNCTION_INIT:String = "qh360_function_init";//与java端中Map里的key一致
		public static const QH360_FUNCTION_LOGIN:String = "qh360_function_login";//与java端中Map里的key一致
		public static const QH360_FUNCTION_PAY:String = "qh360_function_pay";//与java端中Map里的key一致
		public static const QH360_FUNCTION_EXIT:String = "qh360_function_exit";//与java端中Map里的key一致
		
		public static const QH360_FUNCTION_REAL_NAME_REG:String = "qh360_function_real_name_reg";//与java端中Map里的key一致
		public static const QH360_FUNCTION_TRY_ACCOUNT_REG:String = "qh360_function_try_Account_reg";//与java端中Map里的key一致
		public static const QH360_FUNCTION_SWITCH_ACCOUNT:String = "qh360_function_switch_account";//与java端中Map里的key一致
		public static const QH360_FUNCTION_ANTI_ADDICTION:String = "qh360_function_anti_addiction";//与java端中Map里的key一致
		public static const QH360_FUNCTION_QUICK_PLAY:String = "qh360_function_quick_play";//与java端中Map里的key一致
		public static const QH360_FUNCTION_TRY_PLAY:String = "qh360_function_try_play";//与java端中Map里的key一致
		public static const QH360_FUNCTION_EXITGAME:String = "qh360_function_exitgame";//与java端中Map里的key一致
		public static const QH360_FUNCTION_BBS:String = "qh360_function_bbs";//与java端中Map里的key一致
		
		public static const EXTENSION_ID:String = "com.qh360.ane";//与extension.xml中的id标签一致
		private var extContext:ExtensionContext;
		
		/**单例的实例*/
		private static var _instance:Qh360Extension; 
		public function Qh360Extension(target:IEventDispatcher=null)
		{
			super(target);
			if(extContext == null) {
				extContext = ExtensionContext.createExtensionContext(EXTENSION_ID, "");
				extContext.addEventListener(StatusEvent.STATUS, statusHandler);
			}
			
		} 
		
		//第二个为参数，会传入java代码中的FREExtension的createContext方法
		/**
		 * 获取实例
		 * @return DLExtension 单例
		 */
		public static function getInstance():Qh360Extension
		{
			if(_instance == null) 
				_instance = new Qh360Extension();
			return _instance;
		}
		
		/**
		 * 转抛事件
		 * @param event 事件
		 */
		private function statusHandler(event:StatusEvent):void
		{
			dispatchEvent(event);
		}
		/**
		 * 
		 * @param fixedPay 以分为单位。100分，即1元。fixedPay大于等于100时，即定额支付。
		 * @param notFixed  为0分时，即不定额支付。
		 * @param rate 人民币与游戏充值币的比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
		 * @param productID  购买商品的商品id，应用指定，最大16字符。
		 * @param appUserID 应用内的用户id。 最大32字符
		 * @param serverNotifyURL 应用服务器为360服务器提供的支付结果通知接口，由360服务器把支付结果通知到这个URI
		 * @param getTokenURL 应用服务器为应用客户端提供的接口Url，用于通过AuthorizationCode获取TokenInfo
		 * @param getUserInfoURL 应用服务器为应用客户端提供的接口Url，用于通过AccessToken获取QihooUserInfo
		 * @return 
		 * 
		 */			
		public function Qh360Init(
			fixedPay:String,
			notFixed:String,
			rate:String,
			productID:String,
			appUserID:String,
			serverNotifyURL:String,
			getTokenURL:String,
			getUserInfoURL:String):String{
			
			if(extContext ){
				return extContext.call(QH360_FUNCTION_INIT,
					fixedPay,
					notFixed,
					rate,
					productID,
					appUserID,
					serverNotifyURL,
					getTokenURL,
					getUserInfoURL) as String;
			}
			return "call init failed";
		} 
		
		/**
		 * 
		 * @param isLandScape  是否横屏
		 * @param isBgTransparent  是否透明
		 * @return 
		 * 
		 */			
		public function Qh360LogIn(isLandScape:Boolean,isBgTransparent:Boolean):String{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_LOGIN,isLandScape,isBgTransparent) as String;
			}
			return "call login failed";
		} 
		/**
		 * 
		 * @param isVisible  是否透明
		 * @param userName 游戏角色名
		 * @param userID 游戏ID
		 * @return 
		 * 
		 */				 
		public function Qh360Pay(isVisible:Boolean,userName:String,userID:String):String{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_PAY,isVisible,userName,userID)as String;
			}
			return "call pay failed";
		}
		
		public function Qh360QuickPlay(key:int = 0):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_QUICK_PLAY,key)as String;
			}
			return "call Qh360QuickPlay failed";
		}
		/**
		 * 
		 * @param isVisible 是否透明模式
		 * @return 
		 * 
		 */		
		public function Qh360RealNameReg(isVisible:Boolean):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_REAL_NAME_REG,isVisible)as String;
			}
			return "call Qh360RealNameReg failed";
		}
		/**
		 * 
		 * @param isVisible 是否透明模式
		 * @return 
		 * 
		 */
		public function Qh360SwitchAccount(isVisible:Boolean):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_SWITCH_ACCOUNT,isVisible)as String;
			}
			return "call Qh360SwitchAccount failed";
		}
		/**
		 * 
		 * @param isVisible 是否透明模式
		 * @return 
		 * 
		 */
		public function Qh360TryAccountReg(isVisible:Boolean):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_TRY_ACCOUNT_REG,isVisible)as String;
			}
			return "call Qh360TryAccountReg failed";
		}
		/**
		 * 
		 * @param isVisible 是否透明模式
		 * @return 
		 * 
		 */
		public function Qh360TryPlay(isVisible:Boolean):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_TRY_PLAY,isVisible)as String;
			}
			return "call Qh360TryPlay failed";
		}
		
		public function Qh360AntiAddiction(key:int = 0):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_ANTI_ADDICTION,key)as String;
			}
			return "call Qh360AntiAddiction failed";
		}
		
		public function Qh360EnterBBS(key:int = 0):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_BBS,key)as String;
			}
			return "call Qh360EnterBBS failed";
		}
		
		public function Qh360ExitGame(key:int = 0):String
		{
			if(extContext ){
				return extContext.call(QH360_FUNCTION_EXITGAME,key)as String;
			}
			return "call Qh360ExitGame failed";
		}
		
		/**
		 *退出SDK时候调用   这个函数只在退出游戏的时候调用  
		 * @param key
		 * @return 
		 * 
		 */		
		public function ExitSDKHandle(key:int):String{
			if(extContext){ 
				return extContext.call(QH360_FUNCTION_EXIT,key) as String;
			}
			return "call exit failed";
		}
	} 
}