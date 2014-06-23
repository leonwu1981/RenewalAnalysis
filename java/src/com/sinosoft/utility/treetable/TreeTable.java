/*
 * 创建日期 2006-2-24<br> 作者：王育春 <br> 邮箱:wangyc@sinosoft.com.cn<br>
 */
package com.sinosoft.utility.treetable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Administrator
 */
public class TreeTable {
    private DataColumn[] cols;

    private String DataSQL = null;

    private String SortType = null;

    private String TopLevelParentValue = null;

    private TreeTableRow[] rows;

    private String ScriptString = null;

    public TreeTable() {
    }

    private void generateScript() {
        StringBuffer sb = new StringBuffer(10000);
        sb.append("var _A = new Array();\n");

        // 增加表头
        DataColumn[] dcs = this.cols;
        sb.append("_A[0] = [");
        for (int i = 0; i < dcs.length; i++) {
            if (i == 0) {
                sb.append("'" + dcs[i].getColumnName().replaceAll("'", "") + "'");
            } else {
                sb.append(",'" + dcs[i].getColumnName().replaceAll("'", "") + "'");
            }
        }
        sb.append("];\n");

        if (rows != null && rows.length > 0) {
            // 增加数据行
            for (int i = 0; i < this.rows.length; i++) {
                sb.append("_A[" + (i + 1) + "] = [");
                for (int j = 0; j < dcs.length; j++) {
                    String v = rows[i].getString(j);
                    if (j == 0) {
                        if (v == null) {
                            sb.append("''");
                            continue;
                        }
                        if (dcs[j].ColumnType != DataColumn.STRING && dcs[j].ColumnType != DataColumn.DATETIME) {
                            sb.append("" + v);
                        } else {
                            sb.append("'" + replaceSpecial(v) + "'");
                        }
                    } else {
                        if (v == null) {
                            sb.append(",''");
                            continue;
                        }
                        if (dcs[j].ColumnType != DataColumn.STRING && dcs[j].ColumnType != DataColumn.DATETIME) {
                            sb.append("," + v);
                        } else {
                            sb.append(",'" + replaceSpecial(v) + "'");
                        }
                    }
                }
                sb.append("];\n");
            }
        }
        this.ScriptString = sb.toString();
    }

    public String getArrayScript() {
        if (this.ScriptString == null) {
            generateScript();
        }
        return ScriptString;
    }

    public DataColumn[] getColumns() {
        return this.cols;
    }

    public TreeTableRow getRow(int index) {
        return rows[index];
    }

    public Object get(int rowIndex, int colIndex) {
        return rows[rowIndex].get(colIndex);
    }

    public int getRowCount() {
        if (rows == null) {
            return 0;
        }
        return rows.length;
    }

    public int getColCount() {
        return cols.length;
    }

    public void setColumns(DataColumn[] cols) {
        this.cols = cols;
    }

    public void setItems(TreeTableRow[] rows) {
        this.rows = rows;
    }

    public void parseSQL(String sql) {
        System.out.println("------TreeTable------" + sql);
        DataSQL = sql;
        DataTable dt = DBUtil.executeDataTable(DataSQL);
        if (dt == null) {
            throw new RuntimeException("发生错误，SQL语句不正确:" + DataSQL);
        }

        dt = sortData(dt);

        setColumns(dt.getDataColumns());
        for (int i = 0; i < dt.getRowCount(); i++) {
            new TreeTableRow(this, dt.getDataValues()[i]);
        }
    }

    /**
     * 排序，性能需调整
     * 
     * @param dt
     * @return
     */
    private DataTable sortData(DataTable dt) {
        if (this.SortType == null || this.SortType.equals("0")) {
            return dt;
        } else {
            Object[][] v = new Object[dt.getRowCount()][dt.getColCount() + 1];
            Object[][] o = dt.getDataValues();
            DataColumn dc = new DataColumn();
            dc.ColumnName = "_TREELEVEL";
            dc.ColumnType = DataColumn.INTEGER;
            DataColumn[] dcs = (DataColumn[]) ArrayUtil.add(dt.getDataColumns(), dc);
            if (dt.getRowCount() > 0) {
                int vLen = 0;
                int colLen = o[0].length;
                sortArray(dt.getDataValues());
                for (int i = 0; i < o.length; i++) {
                    Object[] row = o[i];
                    if (row == null) {
                        continue;
                    }
                    if (row[1] == TopLevelParentValue || row[1].toString().equals(TopLevelParentValue)) {
                        System.arraycopy(row, 0, v[vLen], 0, row.length);
                        v[vLen][colLen] = new Integer(1);
                        o[i] = null;
                        vLen++;
                        if (row[3] != null && row[3].toString().equals("1")) {
                            vLen = sortOneNode(v, o, vLen);
                        }
                    }
                }
                // 处理孤立节点
                for (int i = 0; i < o.length; i++) {
                    Object[] row = o[i];
                    if (row == null) {
                        continue;
                    }
                    Object tParentCode = row[1];
                    if (tParentCode != TopLevelParentValue && !tParentCode.toString().equals(TopLevelParentValue)) {
                        boolean flag = false;
                        for (int j = 0; j < o.length; j++) {
                            Object[] tRow = o[j];
                            if (tRow == null) {
                                continue;
                            }
                            if (tRow[0] == tParentCode || tRow[0].equals(tParentCode)) {
                                flag = true;// 有父一级节点存在
                                break;
                            }
                        }
                        if (flag) {
                            continue;// 有父一级节点存在，则直接跳出
                        }
                        System.arraycopy(row, 0, v[vLen], 0, row.length);
                        v[vLen][colLen] = new Integer(1);
                        o[i] = null;
                        vLen++;
                        if (row[3] != null && row[3].toString().equals("1")) {
                            vLen = sortOneNode(v, o, vLen);
                        }
                    }
                }
            }
            return new DataTable(dcs, v);
        }
    }

