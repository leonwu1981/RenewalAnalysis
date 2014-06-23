package com.sinosoft.lis.pubfun;

import java.util.Vector;
import com.sinosoft.lis.schema.*;
import com.sinosoft.utility.VData;

/**
 * <p>
 * Title: Web业务系统
 * </p>
 * <p>
 * ClassName: CacheCont
 * </p>
 * <p>
 * Description: 用于磁盘投保缓存某个计划的承保数据结构
 * </p>
 * 
 * @author ZhuXF
 * @version 1.0
 * @modify 2008-5-28
 * @depict 人生五十載，去事恍如夢幻，普天之下，豈有長生不滅者！
 */
public class CacheCont {
	public String Id;

	// 特定被保人特定计划下的所有险种的数据结构，包括LCPol/LCDuty/LCPrem/LCGet
	// 附加了LCDutyCtrl，LCDutyCtrlParam，LMRisk,LMRiskApp
	private Vector PolInfo = new Vector();

	// 特定被保人合同数据机构
	private LCContSchema LCContSchmea = new LCContSchema();

	// 特定被保人数据结构
	private LCInsuredSchema LCInsuredSchema = new LCInsuredSchema();

	// 特定计划下的险种个数
	private int PolInfoNum = 0;

	// 保全增人时用到的表信息
	// 特定被保人的保全增人结构
	private LPGrpEdorItemSchema LPGrpEdorItemSchema = new LPGrpEdorItemSchema();// 团险保全项目表

	private LPGrpEdorMainSchema LPGrpEdorMainSchema = new LPGrpEdorMainSchema();// 团险保全批改表

	private LPEdorItemSchema LPEdorItemSchema = new LPEdorItemSchema();// 个险保全项目表

	private LPEdorMainSchema LPEdorMainSchema = new LPEdorMainSchema();// 个险保全批改表

	// 用来表示次缓存是否提交过数据库
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
