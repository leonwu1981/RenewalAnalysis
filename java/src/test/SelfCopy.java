package test;
/**
 * ����ָ���ļ������ļ���ָ�����ļ�����
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SelfCopy {
	public void IOCopy(String path, String path1) {
		File file = new File(path);
		File file1 = new File(path1);
		if (!file.exists()) {
			System.out.println(file.getName() + "�ļ�������");
		} else {
			System.out.println("����");
		}
		
//		byte[] b = new byte[(int) file.length()];
		byte[] b = new byte[1024];
		if (file.isFile()) {
			try {
				System.out.println("file.getName():"+file.getName());
				//�����ļ���Ϊָ���ļ������ļ�
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
			//�ļ�����ΪIBNR,Incurred-Pay�����д���
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
		new SelfCopy().IOCopy("D:\\temp", "C:\\Documents and Settings\\Administrator\\����\\to");
	}
}
