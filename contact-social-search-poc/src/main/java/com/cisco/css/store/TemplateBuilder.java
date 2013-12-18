/**
 * 
 */
package com.cisco.css.store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author zhefang
 * 
 */
public class TemplateBuilder {

	private Configuration cfg = new Configuration();

	private static TemplateBuilder instance = new TemplateBuilder();

	private TemplateBuilder() {
		try {
			cfg.setDirectoryForTemplateLoading(new File(TemplateBuilder.class
					.getClassLoader().getResource("templates").toURI()
					.getPath()));
			cfg.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
		} catch (Exception e) {
			// TODO
			throw new RuntimeException(e.getMessage());
		}
	}

	public static TemplateBuilder getInstance() {
		return instance;
	}

	public String getTemplateStr(Object root, String ftl) {
		Template t;
		String s = new String();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			t = cfg.getTemplate(ftl);
			t.process(root, new OutputStreamWriter(bos));
			s = new String(bos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return s;
	}
}
