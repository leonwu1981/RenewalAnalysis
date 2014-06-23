<%@include file="../common/jsp/UsrCheck.jsp"%>
<%@page contentType="text/html; charset=GBK"%>
<html>
<head>
<%	
  GlobalInput tGI = new GlobalInput();
	tGI = (GlobalInput)session.getValue("GI");
	String radioNum = request.getParameter("radio");   //radio
	String radio1="checked";
	String radio2="";
	if("2".equals(radioNum)){
		radio1="";
		radio2="checked";
	}
%>
<script>
	    //团体保单的查询条件.
	var operator = "<%=tGI.Operator%>";   //记录操作员
	var manageCom = "<%=tGI.ManageCom%>"; //记录登陆机构
	var radioNum = "<%=radioNum%>";
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
<SCRIPT src="LLRenewalAnal.js"></SCRIPT>
<%@include file="LLRenewalAnalInit.jsp"%>

</head>
<body  onload="initForm();" >
<form name=fm  target=fraSubmit method=post>
<Div id="divBussChose" style="display:''">
  <table class=common width=90%>
    <tr>
      <TD class=title>新建任务<input type="radio" name="QueryType" id=QueryType_1 value="1" onclick="chooseQueryType()" <%=radio1%> ></TD>
      <TD class=title>任务列表<input type="radio" name="QueryType" id=QueryType_2 value="2" onclick="chooseQueryType()" <%=radio2%> ></TD>
    </tr>
  </table>
</Div>
<hr>
<Div id="divQueryType1" style="display: ''">
 <table class=common border=0 width=100%>
   <tr>
     <td class=titleImg align=center>请输入团体保单查询条件：</td>
   </tr>
 </table>
 <table class=common>
    <TR class=common>
       <td CLASS="title">团体保单号 </td>    
       <td CLASS="input">
           <input NAME="ContNo"  CLASS="common"  MAXLENGTH="20">
       </td>
       <TD class=title>投保单号</TD>
       <TD class=input>
          <input class="common" name=PrintNo MAXLENGTH="20">
       </TD>
    </tr>
    <TR  class= common8>
				<TD  class= title>保单生效日期起期</TD>
				<TD  class= input><input class= "coolDatePicker" name="CValiDateStart" style="width:162px"></TD>
				<TD class= title> 保单生效日期止期</TD>
				<TD class= input> <Input class= "coolDatePicker" name="CValiDateEnd" style="width:162px"></TD>
	 </TR>
	  <TR class=common>
      <td CLASS="title">单位名称 </td>
      <td CLASS="input">
        <input NAME="GrpName"  CLASS="common">
      </td>
     <td CLASS="title"> </td>
     <td CLASS="input">
         <input   CLASS="readonly">
     </td>
  </tr>
   <tr>
     <TD rowspan=4>
        <INPUT VALUE="查询保单" class= cssbutton TYPE=button onclick="searchGrpCont();">
     </TD>
   </tr>
 </table>
 <table>
    <tr>
      <td class=common>
          <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;" OnClick= "showPage(this,divLDCustomer2);">
      </td>
      <td class= titleImg>
           查询结果
      </td>
     </tr>
 </table>
    <Div  id= "divLDCustomer2" style= "display: ''">
      <table  class= common>
         <tr  class= common>
            <td text-align: left colSpan=1>
              <span id="spanCustomGrid" >
              </span> 
            </td>
        </tr>
      </table>
     <center>      
        <INPUT CLASS=cssbutton VALUE="首页" TYPE=button onclick="turnPage7.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="上一页" TYPE=button onclick="turnPage7.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="下一页" TYPE=button onclick="turnPage7.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="尾页" TYPE=button onclick="turnPage7.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="选择保单" class= cssbutton TYPE=button onclick="checkGrpCont();">
       </td></tr>
     </table>           
    </div> 
 <hr>
 <table>
    <tr>
      <td class=common>
          <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;" OnClick= "showPage(this,divCheckedGrpCont);">
      </td>
      <td class= titleImg>
                       已选择的团体保单<font color=red>*</font>
      </td>
     </tr>
 </table>
    <Div  id= "divCheckedGrpCont" style= "display: ''">
      <table  class= common>
         <tr  class= common>
            <td text-align: left colSpan=1>
              <span id="spanCheckedGrid" >
              </span> 
            </td>
        </tr>
      </table> 
     <center>      
        <INPUT CLASS=cssbutton VALUE="首页" TYPE=button onclick="turnPage2.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="上一页" TYPE=button onclick="turnPage2.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="下一页" TYPE=button onclick="turnPage2.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="尾页" TYPE=button onclick="turnPage2.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="取消选择" class= cssbutton TYPE=button onclick="uncheckGrpCont();">
       </td></tr>
     </table>       
  </div>
  <hr>
  <table>
    <tr>
      <td class=common>
        <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;">
      </td>
      <td class= titleImg>任务信息</td>
    </tr>
  </table>
  <table class=common>
<!--
    <TR class=common>
	  <TD class=title>开始时间</TD>
	  <TD class=input>
	    <Input class="coolDatePicker"   dateFormat="short" name=StartDate elementtype=nacessary verify="开始时间|Date" style="width:162px" readonly >
	  </TD>
	  <TD class=title>结束时间</TD>
	  <TD class=input>
		<Input class="coolDatePicker"  dateFormat="short" name=EndDate elementtype=nacessary verify="结束时间|Date" style="width:162px" readonly >
	  </TD>	
	</TR>
-->
    <TR class=common>
       <td CLASS="title">通知邮箱 </td>    
       <td CLASS="input">
           <input NAME="Email"  CLASS="common"  MAXLENGTH="200" value="@generalichina.com">
           <font color=red>*</font>
       </td>
       <td CLASS="title">任务说明 </td>    
       <td CLASS="input">
           <input NAME="TaskComment"  CLASS="common"  MAXLENGTH="20">
           <font color=red>*</font>
       </td>
    </TR>
  </table>
  <hr>
  <table  class= common>
    <tr><td align=right>
      <INPUT VALUE="新建任务" class= cssbutton TYPE=button onclick="addTask();">
    </td></tr>
  </table>
 
</div>

<Div id="divQueryType2" style="display:'none'">
  <table>
    <tr>
      <td class=common>
          <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;" OnClick= "showPage(this,divRequest);">
      </td>
      <td class= titleImg>
           查询结果
      </td>
     </tr>
 </table>
    <Div  id= "divRequest" style= "display: ''">
      <table  class= common>
         <tr  class= common>
            <td text-align: left colSpan=1>
              <span id="spanReqGrid" >
              </span> 
            </td>
        </tr>
      </table>
     <center>      
        <INPUT CLASS=cssbutton VALUE="首页" TYPE=button onclick="turnPage3.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="上一页" TYPE=button onclick="turnPage3.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="下一页" TYPE=button onclick="turnPage3.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="尾页" TYPE=button onclick="turnPage3.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="查看详细" class= cssbutton TYPE=button onclick="ReportDetail();">
        <!-- <INPUT VALUE="查看预测理赔" class= cssbutton TYPE=button onclick="PredictDetail();"> -->
         <INPUT VALUE="刷新列表" class= cssbutton TYPE=button onclick="refreshReq();">
       </td></tr>
     </table>           
    </div> 
 <hr>
</Div>

 <span id="spanCode"  style="display: none; position:absolute; slategray"></span>

</form>  
</body>
</html>
