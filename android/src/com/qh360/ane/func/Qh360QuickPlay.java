package com.qh360.ane.func;

import org.json.JSONException;
import org.json.JSONObject;

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
public class Qh360QuickPlay implements FREFunction {

	private String TAG = "Qh360QuickPlay";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] $args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null;
		 // 调用360SDK快速游戏（是否横屏显示）
        doSdkQuickPlay(BridgeCode.mIsLandscape);
		return result;
	}

	/**
	 * 使用360SDK的快速游戏接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 */
	protected void doSdkQuickPlay(boolean isLandScape) {
		Intent intent = Qh360AllHandle.getInstance(_context).getQuickPlayIntent(isLandScape);
		Matrix.invokeActivity(this._context.getActivity(), intent, mQuickPlayCallback);
	}
	
	// 快速游戏的回调
	private IDispatcherCallback mQuickPlayCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mQuickPlayCallback, data is " + data);

			JSONObject jsonRes;
			int errCode = -99;
			try {
				jsonRes = new JSONObject(data);
				errCode = jsonRes.optInt("error_code", -99);
				if (errCode == 0) {
					if (jsonRes.optBoolean("is_try_account", false)) {
						TryAccount tryAccount = new TryAccount(jsonRes
								.getJSONObject("content").toString());
						Qh360AllHandle.getInstance(_context).onGotTryAccount(tryAccount);
					} else {
						String authCode = Qh360AllHandle.getInstance(_context).parseAuthorizationCode(data);
						Qh360AllHandle.getInstance(_context).onGotAuthorizationCode(authCode);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (errCode != 0) {
				Qh360AllHandle.getInstance(_context).onGotError(errCode);
			}

			return;
		}
	};
}
