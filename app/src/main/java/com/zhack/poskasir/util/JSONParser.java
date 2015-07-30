package com.zhack.poskasir.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {

	private final String HTTP_POST = "http_post";
	private final String HTTP_GET = "http_get";
	private JSONObject jsonObj;

	public JSONObject getJSONfromURL(String http, String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
		
		HttpGet httpGet;
		HttpPost httpPost;
		HttpResponse response = null;
		InputStream inputStream = null;
		
		try {
			if (http == HTTP_GET) {
				httpGet = new HttpGet(url);
				httpGet.setHeader("Content-type", "application/json");
				response = httpclient.execute(httpGet);           
			} else if (http == HTTP_POST) {
				httpPost = new HttpPost(url);
				httpPost.setHeader("Content-type", "application/json");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
				nameValuePairs.add(new BasicNameValuePair("userid", "12312"));  
				nameValuePairs.add(new BasicNameValuePair("sessionid", "234"));  
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
				
				response = httpclient.execute(httpPost);  
			}
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			// JSON is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			jsonObj = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jsonObj;
	}
	
//	public void postTransferStorageData(List<StorageData> storageData) {
//		// Post data to submit
//		jsonObj = getJSONfromURL(HTTP_POST, Constants.GETURL_POST_TRANSFER_STORAGE());
//	}
//
//	public List<StorageData> getSuggestionStorageList(String tsfId) {
//		// Retrieve Suggestion List of Storage
//		jsonObj = getJSONfromURL(HTTP_GET, Constants.GETURL_GET_JSON_SUGGESTION_STORAGE() + "?" + "tsfId=" + tsfId);
//
//		List<StorageData> storageData = new ArrayList<StorageData>();
//
//		JSONArray contentArr;
//		try {
//			contentArr = jsonObj.getJSONArray("content");
//			for (int i = 0; i < contentArr.length(); i++) {
//				StorageData data = new StorageData();
//				data.kodeRak = contentArr.getString(i);
//				data.kodeBarang = contentArr.getString(i);
//				data.jumlah = "Pura Pura nya 100000000 Semua";
//				storageData.add(data);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return storageData;
//	}
//
//	public boolean validateStorageCode(String storageCode, String warehouseCode) {
//		// Checking Storage
//		jsonObj = getJSONfromURL(HTTP_GET, Constants.GETURL_GET_JSON_CHECK_STORAGE() + "?" + "storageCode=" + storageCode + "&" + "warehouseCode=" + warehouseCode);
//
//		try {
//			if (jsonObj != null && jsonObj.getString("content").equals("true")) {
//				return true;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	public boolean validateWarehouseCode(String warehouseCode) {
//		// Checking Warehouse
//		jsonObj = getJSONfromURL(HTTP_GET, Constants.GETURL_GET_JSON_CHECK_WAREHOUSE() + "?" + "warehouseCode=" + warehouseCode);
//
//		try {
//			if (jsonObj != null && jsonObj.getString("content").equals("true")) {
//				return true;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	public boolean validatePickerCode(String pickerCode) {
//		// Checking Picker
//		jsonObj = getJSONfromURL(HTTP_GET, Constants.GETURL_GET_JSON_CHECK_PICKER() + "?" + "pickerCode=" + pickerCode);
//
//		try {
//			if (jsonObj != null && jsonObj.getString("content").equals("true")) {
//				return true;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
}
