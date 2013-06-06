package com.qh360.ane.func;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.qh360.appserver.QihooUserInfo;
import com.qh360.appserver.QihooUserInfoListener;
import com.qh360.appserver.QihooUserInfoTask;
import com.qh360.appserver.TokenInfo;
import com.qh360.appserver.TokenInfoListener;
import com.qh360.appserver.TokenInfoTask;
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
 * 执行登录
 * @author Rect
 * @version  Time：2013-5-8 
 */
public class Qh360Login implements FREFunction,SdkAccountListener,TokenInfoListener,QihooUserInfoListener	 {

	// 登录响应模式：CODE模式。
    protected static final String RESPONSE_TYPE_CODE = "code";
 // 360SDK登录返回的Json字符串中的Json name，代表CODE模式登录返回的Authorization Code（授权码，60秒有效）。
    private static final String AUTH_CODE = "code";
    	
	private String TAG = "Qh360Login";
	private FREContext _context;
	    
	@Override
	public FREObject call(final FREContext context, FREObject[] args) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null;  
		boolean isLandScape = true;
		boolean isBgTransparent = false;
		// TODO Auto-generated method stub
		//--------------------------------
		//在这里做登录的操作 我这里直接传回。。
		try {
             isLandScape = args[0].getAsBool();
             isBgTransparent = args[1].getAsBool();
             BridgeCode.mIsLandscape = isLandScape;
		}
		catch(Exception e)
		{
			_context.dispatchStatusEventAsync(TAG, "登录传入参数错误："+e.getMessage());
			return null;
		}
		Log.d(TAG, "---------Login开始-------");
		doSdkLogin(isLandScape,isBgTransparent);
		//--------------------------------
		
		return result;
	}
	/**
	 * 登录回调 把登录结果传给AS端 一般都会把获得的游戏ID传回去  怎么传自己看着办
	 */
	public void callBack(String code){
		Log.d(TAG, "---------Login返回-------");
		String userID = "返回数据";
		userID += "*0";
		userID += "*"+code;
		_context.dispatchStatusEventAsync(TAG, userID);
	}
	
	// ---------------------------------调用360SDK接口------------------------------------

    /**
     * 使用360SDK的登录接口
     * 
     * @param isLandScape 是否横屏显示登录界面
     * @param isBgTransparent 是否以透明背景显示登录界面
     */
    protected void doSdkLogin(boolean isLandScape, boolean isBgTransparent) {
    	Log.d(TAG, "---------Login  doSdkLogin-------");
        Intent intent = getLoginIntent(isLandScape, isBgTransparent);
        Matrix.invokeActivity(_context.getActivity(), intent, mLoginCallback);
    }
    
    /**
     * 使用360SDK的登录接口
     * 
     * @param isLandScape 是否横屏显示登录界面
     * @param isBgTransparent 是否以透明背景显示登录界面
     * @param appKey 应用或游戏的AppKey
     * @param appChannel 应用或游戏的自定义子渠道
     */
    protected void doSdkLogin(boolean isLandScape, boolean isBgTransparent, String appKey, String appChannel) {
        Intent intent = getLoginIntent(isLandScape, isBgTransparent, appKey, appChannel);
        Matrix.invokeActivity(_context.getActivity(), intent, mLoginCallback);
    }
    
    // -----------------------------------参数Intent-------------------------------------

    /***
     * 生成调用360SDK登录接口的Intent
     * 
     * @param isLandScape 是否横屏
     * @param isBgTransparent 是否背景透明
     * @return Intent
     */
    private Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent) {
    	Log.d(TAG, "---------Login  getLoginIntent-------");
        return getLoginIntent(isLandScape, isBgTransparent, Matrix.getAppKey(_context.getActivity()), Matrix.getChannel());
    }
    
    /***
     * 生成调用360SDK登录接口的Intent
     * 
     * @param isLandScape 是否横屏
     * @param isBgTransparent 是否背景透明
     * @param appKey 应用或游戏的AppKey
     * @param appChannel 应用或游戏的自定义子渠道
     * @return Intent
     */
    private Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent, String appKey, String appChannel) {
    	
        Bundle bundle = new Bundle();
        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        // 界面相关参数，360SDK登录界面背景是否透明。
        bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

        // *** 以下非界面相关参数 ***
        // 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
        bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);
        Log.d(TAG, "---------Login  F-------");
        // 必需参数，手机的IMEI。
        bundle.putString(ProtocolKeys.APP_IMEI, getImei());
        Log.d(TAG, "---------Login  G-------");
        // 必需参数，App 版本号。
        bundle.putString(ProtocolKeys.APP_VERSION, Matrix.getAppVersionName(_context.getActivity()));
        // 必需参数，App Key。
        bundle.putString(ProtocolKeys.APP_KEY, appKey);
        // App 渠道号。
        bundle.putString(ProtocolKeys.APP_CHANNEL, appChannel);
        // 必需参数，使用360SDK的登录模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
        Intent intent = new Intent(_context.getActivity(), ContainerActivity.class);
        intent.putExtras(bundle);
        return intent;
    }    
    
    private String getImei() {
        return ((TelephonyManager) _context.getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    
    /**
     * 从Json字符中获取授权码
     * 
     * @param data Json字符串
     * @return 授权码
     */
    private String parseAuthorizationCode(String data) {
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
            TokenInfoTask.doRequest(this._context.getActivity(), authorizationCode, Matrix.getAppKey(this._context.getActivity()), this);
        }
    	callBack(authorizationCode);
    }
    
    private void clearLoginResult() {
    	BridgeCode.mTokenInfo = null;
    	BridgeCode.mQihooUserInfo = null;
        updateUserInfoUi();
    }
    
    private void updateUserInfoUi() {
    	_context.dispatchStatusEventAsync(TAG, getLoginResultText());
    }
    private String getLoginResultText() {
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
 // ---------------------------------360SDK接口的回调-----------------------------------

    // 登录、注册的回调
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {
    	
        @Override
        public void onFinished(String data) {
            Log.d(TAG, "mLoginCallback, data is " + data);
            String authorizationCode = parseAuthorizationCode(data);
            onGotAuthorizationCode(authorizationCode);
            
        }
    };
}
