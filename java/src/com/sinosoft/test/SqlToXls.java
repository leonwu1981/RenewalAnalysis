    
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
	 * 根据传入的Sql，查询生成Excel
	 * author hm
	 * @param sql  查询语句
	 * @param filePath 生成文件路径
	 * @param fileName 生成文件名称
	 */
	public class SqlToXls {
		private String filePath;
		private String fileName;

		public boolean testForExcel(String sql, String filePath, String fileName) {
			this.filePath = filePath;   //生成文件路径
			this.fileName = fileName;   //生成文件名称
			
			ExeSQL mExeSQL = new ExeSQL();
			SSRS ssrs = mExeSQL.execSQL(sql);
			System.out.println("SQL==" + sql);
			System.out.println("MaxRow=="+ssrs.getMaxRow()+";MaxCol==" + ssrs.getMaxCol());
			
            //表头
			//String[] title = new String[]{"列1","列2"};
			String[] title = new String[ssrs.getMaxCol()];
			for(int j=0;j<ssrs.getMaxCol();j++)
			{
				title[j] = "Col"+j;
			}
			
			
			String[][] strr = new String[ssrs.getMaxRow()][ssrs.getMaxCol()];
			for (int i = 1; i<= ssrs.getMaxRow(); i++) {
				//需要改变某一列的值时，用方式一
				//方式一
//				for(int j=1;j<=ssrs.getMaxCol();j++)
//				{
//					strr[i-1][j-1] = ssrs.GetText(i, j);
//				}
				
				//方式二
				strr[i-1] = ssrs.getRowData(i);

			}

			if (outputToExcel(title, strr, ssrs.getMaxRow(), ssrs.getMaxCol())) {
				return true;
			} else {
				return false;

			}

		}

		/**
		 * 输出excel文件
		 * 
		 * @param title 标题
		 * @param strr  数据
		 * @param rowCount 行数
		 * @param colCount 列数
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
				fontBold.setFontName("宋体");
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
			String sql="select * from llregister where rgtno like '2009%'";  //查询SQL语句
			String filePath="D:\\";   //生成文件路径
			String fileName="test.xls";  //生成文件名称
			tSqlToXls.testForExcel(sql,filePath,fileName);  //根据传入查询语句，查询生成Excel
			System.out.println("处理完成！");
		}

	}

