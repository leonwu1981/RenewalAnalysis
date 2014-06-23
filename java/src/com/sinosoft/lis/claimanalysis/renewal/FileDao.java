package com.sinosoft.lis.claimanalysis.renewal;

import com.sinosoft.utility.ExeSQL;

public class FileDao {
	public boolean addFile(String filePath, String fileName, String fileTitle, String mTaskDate, String operator, String reportType, String status, String createDate){
		String sql = "insert into RenewalAnalysisFile( TaskDate, TaskUser, FilePath, FileName, FileTitle, FileType, Status, CreateDate ) values ( ";
		sql += "'" + mTaskDate + "', ";
		sql += "'" + operator +"', ";
		sql += "'" + filePath +"', ";
		sql += "'" + fileName +"', ";
		sql += "'" + fileTitle +"', ";
		sql += "'"+reportType+"', ";
		sql += "'"+status+"', ";
		sql += "'"+createDate+"' ";
		sql += " )";
		ExeSQL exeSQL = new ExeSQL();
		return exeSQL.execUpdateSQL(sql);
	}
	
	public boolean updateFileStatus(String taskDate, String taskUser, String reportType, String fileName, String status, String createDate){
		String sql = "update RenewalAnalysisFile set Status='"+status+"',CreateDate='"+createDate+"' where TaskDate='"+taskDate+"' and TaskUser='"+taskUser+"' and FileType='"+reportType+"' and FileName='"+fileName+"' ";
		ExeSQL exeSQL = new ExeSQL();
		return exeSQL.execUpdateSQL(sql);
	}
}
