package com.sinosoft.xreport.bl;

import java.util.Set;

import org.apache.regexp.RE;

/**
 * ����ϵͳ��ص�������ʽ.
 * ������֤�������ȷ��,�����Ľ�����...
 * caution: \w������_,���� <=>[a-zA-Z0-9]
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class XRRegExp
{

    //����...
    //(a.b+[�ʲ�].[����].[�ֺ�])/[�ʲ�]+a(b,c,d())+b()

    //�����еݹ�����...
    /**
     * ������������ʽ.
     */
    public static final String FUNCTION = "[a-zA-Z_$]+\\w*\\"; //[a-zA-Z_$]+[\w\s]*\([^\(\)]*\)

    //�ݹ麯��
    public static final String RECURSIONFUNCTION = "";
    //�ǵݹ麯��  ...����������,todo,���԰����Ǻ�������������
    public static final String NONERECFUNCTION =
            "[a-zA-Z_$]+[\\w\\s]*\\([^\\(\\)]*\\)"; //[a-zA-Z_$]+[\w\s]*\([^\(\)]*\)

    /**
     * ����.�ֶ���, ��������Դʱʹ��
     */
    public static final String TABLECOLUMN = "[\\w_$]+\\.[\\w_$]+"; // \w+\.\w+

    /**
     * ����.�ֶ���, ��������Դʱʹ��
     * re:    [\w_]+(<>|!=|>=|<=|>|<|=)\S+
     * text:  direction_idx<>11/11/11 and c=d
     * match: $0 = direction_idx<>11/11/11 $1 = <>
     * changed by Yang at 2003-4-7 10:15
     * reason: ������߿����к���,��:mid()='',\w.����ƥ��, ʹ��\S
     *
     * changed by Yang at 2003-4-11 10:34
     * reason: ������ߺ����п�����',' �� substr(a.b,1,4)
     *
     * changed by Yang at 2003-4-11 14:20
     * reason: (a=b or b=c)������,���ַ�����Ϊ\w
     */
//  public static final String CONDITION="([\\w_\\.]+)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\\s*(\\S+)";  // [\w_]+(<>|!=|>=|<=|>|<|=)\S+
//  public static final String CONDITION="(\\S+)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\\s*(\\S+)";  // [\w_]+(<>|!=|>=|<=|>|<|=)\S+
//(([\w\(\)_\.,]+)\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\s*('.*?'))|(([\w\(\)_\.,]+)\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\s*(\S+))
    public static final String CONDITION = "(([\\w][\\w\\(\\)_\\.,]*)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] | [mM][aA][tT][cC][hH] | [lL][iI][kK][eE] )\\s*('.*?'))|(([\\w][\\w\\(\\)_\\.,]*)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] | [mM][aA][tT][cC][hH] | [lL][iI][kK][eE] )\\s*(\\S+))";


    /**
     * ��������,�������зǵ��ڵıȽ�sql���ʽ
     */
    public static final String SPCP = "\\b[oO][rR]\\b|\\b[iI][nN]\\b|\\b[mM][aA][tT][cC][hH]\\b|\\b[lL][iI][kK][eE]\\b|<>|!=|>=|<=|>|<";

    /**
     * [����].[��].[��]
     */
    public static final String REPORTREF = "\\[[^-\\+\\*/]+\\]"; // \[[^-\+\*/]+\] ������֮��û��+-*/

    /**
     * condition����֤���ʽ.
     * ���ƥ�䳤�Ȳ����������ַ�����trim()��ĳ���,Ӧ���Ǵ����
     */
    public static final String V_CONDITION = "([\\w_]+)(<>|!=|>=|<=|=|>|<)(\\S+)((\\s+and|\\s+or)\\s+([\\w_]+)(<>|!=|>=|<=|=|>|<)(\\S+))*"; //([\w_]+)(<>|!=|>=|<=|=|>|<)(\S+)((\s+and|\s+or)\s+([\w_]+)(<>|!=|>=|<=|=|>|<)(\S+))*
