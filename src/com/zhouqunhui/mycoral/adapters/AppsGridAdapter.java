package com.zhouqunhui.mycoral.adapters;

import java.util.ArrayList;
import java.util.List;

import com.zhouqunhui.mycoral.R;
import com.zhouqunhui.mycoral.manager.ApplicationInfoManager;
import com.zhouqunhui.mycoral.model.ApplicationInfo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 应用九宫格，适配器
 * @author 837781
 *
 */
public class AppsGridAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ApplicationInfoManager applicationInfoManager;
	private List<ApplicationInfo> applicationInfos;

	static class ViewHolder {
		TextView appName;
		ImageView appImage;
	}

	public AppsGridAdapter(Activity activity) {
		this.inflater = activity.getLayoutInflater();
		applicationInfoManager = ApplicationInfoManager.getInstance();
		//该用户可用的app列表
		List<String> availableApp = new ArrayList<String>();
		availableApp.add("com.tencent.mobileqq");
		availableApp.add("com.miui.notes");
		availableApp.add("com.android.fileexplorer");
		availableApp.add("com.sfpay.webapp");
		applicationInfos = applicationInfoManager.initAvailableApplicationInfos(availableApp);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.app_grid_cell, null);
			viewHolder = new ViewHolder();
			viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
			viewHolder.appImage = (ImageView) convertView.findViewById(R.id.app_image);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ApplicationInfo appInfo = applicationInfos.get(position);

		if (ApplicationInfo.TYPE_WEB.equals(appInfo.getType())) {
			viewHolder.appName.setText(appInfo.getName());
			viewHolder.appImage.setImageResource(R.drawable.web_app);
		} else {
			viewHolder.appName.setText(applicationInfoManager.getNativeApplicationName(appInfo.getId()));
			viewHolder.appImage.setImageResource(applicationInfoManager.getNativeApplicationIcon(appInfo.getId()));
		}
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return applicationInfos.get(position);
	}

	@Override
	public int getCount() {
		return applicationInfos.size();
	}
}