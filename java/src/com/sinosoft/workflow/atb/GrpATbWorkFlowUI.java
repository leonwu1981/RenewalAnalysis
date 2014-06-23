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
 * Title: 团体工作流
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
	/** 错误处理类，每个需要错误处理的类中都放置该类 */
	public CErrors mErrors = new CErrors();

	/** 往界面传输数据的容器 */
	private VData mResult = new VData();

	/** 数据操作字符串 */
	private String mOperate;

	public GrpATbWorkFlowUI() {
	}

	/**
	 * 传输数据的公共方法
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	public boolean submitData(VData cInputData, String cOperate) {
		// 将操作数据拷贝到本类中
		this.mOperate = cOperate;

		GrpATbWorkFlowBL tGrpTbWorkFlowBL = new GrpATbWorkFlowBL();

		System.out.println("---GrpATbWorkFlowBL UI BEGIN---");
		if (tGrpTbWorkFlowBL.submitData(cInputData, mOperate)) {
			return true;
		} else {
			// @@错误处理
			this.mErrors.copyAllErrors(tGrpTbWorkFlowBL.mErrors);
			mResult.clear();
			return false;
		}
		// return true;
	}

	/**
	 * 测试函数
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		VData tVData = new VData();
		GlobalInput mGlobalInput = new GlobalInput();
		TransferData mTransferData = new TransferData();

		/** 全局变量 */
		mGlobalInput.Operator = "001";
		mGlobalInput.ComCode = "86";
		mGlobalInput.ManageCom = "86";

		LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
		tLCGrpContSchema.setGrpContNo("9018000000073388");
		
		mTransferData.setNameAndValue("MissionID", "00000000000000003665");
		mTransferData.setNameAndValue("SubMissionID", "1");

		/** 总变量 */
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
