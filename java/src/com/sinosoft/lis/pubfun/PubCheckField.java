/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.util.Vector;

import com.sinosoft.lis.db.LMCheckFieldDB;
import com.sinosoft.lis.schema.LMCheckFieldSchema;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;


/**
 * <p>Title: У�����</p>
 * <p>Description: ͨ����������ʵ��У�鹦��</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft </p>
 * @author LL
 * @version 1.1
 */
public class PubCheckField
{
    /** ȫ�ֱ������� */
    /** ������ż���SQLʱ�õ������� */
    private TransferData mCalBaseValue = new TransferData();


    /** ������Ų�ѯLMCheckField���õ�������*/
    private LMCheckFieldSchema mCalRelValue = new LMCheckFieldSchema();


    /** �������УҪ�õ���ҵ��У�鼯������*/
    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();


    /** ������ż������з��ֵĴ�����Ϣ���Ѵ˴�����Ϣ���ظ�ǰ̨*/
    private VData mResultMess = new VData();


    /** ������ż������з��ֵĴ�����Ϣ������ǰ̨����ͨ������ĳ������ô�����������*/
    private LMCheckFieldSet mResultMessSet = new LMCheckFieldSet();


    /** ���ǰ̨���������������� */
//    private VData mInputData;


    /** ���ݲ����ַ��� */
    private String mOperate;


    /** �������� */
    public CErrors mErrors = new CErrors();

    public PubCheckField()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������
        this.mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��PubCheckField-->getInputData!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //����ҵ����
        boolean tFlag = dealData();

