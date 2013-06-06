
package com.qh360.appserver;

/**
 * 此接口由应用客户端与应用服务器协商决定。
 */
public interface TokenInfoListener {

    public void onGotTokenInfo(TokenInfo tokenInfo);

}
