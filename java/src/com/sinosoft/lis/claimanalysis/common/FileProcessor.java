package com.sinosoft.lis.claimanalysis.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.Range;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class FileProcessor {
	public static void main(String[] args) {
		FileProcessor fileProcessor = new FileProcessor();
		fileProcessor.mergeFiles("D:\\app\\receivePPF_SZ\\", "aaa", 0, 1, "txt", "aaa");
	}
	
	public void newFolder(String folderPath) {
		
	    try {
	    	String filePath = folderPath; 
	        File myFilePath = new File(filePath); 
	        if (!myFilePath.exists()) { 
	        	myFilePath.mkdirs();
	        } 
	    } catch (Exception e) { 
	    	System.out.println("新建文件夹操作出错"); 
	    	e.printStackTrace(); 
	    }
	} 
	
	public boolean outputFile( String content, String path, String name, String type) {
		return outputFile(  content,  path,  name,  type,true);
	}

	/**
	 * Create or Replace a text file
	 * @param content
	 * @param path
	 * @param name
	 * @param type
	 * @return
	 */
	public boolean outputFile( String content, String path, String name, String type,boolean append ) {
		this.newFolder(path);
		File of;
	    FileOutputStream ofs;
	    BufferedOutputStream bos;
	    try {
	    	of = new File( path + name + "." + type );
            ofs = new FileOutputStream(of,append);
            bos = new BufferedOutputStream(ofs);
            
            
            byte[] tmp = new byte[1024*100];
            StringInputStream in = new StringInputStream(content);
            while(true){
            	int r = in.read(tmp);
            	if(r==-1){
            		break;
            	}

            	bos.write(tmp,0,r);
            	tmp = new byte[1024*100];
            }
            
//            bos.write((content).getBytes());
            bos.close();
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
	public boolean mergeFiles ( String tFilePath, String tName, int startNum, int endNum, String fileType, String newName ) {
		this.mergeFiles(tFilePath, tName, startNum, endNum, fileType, newName, true);
		return true;
	}
	
	
	// 子文件命名 tName_subFileNum
	public boolean mergeFiles ( String tFilePath, String tName, int startNum, int endNum, String fileType, String newName, boolean deleteSubfile ) {
		String tFileType = "." + fileType;
		boolean succFlag = true;  //文件是否全部成功
		//String tName = tFileName.substring(0, tFileName.indexOf("."));
		//判断文件是否都存在且不为空
		for(int i=startNum;i<=endNum;i++) {
			String subFileName = tName + "_" + i;
			File fileObject = new File(tFilePath+subFileName+tFileType);
			if(!fileObject.exists()) {
				System.out.println("文件"+fileObject+"不存在!");
				succFlag = false;
			    continue;
			}
//		   long fileLen = fileObject.length();
//		   if(fileLen<1) {
//			   System.out.println("文件"+fileObject+"内容为空! length= " +fileLen);
//			   succFlag = false;
//			   break;
//		   }
		   
		}
		//建立合并后的文件
		File dirObject = new File(tFilePath);
//		File fileObject = new File(tFilePath+newName+tFileType);
//		String newName = tName;
//		for( int i = 1 ; fileObject.exists() ; i++ ) {
//			newName = tName + "(" + i + ")";
//			fileObject = new File( tFilePath + newName + tFileType );
//    	}
		
        try {
            dirObject.mkdir();
//            fileObject.createNewFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        //文件成功,可以进行合并处理
        String writeFileName = tFilePath+newName+tFileType;
		if(succFlag) {
			
            //开始合并到新文件
			for(int i=startNum;i<=endNum;i++) {
				String subFileName = tName + "_" + i;
	 			String readFileName = tFilePath + subFileName + tFileType;
	 			if(readFileName.equals(writeFileName)){
	 				continue;
	 			}
	 			
		        FileInputStream inStream;
		        FileOutputStream outStream;
		        try {
		        	System.out.println( "succ:" + readFileName);
		            inStream = new FileInputStream(readFileName);
		            outStream = new FileOutputStream(writeFileName,true); //true追加方式写入
		            this.copyContent(inStream, outStream);
		        } catch(FileNotFoundException e){
		            e.printStackTrace();
		        } catch (IOException e) {
		        	System.out.println("???????");
					e.printStackTrace();
				} 
			}
		} else { //文件失败,写入错误信息
			System.out.println( "error~~");
			FileOutputStream fileOutStream;
	        OutputStreamWriter outputWriter;
	        BufferedWriter bufWriter;
	 		try {
				fileOutStream = new FileOutputStream(writeFileName);//第2个参数为true,追加方式写入
				outputWriter = new OutputStreamWriter(fileOutStream);
		         bufWriter = new BufferedWriter(outputWriter);
		         String newStr = new String("File Fail End !");
		         bufWriter.write(newStr, 0, newStr.length());
		         //System.out.println(newStr);
		         bufWriter.close();
		         outputWriter.close();
		         fileOutStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if( !deleteSubfile || !succFlag ) {
			return true;
		}
		//删除已合并完的文件
		for(int i=startNum;i<=endNum;i++) {
			String subFileName = tName + "_" + i;
			if( subFileName.equals(newName) ){
				continue;
			}
 			String readFileName = tFilePath + subFileName + tFileType;
			File dirObject1 = new File(readFileName);
            boolean sflag = dirObject1.delete();
            if(sflag) {
            	System.out.println("文件"+readFileName+"删除成功!");
            } else {
            	System.out.println("文件"+readFileName+"删除失败!");
            }
		}
		return true;
	}
	
	public boolean deleteFile(String filePath){
		File file = new File(filePath);
        return file.delete();
	}
	
	public void copyContent(FileInputStream inObj, FileOutputStream outObj) {
        int copyLen;
        byte[] copyBuf = new byte[1024*1024*10];
        try {
       	 while ((copyLen = inObj.read(copyBuf, 0, 1024*1024*10)) != -1) {
                String copyStr = new String(copyBuf);
                outObj.write(copyBuf, 0, copyLen);
            }
        } catch (IOException e) {
            System.out.println("error: " + e);
        }  finally {
			try {
				inObj.close();
				outObj.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
   }
	
	public boolean mergeCsvToXls( List files, List sheetNames, String newFile ){
		int sheetnum = 0;
		try {
			WritableWorkbook copyDocument = Workbook.createWorkbook(new File(newFile));
			
			for(int i=0;i<files.size();i++){
				String sheetname = (String)sheetNames.get(i);
				String newsheetname = sheetname;
				int sheetno = 2;
		    	while( copyDocument.getSheet( newsheetname ) != null ){
		    		newsheetname = sheetname + "(" + sheetno++ + ")";
		    	}
			    WritableSheet targetSheet = copyDocument.createSheet(newsheetname, sheetnum++);
				
				String line;
				File file = new File( (String)files.get(i) );
				BufferedReader br = new BufferedReader(new FileReader(file));
				int row = 0;
				int page = 1;
				while ( (line = br.readLine()) != null ) {
					String[] cellsLine = line.split(",");
					for(int col=0;col<cellsLine.length;col++){
						Label label = new Label(col, row, cellsLine[col]); 
						targetSheet.addCell(label);
					}
					row++;
					if(row==65535){
						row = 0;
						targetSheet = copyDocument.createSheet(newsheetname+(page++), sheetnum++);
					}
				}
			}
			copyDocument.write();
		    copyDocument.close();
		} catch (Exception e) {
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	public boolean mergeXls( List files, String newFile ){
		int sheetnum = 0;
		try {
			WritableWorkbook copyDocument = Workbook.createWorkbook(new File(newFile));
			File tmpFile = null;
			for(int i=0;i<files.size();i++){
				tmpFile = new File("copy_xls_temp.xls");
			    Workbook sourceDocument = Workbook.getWorkbook(new File( (String)files.get(i) ));
			    WritableWorkbook writableTempSource = Workbook.createWorkbook(tmpFile, sourceDocument);
			    
			    for(int j=0;j<writableTempSource.getNumberOfSheets();j++){
			    	WritableSheet sourceSheet = writableTempSource.getSheet(j);
			    	String sheetname = sourceSheet.getName();
			    	// blank sheet
			    	if(sourceSheet.getRows()+sourceSheet.getColumns()==0){
			    		continue;
			    	}
			    	int sheetno = 2;
			    	while( copyDocument.getSheet( sheetname ) != null ){
			    		sheetname = sourceSheet.getName() + "(" + sheetno++ + ")";
			    	}
				    WritableSheet targetSheet = copyDocument.createSheet(sheetname, sheetnum++);
				    // cells
				    for (int row = 0; row < sourceSheet.getRows(); row++) {
				        for (int col = 0; col < sourceSheet.getColumns(); col++) {
				            WritableCell readCell = sourceSheet.getWritableCell(col, row);
				            WritableCell newCell = readCell.copyTo(col, row);
				            CellFormat readFormat = readCell.getCellFormat();
				            CellFeatures readFeatures = readCell.getCellFeatures();

				            WritableCellFormat newFormat;
				            if (readFormat == null) {
				            	newFormat = new WritableCellFormat();
				            } else {
				            	newFormat = new WritableCellFormat(readFormat);
				            }
				            WritableCellFeatures newFeatures;
				            if (readFeatures == null) {
				            	newFeatures = new WritableCellFeatures();
				            } else {
				            	newFeatures = new WritableCellFeatures(readFeatures);
				            }
				            newCell.setCellFormat(newFormat);
				            newCell.setCellFeatures(newFeatures);
				            targetSheet.addCell(newCell);
				        }
				    }
				    // merged cells
				    Range[] r = sourceSheet.getMergedCells();
				    for(int k=0;k<r.length;k++){
				    	Cell topLeftCell = r[k].getTopLeft();
				    	Cell bottomRightCell = r[k].getBottomRight();
				    	
				    	targetSheet.mergeCells(topLeftCell.getColumn(), topLeftCell.getRow(), bottomRightCell.getColumn(), bottomRightCell.getRow());
				    }
			    }
			    writableTempSource.close();
			    sourceDocument.close();
			}
			copyDocument.write();
		    copyDocument.close();
			tmpFile.delete();
		} catch (Exception e) {
		    e.printStackTrace();
		    return false;
		}
		// delete
		boolean flag = true;
		for(int i=0;i<files.size();i++){
			String filePath = (String)files.get(i);
			File file = new File( filePath );
			boolean sflag = file.delete();
            if(sflag) {
            	System.out.println("文件"+filePath+"删除成功!");
            } else {
            	System.out.println("文件"+filePath+"删除失败!");
            	flag = false;
            }
		}
		
		return flag;
	}
	
	public boolean mergeXlsToOneSheet( List files, String newFile, String newSheetName ){
		int rowNum = 0;
		try {
			WritableWorkbook copyDocument = Workbook.createWorkbook(new File(newFile));
			WritableSheet targetSheet = copyDocument.createSheet(newSheetName, 0);
			File tmpFile = null;
			for(int i=0;i<files.size();i++){
				tmpFile = new File("copy_xls_temp.xls");
			    Workbook sourceDocument = Workbook.getWorkbook(new File( (String)files.get(i) ));
			    WritableWorkbook writableTempSource = Workbook.createWorkbook(tmpFile, sourceDocument);
			    
			    for(int j=0;j<writableTempSource.getNumberOfSheets();j++){
			    	WritableSheet sourceSheet = writableTempSource.getSheet(j);
			    	String sheetname = sourceSheet.getName();
			    	// blank sheet
			    	if(sourceSheet.getRows()+sourceSheet.getColumns()==0){
			    		continue;
			    	}
			    	int sheetno = 2;
			    	while( copyDocument.getSheet( sheetname ) != null ){
			    		sheetname = sourceSheet.getName() + "(" + sheetno++ + ")";
			    	}
				    
				    // cells
				    for (int row = 0; row < sourceSheet.getRows(); row++) {
				        for (int col = 0; col < sourceSheet.getColumns(); col++) {
				            WritableCell readCell = sourceSheet.getWritableCell(col, row);
				            WritableCell newCell = readCell.copyTo(col, row+rowNum);
				            CellFormat readFormat = readCell.getCellFormat();
				            CellFeatures readFeatures = readCell.getCellFeatures();

				            WritableCellFormat newFormat;
				            if (readFormat == null) {
				            	newFormat = new WritableCellFormat();
				            } else {
				            	newFormat = new WritableCellFormat(readFormat);
				            }
				            WritableCellFeatures newFeatures;
				            if (readFeatures == null) {
				            	newFeatures = new WritableCellFeatures();
				            } else {
				            	newFeatures = new WritableCellFeatures(readFeatures);
				            }
				            newCell.setCellFormat(newFormat);
				            newCell.setCellFeatures(newFeatures);
				            targetSheet.addCell(newCell);
				        }
				    }
				    
				    // merged cells
				    Range[] r = sourceSheet.getMergedCells();
				    for(int k=0;k<r.length;k++){
				    	Cell topLeftCell = r[k].getTopLeft();
				    	Cell bottomRightCell = r[k].getBottomRight();
				    	
				    	targetSheet.mergeCells(topLeftCell.getColumn(), topLeftCell.getRow()+rowNum, bottomRightCell.getColumn(), bottomRightCell.getRow()+rowNum);
				    }
				    
				    //
				    rowNum += sourceSheet.getRows();
			    }
			    writableTempSource.close();
			    sourceDocument.close();
			}
			copyDocument.write();
		    copyDocument.close();
			tmpFile.delete();
		} catch (Exception e) {
		    e.printStackTrace();
		    return false;
		}
		// delete
		boolean flag = true;
		for(int i=0;i<files.size();i++){
			String filePath = (String)files.get(i);
			File file = new File( filePath );
			boolean sflag = file.delete();
            if(sflag) {
            	System.out.println("文件"+filePath+"删除成功!");
            } else {
            	System.out.println("文件"+filePath+"删除失败!");
            	flag = false;
            }
		}
		
		return flag;
	}
}
