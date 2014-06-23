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
 * sql��ѯ�Ľ����.
 * ִ��ǰ�����趨DataSource,TableRelations
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
     * ��������Դ��,condition���ϵõ���Ӧ��ֵ
     * @param tableColumn ����Դ��,��������.����
     * @param wcSet condition����,condition��Ҫ����ΪWhereClause
     * @return ƥ������������ֵ,û���򷵻�"0"
     * @throws Exception ȡ������
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

        //ȷ��������Ӧ��,��Ӧȡֵ...
        Iterator itWC = wcSet.iterator();
        while (itWC.hasNext())
        {
            WhereClause wc = (WhereClause) itWC.next();

            //����Ҫ����where����Ϊ=�����,��or������ν��,ֵ��Ϊ��,�����ڵڶ�������
            if (!"=".equals(wc.cp))
            {
                continue;
            }

            //�õ���Ӧ�ֶ�
            String actualField = tableRelations.getActualField(wc.left,
                    tableName);
            //��Ϊdata�в�������
            if (actualField != null)
            {
//        String actualColumn=StringUtility.getRightWord(actualField,actualField.indexOf("."));
                String actualColumn = actualField;
                for (int i = 0; i < columnNames.length; i++)
                {
                    if (actualColumn.equalsIgnoreCase(columnNames[i]))
                    {
                        String value = wc.getRight();
                        if (value.startsWith("'")) //ȥ��"'"
                        {
                            value = value.substring(1, value.lastIndexOf("'"));
                        }

                        indexValueMap.put(new Integer(i), value);
                    }
                }
            }
        }

        int valueIdx = -1;

        //ȷ��ȡֵ�е�����
        for (int i = 0; i < columnNames.length; i++)
        {
            if (colName.equalsIgnoreCase(columnNames[i]))
            {
                valueIdx = i;
            }
        }

        log.debug("valueIdx:" + valueIdx);

        //��conditionת���ɱ�����Ӧ�Ĺ������ֶ�  ...��ͼ...���ܵ�������:��ͼ�ֶ�ʹ�ñ���:�����в�����.


        /**
         * 0.����TableRelation,����getActureField����,�����ֶ�ƥ���㷨��ת�ƽ�ȥ.
         * 1.�õ�ʵ�ʵ��ֶ�
         * 2.�������Ƶõ���Ӧ������
         * 3.ƥ�����Ӧ��ֵ
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
            //�ж�ÿ�������Ƿ�����
            while (it.hasNext())
            {
                Integer idx = (Integer) it.next();
                String value = (String) indexValueMap.get(idx);

                //ʹoracleʱ��һ��.
                if (!value.equals(line[idx.intValue()].trim()) &&
                    !dateEquals(value, line[idx.intValue()].trim()))
                {
                    allEquals = false;
                    break;
                }
            }

//      log.debug("allEquals:"+allEquals);

            if (allEquals) //��һ����������.............sum..........
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

                    if (!StringUtility.isNumber(line[valueIdx])) //������,������sum
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

        return double2Str(dValue); //�̶����ȡֵ������ֵ,����3.

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
                equals(right) //ʹ 2003-2-10 == 2003-02-10
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