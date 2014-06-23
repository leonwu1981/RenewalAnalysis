<%@include file="../common/jsp/UsrCheck.jsp"%>
<%@page contentType="text/html; charset=GBK"%>
<html>
<head>
<%	
  GlobalInput tGI = new GlobalInput();
	tGI = (GlobalInput)session.getValue("GI");
	String TaskDate = request.getParameter("TaskDate");   //TaskDate
	String TaskUser = request.getParameter("TaskUser");   //TaskUser
	
	String GrpContNo = request.getParameter("GrpContNo");   //GrpContNo
	//String StartDate = request.getParameter("StartDate");   //StartDate
	//String EndDate = request.getParameter("EndDate");   //EndDate
	//String TaskComment = request.getParameter("TaskComment");   //TaskComment
	
	String realPath = application.getRealPath("/").replace('\\','/') + "claimanalysis/renewal/";
    realPath = "../claimanalysis/renewal/";
    
    String TaskDateNum = TaskDate.replaceAll("-","").replaceAll(":","").replaceAll(" ","");
    String planJsp = "../claimanalysis/renewal/file/" + TaskUser + "/" + TaskDateNum + "/plan.jsp";
%>
<script>
	    //团体保单的查询条件.
	var operator = "<%=tGI.Operator%>";   //记录操作员
	var manageCom = "<%=tGI.ManageCom%>"; //记录登陆机构
	var realPath = "<%=realPath%>";
	var TaskDate = "<%=TaskDate%>";
	var TaskUser = "<%=TaskUser%>";

</script>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<LINK href="../common/css/Project.css" rel=stylesheet type=text/css>
<LINK href="../common/css/mulLine.css" rel=stylesheet type=text/css>
<SCRIPT src="../common/javascript/Common.js" ></SCRIPT>
<SCRIPT src="../common/javascript/MulLine.js"></SCRIPT>
<SCRIPT src="../common/cvar/CCodeOperate.js"></SCRIPT>
<SCRIPT src="../common/Calendar/Calendar.js"></SCRIPT>
<SCRIPT src="../common/easyQueryVer3/EasyQueryVer3.js"></SCRIPT>
<SCRIPT src="../common/easyQueryVer3/EasyQueryCache.js"></SCRIPT>
<SCRIPT src="../common/javascript/VerifyInput.js"></SCRIPT>
<SCRIPT src="LLRenewalReport.js"></SCRIPT>
<%@include file="LLRenewalReportInit.jsp"%>

</head>
<body  onload="initForm();" >
  <table>
    <tr>
      <td class= titleImg>续保分析 --> 汇总信息</td>
    </tr>
  </table>
  <table>
    <tr>
      <td class=common>
        <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;">
      </td>
      <td class= titleImg>任务信息</td>
    </tr>
  </table>
  <table class=common>
    <TR class=common>
      <TD class=title>团体保单</TD>
      <td class=input colspan=3>
        <input name=grpcontno readonly size=80 />
      </td>
    </TR>
<!--  
    <TR class=common>
	  <TD class=title>开始时间</TD>
	  <TD class=input>
	    <input name=startdate readonly  readonly size=24 />
	  </TD>
	  <TD class=title>结束时间</TD>
	  <TD class=input>
		<input name=enddate readonly readonly size=24 />
	  </TD>	
	</TR>
-->
    <TR class=common>
       <td CLASS="title">任务说明 </td>    
       <td CLASS="input" colspan=3>
           <input name=taskcomment readonly size=80 />
       </td>
    </TR>
    <tr>
      <td colspan=2><INPUT VALUE="返回任务列表" class= cssbutton TYPE=button onclick="returnList();">
      </td>
    </tr>
  </table>
  
<form name=fm  target=fraSubmit method=post>
  <table>
    <tr>
      <td class=common>
        <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;">
      </td>
      <td class= titleImg>文件列表</td>
    </tr>
  </table>
    <Div  id= "divReport" style= "display: ''">
      <table  class= common>
         <tr  class= common>
            <td text-align: left colSpan=1>
              <span id="spanReportGrid" >
              </span> 
            </td>
        </tr>
      </table>
     <center>      
        <INPUT CLASS=cssbutton VALUE="首页" TYPE=button onclick="turnPage.firstPage();"> 
        <INPUT CLASS=cssbutton VALUE="上一页" TYPE=button onclick="turnPage.previousPage();">           
        <INPUT CLASS=cssbutton VALUE="下一页" TYPE=button onclick="turnPage.nextPage();"> 
        <INPUT CLASS=cssbutton VALUE="尾页" TYPE=button onclick="turnPage.lastPage();">      
     </center>  
     <table  class= common>
       <tr><td>
         <a onClick= "return SaveAs()"><INPUT VALUE="下载报表" class= cssbutton TYPE=button></a>
         
       </td></tr>
     </table>          
    </div>
    <br>
    <Div id=DivFileDownload style="display:'none'">
	    <A id=fileUrl href="" target="_blank">点击下载</A>
	</Div>	
<!--
 <hr> 
  <table>
    <tr>
      <td class= titleImg>新建医疗险理赔情况统计(按计划)</td>
    </tr>
  </table>
  <table class=common>
    <TR class=common>
       <td CLASS="title">生成报表 </td>    
       <td CLASS="input" colspan=2>
                        报表标识            
         <input name=ReportName size=30/>
         <font color=red>*</font>
       </td>
       <td class=input>
         <INPUT VALUE="按计划分组汇总" class= cssbutton TYPE=button onclick="submit1();">
         <INPUT VALUE="计划不分组汇总" class= cssbutton TYPE=button onclick="submit2();">
       </td>
    </TR>
    <TR class=common>
       <td CLASS="title">已建分组</td>    
       <td CLASS="input" colspan=3>
         <textarea name="PlanGroups" class="common4" ></textarea>
       </td>
    </TR>
    <TR class=common>
	  <TD class=title>计划筛选</TD>
	  <TD class=input colspan=2>
	              计划编码
         <input name=PlanCode  size=30/>
      </TD>
      <TD class=input>
         <INPUT VALUE="选中" class= cssbutton TYPE=button onclick="checkPlanAll();">
         <INPUT VALUE="取消选中" class= cssbutton TYPE=button onclick="uncheckPlanAll();">
	  </TD>
	</TR>
    <TR class=common>
       <td CLASS="title">
                         新建分组                
       </td>    
       <td CLASS="input" colspan=2>
                         计划组名
         <input name=GroupName size=30/>
         <font color=red>*</font> 
       </td>
       <td class=input>
         <INPUT VALUE="新建计划分组" class= cssbutton TYPE=button onclick="addGroup();">
       </td>
    </TR>
  </table>
<jsp:include page="<%=planJsp%>"></jsp:include>

-->
</form>  
</body>
</html>
 