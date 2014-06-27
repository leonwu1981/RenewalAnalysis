package com.sinosoft.lis.claimanalysis.renewal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.sinosoft.lis.claimanalysis.common.EmailSender;
import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

public class RenewalAnalysisBL {
	String RealPath;
	
	String mGrpContNo;
	String mStartDate;
	String mEndDate;
	private String mTaskDate;
	private String mTaskComment;
	String mEmail;
	
	String orgs;
	
	public CErrors mErrors = new CErrors();
	private VData mResult = new VData();
	private GlobalInput mGlobalInput;
	private VData mInputData;
    private String mOperate;
    private FileDao FileDao = new FileDao();
    private TaskDao TaskDao = new TaskDao();
	public boolean submitData(VData cInputData, String cOperate) {
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;
        if (!this.getInputData(cInputData)) {
        	return false;
        }
        mResult.clear();
        Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mTaskDate = dateFm.format(date);
        if (!TaskDao.addTask(mTaskDate, mGlobalInput.Operator, mGrpContNo, mStartDate, mEndDate, mTaskComment, mEmail, mGlobalInput.ManageCom)) {
        	return false;
        }
//        String taskDate = mTaskDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
//        String bat = RealPath + "bat/test.bat";
//        // param1 path
//        // param2 log
//        String log = RealPath + "log/"+mGlobalInput.Operator+"_"+taskDate+".log";
//        String command = "cmd /k start "+bat+" "+RealPath+" "+log;
//		try {
//			Runtime.getRuntime().exec(command);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        String taskDate = mTaskDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
        String relativePath =  "file/" + mGlobalInput.Operator + "/"+taskDate+"/";
        String filePath = RealPath + relativePath;
        String jasperPath = RealPath + "jasper/";
        String taskStatus;
        if (!this.downloadData(filePath,null)) {
        	taskStatus = "��������ʧ��";
        	TaskDao.updateTaskStatus(mTaskDate, mGlobalInput.Operator, taskStatus);
        } else {
        	taskStatus = "�������ݳɹ�";
        	String createDate = dateFm.format(new Date());
            FileDao.addFile(relativePath, "claim.csv", "��������", mTaskDate, mGlobalInput.Operator, "report", "���ɳɹ�", createDate);
            FileDao.addFile(relativePath, "match.csv", "��������ƥ������", mTaskDate, mGlobalInput.Operator, "report", "���ɳɹ�", createDate);
            if (!this.createReport(filePath, jasperPath, relativePath, mTaskDate, mGlobalInput.Operator )) {
            	taskStatus = taskStatus + ";���ɱ���ʧ��";
            } else {
            	taskStatus = taskStatus + ";���ɱ���ɹ�";
            }
            
            if (!this.predictClaim(filePath, jasperPath, relativePath)) {
            	taskStatus = taskStatus + ";Ԥ������ʧ��";
            } else {
            	taskStatus = taskStatus + ";Ԥ������ɹ�";
            }
            
            TaskDao.updateTaskStatus(mTaskDate, mGlobalInput.Operator, taskStatus);
            //
            String mailContent = "���������½�����( "+ mGlobalInput.Operator + "," + mTaskDate +" )�ѽ�����ִ�н��Ϊ  "+taskStatus+".";
            mailContent = mailContent + "<br>���¼����ϵͳ�����ⰸ��-->����ͳ��-->��������-->��������-->�����б�  �鿴����.";
            EmailSender EmailSender = new EmailSender();
            EmailSender.sendEmail(mEmail, mGlobalInput.Operator, mGlobalInput.ManageCom, "���������������֪ͨ�ʼ�", mailContent);
            
        }    
        
        return true;
    }
	
	
	private boolean predictClaim(String filePath, String jasperPath, String relativePath){
		ClaimPredictBL2 aClaimPredictBL = new ClaimPredictBL2();
		aClaimPredictBL.id = mTaskDate.replaceAll( "-", "" ).replaceAll( " ", "" ).replaceAll( ":", "" );
		aClaimPredictBL.file_path = filePath;
		aClaimPredictBL.jasperPath = jasperPath;
		aClaimPredictBL.grpContNos = mGrpContNo;
		boolean re = aClaimPredictBL.exportPredictiveClaim();
		if(re){
			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createDate = dateFm.format(date);
			FileDao.addFile(relativePath, "predictclaim.xls", "Ԥ������", mTaskDate, mGlobalInput.Operator, "report", "���ɳɹ�", createDate);
		}
		
		return re;
	}
	
