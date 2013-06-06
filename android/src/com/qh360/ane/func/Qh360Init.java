package com.qh360.ane.func;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.payUtil.Constants;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;

/**
 * 初始化SDK
 * @author Rect
 * @version  Time：2013-5-8 
 */
public class Qh360Init implements FREFunction {

	private String TAG = "Qh360Init";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		// TODO Auto-generated method stub
		//--------------------------------
		//在这里做初始化的操作 我这里直接传回。。
		try {
			Constants.DEMO_FIXED_PAY_MONEY_AMOUNT = args[0].getAsString(); 
			Constants.DEMO_NOT_FIXED_PAY_MONEY_AMOUNT = args[1].getAsString();
			Constants.DEMO_PAY_EXCHANGE_RATE = args[2].getAsString();
			Constants.DEMO_PAY_PRODUCT_ID = args[3].getAsString();
			Constants.DEMO_PAY_APP_USER_ID = args[4].getAsString();
			Constants.DEMO_APP_SERVER_NOTIFY_URI = args[5].getAsString();
			Constants.DEMO_APP_SERVER_URL_GET_TOKEN = args[6].getAsString();
			Constants.DEMO_APP_SERVER_URL_GET_USER = args[7].getAsString();
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG, "登录传入参数错误："+e.getMessage());
			return null;
		}
		callBack();
		//--------------------------------
		
		return result;
	}

	/**
	 * 初始化回调 把初始化结果传给AS端
	 */
	public void callBack(){
		Log.d(TAG, "---------初始化开始-------");
		// 初始化
        Matrix.init(_context.getActivity(), false, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                Log.d(TAG, "初始化返回: " + data);
                _context.dispatchStatusEventAsync(TAG, "初始化返回:"+data);
            }
        });
		
	}

}
