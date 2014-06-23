/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.schema.LMCalFactorSchema;
import com.sinosoft.lis.vschema.LMCalFactorSet;
import com.sinosoft.utility.*;

/*
 * <p>Title: ���������� </p>
 * <p>Description: ͨ������ı�����Ϣ��������Ϣ������������Ϣ����ȡ��Ϣ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author HST
 * @version 1.0
 * @date 2002-07-01
 */
public class PubCalculator
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** ������Ҫ�õ��ı������� */
    //public String  PolNo;

    /** ����Ҫ�ش�ŵ�����
     *  1--����Ҫ�ء��ͳ���Ҫ����ͬ���������ȼ����
     *  2--��չҪ�أ�����SQL�����¼���
     *  3--����Ҫ�أ�ֻȡĬ��ֵ��
     */
    //private LMCalFactorSet mCalFactors1=new LMCalFactorSet();//��Ż���Ҫ��

    public LMCalFactorSet mCalFactors = new LMCalFactorSet();

    // @Field
    //�������
    private String mSourCalSql = "";
    private String mDestCalSql = "";

    //�㷨��ӦSQL������ڱ�ṹ

    /**
     * ���ӻ���Ҫ��
     * @param cFactorCode Ҫ�صı���
     * @param cFactorValue  Ҫ�ص�����ֵ
     */
    public void addBasicFactor(String cFactorCode, String cFactorValue)
    {
        LMCalFactorSchema tS = new LMCalFactorSchema();
        tS.setFactorCode(cFactorCode);
        tS.setFactorDefault(cFactorValue);
        tS.setFactorType("1"); //��ʱ����Ҫ��
        mCalFactors.add(tS);
    }


    // @Method
    public void setCalSql(String tCalSql)
    {
        mSourCalSql = tCalSql;
    }


    /**
     * ��ʽ���㺯��
     * @return: String ����Ľ����ֻ���ǵ�ֵ�����ݣ������͵�ת�����ַ��ͣ�
     * @author: YT
     **/
    public String calculate()
    {
        if (!checkCalculate())
            return "0";

        //����SQL����еı���
        if (!interpretFactorInSQL())
            return "0";
        //ִ��SQL���
        System.out.println("PubCalculate execute SQL.....");
        return executeSQL();
    }

    public String calculateEx()
    {
        if (!checkCalculate())
            return "0";
        //����SQL����еı���
        if (!interpretFactorInSQL())
            return "0";
        //ִ��SQL���
        System.out.println("PubCalculate execute SQL.....");
        return mDestCalSql;
    }


    /**
     * ִ��SQL���
     * @return String
     */
    private String executeSQL()
    {
        String tReturn = "0";
        ExeSQL tExeSQL = new ExeSQL();
        tReturn = tExeSQL.getOneValue(mDestCalSql);
        if (tExeSQL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "executeSQL";
            tError.errorMessage = "ִ��SQL��䣺" + mDestCalSql + "ʧ��!";
            this.mErrors.addOneError(tError);
            return "0";
        }
        return tReturn;
    }


    /**
     * ����SQL����еı���
     * @return boolean
     */
    private boolean interpretFactorInSQL()
    {
        String tSql, tStr = "", tStr1 = "";
        tSql = mSourCalSql;
        try
        {
            while (true)
            {
                tStr = PubFun.getStr(tSql, 2, "?");
                if (tStr.equals(""))
                    break;
                tStr1 = "?" + tStr.trim() + "?";
                //�滻����
                tSql = StrTool.replaceEx(tSql, tStr1, getValueByName(tStr));
            }
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "interpretFactorInSQL";
            tError.errorMessage = "����" + tSql + "�ı���:" + tStr + "ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        mDestCalSql = tSql;
        return true;
    }


    /**
     * У�����������Ƿ��㹻
     * @return boolean �������ȷ����false
     */
    private boolean checkCalculate()
    {
        if (mSourCalSql == null || mSourCalSql.equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "checkCalculate";
            tError.errorMessage = "����ʱ�����м���SQL��䡣";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * ���ݱ������õ�������ֵ
     * �������ȷ����"",���򷵻ر���ֵ
     * @param cVarName String
     * @return String
     */
    private String getValueByName(String cVarName)
    {
        cVarName = cVarName.toLowerCase();
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++)
        {
            tC = mCalFactors.get(i);
            if (tC.getFactorCode().toLowerCase().equals(cVarName))
            {
                tReturn = tC.getFactorDefault();
                break;
            }
        }
        return tReturn;
    }


    /**
     * Kevin 2003-08-20
     * �õ���������SQL��䣬������SQL���ִ�к��ֵ��
     * @return String
     */
    public String getCalSQL()
    {

        if (!checkCalculate())
            return "0";

        //����SQL����еı���
        if (!interpretFactorInSQL())
            return "0";

        // ���ؽ�������SQL���
        return mSourCalSql;
    }

    public static void main(String[] args)
    {
//        Calculator tC = new Calculator();
//        LMCalFactorSchema tLMCalFactorSchema = new LMCalFactorSchema();
//        tC.addBasicFactor("PolNo", "00000120021100000000");
//        tC.setCalCode("001001");
//        System.out.println(tC.calculate());
//        if (tC.mErrors.needDealError())
//            System.out.println(tC.mErrors.getFirstError());
    }
}
