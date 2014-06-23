/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.atb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCGrpContSchema;
// import com.sinosoft.lis.vschema.LCGrpContSet;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>
 * Title: ���幤����
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: SinoSoft
 * </p>
 * 
 * @author HYQ
 * @version 1.0
 */

public class GrpATbWorkFlowUI {
	/** �������࣬ÿ����Ҫ����������ж����ø��� */
	public CErrors mErrors = new CErrors();

	/** �����洫�����ݵ����� */
	private VData mResult = new VData();

	/** ���ݲ����ַ��� */
	private String mOperate;

	public GrpATbWorkFlowUI() {
	}

	/**
	 * �������ݵĹ�������
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	public boolean submitData(VData cInputData, String cOperate) {
		// ���������ݿ�����������
		this.mOperate = cOperate;

		GrpATbWorkFlowBL tGrpTbWorkFlowBL = new GrpATbWorkFlowBL();

		System.out.println("---GrpATbWorkFlowBL UI BEGIN---");
		if (tGrpTbWorkFlowBL.submitData(cInputData, mOperate)) {
			return true;
		} else {
			// @@������
			this.mErrors.copyAllErrors(tGrpTbWorkFlowBL.mErrors);
			mResult.clear();
			return false;
		}
		// return true;
	}

	/**
	 * ���Ժ���
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		VData tVData = new VData();
		GlobalInput mGlobalInput = new GlobalInput();
		TransferData mTransferData = new TransferData();

		/** ȫ�ֱ��� */
		mGlobalInput.Operator = "001";
		mGlobalInput.ComCode = "86";
		mGlobalInput.ManageCom = "86";

		LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
		tLCGrpContSchema.setGrpContNo("9018000000073388");
		
		mTransferData.setNameAndValue("MissionID", "00000000000000003665");
		mTransferData.setNameAndValue("SubMissionID", "1");

		/** �ܱ��� */
		tVData.add(tLCGrpContSchema);		
		tVData.add(mTransferData);
		tVData.add(mGlobalInput);
		
		GrpATbWorkFlowUI tGrpTbWorkFlowUI = new GrpATbWorkFlowUI();
		try {
			if (!tGrpTbWorkFlowUI.submitData(tVData, "0000000710")) {
				System.out.println(tGrpTbWorkFlowUI.mErrors.getError(0).errorMessage);
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
