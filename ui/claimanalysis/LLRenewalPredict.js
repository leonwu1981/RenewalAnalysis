var turnPage = new turnPageClass();

function queryReqDB(){
	var strSQL=" select FileTitle,CreateDate,Status,FilePath,FileName from RenewalAnalysisFile where TaskDate='"+TaskDate+"' and TaskUser='"+TaskUser+"' and FileType='predict' order by CreateDate desc ";
	turnPage.queryModal(strSQL, ReportGrid, 0, 1);
}

function SaveAs() {
	var tSel = ReportGrid.getSelNo(); 
	if( tSel == 0 || tSel == null ) {	
	    alert( "请先选择一条记录。" );
	    return;
	}
	var tRow = ReportGrid.getSelNo() - 1;
	var status = ReportGrid.getRowColData(tRow,3);
	
	if(status!="生成成功"){
		alert("文件还未生成成功");
		return;
	}
	
	var filePath = ReportGrid.getRowColData(tRow,4);
	var fileName = ReportGrid.getRowColData(tRow,5);
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

function checkPlan(grpContNo, planCode){
	checkAll(grpContNo+"_"+planCode+"_duty");
}
function checkDutyAll(){
	var riskCode = fm.RiskCode.value;
	var dutyCode = fm.DutyCode.value;
	doDutyAll(riskCode,dutyCode,true);
}
function uncheckDutyAll(){
	var riskCode = fm.RiskCode.value;
	var dutyCode = fm.DutyCode.value;
	doDutyAll(riskCode,dutyCode,false);
}
function doDutyAll(riskCode,dutyCode,checkFlag){
	var dutyArr = document.getElementsByTagName("input");
	for(var i=0;i<dutyArr.length;i++){
		if( endsWith(dutyArr[i].name, "duty") && endsWith(dutyArr[i].value, dutyCode) && startsWith(dutyArr[i].value, riskCode)  ){
			dutyArr[i].checked=checkFlag;
		}
	}
}

function isMedicalDuty( duty ){
	return ( startsWith(duty, "NIK01") || startsWith(duty, "NIK02") || startsWith(duty, "NIK12") || startsWith(duty, "NIK03") || startsWith(duty, "NIK07") || startsWith(duty, "NIK08") || startsWith(duty, "NIK09") || startsWith(duty, "MIK01") || startsWith(duty, "MOK01") || startsWith(duty, "MKK01") );
}

function isIpDuty( duty ){
	return ( endsWith(duty, "615001") || endsWith(duty, "617001") || endsWith(duty, "612001") || endsWith(duty, "637001") || endsWith(duty, "641001") || endsWith(duty, "642001") );
}

function isOpDuty( duty ){
	return ( endsWith(duty, "615002") || endsWith(duty, "617002") || endsWith(duty, "612002") || endsWith(duty, "637006") || endsWith(duty, "641004") || endsWith(duty, "642003") );
}

function checkMedicalDuty(){
	var dutyArr = document.getElementsByTagName("input");
	for(var i=0;i<dutyArr.length;i++){
		if( endsWith(dutyArr[i].name, "duty") ){
			if( isMedicalDuty( dutyArr[i].value ) ){
				dutyArr[i].checked=true;
			}else{
				dutyArr[i].checked=false;
			}
		}
	}
}

function checkIpDuty(){
	var dutyArr = document.getElementsByTagName("input");
	for(var i=0;i<dutyArr.length;i++){
		if( endsWith(dutyArr[i].name, "duty") ){
			if( isIpDuty( dutyArr[i].value ) ){
				dutyArr[i].checked=true;
			}else{
				dutyArr[i].checked=false;
			}
		}
	}
}

function checkOpDuty(){
	var dutyArr = document.getElementsByTagName("input");
	for(var i=0;i<dutyArr.length;i++){
		if( endsWith(dutyArr[i].name, "duty") ){
			if( isOpDuty( dutyArr[i].value ) ){
				dutyArr[i].checked=true;
			}else{
				dutyArr[i].checked=false;
			}
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
function returnList() {
	self.location="LLRenewalAnal.jsp?radio=2";
}

function predict(){
	var predictType = "";
	var typeArr = document.getElementsByName("PredictType");
	for(var i=0;i<typeArr.length;i++){
		if(typeArr[i].checked==true){
			predictType += typeArr[i].value;
		}
	}
	if(predictType==""){
		alert("请选择预测理赔数据汇总方式!");
		return;
	}
	if(fm.FileTitle.value==""){
		alert("请填写说明!");
		return;
	}

	var str = "";
	var contArr = document.getElementsByName("cont");
	for(var i=0;i<contArr.length;i++){
		var grpContNo = contArr[i].value;
		var planArr = document.getElementsByName(grpContNo+"_plan");
		for(var j=0;j<planArr.length;j++){
			var planCode = planArr[j].value;
			var dutyArr = document.getElementsByName(grpContNo+"_"+planCode+"_duty");
			for(var k=0;k<dutyArr.length;k++){
				var duty = dutyArr[k].value;
				if(dutyArr[k].checked==true){
					s = grpContNo+"_"+planCode+"_"+duty;
					if(str==""){
						str = s;
					} else {
						str += (";"+s);
					}
					contArr[i].checked = true;
					planArr[j].checked = true;
				}
				
			}
		}
	}
	if(str==""){
		alert("请选择需要预测的责任!");
		return;
	}
	
	var strEmail = fm.Email.value;
	if (strEmail.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) == -1){
		alert("邮箱格式不正确!");
		return;
	}
	
	fm.action = "LLRenewalPredictSave.jsp?PredictType="+predictType;
	fm.submit(); //提交 
	alert("任务已开始, 结束后会向您的邮箱发送通知邮件");
	
	
}
