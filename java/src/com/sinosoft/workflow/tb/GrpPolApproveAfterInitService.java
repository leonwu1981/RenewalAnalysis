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
 * <p>Title: 工作流团单复核服务类 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */
/****************************************************************************************************************
 * Fang 2008-11-03 中意附加女性疾病保障团体疾病保险MGK04 产品定义 
 * Change1、GIL值保存增加MGK04
 * **************************************************************************************************************
 * Fang 2009-05-26 中意借贷宝团体定期寿险NAK02 产品定义 
 * Change2、GIL值保存增加NAK02
 * **************************************************************************************************************
 * frost 2010-09-19 自动核保使用规则引擎改造 
 * 去掉GrpPolApproveAfterInitService类中关于GIL相关处理，GIL的计算通过规则引擎完成，
 * 人工核保GIL的个人调整删除移到新的自动核保处理类中
 * **************************************************************************************************************
 * Fang 2011-04-19 
 * Change4、外扣初始费用保单被保人所在机构必须存在对应的公共账户
 * **************************************************************************************************************
 **/

public class GrpPolApproveAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** 数据操作字符串 */
    private String mOperator;

    /** 业务数据操作字符串 */
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

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData(cInputData))
        {
            return false;
        }

        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }

    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData(VData cInputData)
    {
        mResult.clear();
        MMap map = new MMap();

        //修改险种保单表
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

        //修改合同表
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

        //修改集体合同表
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
         * 团单流程修改后，被保人改在复核处导入，新单录入完毕时LCGrpCont表的peoples字段被置空，复核完毕后需将该字段更新
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
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //校验保单信息
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mGrpContNo + "信息查询失败!";
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
            tError.errorMessage = "未录入集体保单信息！";
            this.mErrors.addOneError(tError);
            return false;
        }
        //校验此险种是否已经录入被保人 根据中意需求 在此 将其校验注视掉 2007.5.15 guoxq
//        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
//        {
//            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpPolApproveAfterInitService";
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

        mLCGrpContSchema.setSchema(tLCGrpContDB);
        if (!mLCGrpContSchema.getAppFlag().trim().equals("0"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "此集体单不是投保单，不能进行复核操作!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCGrpContSchema.getApproveFlag().equals("9"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "此集体投保单复核已经通过，不能再进行复核通过操作!";
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
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "集体投保单下没有个人投保单，不能进行复核操作!";
            this.mErrors.addOneError(tError);
            return false;
        }
//      增加被保险人下是否有保费项的校验
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
	    //Added By Fang for Change4 外扣初始费用保单被保人所在机构必须存在对应的公共账户(20110419)
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
	    //Ended for Change4
	    
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

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
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

        //判断团体主险投保单下是否有操作员的问题件,有则置该团体保单状态为待复核修改
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
            mApproveFlag = "9"; //不走复核修改，跳过
        }
        else
        {
            mApproveFlag = "9"; //自动核保
        }

        System.out.print(" mApproveFlag: " + mApproveFlag);

        String tCurrentDate = PubFun.getCurrentDate();
        String tCurrentTime = PubFun.getCurrentTime();
        // 修改集体投保单复核人编码和复核日期
        mLCGrpContSchema.setApproveCode(mOperator);
        mLCGrpContSchema.setApproveDate(tCurrentDate);
        mLCGrpContSchema.setApproveTime(tCurrentTime);
        mLCGrpContSchema.setApproveFlag(mApproveFlag);
        mLCGrpContSchema.setModifyDate(tCurrentDate);
        mLCGrpContSchema.setModifyTime(tCurrentTime);
        mLCGrpContSchema.setSpecFlag("0");
        
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
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
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
     * 返回处理后的结果
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 返回工作流中的Lwfieldmap所描述的值
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    /**
     *  返回错误对象
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}
