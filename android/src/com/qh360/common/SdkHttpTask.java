
package com.qh360.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.qihoopay.insdk.utils.HttpUtils;

/***
 * 通过http访问应用服务器，获取http返回结果
 */
public class SdkHttpTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "SdkHttpTask";
    
    private static final int MAX_RETRY_TIME = 3;
    
    private int mRetryCount;

    private SdkHttpListener mListener;

    private ArrayList<NameValuePair> mKeyValueArray;

    private boolean mIsHttpPost;

    private Context mContext;

    public SdkHttpTask(Context context) {
        mContext = context;
    }

    public void doPost(SdkHttpListener listener, ArrayList<NameValuePair> keyValueArray,
            String url) {
        this.mListener = listener;
        this.mIsHttpPost = true;
        this.mKeyValueArray = keyValueArray;
        this.mRetryCount = 0;

        execute(url);
    }

    public void doGet(SdkHttpListener listener, String url) {
        this.mListener = listener;
        this.mIsHttpPost = false;
        this.mRetryCount = 0;

        execute(url);
    }

    @Override
    protected String doInBackground(String... params) {
        
        String response = null;
        while (response == null && mRetryCount < MAX_RETRY_TIME) {

            if (isCancelled())
                return null;

            try {
                String uri = params[0];
                
                Log.d(TAG, this.toString() + "||mRetryCount=" + mRetryCount);
                Log.d(TAG, this.toString() + "||request=" + uri);
                HttpResponse httpResp = executeHttp(mContext, uri);
                if (httpResp != null && !isCancelled()) {

                    int st = httpResp.getStatusLine().getStatusCode();
                    Log.d(TAG, this.toString() + "||st=" + st);
                    // if (st == HttpStatus.SC_OK) {
                    HttpEntity entity = httpResp.getEntity();
                    if (entity != null) {
                        InputStream content = entity.getContent();
                        if (content != null) {
                            response = convertStreamToString(content);
                        }
                    }
                    // }
                }
            } catch (SSLHandshakeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            Log.d(TAG, this.toString() + "||response=" + response);
            
            mRetryCount++;
        }

        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mListener != null) {
            Log.d(TAG, this.toString() + "||onCancelled");
            mListener.onCancelled();
            mListener = null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (mListener != null && !isCancelled()) {
            Log.d(TAG, this.toString() + "||onResponse");
            mListener.onResponse(response);
            mListener = null;
        }
    }

    private HttpResponse executeHttp(Context context, String uri) throws SSLHandshakeException,
            ClientProtocolException, IOException {
        return mIsHttpPost ? HttpUtils.post(context, uri, mKeyValueArray) : HttpUtils.get(context, uri);
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
