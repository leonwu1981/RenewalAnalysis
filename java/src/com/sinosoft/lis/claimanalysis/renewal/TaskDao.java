package com.sinosoft.lis.claimanalysis.renewal;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class TaskDao {
	public boolean addTask(String mTaskDate, String operator, String mGrpContNo, String mStartDate, String mEndDate, String mTaskComment, String mEmail, String mManageCom){
		
		
		String sql = "insert into RenewalAnalysisTask( TaskDate, TaskUser, TaskStatus, GrpContNo, StartDate, EndDate, TaskComment, EmailAddress, ManageCom ) values ( ";
		sql += "'" + mTaskDate + "', ";
		sql += "'" + operator +"', ";
		sql += "'待处理', ";
		sql += "'" + mGrpContNo +"', ";
		sql += "'" + mStartDate +"', ";
		sql += "'" + mEndDate +"', ";
		sql += "'" + mTaskComment +"', ";
		sql += "'" + mEmail +"', ";
		sql += "'" + mManageCom +"' ";
		sql += " )";
		ExeSQL exeSQL = new ExeSQL();
		return exeSQL.execUpdateSQL(sql);
	}
	public boolean updateTaskStatus(String date, String user, String status){
		String sql = "update RenewalAnalysisTask set TaskStatus = '"+status+"' where TaskDate = '"+date+"' and TaskUser = '"+user+"'";
		ExeSQL exeSQL = new ExeSQL();
		return exeSQL.execUpdateSQL(sql);
	}
	public boolean hasTaskExecuting(){
		String sql = "select 1 from RenewalAnalysisTask where TaskStatus='正处理'";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql);
		if( ssrs.getMaxRow()>0 ){
			return true;
		}
		return false;
	}
	
	public String[] getOneTask(){
		String sql = "select * from RenewalAnalysisTask where TaskStatus='待处理' order by taskdate ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql);
		if( ssrs.getMaxRow()<=0 ){
			return null;
		}
		String[] s = ssrs.getRowData(1);
		String date = s[0];
		String user = s[1];
		this.updateTaskStatus(date, user, "正处理");
		return s;
	}
	
}
