/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/*
 * <p>ClassName: </p>
 * <p>Description: 生成 DBTablename.java 文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @author: HST
 * @version: 1.0
 * @date: 2002-06-17
 */
public class SQLString
{
    /**
     * 构造函数，给TableName赋值
     * @param t String
     */
    public SQLString(String t)
    {
        TableName = t;
    }

    //表名
    private String TableName;
    //普通查询条件
    private StringBuffer WherePart = new StringBuffer(256);
    //主键查询条件
    private StringBuffer PKWherePart = new StringBuffer(256);
    //更新条件
    private StringBuffer UpdPart = new StringBuffer(256);
    //插入条件
    private StringBuffer InsPart = new StringBuffer(256);
    //sql语句
    private StringBuffer mSql = new StringBuffer(256);
    //常量间隔符
    private static final String mark = "'";

    /**
     * 获得数据库操作sql
     * @param flag int
     * @param s Schema
     */
    public void setSQL(int flag, Schema s)
    {
        switch (flag)
        {
            case 1:

                // insert
                this.setInsPart(s);
                if (InsPart.equals(""))
                {
                    mSql.setLength(0);
                }
                else
                {
                    mSql.append("insert into ");
                    mSql.append(TableName);
                    mSql.append(" ");
                    mSql.append(InsPart);
                }
                break;
            case 2:

                // update (by Primary Key)
                this.setUpdPart(s);
                this.setPKWherePart(s);
                if (UpdPart.equals(""))
                {
                    mSql.setLength(0);
                }
                else
                {
                    mSql.append("update ");
                    mSql.append(TableName);
                    mSql.append(" ");
                    mSql.append(UpdPart);
                    mSql.append(" ");
                    mSql.append(PKWherePart);
                }
                break;
            case 3:

                // delete
                this.setWherePart(s);
                mSql.append("delete from ");
                mSql.append(TableName);
                mSql.append(" ");
                mSql.append(WherePart);
                break;
            case 4:

                // delete (by Primary Key)
                this.setPKWherePart(s);
                mSql.append("delete from ");
                mSql.append(TableName);
                mSql.append(" ");
                mSql.append(PKWherePart);
                break;
            case 5:

                // select
                this.setWherePart(s);
                mSql.append("select * from ");
                mSql.append(TableName);
                mSql.append(" ");
                mSql.append(WherePart);
                break;
            case 6:

                // select (by Primary Key)
                this.setPKWherePart(s);
                mSql.append("select * from ");
                mSql.append(TableName);
                mSql.append(" ");
                mSql.append(PKWherePart);
                break;
            case 7:

                // select Count
                this.setWherePart(s);
                mSql.append("select count(1) from ");
                mSql.append(TableName);
                mSql.append(" ");
                mSql.append(WherePart);
                break;
        }
    }

    public String getSQL()
    {
//        System.out.print("DBOper'Sql in SQLString, it's : ");
//        System.out.println(mSql.toString());
        return mSql.toString();
    }

    public String getSQL(int sqlFlag, Schema s)
    {
        if (sqlFlag == 5 || sqlFlag == 6)
        {
            this.setSQL(sqlFlag, s);
        }
        else
        {
            mSql.setLength(0);
        }
        return mSql.toString();
    }

    /**
     * 通过 Primary Key 组成 WherePart
     * @param s Schema
     */
    public void setPKWherePart(Schema s)
    {
        PKWherePart.append("where");

        String[] pk = s.getPK();
        int n = pk.length;

        String strFieldName = "";
        StringBuffer strFieldValue = null;
        for (int i = 0; i < n; i++)
        {
            strFieldName = pk[i];
            strFieldValue = new StringBuffer(100);

            int nFieldType = s.getFieldType(strFieldName);
            switch (nFieldType)
            {
                case Schema.TYPE_STRING:
                case Schema.TYPE_DATE:
                    strFieldValue.append(mark);
                    strFieldValue.append(s.getV(strFieldName));
                    strFieldValue.append(mark);
                    break;
                case Schema.TYPE_DOUBLE:
                case Schema.TYPE_FLOAT:
                case Schema.TYPE_INT:
                    strFieldValue.append(s.getV(strFieldName));
                    break;
                default:
                    System.out.println("出现异常数据类型");
                    break;
            }

            if (i != 0)
            {
                PKWherePart.append(" and");
            }

            PKWherePart.append(" ");
            PKWherePart.append(strFieldName);
            PKWherePart.append("=");
            PKWherePart.append(strFieldValue);
        }
    }

    public String getPKWherePart()
    {
        return PKWherePart.toString();
    }

