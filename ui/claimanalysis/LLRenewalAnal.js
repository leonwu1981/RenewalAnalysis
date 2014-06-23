var turnPage = new turnPageClass();
var turnPage2 = new turnPageClass();
var turnPage3 = new turnPageClass();
var turnPage4 = new turnPageClass();
var turnPage5 = new turnPageClass();
var turnPage6 = new turnPageClass();
var turnPage7 = new turnPageClass();
var turnPage8 = new turnPageClass();
var arrChecked = new Array();
var arrChecked_length = 0;

function verifyDate(){
	var s1 = fm.StartDate.value;
	if( s1 == null || s1 == "" ){
		d1 = new Date( Date.parse("2005/01/01") );
		//alert( "请选择开始时间！" );
		//return false;
	}else{
		d1 = new Date( Date.parse( s1.replace(/-/g, "/") ) );
	}

	var s2 = fm.EndDate.value;
	if( s2 == null || s2 == "" ){
		d2 = new Date();
		//alert( "请选择结束时间！" );
		//return false;
	}else{
		d2 = new Date( Date.parse( s2.replace(/-/g, "/") ) );
	}

	if ( d1 > d2 ){
		alert( "开始时间在结束时间之后！" );
		return false;
	}
	return true;
}

function addTask()
{
	if(arrChecked_length == 0){
		alert("至少选择一个团体保单！");
        return false;
	} else {
//		if ( verifyDate() == false ){
//			return false;
//		}
		
		var strEmail = fm.Email.value;
		if (strEmail.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) == -1){
			alert("邮箱格式不正确!");
			return false;
		}
		
		var taskComment = fm.TaskComment.value;
		if( taskComment=="" ){
			alert("请填写任务说明，以便您的查找");
			return false;
		}
		
		// GrpContNo 用  逗号 分隔后，以字符串格式传递   
		var checkedAsAString = arrChecked.join(",");
		fm.action = "LLRenewalAnalSave.jsp?GrpContNo="+checkedAsAString;
		fm.submit(); //提交 
		alert("任务已开始, 结束后会向您的邮箱发送通知邮件");
		changeQueryType( 2 );
		return true;
	}
}

function chooseQueryType()
{
  var chooseType="";
    for(i = 0; i <fm.QueryType.length; i++){
      if(fm.QueryType[i].checked){
        chooseType=fm.QueryType[i].value;
        break;
      }
    }
    
    changeDiv( chooseType );
}

function changeQueryType( j ){
	for(i = 0; i <fm.QueryType.length; i++){
		if( i == (j-1) ){
			fm.QueryType[i].checked = true;
	    } else {
	    	fm.QueryType[i].checked = false;
	    }
	}
	var chooseType = j.toString();
	changeDiv( chooseType );
}

function changeDiv( chooseType ){
	
	switch(chooseType){
    case "1":
      divQueryType1.style.display="";
      divQueryType2.style.display="none";
      break;
    case "2":
      divQueryType1.style.display="none";
      divQueryType2.style.display="";
      refreshReq();
      break;  
  }  
}


