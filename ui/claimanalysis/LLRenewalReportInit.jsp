<%@include file="../common/jsp/UsrCheck.jsp"%>
<SCRIPT src="../common/javascript/Common.js"></SCRIPT>
<script language="JavaScript">

function initForm() {
  try {
	initReqInfo();
    initReportGrid();
    queryReqDB( TaskDate, TaskUser );
  }
  catch(re) {
    alert("LLRenewalReportInit.jsp-->InitForm�����з����쳣:��ʼ���������!");
  }
}

function initReqInfo(){
	var sql = "select grpcontno,startdate,enddate,taskcomment from RenewalAnalysisTask where taskdate='"+TaskDate+"' and taskuser='"+TaskUser+"'";
	var result = decodeEasyQueryResult(easyQueryVer3(sql));
	grpcontno.value=result[0][0];
	//startdate.value=result[0][1];
	//enddate.value=result[0][2];
	taskcomment.value=result[0][3];
}

function initReportGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="���";                 //����
	    iArray[0][1]="30px";                 //����
	    iArray[0][3]=0;                 //����

	    iArray[1]=new Array();
	    iArray[1][0]="��������";               //����
	    iArray[1][1]="200px";                //�п�
	    iArray[1][2]=400;                        //�����ֵ
	    iArray[1][3]=0;                          //�Ƿ���������,1��ʾ����0��ʾ������

	    iArray[2]=new Array();
	    iArray[2][0]="����ʱ��";
	    iArray[2][1]="60px";
	    iArray[2][2]=1200;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="�ļ�·��";
	    iArray[3][1]="0px";
	    iArray[3][2]=1200;
	    iArray[3][3]=0;

	    iArray[4]=new Array();
	    iArray[4][0]="�ļ�����";
	    iArray[4][1]="0px";
	    iArray[4][2]=1200;
	    iArray[4][3]=0;

	    
	    ReportGrid = new MulLineEnter("fm", "ReportGrid");

	    //����Grid����
	    ReportGrid.mulLineCount = 1;
	    ReportGrid.displayTitle = 1;
	    ReportGrid.locked = 1;
	    ReportGrid.canSel = 1;
	    ReportGrid.canChk = 0;
	    ReportGrid.hiddenSubtraction = 1;
	    ReportGrid.hiddenPlus = 1;
	    ReportGrid.loadMulLine(iArray);
	    
	    //����Grid���ԡ�����ѡ
	    //ReportGrid.mulLineCount = 0;
	    //ReportGrid.displayTitle = 1;
	    //ReportGrid.locked = 1;
	    ////ReportGrid.canSel = 1;
	    //ReportGrid.canChk = 1;
	    
	    //ReportGrid.hiddenSubtraction = 1;
	    //ReportGrid.hiddenPlus = 1;
	    //ReportGrid.loadMulLine(iArray);
	  }
	  catch(ex) {
	    alert(ex);
	  }
}

</script>