    /**
     * 通过 Schema 对象组成 WherePart
     * @param s Schema
     */
    public void setWherePart(Schema s)
    {
        WherePart.append("where");

        int nFieldCount = s.getFieldCount();
        int j = 0;

        String strFieldName = "";
        StringBuffer strFieldValue = null;
        for (int i = 0; i < nFieldCount; i++)
        {
            strFieldName = s.getFieldName(i);
            strFieldValue = new StringBuffer(100);

            int nFieldType = s.getFieldType(i);
            boolean bFlag = false;

            switch (nFieldType)
            {
                case Schema.TYPE_STRING:
                case Schema.TYPE_DATE:
                    if (s.getV(i).equals("null"))
                    {
                        strFieldValue.append(s.getV(i));
                    }
                    else
                    {
                        strFieldValue.append(mark);
                        strFieldValue.append(s.getV(i));
                        strFieldValue.append(mark);
                        bFlag = true;
                    }
                    break;

                case Schema.TYPE_DOUBLE:
                    if (!s.getV(i).equals("0.0"))
                    {
                        strFieldValue.append(s.getV(i));
                        bFlag = true;
                    }
                    break;

                case Schema.TYPE_FLOAT:
                    if (!s.getV(i).equals("0.0"))
                    {
                        strFieldValue.append(s.getV(i));
                        bFlag = true;
                    }
                    break;

                case Schema.TYPE_INT:
                    if (!s.getV(i).equals("0"))
                    {
                        strFieldValue.append(s.getV(i));
                        bFlag = true;
                    }
                    break;

                default:
                    System.out.println("出现异常数据类型");
                    bFlag = false;
                    break;
            }

            if (bFlag)
            {
                j++;
                if (j != 1)
                {
                    WherePart.append(" and");
                }
                WherePart.append(" ");
                WherePart.append(strFieldName);
                WherePart.append("=");
                WherePart.append(strFieldValue);
            }
        }
        if (j == 0)
        {
            WherePart.setLength(0);
        }
    }

    public String getWherePart()
    {
        return WherePart.toString();
    }

    /**
     * 通过 Schema 对象组成 UpdPart
     * @param s Schema
     */
    public void setUpdPart(Schema s)
    {
        UpdPart.append("set ");

        int nFieldCount = s.getFieldCount();

        String strFieldName = "";
        StringBuffer strFieldValue = null;
        for (int i = 0; i < nFieldCount; i++)
        {
            strFieldName = s.getFieldName(i);
            strFieldValue = new StringBuffer(100);

            int nFieldType = s.getFieldType(i);

            switch (nFieldType)
            {
                case Schema.TYPE_STRING:
                case Schema.TYPE_DATE:
                    if (s.getV(i).equals("null"))
                    {
                        strFieldValue.append(s.getV(i));
                    }
                    else
                    {
                        strFieldValue.append(mark);
                        strFieldValue.append(s.getV(i));
                        strFieldValue.append(mark);
                    }
                    break;
                case Schema.TYPE_DOUBLE:
                case Schema.TYPE_FLOAT:
                case Schema.TYPE_INT:
                    strFieldValue.append(s.getV(i));

                    // 不管初始值是0，还是输入0，一律插入数据库。
                    break;
                default:
                    System.out.println("出现异常数据类型");
                    break;
            }

            if (i != 0)
            {
                UpdPart.append(",");
            }
            UpdPart.append(strFieldName);
            UpdPart.append("=");
            UpdPart.append(strFieldValue);
        }
    }

    public String getUpdPart()
    {
        return UpdPart.toString();
    }

    /**
     * 通过 Schema 对象组成 InsPart
     * @param s Schema
     */
    public void setInsPart(Schema s)
    {
        StringBuffer ColPart = new StringBuffer(256);
        ColPart.append("( ");
        StringBuffer ValPart = new StringBuffer(256);
        ValPart.append("values ( ");

        int nFieldCount = s.getFieldCount();
        int j = 0;

        String strFieldName = "";
        StringBuffer strFieldValue = null;
        for (int i = 0; i < nFieldCount; i++)
        {
            strFieldName = s.getFieldName(i);
            strFieldValue = new StringBuffer(100);

            int nFieldType = s.getFieldType(i);
            boolean bFlag = false;
            switch (nFieldType)
            {
                case Schema.TYPE_STRING:
                case Schema.TYPE_DATE:
                    if (s.getV(i).equals("null"))
                    {
                        strFieldValue.append(s.getV(i));
                    }
                    else
                    {
                        strFieldValue.append(mark);
                        strFieldValue.append(s.getV(i));
                        strFieldValue.append(mark);
                        bFlag = true;
                    }
                    break;
                case Schema.TYPE_DOUBLE:
                case Schema.TYPE_FLOAT:
                case Schema.TYPE_INT:
                    strFieldValue.append(s.getV(i));
                    bFlag = true;
                    break;
                default:
                    bFlag = false; // 不支持的数据类型
                    break;
            }

            if (bFlag)
            {
                j++;
                if (j != 1)
                {
                    ColPart.append(",");
                    ValPart.append(",");
                }
                ColPart.append(strFieldName);
                ValPart.append(strFieldValue);
            }
        }
        ColPart.append(" )");
        ValPart.append(" )");

        InsPart.append(ColPart);
        InsPart.append(" ");
        InsPart.append(ValPart);
        if (j == 0)
        {
            InsPart.setLength(0);
        }
    }

    public String getInsPart()
    {
        return InsPart.toString();
    }
}
