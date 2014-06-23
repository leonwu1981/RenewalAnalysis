/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;
import java.sql.SQLException;

import com.sinosoft.lis.schema.LMCalFactorSchema;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.vschema.LMCalFactorSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.StrTool;

/*
 * <p>Title: ���Ѽ����� </p> <p>Description: ͨ������ı�����Ϣ��������Ϣ������������Ϣ����ȡ��Ϣ </p> <p>Copyright: Copyright (c) 2002</p> <p>Company:
 * sinosoft</p> @author HST
 * 
 * @version 1.0 @date 2002-07-01
 */
public class Calculator {
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** ������Ҫ�õ��ı������� */
    public String PolNo;

    /**
     * ����Ҫ�ش�ŵ����� 1--����Ҫ�ء��ͳ���Ҫ����ͬ���������ȼ���� 2--��չҪ�أ�����SQL�����¼��� 3--����Ҫ�أ�ֻȡĬ��ֵ��
     */
    private LMCalFactorSet mCalFactors1 = new LMCalFactorSet(); // ��Ż���Ҫ��

    public LMCalFactorSet mCalFactors = new LMCalFactorSet();

    private boolean mFlag = false;

    private Connection conn = null;

    public Calculator() {
    }

    public Calculator(Connection conn) {
        mFlag = true;
        this.conn = conn;
    }

    // @Field
    // �������
    private String CalCode = "";

    // �㷨��ӦSQL������ڱ�ṹ
    private LMCalModeSchema mLMCalMode = new LMCalModeSchema();

    private CachedRiskInfo mCRI = CachedRiskInfo.getInstance();

    /**
     * ���ӻ���Ҫ��
     * 
     * @param cFactorCode
     *            Ҫ�صı���
     * @param cFactorValue
     *            Ҫ�ص�����ֵ
     */
    public void addBasicFactor(String cFactorCode, String cFactorValue) {
        LMCalFactorSchema tS = new LMCalFactorSchema();
        tS.setFactorCode(cFactorCode);
        tS.setFactorDefault(cFactorValue);
        tS.setFactorType("1");
        mCalFactors1.add(tS);
    }

    // @Method
    public void setCalCode(String tCalCode) {
        CalCode = tCalCode;
    }

