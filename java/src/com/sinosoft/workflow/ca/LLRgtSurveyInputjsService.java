/**
 * Copyright (c) 2005 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.BeforeInitService;
import com.sinosoft.lis.llcase.RgtSurveyUI;
import com.sinosoft.lis.llcase.RgtSurveyBL;
import com.sinosoft.lis.llcase.LLCaseReturnUI;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLSurveySchema;


/**
 * <p>Title: 工作流节点任务:理赔案件回退工作流服务类 </p>
 * <p>Description: 理赔案件回退工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: SinoSoft</p>
 * @author liruofei
 * @version 1.0
 */
public class LLRgtSurveyInputjsService implements BeforeInitService
{
    /*错误处理类，每个需要错误处理的类中都放置该类*/
    public CErrors mErrors  = new CErrors();

    /*往界面传输数据的容器*/
    private VData mResult = new VData();

    /*往工作流引擎中传输数据的容器*/
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /*数据操作字符串*/
    private String mOperater;
    private String mManageCom;

    /*业务数据操作字符串*/
    private String mMissionID;
    private String tSubMissionID;

    public String SurveyNo = "";

    /**/
    public LLRgtSurveyInputjsService()
    {}
    /**
     * 传输数据的公共方法
     * @param cInputData VData 输入的数据
     * @param cOperate String 数据操作
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //得到外部传入的数据，将数据备份到本类中
        if (!getInputData(cInputData, cOperate))

        {
            return false;
        }

        //进行业务处理
        if (!dealData(cInputData))
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        return true;
    }

    /**
    * 从输入数据中得到所有对象
    * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
    * @param cInputData VData
    * @param cOperate String
    * @return boolean
    */

   private boolean getInputData(VData cInputData, String cOperate)
   {
       //从输入数据中得到所有对象
       //获取全局公共数据
       mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
       mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
       System.out.println("SurveyNo==="+mTransferData.getValueByName("SurveyNo"));
    if(mTransferData==null){
        CError.buildErr(mErrors, "前台传输全局公共数据失败!");
           return false;
    }
       if (mGlobalInput == null)
       {
           //错误处理
           CError.buildErr(mErrors, "前台传输全局公共数据失败!");
           return false;
       }

       //获得操作员编码
       mOperater = mGlobalInput.Operator;
       if (mOperater == null || mOperater.trim().equals(""))
      {

           //错误处理
           CError.buildErr(mErrors, "前台传输全局公共数据Operater失败!");
           return false;
      }

      //获得登陆机构编码
      mManageCom = mGlobalInput.ManageCom;
      if (mManageCom == null || mManageCom.trim().equals(""))
      {
           //错误处理
           CError.buildErr(mErrors, "前台传输全局公共数据ManageCom失败!");
           return false;
      }

      //获取当前工作任务ID
      mMissionID = (String) mTransferData.getValueByName("MissionID");
      if (mMissionID == null)
      {
          //错误处理
          CError.buildErr(mErrors, "前台传输业务数据中MissionID失败!");
          return false;
      }

      return true;
  }

  /**
    * 根据前面的输入数据，进行BL逻辑处理
    * 如果在处理过程中出错，则返回false,否则返回true
    * @return boolean
    */
   private boolean dealData(VData cInputData)
   {
       VData tVData = new VData();
       RgtSurveyUI tRgtSurveyUI   = new RgtSurveyUI();
       LLCaseReturnUI tLLCaseReturnUI   = new LLCaseReturnUI();//C
       String transact = (String)mTransferData.getValueByName("transact");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        System.out.println("transact ======== "+transact);
        String SurveyReturn = "";
        SurveyReturn = (String)mTransferData.getValueByName("SurveyReturn");
        System.out.println("SurveyReturn ======== "+SurveyReturn);
        if(SurveyReturn!=null && SurveyReturn.equals("1"))
        {
            if (tRgtSurveyUI.submitData(cInputData, transact)) {
                tVData = tRgtSurveyUI.getResult();
                LLSurveySchema mLLSurveySchema = new LLSurveySchema();
                mLLSurveySchema.setSchema((LLSurveySchema) tVData.
                                          getObjectByObjectName(
                        "LLSurveySchema", 0));
                System.out.println("SurveyNo2===" + mLLSurveySchema.getSurveyNo());
                SurveyNo = mLLSurveySchema.getSurveyNo();
                mResult.clear();
                mResult.add(tVData);
            } else {
                this.mErrors.copyAllErrors(tRgtSurveyUI.mErrors);
                return false;
            }


        }else {
            String CAFlag = (String) mTransferData.getValueByName("CAFlag");
            System.out.println("CAFlag ======== " + CAFlag);
            if (transact != null && !transact.equals("")) {
                if (tLLCaseReturnUI.submitData(cInputData, transact)) {

                    tVData = tLLCaseReturnUI.getResult();
                    mResult.add(tVData);
                } else {
                    this.mErrors.copyAllErrors(tLLCaseReturnUI.mErrors);
                    return false;
                }
            }


        }
       return true;
   }

   /**
       * 返回处理后的结果
       * @return VData
     */

  /*调试
  RgtSurveyBL tRgtSurveyBL = new RgtSurveyBL();
  VData mVData = tRgtSurveyBL.getResult();
  LLSurveySchema mLLSurveySchema = new LLSurveySchema();
  mLLSurveySchema.setSchema(( LLSurveySchema )mVData.getObjectByObjectName( "LLSurveySchema", 0 ));
  System.out.println("SurveyNo2==="+mLLSurveySchema.getSurveyNo());
   /*调试结束*/
  public VData getResult()
  {
      return this.mResult;

  }

public String getSurveyNo()
 {
    return this.SurveyNo;
 }
  /**
     * 返回工作流中的Lwfieldmap所描述的值
     * @return TransferData
     */

  public TransferData getReturnTransferData()
  {
      return this.mTransferData;
  }

  /**
     * 返回错误对象
     * @return CErrors
     */
  public CErrors getErrors()
  {
      return this.mErrors;
  }
}
