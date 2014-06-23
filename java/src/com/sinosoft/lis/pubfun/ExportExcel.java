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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;

public class ExportExcel {
    public static class Format{
        public ArrayList mListCell=null;
        public ArrayList mListBL = null;
        public ArrayList mListColWidth = null;
    }
    public static class Cell{
        public Cell(){}
        public int col;
        public int row;
        public int width=1;
        public int height=1;
        public boolean bBorder=false;
        public boolean bBold=false;
        public String content="";
    }

    public static class ListBlock{
        private String companyCode;
        public ListBlock(String str){companyCode = str;};

        public String[] colName=null;
        public String sql;
        public int row1=0;
        public int col1=0;
        public int row2;
        public int col2;
        public int size=0;
        public String[][] data=null;
        public boolean InitData(){
            if(sql==null||sql.equals(""))
                return false;
            Connection conn=null;
            try{
                conn=DBConnPool.getConnection();
                if (conn==null)
                {
                    System.out.println("数据库连接失败！");
                    return false;
                }
                data = exeSQL(sql,conn);
                if(data==null){
                    row2=row1;
                    col2=col1;
                    if(colName!=null){
                        row2++;
                        col2+=colName.length;
                    }
                }
                else{
                    row2=row1+data.length;
                    size=data.length;
                    if(colName!=null){
                        row2++;
                        col2=col1+colName.length;
                    }
                    else if(data.length>1){
                        col2=col1+data[0].length;
                    }
                    else
                        col2=col1;
                }
            }
            catch(Exception e){
                System.out.println(e);return false;
            }
            finally{
                try{conn.close();}catch(Exception e){};
            }
            return true;
        }
        public boolean InitData(String[][] result){
            
            Connection conn=null;
            try{
                conn=DBConnPool.getConnection();
                if (conn==null)
                {
                    System.out.println("数据库连接失败！");
                    return false;
                }
                if(sql!=null && !sql.equals(""))
                	data = exeSQL(sql,conn);
                
                if((data == null || data.equals("")) && (result == null || result.equals(""))){
                    row2=row1;
                    col2=col1;
                    if(colName!=null){
                        row2++;
                        col2+=colName.length;
                    }
                }
                if((data != null && !data.equals("")) && (result == null || result.equals(""))){
                    row2=row1+data.length;
                    size=data.length;
                    if(colName!=null){
                        row2++;
                        col2=col1+colName.length;
                    }
                    else if(data.length>1){
                        col2=col1+data[0].length;
                    }
                    else
                        col2=col1;
                }
                
                if(data!=null && !data.equals("") && result != null && !result.equals("")){
                	String[][] a3 = new String[data.length+result.length][data[0].length];   
                    System.arraycopy(data,0,a3,0,data.length);   
                    System.arraycopy(result,0,a3,data.length,result.length);
                    data = a3;
                    
                    row2=row1+data.length;
                    size=data.length;
                    if(colName!=null){
                        row2++;
                        col2=col1+colName.length;
                    }
                    else if(data.length>1){
                        col2=col1+data[0].length;
                    }
                    else
                        col2=col1;
                }
                
                if((data == null || data.equals(""))  && result != null && !result.equals("")){
                    data = result;
                    row2=row1+data.length;
                    size=data.length;
                    if(colName!=null){
                        row2++;
                        col2=col1+colName.length;
                    }
                    else if(data.length>1){
                        col2=col1+data[0].length;
                    }
                    else
                        col2=col1;
                }
            }
            catch(Exception e){
                System.out.println(e);return false;
            }
            finally{
                try{conn.close();}catch(Exception e){};
            }
            return true;
        }
    }

    public static void AddCellToList(ArrayList list,String text,int row,int col,int width,int height,boolean bBorder,boolean bBold){
        Cell tCell = new Cell();
        tCell.row=row;
        tCell.col=col;
        tCell.bBorder=bBorder;
        tCell.bBold=bBold;
        tCell.height=height;
        tCell.width=width;
        tCell.content=text;
        list.add(tCell);
    }

