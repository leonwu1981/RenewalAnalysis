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
 * �������������й������������Ҫ����.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class TableRelations
{

    //SQL����,��Ҫ����,��ֹ��Ϊ�ֶ�ȥ��Ӧ.
    static String[] SQLFUNC =
            {
            "SUBSTR",
            "ADDMONTH",
            "TO_CHAR"
    };

    //datasource
    DataSource dbDataSource;

    //��Ĳ��ն���
    Collection relation;

    //�������ܵ����.
    static final String COLUMNNAME = "[a-zA-Z][_\\w\\.]*";

    //���е�����
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
     * ���ݹ�������ֶ�,ת��Ϊ�����ֶ�
     * @param sourceField ԭ�ֶ�
     * @param targetTable Ŀ���
     * @return Ŀ����ֶ�,����Ϊ��
     */
    private String getConvertField_(String sourceField, String targetTable) throws
            Exception
    {
        //�����sourceFiele�е�.��Ҫ�滻Ϊ\.
        sourceField = Str.replace(sourceField, ".", "\\.");

        //add by yang at 2003-4-10 15:16
        //�ֶβ��ִ�Сд
        sourceField = sourceField.toUpperCase();
        targetTable = targetTable.toUpperCase();

        //����datasourceSchema�Ķ������
        //e.g. (\bsourcetable\.con\s*=(\s*target\.[\w_]*)\b)|((\btarget\.[\w_]*)\s*=\s*sourcetable\.con\b)
//    String pattern="(\\b"+sourceField+"\\s*=(\\s*"+targetTable+"\\.[\\w_]*)\\b)|((\\b"
//                  +targetTable+"\\.[\\w_]*)\\s*=\\s*"+sourceField+"\\b)";
//////////////////////////////////////
// �����д�,�� a.b=c_d.e ������Ϊ��a.b ���Ժ�d.e ����,��Ҫ��Ϊ\baaa ��ƥ�� _aaa
// ���Ա���ȥ���»���_,�����ַ���_����ĸ.
/////////////////////////////////////
        String pattern = "([\\[\\s,]+" + sourceField + "\\s*=(\\s*" +
                         targetTable + "\\.[\\w_]*)[\\s\\],]+)|(([\\[\\s,]+"
                         + targetTable + "\\.[\\w_]*)\\s*=\\s*" + sourceField +
                         "[\\s\\],]+)";

        RE re = new RE(pattern);

//    Logger log=XTLogger.getLogger(this.getClass());
//    log.debug(pattern);

        //���ִ�Сд.
        boolean matched = re.match(relationString);

        if (!matched)
        {
            return null;
        }

        return (re.getParen(2) != null) ? re.getParen(2) : re.getParen(4);

    }

    /**
     * ��ά��[��->�ֶμ���Set]
     */
    Map tableColumnMap;

    /**
     * ������Դ�������
     * @throws Exception
     */
    private void parseDefine() throws Exception
    {
        Collection cc = dbDataSource.getDimensionDefine();

        Iterator i = cc.iterator();

        while (i.hasNext())
        {
            Dim d = (Dim) i.next();
            String table = d.getDataSourceId().toUpperCase(); //���ִ�Сд

            Set columnSet = (Set) tableColumnMap.get(table);
            if (columnSet == null)
            {
                columnSet = new HashSet();
                tableColumnMap.put(table, columnSet);
            }

            columnSet.add(d.getDimenId().toUpperCase()); //���ִ�Сд

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
            //�ڵ�ʽ���
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

            //�ڵ�ʽ�ұ�
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

            leftList.add(left); //��Ӧ����
            rightList.add(right); //��Ӧ����
        }

    }

    /**
     * �ж�ĳ���ֶ��Ƿ�����ĳ������
     * @param table ����
     * @param column �ֶ���,�ֶβ�������.
     * @return true-���������ֶ�
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
     * �õ�condition��ʵ���ֶ�.
     * condition���ֶο����Ǳ�����ֶ�,�û�����Ĺ�������ֶ�,��������ͬ�����ֶ�.
     * @change by yang 2003/04/08 ����ֵ�в���������.
     * @param sourceField condition�е�Դ�ֶ�
     * @param targetTable ������
     * @return ʵ�ʵ��ֶ�
     * @throws Exception
     */
    private String getActualField0(String sourceField, String targetTable) throws
            Exception
    {

        String actualField = null;

        //�ֶ�����ͬ,�Զ��ϲ�...��Ҫ��֤..
        //������ͬ?
        int dotPos = sourceField.indexOf(".");
        if (dotPos < 0) //û�б���,ֻ��һ����,ʡ���˱���
        {
            //���Ƿ��Ǳ����ֶ�
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
            //�����б���
            String conTableName = StringUtility.getLeftWord(sourceField, dotPos);
            //����������
            String conColName = StringUtility.getRightWord(sourceField, dotPos);

            //���������Դ������
            if (targetTable.equals(conTableName))
            {
                actualField = conColName;
                return actualField;
            }
            //�����������ֶ�,������
            else
            {
                //�ѵ�����
                String relateField = getConvertField(sourceField, targetTable);
                if (relateField != null)
                {
//          currentCon = buildInClause(relateField,(Set)colValues.get(currentColumn));
                    actualField = relateField;
                    return StringUtility.getRightWord(actualField,
                            actualField.indexOf(".")); //ȥ������
                }
                else //û�ѵ�����,ƥ����ͬ���ֵ��ֶ�? .....���ܵ��´���......
                {
                    if (contains(targetTable, conColName))
                    {
//            currentCon = buildInClause(currentTable+"."+conColName,(Set)colValues.get(currentColumn));
                        return conColName;
                    }
                    else //����,��û���ҵ�,ֻ�ò�������
                    {
                        return null;
                    }
                }
            }

        } //end if

//    return actualField;

    }

    /**
     * �õ�condition��ʵ���ֶ�.
     * condition���ֶο����Ǳ�����ֶ�,�û�����Ĺ�������ֶ�,��������ͬ�����ֶ�.
     * @change by yang 2003/04/11 ���Ӷ�substr��֧��.
     * @param sourceField condition�е�Դ�ֶ�
     * @param targetTable ������
     * @return ʵ�ʵ��ֶ�
     * @throws Exception
     */
    public String getActualField(String sourceField, String targetTable) throws
            Exception
    {
        String result = sourceField;
        //���µ��ַ���
        String rest = sourceField;

        //ȡ�����ֶ�
        while (reCN.match(rest))
        {
            //����.
            String cn = reCN.getParen(0);
            if (isSqlFunc(cn)) //��sql����
            {
            }
            else
            {
                String actualCN = getActualField0(cn, targetTable);
                if (actualCN == null) //�в�ƥ����ֶ�,����null
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
     * ȡת�����condition.
     * ������condition�����,ȡ���е�whereClause,��������ת��Ϊʵ���ֶ�.
     * @param condition �л���������
     * @param targetTable Ŀ�ı�
     * @return ת���������
     * @throws Exception �쳣
     */
    public String getActualCondition(String condition, String targetTable) throws
            Exception
    {
        if (null == condition || "".equals(condition))
        {
            return " 1=1 "; //�޹ؽ�Ҫ������
        }

        String result = condition;

        Set wcSet = new HashSet();
        //ȡ���������Ӿ�
        XRRegExp.getWhereClause(condition, wcSet);
        //����滻
        Iterator itWC = wcSet.iterator();
        while (itWC.hasNext())
        {
            WhereClause wc = (WhereClause) itWC.next();
            String actualField = getActualField(wc.getLeft(), targetTable);
            String convertedWC = " 1=1 ";
            if (actualField == null) //���Ǳ����ֶ�
            {}
            else //�Ǳ����ֶ�
            {
                convertedWC = actualField + wc.getCp() + wc.getRight();
            }
            //�滻����Ϊ�����ֶ�
            result = StringUtility.replaceWord(result, wc.getContext(),
                                               convertedWC);

        }

        return result;
    }


    /**
     * �жϵ����Ƿ���sql�ĺ���.
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
