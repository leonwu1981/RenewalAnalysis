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
	    //���屣���Ĳ�ѯ����.
	var operator = "<%=tGI.Operator%>";   //��¼����Ա
	var manageCom = "<%=tGI.ManageCom%>"; //��¼��½����
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
      <TD class=title>�½�����<input type="radio" name="QueryType" id=QueryType_1 value="1" onclick="chooseQueryType()" <%=radio1%> ></TD>
      <TD class=title>�����б�<input type="radio" name="QueryType" id=QueryType_2 value="2" onclick="chooseQueryType()" <%=radio2%> ></TD>
    </tr>
  </table>
</Div>
<hr>
<Div id="divQueryType1" style="display: ''">
 <table class=common border=0 width=100%>
   <tr>
     <td class=titleImg align=center>���������屣����ѯ������</td>
   </tr>
 </table>
 <table class=common>
    <TR class=common>
       <td CLASS="title">���屣���� </td>    
       <td CLASS="input">
           <input NAME="ContNo"  CLASS="common"  MAXLENGTH="20">
       </td>
       <TD class=title>Ͷ������</TD>
       <TD class=input>
          <input class="common" name=PrintNo MAXLENGTH="20">
       </TD>
    </tr>
    <TR  class= common8>
				<TD  class= title>������Ч��������</TD>
				<TD  class= input><input class= "coolDatePicker" name="CValiDateStart" style="width:162px"></TD>
				<TD class= title> ������Ч����ֹ��</TD>
				<TD class= input> <Input class= "coolDatePicker" name="CValiDateEnd" style="width:162px"></TD>
	 </TR>
	  <TR class=common>
      <td CLASS="title">��λ���� </td>
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
        <INPUT VALUE="��ѯ����" class= cssbutton TYPE=button onclick="searchGrpCont();">
     </TD>
   </tr>
 </table>
 <table>
    <tr>
      <td class=common>
          <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;" OnClick= "showPage(this,divLDCustomer2);">
      </td>
      <td class= titleImg>
           ��ѯ���
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
        <INPUT CLASS=cssbutton VALUE="��ҳ" TYPE=button onclick="turnPage7.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage7.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage7.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="βҳ" TYPE=button onclick="turnPage7.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="ѡ�񱣵�" class= cssbutton TYPE=button onclick="checkGrpCont();">
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
                       ��ѡ������屣��<font color=red>*</font>
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
        <INPUT CLASS=cssbutton VALUE="��ҳ" TYPE=button onclick="turnPage2.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage2.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage2.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="βҳ" TYPE=button onclick="turnPage2.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="ȡ��ѡ��" class= cssbutton TYPE=button onclick="uncheckGrpCont();">
       </td></tr>
     </table>       
  </div>
  <hr>
  <table>
    <tr>
      <td class=common>
        <IMG  src= "../common/images/butExpand.gif" style= "cursor:hand;">
      </td>
      <td class= titleImg>������Ϣ</td>
    </tr>
  </table>
  <table class=common>
<!--
    <TR class=common>
	  <TD class=title>��ʼʱ��</TD>
	  <TD class=input>
	    <Input class="coolDatePicker"   dateFormat="short" name=StartDate elementtype=nacessary verify="��ʼʱ��|Date" style="width:162px" readonly >
	  </TD>
	  <TD class=title>����ʱ��</TD>
	  <TD class=input>
		<Input class="coolDatePicker"  dateFormat="short" name=EndDate elementtype=nacessary verify="����ʱ��|Date" style="width:162px" readonly >
	  </TD>	
	</TR>
-->
    <TR class=common>
       <td CLASS="title">֪ͨ���� </td>    
       <td CLASS="input">
           <input NAME="Email"  CLASS="common"  MAXLENGTH="200" value="@generalichina.com">
           <font color=red>*</font>
       </td>
       <td CLASS="title">����˵�� </td>    
       <td CLASS="input">
           <input NAME="TaskComment"  CLASS="common"  MAXLENGTH="20">
           <font color=red>*</font>
       </td>
    </TR>
  </table>
  <hr>
  <table  class= common>
    <tr><td align=right>
      <INPUT VALUE="�½�����" class= cssbutton TYPE=button onclick="addTask();">
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
           ��ѯ���
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
        <INPUT CLASS=cssbutton VALUE="��ҳ" TYPE=button onclick="turnPage3.firstPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage3.previousPage();prewarningNameSet();">           
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage3.nextPage();prewarningNameSet();"> 
        <INPUT CLASS=cssbutton VALUE="βҳ" TYPE=button onclick="turnPage3.lastPage();prewarningNameSet();">      
     </center>  
     <table  class= common>
       <tr><td>
         <INPUT VALUE="�鿴��ϸ" class= cssbutton TYPE=button onclick="ReportDetail();">
        <!-- <INPUT VALUE="�鿴Ԥ������" class= cssbutton TYPE=button onclick="PredictDetail();"> -->
         <INPUT VALUE="ˢ���б�" class= cssbutton TYPE=button onclick="refreshReq();">
       </td></tr>
     </table>           
    </div> 
 <hr>
</Div>

 <span id="spanCode"  style="display: none; position:absolute; slategray"></span>

</form>  
</body>
</html>
