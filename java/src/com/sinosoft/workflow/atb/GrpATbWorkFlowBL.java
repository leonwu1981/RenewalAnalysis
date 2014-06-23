/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.atb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: SinoSoft
 * </p>
 * 
 * @author HYQ
 * @version 1.0
 */
public class GrpATbWorkFlowBL {
	/** 错误处理类，每个需要错误处理的类中都放置该类 */
	public CErrors mErrors = new CErrors();

	/** 往后面传输数据的容器 */
	private VData mInputData;

	/** 往界面传输数据的容器 */
	private VData mResult = new VData();

	/** 往工作流引擎中传输数据的容器 */
	private GlobalInput mGlobalInput = new GlobalInput();

	// private VData mIputData = new VData();
	private TransferData mTransferData = new TransferData();

	/** 工作流引擎 */
	ActivityOperator mActivityOperator = new ActivityOperator();

	/** 数据操作字符串 */
	private String mOperater;

	private String mManageCom;

	private String mOperate;

	public GrpATbWorkFlowBL() {
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
		// 得到外部传入的数据,将数据备份到本类中
		if (!getInputData(cInputData, cOperate)) {
			return false;
		}

		// 数据操作业务处理
		if (!dealData()) {
			return false;
		}

		System.out.println("---GrpTbWorkFlowBL dealData---");

		// 准备给后台的数据
		if (!prepareOutputData()) {
			return false;
		}

		System.out.println("---GrpTbWorkFlowBL prepareOutputData---");

		// 数据提交
		GrpATbWorkFlowBLS tGrpTbWorkFlowBLS = new GrpATbWorkFlowBLS();
		System.out.println("Start GrpTbWorkFlowBL Submit...");

		if (tGrpTbWorkFlowBLS.submitData(mResult, mOperate)) {
			System.out.println("---GrpTbWorkFlowBLS commitData End ---");
			return true;
		} else {
			// @@错误处理
			this.mErrors.copyAllErrors(tGrpTbWorkFlowBLS.mErrors);

			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "submitData";
			tError.errorMessage = "数据提交失败!";
			this.mErrors.addOneError(tError);
			System.out.println("---GrpTbWorkFlowBLS commitData End ---");
			return false;
		}
	}

	/**
	 * 从输入数据中得到所有对象 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	private boolean getInputData(VData cInputData, String cOperate) {
		// 从输入数据中得到所有对象
		// 获得全局公共数据
		mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
		mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
		mInputData = cInputData;
		if (mGlobalInput == null) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "前台传输全局公共数据失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		// 获得操作员编码
		mOperater = mGlobalInput.Operator;
		if ((mOperater == null) || mOperater.trim().equals("")) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "前台传输全局公共数据Operate失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		// 获得登陆机构编码
		mManageCom = mGlobalInput.ManageCom;
		if ((mManageCom == null) || mManageCom.trim().equals("")) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		mOperate = cOperate;
		if ((mOperate == null) || mOperate.trim().equals("")) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "前台传输全局公共数据Operate任务节点编码失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		return true;
	}

	/**
	 * 数据操作类业务处理 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * 
	 * @return boolean
	 */
	private boolean dealData() {
		if (mOperate.trim().equals("A0000000701")) {
			if (!ExecuteA0000000701()) {
				// @@错误处理
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				return false;

			}
			return true;
		} else {
			if (!Execute()) {
				// @@错误处理
				// this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				return false;
			}
		}

		return true;
	}

	/**
	 * 执行承保工作流待人工核保活动表任务 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * 
	 * @return boolean
	 */
	private boolean Execute() {
		mResult.clear();

		VData tVData = new VData();
		ActivityOperator tActivityOperator = new ActivityOperator();

		// 获得当前工作任务的任务ID
		String tMissionID = (String) mTransferData.getValueByName("MissionID");
		String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
		if (tMissionID == null) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "Execute0000000000";
			tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		if (tSubMissionID == null) {
			// @@错误处理
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "Execute0000000000";
			tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
			this.mErrors.addOneError(tError);

			return false;
		}

		try {
			if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, mOperate, mInputData)) {
				// @@错误处理
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);

