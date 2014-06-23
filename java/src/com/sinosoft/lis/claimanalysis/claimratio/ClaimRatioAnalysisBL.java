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
		 * Ԥ��ο��� t1~t4
		 * ��һ������ t2~(t3-1)
		 * �������� t3~t4
		 */
		t1 = "2008-1-1";
		t2 = "2010-1-1";
		t3 = "2011-1-1";
		t4 = "2011-09-30";
		// 1. download data
		DataDownloader dataDownloader = new DataDownloader(file_path);
		dataDownloader.downloadPolicyData(t1, t4); // policy.csv
		dataDownloader.exportPolicyDataSource(t3, t4); // ����ʱ�����������
		dataDownloader.downloadClaimData(t1, t4); // claim.csv
		dataDownloader.exportClaimDataSource(t1, t4, t2, t4, t3, t4); // ����ʱ�����������
		// 2. calculate CF
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		IbnrFactorCalculator.calIbnrFactors(); // ���ӽ������ibnr_factor.csv, �������, �ı���ļ�, �ٽ��б������
		this.mergeFcCsv(); // ���Ӽ������ CF.xls
		// 3. reports
		dataDownloader.exportIbnrByQuarter(t2, t4);// report1: predictclaim.csv Ԥ�����ⱨ�� 
		dataDownloader.exportInfoByQuarter(t2, t4);// report2: infoQ.csv ������ͳ�ơ���¶����������׬���ѡ��������⡱����IBNR����
		
		ClaimReportWriter claimReportWriter = new ClaimReportWriter();
		claimReportWriter.exportDataSource(); // ׼������Դ
		/**
		 * �����е�ʱ���������
		 * 1.��Ч�� effectYear ( ���ݴˣ����� effectYear��Ч����������effectYear֮ǰ��Ч���ѷ��� )
		 * 2.�ѽ�������ʱ���� ( һ���ڱ�������ǰһ������ )
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
