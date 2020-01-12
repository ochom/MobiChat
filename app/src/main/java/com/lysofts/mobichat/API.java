package com.lysofts.mobichat;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.StringEntity;

public class API {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String API_HOST = "https://rest-api.mobidev-sandbox.com/api";

    public static void getData(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(API_HOST + url, params, responseHandler);
    }

    public static void postData(Context context, String url, StringEntity entity, String accept, AsyncHttpResponseHandler responseHandler) {
        client.post(context,API_HOST + url, entity, accept, responseHandler);

    }

    public static void deleteData(Context context,String url,AsyncHttpResponseHandler responseHandler) {
        client.delete(context,API_HOST + url, responseHandler);

    }
    public static void patchData(Context context,String url,  StringEntity entity, String accept, AsyncHttpResponseHandler responseHandler) {
        client.patch(context,API_HOST + url, entity, accept, responseHandler);
    }

}
