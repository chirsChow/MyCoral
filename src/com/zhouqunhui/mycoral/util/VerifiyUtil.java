package com.zhouqunhui.mycoral.util;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import com.zhouqunhui.mycoral.Contants;

import android.text.TextUtils;

public class VerifiyUtil {
	/**
	 * 验证文件或目录
	 * @param file
	 * @param md5Result
	 * @return
	 */
	public static boolean verifiy(File file, JSONObject md5Result) {
		if (!Contants.isVerifiy) {
			return true;
		}
		
		if (md5Result == null || file == null) {
			return false;
		} else {
			try {
				List<File> files = FileUtils.listFolder(file);
				if (files.size() != md5Result.length()) {
					return false;
				}
				for (File f : files) {
					String fileName = f.getName();
					String v = Md5.getMd5ByFile(f);
					String v2 = md5Result.getString(fileName);
					if (!v.equalsIgnoreCase(v2)) {
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	/**
	 * 验证单个文件
	 */
	public static boolean verifiy(String md5, File appPackageFile) throws Exception {
		if (!Contants.isVerifiy) {
			return true;
		}
		
		String v = Md5.getMd5ByFile(appPackageFile);
		if (!TextUtils.isEmpty(md5) && md5.equalsIgnoreCase(v)) {
			return true;
		} else {
			return false;
		}
	}
	
}
