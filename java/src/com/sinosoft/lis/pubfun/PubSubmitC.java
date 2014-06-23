/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;

/**
 * <p>
 * Title:
 * </p>
 * 公共提交类
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed Modify:,针对含blob字段且blob字段不在最后一列的情况2,添加blob的set入库操作
 * @version 1.0
 */
public class PubSubmitC {
    // 传输数据类
    private VData mInputData;

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 数据库连接 * */
    private Connection conn = null;

    /** 数据库连接 * */
    private boolean mFlag = false;

    /** 立即提交标志 * */
    private final boolean commitFlag = true;

    public PubSubmitC() {
    }

    public PubSubmitC(Connection con) {
        conn = con;
        mFlag = true;
    }

    /**
     * 建立连接，开启事务锁
     * @return boolean
     */
    public boolean createConn() {
        //建立数据库连接
        if (conn == null) {
            conn = DBConnPool.getConnection();
        }
        if (conn == null) {
            // @@错误处理
            CError.buildErr(this, "数据库连接失败");
            return false;
        }
        try {
            // 开始事务，锁表
            conn.setAutoCommit(false);
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    	return true;
    }
    
    /**
     * 传输数据的公共方法 传入数据
     * 
     * @param cInputData
     *            VData 数据操作
     * @param cOperate
     *            String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate) {
        // 首先将数据在本类中做一个备份
        mInputData = (VData) cInputData.clone();
        if (this.saveData()) {
            mInputData = null;
            return true;
        } else {
            mInputData = null;
            return false;
        }
    }
    
    /**
     * 数据提交，释放连接
     * @return boolean
     */
    public boolean commitConn() {
    	if (conn == null) {
            // @@错误处理
            CError.buildErr(this, "未建立数据库连接,提交失败！");
            return false;
        }
    	try {
    		conn.commit();
        } catch (Exception e) {
        	e.printStackTrace();
        	try {
                // 回滚事务、关闭连接
                conn.rollback();
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }finally {
            try {
                    conn.close();
                    conn = null;
            } catch (SQLException e) {
                    e.printStackTrace();
            }
          
        }
    	return true;
    }
    
    /**
     * 数据回滚，释放连接
     * @return boolean
     */
    public boolean rollbackConn() {
    	if (conn == null) {
            // @@错误处理
            CError.buildErr(this, "未建立数据库连接,提交失败！");
            return false;
        }
    	try {
    		conn.rollback();
        } catch (Exception e) {
        	e.printStackTrace();
        	try {
                // 回滚事务、关闭连接
                conn.rollback();
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }finally {
            try {
                    conn.close();
                    conn = null;
            } catch (SQLException e) {
                    e.printStackTrace();
            }
          
        }
    	return true;
    }
    /**
     * 判断连接是否存在
     * @return boolean
     */
    public boolean existsConn() {
    	if (conn == null) {
            return false;
        }
    	else
    	{
    		return true;
    	}
    }
    /**
     * 得到连接
     * @return boolean
     */
    public Connection getConn() {
    	if (conn == null) {
            // @@错误处理
            CError.buildErr(this, "未建立数据库连接,提交失败！");
            return null;
        }
    	else
    	{
    		return conn;
    	}
    }
    /**
     * 数据库操作
     * 
     * @return: boolean
     */
    private boolean saveData() {

        if (conn == null) {
            // @@错误处理
            CError.buildErr(this, "未查询到已建立的数据库连接!");
            return false;
        }

        try {

            String action = ""; // 操作方式，INSERT\UPDATE\DELETE
            String className = ""; // 类名
            StringBuffer tSBql = null;
            Object o = null; // Schema或Set对象
            Object DBObject = null; // DB或DBSet对象
            Method m = null; // 方法
            Constructor constructor = null; // 构造函数
            Class[] parameterC = new Class[1]; // 调用方法的参数数组
            Object[] parameterO = new Object[1]; // 调用方法的对象数组
            // System.out.println("mInputData.size():" + mInputData.size());
            // System.out.println("mInputData :" + mInputData);

            // 通过MMap来传递每个Schema或Set的数据库操作方式，约定使用
            MMap map = (MMap) mInputData.getObjectByObjectName("MMap", 0);
            if (map != null && map.keySet().size() != 0) {
                Set set = map.keySet();
                // Iterator iterator = map.keySet().iterator();
                // while (iterator.hasNext()) {

                // 循环mmap对象，执行操作
                for (int j = 0; j < set.size(); j++) {
                    // 获取操作对象Schema或Set或SQL
                    // o = iterator.next();
                    o = map.getOrder().get(String.valueOf(j + 1));
                    // 获取操作方式
                    action = (String) map.get(o);
                    if (action == null) {
                        continue;
                    }

                    // 构造相应的DB类名
                    className = o.getClass().getName();
                    if (className.endsWith("String")) {
                        if (action.equals("UPDATE")) {
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.db.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("S")));
                            tSBql.append("DB");
                            className = tSBql.toString();

                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                            System.out.println("执行SQL语句:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "执行更新语句失败");
                                conn.rollback();
                                conn.close();
                                conn = null;
                                return false;
                            }
                        }
                        if (action.equals("DELETE")) {
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.db.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("S")));
                            tSBql.append("DB");
                            className = tSBql.toString();

                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                             System.out.println("执行SQL语句:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "执行删除语句失败");
                                conn.rollback();
                                conn.close();
                                conn = null;
                                return false;
                            }
                        }
                        if (action.equals("INSERT")) {
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.db.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("S")));
                            tSBql.append("DB");
                            className = tSBql.toString();

                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                             System.out.println("执行SQL语句:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "执行插入语句失败原因：" + tExeSQL.mErrors.getError(0).errorMessage);
                                conn.rollback();
                                conn.close();
                                conn = null;
                                return false;
                            }
                        }
                    } else if (className.endsWith("Schema")) {
                        tSBql = new StringBuffer(50);
                        tSBql.append("com.sinosoft.lis.db.");
                        tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("S")));
                        tSBql.append("DB");
                        className = tSBql.toString();
                    } else if (className.endsWith("BLSet")) {
                        tSBql = new StringBuffer(50);
                        tSBql.append("com.sinosoft.lis.vdb.");
                        tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("B")));
                        tSBql.append("DBSet");
                        className = tSBql.toString();
                    } else if (className.endsWith("Set")) {
                        tSBql = new StringBuffer(50);
                        tSBql.append("com.sinosoft.lis.vdb.");
                        tSBql.append(className.substring(className.lastIndexOf(".") + 1, className.lastIndexOf("S")));
                        tSBql.append("DBSet");
                        className = tSBql.toString();
                    }
                    Class DBClass = Class.forName(className);

                    // 选择构造函数，构造相同事务的DB或DBSet对象
                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

                    // 给DB对象付值，将传入的Schema或Set对象的内容复制到DB中
                    parameterC[0] = o.getClass();
                    if (o.getClass().getName().endsWith("Schema")) {
                        m = DBObject.getClass().getMethod("setSchema", parameterC);
                    } else if (o.getClass().getName().endsWith("Set")) {
                        m = DBObject.getClass().getMethod("set", parameterC);
                    }
                    parameterO[0] = o;
                    m.invoke(DBObject, parameterO);

                    // 进行数据库操作
                    if (action.equals("INSERT")) {
                        m = DBObject.getClass().getMethod("insert", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (b.booleanValue()) {
                            continue;
                        } else {
                            CError.buildErr(this, "执行插入语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    } else if (action.equals("UPDATE")) {
                        m = DBObject.getClass().getMethod("update", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (b.booleanValue()) {
                            continue;
                        } else {
                            CError.buildErr(this, "执行更新语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    } else if (action.equals("DELETE")) {
                        m = DBObject.getClass().getMethod("delete", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (b.booleanValue()) {
                            continue;
                        } else {
                            CError.buildErr(this, "执行删除语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    } else if (action.equals("DELETE&INSERT")) {
                        // DELETE
                        m = DBObject.getClass().getMethod("delete", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);
                        if (!b.booleanValue()) {
                            CError.buildErr(this, "执行删除，插入语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }

                        // INSERT
                        m = DBObject.getClass().getMethod("insert", null);
                        b = (Boolean) m.invoke(DBObject, null);
                        if (!b.booleanValue()) {
                            CError.buildErr(this, "执行插入语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    } else if (action.equals("BLOBINSERT")) {
                        // add by Alex at 2005.1.12
                        // first,insert a record with a empty_blob , at the same time prepare the param for the second
                        // step
                        String aClassName = o.getClass().getName();
                        if (aClassName.endsWith("Schema")) {
                            String tSQL = "";
                            // 用于第二步update时的参数
                            String pWhereSQL = " and ";
                            String pTabName = "";
                            String pUpdateField = "";

                            String[] parmStrArr = getBlobInsertStr(o, tSQL, pWhereSQL, pTabName, pUpdateField);
                            m = o.getClass().getMethod("get" + parmStrArr[2], null);
                            InputStream ins = (InputStream) m.invoke(o, null);
                            if (CBlob.BlobInsert(ins, parmStrArr, conn)) {
                                ins.close();
                                ins = null;
                                continue;
                            } else {
                                CError.buildErr(this, "执行更新blob语句失败");
                                try {
                                    conn.rollback();
                                    conn.close();
                                    conn = null;
                                } catch (Exception e) {
                                }

                                ins.close();
                                System.out.println("tCBlob.BlobInsert" + " " + action + " Failed");
                                return false;
                            }
                        } else if (aClassName.endsWith("Set")) {
                            SchemaSet tset = (SchemaSet) o;
                            for (int i = 1; i <= tset.size(); i++) {
                                String tSQL = "";
                                // 用于第二步update时的参数
                                String pWhereSQL = " and ";
                                String pTabName = "";
                                String pUpdateField = "";

                                String[] parmStrArr = getBlobInsertStr(tset.getObj(i), tSQL, pWhereSQL, pTabName, pUpdateField);
                                m = tset.getObj(i).getClass().getMethod("get" + parmStrArr[2], null);
                                InputStream ins = (InputStream) m.invoke(tset.getObj(i), null);
                                if (CBlob.BlobInsert(ins, parmStrArr, conn)) {
                                    ins.close();
                                    ins = null;
                                    continue;
                                } else {
                                    CError.buildErr(this, "执行更新blob语句失败");
                                    try {
                                        conn.rollback();
                                        conn.close();
                                        conn = null;
                                    } catch (Exception e) {
                                    }

                                    ins.close();
                                    System.out.println("tCBlob.BlobInsert" + " " + action + " Failed");
                                    return false;
                                }

                            }
                        }

                    } else if (action.equals("BLOBUPDATE")) {
                        // add by Alex at 2005.1.12
                        // first,prepare the param for the UpdateBlob
                        String aClassName = o.getClass().getName();

                        String tSQL = "";
                        // 用于第二步update时的参数
                        String pWhereSQL = " and ";
                        String pTabName = "";
                        String pUpdateField = "";

                        String[] parmStrArr = getBlobInsertStr(o, tSQL, pWhereSQL, pTabName, pUpdateField);
                        m = o.getClass().getMethod("get" + parmStrArr[2], null);
                        InputStream ins = (InputStream) m.invoke(o, null);
                        if (CBlob.BlobUpdate(ins, parmStrArr, conn)) {
                            ins.close();
                            ins = null;
                            continue;
                        } else {
                            CError.buildErr(this, "执行更新blob语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            ins.close();
                            System.out.println("tCBlob.BlobUpdate" + " " + action + " Failed");
                            return false;
                        }
                    } else if (action.equals("BLOBDELETE")) {
                        // add by Alex at 2005.1.12
                        m = DBObject.getClass().getMethod("delete", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (b.booleanValue()) {
                            continue;
                        } else {
                            CError.buildErr(this, "执行删除语句失败");
                            try {
                                conn.rollback();
                                conn.close();
                                conn = null;
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            // @@错误处理
            CError.buildErr(this, e.toString());
            try {
                // 回滚事务、关闭连接
                conn.rollback();
                conn.close();
                conn = null;
            } catch (Exception ex) {
            }
            return false;
        } 
        return true;
    }

    /**
     * 获取Blob操作所需信息公共方法
     * 
     * @param o
     *            Object
     * @param tSQL
     *            String
     * @param pTabName
     *            String
     * @param pUpdateField
     *            String
     * @param pWhereSQL
     *            String
     * @return String[]
     */
    private static String[] getBlobInsertStr(Object o, String tSQL, String pTabName, String pUpdateField,
            String pWhereSQL) {
        String aClassName = o.getClass().getName();

        if (aClassName.endsWith("Schema")) {
            Schema s = (Schema) o;
            String[] pk = s.getPK();
            pTabName = aClassName.substring(aClassName.lastIndexOf(".") + 1, aClassName.lastIndexOf("S"));

            String ValPart = "values(";

            int nFieldCount = s.getFieldCount();
            int jj = 0;
            int blobnum = 0;
            for (int i = 0; i < nFieldCount; i++) {
                String strFieldName = s.getFieldName(i);
                String strFieldValue = s.getV(i);
                for (int ii = 0; ii < pk.length; ii++) {
                    if (strFieldName.equals(pk[ii])) {
                        pWhereSQL += strFieldName + " = '" + strFieldValue + "' and ";
                    }
                }
                int nFieldType = s.getFieldType(i);
                boolean bFlag = false;
                boolean blobFlag = false;
                switch (nFieldType) {
                case Schema.TYPE_STRING:
                case Schema.TYPE_DATE:
                    if (!strFieldValue.equals("null")) {
                        strFieldValue = "'" + strFieldValue + "'";
                        bFlag = true;
                    }else{
                    	bFlag = true;
                    }
                    break;
                case Schema.TYPE_DOUBLE:
                case Schema.TYPE_FLOAT:
                case Schema.TYPE_INT:
                    bFlag = true;
                    break;
                // 以此作为blob的判断条件,现阶段可用,且只支持一个blob字段
                case Schema.TYPE_NOFOUND:
                    bFlag = true;
                    blobFlag = true;
                    blobnum = i;
                    break;
                default:
                    bFlag = false;
                    break;
                }

                if (bFlag) {
                    jj++;
                    if (jj != 1) {
                        ValPart += ",";
                    }
                    if (blobFlag) {
                        ValPart += "empty_blob()";
                    } else {
                        ValPart += strFieldValue;
                    }
                }
            }
            ValPart += ")";
            tSQL = "insert into " + pTabName + " " + ValPart;
            if (jj == 0) {
                tSQL = "";
            }
            pUpdateField = s.getFieldName(blobnum);
            if (pWhereSQL.lastIndexOf("and") != -1) {
                pWhereSQL = "and " + pWhereSQL.substring(0, pWhereSQL.lastIndexOf("and"));

            }
        } else {
            return null;
        }
        String[] returnStr = new String[4];
        returnStr[0] = tSQL;
        returnStr[1] = pTabName;
        returnStr[2] = pUpdateField;
        returnStr[3] = pWhereSQL;
        return returnStr;
    }

    public static void main(String[] args) {
    	PubSubmitC pubSubmit1 = new PubSubmitC();
        LLCaseRelaSchema tLLCaseRelaSchema = new LLCaseRelaSchema();
        LLCaseRelaSet tLLCaseRelaSet = new LLCaseRelaSet();
        tLLCaseRelaSchema.setCaseNo("200055556666001");
        tLLCaseRelaSchema.setCaseRelaNo("1111");
        tLLCaseRelaSchema.setSubRptNo("2222");
        tLLCaseRelaSet.add(tLLCaseRelaSchema);
        VData mResult = new VData();
        MMap map = new MMap();
        map.put(tLLCaseRelaSet, "INSERT");
        mResult.addElement(map);
        pubSubmit1.createConn();
        pubSubmit1.submitData(mResult, "");
        pubSubmit1.commitConn();
    }
}
