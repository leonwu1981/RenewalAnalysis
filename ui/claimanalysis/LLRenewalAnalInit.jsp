<%@include file="../common/jsp/UsrCheck.jsp"%>
<SCRIPT src="../common/javascript/Common.js"></SCRIPT>

<script language="JavaScript">

function initForm() {
  try {
    initCustomGrid();
    initCheckedGrid();
    initReqGrid();
    //
    divQueryType1.style.display="";
    divQueryType2.style.display="none";
    if(radioNum=="2"){
    	divQueryType1.style.display="none";
        divQueryType2.style.display="";
        refreshReq();
    }
  }
  catch(re) {
    alert("LLRenewalAnalInit.jsp-->InitForm函数中发生异常:初始化界面错误!");
  }
}

function initCustomGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="序号";                 //列名
	    iArray[0][1]="30px";                 //列名
	    iArray[0][3]=0;                 //列名

	    iArray[1]=new Array();
	    iArray[1][0]="单位名称";               //列名
	    iArray[1][1]="115px";                //列宽
	    iArray[1][2]=100;                        //列最大值
	    iArray[1][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    iArray[2]=new Array();
	    iArray[2][0]="联系人";
	    iArray[2][1]="35px";
	    iArray[2][2]=200;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="联系电话";             //列名
	    iArray[3][1]="0px";                //列宽
	    iArray[3][2]=200;                   //列最大值
	    iArray[3][3]=3;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[4]=new Array();
	    iArray[4][0]="销售渠道";             //列名
	    iArray[4][1]="40px";                //列宽
	    iArray[4][2]=200;                   //列最大值
	    iArray[4][3]=0;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[5]=new Array();
	    iArray[5][0]="单位性质";             //列名
	    iArray[5][1]="0px";                //列宽
	    iArray[5][2]=200;                   //列最大值
	    iArray[5][3]=3;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[6]=new Array();
	    iArray[6][0]="投保员工人数";             //列名
	    iArray[6][1]="55px";                //列宽
	    iArray[6][2]=200;                   //列最大值
	    iArray[6][3]=0;  

	    iArray[7]=new Array();
	    iArray[7][0]="客户号";             //列名
	    iArray[7][1]="0px";                //列宽
	    iArray[7][2]=200;                   //列最大值
	    iArray[7][3]=0; 
	    
	    iArray[8]=new Array();
	    iArray[8][0]="生效日期";             //列名
	    iArray[8][1]="50px";                //列宽
	    iArray[8][2]=200;                   //列最大值
	    iArray[8][3]=0; 
	    
	    iArray[9]=new Array();
	    iArray[9][0]="总人数";             //列名
	    iArray[9][1]="0px";                //列宽
	    iArray[9][2]=200;                   //列最大值
	    iArray[9][3]=3; 
	    
	    iArray[10]=new Array();
	    iArray[10][0]="总保费";             //列名
	    iArray[10][1]="40px";                //列宽
	    iArray[10][2]=200;                   //列最大值
	    iArray[10][3]=0; 
	    
	    iArray[11]=new Array();
	    iArray[11][0]="总保额";             //列名
	    iArray[11][1]="40px";                //列宽
	    iArray[11][2]=200;                   //列最大值
	    iArray[11][3]=0; 
	    
	    iArray[12]=new Array();
	    iArray[12][0]="出单机构";             //列名
	    iArray[12][1]="40px";                //列宽
	    iArray[12][2]=200;                   //列最大值
	    iArray[12][3]=0; 
	    
	    iArray[13]=new Array();
	    iArray[13][0]="团体保单号";             //列名
	    iArray[13][1]="50px";                //列宽
	    iArray[13][2]=200;                   //列最大值
	    iArray[13][3]=1; 
	    
	    
	    iArray[14]=new Array();
	    iArray[14][0]="投保单号";             //列名
	    iArray[14][1]="0px";                //列宽
	    iArray[14][2]=200;                   //列最大值
	    iArray[14][3]=3; 
	    
	    CustomGrid = new MulLineEnter("fm", "CustomGrid");
	    //设置Grid属性
	    CustomGrid.mulLineCount = 0;
	    CustomGrid.displayTitle = 1;
	    CustomGrid.locked = 1;
	    //CustomGrid.canSel = 1;
	    CustomGrid.canChk = 1;
	    
	    CustomGrid.hiddenSubtraction = 1;
	    CustomGrid.hiddenPlus = 1;
	    CustomGrid.loadMulLine(iArray);
	  }
	  catch(ex) {
	    alert(ex);
	  }
	}

