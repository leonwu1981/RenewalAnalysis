<%@include file="../common/jsp/UsrCheck.jsp"%>
<SCRIPT src="../common/javascript/Common.js"></SCRIPT>
<script language="JavaScript">

function initForm() {
  try {
	initReqInfo();
    initReportGrid();
    queryReqDB();
  }
  catch(re) {
    alert("LLRenewalPredictInit.jsp-->InitForm函数中发生异常:初始化界面错误!");
  }
}

function initReqInfo(){
	var sql = "select grpcontno,startdate,enddate,taskcomment from RenewalAnalysisTask where taskdate='"+TaskDate+"' and taskuser='"+TaskUser+"'";
	var result = decodeEasyQueryResult(easyQueryVer3(sql));
	grpcontno.value=result[0][0];
	//startdate.value=result[0][1];
	//enddate.value=result[0][2];
	taskcomment.value=result[0][3];

	fm.TaskDate.value = TaskDate;
	fm.TaskUser.value = TaskUser;
}

function initReportGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="序号";                 //列名
	    iArray[0][1]="30px";                 //列名
	    iArray[0][3]=0;                 //列名

	    iArray[1]=new Array();
	    iArray[1][0]="文件说明";               //列名
	    iArray[1][1]="220px";                //列宽
	    iArray[1][2]=1200;                        //列最大值
	    iArray[1][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    iArray[2]=new Array();
	    iArray[2][0]="时间";
	    iArray[2][1]="115px";
	    iArray[2][2]=400;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="状态";
	    iArray[3][1]="80px";
	    iArray[3][2]=800;
	    iArray[3][3]=0;

	    iArray[4]=new Array();
	    iArray[4][0]="文件路径";
	    iArray[4][1]="0px";
	    iArray[4][2]=1200;
	    iArray[4][3]=0;

	    iArray[5]=new Array();
	    iArray[5][0]="文件名称";
	    iArray[5][1]="0px";
	    iArray[5][2]=1200;
	    iArray[5][3]=0;

	    
	    ReportGrid = new MulLineEnter("fm", "ReportGrid");

	    //设置Grid属性
	    ReportGrid.mulLineCount = 1;
	    ReportGrid.displayTitle = 1;
	    ReportGrid.locked = 1;
	    ReportGrid.canSel = 1;
	    ReportGrid.canChk = 0;
	    ReportGrid.hiddenSubtraction = 1;
	    ReportGrid.hiddenPlus = 1;
	    ReportGrid.loadMulLine(iArray);
	    
	    //设置Grid属性——多选
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