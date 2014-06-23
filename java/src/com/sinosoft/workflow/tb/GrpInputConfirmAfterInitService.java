/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.vschema.LCGrpPolSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ��������Լ¼����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */
/****************************************************************************************************************
 * Fang 2011-04-19 
 * Change1����۳�ʼ���ñ������������ڻ���������ڶ�Ӧ�Ĺ����˻�
 * **************************************************************************************************************
 **/

public class GrpInputConfirmAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();


    /** ���ݲ����ַ��� */
    private String mOperater;
//    private String mManageCom;
    private String mOperate;
    private String mMissionID;
//    private String mSubMissionID;
    private String mGrpContNo;
    private String mContSql;
    private String mGrpContSql;
    private String[] mGrpPolSql;

    public GrpInputConfirmAfterInitService()
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
        MMap map = new MMap();

        map.put(mContSql, "UPDATE");
        map.put(mLCGrpContSchema, "UPDATE");
        map.put(mGrpContSql, "UPDATE");
        for (int i = 0; i < mGrpPolSql.length; i++)
        {
            map.put(mGrpPolSql[i], "UPDATE");
        }
        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {

        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ�����ͬ��Ϣʧ�ܣ���ȷ���Ƿ�¼����ȷ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
        if (tLCGrpPolSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "δ¼�뼯�屣����Ϣ��";
            this.mErrors.addOneError(tError);
            return false;
        }
        ExeSQL tExeSQL = new ExeSQL();
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
        //У��������Ƿ��Ѿ�¼�뱻����
//        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
//        {
//            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpFirstWorkFlowCheck";
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
	    //Added By Fang for Change1 ��۳�ʼ���ñ������������ڻ���������ڶ�Ӧ�Ĺ����˻�(20110419)
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
	    //Ended for Change1
	    	    
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
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������mContNoʧ��!";
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

        //Ϊ�����ͬ�����¼���ˡ�¼��ʱ��
        mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setInputDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setInputTime(PubFun.getCurrentTime());
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
          else if(Integer.parseInt(tmpSSRS.GetText(i,1)) < Integer.parseInt(temPayintv) &&
              !temPayintv.equals("0"))
          {
            temPayintv=tmpSSRS.GetText(i,1);
          }
        }
        mLCGrpContSchema.setPayIntv(temPayintv);
        //added by yeshu,2005-12-22,end

        //Ϊ��ͬ�����¼���ˡ�¼��ʱ��
//        mContSql = "update lccont set InputOperator ='" + mOperater + "',InputDate = '"
//                + PubFun.getCurrentDate() + "',InputTime = '" + PubFun.getCurrentTime()
//                + "' where grpcontno = '" + mGrpContNo + "'";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("update lccont set InputOperator ='");
        tSBql.append(mOperater);
        tSBql.append("',InputDate = '");
        tSBql.append(PubFun.getCurrentDate());
        tSBql.append("',InputTime = '");
        tSBql.append(PubFun.getCurrentTime());
        tSBql.append("' where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mContSql = tSBql.toString();

        //���¶Ը��˺�ͬ���������֣������ͬ��������ͳ�ƺϼ��������ϼƱ��ѣ��ϼƱ���
        if (!sumData())
        {
            return false;
        }

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
     * sumData
     * ���б��ѡ�����Ļ���
     * @return boolean
     */
    private boolean sumData()
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        tLCGrpPolSet = tLCGrpPolDB.query();
        mGrpPolSql = new String[tLCGrpPolSet.size()];

        StringBuffer tSBql = null;
        String tGrpPolNo = null;
        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
        {
            tGrpPolNo = tLCGrpPolSet.get(i).getGrpPolNo();
//            mGrpPolSql[i - 1] =
//                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '" +
//                    tGrpPolNo + "'),amnt = (select sum(amnt) from lcpol where grppolno = '" +
//                    tGrpPolNo
//                    + "'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '" +
//                    tGrpPolNo + "') where grppolno = '" + tGrpPolNo + "'";
            tSBql = new StringBuffer(128);
            tSBql.append(
                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),amnt = (select sum(amnt) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("') where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'");
            mGrpPolSql[i - 1] = tSBql.toString();
        }

//        mGrpContSql =
//                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '"
//                + mGrpContNo + "'),amnt=(select sum(amnt) from lccont where grpcontno = '"
//                + mGrpContNo + "'),Peoples = (select sum(Peoples) from lccont where grpcontno = '"
//                + mGrpContNo + "') where grpcontno = '" + mGrpContNo + "'";
        tSBql = new StringBuffer(128);
        tSBql.append(
                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'),amnt=(select sum(amnt) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
        //tSBql.append("'),Peoples = (select sum(Peoples) from lccont where grpcontno = '");
        //tSBql.append(mGrpContNo);
        //tSBql.append("') where grpcontno = '");
        tSBql.append("') where grpcontno='");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mGrpContSql = tSBql.toString();
        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ProposalGrpContNo", mLCGrpContSchema.getProposalGrpContNo());
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom", mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate", mLCGrpContSchema.getCValiDate());

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

}
