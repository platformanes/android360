package com.qh360.ane.func;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.qh360.appserver.QihooUserInfo;
import com.qh360.appserver.QihooUserInfoListener;
import com.qh360.appserver.QihooUserInfoTask;
import com.qh360.appserver.TokenInfo;
import com.qh360.appserver.TokenInfoListener;
import com.qh360.common.QihooPayInfo;
import com.qh360.common.SdkAccountListener;
import com.qh360.common.TryAccount;
import com.qh360.payUtil.BridgeCode;
import com.qh360.payUtil.Constants;
import com.qihoopay.insdk.activity.ContainerActivity;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;
import com.qihoopay.sdk.protocols.ProtocolConfigs;
import com.qihoopay.sdk.protocols.ProtocolKeys;

/**
 * @author Rect
 * @version  Time：2013-6-6 
 */
public class Qh360AllHandle implements TokenInfoListener, QihooUserInfoListener,SdkAccountListener {

	public  FREContext _context;
	public  String TAG = "Qh360AllHandle";
	private static Qh360AllHandle instance;
	protected static final String RESPONSE_TYPE_CODE = "code";
	// 360SDK登录返回的Json字符串中的Json name，代表CODE模式登录返回的Authorization Code（授权码，60秒有效）。
	private static final String AUTH_CODE = "code";
	public static Qh360AllHandle getInstance(FREContext value)
	{
		if(instance == null)
			instance = new Qh360AllHandle();
		if(instance._context != value)
			instance._context = value;
		return instance;
	}

	
	/**
	 * 360SDK试玩和快速游戏接口，正式账号和试玩账号都无法返回时，通过此方法返回
	 */
	public void onGotError(int errCode)
	{
		Log.d(TAG, "onGotError" );
	}
	/**
	 * 360SDK试玩账号成功进入，返回TryAccount
	 */
	public void onGotTryAccount(TryAccount tryAccount)
	{
		Log.d(TAG, "onGotTryAccount" );
		BridgeCode.mTryAccount = tryAccount;
	}

	/**
	 * 应用服务器通过此方法返回AccessToken
	 */
	@Override
	public void onGotTokenInfo(TokenInfo tokenInfo) {

		if (tokenInfo == null || TextUtils.isEmpty(tokenInfo.getAccessToken())) {
			//            ProgressUtil.dismiss(mProgress);
			_context.dispatchStatusEventAsync(
					TAG, 
					this._context.getActivity().getString(this._context.getResourceId("string.get_token_fail")));
		} else {
			// 保存TokenInfo
			BridgeCode.mTokenInfo = tokenInfo;

			// 界面显示AccessToken
			updateUserInfoUi();

			// 请求应用服务器，用AccessToken换取UserInfo
			QihooUserInfoTask.doRequest(this._context.getActivity(), tokenInfo.getAccessToken(), Matrix.getAppKey(this._context.getActivity()), this);
		}

	}

	/**
	 * 应用服务器通过此方法返回UserInfo
	 */
	@Override
	public void onGotUserInfo(QihooUserInfo userInfo) {


		if (userInfo != null && userInfo.isValid()) {
			// 保存QihooUserInfo
			BridgeCode.mQihooUserInfo = userInfo;

			// 界面显示QihooUser的Id和Name
			updateUserInfoUi();
			Intent intent = this._context.getActivity().getIntent();
			intent.putExtra(Constants.TOKEN_INFO, BridgeCode.mTokenInfo.toJsonString());
			intent.putExtra(Constants.QIHOO_USER_INFO, BridgeCode.mQihooUserInfo.toJsonString());

		} else {
			_context.dispatchStatusEventAsync(
					TAG, 
					this._context.getActivity().getString(this._context.getResourceId("string.get_user_fail")));
		}
	}

	@Override
	public void onGotAuthorizationCode(String authorizationCode)
	{
		if (TextUtils.isEmpty(authorizationCode)) {
			_context.dispatchStatusEventAsync(TAG, "返回的code数据为空!");
		} else {
			clearLoginResult();

			// 请求应用服务器，用AuthorizationCode换取AccessToken
			com.qh360.appserver.TokenInfoTask.doRequest(this._context.getActivity(), authorizationCode, Matrix.getAppKey(this._context.getActivity()), this);
		}
	}


	public void clearLoginResult() {
		BridgeCode.mTokenInfo = null;
		BridgeCode.mQihooUserInfo = null;
		updateUserInfoUi();
	}

	public void updateUserInfoUi() {
		_context.dispatchStatusEventAsync(TAG, getLoginResultText());
	}
	public String getLoginResultText() {
		String result;
		if (BridgeCode.mTokenInfo == null) {
			result = this._context.getActivity().getString(this._context.getResourceId("string.nologin"));
		} else {
			result = this._context.getActivity().getString(this._context.getResourceId("string.formal_account"));
			if (BridgeCode.mTokenInfo != null && !TextUtils.isEmpty(BridgeCode.mTokenInfo.getAccessToken())) {
				result += ("TokenInfo=" + BridgeCode.mTokenInfo.toJsonString() + "\n\n");
			}

			if (BridgeCode.mQihooUserInfo != null && BridgeCode.mQihooUserInfo.isValid()) {
				result += ("UserInfo=" + BridgeCode.mQihooUserInfo.toJsonString());
			}
		}

		return result;
	}

