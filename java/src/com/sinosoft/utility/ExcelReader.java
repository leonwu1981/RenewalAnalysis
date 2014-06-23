package com.sinosoft.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * ��Excel�ļ�����
 * 
 * @author hanming
 * 
 */
public class ExcelReader {
	private HSSFWorkbook wb = null;// book [includes sheet]

	private HSSFSheet sheet = null;

	private HSSFRow row = null;

	private int sheetNum = 0; // ��sheetnum��������

	private int rowNum = 0;

	private FileInputStream fis = null;

	private File file = null;

	public ExcelReader() {
	}

	public ExcelReader(File file) {
		this.file = file;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public void setSheetNum(int sheetNum) {
		this.sheetNum = sheetNum;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * ��ȡexcel�ļ����HSSFWorkbook����
	 */
	public void open() throws IOException {
		fis = new FileInputStream(file);
		wb = new HSSFWorkbook(new POIFSFileSystem(fis));
		fis.close();
	}

	/**
	 * ����sheet����Ŀ
	 * 
	 * @return int
	 */
	public int getSheetCount() {
		int sheetCount = -1;
		sheetCount = wb.getNumberOfSheets();
		return sheetCount;
	}
	/**
	 * ����sheet�������
	 * 
	 * @return int
	 */
	public String getSheetName() {
		if (wb == null)
			System.out.println("=============>WorkBookΪ��");
		return wb.getSheetName(this.sheetNum);
	}
	/**
	 * ����sheet�������
	 * 
	 * @return int
	 */
	public String getSheetName(int sheetNum) {
		if (wb == null)
			System.out.println("=============>WorkBookΪ��");
		return wb.getSheetName(sheetNum);
	}

	/**
	 * sheetNum�µļ�¼����
	 * 
	 * @return int
	 */
	public int getRowCount() {
		if (wb == null)
			System.out.println("=============>WorkBookΪ��");
		HSSFSheet sheet = wb.getSheetAt(this.sheetNum);
		int rowCount = -1;
		rowCount = sheet.getLastRowNum();
		for (int i = rowCount; i >= 1; i--) {// ѭ��ÿһ��
			HSSFRow row = sheet.getRow(i);// ������
			if (row == null) {
				rowCount--;
				continue;
			}
			HSSFCell cell = row.getCell((short) 0);// �������е�һ����Ԫ��
			HSSFCell cell1 = row.getCell((short) 1);// �������еĶ�����Ԫ��
			String str = getStringCellValue(cell);
			String str1 = getStringCellValue(cell1);
			if ((str == null || str.equals(""))&& (str1 == null || str1.equals(""))) {
				rowCount--;
			} else {
				break;
			}
		}
		return rowCount;
	}

	/**
	 * ��ȡָ��sheetNum��rowCount
	 * 
	 * @param sheetNum
	 * @return int
	 */
	public int getRowCount(int sheetNum) {
		HSSFSheet sheet = wb.getSheetAt(sheetNum);
		int rowCount = -1;
		rowCount = sheet.getLastRowNum();
		for (int i = rowCount; i >= 1; i--) {// ѭ��ÿһ��
			HSSFRow row = sheet.getRow(i);// ������
			if (row == null) {
				rowCount--;
				continue;
			}
			HSSFCell cell = row.getCell((short) 0);// �������е�һ����Ԫ��
			HSSFCell cell1 = row.getCell((short) 1);// �������еĶ�����Ԫ��
			String str = getStringCellValue(cell);
			String str1 = getStringCellValue(cell1);
			if ((str == null || str.equals(""))&& (str1 == null || str1.equals(""))) {
				rowCount--;
			} else {
				break;
			}
		}
		return rowCount;
	}

	/**   
	 * ��ȡ��Ԫ����������Ϊ�ַ������͵�����   
	 * @param cell Excel��Ԫ��   
	 * @return String ��Ԫ����������   
	 */
	private String getStringCellValue(HSSFCell cell) {
		if (cell == null) {
			return "";
		}
		String strCell = "";
		switch (cell.getCellType()) {
		// ��Ԫ������Ϊ����
		case HSSFCell.CELL_TYPE_NUMERIC:
			strCell = String.valueOf(cell.getNumericCellValue());
			break;
		// ��Ԫ������Ϊ�ַ���
		case HSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			if (strCell.trim().equals("") || strCell.trim().length() <= 0)
				strCell = " ";
			break;
		// ��Ԫ������Ϊ��ʽ
		case HSSFCell.CELL_TYPE_FORMULA:
			strCell = cell.getCellFormula();
			break;
		// ��Ԫ������Ϊ����ֵ
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		// ��Ԫ������Ϊ����
		case HSSFCell.CELL_TYPE_ERROR:
			strCell = String.valueOf(cell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		if (strCell.equals("") || strCell == null) {
			return "";
		}

		return strCell.trim();
	}

	/**
	 * �õ�ָ���е�����
	 * 
	 * @param lineNum
	 * @return String[]
	 */
	public String[] readExcelLine(int lineNum) {
		return readExcelLine(this.sheetNum, lineNum);
	}

	/**
	 * ָ�������������������
	 * 
	 * @param sheetNum
	 * @param lineNum
	 * @return String[]
	 */
	public String[] readExcelLine(int sheetNum, int lineNum) {
		if (sheetNum < 0 || lineNum < 0)
			return null;
		String[] strExcelLine = null;
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(lineNum);

			int cellCount = row.getLastCellNum();
			strExcelLine = new String[cellCount + 1];
			for (int i = 0; i <= cellCount; i++) {
				strExcelLine[i] = readStringExcelCell(lineNum, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strExcelLine;
	}

	/**
	 * ��ȡָ���е�����
	 * 
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int cellNum) {
		return readStringExcelCell(this.rowNum, cellNum);
	}

	/**
	 * ָ���к��б�ŵ�����
	 * 
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int rowNum, int cellNum) {
		return readStringExcelCell(this.sheetNum, rowNum, cellNum);
	}

	/**
	 * ָ���������С����µ�����
	 * 
	 * @param sheetNum
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int sheetNum, int rowNum, int cellNum) {
		if (sheetNum < 0 || rowNum < 0)
			return "";
		String strExcelCell = "";
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(rowNum);

			if (row.getCell((short) cellNum) != null) { // add this condition
				strExcelCell = getStringCellValue(row.getCell((short) cellNum));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strExcelCell;
	}

	public static void main(String args[]) {
		File file = new File("C:\\qt.xls");
		ExcelReader readExcel = new ExcelReader(file);
		try {
			readExcel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readExcel.setSheetNum(0); // ���ö�ȡ����Ϊ0�Ĺ�����
		// ������
		int count = readExcel.getRowCount();
		for (int i = 0; i <= count; i++) {
			String[] rows = readExcel.readExcelLine(i);
			for (int j = 0; j < rows.length; j++) {
				System.out.print(rows[j] + " ");
			}
			System.out.print("\n");
		}
	}
}
