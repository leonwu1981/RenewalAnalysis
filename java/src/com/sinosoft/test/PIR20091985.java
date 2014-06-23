package com.sinosoft.test;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.GrpAcomDataSchema;
import com.sinosoft.lis.schema.GrpAcomDataTmpSchema;
import com.sinosoft.lis.vschema.GrpAcomDataSet;
import com.sinosoft.lis.db.GrpAcomDataDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.GrpPrmmDataSchema;
import com.sinosoft.lis.vschema.GrpPrmmDataSet;
import com.sinosoft.lis.db.GrpPrmmDataDB;
import com.sinosoft.lis.schema.GrpPrmmDataTmpSchema;

import java.io.*;
import java.util.*;
import com.sinosoft.utility.*;
public class PIR20091985 {

	/**
	 * @param args
	 */
	ExeSQL exesql = new ExeSQL();
	MMap mMMap = new MMap();
	List list = new ArrayList();
	int GrpAcomData=0;
	int GrpAcomDataTmp=0;
	int GrpPrmmData=0;
	int GrpPrmmDataTmp=0;
	public static void main(String[] args) 
	{
		// TODO 自动生成方法存根
		PIR20091985 tPIR20091985 = new PIR20091985();
		if(tPIR20091985.start())
		{
			System.out.println("ok");
		}
		else
		{
			System.out.println("fail");
		}
		
	}

