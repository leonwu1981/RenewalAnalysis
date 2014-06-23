/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCBDutyDB;
import com.sinosoft.lis.db.LCBGetDB;
import com.sinosoft.lis.db.LCBPremDB;
import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCDutyDB;
import com.sinosoft.lis.db.LCGILTraceDB;
import com.sinosoft.lis.db.LCGetDB;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LCPremDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCGILTraceSchema;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.vschema.LCBDutySet;
import com.sinosoft.lis.vschema.LCBGetSet;
import com.sinosoft.lis.vschema.LCBPremSet;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCDutySet;
import com.sinosoft.lis.vschema.LCGILTraceSet;
import com.sinosoft.lis.vschema.LCGetSet;
import com.sinosoft.lis.vschema.LCPremSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: ������������:��������Լ�˱����� </p>
 * <p>Description:����˱�����������AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpUWModifyAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private Reflections mReflections = new Reflections();

    /** ҵ�������� */
    private LCContSet mLCContSet = new LCContSet();
//    private LCPolSet mLCPolSet = new LCPolSet();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mMissionID;
    private String mGrpContNo;
    private String mPrtNo;
    
    private MMap map = new MMap();

    public GrpUWModifyAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //У���Ƿ���δ��ӡ�����֪ͨ��
        if (!checkData())
        {
            return false;
        }

        System.out.println("Start  dealData...");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //Ϊ��������һ�ڵ������ֶ�׼������
        if (!prepareTransferData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        return true;
    }


    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {
        //У�鱣����Ϣ
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "���屣��" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);
        mPrtNo = mLCGrpContSchema.getPrtNo();

        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setPrtNo(mPrtNo);
        mLCContSet = tLCContDB.query();
        if (mLCContSet == null || mLCContSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "���˺�ͬ������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
//        LCPolDB tLCPolDB = null;
//        LCUWSubDB tLCUWSubDB =null;
//        for(int i =1;i<mLCContSet.size();i++)
//        {
//            //��ѯ��ͬ�˱����������uwflag����5�ļ�¼����˵���Ժ˲�ͨ����û����˵���Ժ�ͨ��
//            //�˱�������ʹ��ָ��Ժ˺��״̬
//            String tSql = "select distinct 1 from LCCUWSub where 1=1 "
//                          + " and ContNo = '"+mLCContSet.get(i).getContNo() + "'"
//                          + " and uwflag = '5'"
//                          ;
//            ExeSQL lccuw = new ExeSQL();
//            String rs = lccuw.getOneValue(tSql);
//            if(rs.equals("1"))
//            {
//                //�˷�֧��ʾ�Ժ�δͨ���ı���
//                mLCContSet.get(i).setUWFlag("5");
//                tLCPolDB = new LCPolDB();
//                tLCPolDB.setContNo(mLCContSet.get(i).getContNo());
//                mLCPolSet = tLCPolDB.query();
//                if(mLCPolSet==null||mLCPolSet.size()<=0)
//                {
//                    // @@������
//                    //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
//                    CError tError = new CError();
//                    tError.moduleName = "GrpUWModifyAfterInitService";
//                    tError.functionName = "dealData";
//                    tError.errorMessage = "��ѯ���ֱ�����ʧ��!";
//                    this.mErrors.addOneError(tError);
//                    return false;
//                }
//                for(int j=1;j<=mLCPolSet.size();j++)
//                {
//                    String tPolSql = "select distinct 1 from LCUWSub where 1=1 "
//                          + " and PolNo = '"+mLCPolSet.get(j).getPolNo() + "'"
//                          + " and uwflag = '5'"
//                          ;
//                  ExeSQL lcuw = new ExeSQL();
//                  String rsc = lcuw.getOneValue(tSql);
//                  if (rsc.equals("1"))
//                    {
//                        mLCPolSet.get(j).setUWFlag("5");
//                    }
//                }
//            }
//        }
    	String mGrpContSql = "update lcgrpcont set uwflag= 'z' where grpcontno = '" + mGrpContNo + "'";

    	String mGrpPolSql = " update lcgrppol set uwflag = 'z' where grpcontno = '" + mGrpContNo + "'";

    	String mContSql = "update lccont set uwflag = 'z' where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mPolSql = "update lcpol set uwflag = 'z' where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCCUWMasterSql = "update lccuwmaster set passflag = 'z',uwno=uwno+1 where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCCUWSubSql = "insert into lccuwsub select * from lccuwmaster where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5') ";

    	String mCUWMasterSql = "update lcuwmaster set passflag = 'z',uwno=uwno+1 where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCUWSubSql = "insert into lcuwsub select * from lcuwmaster where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5') and passflag = 'z'";

    	String mGCUWMasterSql = "update lcgcuwmaster set passflag = 'z',uwno=uwno+1 where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGCUWSubSql = "insert into lcgcuwsub select * from lcgcuwmaster where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGUWMasterSql = "update lcguwmaster set passflag = 'z',uwno=uwno+1 where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGUWSubSql = "insert into lcguwsub select * from lcguwmaster where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";
    	
    	//��ԭ�˹��˱��׶δ����GIL��Ϣ
    	String gilLCPrem = " update lcprem a set "
    		+ " a.sumprem=(select sumprem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode), "
    		+ " a.prem=(select prem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode), "
    		+ " a.standprem=(select standprem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode) "
    		+ " where grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode) ";
    	String gilLCGet = "  update lcget a set "
    		+ " a.standmoney=(select standmoney from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode), "
    		+ " a.actuget=(select actuget from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode) "
    		+ " where grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode) ";
    	String gilLCDuty = " update lcduty a set "
    		+ " a.sumprem=(select sumprem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.prem=(select prem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.standprem=(select standprem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.amnt=(select amnt from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.subgil=(select subgil from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.riskamnt=(select riskamnt from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode) "
    		+ " where exists(select 1 from lcpol where grpcontno='"+mGrpContNo+"' and polno=a.polno) "
    		+ " and exists(select 1 from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode) ";
    	String gilLCPol = " update lcpol a set " 
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcduty where polno=a.polno), "
			+ " a.prem=(select nvl(sum(prem),0) from lcduty where polno=a.polno), "
			+ " a.standprem=(select nvl(sum(standprem),0) from lcduty where polno=a.polno), "
			+ " a.amnt=(select nvl(max(amnt),0) from lcduty where polno=a.polno), "
			+ " a.riskamnt=(select nvl(max(riskamnt),0) from lcduty where polno=a.polno), "
			+ " a.subgil =(select nvl(max(subgil),0) from lcduty where polno=a.polno) "
			+ " where a.grpcontno='"+mGrpContNo+"' "
			+ " and exists(select 1 from lcbduty where serialno='Q'  and polno=a.polno) ";
    	String gilLCCont =" update lccont a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where contno=a.contno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where contno=a.contno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where contno=a.contno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbduty where serialno='Q'  and contno = a.contno) ";
    	String gilLLCGrpPol = " update lcgrppol a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where grppolno=a.grppolno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grppolno=a.grppolno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grppolno=a.grppolno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' ";
    	String gilLCGrpCont =" update lcgrpcont a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where grpcontno=a.grpcontno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grpcontno=a.grpcontno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grpcontno=a.grpcontno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' ";

    	
    	 map = new MMap();
         map.put(mGrpContSql, "UPDATE");
         map.put(mGrpPolSql, "UPDATE");
         map.put(mContSql, "UPDATE");
         map.put(mPolSql, "UPDATE");
         map.put(mCCUWMasterSql, "UPDATE");
         map.put(mCUWMasterSql, "UPDATE");
         map.put(mGCUWMasterSql, "UPDATE");
         map.put(mGUWMasterSql, "UPDATE");
         map.put(mCCUWSubSql, "INSERT");
         map.put(mCUWSubSql, "INSERT");
         map.put(mGCUWSubSql, "INSERT");
         map.put(mGUWSubSql, "INSERT");
         
         map.put(gilLCPrem, "UPDATE");
         map.put(gilLCGet, "UPDATE");
         map.put(gilLCDuty, "UPDATE");
         map.put(gilLCPol, "UPDATE");
         map.put(gilLCCont, "UPDATE");
         map.put(gilLLCGrpPol, "UPDATE");
         map.put(gilLCGrpCont, "UPDATE");

        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());

        return true;
    }

    /**
     * ���ش����Ľ��
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���ع������е�Lwfieldmap��������ֵ
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    /**
     * ���ش������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}
