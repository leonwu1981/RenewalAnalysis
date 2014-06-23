package com.sinosoft.xreport.bl;

import java.util.Set;

import org.apache.regexp.RE;

/**
 * 报表系统相关的正则表达式.
 * 可以验证定义的正确性,函数的解析等...
 * caution: \w不包含_,中文 <=>[a-zA-Z0-9]
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

    //例子...
    //(a.b+[资产].[保费].[分红])/[资产]+a(b,c,d())+b()

    //函数有递归问题...
    /**
     * 函数的正则表达式.
     */
    public static final String FUNCTION = "[a-zA-Z_$]+\\w*\\"; //[a-zA-Z_$]+[\w\s]*\([^\(\)]*\)

    //递归函数
    public static final String RECURSIONFUNCTION = "";
    //非递归函数  ...不包括括号,todo,可以包含非函数的运算括号
    public static final String NONERECFUNCTION =
            "[a-zA-Z_$]+[\\w\\s]*\\([^\\(\\)]*\\)"; //[a-zA-Z_$]+[\w\s]*\([^\(\)]*\)

    /**
     * 表名.字段名, 处理数据源时使用
     */
    public static final String TABLECOLUMN = "[\\w_$]+\\.[\\w_$]+"; // \w+\.\w+

    /**
     * 表名.字段名, 处理数据源时使用
     * re:    [\w_]+(<>|!=|>=|<=|>|<|=)\S+
     * text:  direction_idx<>11/11/11 and c=d
     * match: $0 = direction_idx<>11/11/11 $1 = <>
     * changed by Yang at 2003-4-7 10:15
     * reason: 条件左边可能有函数,如:mid()='',\w.不能匹配, 使用\S
     *
     * changed by Yang at 2003-4-11 10:34
     * reason: 条件左边函数中可能有',' 如 substr(a.b,1,4)
     *
     * changed by Yang at 2003-4-11 14:20
     * reason: (a=b or b=c)有问题,首字符必须为\w
     */
//  public static final String CONDITION="([\\w_\\.]+)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\\s*(\\S+)";  // [\w_]+(<>|!=|>=|<=|>|<|=)\S+
//  public static final String CONDITION="(\\S+)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\\s*(\\S+)";  // [\w_]+(<>|!=|>=|<=|>|<|=)\S+
//(([\w\(\)_\.,]+)\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\s*('.*?'))|(([\w\(\)_\.,]+)\s*(<>|!=|>=|<=|>|<|=| [iI][nN] )\s*(\S+))
    public static final String CONDITION = "(([\\w][\\w\\(\\)_\\.,]*)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] | [mM][aA][tT][cC][hH] | [lL][iI][kK][eE] )\\s*('.*?'))|(([\\w][\\w\\(\\)_\\.,]*)\\s*(<>|!=|>=|<=|>|<|=| [iI][nN] | [mM][aA][tT][cC][hH] | [lL][iI][kK][eE] )\\s*(\\S+))";


    /**
     * 特殊条件,包括所有非等于的比较sql表达式
     */
    public static final String SPCP = "\\b[oO][rR]\\b|\\b[iI][nN]\\b|\\b[mM][aA][tT][cC][hH]\\b|\\b[lL][iI][kK][eE]\\b|<>|!=|>=|<=|>|<";

    /**
     * [报表].[列].[行]
     */
    public static final String REPORTREF = "\\[[^-\\+\\*/]+\\]"; // \[[^-\+\*/]+\] 方括号之间没有+-*/

    /**
     * condition的验证表达式.
     * 如果匹配长度不等于输入字符串的trim()后的长度,应当是错误的
     */
    public static final String V_CONDITION = "([\\w_]+)(<>|!=|>=|<=|=|>|<)(\\S+)((\\s+and|\\s+or)\\s+([\\w_]+)(<>|!=|>=|<=|=|>|<)(\\S+))*"; //([\w_]+)(<>|!=|>=|<=|=|>|<)(\S+)((\s+and|\s+or)\s+([\w_]+)(<>|!=|>=|<=|=|>|<)(\S+))*
