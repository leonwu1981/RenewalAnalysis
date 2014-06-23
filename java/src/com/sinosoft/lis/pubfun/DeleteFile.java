package com.sinosoft.lis.pubfun;

import java.io.File;

public class DeleteFile {

	/**
	 * �����ļ�����������ɾ���ļ�
	 * @param file
	 * @param prefix �ļ���ǰ׺
	 * @param suffix �ļ�����׺
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
						System.out.println("ɾ���ļ��ɹ���"+file.getAbsolutePath());
						return true;
					}else{
						System.out.println("ɾ���ļ�ʧ�ܣ�"+file.getAbsolutePath());
						return false;
					}
				}else {
					System.out.println("���ļ�ǰ׺���ߺ�׺��ƥ�䣺"+file.getAbsolutePath());
					return true;
				}
				
			}
			
		}
		
		System.out.println("���ļ������ڣ�"+file.getAbsolutePath());
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
		//��־�ļ���
		String logs = "logs";
		//��ɾ���¶�ȷ�ϵ���־
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"AutoCalAnnuity_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) ,".log.gz");
		// ɾ���������־
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"AutoClaimBatchPrint_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) ,".log.gz");
		// ɾ��ϵͳ��־
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"SystemOut_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7) ,".log.gz");
		// ɾ��ϵͳ������־
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+logs,
				"SystemErr_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7) ,".log.gz");
		// ɾ�������ļ�
		df.deleteFiles(dfile.getParentFile().getParentFile().getParent()+File.separator+"lis.war.backup",
				"lis_"+_15days_ago.substring(2,4)+_15days_ago.substring(5,7)+_15days_ago.substring(8,10) ,".tar.gz");
		System.out.println("AutoCalAnnuity_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7));
		System.out.println("AutoClaimBatchPrint_"+twomonth_ago.substring(2,4)+twomonth_ago.substring(5,7) );
		System.out.println("SystemOut_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7));
		System.out.println("SystemErr_"+twomonth_ago.substring(2,4)+"."+twomonth_ago.substring(5,7));
		System.out.println("lis_"+_15days_ago.substring(2,4)+_15days_ago.substring(5,7)+_15days_ago.substring(8,10));
		
	}
	
}
