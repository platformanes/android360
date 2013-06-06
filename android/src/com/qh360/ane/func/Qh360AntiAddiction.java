package com.qh360.ane.func;


import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.payUtil.BridgeCode;

/**
 * @author Rect
 * @version  Time：2013-6-6 
 */
public class Qh360AntiAddiction implements FREFunction {

	private String TAG = "Qh360AntiAddiction";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] $args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null;
		if (BridgeCode.mQihooUserInfo == null || BridgeCode.mTokenInfo == null) {
			_context.dispatchStatusEventAsync(TAG,"请先登录！");
			return null;
		}
        // 防沉迷查询
        Qh360AllHandle.getInstance(_context).doSdkAntiAddictionQuery(
        		BridgeCode.mQihooUserInfo.getId(), 
        		BridgeCode.mTokenInfo.getAccessToken());
		return result;
	}

}
