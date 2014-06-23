package com.sinosoft.lis.claimanalysis.claimratio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;

public class ClaimRatioAnalysisBL {
	String file_path = "d:/ClaimAnal/";
	
	public static void main(String[] args) {
		ClaimRatioAnalysisBL analysis = new ClaimRatioAnalysisBL();
//		analysis.analysis();
	}
	
	public void analysis(){
		// 0. params
		String t1;
		String t2;
		String t3;
		String t4;
		/**
		 * 预测参考期 t1~t4
		 * 上一评估期 t2~(t3-1)
		 * 本评估期 t3~t4
		 */
		t1 = "2008-1-1";
		t2 = "2010-1-1";
		t3 = "2011-1-1";
		t4 = "2011-09-30";
		// 1. download data
		DataDownloader dataDownloader = new DataDownloader(file_path);
		dataDownloader.downloadPolicyData(t1, t4); // policy.csv
		dataDownloader.exportPolicyDataSource(t3, t4); // 根据时间段整理数据
		dataDownloader.downloadClaimData(t1, t4); // claim.csv
		dataDownloader.exportClaimDataSource(t1, t4, t2, t4, t3, t4); // 根据时间段整理数据
		// 2. calculate CF
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		IbnrFactorCalculator.calIbnrFactors(); // 因子结果放在ibnr_factor.csv, 如需调整, 改变此文件, 再进行报表计算
		this.mergeFcCsv(); // 因子计算过程 CF.xls
		// 3. reports
		dataDownloader.exportIbnrByQuarter(t2, t4);// report1: predictclaim.csv 预测理赔报表 
		dataDownloader.exportInfoByQuarter(t2, t4);// report2: infoQ.csv 按季度统计“暴露数”、“已赚保费”、“理赔”、“IBNR”等
		
		ClaimReportWriter claimReportWriter = new ClaimReportWriter();
		claimReportWriter.exportDataSource(); // 准备数据源
		/**
		 * 报表中的时间参数设置
		 * 1.生效年 effectYear ( 根据此，生成 effectYear生效保单分析及effectYear之前生效保费分析 )
		 * 2.已结束保单时间线 ( 一般在本评估期前一个季度 )
		 */
		int effectYear = 2011; // Integer.parseInt( t4.substring(0, 4) );
		String runoffLine = "2011-06-30";
		
		claimReportWriter.writeReports( effectYear, runoffLine); // report3: report.xls
	}
	
	public void mergeFcCsv(){
//		String[] types = new String[]{"BJ_NONGEB_MED01","BJ_NONGEB_MED02","BJ_GEB_MED01","BJ_GEB_MED02","BJ_WMP","BJ_AMR","SH_NONGEB_MED01","SH_NONGEB_MED02","SH_GEB_MED01","SH_GEB_MED02","SH_WMP","SH_AMR","GD_NONGEB_MED01","GD_NONGEB_MED02","GD_GEB_MED01","GD_GEB_MED02","GD_AMR","GC_MED01","GC_MED02","GC_WMP","GC_AMR","Captive_BJ_MED"};
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		String[] types = IbnrFactorCalculator.getIbnrTypes();
		List sheetNames = new ArrayList();
		List files = new ArrayList();
		for(int i=0;i<types.length;i++){
			String type = types[i];
			sheetNames.add(type);
			files.add( file_path + "data/IBNR/ibnr_" + type + ".csv" );
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		FileProcessor.mergeCsvToXls(files, sheetNames, file_path + "data/CF1.xls");
	}

}
