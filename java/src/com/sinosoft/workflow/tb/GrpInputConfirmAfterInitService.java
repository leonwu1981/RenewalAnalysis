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
 * <p>Title: 团体新契约录入完毕 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */
/****************************************************************************************************************
 * Fang 2011-04-19 
 * Change1、外扣初始费用保单被保人所在机构必须存在对应的公共账户
 * **************************************************************************************************************
 **/

public class GrpInputConfirmAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();


    /** 数据操作字符串 */
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
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //校验是否有未打印的体检通知书
        if (!checkData())
        {
            return false;
        }

        System.out.println("Start  dealData...");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
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
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {

        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "查询集体合同信息失败，请确认是否录入正确!";
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
            tError.errorMessage = "未录入集体保单信息！";
            this.mErrors.addOneError(tError);
            return false;
        }
        ExeSQL tExeSQL = new ExeSQL();
	    String tSql = "select a.insuredname||' 险种 '||a.riskcode from lcpol a where a.grpcontno='" + mGrpContNo
	                + "' and not exists (select 1 from lcprem where polno=a.polno) ";
	    String rs = tExeSQL.getOneValue(tSql);
	    if (!"".equals(rs) && rs != null){
	    	CError tError = new CError();
          tError.moduleName = "GrpFirstWorkFlowCheck";
          tError.functionName = "checkData";
          tError.errorMessage = "被保险人 "+rs+" 下无保费信息，请重新录入！";
          this.mErrors.addOneError(tError);
          return false;
	    }
        //校验此险种是否已经录入被保人
//        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
//        {
//            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpFirstWorkFlowCheck";
//                tError.functionName = "checkData";
//                tError.errorMessage = "险种" + tLCGrpPolSet.get(i).getRiskCode() +
//                        "下未录入被保人，请删除此险种或录入被保险人！";
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
//                tError.errorMessage = "集体险种下未录入个人险种信息！";
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
	    	//如果某一机构是结算机构，那么其同级的末级机构（上级机构相同）都要是结算机构。
	    	String organComcodeStr = " select lco.organcomcode||'——'||lco.grpname "
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
				tError.errorMessage = "分支机构"+organComSSRS.GetText(1, 1)
						+"是结算机构，但其存在同一层级的不是结算机构的机构，请修改后再进行录入完毕";
				this.mErrors.addOneError(tError);
				return false;
			}
			
			//结算机构的上层机构不能是结算机构，只能是非结算机构。
			String UpComcodeStr = " select lco.organcomcode||'——'||lco.grpname "
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
				tError.errorMessage = "分支机构"+UpComSSRS.GetText(1, 1)
						+"是结算机构，其上层机构不能是结算机构，请修改后再进行录入完毕";
				this.mErrors.addOneError(tError);
				return false;
			}
			
			//每一个末级机构都要有其结算机构。
			
			String MJComcodeStr = " select lco.organcomcode||'——'||lco.grpname "
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
				tError.errorMessage = "末级分支机构"+MJComSSRS.GetText(1, 1)
						+"找不到相应的结算机构，请修改后再进行录入完毕";
				this.mErrors.addOneError(tError);
				return false;
			}
			
	    }
	    //Added By Fang for Change1 外扣初始费用保单被保人所在机构必须存在对应的公共账户(20110419)
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
	        tError.errorMessage = "该保单为一笔外扣初始费用，被保人所在末级机构都必须存在对应公共账户，请录入完整！";
	        this.mErrors.addOneError(tError);
	        return false;
	    }
	    //Ended for Change1
	    	    
        return true;
    }


    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中mContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {

        //为团体合同表存入录入人、录入时间
        mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setInputDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setInputTime(PubFun.getCurrentTime());
        //added by yeshu,2005-12-22,begin
        //更新团体合同表的payintv，若团单险种有不定期交费则合同的交费方式为-1，
        //否则，若险种有趸交则合同交费方式为0
        //若险种全为期交，则取交费间隔时间最短的方式
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

        //为合同表存入录入人、录入时间
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

        //重新对个人合同，集体险种，集体合同进行重新统计合计人数，合计保费，合计保额
        if (!sumData())
        {
            return false;
        }

        return true;

    }

	/**
	 *  对于三期的产品校验：
	 *  1、必须选择按照分支机构结算
	 *  2、末级机构必须是结算机构
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
		
		//说明是三期的保单
		if(sanqiCount!=null&&Double.parseDouble(sanqiCount)>0)
		{
			String difflagstr = "select nvl(difflag,'1') from lcgrpcont where grpcontno = '"+tGrpContNo+"'";
			
			String difflag = tExeSQL.getOneValue(difflagstr);
			
			if(difflag!=null&&!"2".equals(difflag))
			{
        		CError.buildErr(this, "三期产品的保单，必须选择按照分支机构收费！");
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
        		CError.buildErr(this, "三期产品的保单，末级机构必须是结算机构！");
    			return false;	
			}
			
		}
		
		return true;
	}
	
    /**
     * sumData
     * 进行保费、保额的汇总
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
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
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
