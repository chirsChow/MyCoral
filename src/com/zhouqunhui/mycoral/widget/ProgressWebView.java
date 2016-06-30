package com.zhouqunhui.mycoral.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView {
    private static final String TAG="ProgressWebView";
	private ProgressBar progressbar;
	private OnH5LoadListener listener;
	/**
	 * 特殊协议头前缀
	 */
	private List<String> schemes = new ArrayList<String>(10);
	
	public ProgressWebView(Context context) {
		this(context, null);
	}

	public ProgressBar getProgressbar() {
		return progressbar;
	}

	public void setOnH5LoadListener(OnH5LoadListener listener) {
		this.listener = listener;
	}
	/**
	 * 
	 * 方法说明：<br>
	 * 添加特殊协议头
	 * @param scheme
	 * Anthor:程庞钢(hunter.cheng)
	 */
	public void add(String scheme){
		schemes.add(scheme);
	}
	@SuppressLint("NewApi")
	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
//		progressbar.setBackgroundResource(R.color.transparent);
		setBackgroundColor(Color.parseColor("#00000000"));
		ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.parseColor("#f34b4e")), Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressbar.setProgressDrawable(d);
		
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 6, 0, 0));
		addView(progressbar);
		// 触摸焦点起作用：如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件
		requestFocus(View.FOCUS_DOWN);
		setBackgroundColor(0);
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);
		setTag("");
		
		WebSettings webSettings = getSettings();
		webSettings.setBlockNetworkImage(false);
		webSettings.setAllowFileAccess(true); // 设置允许访问文件数据
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setDefaultTextEncodingName("utf-8"); // 设置编码
		webSettings.setDatabaseEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true); // 支持js互调
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 允许js弹出窗口
		webSettings.setPluginState(PluginState.ON);

		setWebViewClient(new DialogWebViewClient());
		setWebChromeClient(new MyWebChromeClient());
		try{
			removeJavascriptInterface("searchBoxJavaBridge_");
			removeJavascriptInterface("accessibility")  ;
			removeJavascriptInterface("accessibilityTraversal");
		}catch(Throwable e){
			
		}
	}
	public class DialogWebViewClient extends WebViewClient {

		@Override
		// 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(TextUtils.isEmpty(url)){
				return false;
			}
			if(listener!=null){
				return listener.shouldOverrideUrlLoading(url);
			}
			return false;
		}

		// 加载错误时要做的工作
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			if(TextUtils.isEmpty(failingUrl)){
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			if (errorCode == -10
					&& startsWithInList(failingUrl,schemes)) {
				shouldOverrideUrlLoading(view, failingUrl);
				return;
			}
			
			if(listener!=null){
				listener.onReceivedError(errorCode, description, failingUrl);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if(listener != null){
				listener.onPageFinished(view, url);
			}
		}
	}
	
	public class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						progressbar.setVisibility(GONE);
					}
				}, 100);
				progressbar.setProgress(100);
			} else {
				if (progressbar.getVisibility() == GONE) {
					progressbar.setVisibility(VISIBLE);
				}
				if (newProgress > 10) {
					progressbar.setProgress(newProgress);
				}

			}
			super.onProgressChanged(view, newProgress);
		}
		
		@Override
		// 修改h5页面的弹框样式
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			if(listener!=null){
				listener.onJsAlert(message);
			}
			result.confirm();
			return true;
		}
	}

	/**
	 * 
	 * 方法说明：<br>
	 * TODO 可以提取功能方法
	 * 判断key是不是以集合中的某个元素开头
	 * @param key
	 * @param list
	 * @return 是则返回true  否则返回false
	 * Anthor:程庞钢(hunter.cheng)
	 */
	private boolean startsWithInList(String key,List<String> list){
		for (String string : list) {
			if(key.startsWith(string)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void loadUrl(String url) {
		Log.d(TAG, "打开URL==>" + url);
		super.loadUrl(url);
		progressbar.setProgress(10);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	/**
	 * 类说明：<br>
	 * 
	 * 
	 * <p>
	 * 详细描述：<br>
	 * H5监听事件
	 * 监听JS 模式对话框弹出事件
	 * 监听网页加载成功事件
	 * 监听网页加载失败事件
	 * 
	 * CreateDate: 2015年10月10日
	 */
	public interface OnH5LoadListener {
	     public void onJsAlert(String message);
	     /**
	      * 
	      * 方法说明：<br>
	      * 
	      * @param errorCode
	      * @param description
	      * @param failingUrl  取值不为空与null
	      * Anthor:程庞钢(hunter.cheng)
	      */
	     public void onReceivedError(int errorCode,
					String description, String failingUrl);
	     /**
	      * 
	      * 方法说明：<br>
	      * 
	      * @param url 取值不为空与null
	      * @return
	      * Anthor:程庞钢(hunter.cheng)
	      */
	     public boolean shouldOverrideUrlLoading(String url);
	     
	     /**
	      * 网页加载完毕
	      * @param view
	      * @param url
	      */
	     public void onPageFinished(WebView view, String url);
	}


}
