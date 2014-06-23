package com.sinosoft.lis.pubfun;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties {
	/**
	 * 读取properties配置文件内相关配置字段的值。
	 * 
	 * @param path
	 *            配置文件路径
	 * @param fileName
	 *            配置文件名
	 * @param key
	 *            配置字段名
	 * @return 相关配置字段的对应值
	 */
	public String readPropertiesValue(String path, String fileName, String key) {
		if (null == path || null == fileName || null == key || "".equals(path)
				|| "".equals(fileName) || "".equals(key)) {
			return "";
		}
		Properties p = new Properties();
		try {
			System.out.println(path + fileName);
			InputStream in = new FileInputStream(path + fileName);
			p.load(in);
			in.close();
			if (p.containsKey(key)) {
				return p.getProperty(key);
			} else {
				return "";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadProperties properties = new ReadProperties();
		System.out.println("---------->"
				+ properties.readPropertiesValue("d:\\", "abc.properties",
						"prtserver"));
	}

}
