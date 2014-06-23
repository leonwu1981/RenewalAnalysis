package com.sinosoft.lis.pubfun;

import java.io.File;

public class DeleteFile {

	/**
	 * 根据文件名称来级联删除文件
	 * @param file
	 * @param prefix 文件名前缀
	 * @param suffix 文件名后缀
	 * @return
	 */
	public boolean deleteFiles(File file,String prefix,String suffix){
		System.out.println(file.getName()+":"+prefix+"***"+suffix);
		if(file.exists()){
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for(int i=0;i<files.length;i++){
					if(!deleteFiles(files[i],prefix,suffix)){
						return false;
					}
				}
				return true;
			} else{
				String filename = file.getName();
				if(filename.startsWith(prefix)&&filename.endsWith(suffix)){
					if(file.delete()){
						System.out.println("删除文件成功："+file.getAbsolutePath());
						return true;
					}else{
						System.out.println("删除文件失败："+file.getAbsolutePath());
						return false;
					}
				}else {
					System.out.println("此文件前缀或者后缀不匹配："+file.getAbsolutePath());
					return true;
				}
				
			}
			
		}
		
		System.out.println("此文件不存在："+file.getAbsolutePath());
		return true;
		
	}
	
	public boolean deleteFiles(File file,String prefix){
		return deleteFiles(file,prefix,"");
	}
	
	public boolean deleteFiles(File file){
		return deleteFiles(file,"","");
	}
	
	public boolean deleteFiles(String filestr,String prefix,String suffix){
		File file =new File(filestr);
		return deleteFiles(file,prefix,suffix);
	}
	
	public boolean deleteFiles(String filestr,String prefix){
		File file =new File(filestr);
		return deleteFiles(file,prefix,"");
	}
	
	public boolean deleteFiles(String filestr){
		File file =new File(filestr);
		return deleteFiles(file,"","");
	}
	
	public File getclassespath(){
		return new File(DeleteFile.class.getResource("/").getFile());
	}
	
	public static void main(String[] args){
		DeleteFile df = new DeleteFile();
		System.out.println(df.getclassespath());
		File dfile = df.getclassespath();
		String today = PubFun.getCurrentDate();
		String twomonth_ago = PubFun.calDate(today, -2, "M", "");
		String _15days_ago =  PubFun.calDate(today, -15, "D", "");
		System.out.println(twomonth_ago+"--------"+_15days_ago);
		//日志文件夹
		String logs = "logs";
		//　删除月度确认的日志
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"AutoCalAnnuity_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) ,".log.gz");
		// 删除理赔的日志
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"AutoClaimBatchPrint_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) ,".log.gz");
		// 删除系统日志
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"SystemOut_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7) ,".log.gz");
		// 删除系统错误日志
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"SystemErr_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7) ,".log.gz");
		// 删除备份文件
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+"lis.war.backup",
				"lis_"+_15days_ago.substring(2,4)+_15days_ago.substring(5,7)+_15days_ago.substring(8,10) ,".tar.gz");
		System.out.println("AutoCalAnnuity_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7));
		System.out.println("AutoClaimBatchPrint_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) );
		System.out.println("SystemOut_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7));
		System.out.println("SystemErr_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7));
		System.out.println("lis_"+_15days_ago.substring(2,4)+_15days_ago.substring(5,7)+_15days_ago.substring(8,10));
		
	}
	
}
