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
	    //���屣���Ĳ�ѯ����.
	var operator = "<%=tGI.Operator%>";   //��¼����Ա
	var manageCom = "<%=tGI.ManageCom%>"; //��¼��½����
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
      <td class= titleImg>�������� --> Ԥ������</td>
    </tr>
  </table>
  <table>
    <tr>
      <td class= titleImg>������Ϣ</td>
    </tr>
  </table>
  <table class=common>
    <TR class=common>
      <TD class=title>���屣��</TD>
      <td class=input colspan=3>
        <input name=grpcontno readonly size=80 />
      </td>
    </TR>
<!--  
    <TR class=common>
	  <TD class=title>��ʼʱ��</TD>
	  <TD class=input>
	    <input name=startdate readonly  readonly size=24 />
	  </TD>
	  <TD class=title>����ʱ��</TD>
	  <TD class=input>
		<input name=enddate readonly readonly size=24 />
	  </TD>	
	</TR>
-->
    <TR class=common>
       <td CLASS="title">����˵�� </td>    
       <td CLASS="input" colspan=3>
           <input name=taskcomment readonly size=80 />
       </td>
    </TR>
    <tr>
      <td colspan=2><INPUT VALUE="���������б�" class= cssbutton TYPE=button onclick="returnList();">
      </td>
    </tr>
  </table>
   <hr>
<form name=fm  target=fraSubmit method=post>
  <input type=hidden name=TaskDate />
  <input type=hidden name=TaskUser />
  <table>
    <tr>
      <td class= titleImg>Ԥ�������ļ�</td>
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
        <INPUT CLASS=cssbutton VALUE="��ҳ" TYPE=button onclick="turnPage.firstPage();"> 
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage.previousPage();">           
        <INPUT CLASS=cssbutton VALUE="��һҳ" TYPE=button onclick="turnPage.nextPage();"> 
        <INPUT CLASS=cssbutton VALUE="βҳ" TYPE=button onclick="turnPage.lastPage();">      
     </center>  
     <table  class= common>
       <tr><td>
         <a onClick= "return SaveAs()"><INPUT VALUE="�����ļ�" class= cssbutton TYPE=button></a>
         <INPUT VALUE="ˢ���б�" class= cssbutton TYPE=button onclick="queryReqDB();">
       </td></tr>
     </table>          
    </div>
    <br>
    <Div id=DivFileDownload style="display:'none'">
	    <A id=fileUrl href="" target="_blank">�������</A>
	</Div>	
 <hr> 
  <table>
    <tr>
      <td class= titleImg>�½�Ԥ��</td>
    </tr>
  </table>
  <table class=common>
    <TR class=common>
       <td CLASS="title">���� </td>    
       <td CLASS="input" colspan=3>
         <input type=checkbox name='PredictType' value='all' checked > ����ȫ��
         <input type=checkbox name='PredictType' value='duty' > �����λ���
         <INPUT VALUE="����Ԥ�����ⱨ��" class= cssbutton TYPE=button onclick="predict();">
       </td>
    </TR>
    <TR class=common>
       <td CLASS="title">˵��</td>    
       <td CLASS="input" colspan=3>
         <input name=FileTitle size=80/>
         <font color=red>*</font>
       </td>
    </TR>
     <TR class=common>
       <td CLASS="title">֪ͨ����</td>    
       <td CLASS="input" colspan=3>
         <input name=Email size=80 value="@generalichina.com" />
         <font color=red>*</font>
       </td>
    </TR>
    <TR class=common>
	  <TD class=title rowspan="2">����ɸѡ</TD>
	  <TD class=input>
	               ���ִ���
         <input name=RiskCode />
      </TD>
      <TD class=input>
                         ���δ���
         <input name=DutyCode />
      </TD>
      <TD class=input>
         <INPUT VALUE="ѡ��" class= cssbutton TYPE=button onclick="checkDutyAll();">
         <INPUT VALUE="ȡ��ѡ��" class= cssbutton TYPE=button onclick="uncheckDutyAll();">
	  </TD>
	</TR>
    <TR class=common>
	  <TD colspan="3" class=input>
         <INPUT VALUE="ѡ������ҽ��������" class= cssbutton TYPE=button onclick="checkMedicalDuty();">
         <INPUT VALUE="ѡ������סԺ����" class= cssbutton TYPE=button onclick="checkIpDuty();">
         <INPUT VALUE="ѡ��������������" class= cssbutton TYPE=button onclick="checkOpDuty();">
	  </TD>
	</TR>
   
  </table>

<jsp:include page="<%=predictJsp%>"></jsp:include>
  


</form>  
</body>
</html>
 