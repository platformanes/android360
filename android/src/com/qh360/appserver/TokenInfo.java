
package com.qh360.appserver;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class TokenInfo {

    private String accessToken; // Access Token值，缺省返回。

    private Long expiresIn; // Access Token的有效期 以秒计，缺省返回。

    private String scope; // Access Token最终的访问范围，即用户实际授予的权限列表当前只支持值basic，缺省返回。

    private String refreshToken; // 用于刷新Access Token的Token, 有效期14天，缺省返回。

    /***
     * 从应用服务器返回的数据解析出AccessToken。 此处数据格式由，应用服务器和应用客户端之间自行商议决定。
     * 不必参考Demo的数据格式，只需应用服务器和应用客户端商定即可。
     * 目的就是应用客户端使用AuthorizationCode，请求您的应用服务器，以获取TokenInfo。
     * （注：应用服务器和360服务器交互协议，请查看文档中，服务器端接口）
     * 此处json示例：{"status":"ok","data":{"access_token"
     * :"13915949ca7f20dfe9bb4a69a655eab83b05614825f66d2d"
     * ,"expires_in":"36000","scope":"basic","refresh_token":
     * "13915949cd2690b03ac9405fe9baab0c9ab02f1381522cb6"}}
     */
    public static TokenInfo parseJson(String jsonString) {
        TokenInfo tokenInfo = null;
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                String status = jsonObj.getString("status");
                JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                if (status != null && status.equals("ok")) {
                    // 必返回项
                    String accessToken = dataJsonObj.getString("access_token");
                    Long expiresIn = dataJsonObj.getLong("expires_in");
                    String scope = dataJsonObj.getString("scope");
                    String refreshToken = dataJsonObj.getString("refresh_token");

                    tokenInfo = new TokenInfo();
                    tokenInfo.setAccessToken(accessToken);
                    tokenInfo.setExpiresIn(expiresIn);
                    tokenInfo.setScope(scope);
                    tokenInfo.setRefreshToken(refreshToken);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tokenInfo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String toJsonString() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("status", "ok");

            JSONObject dataObj = new JSONObject();
            dataObj.put("access_token", accessToken);
            dataObj.put("expires_in", expiresIn);
            dataObj.put("scope", scope);
            dataObj.put("refresh_token", refreshToken);

            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
