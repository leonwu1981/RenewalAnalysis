/*
 * 创建日期 2005-7-15 作者：王育春 邮箱:wangyc@sinosoft.com.cn
 */
package com.sinosoft.utility.treetable;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import com.sinosoft.lis.pubfun.FDate;

public class DataTable implements IDataCollection {
    private DataRow[] rows;

    private DataColumn[] columnTypes;

    private Object[][] columnValues;

    private int ColumnCount = 0;

    private int RowCount = 0;

    private FDate fDate = new FDate();
    
    public DataTable(DataColumn[] types, Object[][] values) {
        initArray(types, values);
    }

    public DataTable() {
    }

    private void initArray(DataColumn[] types, Object[][] values) {
        // 判断是否有相同列名
        for (int i = 0; i < types.length; i++) {
            String columnName = types[i].getColumnName();
            for (int j = 0; j < i; j++) {
                if (columnName == null) {
                    throw new RuntimeException("DataTable中第" + i + "列列名为null!");
                }
                if (columnName.equals(types[j].getColumnName())) {
                    throw new RuntimeException("一个DataTable中不充许有重名的列");
                }
            }
        }
        columnTypes = types;
        columnValues = values;
        ColumnCount = columnTypes.length;
        if (columnValues != null) {
            RowCount = columnValues.length;
        }
    }

    public DataTable(ResultSet rs) {
        init(rs);
    }

