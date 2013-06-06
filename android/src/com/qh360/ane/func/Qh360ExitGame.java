package com.qh360.ane.func;

import android.content.Intent;
import android.os.Handler;
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
public class Qh360ExitGame implements FREFunction {

	private String TAG = "Qh360ExitGame";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		// 调用360SDK退出（是否横屏显示）
        doSdkQuit(BridgeCode.mIsLandscape);
		return result;
	}

	/**
	 * 使用360SDK的退出接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示支付界面
	 */
	protected void doSdkQuit(boolean isLandScape) {
		Intent intent = Qh360AllHandle.getInstance(_context).getQuitIntent(isLandScape);
		Matrix.invokeActivity(this._context.getActivity(), intent, mQuitCallback);
	}
	
	// 退出的回调
	private IDispatcherCallback mQuitCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mQuitCallback, data is " + data);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (_context.getActivity().isFinishing()) {
						return;
					}
					_context.getActivity().finish();
				}
			}, 200);

		}
	};
}
