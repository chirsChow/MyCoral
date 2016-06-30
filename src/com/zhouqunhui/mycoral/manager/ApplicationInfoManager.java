package com.zhouqunhui.mycoral.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.zhouqunhui.mycoral.AppApplication;
import com.zhouqunhui.mycoral.Contants;
import com.zhouqunhui.mycoral.R;
import com.zhouqunhui.mycoral.model.ApplicationInfo;
import com.zhouqunhui.mycoral.module.AccountActivity;
import com.zhouqunhui.mycoral.module.PayActivity;
import com.zhouqunhui.mycoral.module.TransferActivity;
import com.zhouqunhui.mycoral.util.FileUtils;
import com.zhouqunhui.mycoral.util.VerifiyUtil;
import com.zhouqunhui.mycoral.volley.Response;
import com.zhouqunhui.mycoral.volley.VolleyError;
import com.zhouqunhui.mycoral.volley.toolbox.JsonObjectRequest;
import com.zhouqunhui.mycoral.webview.WebpageActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * 应用管理(单例)，初始化应用，查找应用列表.....
 * @author 837781
 *
 */
public class ApplicationInfoManager {
	/**
	 * 用户可用的app列表
	 */
    private List<ApplicationInfo> availableApps;

	private static class NativeApplicationInfo {
		int nameResId;
		int iconResId;
		Class<? extends Activity> clazz;

		NativeApplicationInfo(int nameResId, int iconResId, Class<? extends Activity> clazz) {
			this.nameResId = nameResId;
			this.iconResId = iconResId;
			this.clazz = clazz;
		}
	}

    private List<ApplicationInfo> applicationInfos;
    private Map<String, NativeApplicationInfo> nativeApps = new HashMap<String, ApplicationInfoManager.NativeApplicationInfo>();

    private static ApplicationInfoManager instance;
    
    private ApplicationInfoManager() {
		nativeApps.put("com.tencent.mobileqq", new NativeApplicationInfo(R.string.transfer, R.drawable.transfer, TransferActivity.class));
		nativeApps.put("com.miui.notes", new NativeApplicationInfo(R.string.pay, R.drawable.payment, PayActivity.class));
		nativeApps.put("com.android.fileexplorer", new NativeApplicationInfo(R.string.account_info, R.drawable.account, AccountActivity.class));
    }
    
	public synchronized static ApplicationInfoManager getInstance() {
		if (instance == null) {
			instance = new ApplicationInfoManager();
		}
		return instance;
	}

    public void setApplicationInfos(List<ApplicationInfo> applicationInfos) {
        this.applicationInfos = applicationInfos;
    }

    public List<ApplicationInfo> initAvailableApplicationInfos(List<String> appIds) {
        if (availableApps == null) {
            availableApps = new ArrayList<ApplicationInfo>();
            for (String id : appIds) {
                ApplicationInfo applicationInfo = findApplicationInfo(id);
                if (applicationInfo == null) {
                    continue;
                } else if (ApplicationInfo.TYPE_WEB.equals(applicationInfo.getType())) {
                    availableApps.add(applicationInfo);
                } else {
                    if (nativeApps.containsKey(applicationInfo.getId()))
                        availableApps.add(applicationInfo);
                }
            }
        }

        return availableApps;
    }

    public List<ApplicationInfo> getAvailableApplicationInfos() {
        return availableApps;
    }

    private ApplicationInfo findApplicationInfo(String appId) {
        for (ApplicationInfo applicationInfo : applicationInfos) {
            if (appId.equals(applicationInfo.getId())){
                return applicationInfo;
            }
        }
        return null;
    }

    public boolean isAppInstalled(ApplicationInfo appInfo) {
        return generateAppUri(appInfo) != null;
    }

