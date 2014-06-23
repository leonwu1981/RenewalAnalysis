package com.sinosoft.xreport.bl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sinosoft.xreport.dl.BufferDataSourceImpl;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.dl.Dim;
import com.sinosoft.xreport.util.Str;
import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;


/**
 * 包含解析定义中关联表的两个重要操作.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class TableRelations
{

    //SQL函数,不要处理,防止作为字段去对应.
    static String[] SQLFUNC =
            {
            "SUBSTR",
            "ADDMONTH",
            "TO_CHAR"
    };

    //datasource
    DataSource dbDataSource;

    //表的参照定义
    Collection relation;

    //列名可能的情况.
    static final String COLUMNNAME = "[a-zA-Z][_\\w\\.]*";

    //所有的列名
    RE reCN;

    public TableRelations(DataSource dataSource) throws Exception
    {
        reCN = new RE(COLUMNNAME);

        setDataSource(dataSource);
    }

    String relationString;

    public void setDataSource(DataSource dataSource) throws Exception
    {
        this.dbDataSource = dataSource;
        relation = dbDataSource.getRelations();
        relationString = relation.toString().toUpperCase();
        tableColumnMap = new HashMap();
        parseDefine();

    }

    private void buildMap()
    {

    }

    /**
     * 根据关联表的字段,转换为本表字段
     * @param sourceField 原字段
     * @param targetTable 目标表
     * @return 目标表字段,否则为空
     */
    private String getConvertField_(String sourceField, String targetTable) throws
            Exception
    {
        //传入的sourceFiele中的.需要替换为\.
        sourceField = Str.replace(sourceField, ".", "\\.");

        //add by yang at 2003-4-10 15:16
        //字段不分大小写
        sourceField = sourceField.toUpperCase();
        targetTable = targetTable.toUpperCase();

        //搜索datasourceSchema的定义关联
        //e.g. (\bsourcetable\.con\s*=(\s*target\.[\w_]*)\b)|((\btarget\.[\w_]*)\s*=\s*sourcetable\.con\b)
//    String pattern="(\\b"+sourceField+"\\s*=(\\s*"+targetTable+"\\.[\\w_]*)\\b)|((\\b"
//                  +targetTable+"\\.[\\w_]*)\\s*=\\s*"+sourceField+"\\b)";
//////////////////////////////////////
// 以上有错,将 a.b=c_d.e 错误认为是a.b 可以和d.e 关联,主要因为\baaa 会匹配 _aaa
// 所以必须去除下划线_,左右字符非_和字母.
/////////////////////////////////////
        String pattern = "([\\[\\s,]+" + sourceField + "\\s*=(\\s*" +
                         targetTable + "\\.[\\w_]*)[\\s\\],]+)|(([\\[\\s,]+"
                         + targetTable + "\\.[\\w_]*)\\s*=\\s*" + sourceField +
                         "[\\s\\],]+)";

        RE re = new RE(pattern);

//    Logger log=XTLogger.getLogger(this.getClass());
//    log.debug(pattern);

        //不分大小写.
        boolean matched = re.match(relationString);

        if (!matched)
        {
            return null;
        }

        return (re.getParen(2) != null) ? re.getParen(2) : re.getParen(4);

    }

    /**
     * 二维的[表->字段集合Set]
     */
    Map tableColumnMap;

    /**
     * 将数据源定义解析
     * @throws Exception
     */
    private void parseDefine() throws Exception
    {
        Collection cc = dbDataSource.getDimensionDefine();

        Iterator i = cc.iterator();

        while (i.hasNext())
        {
            Dim d = (Dim) i.next();
            String table = d.getDataSourceId().toUpperCase(); //不分大小写

            Set columnSet = (Set) tableColumnMap.get(table);
            if (columnSet == null)
            {
                columnSet = new HashSet();
                tableColumnMap.put(table, columnSet);
            }

            columnSet.add(d.getDimenId().toUpperCase()); //不分大小写

        }
    }

    List leftList;
    List rightList;

    public String getConvertField(String sourceField, String targetTable) throws
            Exception
    {
        if (leftList == null)
        {
            getLRList();
        }

        for (int i = 0; i < leftList.size(); i++)
        {
            //在等式左边
            if (sourceField.trim().toUpperCase().equals(leftList.get(i)))
            {
                String rightTable = (String) rightList.get(i);
                int dotPos = rightTable.indexOf(".");
                String table = rightTable.substring(0, dotPos);
                String column = rightTable.substring(dotPos + 1);

                if (targetTable.trim().toUpperCase().equals(table))
                {
                    return rightTable;
                }
            }

            //在等式右边
            if (sourceField.trim().toUpperCase().equals(rightList.get(i)))
            {
                String leftTable = (String) leftList.get(i);
                int dotPos = leftTable.indexOf(".");
                String table = leftTable.substring(0, dotPos);
                String column = leftTable.substring(dotPos + 1);

                if (targetTable.trim().toUpperCase().equals(table))
                {
                    return leftTable;
                }
            }
        }

        return null;
    }

    Logger log = XTLogger.getLogger(getClass());

    private void getLRList()
    {
        leftList = new Vector();
        rightList = new Vector();

        Iterator it = relation.iterator();

        while (it.hasNext())
        {
            String context = String.valueOf(it.next());

            int pos = context.indexOf("=");

            String left = context.substring(0, pos).trim().toUpperCase();
            String right = context.substring(pos + 1).trim().toUpperCase();

            leftList.add(left); //对应加入
            rightList.add(right); //对应加入
        }

    }

    /**
     * 判断某个字段是否是在某个表中
     * @param table 表名
     * @param column 字段名,字段不含表名.
     * @return true-表包含这个字段
     */
    public boolean contains(String table, String column)
    {
        table = table.toUpperCase();
        column = column.toUpperCase();

        try
        {
            return ((Set) tableColumnMap.get(table)).contains(column);
        }
        catch (Exception ex)
        {
            return false;
        }

    }

    /**
     * 得到condition的实际字段.
     * condition的字段可能是本表的字段,用户定义的关联表的字段,关联表中同名的字段.
     * @change by yang 2003/04/08 返回值中不包含表名.
     * @param sourceField condition中的源字段
     * @param targetTable 关联表
     * @return 实际的字段
     * @throws Exception
     */
    private String getActualField0(String sourceField, String targetTable) throws
            Exception
    {

        String actualField = null;

        //字段名相同,自动合并...需要论证..
        //表名相同?
        int dotPos = sourceField.indexOf(".");
        if (dotPos < 0) //没有表名,只有一个表,省略了表名
        {
            //看是否是本表字段
            if (contains(targetTable, sourceField))
            {
                return sourceField;
            }
            else
            {
                return null;
            }
        }
        else
        {
            //条件中表名
            String conTableName = StringUtility.getLeftWord(sourceField, dotPos);
            //条件中列名
            String conColName = StringUtility.getRightWord(sourceField, dotPos);

            //是这个数据源的条件
            if (targetTable.equals(conTableName))
            {
                actualField = conColName;
                return actualField;
            }
            //不是这个表的字段,看关联
            else
            {
                //搜到关联
                String relateField = getConvertField(sourceField, targetTable);
                if (relateField != null)
                {
//          currentCon = buildInClause(relateField,(Set)colValues.get(currentColumn));
                    actualField = relateField;
                    return StringUtility.getRightWord(actualField,
                            actualField.indexOf(".")); //去掉表名
                }
                else //没搜到关联,匹配相同名字的字段? .....可能导致错误......
                {
                    if (contains(targetTable, conColName))
                    {
//            currentCon = buildInClause(currentTable+"."+conColName,(Set)colValues.get(currentColumn));
                        return conColName;
                    }
                    else //绝望,再没有找到,只好不理它了
                    {
                        return null;
                    }
                }
            }

        } //end if

//    return actualField;

    }

    /**
     * 得到condition的实际字段.
     * condition的字段可能是本表的字段,用户定义的关联表的字段,关联表中同名的字段.
     * @change by yang 2003/04/11 增加对substr的支持.
     * @param sourceField condition中的源字段
     * @param targetTable 关联表
     * @return 实际的字段
     * @throws Exception
     */
    public String getActualField(String sourceField, String targetTable) throws
            Exception
    {
        String result = sourceField;
        //余下的字符串
        String rest = sourceField;

        //取所有字段
        while (reCN.match(rest))
        {
            //列名.
            String cn = reCN.getParen(0);
            if (isSqlFunc(cn)) //是sql函数
            {
            }
            else
            {
                String actualCN = getActualField0(cn, targetTable);
                if (actualCN == null) //有不匹配的字段,返回null
                {
                    return null;
                }
                else
                {
                    result = StringUtility.replaceWord(result, cn, actualCN);
                }
            }

            rest = rest.substring(reCN.getParenEnd(0));
            continue;
        }

        return result;
    }

    /**
     * 取转换后的condition.
     * 将整个condition传入后,取所有的whereClause,将左条件转换为实际字段.
     * @param condition 行或者列条件
     * @param targetTable 目的表
     * @return 转换后的条件
     * @throws Exception 异常
     */
    public String getActualCondition(String condition, String targetTable) throws
            Exception
    {
        if (null == condition || "".equals(condition))
        {
            return " 1=1 "; //无关紧要的条件
        }

        String result = condition;

        Set wcSet = new HashSet();
        //取所有条件子句
        XRRegExp.getWhereClause(condition, wcSet);
        //逐个替换
        Iterator itWC = wcSet.iterator();
        while (itWC.hasNext())
        {
            WhereClause wc = (WhereClause) itWC.next();
            String actualField = getActualField(wc.getLeft(), targetTable);
            String convertedWC = " 1=1 ";
            if (actualField == null) //不是本表字段
            {}
            else //是本表字段
            {
                convertedWC = actualField + wc.getCp() + wc.getRight();
            }
            //替换条件为本表字段
            result = StringUtility.replaceWord(result, wc.getContext(),
                                               convertedWC);

        }

        return result;
    }


    /**
     * 判断单词是否是sql的函数.
     * @param word
     * @return
     */
    private boolean isSqlFunc(String word)
    {
        for (int i = 0; i < SQLFUNC.length; i++)
        {
            if (SQLFUNC[i].equalsIgnoreCase(word))
            {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws Exception
    {
        DataSource dataSource = new BufferDataSourceImpl();

        TableRelations tr = new TableRelations(dataSource);

        System.out.println(tr.getActualField("substr(LMRisk.Riskcode,4,1)",
                                             "LJAPayPerson_LMRisk"));

        System.out.println(tr.getActualCondition(
                "substr(LMRisk.Riskcode,4,1)!='1' and LMRisk.Riskcode=2",
                "LJAPayPerson_LMRisk"));

        System.out.println(tr.getActualCondition("VIEW_LCPol.SaleChnl='02' and substr(VIEW_LCPol.RiskCode,4,1)!=6 and substr(VIEW_LCPol.RiskCode,4,1)!=7",
                                                 "LJAPayPerson_LCPol"));

        System.out.println(tr.getActualCondition("view_lcpol.riskcode=1",
                                                 "LCPol_LMRiskApp"));

        System.out.println(tr.getActualCondition("VIEW_LCPol.RiskCode=1",
                                                 "LJAGetEndorse_LMRiskApp"));
    }

}
