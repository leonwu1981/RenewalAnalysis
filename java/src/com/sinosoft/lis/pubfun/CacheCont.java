package com.sinosoft.lis.pubfun;

import java.util.Vector;
import com.sinosoft.lis.schema.*;
import com.sinosoft.utility.VData;

/**
 * <p>
 * Title: Webҵ��ϵͳ
 * </p>
 * <p>
 * ClassName: CacheCont
 * </p>
 * <p>
 * Description: ���ڴ���Ͷ������ĳ���ƻ��ĳб����ݽṹ
 * </p>
 * 
 * @author ZhuXF
 * @version 1.0
 * @modify 2008-5-28
 * @depict ������ʮ�d��ȥ�»�����ã�����֮�£��M���L�������ߣ�
 */
public class CacheCont {
	public String Id;

	// �ض��������ض��ƻ��µ��������ֵ����ݽṹ������LCPol/LCDuty/LCPrem/LCGet
	// ������LCDutyCtrl��LCDutyCtrlParam��LMRisk,LMRiskApp
	private Vector PolInfo = new Vector();

	// �ض������˺�ͬ���ݻ���
	private LCContSchema LCContSchmea = new LCContSchema();

	// �ض����������ݽṹ
	private LCInsuredSchema LCInsuredSchema = new LCInsuredSchema();

	// �ض��ƻ��µ����ָ���
	private int PolInfoNum = 0;

	// ��ȫ����ʱ�õ��ı���Ϣ
	// �ض������˵ı�ȫ���˽ṹ
	private LPGrpEdorItemSchema LPGrpEdorItemSchema = new LPGrpEdorItemSchema();// ���ձ�ȫ��Ŀ��

	private LPGrpEdorMainSchema LPGrpEdorMainSchema = new LPGrpEdorMainSchema();// ���ձ�ȫ���ı�

	private LPEdorItemSchema LPEdorItemSchema = new LPEdorItemSchema();// ���ձ�ȫ��Ŀ��

	private LPEdorMainSchema LPEdorMainSchema = new LPEdorMainSchema();// ���ձ�ȫ���ı�

	// ������ʾ�λ����Ƿ��ύ�����ݿ�
	private boolean Flag = false;

	public boolean isFlag() {
		return Flag;
	}

	public void setFlag(boolean flag) {
		Flag = flag;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public void addPolInfo(VData polInfo) {
		PolInfo.add(polInfo);
		PolInfoNum++;
	}

	public VData getPolInfo(int i) {
		return (VData) PolInfo.get(i);
	}

	public int getPolInfoNum() {
		return PolInfoNum;
	}

	public LCInsuredSchema getLCInsuredSchema() {
		return LCInsuredSchema;
	}

	public void setLCInsuredSchema(LCInsuredSchema insuredSchema) {
		LCInsuredSchema = insuredSchema;
	}

	public LCContSchema getLCContSchmea() {
		return LCContSchmea;
	}

	public void setLCContSchmea(LCContSchema contSchmea) {
		LCContSchmea = contSchmea;
	}

	public LPEdorItemSchema getLPEdorItemSchema() {
		return LPEdorItemSchema;
	}

	public void setLPEdorItemSchema(LPEdorItemSchema edorItemSchema) {
		LPEdorItemSchema = edorItemSchema;
	}

	public LPGrpEdorItemSchema getLPGrpEdorItemSchema() {
		return LPGrpEdorItemSchema;
	}

	public void setLPGrpEdorItemSchema(LPGrpEdorItemSchema grpEdorItemSchema) {
		LPGrpEdorItemSchema = grpEdorItemSchema;
	}

	public LPGrpEdorMainSchema getLPGrpEdorMainSchema() {
		return LPGrpEdorMainSchema;
	}

	public void setLPGrpEdorMainSchema(LPGrpEdorMainSchema grpEdorMainSchema) {
		LPGrpEdorMainSchema = grpEdorMainSchema;
	}

	public LPEdorMainSchema getLPEdorMainSchema() {
		return LPEdorMainSchema;
	}

	public void setLPEdorMainSchema(LPEdorMainSchema edorMainSchema) {
		LPEdorMainSchema = edorMainSchema;
	}
}
