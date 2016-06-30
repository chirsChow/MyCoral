package com.zhouqunhui.mycoral.module;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
/**
 * 本地应用入口
 * @author 837781
 *
 */
public class AccountActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("帐户入口");
		tv.setTextColor(Color.GREEN);
		setContentView(tv);
	}
	
	
}