	private boolean start() 
	{
		// TODO 自动生成方法存根
		boolean flag=false;
		SSRS allssrs = new SSRS();
		String sql=" select policyno,productcode,batchdate,sum(premamt),makedate from grpacomdata  bb"
			      +" where operationtype = 'DJ'"
			      +" and commtype = 'PC' and makedate >= '2009-3-20'"
			     // +" and policyno ='86200256800'"
			      +" group by policyno, bb.productcode, batchdate,makedate"
			      +" order by policyno,batchdate,productcode";
		allssrs=exesql.execSQL(sql);
		System.out.println("sum :"+allssrs.getMaxRow());
		if(null!=allssrs&&allssrs.getMaxRow()>0)
		{
			for(int i=1;i<=allssrs.getMaxRow();i++)
			{
				if(allssrs.GetText(i, 1).equals("880006924")&&allssrs.GetText(i, 5).equals("2009-04-25"))
				{
					System.out.println("-------------------------------------------------");
					continue;
				}
				String grpcontno=allssrs.GetText(i, 1);
				String RiskCode=allssrs.GetText(i, 2);
				String BatchDate = allssrs.GetText(i, 3);
				String MakeDate = allssrs.GetText(i, 3);
				
				String unionsql = " SELECT "
				    +" nvl(sum(-n.sumactupaymoney * nvl(l.BusiRate, 0) / 100), 0),"
					+" (d.money  * nvl(l.BusiRate, 0) / 100),"
					+" n.PayCount,d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate + 11,'',nvl(l.BusiRate, 0)"
					+" from "
					+"  (select t.balancerelano nunmber,t.payno payno,t.grpcontno "
					+"    from "
					+"       (select r.balancerelano,r.payno,nvl(sum(r.balancerelasum),0) money,r.grpcontno"
					+"         from ljpbalancerela r where  r.BalanceRelaState = '1'"//r.balancerelasum <> 0 and应该查询定期结算已结算状态数据，而不是未结算(未结算记录还可撤消)记录
					+"         group by r.balancerelano, r.payno, r.grpcontno) t,"
					+"       (select s.balancerelano, nvl(sum(s.money),0) money  from ljpbalancerelasub s"
					+"          group by s.balancerelano) n"//where s.money <> 0
					+"    where t.balancerelano = n.balancerelano ) "//and n.money <> t.money
					+" a,"
					+" (select '1' PayCount, sumactupaymoney, payno, grpcontno, riskcode from ljapayperson"
					+"  union   "
					+"  select '1' PayCount,getmoney  as sumactupaymoney, actugetno  as payno,grpcontno,riskcode from ljagetendorse)"
					+" n,"
					+" ljpbalancerelasub d,lcgrpcont e,LagrpCommisiondetail l"
					+" where n.payno = a.payno and d.riskcode='"+RiskCode+"'"
					+" and n.grpcontno = e.grpcontno and d.balancerelano = a.nunmber and a.grpcontno = e.grpcontno "
					+" and n.grpcontno = l.grpcontno  AND e.GrpContNo <> '99029288' and e.grpcontno = '"+grpcontno+"' "//and n.sumactupaymoney <> 0
					+" and n.riskcode=d.riskcode"
					+" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno)"
					+" AND d.MakeDate = TO_DATE('"+BatchDate+"', 'yyyy-mm-dd') "
					+" group by d.money,n.PayCount, d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate,l.BusiRate"
					+" union all"
					+" SELECT "
				    +" nvl(sum(-n.sumactupaymoney * nvl(l.BusiRate, 0) / 100), 0),"
					+" (d.money  * nvl(l.BusiRate, 0) / 100),"
					+" n.PayCount,d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate + 11,'',nvl(l.BusiRate, 0)"
					+" from "
					+"  (select t.balancerelano nunmber,t.payno payno,t.grpcontno "
					+"    from "
					+"       (select r.balancerelano,r.payno,nvl(sum(r.balancerelasum),0) money,r.grpcontno"
					+"         from ljpbalancerela r where  r.BalanceRelaState = '1'"//r.balancerelasum <> 0 and应该查询定期结算已结算状态数据，而不是未结算(未结算记录还可撤消)记录
					+"         group by r.balancerelano, r.payno, r.grpcontno) t,"
					+"       (select s.balancerelano, nvl(sum(s.money),0) money from ljpbalancerelasub s"
					+"          group by s.balancerelano) n"//where s.money <> 0
					+"    where t.balancerelano = n.balancerelano ) "//and n.money <> t.money
					+" a,"
					+" (select '1' PayCount, sumactupaymoney, payno, grpcontno, riskcode from ljapayperson"
					+"  union   "
					+"  select '1' PayCount,getmoney as sumactupaymoney, actugetno as payno,grpcontno,riskcode from ljagetendorse)"
					+" n,"
					+" ljpbalancerelasub d,lbgrpcont e,LagrpCommisiondetail l"
					+" where n.payno = a.payno and d.riskcode='"+RiskCode+"'"
					+" and n.grpcontno = e.grpcontno and d.balancerelano = a.nunmber and a.grpcontno = e.grpcontno "
					+" and n.grpcontno = l.grpcontno  AND e.GrpContNo <> '99029288' and e.grpcontno = '"+grpcontno+"'"//and n.sumactupaymoney <> 0
					+" and n.riskcode=d.riskcode"
					+" AND d.MakeDate = TO_DATE('"+BatchDate+"', 'yyyy-mm-dd') "
					+" group by d.money,n.PayCount, d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate,l.BusiRate"
					;
				
				String unionAllsql = " SELECT "
				    +" nvl(sum(-n.sumactupaymoney * nvl(l.BusiRate, 0) / 100), 0),"
					+" (d.money  * nvl(l.BusiRate, 0) / 100),"
					+" n.PayCount,d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate + 11,'',nvl(l.BusiRate, 0)"
					+" from "
					+"  (select t.balancerelano nunmber,t.payno payno,t.grpcontno "
					+"    from "
					+"       (select r.balancerelano,r.payno,nvl(sum(r.balancerelasum),0) money,r.grpcontno"
					+"         from ljpbalancerela r where  r.BalanceRelaState = '1'"//r.balancerelasum <> 0 and应该查询定期结算已结算状态数据，而不是未结算(未结算记录还可撤消)记录
					+"         group by r.balancerelano, r.payno, r.grpcontno) t,"
					+"       (select s.balancerelano, nvl(sum(s.money),0) money  from ljpbalancerelasub s"
					+"          group by s.balancerelano) n"//where s.money <> 0
					+"    where t.balancerelano = n.balancerelano ) "//and n.money <> t.money
					+" a,"
					+" (select '1' PayCount, sumactupaymoney, payno, grpcontno, riskcode from ljapayperson"
					+"  union  all "
					+"  select '1' PayCount,getmoney  as sumactupaymoney, actugetno  as payno,grpcontno,riskcode from ljagetendorse)"
					+" n,"
					+" ljpbalancerelasub d,lcgrpcont e,LagrpCommisiondetail l"
					+" where n.payno = a.payno and d.riskcode='"+RiskCode+"'"
					+" and n.grpcontno = e.grpcontno and d.balancerelano = a.nunmber and a.grpcontno = e.grpcontno "
					+" and n.grpcontno = l.grpcontno  AND e.GrpContNo <> '99029288' and e.grpcontno = '"+grpcontno+"' "//and n.sumactupaymoney <> 0
					+" and n.riskcode=d.riskcode"
					+" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno)"
					+" AND d.MakeDate = TO_DATE('"+BatchDate+"', 'yyyy-mm-dd') "
					+" group by d.money,n.PayCount, d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate,l.BusiRate"
					+" union all"
					+" SELECT "
				    +" nvl(sum(-n.sumactupaymoney * nvl(l.BusiRate, 0) / 100), 0),"
					+" (d.money  * nvl(l.BusiRate, 0) / 100),"
					+" n.PayCount,d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate + 11,'',nvl(l.BusiRate, 0)"
					+" from "
					+"  (select t.balancerelano nunmber,t.payno payno,t.grpcontno "
					+"    from "
					+"       (select r.balancerelano,r.payno,nvl(sum(r.balancerelasum),0) money,r.grpcontno"
					+"         from ljpbalancerela r where  r.BalanceRelaState = '1'"//r.balancerelasum <> 0 and应该查询定期结算已结算状态数据，而不是未结算(未结算记录还可撤消)记录
					+"         group by r.balancerelano, r.payno, r.grpcontno) t,"
					+"       (select s.balancerelano, nvl(sum(s.money),0) money from ljpbalancerelasub s"
					+"          group by s.balancerelano) n"//where s.money <> 0
					+"    where t.balancerelano = n.balancerelano ) "//and n.money <> t.money
					+" a,"
					+" (select '1' PayCount, sumactupaymoney, payno, grpcontno, riskcode from ljapayperson"
					+"  union  all "
					+"  select '1' PayCount,getmoney as sumactupaymoney, actugetno as payno,grpcontno,riskcode from ljagetendorse)"
					+" n,"
					+" ljpbalancerelasub d,lbgrpcont e,LagrpCommisiondetail l"
					+" where n.payno = a.payno and d.riskcode='"+RiskCode+"'"
					+" and n.grpcontno = e.grpcontno and d.balancerelano = a.nunmber and a.grpcontno = e.grpcontno "
					+" and n.grpcontno = l.grpcontno  AND e.GrpContNo <> '99029288' and e.grpcontno = '"+grpcontno+"'"//and n.sumactupaymoney <> 0
					+" and n.riskcode=d.riskcode"
					+" AND d.MakeDate = TO_DATE('"+BatchDate+"', 'yyyy-mm-dd') "
					+" group by d.money,n.PayCount, d.riskcode,e.agentcode,e.managecom,e.grpcontno,"
					+" d.MakeDate,e.GetPolDate,e.customgetpoldate,l.BusiRate"
					;
				
				SSRS unionssrs = new SSRS();
				unionssrs=exesql.execSQL(unionsql);
				SSRS unionAllssrs = new SSRS();
				unionAllssrs=exesql.execSQL(unionAllsql);
				
				if(null!=unionssrs&&unionssrs.getMaxRow()>0)
				{
					if(null!=unionAllssrs&&unionAllssrs.getMaxRow()>0)
					{
						if(unionssrs.getMaxRow()==unionAllssrs.getMaxRow())
						{
							for(int j=1;j<=unionAllssrs.getMaxRow();j++)
							{
								if(unionAllssrs.GetText(j, 1).equals(unionssrs.GetText(j, 1)))
								{
									System.out.println("grpcontno is "+grpcontno+",riskcode is "+RiskCode+"makedate is "+BatchDate+",is ok NATURE");
									flag=true;
								}
								else
								{
									System.out.println("grpcontno is "+grpcontno+",riskcode is "+RiskCode+"makedate is "+BatchDate+",is FAIL NATURE");
									String unionpremamt=unionssrs.GetText(j, 1);
								    String unionAllpremamt=unionAllssrs.GetText(j, 1);
									String querydbsql=" select * From grpacomdata "
										             +" where policyno = '"+grpcontno+"' "
										             +" and productcode = '"+RiskCode+"' "
										             +" and operationtype = 'DJ' and commtype = 'PC'"
										             +" and batchdate='"+MakeDate+"'"
										             +" and premamt='"+unionpremamt+"'";
									GrpAcomDataDB tGrpAcomDataDB = new GrpAcomDataDB();
									GrpAcomDataSet tGrpAcomDataSet = tGrpAcomDataDB.executeQuery(querydbsql);
									if(null!=tGrpAcomDataSet&&tGrpAcomDataSet.size()>0)
									{
									    for(int m=1;m<=tGrpAcomDataSet.size();m++)
									    {
									    	GrpAcomDataSchema tGrpAcomDataSchema = new GrpAcomDataSchema();
									    	tGrpAcomDataSchema=tGrpAcomDataSet.get(m);
									    	String maxserialno=tGrpAcomDataSchema.getSerialNo();
									    	maxserialno=maxserialno.substring(0, maxserialno.length()-2)+"00";
									    	tGrpAcomDataSchema.setPremAmt(tGrpAcomDataSchema.getPremAmt()*(-1));
									    	tGrpAcomDataSchema.setSerialNo(maxserialno);
									    	tGrpAcomDataSchema.setCommAmt(tGrpAcomDataSchema.getCommAmt()*(-1));
//									    	tGrpAcomDataSchema.setMakeDate(PubFun.getCurrentDate());
//									    	tGrpAcomDataSchema.setMakeTime(PubFun.getCurrentTime());
									    	list.add(getSqlGrpAcomData(tGrpAcomDataSchema));
									    	
									    	GrpAcomDataTmpSchema tGrpAcomDataTmpSchema=new GrpAcomDataTmpSchema();
									    	tGrpAcomDataTmpSchema=transafeAcom(tGrpAcomDataSchema);
									    	list.add(getSqlGrpAcomDataTmp(tGrpAcomDataTmpSchema));
									    	mMMap.put(tGrpAcomDataSchema, "INSERT");
									    	mMMap.put(tGrpAcomDataTmpSchema, "INSERT");
									    	this.GrpAcomData++;
									    	this.GrpAcomDataTmp++;
									    	
									    	GrpAcomDataSchema ttGrpAcomDataSchema = new GrpAcomDataSchema();
									    	ttGrpAcomDataSchema=tGrpAcomDataSet.get(m);
									    	String tmaxserialno=tGrpAcomDataSchema.getSerialNo();
									    	tmaxserialno=maxserialno.substring(0, maxserialno.length()-2)+"01";
									    	ttGrpAcomDataSchema.setPremAmt(unionAllpremamt);
									    	ttGrpAcomDataSchema.setCommAmt(Double.parseDouble(unionAllpremamt)*Double.parseDouble(ttGrpAcomDataSchema.getCommisionRate())/100);
									    	ttGrpAcomDataSchema.setSerialNo(tmaxserialno);
//									    	ttGrpAcomDataSchema.setMakeDate(PubFun.getCurrentDate());
//									    	ttGrpAcomDataSchema.setMakeTime(PubFun.getCurrentTime());
									    	list.add(getSqlGrpAcomData(ttGrpAcomDataSchema));
									    	
									    	GrpAcomDataTmpSchema ttGrpAcomDataTmpSchema=new GrpAcomDataTmpSchema();
									    	ttGrpAcomDataTmpSchema=transafeAcom(ttGrpAcomDataSchema);
									    	list.add(getSqlGrpAcomDataTmp(ttGrpAcomDataTmpSchema));
									    	mMMap.put(ttGrpAcomDataSchema, "INSERT");
									    	mMMap.put(ttGrpAcomDataTmpSchema, "INSERT");
									    	this.GrpAcomData++;
									    	this.GrpAcomDataTmp++;
									    }
									    
									    String querydbsql2=" select * From grpprmmdata "
								             +" where policyno = '"+grpcontno+"'"
								             +" and operationtype = 'DJ'"
								             +" and batchdate >= '"+MakeDate+"'"
								             +" and premamt='"+unionpremamt+"'"
								             +" and productcode='"+RiskCode+"'";
//								             +" and makedate='"+MakeDate+"'";
									    GrpPrmmDataDB tGrpPrmmDataDB = new GrpPrmmDataDB();
									    GrpPrmmDataSet tGrpPrmmDataSet = tGrpPrmmDataDB.executeQuery(querydbsql2);
									    if(null!=tGrpPrmmDataSet&&tGrpPrmmDataSet.size()>0)
									    {
									    	for(int n=1;n<=tGrpPrmmDataSet.size();n++)
									    	{
									    		GrpPrmmDataSchema tGrpPrmmDataSchema = new GrpPrmmDataSchema();
									    		tGrpPrmmDataSchema=tGrpPrmmDataSet.get(n);
									    		String maxserialno=tGrpPrmmDataSchema.getSerialNo();
										    	maxserialno=maxserialno.substring(0, maxserialno.length()-2)+"00";
									    		tGrpPrmmDataSchema.setPremAmt(tGrpPrmmDataSchema.getPremAmt()*(-1));
									    		tGrpPrmmDataSchema.setSerialNo(maxserialno);
//									    		tGrpPrmmDataSchema.setMakeDate(PubFun.getCurrentDate());
//									    		tGrpPrmmDataSchema.setMakeTime(PubFun.getCurrentTime());
									    		list.add(getSqlGrpPrmmData(tGrpPrmmDataSchema));
									    		
									    		GrpPrmmDataTmpSchema tGrpPrmmDataTmpSchema = new GrpPrmmDataTmpSchema();
									    		tGrpPrmmDataTmpSchema=transafePrmm(tGrpPrmmDataSchema);
									    		list.add(getSqlGrpPrmmDataTmp(tGrpPrmmDataTmpSchema));
									    		mMMap.put(tGrpPrmmDataSchema, "INSERT");
									    		mMMap.put(tGrpPrmmDataTmpSchema, "INSERT");
									    		this.GrpPrmmData++;
									    		this.GrpPrmmDataTmp++;
									    		
									    		GrpPrmmDataSchema ttGrpPrmmDataSchema = new GrpPrmmDataSchema();
									    		ttGrpPrmmDataSchema=tGrpPrmmDataSet.get(n);
									    		String tmaxserialno=ttGrpPrmmDataSchema.getSerialNo();
										    	tmaxserialno=maxserialno.substring(0, maxserialno.length()-2)+"01";
									    		ttGrpPrmmDataSchema.setPremAmt(unionAllpremamt);
									    		ttGrpPrmmDataSchema.setSerialNo(tmaxserialno);
//									    		ttGrpPrmmDataSchema.setMakeDate(PubFun.getCurrentDate());
//									    		ttGrpPrmmDataSchema.setMakeTime(PubFun.getCurrentTime());
									    		list.add(getSqlGrpPrmmData(ttGrpPrmmDataSchema));
									    		
									    		GrpPrmmDataTmpSchema ttGrpPrmmDataTmpSchema = new GrpPrmmDataTmpSchema();
									    		ttGrpPrmmDataTmpSchema=transafePrmm(ttGrpPrmmDataSchema);
									    		list.add(getSqlGrpPrmmDataTmp(ttGrpPrmmDataTmpSchema));
									    		mMMap.put(ttGrpPrmmDataSchema, "INSERT");
									    		mMMap.put(ttGrpPrmmDataTmpSchema, "INSERT");
									    		this.GrpPrmmData++;
									    		this.GrpPrmmDataTmp++;
									    		
									    		flag=true;
									    	}
									    }
									}
									else
									{
										System.out.println("tGrpAcomDataSet is null");
										flag=false;
									}
								}
							}
						}
						else
						{
							System.out.println("unionssrs.getMaxRow()!=unionAllssrs.getMaxRow()");
							flag=false;
						}
					}
					else
					{
						System.out.println("unionAllssrs is null");
						flag=false;
					}
				}
				else
				{
				    System.out.println("unionssrs is null :"+unionsql);
				    flag=false;
				}
			}
		}
		else
		{
			System.out.println("allssrs is null");
			flag=false;
		}
		
		if(!PubSubmit())
		{
			flag=false;
		}
		return flag;
	}

