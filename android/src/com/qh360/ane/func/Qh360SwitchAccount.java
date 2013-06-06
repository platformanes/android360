package com.qh360.ane.func;

import android.content.Intent;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.payUtil.BridgeCode;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;

/**
 * @author Rect
 * @version  Time：2013-6-6 
 */
public class Qh360SwitchAccount implements FREFunction {

	private String TAG = "Qh360SwitchAccount";
	private FREContext _context;
	// 登录响应模式：CODE模式。
	protected static final String RESPONSE_TYPE_CODE = "code";
	
	@Override
	public FREObject call(final FREContext context, FREObject[] $args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null;
		Boolean isVisible = false;
		
		try
		{
			// 切换账号（是否透明）
			isVisible = $args[0].getAsBool();
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG,"参数错误！");
			return null;
		}
		 // 调用360SDK切换账号（是否横屏显示、透明背景）
        doSdkSwitchAccount(BridgeCode.mIsLandscape, isVisible);
		return result;
	}

	/**
	 * 使用360SDK的切换账号接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 * @param isBgTransparent
	 *            是否以透明背景显示登录界面
	 */
	protected void doSdkSwitchAccount(boolean isLandScape,
			boolean isBgTransparent) {
		Intent intent = Qh360AllHandle.getInstance(_context).getSwitchAccountIntent(isLandScape, isBgTransparent);
		Matrix.invokeActivity(this._context.getActivity(), intent, mAccountSwitchCallback);
	}
	
	// 切换账号的回调
	private IDispatcherCallback mAccountSwitchCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mAccountSwitchCallback, data is " + data);
			String authorizationCode = Qh360AllHandle.getInstance(_context).parseAuthorizationCode(data);
			Qh360AllHandle.getInstance(_context).onGotAuthorizationCode(authorizationCode);
		}
	};
	
	
}
