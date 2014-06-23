package com.sinosoft.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.sinosoft.lis.pubfun.ReadProperties;

public class EnDEcrypt {
	private static final char[] map = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	private String KEY = "";
	
	// ����
	public String cryptEn(String source) {
		if (source==null||"".equals(source.trim())||"null".equalsIgnoreCase(source)) {
			return "";
		}
		if (source.startsWith("[")) {
			return source;
		}
		if(source.indexOf("*")>=0){
			try{
				throw new Exception("��������Ϣ��*�����ܻ�Ѽ��˽��ܲ��Ե�����ֱ�ӷ����ݿ⣡"+source);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		byte[] keyByte = getKey().getBytes();

		byte[] out = null;
		try {
			out = cryptBytes(source.getBytes(), Cipher.ENCRYPT_MODE, keyByte,
					1 * 100);
		} catch (Exception e) {
			new RuntimeException(e);
		}

		// ���ϱ�ʾ���ܵ�������
		String sourceCrypt = "[" + getHexString(out) + "]";
		return sourceCrypt;
	}

	private String getKey() {
		if(KEY.equals("")){
			String path = System.getProperty("uicontextpath");
			//�������ô˷����ĵ�·��
			if(path==null||path.equals(null)){
				File f = new File(EnDEcrypt.class.getResource("/").getFile());
				File get = f.getParentFile().getParentFile();
				path = get.getPath();
			}
			path = path+"/";
			ReadProperties rp = new ReadProperties();
			KEY = rp.readPropertiesValue(path,"grpurl.properties","KEY").trim();
		}
		return KEY;
	}
	
	private void setKey(String key) {
		KEY = key;
	}

	/**
	 * ����
	 * 
	 * @param source
	 *            ���������
	 * @param key
	 *            ��Կ
	 * @return ���ܺ�����
	 */
	public String cryptDe(String source) {
		byte[] keyByte = getKey().getBytes();
		// ȥ����ʾ���ܵ�������
		String sourceOut = source.substring(1, source.length() - 1);

		byte[] sourceByte = getBytesFromHexString(sourceOut);
		byte[] out = null;
		try {
			out = cryptBytes(sourceByte, Cipher.DECRYPT_MODE, keyByte, 1 * 100);
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e);
		}

		String sourceCrypt = new String(out);
		
		return sourceCrypt;
	}

	public byte[] cryptBytes(byte[] in, int opMode, byte[] key,
			int bufferSize) throws Exception {
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		KeySpec ks = new DESKeySpec(key);
		SecretKey ky = kf.generateSecret(ks);
		Cipher c1 = Cipher.getInstance("DES");
		c1.init(opMode, ky);
		byte[] out = c1.doFinal(in);
		return out;
	}

	public String getHexString(byte[] input) {
		char[] result = new char[input.length * 2];
		for (int i = 0; i < input.length; i++) {
			result[i * 2] = map[(input[i] & 240) >>> 4];
			result[i * 2 + 1] = map[input[i] & 15];
		}
		return new String(result);
	}

	public byte[] getBytesFromHexString(String input) {
		String hex = input.toUpperCase();
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < result.length; i++) {
			char c = hex.charAt(i * 2);
			int ci = -1;
			for (int j = 0; j < map.length; j++) {
				if (c == map[j]) {
					ci = j;
					break;
				}
			}
			char c1 = hex.charAt(i * 2 + 1);
			int ci1 = -1;
			for (int j = 0; j < map.length; j++) {
				if (c1 == map[j]) {
					ci1 = j;
					break;
				}
			}
			if (ci == -1 || ci1 == -1) {
				throw new RuntimeException("Illeagal Hex String");
			}
			result[i] = (byte) (ci * 16 + ci1);
		}
		return result;
	}
	
    public static void main(String[] args){
    	int start = 0;
    	if(args.length>0){
    		start = Integer.parseInt(args[0]);
    	}
    	String[] a = {"0","1","2","3","4","5","6","7","8","9",
    			"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
    			"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    			};
    	EnDEcrypt en = new EnDEcrypt();
    	String[] keys = new String[8];
    	long tmp = 0;
		keys[0]=a[start];
		for (int j = 0; j < a.length; j++) {
			keys[1]=a[j];
			for (int k = 0; k < a.length; k++) {
				keys[2] = a[k];
				for (int i1 = 0; i1 < a.length; i1++) {
					keys[3] = a[i1];
					for (int j1 = 0; j1 < a.length; j1++) {
						keys[4] = a[j1];
						for (int k1 = 0; k1 < a.length; k1++) {
							keys[5] = a[k1];
							for (int i2 = 0; i2 < a.length; i2++) {
								keys[6] = a[i2];
								for (int j2 = 0; j2 < a.length; j2++) {
									keys[7] = a[j2];
									String key = keys[0]+keys[1]+keys[2]+keys[3]+keys[4]+keys[5]+keys[6]+keys[7];
									en.setKey(key);
									try{
										if(en.cryptEn("429006198101235118").equals("[1E07451F0FAC0541DD79D534B3FEA844CC3C6650CA3FAA6F]")){
											System.out.println(key);
											outputFile("Success!"+key+"\r\n", "d:\\", "EnDEcrypt"+start, "csv", true);
											return;
										}
									}catch(Exception e){
										
									}
									tmp++;
									if(tmp%1000000==0){
										System.out.println(tmp);
										outputFile(tmp+"\r\n", "d:\\", "EnDEcrypt"+start, "csv", true);
										outputFile(key+"\r\n", "d:\\", "EnDEcrypt"+start, "csv", true);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	
	public static boolean outputFile( String content, String path, String name, String type ,boolean append) {
//		File of;
//	    FileOutputStream ofs;
//	    BufferedOutputStream bos;
	    try {
//	    	of = new File( path + name + "." + type );
//            ofs = new FileOutputStream(of,append);
//            bos = new BufferedOutputStream(ofs);
//            
//            
//            byte[] tmp = new byte[1024*100];
//            StringInputStream in = new StringInputStream(content);
//            while(true){
//            	int r = in.read(tmp);
//            	if(r==-1){
//            		break;
//            	}
//
//            	bos.write(tmp,0,r);
//            	tmp = new byte[1024*100];
//            }
//            
////            bos.write((content).getBytes());
//            bos.close();
/*	    	
	    	//���ԣ�ͬʱд��utf-8���ļ����������������ǲ�������
	        FileOutputStream fout = new FileOutputStream(path + name + "(utf-8)." + type,append);
	        OutputStreamWriter out = new OutputStreamWriter(
	          new BufferedOutputStream(fout), "UTF-8");
	        out.write(content);
	        out.close();
	        fout.close();
*/	        
	    	//���ԣ�ͬʱд��GBK���ļ����������������ǲ�������
	        FileOutputStream foutGBK = new FileOutputStream(path + name + "." + type,append);
	        OutputStreamWriter outGBK = new OutputStreamWriter(
	          new BufferedOutputStream(foutGBK), "GBK");
	        outGBK.write(content);
	        outGBK.close();
	        foutGBK.close();
/*	        
	    	//���ԣ�ͬʱд��GB2312���ļ����������������ǲ�������
	        FileOutputStream foutGB2312 = new FileOutputStream(path + name + "." + type,append);
	        OutputStreamWriter outGB2312 = new OutputStreamWriter(
	          new BufferedOutputStream(foutGB2312), "GB2312");
	        outGB2312.write(content);
	        outGB2312.close();
	        foutGB2312.close();
*/
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
}
