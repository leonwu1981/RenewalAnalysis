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
	/** �������࣬ÿ����Ҫ����������ж����ø��� */
	public CErrors mErrors = new CErrors();

	/** �����洫�����ݵ����� */
	private VData mInputData;

	/** �����洫�����ݵ����� */
	private VData mResult = new VData();

	/** �������������д������ݵ����� */
	private GlobalInput mGlobalInput = new GlobalInput();

	// private VData mIputData = new VData();
	private TransferData mTransferData = new TransferData();

	/** ���������� */
	ActivityOperator mActivityOperator = new ActivityOperator();

	/** ���ݲ����ַ��� */
	private String mOperater;

	private String mManageCom;

	private String mOperate;

	public GrpATbWorkFlowBL() {
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
		// �õ��ⲿ���������,�����ݱ��ݵ�������
		if (!getInputData(cInputData, cOperate)) {
			return false;
		}

		// ���ݲ���ҵ����
		if (!dealData()) {
			return false;
		}

		System.out.println("---GrpTbWorkFlowBL dealData---");

		// ׼������̨������
		if (!prepareOutputData()) {
			return false;
		}

		System.out.println("---GrpTbWorkFlowBL prepareOutputData---");

		// �����ύ
		GrpATbWorkFlowBLS tGrpTbWorkFlowBLS = new GrpATbWorkFlowBLS();
		System.out.println("Start GrpTbWorkFlowBL Submit...");

		if (tGrpTbWorkFlowBLS.submitData(mResult, mOperate)) {
			System.out.println("---GrpTbWorkFlowBLS commitData End ---");
			return true;
		} else {
			// @@������
			this.mErrors.copyAllErrors(tGrpTbWorkFlowBLS.mErrors);

			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "submitData";
			tError.errorMessage = "�����ύʧ��!";
			this.mErrors.addOneError(tError);
			System.out.println("---GrpTbWorkFlowBLS commitData End ---");
			return false;
		}
	}

	/**
	 * �����������еõ����ж��� ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	private boolean getInputData(VData cInputData, String cOperate) {
		// �����������еõ����ж���
		// ���ȫ�ֹ�������
		mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
		mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
		mInputData = cInputData;
		if (mGlobalInput == null) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		// ��ò���Ա����
		mOperater = mGlobalInput.Operator;
		if ((mOperater == null) || mOperater.trim().equals("")) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		// ��õ�½��������
		mManageCom = mGlobalInput.ManageCom;
		if ((mManageCom == null) || mManageCom.trim().equals("")) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		mOperate = cOperate;
		if ((mOperate == null) || mOperate.trim().equals("")) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "getInputData";
			tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operate����ڵ����ʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		return true;
	}

	/**
	 * ���ݲ�����ҵ���� ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
	 * 
	 * @return boolean
	 */
	private boolean dealData() {
		if (mOperate.trim().equals("A0000000701")) {
			if (!ExecuteA0000000701()) {
				// @@������
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				return false;

			}
			return true;
		} else {
			if (!Execute()) {
				// @@������
				// this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				return false;
			}
		}

		return true;
	}

	/**
	 * ִ�гб����������˹��˱�������� ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
	 * 
	 * @return boolean
	 */
	private boolean Execute() {
		mResult.clear();

		VData tVData = new VData();
		ActivityOperator tActivityOperator = new ActivityOperator();

		// ��õ�ǰ�������������ID
		String tMissionID = (String) mTransferData.getValueByName("MissionID");
		String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
		if (tMissionID == null) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "Execute0000000000";
			tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		if (tSubMissionID == null) {
			// @@������
			// this.mErrors.copyAllErrors( tLCPolDB.mErrors );
			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "Execute0000000000";
			tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
			this.mErrors.addOneError(tError);

			return false;
		}

		try {
			if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, mOperate, mInputData)) {
				// @@������
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);

				return false;
			}

			// ���ִ�гб����������˹��˱��������Ľ��
			tVData = mActivityOperator.getResult();
			if (tVData != null) {
				for (int i = 0; i < tVData.size(); i++) {
					VData tempVData = new VData();
					tempVData = (VData) tVData.get(i);
					mResult.add(tempVData);
				}
			}

			// ����ִ����б����������˹��˱��������������ڵ�
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
			// @@������
			ex.printStackTrace();
			this.mErrors.copyAllErrors(mActivityOperator.mErrors);

			CError tError = new CError();
			tError.moduleName = "GrpTbWorkFlowBL";
			tError.functionName = "dealData";
			tError.errorMessage = "����������ִ������Լ����������!";
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
		// ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
		try {
			System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
			if (CheckGrpFirstTrial()) {
				return true;
			}
			// ����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
			// LWMissionSchema tLWMissionSchema = new LWMissionSchema();
			if (tActivityOperator.CreateStartMission("0000000007", "0000000701", mInputData)) {
				VData tempVData = new VData();
				tempVData = tActivityOperator.getResult();
				mResult.add(tempVData);
				tempVData = null;
			} else {
				// @@������
				this.mErrors.copyAllErrors(mActivityOperator.mErrors);
				// CError tError = new CError();
				// tError.moduleName = "TbWorkFlowBL";
				// tError.functionName = "Execute9999999999";
				// tError.errorMessage = "���������湤�������쳣!";
				// this.mErrors .addOneError(tError) ;
				return false;
			}
		} catch (Exception ex) {
			// @@������
			this.mErrors.copyAllErrors(mActivityOperator.mErrors);
			CError tError = new CError();
			tError.moduleName = "TbWorkFlowBL";
			tError.functionName = "Execute0000000701";
			tError.errorMessage = "���������湤�������쳣!";
			this.mErrors.addOneError(tError);
			return false;
		}

		return true;
	}

	/**
	 * ׼����Ҫ���������
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
				+ "' and activityid = '0000002096'", "DELETE"); // ɾ����ǰ�Ľڵ�
		tLWMissionSchema.setActivityID("0000002098");
		if (mTransferData.getValueByName("SubType") != null) {
			tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
		}
		tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
		tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
		tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
		tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
		map.put(tLWMissionSchema, "INSERT"); // �����µĽڵ�
		tVData.add(map);
		mResult.add(tVData);
		return true;
	}

}
