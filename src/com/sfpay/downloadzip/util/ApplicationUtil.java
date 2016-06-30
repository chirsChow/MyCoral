package com.sfpay.downloadzip.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sfpay.downloadzip.AppApplication;
import com.sfpay.downloadzip.Contants;
import com.sfpay.downloadzip.model.ApplicationInfo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
/**
 * 实现WEB应用下载功能(AsyncTask异步实现)
 * @author 837781
 *
 */
public class ApplicationUtil {
	private static final String TAG = "ApplicationUtil";
	/**
	 * 下载进度、成功、失败等状态回调接口
	 */
	public static interface Callback {
		void onStart();

		void onDownloadUpdate(int progress);

		void onDownloadError();

		void onVerify();

		void onVerifyError();

		void onUnzip();

		void onUnzipError();

		void onFinished();
	}
	/**
	 * 启动下载，下载后，对下载的ZIP包进行MD5校验
	 * @param applicationInfo
	 * @param callback
	 */
	public void downloadAndInstall(final ApplicationInfo applicationInfo, final Callback callback) {
		callback.onStart();
		new AsyncTask<String, Integer, Void>() {
			public static final int DOWNLOAD_UPDATE = 1;
			public static final int DOWNLOAD_ERROR = 2;
			public static final int VERIFY = 3;
			public static final int VERIFY_ERROR = 4;
			public static final int UNZIP = 5;
			public static final int UNZIP_ERROR = 6;
			public static final int FINISHED = 7;

			@Override
			protected void onProgressUpdate(Integer... values) {
				switch (values[0]) {
				case DOWNLOAD_UPDATE:
					callback.onDownloadUpdate(values[1]);
					break;
				case DOWNLOAD_ERROR:
					callback.onDownloadError();
					break;
				case VERIFY:
					callback.onVerify();
					break;
				case VERIFY_ERROR:
					callback.onVerifyError();
					break;
				case UNZIP:
					callback.onUnzip();
					break;
				case UNZIP_ERROR:
					callback.onUnzipError();
					break;
				case FINISHED:
					callback.onFinished();
					break;
				default:
					break;
				}
			};

			@Override
			protected Void doInBackground(String... params) {
				File appPackageFile = null;
				try {
					
					File tmpDir = null;
					if (Contants.isDebug) {
						tmpDir = FileUtils.createSDCardDir(Contants.ZipFolderTmp);
					} else {
						tmpDir = AppApplication.getApplication().getDir(Contants.ZipFolderTmp, Context.MODE_PRIVATE);
					}
					
					appPackageFile = new File(tmpDir, applicationInfo.getId());
					final FileOutputStream fos = new FileOutputStream(appPackageFile);
					URL url = new URL(params[0]);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(5000);
					connection.setRequestMethod("GET");
					connection.connect();
					final long total = connection.getContentLength();
					InputStream in = connection.getInputStream();

					long downloaded = 0;
					
					int bytes = 0;
					byte[] buffer = new byte[1024];
					while ((bytes = in.read(buffer)) != -1) {
						fos.write(buffer, 0, bytes);
						downloaded += bytes;
						int progress = (int) (100 * downloaded / total);
						publishProgress(DOWNLOAD_UPDATE, progress);
					}
					fos.flush();
					in.close();
					fos.close();
					connection.disconnect();
				} catch (IOException e) {
					publishProgress(DOWNLOAD_ERROR);
					return null;
				}

				publishProgress(VERIFY);
				boolean verified = false;
				try {
					verified = VerifiyUtil.verifiy(applicationInfo.getSignature(), appPackageFile);
				} catch (Throwable e) {
					publishProgress(VERIFY_ERROR);
					return null;
				}

				if (!verified) {
					Log.e(TAG, "Verify signature failed!");
					publishProgress(VERIFY_ERROR);
					return null;
				} else {
					try {
						publishProgress(UNZIP);

						File appsDir = null;
						if (Contants.isDebug) {
							appsDir = FileUtils.createSDCardDir(Contants.ZipFolder);
						} else {
							appsDir = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
						}
						File[] dirs = appsDir.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String filename) {
								return filename.startsWith(applicationInfo.getId());
							}
						});
						if (dirs != null && dirs.length > 0) {
							for(File f:dirs) {
                                FileUtils.deleteDirectory(f);
							}
						}
						
						File appsDiretory = null;
						if (Contants.isDebug) {
							appsDiretory = FileUtils.createSDCardDir(Contants.ZipFolder);
						} else {
							appsDiretory = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
						}

						File appDir = new File(appsDiretory, applicationInfo.getId() + "@" + applicationInfo.getVersion());
						appDir.mkdir();
						ByteStreams.unzip(appPackageFile.getAbsolutePath(), appDir.getAbsolutePath());
					} catch (IOException e) {
						publishProgress(UNZIP_ERROR);
						return null;
					}
				}

				appPackageFile.delete();
				publishProgress(FINISHED);
				return null;
			}

		}.execute(applicationInfo.getDownloadUrl());
	}
	
}
