package com.sinosoft.lis.pubfun;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import com.sinosoft.utility.StrTool;
import com.sinosoft.utility.DBConnPool;
import java.io.*;
import java.util.*;
import java.sql.*; 
import java.util.zip.ZipOutputStream;
import au.com.bytecode.opencsv.CSVWriter;   
public class CsvOut {
	public String[] colName=null;
	public String sql;
	public int row1=0;
	public int col1=0;
	public int row2;
	public int col2;
	public int size=0;  
	public String[][] data=null;
	public boolean InitData() {
		String[][] dataOut = new String[][] { colName };
		if (sql == null || sql.equals(""))
			return false;
		Connection conn = null;
		try {
			conn = DBConnPool.getConnection();
			if (conn == null) {
				System.out.println("数据库连接失败！");
				return false;
			}
			data = exeSQL(sql, conn);
			if (data == null) {
				row2 = row1;
				col2 = col1;
				if (colName != null) {
					row2++;
					col2 += colName.length;
				}
			} else {
				row2 = row1 + data.length;
				size = data.length;
				if (colName != null) {
					row2++;
					col2 = col1 + colName.length;
				} else if (data.length > 1) {
					col2 = col1 + data[0].length;
				} else
					col2 = col1;
			}
			CsvOutData();
			
		} catch (Exception e) {
			System.out.println(e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println(e);
				return false;
			}

		}
		return true;
	}
	
	public boolean CsvOutData() {
		try {
			int length=data.length;
			int blocksize=50000;
			int index = data.length / blocksize + 1;
			for(int c = 0; c < index; c++){
				File tempFile = new File("D:/gogo/Output"+c+".csv");
				CSVWriter writer = new CSVWriter(new FileWriter(tempFile));
				writer.writeNext(colName);	
				for(int i = c * blocksize; i < (blocksize * (c + 1) >= length ? length : blocksize * (c + 1)); i++){
					writer.writeNext(data[i]);	
				}
				writer.close();
				System.out.println("D:/gogo/Output"+c+".csv");
				
		    }

			zip("D:/gogo/");
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	public static String[][] exeSQL(String sql, Connection conn) {
		String[][] retArray = null;

		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String mResult = "";
		ArrayList tempList = new ArrayList();

		System.out.println("ExportExcel.exeSQL() : " + sql.trim());
		boolean connflag = true;
		if (conn == null) {
			System.out.println("数据库连接失败！");
			return null;
		}

		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery(StrTool.GBKToUnicode(sql));
			rsmd = rs.getMetaData();
			int n = rsmd.getColumnCount();
			int k = 0;

			// 取得总记录数
//			while (rs.next()) {
//				String[] tempRow = new String[n];
//				k++;
//				for (int j = 1; j <= n; j++) {
//					tempRow[j - 1] = rs.getString(j);
//				}
//				tempList.add(tempRow);
//			}
		     while( rs.next() ) {
	                String[] tempRow = new String[n];
	                k++;
	                for( int j = 1; j <= n; j++ ) {
	                    String strValue = "";
	                    //根据数据类型取得数据的值
	                    strValue = getDataValue( rsmd, rs, j );
	                    tempRow[j-1] = strValue;
	                }
	                tempList.add(tempRow);
	            }
			rs.close();
			stmt.close();

			if (tempList.size() > 0) {
				retArray = new String[tempList.size()][];
				for (int i = 0; i < tempList.size(); i++) {
					String[] row = (String[]) tempList.get(i);
					retArray[i] = row;
					// for(int j=0;j<row.length;j++){
					// retArray[i][j] = row[j];
					// }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				rs.close();
				stmt.close();
			} catch (Exception ex) {
				
			}
		}
		return retArray;
	}




	public void zip(String inputFileName) throws Exception {
        String zipFileName = "d:\\test.zip"; //打包后文件名字
        System.out.println(zipFileName);
        zip(zipFileName, new File(inputFileName));
    }

   private void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        System.out.println("zip done");
        out.close();
    }

   private void zip(ZipOutputStream out, File f, String base) throws Exception {
       if (f.isDirectory()) {
           File[] fl = f.listFiles();
           out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
           base = base.length() == 0 ? "" : base + "/";
          for (int i = 0; i < fl.length; i++) {
           zip(out, fl[i], base + fl[i].getName());
         }
        }else {
           out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
           FileInputStream in = new FileInputStream(f);
          int b;
           System.out.println(base);
          while ( (b = in.read()) != -1) {
            out.write(b);
         }
         in.close();
       }
    }
   
   

   public static String getDataValue( ResultSetMetaData rsmd, ResultSet rs, int i )
   {
     String strValue = "";
     try
     {
       int dataType = rsmd.getColumnType( i );
       int dataScale = rsmd.getScale( i );
       int dataPrecision =rsmd.getPrecision(i);
//       if( dataType == Types.CHAR || dataType == Types.VARCHAR ) strValue = StrTool.unicodeToGBK( rs.getString( i ));
       if( dataType == Types.TIMESTAMP || dataType == Types.DATE ) strValue = (new FDate()).getString( rs.getDate( i ));
//       if( dataType == Types.DECIMAL || dataType == Types.DOUBLE ){
//       	strValue = String.valueOf( rs.getDouble( i ));
////         只有数值的小数点后都为0才去掉小数点以及后面的0
//       	strValue = PubFun.getInt(strValue);
//       }
//       if( dataType == Types.INTEGER || dataType == Types.SMALLINT ) strValue = String.valueOf( rs.getInt( i ));
//       if( dataType == Types.NUMERIC )
//       {
//         if( dataScale == 0 )
//         {
//           if (dataPrecision==0){
//             strValue = String.valueOf( rs.getDouble(i));
////           只有数值的小数点后都为0才去掉小数点以及后面的0
//             strValue = PubFun.getInt(strValue);
//           }else{
//             strValue = String.valueOf( rs.getLong(i));
//           }
//         }
//         else{
//           strValue = String.valueOf(rs.getBigDecimal(i));
//           System.out.println("BigDecimal: The Numeric is = "+strValue);
//         }
//       }
       else strValue = "	"+StrTool.unicodeToGBK( rs.getString( i ));

       if(strValue == null)
           strValue = "";
//     只有数值的小数点后都为0才去掉小数点以及后面的0
//       strValue = PubFun.getInt(strValue);
     }
     catch( Exception ex ){}

     return strValue;
 }
 



public static void main(String[] args) throws IOException {

	final String[] header = new String[]{"name", "sex", "age"};
	final String[][] data = new String[][]{header, {"日日", "F", "22"}, {"Tom", "M", "25"}, {"Lily", "F", "19"}};

	File tempFile = new File("D:/Output.csv");        

	CSVWriter writer = new CSVWriter(new FileWriter(tempFile));
	for (int i = 0; i < data.length; i++) {
		writer.writeNext(data[i]);
	}
	writer.close();
	System.out.println("D:/Output.csv");
}
}