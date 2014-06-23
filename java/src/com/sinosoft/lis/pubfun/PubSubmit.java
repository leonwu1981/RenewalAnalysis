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

import org.apache.log4j.Logger;

import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;

/**
 * <p>
 * Title:
 * </p>
 * �����ύ��
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed Modify:yuanaq--1,��Ժ�blob�ֶ���blob�ֶβ������һ�е����2,���blob��set������
 * @version 1.0
 */
public class PubSubmit {
    // ����������
    private VData mInputData;

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** ���ݿ����� * */
    private Connection conn = null;

    /** ���ݿ����� * */
    private boolean mFlag = false;

    /** �����ύ��־ * */
    private final boolean commitFlag = true;
    /** mmap��������**/
    public static int Max_Warn_Size;
    
//    public static Logger m_log = Logger.getLogger(PubSubmit.class);
//    static{
//    	Max_Warn_Size = 100;
//    	ExeSQL tExeSql = new ExeSQL();
//    	String tMax_Warn_Size = tExeSql.getOneValue("select a.sysvarvalue from ldsysvar a where a.sysvar='map_warn_size'");
//    	if(tMax_Warn_Size!=null && !tMax_Warn_Size.equals("")){
//    		Max_Warn_Size = Integer.parseInt(tMax_Warn_Size);
//    	}
//    }

    public PubSubmit() {
    }

    public PubSubmit(Connection con) {
        conn = con;
        mFlag = true;
    }

