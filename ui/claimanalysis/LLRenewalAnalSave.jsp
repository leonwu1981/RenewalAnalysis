 <%@ page contentType="text/html; charset=GBK" import="com.sinosoft.lis.xpath.*" import="java.io.*"%>
 <%@page import="javax.xml.parsers.*"%>
 <%@page import="org.w3c.dom.*"%>
 <%@page import="java.net.*"%>
 <%@page import="org.apache.xml.serialize.*"%>
 <%@page import="java.util.*"%>
 <%@page import="org.xml.sax.InputSource" %>
 <%@page import="com.sinosoft.lis.schema.*"%>
 <%@page import="com.sinosoft.lis.vschema.*"%>
 <%@page import="com.sinosoft.lis.db.*"%>
 <%@page import="com.sinosoft.lis.claimanalysis.renewal.*"%>
 <%@page import="com.sinosoft.lis.vdb.*"%>
 <%@page import = "com.sinosoft.lis.pubfun.*"%>
 <%@page import = "com.sinosoft.utility.*"%>
 <%@page import="java.net.*"%>

<%

  GlobalInput tGI = new GlobalInput();
  tGI=(GlobalInput)session.getValue("GI");

  String FlagStr = "";
  String Content = "";
  CErrors tError = null;
  
  //文件路径
  String filePath = application.getRealPath("/").replace('\\','/');  
  
  filePath = filePath + "/";
  String tGrpContNo = request.getParameter("GrpContNo");   //团体保单号
  //String tStartDate = request.getParameter("StartDate"); 
  //String tEndDate = request.getParameter("EndDate"); 
  String tRiskCode = request.getParameter("RiskCode"); 
  String tTaskComment = request.getParameter("TaskComment");
  String tEmail = request.getParameter("Email");

  RenewalAnalysisUI tRenewalAnalysisUI = new RenewalAnalysisUI();
  TransferData tTransferData = new TransferData();
  VData vData = new VData();
  try
  {  
	    tTransferData.setNameAndValue("GrpContNo",tGrpContNo);	
	    
	   // tTransferData.setNameAndValue("StartDate",tStartDate);	
	   // tTransferData.setNameAndValue("EndDate",tEndDate);	
	    //tTransferData.setNameAndValue("RiskCode",tRiskCode);	
	    //tTransferData.setNameAndValue("ManageCom",tManageCom);	
	    
	    tTransferData.setNameAndValue("FilePath",filePath);	
	    tTransferData.setNameAndValue("TaskComment",tTaskComment);	
	    tTransferData.setNameAndValue("Email",tEmail);
    	vData.add(tGI);
    	vData.add(tTransferData);  
    	
    	if( !tRenewalAnalysisUI.submitData(vData, "PRINT") )
	    {
		      tError = tRenewalAnalysisUI.mErrors;
		      System.out.println("fail to deal the data");
		      Content = " 生成文件失败，原因是："+tError.getFirstError();
		      FlagStr = "Fail";
	    }
	    else
	    {
		      System.out.println("jsppage------Successful^^^^^^^^^^^^^^^^^^^^^^^^^^");
		      Content = " 生成文件成功";
		      FlagStr = "Succ";
	    }
  }
  catch(Exception ex)
  {
	      Content = " 生成文件失败，原因是："+ex.toString();
	      FlagStr = "Fail";  			
  }

%>


