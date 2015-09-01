package com.zhack.poskasir.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zunaidi.chandra on 10/08/2015.
 */
public class HttpConnect {

    public String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(15000);

        StringBuffer response = null;
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        Log.i("HttpConnect", "Url:" + url + " Response:" + con.getResponseCode() + "\nJSON: " + response);

        return null;
    }

    public int sendPost(String url, String params) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Log.i("HttpConnect", "Url:" + url + " Response:" + con.getResponseCode() + "\nJSON: " + response);

        return con.getResponseCode();
    }
}
