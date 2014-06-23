package com.sinosoft.xreport.dl;

/**
 * 缓冲处理执行SQL语句
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;


public class BufferDataSourceImpl extends DataSourceImpl
{
    //存放SQL语句和其执行的结果
    Hashtable htResult = new Hashtable();
    DataSourceInf ds = null;

    public BufferDataSourceImpl() throws Exception
    {

    }

    /**
     * 检测sql是否已经执行，如果已经执行，从哈希表中取出结果，否则，执行，然后存入哈希表
     * @param sql 传入的字符串
     * @return Collection
     * @throws Exception 异常错误
     */
    public Collection execSQL(String sql)
    {
        Collection collection = new Vector();
        if (htResult.containsKey(sql))
        {
            collection = (Vector) htResult.get(sql);
        }
        else
        {
            try
            {
                collection = this.getDataSet(sql);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            htResult.put(sql, collection);
        }
        return collection;
    }


    /**
     * 重载DataSourceImpl中的getDataSet()函数
     * 检测sql是否已经执行，如果已经执行，从哈希表中取出结果，否则，执行，然后存入哈希表
     * @param sql 传入的字符串
     * @return Collection
     * @throws Exception 异常错误
     */
    public Collection getDataSet(String sql) throws DBExecption, Exception
            {
            if (htResult.containsKey(sql))
    {
        return (Collection) htResult.get(sql);
    }
    else
    {
        Collection c = super.getDataSet(sql);
        htResult.put(sql, c);
        return c;
    }
    }
            /**
             * 主函数
             * @param args[]
             * @throws Exception 异常错误
             */
            public static void main(String[] args) throws Exception
    {
        BufferDataSourceImpl sh = new BufferDataSourceImpl();

    }
}