//选择团体保单
function searchGrpCont() {
	
	var PrintNo="";//印刷号
	var GrpContNo="";//保单号
	if(fm.all("PrintNo").value!="")
	{ 
		PrintNo=" and c.PrtNo='"+fm.all("PrintNo").value+"'";
    }
    if(fm.all("ContNo").value!="")
    {
        GrpContNo=" and c.grpcontno ='"+fm.all("ContNo").value+"'";
    }
    var GrpName="";
    if(fm.all("GrpName").value!="")
    {
        GrpName=" and c.GrpName like '%"+fm.all("GrpName").value+"%'";
    }
    var tCValidate="";
    if(fm.all("CValiDateStart").value!="" && fm.all("CValiDateEnd").value=="")
    {
  	    tCValidate=" and c.CValiDate>= to_date('"+ fm.all("CValiDateStart").value+"','yyyy-mm-dd')";
    }
    if(fm.all("CValiDateEnd").value!="" && fm.all("CValiDateStart").value=="")
    {
  	    tCValidate=" and c.CValiDate<= to_date('"+ fm.all("CValiDateEnd").value+"','yyyy-mm-dd')";
    }
    if(fm.all("CValiDateStart").value!="" && fm.all("CValiDateEnd").value!="")
    {
        tCValidate=" and c.CValiDate>= to_date('"+ fm.all("CValiDateStart").value+"','yyyy-mm-dd')"
           +" and c.CValiDate<= to_date('"+ fm.all("CValiDateEnd").value+"','yyyy-mm-dd')";
    }
    if(PrintNo=="" && GrpContNo=="" && GrpName=="" && tCValidate==""){
        alert("必须至少输入一个查询条件才能进行查询，谢谢！");
        return false;
    }
    queryGrpContDB( PrintNo+GrpContNo+GrpName+tCValidate, turnPage7, CustomGrid );
    if( CustomGrid.mulLineCount == 0 ){
        alert("查询结果为空!");
        return;
    }
}
//选择团体保单
function checkGrpCont() {
	var GrpContNo="";//保单号
    var tmpArr = new Array();
    var n = 0;
    for (i=0; i<CustomGrid.mulLineCount; i++)
	{
		if (CustomGrid.getChkNo(i))
		{
			tmpArr[n] = CustomGrid.getRowColData(i, 13);
			n++;
			if(GrpContNo==""){
				GrpContNo = "'" + CustomGrid.getRowColData(i, 13) + "'";
			} else {
				GrpContNo = GrpContNo + ", '" + CustomGrid.getRowColData(i, 13) + "'";
			}
		}
	}
    
    if(GrpContNo==""){
        alert("至少选择一个团体保单！");
        return false;
    }
    var m = n;
    for (i=0; i<arrChecked_length; i++ ){
    	flag = 0;
    	for(j=0; j<n; j++){
    		if( arrChecked[i] == tmpArr[j] ) {
    			flag = 1;
    			break;
    		}
    	}
    	if(flag == 0){
    		GrpContNo = GrpContNo + ", '" + arrChecked[i] + "'";
    		tmpArr[m] = arrChecked[i];
    		m++;
    	}
    }
    if( m > arrChecked_length ){
    	arrChecked = tmpArr;
        arrChecked_length = m;
        GrpContNo = " and c.grpcontno in ( " +  GrpContNo + ") ";
        queryGrpContDB( GrpContNo, turnPage2, CheckedGrid );
    }
    
}
// 选择团体保单
function uncheckGrpCont() {
	var GrpContNo="";//保单号
	var unchkArr = new Array();
    var n = 0;
    var m = 0;
    for (i=0; i<CheckedGrid.mulLineCount; i++)
	{
		if (CheckedGrid.getChkNo(i))
		{
			unchkArr[n] = CheckedGrid.getRowColData(i, 13);
			n++;
		}
	}
    if( n == 0 ){
        alert("至少选择一个团体保单！");
        return false;
    }
    var tmpArr = new Array();
    for (i=0; i<arrChecked_length; i++ )
    {
    	flag = 0;
    	for(j=0; j<n; j++){
    		if( arrChecked[i] == unchkArr[j] ) {
    			flag = 1;
    			break;
    		}
    	}
    	if(flag == 0){
    		tmpArr[m] = arrChecked[i];
    		m++;
    		if(GrpContNo==""){
				GrpContNo = "'" + arrChecked[i] + "'";
			} else {
				GrpContNo = GrpContNo + ", '" + arrChecked[i] + "'";
			}
    	}
    }
    arrChecked = tmpArr;
    arrChecked_length = m;
    GrpContNo = " and c.grpcontno in ( " +  GrpContNo + ") ";
    queryGrpContDB( GrpContNo, turnPage2, CheckedGrid );
}

