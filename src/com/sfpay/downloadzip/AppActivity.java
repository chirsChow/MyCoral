package com.sfpay.downloadzip;

import java.util.ArrayList;
import java.util.List;

import com.sfpay.downloadzip.adapters.AppsGridAdapter;
import com.sfpay.downloadzip.manager.ApplicationInfoManager;
import com.sfpay.downloadzip.model.ApplicationInfo;
import com.sfpay.downloadzip.util.ApplicationUtil;
import com.sfpay.downloadzip.util.ApplicationUtil.Callback;
import com.sfpay.downloadzip.widget.SfPayGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

public class AppActivity extends Activity {
	private ApplicationInfoManager applicationInfoManager;
	private ApplicationUtil applicationUtil;

	private SfPayGridView appsGrid;
	
	private ProgressBar downloadProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_layout);
		applicationInfoManager = ApplicationInfoManager.getInstance();
		applicationUtil = new ApplicationUtil();
		getApplicationList();
		AppsGridAdapter adapter = new AppsGridAdapter(this);
		appsGrid = (SfPayGridView) findViewById(R.id.apps_grid);
		appsGrid.setAdapter(adapter);
		appsGrid.setOnItemClickListener(new AppClick());
	}
	/**
	 * 获取app列表
	 */
	private void getApplicationList() {
		List<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();

		ApplicationInfo app = new ApplicationInfo();
		app.setId("com.tencent.mobileqq");
		app.setName("转账");
		app.setDownloadUrl("");
		app.setType(ApplicationInfo.TYPE_NATIVE);
		app.setVersion("1.0");
		app.setSignature("");
		app.setUrl("");
		applicationInfos.add(app);

		ApplicationInfo app2 = new ApplicationInfo();
		app2.setId("com.miui.notes");
		app2.setName("支付");
		app2.setDownloadUrl("");
		app2.setType(ApplicationInfo.TYPE_NATIVE);
		app2.setVersion("1.0");
		app2.setSignature("");
		app2.setUrl("");
		applicationInfos.add(app2);

		ApplicationInfo app3 = new ApplicationInfo();
		app3.setId("com.android.fileexplorer");
		app3.setName("帐户");
		app3.setDownloadUrl("");
		app3.setType(ApplicationInfo.TYPE_NATIVE);
		app3.setVersion("1.0");
		app3.setSignature("");
		app3.setUrl("");
		applicationInfos.add(app3);

		ApplicationInfo app4 = new ApplicationInfo();
		app4.setId("com.sfpay.webapp");
		app4.setName("Web应用");
		app4.setDownloadUrl("http://10.118.200.50:8084/test.zip");
		app4.setType(ApplicationInfo.TYPE_WEB);
		app4.setVersion("1.0");
		app4.setSignature("13977469C017461623FE3152870A7505");
		app4.setUrl("index.html");
		applicationInfos.add(app4);

		applicationInfoManager.setApplicationInfos(applicationInfos);

	}
	/**
	 * 打开App
	 */
	private final class AppClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ApplicationInfo appInfo = applicationInfoManager.getAvailableApplicationInfos().get(position);
			if (ApplicationInfo.TYPE_NATIVE.equals(appInfo.getType()) || (applicationInfoManager.isAppInstalled(appInfo)
					&& !applicationInfoManager.isAppStale(appInfo))) {
				applicationInfoManager.startApplication(AppActivity.this, appInfo);
			} else {
				if(ApplicationInfo.TYPE_WEB.equals(appInfo.getType())){
					downloadProgress = (ProgressBar) view.findViewById(R.id.download_progressbar);
				}
				popupDownloadAppDialog(appInfo, applicationInfoManager.isAppStale(appInfo));
			}
		}
	}
	/**
	 * 提示是否安装WEB APP
	 * @param appInfo
	 * @param isUpdate
	 */
	private void popupDownloadAppDialog(final ApplicationInfo appInfo, final boolean isUpdate) {
		new AlertDialog.Builder(this).setMessage(isUpdate ? R.string.update_app_tip : R.string.download_app_tip)
				.setPositiveButton(R.string.confirm, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						applicationUtil.downloadAndInstall(appInfo, new DownloadCallback(appInfo));
					}
				}).setNegativeButton(R.string.cancel, null).show();

	}
	/**
	 * 下载进度和各状态的回调接口实现
	 */
	private final class DownloadCallback implements Callback {
		private ProgressDialog verifyDialog;
		private ProgressDialog unZipDialog;
		private ApplicationInfo appInfo;

		public DownloadCallback(ApplicationInfo appInfo) {
			this.appInfo = appInfo;
		}

		@Override
		public void onVerifyError() {
			verifyDialog.dismiss();
			new AlertDialog.Builder(AppActivity.this).setTitle(R.string.error).setMessage(R.string.verify_app_error)
					.setPositiveButton(R.string.confirm, null).show();
		}

		// 下载完成，开始验证
		@Override
		public void onVerify() {
			downloadProgress.setProgress(100);
			downloadProgress.setVisibility(View.GONE);

			verifyDialog = new ProgressDialog(AppActivity.this);
			verifyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			verifyDialog.setMessage(getString(R.string.verify_app));
			verifyDialog.setCancelable(false);
			verifyDialog.show();
		}

		@Override
		public void onUnzipError() {
			unZipDialog.dismiss();
			new AlertDialog.Builder(AppActivity.this).setTitle(R.string.error).setMessage(R.string.unzip_app_error)
					.setPositiveButton(R.string.confirm, null).show();
		}

		// 验证完成，开始解压缩
		@Override
		public void onUnzip() {
			verifyDialog.dismiss();

			unZipDialog = new ProgressDialog(AppActivity.this);
			unZipDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			unZipDialog.setMessage(getString(R.string.unzip_app));
			unZipDialog.setCancelable(false);
			unZipDialog.show();
		}

		@Override
		public void onStart() {
			downloadProgress.setVisibility(View.VISIBLE);
			downloadProgress.setProgress(0);
		}

		@Override
		public void onFinished() {
			downloadProgress.setVisibility(View.GONE);
			verifyDialog.dismiss();
			unZipDialog.dismiss();
			new AlertDialog.Builder(AppActivity.this).setMessage(R.string.install_app_finished_tip)
			.setPositiveButton(R.string.confirm, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					applicationInfoManager.startApplication(AppActivity.this, appInfo);
				}
			}).show();
		}

		@Override
		public void onDownloadUpdate(int progress) {
			downloadProgress.setProgress(progress);
		}

		@Override
		public void onDownloadError() {
			downloadProgress.setVisibility(View.GONE);
			new AlertDialog.Builder(AppActivity.this).setTitle(R.string.error).setMessage(R.string.download_app_error)
					.setPositiveButton(R.string.confirm, null).show();
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppApplication.getApplication().getRequestQueue().cancelAll("md5Request");
	}

}