    private void init(ResultSet rs) {
        ResultSetMetaData rsmd;
        try {
            // 以下准备DataColumn[]
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            DataColumn[] dcArray = new DataColumn[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String name = rsmd.getColumnName(i);
                boolean b = rsmd.isNullable(i) == ResultSetMetaData.columnNullable;
                DataColumn dc = new DataColumn();
                dc.setAllowNull(b);
                dc.setColumnName(name);

                // 以下设置数据类型
                int dataType = rsmd.getColumnType(i);
                if (dataType == Types.CHAR || dataType == Types.VARCHAR) {
                    dc.ColumnType = DataColumn.STRING;
                } else if (dataType == Types.TIMESTAMP || dataType == Types.DATE) {
                    dc.ColumnType = DataColumn.DATETIME;
                } else if (dataType == Types.DECIMAL) {
                    dc.ColumnType = DataColumn.DECIMAL;
                } else if (dataType == Types.FLOAT) {
                    dc.ColumnType = DataColumn.FLOAT;
                } else if (dataType == Types.INTEGER) {
                    dc.ColumnType = DataColumn.INTEGER;
                } else if (dataType == Types.SMALLINT) {
                    dc.ColumnType = DataColumn.SMALLINT;
                } else if (dataType == Types.BLOB) {
                    dc.ColumnType = DataColumn.BLOB;
                } else if (dataType == Types.NUMERIC) {
                    int dataScale = rsmd.getScale(i);
                    int dataPrecision = rsmd.getPrecision(i);
                    if (dataScale == 0) {
                        if (dataPrecision == 0) {
                            dc.ColumnType = DataColumn.BIGDECIMAL;
                        } else {
                            dc.ColumnType = DataColumn.LONG;
                        }
                    } else {
                        dc.ColumnType = DataColumn.BIGDECIMAL;
                    }
                }
                dcArray[i - 1] = dc;
            }
            // 以下准备ColumnValues[]
            ArrayList list = new ArrayList();
            int i = 0;
            while (rs.next()) {
                Object[] t = new Object[columnCount];
                for (int j = 1; j <= columnCount; j++) {
                    t[j - 1] = rs.getObject(j);
                    if(dcArray[j-1].ColumnType==DataColumn.DATETIME && t[j - 1]!=null && !t[j - 1].equals(""))// 数据类型为日期、时间
                    {
                    	t[j - 1] = fDate.getString((Date) t[j - 1]);
                    }
                 }
                list.add(t);
                i++;
            }
            Object[][] valueArray = new Object[list.size()][];
            for (i = 0; i < list.size(); i++) {
                valueArray[i] = (Object[]) list.get(i);
            }
            list.clear();
            initArray(dcArray, valueArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteColumn(int columnIndex) {
        this.columnTypes = (DataColumn[]) ArrayUtil.remove(this.columnTypes, columnIndex);
        for (int i = 0; i < this.RowCount; i++) {
            this.columnValues[i] = (Object[]) ArrayUtil.remove(this.columnValues[i], columnIndex);
        }
        this.ColumnCount--;
    }

    public void deleteColumn(String columnName) {
        for (int i = 0; i < ColumnCount; i++) {
            if (columnTypes[i].getColumnName().equalsIgnoreCase(columnName)) {
                deleteColumn(i);
                break;
            }
        }
    }

    public void addColumn(DataColumn dc, Object[] columnValue) {

    }

    public void addRow(Object[] rowValue) {

    }

    public void deleteRow(int rowIndex) {

    }

	public void set(int rowIndex, int colIndex, Object value) {
		getDataRow(rowIndex).set(colIndex, value);
	}

	public void set(int rowIndex, String columnName, Object value) {
		getDataRow(rowIndex).set(columnName, value);
	}
    // public addRow

    public Object get(int rowIndex, int colIndex) {
        return columnValues[rowIndex][colIndex];
    }

    public Object get(int rowIndex, String columnName) {
        return getDataRow(rowIndex).get(columnName);
    }

    public String getString(int rowIndex, int colIndex) {
        if (columnValues[rowIndex][colIndex] != null) {
            return String.valueOf(columnValues[rowIndex][colIndex]).trim();
        } else {
            return null;
        }
    }

    public String getString(int rowIndex, String columnName) {
        Object o = getDataRow(rowIndex).get(columnName);
        if (o == null) {
            return null;
        } else {
            return String.valueOf(o);
        }
    }

    public DataRow getDataRow(int rowIndex) {
        if (rowIndex >= RowCount || rowIndex < 0) {
            throw new RuntimeException("指定的行索引值超出范围");
        }
        if (this.rows == null || this.rows.length < this.RowCount) {
            this.rows = new DataRow[this.RowCount];
        }
        if (rows[rowIndex] == null) {
            rows[rowIndex] = new DataRow(columnTypes, columnValues[rowIndex]);
        }
        return rows[rowIndex];
    }

    public DataColumn getDataColumn(int columnIndex) {
        if (columnIndex >= ColumnCount) {
            throw new RuntimeException("指定的列索引值超出范围");
        }
        return columnTypes[columnIndex];
    }

    public DataColumn getDataColumn(String columnName) {
        for (int i = 0; i < ColumnCount; i++) {
            if (columnTypes[i].getColumnName().equalsIgnoreCase(columnName)) {
                return (getDataColumn(i));
            }
        }
        throw new RuntimeException("指定的列名没有找到");
    }

    public DataColumnValues getColumnValues(int columnIndex) {
        if (columnIndex >= ColumnCount) {
            throw new RuntimeException("指定的列索引值超出范围");
        }
        DataColumnValues dcv = new DataColumnValues();
        DataColumn dc = columnTypes[columnIndex];
        dcv.setAllowNull(dc.isAllowNull);
        dcv.setColumnName(dc.getColumnName());
        dcv.setColumnType(dc.getColumnType());
        fill(dcv, columnIndex);
        return dcv;
    }

    public DataColumnValues getColumnValues(String columnName) {
        for (int i = 0; i < columnTypes.length; i++) {
            if (columnTypes[i].getColumnName().equalsIgnoreCase(columnName)) {
                return (getColumnValues(i));
            }
        }
        throw new RuntimeException("指定的列名没有找到");
    }

    private void fill(DataColumnValues dcv, String columnName) {
        for (int i = 0; i < ColumnCount; i++) {
            if (columnTypes[i].getColumnName().equalsIgnoreCase(columnName)) {
                fill(dcv, i);
            }
        }
        throw new RuntimeException("指定的列名没有找到");
    }

    private void fill(DataColumnValues dcv, int columnIndex) {
        if (columnIndex >= ColumnCount) {
            throw new RuntimeException("指定的列索引值超出范围");
        }
        if (columnTypes[columnIndex].ColumnType == dcv.getColumnType()) {
            Object[] t = new Object[columnValues.length];
            for (int i = 0; i < t.length; i++) {
                t[i] = columnValues[i][columnIndex];
            }
            dcv.setValues(t);
        } else {
            throw new RuntimeException("往一个DataColumnValues中填充数据时发生数据类型不一致错误");
        }
    }

    public int getRowCount() {
        return columnValues.length;
    }

    public int getColCount() {
        return columnTypes.length;
    }

    public DataColumn[] getDataColumns() {
        return columnTypes;
    }

    public Object[][] getDataValues() {
        return columnValues;
    }

    public static void main(String[] args) {
        String sql = "select OrganAuditType,SendOrganAuditType,GrpOrganAuditType,InsuOrganAuditType from lcedoritemflow where flowtype='0' and edortype='AV'";
        DataTable dt = DBUtil.executeDataTable(sql);
        System.out.println(dt.getString(0, 0));
        System.out.println(dt.getString(0, 1));
        System.out.println(dt.getString(0, 2));
        System.out.println(dt.getString(0, 3));
    }

}
