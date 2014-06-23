package com.sinosoft.lis.claimanalysis.renewal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;

public class GrpcontSpit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "jianpaitangguo";
		String grpcontno = "880007221,880007222,880007223,880007224,880007225,880007410,880007411,880007412,880007413,880007414,880007415";
		splitDS(path,"match","csv",0,grpcontno);
		splitDS(path,"claim","csv",0,grpcontno);
	}
	
	public static HashMap splitDS(String dir,String fileName, String fileType, int keyNum,String str){
		// match.csv 47
		// claim.csv 36
		String file_path = "D:/temp/"+dir+"/";
		HashMap years = new HashMap();
		String path = file_path + fileName + "." + fileType;
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			String head = br.readLine();
			HashMap fileMap = new HashMap();
			FileProcessor fileProcessor = new FileProcessor();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String key = row[keyNum]; // year
				String key2 = row[1]; // year
				if(str.indexOf(key2)<0){
					continue;
				}
				Object o = fileMap.get(key);
				if( o == null ){
					StringBuffer s = new StringBuffer();
					s.append(head).append("\r\n");
					s.append(line).append("\r\n");
					fileMap.put(key, s);
				} else {
					StringBuffer s = (StringBuffer)o;
					
					if(s.length()>=1024*1024*10){
						fileProcessor.outputFile(s.toString(), file_path, fileName+"_"+key, fileType);
						s = new StringBuffer();
						fileMap.put(key, s);
					}
					
					s.append(line).append("\r\n");

				}
			}
			Set keySet = fileMap.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				StringBuffer s = (StringBuffer)fileMap.get(key);
//				FileProcessor FileProcessor = new FileProcessor();
				fileProcessor.outputFile(s.toString(), file_path, fileName+"_"+key, fileType);
				// years
				years.put(key, "");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return years;
	}
}