    private int sortOneNode(Object[][] v, Object[][] o, int vLen) {
        Object ParnetCode = v[vLen - 1][0];
        int colLen = v[vLen - 1].length - 1;
        int Level = ((Integer) v[vLen - 1][colLen]).intValue();
        for (int i = 0; i < o.length; i++) {
            Object[] row = o[i];
            if (row == null) {
                continue;
            }
            if (row[1].equals(ParnetCode)) {
                System.arraycopy(row, 0, v[vLen], 0, row.length);
                v[vLen][colLen] = new Integer(Level + 1);
                o[i] = null;
                vLen++;
                if (row[3] != null && row[3].toString().equals("1")) {
                    vLen = sortOneNode(v, o, vLen);
                }
            }
        }
        return vLen;
    }

    private void sortArray(Object[][] array) {
        List list = Arrays.asList(array);
        final int colLen = array[0].length - 2;
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object[] node1 = (Object[]) o1;
                Object[] node2 = (Object[]) o2;
                Comparable order1 = (Comparable) node1[colLen];
                Comparable order2 = (Comparable) node2[colLen];
                int c = order1.compareTo(order2);
                if (c == 0) {
                    Comparable code1 = (Comparable) node1[0];
                    Comparable code2 = (Comparable) node2[0];
                    return code1.compareTo(code2);
                }
                return c;
            }
        });
    }

    public void addRow(TreeTableRow row) {
        rows = (TreeTableRow[]) ArrayUtil.add(rows, row);
    }

    public static TreeTable parseArray(String str) {
        String[] array = str.split(";");
        TreeTable tt = new TreeTable();
        // 得到DataColumn
        String[] cols = array[0].split("\",");
        DataColumn[] dcs = new DataColumn[cols.length];
        for (int i = 0; i < cols.length; i++) {
            DataColumn dc = new DataColumn();
            if (i == cols.length - 1) {
                dc.ColumnName = cols[i].substring(1, cols[i].length() - 1).replaceAll("&nbsp;", "\"");
            } else {
                dc.ColumnName = cols[i].substring(1).replaceAll("&nbsp;", "\"");
            }
            dc.ColumnType = DataColumn.STRING;
            dcs[i] = dc;
        }
        tt.setColumns(dcs);
        Object[][] values = new Object[array.length - 1][dcs.length];
        for (int i = 1; i < array.length; i++) {
            String[] row = array[i].split("\",");
            for (int j = 0; j < row.length; j++) {
                if (j == row.length - 1) {
                    values[i - 1][j] = row[j].substring(1, row[j].length() - 1).replaceAll("&quot;", "\"");
                } else {
                    values[i - 1][j] = row[j].substring(1).replaceAll("&quot;", "\"");
                }
            }
            new TreeTableRow(tt, values[i - 1]);
        }
        return tt;
    }

    public static String replaceSpecial(String str) {
        char[] t = str.toCharArray();
        char[] r = new char[2 * t.length];
        int i = 0, j = 0;
        for (; i < t.length; i++, j++) {
            char c = t[i];
            if (c == '\\') {
                r[j] = '\\';
                r[++j] = '\\';
            } else if (c == '\"') {
                r[j] = '\\';
                j++;
                r[j] = '\"';
            } else if (c == '\'') {
                r[j] = '\\';
                j++;
                r[j] = '\'';
            } else {
                r[j] = c;
            }
        }
        return new String(ArrayUtil.subarray(r, 0, j));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // String t =
        // "\"ORGANCOMCODE\",\"UPCOMCODE\",\"GRPNAME\",\"CHILDFLAG\",\"GROUPLEVEL\",\"ORGANTYPE\",\"1\",\"0\",\"_TREELEVEL\";\"1102\",\"0000\",\"中国石油辽河油田分公司\",\"1\",\"1\",\"O\",\"1\",\"1\",\"1\";\"1103\",\"0000\",\"中国石油吉林油田分公司\",\"1\",\"1\",\"O\",\"1\",\"1\",\"1\"";
        // String[] a = t.split("\";");
        // parseArray(t);
        // System.out.println(t);
        // System.out.println(replaceSpecial(t));\
        for (int i = 0; i < 1; i++) {
            TreeTable tt = new TreeTable();
            tt.DataSQL = "select nodecode,parentnodecode,nodename,case childflag when '0' then 0 else 1 end,runscript,nodeorder,'0' from ldmenu a";
            tt.TopLevelParentValue = "0";
            // DataTable dt = DBUtil.executeDataTable(tt.DataSQL);
            long t = System.currentTimeMillis();
            tt.parseSQL(tt.DataSQL);
            System.out.println(tt.getArrayScript());
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    public String getSortType() {
        return SortType;
    }

    public void setSortType(String sortType) {
        SortType = sortType;
    }

    public String getTopLevelParentValue() {
        return TopLevelParentValue;
    }

    public void setTopLevelParentValue(String TopLevelParentValue) {
        if (TopLevelParentValue != null && !TopLevelParentValue.equals("null")) {
            this.TopLevelParentValue = TopLevelParentValue;
        }
    }

}