//                                         ^ �ֶ� ^ ^    �ȽϷ���     ^ �Ƚ�ֵ^^^^   �ڶ���...��n�� �Ƚϱ��ʽ                        ^^^^


    private static RE reWC; //������RE
    private static RE reDS; //����Դ��RE
    private static RE reSPCP; //����������RE

    //��ʼ������RE������ԴRE
    static
    {
        try
        {
            reWC = new RE(CONDITION);
            reDS = new RE(TABLECOLUMN);
            reSPCP = new RE(SPCP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public XRRegExp()
    {

    }

//  public static  void getFuncs(String in,Stack stack) throws Exception
//  {
//    RE r=new RE(FUNCTION);
//    boolean matched=r.match(in);
//
//    if(!matched)
//      return ;
//
//    int count=r.getParenCount();
//
//    for(int i=0;i<count;i++)
//    {
//
//    }
//
//  }


    //  public String

    /**
     * �ݹ�ȡ���е�����Դ.
     * @param s formula�����������ʽ
     * @param dsSet �����
     * @throws Exception ����
     */
    public static void getAllDS(String s, Set dsSet) throws Exception
    {
        //�ݹ����
        if (null == s || "".equals(s))
        {
            return;
        }

        RE re = reDS;

        synchronized (re)
        {

            boolean b = re.match(s);

            //�ݹ����
            if (!b)
            {
                return;
            }

            String ds = re.getParen(0);
            dsSet.add(ds);

            //�ݹ�ȡ���µ�����Դ
            getAllDS(s.substring(re.getParenEnd(0)), dsSet);
        }
    }


    /**
     * �ݹ�ȡ���е�where�Ӿ�.
     * @param s condition�����������ʽ
     * @param dsSet �����,��ɶ���ΪWhereClause
     * @throws Exception ����
     */
    public static void getWhereClause(String s, Set wcSet) throws Exception
    {
        //�ݹ����
        if (null == s || "".equals(s))
        {
            return;
        }

        RE re = reWC;

        //��ֹreͬʱƥ��ʱ����.��Ҫ���ڶ��û�ʱ
        synchronized (re)
        {
            boolean b = re.match(s);

            //�ݹ����
            if (!b)
            {
                return;
            }

            WhereClause wc = new WhereClause();

            if (re.getParen(8) != null)
            {
                wc.context = re.getParen(0);
                wc.left = re.getParen(6);
                wc.cp = re.getParen(7);
                wc.right = re.getParen(8);
            }
            else
            {
                wc.context = re.getParen(0);
                wc.left = re.getParen(2);
                wc.cp = re.getParen(3);
                wc.right = re.getParen(4);
            }

            //���ұߵ�ʽ���� ' ',
            if (wc.right.startsWith("'") && wc.right.endsWith("'"))
            {}
            else
            {
                wc.right = "'" + wc.right + "'";
            }

            wcSet.add(wc);

            //�ݹ�ȡ���µ�����Դ
            getWhereClause(s.substring(re.getParenEnd(0)), wcSet);
        }

    }


    public static void main(String[] args) throws Exception
    {
//    //test subst
//    XRRegExp XRRegExp1 = new XRRegExp();
//    RE re=new RE("a*b");
//    String result=re.subst("aaabcab","-",RE.REPLACE_FIRSTONLY);
        //test  is successful.

        //����ȡ����Դ...
//    Set set=new HashSet();
//    getAllDS("sub.c+b.k+a*b",set);
//    System.out.println(set);
        //����ȡ����Դ...pass

//    getWhereClause("direction_idx= 'a' and d=a()",set);
//    System.out.println(set);

        System.out.println(onlyAndCondition("a=b and b != c"));

    }

    /**
     * �ж��Ƿ��з�'��'�ı��ʽ
     * ���еĶ�Ϊand��=�ı��ʽ,Ϊ��,����Ϊ��'��'
     * @param condition
     * @return
     * @throws Exception
     */
    public static boolean onlyAndCondition(String condition) throws Exception
    {
        if (null == condition || "".equals(condition))
        {
            return true;
        }

        //
        RE re = reSPCP;

        synchronized (re)
        {
            return!re.match(condition);
        }

    }

    /**
     * �Ƿ��������������.
     * @param formula
     * @return
     * @throws Exception
     */
    public static boolean hasRefOtherReports(String formula) throws Exception
    {

        //���յ���,��ͷ�ϲ���ϸ����Ԫ��
//    RE re=new RE("\\b\\[.*\\]\\.\\[.*\\]\\b");  // \b\[.*\]\.\[.*\]\b

//    RE re=new RE("[^\\w]*\\[.+\\]\\.\\[.+\\][^\\w]*");

        RE re = new RE(
                "\\$[bst]q\\(\\[(.*?)\\]\\.\\[(.*?)\\](\\.\\[(.*)\\])?\\)");
        return re.match(formula);

    }

    /**
     * �Ƿ��б��ڲ���.
     * @param formula
     * @return
     * @throws Exception
     */
    public static boolean hasRefSelf(String formula) throws Exception
    {

        //���յ���,��ͷ�ϲ���ϸ����Ԫ��
//    RE re=new RE("\\b\\[.*\\]\\b");  // \b\[.*\]\.\[.*\]\b --error
//    RE re=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");  //[^\w]*\[.*\]\.\[.*\][^\w]*
        RE re = new RE("[^\\w\\.]*\\[([^\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        //yang ȥ����"."��Ҫ��[1.����]���������� at 2003-4-1 20:35

        return re.match(formula);

    }

}


/**
 * where�Ӿ�����Ԫ��.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
class WhereClause
{
    //����ʽ
    String left;
    //�ұ��ʽ
    String right;
    //�Ƚϲ�����
    String cp;
    //�������ʽ
    String context;

//  public static WhereClause buildWC(String context) throws Exception
//  {
//    RE re=new RE(XRRegExp.CONDITION);
//
//    boolean b=re.match(context);
//
//    //�ݹ����
//    if(!b)
//      return null;
//
//    WhereClause wc=new WhereClause();
//    wc.context = re.getParen(0);
//    wc.left = re.getParen(1);
//    wc.cp = re.getParen(2);
//    wc.right = re.getParen(3);
//
//    return wc;
//
//  }

    public String toString()
    {
        return context;
    }


    public int hashCode()
    {
        return context.hashCode();
    }

    public boolean equals(Object wc)
    {
        if (wc instanceof String)
        {
            return wc.equals(context);
        }
        else if (wc instanceof WhereClause)
        {
            return ((WhereClause) wc).context.equals(context);
        }
        return false;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getCp()
    {
        return cp;
    }

    public void setCp(String cp)
    {
        this.cp = cp;
    }

    public String getLeft()
    {
        return left;
    }

    public void setLeft(String left)
    {
        this.left = left;
    }

    public String getRight()
    {
        return right;
    }

    public void setRight(String right)
    {
        this.right = right;
    }

}