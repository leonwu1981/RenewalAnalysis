


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.dom4j.Attribute;

import jxl.Sheet;
import jxl.Workbook;

import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.ExeSQL;

public class XlsToXml {
	
	//����������Ϣ��Ҫ���ݸ�����������޸�
	private String filePath = "C:\\Documents and Settings\\Administrator\\����\\����ģ��.xls";    //����ģ��·�� 
	private String xmlPath = "D:\\�ύ\\";                 //����xml�ļ����Ŀ¼

	private String SSDIR2="\\\\10.0.60.87\\sinosoft$\\vss_dev2";    //	2��vssĿ¼
	private String SSDIR3="\\\\10.0.60.87\\sinosoft$\\vss_dev3";    //	3��vssĿ¼
	
	private String path="D:\\Program Files\\VSS\\template\\win32";      //������vss��װĿ¼
	
	private String username2 ="##";         //2�ڵ�¼vss�û���
	private String password2 ="##";         //2�ڵ�¼vss����
	
	private String username3 ="##";
	private String password3 ="##";
	
	
	//����ʽ����ÿ�ո����ļ�,��Ҫ����jxl.jar��
	public void everyUpFile()
	{  
		Pattern myPattern=Pattern.compile("[A-Z]");
		Matcher myMatcher;
		try
        {
	        InputStream is = new FileInputStream(filePath);
	        jxl.Workbook rwb = Workbook.getWorkbook(is);
	        Sheet rs = rwb.getSheet(0);  	        	        
	         
            System.out.println("Excel���:"+rs.getRows()+"��");  
            
            String dateString = PubFun.getCurrentDate();  //�ļ�����
            
            int upfile2=0;  //2�ڸ����ļ�����
            int upfile3=0;  //3�ڸ����ļ�����
            String errStr="";
            
            PrintWriter printwriter2 = new PrintWriter(new FileOutputStream(xmlPath+"2��_"+dateString+".xml"));   //�����ļ�
            PrintWriter printwriter3 = new PrintWriter(new FileOutputStream(xmlPath+"3��_"+dateString+".xml"));   //�����ļ�
            
            if(rs.getRows()>=2)
            {
            	//2���ļ�ͷ
            	String insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter2.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter2.println(insertStr);
            	//3���ļ�ͷ
            	insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter3.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter3.println(insertStr);
            	
            	for(int i=1;i<rs.getRows();i++)  //�ӵڶ����𣬵�һ���Ǳ���
            	{
            		if(!"2".equals(rs.getCell(0, i).getContents())&&!"3".equals(rs.getCell(0, i).getContents())) //2,3�ڱ�־
            		{
            			break;
            		}
            		else if("2".equals(rs.getCell(0, i).getContents()))  //2�ڸ����ļ�����
            		{
            		
            		    // У����Ϣ
						String fileName = ""; // �����ļ�����
						String fileCheckOut = ""; // check out �汾
						String fileCheckIn = ""; // check in �汾
						fileName = rs.getCell(1, i).getContents().trim();
						fileCheckOut = rs.getCell(2, i).getContents().trim();
						fileCheckIn = rs.getCell(3, i).getContents().trim();
						if (fileName != null && !fileName.equals("")) {
							// У������ļ�ͷ
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ�·������"
										+ "\n";
								break;
							}
							// У������ļ�β
							if (fileName.endsWith("jsp")) {
							} else if (fileName.endsWith("js")) {
							} else if (fileName.endsWith("java")) {
							} else if (fileName.endsWith("xls")) {
							} else if (fileName.endsWith("xml")) {
							} else if (fileName.endsWith("txt")) {
							} else if (fileName.endsWith("vts")) {
							} else if (fileName.endsWith("htm")) {
							} else if (fileName.endsWith("xsl")) {
							} else if (fileName.endsWith("gif")) {	
							} else if (fileName.endsWith("doc")) {
							} else if (fileName.endsWith("css")) {
							} else if (fileName.endsWith("jasper")) {
							} else if (fileName.endsWith("jrxml")) {
							} else if (fileName.endsWith("jar")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ���������"
										+ "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1) + "�и����ļ���Ϊ�գ�����!"
									+ "\n";
							break;
						}
						//check in �汾>check out�汾
						if (fileCheckOut != null && !fileCheckOut.equals("")
								&& fileCheckIn != null
								&& !fileCheckIn.equals("")) {
							if (Integer.parseInt(fileCheckOut) > Integer
									.parseInt(fileCheckIn)) {
								errStr += "����ģ���е�" + (i + 1)
										+ "��check out�汾����check in�汾��" + "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1)
									+ "��check in��check out�汾Ϊ�գ�" + "\n";
							break;
						}
						
						// �����ļ�
						insertStr = "<FileName FileType='2' version='"
								+ fileCheckIn + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("2�ڣ�"+insertStr);
						
                         //�ļ�·���д�д��ĸУ��
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     ����ģ���е�" + (i + 1) + "�и����ļ�·���к��д�д��ĸ����ȷ����ȷ��!");
						}
						
						//check in�汾<vss�����汾
						if(getMaxVersion("2",fileName,fileCheckIn))
						{
							errStr += "����ģ���е�" + (i + 1)
							+ "�д������⣬���ȼ�鲢�޸ģ�" + "\n";
							break;
						}
						
						printwriter2.println(insertStr);
						upfile2++;
            		}
            		else if("3".equals(rs.getCell(0, i).getContents()))  //3�ڸ����ļ�����
            		{
            		
            		    // У����Ϣ
						String fileName = ""; // �����ļ�����
						String fileCheckOut = ""; // check out �汾
						String fileCheckIn = ""; // check in �汾
						fileName = rs.getCell(1, i).getContents().trim();
						fileCheckOut = rs.getCell(2, i).getContents().trim();
						fileCheckIn = rs.getCell(3, i).getContents().trim();
						if (fileName != null && !fileName.equals("")) {
							// У������ļ�ͷ
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ�·������"
										+ "\n";
								break;
							}
							// У������ļ�β
							if (fileName.endsWith("jsp")) {
							} else if (fileName.endsWith("js")) {
							} else if (fileName.endsWith("java")) {
							} else if (fileName.endsWith("xls")) {
							} else if (fileName.endsWith("xml")) {
							} else if (fileName.endsWith("txt")) {
							} else if (fileName.endsWith("vts")) {
							} else if (fileName.endsWith("htm")) {
							} else if (fileName.endsWith("xsl")) {
							} else if (fileName.endsWith("gif")) {	
							} else if (fileName.endsWith("doc")) {
							} else if (fileName.endsWith("css")) {
							} else if (fileName.endsWith("jasper")) {
							} else if (fileName.endsWith("jrxml")) {
							} else if (fileName.endsWith("jar")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ���������"
										+ "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1) + "�и����ļ���Ϊ�գ�����!"
									+ "\n";
							break;
						}
						//check in �汾>check out�汾
						if (fileCheckOut != null && !fileCheckOut.equals("")
								&& fileCheckIn != null
								&& !fileCheckIn.equals("")) {
							if (Integer.parseInt(fileCheckOut) > Integer
									.parseInt(fileCheckIn)) {
								errStr += "����ģ���е�" + (i + 1)
										+ "��check out�汾����check in�汾��" + "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1)
									+ "��check in��check out�汾Ϊ�գ�" + "\n";
							break;
						}

						// �����ļ�
						insertStr = "<FileName FileType='2' version='"
								+ fileCheckIn + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("3�ڣ�"+insertStr);
											
                        //�ļ�·���д�д��ĸУ��
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     ����ģ���е�" + (i + 1) + "�и����ļ�·���к��д�д��ĸ����ȷ����ȷ��!");
						}
						
						//check in�汾<vss�����汾
						if(getMaxVersion("3",fileName,fileCheckIn))
						{
							errStr += "����ģ���е�" + (i + 1)
							+ "�д������⣬���ȼ�鲢�޸ģ�" + "\n";
							break;
						}
						
						printwriter3.println(insertStr);
						upfile3++;
            		}
            	}
            	insertStr = "</SUTrace>";
            	printwriter2.println(insertStr);
            	printwriter2.close();
            	
            	printwriter3.println(insertStr);
            	printwriter3.close();
            	
            	if(errStr.equals(""))
            	{
            		System.out.println("\n  ^_^�ļ�ȫ�����ɳɹ���\n");
            	}
            	else
            	{
            		System.out.println(errStr);
            	}
            	System.out.println("2���ܹ������ļ���"+upfile2+"��!");
            	System.out.println("3���ܹ������ļ���"+upfile3+"��!");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        } 
	}
	//String qici 2,3�ڱ�־��String fileName �ļ�·�������ƣ�String fileCheckIn check in�汾��
	public boolean getMaxVersion(String qici,String fileName,String fileCheckIn ) throws IOException
	{
		
		int maxVersion=0;
		int nowVersion = Integer.parseInt(fileCheckIn);
		boolean ErrorFlag=false;
		String version,cmd;
		Runtime rt = Runtime.getRuntime();
		Process pro;
	    InputStream is,is2;
	    InputStreamReader isr,isr2;
	    //vssĿ¼,�û���������
		String SSDIR="";
		String username="";
		String password="";
		
		if(qici.equals("2"))
		{
			SSDIR=this.SSDIR2;
			username = this.username2;
			password = this.password2;
		}
		else if(qici.equals("3"))
		{
			SSDIR=this.SSDIR3;
			username = this.username3;
			password = this.password3;
		}
		
		//������vss��װĿ¼
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/�ļ������vss��·�������� -I- -Y��¼vss�û���,����
		cmd ="cmd.exe /c @ss history -v -b $/"+fileName+" -I- -Y"+username+","+password;
		
		pro = rt.exec(cmd,env);
	    is2 = pro.getInputStream();
	    isr2 = new InputStreamReader(is2,Charset.forName("GB2312"));
	    is = pro.getErrorStream();
	    isr = new InputStreamReader(is,Charset.forName("GB2312"));
	    boolean hasToWait = true;
	    boolean hasTimeOut = false;
	    int waitTime = 0;
	    do{
	    	try{
			    Thread.sleep(20);
			    waitTime += 20;
		    }catch(InterruptedException intrupe){
			    intrupe.printStackTrace();
		    }
		    if(isr2.ready())
		    {
			    int ch2 = isr2.read();
			    String stdout = "";
			    while(ch2 != -1){
			    	stdout += (char)ch2;
				    ch2 = isr2.read();
			    }
			    String strMaxVersion = stdout.substring(stdout.indexOf("Version"),stdout.indexOf("Version")+13);
//			    System.out.print("�ļ�������Ϣ"+strMaxVersion);
//			    System.out.println(stdout);
//			    System.out.print("\t��ȡ���汾�ŵȴ�ʱ��: "+waitTime+"���롣\r\n");
			    maxVersion = Integer.parseInt(strMaxVersion.substring(7).trim());
			   
			    int difVersion = maxVersion - nowVersion;
			    if(nowVersion==0){
			    	System.out.println("    check in �汾Ϊ0���汾��д����!");
			    	ErrorFlag = true;
			    }else if(maxVersion<nowVersion){
			    	System.out.println("    ���ļ����汾�ţ�"+maxVersion+"����дcheck in�汾��Ϊ��"+nowVersion+"�� ��������д�İ汾�ţ�");
			    	ErrorFlag = true;
			    }else if(difVersion>0){
			    	System.out.println("    ���ļ����汾�ţ�"+maxVersion+"����дcheck in�汾��Ϊ��"+nowVersion+"�� ���߲���ȣ���ȷ�ϣ�");
			    }
			    hasToWait = false;
		    }else if(waitTime>=8000){
		    	hasToWait = false;
		    	hasTimeOut = true;
		    	System.out.println("   ��ȡ���汾�ų�ʱ�������ļ�:"+fileName+"��vss���Ƿ���ڣ��ļ�·���Ƿ���ȷ������");
		    	ErrorFlag = true;
		    }
	    }while( hasToWait && !hasTimeOut );
        //������Ϣ��ӡ
        if (isr.ready()) {
			int ch2 = isr.read();
			String stdout = "";
			while (ch2 != -1) {
				stdout += (char) ch2;
				ch2 = isr.read();
			}
			System.out.println(stdout);
		}
        return ErrorFlag;
	  
	}
	
//	����ʽ����ÿ�ո����ļ�,��Ҫ����jxl.jar��,ȡvss���ļ����汾�����ɸ����ļ�
	public void everyUpFileByMaxVerson()
	{  
		Pattern myPattern=Pattern.compile("[A-Z]");
		Matcher myMatcher;
		try
        {
	        InputStream is = new FileInputStream(filePath);
	        jxl.Workbook rwb = Workbook.getWorkbook(is);
	        Sheet rs = rwb.getSheet(0);  	        	        
	         
            System.out.println("Excel���:"+rs.getRows()+"��");  
            
            String dateString = PubFun.getCurrentDate();  //�ļ�����
            
            int upfile2=0;  //2�ڸ����ļ�����
            int upfile3=0;  //3�ڸ����ļ�����
            String errStr="";
            
            PrintWriter printwriter2 = new PrintWriter(new FileOutputStream(xmlPath+"2��_"+dateString+".xml"));   //�����ļ�
            PrintWriter printwriter3 = new PrintWriter(new FileOutputStream(xmlPath+"3��_"+dateString+".xml"));   //�����ļ�
            
            if(rs.getRows()>=2)
            {
            	//2���ļ�ͷ
            	String insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter2.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter2.println(insertStr);
            	//3���ļ�ͷ
            	insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter3.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter3.println(insertStr);
            	
            	for(int i=1;i<rs.getRows();i++)  //�ӵڶ����𣬵�һ���Ǳ���
            	{
            		if(!"2".equals(rs.getCell(0, i).getContents().trim())&&!"3".equals(rs.getCell(0, i).getContents().trim())) //2,3�ڱ�־
            		{
            			break;
            		}
            		else if("2".equals(rs.getCell(0, i).getContents().trim()))  //2�ڸ����ļ�����
            		{
            		
            		    // У����Ϣ
						String fileName = ""; // �����ļ�����
						fileName = rs.getCell(1, i).getContents().trim();
						
						//�ж��Ƿ�����ظ����ļ�,ȥ���ظ��ļ�
						boolean CFFlag=false;
						for(int j=1;j<rs.getRows()&&j<i;j++)
						{
							if("2".equals(rs.getCell(0, j).getContents().trim()))
							{
								String BfileName = rs.getCell(1, j).getContents().trim();
								if(fileName.equals(BfileName))
								{
									System.out.println();
									System.out.println("��"+(i+1)+"�����"+(j+1)+"�д����ظ��ļ�:"+BfileName);
									CFFlag = true;
									//���ظ��汾��д�˸���ģ��
									readWriteXls(i,3,"",false);  //���
									readWriteXls(i,4,("��Excel�е�"+(j+1)+"���ظ�"),true);
									break;
								}
							}
						}
						if(CFFlag)
						{
							continue;
						}
						
						if (fileName != null && !fileName.equals("")) {
							// У������ļ�ͷ
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ�·������"
										+ "\n";
								break;
							}
							// У������ļ�β
							if (fileName.endsWith("jsp")) {
							} else if (fileName.endsWith("js")) {
							} else if (fileName.endsWith("java")) {
							} else if (fileName.endsWith("xls")) {
							} else if (fileName.endsWith("xml")) {
							} else if (fileName.endsWith("txt")) {
							} else if (fileName.endsWith("vts")) {
							} else if (fileName.endsWith("htm")) {
							} else if (fileName.endsWith("xsl")) {
							} else if (fileName.endsWith("gif")) {	
							} else if (fileName.endsWith("doc")) {
							} else if (fileName.endsWith("css")) {
							} else if (fileName.endsWith("jasper")) {
							} else if (fileName.endsWith("jrxml")) {
							} else if (fileName.endsWith("jar")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ���������"
										+ "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1) + "�и����ļ���Ϊ�գ�����!"
									+ "\n";
							break;
						}
						
						//ȡ�ļ����汾
						int maxVerson = getMaxVersionNum("2",fileName);
						// �����ļ�
						insertStr = "<FileName FileType='2' version='"
								+ maxVerson + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("2�ڣ�"+insertStr);
						
                         //�ļ�·���д�д��ĸУ��
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     ����ģ���е�" + (i + 1) + "�и����ļ�·���к��д�д��ĸ����ȷ����ȷ��!");
						}
						
						printwriter2.println(insertStr);
						upfile2++;
						//���汾��д�˸���ģ��
						if(maxVerson==0)
						{
							//������汾д�˸���ģ��
							readWriteXls(i,3,"",false);  //���
							readWriteXls(i,4,("����汾"),true);
						}
						else
						{
							readWriteXls(i,2,String.valueOf(maxVerson-1),false);
							readWriteXls(i,3,String.valueOf(maxVerson),false);
							readWriteXls(i,4,"",false);  //���
						}
            		}
            		else if("3".equals(rs.getCell(0, i).getContents().trim()))  //3�ڸ����ļ�����
            		{
            		
            		    // У����Ϣ
						String fileName = ""; // �����ļ�����
						fileName = rs.getCell(1, i).getContents().trim();
						
						//�ж��Ƿ�����ظ����ļ�,ȥ���ظ��ļ�
						boolean CFFlag=false;
						for(int j=1;j<rs.getRows()&&j<i;j++)
						{
							if("3".equals(rs.getCell(0, j).getContents().trim()))
							{
								String BfileName = rs.getCell(1, j).getContents().trim();
								if(fileName.equals(BfileName))
								{
									System.out.println();
									System.out.println("��"+(i+1)+"�����"+(j+1)+"�д����ظ��ļ�:"+BfileName);
									CFFlag = true;
									//���ظ��汾��д�˸���ģ��
									readWriteXls(i,3,"",false);  //���
									readWriteXls(i,4,("��Excel�е�"+(j+1)+"���ظ�"),true);
									break;
								}
							}
						}
						if(CFFlag)
						{
							continue;
						}
						
						if (fileName != null && !fileName.equals("")) {
							// У������ļ�ͷ
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ�·������"
										+ "\n";
								break;
							}
							// У������ļ�β
							if (fileName.endsWith("jsp")) {
							} else if (fileName.endsWith("js")) {
							} else if (fileName.endsWith("java")) {
							} else if (fileName.endsWith("xls")) {
							} else if (fileName.endsWith("xml")) {
							} else if (fileName.endsWith("txt")) {
							} else if (fileName.endsWith("vts")) {
							} else if (fileName.endsWith("htm")) {
							} else if (fileName.endsWith("xsl")) {
							} else if (fileName.endsWith("gif")) {	
							} else if (fileName.endsWith("doc")) {
							} else if (fileName.endsWith("css")) {
							} else if (fileName.endsWith("jasper")) {
							} else if (fileName.endsWith("jrxml")) {
							} else if (fileName.endsWith("jar")) {
							} else {
								errStr += "����ģ���е�" + (i + 1) + "�и����ļ���������"
										+ "\n";
								break;
							}
						} else {
							errStr += "����ģ���е�" + (i + 1) + "�и����ļ���Ϊ�գ�����!"
									+ "\n";
							break;
						}

						//ȡ�ļ����汾
						int maxVerson = getMaxVersionNum("3",fileName);

						// �����ļ�
						insertStr = "<FileName FileType='2' version='"
								+ maxVerson + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("3�ڣ�"+insertStr);
											
                        //�ļ�·���д�д��ĸУ��
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     ����ģ���е�" + (i + 1) + "�и����ļ�·���к��д�д��ĸ����ȷ����ȷ��!");
						}
						
						printwriter3.println(insertStr);
						upfile3++;
						
						//���汾��д�˸���ģ��
						if(maxVerson==0)
						{
							//������汾д�˸���ģ��
							readWriteXls(i,3,"",false);  //���
							readWriteXls(i,4,("����汾"),true);
						}
						else
						{
							readWriteXls(i,2,String.valueOf(maxVerson-1),false);
							readWriteXls(i,3,String.valueOf(maxVerson),false);
							readWriteXls(i,4,"",false);  //���
						}
            		}
            	}
            	insertStr = "</SUTrace>";
            	printwriter2.println(insertStr);
            	printwriter2.close();
            	
            	printwriter3.println(insertStr);
            	printwriter3.close();
            	
            	if(errStr.equals(""))
            	{
            		System.out.println("\n  ^_^�ļ�ȫ�����ɳɹ���\n");
            	}
            	else
            	{
            		System.out.println(errStr);
            	}
            	System.out.println("2���ܹ������ļ���"+upfile2+"��!");
            	System.out.println("3���ܹ������ļ���"+upfile3+"��!");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        } 
	}
    //	��ȡ���汾������ӡ�汾��Ϣ
	public int getMaxVersionNum1(String qici,String fileName ) throws IOException
	{
		
		int maxVersion=0;
		String cmd;
		Runtime rt = Runtime.getRuntime();
		Process pro;
	    InputStream is,is2;
	    InputStreamReader isr,isr2;
	    //vssĿ¼,�û���������
		String SSDIR="";
		String username="";
		String password="";
		
		if(qici.equals("2"))
		{
			SSDIR=this.SSDIR2;
			username = this.username2;
			password = this.password2;
		}
		else if(qici.equals("3"))
		{
			SSDIR=this.SSDIR3;
			username = this.username3;
			password = this.password3;
		}
		
		//������vss��װĿ¼
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/�ļ������vss��·�������� -I- -Y��¼vss�û���,����
		cmd ="cmd.exe /c @ss history -v -b $/"+fileName+" -I- -Y"+username+","+password;
		
		pro = rt.exec(cmd,env);
	    is2 = pro.getInputStream();
	    isr2 = new InputStreamReader(is2,Charset.forName("GB2312"));
	    is = pro.getErrorStream();
	    isr = new InputStreamReader(is,Charset.forName("GB2312"));
	    boolean hasToWait = true;
	    boolean hasTimeOut = false;
	    int waitTime = 0;
	    do{
	    	try{
			    Thread.sleep(20);
			    waitTime += 20;
		    }catch(InterruptedException intrupe){
			    intrupe.printStackTrace();
		    }
		    if(isr2.ready())
		    {
			    int ch2 = isr2.read();
			    String stdout = "";
			    while(ch2 != -1){
			    	stdout += (char)ch2;
				    ch2 = isr2.read();
			    }
			    String strMaxVersion = stdout.substring(stdout.indexOf("Version"),stdout.indexOf("Version")+13);
			    maxVersion = Integer.parseInt(strMaxVersion.substring(7).trim());
			    hasToWait = false;
		    }else if(waitTime>=8000){
		    	hasToWait = false;
		    	hasTimeOut = true;
		    	System.out.println("   ��ȡ���汾�ų�ʱ�������ļ�:"+fileName+"��vss���Ƿ���ڣ��ļ�·���Ƿ���ȷ������");
		    }
	    }while( hasToWait && !hasTimeOut );
        //������Ϣ��ӡ
        if (isr.ready()) {
			int ch2 = isr.read();
			String stdout = "";
			while (ch2 != -1) {
				stdout += (char) ch2;
				ch2 = isr.read();
			}
			System.out.println(stdout);
		}
        return maxVersion;
	  
	}
	//��ȡ���汾���Ҵ�ӡ�汾��Ϣ
	public int getMaxVersionNum(String qici,String fileName ) throws IOException
	{
		
		int maxVersion=0;
		String cmd;
		Runtime rt = Runtime.getRuntime();
		Process pro;
	    InputStream is,is2;
	    InputStreamReader isr,isr2;
	    //vssĿ¼,�û���������
		String SSDIR="";
		String username="";
		String password="";
		
		if(qici.equals("2"))
		{
			SSDIR=this.SSDIR2;
			username = this.username2;
			password = this.password2;
		}
		else if(qici.equals("3"))
		{
			SSDIR=this.SSDIR3;
			username = this.username3;
			password = this.password3;
		}
		
		//������vss��װĿ¼
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/�ļ������vss��·�������� -I- -Y��¼vss�û���,����
		cmd ="cmd.exe /c @ss history -v -b $/"+fileName+" -I- -Y"+username+","+password;
		
		pro = rt.exec(cmd,env);
	    is2 = pro.getInputStream();
	    isr2 = new InputStreamReader(is2,Charset.forName("GB2312"));
	    is = pro.getErrorStream();
	    isr = new InputStreamReader(is,Charset.forName("GB2312"));
	    boolean hasToWait = true;
	    boolean hasTimeOut = false;
	    int waitTime = 0;
	    do{
	    	try{
			    Thread.sleep(20);
			    waitTime += 20;
		    }catch(InterruptedException intrupe){
			    intrupe.printStackTrace();
		    }
		    if(isr2.ready())
		    {
			    int ch2 = isr2.read();
			    String stdout = "";
			    while(ch2 != -1){
			    	stdout += (char)ch2;
				    ch2 = isr2.read();
			    }
			  //  System.out.println("stdout"+stdout);
			    stdout = stdout.substring(stdout.indexOf("Version"));
			    if(stdout.indexOf("$")>0)
			    {
			    	stdout = stdout.substring(0,stdout.indexOf("$"));
			    }
			    else
			    {
			    	stdout = stdout.substring(0,stdout.indexOf("Created")+8);
			    }
			    String strMaxVersion = stdout.substring(7,13);   //���汾��
			    maxVersion = Integer.parseInt(strMaxVersion.trim());
			    System.out.println();
			    System.out.println("   ����һ���ļ��汾��Ϣ��"+stdout);
			    hasToWait = false;
		    }else if(waitTime>=8000){
		    	hasToWait = false;
		    	hasTimeOut = true;
		    	System.out.println("   ��ȡ���汾�ų�ʱ�������ļ�:"+fileName+"��vss���Ƿ���ڣ��ļ�·���Ƿ���ȷ������");
		    }
	    }while( hasToWait && !hasTimeOut );
        //������Ϣ��ӡ
        if (isr.ready()) {
			int ch2 = isr.read();
			String stdout = "";
			while (ch2 != -1) {
				stdout += (char) ch2;
				ch2 = isr.read();
			}
			System.out.println(stdout);
		}
        return maxVersion;
	  
	}