    public ExportExcel() {
    }


    public boolean write(Format format,BufferedOutputStream bos)
    {
        if(format==null)
            return false;
        ArrayList listCell = format.mListCell;
        ArrayList listLB = format.mListBL;
        ArrayList listColWidth = format.mListColWidth;
        
        if(null==listCell && null==listLB)
            return false;
        if(null==bos)
            return false;

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("new sheet");

        try{

        if(listColWidth!=null){
            for(int i=0;i<listColWidth.size();i++){
                String[] para = (String[])listColWidth.get(i);
                short nCol = (short)Integer.parseInt(para[0]);
                short nWidth = (short)Integer.parseInt(para[1]);
                sheet.setColumnWidth(nCol,nWidth);
            }
        }

        // Create a new font and alter it.
        HSSFFont fontBold = wb.createFont();
        fontBold.setFontHeightInPoints((short)10);
        fontBold.setFontName("宋体");
        fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFFont fontNormal = wb.createFont();
        fontNormal.setFontHeightInPoints((short)10);
        fontNormal.setFontName("宋体");
        fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

        HSSFCellStyle styleBorderBold = wb.createCellStyle();
        styleBorderBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBorderBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBorderBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBorderBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBorderBold.setFont(fontBold);

        HSSFCellStyle styleBorderNormal = wb.createCellStyle();
        styleBorderNormal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBorderNormal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBorderNormal.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBorderNormal.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBorderNormal.setFont(fontNormal);

        HSSFCellStyle styleBold = wb.createCellStyle();
        styleBold.setFont(fontBold);

        HSSFCellStyle styleNormal = wb.createCellStyle();
        styleNormal.setFont(fontNormal);

        if(listLB!=null){
            for(int iLB=0;iLB<listLB.size();iLB++){
                ListBlock currLB = (ListBlock)listLB.get(iLB);
                int startRow = currLB.row1;
                for(int i=0;currLB.colName!=null&&i<currLB.colName.length;i++){
                    int rowIndex = startRow;
                    int colIndex = currLB.col1+i;
                    HSSFRow row = sheet.getRow(rowIndex);
                    if(row==null)
                        row = sheet.createRow(rowIndex);
                    HSSFCell cell = row.getCell((short)colIndex);
                    if(cell==null)
                        cell = row.createCell((short)colIndex);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellStyle(styleBorderBold);
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellValue(currLB.colName[i]);
                }

                if(currLB.colName!=null)
                    startRow=currLB.row1+1;

                String[][] data = currLB.data;
                if(data!=null){
                    for(int i=0;i<data.length;i++){
                        for(int j=0;j<data[i].length;j++){
                            int rowIndex = startRow+i;
                            int colIndex = currLB.col1+j;

                            HSSFRow row = sheet.getRow(rowIndex);
                            if(row==null)
                                row = sheet.createRow(rowIndex);
                            HSSFCell cell = row.getCell((short)colIndex);
                            if(cell==null)
                                cell = row.createCell((short)colIndex);
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellStyle(styleBorderNormal);
                            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                            cell.setCellValue(data[i][j]);
                        }
                    }
                }
            }
        }


        if(listCell!=null){
            for(int i=0;i<listCell.size();i++){
                Cell currCell = (Cell)listCell.get(i);
                for(int j=currCell.row;j<currCell.row+currCell.height;j++){
                    for(int k=currCell.col;k<currCell.col+currCell.width;k++){
                        HSSFRow trow = sheet.getRow(j);
                        if(trow==null)
                            trow = sheet.createRow(j);
                        HSSFCell tcell = trow.getCell((short)k);
                        if(tcell==null)
                            tcell = trow.createCell((short)k);
                        tcell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        tcell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        if(j==currCell.row&&k==currCell.col)
                            tcell.setCellValue(currCell.content);
                        if(currCell.bBorder){
                            if(currCell.bBold)
                                tcell.setCellStyle(styleBorderBold);
                            else
                                tcell.setCellStyle(styleBorderNormal);
                        }
                        else{
                            if(currCell.bBold)
                                tcell.setCellStyle(styleBold);
                            else
                                tcell.setCellStyle(styleNormal);
                        }
                    }
                }
                if(currCell.height>1||currCell.width>1)
                    sheet.addMergedRegion(new Region(currCell.row,(short)currCell.col,currCell.row+currCell.height-1,(short)(currCell.col+currCell.width-1)));
            }
        }

        wb.write(bos);
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }

        return true;
    }

    
    public static String[][] exeSQL(String sql,String companyCode){
        String[][] retArray = null;

        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String mResult = "";
        ArrayList tempList = new ArrayList();

        System.out.println("ExportExcel.exeSQL() : " + sql.trim());
        Connection conn=null;
        conn=DBConnPool.getConnection();
        if (conn==null)
        {
          System.out.println("数据库连接失败！");
          return null;
        }

        try {
            stmt = conn.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );

            rs = stmt.executeQuery( StrTool.GBKToUnicode( sql ));
            rsmd = rs.getMetaData();
            int n = rsmd.getColumnCount();
            int k = 0;

            // 取得总记录数
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

            if(tempList.size()>0){
            retArray = new String[tempList.size()][];
            for(int i=0;i<tempList.size();i++){
                String[] row = (String[])tempList.get(i);
                retArray[i]=row;
                //for(int j=0;j<row.length;j++){
                //    retArray[i][j] = row[j];
                //}
            }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            try{ rs.close(); stmt.close(); } catch( Exception ex ) {}
        }
        finally{
                try{conn.close();} catch( Exception ex ) {}
        }

        return retArray;
    }

