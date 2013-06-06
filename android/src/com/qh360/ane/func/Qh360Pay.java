package com.qh360.ane.func;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.appserver.QihooUserInfo;
import com.qh360.appserver.TokenInfo;
import com.qh360.common.QihooPayInfo;
import com.qh360.payUtil.BridgeCode;
import com.qh360.payUtil.Constants;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;
import com.qihoopay.sdk.protocols.ProtocolKeys;

/**
 * 执行付费
 * @author Rect
 * @version  Time：2013-5-8 
 */
public class Qh360Pay implements FREFunction {

	private String TAG = "Qh360Pay";
	private FREContext _context;


	// 登录响应模式：CODE模式。
	protected static final String RESPONSE_TYPE_CODE = "code";

	private static final String[] PAY_TYPE_VAL = new String[] {
		ProtocolKeys.PayType.ALIPAY,
		ProtocolKeys.PayType.MOBILE_CARD,
		ProtocolKeys.PayType.JCARD,
		ProtocolKeys.PayType.QIHOO_CARD,
		ProtocolKeys.PayType.QUICKLY_PAY,
		"unknown1",
		"unknown2"
	};
	private static final String[] PAY_TYPE_DES = new String[] {
		"支付宝",
		"充值卡",
		"骏网卡",
		"360币卡",
		"快捷支付",
		"未知卡1",
		"未知卡2"
	};
	private List<String> mPayTypeList = new ArrayList<String>(); 

	private TokenInfo mTokenInfo;

	private QihooUserInfo mQihooUserInfo;

	@Override
	public FREObject call(final FREContext context, FREObject[] $args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		Boolean isVisible = false;
		// TODO Auto-generated method stub

		Log.d(TAG, "---------支付开始-------");

		try
		{
			
			isVisible = $args[0].getAsBool();
			BridgeCode.GAME_USER_NAME = $args[1].getAsString();
			BridgeCode.GAME_PAY_APP_USER_ID = $args[2].getAsString();
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG,"参数错误！");
			return null;
		}
		//--------------------------------
		//在这里做付费的操作 我这里直接传回。。
		Log.d(TAG, "---------付费开始-------");
		// 调用360SDK定额支付（是否横屏显示）
		doSdkPay(BridgeCode.mIsLandscape, isVisible);
		//--------------------------------

		return result;
	}



	// 支付的回调
	private IDispatcherCallback mPayCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mPayCallback, data is " + data);
			JSONObject jsonRes;
			try {
				jsonRes = new JSONObject(data);
				// error_code 
				//状态码   0 支付成功      -1 支付取消 	1 支付失败 	2 支付进行中
				// error_msg 状态描述
				int errorCode = jsonRes.getInt("error_code");
				String errorMsg = jsonRes.getString("error_msg");
				Log.d(TAG, "---------付费返回-------errorCode:"+errorCode+":"+errorMsg);
				_context.dispatchStatusEventAsync(TAG,"付费返回:！"+errorMsg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};



	/**
	 * 使用360SDK的支付接口
	 * 
	 * @param isLandScape 是否横屏显示支付界面
	 * @param isFixed 是否定额支付
	 */
	private void toSdkPay(boolean isLandScape, boolean isFixed) {
		Log.d(TAG, "---------toSdkPay-------");
		QihooPayInfo pay = getQihooPayInfo(isFixed);

		int iLen = mPayTypeList.size();
		if(iLen > 0) {
			pay.setPayTypes((String[])mPayTypeList.toArray(new String[iLen]));
		}

		Intent intent = Qh360AllHandle.getInstance(_context).getPayIntent(isLandScape, pay);
		Matrix.invokeActivity(_context.getActivity(), intent, mPayCallback);
	}

	/**
	 * 使用360SDK的支付接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示支付界面
	 * @param isFixed
	 *            是否定额支付
	 */
	protected void doSdkPay(final boolean isLandScape, final boolean isFixed) {
		mPayTypeList.clear();
		new AlertDialog.Builder(this._context.getActivity())
		.setTitle("请定制支付方式")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMultiChoiceItems(PAY_TYPE_DES, null,
				new DialogInterface.OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which, boolean isChecked) {
				String payType = PAY_TYPE_VAL[which];
				if (isChecked
						&& !mPayTypeList.contains(payType)) {
					mPayTypeList.add(payType);
				} else if (!isChecked
						&& mPayTypeList.contains(payType)) {
					mPayTypeList.remove(payType);
				}
			}
		})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				toSdkPay(isLandScape, isFixed);
			}
		}).setNegativeButton("取消", null).show();
	}

	/**
	 * 钩子方法，留给使用支付的子类实现getQihooPayInfo
	 * @param isFixed
	 * @return
	 */
	protected QihooPayInfo getQihooPayInfo(boolean isFixed) {
		QihooPayInfo payInfo = null;
		if(isFixed) {
			payInfo = getQihooPay(Constants.DEMO_FIXED_PAY_MONEY_AMOUNT);
		}
		else {
			payInfo = getQihooPay(Constants.DEMO_NOT_FIXED_PAY_MONEY_AMOUNT);
		}

		return payInfo;
	}

	/***
	 * @param moneyAmount 金额数，使用者可以自由设定数额。金额数为100的整数倍，360SDK运行定额支付流程；
	 *            金额数为0，360SDK运行不定额支付流程。
	 * @return QihooPay
	 */
	private QihooPayInfo getQihooPay(String moneyAmount) {
		// 登录得到AccessToken和UserId，用于支付。TODO null的情况还让请求支付吗？
		String accessToken = (mTokenInfo != null) ? BridgeCode.mTokenInfo.getAccessToken() : null;
		String qihooUserId = (mQihooUserInfo != null) ? BridgeCode.mQihooUserInfo.getId() : null;
		Log.d(TAG, "---------doSdkPay-------"+accessToken + ":XXX:"+qihooUserId);
		// 创建QihooPay
		QihooPayInfo qihooPay = new QihooPayInfo();
		qihooPay.setAccessToken(accessToken);
		qihooPay.setQihooUserId(qihooUserId);

		// 360SDK从AndroidManifest的meta-data中读取的AppKey和 PrivateKey
		qihooPay.setAppKey(Matrix.getAppKey(this._context.getActivity()));
		qihooPay.setPrivateKey(Matrix.getPrivateKey(this._context.getActivity()));

		qihooPay.setMoneyAmount(moneyAmount);
		qihooPay.setExchangeRate(Constants.DEMO_PAY_EXCHANGE_RATE);

		qihooPay.setProductName(_context.getActivity().getString(_context.getResourceId("string.demo_pay_product_name")));
		qihooPay.setProductId(Constants.DEMO_PAY_PRODUCT_ID);

		qihooPay.setNotifyUri(Constants.DEMO_APP_SERVER_NOTIFY_URI);

		qihooPay.setAppName(_context.getActivity().getString(_context.getResourceId("string.demo_pay_app_name")));
		qihooPay.setAppUserName(BridgeCode.GAME_USER_NAME);
		qihooPay.setAppUserId(BridgeCode.GAME_PAY_APP_USER_ID);

		// 可选参数
		qihooPay.setAppExt1(_context.getActivity().getString(_context.getResourceId("string.demo_pay_app_ext1")));
		qihooPay.setAppExt2(_context.getActivity().getString(_context.getResourceId("string.demo_pay_app_ext2")));
		qihooPay.setAppOrderId("");

		return qihooPay;
	}
}