//                                         ^ 字段 ^ ^    比较符号     ^ 比较值^^^^   第二个...第n个 比较表达式                        ^^^^


    private static RE reWC; //条件的RE
    private static RE reDS; //数据源的RE
    private static RE reSPCP; //特殊条件的RE

    //初始化条件RE和数据源RE
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
     * 递归取所有的数据源.
     * @param s formula或者其它表达式
     * @param dsSet 结果集
     * @throws Exception 错误
     */
    public static void getAllDS(String s, Set dsSet) throws Exception
    {
        //递归出口
        if (null == s || "".equals(s))
        {
            return;
        }

        RE re = reDS;

        synchronized (re)
        {

            boolean b = re.match(s);

            //递归出口
            if (!b)
            {
                return;
            }

            String ds = re.getParen(0);
            dsSet.add(ds);

            //递归取余下的数据源
            getAllDS(s.substring(re.getParenEnd(0)), dsSet);
        }
    }


    /**
     * 递归取所有的where子句.
     * @param s condition或者其它表达式
     * @param dsSet 结果集,组成对象为WhereClause
     * @throws Exception 错误
     */
    public static void getWhereClause(String s, Set wcSet) throws Exception
    {
        //递归出口
        if (null == s || "".equals(s))
        {
            return;
        }

        RE re = reWC;

        //防止re同时匹配时出错.主要是在多用户时
        synchronized (re)
        {
            boolean b = re.match(s);

            //递归出口
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

            //给右边等式加上 ' ',
            if (wc.right.startsWith("'") && wc.right.endsWith("'"))
            {}
            else
            {
                wc.right = "'" + wc.right + "'";
            }

            wcSet.add(wc);

            //递归取余下的数据源
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

        //测试取数据源...
//    Set set=new HashSet();
//    getAllDS("sub.c+b.k+a*b",set);
//    System.out.println(set);
        //测试取数据源...pass

//    getWhereClause("direction_idx= 'a' and d=a()",set);
//    System.out.println(set);

        System.out.println(onlyAndCondition("a=b and b != c"));

    }

    /**
     * 判断是否有非'块'的表达式
     * 所有的都为and和=的表达式,为块,否则为非'块'
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
     * 是否参照了其它报表.
     * @param formula
     * @return
     * @throws Exception
     */
    public static boolean hasRefOtherReports(String formula) throws Exception
    {

        //参照到列,列头上不会细到单元格
//    RE re=new RE("\\b\\[.*\\]\\.\\[.*\\]\\b");  // \b\[.*\]\.\[.*\]\b

//    RE re=new RE("[^\\w]*\\[.+\\]\\.\\[.+\\][^\\w]*");

        RE re = new RE(
                "\\$[bst]q\\(\\[(.*?)\\]\\.\\[(.*?)\\](\\.\\[(.*)\\])?\\)");
        return re.match(formula);

    }

    /**
     * 是否有表内参照.
     * @param formula
     * @return
     * @throws Exception
     */
    public static boolean hasRefSelf(String formula) throws Exception
    {

        //参照到列,列头上不会细到单元格
//    RE re=new RE("\\b\\[.*\\]\\b");  // \b\[.*\]\.\[.*\]\b --error
//    RE re=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");  //[^\w]*\[.*\]\.\[.*\][^\w]*
        RE re = new RE("[^\\w\\.]*\\[([^\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        //yang 去掉了"."主要是[1.其它]都会有问题 at 2003-4-1 20:35

        return re.match(formula);

    }

}


/**
 * where子句的组成元素.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
class WhereClause
{
    //左表达式
    String left;
    //右表达式
    String right;
    //比较操作符
    String cp;
    //完整表达式
    String context;

//  public static WhereClause buildWC(String context) throws Exception
//  {
//    RE re=new RE(XRRegExp.CONDITION);
//
//    boolean b=re.match(context);
//
//    //递归出口
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