function queryGrpContDB( Condition, GrpContPage, GrpContGrid )
{ 
    var strSQL=" select c.grpname,a.linkman1,a.phone1,"
          +"  (select codename from ldcode where codetype='agenttype' and code=c.salechnl), "
          +"(select e.codename from ldcode e where e.codetype='grpnature' and e.code=c.GrpNature),"
          +" (select count(1) from lccont where poltype not in ('2','5')  and appflag<>'2'  and grpcontno=c.grpcontno)"
          +", a.CustomerNo ,c.CValiDate,c.Peoples2,"
          +" (SELECT "
          +"   (SELECT nvl(SUM(p.sumprem),0) FROM lcpol p  WHERE GrpContNo=c.grpcontno ) + (SELECT nvl(SUM(p.sumprem),0) FROM lbpol p  WHERE GrpContNo=c.grpcontno ) "
          +"   +"
          +"   (select nvl(sum(d.getmoney),0) from ljagetendorse d where d.grpcontno=c.grpcontno"
          +"   and d.FeeOperationtype in ('ZT','HT') and d.operator not like 'GRP%' and d.endorsementno not like '9046%')"
          +" FROM dual) ,"
          +" c.Amnt,c.SignCom ,c.grpcontno,c.prtno"
          +" from LCGrpCont c,LcgrpAddress a"
          +" where   c.appflag='1' and c.GrpGroupNo IS NULL  and  c.AppntNo=a.CustomerNo and a.addressno=c.addressno "
          +" and c.ManageCom LIKE '"+manageCom+"%'"
          +Condition;

     strSQL +=" union select  distinct c.grpname,a.linkman1,a.phone1,"
            +"  (select codename from ldcode where codetype='agenttype' and code=c.salechnl), "
            +"  (select e.codename from ldcode e where e.codetype='grpnature' and e.code=c.GrpNature),"
            +" (select count(1) from lccont where poltype not in ('2','5')  and appflag<>'2'  and grpcontno=c.grpcontno)"
            +" , a.CustomerNo ,c.CValiDate,c.Peoples2,"
            +" (SELECT "
            +"   (SELECT nvl(SUM(p.sumprem),0) FROM lcpol p  WHERE GrpContNo=c.grpcontno ) + (SELECT nvl(SUM(p.sumprem),0) FROM lbpol p  WHERE GrpContNo=c.grpcontno ) "
            +"   +"
            +"   (select nvl(sum(d.getmoney),0) from ljagetendorse d where d.grpcontno=c.grpcontno"
            +"   and d.FeeOperationtype in ('ZT','HT') and d.operator not like 'GRP%' and d.endorsementno not like '9046%')"
            +" FROM dual) ,"
            +" c.Amnt,c.SignCom,c.grpcontno,c.prtno "
            +"  from LBGrpCont c,LcgrpAddress a where c.AppntNo=a.CustomerNo and a.addressno=c.addressno  "
            +" and exists (select k.grpcontno from LCGeneralToRisk k where k.GrpPolNo='666666' and k.ExecuteCom  like '"
            + manageCom+"%' and c.GrpContNo=k.grpcontno and c.PrtNo=k.PrtNo  ) "
            +Condition;    

     strSQL +=" union select  distinct c.grpname,a.linkman1,a.phone1,"
            +" (select codename from ldcode where codetype='agenttype' and code=c.salechnl), "
            +" (select e.codename from ldcode e where e.codetype='grpnature' and e.code=c.GrpNature),"
            +" (select count(1) from lccont where poltype not in ('2','5')  and appflag<>'2'  and grpcontno=c.grpcontno)"
            +", a.CustomerNo ,c.CValiDate,c.Peoples2,"
            +" (SELECT "
            +"   (SELECT nvl(SUM(p.sumprem),0) FROM lcpol p  WHERE GrpContNo=c.grpcontno ) + (SELECT nvl(SUM(p.sumprem),0) FROM lbpol p  WHERE GrpContNo=c.grpcontno ) "
            +"   +"
            +"   (select nvl(sum(d.getmoney),0) from ljagetendorse d where d.grpcontno=c.grpcontno"
            +"   and d.FeeOperationtype in ('ZT','HT') and d.operator not like 'GRP%' and d.endorsementno not like '9046%')"
            +" FROM dual) ,"
            +" c.Amnt,c.SignCom ,c.grpcontno,c.prtno"
            +" from LBGrpCont c,LcgrpAddress a where c.AppntNo=a.CustomerNo and a.addressno=c.addressno  "
            +" AND c.ManageCom LIKE '"+manageCom+"%' "    
            +Condition;
            
     strSQL +=" union  select   distinct c.grpname,a.linkman1,a.phone1,"
            +"  (select codename from ldcode where codetype='agenttype' and code=c.salechnl),"
            +" (select e.codename from ldcode e where e.codetype='grpnature' and e.code=c.GrpNature),"
            +" (select count(1) from lccont where poltype not in ('2','5')  and appflag<>'2'  and grpcontno=c.grpcontno)"
            +", a.CustomerNo ,c.CValiDate,c.Peoples2,"
            +" (SELECT "
            +"   (SELECT nvl(SUM(p.sumprem),0) FROM lcpol p  WHERE GrpContNo=c.grpcontno ) + (SELECT nvl(SUM(p.sumprem),0) FROM lbpol p  WHERE GrpContNo=c.grpcontno ) "
            +"   +"
            +"   (select nvl(sum(d.getmoney),0) from ljagetendorse d where d.grpcontno=c.grpcontno"
            +"   and d.FeeOperationtype in ('ZT','HT') and d.operator not like 'GRP%' and d.endorsementno not like '9046%')"
            +" FROM dual) ,"
            +" c.Amnt,c.SignCom ,c.grpcontno,c.prtno"
            +" from LCGrpCont c,LcgrpAddress a where c.AppntNo=a.CustomerNo and a.addressno=c.addressno and  c.appflag='1' and c.GrpGroupNo IS NULL   "
            +" AND exists (select '' from LCGeneralToRisk k where k.GrpPolNo='666666' and k.ExecuteCom  like '"+manageCom+"%'"
            +"and c.GrpContNo=k.grpcontno and c.PrtNo=k.PrtNo  )"
            +Condition;
            
     GrpContPage.queryModal(strSQL, GrpContGrid, 0, 1);
     
     prewarningNameSet();
     
}

