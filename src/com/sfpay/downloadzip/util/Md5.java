package com.sfpay.downloadzip.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import android.util.Base64;

public class Md5 {
	/**
	 * 获取文件的MD5码
	 * @param file
	 * @return Md5
	 * @throws FileNotFoundException
	 */
	public static String getMd5ByFile(File file) throws FileNotFoundException {
		String value = null;
		FileInputStream in = new FileInputStream(file);
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
	
	/**
	 *  加密传入的数据是byte类型的，并非使用decode方法将原始数据转二进制，String类型的数据 使用 str.getBytes()即可
		 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
		这里 encodeToString 则直接将返回String类型的加密数据
	 * @param str
	 * @return
	 */
	public static final String base64Encode(String str){
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
		
	}
	/**
	 *  对base64加密后的数据进行解密
	 * @param strBase64
	 * @return
	 */
	public static final String base64Decode(String strBase64) {
		return new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT));
	}

}
