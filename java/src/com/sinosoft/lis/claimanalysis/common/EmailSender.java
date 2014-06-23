package com.sinosoft.lis.claimanalysis.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import com.sinosoft.lis.db.LDComDB;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.LLMailListSchema;
import com.sinosoft.utility.VData;
import com.sinosoft.xreport.util.StringUtility;

public class EmailSender {
	String tMailAddress;
	String tUser;
	String tManageCom;
	String tMailTitle;
	String tMailContent;
	public static void main(String[] args) {
		FileProcessor fileProcessor = new FileProcessor();
		fileProcessor.mergeFiles("D:\\temp\\1\\", "claim", 1, 120, "csv", "claim" ,false);
	}
	
	public boolean sendEmail(String mailAddress, String user, String manageCom, String mailTitle, String mailContent){
		this.tMailAddress = mailAddress;
		this.tUser = user;
		this.tManageCom = manageCom;
		this.tMailTitle = mailTitle;
		this.tMailContent = mailContent;
		
		LLMailListSchema tLLMailListSchema = getLLMailListSchema();
		
		addToMailList(tLLMailListSchema);		
		
		noticeEmailServer();
		
		return true;
	}
	
	public String getMessage() {
    	String templateModestr ="";
		StringBuffer tmpMode = new StringBuffer();
		tmpMode.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		tmpMode.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">    ");
		tmpMode.append("<head>");
		tmpMode.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />");
		tmpMode.append("<title>Email</title>");
		tmpMode.append("</head>");
		tmpMode.append("<body>");
		tmpMode.append("<div>");
		tmpMode.append("  <p align=\"center\"><strong>").append(tMailTitle).append("</strong><strong><span lang=\"EN-US\" xml:lang=\"EN-US\"></span></strong></p>");
		tmpMode.append("  <p>").append(tMailContent).append("</p>");
		tmpMode.append("</div>");
		tmpMode.append("</body>");
		tmpMode.append("</html>");
		templateModestr = tmpMode.toString();
		return templateModestr;
	}
	
	public LLMailListSchema getLLMailListSchema(){
		InputStream inputStream = new ByteArrayInputStream( getMessage().getBytes() );
		
		String tLimit = PubFun.getNoLimit("86");
		String tSerialNo = PubFun1.CreateMaxNo("SERIALNO",tLimit);
		
		String currentDate = PubFun.getCurrentDate();
		String currentTime = PubFun.getCurrentTime();
		
		// Glsrvbrch�ֶ�
		LDComDB tLDComDB = new LDComDB();
		tLDComDB.setComCode(tManageCom);
		if (!tLDComDB.getInfo()) {
			System.out.println("��ѯldcom��ʧ�ܣ�����Ϊ��"+tManageCom);
			return null;
		}
		String tGlsrvbrch = "";
		if ( (tLDComDB.getInnerComCode() != null) && (!tLDComDB.getInnerComCode().equals("")) ) {
			tGlsrvbrch = tLDComDB.getInnerComCode().substring(1, 3);
		}
		// Glrecvdate�ֶ�
		String tGetDate = StringUtility.replace(currentDate, "-", "");
		double tGlrecvdate = Double.parseDouble(tGetDate);
		
		LLMailListSchema tLLMailListSchema = new LLMailListSchema();
		tLLMailListSchema.setGLPOLPFX("CH");
		tLLMailListSchema.setGLPOLCOY("r"); // 3�����������֪ͨ�� ,4������ת�����ʼ�
		tLLMailListSchema.setGLPOLNUM("5");// 1�������2�������3�������ϣ�4��7/3�������
		tLLMailListSchema.setGLRENNO(0);
		tLLMailListSchema.setGLCERT("1");// ��С��,��1��ʼ
		tLLMailListSchema.setGLCERTPFIX("0");
		tLLMailListSchema.setGLCERTSEQ("1");
		tLLMailListSchema.setGlemail(tMailAddress); // �ʼ���ַ
		tLLMailListSchema.setGlphone(""); // �绰
		tLLMailListSchema.setGlmessage(inputStream); // �ʼ��ı�
		tLLMailListSchema.setGlstatus("0");
		tLLMailListSchema.setGltype(0);
		tLLMailListSchema.setGLCSEQNO("1");
		tLLMailListSchema.setGLCLAIM( currentDate );// ��������
		tLLMailListSchema.setGlmessageDate( currentDate + " " + currentTime );
		tLLMailListSchema.setGlsubject(tMailTitle);
		tLLMailListSchema.setGlsurname(tUser);
		tLLMailListSchema.setGlsrvbrch(tGlsrvbrch);
		tLLMailListSchema.setGlrecvdate(tGlrecvdate);
		tLLMailListSchema.setGltotclamt("");
		tLLMailListSchema.setGltotpyamt("");
		tLLMailListSchema.setRECORDERNUM(tSerialNo);
		tLLMailListSchema.setMakeDate( currentDate );
		tLLMailListSchema.setMakeTime( currentTime );
		tLLMailListSchema.setOperator(tUser);
		return tLLMailListSchema;
	}
	
	public boolean addToMailList(LLMailListSchema tLLMailListSchema){
		VData mResult = new VData();
		mResult.clear();
		MMap map = new MMap();
		map.put(tLLMailListSchema,"BLOBINSERT");
        mResult.add(map);
        PubSubmit tPubSubmit = new PubSubmit();
        try
        {
        	if (tPubSubmit.submitData(mResult, "")) {
//        		System.out.println("������߰��ʼ����ɳɹ����������ʱ��"+currentDate+PubFun.getCurrentTime());
//        		this.mSystemOutList.add("������߰��ʼ����ɳɹ����������ʱ��"+currentDate+PubFun.getCurrentTime());
//        	 	logTxt+=currentDate+PubFun.getCurrentTime()+",������߰��ʼ����ɳɹ�!"+ "\r\n"; 
        	}
        	else
        	{
//        		System.out.println("������߰��ʼ��ύʧ�ܣ��������ʱ��"+currentDate+PubFun.getCurrentTime());
//        		this.mErrorInfoList.add("������߰��ʼ��ύʧ�ܣ��������ʱ��"+currentDate+PubFun.getCurrentTime());
//        		logTxt+=currentDate+PubFun.getCurrentTime()+",������߰��ʼ��ύʧ��!"+ "\r\n"; 
        	}
        }
        catch(Exception e){
//        	System.out.println("������߰��ʼ��ύʧ�ܣ��������ʱ��"+currentDate+PubFun.getCurrentTime());
//        	this.mErrorInfoList.add("������߰��ʼ��ύʧ�ܣ��������ʱ��"+currentDate+PubFun.getCurrentTime());
//    		logTxt+=currentDate+PubFun.getCurrentTime()+",������߰��ʼ��ύʧ��!"+ "\r\n"; 
        	e.printStackTrace();
        }
		
		return true;
	}
	
	public void noticeEmailServer(){
	}
	
	

}
