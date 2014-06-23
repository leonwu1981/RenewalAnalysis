var turnPage = new turnPageClass();

function queryReqDB( TaskDate, TaskUser ){
	var strSQL=" select FileTitle,CreateDate, FilePath,FileName from RenewalAnalysisFile where TaskDate='"+TaskDate+"' and TaskUser='"+TaskUser+"' and FileType='report' ";
	strSQL += " order by createdate desc "
	turnPage.queryModal(strSQL, ReportGrid, 0, 1);
}

function SaveAs() {
	var tSel = ReportGrid.getSelNo(); 
	if( tSel == 0 || tSel == null ) {	
	    alert( "请先选择一条记录。" );
	    return;
	}
	var tRow = ReportGrid.getSelNo() - 1;
	var filePath = ReportGrid.getRowColData(tRow,3);
	var fileName = ReportGrid.getRowColData(tRow,4);
	var href = realPath + filePath + fileName;
	var a = window.open(href);
	a.document.execCommand('saveas', true);
//	a.window.close();
	
	
	var fileTitle = ReportGrid.getRowColData(tRow,1);
	fileUrl.href = href;
    fileUrl.innerText = "如下载异常请点击此处下载文件：" + fileTitle;
    DivFileDownload.style.display = '';
	return;
}

function returnList() {
	self.location="LLRenewalAnal.jsp?radio=2";
}

function checkAll(str){
	var a = document.getElementsByName(str);    
    var n = a.length;
    for (var i=0; i<n; i++){
    	a[i].checked = window.event.srcElement.checked; 
    }
}

function checkGrpCont(grpContNo){
	checkAll(grpContNo+"_plan");
	var planArr = document.getElementsByName(grpContNo+"_plan");
	for(var j=0;j<planArr.length;j++){
		var planCode = planArr[j].value;
		checkPlan(grpContNo, planCode);
	}
}

function checkPlanAll(){
	var planCode = fm.PlanCode.value;
	doPlanAll(planCode,true);
}
function uncheckPlanAll(){
	var planCode = fm.PlanCode.value;
	doPlanAll(planCode,false);
}
function doPlanAll(planCode,checkFlag){
	var planArr = document.getElementsByTagName("input");
	for(var i=0;i<planArr.length;i++){
		if( endsWith(planArr[i].name, "plan") && endsWith(planArr[i].value, planCode) && startsWith(planArr[i].value, planCode)  ){
			planArr[i].checked=checkFlag;
		}
	}
}

//function endsWith(str, suffix) {     
//	return str.indexOf(suffix, str.length - suffix.length) !== -1; 
//} 
function endsWith(str, suffix) {     
	return str.substr(str.length - suffix.length) === suffix; 
} 
function startsWith(str, prefix) {     
	return str.substr(0, prefix.length) === prefix; 
}

function addGroup(){
	var GroupName = fm.GroupName.value;
	if( GroupName == 0 || GroupName == null ) {	
	    alert( "请填写计划分组名称。" );
	    return;
	}
	if( GroupName.indexOf(":")>=0 || GroupName.indexOf("_")>=0 || GroupName.indexOf(";")>=0 ){
		alert("名称中不能包含 : _ ; 等字符!");
		return;
	}
	
	var str = null;
	var contArr = document.getElementsByName("cont");
	for(var i=0;i<contArr.length;i++){
		var grpContNo = contArr[i].value;
		var planArr = document.getElementsByName(grpContNo+"_plan");
		for(var j=0;j<planArr.length;j++){
			
			if(planArr[j].checked==true){
				var planCode = planArr[j].value;
				if( str == null ){
					str = GroupName + ":" + grpContNo + "_" + planCode;
				} else {
					str += ("," + grpContNo + "_" + planCode);
				}
			}
			
		}
	}
	if( str == null ){
		alert( "没有选择任何计划!" );
		return;
	}
	
	str += ";";
	
	fm.PlanGroups.value += str;
}

function submit1(){
	var reportName = fm.ReportName.value;
	if( reportName == "" || reportName == null ) {	
	    alert( "请填写报表标识!" );
	    return;
	}
	var planGroup = fm.PlanGroups.value;
	if( planGroup == "" || planGroup == null ) {	
	    alert( "没有任何计划分组!" );
	    return;
	}
//	fm.action = "LLRenewalReportSave.jsp?PlanGroup="+planGroup;
//	fm.submit(); //提交 
	submitForm(planGroup);
}

function submit2(){
	var reportName = fm.ReportName.value;
	if( reportName == "" || reportName == null ) {	
	    alert( "请填写报表标识!" );
	    return;
	}

//	fm.action = "LLRenewalReportSave.jsp";
//	fm.submit(); //提交 
	submitForm("");
	
}

function submitForm( planGroup ){
	
	var showStr="正在生成报表，请您稍候并且不要修改屏幕上的值或链接其他页面";
	var urlStr="../common/jsp/MessagePage.jsp?picture=C&content=" + showStr ;  
	showInfo=window.showModelessDialog(urlStr,window,"status:no;help:0;close:0;dialogWidth:550px;dialogHeight:250px");  
	fm.action = "LLRenewalReportSave.jsp?TaskDate="+TaskDate+"&TaskUser="+TaskUser+"&PlanGroup="+planGroup;
	fm.submit(); //提交 
}

//提交后操作,服务器数据返回后执行的操作
function afterSubmit( FlagStr )
{
  showInfo.close();
  if (FlagStr == "Fail" )
  {
    alert("生成报表失败");
  }
  else
  {
	  queryReqDB( TaskDate, TaskUser );
  }
}
