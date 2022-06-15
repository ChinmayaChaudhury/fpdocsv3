package com.ntuc.vendorservice.foundationcontext.utils;


import org.json.JSONArray;
import org.json.JSONObject;

public class AdapterUtils {


 /**
     * Read the connectivity service binding details
     * @return
     */
    public static JSONObject getConnectivityAuthServiceCredentials(){
        JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
        JSONArray jsonArr = jsonObj.getJSONArray("connectivity");
        JSONObject connectivityCredentials = jsonArr.getJSONObject(0).getJSONObject("credentials");
        return connectivityCredentials;
    }

}