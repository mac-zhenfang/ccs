/**
 * 
 */
package com.cisco.cc.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

/**
 * @author zhefang
 * 
 */
public class Utils {
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static int getRandomInt(int size) {
		Random random = new Random();
		int ran;
		ran = size != 0 ? random.nextInt(size) : random.nextInt();
		return ran;
	}
}