    public boolean isAppStale(final ApplicationInfo appInfo) {
    	File appsDir = null;
		if (Contants.isDebug) {
			appsDir = FileUtils.createSDCardDir(Contants.ZipFolder);
		} else {
			appsDir = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
		}
        
        File[] dirs = appsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(appInfo.getId());
            }
        });
        if (dirs != null && dirs.length > 0) {
            return !dirs[0].getName().equals(appInfo.getId() + "@" + appInfo.getVersion());
        } else return false;
    }
    /**
     * 生成WEB应用的App访问Uri
     * @param appInfo
     * @return
     */
    private String generateAppUri(ApplicationInfo appInfo) {
        final String appDirName = appInfo.getId() + "@" + appInfo.getVersion();
        File appsDir = null;
		if (Contants.isDebug) {
			appsDir = FileUtils.createSDCardDir(Contants.ZipFolder);
		} else {
			appsDir = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
		}
        File[] dirs = appsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return appDirName.equals(filename);
            }
        });
		if (dirs != null && dirs.length > 0) {
			return "file:///" + dirs[0].getAbsolutePath() + "/" + appInfo.getUrl();
		} else {
			return null;
		}
    }
    /**
     * 启动应用（本地，WEB）
     * @param activity
     * @param appInfo
     */
    public void startApplication(Activity activity, ApplicationInfo appInfo) {
        if (ApplicationInfo.TYPE_WEB.equals(appInfo.getType())) {
			if (!Contants.isVerifiy) {
				startWebApp(activity, appInfo);
			} else {
				verifiy(activity, appInfo);
			}
        } else {
        	startNativeApp(activity, appInfo);
        }
    }
    /**
     * 启动本地应用
     * @param activity
     * @param appInfo
     */
	private void startNativeApp(Activity activity, ApplicationInfo appInfo) {
		Intent intent = new Intent();
		NativeApplicationInfo nativeApplicationInfo = nativeApps.get(appInfo.getId());
		intent.setClass(activity, nativeApplicationInfo.clazz);
		activity.startActivity(intent);
	}
	/**
	 * 用WebView打开WEB应用
	 * @param activity
	 * @param appInfo
	 */
	private void startWebApp(Activity activity, ApplicationInfo appInfo) {
    	Intent intent = new Intent();
        String appUri = generateAppUri(appInfo);
//		String appUri = "file:///android_asset/www/" + appInfo.getUrl();
        
        intent.setClass(activity, WebpageActivity.class);
        intent.putExtra(WebpageActivity.APP_URI, appUri);
        activity.startActivity(intent);
	}

	public int getNativeApplicationIcon(String appId) {
        return nativeApps.get(appId).iconResId;
    }

    public int getNativeApplicationName(String appId) {
        return nativeApps.get(appId).nameResId;
    }
    
    private JSONObject md5Result;
    
	public JSONObject getMd5Result() {
		return md5Result;
	}
	/**
	 * 打开WEB应用时，对本地的文件进行MD5校验
	 * @param activity
	 * @param appInfo
	 */
	private void verifiy(final Activity activity, final ApplicationInfo appInfo) {
		
		final ProgressDialog verifyDialog = new ProgressDialog(activity);
		verifyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		verifyDialog.setMessage(activity.getString(R.string.verify_app));
		verifyDialog.setCancelable(false);
		verifyDialog.show();
		
		final String appDirName = appInfo.getId() + "@" + appInfo.getVersion();
		File appsDir = null;
		if (Contants.isDebug) {
			appsDir = FileUtils.createSDCardDir(Contants.ZipFolder);
		} else {
			appsDir = AppApplication.getApplication().getDir(Contants.ZipFolder, Context.MODE_PRIVATE);
		}
		final File appDir = new File(appsDir, appDirName);
		
		//下载服务器端的MD5码文件，对本地文件进行MD5校验
		String url = "http://10.118.200.45:8082/h5-cashier/css/md5.json";
		Map<String, String> map = new HashMap<String, String>();
		map.put("appId", appInfo.getId());
		JSONObject params = new JSONObject(map);

		JsonObjectRequest jsonRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject obj) {
				md5Result = obj;
				verifyDialog.dismiss();
				if (VerifiyUtil.verifiy(appDir, obj)) {
					startWebApp(activity, appInfo);
				}else{
					Toast.makeText(activity, R.string.verify_app_error, Toast.LENGTH_LONG).show();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.getMessage();
				verifyDialog.dismiss();
				Toast.makeText(activity, R.string.network_error, Toast.LENGTH_LONG).show();
			}
		});
		jsonRequest.setTag("md5Request");
		AppApplication.getApplication().getRequestQueue().add(jsonRequest);
	}
}
