package com.sinosoft.test;

import com.sinosoft.lis.db.LCComYearInfoDB;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.LCComYearInfoSchema;
import com.sinosoft.lis.vschema.LCComYearInfoSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.VData;

/**
 * 06年机构变码汇总表合并处理
 * 
 * @author jerry
 * 
 */
public class Table6Data {

	private MMap map = new MMap();
	
	public void dataUpdate() {
		LCComYearInfoDB mLCComYearInfoDB = new LCComYearInfoDB();
		LCComYearInfoSet mLCComYearInfoSet = new LCComYearInfoSet();
		mLCComYearInfoSet = mLCComYearInfoDB
				.executeQuery("select * from lccomyearinfo a where year='2006' and not exists ( select 'x' from ltsendorgan b where b.organcomcode=a.sendcomcode and calmonth='200612') order by sendcomcode");
		if (mLCComYearInfoSet.size() > 0) {
			for (int i = 1; i <= mLCComYearInfoSet.size(); i++) {
				System.out.println("第"+i+"行");
				LCComYearInfoDB aLCComYearInfoDB = new LCComYearInfoDB();
				LCComYearInfoSet aLCComYearInfoSet = new LCComYearInfoSet();
				aLCComYearInfoDB.setGrpContNo(mLCComYearInfoSet.get(i).getGrpContNo());
				aLCComYearInfoDB.setYear("2006");
				aLCComYearInfoDB.setContFlag(mLCComYearInfoSet.get(i).getContFlag());
				aLCComYearInfoDB.setContNoSub(mLCComYearInfoSet.get(i).getContNoSub());
				aLCComYearInfoDB.setSendStandard(mLCComYearInfoSet.get(i).getSendStandard());
				aLCComYearInfoDB.setRetireType(mLCComYearInfoSet.get(i).getRetireType());
				aLCComYearInfoDB.setProposalState(mLCComYearInfoSet.get(i).getProposalState());
				aLCComYearInfoDB.setStockFlag(mLCComYearInfoSet.get(i).getStockFlag());
				aLCComYearInfoDB.setORGANCOMCODE(mLCComYearInfoSet.get(i).getORGANCOMCODE());
				aLCComYearInfoDB.setORGANINNERCODE(mLCComYearInfoSet.get(i).getORGANINNERCODE());
				if("00002103".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("21031703");
				}else if("00002109".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("1112");
				}else if("2110".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("21101425");
				}else if("2111".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("21111703");
				}else if("2203".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("22031702");
				}else if("2212".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("3008");
				}else if("2213".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("3008");
				}else if("2301".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("1601");
				}else if("2405".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("24051719");
				}else if("2502".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("25021701");
				}else if("2507".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("1114");
				}else if("4001".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("400117031701");
				}else if("4003".equals(mLCComYearInfoSet.get(i).getSendComCode())){
					aLCComYearInfoDB.setSendComCode("40031704");
				}
				if(aLCComYearInfoDB.getInfo()){
					aLCComYearInfoSet = aLCComYearInfoDB.query();
					if(aLCComYearInfoSet.size()!=1){
						System.out.println("错误，返回数据多于一行，条件为："+mLCComYearInfoSet.get(i).getGrpContNo()+"--"+mLCComYearInfoSet.get(i).getContFlag()+"--"+mLCComYearInfoSet.get(i).getContNoSub()+"--"+mLCComYearInfoSet.get(i).getSendStandard()+"--"+mLCComYearInfoSet.get(i).getRetireType()+"--"+mLCComYearInfoSet.get(i).getProposalState()+"--"+mLCComYearInfoSet.get(i).getStockFlag()+"--"+mLCComYearInfoSet.get(i).getORGANCOMCODE()+"--"+aLCComYearInfoDB.getSendComCode());
						continue;
					}else{
						aLCComYearInfoSet.get(1).setLastNum(aLCComYearInfoSet.get(1).getLastNum()+mLCComYearInfoSet.get(i).getLastNum());
						aLCComYearInfoSet.get(1).setAVNum(aLCComYearInfoSet.get(1).getAVNum()+mLCComYearInfoSet.get(i).getAVNum());
						aLCComYearInfoSet.get(1).setSRNum(aLCComYearInfoSet.get(1).getSRNum()+mLCComYearInfoSet.get(i).getSRNum());
						aLCComYearInfoSet.get(1).setDRNum(aLCComYearInfoSet.get(1).getDRNum()+mLCComYearInfoSet.get(i).getDRNum());
						aLCComYearInfoSet.get(1).setDRSPNum(aLCComYearInfoSet.get(1).getDRSPNum()+mLCComYearInfoSet.get(i).getDRSPNum());
						aLCComYearInfoSet.get(1).setIINum(aLCComYearInfoSet.get(1).getIINum()+mLCComYearInfoSet.get(i).getIINum());
						aLCComYearInfoSet.get(1).setSSNewNum(aLCComYearInfoSet.get(1).getSSNewNum()+mLCComYearInfoSet.get(i).getSSNewNum());
						aLCComYearInfoSet.get(1).setDVNum(aLCComYearInfoSet.get(1).getDVNum()+mLCComYearInfoSet.get(i).getDVNum());
						aLCComYearInfoSet.get(1).setDVSPNum(aLCComYearInfoSet.get(1).getDVSPNum()+mLCComYearInfoSet.get(i).getDVSPNum());
						aLCComYearInfoSet.get(1).setSPNum(aLCComYearInfoSet.get(1).getSPNum()+mLCComYearInfoSet.get(i).getSPNum());
						aLCComYearInfoSet.get(1).setIONum(aLCComYearInfoSet.get(1).getIONum()+mLCComYearInfoSet.get(i).getIONum());
						aLCComYearInfoSet.get(1).setSSOldNum(aLCComYearInfoSet.get(1).getSSOldNum()+mLCComYearInfoSet.get(i).getSSOldNum());
						aLCComYearInfoSet.get(1).setNowNum(aLCComYearInfoSet.get(1).getNowNum()+mLCComYearInfoSet.get(i).getNowNum());
						aLCComYearInfoSet.get(1).setARNum(aLCComYearInfoSet.get(1).getARNum()+mLCComYearInfoSet.get(i).getARNum());
						aLCComYearInfoSet.get(1).setPDNum(aLCComYearInfoSet.get(1).getPDNum()+mLCComYearInfoSet.get(i).getPDNum());
						aLCComYearInfoSet.get(1).setSDNum(aLCComYearInfoSet.get(1).getSDNum()+mLCComYearInfoSet.get(i).getSDNum());
						aLCComYearInfoSet.get(1).setDINum(aLCComYearInfoSet.get(1).getDINum()+mLCComYearInfoSet.get(i).getDINum());
						aLCComYearInfoSet.get(1).setRINum(aLCComYearInfoSet.get(1).getRINum()+mLCComYearInfoSet.get(i).getRINum());
						aLCComYearInfoSet.get(1).setIPNewNum(aLCComYearInfoSet.get(1).getIPNewNum()+mLCComYearInfoSet.get(i).getIPNewNum());
						aLCComYearInfoSet.get(1).setIPOldNum(aLCComYearInfoSet.get(1).getIPOldNum()+mLCComYearInfoSet.get(i).getIPOldNum());
						aLCComYearInfoSet.get(1).setNowMoney(aLCComYearInfoSet.get(1).getNowMoney()+mLCComYearInfoSet.get(i).getNowMoney());
						aLCComYearInfoSet.get(1).setSumMoney(aLCComYearInfoSet.get(1).getSumMoney()+mLCComYearInfoSet.get(i).getSumMoney());
						aLCComYearInfoSet.get(1).setAVSupplyMoney(aLCComYearInfoSet.get(1).getAVSupplyMoney()+mLCComYearInfoSet.get(i).getAVSupplyMoney());
						aLCComYearInfoSet.get(1).setDRSupplyMoney(aLCComYearInfoSet.get(1).getDRSupplyMoney()+mLCComYearInfoSet.get(i).getDRSupplyMoney());
						aLCComYearInfoSet.get(1).setSRSupplyMoney(aLCComYearInfoSet.get(1).getSRSupplyMoney()+mLCComYearInfoSet.get(i).getSRSupplyMoney());
						aLCComYearInfoSet.get(1).setSDSupplyMoney(aLCComYearInfoSet.get(1).getSDSupplyMoney()+mLCComYearInfoSet.get(i).getSDSupplyMoney());
						aLCComYearInfoSet.get(1).setPDSupplyMoney(aLCComYearInfoSet.get(1).getPDSupplyMoney()+mLCComYearInfoSet.get(i).getPDSupplyMoney());
						aLCComYearInfoSet.get(1).setSSSupplyMoney(aLCComYearInfoSet.get(1).getSSSupplyMoney()+mLCComYearInfoSet.get(i).getSSSupplyMoney());
						aLCComYearInfoSet.get(1).setITSupplyMoney(aLCComYearInfoSet.get(1).getITSupplyMoney()+mLCComYearInfoSet.get(i).getITSupplyMoney());
						aLCComYearInfoSet.get(1).setDISupplyMoney(aLCComYearInfoSet.get(1).getDISupplyMoney()+mLCComYearInfoSet.get(i).getDISupplyMoney());
						aLCComYearInfoSet.get(1).setIPSupplyMoney(aLCComYearInfoSet.get(1).getIPSupplyMoney()+mLCComYearInfoSet.get(i).getIPSupplyMoney());
						aLCComYearInfoSet.get(1).setDVDeduckMoney(aLCComYearInfoSet.get(1).getDVDeduckMoney()+mLCComYearInfoSet.get(i).getDVDeduckMoney());
						aLCComYearInfoSet.get(1).setARDeduckMoney(aLCComYearInfoSet.get(1).getARDeduckMoney()+mLCComYearInfoSet.get(i).getARDeduckMoney());
						aLCComYearInfoSet.get(1).setSPDeduckMoney(aLCComYearInfoSet.get(1).getSPDeduckMoney()+mLCComYearInfoSet.get(i).getSPDeduckMoney());
						aLCComYearInfoSet.get(1).setSDDeduckMoney(aLCComYearInfoSet.get(1).getSDDeduckMoney()+mLCComYearInfoSet.get(i).getSDDeduckMoney());
						aLCComYearInfoSet.get(1).setPDDeduckMoney(aLCComYearInfoSet.get(1).getPDDeduckMoney()+mLCComYearInfoSet.get(i).getPDDeduckMoney());
						aLCComYearInfoSet.get(1).setSSDeduckMoney(aLCComYearInfoSet.get(1).getSSDeduckMoney()+mLCComYearInfoSet.get(i).getSSDeduckMoney());
						aLCComYearInfoSet.get(1).setITDeduckMoney(aLCComYearInfoSet.get(1).getITDeduckMoney()+mLCComYearInfoSet.get(i).getITDeduckMoney());
						aLCComYearInfoSet.get(1).setRIDeduckMoney(aLCComYearInfoSet.get(1).getRIDeduckMoney()+mLCComYearInfoSet.get(i).getRIDeduckMoney());
						aLCComYearInfoSet.get(1).setIPDeduckMoney(aLCComYearInfoSet.get(1).getIPDeduckMoney()+mLCComYearInfoSet.get(i).getIPDeduckMoney());
						
						map.put(aLCComYearInfoSet.get(1),"UPDATE");
					}
				}else{
					System.out.println("提示，没有数据需新增，条件为："+mLCComYearInfoSet.get(i).getGrpContNo()+"--"+mLCComYearInfoSet.get(i).getContFlag()+"--"+mLCComYearInfoSet.get(i).getContNoSub()+"--"+mLCComYearInfoSet.get(i).getSendStandard()+"--"+mLCComYearInfoSet.get(i).getRetireType()+"--"+mLCComYearInfoSet.get(i).getProposalState()+"--"+mLCComYearInfoSet.get(i).getStockFlag()+"--"+mLCComYearInfoSet.get(i).getORGANCOMCODE()+"--"+aLCComYearInfoDB.getSendComCode());
					

					LCComYearInfoSchema rbLCComYearInfoSechma = new LCComYearInfoSchema();
					rbLCComYearInfoSechma.setGrpContNo(aLCComYearInfoDB.getGrpContNo());
					rbLCComYearInfoSechma.setYear("2006");
					rbLCComYearInfoSechma.setGroupLevel("1");
					rbLCComYearInfoSechma.setContFlag(aLCComYearInfoDB.getContFlag());
					rbLCComYearInfoSechma.setContNoSub(aLCComYearInfoDB.getContNoSub());
					rbLCComYearInfoSechma.setProposalState(aLCComYearInfoDB.getProposalState());
					rbLCComYearInfoSechma.setStockFlag(aLCComYearInfoDB.getStockFlag());
					
					if("00002103".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("21031703");
						rbLCComYearInfoSechma.setSendInnerCode("860771");
					}else if("00002109".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("1112");
						rbLCComYearInfoSechma.setSendInnerCode("860853");
					}else if("2110".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("21101425");
						rbLCComYearInfoSechma.setSendInnerCode("860802");
					}else if("2111".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("21111703");
						rbLCComYearInfoSechma.setSendInnerCode("860803");
					}else if("2203".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("22031702");
						rbLCComYearInfoSechma.setSendInnerCode("860806");
					}else if("2212".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("3008");
						rbLCComYearInfoSechma.setSendInnerCode("860849");
					}else if("2213".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("3008");
						rbLCComYearInfoSechma.setSendInnerCode("860849");
					}else if("2301".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("1601");
						rbLCComYearInfoSechma.setSendInnerCode("860852");
					}else if("2405".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("24051719");
						rbLCComYearInfoSechma.setSendInnerCode("860845");
					}else if("2502".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("25021701");
						rbLCComYearInfoSechma.setSendInnerCode("860846");
					}else if("2507".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("1114");
						rbLCComYearInfoSechma.setSendInnerCode("860850");
					}else if("4001".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("400117031701");
						rbLCComYearInfoSechma.setSendInnerCode("860834");
					}else if("4003".equals(mLCComYearInfoSet.get(i).getSendComCode())){
						rbLCComYearInfoSechma.setSendComCode("40031704");
						rbLCComYearInfoSechma.setSendInnerCode("860844");
					}
					
					rbLCComYearInfoSechma.setORGANCOMCODE(aLCComYearInfoDB.getORGANCOMCODE());
					rbLCComYearInfoSechma.setORGANINNERCODE(aLCComYearInfoDB.getORGANINNERCODE());
					rbLCComYearInfoSechma.setRetireType(aLCComYearInfoDB.getRetireType());
					rbLCComYearInfoSechma.setSendStandard(aLCComYearInfoDB.getSendStandard());
					rbLCComYearInfoSechma.setLastNum(mLCComYearInfoSet.get(i).getLastNum());
					rbLCComYearInfoSechma.setAVNum(mLCComYearInfoSet.get(i).getAVNum());
					rbLCComYearInfoSechma.setSRNum(mLCComYearInfoSet.get(i).getSRNum());
					rbLCComYearInfoSechma.setDRNum(mLCComYearInfoSet.get(i).getDRNum());
					rbLCComYearInfoSechma.setDRSPNum(mLCComYearInfoSet.get(i).getDRSPNum());
					rbLCComYearInfoSechma.setIINum(mLCComYearInfoSet.get(i).getIINum());
					rbLCComYearInfoSechma.setSSNewNum(mLCComYearInfoSet.get(i).getSSNewNum());
					rbLCComYearInfoSechma.setDVNum(mLCComYearInfoSet.get(i).getDVNum());
					rbLCComYearInfoSechma.setDVSPNum(mLCComYearInfoSet.get(i).getDVSPNum());
					rbLCComYearInfoSechma.setSPNum(mLCComYearInfoSet.get(i).getSPNum());
					rbLCComYearInfoSechma.setIONum(mLCComYearInfoSet.get(i).getIONum());
					rbLCComYearInfoSechma.setSSOldNum(mLCComYearInfoSet.get(i).getSSOldNum());
					rbLCComYearInfoSechma.setNowNum(mLCComYearInfoSet.get(i).getNowNum());
					rbLCComYearInfoSechma.setARNum(mLCComYearInfoSet.get(i).getARNum());
					rbLCComYearInfoSechma.setPDNum(mLCComYearInfoSet.get(i).getPDNum());
					rbLCComYearInfoSechma.setSDNum(mLCComYearInfoSet.get(i).getSDNum());
					rbLCComYearInfoSechma.setDINum(mLCComYearInfoSet.get(i).getDINum());
					rbLCComYearInfoSechma.setRINum(mLCComYearInfoSet.get(i).getRINum());
					rbLCComYearInfoSechma.setIPNewNum(mLCComYearInfoSet.get(i).getIPNewNum());
					rbLCComYearInfoSechma.setIPOldNum(mLCComYearInfoSet.get(i).getIPOldNum());
					rbLCComYearInfoSechma.setNowMoney(mLCComYearInfoSet.get(i).getNowMoney());
					rbLCComYearInfoSechma.setSumMoney(mLCComYearInfoSet.get(i).getSumMoney());
					rbLCComYearInfoSechma.setAVSupplyMoney(mLCComYearInfoSet.get(i).getAVSupplyMoney());
					rbLCComYearInfoSechma.setDRSupplyMoney(mLCComYearInfoSet.get(i).getDRSupplyMoney());
					rbLCComYearInfoSechma.setSRSupplyMoney(mLCComYearInfoSet.get(i).getSRSupplyMoney());
					rbLCComYearInfoSechma.setSDSupplyMoney(mLCComYearInfoSet.get(i).getSDSupplyMoney());
					rbLCComYearInfoSechma.setPDSupplyMoney(mLCComYearInfoSet.get(i).getPDSupplyMoney());
					rbLCComYearInfoSechma.setSSSupplyMoney(mLCComYearInfoSet.get(i).getSSSupplyMoney());
					rbLCComYearInfoSechma.setITSupplyMoney(mLCComYearInfoSet.get(i).getITSupplyMoney());
					rbLCComYearInfoSechma.setDISupplyMoney(mLCComYearInfoSet.get(i).getDISupplyMoney());
					rbLCComYearInfoSechma.setIPSupplyMoney(mLCComYearInfoSet.get(i).getIPSupplyMoney());
					rbLCComYearInfoSechma.setDVDeduckMoney(mLCComYearInfoSet.get(i).getDVDeduckMoney());
					rbLCComYearInfoSechma.setARDeduckMoney(mLCComYearInfoSet.get(i).getARDeduckMoney());
					rbLCComYearInfoSechma.setSPDeduckMoney(mLCComYearInfoSet.get(i).getSPDeduckMoney());
					rbLCComYearInfoSechma.setSDDeduckMoney(mLCComYearInfoSet.get(i).getSDDeduckMoney());
					rbLCComYearInfoSechma.setPDDeduckMoney(mLCComYearInfoSet.get(i).getPDDeduckMoney());
					rbLCComYearInfoSechma.setSSDeduckMoney(mLCComYearInfoSet.get(i).getSSDeduckMoney());
					rbLCComYearInfoSechma.setITDeduckMoney(mLCComYearInfoSet.get(i).getITDeduckMoney());
					rbLCComYearInfoSechma.setRIDeduckMoney(mLCComYearInfoSet.get(i).getRIDeduckMoney());
					rbLCComYearInfoSechma.setIPDeduckMoney(mLCComYearInfoSet.get(i).getIPDeduckMoney());
					
					map.put(rbLCComYearInfoSechma,"INSERT");
				}
				if (!Submit()) {
					System.out.println("错误：更新失败！！！！");
					break;
				}
			}
		}
	}
	public boolean Submit() {
		PubSubmit tPubSubmit = new PubSubmit();
		VData mResult = new VData();
		
		mResult.add(map);
		if (!tPubSubmit.submitData(mResult, "")) {
			// @@错误处理
			CError.buildErr(this, "PubSubmit提交数据失败");
			map = null;
			mResult.clear();
			mResult = null;
			return false;
		}
		map = new MMap();
		return true;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		Table6Data td = new Table6Data();
		td.dataUpdate();
	}

}
