package com.sinosoft.utility.treetable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;


public class DataTableUtil {

		/**
		 * 将dateTable数据导入到Excel文件中
		 * @param dt:DataTable
		 * @param os:Excel文件
		 * @param columnNames:列名
		 * @param columnWidths:列宽,可传入null，默认宽为80
		 * @param tableName:生成的表名,可传入null
		 * @param tableHeads:生成的表头信息,可传入null
		 * @param RowIDFlag:是否需要序号.true 需要,false 不需要
		 * @return 
		 */
	 public static void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag) {
	    	HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="序号";
				for(int i=0;i<tempcolumnNames.length;i++){
					columnNames[i+1] = tempcolumnNames[i];
				}
				if(columnWidths!=null && columnWidths.length==(columnNames.length-1)){
					String[] temcolumnWidths =  columnWidths;
					columnWidths = new String[(temcolumnWidths.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<temcolumnWidths.length;i++){
						columnWidths[i+1] = temcolumnWidths[i];
					}
				}else{
					columnWidths = new String[(columnNames.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<columnNames.length;i++){
						columnWidths[i+1] = "80";
					}
				}
			}
			try {
				//表名称的式样(先设置居中和字体样式，这里我们采用20号字体)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("宋体");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//表头的式样
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("宋体");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//列头的式样
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //自动换行
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("宋体");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//列表数据的式样
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //边框
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("宋体");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //已存在的行数
				
				//表名
				if(tableName!=null && !tableName.equals(""))
				{
		            //标题行的合并
					HSSFRow row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (20 * 23));
					HSSFCell cell = row.getCell((short) 0);
					if (cell == null) {
						cell = row.createCell((short) 0);
					}
		            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		            cell.setCellStyle(styleTableName);
		            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		            cell.setCellValue(tableName);
		            //单元格合并，然后添加合并区域：
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//表头信息
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//合并
						HSSFRow row = sheet.getRow(sumRow);
						if (row == null) {
							row = sheet.createRow(sumRow);
						}
						row.setHeight((short) (18 * 23));
						HSSFCell cell = row.getCell((short) 0);
						if (cell == null) {
							cell = row.createCell((short) 0);
						}
			            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			            cell.setCellStyle(styleTableHead);
			            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			            cell.setCellValue(tableHeads[i]);
			            //单元格合并，然后添加合并区域：
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				
				// 转换列名
				HSSFRow row = sheet.getRow(sumRow);
				if (row == null) {
					row = sheet.createRow(sumRow);
				}
				for (int i = 0; i < columnNames.length; i++) {
					HSSFCell cell = row.getCell((short) i);
					if (cell == null) {
						cell = row.createCell((short) i);
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(styleBorderBold);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(columnNames[i]);
					row.setHeightInPoints(30);
					if (columnWidths != null && columnWidths.length > i) {
						sheet.setColumnWidth((short) i, (short) (Short.parseShort(columnWidths[i]) * 37.5));
					}
				}
				sumRow++;
				
				// 填充数据
				for (int i = 0; i < dt.getRowCount(); i++) {	
					row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (15.5 * 23));
					
					if(RowIDFlag){
						for (int j = 0; j < dt.getColCount()+1 && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if(j==0){
								cell.setCellValue((i+1));
							}else if(dt.get(i, (j-1)) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, (j-1)));
							}
						}
					}else{
						for (int j = 0; j < dt.getColCount() && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if (dt.get(i, j) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, j));
							}
						}
					}
					sumRow++;
				}
				wb.write(os);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	 /**
		 * 将dateTable数据导入到Excel文件中(复杂列名处理,如3个口,第2个口中间填个"T"的列头)
		 * @param dt:DataTable
		 * @param os:Excel文件
		 * @param columnNames:列名
		 * @param columnWidths:列宽,可传入null，默认宽为80
		 * @param tableName:生成的表名,可传入null
		 * @param tableHeads:生成的表头信息,可传入null
		 * @param RowIDFlag:是否需要序号.true 需要,false 不需要
		 * @param MergedRegionFlag:是否存在合并列头.true 存在(tableHeads1,不能为空),false 不存在(tableHeads1,可以为null)
		 * @param tableHeads1:合并列头的起止位置及名称 参数形式:如 String[][] title1 = {{"0","1","0","2","合并列1"},{"0","3","0","4","合并列2"}};  //合并0行1列到0行2列,列名为合并列1
		 * @return 
		 */
	 public void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,boolean MergedRegionFlag,String[][] tableHeads1) {
	    	HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			int ColNameRow = 0;  //列名开始所在的行
			if(RowIDFlag){
				//序号列的列名
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="序号";
				for(int i=0;i<tempcolumnNames.length;i++){
					columnNames[i+1] = tempcolumnNames[i];
				}
				//序号列的列宽
				if(columnWidths!=null && columnWidths.length==(columnNames.length-1)){
					String[] temcolumnWidths =  columnWidths;
					columnWidths = new String[(temcolumnWidths.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<temcolumnWidths.length;i++){
						columnWidths[i+1] = temcolumnWidths[i];
					}
				}else{
					columnWidths = new String[(columnNames.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<columnNames.length;i++){
						columnWidths[i+1] = "80";
					}
				}
				//序号列的合并处理
				if(tableHeads1!=null && tableHeads1.length>0){
					for(int k = 0;k<tableHeads1.length;k++){
						tableHeads1[k][1] = String.valueOf(Integer.parseInt(tableHeads1[k][1]) + 1);
						tableHeads1[k][3] = String.valueOf(Integer.parseInt(tableHeads1[k][3]) + 1);
					}
				}

			}
			//对是否存在合并列头的标志进行校验
			if(MergedRegionFlag && tableHeads1!=null && tableHeads1.length>0){
				MergedRegionFlag = true;
			}else{
				MergedRegionFlag = false;
			}
			
			try {
				//表名称的式样(先设置居中和字体样式，这里我们采用20号字体)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("宋体");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//表头的式样
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("宋体");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//列头的式样
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //自动换行
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("宋体");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//列表数据的式样
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //边框
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("宋体");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //已存在的行数
				
				//表名
				if(tableName!=null && !tableName.equals(""))
				{
		            //标题行的合并
					HSSFRow row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (20 * 23));
					HSSFCell cell = row.getCell((short) 0);
					if (cell == null) {
						cell = row.createCell((short) 0);
					}
		            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		            cell.setCellStyle(styleTableName);
		            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		            cell.setCellValue(tableName);
		            //单元格合并，然后添加合并区域：
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//表头信息
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//合并
						HSSFRow row = sheet.getRow(sumRow);
						if (row == null) {
							row = sheet.createRow(sumRow);
						}
						row.setHeight((short) (18 * 23));
						HSSFCell cell = row.getCell((short) 0);
						if (cell == null) {
							cell = row.createCell((short) 0);
						}
			            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			            cell.setCellStyle(styleTableHead);
			            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			            cell.setCellValue(tableHeads[i]);
			            //单元格合并，然后添加合并区域：
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				//由于表头由2行组成,1行部分列合并显示在上,2行的部分列分开显示,1行为空行
				if(MergedRegionFlag){
					ColNameRow = sumRow;
					HSSFRow row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					for (int i = 0; i < columnNames.length; i++) {
						HSSFCell cell = row.getCell((short) i);
						if (cell == null) {
							cell = row.createCell((short) i);
						}
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellStyle(styleBorderBold);
						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(columnNames[i]);
						for(int k = 0;k<tableHeads1.length;k++){
							if(Integer.parseInt(tableHeads1[k][0])==sumRow
									&& Integer.parseInt(tableHeads1[k][1])==i ){
								cell.setCellValue(tableHeads1[k][4]);
							}
						}
						row.setHeightInPoints(20);
						if (columnWidths != null && columnWidths.length > i) {
							sheet.setColumnWidth((short) i, (short) (Short.parseShort(columnWidths[i]) * 37.5));
						}
					}
					sumRow++;
				}
				
				// 转换列名,2行为实际的表头行
				HSSFRow row = sheet.getRow(sumRow);
				if (row == null) {
					row = sheet.createRow(sumRow);
				}
				for (int i = 0; i < columnNames.length; i++) {
					HSSFCell cell = row.getCell((short) i);
					if (cell == null) {
						cell = row.createCell((short) i);
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(styleBorderBold);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(columnNames[i]);
					row.setHeightInPoints(20);
					if (columnWidths != null && columnWidths.length > i) {
						sheet.setColumnWidth((short) i, (short) (Short.parseShort(columnWidths[i]) * 37.5));
					}
				}
				sumRow++;
				
				//合并的处理
				if(MergedRegionFlag){
					//上下行的合并
					boolean flag = false;
					for(int k = 0;k<columnNames.length;k++){
						flag = false;
						for(int h = 0;h<tableHeads1.length;h++){
							if(Integer.parseInt(tableHeads1[h][0])==ColNameRow && Integer.parseInt(tableHeads1[h][2])==ColNameRow){
								if(k<Integer.parseInt(tableHeads1[h][1]) || k>Integer.parseInt(tableHeads1[h][3])){
									flag = true;
									continue;
								}else{
									flag = false;
									break;
								}
							}
						}
						if(flag){
							sheet.addMergedRegion(new Region((short)ColNameRow,(short)k,(short)(ColNameRow+1),(short)k)); //0行0列到1行0列合并
						}
						
					}
					//一行中的合并
					for(int k = 0;k<tableHeads1.length;k++){
						sheet.addMergedRegion(new Region((short)Integer.parseInt(tableHeads1[k][0]),(short)Integer.parseInt(tableHeads1[k][1]),(short)Integer.parseInt(tableHeads1[k][2]),(short)Integer.parseInt(tableHeads1[k][3])));
					}	
				}
				
				
				// 填充数据
				for (int i = 0; i < dt.getRowCount(); i++) {	
					row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (15.5 * 23));
					
					if(RowIDFlag){
						for (int j = 0; j < dt.getColCount()+1 && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if(j==0){
								cell.setCellValue((i+1));
							}else if(dt.get(i, (j-1)) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, (j-1)));
							}
						}
					}else{
						for (int j = 0; j < dt.getColCount() && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if (dt.get(i, j) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, j));
							}
						}
					}
					sumRow++;
				}
				wb.write(os);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		/**
		 * 将dateTable数据导入到Excel文件中(分多次查询,多个文件方式),和dataTableAddToExcel一起使用
		 * @param dt:DataTable
		 * @param fileName:生成的Excel文件路径及名称
		 * @param columnNames:列名
		 * @param columnWidths:列宽,可传入null，默认宽为80
		 * @param tableName:生成的表名,可传入null
		 * @param tableHeads:生成的表头信息,可传入null
		 * @param RowIDFlag:是否需要序号.true 需要,false 不需要
		 * @param startRowID:序号的起始数(对于多Excel的方式,不需要时传入0)
		 * @return 
		 */
	 public static void dataTableToExcel(DataTable dt, String fileName, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,int startRowID) {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="序号";
				for(int i=0;i<tempcolumnNames.length;i++){
					columnNames[i+1] = tempcolumnNames[i];
				}
				if(columnWidths!=null && columnWidths.length==(columnNames.length-1)){
					String[] temcolumnWidths =  columnWidths;
					columnWidths = new String[(temcolumnWidths.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<temcolumnWidths.length;i++){
						columnWidths[i+1] = temcolumnWidths[i];
					}
				}else{
					columnWidths = new String[(columnNames.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<columnNames.length;i++){
						columnWidths[i+1] = "80";
					}
				}
			}
			try {
				//表名称的式样(先设置居中和字体样式，这里我们采用20号字体)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("宋体");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//表头的式样
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("宋体");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//列头的式样
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //自动换行
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("宋体");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//列表数据的式样
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //边框
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("宋体");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //已存在的行数
				
				//表名
				if(tableName!=null && !tableName.equals(""))
				{
		            //标题行的合并
					HSSFRow row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (20 * 23));
					HSSFCell cell = row.getCell((short) 0);
					if (cell == null) {
						cell = row.createCell((short) 0);
					}
		            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		            cell.setCellStyle(styleTableName);
		            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		            cell.setCellValue(tableName);
		            //单元格合并，然后添加合并区域：
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//表头信息
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//合并
						HSSFRow row = sheet.getRow(sumRow);
						if (row == null) {
							row = sheet.createRow(sumRow);
						}
						row.setHeight((short) (18 * 23));
						HSSFCell cell = row.getCell((short) 0);
						if (cell == null) {
							cell = row.createCell((short) 0);
						}
			            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			            cell.setCellStyle(styleTableHead);
			            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			            cell.setCellValue(tableHeads[i]);
			            //单元格合并，然后添加合并区域：
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				
				// 转换列名
				HSSFRow row = sheet.getRow(sumRow);
				if (row == null) {
					row = sheet.createRow(sumRow);
				}
				for (int i = 0; i < columnNames.length; i++) {
					HSSFCell cell = row.getCell((short) i);
					if (cell == null) {
						cell = row.createCell((short) i);
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(styleBorderBold);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(columnNames[i]);
					row.setHeightInPoints(30);
					if (columnWidths != null && columnWidths.length > i) {
						sheet.setColumnWidth((short) i, (short) (Short.parseShort(columnWidths[i]) * 37.5));
					}
				}
				sumRow++;
				
				// 填充数据
				for (int i = 0; i < dt.getRowCount(); i++) {	
					row = sheet.getRow(sumRow);
					if (row == null) {
						row = sheet.createRow(sumRow);
					}
					row.setHeight((short) (15.5 * 23));
					
					if(RowIDFlag){
						for (int j = 0; j < dt.getColCount()+1 && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if(j==0){
								cell.setCellValue((i+1+startRowID));
							}else if(dt.get(i, (j-1)) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, (j-1)));
							}
						}
					}else{
						for (int j = 0; j < dt.getColCount() && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if (dt.get(i, j) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, j));
							}
						}
					}
					sumRow++;
				}
	   		    //生成新文件
	   		    File file = new File(fileName);
				FileOutputStream fStream = null;
				
				try {
					fStream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				BufferedOutputStream os = new BufferedOutputStream(fStream);
				wb.write(os); //写入文件
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		/**
		 * 将dateTable数据追加到Excel文件中(分多次查询,多个文件方式),和dataTableToExcel一起使用
		 * @param dt:DataTable
		 * @param fileName:生成的Excel文件路径及名称
		 * @param columnNames:列名
		 * @param columnWidths:列宽,可传入null，默认宽为80
		 * @param tableName:生成的表名,可传入null
		 * @param tableHeads:生成的表头信息,可传入null
		 * @param RowIDFlag:是否需要序号.true 需要,false 不需要
		 * @param startRowID:序号的起始数(对于多Excel的方式,不需要时传入0)
		 * @return 
		 */                               
	 public static void dataTableAddToExcel(DataTable dt, String fileName, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,int startRowID) {
			
			//列名,列宽度处理
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="序号";
				for(int i=0;i<tempcolumnNames.length;i++){
					columnNames[i+1] = tempcolumnNames[i];
				}
				if(columnWidths!=null && columnWidths.length==(columnNames.length-1)){
					String[] temcolumnWidths =  columnWidths;
					columnWidths = new String[(temcolumnWidths.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<temcolumnWidths.length;i++){
						columnWidths[i+1] = temcolumnWidths[i];
					}
				}else{
					columnWidths = new String[(columnNames.length+1)];
					columnWidths[0]="40";
					for(int i=0;i<columnNames.length;i++){
						columnWidths[i+1] = "80";
					}
				}
			}
			try {
				HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(fileName));
				HSSFSheet sheet = wb.getSheetAt(0); //对excel的第一个表引用 
				
				//表名称的式样(先设置居中和字体样式，这里我们采用20号字体)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("宋体");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//表头的式样
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("宋体");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//列头的式样
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //自动换行
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("宋体");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//列表数据的式样
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //边框
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("宋体");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //已存在的行数
				sumRow = getRowCount(wb,0);
				
				// 填充数据
				for (int i = 0; i < dt.getRowCount(); i++) {	
					HSSFRow row = sheet.getRow(sumRow+1);
					if (row == null) {
						row = sheet.createRow(sumRow+1);
					}
					row.setHeight((short) (15.5 * 23));
					
					if(RowIDFlag){
						for (int j = 0; j < dt.getColCount()+1 && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if(j==0){
								cell.setCellValue(i+1+startRowID);
							}else if(dt.get(i, (j-1)) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, (j-1)));
							}
						}
					}else{
						for (int j = 0; j < dt.getColCount() && j<columnNames.length; j++) {
							HSSFCell cell = row.getCell((short) j);
							if (cell == null) {
								cell = row.createCell((short) j);
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(styleBorderNormal);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if (dt.get(i, j) == null) {
								cell.setCellValue("");
							} else {
								cell.setCellValue(dt.getString(i, j));
							}
						}
					}
					sumRow++;
				}
			    FileOutputStream fileOut = new FileOutputStream(fileName);
			    wb.write(fileOut);
				fileOut.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	 /**
		 * 读取指定sheetNum的rowCount
		 * 
		 * @param sheetNum
		 * @return int
		 */
		public static int getRowCount(HSSFWorkbook wb,int sheetNum) {
			HSSFSheet sheet = wb.getSheetAt(sheetNum);
			int rowCount = -1;
			rowCount = sheet.getLastRowNum();
			for (int i = rowCount; i >= 1; i--) {// 循环每一行
				HSSFRow row = sheet.getRow(i);// 引用行
				if (row == null) {
					rowCount--;
					continue;
				}
				HSSFCell cell = row.getCell((short) 0);// 引用行中的一个单元格
				HSSFCell cell1 = row.getCell((short) 1);// 引用行中的二个单元格
				String str = getStringCellValue(cell);
				String str1 = getStringCellValue(cell1);
				if ((str == null || str.equals(""))&& (str1 == null || str1.equals(""))) {
					rowCount--;
				} else {
					break;
				}
			}
			return rowCount;
		}
		/**   
		 * 获取单元格数据内容为字符串类型的数据   
		 * @param cell Excel单元格   
		 * @return String 单元格数据内容   
		 */
		private static String getStringCellValue(HSSFCell cell) {
			if (cell == null) {
				return "";
			}
			String strCell = "";
			switch (cell.getCellType()) {
			// 单元格类型为数字
			case HSSFCell.CELL_TYPE_NUMERIC:
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			// 单元格类型为字符串
			case HSSFCell.CELL_TYPE_STRING:
				strCell = cell.getStringCellValue();
				if (strCell.trim().equals("") || strCell.trim().length() <= 0)
					strCell = " ";
				break;
			// 单元格类型为公式
			case HSSFCell.CELL_TYPE_FORMULA:
				strCell = cell.getCellFormula();
				break;
			// 单元格类型为布尔值
			case HSSFCell.CELL_TYPE_BOOLEAN:
				strCell = String.valueOf(cell.getBooleanCellValue());
				break;
			// 单元格类型为错误
			case HSSFCell.CELL_TYPE_ERROR:
				strCell = String.valueOf(cell.getErrorCellValue());
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				strCell = "";
				break;
			default:
				strCell = "";
				break;
			}
			if (strCell.equals("") || strCell == null) {
				return "";
			}

			return strCell.trim();
		}


		/**
		 * 将dateTable数据导入到txt文件中
		 * @param dt:DataTable
		 * @param os:OutputStream
		 * @param columnNames:列头名.  为null时,没有列头信息
		 * @param filedspliter:字段分隔符. 为null时，用","
		 * @param rowspliter:行分隔符. 为null时，用"\n"
		 * @return 
		 */
	 public static void dataTableToTxt(DataTable dt, OutputStream os, String[] columnNames, String filedspliter, String rowspliter) {
			if(filedspliter==null || filedspliter.equals("") ){
				filedspliter = ",";
			}
			if(rowspliter==null || rowspliter.equals("") ){
				rowspliter = "\n";
			}
			StringBuffer sb = new StringBuffer();
			
			//列头
//			if (columnNames == null) {
//				columnNames = new String[dt.getColCount()];
//				for (int i = 0; i < columnNames.length; i++) {
//					columnNames[i] = dt.getDataColumn(i).getColumnName();
//				}
//			}
			if(columnNames!=null)
			{
				for (int i = 0; i < columnNames.length; i++) {
					if (i != 0) {
						sb.append(filedspliter);
					}
					sb.append(columnNames[i]);
				}
				sb.append(rowspliter);
			}
			//数据信息
			for (int i = 0; i < dt.getRowCount(); i++) {
				for (int j = 0; j < dt.getColCount(); j++) {
					if (j != 0) {
						sb.append(filedspliter);
					}
					sb.append(dt.getString(i, j));
				}
				sb.append(rowspliter);
			}
			try {
				os.write((sb.toString()).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
