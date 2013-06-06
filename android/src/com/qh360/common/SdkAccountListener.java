
package com.qh360.common;


public interface SdkAccountListener {
    
    /**
     * 360SDK正式账号登录成功，返回授权码（授权码生存期只有60秒，必需立即请求应用服务器，以得到TokenInfo，QihooUserInfo）。
     */
    public void onGotAuthorizationCode(String code);

    /**
     * 360SDK试玩账号成功进入，返回TryAccount
     */
    public void onGotTryAccount(TryAccount tryAccount);
    
    /**
     * 360SDK试玩和快速游戏接口，正式账号和试玩账号都无法返回时，通过此方法返回
     */
    public void onGotError(int errCode);
    

}