	private String getSqlGrpPrmmDataTmp(GrpPrmmDataTmpSchema tGrpPrmmDataSchema) 
	{
		// TODO 自动生成方法存根
		String sql="";
		sql=" insert into GrpPrmmDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, "
		   +" PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1,"
		   +" AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE,"
		   +" IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG,"
		   +" BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
		   +" values ('"+tGrpPrmmDataSchema.getSerialNo()+"', '"+tGrpPrmmDataSchema.getBatchBatch()+"', '"
		   +tGrpPrmmDataSchema.getAccYear()+"', '"+tGrpPrmmDataSchema.getAccMth()+"', '"+tGrpPrmmDataSchema.getPremLevel()+"', '"
		   +tGrpPrmmDataSchema.getPremAccType()+"', '"+tGrpPrmmDataSchema.getTranno()+"',"+tGrpPrmmDataSchema.getPremAmt()
		   +" , to_date('"+tGrpPrmmDataSchema.getPremDate()+"', 'yyyy-mm-dd'), '"+tGrpPrmmDataSchema.getPolicyYear()+"', '"+tGrpPrmmDataSchema.getProductCode()
		   +"', '"+tGrpPrmmDataSchema.getAgentCode1()+"', '"+tGrpPrmmDataSchema.getAgentCode2()+"', '"+tGrpPrmmDataSchema.getAgentCode3()+"', '"
		   +tGrpPrmmDataSchema.getAgentCode4()+"', '"+tGrpPrmmDataSchema.getCarrierCode()+"', '"+tGrpPrmmDataSchema.getPolicyNo()+"', '"+tGrpPrmmDataSchema.getRiskNo()
		   +"', '"+tGrpPrmmDataSchema.getBranchCode()+"', '"+tGrpPrmmDataSchema.getIFYP()+"', '"+tGrpPrmmDataSchema.getSalesUnit1()
		   +"', '"+tGrpPrmmDataSchema.getSalesUnit2()+"', '"+tGrpPrmmDataSchema.getSalesUnit3()
		   +"', '"+tGrpPrmmDataSchema.getSalesUnit4()+"', '"+tGrpPrmmDataSchema.getOperationType()
		   +"', '"+tGrpPrmmDataSchema.getStateFlag()+"', to_date('"+tGrpPrmmDataSchema.getBatchDate()
		   +"', 'yyyy-mm-dd'), to_date('"+tGrpPrmmDataSchema.getMakeDate()+"', 'yyyy-mm-dd'), '"+tGrpPrmmDataSchema.getMakeTime()
		   +"', '"+tGrpPrmmDataSchema.getNJFlag()+"');";
		sql=sql.replaceAll("'null'", "null");
		//System.out.println("sql="+sql);
		return sql;
	}

