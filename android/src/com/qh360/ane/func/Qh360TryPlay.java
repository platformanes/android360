package com.qh360.ane.func;

import android.content.Intent;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.common.TryAccount;
import com.qh360.payUtil.BridgeCode;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;

/**
 * @author Rect
 * @version  Time：2013-6-6 
 */
public class Qh360TryPlay implements FREFunction {

	private String TAG = "Qh360TryPlay";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] $args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null;
		Boolean isVisible = false;
		if (BridgeCode.mQihooUserInfo == null || BridgeCode.mTokenInfo == null) {
			_context.dispatchStatusEventAsync(TAG,"请先登录！");
			return null;
		}
		// 调用360SDK试玩注册（是否横屏显示、透明背景）
		try
		{
			// （是否透明）
			isVisible = $args[0].getAsBool();
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG,"参数错误！");
			return null;
		}
		
        doSdkTryAccountRegister(BridgeCode.mIsLandscape, isVisible, BridgeCode.mTryAccount);
		return result;
	}
	// 试玩注册的回调
	private IDispatcherCallback mTryAccountRegisterCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mTryAccountRegisterCallback, data is " + data);
			String authorizationCode = Qh360AllHandle.getInstance(_context).parseAuthorizationCode(data);
			Qh360AllHandle.getInstance(_context).onGotAuthorizationCode(authorizationCode);
		}
	};
	/**
	 * 使用360SDK的试玩注册接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 * @param isBgTransparent
	 *            是否以透明背景显示登录界面
	 * @param tryAccount
	 *            试玩账号
	 */
	protected void doSdkTryAccountRegister(boolean isLandScape,
			boolean isBgTransparent, TryAccount tryAccount) {
		Intent intent = Qh360AllHandle.getInstance(_context).getTryAccountRegisterIntent(isLandScape,
				isBgTransparent, tryAccount);
		Matrix.invokeActivity(this._context.getActivity(), intent, mTryAccountRegisterCallback);
	}
	
}