    /**
     * �������ݵĹ������� ��������
     * 
     * @param cInputData
     *            VData ���ݲ���
     * @param cOperate
     *            String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate) {
        // ���Ƚ������ڱ�������һ������
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
     * ���ݿ����
     * 
     * @return: boolean
     */
    private boolean saveData() {
        // �������ݿ�����
        if (conn == null) {
            conn = DBConnPool.getConnection();
        }

        if (conn == null) {
            // @@������
            CError.buildErr(this, "���ݿ�����ʧ��");
            return false;
        }

        try {
            // ��ʼ��������
            conn.setAutoCommit(false);

            String action = ""; // ������ʽ��INSERT\UPDATE\DELETE
            String className = ""; // ����
            StringBuffer tSBql = null;
            Object o = null; // Schema��Set����
            Object DBObject = null; // DB��DBSet����
            Method m = null; // ����
            Constructor constructor = null; // ���캯��
            Class[] parameterC = new Class[1]; // ���÷����Ĳ�������
            Object[] parameterO = new Object[1]; // ���÷����Ķ�������
            // System.out.println("mInputData.size():" + mInputData.size());
            // System.out.println("mInputData :" + mInputData);

            // ͨ��MMap������ÿ��Schema��Set�����ݿ������ʽ��Լ��ʹ��
            MMap map = (MMap) mInputData.getObjectByObjectName("MMap", 0);
            if (map != null && map.keySet().size() != 0) {
                Set set = map.keySet();
//                int mapSize = set.size();
//                if(mapSize>=PubSubmit.Max_Warn_Size){
//                	String errMsg = "mmap��������������ֵ"+PubSubmit.Max_Warn_Size+"��ʵ��ֵΪ"+mapSize;
//                	m_log.error(errMsg);
//                	try {
//                		throw new Exception(errMsg);
//                    } catch (Exception ex) {
//                		m_log.error(ex.getMessage(), ex);
//                    }
//
//                }
                // Iterator iterator = map.keySet().iterator();
                // while (iterator.hasNext()) {

                // ѭ��mmap����ִ�в���
                for (int j = 0; j < set.size(); j++) {
                    // ��ȡ��������Schema��Set��SQL
                    // o = iterator.next();
                    o = map.getOrder().get(String.valueOf(j + 1));
                    // ��ȡ������ʽ
                    action = (String) map.get(o);
                    if (action == null) {
                        continue;
                    }

                    // ������Ӧ��DB����
                    className = o.getClass().getName();
                    if (className.endsWith("String")) {
                        if (action.equals("UPDATE")) {
                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                            System.out.println("ִ��SQL���:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "ִ�и������ʧ��");
                                conn.rollback();

                                return false;
                            }
                        }
                        if (action.equals("DELETE")) {
                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                             System.out.println("ִ��SQL���:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "ִ��ɾ�����ʧ��");
                                conn.rollback();

                                return false;
                            }
                        }
                        if (action.equals("INSERT")) {
                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                             System.out.println("ִ��SQL���:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL)) {
                                continue;
                            } else {
                                CError.buildErr(this, "ִ�в������ʧ��ԭ��" + tExeSQL.mErrors.getError(0).errorMessage);
                                conn.rollback();

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

                    // ѡ���캯����������ͬ�����DB��DBSet����
                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

                    // ��DB����ֵ���������Schema��Set��������ݸ��Ƶ�DB��
                    parameterC[0] = o.getClass();
                    if (o.getClass().getName().endsWith("Schema")) {
                        m = DBObject.getClass().getMethod("setSchema", parameterC);
                    } else if (o.getClass().getName().endsWith("Set")) {
                        m = DBObject.getClass().getMethod("set", parameterC);
                    }
                    parameterO[0] = o;
                    m.invoke(DBObject, parameterO);

                    // �������ݿ����
                    if (action.equals("INSERT")) {
                        m = DBObject.getClass().getMethod("insert", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (b.booleanValue()) {
                            continue;
                        } else {
                            CError.buildErr(this, "ִ�в������ʧ��");
                            try {
                                conn.rollback();
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
                            CError.buildErr(this, "ִ�и������ʧ��");
                            try {
                                conn.rollback();
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
                            CError.buildErr(this, "ִ��ɾ�����ʧ��");
                            try {
                                conn.rollback();
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
                            CError.buildErr(this, "ִ��ɾ�����������ʧ��");
                            try {
                                conn.rollback();
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }

                        // INSERT
                        m = DBObject.getClass().getMethod("insert", null);
                        b = (Boolean) m.invoke(DBObject, null);
                        if (!b.booleanValue()) {
                            CError.buildErr(this, "ִ�в������ʧ��");
                            try {
                                conn.rollback();
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
                            // ���ڵڶ���updateʱ�Ĳ���
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
                                CError.buildErr(this, "ִ�и���blob���ʧ��");
                                try {
                                    conn.rollback();
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
                                // ���ڵڶ���updateʱ�Ĳ���
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
                                    CError.buildErr(this, "ִ�и���blob���ʧ��");
                                    try {
                                        conn.rollback();
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
                        // ���ڵڶ���updateʱ�Ĳ���
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
                            CError.buildErr(this, "ִ�и���blob���ʧ��");
                            try {
                                conn.rollback();
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
                            CError.buildErr(this, "ִ��ɾ�����ʧ��");
                            try {
                                conn.rollback();
                            } catch (Exception e) {
                            }

                            System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
                            return false;
                        }
                    }
                }
            }

            // �����ύ:Ϊ��������һ������������׼����Ϻ�һ�����ύ.
            if (commitFlag) {
                conn.commit();
            } else {
                System.out.println("---End Datebase Operation, but not Commit in AutoBLS---");
            }
        } catch (Exception e) {
            // @@������
            CError.buildErr(this, e.toString());
            try {
                // �ع����񡢹ر�����
                conn.rollback();
            } catch (Exception ex) {
            }
            return false;
        } finally {
            if (!mFlag) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * ��ȡBlob����������Ϣ��������
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
                strFieldValue=strFieldValue.replaceAll("'", "''");
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
                // �Դ���Ϊblob���ж�����,�ֽ׶ο���,��ֻ֧��һ��blob�ֶ�
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
        PubSubmit pubSubmit1 = new PubSubmit();
        LCPolPrintSchema tLCPolPrintSchema = new LCPolPrintSchema();
        LCPolPrintSet tLCPolPrintSet = new LCPolPrintSet();
        tLCPolPrintSchema.setMainPolNo("410110001000065");
        tLCPolPrintSchema.setManageCom("86");
        tLCPolPrintSchema.setPrtFlag("N");
        tLCPolPrintSchema.setPrtTimes(1);
        tLCPolPrintSchema.setMakeDate(PubFun.getCurrentDate());
        tLCPolPrintSchema.setMakeTime(PubFun.getCurrentTime());
        tLCPolPrintSchema.setOperator("001");
        tLCPolPrintSchema.setModifyDate(PubFun.getCurrentDate());
        tLCPolPrintSchema.setModifyTime(PubFun.getCurrentTime());
        tLCPolPrintSchema.setPolType("1");
        XmlExport xmlexport = new XmlExport();
        xmlexport.createDocument("PrtEndorsementApp.vts", "printer"); // ��ý����žͳ�ʼ��xml�ĵ�
        TextTag textTag = new TextTag();
        textTag.add("PrintDate", StrTool.replace(StrTool.getDate(), "/", "-"));
        xmlexport.addTextTag(textTag);
        InputStream ins = xmlexport.getInputStream();
        tLCPolPrintSchema.setPolInfo(ins);
        tLCPolPrintSet.add(tLCPolPrintSchema);
        VData mResult = new VData();// BLOBUPDATE
        MMap map = new MMap();// BLOBINSERT,BLOBDELETE
        map.put(tLCPolPrintSet, "BLOBDELETE");
        mResult.addElement(map);
        pubSubmit1.submitData(mResult, "");
    }
}