    /**
     * ��ʽ���㺯��
     * 
     * @return: String ����Ľ����ֻ���ǵ�ֵ�����ݣ������͵�ת�����ַ��ͣ�
     * @author: YT
     */
    public String calculate() {
        if (!mFlag) {
            conn = DBConnPool.getConnection();
        }
        try {
            // System.out.println("start calculate++++++++++++++");
            if (!checkCalculate()) {
                return "0";
            }
            // ȡ�����ݿ��м���Ҫ��
            LMCalFactorSet tLMCalFactorSet = mCRI.findCalFactorByCalCodeClone(CalCode);
            // System.out.println("calCode :" + CalCode);

            if (tLMCalFactorSet == null) {
                return "0";
            }

            mCalFactors.add(tLMCalFactorSet);
            // ���ӻ���Ҫ��
            mCalFactors.add(mCalFactors1);
            // ���ͼ���Ҫ��
            if (!interpretFactors()) {
                return "0";
            }

            // ��ȡSQL���
            if (!getSQL()) {
                return "0";
            }

            // ����SQL����еı���
            if (!interpretFactorInSQL()) {
                return "0";
            }
            // ִ��SQL���
            // System.out.println("start execute SQL.....");
            return executeSQL();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            if (!mFlag) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ִ��SQL���
     * 
     * @return String
     */
    private String executeSQL() {
    	long t0 = System.currentTimeMillis();
        String tReturn = "0";
        ExeSQL tExeSQL = new ExeSQL(conn);
        tReturn = tExeSQL.getOneValue(mLMCalMode.getCalSQL());
        System.out.println();
        String tResult="";
        if(tReturn.length()<10){
        	tResult="@"+tReturn;
        }
        t0 = System.currentTimeMillis()-t0;
        System.out.println("/* ִ�е�����ǣ�("+mLMCalMode.getCalCode()+tResult+")��ִ��ʱ�䣺"+t0+"���� */"+mLMCalMode.getCalSQL());
        if(t0>500)
        	System.out.println("/* ���棺�㷨ִ��sql��ʱ��("+mLMCalMode.getCalCode()+tResult+")��ִ��ʱ�䣺"+t0+"���� */"+mLMCalMode.getCalSQL());
        if (tExeSQL.mErrors.needDealError()) {
            // @@������
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "executeSQL";
//            tError.errorMessage = "ִ��SQL��䣺" + mLMCalMode.getCalCode() + "ʧ��!";
            tError.errorMessage = "ִ��SQL���ʧ��!";
            this.mErrors.addOneError(tError);
            return "0";
        }
        return tReturn;
    }

    /**
     * ����SQL����еı���
     * 
     * @return boolean
     */
    private boolean interpretFactorInSQL() {
        String tStr = null;
        // tStr1 = "";
        StringBuffer tSBql = null;
        String tSql = mLMCalMode.getCalSQL();
        try {
            while (true) {
                tStr = PubFun.getStr(tSql, 2, "?");
                if (tStr.equals("")) {
                    break;
                }
                tSBql = new StringBuffer();
                tSBql.append("?");
                tSBql.append(tStr);
                tSBql.append("?");
                // tStr1 = "?" + tStr.trim() + "?";
                // �滻����
                // tSql = StrTool.replaceEx(tSql, tStr1, getValueByName(tStr));
                tSql = StrTool.replaceEx(tSql, tSBql.toString(), getValueByName(tStr));
            }
        } catch (Exception ex) {
            // @@������
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "interpretFactorInSQL";
            tError.errorMessage = "����" + tSql + "�ı���:" + tStr + "ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLMCalMode.setCalSQL(tSql);
        return true;
    }

    /**
     * ������Ҫ�ص�ֵ
     * 
     * @param cF
     *            LMCalFactorSchema
     * @return String
     */
    private String calSubFactors(LMCalFactorSchema cF) {
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        Calculator tNewC = null;
        if(!this.mFlag)
        	tNewC = new Calculator();
        else
        	tNewC = new Calculator(this.conn);
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            // ����ǻ���Ҫ�ػ���Ҫ�أ�������һ��Ҫ����
            if (tC.getFactorType().toUpperCase().equals("1") || tC.getFactorType().toUpperCase().equals("3")) {
                tNewC.mCalFactors.add(tC);
            }
        }
        tNewC.setCalCode(cF.getFactorCalCode());
        // System.out.println("----SubFactor---calcode = " + cF.getFactorCalCode());
        tReturn = String.valueOf(tNewC.calculate());
        // System.out.println("----SubFactor = " + tReturn);
        // ����д����򽫴��󿽱�����һҪ��,���ҷ���"0"
        if (tNewC.mErrors.needDealError()) {
            this.mErrors.copyAllErrors(tNewC.mErrors);
            tReturn = "0";
        }
        return tReturn;
    }

    /**
     * ��ȡSQL���
     * 
     * @return boolean
     */
    private boolean getSQL() {
        CachedRiskInfo cri = CachedRiskInfo.getInstance();
        LMCalModeSchema tLMCalModeSchema = cri.findCalModeByCalCode(CalCode);

        if (tLMCalModeSchema == null) {
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "getSql";
            tError.errorMessage = "�õ�" + CalCode + "��SQL���ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLMCalMode.setSchema(tLMCalModeSchema);

        return true;
    }

    /**
     * ����Ҫ�������еķǱ���Ҫ��
     * 
     * @return boolean
     */
    private boolean interpretFactors() {
        int i, iMax;
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            // �������չҪ�أ�����͸���չҪ��
            if (tC.getFactorType().toUpperCase().equals("2")) {
                tC.setFactorDefault(calSubFactors(tC));
                // ����ڼ�����Ҫ�ص�ʱ���������򷵻�false
                if (this.mErrors.needDealError()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * У�����������Ƿ��㹻
     * 
     * @return boolean �������ȷ����false
     */
    private boolean checkCalculate() {
        if (CalCode == null || CalCode.equals("")) {
            // @@������
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "checkCalculate";
            tError.errorMessage = "����ʱ�����м�����롣";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ���ݱ������õ�������ֵ
     * 
     * @param cVarName
     *            String
     * @return String �������ȷ����"",���򷵻ر���ֵ
     */
    private String getValueByName(String cVarName) {
        cVarName = cVarName.toLowerCase();
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            if (tC.getFactorCode().toLowerCase().equals(cVarName)) {
                tReturn = tC.getFactorDefault();
                break;
            }
        }
        return StrTool.cTrim(tReturn);
    }

    /**
     * Kevin 2003-08-20 �õ���������SQL��䣬������SQL���ִ�к��ֵ��
     * 
     * @return String
     */
    public String getCalSQL() {
        if (!mFlag) {
            conn = DBConnPool.getConnection();
        }
        try {
            if (!checkCalculate()) {
                return "0";
            }

            LMCalFactorSet tLMCalFactorSet = mCRI.findCalFactorByCalCodeClone(CalCode);

            if (tLMCalFactorSet == null) {
                return "0";
            }

            mCalFactors.add(tLMCalFactorSet);
            // ���ӻ���Ҫ��
            mCalFactors.add(mCalFactors1);
            // ���ͼ���Ҫ��
            if (!interpretFactors()) {
                return "0";
            }

            // ��ȡSQL���
            if (!getSQL()) {
                return "0";
            }

            // ����SQL����еı���
            if (!interpretFactorInSQL()) {
                return "0";
            }

            // ���ؽ�������SQL���
            return mLMCalMode.getCalSQL();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            if (!mFlag) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ���Ժ���
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        // Calculator tC=new Calculator();
        // LMCalFactorSchema tLMCalFactorSchema=new LMCalFactorSchema();
        // tC.addBasicFactor("PolNo","00000120021100000000");
        // tC.setCalCode("001001") ;
        // System.out.println(tC.calculate());
        // if(tC.mErrors.needDealError())
        // System.out.println(tC.mErrors.getFirstError());
    }
}
