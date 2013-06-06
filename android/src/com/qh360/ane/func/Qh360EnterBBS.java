package com.qh360.ane.func;

import android.content.Intent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.payUtil.BridgeCode;
import com.qihoopay.insdk.matrix.Matrix;

/**
 * @author Rect
 * @version  Time：2013-6-6 
 */
public class Qh360EnterBBS implements FREFunction {

	private String TAG = "Qh360EnterBBS";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		doSdkBBS(BridgeCode.mIsLandscape);
		return result;
	}

	/**
	 * 使用360SDK的论坛接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示支付界面
	 */
	protected void doSdkBBS(boolean isLandScape) {
		Intent intent = Qh360AllHandle.getInstance(_context).getBBSIntent(isLandScape);
		Matrix.invokeActivity(this._context.getActivity(), intent, null);
		this._context.dispatchStatusEventAsync(TAG, "打开论坛");
	}
}
