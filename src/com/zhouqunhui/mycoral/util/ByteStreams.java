package com.zhouqunhui.mycoral.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ByteStreams {
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	public static long copy(InputStream from, OutputStream to) throws IOException {
		checkNotNull(from);
		checkNotNull(to);
		byte[] buf = new byte[1024];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}

	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}
	
	public static void unzip(String zipFilePath, String outputFolderPath) throws IOException {
		File folder = new File(outputFolderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));

		while (true) {
			ZipEntry entry = zis.getNextEntry();
			if (entry == null)
				break;

			String fileName = entry.getName();
			
//			if (fileName.startsWith("android/")) {
//				fileName = fileName.substring("android/".length());
//			} else {
//				continue;
//			}

			File newFile = new File(outputFolderPath + File.separator + fileName);
			if (entry.isDirectory()) {
				newFile.mkdirs();
			} else {
				FileOutputStream fos = new FileOutputStream(newFile);
				copy(zis, fos);
				fos.close();
			}

			zis.closeEntry();
		}
		zis.close();
	}
}