        return tFlag;
    }


    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        if (this.mOperate.equals("CKBYFIELD"))
        {
            this.mCalBaseValue = (TransferData) cInputData.getObject(0);
            this.mCalRelValue = (LMCheckFieldSchema) cInputData.getObject(1);
            return true;
        }
        else if (this.mOperate.equals("CKBYSET"))
        {
            this.mCalBaseValue = (TransferData) cInputData.getObject(0);
            this.mLMCheckFieldSet = (LMCheckFieldSet) cInputData.getObject(1);
            //�޸�: 2004-11-12 LL
            //�޸�ԭ��:���У�鼯����û�м�¼��ֱ�ӷ���true
//      if (this.mLMCheckFieldSet.size() == 0)
//      {
//        // @@������
//        CError tError = new CError();
//        tError.moduleName = "PubCheckField";
//        tError.functionName = "dealData";
//        tError.errorMessage = "У�鼯����û���κ�У�����ݣ�";
//        this.mErrors.addOneError(tError);
//        System.out.println("У�鼯����û���κ�У�����ݣ�");
//        return false;
//      }
        }
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        //1 - ͨ��CKBYFIELD���У������
        if (this.mOperate.equals("CKBYFIELD"))
        {
            if (!this.getSetByField())
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "PubCheckField";
                tError.functionName = "dealData";
                tError.errorMessage = "���ݴ���ʧ��PubCheckField-->setBaseValue!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        //�޸�: 2004-11-12 LL
        //�޸�ԭ��:���У�鼯����û�м�¼��ֱ�ӷ���true
        if (this.mLMCheckFieldSet.size() == 0)
        {
            System.out.println("У�鼯����û���κ�У�����ݣ�");
            return true;
        }

        //2 - ���ü���ʱҪ�õ��Ĳ�����ֵ
        Calculator tCal = new Calculator();
        if (!setBaseValue(tCal))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "dealData";
            tError.errorMessage = "���ݴ���ʧ��PubCheckField-->setBaseValue!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //3 - ����Ҫ����ָ�꼯�϶�ָ���������У��
        LMCheckFieldSchema tLMCheckFieldSchema;
        String tReturn = "";
        String tResult[];
        boolean tFlag = false;
        //������¼������У��������Ƿ��ִ����־λ
        boolean tLastFlag = true;
        for (int i = 1; i <= this.mLMCheckFieldSet.size(); i++)
        {
            tFlag = false;
            tLMCheckFieldSchema = new LMCheckFieldSchema();
            tLMCheckFieldSchema.setSchema(this.mLMCheckFieldSet.get(i));
            tCal.setCalCode(tLMCheckFieldSchema.getCalCode());
            tReturn = tCal.calculate();
            System.out.println("������Ϊ��" + tReturn);
            if (tCal.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tCal.mErrors);
                CError tError = new CError();
                tError.moduleName = "PubCheckField";
                tError.functionName = "dealData";
                tError.errorMessage = "����ָ��SQL�������!";
                this.mErrors.addOneError(tError);
                System.out.println("Error:" + this.mErrors.getFirstError());
                return false;
            }

            //��ѯLMCheckField�����Ч������ֶ�ValiFlag����ֽ���������ҽ���Ƿ���
            //�������֮��
            System.out.println("�����Ϊ��" + tLMCheckFieldSchema.getValiFlag());
            tResult = PubFun.split(tLMCheckFieldSchema.getValiFlag(), ";");
            //�ж�У�����Ƿ��ڽ������
            for (int j = 0; j < tResult.length; j++)
            {
                if (tReturn != null && tReturn.equals(tResult[j]))
                {
                    tFlag = true; //ָ�꼯���н��
                    break;
                }
            }
            //���ָ�꼯�в����ҵ����
            if (!tFlag)
            {
                tLastFlag = false;
            }
            //�ж�У�����Ƿ���ȷ
            if (!tFlag)
            { //���ؽ�����ڽ������
                //�ж��Ƿ���Ҫ��¼������Ϣ
                if (tLMCheckFieldSchema.getMsgFlag().equals("Y"))
                {
                    String tStr = new String();
                    tStr = tLMCheckFieldSchema.getMsg();
                    //���������Ϣ
                    this.mResultMess.add(tStr);
                    //���������Ϣ��������
                    this.mResultMessSet.add(tLMCheckFieldSchema);
                    System.out.println("������Ϣ��" + tLMCheckFieldSchema.getMsg());
                }

                //�ж��Ƿ���Ҫ��һ����������У��
                //�������Ҫ��һ����������У�飬ֱ�ӷ��ش��󣬲��ٽ�������У��
                if (tLMCheckFieldSchema.getReturnValiFlag().equals("N"))
                {
                    return tLastFlag;
                }
            }

        }

        return tLastFlag;
    }


    /**
     * ���ü���ʱҪ�õ��Ĳ�����ֵ
     * @param tCal Calculator
     * @return boolean
     */
    private boolean setBaseValue(Calculator tCal)
    {
        Vector tName = this.mCalBaseValue.getValueNames();
        for (int i = 0; i < tName.size(); i++)
        {
            tCal.addBasicFactor(tName.get(i).toString(),
                                this.mCalBaseValue.getValueByName(tName.get(i)).
                                toString());
        }

        return true;
    }


    /**
     * ���ش�������м�¼�����Ĳ����������ļ�¼
     * @return VData
     */
    public VData getResultMess()
    {
        return mResultMess;
    }


    /**
     * ���ش�������м�¼�����Ĳ����������ļ�¼����
     * @return LMCheckFieldSet
     */
    public LMCheckFieldSet getResultMessSet()
    {
        return mResultMessSet;
    }

    /**
     * ���ش�������м�¼�����Ĳ����������ļ�¼
     * @return boolean
     */
    private boolean getSetByField()
    {
        //������������ͬʱΪ��
        if (!((this.mCalRelValue.getRiskCode() != null &&
               !this.mCalRelValue.getRiskCode().trim().equals(""))
              ||
              (this.mCalRelValue.getRiskVer() != null &&
               !this.mCalRelValue.getRiskVer().trim().equals(""))
              ||
              (this.mCalRelValue.getFieldName() != null &&
               !this.mCalRelValue.getFieldName().trim().equals(""))
            )
                )
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "getSetByField";
            tError.errorMessage = "��ѯУ�鼯��ʱ����¼����Ӧ������";
            this.mErrors.addOneError(tError);
            System.out.println("��ѯУ�鼯��ʱ����¼����Ӧ������");
            return false;
        }
        //1 - ��LMCheckField���в�ѯҪУ��ļ�¼����
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        String tSql = "select * from LMCheckField where ";
        //����¼������ƴдSQL���
        if (this.mCalRelValue.getRiskCode() != null
            && !this.mCalRelValue.getRiskCode().trim().equals(""))
        {
            tSql = tSql + "RiskCode = '" + this.mCalRelValue.getRiskCode() +
                   "' and ";
        }
        if (this.mCalRelValue.getRiskVer() != null
            && !this.mCalRelValue.getRiskVer().trim().equals(""))
        {
            tSql = tSql + " RiskVer = '" + this.mCalRelValue.getRiskVer() +
                   "' and ";
        }
        if (this.mCalRelValue.getFieldName() != null
            && !this.mCalRelValue.getFieldName().trim().equals(""))
        {
            tSql = tSql + " FieldName = '" + this.mCalRelValue.getFieldName() +
                   "' ";
        }

        tSql += " order by SerialNo asc ";

        System.out.println("��ѯָ�����Ϊ��" + tSql);
        this.mLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldDB.mErrors.needDealError())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "getSetByField";
            tError.errorMessage = "���ݴ���ʧ��PubCheckField-->getSetByField!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
        //��д��������
        //���ô�У����֮ǰ���ʼ����������
        //1 - tTransferData��������ſ��˼���ʱҪ�õ���ֵ
        //2 - tLMCheckFieldSchema�����������������
        //  - ��tLMCheckFieldSet
        PubCheckField checkField1 = new PubCheckField();
        VData cInputData = new VData();
        //���ü���ʱҪ�õ��Ĳ���ֵ
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("IndexCalNo", "200406");
        tTransferData.setNameAndValue("BranchType", "1");
        tTransferData.setNameAndValue("ManageCom", "86110000");
        //ͨ��CKBYFIELD
        LMCheckFieldSchema tLMCheckFieldSchema = new LMCheckFieldSchema();
        tLMCheckFieldSchema.setRiskCode("000000");
        tLMCheckFieldSchema.setRiskVer("2004");
        tLMCheckFieldSchema.setFieldName("WageCalCheck");

        //ͨ�� CKBYFIELD ��ʽУ��
        cInputData.add(tTransferData);
        cInputData.add(tLMCheckFieldSchema);

        //ͨ�� CKBYSET ��ʽУ��
//    LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
//    LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
//    String tSql = "select * from lmcheckfield where riskcode = '000000'"
//                  +" and riskver = '2004' and fieldname = 'WageCalCheck' order by serialno asc";
//    tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
//
//    cInputData.add(tTransferData);
//    cInputData.add(tLMCheckFieldSet);

        if (!checkField1.submitData(cInputData, "CKBYFIELD"))
        {
            System.out.println("Enter Error Field!");
            //���ж������������ǳ���������б��Ĵ��󣬻���У��ʱ���Ĵ���
            if (checkField1.mErrors.needDealError())
            {
                System.out.println("ERROR-S-" +
                                   checkField1.mErrors.getFirstError());
            }
            else
            {
                VData t = checkField1.getResultMess();
                LMCheckFieldSet ntLMCheckFieldSet = checkField1.
                        getResultMessSet();
                for (int i = 0; i < t.size(); i++)
                {
                    System.out.println("ERROR-C-" + i + ":" + t.get(i).toString());
                }
            }
        }
        else
        {
            System.out.println("Congratulattion!");
        }
    }
}
