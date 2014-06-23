/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import com.sinosoft.lis.schema.LMCheckFieldSchema;
import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description:
 * �����ࣺ�������������鴫��Ĳ����а������ֶΡ�
 * ����Ψһ�Ĳ���VData,ĿǰVData�а���һ��������Ա��FieldCarrier�࣬��FieldCarrier����Ϊ����Ҫ������ֶε�����.
 * �����Ҫ��������VData�з���������Ա���Ա���չʹ��
 * </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author liujw
 * @version 1.0
 */
public class CheckFieldCom
{
    /** �������� */
    public CErrors mErrors = new CErrors();
    /**�������ݼ���*/
//    private VData mResult = new VData();
    /**�����ʾ��Ϣ�����ݼ���*/
//    private VData mResultMsg = new VData();
    /**�������Ҫ����,���ڼ���*/
    private Calculator mCalculator = new Calculator();
    /**�������Ҫ�ص�����*/
    private FieldCarrier mFieldCarrier = new FieldCarrier();
    /**���洫�������У����Ϣ�����ݼ�¼*/
    private LMCheckFieldSchema mLMCheckFieldSchema = new LMCheckFieldSchema();
    /**������ݴ��������У����Ϣ�����ݴ����ݿ��в�ѯ���ļ���*/
    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();

    /**
     * �����ֶε�������
     * @param InputData VData
     * @return boolean
     */
    public boolean CheckField(VData InputData)
    {
        if (!InitData(InputData))
        {
            return false;
        }
        if (!SaveFactor())
        {
            return false;
        }
        if (!QueryLMCheckFieldSet())
        {
            return false;
        }
        if (!CheckProcedure())
        {
            return false;
        }

        return true;
    }

    /**
     * ��ʼ�����������
     * @param InputData VData
     * @return boolean
     */
    private boolean InitData(VData InputData)
    {
        if (InputData == null)
        {
            return SetError("InitData", "�������(VData)����Ϊ�գ�");
        }
        //�������崫��
        mFieldCarrier = (FieldCarrier) InputData.getObjectByObjectName(
                "FieldCarrier", 0);
        if (mFieldCarrier == null)
        {

            return SetError("InitData", "�������(FieldCarrier)����Ϊ�գ�");
        }
        //�������崫��
        mLMCheckFieldSchema = (LMCheckFieldSchema) InputData.
                              getObjectByObjectName("LMCheckFieldSchema", 0);
        if (mLMCheckFieldSchema == null)
        {

            return SetError("InitData", "�������(LMCheckFieldSchema)����Ϊ�գ�");
        }

        return true;
    }

    /**
     * ���������FieldCarrier�е����е��ֶ�-���浽����Ҫ�����С����ü���¼A(����"��¼A"����)
     * @return boolean
     */
    private boolean SaveFactor()
    {
        Class ClassOfFieldCarrier = mFieldCarrier.getClass(); //�õ������
        Field[] fields = ClassOfFieldCarrier.getDeclaredFields(); //�õ������ֶζ���ļ���
        AccessibleObject.setAccessible(fields, true); //���������ֶζ��ǿɲ����ģ�public,protect,private��
        for (int n = 0; n < fields.length; n++)
        {
            String fieldName = fields[n].getName(); //�õ���ǰ�ֶε�����
            Class classOfFieldType = fields[n].getType(); //�õ���ǰ�ֶε����Ͷ���
            String fieldTypeName = classOfFieldType.getName(); //�õ���ǰ�ֶε����Ͷ��������(Long,String...)
            if (fields[n].isAccessible()) //�����ǰ�ֶεĿɴ�ȡ����Ϊ��
            {
                try
                {
                    //�õ���ǰ�ֶε�ֵ�Ķ���ע���÷���������Ϊ�ֶ�������ʵ������
                    Object objectOfFieldValue = fields[n].get(mFieldCarrier);
                    if (objectOfFieldValue != null)
                    {
                        mCalculator.addBasicFactor(fieldName,
                                objectOfFieldValue.toString());
                    }
                }
                catch (Exception ex)
                {
                    //ex.printStackTrace();
                    System.out.println("�׳����⣺" + ex.toString());
                    return SetError("SaveField", ex.toString());
                }
            }
            else
            {
                return SetError("SaveField", "����������ֶ�" + fieldName + "����Ϊ���ܴ�ȡ��");
            }
        }
        return true;
    }

    private boolean QueryLMCheckFieldSet()
    {
        CachedRiskInfo cri = CachedRiskInfo.getInstance();

        mLMCheckFieldSet =
                cri.findCheckFieldByRiskCodeClone(
                        mLMCheckFieldSchema.getRiskCode(),
                        mLMCheckFieldSchema.getFieldName());

        if (mLMCheckFieldSet == null)
        {
            return SetError("GetLMCheckFieldSet", "��ѯCheckField����ʧ�ܣ�");
        }
        return true;
    }

