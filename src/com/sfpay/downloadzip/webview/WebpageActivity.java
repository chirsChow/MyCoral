package com.sfpay.downloadzip.webview;

import java.io.File;

import org.json.JSONObject;

import com.sfpay.downloadzip.AppApplication;
import com.sfpay.downloadzip.Contants;
import com.sfpay.downloadzip.R;
import com.sfpay.downloadzip.manager.ApplicationInfoManager;
import com.sfpay.downloadzip.util.FileUtils;
import com.sfpay.downloadzip.util.VerifiyUtil;
import com.sfpay.downloadzip.widget.ProgressWebView;
import com.sfpay.downloadzip.widget.ProgressWebView.OnH5LoadListener;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

public class WebpageActivity extends Activity {
	
	public static final String APP_URI = "APP_URI";

	private ProgressWebView webView;

	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		webView = (ProgressWebView) findViewById(R.id.webview);

		url = getIntent().getStringExtra(APP_URI);

		webView.setOnH5LoadListener(new OnH5LoadListener() {
			
			@Override
			public boolean shouldOverrideUrlLoading(String url) {
				verify(url);
				return false;
			}
			
			@Override
			public void onReceivedError(int errorCode, String description, String failingUrl) {
				setErrorView(failingUrl);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onJsAlert(String message) {
				// TODO Auto-generated method stub
				
			}
		});
		webView.addJavascriptInterface(new AndroidFunForJS(webView, this), "JavaScriptInterface");
		
		if (!TextUtils.isEmpty(url)) {
			webView.loadUrl(url);
		}
	}
	
	private void verify(String url) {
		JSONObject md5Obj = ApplicationInfoManager.getInstance().getMd5Result();
		
		Uri uri = Uri.parse(url);
		 if ("file".equals(uri.getScheme())) {
			String path = url.substring(url.indexOf(Contants.ZipFolder)+Contants.ZipFolder.length(), url.lastIndexOf("/"));
			
			File appsDir = null;
			if (Contants.isDebug) {
				appsDir = FileUtils.createSDCardDir(Contants.ZipFolder);
			} else {
				appsDir = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
			}
			File appDir = new File(appsDir, path);
			
			if (!VerifiyUtil.verifiy(appDir, md5Obj)) {
				Toast.makeText(WebpageActivity.this, R.string.verify_app_error, Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	private void setErrorView(String url) {
		Toast.makeText(this, "提示：加载失败，点击可重新加载。", 1).show();
	}

}
