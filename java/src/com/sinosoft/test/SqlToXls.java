    
    import java.io.BufferedOutputStream;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import org.apache.poi.hssf.usermodel.HSSFCell;
	import org.apache.poi.hssf.usermodel.HSSFCellStyle;
	import org.apache.poi.hssf.usermodel.HSSFFont;
	import org.apache.poi.hssf.usermodel.HSSFRow;
	import org.apache.poi.hssf.usermodel.HSSFSheet;
	import org.apache.poi.hssf.usermodel.HSSFWorkbook;
	import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

	/**
	 * ���ݴ����Sql����ѯ����Excel
	 * author hm
	 * @param sql  ��ѯ���
	 * @param filePath �����ļ�·��
	 * @param fileName �����ļ�����
	 */
	public class SqlToXls {
		private String filePath;
		private String fileName;

		public boolean testForExcel(String sql, String filePath, String fileName) {
			this.filePath = filePath;   //�����ļ�·��
			this.fileName = fileName;   //�����ļ�����
			
			ExeSQL mExeSQL = new ExeSQL();
			SSRS ssrs = mExeSQL.execSQL(sql);
			System.out.println("SQL==" + sql);
			System.out.println("MaxRow=="+ssrs.getMaxRow()+";MaxCol==" + ssrs.getMaxCol());
			
            //��ͷ
			//String[] title = new String[]{"��1","��2"};
			String[] title = new String[ssrs.getMaxCol()];
			for(int j=0;j<ssrs.getMaxCol();j++)
			{
				title[j] = "Col"+j;
			}
			
			
			String[][] strr = new String[ssrs.getMaxRow()][ssrs.getMaxCol()];
			for (int i = 1; i<= ssrs.getMaxRow(); i++) {
				//��Ҫ�ı�ĳһ�е�ֵʱ���÷�ʽһ
				//��ʽһ
//				for(int j=1;j<=ssrs.getMaxCol();j++)
//				{
//					strr[i-1][j-1] = ssrs.GetText(i, j);
//				}
				
				//��ʽ��
				strr[i-1] = ssrs.getRowData(i);

			}

			if (outputToExcel(title, strr, ssrs.getMaxRow(), ssrs.getMaxCol())) {
				return true;
			} else {
				return false;

			}

		}

		/**
		 * ���excel�ļ�
		 * 
		 * @param title ����
		 * @param strr  ����
		 * @param rowCount ����
		 * @param colCount ����
		 */
		private boolean outputToExcel(String[] title, String[][] strr,
				int rowCount, int colCount) {

			File file = new File(filePath + fileName);
			FileOutputStream fStream = null;
			try {
				fStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;

			} 
			BufferedOutputStream bos = new BufferedOutputStream(fStream);

			HSSFWorkbook workbook = new HSSFWorkbook();

			HSSFSheet sheet = workbook.createSheet();

			HSSFRow excelTitle = sheet.createRow(0);

			for (int i = 0; i < title.length; i++) {

				HSSFCell cellTitle0 = excelTitle.createCell((short) i);
				cellTitle0.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellTitle0.setCellValue(title[i]);
				cellTitle0.setCellType(HSSFCell.CELL_TYPE_STRING);
				HSSFFont fontBold = workbook.createFont();
				fontBold.setFontHeightInPoints((short) 10);
				fontBold.setFontName("����");
				fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

				HSSFCellStyle styleBorderBold = workbook.createCellStyle();
				styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
				styleBorderBold.setFont(fontBold);
				cellTitle0.setCellStyle(styleBorderBold);

			}

			for (int i = 0; i < rowCount; i++) {

				HSSFRow row = sheet.createRow(i + 1);
				for (int j = 0; j < colCount; j++) {

					HSSFCell cell = row.createCell((short) j);

					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(strr[i][j]);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}

			}
			try {
				workbook.write(bos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;

			}

			try {
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;

			}
			// Cell cell
			return true;
		}
      
		public static void main(String[] args) {
			SqlToXls tSqlToXls = new SqlToXls();
			String sql="select * from llregister where rgtno like '2009%'";  //��ѯSQL���
			String filePath="D:\\";   //�����ļ�·��
			String fileName="test.xls";  //�����ļ�����
			tSqlToXls.testForExcel(sql,filePath,fileName);  //���ݴ����ѯ��䣬��ѯ����Excel
			System.out.println("������ɣ�");
		}

	}