	private boolean downloadData(String filePath,String risk){
		DataDownloader aDataDownloader = new DataDownloader();
		aDataDownloader.downloadData(filePath, mGrpContNo, mStartDate, mEndDate, null, risk, orgs);
		return true;
	}
	private boolean createReport(String filePath, String jasperPath, String relativePath, String mTaskDate, String operator){
    	RenewalReportWriter reportWriter = new RenewalReportWriter();
    	reportWriter.writeReports(filePath, jasperPath, relativePath, mTaskDate, operator);
		return true;
	}
	
	
	
	
	private boolean getInputData(VData cInputData) {
    	mGlobalInput = (GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0);
    	if (mGlobalInput == null) {
    		buildError("getInputData", "û�еõ��㹻����Ϣ��");
            return false;
        }

    	TransferData mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mTransferData == null) {	
            buildError("getInputData", "û�еõ�������ŵ�������Ϣ��");
            return false;
        }
        // RealPath
        RealPath = (String) mTransferData.getValueByName("FilePath");
        if( RealPath == null || "".equals(RealPath) ){
        	buildError("getInputData", "û�еõ������·����Ϣ��");
            return false;
        }        
        RealPath = RealPath + "claimanalysis/renewal/";
        // GrpContNo ��  ���� �ָ������ַ�����ʽ����
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");

        // StartDate
        mStartDate = (String) mTransferData.getValueByName("StartDate");
        if( mStartDate == null || "".equals(mStartDate) ){
        	mStartDate = "2005-1-1";
		}
        // EndDate
        mEndDate = (String) mTransferData.getValueByName("EndDate");
        if( mEndDate == null || "".equals(mEndDate) ){
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			mEndDate = dateFm.format( date );
		}
        // Email
        mEmail = (String) mTransferData.getValueByName("Email");
        
