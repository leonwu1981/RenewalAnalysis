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
            
         // 以下部分设置格式
			exporter.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);// 删除每页最下面多余的空行
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
					Boolean.FALSE);// 删除多余的ColumnHeader
			exporter.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);// 显示边框
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
					Boolean.TRUE);// 避免跨行合并单元格
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint); 
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, file); 
//            exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, new Integer(20000));///一个sheet页中最大行数
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
		//是否启用内存（大数据量采用）
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
//			//是否启用内存（大数据量采用）
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
		
		String DUTY_COL_NAMES = "分公司代码,团体保单号,保单生效日,保单终止日,是否续保,投保单位,投保单位部门,单位性质,行业类别,主营业务,被保险人客户号,被保险人姓名,被保险人生效日,被保险人终止日,被保险人职业等级,在职或退休,与被保险人的关系,出生年月,性别,计划编码,计划名称,险种,责任,保额,保费,起付线,日药费限额,日费用限额,日床位费限额,日检查费限额,赔付比例,意外赔付比例,服务机构,年龄,年龄段,曝光数,曝光数2,费用金额,自费金额,部分自付金额,医保支付金额,实际赔付金额,门诊就诊次数,就诊次数,就诊人数,住院天数,保全类型";
		String AGE_COL_NAMES = "被保险人客户号,性别,与被保险人的关系,保全类型,年龄,年龄段";
		String CLAIM_COL_NAMES = "分公司代码,团体保单号,保单生效日,保单终止日,被保险人客户号,出生年月,性别,被保险人生效日,被保险人终止日,在职或退休,与被保险人的关系,计划编码,计划名称,被保险人赔案号,出险日期,住院结束日期,住院天数,就医医院代码,就医医院名称,医院等级,索赔原因,申请赔付日期,费用项目代码,费用项目名称,费用金额,自费金额,部分自付金额,医保支付金额,索赔项目代码,索赔项目名称,实际赔付金额,赔付日期,险种代码,出险时间段,理赔时间段,理赔延迟时间";
		
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
		String[] sheetNamesArray = new String[]{"员工 人员变化"};
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
