


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
	
	//以下配置信息需要根据个人情况进行修改
	private String filePath = "C:\\Documents and Settings\\Administrator\\桌面\\更新模版.xls";    //更新模板路径 
	private String xmlPath = "D:\\提交\\";                 //生成xml文件存放目录

	private String SSDIR2="\\\\10.0.60.87\\sinosoft$\\vss_dev2";    //	2期vss目录
	private String SSDIR3="\\\\10.0.60.87\\sinosoft$\\vss_dev3";    //	3期vss目录
	
	private String path="D:\\Program Files\\VSS\\template\\win32";      //本机器vss安装目录
	
	private String username2 ="##";         //2期登录vss用户名
	private String password2 ="##";         //2期登录vss密码
	
	private String username3 ="##";
	private String password3 ="##";
	
	
	//按格式生成每日更新文件,需要倒入jxl.jar包
	public void everyUpFile()
	{  
		Pattern myPattern=Pattern.compile("[A-Z]");
		Matcher myMatcher;
		try
        {
	        InputStream is = new FileInputStream(filePath);
	        jxl.Workbook rwb = Workbook.getWorkbook(is);
	        Sheet rs = rwb.getSheet(0);  	        	        
	         
            System.out.println("Excel表格:"+rs.getRows()+"行");  
            
            String dateString = PubFun.getCurrentDate();  //文件名称
            
            int upfile2=0;  //2期更新文件个数
            int upfile3=0;  //3期更新文件个数
            String errStr="";
            
            PrintWriter printwriter2 = new PrintWriter(new FileOutputStream(xmlPath+"2期_"+dateString+".xml"));   //生成文件
            PrintWriter printwriter3 = new PrintWriter(new FileOutputStream(xmlPath+"3期_"+dateString+".xml"));   //生成文件
            
            if(rs.getRows()>=2)
            {
            	//2期文件头
            	String insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter2.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter2.println(insertStr);
            	//3期文件头
            	insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter3.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter3.println(insertStr);
            	
            	for(int i=1;i<rs.getRows();i++)  //从第二行起，第一行是标题
            	{
            		if(!"2".equals(rs.getCell(0, i).getContents())&&!"3".equals(rs.getCell(0, i).getContents())) //2,3期标志
            		{
            			break;
            		}
            		else if("2".equals(rs.getCell(0, i).getContents()))  //2期更新文件处理
            		{
            		
            		    // 校验信息
						String fileName = ""; // 更新文件名称
						String fileCheckOut = ""; // check out 版本
						String fileCheckIn = ""; // check in 版本
						fileName = rs.getCell(1, i).getContents().trim();
						fileCheckOut = rs.getCell(2, i).getContents().trim();
						fileCheckIn = rs.getCell(3, i).getContents().trim();
						if (fileName != null && !fileName.equals("")) {
							// 校验更新文件头
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "更新模板中第" + (i + 1) + "行更新文件路径有误！"
										+ "\n";
								break;
							}
							// 校验更新文件尾
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
								errStr += "更新模板中第" + (i + 1) + "行更新文件类型有误！"
										+ "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1) + "行更新文件名为空！请检查!"
									+ "\n";
							break;
						}
						//check in 版本>check out版本
						if (fileCheckOut != null && !fileCheckOut.equals("")
								&& fileCheckIn != null
								&& !fileCheckIn.equals("")) {
							if (Integer.parseInt(fileCheckOut) > Integer
									.parseInt(fileCheckIn)) {
								errStr += "更新模板中第" + (i + 1)
										+ "行check out版本大于check in版本！" + "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1)
									+ "行check in或check out版本为空！" + "\n";
							break;
						}
						
						// 生成文件
						insertStr = "<FileName FileType='2' version='"
								+ fileCheckIn + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("2期："+insertStr);
						
                         //文件路径中大写字母校验
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     更新模板中第" + (i + 1) + "行更新文件路径中含有大写字母，请确认正确性!");
						}
						
						//check in版本<vss上最大版本
						if(getMaxVersion("2",fileName,fileCheckIn))
						{
							errStr += "更新模板中第" + (i + 1)
							+ "行存在问题，请先检查并修改！" + "\n";
							break;
						}
						
						printwriter2.println(insertStr);
						upfile2++;
            		}
            		else if("3".equals(rs.getCell(0, i).getContents()))  //3期更新文件处理
            		{
            		
            		    // 校验信息
						String fileName = ""; // 更新文件名称
						String fileCheckOut = ""; // check out 版本
						String fileCheckIn = ""; // check in 版本
						fileName = rs.getCell(1, i).getContents().trim();
						fileCheckOut = rs.getCell(2, i).getContents().trim();
						fileCheckIn = rs.getCell(3, i).getContents().trim();
						if (fileName != null && !fileName.equals("")) {
							// 校验更新文件头
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "更新模板中第" + (i + 1) + "行更新文件路径有误！"
										+ "\n";
								break;
							}
							// 校验更新文件尾
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
								errStr += "更新模板中第" + (i + 1) + "行更新文件类型有误！"
										+ "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1) + "行更新文件名为空！请检查!"
									+ "\n";
							break;
						}
						//check in 版本>check out版本
						if (fileCheckOut != null && !fileCheckOut.equals("")
								&& fileCheckIn != null
								&& !fileCheckIn.equals("")) {
							if (Integer.parseInt(fileCheckOut) > Integer
									.parseInt(fileCheckIn)) {
								errStr += "更新模板中第" + (i + 1)
										+ "行check out版本大于check in版本！" + "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1)
									+ "行check in或check out版本为空！" + "\n";
							break;
						}

						// 生成文件
						insertStr = "<FileName FileType='2' version='"
								+ fileCheckIn + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("3期："+insertStr);
											
                        //文件路径中大写字母校验
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     更新模板中第" + (i + 1) + "行更新文件路径中含有大写字母，请确认正确性!");
						}
						
						//check in版本<vss上最大版本
						if(getMaxVersion("3",fileName,fileCheckIn))
						{
							errStr += "更新模板中第" + (i + 1)
							+ "行存在问题，请先检查并修改！" + "\n";
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
            		System.out.println("\n  ^_^文件全部生成成功！\n");
            	}
            	else
            	{
            		System.out.println(errStr);
            	}
            	System.out.println("2期总共更新文件："+upfile2+"个!");
            	System.out.println("3期总共更新文件："+upfile3+"个!");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        } 
	}
	//String qici 2,3期标志，String fileName 文件路径及名称，String fileCheckIn check in版本号
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
	    //vss目录,用户名，密码
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
		
		//本机器vss安装目录
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/文件相对于vss的路径及名称 -I- -Y登录vss用户名,密码
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
//			    System.out.print("文件所有信息"+strMaxVersion);
//			    System.out.println(stdout);
//			    System.out.print("\t获取最大版本号等待时间: "+waitTime+"毫秒。\r\n");
			    maxVersion = Integer.parseInt(strMaxVersion.substring(7).trim());
			   
			    int difVersion = maxVersion - nowVersion;
			    if(nowVersion==0){
			    	System.out.println("    check in 版本为0！版本填写错误!");
			    	ErrorFlag = true;
			    }else if(maxVersion<nowVersion){
			    	System.out.println("    该文件最大版本号："+maxVersion+"，填写check in版本号为："+nowVersion+"。 不存在填写的版本号！");
			    	ErrorFlag = true;
			    }else if(difVersion>0){
			    	System.out.println("    该文件最大版本号："+maxVersion+"，填写check in版本号为："+nowVersion+"。 二者不相等，请确认！");
			    }
			    hasToWait = false;
		    }else if(waitTime>=8000){
		    	hasToWait = false;
		    	hasTimeOut = true;
		    	System.out.println("   获取最大版本号超时，请检查文件:"+fileName+"在vss上是否存在，文件路径是否正确！！！");
		    	ErrorFlag = true;
		    }
	    }while( hasToWait && !hasTimeOut );
        //错误信息打印
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
	
