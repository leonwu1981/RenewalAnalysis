package com.sinosoft.workflow.ask;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title:����Լ������ </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class AskWorkFlowUI {
  /** �������࣬ÿ����Ҫ����������ж����ø��� */
  public CErrors mErrors = new CErrors();

  /** �����洫�����ݵ����� */
  private VData mInputData = new VData();

  /** �����洫�����ݵ����� */
  private VData mResult = new VData();

  /** ���ݲ����ַ��� */
  private String mOperate;

  public AskWorkFlowUI() {}

  public static void main(String[] args) {
    VData tVData = new VData();
    GlobalInput mGlobalInput = new GlobalInput();
    TransferData mTransferData = new TransferData();

    /** ȫ�ֱ��� */
    mGlobalInput.Operator = "001";
    mGlobalInput.ComCode = "86";
    mGlobalInput.ManageCom = "86";
    mTransferData.setNameAndValue("ProposalGrpContNo","140110000000601");
    mTransferData.setNameAndValue("GrpContNo","140110000000601");

  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
  tLZSysCertifySchema.setTakeBackOperator("001");
  tLZSysCertifySchema.setTakeBackMakeDate(PubFun.getCurrentDate());
  mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);

    //��ɨ��¼�������һ�����˱�
//    LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();
//    mTransferData.setNameAndValue("LCGCUWMasterSchema",tLCGCUWMasterSchema);
//    tLCGCUWMasterSchema.setUWIdea("123");
//    tLCGCUWMasterSchema.setPassFlag("11");
//    mTransferData.setNameAndValue("ProposalGrpContNo","140110000000601");
//    mTransferData.setNameAndValue("GrpContNo","140110000000601");
    mTransferData.setNameAndValue("MissionID","00000000000000004910");
    mTransferData.setNameAndValue("SubMissionID", "1");
    mTransferData.setNameAndValue("CertifyNo", "810000000000476");
    mTransferData.setNameAndValue("CertifyCode", "6007");
//    mTransferData.setNameAndValue("PrtNo","190001231");
//    mTransferData.setNameAndValue("AgentCode", "86110527");
//    mTransferData.setNameAndValue("ManageCom","86110000");
//    mTransferData.setNameAndValue("GrpNo","0000006520");
//    mTransferData.setNameAndValue("GrpName","123");

//    mTransferData.setNameAndValue("PrtNo", "200020111");
//    mTransferData.setNameAndValue("AgentCode", "86110527");
//    mTransferData.setNameAndValue("ManageCom", "86110000");
//    mTransferData.setNameAndValue("GrpNo", "0000006520 ");
//    mTransferData.setNameAndValue("GrpName","һ��");
//    mTransferData.setNameAndValue("MissionID","00000000000000000760");
//    mTransferData.setNameAndValue("SubMissionID","1");
//        //�µ�����
//        mTransferData.setNameAndValue("ContNo", "130110000009383");
//        mTransferData.setNameAndValue("MissionID", "00000000000000000742");
//        mTransferData.setNameAndValue("SubMissionID", "1");
//    LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();
//    tLCGCUWMasterSchema.setPassFlag("9");
//    tLCGCUWMasterSchema.setUWIdea("dfasdfsdf");
//
//    mTransferData.setNameAndValue("GrpContNo", "140110000000545");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000760");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//
    tVData.add(mGlobalInput);
//    //    tVData.add( tLCContSet );
   tVData.add(mTransferData);
//      TransferData mTransferData = new TransferData();
      /**�ܱ���*/


    AskWorkFlowUI tAskWorkFlowUI = new AskWorkFlowUI();
    try {
      if (tAskWorkFlowUI.submitData(tVData, "0000006022")) {
        VData tResult = new VData();

        //tResult = tActivityOperator.getResult() ;
      }
      else {
        System.out.println(tAskWorkFlowUI.mErrors.getError(0).
                           errorMessage);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  /**
    �������ݵĹ�������
   */
  public boolean submitData(VData cInputData, String cOperate) {
    //���������ݿ�����������
    this.mOperate = cOperate;

    AskWorkFlowBL tAskWorkFlowBL = new AskWorkFlowBL();

    System.out.println("---AskWorkFlowBL UI BEGIN---");
    if (tAskWorkFlowBL.submitData(cInputData, mOperate) == false) {
      // @@������
      this.mErrors.copyAllErrors(tAskWorkFlowBL.mErrors);
      mResult.clear();
      return false;
    }
    return true;
  }
}
