package com.icloud.listenbook.http;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyUtils {
	static VolleyUtils instance;
	RequestQueue requestQueue;

	public static synchronized VolleyUtils instance() {
		if (instance == null)
			instance = new VolleyUtils();
		return instance;
	}

	public void init(Context context) {
		requestQueue = Volley.newRequestQueue(context.getApplicationContext());
	}

	public void get(String url, Response.Listener<String> responseList,
			Response.ErrorListener erroList) {
		StringRequest request = new StringRequest(url, responseList, erroList);
		if (requestQueue != null)
			requestQueue.add(request);
	}

	public void post(String url, JSONObject josn,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
				url, josn, responseList, erroList);
		request.isEncrypted(true);//加密post请求

		if (requestQueue != null)
			requestQueue.add(request);
	}
}
