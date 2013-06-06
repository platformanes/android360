
package com.qh360.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class TryAccount {

    private String name;
    private String qid;

    private boolean isNull = true;
    
    public TryAccount() {
        isNull = true;
    }
    
    public TryAccount(String jsonString) {
        isNull = true;
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject obj = new JSONObject(jsonString);
                name = obj.getString("name");
                qid = obj.getString("qid");
                isNull = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isNull() {
        return isNull;
    }

    public String toJsonString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("qid", qid);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public String getQid() {
        return qid;
    }

}
