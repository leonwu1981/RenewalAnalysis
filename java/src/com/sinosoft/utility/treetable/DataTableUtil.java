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
		 * ��dateTable���ݵ��뵽Excel�ļ���
		 * @param dt:DataTable
		 * @param os:Excel�ļ�
		 * @param columnNames:����
		 * @param columnWidths:�п�,�ɴ���null��Ĭ�Ͽ�Ϊ80
		 * @param tableName:���ɵı���,�ɴ���null
		 * @param tableHeads:���ɵı�ͷ��Ϣ,�ɴ���null
		 * @param RowIDFlag:�Ƿ���Ҫ���.true ��Ҫ,false ����Ҫ
		 * @return 
		 */
	 public static void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag) {
	    	HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="���";
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
				//�����Ƶ�ʽ��(�����þ��к�������ʽ���������ǲ���20������)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("����");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//��ͷ��ʽ��
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("����");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//��ͷ��ʽ��
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //�Զ�����
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("����");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//�б����ݵ�ʽ��
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�߿�
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("����");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //�Ѵ��ڵ�����
				
				//����
				if(tableName!=null && !tableName.equals(""))
				{
		            //�����еĺϲ�
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
		            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//��ͷ��Ϣ
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//�ϲ�
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
			            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				
				// ת������
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
				
				// �������
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
		 * ��dateTable���ݵ��뵽Excel�ļ���(������������,��3����,��2�����м����"T"����ͷ)
		 * @param dt:DataTable
		 * @param os:Excel�ļ�
		 * @param columnNames:����
		 * @param columnWidths:�п�,�ɴ���null��Ĭ�Ͽ�Ϊ80
		 * @param tableName:���ɵı���,�ɴ���null
		 * @param tableHeads:���ɵı�ͷ��Ϣ,�ɴ���null
		 * @param RowIDFlag:�Ƿ���Ҫ���.true ��Ҫ,false ����Ҫ
		 * @param MergedRegionFlag:�Ƿ���ںϲ���ͷ.true ����(tableHeads1,����Ϊ��),false ������(tableHeads1,����Ϊnull)
		 * @param tableHeads1:�ϲ���ͷ����ֹλ�ü����� ������ʽ:�� String[][] title1 = {{"0","1","0","2","�ϲ���1"},{"0","3","0","4","�ϲ���2"}};  //�ϲ�0��1�е�0��2��,����Ϊ�ϲ���1
		 * @return 
		 */
	 public void dataTableToExcel(DataTable dt, OutputStream os, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,boolean MergedRegionFlag,String[][] tableHeads1) {
	    	HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			int ColNameRow = 0;  //������ʼ���ڵ���
			if(RowIDFlag){
				//����е�����
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="���";
				for(int i=0;i<tempcolumnNames.length;i++){
					columnNames[i+1] = tempcolumnNames[i];
				}
				//����е��п�
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
				//����еĺϲ�����
				if(tableHeads1!=null && tableHeads1.length>0){
					for(int k = 0;k<tableHeads1.length;k++){
						tableHeads1[k][1] = String.valueOf(Integer.parseInt(tableHeads1[k][1]) + 1);
						tableHeads1[k][3] = String.valueOf(Integer.parseInt(tableHeads1[k][3]) + 1);
					}
				}

			}
			//���Ƿ���ںϲ���ͷ�ı�־����У��
			if(MergedRegionFlag && tableHeads1!=null && tableHeads1.length>0){
				MergedRegionFlag = true;
			}else{
				MergedRegionFlag = false;
			}
			
			try {
				//�����Ƶ�ʽ��(�����þ��к�������ʽ���������ǲ���20������)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("����");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//��ͷ��ʽ��
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("����");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//��ͷ��ʽ��
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //�Զ�����
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("����");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//�б����ݵ�ʽ��
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�߿�
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("����");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //�Ѵ��ڵ�����
				
				//����
				if(tableName!=null && !tableName.equals(""))
				{
		            //�����еĺϲ�
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
		            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//��ͷ��Ϣ
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//�ϲ�
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
			            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				//���ڱ�ͷ��2�����,1�в����кϲ���ʾ����,2�еĲ����зֿ���ʾ,1��Ϊ����
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
				
				// ת������,2��Ϊʵ�ʵı�ͷ��
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
				
				//�ϲ��Ĵ���
				if(MergedRegionFlag){
					//�����еĺϲ�
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
							sheet.addMergedRegion(new Region((short)ColNameRow,(short)k,(short)(ColNameRow+1),(short)k)); //0��0�е�1��0�кϲ�
						}
						
					}
					//һ���еĺϲ�
					for(int k = 0;k<tableHeads1.length;k++){
						sheet.addMergedRegion(new Region((short)Integer.parseInt(tableHeads1[k][0]),(short)Integer.parseInt(tableHeads1[k][1]),(short)Integer.parseInt(tableHeads1[k][2]),(short)Integer.parseInt(tableHeads1[k][3])));
					}	
				}
				
				
				// �������
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
		 * ��dateTable���ݵ��뵽Excel�ļ���(�ֶ�β�ѯ,����ļ���ʽ),��dataTableAddToExcelһ��ʹ��
		 * @param dt:DataTable
		 * @param fileName:���ɵ�Excel�ļ�·��������
		 * @param columnNames:����
		 * @param columnWidths:�п�,�ɴ���null��Ĭ�Ͽ�Ϊ80
		 * @param tableName:���ɵı���,�ɴ���null
		 * @param tableHeads:���ɵı�ͷ��Ϣ,�ɴ���null
		 * @param RowIDFlag:�Ƿ���Ҫ���.true ��Ҫ,false ����Ҫ
		 * @param startRowID:��ŵ���ʼ��(���ڶ�Excel�ķ�ʽ,����Ҫʱ����0)
		 * @return 
		 */
	 public static void dataTableToExcel(DataTable dt, String fileName, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,int startRowID) {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("First");
			
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="���";
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
				//�����Ƶ�ʽ��(�����þ��к�������ʽ���������ǲ���20������)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("����");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//��ͷ��ʽ��
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("����");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//��ͷ��ʽ��
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //�Զ�����
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("����");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//�б����ݵ�ʽ��
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�߿�
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("����");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //�Ѵ��ڵ�����
				
				//����
				if(tableName!=null && !tableName.equals(""))
				{
		            //�����еĺϲ�
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
		            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
		            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
		            sumRow++;
				}
				
				//��ͷ��Ϣ
				if(tableHeads!=null && tableHeads.length>0)
				{
					for(int i = 0 ;i<tableHeads.length; i++)
					{
						//�ϲ�
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
			            //��Ԫ��ϲ���Ȼ����Ӻϲ�����
			            sheet.addMergedRegion(new Region(sumRow,(short)0,sumRow,(short)(columnNames.length-1))); 
			            sumRow++;
					}
				}
				
				// ת������
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
				
				// �������
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
	   		    //�������ļ�
	   		    File file = new File(fileName);
				FileOutputStream fStream = null;
				
				try {
					fStream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				BufferedOutputStream os = new BufferedOutputStream(fStream);
				wb.write(os); //д���ļ�
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		/**
		 * ��dateTable����׷�ӵ�Excel�ļ���(�ֶ�β�ѯ,����ļ���ʽ),��dataTableToExcelһ��ʹ��
		 * @param dt:DataTable
		 * @param fileName:���ɵ�Excel�ļ�·��������
		 * @param columnNames:����
		 * @param columnWidths:�п�,�ɴ���null��Ĭ�Ͽ�Ϊ80
		 * @param tableName:���ɵı���,�ɴ���null
		 * @param tableHeads:���ɵı�ͷ��Ϣ,�ɴ���null
		 * @param RowIDFlag:�Ƿ���Ҫ���.true ��Ҫ,false ����Ҫ
		 * @param startRowID:��ŵ���ʼ��(���ڶ�Excel�ķ�ʽ,����Ҫʱ����0)
		 * @return 
		 */                               
	 public static void dataTableAddToExcel(DataTable dt, String fileName, String[] columnNames, String[] columnWidths,String tableName,String[] tableHeads,boolean RowIDFlag,int startRowID) {
			
			//����,�п�ȴ���
			if(RowIDFlag){
				String[] tempcolumnNames =  columnNames;
				columnNames = new String[(tempcolumnNames.length+1)];
				columnNames[0]="���";
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
				HSSFSheet sheet = wb.getSheetAt(0); //��excel�ĵ�һ�������� 
				
				//�����Ƶ�ʽ��(�����þ��к�������ʽ���������ǲ���20������)
	            HSSFCellStyle styleTableName = wb.createCellStyle();
	            styleTableName.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	            HSSFFont fontTableName = wb.createFont();
	            fontTableName.setFontHeightInPoints((short) 20);
	            fontTableName.setFontName("����");
	            fontTableName.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	            styleTableName.setFont(fontTableName);
	            
				//��ͷ��ʽ��
	            HSSFCellStyle styleTableHead = wb.createCellStyle();
	            styleTableHead.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	            styleTableHead.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				HSSFFont fontTableHead = wb.createFont();
				fontTableHead.setFontHeightInPoints((short) 12);
				fontTableHead.setFontName("����");
				fontTableHead.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	            styleTableHead.setFont(fontTableHead);

				//��ͷ��ʽ��
				HSSFCellStyle styleBorderBold = wb.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				styleBorderBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				styleBorderBold.setWrapText(true); //�Զ�����
				HSSFFont fontBold = wb.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("����");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				styleBorderBold.setFont(fontBold);

				//�б����ݵ�ʽ��
				HSSFCellStyle styleBorderNormal = wb.createCellStyle();
				styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN); //�߿�
				styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);//�߿�
				styleBorderNormal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				HSSFFont fontNormal = wb.createFont();
				fontNormal.setFontHeightInPoints((short) 10);
				fontNormal.setFontName("����");
				fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				styleBorderNormal.setFont(fontNormal);

				HSSFCellStyle styleBold = wb.createCellStyle();
				styleBold.setFont(fontBold);

				HSSFCellStyle styleNormal = wb.createCellStyle();
				styleNormal.setFont(fontNormal);

				int sumRow = 0;  //�Ѵ��ڵ�����
				sumRow = getRowCount(wb,0);
				
				// �������
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
		 * ��ȡָ��sheetNum��rowCount
		 * 
		 * @param sheetNum
		 * @return int
		 */
		public static int getRowCount(HSSFWorkbook wb,int sheetNum) {
			HSSFSheet sheet = wb.getSheetAt(sheetNum);
			int rowCount = -1;
			rowCount = sheet.getLastRowNum();
			for (int i = rowCount; i >= 1; i--) {// ѭ��ÿһ��
				HSSFRow row = sheet.getRow(i);// ������
				if (row == null) {
					rowCount--;
					continue;
				}
				HSSFCell cell = row.getCell((short) 0);// �������е�һ����Ԫ��
				HSSFCell cell1 = row.getCell((short) 1);// �������еĶ�����Ԫ��
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
		 * ��ȡ��Ԫ����������Ϊ�ַ������͵�����   
		 * @param cell Excel��Ԫ��   
		 * @return String ��Ԫ����������   
		 */
		private static String getStringCellValue(HSSFCell cell) {
			if (cell == null) {
				return "";
			}
			String strCell = "";
			switch (cell.getCellType()) {
			// ��Ԫ������Ϊ����
			case HSSFCell.CELL_TYPE_NUMERIC:
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			// ��Ԫ������Ϊ�ַ���
			case HSSFCell.CELL_TYPE_STRING:
				strCell = cell.getStringCellValue();
				if (strCell.trim().equals("") || strCell.trim().length() <= 0)
					strCell = " ";
				break;
			// ��Ԫ������Ϊ��ʽ
			case HSSFCell.CELL_TYPE_FORMULA:
				strCell = cell.getCellFormula();
				break;
			// ��Ԫ������Ϊ����ֵ
			case HSSFCell.CELL_TYPE_BOOLEAN:
				strCell = String.valueOf(cell.getBooleanCellValue());
				break;
			// ��Ԫ������Ϊ����
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
		 * ��dateTable���ݵ��뵽txt�ļ���
		 * @param dt:DataTable
		 * @param os:OutputStream
		 * @param columnNames:��ͷ��.  Ϊnullʱ,û����ͷ��Ϣ
		 * @param filedspliter:�ֶηָ���. Ϊnullʱ����","
		 * @param rowspliter:�зָ���. Ϊnullʱ����"\n"
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
			
			//��ͷ
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
			//������Ϣ
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
