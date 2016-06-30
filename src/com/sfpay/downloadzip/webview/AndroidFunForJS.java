package com.sfpay.downloadzip.webview;

import org.json.JSONException;
import org.json.JSONObject;

import com.sfpay.downloadzip.AppApplication;
import com.sfpay.downloadzip.R;
import com.sfpay.downloadzip.util.Md5;
import com.sfpay.downloadzip.volley.Response;
import com.sfpay.downloadzip.volley.VolleyError;
import com.sfpay.downloadzip.volley.toolbox.JsonObjectRequest;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class AndroidFunForJS {

	private Context mContext;
	protected WebView mWebView;
	
	private static final String TAGJSONOBJECTREQUEST = "jsonObjectRequestTag";

	public AndroidFunForJS(WebView mWebView, Context context) {
		this.mContext = context;
		this.mWebView = mWebView;
	}

	@JavascriptInterface
	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}
	/**
	 * android的异常网络请求的结果与javascript的交互，通过eval实现js的成功回调和失败回调
	 * @param url js发起的网络请求URL
	 * @param jsonParam js发起的网络请求参数
	 * @param successFun 请求成功回调javascript的方法
	 * @param errorFun 请求失败回调javascript的方法 
	 */
	@JavascriptInterface
	public void jsonObjectRequest(String url, String jsonParam, final String successFun, final String errorFun) {
		JSONObject params = null;
		if (!TextUtils.isEmpty(jsonParam)) {
			try {
				params = new JSONObject(jsonParam);
			} catch (JSONException e) {
				if (!TextUtils.isEmpty(errorFun)) {
					String action = "javascript:(eval(" + errorFun + ".call(this,\"" + Md5.base64Encode(e.getMessage()) + "\")" + "()" + "))";
					if (action != null && mWebView != null) {
						mWebView.loadUrl(action);
					}
				}
				Toast.makeText(mContext, R.string.request_param_error, Toast.LENGTH_LONG).show();
			}
		}
		AppApplication.getApplication().getRequestQueue().cancelAll(TAGJSONOBJECTREQUEST);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject obj) {
				if (!TextUtils.isEmpty(successFun)) {
					String resultJson = obj.toString();
					String action = "javascript:(eval(" + successFun + ".call(this,\"" + Md5.base64Encode(resultJson) + "\")" + "()" + "))";
					if (action != null && mWebView != null) {
						mWebView.loadUrl(action);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (!TextUtils.isEmpty(errorFun)) {
					String action = "javascript:(eval(" + errorFun + ".call(this,\"" + Md5.base64Encode(error.getMessage()) + "\")" + "()" + "))";
					if (action != null && mWebView != null) {
						mWebView.loadUrl(action);
					}
				}
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
			}
		});
		jsonRequest.setTag(TAGJSONOBJECTREQUEST);
		AppApplication.getApplication().getRequestQueue().add(jsonRequest);
	}
	
}