	private String getSqlGrpAcomDataTmp(GrpAcomDataTmpSchema grpAcomDataSchema) 
	{
		// TODO 自动生成方法存根
		String sql="";
		sql="insert into GrpAcomDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, "
		  +" ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, "
		  +" POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, "
		  +" AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, "
		  +" GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, "
		  +" AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE,"
		  +" OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
		  
		  +"values ('"+grpAcomDataSchema.getSerialNo()+"', '"+grpAcomDataSchema.getBatchBatch()+"', '"+grpAcomDataSchema.getAccYear()+"', '"+grpAcomDataSchema.getAccMth()+"', '"+grpAcomDataSchema.getCommCode()+"', "
		  +" '"+grpAcomDataSchema.getCommType()+"', '"+grpAcomDataSchema.getTranno()+"',"+grpAcomDataSchema.getCommAmt()+" , to_date('"+grpAcomDataSchema.getCommDate()+"', 'yyyy-mm-dd'), "
		  +" '"+grpAcomDataSchema.getPolicyYear()+"', '"+grpAcomDataSchema.getProductCode()+"', '"+grpAcomDataSchema.getAgentCode1()+"', '"+grpAcomDataSchema.getAgentCode2()+"', '"+grpAcomDataSchema.getAgentCode3()+"', '"+grpAcomDataSchema.getAgentCode4()+"', '"+grpAcomDataSchema.getCarrierCode()+"', '"+grpAcomDataSchema.getPolicyNo()+"', '"+grpAcomDataSchema.getRiskNo()+"', "
		  +" to_date('"+grpAcomDataSchema.getClawBackDate()+"','yyyy-mm-dd'), '"+grpAcomDataSchema.getBranchCode()+"', '"+grpAcomDataSchema.getGLSign()+"', '"+grpAcomDataSchema.getSalesUnit1()+"', '"+grpAcomDataSchema.getSalesUnit2()+"', '"+grpAcomDataSchema.getSalesUnit3()+"', '"+grpAcomDataSchema.getSalesUnit4()+"', "+grpAcomDataSchema.getPremAmt()+", '"+grpAcomDataSchema.getAgtProportion()+"', '"+grpAcomDataSchema.getCommisionRate()+"', "
		  +" to_date('"+grpAcomDataSchema.getReceiptIntoSysDate()+"','yyyy-mm-dd'),  to_date('"+grpAcomDataSchema.getGeneralReleaseDate()+"','yyyy-mm-dd'), '"+grpAcomDataSchema.getOperationType()+"', '"+grpAcomDataSchema.getStateFlag()+"', to_date('"+grpAcomDataSchema.getBatchDate()+"', 'yyyy-mm-dd'), "
		  +"to_date('"+grpAcomDataSchema.getMakeDate()+"', 'yyyy-mm-dd'), '"+grpAcomDataSchema.getMakeTime()+"', '"+grpAcomDataSchema.getNJFlag()+"');";
		sql=sql.replaceAll("'null'", "null");
//		System.out.println("sql="+sql);
		return sql;
	}