    /**
     * �����ֶεĹ���(��չʱ���Ը��ݲ�ͬ�����չ)
     * @return boolean
     */
    private boolean CheckProcedure()
    {
        for (int n = 1; n <= mLMCheckFieldSet.size(); n++)
        {
            LMCheckFieldSchema tLMCheckFieldSchema = mLMCheckFieldSet.get(n);
            mCalculator.setCalCode(tLMCheckFieldSchema.getCalCode()); //��Ӽ������
            String RSTR = mCalculator.calculate(); //�õ������Ľ��
            //���������Ϊ�գ���˵����У���ֶε�ֵ����,��Ϊ����ȷ��ȡֵ��Χ��û��ȡ��ֵ
            if (RSTR == null || RSTR.equals(""))
            {
                //���ִ��󽫷���ֵ��Ч�����ΪN,ֱ������
                tLMCheckFieldSchema.setReturnValiFlag("N");
                mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                break;
            }
            else
            {
                //�������Ҫ�Լ����õ���ֵ��ValiFlag��У��
                if (tLMCheckFieldSchema.getValiFlag() == null ||
                    tLMCheckFieldSchema.getValiFlag().equals(""))
                {
                    //������ֵ��Ч�����ΪY
                    tLMCheckFieldSchema.setReturnValiFlag("Y");
                    mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                    continue;
                }
                else //�����Ҫ�Լ����õ���ֵ��ValiFlag��У��
                {
                    boolean valiflag = false;
//                    PubFun tPub = new PubFun();
                    String valiData = tLMCheckFieldSchema.getValiFlag(); //�õ���Чֵ
                    String[] arrValiData;
                    int index = valiData.indexOf(";");
                    if (index == -1) //���û�зֺ�,����Ϊ�ǵ���һ����.
                    {
                        arrValiData = new String[1];
                        arrValiData[0] = valiData;
                    }
                    else
                    {
                        arrValiData = PubFun.split(valiData, ";");
                    }
                    for (int i = 0; i < arrValiData.length; i++)
                    {
                        if (RSTR.trim().equals(arrValiData[i]))
                        {
                            valiflag = true;
                            tLMCheckFieldSchema.setReturnValiFlag("Y");
                            mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                            break;
                        }
                    }
                    if (!valiflag)
                    {
                        //������ֵ��Ч�����ΪN.ֱ������
                        tLMCheckFieldSchema.setReturnValiFlag("N");
                        mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                        break;
                    }
                }
            }
        }
        return true;
    }

    /**
     * ����LMCheckFieldSet�������
     * @return LMCheckFieldSet
     */
    public LMCheckFieldSet GetCheckFieldSet()
    {
        return mLMCheckFieldSet;
    }

//    /**
//     * ������ʾ��Ϣ�������
//     * @return VData
//     */
//    private VData GetResultMsg()
//    {
//        return mResultMsg;
//    }
//
//    /**
//     * ����������Ϣ�������--�ݲ���
//     * @return VData
//     */
//    private VData GetResult()
//    {
//        return mResult;
//    }


    /**
     * ��������
     * @param funName String
     * @param errMsg String
     * @return boolean
     */
    private boolean SetError(String funName, String errMsg)
    {
        // @@������
        CError tError = new CError();
        tError.moduleName = "CheckFieldCom";
        tError.functionName = funName;
        tError.errorMessage = errMsg;
        this.mErrors.addOneError(tError);
        return false;
    }

    public static void main(String args[])
    {
//        CheckFieldCom tCheckFieldCom = new CheckFieldCom();
//        VData tVData = new VData();
//        FieldCarrier tFieldCarrier = new FieldCarrier();
//        tFieldCarrier.setPayEndYear(5); //�����ڼ�-88|����
//        tFieldCarrier.setPayIntv(12); //�ɷѼ��-12�꽻
//        tFieldCarrier.setPolNo("86110020030110001111");
//        tFieldCarrier.setMult(2);
//        tFieldCarrier.setAppAge(17);
//        tFieldCarrier.setIDNo("340102650117351");
//        tFieldCarrier.setIDType("0");
//        tFieldCarrier.setRiskCode("121703");
//        tFieldCarrier.setMainPolNo("86110020030110004205");

//        tVData.add(tFieldCarrier);
//        LMCheckFieldSchema tLMCheckFieldSchema = new LMCheckFieldSchema();
//        tLMCheckFieldSchema.setRiskCode("121703");
//        tLMCheckFieldSchema.setFieldName("TB"); //Ͷ��
//        tVData.add(tLMCheckFieldSchema);
//        if (tCheckFieldCom.CheckField(tVData) == false)
//        {
//            System.out.println("error");
//        }
//        else
//        {
//            LMCheckFieldSet mLMCheckFieldSet = tCheckFieldCom.GetCheckFieldSet();
//            for (int n = 1; n <= mLMCheckFieldSet.size(); n++)
//            {
//                LMCheckFieldSchema t = mLMCheckFieldSet.get(n);
//                if (t.getReturnValiFlag().equals("N"))
//                {
//                    if (t.getMsgFlag().equals("Y"))
//                    {
//                        System.out.println(t.getMsg());
//                    }
//                }
//            }
//        }
    }
}