function queryReqDB( TaskUser, Status ){
	var strSQL=" select TaskDate, TaskUser, TaskStatus, GrpContNo, TaskComment from RenewalAnalysisTask ";
	var whereStr = "";
	if( TaskUser != null && !"".equals(TaskUser) ){
		whereStr += " where TaskUser='"+ TaskUser +"' "
	}
	if( Status != null && !"".equals(Status) ){
		if( "".equals(whereStr) ){
			whereStr += " where TaskStatus='"+ Status +"' "
		} else {
			whereStr += " and TaskStatus='"+ Status +"' "
		}
	}
	strSQL += whereStr;
	strSQL += " order by TaskDate desc "
	turnPage3.queryModal(strSQL, ReqGrid, 0, 1);
}

function refreshReq(){
	
	queryReqDB();
}

function ReportDetail() {
	var tSel = ReqGrid.getSelNo();
	if( tSel == 0 || tSel == null ) {
		alert( "请先选择一个任务!" );
	    return;
	}
	// status
	TaskStatus = ReqGrid.getRowColData(tSel - 1, 3);
	
	if( TaskStatus == "待处理" || TaskStatus == "正处理" ) {
		alert( "任务未完成，请等待！" );
	    return;
	}
//	if( TaskStatus == "下载数据失败" ) {
//		alert( "下载数据失败，请重建任务或联系IT人员！" );
//	    return;
//	}
//	if( TaskStatus == "生成报表失败" ) {
//		alert( "部分报表生成失败！" );
//	    //return;
//	}
	//
	TaskDate = ReqGrid.getRowColData(tSel - 1, 1);
	TaskUser = ReqGrid.getRowColData(tSel - 1, 2);
	GrpContNo = ReqGrid.getRowColData(tSel - 1, 4);
	//StartDate = ReqGrid.getRowColData(tSel - 1, 5);
	//EndDate = ReqGrid.getRowColData(tSel - 1, 6);
	//TaskComment = ReqGrid.getRowColData(tSel - 1, 5);
	//self.location="LLRenewalReport.jsp?TaskDate="+TaskDate+"&TaskUser="+TaskUser+"&GrpContNo="+GrpContNo+"&StartDate="+StartDate+"&EndDate="+EndDate+"&TaskComment="+TaskComment;
	self.location="LLRenewalReport.jsp?TaskDate="+TaskDate+"&TaskUser="+TaskUser+"&GrpContNo="+GrpContNo;
	
}

function PredictDetail() {
	var tSel = ReqGrid.getSelNo();
	if( tSel == 0 || tSel == null ) {
		alert( "请先选择一个任务!" );
	    return;
	}
	// status
	TaskStatus = ReqGrid.getRowColData(tSel - 1, 3);
	
	if( TaskStatus == "处理中" ) {
		alert( "任务处理中，请等待！" );
	    return;
	}
	if( TaskStatus == "下载数据失败" ) {
		alert( "下载数据失败，请重建任务或联系IT人员！" );
	    return;
	}
	//
	TaskDate = ReqGrid.getRowColData(tSel - 1, 1);
	TaskUser = ReqGrid.getRowColData(tSel - 1, 2);
	GrpContNo = ReqGrid.getRowColData(tSel - 1, 4);
	//StartDate = ReqGrid.getRowColData(tSel - 1, 5);
	//EndDate = ReqGrid.getRowColData(tSel - 1, 6);
	//TaskComment = ReqGrid.getRowColData(tSel - 1, 5);
	//self.location="LLRenewalPredict.jsp?TaskDate="+TaskDate+"&TaskUser="+TaskUser+"&GrpContNo="+GrpContNo+"&StartDate="+StartDate+"&EndDate="+EndDate+"&TaskComment="+TaskComment;
	self.location="LLRenewalPredict.jsp?TaskDate="+TaskDate+"&TaskUser="+TaskUser+"&GrpContNo="+GrpContNo;

}


//预警客户名称变红
 function prewarningNameSet()
 {
     //预警客户名称变红
    var multCount = CustomGrid.mulLineCount;
    for(var i=0; i<multCount; i++)
    {
    	var tCustomerNo = CustomGrid.getRowColData(i,7);
    	if(tCustomerNo !="")
    	{
    	   	var arrPrew = easyExecSql("select Prewarningflag('"+tCustomerNo+"','2') from dual",'','','','','1'); //返回1变红，2变蓝，0不变
			if (arrPrew!=null && arrPrew[0][0]!="")
			{
				if(arrPrew[0][0]=="1")
				{
					if(i==0)
           				setRowColColor(CustomGrid,'0','1','#FF0000'); //参数：Grid名称，行号从0开始，列号从0开始，颜色代码
           			else
    	   				setRowColColor(CustomGrid,i,'1','#FF0000'); //参数：Grid名称，行号从0开始，列号从0开始，颜色代码
				}
			}
    	}
    }
 }
 