	/**
	 * 从Json字符中获取授权码
	 * 
	 * @param data Json字符串
	 * @return 授权码
	 */
	public String parseAuthorizationCode(String data) {
		String authorizationCode = null;
		if (!TextUtils.isEmpty(data)) {
			try {
				JSONObject json = new JSONObject(data);
				int errCode = json.optInt("error_code");
				if (errCode == 0) {
					// 只支持code登陆模式
					JSONObject content = json.optJSONObject("content");
					authorizationCode = content.optString(AUTH_CODE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Log.d(TAG, "parseAuthorizationCode=" + authorizationCode);
		return authorizationCode;
	}

	/***
	 * 生成调用360SDK论坛接口的Intent
	 * 
	 * @param isLandScape
	 * @return Intent
	 */
	public Intent getBBSIntent(boolean isLandScape) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);
		// 必需参数，App Key。
		bundle.putString(ProtocolKeys.APP_KEY, Matrix.getAppKey(this._context.getActivity()));
		// 必需参数，使用360SDK的论坛模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_BBS);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}
	/***
	 * 生成调用360SDK退出接口的Intent
	 * 
	 * @param isLandScape
	 * @return Intent
	 */
	public  Intent getQuitIntent(boolean isLandScape) {
		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);
		// 必需参数，App Key。
		bundle.putString(ProtocolKeys.APP_KEY, Matrix.getAppKey(this._context.getActivity()));
		// 必需参数，使用360SDK的退出模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_QUIT);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}
	/***
	 * 生成实名注册登录接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @param isBgTransparent
	 *            是否背景透明
	 * @param qihooUserId
	 *            奇虎UserId
	 * @return Intent
	 */
	public Intent getRealNameRegisterIntent(boolean isLandScape,
			boolean isBgTransparent, String qihooUserId) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 背景是否透明
		bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

		// 必需参数，360账号id，整数。
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, qihooUserId);

		// 必需参数，使用360SDK的实名注册模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_REAL_NAME_REGISTER);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}

	/***
	 * 生成调用360SDK切换账号接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @param isBgTransparent
	 *            是否背景透明
	 * @return Intent
	 */
	public Intent getSwitchAccountIntent(boolean isLandScape,
			boolean isBgTransparent) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 界面相关参数，360SDK登录界面背景是否透明。
		bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

		// *** 以下非界面相关参数 ***

		// 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
		bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

		// 必需参数，手机的IMEI。
		bundle.putString(ProtocolKeys.APP_IMEI, getImei());

		// 必需参数，App 版本号。
		bundle.putString(ProtocolKeys.APP_VERSION,
				Matrix.getAppVersionName(this._context.getActivity()));

		// 必需参数，App Key。
		bundle.putString(ProtocolKeys.APP_KEY, Matrix.getAppKey(this._context.getActivity()));

		// App 渠道号。
		bundle.putString(ProtocolKeys.APP_CHANNEL, Matrix.getChannel());

		// 必需参数，使用360SDK的切换账号模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}

	/***
     * 生成调用360SDK支付接口的Intent
     * 
     * @param isLandScape
     * @param pay
     * @return Intent
     */
    public Intent getPayIntent(boolean isLandScape, QihooPayInfo pay) {
        Bundle bundle = new Bundle();
        
        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // *** 以下非界面相关参数 ***

        // 设置QihooPay中的参数。
        // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
        bundle.putString(ProtocolKeys.ACCESS_TOKEN, pay.getAccessToken());

        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

        // 必需参数，应用app key。
        bundle.putString(ProtocolKeys.APP_KEY, pay.getAppKey());

        // 必需参数，值为md5(app_secret +“#”+
        // app_key)全小写，用于签名的密钥不能把app_secret写到应用客户端程序里因此使用这样一个特殊的KEY，应算出值直接写在app中，而不是写md5的计算过程。
        bundle.putString(ProtocolKeys.PRIVATE_KEY, pay.getPrivateKey());

        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

        // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
        bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

        // 可选参数，应用扩展信息1，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

        // 可选参数，应用扩展信息2，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

        // 可选参数，定制支付类型
        bundle.putStringArray(ProtocolKeys.PAY_TYPE, pay.getPayTypes());

        // 必需参数，使用360SDK的支付模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

        Intent intent = new Intent(_context.getActivity(), ContainerActivity.class);
        intent.putExtras(bundle);
        
        return intent;
    }
	/***
	 * 生成调用360SDK试玩注册接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @param isBgTransparent
	 *            是否背景透明
	 * @param tryAccount
	 *            试玩账号
	 * @return Intent
	 */
	public Intent getTryAccountRegisterIntent(boolean isLandScape,
			boolean isBgTransparent, TryAccount tryAccount) {
		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 界面相关参数，360SDK登录界面背景是否透明。
		bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

		// *** 以下非界面相关参数 ***

		// 试玩账号的name qid
		bundle.putString(ProtocolKeys.TRY_ACCOUNT_NAME, tryAccount.getName());

		bundle.putString(ProtocolKeys.TRY_ACCOUNT_QID, tryAccount.getQid());

		// 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
		bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

		// 必需参数，手机的IMEI。
		bundle.putString(ProtocolKeys.APP_IMEI, getImei());

		// 必需参数，App 版本号。
		bundle.putString(ProtocolKeys.APP_VERSION,
				Matrix.getAppVersionName(this._context.getActivity()));

		// 必需参数，App Key。
		bundle.putString(ProtocolKeys.APP_KEY, Matrix.getAppKey(this._context.getActivity()));

		// App 渠道号。
		bundle.putString(ProtocolKeys.APP_CHANNEL, Matrix.getChannel());

		// 必需参数，使用360SDK的快速游戏模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_TRY_ACCOUNT_REGISTER);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}

	public String getImei() {
		return ((TelephonyManager) this._context.getActivity().getSystemService(Context.TELEPHONY_SERVICE))
		.getDeviceId();
	}

	// -----------------------------------------防沉迷查询接口----------------------------------------

	/**
	 * 本方法中的callback实现仅用于测试, 实际使用由游戏开发者自己处理
	 * 
	 * @param qihooUserId
	 * @param accessToken
	 */
	protected void doSdkAntiAddictionQuery(String qihooUserId,
			String accessToken) {
		Intent intent = getAntiAddictionIntent(qihooUserId, accessToken);
		Matrix.execute(this._context.getActivity(), intent, new IDispatcherCallback() {

			@Override
			public void onFinished(String data) {
				Log.d("demo,anti-addiction query result = ", data);
				if (!TextUtils.isEmpty(data)) {
					try {
						JSONObject resultJson = new JSONObject(data);
						int errorCode = resultJson.getInt("error_code");
						if (errorCode == 0) {
							JSONObject contentData = resultJson
							.getJSONObject("content");
							// 保存登录成功的用户名及密码
							JSONArray retData = contentData.getJSONArray("ret");
							Log.d(TAG, "ret data = " + retData);
							int status = retData.getJSONObject(0).getInt(
							"status");
							Log.d(TAG, "status = " + status);
							if (status == 0) {
								_context.dispatchStatusEventAsync(
										TAG,
										_context.getActivity().getString(_context.getResourceId("string.anti_addiction_query_result_0")));
							} else if (status == 1) {
								_context.dispatchStatusEventAsync(
										TAG,
										_context.getActivity().getString(_context.getResourceId("string.anti_addiction_query_result_1")));
							} else if (status == 2) {
								_context.dispatchStatusEventAsync(
										TAG,
										_context.getActivity().getString(_context.getResourceId("string.anti_addiction_query_result_2")));
							}
						} else {
							_context.dispatchStatusEventAsync(
									TAG,
									resultJson.getString("error_msg"));
						}

					} catch (JSONException e) {
						_context.dispatchStatusEventAsync(
								TAG,
								_context.getActivity().getString(_context.getResourceId("string.anti_addiction_query_exception")));
						e.printStackTrace();
					}
				}
			}
		});
	}

	/***
	 * 生成调用360SDK快速游戏接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @return Intent
	 */
	public Intent getQuickPlayIntent(boolean isLandScape) {
		Bundle bundle = new Bundle();
		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// *** 以下非界面相关参数 ***

		// 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
		bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

		// 必需参数，手机的IMEI。
		bundle.putString(ProtocolKeys.APP_IMEI, getImei());

		// 必需参数，App 版本号。
		bundle.putString(ProtocolKeys.APP_VERSION,
				Matrix.getAppVersionName(this._context.getActivity()));

		// 必需参数，App Key。
		bundle.putString(ProtocolKeys.APP_KEY, Matrix.getAppKey(this._context.getActivity()));

		// App 渠道号。
		bundle.putString(ProtocolKeys.APP_CHANNEL, Matrix.getChannel());

		// 必需参数，使用360SDK的快速游戏模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_QUICK_PLAY);

		Intent intent = new Intent(this._context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}
	/**
	 * 生成防沉迷查询接口的Intent参数
	 * 
	 * @param qihooUserId
	 * @param accessToken
	 * @return Intent
	 */
	private Intent getAntiAddictionIntent(String qihooUserId, String accessToken) {

		Bundle bundle = new Bundle();
		// 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
		bundle.putString(ProtocolKeys.ACCESS_TOKEN, accessToken);
		// 必需参数，360账号id，整数。
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, qihooUserId);
		// 必需参数，使用360SDK的防沉迷查询模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_ANTI_ADDICTION_QUERY);

		Intent intent = new Intent(_context.getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}
}
