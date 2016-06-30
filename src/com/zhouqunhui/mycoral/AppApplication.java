package com.zhouqunhui.mycoral;

import com.zhouqunhui.mycoral.volley.RequestQueue;
import com.zhouqunhui.mycoral.volley.toolbox.Volley;

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
