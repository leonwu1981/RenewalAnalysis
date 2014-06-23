package test;
/**
 * 复制指定文件名的文件到指定的文件夹中
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SelfCopy {
	public void IOCopy(String path, String path1) {
		File file = new File(path);
		File file1 = new File(path1);
		if (!file.exists()) {
			System.out.println(file.getName() + "文件不存在");
		} else {
			System.out.println("存在");
		}
		
//		byte[] b = new byte[(int) file.length()];
		byte[] b = new byte[1024];
		if (file.isFile()) {
			try {
				System.out.println("file.getName():"+file.getName());
				//复制文件名为指定文件名的文件
				if (file.getName().equals("claim.csv")
						|| file.getName().equals("match.csv")
						|| file.getName().equals("misMatch.csv")
						|| file.getName().equals("predictclaim.xls")
						|| file.getName().equals("predictclaim_ALL.xls")
						|| file.getName().equals("report.xls")) {
					FileInputStream is = new FileInputStream(file);
					FileOutputStream ps = new FileOutputStream(file1);
					while ((is.read(b)) != -1) {
						ps.write(b);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (file.isDirectory()) {
			//文件夹名为IBNR,Incurred-Pay不进行创建
			if(!file.getName().equals("IBNR")&&!file.getName().equals("Incurred-Pay")){
				if (!file1.exists()){
					file1.mkdir();
				}
			}
			String[] list = file.list();
			for (int i = 0; i < list.length; i++) {
				this.IOCopy(path + "/" + list[i], path1 + "/" + list[i]);
			}
		}
	}

	public static void main(String args[]) {
		new SelfCopy().IOCopy("D:\\temp", "C:\\Documents and Settings\\Administrator\\桌面\\to");
	}
}
