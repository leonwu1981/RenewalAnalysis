<%@include file="../common/jsp/UsrCheck.jsp"%>
<%@page contentType="text/html; charset=GBK"%>
<html>
<head>
<%	
  GlobalInput tGI = new GlobalInput();
	tGI = (GlobalInput)session.getValue("GI");
	String TaskDate = request.getParameter("TaskDate");   //TaskDate
	String TaskUser = request.getParameter("TaskUser");   //TaskUser
	
	String TaskDateNum = TaskDate.replaceAll("-","").replaceAll(":","").replaceAll(" ","");
	String predictJsp = "../claimanalysis/renewal/file/" + TaskUser + "/" + TaskDateNum + "/predict.jsp";
	String GrpContNo = request.getParameter("GrpContNo");   //GrpContNo
	//String StartDate = request.getParameter("StartDate");   //StartDate
	//String EndDate = request.getParameter("EndDate");   //EndDate
	//String TaskComment = request.getParameter("TaskComment");   //TaskComment
	
	String realPath = application.getRealPath("/").replace('\\','/') + "claimanalysis/renewal/";
    realPath = "../claimanalysis/renewal/";
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
<SCRIPT src="LLRenewalPredict.js"></SCRIPT>
<%@include file="LLRenewalPredictInit.jsp"%>

</head>
<body  onload="initForm();" >
  <table>
    <tr>
      <td class= titleImg>续保分析 --> 预测理赔</td>
    </tr>
  </table>
  <table>
    <tr>
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
   <hr>
<form name=fm  target=fraSubmit method=post>
  <input type=hidden name=TaskDate />
  <input type=hidden name=TaskUser />
  <table>
    <tr>
      <td class= titleImg>预测理赔文件</td>
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
         <a onClick= "return SaveAs()"><INPUT VALUE="下载文件" class= cssbutton TYPE=button></a>
         <INPUT VALUE="刷新列表" class= cssbutton TYPE=button onclick="queryReqDB();">
       </td></tr>
     </table>          
    </div>
    <br>
    <Div id=DivFileDownload style="display:'none'">
	    <A id=fileUrl href="" target="_blank">点击下载</A>
	</Div>	
 <hr> 
  <table>
    <tr>
      <td class= titleImg>新建预测</td>
    </tr>
  </table>
  <table class=common>
    <TR class=common>
       <td CLASS="title">操作 </td>    
       <td CLASS="input" colspan=3>
         <input type=checkbox name='PredictType' value='all' checked > 汇总全部
         <input type=checkbox name='PredictType' value='duty' > 按责任汇总
         <INPUT VALUE="生成预测理赔报表" class= cssbutton TYPE=button onclick="predict();">
       </td>
    </TR>
    <TR class=common>
       <td CLASS="title">说明</td>    
       <td CLASS="input" colspan=3>
         <input name=FileTitle size=80/>
         <font color=red>*</font>
       </td>
    </TR>
     <TR class=common>
       <td CLASS="title">通知邮箱</td>    
       <td CLASS="input" colspan=3>
         <input name=Email size=80 value="@generalichina.com" />
         <font color=red>*</font>
       </td>
    </TR>
    <TR class=common>
	  <TD class=title rowspan="2">责任筛选</TD>
	  <TD class=input>
	               险种代码
         <input name=RiskCode />
      </TD>
      <TD class=input>
                         责任代码
         <input name=DutyCode />
      </TD>
      <TD class=input>
         <INPUT VALUE="选中" class= cssbutton TYPE=button onclick="checkDutyAll();">
         <INPUT VALUE="取消选中" class= cssbutton TYPE=button onclick="uncheckDutyAll();">
	  </TD>
	</TR>
    <TR class=common>
	  <TD colspan="3" class=input>
         <INPUT VALUE="选择所有医疗险责任" class= cssbutton TYPE=button onclick="checkMedicalDuty();">
         <INPUT VALUE="选择所有住院责任" class= cssbutton TYPE=button onclick="checkIpDuty();">
         <INPUT VALUE="选择所有门诊责任" class= cssbutton TYPE=button onclick="checkOpDuty();">
	  </TD>
	</TR>
   
  </table>

<jsp:include page="<%=predictJsp%>"></jsp:include>
  


</form>  
</body>
</html>
 