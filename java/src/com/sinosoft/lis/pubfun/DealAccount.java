/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.LCInsureAccTraceDBSet;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import java.util.Date;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author hzm
 * @version 1.0
 */
public class DealAccount
{
    /**
     * ��Ա����
     */
    public CErrors mErrors = new CErrors(); //������
    private String CurrentDate = PubFun.getCurrentDate(); //ϵͳ��ǰʱ��
    private String CurrentTime = PubFun.getCurrentTime();
    private String tLimit = ""; //��ˮ��
    private String serNo = "";
    private String mEnterAccDate = CurrentDate; //�ʽ�������

    private GlobalInput mGlobalInput = null;
    public DealAccount()
    {
    }

    public DealAccount(GlobalInput inGlobalInput)
    {
        this.mGlobalInput = inGlobalInput;
    }


    /**
     * ���ɱ����ʻ�(���ɽṹ:���������˻���,�����������Ϳͻ��˻���Ĺ�����,�����������Ϳͻ��˻���Ĺ�����)
     * @param parmData (Type:TransferData include: PolNo��AccCreatePos��OtherNo��OtherNoType��Rate)
     * @return VData (include: LCInsureAccSet��LCPremToAccSet��LCGetToAccSet)
     */
    public VData createInsureAcc(TransferData parmData)
    {
        //1-����
        if (!checkTransferData(parmData))
        {
            return null;
        }

        //2-�õ����ݺ���
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("����:" + tRate);

        //3-���������˻���
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAcc(tPolNo, tAccCreatePos, tOtherNo,
                                         tOtherNoType);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-�����������Ϳͻ��˻���Ĺ�����
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAcc(tPolNo, tAccCreatePos, tRate);

        //if(tLCPremToAccSet==null) return null;
        //5-�����������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAcc(tPolNo, tAccCreatePos, tRate);

        //if(tLCGetToAccSet==null) return null;
        //6-��������
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //������null
        tVData.add(tLCGetToAccSet); //������null

        return tVData;
    }


