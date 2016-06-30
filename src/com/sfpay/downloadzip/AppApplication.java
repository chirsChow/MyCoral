package com.sfpay.downloadzip;

import com.sfpay.downloadzip.volley.RequestQueue;
import com.sfpay.downloadzip.volley.toolbox.Volley;

import android.app.Application;

public class AppApplication extends Application {

	private RequestQueue mQueue;

	private static AppApplication app;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
	}
	/**
	 * Volley网络请求队列对象
	 */
	public synchronized RequestQueue getRequestQueue() {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(this);
		}
		return mQueue;
	}

	public static AppApplication getApplication() {
		return app;
	}
}