    public static String[][] exeSQL(String sql,Connection conn){
        String[][] retArray = null;

        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String mResult = "";
        ArrayList tempList = new ArrayList();

        System.out.println("ExportExcel.exeSQL() : " + sql.trim());
        boolean connflag=true;
        if (conn==null)
        {
          System.out.println("数据库连接失败！");
          return null;
        }

        try {
            stmt = conn.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );

            rs = stmt.executeQuery( StrTool.GBKToUnicode( sql ));
            rsmd = rs.getMetaData();
            int n = rsmd.getColumnCount();
            int k = 0;

            // 取得总记录数
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

            if(tempList.size()>0){
            retArray = new String[tempList.size()][];
            for(int i=0;i<tempList.size();i++){
                String[] row = (String[])tempList.get(i);
                retArray[i]=row;
                //for(int j=0;j<row.length;j++){
                //    retArray[i][j] = row[j];
                //}
            }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            try{ rs.close(); stmt.close(); } catch( Exception ex ) {}
        }
        return retArray;
    }


    public static String getDataValue( ResultSetMetaData rsmd, ResultSet rs, int i )
    {
      String strValue = "";
      try
      {
        int dataType = rsmd.getColumnType( i );
        int dataScale = rsmd.getScale( i );
        int dataPrecision =rsmd.getPrecision(i);
        if( dataType == Types.CHAR || dataType == Types.VARCHAR ) strValue = StrTool.unicodeToGBK( rs.getString( i ));
        if( dataType == Types.TIMESTAMP || dataType == Types.DATE ) strValue = (new FDate()).getString( rs.getDate( i ));
        if( dataType == Types.DECIMAL || dataType == Types.DOUBLE ){
        	strValue = String.valueOf( rs.getDouble( i ));
//          只有数值的小数点后都为0才去掉小数点以及后面的0
        	strValue = PubFun.getInt(strValue);
        }
        if( dataType == Types.INTEGER || dataType == Types.SMALLINT ) strValue = String.valueOf( rs.getInt( i ));
        if( dataType == Types.NUMERIC )
        {
          if( dataScale == 0 )
          {
            if (dataPrecision==0){
              strValue = String.valueOf( rs.getDouble(i));
//            只有数值的小数点后都为0才去掉小数点以及后面的0
              strValue = PubFun.getInt(strValue);
            }else{
              strValue = String.valueOf( rs.getLong(i));
            }
          }
          else{
            strValue = String.valueOf(rs.getBigDecimal(i));
            System.out.println("BigDecimal: The Numeric is = "+strValue);
          }
        }
        if(strValue == null)
            strValue = "";
//      只有数值的小数点后都为0才去掉小数点以及后面的0
//        strValue = PubFun.getInt(strValue);
      }
      catch( Exception ex ){}

      return strValue;
  }