//��¼A��
//���ӻ���Ҫ��,������ֹ���ʽ
//      mCalculator.addBasicFactor("Get", mFieldCarrier.getGet() );         /** ���� */
//      mCalculator.addBasicFactor("Mult", mFieldCarrier.getMult() );       /** ���� */
//      mCalculator.addBasicFactor("Prem", mFieldCarrier.getPrem() );       /** ���� */
//      mCalculator.addBasicFactor("PayIntv", mFieldCarrier.getPayIntv() ); /** �ɷѼ�� */
//      mCalculator.addBasicFactor("GetIntv", mFieldCarrier.getGetIntv() ); /** ��ȡ��� */
//      mCalculator.addBasicFactor("AppAge", mFieldCarrier.getAppAge() );   /** ������Ͷ������ */
//      mCalculator.addBasicFactor("Sex", mFieldCarrier.getSex() );         /** �������Ա� */
//      mCalculator.addBasicFactor("Job", mFieldCarrier.getJob() );         /** �����˹��� */
//
//      mCalculator.addBasicFactor("PayEndYear", mFieldCarrier.getPayEndYear() );/** �ɷ���ֹ���ڻ����� */
//      mCalculator.addBasicFactor("PayEndYearFlag", mFieldCarrier.getPayEndYearFlag() );/** �ɷ���ֹ���ڻ������� */
//      mCalculator.addBasicFactor("GetYear", mFieldCarrier.getGetYear() );/** ��ȡ��ʼ���ڻ����� */
//      mCalculator.addBasicFactor("GetYearFlag", mFieldCarrier.getGetYearFlag() );/** ��ȡ��ʼ���ڻ������� */
//      mCalculator.addBasicFactor("Years", mFieldCarrier.getYears() );  /** �����ڼ� */
//      mCalculator.addBasicFactor("InsuYear", mFieldCarrier.getInsuYear() );/** �����ڼ� */
//      mCalculator.addBasicFactor("InsuYearFlag", mFieldCarrier.getInsuYearFlag() );/** �����ڼ��� */
//
//      mCalculator.addBasicFactor("Count", mFieldCarrier.getCount() );       /** Ͷ������ */
//      mCalculator.addBasicFactor("RnewFlag", mFieldCarrier.getRnewFlag() ); /** �������� */
//      mCalculator.addBasicFactor("AddRate", mFieldCarrier.getAddRate() );   /** ������ */
//      mCalculator.addBasicFactor("GDuty", mFieldCarrier.getGDuty() );      /** ���θ������� */
//      mCalculator.addBasicFactor("PolNo", mFieldCarrier.getPolNo() );      /** ������ */
//      mCalculator.addBasicFactor("FRate", mFieldCarrier.getFloatRate() );  /** �������� */
//      mCalculator.addBasicFactor("GetDutyKind", mFieldCarrier.getGetDutyKind() );/** ������ȡ���� */
//      mCalculator.addBasicFactor("RiskCode",mFieldCarrier.getRiskCode() );  /**���ֱ���*/
//
//      mCalculator.addBasicFactor("Interval",mFieldCarrier.getInterval() );  /**ʱ����*/
//      mCalculator.addBasicFactor("GetMoney",mFieldCarrier.getGetMoney() );  /**���˷ѽ��*/
//      mCalculator.addBasicFactor("EdorNo",mFieldCarrier.getEdorNo() );      /**��ȫ�����*/
//      mCalculator.addBasicFactor("EdorType",mFieldCarrier.getEdorType() );  /**��ȫ����*/
//      mCalculator.addBasicFactor("GrpPolNo",mFieldCarrier.getGrpPolNo() );  /**���屣���� */
//
//      mCalculator.addBasicFactor("Amnt",mFieldCarrier.getAmnt() );          /** ԭ����--���ձ��� */
//
//      mCalculator.addBasicFactor("GetStartDate", "" );                      /**��������*/
//      mCalculator.addBasicFactor("Grp","" );
//      mCalculator.addBasicFactor("GetFlag","" );                            /**��ȡ���*/
//      mCalculator.addBasicFactor("CValiDate","" );                          /**������Ч����*/
//      mCalculator.addBasicFactor("FirstPayDate","" );                       /**���ڽ�������*/