	private GrpPrmmDataTmpSchema transafePrmm(GrpPrmmDataSchema ttGrpPrmmDataSchema) 
	{
		// TODO 自动生成方法存根
		GrpPrmmDataTmpSchema tGrpPrmmDataTmpSchema = new GrpPrmmDataTmpSchema();
		tGrpPrmmDataTmpSchema.setAccMth(ttGrpPrmmDataSchema.getAccMth());
		tGrpPrmmDataTmpSchema.setAccYear(ttGrpPrmmDataSchema.getAccYear());
		tGrpPrmmDataTmpSchema.setAgentCode1(ttGrpPrmmDataSchema.getAgentCode1());
		tGrpPrmmDataTmpSchema.setAgentCode2(ttGrpPrmmDataSchema.getAgentCode2());
		tGrpPrmmDataTmpSchema.setAgentCode3(ttGrpPrmmDataSchema.getAgentCode3());
		tGrpPrmmDataTmpSchema.setAgentCode4(ttGrpPrmmDataSchema.getAgentCode4());
		tGrpPrmmDataTmpSchema.setBatchBatch(ttGrpPrmmDataSchema.getBatchBatch());
		tGrpPrmmDataTmpSchema.setBatchDate(ttGrpPrmmDataSchema.getBatchDate());
		tGrpPrmmDataTmpSchema.setBranchCode(ttGrpPrmmDataSchema.getBranchCode());
		tGrpPrmmDataTmpSchema.setCarrierCode(ttGrpPrmmDataSchema.getCarrierCode());
		tGrpPrmmDataTmpSchema.setIFYP(ttGrpPrmmDataSchema.getIFYP());
		tGrpPrmmDataTmpSchema.setMakeDate(ttGrpPrmmDataSchema.getMakeDate());
		tGrpPrmmDataTmpSchema.setMakeTime(ttGrpPrmmDataSchema.getMakeTime());
		tGrpPrmmDataTmpSchema.setNJFlag(ttGrpPrmmDataSchema.getNJFlag());
		tGrpPrmmDataTmpSchema.setOperationType(ttGrpPrmmDataSchema.getOperationType());
		tGrpPrmmDataTmpSchema.setPolicyNo(ttGrpPrmmDataSchema.getPolicyNo());
		tGrpPrmmDataTmpSchema.setPolicyYear(ttGrpPrmmDataSchema.getPolicyYear());
		tGrpPrmmDataTmpSchema.setPremAccType(ttGrpPrmmDataSchema.getPremAccType());
		tGrpPrmmDataTmpSchema.setPremAmt(ttGrpPrmmDataSchema.getPremAmt());
		tGrpPrmmDataTmpSchema.setPremDate(ttGrpPrmmDataSchema.getPremDate());
		tGrpPrmmDataTmpSchema.setPremLevel(ttGrpPrmmDataSchema.getPremLevel());
		tGrpPrmmDataTmpSchema.setProductCode(ttGrpPrmmDataSchema.getProductCode());
		tGrpPrmmDataTmpSchema.setRiskNo(ttGrpPrmmDataSchema.getRiskNo());
		tGrpPrmmDataTmpSchema.setSalesUnit1(ttGrpPrmmDataSchema.getSalesUnit1());
		tGrpPrmmDataTmpSchema.setSalesUnit2(ttGrpPrmmDataSchema.getSalesUnit2());
		tGrpPrmmDataTmpSchema.setSalesUnit3(ttGrpPrmmDataSchema.getSalesUnit3());
		tGrpPrmmDataTmpSchema.setSalesUnit4(ttGrpPrmmDataSchema.getSalesUnit4());
		tGrpPrmmDataTmpSchema.setSerialNo(ttGrpPrmmDataSchema.getSerialNo());
		tGrpPrmmDataTmpSchema.setStateFlag(ttGrpPrmmDataSchema.getStateFlag());
		tGrpPrmmDataTmpSchema.setTranno(ttGrpPrmmDataSchema.getTranno());
		return tGrpPrmmDataTmpSchema;
	}

