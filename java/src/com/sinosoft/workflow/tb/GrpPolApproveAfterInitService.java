/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.vschema.LCGrpPolSet;

/**
 * <p>Title: �������ŵ����˷����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */
/****************************************************************************************************************
 * Fang 2008-11-03 ���⸽��Ů�Լ����������弲������MGK04 ��Ʒ���� 
 * Change1��GILֵ��������MGK04
 * **************************************************************************************************************
 * Fang 2009-05-26 �����������嶨������NAK02 ��Ʒ���� 
 * Change2��GILֵ��������NAK02
 * **************************************************************************************************************
 * frost 2010-09-19 �Զ��˱�ʹ�ù���������� 
 * ȥ��GrpPolApproveAfterInitService���й���GIL��ش���GIL�ļ���ͨ������������ɣ�
 * �˹��˱�GIL�ĸ��˵���ɾ���Ƶ��µ��Զ��˱���������
 * **************************************************************************************************************
 * Fang 2011-04-19 
 * Change4����۳�ʼ���ñ������������ڻ���������ڶ�Ӧ�Ĺ����˻�
 * **************************************************************************************************************
 **/

public class GrpPolApproveAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** ���ݲ����ַ��� */
    private String mOperator;

    /** ҵ�����ݲ����ַ��� */
    private String mGrpContNo;
    private String mApproveFlag;
    private String mMissionID;
    private String mPolSql;
    private String mContSql;
    private String mGrpContSql;
    private String mGrpContSql2;
    private String mGrpPolSql;

    public GrpPolApproveAfterInitService()
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

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //Ϊ��������һ�ڵ������ֶ�׼������
        if (!prepareTransferData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData(cInputData))
        {
            return false;
        }

        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData(VData cInputData)
    {
        mResult.clear();
        MMap map = new MMap();

        //�޸����ֱ�����
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("update LCPol set ApproveCode = '");
        tSBql.append(mLCGrpContSchema.getApproveCode());
        tSBql.append("', ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(mApproveFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append(
                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'");
        mPolSql = tSBql.toString();

        //�޸ĺ�ͬ��
        tSBql = new StringBuffer(128);
        tSBql.append("update LCCont set ApproveCode = '");
        tSBql.append(mLCGrpContSchema.getApproveCode());
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(mApproveFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append(
                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'");
        mContSql = tSBql.toString();

        //�޸ļ����ͬ��
        tSBql = new StringBuffer(128);
        tSBql.append("update LCGrpCont set ApproveCode = '");
        tSBql.append(mLCGrpContSchema.getApproveCode());
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(mApproveFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("',Payintv = '");
        tSBql.append(mLCGrpContSchema.getPayIntv());
        tSBql.append("' where PrtNo = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append("' and appflag = '0' and approveflag <> '9'");
        mGrpContSql = tSBql.toString();

        tSBql = new StringBuffer(128);
        tSBql.append("update LCGrpPol set ApproveCode = '");
        tSBql.append(mLCGrpContSchema.getApproveCode());
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(mApproveFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("' where PrtNo = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append("' and appflag = '0' and approveflag <> '9'");
        mGrpPolSql = tSBql.toString();

        /**
         * �ŵ������޸ĺ󣬱����˸��ڸ��˴����룬�µ�¼�����ʱLCGrpCont���peoples�ֶα��ÿգ�������Ϻ��轫���ֶθ���
         * @return boolean
         */
             tSBql = new StringBuffer(128);
             tSBql.append(
                     "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("'),amnt=(select sum(amnt) from lccont where grpcontno = '");
             tSBql.append(mGrpContNo);
            // tSBql.append("'),Peoples = (select sum(Peoples) from lccont where grpcontno = '");
            // tSBql.append(mGrpContNo);
             tSBql.append("') where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("'");
             mGrpContSql2 = tSBql.toString();

    
        map.put(mPolSql, "UPDATE");
        map.put(mContSql, "UPDATE");
        map.put(mGrpContSql, "UPDATE");
        map.put(mGrpPolSql, "UPDATE");
        map.put(mGrpContSql2, "UPDATE");
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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
        if (tLCGrpPolSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "δ¼�뼯�屣����Ϣ��";
            this.mErrors.addOneError(tError);
            return false;
        }
        //У��������Ƿ��Ѿ�¼�뱻���� ������������ �ڴ� ����У��ע�ӵ� 2007.5.15 guoxq
//        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
//        {
//            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpPolApproveAfterInitService";
//                tError.functionName = "checkData";
//                tError.errorMessage = "����" + tLCGrpPolSet.get(i).getRiskCode() +
//                        "��δ¼�뱻���ˣ���ɾ�������ֻ�¼�뱻�����ˣ�";
//                this.mErrors.addOneError(tError);
//                return false;
//            }
//            ExeSQL tExeSQL = new ExeSQL();
//            String tSql = "select distinct 1 from lcpol where grpcontno = '" + mGrpContNo
//                    + "' and riskcode = '" + tLCGrpPolSet.get(i).getRiskCode() + "'";
//            String rs = tExeSQL.getOneValue(tSql);
//            if (rs == null || rs.length() == 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpFirstWorkFlowCheck";
//                tError.functionName = "checkData";
//                tError.errorMessage = "����������δ¼�����������Ϣ��";
//                this.mErrors.addOneError(tError);
//                return false;
//            }
//        }

        mLCGrpContSchema.setSchema(tLCGrpContDB);
        if (!mLCGrpContSchema.getAppFlag().trim().equals("0"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "�˼��嵥����Ͷ���������ܽ��и��˲���!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCGrpContSchema.getApproveFlag().equals("9"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "�˼���Ͷ���������Ѿ�ͨ���������ٽ��и���ͨ������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        ExeSQL tExeSQL = new ExeSQL();
        String sql = "select count(1) from LCCont where GrpContNo = '"
                + mLCGrpContSchema.getGrpContNo() + "'";

        String tStr = "";
        double tCount = -1;
        tStr = tExeSQL.getOneValue(sql);
        if (tStr.trim().equals(""))
        {
            tCount = 0;
        }
        else
        {
            tCount = Double.parseDouble(tStr);
        }

        if (tCount <= 0.0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "����Ͷ������û�и���Ͷ���������ܽ��и��˲���!";
            this.mErrors.addOneError(tError);
            return false;
        }
//      ���ӱ����������Ƿ��б������У��
	    String tSql = "select a.insuredname||' ���� '||a.riskcode from lcpol a where a.grpcontno='" + mGrpContNo
	                + "' and not exists (select 1 from lcprem where polno=a.polno) ";
	    String rs = tExeSQL.getOneValue(tSql);
	    if (!"".equals(rs) && rs != null){
	    	CError tError = new CError();
          tError.moduleName = "GrpFirstWorkFlowCheck";
          tError.functionName = "checkData";
          tError.errorMessage = "�������� "+rs+" ���ޱ�����Ϣ��������¼�룡";
          this.mErrors.addOneError(tError);
          return false;
	    }
	    
	    if(!jscheck(mGrpContNo))
	    {
	    	return false;
	    }
	    	    
	    if("2".equals(mLCGrpContSchema.getDifFlag()))
	    {
	    	//���ĳһ�����ǽ����������ô��ͬ����ĩ���������ϼ�������ͬ����Ҫ�ǽ��������
	    	String organComcodeStr = " select lco.organcomcode||'����'||lco.grpname "
                +" from lcorgan lco "
                +" where lco.grpcontno = '"+mGrpContNo+"' "
                +" and lco.balanceflag = 'Y' "
                +" and exists (select '' "
                +" from lcorgan a "
                +" where a.grpcontno = '"+mGrpContNo+"' "
                +" and a.upcomcode = lco.upcomcode "
                +" and a.childflag = '0' "
                +" and (a.balanceflag <> 'Y' or a.balanceflag is null))";

			SSRS organComSSRS = new SSRS();
			
			organComSSRS = tExeSQL.execSQL(organComcodeStr);
			
			if(organComSSRS!=null&&organComSSRS.getMaxRow()>0)
			{
				CError tError = new CError();
				tError.moduleName = "GrpFirstWorkFlowCheck";
				tError.functionName = "checkData";
				tError.errorMessage = "��֧����"+organComSSRS.GetText(1, 1)
						+"�ǽ���������������ͬһ�㼶�Ĳ��ǽ�������Ļ��������޸ĺ��ٽ���¼�����";
				this.mErrors.addOneError(tError);
				return false;
			}
			
			//����������ϲ���������ǽ��������ֻ���Ƿǽ��������
			String UpComcodeStr = " select lco.organcomcode||'����'||lco.grpname "
                +" from lcorgan lco "
                +" where lco.grpcontno = '"+mGrpContNo+"' "
                +" and lco.balanceflag = 'Y' "
                +" and exists (select '' "
                +" from lcorgan a "
                +" where a.grpcontno = '"+mGrpContNo+"' "
                +" and a.organcomcode = lco.upcomcode "
                +" and a.balanceflag = 'Y')";

			SSRS UpComSSRS = new SSRS();
			
			UpComSSRS = tExeSQL.execSQL(UpComcodeStr);
			
			if(UpComSSRS!=null&&UpComSSRS.getMaxRow()>0)
			{
				CError tError = new CError();
				tError.moduleName = "GrpFirstWorkFlowCheck";
				tError.functionName = "checkData";
				tError.errorMessage = "��֧����"+UpComSSRS.GetText(1, 1)
						+"�ǽ�����������ϲ���������ǽ�����������޸ĺ��ٽ���¼�����";
				this.mErrors.addOneError(tError);
				return false;
			}
			
			//ÿһ��ĩ��������Ҫ������������
			
			String MJComcodeStr = " select lco.organcomcode||'����'||lco.grpname "
                                 +" from lcorgan lco "
                                 +" where lco.grpcontno = '"+mGrpContNo+"'"
                                 +" and GetJsComcode(lco.grpcontno, lco.organcomcode,'') is null"
                                 +" and lco.childflag = '0'";

			SSRS MJComSSRS = new SSRS();
			
			MJComSSRS = tExeSQL.execSQL(MJComcodeStr);
			
			if(MJComSSRS!=null&&MJComSSRS.getMaxRow()>0)
			{
				CError tError = new CError();
				tError.moduleName = "GrpFirstWorkFlowCheck";
				tError.functionName = "checkData";
				tError.errorMessage = "ĩ����֧����"+MJComSSRS.GetText(1, 1)
						+"�Ҳ�����Ӧ�Ľ�����������޸ĺ��ٽ���¼�����";
				this.mErrors.addOneError(tError);
				return false;
			}
			
	    }
	    //Added By Fang for Change4 ��۳�ʼ���ñ������������ڻ���������ڶ�Ӧ�Ĺ����˻�(20110419)
	    String chkFeeModeStr = "select o.organcomcode from lcpol m,lcgrpfee n,lcinsured o where m.grpcontno=n.grpcontno"
	    	   + " and m.grppolno=n.grppolno and m.contno=o.contno and m.poltypeflag not in ('2','5') and n.feecalmode='09'"
	    	   + " and n.feetype='0' and not exists (select '' from lcpol a,lcinsured b where b.grpcontno=m.grpcontno"
	    	   + " and a.contno=b.contno and b.organcomcode=o.organcomcode and a.poltypeflag='2') and m.grpcontno='"
	    	   + mGrpContNo + "' and rownum=1";
	    String chkFeeMode = tExeSQL.getOneValue(chkFeeModeStr);
	    if (chkFeeMode != null && !"".equals(chkFeeMode)){
	    	CError tError = new CError();
	    	tError.moduleName = "GrpFirstWorkFlowCheck";
	        tError.functionName = "checkData";
	        tError.errorMessage = "�ñ���Ϊһ����۳�ʼ���ã�����������ĩ��������������ڶ�Ӧ�����˻�����¼��������";
	        this.mErrors.addOneError(tError);
	        return false;
	    }
	    //Ended for Change4
	    
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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
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
            tError.moduleName = "GrpPolApproveAfterInitService";
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
            tError.moduleName = "GrpPolApproveAfterInitService";
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

        //�ж���������Ͷ�������Ƿ��в���Ա�������,�����ø����屣��״̬Ϊ�������޸�
        ExeSQL tExeSQL = new ExeSQL();
        String sql = "select count(1) from LCGrpIssuePol where GrpContNo = '"
                + mLCGrpContSchema.getGrpContNo() + "' and replyman is null and backobjtype = '1'";

        String tStr = "";
        double tCount = -1;
        tStr = tExeSQL.getOneValue(sql);
        if (tStr.trim().equals(""))
        {
            tCount = 0;
        }
        else
        {
            tCount = Double.parseDouble(tStr);
        }

        if (tCount > 0.0)
        {
            mApproveFlag = "9"; //���߸����޸ģ�����
        }
        else
        {
            mApproveFlag = "9"; //�Զ��˱�
        }

        System.out.print(" mApproveFlag: " + mApproveFlag);

        String tCurrentDate = PubFun.getCurrentDate();
        String tCurrentTime = PubFun.getCurrentTime();
        // �޸ļ���Ͷ���������˱���͸�������
        mLCGrpContSchema.setApproveCode(mOperator);
        mLCGrpContSchema.setApproveDate(tCurrentDate);
        mLCGrpContSchema.setApproveTime(tCurrentTime);
        mLCGrpContSchema.setApproveFlag(mApproveFlag);
        mLCGrpContSchema.setModifyDate(tCurrentDate);
        mLCGrpContSchema.setModifyTime(tCurrentTime);
        mLCGrpContSchema.setSpecFlag("0");
        
        //added by yeshu,2005-12-22,begin
        //���������ͬ���payintv�����ŵ������в����ڽ������ͬ�Ľ��ѷ�ʽΪ-1��
        //�������������������ͬ���ѷ�ʽΪ0
        //������ȫΪ�ڽ�����ȡ���Ѽ��ʱ����̵ķ�ʽ
        String temPayintv="12";
        StringBuffer SQLPayintv =  new StringBuffer(128);
        SQLPayintv.append("select payintv from lcgrppol where grpcontno='");
        SQLPayintv.append(mGrpContNo);
        SQLPayintv.append("'");
        System.out.println(SQLPayintv.toString());
        ExeSQL temExeSql = new ExeSQL();
        SSRS tmpSSRS = temExeSql.execSQL(SQLPayintv.toString());
        
        System.out.println("tmpSSRS.MAXROW:"+tmpSSRS.MaxRow);
        for(int i=1;i<=tmpSSRS.MaxRow;i++)
        {
          System.out.println("tmpSSRS.GetText("+i+",1)="+tmpSSRS.GetText(i,1));
          if(tmpSSRS.GetText(i,1).equals("-1"))
          {
            temPayintv="-1";
            break;
          }
          else if (tmpSSRS.GetText(i,1).equals("0"))
          {
            temPayintv="0";
          }
          else if( Integer.parseInt(tmpSSRS.GetText(i,1)) < Integer.parseInt(temPayintv) &&
                  !temPayintv.equals("0"))
          {
            temPayintv=tmpSSRS.GetText(i,1);
          }
        }
        mLCGrpContSchema.setPayIntv(temPayintv);
        //added by yeshu,2005-12-22,end
        return true;


    }
	/**
	 *  �������ڵĲ�ƷУ�飺
	 *  1������ѡ���շ�֧��������
	 *  2��ĩ�����������ǽ������
	 *  ASR20092836 add by lilei 2009-10-28 
	 * @param String tGrpContNo
	 */
	
	private boolean jscheck(String tGrpContNo)
	{
		ExeSQL tExeSQL = new ExeSQL();
		
		String sanqiStr = " select count(1) "
			             +" from lcgrppol lcg "
			             +" where lcg.grpcontno = '"+tGrpContNo+"' "
			             +" and exists (Select * "
			             +" from lmriskapp lmr "
			             +" where lmr.risktype3 in ('3', '4') "
			             +" and lmr.riskcode = lcg.riskcode) ";
		
		String sanqiCount = tExeSQL.getOneValue(sanqiStr);
		
		//˵�������ڵı���
		if(sanqiCount!=null&&Double.parseDouble(sanqiCount)>0)
		{
			String difflagstr = "select nvl(difflag,'1') from lcgrpcont where grpcontno = '"+tGrpContNo+"'";
			
			String difflag = tExeSQL.getOneValue(difflagstr);
			
			if(difflag!=null&&!"2".equals(difflag))
			{
        		CError.buildErr(this, "���ڲ�Ʒ�ı���������ѡ���շ�֧�����շѣ�");
    			return false;		
			}
			
			String balancestr = " select count(1) "
                                   +" from lcorgan lco "
                                   +" where lco.grpcontno = '"+tGrpContNo+"' "
                                   +" and lco.childflag = '0' "
                                   +" and lco.balanceflag <> 'Y'";
			
			String balanceCount = tExeSQL.getOneValue(balancestr);
			
			if(balanceCount!=null&&Double.parseDouble(balanceCount)>0)
			{
        		CError.buildErr(this, "���ڲ�Ʒ�ı�����ĩ�����������ǽ��������");
    			return false;	
			}
			
		}
		
		return true;
	}
    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        System.out.println("-----ApproveFlag==" + mApproveFlag);
        mTransferData.setNameAndValue("ApproveFlag", mApproveFlag);
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
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
     *  ���ش������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}
