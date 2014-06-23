package com.sinosoft.xreport.bl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;

/**
 * sql查询的结果集.
 * 执行前需先设定DataSource,TableRelations
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class DataMap
{

    Map sqlMap;
    Map resultMap;

    Logger log = XTLogger.getLogger(this.getClass());

    public DataMap(Map sqlMap)
    {
        this.sqlMap = sqlMap;
    }


    private DataSource dataSource;
    private TableRelations tableRelations;

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public void setTableRalation(TableRelations tableRelations)
    {
        this.tableRelations = tableRelations;
    }

    private void query() throws Exception
    {
        resultMap = new HashMap();

        Iterator it = sqlMap.keySet().iterator();

        while (it.hasNext())
        {
            String tableName = (String) it.next();

            log.debug("block's sql:" +
                      ((StringBuffer) sqlMap.get(tableName)).toString());
            Vector data = (Vector) dataSource.getDataSet(((StringBuffer) sqlMap.
                    get(tableName)).toString());

//      log.debug("==============\n");
//      log.debug(tableName);
//      for(int i=0;i<data.size();i++)
//      {
//        String[] line=(String[]) data.get(i);
//        StringBuffer sb=new StringBuffer();
//        for(int j=0;j<line.length;j++)
//        {
//          sb.append(line[j]+",");
//        }
//
//        log.debug(i+"\t:"+sb);
//      }
//      log.debug("==============\n");

            resultMap.put(tableName, data);
        }

    }


    /**
     * 根据数据源名,condition集合得到相应的值
     * @param tableColumn 数据源名,包括表名.列名
     * @param wcSet condition集合,condition需要解析为WhereClause
     * @return 匹配所有条件的值,没有则返回"0"
     * @throws Exception 取数错误
     */
    public String getValue(String tableColumn, Set wcSet) throws Exception
    {
        if (resultMap == null)
        {
            query();
        }

        log.debug("tableColumn:" + tableColumn + ",wcSet:" + wcSet);

        String tableName = StringUtility.getLeftWord(tableColumn,
                tableColumn.indexOf("."));
        String colName = StringUtility.getRightWord(tableColumn,
                tableColumn.indexOf("."));
        Vector data = (Vector) resultMap.get(tableName);
        Map indexValueMap = new HashMap();

        String[] columnNames = (String[]) data.get(0);

        //确定条件对应列,对应取值...
        Iterator itWC = wcSet.iterator();
        while (itWC.hasNext())
        {
            WhereClause wc = (WhereClause) itWC.next();

            //仅需要处理where条件为=的情况,有or的无所谓了,值会为零,但是在第二轮重算
            if (!"=".equals(wc.cp))
            {
                continue;
            }

            //得到相应字段
            String actualField = tableRelations.getActualField(wc.left,
                    tableName);
            //因为data中不含表名
            if (actualField != null)
            {
//        String actualColumn=StringUtility.getRightWord(actualField,actualField.indexOf("."));
                String actualColumn = actualField;
                for (int i = 0; i < columnNames.length; i++)
                {
                    if (actualColumn.equalsIgnoreCase(columnNames[i]))
                    {
                        String value = wc.getRight();
                        if (value.startsWith("'")) //去掉"'"
                        {
                            value = value.substring(1, value.lastIndexOf("'"));
                        }

                        indexValueMap.put(new Integer(i), value);
                    }
                }
            }
        }

        int valueIdx = -1;

        //确定取值列的索引
        for (int i = 0; i < columnNames.length; i++)
        {
            if (colName.equalsIgnoreCase(columnNames[i]))
            {
                valueIdx = i;
            }
        }

        log.debug("valueIdx:" + valueIdx);

        //把condition转换成本表相应的关联的字段  ...视图...可能导致问题:视图字段使用别名:别名中不包含.


        /**
         * 0.改造TableRelation,增加getActureField方法,所有字段匹配算法都转移进去.
         * 1.得到实际的字段
         * 2.根据名称得到相应的索引
         * 3.匹配相对应的值
         * finished on 2003.03.26
         */

        log.debug(">>>" + indexValueMap);

//    if("[VIEW_LCPol.ManageCom='86110000', VIEW_LCPol.CValiDate='2003-02-20']".equals(wcSet.toString())&&"VIEW_LCPol.count_MainPolNo_distinct".equals(tableColumn))
//    {
//      log.debug("here");
//    }


        double dValue = 0d;

        for (int i = 1; i < data.size(); i++)
        {
            String[] line = (String[]) data.get(i);

            Iterator it = indexValueMap.keySet().iterator();

            boolean allEquals = true;
            //判断每个条件是否满足
            while (it.hasNext())
            {
                Integer idx = (Integer) it.next();
                String value = (String) indexValueMap.get(idx);

                //使oracle时间一致.
                if (!value.equals(line[idx.intValue()].trim()) &&
                    !dateEquals(value, line[idx.intValue()].trim()))
                {
                    allEquals = false;
                    break;
                }
            }

//      log.debug("allEquals:"+allEquals);

            if (allEquals) //这一行数据满足.............sum..........
            {
//        String s="line: ";
//        for(int j=0;j<line.length;j++)
//        {
//          s+=line[j]+":";
//        }
//
//        log.debug(s);


                try
                {

                    if (!StringUtility.isNumber(line[valueIdx])) //非数字,不可能sum
                    {
                        return line[valueIdx];
                    }
                    else
                    {
                        dValue += Double.parseDouble(line[valueIdx]);
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        return double2Str(dValue); //固定表的取值都是数值,假设3.

    }

    private DecimalFormat df = new DecimalFormat("0.00");

    private String double2Str(double in)
    {
        return df.format(in);
    }


    private boolean dateEquals(String left, String right)
    {
        if (null == left || left.length() < 8 || left.length() > 10)
        {
            return false;
        }

        return (left + " 0:00:00").equals(right)
                || (left + " 0:00:00.0").equals(right)
                || (left + " 00:00:00").equals(right)
                || (left + " 00:00:00.0").equals(right)
                || (left + " 0:00:00.p").equals(right)
                || (left + " 00:00:00.p").equals(right)
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 0:00:00").
                equals(right) //使 2003-2-10 == 2003-02-10
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 0:00:00.0").
                equals(right)
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 00:00:00").
                equals(right)
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 00:00:00.0").
                equals(right)
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 0:00:00.p").
                equals(right)
                ||
                (left.substring(0, 5) + "0" + left.substring(5) + " 00:00:00.p").
                equals(right);
    }


    public static void main(String[] args)
    {
        String a = "aaa";
        System.out.println(StringUtility.getLeftWord(a, a.indexOf(".")));
    }
}