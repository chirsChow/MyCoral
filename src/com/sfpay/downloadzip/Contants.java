package com.sfpay.downloadzip;

public class Contants {
	/**
	 * 调试环境，文件下载在SDCard中的，生产环境，文件下载在手机内存储空间中
	 */
	public static final boolean isDebug = true;
	/**
	 * 解压后的文件，WEB应用存放在这个目录下，临时目录中的临时文件会删除
	 */
	public static final String ZipFolder = "sfPayZip";
	/**
	 * 下载的ZIP包临时文件夹
	 */
	public static final String ZipFolderTmp = "sfPayZipTmp";
	/**
	 * 是否开启对下载的文件进行MD5校验，
	 */
	public static final boolean isVerifiy = false;
	
}