    /**
     * �Ը��˱������ɱ����ʻ���(���� 1�����ʻ�,����Ҫ����������¼)
     * @param PolNo  ������
     * @param AccCreatePos ����λ�� :1-Ͷ����¼��ʱ���� 2���ɷ�ʱ���� 3����ȡʱ����
     * @param OtherNo �����Ż򽻷Ѻ�
     * @param OtherNoType  �����Ż򽻷Ѻ�
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAcc(String PolNo, String AccCreatePos,
                                         String OtherNo, String OtherNoType)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���

        //1-��ѯ������
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        //2-����Ͷ�������е������ֶβ�ѯLMRisk��
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCPolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-�ж��Ƿ����ʻ����
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //�������ֲ�ѯLMRiskToAcc��(�����˻�������)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //�����˻�������
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //���ֱ����ʻ�
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //�����ʻ���

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //���ݱ����˻������ѯLMRiskInsuAcc��(���ֱ����ʻ�)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema
                        .getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    //return con;
                    continue;
                }

//              LMRiskInsuAccSet tLMRiskInsuAccSet = queryLMRiskInsuAccSet(tLMRiskToAccSchema.getInsuAccNo());
//              if (tLMRiskInsuAccSet == null)
//              {
//                  CError.buildErr(this,"�˻�������ѯʧ��");
//                  return null;
//              }
//                  for ( int u=1;u<=tLMRiskInsuAccSet.size();u++)
//                  {
//                      tLMRiskInsuAccSchema = tLMRiskInsuAccSet.get(u);
                //����ʻ������Ǽ����ʻ�,�˳�
                if (tLMRiskInsuAccSchema.getAccType().equals("001"))
                {
                    //�������������-2 --���ŵ��������ʻ�(���磺�������ļ����ʻ����ӽ���¼�����ʱѡ�񱣵�����Ϊ2)
                    //��ʱ���������ĸ��˳������ɸ��˵��˻��⣬�����ɼ�����˻�
                    if ((tLCPolSchema.getPolTypeFlag() != null)
                        && tLCPolSchema.getPolTypeFlag().equals("2"))
                    {
                        System.out.println("��Ҫ���ɼ����ʻ�");
                    }
                    else
                    {
                        continue;
                    }
                }

                //���ɱ����˻���
                //����˻�����λ���ҵ�ƥ��ı����˻�
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    //  tLCInsureAccSchema.setOtherNo(OtherNo);
                    //  tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(tLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
//                    tLCInsureAccSchema.setAppntName(tLCPolSchema.
//                            getAppntName());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(tLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(tLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(tLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);

                    //������
                    tLCInsureAccSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setPrtNo(tLCPolSchema.getPrtNo());
                    tLCInsureAccSchema.setAppntNo(tLCPolSchema.getAppntNo());

                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
                //                 }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * �Լ��屣�����ɱ����ʻ���(���� 1�����ʻ�,����Ҫ����������¼)
     * @param GrpPolNo String ���屣����
     * @param AccCreatePos String ����λ�� :1-Ͷ����¼��ʱ���� 2���ɷ�ʱ���� 3����ȡʱ����
     * @param OtherNo String ���屣���Ż򽻷Ѻ�
     * @param OtherNoType String ���屣���Ż򽻷Ѻ�
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForGrp(String GrpPolNo,
                                               String AccCreatePos,
                                               String OtherNo
                                               , String OtherNoType)
    {
        if ((GrpPolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���

        //1-��ѯ������
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpPolNo(GrpPolNo);
        if (!tLCGrpPolDB.getInfo())
        {
            return null;
        }

        LCGrpPolSchema tLCGrppolSchema = tLCGrpPolDB.getSchema();

        //2-����Ͷ�������е������ֶβ�ѯLMRisk��
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCGrppolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-�ж��Ƿ����ʻ����
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //�������ֲ�ѯLMRiskToAcc��(�����˻�������)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCGrppolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //�����˻�������
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //���ֱ����ʻ�
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //�����ʻ���

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //���ݱ����˻������ѯLMRiskInsuAcc��(���ֱ����ʻ�)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //����ʻ����Ͳ��Ǽ����ʻ�,�˳�
                if (!tLMRiskInsuAccSchema.getAccType().equals("001"))
                {
                    continue;
                }

                //���ɱ����˻���
                //����˻�����λ���ҵ�ƥ��ı����˻�
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(tLCGrppolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    /*Lis5.3 upgrade get
                     tLCInsureAccSchema.setContNo(tLCGrppolSchema.getContNo());
                     */
                    tLCInsureAccSchema.setGrpPolNo(tLCGrppolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo("0"); //��Ϊ���ü������Ϣ��û�б����˵Ŀͻ��ţ�������0
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(tLCGrppolSchema.
                            getManageCom());
                    tLCInsureAccSchema.setOperator(tLCGrppolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(tLCGrppolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * ���ɱ����ʻ���(���� 2:�������ʻ���ע���ʽ�϶�Ϊһ,��Ҫ����������¼,ע�⣺�Ѿ�����Ҫע����ʽ� )
     * @param PolNo  ������
     * @param AccCreatePos ����λ�� :1-Ͷ����¼��ʱ���� 2���ɷ�ʱ���� 3����ȡʱ����
     * @param OtherNo �����Ż򽻷Ѻ�
     * @param OtherNoType �����Ż򽻷Ѻ�
     * @param ManageCom ��½����
     * @param AccType �˺�����: 001-���幫���˻� 002-���˽ɷ��˻� 003-���˴����˻� 004-���˺����˻�
     * @param MoneyType �������:BF������ GL������� HL������ LX���ۻ���Ϣ����Ϣ
     * @param Money �����ʻ��Ľ��
     * @return VData(LCInsureAccSet,LCInsureAccTraceSet)
     */
    public VData getLCInsureAcc(String PolNo, String AccCreatePos,
                                String OtherNo
                                , String OtherNoType, String ManageCom,
                                String AccType, String MoneyType, double Money)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (ManageCom == null)
            || (AccType == null) || (MoneyType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //1-��ѯ������
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        //2-����Ͷ�������е������ֶβ�ѯLMRisk��
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCPolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-�ж��Ƿ����ʻ����
        VData tVData = new VData();
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //�������ֲ�ѯLMRiskToAcc��(�����˻�������)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //�����˻�������
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //���ֱ����ʻ�
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //�����ʻ���
            LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���
            LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
            LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                    LCInsureAccTraceSchema();
            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //���ݱ����˻������ѯLMRiskInsuAcc��(���ֱ����ʻ�)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo()
                        , AccType);
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //���ɱ����˻���
                //����˻�����λ���ҵ�ƥ��ı����˻�
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    //  tLCInsureAccSchema.setOtherNo(OtherNo);
                    //  tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(tLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
//                    tLCInsureAccSchema.setAppntName(tLCPolSchema.getAppntName());
                    tLCInsureAccSchema.setSumPay(Money);
                    tLCInsureAccSchema.setInsuAccBala(Money);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(ManageCom);
                    tLCInsureAccSchema.setOperator(tLCPolSchema.getOperator());
//                    tLCInsureAccSchema.setBalaDate(tLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setBalaDate(CurrentDate);
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);

                    //��䱣���ʻ���Ǽ�������
                    tLimit = PubFun.getNoLimit(ManageCom);
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(Money);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    /*Lis5.3 upgrade set
                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                                         .getAppntName());
                     */
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);

                    //�������
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }
            }
            if ((tLCInsureAccSet.size() == 0)
                || (tLCInsureAccTraceSet.size() == 0))
            {
                // @@������
                //        CError tError =new CError();
                //        tError.moduleName="DealAccount";
                //        tError.functionName="addPrem";
                //        tError.errorMessage="���������ϣ�û�����ɼ�¼";
                //        this.mErrors .addOneError(tError) ;
                return null;
            }
            tVData.add(tLCInsureAccSet);
            tVData.add(tLCInsureAccTraceSet);

            return tVData;
        }
        else
        {
            //      CError tError = new CError();
            //      tError.moduleName = "DealAccount";
            //      tError.functionName = "getLCInsureAcc";
            //      tError.errorMessage = "���ֶ����¼�б����ʻ����ΪN!";
            //      this.mErrors.addOneError(tError);
            return null;
        }
    }


    /**
     * ���ɱ������Ϳͻ��ʻ���Ĺ�����
     * @param PolNo ������
     * @param AccCreatePos ����λ��
     * @param Rate ����
     * @return LCPremToAccSet �����������
     */
    public LCPremToAccSet getPremToAcc(String PolNo, String AccCreatePos,
                                       Double Rate)
    {
        if ((PolNo == null) || (AccCreatePos == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getPremToAcc";
            tError.errorMessage = "����ԭ��:�����������Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        String tPolNo = PolNo;
        String tAccCreatePos = AccCreatePos;
        Double tRate = Rate;
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();

        //1-ȡ���������
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = queryLCPrem(tPolNo);
        if (tLCPremSet == null)
        {
            return null;
        }

        //2-���ݱ������ȡ����Ӧ�����νɷ�������
        VData tVData = new VData();
        tVData = getFromLMDutyPay(tLCPremSet);
        if (tVData == null)
        {
            return null;
        }

        //3-���ɱ������Ϳͻ��˻���Ĺ�����
        tLCPremToAccSet = createPremToAcc(tVData, tPolNo, tAccCreatePos, tRate);

        return tLCPremToAccSet;
    }


    /**
     * �����˻��ʽ�ע��(����1 ��Ա�����,ע��û�и���ע���ʽ��ڲ�����ü�����ĺ���)
     * @param pLCPremSchema ������
     * @param AccCreatePos  �μ� ���ֱ����ʻ��ɷ� LMRiskAccPay
     * @param OtherNo  �μ� �����ʻ��� LCInsureAcc
     * @param OtherNoType  ��������
     * @param MoneyType  �μ� �����ʻ���Ǽ������� LCInsureAccTrace
     * @param Rate ����
     * @return VData(tLCInsureAccSet:update or insert ,tLCInsureAccTraceSet: insert)
     */
    public VData addPrem(LCPremSchema pLCPremSchema, String AccCreatePos,
                         String OtherNo
                         , String OtherNoType, String MoneyType, Double Rate)
    {
        if ((pLCPremSchema == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //�������Ϳͻ��ʻ���Ĺ�����
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        String newFlag = "";
//        boolean addPrem = false;
        double inputMoney = 0;

        //�ж��Ƿ��ʻ����
        if (pLCPremSchema.getNeedAcc().equals("1"))
        {
            tLCPremToAccSet = queryLCPremToAccSet(pLCPremSchema);
            if (tLCPremToAccSet == null)
            {
                return null;
            }

            TransferData tFData = new TransferData();
            LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

            //�ж�����λ���Ƿ�ƥ��
            if (AccCreatePos.equals(tLCPremToAccSet.get(1).getNewFlag()))
            {
                //���ƥ�䣺�����ʻ�(������ÿ�ν��Ѷ��������˺ŵ�������ο�LCInsureAcc-�����ʻ���)
                tFData = new TransferData();
                tFData.setNameAndValue("PolNo", pLCPremSchema.getPolNo());
                tFData.setNameAndValue("OtherNo", OtherNo); //����ÿ�ν��Ѷ��������˺ŵ���������ֶδ�Ž��Ѻš�����
                tFData.setNameAndValue("OtherNoType", OtherNoType);
                tFData.setNameAndValue("Rate", Rate);
                tLCInsureAccSet = new LCInsureAccSet();
                mLCInsureAccSet = getLCInsureAcc(pLCPremSchema.getPolNo(),
                                                 AccCreatePos, OtherNo
                                                 , OtherNoType);
                if (mLCInsureAccSet == null)
                {
                    return null;
                }
                newFlag = "INSERT";
            }

            for (int i = 1; i <= tLCPremToAccSet.size(); i++)
            {
                tLCPremToAccSchema = new LCPremToAccSchema();
                tLCPremToAccSchema = tLCPremToAccSet.get(i);

                //����ʵ��Ӧ��ע����ʽ�
                inputMoney = calInputMoney(tLCPremToAccSchema,
                                           pLCPremSchema.getPrem());
                if (inputMoney == -1)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "DealAccount";
                    tError.functionName = "addPrem";
                    tError.errorMessage = "����ʵ��Ӧ��ע����ʽ����";
                    this.mErrors.addOneError(tError);

                    return null;
                }
                if (newFlag.equals("INSERT"))
                {
                    //������������ʻ�
                    //���ݱ����źͱ����˻��ź����������ѯmLCInsureAccSet������Ψһһ������
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAccSet(pLCPremSchema.
                            getPolNo()
                            , tLCPremToAccSchema.getInsuAccNo(), OtherNo,
                            mLCInsureAccSet);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }
                else
                {
                    //���ݱ����źͱ����˻��ź����������ѯLCInsureAcc���Ψһһ������
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAcc(pLCPremSchema.
                            getPolNo()
                            , tLCPremToAccSchema.getInsuAccNo(), OtherNo);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }

                //�޸ı����ʻ����
                tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema.
                                                  getInsuAccBala() + inputMoney);
                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay() +
                                             inputMoney);
                tLCInsureAccSchema.setModifyDate(CurrentDate);
                tLCInsureAccSchema.setModifyTime(CurrentTime);

                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                tLMRiskAccPaySchema = queryLMRiskAccPay2(tLCPremToAccSchema); //��ѯ���ֱ����ʻ��ɷ�
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }
                if (tLMRiskAccPaySchema.getPayNeedToAcc().equals("1") &&
                    (inputMoney != 0))
                {
                    //��䱣���ʻ���Ǽ�������
                    tLimit = PubFun.getNoLimit(pLCPremSchema.getManageCom());
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }

                //�������
                tLCInsureAccSet.add(tLCInsureAccSchema);
            }
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@������
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="���������ϣ�û�����ɼ�¼";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //����ڲ���VDataʱ��������tLCInsureAccSet������update or insert��
        //��˲������ݿ�ʱ��ִ��ɾ����������ִ�в������
    }


    /**
     * �÷�����ȱ�ݣ���������ǩ��������Ϊ����������еĵõ����Ǳ����ţ����ǿ��е�������
     * ��δǩ�������ݣ�ֻ��Ͷ�����š�
     * �����˻��ʽ�ע��(����3 ��Ա�����,ע��û�и���ע���ʽ��ڲ�����ü�����ĺ���)
     * �����ڣ��������ʻ��ṹ�󣬴�ʱ������δ�ύ�����ݿ⣬����Ҫִ���ʻ����ʽ�ע�롣
     * ����ʹ���� createInsureAcc()�����󣬵õ�VData���ݣ������޸�VData���ʻ��Ľ��
     * @param inVData VData ʹ���� createInsureAcc()�����󣬵õ���VData����
     * @param pLCPremSet LCPremSet �������
     * @param AccCreatePos String �μ� ���ֱ����ʻ��ɷ� LMRiskAccPay
     * @param OtherNo String �μ� �����ʻ��� LCInsureAcc
     * @param OtherNoType String ��������
     * @param MoneyType String �μ� �����ʻ���Ǽ������� LCInsureAccTrace
     * @param RiskCode String ������Ϣ
     * @param Rate String ����
     * @return VData
     */
    public VData addPremInner(VData inVData, LCPremSet pLCPremSet,
                              String AccCreatePos
                              , String OtherNo, String OtherNoType,
                              String MoneyType, String RiskCode, String Rate)
    {
        if ((inVData == null) || (pLCPremSet == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null) || (RiskCode == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

//        VData tVData = new VData();

        //�õ����ɵı����ʻ���
        LCInsureAccSet tLCInsureAccSet = (LCInsureAccSet) (inVData.
                getObjectByObjectName(
                "LCInsureAccSet", 0));

        //�õ����ɵĽɷ��ʻ�������
        LCPremToAccSet tLCPremToAccSet = (LCPremToAccSet) (inVData.
                getObjectByObjectName(
                "LCPremToAccSet", 0));

        LCInsureAccClassSet tInsureAccClassSet = (LCInsureAccClassSet) inVData.
                                                 getObjectByObjectName(
                "LCInsureAccClassSet", 0);

        //�õ���ȡ�ʻ�������--Ŀǰ����
        LCGetToAccSet tLCGetToAccSet = (LCGetToAccSet) (inVData.
                getObjectByObjectName(
                "LCGetToAccSet", 0));

        if (tLCInsureAccSet == null)
        {
            tLCInsureAccSet = new LCInsureAccSet();
        }
        if (tLCPremToAccSet == null)
        {
            tLCPremToAccSet = new LCPremToAccSet();
        }
        if (tLCGetToAccSet == null)
        {
            tLCGetToAccSet = new LCGetToAccSet();
        }
        if (tInsureAccClassSet == null)
        {
            tInsureAccClassSet = new LCInsureAccClassSet();
        }

        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCInsureAccClassSchema tClassSchema = null;
        double inputMoney = 0;
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            LCPremSchema tLCPremSchema = pLCPremSet.get(n);

            //�ж��Ƿ��ʻ����
            if (tLCPremSchema.getNeedAcc().equals("1"))
            {
                for (int m = 1; m <= tLCPremToAccSet.size(); m++)
                {
                    LCPremToAccSchema tLCPremToAccSchema = tLCPremToAccSet.get(
                            m);

                    //�����ǰ������͵�ǰ�Ľɷ��ʻ�������ı����ţ����α��룬���Ѽƻ�������ͬ
                    if (tLCPremSchema.getPolNo().equals(tLCPremToAccSchema.
                            getPolNo())
                        &&
                        tLCPremSchema.getDutyCode().equals(tLCPremToAccSchema.
                            getDutyCode())
                        &&
                        tLCPremSchema.getPayPlanCode().equals(
                            tLCPremToAccSchema.getPayPlanCode()))
                    {

//               if ( tLCPremToAccSchema.getCalFlag()==null
//                   || "0".equals( tLCPremToAccSchema.getCalFlag() ))
//              {
                        //������Ҫע����ʽ�
                        inputMoney = calInputMoney(tLCPremToAccSchema,
                                tLCPremSchema.getPrem());

                        if (inputMoney == -1)
                        {
                            // @@������
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "addPrem";
                            tError.errorMessage = "����ʵ��Ӧ��ע����ʽ����";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
//              }else
//              {
//                  //��������
//                  VData feeData = new VData();
//              CManageFee cManageFee = new CManageFee();
//              LCPremSet tLCPremSet = new LCPremSet();
//
//              feeData.add(tLCPremSet);
//              cManageFee.Initialize( feeData );
//              cManageFee.computeManaFee();
//              if ( cManageFee.mErrors.needDealError())
//              {
//                  this.mErrors.copyAllErrors( cManageFee.mErrors);
//                  return false;
//              }
//              VData tResult = cManageFee.getResult();
//              LCInsureAccFeeSet tmpLCInsureAccFeeSet =(LCInsureAccFeeSet) tResult.getObjectByObjectName("LCInsureAccFeeSet",0);
//              LCInsureAccClassFeeSet tmpLCInsureAccClassFeeSet=(LCInsureAccClassFeeSet)tResult.getObjectByObjectName("LCInsureAccClassFeeSet",0);
//              manFeeMap.put(tmpLCInsureAccFeeSet,this.INSERT );
//              manFeeMap.put(tmpLCInsureAccClassFeeSet, this.INSERT);
//
//
//              }

                        //�ۼ��˻�����ss
                        for (int t = 1; t <= tInsureAccClassSet.size(); t++)
                        {
                            tClassSchema = tInsureAccClassSet.get(t);
                            if (tClassSchema.getInsuAccNo().equals(
                                    tLCPremToAccSchema.getInsuAccNo())
                                &&
                                tClassSchema.getPayPlanCode().equals(
                                    tLCPremToAccSchema.
                                    getPayPlanCode())
                                &&
                                tClassSchema.getPolNo().equals(
                                    tLCPremToAccSchema.getPolNo()))
                            {

                                tClassSchema.setSumPay(tClassSchema.getSumPay() +
                                        inputMoney);
                                tClassSchema.setInsuAccBala(tClassSchema.
                                        getInsuAccBala()
                                        + inputMoney);
                                break;
                            }

                        }
                        for (int j = 1; j <= tLCInsureAccSet.size(); j++)
                        {
                            //�����ǰ�ɷ��ʻ�������ı����ţ��˻��ź͵�ǰ���˻���ı����ţ��˻�����ͬ�����ʽ�Ϊ0�����ʽ�ע��
                            LCInsureAccSchema tLCInsureAccSchema =
                                    tLCInsureAccSet.get(j);
                            if (tLCPremToAccSchema.getPolNo().equals(
                                    tLCInsureAccSchema.getPolNo())
                                &&
                                tLCPremToAccSchema.getInsuAccNo().equals(
                                    tLCInsureAccSchema.
                                    getInsuAccNo())
                                && (inputMoney != 0))
                            {
                                //�޸ı����ʻ����
                                tLCInsureAccSchema.setInsuAccBala(
                                        tLCInsureAccSchema.getInsuAccBala()
                                        + inputMoney);
                                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.
                                        getSumPay()
                                        + inputMoney);

                                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                                tLCInsureAccSet.set(j, tLCInsureAccSchema);

                                //��ѯ���ֱ����ʻ��ɷ�
                                LMRiskAccPaySchema tLMRiskAccPaySchema =
                                        queryLMRiskAccPay3(
                                        RiskCode, tLCPremToAccSchema);
                                if (tLMRiskAccPaySchema == null)
                                {
                                    return null;
                                }
                                if (tLMRiskAccPaySchema.getPayNeedToAcc().
                                    equals("1"))
                                {
                                    //��䱣���ʻ���Ǽ�������
                                    tLimit = PubFun.getNoLimit(tLCPremSchema.
                                            getManageCom());
                                    serNo = PubFun1.CreateMaxNo("SERIALNO",
                                            tLimit);
                                    tLCInsureAccTraceSchema = new
                                            LCInsureAccTraceSchema();
                                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                                    tLCInsureAccTraceSchema.setInsuredNo(
//                                            tLCInsureAccSchema
//                                            .getInsuredNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setMoneyType(
                                            MoneyType);
                                    tLCInsureAccTraceSchema.setRiskCode(
                                            tLCInsureAccSchema.
                                            getRiskCode());
                                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                                    tLCInsureAccTraceSchema.setOtherType(
                                            OtherNoType);
                                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                                    tLCInsureAccTraceSchema.setContNo(
                                            tLCInsureAccSchema.getContNo());
                                    tLCInsureAccTraceSchema.setGrpPolNo(
                                            tLCInsureAccSchema.
                                            getGrpPolNo());
                                    tLCInsureAccTraceSchema.setInsuAccNo(
                                            tLCInsureAccSchema.
                                            getInsuAccNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setGrpContNo(
                                            tLCInsureAccSchema.
                                            getGrpContNo());
                                    tLCInsureAccTraceSchema.setState(
                                            tLCInsureAccSchema.getState());
                                    tLCInsureAccTraceSchema.setManageCom(
                                            tLCInsureAccSchema.
                                            getManageCom());
                                    tLCInsureAccTraceSchema.setOperator(
                                            tLCInsureAccSchema.
                                            getOperator());
                                    tLCInsureAccTraceSchema.setMakeDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setMakeTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setModifyDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setModifyTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setPayDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setGrpContNo(
                                            tLCInsureAccSchema.
                                            getGrpContNo());

                                    tLCInsureAccTraceSet.add(
                                            tLCInsureAccTraceSchema);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        inVData.clear();
        inVData.add(tLCInsureAccSet);
        inVData.add(tLCPremToAccSet);
        inVData.add(tLCGetToAccSet);

        //����ʻ�ע���ʽ�켣
        inVData.add(tLCInsureAccTraceSet);

        //�������ݿ�ʱִ�в������
        return inVData; //(LCInsureAccSet,LCPremToAccSet,LCGetToAccSet,LCInsureAccTraceSet)
    }


    /**
     * �����˻��ʽ�ע��(����2 ͨ��)
     * @param PolNo    ������
     * @param InsuAccNo    �ʻ���
     * @param OtherNo  ��Ž��ѺŻ򱣵���
     * @param OtherNoType ��Ž��ѺŻ򱣵���
     * @param MoneyType �������:BF������ GL������� HL������ LX���ۻ���Ϣ����Ϣ
     * @param ManageCom �������
     * @param money     ע���ʽ�
     * @return VData(tLCInsureAccSet:update ,tLCInsureAccTraceSet: insert)
     */
    public VData addPrem(String PolNo, String InsuAccNo, String OtherNo,
                         String OtherNoType
                         , String MoneyType, String ManageCom, double money)
    {
        if ((PolNo == null) || (InsuAccNo == null) || (OtherNo == null)
            || (OtherNoType == null) || (MoneyType == null)
            || (ManageCom == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();

        //���ݱ����źͱ����˻��ź����������ѯLCInsureAcc���Ψһһ������
        tLCInsureAccSchema = new LCInsureAccSchema();
        tLCInsureAccSchema = queryLCInsureAcc(PolNo, InsuAccNo, OtherNo);
        if (tLCInsureAccSchema == null)
        {
            return null;
        }

        //��䱣���ʻ���Ǽ�������
        tLimit = PubFun.getNoLimit(ManageCom);
        tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
        if (money != 0)
        { //���ע���ʽ�=0������ӹ켣
            serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
            tLCInsureAccTraceSchema.setSerialNo(serNo);
//            tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                                                 .getInsuredNo());
            tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.getPolNo());
            tLCInsureAccTraceSchema.setMoneyType(MoneyType);
            tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.getRiskCode());
            tLCInsureAccTraceSchema.setOtherNo(OtherNo);
            tLCInsureAccTraceSchema.setOtherType(OtherNoType);
            tLCInsureAccTraceSchema.setMoney(money);
            tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.getContNo());
            tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.getGrpPolNo());
            tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                                                 getInsuAccNo());
            tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.getState());
            tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                                                 getManageCom());
            tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.getOperator());
            tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
            tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
            tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
            tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
            tLCInsureAccTraceSchema.setPayDate(CurrentDate);
        }

        //�޸ı����ʻ����
        tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema.getInsuAccBala() +
                                          money);
        tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay() + money);

        //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+money);
        //�������
        tLCInsureAccSet.add(tLCInsureAccSchema);
        tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
        if (tLCInsureAccSet.size() == 0)
        {
            //      // @@������
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="���������ϣ�û�����ɼ�¼";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //�������ݿ�ʱ��ֻ��Ҫ����tLCInsureAccSet������tLCInsureAccTraceSet
    }


    //--------�������ĸ���Ҫ�ĺ����е��õ���ظ�������---------------

    /**
     * ���鴫�������Ƿ�����
     * @param parmData ��������
     * @return boolean
     */
    public boolean checkTransferData(TransferData parmData)
    {
        if (parmData == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "checkTransferData";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return false;
        }
        try
        {
            String tPolNo = (String) parmData.getValueByName("PolNo");
            String tAccCreatePos = (String) parmData.getValueByName(
                    "AccCreatePos");
            String tOtherNo = (String) parmData.getValueByName("OtherNo");
            String tOtherNoType = (String) parmData.getValueByName(
                    "OtherNoType");

            //Double tRate=(Double)parmData.getValueByName("Rate"); //��У����ʣ�����Ϊ��
            String FieldName = "";
            boolean errFlag = false;
            if (tPolNo == null)
            {
                FieldName = "PolNo";
                errFlag = true;
            }
            else if (tAccCreatePos == null)
            {
                FieldName = "AccCreatePos";
                errFlag = true;
            }
            else if (tOtherNo == null)
            {
                FieldName = "OtherNo";
                errFlag = true;
            }
            else if (tOtherNoType == null)
            {
                FieldName = "OtherNoType";
                errFlag = true;
            }
            if (errFlag)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "checkTransferData";
                tError.errorMessage = "û�н��ܵ��ֶ���Ϊ'" + FieldName + "'������";
                this.mErrors.addOneError(tError);

                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "checkTransferData";
            tError.errorMessage = "����ԭ��:������������Ͳ�ƥ��";
            this.mErrors.addOneError(tError);

            return false;
        }

        return true;
    }


    /**
     * ���ݱ����Ų�ѯ�������
     * @param cPolNo ������
     * @return LCPremSet or null
     */
    public LCPremSet queryLCPrem(String cPolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCPrem where PolNo='");
        tSBql.append(cPolNo);
        tSBql.append("' and needacc='1'");

        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        LCPremDB tLCPremDB = tLCPremSchema.getDB();
        tLCPremSet = tLCPremDB.executeQuery(tSBql.toString());

        if (tLCPremDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCPremDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPrem";
            tError.errorMessage = "��������ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLCPremSet.clear();

            return null;
        }
        if (tLCPremSet.size() == 0)
        {
            return null;
        }

        return tLCPremSet;
    }


    /**
     * ͨ������ı������¼��ѯ�õ����ν��Ѽ�¼����
     * @param pLCPremSet ����ı������¼
     * @return VData
     */
    public VData getFromLMDutyPay(LCPremSet pLCPremSet)
    {
        if (pLCPremSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getFromLMDutyPay";
            tError.errorMessage = "�����������Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //1 ѭ���жϽ��Ѽƻ������ǰ6λȫ0��2 �жϽ��Ѽƻ������ǰ6λ�Ƿ����ظ�ֵ��
        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        String[] payPlanCode = new String[pLCPremSet.size()];
        int i = 0;
        String strCode = "";
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            tLCPremSchema = pLCPremSet.get(n);
            //����������Ŀ������ݿ��е�char����ȫ���ĳ�carchar2����
            //�漰����DB�ж�StrTool.space��������
            //���԰�StrTool.space�Ĺ��ܸı��ˣ����������ﲻ���ٵ����������
            //���ĳɵ���String����ĺ�������huanglei
            //strCode = StrTool.space(tLCPremSchema.getPayPlanCode(), 6);
            strCode = tLCPremSchema.getPayPlanCode().substring(0,6);
            if (i == 0)
            {
                if (!strCode.equals("000000"))
                {
                    payPlanCode[i] = strCode;
                    i++;
                    tLMDutyPaySchema = new LMDutyPaySchema();
                    tLMDutyPaySchema = queryLMDutyPay(strCode);
                    if (tLMDutyPaySchema == null)
                    {
                        // @@������
                        CError tError = new CError();
                        tError.moduleName = "DealAccount";
                        tError.functionName = "getFromLMDutyPay";
                        tError.errorMessage = "û���ҵ��ɷѱ���=" + strCode + "�����ν��Ѽ�¼";
                        this.mErrors.addOneError(tError);

                        return null;
                    }
                    tLMDutyPaySet.add(tLMDutyPaySchema);
                    tLCPremSet.add(tLCPremSchema);
                }
            }
            else
            {
                boolean saveFlag = true;
                if (!strCode.equals("000000"))
                {
                    for (int m = 0; m < i; m++)
                    {
                        if (strCode.equals(payPlanCode[m]))
                        {
                            saveFlag = false;

                            break;
                        }
                    }
                    if (saveFlag)
                    {
                        payPlanCode[i] = strCode;
                        i++;
                        tLMDutyPaySchema = new LMDutyPaySchema();
                        tLMDutyPaySchema = queryLMDutyPay(strCode);
                        if (tLMDutyPaySchema == null)
                        {
                            // @@������
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "getFromLMDutyPay";
                            tError.errorMessage = "û���ҵ��ɷѱ���=" + strCode
                                                  + "�����ν��Ѽ�¼";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        tLMDutyPaySet.add(tLMDutyPaySchema);
                        tLCPremSet.add(tLCPremSchema);
                    }
                }
            }
        }
        if ((tLMDutyPaySet.size() == 0) || (tLCPremSet.size() == 0))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getFromLMDutyPay";
            tError.errorMessage = "û���ҵ����ν��Ѽ�¼";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        tVData.add(tLMDutyPaySet);
        tVData.add(tLCPremSet);

        return tVData;
    }


    /**
     * �������ν��ѱ����ѯ���ν��ѱ�
     * @param payPlanCode �ӱ�������ѯ���Ľ��ѱ��루��ȡǰ6λ��
     * @return LMDutyPaySchema or null
     */
    private LMDutyPaySchema queryLMDutyPay(String payPlanCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMDutyPay where payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        LMDutyPayDB tLMDutyPayDB = tLMDutyPaySchema.getDB();
        tLMDutyPaySet = tLMDutyPayDB.executeQuery(tSBql.toString());

        if (tLMDutyPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMDutyPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyPay";
            tError.errorMessage = "���ν��ѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMDutyPaySet.clear();

            return null;
        }
        if (tLMDutyPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMDutyPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyPay";
            tError.errorMessage = "���ν��ѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMDutyPaySet.clear();

            return null;
        }

        return tLMDutyPaySet.get(1);
    }


    /**
     * ���ɱ������Ϳͻ��˻���Ĺ�����
     * @param tVData        �������ν��Ѻͱ������
     * @param PolNo ������
     * @param AccCreatePos  �����ʻ�������λ�ñ�ǣ��б������ѵȣ�
     * @param Rate          ��ȡ����
     * @return LCPremToAccSet
     */
    public LCPremToAccSet createPremToAcc(VData tVData, String PolNo,
                                          String AccCreatePos, Double Rate)
    {
        if ((tVData == null) || (PolNo == null) || (AccCreatePos == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createPremToAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCPremSet tLCPremSet = new LCPremSet();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        tLCPremSet = (LCPremSet) tVData.getObjectByObjectName("LCPremSet", 0);
        tLMDutyPaySet = (LMDutyPaySet) tVData.getObjectByObjectName(
                "LMDutyPaySet", 0);

        LCPremSchema tLCPremSchema = new LCPremSchema(); //�������
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema(); //���ν��ѱ�
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema(); //
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //�������Ϳͻ��ʻ���Ĺ�����
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema(); //�������Ϳͻ��ʻ���Ĺ�����

        double tRate = 0;
        for (int i = 1; i <= tLMDutyPaySet.size(); i++)
        {
            tLMDutyPaySchema = tLMDutyPaySet.get(i);
            tLCPremSchema = tLCPremSet.get(i);

            //�ж��Ƿ���ʻ�����
            if (tLMDutyPaySchema.getNeedAcc().equals("1"))
            {
                //��ѯ���ֱ����ʻ��ɷѱ�
                tLMRiskAccPaySchema = new LMRiskAccPaySchema();
                tLMRiskAccPaySchema = queryLMRiskAccPay(tLMDutyPaySchema,
                        tLCPremSchema, PolNo);
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }

                //�ж�����λ�ñ���Ƿ�ƥ��
                if (AccCreatePos.equals(tLMRiskAccPaySchema.getAccCreatePos()))
                {
                    //�жϷ����Ƿ���Ҫ¼��
                    if (tLMRiskAccPaySchema.getNeedInput().equals("1"))
                    {
                        //�����Ҫ¼��:�жϴ���ķ����Ƿ�Ϊ��
                        if (Rate == null)
                        {
                            // @@������
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "createPremToAcc";
                            tError.errorMessage = "������Ҫ�ӽ���¼�룬����Ϊ��!";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        tRate = Rate.doubleValue();
                    }
                    else
                    { //ȡĬ��ֵ
                        tRate = tLMRiskAccPaySchema.getDefaultRate();
                    }

                    tLCPremToAccSchema = new LCPremToAccSchema();
                    tLCPremToAccSchema.setPolNo(PolNo);
                    tLCPremToAccSchema.setDutyCode(tLCPremSchema.getDutyCode());
                    tLCPremToAccSchema.setPayPlanCode(tLCPremSchema.
                            getPayPlanCode());
                    tLCPremToAccSchema.setInsuAccNo(tLMRiskAccPaySchema.
                            getInsuAccNo());
                    tLCPremToAccSchema.setRate(tRate);
                    tLCPremToAccSchema.setNewFlag(tLMRiskAccPaySchema.
                                                  getAccCreatePos());
                    tLCPremToAccSchema.setCalCodeMoney(tLMRiskAccPaySchema.
                            getCalCodeMoney());
                    tLCPremToAccSchema.setCalCodeUnit(tLMRiskAccPaySchema.
                            getCalCodeUnit());
                    tLCPremToAccSchema.setCalFlag(tLMRiskAccPaySchema.
                                                  getCalFlag());
                    tLCPremToAccSchema.setOperator(tLCPremSchema.getOperator());
                    tLCPremToAccSchema.setModifyDate(CurrentDate);
                    tLCPremToAccSchema.setModifyTime(CurrentTime);
                    tLCPremToAccSet.add(tLCPremToAccSchema);
                }
            }
        }

        if (tLCPremToAccSet.size() == 0)
        {
            // @@������
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�
     * @param cLMDutyPaySchema LMDutyPaySchema
     * @param cLCPremSchema LCPremSchema
     * @param cPolNo String
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay(LMDutyPaySchema
                                                cLMDutyPaySchema
                                                , LCPremSchema cLCPremSchema,
                                                String cPolNo)
    {
        if ((cLMDutyPaySchema == null) || (cLCPremSchema == null)
            || (cPolNo == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //��ѯ������
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(cPolNo);
        if (tLCPolSchema == null)
        {
            //ȡĬ��ֵ
            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = cLMDutyPaySchema.getPayPlanCode();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�2
     * @param cLCPremToAccSchema LCPremToAccSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay2(LCPremToAccSchema
                                                 cLCPremToAccSchema)
    {
        if (cLCPremToAccSchema == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //��ѯ������
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(cLCPremToAccSchema.getPolNo());
        if (tLCPolSchema == null)
        {
            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = cLCPremToAccSchema.getPayPlanCode();
        String InsuAccNo = cLCPremToAccSchema.getInsuAccNo();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("' and InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�3
     * @param riskCode String
     * @param pLCPremToAccSchema LCPremToAccSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay3(String riskCode
                                                 ,
                                                 LCPremToAccSchema
                                                 pLCPremToAccSchema)
    {
        if (riskCode == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay3";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        String payPlanCode = pLCPremToAccSchema.getPayPlanCode();
        String InsuAccNo = pLCPremToAccSchema.getInsuAccNo();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("' and InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * ��ѯ������
     * @param PolNo String
     * @return LCPolSchema
     */
    public LCPolSchema queryLCPol(String PolNo)
    {
//        System.out.println("�ʻ��ڲ�ѯ������");
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(PolNo);
        if (tLCPolDB.getInfo())
        {
            return tLCPolDB.getSchema();
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tLCPolDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPol";
            tError.errorMessage = "�������ѯʧ��!";
            this.mErrors.addOneError(tError);

            return null;
        }
    }


    /**
     * ��ѯ�����ʻ���(����3������������Ψһ��¼)
     * @param PolNo String
     * @param InsuAccNo String
     * @param OtherNo String
     * @return LCInsureAccSchema
     */
    public LCInsureAccSchema queryLCInsureAcc(String PolNo, String InsuAccNo,
                                              String OtherNo)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LCInsureAcc where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' and InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("' and OtherNo='");
        tSBql.append(OtherNo);
        tSBql.append("' ");

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccDB tLCInsureAccDB = tLCInsureAccSchema.getDB();
        tLCInsureAccSet = tLCInsureAccDB.executeQuery(tSBql.toString());

        if (tLCInsureAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "�����ʻ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "�����ʻ���û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }

        return tLCInsureAccSet.get(1);
    }


    /**
     * ��ѯ�����ʻ���(����һ�����������ؼ�¼����)
     * @param PolNo String
     * @return LCInsureAccSet
     */
    public LCInsureAccSet queryLCInsureAcc(String PolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCInsureAcc where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' ");

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccDB tLCInsureAccDB = tLCInsureAccSchema.getDB();
        tLCInsureAccSet = tLCInsureAccDB.executeQuery(tSBql.toString());

        if (tLCInsureAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "�����ʻ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "�����ʻ���û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }

        return tLCInsureAccSet;
    }


    /**
     * ���ֶ�����ѯ
     * @param RiskCode String
     * @return LMRiskSchema
     */
    public LMRiskSchema queryLMRisk(String RiskCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRisk where RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskSet tLMRiskSet = new LMRiskSet();
        LMRiskDB tLMRiskDB = tLMRiskSchema.getDB();
        tLMRiskSet = tLMRiskDB.executeQuery(tSBql.toString());

        if (tLMRiskDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRisk";
            tError.errorMessage = "���ֶ�����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskSet.clear();

            return null;
        }
        if (tLMRiskSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRisk";
            tError.errorMessage = "���ֶ����û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskSet.clear();

            return null;
        }

        return tLMRiskSet.get(1);
    }


    /**
     * ��ѯ�����˻�������
     * @param RiskCode String
     * @return LMRiskToAccSet
     */
    public LMRiskToAccSet queryLMRiskToAcc(String RiskCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskToAcc where RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema();
        LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
        LMRiskToAccDB tLMRiskToAccDB = tLMRiskToAccSchema.getDB();
        tLMRiskToAccSet = tLMRiskToAccDB.executeQuery(tSBql.toString());

        if (tLMRiskToAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskToAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskToAcc";
            tError.errorMessage = "�����˻��������ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskToAccSet.clear();

            return null;
        }
        if (tLMRiskToAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskToAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskToAcc";
            tError.errorMessage = "�����˻�������û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskToAccSet.clear();

            return null;
        }

        return tLMRiskToAccSet;
    }


    /**
     * ��ѯ���ֱ����ʻ�(����1)
     * @param InsuAccNo String
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(String InsuAccNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ���û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ�(����2)
     * @param InsuAccNo String �ʺ�
     * @param AccType String �ʻ�����
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(String InsuAccNo,
                                                  String AccType)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("' and AccType='");
        tSBql.append(AccType);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ���û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * ���ɸ������Ϳͻ��ʻ���Ĺ�����
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @return LCGetToAccSet
     */
    public LCGetToAccSet getGetToAcc(String PolNo, String AccCreatePos,
                                     Double Rate)
    {
        //1-ȡ����ȡ���
        LCGetSet tLCGetSet = new LCGetSet();
        tLCGetSet = queryLCGet(PolNo);
        if (tLCGetSet == null)
        {
            return null;
        }

        //2-������ȡ���ȡ����Ӧ�����θ���������
        //LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        VData tVData = new VData();
        tVData = createLMDutyGet(tLCGetSet);
        if (tVData == null)
        {
            return null;
        }

        //3-���ɸ������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = createGetToAcc(tVData, PolNo, AccCreatePos, Rate);

        return tLCGetToAccSet;
    }


    /**
     * ȡ����ȡ���
     * @param PolNo String
     * @return LCGetSet
     */
    public LCGetSet queryLCGet(String PolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCGet where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' and needacc='1'");

        LCGetSchema tLCGetSchema = new LCGetSchema();
        LCGetSet tLCGetSet = new LCGetSet();
        LCGetDB tLCGetDB = tLCGetSchema.getDB();
        tLCGetSet = tLCGetDB.executeQuery(tSBql.toString());

        if (tLCGetDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCGet";
            tError.errorMessage = "��ȡ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLCGetSet.clear();

            return null;
        }
        if (tLCGetSet.size() == 0)
        {
            return null;
        }

        return tLCGetSet;
    }


    /**
     * ������ȡ���ȡ����Ӧ�����θ���������
     * @param pLCGetSet LCGetSet
     * @return VData
     */
    public VData createLMDutyGet(LCGetSet pLCGetSet)
    {
        if (pLCGetSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "���ܴ��������!";
            this.mErrors.addOneError(tError);
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //���θ���
        LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        LCGetSchema tLCGetSchema = new LCGetSchema(); //��ȡ���
        LCGetSet tLCGetSet = new LCGetSet();
        for (int i = 1; i <= pLCGetSet.size(); i++)
        {
            tLCGetSchema = new LCGetSchema();
            tLCGetSchema = pLCGetSet.get(i);
            tLMDutyGetSchema = new LMDutyGetSchema();

            //��ѯ���θ�����
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = queryLMDutyGet(tLCGetSchema.getGetDutyCode());
            if (tLMDutyGetSchema == null)
            {
                continue;
            }
            tLMDutyGetSet.add(tLMDutyGetSchema);
            tLCGetSet.add(tLCGetSchema);
        }
        if (tLMDutyGetSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "û�в鵽���θ�����¼!";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        tVData.add(tLMDutyGetSet);
        tVData.add(tLCGetSet);

        return tVData;
    }


    /**
     * ��ѯ���θ�����
     * @param GetDutyCode String
     * @return LMDutyGetSchema
     */
    public LMDutyGetSchema queryLMDutyGet(String GetDutyCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMDutyGet where GetDutyCode='");
        tSBql.append(GetDutyCode);
        tSBql.append("'");

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema();
        LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        LMDutyGetDB tLMDutyGetDB = tLMDutyGetSchema.getDB();
        tLMDutyGetSet = tLMDutyGetDB.executeQuery(tSBql.toString());

        if (tLMDutyGetDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMDutyGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "���θ������ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMDutyGetSet.clear();

            return null;
        }
        if (tLMDutyGetSet.size() == 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "���θ�����û�в�ѯ���������!";
            this.mErrors.addOneError(tError);

            return null;
        }

        return tLMDutyGetSet.get(1);
    }


    /**
     * ���ɸ������Ϳͻ��˻���Ĺ�����
     * @param pVData VData
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @return LCGetToAccSet
     */
    public LCGetToAccSet createGetToAcc(VData pVData, String PolNo,
                                        String AccCreatePos
                                        , Double Rate)
    {
        if ((pVData == null) || (AccCreatePos == null) || (PolNo == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createGetToAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LMDutyGetSet tLMDutyGetSet = (LMDutyGetSet) pVData.
                                     getObjectByObjectName("LMDutyGetSet", 0);
        LCGetSet tLCGetSet = (LCGetSet) pVData.getObjectByObjectName("LCGetSet",
                0);
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //���θ���
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet(); //���θ���
        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema(); //���ֱ����ʻ�����
        LCGetToAccSchema tLCGetToAccSchema = new LCGetToAccSchema(); //�������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet(); //�������Ϳͻ��˻���Ĺ�����

        for (int i = 1; i <= tLMDutyGetSet.size(); i++)
        {
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = tLMDutyGetSet.get(i);

            //�ж��Ƿ���ʻ����
            if (tLMDutyGetSchema.getNeedAcc().equals("1"))
            {
                //��ѯ���ֱ����ʻ�������
                tLMRiskAccGetSet = new LMRiskAccGetSet();
                tLMRiskAccGetSet = queryLMRiskAccGet(tLCPolSchema.getRiskCode()
                        , tLMDutyGetSchema.getGetDutyCode());
                if (tLMRiskAccGetSet == null)
                {
                    continue;
                }
                for (int n = 1; n <= tLMRiskAccGetSet.size(); n++)
                {
                    tLMRiskAccGetSchema = new LMRiskAccGetSchema();
                    tLMRiskAccGetSchema = tLMRiskAccGetSet.get(n);
                    if (tLMRiskAccGetSchema.getDealDirection().equals("0")
                        &&
                        tLMRiskAccGetSchema.getAccCreatePos().equals(
                            AccCreatePos))
                    {
                        tLCGetToAccSchema = new LCGetToAccSchema();

                        //�ж��Ƿ���Ҫ¼��
                        if (tLMRiskAccGetSchema.getNeedInput().equals("1"))
                        {
                            if (Rate == null)
                            {
                                // @@������
                                CError tError = new CError();
                                tError.moduleName = "DealAccount";
                                tError.functionName = "createGetToAcc";
                                tError.errorMessage = "������Ҫ�ӽ���¼�룬����Ϊ��!";
                                this.mErrors.addOneError(tError);

                                return null;
                            }
                            tLCGetToAccSchema.setDefaultRate(Rate.doubleValue());
                        }
                        else
                        {
                            tLCGetToAccSchema.setDefaultRate(
                                    tLMRiskAccGetSchema.getDefaultRate());
                        }

                        tLCGetToAccSchema.setNeedInput(tLMRiskAccGetSchema.
                                getNeedInput());
                        tLCGetToAccSchema.setPolNo(PolNo);
                        tLCGetToAccSchema.setDutyCode(tLCGetSet.get(i).
                                getDutyCode());
                        tLCGetToAccSchema.setGetDutyCode(tLMRiskAccGetSchema.
                                getGetDutyCode());
                        tLCGetToAccSchema.setInsuAccNo(tLMRiskAccGetSchema.
                                getInsuAccNo());
                        tLCGetToAccSchema.setCalCodeMoney(tLMRiskAccGetSchema.
                                getCalCodeMoney());
                        tLCGetToAccSchema.setDealDirection(tLMRiskAccGetSchema.
                                getDealDirection());
                        tLCGetToAccSchema.setCalFlag(tLMRiskAccGetSchema.
                                getCalFlag());
                        tLCGetToAccSchema.setModifyDate(CurrentDate);
                        tLCGetToAccSchema.setModifyTime(CurrentTime);
                        tLCGetToAccSet.add(tLCGetToAccSchema);
                    }
                }
            }
        }

        if (tLCGetToAccSet.size() == 0)
        {
            // @@������
            //      CError tError = new CError();
            //      tError.moduleName = "DealAccount";
            //      tError.functionName = "createGetToAcc";
            //      tError.errorMessage = "û�з��������ĸ������Ϳͻ��˻���Ĺ������¼!";
            //      this.mErrors.addOneError(tError);
            return null;
        }

        return tLCGetToAccSet;
    }


    /**
     * ��ѯ���ֱ����ʻ�������
     * @param RiskCode String
     * @param GetDutyCode String
     * @return LMRiskAccGetSet
     */
    public LMRiskAccGetSet queryLMRiskAccGet(String RiskCode,
                                             String GetDutyCode)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccGet where GetDutyCode='");
        tSBql.append(GetDutyCode);
        tSBql.append("' and RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema();
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet();
        LMRiskAccGetDB tLMRiskAccGetDB = tLMRiskAccGetSchema.getDB();
        tLMRiskAccGetSet = tLMRiskAccGetDB.executeQuery(tSBql.toString());

        if (tLMRiskAccGetDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccGet";
            tError.errorMessage = "���ֱ����ʻ��������ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccGetSet.clear();

            return null;
        }
        if (tLMRiskAccGetSet.size() == 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccGet";
            tError.errorMessage = "���ֱ����ʻ�������û�в�ѯ���������!";
            this.mErrors.addOneError(tError);

            return null;
        }

        return tLMRiskAccGetSet;
    }


    /**
     * ��ѯ�������Ϳͻ��ʻ���Ĺ�����
     * @param pLCPremSchema LCPremSchema
     * @return LCPremToAccSet
     */
    public LCPremToAccSet queryLCPremToAccSet(LCPremSchema pLCPremSchema)
    {
        if (pLCPremSchema == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPremToAccSet";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LCPremToAcc where PolNo='");
        tSBql.append(pLCPremSchema.getPolNo());
        tSBql.append("' and  DutyCode='");
        tSBql.append(pLCPremSchema.getDutyCode());
        tSBql.append("' and PayPlanCode='");
        tSBql.append(pLCPremSchema.getPayPlanCode());
        tSBql.append("' ");

        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        LCPremToAccDB tLCPremToAccDB = tLCPremToAccSchema.getDB();
        tLCPremToAccSet = tLCPremToAccDB.executeQuery(tSBql.toString());

        if (tLCPremToAccDB.mErrors.needDealError())
        {
            return null;
        }
        if (tLCPremToAccSet.size() == 0)
        {
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * �Ӵ���ı����ʻ������в�ѯ���������ļ�¼
     * @param PolNo String
     * @param InsuAccNo String
     * @param OtherNo String
     * @param pLCInsureAccSet LCInsureAccSet
     * @return LCInsureAccSchema
     */
    public LCInsureAccSchema queryLCInsureAccSet(String PolNo, String InsuAccNo,
                                                 String OtherNo
                                                 ,
                                                 LCInsureAccSet pLCInsureAccSet)
    {
        if ((PolNo == null) || (InsuAccNo == null) || (OtherNo == null)
            || (pLCInsureAccSet == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAccSet";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        for (int i = 1; i <= pLCInsureAccSet.size(); i++)
        {
            tLCInsureAccSchema = new LCInsureAccSchema();
            tLCInsureAccSchema = pLCInsureAccSet.get(i);
            if (tLCInsureAccSchema.getPolNo().equals(PolNo)
                && tLCInsureAccSchema.getInsuAccNo().equals(InsuAccNo)
                // && tLCInsureAccSchema.getOtherNo().equals(OtherNo)
                )
            {
                return tLCInsureAccSchema;
            }
        }

        // @@������
        CError tError = new CError();
        tError.moduleName = "DealAccount";
        tError.functionName = "queryLCInsureAccSet";
        tError.errorMessage = "û�д�Ҫ���ɵı����˻����ҵ�ƥ�������!";
        this.mErrors.addOneError(tError);

        return null;
    }


    /**
     * ����ʵ��Ӧ��ע����ʽ�(����Ӷ�����,�������ݿ��ڵļ��������δ����)
     * @param tLCPremToAccSchema ���뱣�����Ϳͻ��ʻ���Ĺ������¼
     * @param Prem ���ɱ���
     * @return ʵ��Ӧ��ע����ʽ�
     */
    public double calInputMoney(LCPremToAccSchema tLCPremToAccSchema,
                                double Prem)
    {
        // @@������
        if (tLCPremToAccSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "calInputMoneyRate";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return -1;
        }

//        String[] F = new String[5];
//        int m = 0;
        double defaultRate = 0;
        double inputMoney = 0;
        String calMoney = "";
        defaultRate = tLCPremToAccSchema.getRate(); //ȱʡ����

        Calculator tCalculator = new Calculator(); //������

        if (tLCPremToAccSchema.getCalFlag() == null)
        { //����ñ��Ϊ��
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }

        //�˻�ת������־:0 ���� ��ȫת���˻�
        // 1 ���� ���ֽ����ת���˻�
        // 2 ���� ���ɷݼ���ת���˻�
        // 3 ���� �����ֽ�Ȼ�󰴹ɷݼ��㡣(δ��)
        if (tLCPremToAccSchema.getCalFlag().equals("0"))
        {
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("1"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "δ�ҵ�ת���˻�ʱ���㷨����(�ֽ�)!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeMoney()); //��Ӽ������

            //��Ӽ����Ҫ����������
            LCPolDB tLCPolDB = new LCPolDB();

            //ע�⣺��ʱ���������ǻ�û��ǩ��������Ҫ���ݾ������������루Ͷ�����Ż򱣵��ţ�
            tLCPolDB.setPolNo(tLCPremToAccSchema.getPolNo());
            if (!tLCPolDB.getInfo())
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "δ�ҵ��˻���Ӧ�ı���!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.addBasicFactor("ManageFeeRate",
                                       String.valueOf(tLCPolDB.getManageFeeRate())); //����ѱ���-�μ��������ֺ�-�������601304
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));

            //����Ҫ�ؿɺ������
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "����ע���ʻ��ʽ�ʧ��!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("2"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "δ�ҵ�ת���˻�ʱ���㷨����(�ɷ�)";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeUnit()); //��Ӽ������

            //��Ӽ����Ҫ����������
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "����ע���ʻ��ʽ�ʧ��!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }

        return 0;
    }


    /**
     * �޸ı����ʻ���Ǽ��������¼�Ľ�������
     * @param PayDate String
     * @param pVData VData
     * @return VData
     */
    public VData updateLCInsureAccTraceDate(String PayDate, VData pVData)
    {
        // @@������
        if ((PayDate == null) || (pVData == null))
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "updateLCInsureAccTraceDate";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();

        tLCInsureAccSet = (LCInsureAccSet) pVData.getObjectByObjectName(
                "LCInsureAccSet", 0);
        tLCInsureAccTraceSet = (LCInsureAccTraceSet) pVData.
                               getObjectByObjectName(
                "LCInsureAccTraceSet", 0);

        if (tLCInsureAccTraceSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "updateLCInsureAccTraceDate";
            tError.errorMessage = "VData��û���ҵ���Ҫ������!";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccTraceSet newLCInsureAccTraceSet = new LCInsureAccTraceSet();
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        for (int n = 1; n <= tLCInsureAccTraceSet.size(); n++)
        {
            tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
            tLCInsureAccTraceSchema = tLCInsureAccTraceSet.get(n);
            tLCInsureAccTraceSchema.setPayDate(PayDate);
            newLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
        }
        tVData.add(newLCInsureAccTraceSet);
        tVData.add(tLCInsureAccSet);

        return tVData;
    }


    /**
     * Ϊ�Ѿ����ڵļ����¸����˻���ӽ��ѹ켣(Ʃ�磬�б�ǩ��ʱӦ��ע���ʽ��û��ע��)
     * @param GrpPolNo String
     * @param InsuAccNo String
     * @param Money double
     * @return boolean
     */
    public boolean addPremTraceForAcc(String GrpPolNo, String InsuAccNo,
                                      double Money)
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpPolNo(GrpPolNo);
        if (!tLCGrpPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPremTraceForAcc";
            tError.errorMessage = "û���ҵ����屣��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        String ManageCom = tLCGrpPolDB.getManageCom();
        VData tVData = new VData();
        LCInsureAccTraceSet saveLCInsureAccTraceSet = new LCInsureAccTraceSet();
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setGrpPolNo(GrpPolNo);

        LCPolSet tLCPolSet = new LCPolSet();
        tLCPolSet = tLCPolDB.query();

        LCPolSchema tLCPolSchema = new LCPolSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
        for (int i = 1; i <= tLCPolSet.size(); i++)
        {
            tLCPolSchema = tLCPolSet.get(i);
            tVData = addPrem(tLCPolSchema.getPolNo(), InsuAccNo,
                             tLCPolSchema.getPolNo(), "1", "BF"
                             , ManageCom, Money);
            tLCInsureAccTraceSet = (LCInsureAccTraceSet) tVData.
                                   getObjectByObjectName(
                    "LCInsureAccTraceSet", 0);
            if (tLCInsureAccTraceSet != null)
            {
                saveLCInsureAccTraceSet.add(tLCInsureAccTraceSet);
            }
        }

        Connection conn = DBConnPool.getConnection();

        try
        {
            conn.setAutoCommit(false);

            LCInsureAccTraceDBSet tLCInsureAccTraceDBSet = new
                    LCInsureAccTraceDBSet(conn);
            tLCInsureAccTraceDBSet.add(saveLCInsureAccTraceSet);

            //�����ύ
            if (!tLCInsureAccTraceDBSet.insert())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCInsureAccTraceDBSet.mErrors);

                CError tError = new CError();
                tError.moduleName = "tLPAppntIndDB";
                tError.functionName = "insertData";
                tError.errorMessage = "�����ύʧ��!";
                this.mErrors.addOneError(tError);

                conn.rollback();
                conn.close();

                return false;
            }

            conn.commit();
        }
        catch (Exception ex)
        {
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {
            }
        }

        return true;
    }


    //---------------Ϊ��������׼�������磺��������ǩ�������ʻ�------------------

    /**
     * �Ը��˱������ɱ����ʻ���(���� 1�����ʻ�,����Ҫ����������¼)
     * @param PolNo String ������
     * @param AccCreatePos String ����λ�� :1-Ͷ����¼��ʱ���� 2���ɷ�ʱ���� 3����ȡʱ����
     * @param OtherNo String �����Ż򽻷Ѻ�
     * @param OtherNoType String �����Ż򽻷Ѻ�
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForBat(String PolNo,
                                               String AccCreatePos,
                                               String OtherNo
                                               , String OtherNoType,
                                               LCPolSchema inLCPolSchema,
                                               LMRiskSchema inLMRiskSchema)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (inLCPolSchema == null)
            || (inLMRiskSchema == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAccForBat";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���

        if (inLMRiskSchema.getInsuAccFlag().equals("Y")
            || inLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //�������ֲ�ѯLMRiskToAcc��(�����˻�������)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(inLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //�����˻�������
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //���ֱ����ʻ�
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //�����ʻ���

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //���ݱ����˻������ѯLMRiskInsuAcc��(���ֱ����ʻ�)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //�ж��ʻ����ͣ������Ƿ�������ؼ�¼ Alex 20050920
                if (tLMRiskInsuAccSchema.getAccType().equals("001") ||
                    tLMRiskInsuAccSchema.getAccType().equals("005"))
                {
                    //001�����ʻ���005���������ʻ�
                    //�������������-2 --���ŵ��������ʻ�
                    //��ʱ�����ɶ�Ӧ�Ĺ����ʻ���¼
                    if ((inLCPolSchema.getPolTypeFlag() != null)
                        && inLCPolSchema.getPolTypeFlag().equals("2"))
                    {
                        System.out.println("��ʼ���ɼ����ʻ�");
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    //����жϣ�ʹ���ɹ����ʻ�ͬʱ�����ɸ����ʻ����͵ļ�¼
                    //�������������-2 --���ŵ��������ʻ�
                    //�����ɸ������͵��ʻ�, ����Ͷ���˻�����Ҫ���ɵ�
                    if ((inLCPolSchema.getPolTypeFlag() != null)
                        && inLCPolSchema.getPolTypeFlag().equals("2") && (!"2".equals(tLMRiskInsuAccSchema.getAccKind())))
                    {
                        continue;
                    }
                    else
                    {
                        System.out.println("��ʼ���ɸ����ʻ�");
                    }

                }

                //���ɱ����˻���
                //����˻�����λ���ҵ�ƥ��ı����˻�
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(inLCPolSchema.getPolNo());
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    tLCInsureAccSchema.setGrpContNo(inLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setPrtNo(inLCPolSchema.getPrtNo());
                    tLCInsureAccSchema.setContNo(inLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(inLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(inLCPolSchema.getInsuredNo());
                    tLCInsureAccSchema.setAppntNo(inLCPolSchema.getAppntNo());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setLastAccBala(0);
                    tLCInsureAccSchema.setLastUnitCount(0);
                    tLCInsureAccSchema.setLastUnitPrice(0);
                    tLCInsureAccSchema.setUnitPrice(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    tLCInsureAccSchema.setManageCom(inLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(inLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(mEnterAccDate); //Ĭ�ϵĽ�Ϣ�գ�ԭΪǩ���գ��ָ�Ϊ���������� Alex 20051124
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSchema.setState("0");
                    tLCInsureAccSchema.setInvestType(tLMRiskInsuAccSchema.
                            getInvestType());
                    tLCInsureAccSchema.setFundCompanyCode(tLMRiskInsuAccSchema.
                            getFundCompanyCode());
                    tLCInsureAccSchema.setOwner(tLMRiskInsuAccSchema.getOwner());
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * ���ɱ����ʻ�(���ɽṹ:���������˻���,�����������Ϳͻ��˻���Ĺ�����,�����������Ϳͻ��˻���Ĺ�����)
     * @param parmData TransferData
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return VData
     */
    public VData createInsureAccForBat(TransferData parmData,
                                       LCPolSchema inLCPolSchema
                                       , LMRiskSchema inLMRiskSchema)
    {
        //1-����
        if (!checkTransferData(parmData))
        {
            return null;
        }

        if ((inLCPolSchema == null) || (inLMRiskSchema == null))
        {
            return null;
        }

        //2-�õ����ݺ���
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        mEnterAccDate = (String) parmData.getValueByName("FinanceDate");
        if (mEnterAccDate == null || mEnterAccDate.equals(""))
        {
            mEnterAccDate = CurrentDate; //��δ�������ȷ���գ���ǩ����Ϊ�ʽ�ע���ʻ��� Alex 20051124
        }
        else
        {
            //�����Բ���ȷ����Ϊ�ʽ�ע���ʻ��գ������κ��޸�
//            FDate tFDate = new FDate();
//            Date tEndDate = PubFun.calDate(tFDate.getDate(mEnterAccDate), 1,
//                                       "D", null);
//            mEnterAccDate=tFDate.getString(tEndDate);
        }
        if ("1".equals(inLCPolSchema.getContType()))
        {
            //�����Ա���ǩ������Ϊ�ʽ�ע���ʻ��գ�����Ĭ��ǩ���������ǩ������ͬ
//            mEnterAccDate = inLCPolSchema.getCValiDate();
            mEnterAccDate = CurrentDate;
        }

        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("����:" + tRate);

        //3-���������˻���
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAccForBat(tPolNo, tAccCreatePos, tOtherNo,
                                               tOtherNoType
                                               , inLCPolSchema, inLMRiskSchema);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-�����������Ϳͻ��˻���Ĺ�����
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAccForBat(tPolNo, tAccCreatePos, tRate,
                                             inLCPolSchema);

        //4.5 �����˻������
        LCInsureAccClassSet tLCInsureAccClassSet = null;
        if (tLCPremToAccSet != null)
        {
            tLCInsureAccClassSet = getLCInsureAccClassForBat(inLCPolSchema,
                    tLCPremToAccSet);
        }

        //if(tLCPremToAccSet==null) return null;
        //5-�����������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAccForBat(tPolNo, tAccCreatePos, tRate,
                                           inLCPolSchema);

        //if(tLCGetToAccSet==null) return null;
        //6-��������
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //������null
        tVData.add(tLCInsureAccClassSet);
        tVData.add(tLCGetToAccSet); //������null

        return tVData;
    }


    /**
     * ��������ѽṹ
     * @param tLCPolSchema LCPolSchema
     * @param tLCPremToAccSet LCPremToAccSet
     * @param tLCInsureAccSet LCInsureAccSet
     * @return VData
     */
    public VData getManageFeeStru(LCPolSchema tLCPolSchema,
                                  LCPremToAccSet tLCPremToAccSet
                                  , LCInsureAccSet tLCInsureAccSet)
    {

        VData tData = new VData();
        //�����ʻ�����ѱ�
        LCInsureAccFeeSet tLCInsureAccFeeSet = new LCInsureAccFeeSet();
        //�����˻�����ѷ����
        LCInsureAccClassFeeSet tLCInsureAccClassFeeSet = new
                LCInsureAccClassFeeSet();
        //�����ʻ�������
        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
        LCPremToAccSchema tLCPremToAccSchema = null;
        if (tLCPremToAccSet != null)
        {
            for (int t = 1; t <= tLCPremToAccSet.size(); t++)
            {
                tLCPremToAccSchema = tLCPremToAccSet.get(t);

                LCInsureAccSchema tLCInsureAccSchema = null;
                for (int i = 1; i <= tLCInsureAccSet.size(); i++)
                {
                    if (tLCInsureAccSet.get(i).getInsuAccNo().equals(
                            tLCPremToAccSchema.
                            getInsuAccNo()))
                    {
                        tLCInsureAccSchema = tLCInsureAccSet.get(i);
                        break;
                    }
                }
                if (tLCInsureAccSchema == null)
                {
                    // System.out.println("û���ҶԶ�Ӧ���˻�");
                    continue;
                    // return null;
                }
                //��ѯ�����ʻ��������ж���������
                tLMRiskInsuAccDB.setInsuAccNo(tLCInsureAccSchema.getInsuAccNo());
                if (!tLMRiskInsuAccDB.getInfo())
                {
                    CError.buildErr(this, "��ѯ�����˻����������");
                    return null;
                }

                //��������ѷ����
                LCInsureAccClassFeeSchema tLCInsureAccClassFeeSchema = new
                        LCInsureAccClassFeeSchema();
                tLCInsureAccClassFeeSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                tLCInsureAccClassFeeSchema.setPolNo(tLCPolSchema.getPolNo());
                tLCInsureAccClassFeeSchema.setInsuAccNo(tLCPremToAccSchema.
                        getInsuAccNo());
                tLCInsureAccClassFeeSchema.setPayPlanCode(tLCPremToAccSchema.
                        getPayPlanCode());
                //  tLCInsureAccClassFeeSchema.setAcc
                tLCInsureAccClassFeeSchema.setAccType(tLCInsureAccSchema.
                        getAccType());
                tLCInsureAccClassFeeSchema.setContNo(tLCPolSchema.getContNo());
                tLCInsureAccClassFeeSchema.setManageCom(tLCPolSchema.
                        getManageCom());
                tLCInsureAccClassFeeSchema.setGrpContNo(tLCPolSchema.
                        getGrpContNo());
                tLCInsureAccClassFeeSchema.setOtherType("1"); //���˱�����
                tLCInsureAccClassFeeSchema.setOtherNo(tLCPolSchema.getPolNo());
                tLCInsureAccClassFeeSchema.setRiskCode(tLCPolSchema.getRiskCode());
                tLCInsureAccClassFeeSchema.setInsuredNo(tLCPolSchema.
                        getInsuredNo());
                tLCInsureAccClassFeeSchema.setAppntNo(tLCPolSchema.getAppntNo());
                tLCInsureAccClassFeeSchema.setAccComputeFlag(tLCInsureAccSchema.
                        getAccComputeFlag());
                //            tLCInsureAccClassFeeSchema.setAccFoundDate(null);
//                tLCInsureAccClassFeeSchema.setBalaDate(tLCPolSchema.getCValiDate());
                tLCInsureAccClassFeeSchema.setBalaDate(tLCInsureAccSchema.
                        getBalaDate()); //��Ϊ��Ӧ�ʻ�����ʽ�����
                //            tLCInsureAccClassFeeSchema.setBalaTime();
                tLCInsureAccClassFeeSchema.setFee(0);
                tLCInsureAccClassFeeSchema.setFeeRate(0);
                tLCInsureAccClassFeeSchema.setFeeUnit(0);
                tLCInsureAccClassFeeSchema.setMakeDate(PubFun.getCurrentDate());
                tLCInsureAccClassFeeSchema.setOperator(tLCPolSchema.getOperator());
                tLCInsureAccClassFeeSchema.setMakeDate(CurrentDate);
                tLCInsureAccClassFeeSchema.setMakeTime(CurrentTime);
                tLCInsureAccClassFeeSchema.setModifyDate(CurrentDate);
                tLCInsureAccClassFeeSchema.setModifyTime(CurrentTime);
                //��ʱ����δ����
                tLCInsureAccClassFeeSchema.setAccAscription("0");
                if (tLMRiskInsuAccDB.getOwner() != null &&
                    !"".equals(tLMRiskInsuAccDB.getOwner()))
                {
                    //�������ù�������
                    tLCInsureAccClassFeeSchema.setAccAscription(
                            tLMRiskInsuAccDB.getOwner());
                }
                tLCInsureAccClassFeeSet.add(tLCInsureAccClassFeeSchema);

                //����ѱ�
                //���ҿ��Ƿ��Ѿ�����
                boolean has = false;

                for (int j = 1; j <= tLCInsureAccFeeSet.size(); j++)
                {
                    if (tLCInsureAccFeeSet.get(j).getInsuAccNo().equals(
                            tLCPremToAccSchema.getInsuAccNo()))
                    {
                        has = true;
                        break;
                    }

                }

                if (has)
                {
                    continue;
                }
                //��û�У��򴴽�
                LCInsureAccFeeSchema tLCInsureAccFeeSchema = new
                        LCInsureAccFeeSchema();
                tLCInsureAccFeeSchema.setAppntNo(tLCPolSchema.getAppntNo());
                //          tLCInsureAccFeeSchema.setAppntName(tLCPolSchema.getAppntName());
                tLCInsureAccFeeSchema.setInsuAccNo(tLCPremToAccSchema.
                        getInsuAccNo());
                tLCInsureAccFeeSchema.setManageCom(tLCPolSchema.getManageCom());
                tLCInsureAccFeeSchema.setPolNo(tLCPolSchema.getPolNo());
                tLCInsureAccFeeSchema.setRiskCode(tLCPolSchema.getRiskCode());
                tLCInsureAccFeeSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
                tLCInsureAccFeeSchema.setContNo(tLCPolSchema.getContNo());
                tLCInsureAccFeeSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
                tLCInsureAccFeeSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                // tLCInsureAccFeeSchema.setMoney(0);
                tLCInsureAccFeeSchema.setPrtNo(tLCPolSchema.getPrtNo());
                // tLCInsureAccFeeSchema.setUnitCount(0);
                tLCInsureAccFeeSchema.setAccType(tLCInsureAccSchema.getAccType());
                tLCInsureAccFeeSchema.setAccComputeFlag(tLCInsureAccSchema.
                        getAccComputeFlag());
                tLCInsureAccFeeSchema.setFundCompanyCode(tLCInsureAccSchema.
                        getFundCompanyCode());
                tLCInsureAccFeeSchema.setOwner(tLCInsureAccSchema.getOwner());
                tLCInsureAccFeeSchema.setInvestType(tLCInsureAccSchema.
                        getInvestType());
                tLCInsureAccFeeSchema.setMakeDate(CurrentDate);
                tLCInsureAccFeeSchema.setMakeTime(CurrentTime);
                tLCInsureAccFeeSchema.setModifyDate(CurrentDate);
                tLCInsureAccFeeSchema.setModifyTime(CurrentTime);
                tLCInsureAccFeeSchema.setOperator(tLCPolSchema.getOperator());
                tLCInsureAccFeeSet.add(tLCInsureAccFeeSchema);                
            }
        }
     
        tData.add(tLCInsureAccClassFeeSet);
        tData.add(tLCInsureAccFeeSet);
        return tData;
    }


    /**
	 * ���������˻������
	 * 
	 * @param tPolSchema
	 *            LCPolSchema
	 * @param tLCPremToAccSet
	 *            LCPremToAccSet
	 * @return LCInsureAccClassSet
	 */
    private LCInsureAccClassSet getLCInsureAccClassForBat(LCPolSchema
            tPolSchema
            , LCPremToAccSet tLCPremToAccSet)
    {
        LCInsureAccClassSet tSet = new LCInsureAccClassSet();
        LCPremToAccSchema tPremAccSchema = null;
        String nowDate = PubFun.getCurrentDate();
        String nowTime = PubFun.getCurrentTime();
        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
        String tAccNo = "";
        for (int i = 1; i <= tLCPremToAccSet.size(); i++)
        {
            LCInsureAccClassSchema tSchema = new LCInsureAccClassSchema();
            tPremAccSchema = tLCPremToAccSet.get(i);
            //��У����
            //����Ҫ�ж��ʻ����ͣ���У����� Alex 20050902
            //if (!tPremAccSchema.getInsuAccNo().equals(tAccNo)) //?
            //{
            tAccNo = tPremAccSchema.getInsuAccNo();
            tLMRiskInsuAccDB.setInsuAccNo(tAccNo);
            if (!tLMRiskInsuAccDB.getInfo())
            {
                CError.buildErr(this, "��ѯ�����˻����������");
                return null;
            }
            //����ʻ������Ǽ����ʻ���������ʻ�,�˳�  add by Alex 20050920
            //�ж��ʻ����ͣ������Ƿ�������ؼ�¼
            if (tLMRiskInsuAccDB.getAccType().equals("001") ||
                tLMRiskInsuAccDB.getAccType().equals("005"))
            {
                //001�����ʻ���005���������ʻ�
                //�������������-2 --���ŵ��������ʻ�
                //��ʱ�����ɶ�Ӧ�Ĺ����ʻ���¼
                if ((tPolSchema.getPolTypeFlag() != null)
                    && tPolSchema.getPolTypeFlag().equals("2"))
                {
                    System.out.println("��ʼ���ɼ����ʻ�");
                }
                else
                {
                    continue;
                }
            }
            else
            {
                //����жϣ�ʹ���ɹ����ʻ�ͬʱ�����ɸ����ʻ����͵ļ�¼
                //�������������-2 --���ŵ��������ʻ�
                //�����ɸ������͵��ʻ�, ����Ͷ���˻�����Ҫ���ɵ� 
                if ((tPolSchema.getPolTypeFlag() != null)
                    && tPolSchema.getPolTypeFlag().equals("2") && (!"2".equals(tLMRiskInsuAccDB.getAccKind())))
                {
                    continue;
                }
                else
                {
                    System.out.println("��ʼ���ɸ����ʻ�");
                }

            }

            //add by Alex 20050920

            //}

            tSchema.setAccType(tLMRiskInsuAccDB.getAccType());
            tSchema.setState("0");
            tSchema.setAccComputeFlag(tLMRiskInsuAccDB.getAccComputeFlag());

            tSchema.setPolNo(tPremAccSchema.getPolNo());
            tSchema.setInsuAccNo(tPremAccSchema.getInsuAccNo());
            tSchema.setPayPlanCode(tPremAccSchema.getPayPlanCode());
            tSchema.setContNo(tPolSchema.getContNo());
            //       tSchema.setAppntName(tPolSchema.getAppntName());
            tSchema.setInsuredNo(tPolSchema.getInsuredNo());
            tSchema.setGrpContNo(tPolSchema.getGrpContNo());
            tSchema.setGrpPolNo(tPolSchema.getGrpPolNo());
            tSchema.setFrozenMoney(0);
            //  tSchema.setBalaDate(nowDate);
            //   tSchema.setBalaTime(nowTime);
            tSchema.setInsuAccBala(0);
            tSchema.setInsuAccGetMoney(0);
            tSchema.setBalaDate(mEnterAccDate);
            tSchema.setMakeDate(nowDate);
            tSchema.setMakeTime(nowTime);
            tSchema.setManageCom(tPolSchema.getManageCom());
            tSchema.setModifyDate(nowDate);
            tSchema.setModifyTime(nowTime);
            tSchema.setOperator(this.mGlobalInput.Operator);
            tSchema.setOtherNo(tPolSchema.getPolNo());
            tSchema.setOtherType("1");
            tSchema.setState("0");
            tSchema.setSumPay(0);
            tSchema.setSumPaym(0);
//       tSchema.setState();
            //�¼��ֶ�
            tSchema.setGrpContNo(tPolSchema.getGrpContNo());
            tSchema.setAppntNo(tPolSchema.getAppntNo());
            tSchema.setUnitCount(0);
            tSchema.setRiskCode(tPolSchema.getRiskCode());
            //��ʱ����δ����-- wujs
            tSchema.setAccAscription("0");
            //����������LMRiskInsuAcc�й�Ա�����ı�����ȷ���ʻ��Ĺ�������
            //����ʱ�����ʻ����˽��Ѳ��ֺ͸��˺����ʻ����˽��Ѳ���Ϊ�ѹ���������Alex 20050819
            if (tLMRiskInsuAccDB.getOwner() != null &&
                !"".equals(tLMRiskInsuAccDB.getOwner()))
            {
                tSchema.setAccAscription(tLMRiskInsuAccDB.getOwner());
            }
            tSet.add(tSchema);

        }
        return tSet;
    }


    /**
     * ���ɱ������Ϳͻ��ʻ���Ĺ�����
     * @param PolNo ������
     * @param AccCreatePos ����λ��
     * @param Rate ����
     * @param inLCPolSchema ���뱣������
     * @return LCPremToAccSet �����������
     */
    public LCPremToAccSet getPremToAccForBat(String PolNo, String AccCreatePos,
                                             Double Rate,
                                             LCPolSchema inLCPolSchema)
    {
        if ((PolNo == null) || (AccCreatePos == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getPremToAcc";
            tError.errorMessage = "����ԭ��:�����������Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        String tPolNo = PolNo;
        String tAccCreatePos = AccCreatePos;
        Double tRate = Rate;
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();

        //1-ȡ���������
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = queryLCPrem(tPolNo);
        if (tLCPremSet == null)
        {
            return null;
        }

        //2-���ݱ������ȡ����Ӧ�����νɷ�������
        VData tVData = new VData();
        tVData = getFromLMDutyPay(tLCPremSet);
        if (tVData == null)
        {
            return null;
        }

        //3-���ɱ������Ϳͻ��˻���Ĺ�����
        tLCPremToAccSet = createPremToAccForBat(tVData, tPolNo, tAccCreatePos,
                                                tRate, inLCPolSchema);

        return tLCPremToAccSet;
    }


    /**
     * ���ɱ������Ϳͻ��˻���Ĺ�����
     * @param tVData VData �������ν��Ѻͱ������
     * @param PolNo String ������
     * @param AccCreatePos String �����ʻ�������λ�ñ�ǣ��б������ѵȣ�
     * @param Rate Double ��ȡ����
     * @param inLCPolSchema LCPolSchema
     * @return LCPremToAccSet
     */
    public LCPremToAccSet createPremToAccForBat(VData tVData, String PolNo,
                                                String AccCreatePos,
                                                Double Rate,
                                                LCPolSchema inLCPolSchema)
    {
        if ((tVData == null) || (StrTool.cTrim(PolNo).equals("")) || (AccCreatePos == null))
        {
            // @@������
            CError.buildErr(this,"�������ݲ���Ϊ��!");
            return null;
        }
        //TODO:
        //�ж��Ƿ񹫹��˻�
        String tPolTypeFlag = StrTool.cTrim(inLCPolSchema.getPolTypeFlag());
        if("".equals(tPolTypeFlag)){
        	LCPolDB tLCPolDB = new LCPolDB();
        	tLCPolDB.setPolNo(PolNo);
        	tLCPolDB.getInfo();
        	tPolTypeFlag = StrTool.cTrim(tLCPolDB.getPolTypeFlag()); 
        	if("".equals(tPolTypeFlag)){
        	      // @@������
                CError.buildErr(this,"������������!");
                return null;
        	}
        }
        
        LCPremSet tLCPremSet = (LCPremSet) tVData.getObjectByObjectName(
                "LCPremSet", 0);
        LMDutyPaySet tLMDutyPaySet = (LMDutyPaySet) tVData.
                                     getObjectByObjectName("LMDutyPaySet",
                0);

        LCPremSchema tLCPremSchema = new LCPremSchema(); //�������
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema(); //���ν��ѱ�
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema(); //
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //�������Ϳͻ��ʻ���Ĺ�����
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema(); //�������Ϳͻ��ʻ���Ĺ�����
        LMRiskAccPaySet tLMRiskAccPaySet = null;
        double tRate = 0;
        for (int i = 1; i <= tLMDutyPaySet.size(); i++)
        {
            tLMDutyPaySchema = tLMDutyPaySet.get(i);
            tLCPremSchema = tLCPremSet.get(i);

            //�ж��Ƿ���ʻ�����
            if (tLMDutyPaySchema.getNeedAcc().equals("1"))
            {
                //��ѯ���ֱ����ʻ��ɷѱ�
                // tLMRiskAccPaySchema = new LMRiskAccPaySchema();
                tLMRiskAccPaySet = queryLMRiskAccPayForBat(tLMDutyPaySchema.
                        getPayPlanCode(),
                        inLCPolSchema.getRiskCode());
                if (tLMRiskAccPaySet == null || tLMRiskAccPaySet.size() == 0
                    )
                {
                    System.out.println("��ѯ�����˻��ɷѱ�ʧ��");
                    CError.buildErr(this, "��ѯ�����˻��ɷѱ�ʧ��");
                    return null;
                }
                for (int u = 1; u <= tLMRiskAccPaySet.size(); u++)
                {
                    tLMRiskAccPaySchema = tLMRiskAccPaySet.get(u);
                   
                    
                    //�ж�����λ�ñ���Ƿ�ƥ��
                    if (AccCreatePos.equals(tLMRiskAccPaySchema.getAccCreatePos()))
                    {
                        //�жϷ����Ƿ���Ҫ¼��
                        if (tLMRiskAccPaySchema.getNeedInput().equals("1"))
                        {
                            //�����Ҫ¼��:�жϴ���ķ����Ƿ�Ϊ��
                            if (Rate == null)
                            {
                                // @@������
                                CError.buildErr(this,"������Ҫ�ӽ���¼�룬����Ϊ��!") ;
                                return null;
                            }
                            tRate = Rate.doubleValue();
                        }
                        else
                        { //ȡĬ��ֵ
                            tRate = tLMRiskAccPaySchema.getDefaultRate();
                        }
                        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
                        tLMRiskInsuAccDB.setInsuAccNo(tLMRiskAccPaySchema.getInsuAccNo());
                        tLMRiskInsuAccDB.getInfo();
                        if ("2".equals(tPolTypeFlag))// �����˻�
						{
							if (!tLMRiskInsuAccDB.getAccType().equals("001")
									&& !tLMRiskInsuAccDB.equals("005"))
								continue;
						} else {// �����˻�
							if (tLMRiskInsuAccDB.getAccType().equals("001")
									|| tLMRiskInsuAccDB.equals("005"))
								continue;
						}
                        tLCPremToAccSchema = new LCPremToAccSchema();
//                      tLCPremToAccSchema.setPolNo(PolNo);
                        tLCPremToAccSchema.setPolNo(inLCPolSchema.getPolNo());
                        tLCPremToAccSchema.setDutyCode(tLCPremSchema.
                                getDutyCode());
                        tLCPremToAccSchema.setPayPlanCode(tLCPremSchema
                                .getPayPlanCode());
                        tLCPremToAccSchema.setInsuAccNo(tLMRiskAccPaySchema
                                .getInsuAccNo());
                        tLCPremToAccSchema.setRate(tRate);
                        tLCPremToAccSchema.setNewFlag(tLMRiskAccPaySchema
                                .getAccCreatePos());
                        tLCPremToAccSchema.setCalCodeMoney(tLMRiskAccPaySchema
                                .getCalCodeMoney());
                        tLCPremToAccSchema.setCalCodeUnit(tLMRiskAccPaySchema
                                .getCalCodeUnit());
                        tLCPremToAccSchema.setCalFlag(tLMRiskAccPaySchema
                                .getCalFlag());
                        tLCPremToAccSchema.setOperator(this.mGlobalInput.
                                Operator);
                        tLCPremToAccSchema.setMakeDate(CurrentDate);
                        tLCPremToAccSchema.setMakeTime(CurrentTime);
                        tLCPremToAccSchema.setModifyDate(CurrentDate);
                        tLCPremToAccSchema.setModifyTime(CurrentTime);

                        tLCPremToAccSet.add(tLCPremToAccSchema);
                    }
                }
            }
        }

        if (tLCPremToAccSet.size() == 0)
        {
            // @@������
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�
     * @param pLMDutyPaySchema LMDutyPaySchema
     * @param pLCPremSchema LCPremSchema
     * @param PolNo String
     * @param inLCPolSchema LCPolSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPayForBat(LMDutyPaySchema
            pLMDutyPaySchema,
            LCPremSchema pLCPremSchema,
            String PolNo,
            LCPolSchema inLCPolSchema)
    {
        if ((pLMDutyPaySchema == null) || (pLCPremSchema == null)
            || (PolNo == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //��ѯ������
        LCPolSchema tLCPolSchema = inLCPolSchema;
        if (tLCPolSchema == null)
        { //ȡĬ��ֵ

            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = pLMDutyPaySchema.getPayPlanCode();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * ���ɸ������Ϳͻ��ʻ���Ĺ�����
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @param inLCPolSchema LCPolSchema
     * @return LCGetToAccSet
     */
    public LCGetToAccSet getGetToAccForBat(String PolNo, String AccCreatePos,
                                           Double Rate
                                           , LCPolSchema inLCPolSchema)
    {
        //1-ȡ����ȡ���
        LCGetSet tLCGetSet = new LCGetSet();
        tLCGetSet = queryLCGet(PolNo);
        if (tLCGetSet == null)
        {
            return null;
        }

        //2-������ȡ���ȡ����Ӧ�����θ���������
        //LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        VData tVData = new VData();
        tVData = createLMDutyGet(tLCGetSet);
        if (tVData == null)
        {
            return null;
        }

        //3-���ɸ������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = createGetToAccForBat(tVData, PolNo, AccCreatePos, Rate,
                                              inLCPolSchema);

        return tLCGetToAccSet;
    }


    /**
     * ���ɸ������Ϳͻ��˻���Ĺ�����
     * @param pVData VData
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @param inLCPolSchema LCPolSchema
     * @return LCGetToAccSet
     */
    public LCGetToAccSet createGetToAccForBat(VData pVData, String PolNo,
                                              String AccCreatePos
                                              , Double Rate,
                                              LCPolSchema inLCPolSchema)
    {
        if ((pVData == null) || (AccCreatePos == null) || (PolNo == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createGetToAcc";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LMDutyGetSet tLMDutyGetSet = (LMDutyGetSet) pVData.
                                     getObjectByObjectName("LMDutyGetSet", 0);
        LCGetSet tLCGetSet = (LCGetSet) pVData.getObjectByObjectName("LCGetSet",
                0);
        LCPolSchema tLCPolSchema = inLCPolSchema;
        if (tLCPolSchema == null)
        {
            return null;
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //���θ���
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet(); //���θ���
        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema(); //���ֱ����ʻ�����
        LCGetToAccSchema tLCGetToAccSchema = new LCGetToAccSchema(); //�������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet(); //�������Ϳͻ��˻���Ĺ�����

        for (int i = 1; i <= tLMDutyGetSet.size(); i++)
        {
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = tLMDutyGetSet.get(i);

            //�ж��Ƿ���ʻ����
            if (tLMDutyGetSchema.getNeedAcc().equals("1"))
            {
                //��ѯ���ֱ����ʻ�������
                tLMRiskAccGetSet = new LMRiskAccGetSet();
                tLMRiskAccGetSet = queryLMRiskAccGet(tLCPolSchema.getRiskCode()
                        , tLMDutyGetSchema.getGetDutyCode());
                if (tLMRiskAccGetSet == null)
                {
                    continue;
                }
                for (int n = 1; n <= tLMRiskAccGetSet.size(); n++)
                {
                    tLMRiskAccGetSchema = new LMRiskAccGetSchema();
                    tLMRiskAccGetSchema = tLMRiskAccGetSet.get(n);
                    if (tLMRiskAccGetSchema.getDealDirection().equals("0")
                        &&
                        tLMRiskAccGetSchema.getAccCreatePos().equals(
                            AccCreatePos))
                    {
                        tLCGetToAccSchema = new LCGetToAccSchema();

                        //�ж��Ƿ���Ҫ¼��
                        if (tLMRiskAccGetSchema.getNeedInput().equals("1"))
                        {
                            if (Rate == null)
                            {
                                // @@������
                                CError tError = new CError();
                                tError.moduleName = "DealAccount";
                                tError.functionName = "createGetToAcc";
                                tError.errorMessage = "������Ҫ�ӽ���¼�룬����Ϊ��!";
                                this.mErrors.addOneError(tError);

                                return null;
                            }
                            tLCGetToAccSchema.setDefaultRate(Rate.doubleValue());
                        }
                        else
                        {
                            tLCGetToAccSchema.setDefaultRate(
                                    tLMRiskAccGetSchema
                                    .getDefaultRate());
                        }

                        tLCGetToAccSchema.setNeedInput(tLMRiskAccGetSchema
                                .getNeedInput());
                        //  tLCGetToAccSchema.setPolNo(PolNo);
                        tLCGetToAccSchema.setPolNo(inLCPolSchema.getPolNo());
                        tLCGetToAccSchema.setDutyCode(tLCGetSet.get(i)
                                .getDutyCode());
                        tLCGetToAccSchema.setGetDutyCode(tLMRiskAccGetSchema
                                .getGetDutyCode());
                        tLCGetToAccSchema.setInsuAccNo(tLMRiskAccGetSchema
                                .getInsuAccNo());
                        tLCGetToAccSchema.setCalCodeMoney(tLMRiskAccGetSchema
                                .getCalCodeMoney());
                        tLCGetToAccSchema.setDealDirection(tLMRiskAccGetSchema
                                .getDealDirection());
                        tLCGetToAccSchema.setCalFlag(tLMRiskAccGetSchema
                                .getCalFlag());
                        tLCGetToAccSchema.setOperator(this.mGlobalInput.
                                Operator);
                        tLCGetToAccSchema.setMakeDate(CurrentDate);
                        tLCGetToAccSchema.setMakeTime(CurrentTime);
                        tLCGetToAccSchema.setModifyDate(CurrentDate);
                        tLCGetToAccSchema.setModifyTime(CurrentTime);
                        tLCGetToAccSet.add(tLCGetToAccSchema);
                    }
                }
            }
        }

        if (tLCGetToAccSet.size() == 0)
        {
            // @@������
            return null;
        }

        return tLCGetToAccSet;
    }


    /**
     *** ���µķ������ǽ�����ϵͳ���ӵķ���
     * * AUTHOR: GUOXIANG
     * * DATE: 2004-08-24
     * *************************************************************************************
     *
     * ���ɱ����ʻ������ɽṹSET����:
     * ���������˻���, ����getLCInsureAccForHealth(....)
     * �����������Ϳͻ��˻���Ĺ�����,����getPremToAccForBat(....)
     * �����������Ϳͻ��˻���Ĺ�����,����getGetToAccForBat(....)
     * @param parmData TransferData
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return VData
     */
    public VData createInsureAccForHealth(TransferData parmData,
                                          LCPolSchema inLCPolSchema,
                                          LMRiskSchema inLMRiskSchema)
    {
        //1-����
        if (!checkTransferData(parmData))
        {
            return null;
        }

        if ((inLCPolSchema == null) || (inLMRiskSchema == null))
        {
            return null;
        }

        //2-�õ����ݺ���
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("����:" + tRate);

        //3-���������˻���
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAccForHealth(tPolNo, tAccCreatePos,
                                                  tOtherNo, tOtherNoType,
                                                  inLCPolSchema, inLMRiskSchema);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-�����������Ϳͻ��˻���Ĺ�����
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAccForBat(tPolNo, tAccCreatePos, tRate,
                                             inLCPolSchema);

        //if(tLCPremToAccSet==null) return null;
        //5-�����������Ϳͻ��˻���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAccForBat(tPolNo, tAccCreatePos, tRate,
                                           inLCPolSchema);

        //if(tLCGetToAccSet==null) return null;
        //6-��������
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //������null
        tVData.add(tLCGetToAccSet); //������null

        return tVData;
    }


    /**
     * �����ղ��ֶԸ��˱������ɱ����ʻ��������    (���� 1�����ʻ�,����Ҫ����������¼)
     * ԭ������ʽ�ǣ��Ѹ����ֵ������˻�ȫ��������Ȼ�����ݲ��뵽�����˻���
     * ���д���ʽ�ǣ������ǰ������˻���Ҫ���뵽�����˻������ݱ������ĽɷѼƻ�����
     * �͸������ĸ������α�����Ӧ���˻���Ȼ�����ݲ��뵽�����˻���
     * ������ڵ��˻��������Ϊ��
     * 1-У��
     * 2-�ж��Ƿ����ʻ����
     * 3-����Ͷ������ѯlcprem��(�������)��lcget��������������ݿ�����Ͷ�����ţ�
     * 5-����Ͷ�������е������ֶβ�ѯLMRisk��
     * 6-�ж��Ƿ����ʻ����
     * 7-�ж��Ƿ����ʻ����
     * @param PolNo String ������
     * @param AccCreatePos String ����λ�� :1-ǩ��ʱ�������б��� 2���ɷ�ʱ���� 3����ȡʱ����
     * @param OtherNo String �����Ż򽻷Ѻ�
     * @param OtherNoType String �����Ż򽻷Ѻ�
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForHealth(String PolNo,
                                                  String AccCreatePos,
                                                  String OtherNo
                                                  , String OtherNoType,
                                                  LCPolSchema inLCPolSchema,
                                                  LMRiskSchema inLMRiskSchema)
    {
        //1-У��
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (inLCPolSchema == null)
            || (inLMRiskSchema == null))
        {
            // @@������
            buildError("getLCInsureAccForHealth", "�������ݲ���Ϊ��!");

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���

        //2-�ж��Ƿ����ʻ����
        if (inLMRiskSchema.getInsuAccFlag().equals("Y")
            || inLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //3����Ͷ������ѯlcprem��(�������)��lcget���������
            LCPremSet tLCPremSet = new LCPremSet();
            tLCPremSet = queryLCPrem(PolNo);
            if (tLCPremSet == null)
            {
                tLCPremSet = new LCPremSet();

            }

            LCGetSet tLCGetSet = new LCGetSet();
            tLCGetSet = queryLCGet(PolNo);
            if (tLCGetSet == null)
            {
                tLCGetSet = new LCGetSet();

            }

            LCPremSchema tLCPremSchema = new LCPremSchema(); //�������
            LCGetSchema tLCGetSchema = new LCGetSchema(); //�������

            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
            LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
            LMRiskAccPaySet tLMRiskAccPaySet = null; //���ֽɷѱ�
            LMRiskAccGetSet tLMRiskAccGetSet = null; //���ָ�����
            for (int i = 1; i <= tLCPremSet.size(); i++)
            //�������---���ֽ����˻�����---�����˻�����
            {
                tLCPremSchema = tLCPremSet.get(i);

                //�ж��Ƿ���ʻ����
                if (tLCPremSchema.getNeedAcc().equals("1"))
                {
                    LMRiskAccPaySchema tLMRiskAccPaySchema = new
                            LMRiskAccPaySchema();
                    tLMRiskAccPaySchema = queryLMRiskAccPay(tLCPremSchema,
                            inLMRiskSchema
                            .getRiskCode());
                    if (tLMRiskAccPaySchema == null)
                    {
                        continue;
                    }

                    LMRiskInsuAccSchema tLMRiskInsuAccSchema = new
                            LMRiskInsuAccSchema();

                    tLMRiskInsuAccSchema = queryLMRiskInsuAcc(
                            tLMRiskAccPaySchema);

                    if (tLMRiskInsuAccSchema == null)
                    {
                        continue;
                    }

                    tLMRiskInsuAccSet.add(tLMRiskInsuAccSchema);
                }
            }

            for (int i = 1; i <= tLCGetSet.size(); i++)
            //�������---���ָ����˻�����---�����˻����ϣ��������ɷ���ĵõ��˻����ϣ�
            {
                tLCGetSchema = tLCGetSet.get(i);

                //�ж��Ƿ���ʻ����
                if (tLCGetSchema.getNeedAcc().equals("1"))
                {
                    LMRiskAccGetSchema tLMRiskAccGetSchema = new
                            LMRiskAccGetSchema();

                    LMRiskInsuAccSchema tLMRiskInsuAccSchema = new
                            LMRiskInsuAccSchema();

                    tLMRiskAccGetSchema = queryLMRiskAccGet(tLCGetSchema,
                            inLMRiskSchema
                            .getRiskCode());

                    if (tLMRiskAccGetSchema == null)
                    {
                        continue;
                    }

                    boolean continueFlag = false;
                    for (int j = 1; j <= tLMRiskInsuAccSet.size(); j++)
                    {
                        LMRiskInsuAccSchema pLMRiskInsuAccSchema =
                                tLMRiskInsuAccSet
                                .get(j);

                        if (tLMRiskAccGetSchema.getInsuAccNo().equals(
                                pLMRiskInsuAccSchema
                                .getInsuAccNo()))
                        {
                            continueFlag = true;

                            break;
                        }
                    }
                    if (continueFlag)
                    {
                        continue;
                    }

                    tLMRiskInsuAccSchema = queryLMRiskInsuAcc(
                            tLMRiskAccGetSchema);

                    if (tLMRiskInsuAccSchema == null)
                    {
                        continue;
                    }
                    tLMRiskInsuAccSet.add(tLMRiskInsuAccSchema);
                }
            }

            //ѭ������
            for (int k = 1; k <= tLMRiskInsuAccSet.size(); k++)
            {
                //�������˻���---���ɱ����˻���
                LMRiskInsuAccSchema tLMRiskInsuAccSchema = tLMRiskInsuAccSet
                        .get(k); //�����ʻ���

                //����˻�����λ���ҵ�ƥ��ı����˻�
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema
                            .getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(inLMRiskSchema.getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema
                                                  .getAccType());
                    // tLCInsureAccSchema.setOtherNo(OtherNo);
                    // tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(inLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(inLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setGrpContNo(inLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setInsuredNo(inLCPolSchema.getInsuredNo());
                    //                 tLCInsureAccSchema.setAppntName(inLCPolSchema.getAppntName());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema
                            .getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(inLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(inLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(inLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * ������ϵͳע���ʽ�ȡ�ù���ѱ����ӱ��������ȡ
     * �޸�calInputMoney() Ϊ calInputMoneyHealth()
     * �����˻��ʽ�ע��(����3 ��Ա�����,ע��û�и���ע���ʽ��ڲ�����ü�����ĺ���)
     * �����ڣ��������ʻ��ṹ�󣬴�ʱ������δ�ύ�����ݿ⣬����Ҫִ���ʻ����ʽ�ע�롣
     * ����ʹ���� createInsureAccHealth()�����󣬵õ�VData���ݣ������޸�VData���ʻ��Ľ��
     * @param inVData VData ʹ���� createInsureAcc()�����󣬵õ���VData����
     * @param pLCPremSet LCPremSet �������
     * @param AccCreatePos String �μ� ���ֱ����ʻ��ɷ� LMRiskAccPay
     * @param OtherNo String �μ� �����ʻ��� LCInsureAcc
     * @param OtherNoType String ��������
     * @param MoneyType String �μ� �����ʻ���Ǽ������� LCInsureAccTrace
     * @param RiskCode String ���ֱ���
     * @param Rate String ����
     * @return VData
     */
    public VData addPremInnerHealth(VData inVData, LCPremSet pLCPremSet,
                                    String AccCreatePos
                                    , String OtherNo, String OtherNoType,
                                    String MoneyType, String RiskCode,
                                    String Rate)
    {
        if ((inVData == null) || (pLCPremSet == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null) || (RiskCode == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();

        //�õ����ɵı����ʻ���
        LCInsureAccSet tLCInsureAccSet = (LCInsureAccSet) (inVData
                .getObjectByObjectName("LCInsureAccSet",
                                       0));

        //�õ����ɵĽɷ��ʻ�������
        LCPremToAccSet tLCPremToAccSet = (LCPremToAccSet) (inVData
                .getObjectByObjectName("LCPremToAccSet",
                                       0));

        //�õ���ȡ�ʻ�������--Ŀǰ����
        LCGetToAccSet tLCGetToAccSet = (LCGetToAccSet) (inVData
                .getObjectByObjectName("LCGetToAccSet",
                                       0));

        if (tLCInsureAccSet == null)
        {
            tLCInsureAccSet = new LCInsureAccSet();
        }
        if (tLCPremToAccSet == null)
        {
            tLCPremToAccSet = new LCPremToAccSet();
        }
        if (tLCGetToAccSet == null)
        {
            tLCGetToAccSet = new LCGetToAccSet();
        }

        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();

        double inputMoney = 0;
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            LCPremSchema tLCPremSchema = pLCPremSet.get(n);

            //�ж��Ƿ��ʻ����
            if (tLCPremSchema.getNeedAcc().equals("1"))
            {
                for (int m = 1; m <= tLCPremToAccSet.size(); m++)
                {
                    LCPremToAccSchema tLCPremToAccSchema = tLCPremToAccSet.get(
                            m);

                    //�����ǰ������͵�ǰ�Ľɷ��ʻ�������ı����ţ����α��룬���Ѽƻ�������ͬ
                    if (tLCPremSchema.getPolNo().equals(tLCPremToAccSchema
                            .getPolNo())
                        &&
                        tLCPremSchema.getDutyCode().equals(tLCPremToAccSchema
                            .getDutyCode())
                        &&
                        tLCPremSchema.getPayPlanCode().equals(
                            tLCPremToAccSchema
                            .getPayPlanCode()))
                    {
                        //������Ҫע����ʽ�
                        inputMoney = calInputMoneyHealth(tLCPremToAccSchema,
                                tLCPremSchema);
                        if (inputMoney == -1)
                        {
                            // @@������
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "addPrem";
                            tError.errorMessage = "����ʵ��Ӧ��ע����ʽ����";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        for (int j = 1; j <= tLCInsureAccSet.size(); j++)
                        {
                            //�����ǰ�ɷ��ʻ�������ı����ţ��˻��ź͵�ǰ���˻���ı����ţ��˻�����ͬ�����ʽ�Ϊ0�����ʽ�ע��
                            LCInsureAccSchema tLCInsureAccSchema =
                                    tLCInsureAccSet
                                    .get(j);
                            if (tLCPremToAccSchema.getPolNo().equals(
                                    tLCInsureAccSchema
                                    .getPolNo())
                                &&
                                tLCPremToAccSchema.getInsuAccNo().equals(
                                    tLCInsureAccSchema
                                    .getInsuAccNo())
                                && (inputMoney != 0))
                            {
                                //�޸ı����ʻ����
                                tLCInsureAccSchema.setInsuAccBala(
                                        tLCInsureAccSchema
                                        .getInsuAccBala()
                                        + inputMoney);
                                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema
                                        .getSumPay()
                                        + inputMoney);

                                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                                tLCInsureAccSet.set(j, tLCInsureAccSchema);

                                //��ѯ���ֱ����ʻ��ɷ�
                                LMRiskAccPaySchema tLMRiskAccPaySchema =
                                        queryLMRiskAccPay3(
                                        RiskCode,
                                        tLCPremToAccSchema);
                                if (tLMRiskAccPaySchema == null)
                                {
                                    return null;
                                }
                                if (tLMRiskAccPaySchema.getPayNeedToAcc()
                                    .equals("1"))
                                {
                                    //��䱣���ʻ���Ǽ�������
                                    tLimit = PubFun.getNoLimit(tLCPremSchema
                                            .getManageCom());
                                    serNo = PubFun1.CreateMaxNo("SERIALNO",
                                            tLimit);
                                    tLCInsureAccTraceSchema = new
                                            LCInsureAccTraceSchema();
                                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                                    tLCInsureAccTraceSchema.setInsuredNo(
//                                            tLCInsureAccSchema
//                                            .getInsuredNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema
                                            .getPolNo());
                                    tLCInsureAccTraceSchema.setMoneyType(
                                            MoneyType);
                                    tLCInsureAccTraceSchema.setRiskCode(
                                            tLCInsureAccSchema
                                            .getRiskCode());
                                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                                    tLCInsureAccTraceSchema.setOtherType(
                                            OtherNoType);
                                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                                    tLCInsureAccTraceSchema.setContNo(
                                            tLCInsureAccSchema
                                            .getContNo());
                                    tLCInsureAccTraceSchema.setGrpPolNo(
                                            tLCInsureAccSchema
                                            .getGrpPolNo());
                                    tLCInsureAccTraceSchema.setInsuAccNo(
                                            tLCInsureAccSchema
                                            .getInsuAccNo());
                                    /*Lis5.3 upgrade set
                                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                     .getAppntName());
                                     */
                                    tLCInsureAccTraceSchema.setState(
                                            tLCInsureAccSchema
                                            .getState());
                                    tLCInsureAccTraceSchema.setManageCom(
                                            tLCInsureAccSchema
                                            .getManageCom());
                                    tLCInsureAccTraceSchema.setOperator(
                                            tLCInsureAccSchema
                                            .getOperator());
                                    tLCInsureAccTraceSchema.setMakeDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setMakeTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setModifyDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setModifyTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setPayDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSet.add(
                                            tLCInsureAccTraceSchema);
                                }

                                break;
                            }
                        }
                    }
                }
            }
        }

        inVData.clear();
        inVData.add(tLCInsureAccSet);
        inVData.add(tLCPremToAccSet);
        inVData.add(tLCGetToAccSet);

        //����ʻ�ע���ʽ�켣
        inVData.add(tLCInsureAccTraceSet);

        //�������ݿ�ʱִ�в������
        return inVData; //(LCInsureAccSet,LCPremToAccSet,LCGetToAccSet,LCInsureAccTraceSet)
    }


    /**
     * �����˻��ʽ�ע��(����1 ��Ա�����,ע��û�и���ע���ʽ��ڲ�����ü�����ĺ���)
     * @param pLCPremSchema ������
     * @param AccCreatePos  �μ� ���ֱ����ʻ��ɷ� LMRiskAccPay
     * @param OtherNo  �μ� �����ʻ��� LCInsureAcc
     * @param OtherNoType  ��������
     * @param MoneyType  �μ� �����ʻ���Ǽ������� LCInsureAccTrace
     * @param Rate ����
     * @return VData(tLCInsureAccSet:update or insert ,tLCInsureAccTraceSet: insert)
     * @author guoxiang
     * @data 2004-9-2 10:14
     */
    public VData addPremHealth(LCPremSchema pLCPremSchema, String AccCreatePos,
                               String OtherNo, String OtherNoType,
                               String MoneyType,
                               Double Rate)
    {
        if ((pLCPremSchema == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //�����ʻ���
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //�����ʻ���Ǽ�������
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //�������Ϳͻ��ʻ���Ĺ�����
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        String newFlag = "";
        boolean addPrem = false;
        double inputMoney = 0;

        //�ж��Ƿ��ʻ����
        if (pLCPremSchema.getNeedAcc().equals("1"))
        {
            tLCPremToAccSet = queryLCPremToAccSet(pLCPremSchema);
            if (tLCPremToAccSet == null)
            {
                return null;
            }

            TransferData tFData = new TransferData();
            LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

            //�ж�����λ���Ƿ�ƥ��
            if (AccCreatePos.equals(tLCPremToAccSet.get(1).getNewFlag()))
            {
                //���ƥ�䣺�����ʻ�(������ÿ�ν��Ѷ��������˺ŵ�������ο�LCInsureAcc-�����ʻ���)
                tFData = new TransferData();
                tFData.setNameAndValue("PolNo", pLCPremSchema.getPolNo());
                tFData.setNameAndValue("OtherNo", OtherNo); //����ÿ�ν��Ѷ��������˺ŵ���������ֶδ�Ž��Ѻš�����
                tFData.setNameAndValue("OtherNoType", OtherNoType);
                tFData.setNameAndValue("Rate", Rate);
                tLCInsureAccSet = new LCInsureAccSet();
                mLCInsureAccSet = getLCInsureAcc(pLCPremSchema.getPolNo(),
                                                 AccCreatePos, OtherNo,
                                                 OtherNoType);
                if (mLCInsureAccSet == null)
                {
                    return null;
                }
                newFlag = "INSERT";
            }

            for (int i = 1; i <= tLCPremToAccSet.size(); i++)
            {
                tLCPremToAccSchema = new LCPremToAccSchema();
                tLCPremToAccSchema = tLCPremToAccSet.get(i);

                //����ʵ��Ӧ��ע����ʽ�
                inputMoney = calInputMoneyHealth(tLCPremToAccSchema,
                                                 pLCPremSchema);
                if (inputMoney == -1)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "DealAccount";
                    tError.functionName = "addPrem";
                    tError.errorMessage = "����ʵ��Ӧ��ע����ʽ����";
                    this.mErrors.addOneError(tError);

                    return null;
                }
                if (newFlag.equals("INSERT"))
                { //������������ʻ�
                    //���ݱ����źͱ����˻��ź����������ѯmLCInsureAccSet������Ψһһ������
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAccSet(pLCPremSchema
                            .getPolNo(),
                            tLCPremToAccSchema
                            .getInsuAccNo(),
                            OtherNo,
                            mLCInsureAccSet);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }
                else
                {
                    //���ݱ����źͱ����˻��ź����������ѯLCInsureAcc���Ψһһ������
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAcc(pLCPremSchema
                            .getPolNo(),
                            tLCPremToAccSchema
                            .getInsuAccNo(),
                            OtherNo);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }

                //�޸ı����ʻ����
                tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema
                                                  .getInsuAccBala()
                                                  + inputMoney);
                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay()
                                             + inputMoney);
                tLCInsureAccSchema.setModifyDate(CurrentDate);
                tLCInsureAccSchema.setModifyTime(CurrentTime);

                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                tLMRiskAccPaySchema = queryLMRiskAccPay2(tLCPremToAccSchema); //��ѯ���ֱ����ʻ��ɷ�
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }
                if (tLMRiskAccPaySchema.getPayNeedToAcc().equals("1")
                    && (inputMoney != 0))
                {
                    //��䱣���ʻ���Ǽ�������
                    tLimit = PubFun.getNoLimit(pLCPremSchema.getManageCom());
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    /*Lis5.3 upgrade set
                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                                         .getAppntName());
                     */
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }

                //�������
                tLCInsureAccSet.add(tLCInsureAccSchema);
            }
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@������
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="���������ϣ�û�����ɼ�¼";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //����ڲ���VDataʱ��������tLCInsureAccSet������update or insert��
        //��˲������ݿ�ʱ��ִ��ɾ����������ִ�в������
    }


    /**
     * ������ϵͳ�Ĺ���ѱ���ȡ�Ա������
     * �޸ģ�tCalculator.addBasicFactor("ManageFeeRate",
     *       String.valueOf(tLCPolDB.getManageFeeRate())); //����ѱ���-�μ��������ֺ�-�������601304
     * Ϊ��
     * ����ʵ��Ӧ��ע����ʽ�(����Ӷ�����,�������ݿ��ڵļ��������δ����)
     * @param tLCPremToAccSchema LCPremToAccSchema ���뱣�����Ϳͻ��ʻ���Ĺ������¼
     * @param tLCPremSchema LCPremSchema
     * @return double ʵ��Ӧ��ע����ʽ�
     */
    public double calInputMoneyHealth(LCPremToAccSchema tLCPremToAccSchema
                                      , LCPremSchema tLCPremSchema)
    {
        // @@������
        if (tLCPremToAccSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "calInputMoneyRate";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return -1;
        }

        double Prem = tLCPremSchema.getPrem();
        /*Lis5.3 upgrade get
                 double ManageFeeRate = tLCPremSchema.getManageFeeRate();
         */
        double ManageFeeRate = 0;
        String[] F = new String[5];
        int m = 0;
        double defaultRate = 0;
        double inputMoney = 0;
        String calMoney = "";
        defaultRate = tLCPremToAccSchema.getRate(); //ȱʡ����

        Calculator tCalculator = new Calculator(); //������

        if (tLCPremToAccSchema.getCalFlag() == null)
        { //����ñ��Ϊ��
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }

        //�˻�ת������־:0 ���� ��ȫת���˻�
        // 1 ���� ���ֽ����ת���˻�
        // 2 ���� ���ɷݼ���ת���˻�
        // 3 ���� �����ֽ�Ȼ�󰴹ɷݼ��㡣(δ��)
        if (tLCPremToAccSchema.getCalFlag().equals("0"))
        {
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("1"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "δ�ҵ�ת���˻�ʱ���㷨����(�ֽ�)!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeMoney()); //��Ӽ������

            tCalculator.addBasicFactor("ManageFeeRate",
                                       String.valueOf(ManageFeeRate));

            //����ѱ���-�μ��������ֺ�-�������601304
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));

            //����Ҫ�ؿɺ������
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "����ע���ʻ��ʽ�ʧ��!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("2"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "δ�ҵ�ת���˻�ʱ���㷨����(�ɷ�)";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeUnit()); //��Ӽ������

            //��Ӽ����Ҫ����������
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "����ע���ʻ��ʽ�ʧ��!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }

        return 0;
    }


    /**
     * ���ݽɷ����¼�����ֱ���
     * ��ѯ���ֱ����ʻ��ɷѱ�
     * ��ΪֻҪ��֤һ���ɷѼ�¼
     * @param pLCPremSchema LCPremSchema
     * @param riskcode String
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay(LCPremSchema pLCPremSchema,
                                                String riskcode)
    {
        if ((pLCPremSchema == null) || (riskcode == null))
        {
            // @@������
            buildError("queryLMRiskAccPay", "�������ݲ���Ϊ�գ�");

            return null;
        }

        String payPlanCode = pLCPremSchema.getPayPlanCode();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskcode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            buildError("queryLMRiskAccPay", "���ֱ����ʻ��ɷѱ��ѯʧ�ܣ�");

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            buildError("queryLMRiskAccPay", "���ֱ����ʻ��ɷѱ�û�в�ѯ��������ݣ�");
            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ�������
     * @param pLCGetSchema LCGetSchema
     * @param riskcode String
     * @return LMRiskAccGetSchema
     */
    public LMRiskAccGetSchema queryLMRiskAccGet(LCGetSchema pLCGetSchema,
                                                String riskcode)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccGet where GetDutyCode='");
        tSBql.append(pLCGetSchema.getGetDutyCode());
        tSBql.append("' and RiskCode='");
        tSBql.append(riskcode);
        tSBql.append("'");

        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema();
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet();
        LMRiskAccGetDB tLMRiskAccGetDB = tLMRiskAccGetSchema.getDB();
        tLMRiskAccGetSet = tLMRiskAccGetDB.executeQuery(tSBql.toString());

        if (tLMRiskAccGetDB.mErrors.needDealError())
        {
            // @@������
            buildError("queryLMRiskAccGet", "���ֱ����ʻ��������ѯʧ�ܣ�");

            return null;
        }
        if (tLMRiskAccGetSet.size() == 0)
        {
            // @@������
            buildError("queryLMRiskAccGet", "���ֱ����ʻ�������û�в�ѯ��������ݣ�");
            return null;
        }

        return tLMRiskAccGetSet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ�(����)
     * @param tLMRiskAccPaySchema LMRiskAccPaySchema
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(LMRiskAccPaySchema
                                                  tLMRiskAccPaySchema)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(tLMRiskAccPaySchema.getInsuAccNo());
        tSBql.append("'");

        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@������
            buildError("queryLMRiskInsuAcc", "���ֱ����ʻ����ѯʧ�ܣ�");

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@������
            buildError("queryLMRiskInsuAcc", "���ֱ����ʻ���û�в�ѯ��������ݣ�");

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * ��ѯ���ֱ����ʻ�(����)
     * @param tLMRiskAccGetSchema LMRiskAccGetSchema
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(LMRiskAccGetSchema
                                                  tLMRiskAccGetSchema)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(tLMRiskAccGetSchema.getInsuAccNo());
        tSBql.append("'");

        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@������
            buildError("queryLMRiskInsuAcc", "���ֱ����ʻ����ѯʧ�ܣ�");

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@������
            buildError("queryLMRiskInsuAcc", "���ֱ����ʻ���û�в�ѯ��������ݣ�");

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "DealAccount";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }


    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�
     * @param payPlanCode String
     * @param riskCode String
     * @return LMRiskAccPaySet
     */
    private LMRiskAccPaySet queryLMRiskAccPayForBat(String payPlanCode,
            String riskCode)
    {
        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet == null || tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet;
    }


    /**
     * ��ѯ���ֱ����ʻ�(����1)
     * @param InsuAccNo String
     * @return LMRiskInsuAccSet
     */
    private LMRiskInsuAccSet queryLMRiskInsuAccSet(String InsuAccNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ����ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "���ֱ����ʻ���û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet;
    }


    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
        DealAccount dealAccount1 = new DealAccount();
        String PolNo = "86110020020210000217";
        String AccCreatePos = "2"; //�����ɷ�ʱ����
        String OtherNo = "86110020020210000217";
        String OtherNoType = "1"; //���� ���˱�����
        Double Rate = new Double(0.5);
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("PolNo", PolNo);
        tTransferData.setNameAndValue("AccCreatePos", AccCreatePos);
        tTransferData.setNameAndValue("OtherNo", OtherNo);
        tTransferData.setNameAndValue("OtherNoType", OtherNoType);
        tTransferData.setNameAndValue("Rate", Rate);

        VData tVData = new VData();

        dealAccount1.addPremTraceForAcc("86110020030220000068", "000003", 0.0);

        //���������ʻ�
        //tVData=dealAccount1.createInsureAcc(tTransferData);
        //�������ɱ����ʻ���
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = dealAccount1.getLCInsureAcc(PolNo, AccCreatePos,
                OtherNo, OtherNoType);

        //�������ɱ������Ϳͻ��ʻ���Ĺ�����
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = dealAccount1.getPremToAcc(PolNo, AccCreatePos, Rate);

        //���ɸ������Ϳͻ��ʻ���Ĺ�����
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = dealAccount1.getGetToAcc(PolNo, AccCreatePos, Rate);

        //���Ա����˻��ʽ�ע��
        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = dealAccount1.queryLCPrem(PolNo);
        tLCPremSchema = tLCPremSet.get(1);
        tVData.clear();
        tVData = dealAccount1.addPrem(tLCPremSchema, AccCreatePos, OtherNo,
                                      OtherNoType, "BF", Rate);
    }
}