//	按格式生成每日更新文件,需要倒入jxl.jar包,取vss上文件最大版本号生成更新文件
	public void everyUpFileByMaxVerson()
	{  
		Pattern myPattern=Pattern.compile("[A-Z]");
		Matcher myMatcher;
		try
        {
	        InputStream is = new FileInputStream(filePath);
	        jxl.Workbook rwb = Workbook.getWorkbook(is);
	        Sheet rs = rwb.getSheet(0);  	        	        
	         
            System.out.println("Excel表格:"+rs.getRows()+"行");  
            
            String dateString = PubFun.getCurrentDate();  //文件名称
            
            int upfile2=0;  //2期更新文件个数
            int upfile3=0;  //3期更新文件个数
            String errStr="";
            
            PrintWriter printwriter2 = new PrintWriter(new FileOutputStream(xmlPath+"2期_"+dateString+".xml"));   //生成文件
            PrintWriter printwriter3 = new PrintWriter(new FileOutputStream(xmlPath+"3期_"+dateString+".xml"));   //生成文件
            
            if(rs.getRows()>=2)
            {
            	//2期文件头
            	String insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter2.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter2.println(insertStr);
            	//3期文件头
            	insertStr = "<?xml version='1.0' encoding='UTF-8' ?>";
            	insertStr = insertStr.replaceAll("\'", "\"");
            	printwriter3.println(insertStr);
            	insertStr = "<SUTrace>";
            	printwriter3.println(insertStr);
            	
            	for(int i=1;i<rs.getRows();i++)  //从第二行起，第一行是标题
            	{
            		if(!"2".equals(rs.getCell(0, i).getContents().trim())&&!"3".equals(rs.getCell(0, i).getContents().trim())) //2,3期标志
            		{
            			break;
            		}
            		else if("2".equals(rs.getCell(0, i).getContents().trim()))  //2期更新文件处理
            		{
            		
            		    // 校验信息
						String fileName = ""; // 更新文件名称
						fileName = rs.getCell(1, i).getContents().trim();
						
						//判断是否存在重复的文件,去除重复文件
						boolean CFFlag=false;
						for(int j=1;j<rs.getRows()&&j<i;j++)
						{
							if("2".equals(rs.getCell(0, j).getContents().trim()))
							{
								String BfileName = rs.getCell(1, j).getContents().trim();
								if(fileName.equals(BfileName))
								{
									System.out.println();
									System.out.println("第"+(i+1)+"行与第"+(j+1)+"行存在重复文件:"+BfileName);
									CFFlag = true;
									//将重复版本号写人更新模板
									readWriteXls(i,3,"",false);  //清空
									readWriteXls(i,4,("与Excel中第"+(j+1)+"行重复"),true);
									break;
								}
							}
						}
						if(CFFlag)
						{
							continue;
						}
						
						if (fileName != null && !fileName.equals("")) {
							// 校验更新文件头
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "更新模板中第" + (i + 1) + "行更新文件路径有误！"
										+ "\n";
								break;
							}
							// 校验更新文件尾
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
								errStr += "更新模板中第" + (i + 1) + "行更新文件类型有误！"
										+ "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1) + "行更新文件名为空！请检查!"
									+ "\n";
							break;
						}
						
						//取文件最大版本
						int maxVerson = getMaxVersionNum("2",fileName);
						// 生成文件
						insertStr = "<FileName FileType='2' version='"
								+ maxVerson + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("2期："+insertStr);
						
                         //文件路径中大写字母校验
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     更新模板中第" + (i + 1) + "行更新文件路径中含有大写字母，请确认正确性!");
						}
						
						printwriter2.println(insertStr);
						upfile2++;
						//将版本号写人更新模板
						if(maxVerson==0)
						{
							//将问题版本写人更新模板
							readWriteXls(i,3,"",false);  //清空
							readWriteXls(i,4,("问题版本"),true);
						}
						else
						{
							readWriteXls(i,2,String.valueOf(maxVerson-1),false);
							readWriteXls(i,3,String.valueOf(maxVerson),false);
							readWriteXls(i,4,"",false);  //清空
						}
            		}
            		else if("3".equals(rs.getCell(0, i).getContents().trim()))  //3期更新文件处理
            		{
            		
            		    // 校验信息
						String fileName = ""; // 更新文件名称
						fileName = rs.getCell(1, i).getContents().trim();
						
						//判断是否存在重复的文件,去除重复文件
						boolean CFFlag=false;
						for(int j=1;j<rs.getRows()&&j<i;j++)
						{
							if("3".equals(rs.getCell(0, j).getContents().trim()))
							{
								String BfileName = rs.getCell(1, j).getContents().trim();
								if(fileName.equals(BfileName))
								{
									System.out.println();
									System.out.println("第"+(i+1)+"行与第"+(j+1)+"行存在重复文件:"+BfileName);
									CFFlag = true;
									//将重复版本号写人更新模板
									readWriteXls(i,3,"",false);  //清空
									readWriteXls(i,4,("与Excel中第"+(j+1)+"行重复"),true);
									break;
								}
							}
						}
						if(CFFlag)
						{
							continue;
						}
						
						if (fileName != null && !fileName.equals("")) {
							// 校验更新文件头
							if (fileName.startsWith("ui")) {
							} else if (fileName.startsWith("java")) {
							} else {
								errStr += "更新模板中第" + (i + 1) + "行更新文件路径有误！"
										+ "\n";
								break;
							}
							// 校验更新文件尾
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
								errStr += "更新模板中第" + (i + 1) + "行更新文件类型有误！"
										+ "\n";
								break;
							}
						} else {
							errStr += "更新模板中第" + (i + 1) + "行更新文件名为空！请检查!"
									+ "\n";
							break;
						}

						//取文件最大版本
						int maxVerson = getMaxVersionNum("3",fileName);

						// 生成文件
						insertStr = "<FileName FileType='2' version='"
								+ maxVerson + "'>" + fileName + "</FileName>";

						insertStr = insertStr.replaceAll("\'", "\"");
						System.out.println("3期："+insertStr);
											
                        //文件路径中大写字母校验
						String tempfilname =fileName.replaceAll("\\\\", "/");
						myMatcher = myPattern.matcher(tempfilname.substring(0,tempfilname.lastIndexOf("/")));
						if(myMatcher.find()){
							System.out.println("     更新模板中第" + (i + 1) + "行更新文件路径中含有大写字母，请确认正确性!");
						}
						
						printwriter3.println(insertStr);
						upfile3++;
						
						//将版本号写人更新模板
						if(maxVerson==0)
						{
							//将问题版本写人更新模板
							readWriteXls(i,3,"",false);  //清空
							readWriteXls(i,4,("问题版本"),true);
						}
						else
						{
							readWriteXls(i,2,String.valueOf(maxVerson-1),false);
							readWriteXls(i,3,String.valueOf(maxVerson),false);
							readWriteXls(i,4,"",false);  //清空
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
            		System.out.println("\n  ^_^文件全部生成成功！\n");
            	}
            	else
            	{
            		System.out.println(errStr);
            	}
            	System.out.println("2期总共更新文件："+upfile2+"个!");
            	System.out.println("3期总共更新文件："+upfile3+"个!");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        } 
	}
    //	获取最大版本，不打印版本信息
	public int getMaxVersionNum1(String qici,String fileName ) throws IOException
	{
		
		int maxVersion=0;
		String cmd;
		Runtime rt = Runtime.getRuntime();
		Process pro;
	    InputStream is,is2;
	    InputStreamReader isr,isr2;
	    //vss目录,用户名，密码
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
		
		//本机器vss安装目录
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/文件相对于vss的路径及名称 -I- -Y登录vss用户名,密码
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
		    	System.out.println("   获取最大版本号超时，请检查文件:"+fileName+"在vss上是否存在，文件路径是否正确！！！");
		    }
	    }while( hasToWait && !hasTimeOut );
        //错误信息打印
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
	//获取最大版本并且打印版本信息
	public int getMaxVersionNum(String qici,String fileName ) throws IOException
	{
		
		int maxVersion=0;
		String cmd;
		Runtime rt = Runtime.getRuntime();
		Process pro;
	    InputStream is,is2;
	    InputStreamReader isr,isr2;
	    //vss目录,用户名，密码
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
		
		//本机器vss安装目录
		String tpath=this.path;
		
		String[] env = {"SSDIR="+SSDIR,"path=%path%;"+tpath};
		//cmd.exe /c @ss history -v -b $/文件相对于vss的路径及名称 -I- -Y登录vss用户名,密码
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
			    String strMaxVersion = stdout.substring(7,13);   //最大版本号
			    maxVersion = Integer.parseInt(strMaxVersion.trim());
			    System.out.println();
			    System.out.println("   下面一行文件版本信息："+stdout);
			    hasToWait = false;
		    }else if(waitTime>=8000){
		    	hasToWait = false;
		    	hasTimeOut = true;
		    	System.out.println("   获取最大版本号超时，请检查文件:"+fileName+"在vss上是否存在，文件路径是否正确！！！");
		    }
	    }while( hasToWait && !hasTimeOut );
        //错误信息打印
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
//	读写一个Excel，并把相应的数据添入到指定的单元格。
	public  void readWriteXls(int rowNum,int cellNum,String version,boolean redFlag) { 
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));
			int sheetCount = workbook.getNumberOfSheets();// excel几张表 
			HSSFSheet sheet = workbook.getSheetAt(0);// 对excel的第一个表引用 
			int rowCount = sheet.getLastRowNum();// 取得最后一行的下标 
			if(rowNum<=rowCount)
			{
				HSSFRow row = sheet.getRow(rowNum);// 引用行 
				if (row != null) 
				{ 
					HSSFCell curCell= row.createCell((short)cellNum); 
					if(redFlag)
					{
						curCell.setCellStyle(getStyle2(workbook));
						sheet.setColumnWidth((short)cellNum, (short)(18 * 256));  //设置列宽
					}
					curCell.setEncoding(HSSFCell.ENCODING_UTF_16); 
					curCell.setCellValue(version); 

				}
			}
			 FileOutputStream fileOut = new FileOutputStream(filePath);
			 workbook.write(fileOut);
			 fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} 
		
	}
	public static HSSFCellStyle getStyle2(HSSFWorkbook wb) { 
		  HSSFFont font2 = wb.createFont(); // 创建字体格式 
		  font2.setColor(HSSFFont.COLOR_RED); // 设置单元格字体的颜色. 
		  font2.setFontHeight((short) 220); // 设置字体大小 
		  font2.setFontName("宋体"); // 设置单元格字体
		  
		  HSSFCellStyle style3 = wb.createCellStyle(); // 创建单元格风格. 
		  style3.setAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中 
		  style3.setAlignment(HSSFCellStyle.ALIGN_CENTER); // /水平居中 
		  
//		  style3.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);  //设置背景色
//		  style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		  style3.setFont(font2); // 将字体格式加入到单元格风格当中 
		  return style3; 
	} 
	
	public static void main(String[] args)
	{
		XlsToXml xx = new XlsToXml();
        //生成方式一		取填写的文件版本生成更新文件  check in,out版本号必填,不去除重复文件
	//	xx.everyUpFile();   
        //生成方式二		取vss上文件最大版本号生成更新文件,check in,out版本号可不填，自动去除重复文件
		xx.everyUpFileByMaxVerson();  
		
	}
	
	
}