	private GrpAcomDataTmpSchema transafeAcom(GrpAcomDataSchema grpAcomDataSchema) 
	{
		// TODO 自动生成方法存根
		GrpAcomDataTmpSchema tGrpAcomDataTmpSchema = new GrpAcomDataTmpSchema();
		tGrpAcomDataTmpSchema.setAccMth(grpAcomDataSchema.getAccMth());
		tGrpAcomDataTmpSchema.setAccYear(grpAcomDataSchema.getAccYear());
		tGrpAcomDataTmpSchema.setAgentCode1(grpAcomDataSchema.getAgentCode1());
		tGrpAcomDataTmpSchema.setAgentCode2(grpAcomDataSchema.getAgentCode2());
		tGrpAcomDataTmpSchema.setAgentCode3(grpAcomDataSchema.getAgentCode3());
		tGrpAcomDataTmpSchema.setAgentCode4(grpAcomDataSchema.getAgentCode4());
		tGrpAcomDataTmpSchema.setAgtProportion(grpAcomDataSchema.getAgtProportion());
		tGrpAcomDataTmpSchema.setBatchBatch(grpAcomDataSchema.getBatchBatch());
		tGrpAcomDataTmpSchema.setBatchDate(grpAcomDataSchema.getBatchDate());
		tGrpAcomDataTmpSchema.setBranchCode(grpAcomDataSchema.getBranchCode());
		tGrpAcomDataTmpSchema.setCarrierCode(grpAcomDataSchema.getCarrierCode());
		tGrpAcomDataTmpSchema.setClawBackDate(grpAcomDataSchema.getClawBackDate());
		tGrpAcomDataTmpSchema.setCommAmt(grpAcomDataSchema.getCommAmt());
		tGrpAcomDataTmpSchema.setCommCode(grpAcomDataSchema.getCommCode());
		tGrpAcomDataTmpSchema.setCommDate(grpAcomDataSchema.getCommDate());
		tGrpAcomDataTmpSchema.setCommisionRate(grpAcomDataSchema.getCommisionRate());
		tGrpAcomDataTmpSchema.setCommType(grpAcomDataSchema.getCommType());
		tGrpAcomDataTmpSchema.setGeneralReleaseDate(grpAcomDataSchema.getGeneralReleaseDate());
		tGrpAcomDataTmpSchema.setGLSign(grpAcomDataSchema.getGLSign());
		tGrpAcomDataTmpSchema.setMakeDate(grpAcomDataSchema.getMakeDate());
		tGrpAcomDataTmpSchema.setMakeTime(grpAcomDataSchema.getMakeTime());
		tGrpAcomDataTmpSchema.setNJFlag(grpAcomDataSchema.getNJFlag());
		tGrpAcomDataTmpSchema.setOperationType(grpAcomDataSchema.getOperationType());
		tGrpAcomDataTmpSchema.setPolicyNo(grpAcomDataSchema.getPolicyNo());
		tGrpAcomDataTmpSchema.setPolicyYear(grpAcomDataSchema.getPolicyYear());
		tGrpAcomDataTmpSchema.setPremAmt(grpAcomDataSchema.getPremAmt());
		tGrpAcomDataTmpSchema.setProductCode(grpAcomDataSchema.getProductCode());
		tGrpAcomDataTmpSchema.setReceiptIntoSysDate(grpAcomDataSchema.getReceiptIntoSysDate());
		tGrpAcomDataTmpSchema.setRiskNo(grpAcomDataSchema.getRiskNo());
		tGrpAcomDataTmpSchema.setSalesUnit1(grpAcomDataSchema.getSalesUnit1());
		tGrpAcomDataTmpSchema.setSalesUnit2(grpAcomDataSchema.getSalesUnit2());
		tGrpAcomDataTmpSchema.setSalesUnit3(grpAcomDataSchema.getSalesUnit3());
		tGrpAcomDataTmpSchema.setSalesUnit4(grpAcomDataSchema.getSalesUnit4());
		tGrpAcomDataTmpSchema.setSerialNo(grpAcomDataSchema.getSerialNo());
		tGrpAcomDataTmpSchema.setStateFlag(grpAcomDataSchema.getStateFlag());
		tGrpAcomDataTmpSchema.setTranno(grpAcomDataSchema.getTranno());
		return tGrpAcomDataTmpSchema;
	}