//	��дһ��Excel��������Ӧ���������뵽ָ���ĵ�Ԫ��
	public  void readWriteXls(int rowNum,int cellNum,String version,boolean redFlag) { 
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));
			int sheetCount = workbook.getNumberOfSheets();// excel���ű� 
			HSSFSheet sheet = workbook.getSheetAt(0);// ��excel�ĵ�һ�������� 
			int rowCount = sheet.getLastRowNum();// ȡ�����һ�е��±� 
			if(rowNum<=rowCount)
			{
				HSSFRow row = sheet.getRow(rowNum);// ������ 
				if (row != null) 
				{ 
					HSSFCell curCell= row.createCell((short)cellNum); 
					if(redFlag)
					{
						curCell.setCellStyle(getStyle2(workbook));
						sheet.setColumnWidth((short)cellNum, (short)(18 * 256));  //�����п�
					}
					curCell.setEncoding(HSSFCell.ENCODING_UTF_16); 
					curCell.setCellValue(version); 

				}
			}
			 FileOutputStream fileOut = new FileOutputStream(filePath);
			 workbook.write(fileOut);
			 fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		} 
		
	}
	public static HSSFCellStyle getStyle2(HSSFWorkbook wb) { 
		  HSSFFont font2 = wb.createFont(); // ���������ʽ 
		  font2.setColor(HSSFFont.COLOR_RED); // ���õ�Ԫ���������ɫ. 
		  font2.setFontHeight((short) 220); // ���������С 
		  font2.setFontName("����"); // ���õ�Ԫ������
		  
		  HSSFCellStyle style3 = wb.createCellStyle(); // ������Ԫ����. 
		  style3.setAlignment(HSSFCellStyle.VERTICAL_CENTER); // ��ֱ���� 
		  style3.setAlignment(HSSFCellStyle.ALIGN_CENTER); // /ˮƽ���� 
		  
//		  style3.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);  //���ñ���ɫ
//		  style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		  style3.setFont(font2); // �������ʽ���뵽��Ԫ������ 
		  return style3; 
	} 
	
	public static void main(String[] args)
	{
		XlsToXml xx = new XlsToXml();
        //���ɷ�ʽһ		ȡ��д���ļ��汾���ɸ����ļ�  check in,out�汾�ű���,��ȥ���ظ��ļ�
	//	xx.everyUpFile();   
        //���ɷ�ʽ��		ȡvss���ļ����汾�����ɸ����ļ�,check in,out�汾�ſɲ���Զ�ȥ���ظ��ļ�
		xx.everyUpFileByMaxVerson();  
		
	}
	
	
}
