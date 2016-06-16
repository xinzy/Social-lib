package com.xinzy.social;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class SocialUtil {
	
	private static final int MAX_LENGTH = 30 * 1024;

	public static byte[] bmpToByteArray(final Bitmap bmp) {
		byte[] result = bmpToByteArray(bmp, 100);

		if (result.length > MAX_LENGTH) {
			int quality = MAX_LENGTH * 100 / result.length;
			result = bmpToByteArray(bmp, quality);
		}
		
		return result;
	}

	private static byte[] bmpToByteArray(final Bitmap bmp, final int quality) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 30, output);
		bmp.recycle();

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
