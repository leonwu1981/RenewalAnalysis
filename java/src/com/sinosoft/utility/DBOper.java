/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.sql.*;

//import com.sinosoft.lis.schema.LAComSchema;
/*
 * <p>ClassName: DBOper </p>
 * <p>Description: ���ݿ�������ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: HST
 * @CreateDate��2002-06-27
 */
public class DBOper
{
    // @Field
    private Connection con;
    private String tableName;
    private boolean cflag = false;

    public CErrors mErrors = new CErrors(); // ������Ϣ

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
     * db�Ĳ������
     * @param s Schema
     * @return boolean
     */
    public boolean insert(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = "Connection����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //������������
        SQLString sqlObj = new SQLString(tableName);
        //���ò�������
        sqlObj.setSQL(1, s);
        //���ִ��sql
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
                // @@������
                CError tError = new CError();
                tError.moduleName = "DBOper";
                tError.functionName = "insert";
                tError.errorMessage = "ʵ�ʲ��������ݼ�¼��Ϊ��������";
                this.mErrors.addOneError(tError);
                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at insert(Schema s): " + sql+";");
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db�������������
     * @param s SchemaSet
     * @return boolean
     */
    public boolean insert(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;
        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }
        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "insert";
            tError.errorMessage = "Connection����ʧ��!";
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
                //���ò�������
                sqlObj.setSQL(1, aSchema);
                sql = sqlObj.getSQL();
                stmt.addBatch(sql);
            }
//            int operCount = stmt.executeUpdate(sql);
            try
            {
                //���Է���ȫ����������Ϣ�������Ƿ���Ҫ������������Ϣ���ж�������Ҫ�۲�
//                int operCount[] = stmt.executeBatch();
                stmt.executeBatch();
                //�Ƿ���Ҫ�����´���
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
            	System.out.println("##### Error ����"+e.getMessage());
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
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db��update����
     * @param s Schema
     * @return boolean
     */
    public boolean update(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = "Connection����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //���ø�������
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
                // @@������
                CError tError = new CError();
                tError.moduleName = "DBOper";
                tError.functionName = "update";
                tError.errorMessage = "ʵ�ʲ��������ݼ�¼��Ϊ��������";
                this.mErrors.addOneError(tError);

                flag = false;
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("##### Error Sql in DBOper at update(Schema s): " + sql+";");
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db������update����
     * @param s SchemaSet
     * @return boolean
     */
    public boolean update(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "update";
            tError.errorMessage = "Connection����ʧ��!";
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
                //���ø�������
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
                    //���ø�������
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
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db����sqlɾ��
     * @param s Schema
     * @return boolean
     */
    public boolean deleteSQL(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = "Connection����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //����ɾ������
        sqlObj.setSQL(3, s);
        //���ɾ������Ϊ�գ�������أ��Է�ֹ���ݱ��쳣ɾ��
        if (sqlObj.getWherePart().compareTo("") == 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "ɾ������Ϊ��";
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
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db����sql����ɾ��
     * @param s Schema
     * @return boolean
     */
    public boolean deleteSQL(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "deleteSQL";
            tError.errorMessage = "Connection����ʧ��!";
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
                //����ɾ������
                sqlObj.setSQL(3, aSchema);
                //���ɾ������Ϊ�գ�������أ��Է�ֹ���ݱ��쳣ɾ��
                if (sqlObj.getWherePart().compareTo("") == 0)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "DBOper";
                    tError.functionName = "delete";
                    tError.errorMessage = "ɾ������Ϊ��";
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
                    //����ɾ������
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
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db��������ɾ��
     * @param s Schema
     * @return boolean
     */
    public boolean delete(Schema s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "Connection����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        SQLString sqlObj = new SQLString(tableName);
        //����ɾ������
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
            // @@������
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
        // �Ͽ����ݿ�����
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
     * db������������ɾ��
     * @param s Schema
     * @return boolean
     */
    public boolean delete(SchemaSet s)
    {
        Statement stmt = null;
        boolean flag;

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "delete";
            tError.errorMessage = "Connection����ʧ��!";
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
                //����ɾ������
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
                    //����ɾ������
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
            // @@������
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
        // �Ͽ����ݿ�����
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

        // ���Ҫʹ���ڲ���Connection����
        if (cflag)
        {
            con = DBConnPool.getConnection();
        }

        if (null == con)
        {
            CError tError = new CError();
            tError.moduleName = "DBOper";
            tError.functionName = "getCount";
            tError.errorMessage = "Connection����ʧ��!";
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
            // @@������
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