function initCheckedGrid() {
	  var iArray = new Array();

	  try {
	    iArray[0]=new Array();
	    iArray[0][0]="序号";                 //列名
	    iArray[0][1]="30px";                 //列名
	    iArray[0][3]=0;                 //列名

	    iArray[1]=new Array();
	    iArray[1][0]="单位名称";               //列名
	    iArray[1][1]="115px";                //列宽
	    iArray[1][2]=100;                        //列最大值
	    iArray[1][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    iArray[2]=new Array();
	    iArray[2][0]="联系人";
	    iArray[2][1]="35px";
	    iArray[2][2]=200;
	    iArray[2][3]=0;

	    iArray[3]=new Array();
	    iArray[3][0]="联系电话";             //列名
	    iArray[3][1]="0px";                //列宽
	    iArray[3][2]=200;                   //列最大值
	    iArray[3][3]=3;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[4]=new Array();
	    iArray[4][0]="销售渠道";             //列名
	    iArray[4][1]="40px";                //列宽
	    iArray[4][2]=200;                   //列最大值
	    iArray[4][3]=0;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[5]=new Array();
	    iArray[5][0]="单位性质";             //列名
	    iArray[5][1]="0px";                //列宽
	    iArray[5][2]=200;                   //列最大值
	    iArray[5][3]=3;                      //是否允许输入,1表示允许，0表示不允许

	    iArray[6]=new Array();
	    iArray[6][0]="投保员工人数";             //列名
	    iArray[6][1]="55px";                //列宽
	    iArray[6][2]=200;                   //列最大值
	    iArray[6][3]=0;  

	    iArray[7]=new Array();
	    iArray[7][0]="客户号";             //列名
	    iArray[7][1]="0px";                //列宽
	    iArray[7][2]=200;                   //列最大值
	    iArray[7][3]=0; 
	    
	    iArray[8]=new Array();
	    iArray[8][0]="生效日期";             //列名
	    iArray[8][1]="50px";                //列宽
	    iArray[8][2]=200;                   //列最大值
	    iArray[8][3]=0; 
	    
	    iArray[9]=new Array();
	    iArray[9][0]="总人数";             //列名
	    iArray[9][1]="0px";                //列宽
	    iArray[9][2]=200;                   //列最大值
	    iArray[9][3]=3; 
	    
	    iArray[10]=new Array();
	    iArray[10][0]="总保费";             //列名
	    iArray[10][1]="40px";                //列宽
	    iArray[10][2]=200;                   //列最大值
	    iArray[10][3]=0; 
	    
	    iArray[11]=new Array();
	    iArray[11][0]="总保额";             //列名
	    iArray[11][1]="40px";                //列宽
	    iArray[11][2]=200;                   //列最大值
	    iArray[11][3]=0; 
	    
	    iArray[12]=new Array();
	    iArray[12][0]="出单机构";             //列名
	    iArray[12][1]="40px";                //列宽
	    iArray[12][2]=200;                   //列最大值
	    iArray[12][3]=0; 
	    
	    iArray[13]=new Array();
	    iArray[13][0]="团体保单号";             //列名
	    iArray[13][1]="50px";                //列宽
	    iArray[13][2]=200;                   //列最大值
	    iArray[13][3]=1; 
	    
	    
	    iArray[14]=new Array();
	    iArray[14][0]="投保单号";             //列名
	    iArray[14][1]="0px";                //列宽
	    iArray[14][2]=200;                   //列最大值
	    iArray[14][3]=3; 
	    
	    CheckedGrid = new MulLineEnter("fm", "CheckedGrid");
	    //设置Grid属性
	    CheckedGrid.mulLineCount = 0;
	    CheckedGrid.displayTitle = 1;
	    CheckedGrid.locked = 1;
	    //CheckedGrid.canSel = 1;
	    CheckedGrid.canChk = 1;
	    
	    CheckedGrid.hiddenSubtraction = 1;
	    CheckedGrid.hiddenPlus = 1;
	    CheckedGrid.loadMulLine(iArray);
	  }
	  catch(ex) {
	    alert(ex);
	  }
	}

function initReqGrid() {
	var iArray = new Array();
	try {
		iArray[0]=new Array();
	    iArray[0][0]="序号";                 //列名
	    iArray[0][1]="30px";                 //列名
	    iArray[0][3]=0;                 //列名
		
	    iArray[1]=new Array();
	    iArray[1][0]="申请日期";                 //列名
	    iArray[1][1]="100px";                 //列名
	    iArray[1][2]=200;                        //列最大值
	    iArray[1][3]=0;                 //列名

	    iArray[2]=new Array();
	    iArray[2][0]="申请人";                 //列名
	    iArray[2][1]="60px";                 //列名
	    iArray[2][2]=200;                        //列最大值
	    iArray[2][3]=0;                 //列名

	    iArray[3]=new Array();
	    iArray[3][0]="任务状态";               //列名
	    iArray[3][1]="60px";                //列宽
	    iArray[3][2]=100;                        //列最大值
	    iArray[3][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    iArray[4]=new Array();
	    iArray[4][0]="查询保单";               //列名
	    iArray[4][1]="200px";                //列宽
	    iArray[4][2]=280;                        //列最大值
	    iArray[4][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    //iArray[5]=new Array();
	    //iArray[5][0]="开始日期";               //列名
	    //iArray[5][1]="0px";                //列宽
	    //iArray[5][2]=100;                        //列最大值
	    //iArray[5][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    //iArray[6]=new Array();
	    //iArray[6][0]="结束日期";               //列名
	    //iArray[6][1]="0px";                //列宽
	    //iArray[6][2]=100;                        //列最大值
	    //iArray[6][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    iArray[5]=new Array();
	    iArray[5][0]="任务说明";               //列名
	    iArray[5][1]="180px";                //列宽
	    iArray[5][2]=100;                        //列最大值
	    iArray[5][3]=0;                          //是否允许输入,1表示允许，0表示不允许

	    ReqGrid = new MulLineEnter("fm", "ReqGrid");
	    //设置Grid属性
	    ReqGrid.mulLineCount = 1;
	    ReqGrid.displayTitle = 1;
	    ReqGrid.locked = 1;
	    ReqGrid.canSel = 1;
	    ReqGrid.canChk = 0;
	    
	    ReqGrid.hiddenSubtraction = 1;
	    ReqGrid.hiddenPlus = 1;
	    ReqGrid.loadMulLine(iArray);
	} catch(ex) {
		alert(ex);
	}
}
</script>