package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sinosoft.xreport.util.Str;
import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.NodeList;

public class DataSourceImpl implements DataSource
{

    Collection allcol = new Vector(); //构造函数完成的结构
    Hashtable lasths = new Hashtable();
    private int flag = 0; //标志位，标志是否已经解析过XML
    static TableXMLParse tableParse = null; //存放解析过的XML对象


    private boolean isParse()
    {
        if (flag == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private TableXMLParse parse() throws Exception
    {
        if (isParse())
        {
            return tableParse;
        }
        else
        {
            tableParse = new TableXMLParse("");
            tableParse.doParseXML();
            flag = 1;
            return tableParse;
        }
    }


    public DataSourceImpl() throws Exception
    {

        Logger logger = XTLogger.getLogger(DataSourceImpl.class);
        //    TableXMLParse txp = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
        //    txp.doParseXML();//解析XML文件

        TableXMLParse txp = parse();
        NodeList nl = txp.getAllModels();
        Collection collection = new Vector();

        for (int i = 0; i < nl.getLength(); i++)
        {
            Model m = new Model(nl.item(i));
            NodeList collist = m.getAllColumns();
            Vector tabVec = new Vector();
            Hashtable colhash = new Hashtable();
            //Vector intabVec = new Vector();
            for (int j = 0; j < collist.getLength(); j++)
            {
                //colhash = new Hashtable();
                Column c = new Column(collist.item(j));
                String tabid = m.getModelID();
                String colid = c.getColumnName();
                String key = tabid + "." + colid;
                String treemode = c.getColumnTreeMode();
                colhash.put("TableName", tabid);
                //        Hashtable colhash = new Hashtable();
                if (isSQL(treemode)) //如果是SQL类型
                {
                    String tmp = remove(treemode);
                    if (isHave(tmp)) //如果有$符号
                    {
                        StringTokenizer st = new StringTokenizer(tmp, "=");
                        int count = st.countTokens();
                        Vector colvec = new Vector();
                        st.nextElement();
                        int flag = 0;
                        while (st.hasMoreElements())
                        {
                            String s = String.valueOf(st.nextElement());
                            if (flag == count - 2)
                            {
                                int qq = s.indexOf("$");
                                String getstr = s.substring(qq + 1).trim();
                                colvec.add(getstr);
                            }
                            else
                            {
                                int hh = s.indexOf("and");
                                int qq = s.indexOf("$");
                                String getstr = s.substring(qq + 1, hh).trim();
                                colvec.add(getstr);
                            }
                            flag++;
                        }
                        //colhash = new Hashtable();
                        colhash.put(colid, colvec);
                    }
                    else //如果没有$符号
                    {
                        //colhash = new Hashtable();
                        colhash.put(colid, "0");
                    }
                }
            }
            //      collection.add(tabVec);
            collection.add(colhash);
        }
        logger.info("The preview construct build complete!");
        //保存完毕
        //打印
        //    System.out.println("colleciton's length is : " + collection.size());
        //    Vector myvec = (Vector)collection;
        //    for(int i=0;i<myvec.size();i++)
        //    {
        //      Hashtable h = (Hashtable)myvec.get(i);
        //      Enumeration e = h.keys();
        //      while(e.hasMoreElements())
        //      {
        //        Object ob = e.nextElement();
        //        if(!ob.equals("TableName"))
        //          System.out.println(ob + "--------------" + h.get(ob));
        //      }
        //      System.out.println(h.size());
        //    }
        //打印完毕
        //连接

        Vector collect = (Vector) collection;
        for (int i = 0; i < collect.size(); i++)
        {
            Hashtable hs = (Hashtable) collect.get(i);
            showAll(hs);
        }

        logger.info("The construct build complete!");
        //连接完毕

        //System.out.println("colleciton's length is : " + collection.size());
        //    Vector myvec1 = (Vector)collection;
        //    for(int i = 0; i < myvec1.size(); i++)
        //    {
        //      Hashtable h = (Hashtable)myvec1.get(i);
        //      Enumeration e = h.keys();
        //      while(e.hasMoreElements())
        //      {
        //        Object ob = e.nextElement();
        //        if(!ob.equals("TableName"))
        //          System.out.println(ob + "--------------" + h.get(ob));
        //      }
        //    }
        //打印最终
        //    System.out.println("打印结果 ：");
        //    Vector vec = (Vector)allcol;
        //    for(int i=0;i<vec.size();i++)
        //    {
        //      Hashtable h = (Hashtable)vec.get(i);
        //      Enumeration e = h.keys();
        //      while(e.hasMoreElements())
        //      {
        //        Object ob = e.nextElement();
        //        if(!ob.equals("TableName"))
        //          System.out.println(ob + "--------------" + h.get(ob));
        //      }
        //    }
        //打印最终

    }

    /**
     * 循环遍历哈希表hs，得到最终的父节点映射关系
     * @param hs 一个粗略的哈希表
     */
    private void showAll(Hashtable hs)
    {

        lasths = new Hashtable();
        Enumeration e = hs.keys();

        while (hs.size() > 0)
        {
            if (!e.hasMoreElements())
            {
                e = hs.keys();
                //System.out.println("i got :"+ hs.size());
            }
            while (e.hasMoreElements())
            {
                Object ob = e.nextElement();
                if (String.valueOf(ob).equals("TableName"))
                {
                    lasths.put(ob, hs.get(ob));
                    hs.remove(ob);
                }
                else if (hs.get(ob).equals("0"))
                {
                    lasths.put(ob, "0");
                    hs.remove(ob);
                }
                else
                {
                    Vector tmp = (Vector) hs.get(ob);
                    String str1 = String.valueOf(tmp.get(0));
                    if (tmp.size() == 1 && lasths.containsKey(str1))
                    {
                        int sco = Integer.parseInt(String.valueOf(lasths.get(
                                str1))) + 1;
                        lasths.put(ob, String.valueOf(sco));
                        hs.remove(ob);
                    }
                    else
                    {
                        int flag[] = new int[tmp.size()]; //标志位
                        //for(int k=0;k<tmp.size();k++)
                        //  flag[k] = 0;
                        int haveflag = 0;
                        String str = null;
                        for (int ii = 0; ii < tmp.size(); ii++)
                        {
                            str = String.valueOf(tmp.get(ii));
                            if (lasths.containsKey(str))
                            {
                                //tmp.remove(ob);
                                flag[ii] = 1;
                                haveflag++;
                            }
                        }
                        if (haveflag == tmp.size()) //如果都存在在lasths中
                        {
                            //取最大的一个级别
                            int level[] = new int[haveflag];
                            for (int lev = 0; lev < haveflag; lev++)
                            {
                                level[lev] = Integer.parseInt(String.valueOf(
                                        lasths.get(tmp.get(lev))));
                            }
                            int max = getMax(level);
                            lasths.put(ob, String.valueOf(max + 1));
                            hs.remove(ob);
                        }
                        else //有不存在的，把不存在的留下
                        {
                            for (int jj = 0; jj < flag.length; jj++)
                            {
                                if (flag[jj] == 1)
                                {
                                    tmp.remove(jj);
                                }
                            }
                        }
                    }
                }
            }
        }
        allcol.add(lasths);
    }


    /**
     * 取一个整数数组中最大值
     * @param a[] 一个整数数组
     * @return 最大整数值
     */
    private int getMax(int[] a)
    {
        int max = 0;
        for (int i = 0; i < a.length; i++)
        {
            if (a[i] > max)
            {
                max = a[i];
            }
        }
        return max;
    }

    /**
     * 执行sql返回数据集.注意实现要取出全部数据后释放连接
     * @param sql sql查询语句
     * @return 结果集
     * @throws Throwable 出错信息
     */
    //  private DataSourceInf dsi = null;
    //  private DBConnParam db = null;
    //  private DataSourceInf getDSI() throws Exception
    //  {
    //    if(db == null)
    //      db = new DBConnParam();
    //    if(dsi == null)
    //    {
    //      ConnectionFactory.bind("pool",db);
    //      dsi = ConnectionFactory.lookup("pool");
    //    }
    //    return dsi;
    //  }
    //  private Connection conn = null;
    //  private void getConnect() throws Exception
    //  {
    //    if(conn == null)
    //      conn = getDSI().getConnection();
    //  }

    private static DataSourceInf ds = null;
    //private DataSourceImp ds = null;
    public Collection getDataSet(String sql) throws DBExecption, Exception
            {
            Logger logger = XTLogger.getLogger(DataSourceImpl.class);

            DBConnParam db = new DBConnParam();

            Hashtable ht = ConnectionFactory.connectionPools;
            if (!ht.containsKey("pool"))
    {
        ConnectionFactory.bind("pool", db);
        logger.info("bind ok.");
    }
    ds = ConnectionFactory.lookup("pool");

            Collection v = new Vector();
            Statement st = null;
            //ConnectionFactory.bind("pool",db);
            //DataSourceInf ds = ConnectionFactory.lookup("pool");
            try
    {
        Connection conn = ds.getConnection();
        try
        {
            st = conn.createStatement();
            logger.debug("Run execute the SQL : " + sql);
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int clmNum = rsmd.getColumnCount();
            //保存字段类型
            String[] rowStr = new String[clmNum];
            for (int j = 0; j < clmNum; j++)
            {
                rowStr[j] = String.valueOf(rsmd.getColumnName(j + 1));
            }

            v.add(rowStr);
            //保存数据信息
            while (rs.next())
            {
                rowStr = new String[clmNum];
                for (int i = 0; i < clmNum; i++)
                {
                    rowStr[i] = Str.unicodeToGBK(rs.getString(i + 1));
                }
                v.add(rowStr);
            }

        } finally
        {
            try
            {
                st.close();
                conn.close();
            }
            catch (Exception e)
            {
                logger.error(sql, e.fillInStackTrace());
            }
        }
    }
    catch (Exception e)
    {
        logger.error(sql, e.fillInStackTrace());
    } finally
    {
        logger.info("search one time");
//        ConnectionFactory.unbind("pool");
//        System.out.println("unbind datasource ok.");
    }
    //断开连接
    //    conn.close();
    //    ConnectionFactory.unbind("pool");
    return v;
    }
            /**
             * 得到所有的维定义
             * @return 以Collection形返回所有的维 Collection是Dim型
             * @throws DBExecption 读取时发生的错误
             */
            public Collection getDimensionDefine() throws DBExecption,
            Exception
            {
            Collection cc = new Vector();
//    TableXMLParse txp = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
//    txp.doParseXML();//解析XML文件
            TableXMLParse txp = parse();
            NodeList nl = txp.getAllModels();

            for (int i = 0; i < nl.getLength(); i++)
    {
        Model m = new Model(nl.item(i));
        //System.out.println("I get table : " + m.getModelName());
        NodeList collist = m.getAllColumns();
        for (int j = 0; j < collist.getLength(); j++)
        {
            Column c = new Column(collist.item(j));

            DimImpl dim = new DimImpl();
            dim.setDataSourceId(m.getModelID());
            dim.setDimenId(c.getColumnName());
            dim.setDimenName(c.getColumnDesc());

            Vector v = (Vector)this.allcol;
            String pare = "";
            for (int n = 0; n < v.size(); n++)
            {
                Hashtable h = (Hashtable) v.get(n);
                if (h.get("TableName").equals(m.getModelID()))
                {
                    if (isSQL(c.getColumnTreeMode()))
                    {
                        int level = Integer.parseInt(String.valueOf(h.get(c.
                                getColumnName())));
                        Enumeration e = h.keys();
                        while (e.hasMoreElements())
                        {
                            Object ob = e.nextElement();
                            if (h.get(ob).equals(String.valueOf(level - 1)))
                            {
                                pare = String.valueOf(ob);
                                break;
                            }
                        }
                    }
                }
            }

            String s = c.getColumnTreeMode();
            String superid = null;

            if (isSQL(s))
            {
                String sql = remove(s);
                int count = 0;
                if (isHave(sql))
                {
                    StringTokenizer st = new StringTokenizer(sql, "=");
                    count = st.countTokens();
                    int flag = 0;
                    st.nextElement();
                    while (st.hasMoreElements())
                    {
                        String tmp = String.valueOf(st.nextElement());
                        if (flag == count - 2)
                        {
                            int qq = tmp.indexOf("$");
                            String getstr = tmp.substring(qq + 1).trim();
                            if (getstr.equals(pare))
                            {
                                getstr = "[" + getstr + "]";
                            }
                            if (superid == null)
                            {
                                superid = getstr;
                            }
                            else
                            {
                                superid = superid + "^" + getstr;
                            }
                        }
                        else
                        {
                            int hh = tmp.indexOf("and");
                            int qq = tmp.indexOf("$");
                            String getstr = tmp.substring(qq + 1, hh).trim();
                            if (getstr.equals(pare))
                            {
                                getstr = "[" + getstr + "]";
                            }
                            if (superid == null)
                            {
                                superid = getstr;
                            }
                            else
                            {
                                superid = superid + "^" + getstr;
                            }
                        }
                        flag++;
                    }
                }
            }
            dim.setSuperDimenId(superid);
            cc.add(dim);
        }
    }
    return cc;
    }
            /**
             * 得到以strDataSourceId为id号的数据源中所有的维定义
             * @return 以Collection形返回满足条件的维 Collection是Dim型
             * @throws DBExecption 读取时发生的错误
             */
            public Collection getDimensionDefine(String strDataSourceId) throws
            DBExecption, Exception
            {
            Logger logger = XTLogger.getLogger(DataSourceImpl.class);
            Collection cc = new Vector();
//    TableXMLParse txp = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
//    txp.doParseXML();//解析XML文件
            TableXMLParse txp = parse();
            NodeList nl = txp.getAllModels();
            int tabflag = 0;
            for (int i = 0; i < nl.getLength(); i++)
    {
        Model m = new Model(nl.item(i));
        //System.out.println("I get table : " + m.getModelName());
        if (m.getModelID().equals(strDataSourceId))
        {
            NodeList collist = m.getAllColumns();
            for (int j = 0; j < collist.getLength(); j++)
            {
                Column c = new Column(collist.item(j));
                String colname = c.getColumnName();
                DimImpl dim = new DimImpl();
                String tabid = m.getModelID();
                String colid = c.getColumnName();
                dim.setDataSourceId(tabid);
                dim.setDimenId(colid);
                dim.setDimenName(c.getColumnDesc());

                Vector v = (Vector)this.allcol;
                String pare = "";
                for (int n = 0; n < v.size(); n++)
                {
                    Hashtable h = (Hashtable) v.get(n);
                    if (h.get("TableName").equals(m.getModelID()))
                    {
                        if (isSQL(c.getColumnTreeMode()))
                        {
                            int level = Integer.parseInt(String.valueOf(h.get(c.
                                    getColumnName())));
                            Enumeration e = h.keys();
                            while (e.hasMoreElements())
                            {
                                Object ob = e.nextElement();
                                if (h.get(ob).equals(String.valueOf(level - 1)))
                                {
                                    pare = String.valueOf(ob);
                                    break;
                                }
                            }
                        }
                    }
                }

                String s = c.getColumnTreeMode();
                String superid = null;

                if (isSQL(s))
                {
                    String sql = remove(s);
                    int count = 0;
                    if (isHave(sql))
                    {
                        StringTokenizer st = new StringTokenizer(sql, "=");
                        count = st.countTokens();
                        int flag = 0;
                        st.nextElement();
                        while (st.hasMoreElements())
                        {
                            String tmp = String.valueOf(st.nextElement());
                            if (flag == count - 2)
                            {
                                int qq = tmp.indexOf("$");
                                String getstr = tmp.substring(qq + 1).trim();
                                if (getstr.equals(pare))
                                {
                                    getstr = "[" + getstr + "]";
                                }
                                if (superid == null)
                                {
                                    superid = getstr;
                                }
                                else
                                {
                                    superid = superid + "^" + getstr;
                                }
                            }
                            else
                            {
                                int hh = tmp.indexOf("and");
                                int qq = tmp.indexOf("$");
                                String getstr = tmp.substring(qq + 1, hh).trim();
                                if (getstr.equals(pare))
                                {
                                    getstr = "[" + getstr + "]";
                                }
                                if (superid == null)
                                {
                                    superid = getstr;
                                }
                                else
                                {
                                    superid = superid + "^" + getstr;
                                }
                            }
                            flag++;
                        }
                    }
                }
                dim.setSuperDimenId(superid);
                cc.add(dim);
            }
            break;
        }
        if (tabflag == nl.getLength() - 1)
        {
            logger.error("not find the table!");
        }
        //throw new Exception("not find the table!");
        tabflag++;
    }
    return cc;
    }
            /**
             * 得到所有的数据源定义
             * @return 以Collection形返回所有的数据源 Collection是Source型
             * @throws DBExecption 读取时发生的错误
             */
            public Collection getDataSourceDefine() throws DBExecption,
            Exception
            {
            Collection col = new Vector();
//    TableXMLParse txp = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
//    txp.doParseXML();//解析XML文件
            TableXMLParse txp = parse();
            NodeList nl = txp.getAllModels();

            for (int i = 0; i < nl.getLength(); i++)
    {
        Model m = new Model(nl.item(i));
        SourceImpl sour = new SourceImpl();
        sour.setDataSourceId(m.getModelID());
        sour.setDataSourceName(m.getModelName());
        col.add(sour);
    }
    return col;
    }
            /**
             * 得到某个数据源字段的所有取值.
             * @param tableColumn 表名.字段名
             * @param parent 参数,如果参数=null,则提示用户先选择维的上一层次的值
             * @return 树形返回所有取值
             * @throws DBExecption DB错误
             */
            public Object getValue(String tableColumn, Map parent) throws
            Exception, DBExecption
            {
            Logger logger = XTLogger.getLogger(DataSourceImpl.class);
            Object obj = null;
            Collection collect = new Vector();
            String tmpStr = new String(tableColumn);
            String tabName = tmpStr.substring(0, tmpStr.indexOf('.')); //得到表名
            String colName = tmpStr.substring(tmpStr.indexOf('.') + 1); //得到字段名
//    TableXMLParse txp = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
//    txp.doParseXML();//解析XML文件
            TableXMLParse txp = parse();
            NodeList nl = txp.getAllModels();

            for (int i = 0; i < nl.getLength(); i++)
    {
        Model m = new Model(nl.item(i));
        if (m.getModelID().equals(tabName))
        {
            //System.out.println("I get the Table " + tabName);
            NodeList collist = m.getAllColumns();
            for (int j = 0; j < collist.getLength(); j++)
            {
                Column c = new Column(collist.item(j));
                if (c.getColumnName().equals(colName))
                {
                    //System.out.println("I get the Column " + colName);
                    String tree = c.getColumnTreeMode();
                    if (isSQL(tree)) //判断treeMode是否为SQL
                    {
                        String tmpTree = remove(tree);
                        if (parent == null) //判断父节点是否为空，为空直接运行sql
                        {
                            Vector v = (Vector) getDataSet(tmpTree);
                            ValueImpl value = new ValueImpl();
                            for (int h = 1; h < v.size(); h++)
                            {
                                String[] s = (String[]) v.get(h);
                                value = new ValueImpl();
                                value.setValueName(s[0].trim());
                                value.setValueId(s[1].trim());
                                value.setDimensionId(c.getColumnName());
                                collect.add(value);
                            }
                        }
                        else //不为空，把父节点的值替换到sql语句中
                        {
                            StringTokenizer st = new StringTokenizer(tmpTree,
                                    "$");
                            int count = st.countTokens();

                            String allstr = String.valueOf(st.nextElement());
                            int num = 0;
                            while (st.hasMoreElements())
                            {
                                Object ob = st.nextElement();
                                String sub = String.valueOf(ob);

                                if (num == count - 2)
                                {
                                    String getstr = sub.trim();

                                    allstr = allstr + "\"" + parent.get(getstr) +
                                             "\"";
                                    if (parent.get(getstr) == null)
                                    {
                                        allstr = transStr(allstr);
                                    }
                                    //System.out.println("get the last one :" + getstr);
                                }
                                else
                                {
                                    int start = 0;

                                    while (sub.charAt(start) == ' ')
                                    {
                                        start++;
                                    }
                                    int end = start;
                                    while (sub.charAt(end) != ' ' &&
                                           sub.charAt(end) != '=')
                                    {
                                        end++;
                                    }
                                    String getstr = sub.substring(start, end);
                                    allstr = allstr + sub.substring(0, start) +
                                             "\"" + parent.get(getstr) + "\"" +
                                             sub.substring(end);
                                    if (parent.get(getstr) == null)
                                    {
                                        allstr = transStr(allstr);
                                    }
                                    //System.out.println("get :" + sub.substring(start,end));
                                    num++;
                                }
                            }

                            logger.debug("Get the sql is : " + allstr);
                            Vector v = (Vector) getDataSet(allstr);
                            ValueImpl value = new ValueImpl();
                            for (int h = 1; h < v.size(); h++)
                            {
                                String[] s = (String[]) v.get(h);
                                value = new ValueImpl();
                                value.setValueName(s[0].trim());
                                value.setValueId(s[1].trim());
                                value.setDimensionId(c.getColumnName());
                                collect.add(value);
                            }
                        }
                        obj = (Object) collect;
                    }
                    else //如果不是sql语句
                    if (isPLUGIN(tree))
                    {
                        //如果是Plugin类型
                        //待完善
                        TreeNodeImpl t = new TreeNodeImpl();
                        obj = (Object) t;
                    }
                    break;
                }
            }
        }
    }
    return obj;

    }
            /**
             * 关于“null”的解决方法
             * @param sql 传入的SQL语句
             * @return String
             * @throws Exception 异常错误
             */
            private String transStr(String sql) throws Exception
    {
        int start = sql.lastIndexOf("\"null\"");
        int tmp = start - 1;
        int cutstart, cutend;
        while (sql.charAt(tmp) == ' ')
        {
            tmp--;
        }
        if (sql.charAt(tmp) == '=')
        {
            tmp--;
            while (sql.charAt(tmp) == ' ')
            {
                tmp--;
            }
            if (sql.charAt(tmp) != '"')
            {
                cutend = tmp;
                while (sql.charAt(tmp) != ' ')
                {
                    tmp--;
                }
                cutstart = tmp + 1;
                sql = sql.substring(0, cutstart) + "\"" + "null" + "\"" +
                      sql.substring(cutend + 1);
            }
        }
        return sql;

    }


    /**
     * 检测$字符的数量，返回$字符的个数.
     * @param sql 传入的SQL语句
     * @return 整形
     * @throws Exception 异常错误
     */
    public int getCount(String sql) throws Exception
    {
        int count = 0;
        if (isHave(sql))
        {
            StringTokenizer st = new StringTokenizer(sql, "$");
            count = st.countTokens() - 1;
        }
        return count;
    }


    /**
     * 检测$字符，如果存在返回为真，否则返回为假.
     * @param sql 传入的SQL语句
     * @return 布尔值
     * @throws Exception 异常错误
     */
    private boolean check(String sql) throws Exception
    {
        String tmpStr = sql.substring(5, sql.length() - 1);
        int flag = tmpStr.indexOf('$');
        if (flag == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 取嵌套的$符后边的字符串
     * @param sql 传入的SQL语句
     * @return 字符串
     * @throws Exception 异常错误
     */
    private String getStr(String sql) throws Exception
    {
        int start = sql.indexOf('$');
        //int end = sql.indexOf(')');
        String tmpStr = sql.substring(start + 1);
        return tmpStr;
    }

    /**
     * 检测str是否为sql语句
     * @param str 传入的字符串
     * @return 布尔值
     * @throws Exception 异常错误
     */
    private boolean isSQL(String str) throws Exception
    {
        if (str.equals("") || str == null)
        {
            return false;
        }
        else
        {
            int startPot = str.indexOf('$');
            int endPot = str.indexOf('(');
            String type = str.substring(startPot + 1, endPot);
            if (type.equals("sql"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * 检测str是否为plugin语句
     * @param str 传入的字符串
     * @return 布尔值
     * @throws Exception 异常错误
     */
    private boolean isPLUGIN(String str) throws Exception
    {
        int startPot = str.indexOf('$');
        int endPot = str.indexOf('(');
        String type = str.substring(startPot + 1, endPot);
        if (type.equals("plugins"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 拨去外层包装$sql()
     * @param str 传入的字符串
     * @return 拨去外层的SQL
     * @throws Exception 异常错误
     */
    private String remove(String str) throws Exception
    {
        int startPot = str.indexOf('(');
        int endPot = str.lastIndexOf(')');
        String s = str.substring(startPot + 1, endPot);
        if (s == null || s.trim().equals(""))
        {
            return null;
        }
        else
        {
            return s;
        }
    }

    /**
     * 检查一句SQL语句中有无‘$’
     * @param str 传入的字符串
     * @return 布尔值
     * @throws Exception 异常错误
     */
    private boolean isHave(String str) throws Exception
    {
        int pot = str.indexOf('$');
        if (pot == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 取得SELECT语句中的表名
     * @param sql 传入的SQL语句
     * @return 表名
     * @throws Exception 异常错误
     */
    private String getName(String sql) throws Exception
    {
        int startPot = sql.indexOf("from");
        int endPot = sql.indexOf("where", startPot);
        String name = sql.substring(startPot + 4, endPot).trim();
        return name;
    }

    /**
     * 在htt中取出key相对应的SQL
     * @param ht 存放所有列信息的hashtable
     * @param key 列名
     * @return 描述文件中的定义的TreeMode字符串
     * @throws Exception 异常错误
     */
    private String getSQL(Hashtable ht, String key) throws Exception
    {
        String s = null;
        if (ht.containsKey(key))
        {
            s = (String) ht.get(key);
        }
        return s;
    }

    /**
     * select name,code from a where c=$d;取得d所对应的SQL语句
     * @param ht 存放所有列信息的hashtable
     * @param sql 所传入的SQL语句
     * @return 描述文件中的定义的TreeMode字符串
     * @throws Exception 异常错误
     */
    private String getChildStr(Hashtable ht, String sql) throws Exception
    {
        int pot = sql.indexOf('$');
        String str = sql.substring(pot + 1).trim();
        String s = null;
        if (ht.containsKey(str))
        {
            s = (String) ht.get(str);
        }
        return s;
    }

    /**
     * 取得表间字段的关联关系
     * @return 描述文件中的定义的所有的表间字段的关联关系
     * @throws Exception 异常错误
     */

    public Collection getRelations() throws DBExecption, Exception
            {
            Collection col = new Vector();
//    TableXMLParse txp = new TableXMLParse();
//    txp.doParseXML();
            TableXMLParse txp = parse();

            NodeList nlrelation = XPathAPI.selectNodeList(txp.getRoot(),
            "//Relation");
            String relation;
            for (int ii = 0; ii < nlrelation.getLength(); ii++)
    {
        relation = XMLPathTool.getTextContents(nlrelation.item(ii));
        col.add(relation);
    }
    /*
         NodeList nlrelation = txp.getAllRelations();
         for(int ii = 0; ii < nlrelation.getLength(); ii++)
         {
      Relations r = new Relations(nlrelation.item(ii));
      NodeList nlchild = r.getAllChildRelations();
      for(int jj = 0; jj < nlchild.getLength(); jj++)
      {
        Relation rr = new Relation(nlchild.item(jj));
        String relation = rr.getRelationValue();
        col.add(relation);
      }
         }*/
    return col;
    }
            /**
             * 主函数 测试此类
             * @throws DBExecption DB错误
             */
            public static void main(String[] args) throws Exception
    {
        Logger log = XTLogger.getLogger(DataSourceImpl.class);
        Exception e = new Exception();

    }
}