				return false;
			}

			// 获得执行承保工作流待人工核保活动表任务的结果
			tVData = mActivityOperator.getResult();
			if (tVData != null) {
				for (int i = 0; i < tVData.size(); i++) {
					VData tempVData = new VData();
					tempVData = (VData) tVData.get(i);
					mResult.add(tempVData);
				}
			}

			// 产生执行完承保工作流待人工核保活动表任务后的任务节点
			if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, mOperate, mInputData)) {
				VData tempVData = new VData();
				tempVData = tActivityOperator.getResult();
				if ((tempVData != null) && (tempVData.size() > 0)) {
					mResult.add(tempVData);
					tempVData = null;
				}
			} else {
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);

				return false;
			}

			tActivityOperator = new ActivityOperator();
			if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, mOperate, mInputData)) {
				VData tempVData = new VData();
				tempVData = tActivityOperator.getResult();
				if ((tempVData != null) && (tempVData.size() > 0)) {
					mResult.add(tempVData);
					tempVData = null;
				}
			} else {
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);

				return false;
			}
		} catch (Exception ex) {
			// @@错误处理
			ex.printStackTrace();
			this.mErrors.copyAllErrors(mActivityOperator.mErrors);

			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "dealData";
			tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
			this.mErrors.addOneError(tError);

			return false;
		}

		// */
		return true;
	}

	private boolean ExecuteA0000000701() {
		mResult.clear();
		// VData tVData = new VData();
		ActivityOperator tActivityOperator = new ActivityOperator();
		// 执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
		try {
			System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
			if (CheckGrpFirstTrial()) {
				return true;
			}
			// 产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
			// LWMissionSchema tLWMissionSchema = new LWMissionSchema();
			if (tActivityOperator.CreateStartMission("0000000007", "0000000701", mInputData)) {
				VData tempVData = new VData();
				tempVData = tActivityOperator.getResult();
				mResult.add(tempVData);
				tempVData = null;
			} else {
				// @@错误处理
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				// CError tError = new CError();
				// tError.moduleName = "TbWorkFlowBL";
				// tError.functionName = "Execute9999999999";
				// tError.errorMessage = "工作流引擎工作出现异常!";
				// this.mErrors .addOneError(tError) ;
				return false;
			}
		} catch (Exception ex) {
			// @@错误处理
			this.mErrors.copyAllErrors(mActivityOperator.mErrors);
			CError tError = new CError();
			tError.moduleName = "TbWorkFlowBL";
			tError.functionName = "Execute0000000701";
			tError.errorMessage = "工作流引擎工作出现异常!";
			this.mErrors.addOneError(tError);
			return false;
		}

		return true;
	}

	/**
	 * 准备需要保存的数据
	 * 
	 * @return boolean
	 */
	private static boolean prepareOutputData() {
		// mInputData.add( mGlobalInput );
		return true;
	}

	private boolean CheckGrpFirstTrial() {

		VData tVData = new VData();
		LWMissionSet tLWMissionSet = new LWMissionSet();
		LWMissionSchema tLWMissionSchema = new LWMissionSchema();
		LWMissionDB tLWMissionDB = new LWMissionDB();
		tLWMissionDB.setActivityID("0000002096");
		tLWMissionDB.setProcessID("0000000004");
		tLWMissionDB.setMissionProp1((String) mTransferData.getValueByName("PrtNo"));
		tLWMissionSet = tLWMissionDB.query();
		if (tLWMissionSet.size() == 0) {
			return false;
		}
		MMap map = new MMap();

		tLWMissionSchema = tLWMissionSet.get(1);
		map.put("delete from lwmission where missionid='" + tLWMissionSchema.getMissionID()
				+ "' and activityid = '0000002096'", "DELETE"); // 删除以前的节点
		tLWMissionSchema.setActivityID("0000002098");
		if (mTransferData.getValueByName("SubType") != null) {
			tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
		}
		tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
		tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
		tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
		tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
		map.put(tLWMissionSchema, "INSERT"); // 生成新的节点
		tVData.add(map);
		mResult.add(tVData);
		return true;
	}

}
