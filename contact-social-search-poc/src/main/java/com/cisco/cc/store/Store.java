/**
 * 
 */
package com.cisco.cc.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.ObjectMapper;

import com.cisco.cc.util.Utils;

/**
 * @author zhefang
 *
 */
public abstract class Store {
		
	public abstract void init();
	
	public abstract void prepareData();
	
	protected String readFromFile(String filePath) throws Exception {
		File file = new File(PersonStore.class.getClassLoader()
				.getResource(filePath).toURI().getPath());
		InputStream is = new FileInputStream(file);
		byte[] filebt = Utils.readStream(is);
		return new String(filebt);
	}

	protected void writeToFile(String fileName, Object value, ObjectMapper mapper)
			throws Exception {
		File file = new File(Store.class.getClassLoader()
				.getResource(fileName).toURI().getPath());
		System.out.println(file.getPath());
		OutputStream os = new FileOutputStream(file);
		// mapper.writeValue(file, value);
		mapper.writeValue(os, value);
		os.close();
	}

}