	private String getSqlGrpPrmmData(GrpPrmmDataSchema tGrpPrmmDataSchema) 
	{
		// TODO 自动生成方法存根
		String sql="";
		sql=" insert into GrpPrmmData (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, "
		   +" PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1,"
		   +" AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE,"
		   +" IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG,"
		   +" BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
		   +" values ('"+tGrpPrmmDataSchema.getSerialNo()+"', '"+tGrpPrmmDataSchema.getBatchBatch()+"', '"
		   +tGrpPrmmDataSchema.getAccYear()+"', '"+tGrpPrmmDataSchema.getAccMth()+"', '"+tGrpPrmmDataSchema.getPremLevel()+"', '"
		   +tGrpPrmmDataSchema.getPremAccType()+"', '"+tGrpPrmmDataSchema.getTranno()+"',"+tGrpPrmmDataSchema.getPremAmt()
		   +" , to_date('"+tGrpPrmmDataSchema.getPremDate()+"', 'yyyy-mm-dd'), '"+tGrpPrmmDataSchema.getPolicyYear()+"', '"+tGrpPrmmDataSchema.getProductCode()
		   +"', '"+tGrpPrmmDataSchema.getAgentCode1()+"', '"+tGrpPrmmDataSchema.getAgentCode2()+"', '"+tGrpPrmmDataSchema.getAgentCode3()+"', '"
		   +tGrpPrmmDataSchema.getAgentCode4()+"', '"+tGrpPrmmDataSchema.getCarrierCode()+"', '"+tGrpPrmmDataSchema.getPolicyNo()+"', '"+tGrpPrmmDataSchema.getRiskNo()
		   +"', '"+tGrpPrmmDataSchema.getBranchCode()+"', '"+tGrpPrmmDataSchema.getIFYP()+"', '"+tGrpPrmmDataSchema.getSalesUnit1()
		   +"', '"+tGrpPrmmDataSchema.getSalesUnit2()+"', '"+tGrpPrmmDataSchema.getSalesUnit3()
		   +"', '"+tGrpPrmmDataSchema.getSalesUnit4()+"', '"+tGrpPrmmDataSchema.getOperationType()
		   +"', '"+tGrpPrmmDataSchema.getStateFlag()+"', to_date('"+tGrpPrmmDataSchema.getBatchDate()
		   +"', 'yyyy-mm-dd'), to_date('"+tGrpPrmmDataSchema.getMakeDate()+"', 'yyyy-mm-dd'), '"+tGrpPrmmDataSchema.getMakeTime()
		   +"', '"+tGrpPrmmDataSchema.getNJFlag()+"');";
		sql=sql.replaceAll("'null'", "null");
		//System.out.println("sql="+sql);
		return sql;
	}

	private String getSqlGrpAcomData(GrpAcomDataSchema grpAcomDataSchema) {
		// TODO 自动生成方法存根
		String sql="";
		sql="insert into GrpAcomData (SERIALNO, BATCHBATCH, ACCYEAR, "
		  +" ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, "
		  +" POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, "
		  +" AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, "
		  +" GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, "
		  +" AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE,"
		  +" OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
		  
		  +"values ('"+grpAcomDataSchema.getSerialNo()+"', '"+grpAcomDataSchema.getBatchBatch()+"', '"+grpAcomDataSchema.getAccYear()+"', '"+grpAcomDataSchema.getAccMth()+"', '"+grpAcomDataSchema.getCommCode()+"', "
		  +" '"+grpAcomDataSchema.getCommType()+"', '"+grpAcomDataSchema.getTranno()+"',"+grpAcomDataSchema.getCommAmt()+" , to_date('"+grpAcomDataSchema.getCommDate()+"', 'yyyy-mm-dd'), "
		  +" '"+grpAcomDataSchema.getPolicyYear()+"', '"+grpAcomDataSchema.getProductCode()+"', '"+grpAcomDataSchema.getAgentCode1()+"', '"+grpAcomDataSchema.getAgentCode2()+"', '"+grpAcomDataSchema.getAgentCode3()+"', '"+grpAcomDataSchema.getAgentCode4()+"', '"+grpAcomDataSchema.getCarrierCode()+"', '"+grpAcomDataSchema.getPolicyNo()+"', '"+grpAcomDataSchema.getRiskNo()+"', "
		  +" to_date('"+grpAcomDataSchema.getClawBackDate()+"','yyyy-mm-dd'), '"+grpAcomDataSchema.getBranchCode()+"', '"+grpAcomDataSchema.getGLSign()+"', '"+grpAcomDataSchema.getSalesUnit1()+"', '"+grpAcomDataSchema.getSalesUnit2()+"', '"+grpAcomDataSchema.getSalesUnit3()+"', '"+grpAcomDataSchema.getSalesUnit4()+"', "+grpAcomDataSchema.getPremAmt()+", '"+grpAcomDataSchema.getAgtProportion()+"', '"+grpAcomDataSchema.getCommisionRate()+"', "
		  +" to_date('"+grpAcomDataSchema.getReceiptIntoSysDate()+"','yyyy-mm-dd'),  to_date('"+grpAcomDataSchema.getGeneralReleaseDate()+"','yyyy-mm-dd'), '"+grpAcomDataSchema.getOperationType()+"', '"+grpAcomDataSchema.getStateFlag()+"', to_date('"+grpAcomDataSchema.getBatchDate()+"', 'yyyy-mm-dd'), "
		  +"to_date('"+grpAcomDataSchema.getMakeDate()+"', 'yyyy-mm-dd'), '"+grpAcomDataSchema.getMakeTime()+"', '"+grpAcomDataSchema.getNJFlag()+"');";
		sql=sql.replaceAll("'null'", "null");
//		System.out.println("sql="+sql);
		return sql;
	}

