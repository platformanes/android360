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
public class Qh360RealNameReg implements FREFunction {
	private String TAG = "Qh360RealNameReg";
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
		try
		{
			// 实名注册（是否透明）
			isVisible = $args[0].getAsBool();
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG,"参数错误！");
			return null;
		}
		// 实名注册（是否横屏显示）
		doSdkRealNameRegister(BridgeCode.mIsLandscape, isVisible, BridgeCode.mQihooUserInfo.getId());

		return result;
	}

	/**
	 * 启动实名注册
	 * 
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 * @param isBgTransparent
	 *            是否以透明背景显示登录界面
	 */
	protected void doSdkRealNameRegister(boolean isLandScape,
			boolean isBgTransparent, String qihooUserId) {
		Intent intent = Qh360AllHandle.getInstance(_context).getRealNameRegisterIntent(isLandScape, isBgTransparent,
				qihooUserId);
		Matrix.invokeActivity(this._context.getActivity(), intent, mRealNameRegisterCallback);
	}

	

	// 实名注册的回调
	private IDispatcherCallback mRealNameRegisterCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mRealNameRegisterCallback, data is " + data);
			_context.dispatchStatusEventAsync(TAG,"mRealNameRegisterCallback, data is " + data);
		}
	};
}
