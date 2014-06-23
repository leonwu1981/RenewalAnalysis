package com.sinosoft.lis.claimanalysis.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;

public class ReportWriter {

	
	public boolean exportXlsReport(List jasperPrintList, String[] sheetNamesArray, String report){
		try {
//		
            File file = new File(report);
            
            JExcelApiExporter exporter = new JExcelApiExporter();
            
         // ���²������ø�ʽ
			exporter.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);// ɾ��ÿҳ���������Ŀ���
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
					Boolean.FALSE);// ɾ�������ColumnHeader
			exporter.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);// ��ʾ�߿�
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE,
					file);
			exporter
					.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			
			// exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED,
			// Boolean.TRUE);
			// exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
			// Boolean.TRUE);
			// exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER,
			// Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN,
					Boolean.TRUE);// ������кϲ���Ԫ��
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint); 
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, file); 
//            exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, new Integer(20000));///һ��sheetҳ���������
//            exporter.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, "GBK");
//            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheetNamesArray);
            exporter.exportReport();   
            
        } catch (Exception e) { 
        	e.printStackTrace(); 
            return false;  
        }
        return true;
	}
	
	public JasperPrint getJasperPrint( String[] columnNames, String dataSource, boolean useFirstLine, HashMap params, String jasper  ){
		//�Ƿ������ڴ棨�����������ã�
		JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(2);

//		JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(0, new JRSwapFile("d://cache//", 1024 * 1024, 1000), false);
//		FileProcessor FileProcessor = new FileProcessor();
//		String cacheFolder = "d://cache//";
//		FileProcessor.newFolder(cacheFolder);
//		JRFileVirtualizer virtualizer = new JRFileVirtualizer(2, cacheFolder);
//		JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(3, new JRSwapFile(cacheFolder, 2048, 1024), false);
		params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		
		JasperPrint jasperPrint = null;
		JRCsvDataSource ds;
		try {
			ds = this.getCsvDataSource(columnNames, dataSource, useFirstLine);
			jasperPrint = JasperFillManager.fillReport(jasper, params, ds);
		} catch (JRException e) {
			e.printStackTrace();
			return null;
		}
		return jasperPrint;
	}
	
	
	public JRCsvDataSource getCsvDataSource( String[] columnNames, String csv, boolean useFirstLine ) throws JRException {
	    JRCsvDataSource ds = new JRCsvDataSource( JRLoader.getFileInputStream(csv) );
	    ds.setRecordDelimiter("\r\n");
	    ds.setColumnNames(columnNames);
	    ds.setUseFirstRowAsHeader(useFirstLine);
	    return ds;
	}
	
	public boolean exportCsvReport( String dataSource, String[] columnNames, String jasper, HashMap params, String report ) { 
		return this.exportCsvReport(dataSource, columnNames, jasper, params, report, false);
	}
	
	public boolean exportCsvReport( String dataSource, String[] columnNames, String jasper, HashMap params, String report, boolean useFirstLine ) { 
    	boolean flag = true;
    	JRCsvDataSource ds = null;
		try {
//			//�Ƿ������ڴ棨�����������ã�
			JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(2);

//			JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(2, new JRSwapFile("c://cache//", 1024 * 1024, 1000), false);
//			FileProcessor FileProcessor = new FileProcessor();
//			String cacheFolder = "d://cache//";
//			FileProcessor.newFolder(cacheFolder);
//			
//			JRFileVirtualizer virtualizer = new JRFileVirtualizer(2, cacheFolder); 
//			JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(3, new JRSwapFile(cacheFolder, 2048, 1024), false);
			params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
						
    		ds = this.getCsvDataSource(columnNames, dataSource, useFirstLine);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, params, ds); 
            File file = new File(report);
            
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, jasperPrint); 
            exporter.setParameter(JRCsvExporterParameter.OUTPUT_FILE, file); 
            exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "GBK");
            exporter.setParameter(JRCsvExporterParameter.RECORD_DELIMITER, "\r\n");
            exporter.exportReport();   
            
        } catch (Exception e) { 
        	e.printStackTrace(); 
            flag = false;     
        } finally {
        	if( ds != null ){
        		ds.close();
        	}
        }
        return flag;
    }
	
	public boolean exportCsvReport( JasperPrint jasperPrint, String report ) { 
    	boolean flag = true;
    	JRCsvDataSource ds = null;
		try {
			File file = new File(report);
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, jasperPrint); 
            exporter.setParameter(JRCsvExporterParameter.OUTPUT_FILE, file); 
            exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "GBK");
            exporter.setParameter(JRCsvExporterParameter.RECORD_DELIMITER, "\r\n");
            exporter.exportReport();   
            
        } catch (Exception e) { 
        	e.printStackTrace(); 
            flag = false;     
        } finally {
        	if( ds != null ){
        		ds.close();
        	}
        }
        return flag;
    }
	
	public static void main(String[] args) {
//		String[] ss = new String[]{"D:/temp/insuredchanges1.xls", "D:/temp/AgeDistribution1.xls"};
//		mergeXls(ss, "D:/temp/newsss.xls");
//		if(true)return;
		
		String name = "insuredchanges1";
		
		String DUTY_COL_NAMES = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�Ƿ�����,Ͷ����λ,Ͷ����λ����,��λ����,��ҵ���,��Ӫҵ��,�������˿ͻ���,������������,����������Ч��,����������ֹ��,��������ְҵ�ȼ�,��ְ������,�뱻�����˵Ĺ�ϵ,��������,�Ա�,�ƻ�����,�ƻ�����,����,����,����,����,����,��ҩ���޶�,�շ����޶�,�մ�λ���޶�,�ռ����޶�,�⸶����,�����⸶����,�������,����,�����,�ع���,�ع���2,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,ʵ���⸶���,����������,�������,��������,סԺ����,��ȫ����";
		String AGE_COL_NAMES = "�������˿ͻ���,�Ա�,�뱻�����˵Ĺ�ϵ,��ȫ����,����,�����";
		String CLAIM_COL_NAMES = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�������˿ͻ���,��������,�Ա�,����������Ч��,����������ֹ��,��ְ������,�뱻�����˵Ĺ�ϵ,�ƻ�����,�ƻ�����,���������ⰸ��,��������,סԺ��������,סԺ����,��ҽҽԺ����,��ҽҽԺ����,ҽԺ�ȼ�,����ԭ��,�����⸶����,������Ŀ����,������Ŀ����,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,������Ŀ����,������Ŀ����,ʵ���⸶���,�⸶����,���ִ���,����ʱ���,����ʱ���,�����ӳ�ʱ��";
		
		String[] columnNames = DUTY_COL_NAMES.split(",");
//		columnNames = AGE_COL_NAMES.split(",");
//		columnNames = CLAIM_COL_NAMES.split(",");

		String dataSource = "D:/temp/88000403900_88000403901_88000425700_88000425701/match.csv";
//		dataSource = "D:/temp/88000403900_88000403901_88000425700_88000425701/claim.csv";
//		dataSource = "D:/temp/AgeDistribution0.csv";
		String jasper = "D:/vss/lis2/ui/claimanalysis/renewal/jasper/"+name+".jasper";
		
		String report = "d:/temp/"+name+".xls";
//		report = "d:/temp/"+name+".csv";
		ReportWriter ReportWriter = new ReportWriter();
//		ReportWriter.exportCsvReport(dataSource, columnNames, jasper, new HashMap(), report, true);
		
		List jasperPrintList = new ArrayList();
		jasperPrintList.add(ReportWriter.getJasperPrint(columnNames, dataSource, true, new HashMap(), jasper));
		String[] sheetNamesArray = new String[]{"Ա�� ��Ա�仯"};
		ReportWriter.exportXlsReport(jasperPrintList, sheetNamesArray, report);
		

	}
	public boolean exportOneSheetXlsReport(String[] columnNames, String dataSource, HashMap params, String jasper, String sheetName, String report){
		List jasperPrintList = new ArrayList();
		jasperPrintList.add(this.getJasperPrint(columnNames, dataSource, true, params, jasper));
		String[] sheetNamesArray = new String[]{sheetName};
		return this.exportXlsReport(jasperPrintList, sheetNamesArray, report);
	}
	
	public boolean exportOneSheetXlsReport(String[] columnNames, String dataSource, HashMap params, String jasper, String sheetName, String report, boolean useFirstLine){
		List jasperPrintList = new ArrayList();
		jasperPrintList.add(this.getJasperPrint(columnNames, dataSource, useFirstLine, params, jasper));
		String[] sheetNamesArray = new String[]{sheetName};
		return this.exportXlsReport(jasperPrintList, sheetNamesArray, report);
	}
	public static void mergeXls( String[] subFiles, String newFile ){
		WritableWorkbook newXls;
		try {
			newXls = Workbook.createWorkbook(new File(newFile));
//			WritableSheet sheet = newXls.createSheet("Sheet_1", 0); 
//			Label label = new Label(0, 0, "test");  
//			
//			//add defined cell above to sheet instance.   
//			sheet.addCell(label);   
//			//create cell using add numeric. WARN:necessarily use integrated package-path, otherwise will be throws path-error.   
//			//cell is 2nd-Column, 1st-Row. value is 789.123.   
//			jxl.write.Number number = new jxl.write.Number(1, 0, 789.123);   
//			//add defined cell above to sheet instance.   
//			sheet.addCell(number);  
			int sheetNum = 0;
			for(int i=0;i<subFiles.length;i++){
				Workbook workbook;
				workbook = Workbook.getWorkbook(new File(subFiles[i]));
				Sheet[] sheets = workbook.getSheets();
				for(int j=0;j<sheets.length;j++){
					Sheet sheet = sheets[j];
					WritableSheet newSheet = newXls.createSheet(sheet.getName(), sheetNum++);
					for (int row = 0; row < sheet.getRows(); row++) {
						for (int col = 0; col < sheet.getColumns(); col++) {
							Cell readCell = sheet.getCell(col, row);
							
//							WritableCell newCell = readCell.copyTo(col, row);
//							CellFormat readFormat = readCell.getCellFormat();
//							WritableCellFormat newFormat = new WritableCellFormat(readFormat);
//							newCell.setCellFormat(newFormat);
//							newSheet.addCell(newCell);
						}
					}
				}
				workbook.close();
			}

			newXls.write();
			newXls.close();

		}
		catch (BiffException e) {
			e.printStackTrace();
		} 
		catch (WriteException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}


	}
}