    public static void main(String[] args) {
        ExportExcel excel = new ExportExcel();
        try{
            ExportExcel.Format format = new ExportExcel.Format();
            ArrayList listCell = new ArrayList();
            ArrayList listLB = new ArrayList();
            ArrayList listColWidth = new ArrayList();
            format.mListCell=listCell;
            format.mListBL=listLB;
            format.mListColWidth=listColWidth;

            listColWidth.add(new String[]{"0","5000"});

//            ExportExcel.Cell tCell = new ExportExcel.Cell();
//            tCell.row=0;
//            tCell.col=0;
//            tCell.bBorder=true;
//            tCell.height=1;
//            tCell.width=5;
//            tCell.content="边框";
//            listCell.add(tCell);
//
//            tCell = new ExportExcel.Cell();
//            tCell.row=1;
//            tCell.col=0;
//            tCell.bBorder=false;
//            tCell.height=1;
//            tCell.width=5;
//            tCell.content="无边框";
//            listCell.add(tCell);

           // ExportExcel.ListBlock tLB = new ExportExcel.ListBlock("001");
            //tLB.colName=new String[]{"编号","姓名"};
//            tLB.sql="select * from lduser ";
//            tLB.col1=0;
//            tLB.row1=2;
//            tLB.InitData();
//            listLB.add(tLB);

            ExportExcel.ListBlock tLB1 = new ExportExcel.ListBlock("001");
//            tLB1.colName=new String[]{"集体合同号码","合同号码","被保人客户号","印刷号码","投保人客户号码","管理机构","处理机构","家庭保障号","与主被保人关系","与投保人关系","客户地址号码","客户内部号码","被保人名称","被保人性别","被保人出生日期","证件类型",
//						                          "证件号码","国籍","民族","户口所在地","婚姻状况","结婚日期","健康状况","身高","体重","学历","信用等级","银行编码","银行帐号","银行帐户名","入司日期","参加工作日期","职位","工资","职业类别","职业代码",
//												  "职业（工种）","兼职（工种）","是否吸烟标志","保险计划编码","操作员","被保人状态","入机日期","入机时间","最后一次修改日期","最后一次修改时间","核保状态","最终核保人编码","核保完成日期","核保完成时间","","被保人数目"};

			tLB1.colName=new String[]{"集体合同号码","被保人客户号","姓名","性别","出生日期","证件类型","证件号码","保险计划"};
            tLB1.sql="select Grpcontno,InsuredNo,Name,Sex,Birthday,IDType,IDNo,ContPlanCode From LCInsured where insuredno='000001218588'";
            //tLB1.sql="select * From LDuser";
			tLB1.col1=0;
            //tLB1.row1=tLB.row2+10;
			tLB1.row1=0;
			String rr[][] = {{"1","2","3","4","5","6","7","8"},{"11","12","13","14","15","16","17","18"}};
            tLB1.InitData(rr);
            listLB.add(tLB1);

            File of = new File("d:\\excel.xls");
            FileOutputStream ofs = new FileOutputStream(of);
            BufferedOutputStream bos = new BufferedOutputStream(ofs);
            excel.write(format,bos);
        }
        catch(Exception e){System.out.println(e);}
        finally{}

    }

}
