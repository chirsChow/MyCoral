package com.zhouqunhui.mycoral.module;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class PayActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("转账入口");
		tv.setTextColor(Color.RED);
		setContentView(tv);
	}
	
	
}
