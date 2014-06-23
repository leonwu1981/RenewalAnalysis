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
  String tReportName = request.getParameter("ReportName");
  String tPlanGroups = request.getParameter("PlanGroups");
  String tTaskDate = request.getParameter("TaskDate");
  String tTaskUser = request.getParameter("TaskUser");
  PlanReportUI tPlanReportUI = new PlanReportUI();
  
  TransferData tTransferData = new TransferData();
  VData vData = new VData();
  try
  {  
			tTransferData.setNameAndValue("ReportName",tReportName);	
			tTransferData.setNameAndValue("PlanGroups",tPlanGroups);
			tTransferData.setNameAndValue("FilePath",filePath);
			tTransferData.setNameAndValue("TaskDate",tTaskDate);
			tTransferData.setNameAndValue("TaskUser",tTaskUser);
    	vData.add(tGI);
    	vData.add(tTransferData);  
    	if( !tPlanReportUI.submitData(vData, "PRINT") )
	    {
		      tError = tPlanReportUI.mErrors;
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


<html>
<script language="javascript">
try
{
  parent.fraInterface.afterSubmit("<%=FlagStr%>");
}
catch(ex)
{
  alert(ex.description);
}

</script>
</html>

