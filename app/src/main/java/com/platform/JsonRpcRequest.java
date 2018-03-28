package com.platform;

import android.content.Context;
import android.util.Log;

import com.breadwallet.BreadApp;
import com.breadwallet.presenter.activities.util.ActivityUTILS;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by byfieldj on 3/26/18.
 */

public class JsonRpcRequest {

    private static final String TAG = "JsonRpcRequest";
    private JsonRpcRequestListener mRequestListener;
    private String mResponse;

    public JsonRpcRequest() {
    }

    public interface JsonRpcRequestListener {

        void onRpcRequestCompleted(String jsonResult);
    }


    public Response makeRpcRequest(Context app, String url, JSONObject payload, JsonRpcRequestListener listener) {

        this.mRequestListener = listener;

        if (ActivityUTILS.isMainThread()) {
            Log.e(TAG, "makeRpcRequest: network on main thread");
            throw new RuntimeException("network on main thread");
        }


        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        Log.d(TAG, "JSON params -> " + payload.toString());


        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(requestBody);


        String response = null;
        Request request = builder.build();
        Response resp = null;

        try {

            resp = APIClient.getInstance(BreadApp.getBreadContext()).sendRequest(request, true, 0);

            String responseString = resp.body().string();

            Log.d(TAG, "RPC response - > " + responseString);

            if (mRequestListener != null) {
                mRequestListener.onRpcRequestCompleted(responseString);
            }


            if (resp == null) {

                Log.e(TAG, "makeRpcRequest: " + url + ", resp is null");
                return null;

            } else {
                setResponseString(responseString);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return resp;

    }

    private void setResponseString(String response) {
        this.mResponse = response;
    }

    public String getResponseString() {
        return this.mResponse;
    }


}