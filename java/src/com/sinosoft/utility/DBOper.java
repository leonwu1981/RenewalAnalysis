/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.sql.*;

//import com.sinosoft.lis.schema.LAComSchema;
/*
 * <p>ClassName: DBOper </p>
 * <p>Description: 数据库操作类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: HST
 * @CreateDate：2002-06-27
 */
public class DBOper
{
    // @Field
    private Connection con;
    private String tableName;
    private boolean cflag = false;

    public CErrors mErrors = new CErrors(); // 错误信息

    // @Constructor
    public DBOper(Connection tConnection, String t)
    {
        con = tConnection;
        tableName = t;

    }

    public DBOper(String t)
    {
        tableName = t;
        con = null;
        cflag = true;
    }

    // @Method
    public Connection getConnection()
    {
        // return con;
        return DBConnPool.getConnection();
    }

    /**
     * db的插入操作
     * @param s Schema
     * @return boolean
     */
    public boolean insert(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //创建操作对象
        SQLString sqlObj = new SQLString(tableName);
        //设置插入属性
        sqlObj.setSQL(1, s);
        //获得执行sql
        String sql = sqlObj.getSQL();
        try
        {
            stmt = con.createStatement();

            int operCount = stmt.executeUpdate(sql);
            if (operCount > 0)
            {
                flag = true;
            }
            else
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DBOper";
                tError.functionName = "insert";
                tError.errorMessage = "实际操作的数据记录数为０条数据";
                this.mErrors.addOneError(tError);
                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at insert(Schema s): " + sql+";");
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }

        return flag;
    }

    /**
     * db的批量插入操作
     * @param s SchemaSet
     * @return boolean
     */
    public boolean insert(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;
        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }
        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            int n = s.size();
            Schema aSchema = null;
            String sql = null;
            SQLString sqlObj = null;
            stmt = con.createStatement();
            for (int i = 1; i <= n; i++)
            {
                aSchema = (Schema) s.getObj(i);
                sqlObj = new SQLString(tableName);
                //设置插入属性
                sqlObj.setSQL(1, aSchema);
                sql = sqlObj.getSQL();
                stmt.addBatch(sql);
            }
//            int operCount = stmt.executeUpdate(sql);
            try
            {
                //可以返回全部的数组信息，但是是否需要拿这个数组的信息做判定，还需要观察
//                int operCount[] = stmt.executeBatch();
                stmt.executeBatch();
                //是否需要做如下处理？
//                for (int i = 0; i < operCount.length; i++)
//                {
//                    if (operCount[i] <= 0)
//                    {
//                        flag = false;
//                        break;
//                    }
//                }
                flag = true;
            }
            catch (SQLException e)
            {
            	System.out.println("##### Error 错误："+e.getMessage());
                for (int i = 1; i <= n; i++)
                {
                    aSchema = (Schema) s.getObj(i);
                    sqlObj = new SQLString(tableName);
                    sqlObj.setSQL(1, aSchema);
                    sql = sqlObj.getSQL();
                    System.out.println("##### Error Sql in DBOper at insert(SchemaSet s): " + sql+";");
                }
                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }

        return flag;
    }

    /**
     * db的update操作
     * @param s Schema
     * @return boolean
     */
    public boolean update(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //设置更新属性
        sqlObj.setSQL(2, s);
        String sql = sqlObj.getSQL();

        try
        {
            stmt = con.createStatement();

            int operCount = stmt.executeUpdate(sql);
            if (operCount > 0)
            {
                flag = true;
            }
            else
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DBOper";
                tError.functionName = "update";
                tError.errorMessage = "实际操作的数据记录数为０条数据";
                this.mErrors.addOneError(tError);

                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at update(Schema s): " + sql+";");
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    /**
     * db的批量update操作
     * @param s SchemaSet
     * @return boolean
     */
    public boolean update(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            int n = s.size();
            Schema aSchema = null;
            String sql = null;
            SQLString sqlObj = null;
            stmt = con.createStatement();
            for (int i = 1; i <= n; i++)
            {
                aSchema = (Schema) s.getObj(i);
                sqlObj = new SQLString(tableName);
                //设置更新属性
                sqlObj.setSQL(2, aSchema);
                sql = sqlObj.getSQL();
                stmt.addBatch(sql);
            }

            try
            {
//                int operCount[] = stmt.executeBatch();
                stmt.executeBatch();
                flag = true;
            }
            catch (SQLException e)
            {
                for (int i = 1; i <= n; i++)
                {
                    aSchema = (Schema) s.getObj(i);
                    sqlObj = new SQLString(tableName);
                    //设置更新属性
                    sqlObj.setSQL(2, aSchema);
                    sql = sqlObj.getSQL();
                    System.out.println("##### Error Sql in DBOper at update(SchemaSet s): " + sql+";");
                }
                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    /**
     * db根据sql删除
     * @param s Schema
     * @return boolean
     */
    public boolean deleteSQL(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //设置删除属性
        sqlObj.setSQL(3, s);
        //如果删除条件为空，则出错返回，以防止数据被异常删除
        if (sqlObj.getWherePart().compareTo("") == 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "删除条件为空";
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        String sql = sqlObj.getSQL();

        try
        {
            stmt = con.createStatement();

            stmt.executeUpdate(sql);

            flag = true;
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at deleteSQL(Schema s): " + sql+";");
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    /**
     * db根据sql批量删除
     * @param s Schema
     * @return boolean
     */
    public boolean deleteSQL(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            int n = s.size();
            Schema aSchema = null;
            String sql = null;
            SQLString sqlObj = null;
            stmt = con.createStatement();
            for (int i = 1; i <= n; i++)
            {
                aSchema = (Schema) s.getObj(i);
                sqlObj = new SQLString(tableName);
                //设置删除属性
                sqlObj.setSQL(3, aSchema);
                //如果删除条件为空，则出错返回，以防止数据被异常删除
                if (sqlObj.getWherePart().compareTo("") == 0)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "DBOper";
                    tError.functionName = "delete";
                    tError.errorMessage = "删除条件为空";
                    this.mErrors.addOneError(tError);

                    try
                    {
                        stmt.close();
                    }
                    catch (SQLException ex)
                    {}

                    if (cflag)
                    {
                        try
                        {
                            con.close();
                        }
                        catch (SQLException et)
                        {}
                    }
                    return false;
                }
                sql = sqlObj.getSQL();
                stmt.addBatch(sql);
            }

            try
            {
                stmt.executeBatch();
                flag = true;
            }
            catch (SQLException e)
            {
                for (int i = 1; i <= n; i++)
                {
                    aSchema = (Schema) s.getObj(i);
                    sqlObj = new SQLString(tableName);
                    //设置删除属性
                    sqlObj.setSQL(3, aSchema);
                    sql = sqlObj.getSQL();
                    System.out.println("##### Error Sql in DBOper at deleteSQL(SchemaSet s): " + sql+";");
                }
                flag = false;
            }
            stmt.close();
        } // end of try
        catch (SQLException e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    /**
     * db根据主键删除
     * @param s Schema
     * @return boolean
     */
    public boolean delete(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //设置删除属性
        sqlObj.setSQL(4, s);
        String sql = sqlObj.getSQL();

        try
        {
            stmt = con.createStatement();

//            int operCount = stmt.executeUpdate(sql);
            stmt.executeUpdate(sql);

            flag = true;
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at delete(Schema s): " + sql+";");
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    /**
     * db根据主键批量删除
     * @param s Schema
     * @return boolean
     */
    public boolean delete(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            int n = s.size();
            Schema aSchema = null;
            String sql = null;
            SQLString sqlObj = null;
            stmt = con.createStatement();
            for (int i = 1; i <= n; i++)
            {
                aSchema = (Schema) s.getObj(i);
                sqlObj = new SQLString(tableName);
                //设置删除属性
                sqlObj.setSQL(4, aSchema);
                sql = sqlObj.getSQL();
                stmt.addBatch(sql);
            }

            try
            {
                stmt.executeBatch();
                flag = true;
            }
            catch (SQLException e)
            {
                for (int i = 1; i <= n; i++)
                {
                    aSchema = (Schema) s.getObj(i);
                    sqlObj = new SQLString(tableName);
                    //设置删除属性
                    sqlObj.setSQL(4, aSchema);
                    sql = sqlObj.getSQL();
                    System.out.println("##### Error Sql in DBOper at delete(SchemaSet s): " + sql+";");
                }
                flag = false;
            }
            stmt.close();
        } // end of try
        catch (SQLException e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return false;
        }
        // 断开数据库连接
        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return flag;
    }

    public int getCount(Schema s)
    {
        Statement stmt = null;
        ResultSet rs = null;
        int RSCount;

        // 如果要使用内部的Connection对象
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "getCount";
            tError.errorMessage = "Connection建立失败!";
            this.mErrors.addOneError(tError);
            return -1;
        }

        SQLString sqlObj = new SQLString(tableName);
        sqlObj.setSQL(7, s);
        String sql = sqlObj.getSQL();

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery(sql);
            rs.next();
            RSCount = rs.getInt(1);
            rs.close();
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at getCount(Schema s): " + sql+";");
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "getCount";
            tError.errorMessage = e.getMessage();
            this.mErrors.addOneError(tError);

            try
            {
                rs.close();
                stmt.close();
            }
            catch (SQLException ex)
            {}

            if (cflag)
            {
                try
                {
                    con.close();
                }
                catch (SQLException et)
                {}
            }
            return -1;
        }

        if (cflag)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {}
        }
        return RSCount;
    }

    public static void main(String args[])
    {
//        DBOper dbop = new DBOper("LACOM");
//        LAComSchema lacomschema = new LAComSchema();
//        dbop.deleteSQL(lacomschema);
    }
}