	private boolean PubSubmit()
	{
		try
		{
		   System.out.println("txt file is "+System.getProperty("user.dir")+"/sql.txt");	
		   FileWriter fw = new FileWriter(System.getProperty("user.dir")+"/sql.txt");
           System.out.println("size="+list.size());		   
		   for(int i=0;i<list.size();i++)
		   {
			   fw.write((String)list.get(i));
			   fw.write("\r\n");
		   }
		   
		   String othersql1=" insert into grpacomdata (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
	                        + " values ('9090000642952302', '1', '2009', '03', 'GP', 'PC', '1', -45.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', to_date('16-03-2009', 'dd-mm-yyyy'), '88', '+', '', '', '', '', -900.0000, '100', '5', to_date('05-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '23:36:49', '');";
		   String othersql2=" insert into grpacomdata (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
				            +" values ('9090000642952303', '1', '2009', '03', 'GP', 'PC', '1', 135.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', to_date('16-03-2009', 'dd-mm-yyyy'), '88', '+', '', '', '', '', 2700.0000, '100', '5', to_date('05-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '23:36:49', '');";
			
		   String othersql3=" insert into grpprmmdata (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE, IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
			               + " values ('9090000642952202', '1', '2009', '03', 'GP', 'AC', '1', -900.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', '88', '1', '', '', '', '', 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '00:00:00', '');";
		   String othersql4=" insert into grpprmmdata (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE, IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
			                +" values ('9090000642952203', '1', '2009', '03', 'GP', 'AC', '1', 2700.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', '88', '1', '', '', '', '', 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '00:00:00', '');";

		   
		   String othersql5=" insert into GrpAcomDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
                          + " values ('9090000642952302', '1', '2009', '03', 'GP', 'PC', '1', -45.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', to_date('16-03-2009', 'dd-mm-yyyy'), '88', '+', '', '', '', '', -900.0000, '100', '5', to_date('05-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '23:36:49', '');";
		   String othersql6=" insert into GrpAcomDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, COMMCODE, COMMTYPE, TRANNO, COMMAMT, COMMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, CLAWBACKDATE, BRANCHCODE, GLSIGN, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, PREMAMT, AGTPROPORTION, COMMISIONRATE, RECEIPTINTOSYSDATE, GENERALRELEASEDATE, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
				            +" values ('9090000642952303', '1', '2009', '03', 'GP', 'PC', '1', 135.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', to_date('16-03-2009', 'dd-mm-yyyy'), '88', '+', '', '', '', '', 2700.0000, '100', '5', to_date('05-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '23:36:49', '');";
			
		   String othersql7=" insert into GrpPrmmDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE, IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
			              + " values ('9090000642952202', '1', '2009', '03', 'GP', 'AC', '1', -900.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', '88', '1', '', '', '', '', 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '00:00:00', '');";
		   String othersql8=" insert into GrpPrmmDataTmp (SERIALNO, BATCHBATCH, ACCYEAR, ACCMTH, PREMLEVEL, PREMACCTYPE, TRANNO, PREMAMT, PREMDATE, POLICYYEAR, PRODUCTCODE, AGENTCODE1, AGENTCODE2, AGENTCODE3, AGENTCODE4, CARRIERCODE, POLICYNO, RISKNO, BRANCHCODE, IFYP, SALESUNIT1, SALESUNIT2, SALESUNIT3, SALESUNIT4, OPERATIONTYPE, STATEFLAG, BATCHDATE, MAKEDATE, MAKETIME, NJFLAG)"
			               +" values ('9090000642952203', '1', '2009', '03', 'GP', 'AC', '1', 2700.0000, to_date('20-03-2009', 'dd-mm-yyyy'), '1', 'NIK01', '90800051', '', '', '', 'S', '88800111201', '01', '88', '1', '', '', '', '', 'DJ', '0', to_date('20-03-2009', 'dd-mm-yyyy'), to_date('20-03-2009', 'dd-mm-yyyy'), '00:00:00', '');";

		   
		   fw.write(othersql1);
		   fw.write("\r\n");

		   fw.write(othersql2);
		   fw.write("\r\n");

		   fw.write(othersql3);
		   fw.write("\r\n");

		   fw.write(othersql4);
		   fw.write("\r\n");

		   fw.write(othersql5);
		   fw.write("\r\n");

		   fw.write(othersql6);
		   fw.write("\r\n");

		   fw.write(othersql7);
		   fw.write("\r\n");

		   fw.write(othersql8);
		   fw.write("\r\n");
		   
		   fw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("GrpAcomData: "+this.GrpAcomData);
		System.out.println("GrpAcomDataTmp: "+this.GrpAcomDataTmp);
		System.out.println("GrpPrmmData: "+this.GrpPrmmData);
		System.out.println("GrpPrmmDataTmp: "+this.GrpPrmmDataTmp);
		
		
		
//		mMMap.put(othersql1, "INSERT");
//		mMMap.put(othersql2, "INSERT");
//		mMMap.put(othersql3, "INSERT");
//		mMMap.put(othersql4, "INSERT");
//		VData mResult = new VData();
//		mResult.add(mMMap);
//		PubSubmit tPubSubmit = new PubSubmit();
//		if (tPubSubmit.submitData(mResult, "")) 
//		{
//			return true;
//		} else 
//		{
//			return false;
//		}
        return true;
	}

}
