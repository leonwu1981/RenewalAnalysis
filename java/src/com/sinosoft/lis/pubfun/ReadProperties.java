package com.sinosoft.lis.pubfun;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties {
	/**
	 * ��ȡproperties�����ļ�����������ֶε�ֵ��
	 * 
	 * @param path
	 *            �����ļ�·��
	 * @param fileName
	 *            �����ļ���
	 * @param key
	 *            �����ֶ���
	 * @return ��������ֶεĶ�Ӧֵ
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
