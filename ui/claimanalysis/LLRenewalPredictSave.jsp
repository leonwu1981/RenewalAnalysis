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
 <%@page import="com.sinosoft.lis.vdb.*"%>
 <%@page import = "com.sinosoft.lis.pubfun.*"%>
 <%@page import = "com.sinosoft.utility.*"%>
 <%@page import="com.sinosoft.lis.claimanalysis.renewal.*"%>
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
  String tPredictType = request.getParameter("PredictType");
  String tDutyStr = request.getParameter("DutyStr");
  String tTaskDate = request.getParameter("TaskDate");
  String tTaskUser = request.getParameter("TaskUser");
  String tFileTitle = request.getParameter("FileTitle");
  String tEmail = request.getParameter("Email");
  ClaimPredictUI tClaimPredictUI = new ClaimPredictUI();
  
  TransferData tTransferData = new TransferData();
  VData vData = new VData();
  try
  {
			tTransferData.setNameAndValue("PredictType",tPredictType);	
			tTransferData.setNameAndValue("DutyStr",tDutyStr);
			tTransferData.setNameAndValue("FilePath",filePath);
			tTransferData.setNameAndValue("TaskDate",tTaskDate);
			tTransferData.setNameAndValue("TaskUser",tTaskUser);
			tTransferData.setNameAndValue("FileTitle",tFileTitle);
			tTransferData.setNameAndValue("Email",tEmail);
    	vData.add(tGI);
    	vData.add(tTransferData);  
    	if( !tClaimPredictUI.submitData(vData, "PRINT") )
	    {
		      tError = tClaimPredictUI.mErrors;
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