        mTaskComment = (String) mTransferData.getValueByName("TaskComment");
        return true;
	}
	
	private void buildError(String szFunc, String szErrMsg) {
        CError cError = new CError();
        cError.moduleName = "RenewalAnalysisBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }
	
	/**
	 * ������������ 
	 */
	public static void downloadDataByYear(){
    	String[] manageComs = new String[]{"8601","8602","8603","8604","8605","8606","8607","8608","8609","8610"};
    	int[] years = new int[]{2008,2009,2011};
    	
		for(int j=0;j<years.length;j++){
			for(int i=0;i<manageComs.length;i++){
				String manageCom = manageComs[i];
    			int year = years[j];
    			String sql = "select grpcontno from lcgrpcont where managecom like '"+manageCom+"%' and cvalidate > date '"+(year-1)+"-12-31' and cvalidate < date '"+(year+1)+"-01-01'";
    			ExeSQL exeSQL = new ExeSQL();
    			SSRS ssrs = exeSQL.execSQL(sql);
    			String grpContNo = "";
    			for (int a = 1; a <= ssrs.getMaxRow(); a++) {
    				grpContNo = grpContNo + "," + ssrs.GetText(a, 1);
    			}
    			if("".equals(grpContNo)){
    				continue;
    			}
    			DataDownloader DataDownloader = new DataDownloader();
				DataDownloader.downloadData("D:/renewal_analysis/"+year+"/"+manageCom+"/", grpContNo, "2005-01-01", "2014-01-01", "", "","");
//    			DataDownloader.downloadData("D:/renewal_analysis/", grpContNo, "1980-01-01", "2013-06-18", "", "","");
    		}
    	}
    	
    }
	
    public static void main(String[] args) {
//    	downloadDataByYear();
    	
    	
    	String[] path = {
    			"���ϴ�˹������񣨴��������޹�˾"

    			};
    	String[] grpconts = {
    			"880007427,880007426,880007425,880007424,880007423,88001075400,88001075401"
    			
    	};
    	
    	String endDate = "2014-05-31";
    	
    	String[] orgs = {
    			"003|003|003|003|003|%|%"
    			
    	};

    	for (int i = 0; i < path.length; i++) {
    		RenewalAnalysisBL ra = new RenewalAnalysisBL();
    		ra.mGrpContNo = grpconts[i];
    		if(!ra.checkGrpContNo()){
    			System.out.println(path[i]+"�ı����������⣡");
    		}
    	}
    	
    	System.out.println("�����ż�����");
    	
    	for (int i = 0; i < path.length; i++) {
        	RenewalAnalysisBL ra = new RenewalAnalysisBL();
        	String filePath;
        	String jasperPath = "D:/workspace/dev2/ui/claimanalysis/renewal/jasper/";
            ra.mStartDate = "2000-01-01";
            SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
    		Date date = new Date();
    		ra.mEndDate = dateFm.format( date );
//    		ra.mEndDate = endDate[i];
    		ra.mEndDate = endDate;
    		
    		ra.orgs = orgs[i];
    		
    		ClaimPredictBL test;
    		String id = ra.mEndDate.replaceAll( "-", "" );

    		filePath = "D:/temp/"+path[i]+"/";
    		ra.mGrpContNo = grpconts[i];
    		
    		// 1.��������
    		//insurance,�������ƣ����˸�ʽString ���ͣ���","�ֿ���������
    		ra.downloadData(filePath,null);
    		// 2.���ɱ���
    		ra.createReport(filePath, jasperPath, null, null, null );
    		// 3.����Ԥ������
    		test = new ClaimPredictBL();
    		test.id = id;
    		test.file_path = filePath;
    		test.jasperPath = jasperPath;
    		test.grpContNos = ra.mGrpContNo;
    		test.exportPredictiveClaim();	
    		
    		ClaimPredictBL2 test2;
    		test2 = new ClaimPredictBL2();
    		test2.id = id;
    		test2.file_path = filePath;
    		test2.jasperPath = jasperPath;
    		test2.grpContNos = ra.mGrpContNo;
    		test2.exportPredictiveClaim();	
    		
    		File[] f = {new File(filePath+"match.csv"),new File(filePath+"claim.csv"),
    				new File(filePath+"report.xls"),new File(filePath+"predictclaim.xls"),
    				new File(filePath+"predictclaim_ALL.xls")};
    		
    		File zip = new File("D:/temp/"+path[i]+".zip");
    		zipFiles(f,zip);
		}

    }


	private boolean checkGrpContNo() {
		String[] grpcontno = mGrpContNo.split(",");
		boolean b = true;
		for(int i=0;i<grpcontno.length;i++){
			String sql = "select count(1) from grpcont_view where grpcontno='"+grpcontno[i]+"'";
			ExeSQL exeSQL = new ExeSQL();
			String count = exeSQL.getOneValue(sql);
			if(!"1".equals(count)){
				b = false;
				System.out.println("�����������⣡"+grpcontno[i]);
				break;
			}
		}
		return b;

	}
	
	public static void zipFiles(File[] srcfile,File zipfile){
        byte[] buf=new byte[1024];
        try {
            //ZipOutputStream�ࣺ����ļ����ļ��е�ѹ��
            ZipOutputStream out=new ZipOutputStream(new FileOutputStream(zipfile));
            for(int i=0;i<srcfile.length;i++){
                FileInputStream in=new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            System.out.println("ѹ�����.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}