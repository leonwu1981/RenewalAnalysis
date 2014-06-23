package com.sinosoft.lis.claimanalysis.renewal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.FormatConverter;
import com.sinosoft.lis.db.LCContPlanFactoryDB;
import com.sinosoft.lis.pubfun.Arith;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContPlanFactorySchema;
import com.sinosoft.lis.vschema.LCContPlanFactorySet;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.TransferData;

public class DataDownloadThread_waiji implements Runnable {
//外籍
	
	 public  int countStr(String str1, String str2) {
		   int counter = 0; 
		        if (str1.indexOf(str2) == -1) {   
		             return 0;   
		         } else if (str1.indexOf(str2) != -1) {   
		            counter++;   
		            countStr(str1.substring(str1.indexOf(str2) +   
		                    str2.length()), str2);   
		               return counter;   
		      }   
		            return 0;   
		    }   
	int threadNum; // 线程编号
	
	String minContNo;
	String maxContNo;
	String file_path;
	
	String grpContNo;
	String startDate;
	String endDate;
	String manageCom;
	String riskCode;
	
	String orgs;
	
	FileProcessor FileProcessor = new FileProcessor();
	
//	Hashtable lcHt = new Hashtable();
	HashMap lmDutyGetRelaMap = new HashMap();
	
	List misMatchList = new ArrayList();
	
	static int SuccThreadCount;  //成功线程数
	static int FailThreadCount;  //失败线程数
	static int ThreadCount;  //正在执行线程数
	
	ExeSQL exeSQL = new ExeSQL();
		
	DataDownloadThread_waiji() {
	} 
	DataDownloadThread_waiji( int threadNum, String minContNo, String maxContNo, String path, String grpContNo, String startDate, String endDate, String manageCom, String riskCode, HashMap lmDutyGetRelaMap, String orgs ) {
		this.threadNum = threadNum;
		this.minContNo = minContNo;
		this.maxContNo = maxContNo;
		this.file_path = path;
		
		this.grpContNo = grpContNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.manageCom = manageCom;
		this.riskCode = riskCode;
		this.lmDutyGetRelaMap = lmDutyGetRelaMap;
		
		this.orgs = orgs;
	} 

	public void run() {
		
		addThreadCount();
		if( !outputData()){
			addFailThreadCount();
		} else {
			addSuccThreadCount();
		}
	}
	
	public boolean outputData(){
		Hashtable lcHt = new Hashtable();
		lcHt = this.getLpInsuredLC(grpContNo, minContNo, maxContNo);
		// 责任 lcduty
		List dutyInfoBatch = this.getDutyData(lcHt);
		// 理赔 -账单
		List claimInfoBatch = this.getClaimData(lcHt);
		ClaimInfo.outputClaimInfoCsv(claimInfoBatch, file_path, "claim_"+threadNum, 'M');
		
		// 每次(赔付责任)
		Hashtable claimPerVis = this.queryClmPerVis(claimInfoBatch);
		// 每年(责任)
		Hashtable claimAnnual = this.queryClmBenAnnual(claimPerVis );
		
		// 匹配数据
		List matchInfoBatch = this.matchCertClm(dutyInfoBatch, claimAnnual);
		InsuredInfo.outputInsuredInfoCsv(matchInfoBatch, file_path, "match_"+threadNum);
		
		writeMisMatchList();
		
		return true;
	}
	
	 private void writeMisMatchList() {
		 StringBuffer strBuff = new StringBuffer();
		 if(!misMatchList.isEmpty()){
			 for(Iterator i = misMatchList.iterator(); i.hasNext(); ) {
				 strBuff.append((String)i.next());
			}
		 }
		 FileProcessor.outputFile(strBuff.toString(), file_path, "misMatch_"+threadNum, "csv");
		
	}
	/**
	 * 获取 LpInsured 表中 LC保全 信息
	 * @param grpContNo
	 * @param minContNo
	 * @param maxContNo
	 * @return Hashtable - key : ContNo  value : List(ContPlanCode, ContPlanName, EdorValidate)
	 */
	public Hashtable getLpInsuredLC( String grpContNo, String minContNo, String maxContNo ) {
		// GrpContNo
		StringBuffer filter = new StringBuffer();
		StringTokenizer st = new StringTokenizer(grpContNo, ",");
		if(st.hasMoreTokens()){
			filter.append(" and ( a.grpcontno = '").append(st.nextToken()).append("' ");
		}
        while(st.hasMoreTokens()){
        	filter.append(" or a.grpcontno = '").append(st.nextToken()).append("' ");
        }
        filter.append(" ) ");
		StringBuffer sql = new StringBuffer();
//		sql.append(" select a.contno, a.contplancode, c.contplanname, b.edorvalidate, b.edorappdate ");
//		sql.append("   from lpinsured a, lpedoritem b, lccontplan c                  ");
//		sql.append("  where a.edorno = b.edorno                                      ");
//		sql.append("    and b.edortype = 'LC'                                        ");
//		sql.append("    and a.contplancode = c.contplancode                          ");
//		sql.append("    and a.grpcontno = c.grpcontno                                ");
//		sql.append("    and a.contno = b.contno                                      ");
//		sql.append("    and a.grpcontno = '"+grpContNo+"'                            ");
		sql.append(" select a.contno, a.contplancode, c.contplanname, b.edorvalidate, b.edorappdate ");
		sql.append("   from lpedoritem b, lpinsured a                                ");
		sql.append("   left join lccontplan c                                        ");
		sql.append("     on (a.contplancode = c.contplancode and a.grpcontno = c.grpcontno) ");
		sql.append("  where a.edorno = b.edorno                                      ");
		sql.append("    and b.edortype = 'LC'                                        ");
		sql.append("    and a.contno = b.contno                                      ");
		sql.append(filter);
		
		sql.append("    and a.contno > '"+minContNo+"'                               ");
		sql.append("    and a.contno <= '"+maxContNo+"'                              ");
		//
		sql.append(" union                                                           ");
		sql.append(" select a.contno, a.contplancode, c.contplanname, b.edorvalidate, b.edorappdate ");
		sql.append("   from lpinsured a, lpedoritem b, lbcontplan c                  ");
		sql.append("  where a.edorno = b.edorno                                      ");
		sql.append("    and b.edortype = 'LC'                                        ");
		sql.append("    and a.contplancode = c.contplancode                          ");
		sql.append("    and a.grpcontno = c.grpcontno                                ");
		sql.append("    and a.contno = b.contno                                      ");
		sql.append(filter);
		//
		sql.append("    and a.contno > '"+minContNo+"'                               ");
		sql.append("    and a.contno <= '"+maxContNo+"'                              ");
		// 排序方式： 保单、时间从先到后
		sql.append("  order by 1, 4, 5 desc                                          ");
//		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if ( ssrs==null||ssrs.getMaxRow() <= 0) {
			return null;
		}
		Hashtable ht = new Hashtable();
		for (int a = 1; a <= ssrs.getMaxRow(); a++) {
			String contNo = ssrs.GetText(a, 1);
			String contPlanCode = ssrs.GetText(a, 2);
			String contPlanName = ssrs.GetText(a, 3);
			//外籍
			if(countStr(contPlanName,"外籍")!=0){
				continue;
			}
			Date edorValidate = FormatConverter.getDate(ssrs.GetText(a, 4));
			Object[] element = new Object[4];
			element[0] = contPlanCode;
			element[1] = contPlanName;
			element[2] = edorValidate;
			
			Object o = ht.get(contNo);
			List list;
			if( o == null ) {
				list = new ArrayList();
				list.add(element);
			} else {
				list = (ArrayList)o;
				Object[] elem = (Object[])list.get( list.size()-1 );
				Date preValidate = (Date) elem[2];
				if( ! preValidate.equals(edorValidate) ){
					list.add(element);
				}
			}
				
			ht.put(contNo, list);
		}
		
		return ht;
	}
	
	public List getDutyData(Hashtable lcHt){
		StringBuffer filter = new StringBuffer();
		// GrpContNo
		StringTokenizer st = new StringTokenizer(grpContNo, ",");
		if(st.hasMoreTokens()){
			filter.append(" and ( b.grpcontno = '").append(st.nextToken()).append("' ");
		}
        while(st.hasMoreTokens()){
        	filter.append(" or b.grpcontno = '").append(st.nextToken()).append("' ");
        }
        filter.append(" ) ");
        // StartDate
		if( startDate != null && ! "".equals(startDate) ){
			filter.append(" and exists(SELECT 1 FROM lcgrpcont WHERE grpcontno = b.grpcontno and CValiDate>=to_date('").append(startDate).append("', 'yyyy-mm-dd')) ");
		}
		// EndDate
		if( endDate != null && ! "".equals(endDate) ){
			filter.append(" and exists(SELECT 1 FROM lcgrpcont WHERE grpcontno = b.grpcontno and CValiDate<=to_date('").append(endDate).append("', 'yyyy-mm-dd')) ");
		}
		// ManageCom
		if ( manageCom != null && !"".equals(manageCom) ) {
			filter.append(" and exists(select 1 from lcgrpcont where managecom like '").append(manageCom).append("%' and grpcontno=b.grpcontno ) ");
 		}
		// RiskCode
//		if ( riskCode != null && !"".equals(riskCode) ) {
//			filter.append(" and b.RiskCode in ( '").append(riskCode).append("' ) ");
//		}
        if( riskCode != null && !"".equals(riskCode) ){
        	st = new StringTokenizer(riskCode, ",");
        	if(st.hasMoreTokens()){
        		filter.append(" and ( b.riskcode = '").append(st.nextToken()).append("' ");
    		}
            while(st.hasMoreTokens()){
            	filter.append(" or b.riskcode = '").append(st.nextToken()).append("' ");
            }
            filter.append(" ) ");
		}
		
        //生育
//      filter.append(" and a.dutycode like '617002%'");
//		filter.append(" and b.contno in (select z.contno from insured_view z where z.RELATIONTOMAININSURED='00')");
		//机构
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("保单的个数和分支机构的组数不等！");
				return null;
			}
			String finSql = " ";
			for (int i = 0; i < grpcontnos.length; i++) {
				if("%".equals(orgsAll[i])){
					finSql += " (z.grpcontno='"+grpcontnos[i]+"') or";
				}else{
					String[] orgArray = orgsAll[i].split(",");
					String orgsLine = "";
					for (int j = 0; j < orgArray.length; j++) {
						orgsLine += "'"+orgArray[j]+"',";
					}
					orgsLine = orgsLine.substring(0,orgsLine.length()-1);
					finSql += " (z.grpcontno='"+grpcontnos[i]+"' and z.organcomcode in ("+orgsLine+")) or";
				}
			}
			finSql = finSql.substring(0,finSql.length()-2);
			filter.append(" and b.contno in (select z.contno from insured_view z where "+finSql+")");
		}
      		
		StringBuffer sb = new StringBuffer();
        // c表
		
		//1分公司
		sb.append(" select b.managecom as branchCode,                                                        ");
		//2保单号
		sb.append("        b.grpcontno as grpContNo,                                                         ");
		//3保单生效日
		sb.append("        (SELECT CValiDate FROM lcgrpcont WHERE grpcontno = b.grpcontno) as polValiDate,   ");
		//4保单终止日
		sb.append("        (SELECT MAX(EndDate) - 1 FROM lcpol WHERE grpcontno = b.grpcontno) as polEndDate, ");
		//5是否续保
		sb.append("        (SELECT decode(RepeatBill, '1', '否', '2', '是')                                  ");
		sb.append("           FROM lcgrpcont                                                                 ");
		sb.append("          WHERE grpcontno = b.grpcontno) as repeatBill,                                   ");
		//6投保单位
		sb.append("        (SELECT AppntName FROM lcgrpcont WHERE grpcontno = b.grpcontno) as appntName,    ");
		//7单位性质
		sb.append("        (select codename                                                                  ");
		sb.append("           from ldcode aa, ldgrp bb                                                       ");
		sb.append("          where aa.codetype = 'grpnature'                                                 ");
		sb.append("            and aa.code = bb.GrpNature                                                    ");
		sb.append("            and bb.customerno =                                                           ");
		sb.append("                (select CustomerNo                                                        ");
		sb.append("                   from LCGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno                                           ");
		sb.append("                 union                                                                    ");
		sb.append("                 select CustomerNo                                                        ");
		sb.append("                   from LBGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno)) as grpNature,                           ");
		//8行业类别
		sb.append("        (select codename                                                                  ");
		sb.append("           from ldcode aa, ldgrp bb                                                       ");
		sb.append("          where aa.codetype = 'businesstype'                                              ");
		sb.append("            and aa.code = bb.BusinessType                                                 ");
		sb.append("            and bb.customerno =                                                           ");
		sb.append("                (select CustomerNo                                                        ");
		sb.append("                   from LCGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno                                           ");
		sb.append("                 union                                                                    ");
		sb.append("                 select CustomerNo                                                        ");
		sb.append("                   from LBGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno)) as businessType,                        ");
		//9主营业务
		sb.append("        (select MainBussiness                                                             ");
		sb.append("           from LDGrp                                                                     ");
		sb.append("          where customerno = (select CustomerNo                                           ");
		sb.append("                                from LCGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno                              ");
		sb.append("                              union                                                       ");
		sb.append("                              select CustomerNo                                           ");
		sb.append("                                from LBGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno)) as mainBussiness,          ");
		//10被保险人号
		sb.append("        b.insuredno as customerNo,                                                        ");
		//11被保险人合同号
		sb.append("        b.contno as contno,                                                               ");
		//12被保险人生效日 : 放入责任开始时间
		sb.append("        a.getstartdate as cValiDate,                                                      ");
		//13被保险人终止日: 放入责任结束时间
		sb.append("        (a.enddate - 1) as cEndDate,                                                      ");
		//14被保险人职业等级
		sb.append("        (select bb.codename                                                               ");
		sb.append("           from lcinsured aa, ldcode bb                                                   ");
		sb.append("          where aa.OccupationType = bb.code                                               ");
		sb.append("            and bb.codetype = 'occupationtype'                                            ");
		sb.append("            and insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as occupationType,                                     ");
		//15在职或退休
		sb.append("        (select decode(retireflag, '0', '在职', '1', '退休', '')                          ");
		sb.append("           from lcinsured                                                                  ");
		sb.append("          where insuredno = b.insuredno and grpcontno = b.grpcontno and contno=b.contno) as retireFlag,                                  ");
		//16与被保险人的关系
		sb.append("        (select decode(RelationToMainInsured,                                             ");
		sb.append("                       '00',                                                              ");
		sb.append("                       '员工',                                                       ");
		sb.append("                       '01',                                                              ");
		sb.append("                       '配偶',                                                       ");
		sb.append("                       '03',                                                              ");
		sb.append("                       '子女',                                                       ");
		sb.append("                       '04',                                                              ");
		sb.append("                       '父母',                                                       ");
		sb.append("                       '28',                                                              ");
		sb.append("                       '雇主',                                                       ");
		sb.append("                       '29',                                                              ");
		sb.append("                       '雇员',                                                       ");
		sb.append("                       '30',                                                              ");
		sb.append("                       '业务员',                                                       ");
		sb.append("                       '其他')                                                     ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as relationToMainInsured,                              ");
		//17出生年月
		sb.append("        b.insuredbirthday as birthday,                                                    ");
		//18性别
		sb.append("        decode(b.insuredsex, '0', '男', '1', '女') as sex,                                ");
		//19计划 : 暂取当前，若有LC保全，在后面会调整
		sb.append("        (select contplancode                                                              ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as contPlanCode,                                       ");
		//20计划名称
		sb.append("        (select bb.contplanname                                                           ");
		sb.append("           from lcinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode                                         ");
		sb.append("         union                                                                            ");
		sb.append("         select bb.contplanname                                                           ");
		sb.append("           from lcinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode) as contPlanName,                       ");
		//21险种
		sb.append("        b.riskcode as riskCode,                                                           ");
		//22责任
		sb.append("        a.dutycode as dutyCode,                                                           ");
		//23保额
		sb.append("        a.amnt,                                 ");
		//24保费
		sb.append("        a.sumprem,                                 ");
		//25起付线
		sb.append("        a.getlimit as payLine,                                                            ");
		//26日药费限额、27日费用限额、28日床位费限额、29日检查费限额
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		//30赔付比例
		sb.append("        a.getrate as payRatio,                                                            ");
		//31意外赔付比例
		sb.append("        '',                                                                               ");
		//32服务机构代码
		sb.append("        (select executecom                                                                ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as executeCom,                                         ");
		//33 给付责任(其中MKK01,NIK12一对多)
		sb.append("        (select getdutycode from lmdutygetrela where dutycode=a.dutycode  and rownum=1 ), ");
		//34 ZT保全时间
		sb.append("        null as ZTDate                                                                      ");
		// added
		sb.append(" , ");
		//35 客户分支名称    
		sb.append(" ( select organTable.grpname ");
		sb.append("     from lcorgan organTable, lcinsured insuredTable ");
		sb.append("    where organTable.grpcontno = insuredTable.grpcontno ");
		sb.append("      and organTable.organinnercode = insuredTable.organinnercode ");
		sb.append("      and insuredTable.insuredno = b.insuredno ");
		sb.append("      and insuredTable.contno = b.contno ), ");
		//36 被保险人姓名
		sb.append(" (select name ");
		sb.append("    from lcinsured ");
		sb.append("   where insuredno = b.insuredno ");
		sb.append("     and contno = b.contno), ");
		//37 保全类型
		sb.append(" (select to_char(edorvalidate,'yyyy-MM-dd') from lpedoritem where contno=b.contno and edortype='NI' and rownum=1) ");
		// 38 PolNo
		sb.append(" ,a.polno ");
		// 39 paytodate
		sb.append(" ,(a.paytodate-1) as payToDate ");
		// 40 businesstypein
		sb.append(" ,(select businesstypein from lcgrpcont where grpcontno=b.grpcontno) as businesstypein ");
		//41
		sb.append(" ,(select gebclient from lcgrpcont aa where aa.grpcontno=b.grpcontno) as gebclient ");
		//42
		sb.append(" ,(select departmentid from lcgrpcont aa where aa.grpcontno=b.grpcontno) as departmentid ");
		//43
		sb.append(" ,(select OCCUPATIONCODE from lcinsured aa where aa.contno=b.contno) as OCCUPATIONCODE ");
		//44
//		sb.append(" ,GRPCONTNO_POLSTATE(b.grpcontno) as grpState ");
		// Condition
		sb.append("   from lcduty a, lcpol b                                                                 ");
		sb.append("  where a.polno = b.polno                                                                 ");
		sb.append("    and b.appflag = '1'                                                                   ");
		// filter
		sb.append( filter );
		//外籍
		sb.append(" and b.contno in  ");
		sb.append("        (select contno                                                           ");
		sb.append("           from lcinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%外籍%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2'                                  ");
		sb.append("         union                                                                            ");
		sb.append("         select contno                                                           ");
		sb.append("           from lcinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%外籍%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2')                         ");
		
		//
		sb.append("    and a.getstartdate <= a.enddate  ");
		//batch
		sb.append("    and a.contno > '"+minContNo+"'  ");
		sb.append("    and a.contno <= '"+maxContNo+"'  ");
		
//		sb.append(" and a.contno='600089648388' ");
		
		// b表
		sb.append(" union                                                                                    ");
		//1分公司
		sb.append(" select b.managecom,                                                                      ");
		//2保单号
		sb.append("        b.grpcontno,                                                                      ");
		//3保单生效日
		sb.append("        (SELECT CValiDate FROM lcgrpcont WHERE grpcontno = b.grpcontno),                  ");
		//4保单终止日
		sb.append("        (SELECT MAX(EndDate) - 1 FROM lcpol WHERE grpcontno = b.grpcontno),               ");
		//5是否续保
		sb.append("        (SELECT decode(RepeatBill, '1', '否', '2', '是')                                  ");
		sb.append("           FROM lcgrpcont                                                                 ");
		sb.append("          WHERE grpcontno = b.grpcontno),                                                 ");
		//6投保单位
		sb.append("        (SELECT AppntName FROM lcgrpcont WHERE grpcontno = b.grpcontno),                  ");
		//7单位性质
		sb.append("        (select codename                                                                  ");
		sb.append("           from ldcode aa, ldgrp bb                                                       ");
		sb.append("          where aa.codetype = 'grpnature'                                                 ");
		sb.append("            and aa.code = bb.GrpNature                                                    ");
		sb.append("            and bb.customerno =                                                           ");
		sb.append("                (select CustomerNo                                                        ");
		sb.append("                   from LCGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno                                           ");
		sb.append("                 union                                                                    ");
		sb.append("                 select CustomerNo                                                        ");
		sb.append("                   from LBGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno)),                                        ");
		//8行业类别
		sb.append("        (select codename                                                                  ");
		sb.append("           from ldcode aa, ldgrp bb                                                       ");
		sb.append("          where aa.codetype = 'businesstype'                                              ");
		sb.append("            and aa.code = bb.BusinessType                                                 ");
		sb.append("            and bb.customerno =                                                           ");
		sb.append("                (select CustomerNo                                                        ");
		sb.append("                   from LCGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno                                           ");
		sb.append("                 union                                                                    ");
		sb.append("                 select CustomerNo                                                        ");
		sb.append("                   from LBGrpAppnt                                                        ");
		sb.append("                  where GrpContNo = b.grpcontno)),                                        ");
		//9主营业务
		sb.append("        (select MainBussiness                                                             ");
		sb.append("           from LDGrp                                                                     ");
		sb.append("          where customerno = (select CustomerNo                                           ");
		sb.append("                                from LCGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno                              ");
		sb.append("                              union                                                       ");
		sb.append("                              select CustomerNo                                           ");
		sb.append("                                from LBGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno)),                           ");
		//10被保险人号
		sb.append("        b.insuredno,                                                                      ");
		//11被保险人合同号
		sb.append("        b.contno,                                                                         ");
		//12被保险人生效日: 放入责任开始时间
		sb.append("        a.getstartdate,                                                                   ");
		//13被保险人终止日: 放入责任结束时间
		sb.append("        (a.enddate - 1),                                                                  ");
		
		//14被保险人职业等级
		sb.append("        (select bb.codename                                                               ");
		sb.append("           from lbinsured aa, ldcode bb                                                   ");
		sb.append("          where aa.OccupationType = bb.code                                               ");
		sb.append("            and bb.codetype = 'occupationtype'                                            ");
		sb.append("            and insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//15在职或退休
		sb.append("        (select decode(retireflag, '0', '在职', '1', '退休', '')                          ");
		sb.append("           from lbinsured                                                                  ");
		sb.append("          where insuredno = b.insuredno and grpcontno = b.grpcontno and contno=b.contno),                                                ");
		//16与被保险人的关系
		sb.append("        (select decode(RelationToMainInsured,                                             ");
		sb.append("                       '00',                                                              ");
		sb.append("                       '员工',                                                       ");
		sb.append("                       '01',                                                              ");
		sb.append("                       '配偶',                                                       ");
		sb.append("                       '03',                                                              ");
		sb.append("                       '子女',                                                       ");
		sb.append("                       '04',                                                              ");
		sb.append("                       '父母',                                                       ");
		sb.append("                       '28',                                                              ");
		sb.append("                       '雇主',                                                       ");
		sb.append("                       '29',                                                              ");
		sb.append("                       '雇员',                                                       ");
		sb.append("                       '30',                                                              ");
		sb.append("                       '业务员',                                                       ");
		sb.append("                       '其他')                                                     ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//17出生年月
		sb.append("        b.insuredbirthday,                                                                ");
		//18性别
		sb.append("        decode(b.insuredsex, '0', '男', '1', '女'),                                       ");
		//19计划 : 暂取当前，若有LC保全，在后面会调整
		sb.append("        (select contplancode                                                              ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//20计划名称
		sb.append("        (select bb.contplanname                                                           ");
		sb.append("           from lbinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode                                         ");
		sb.append("         union                                                                            ");
		sb.append("         select bb.contplanname                                                           ");
		sb.append("           from lbinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode),                                       ");
		//21险种
		sb.append("        b.riskcode,                                                                       ");
		//22责任
		sb.append("        a.dutycode,                                                                       ");
		//23保额
		sb.append("        a.amnt,                                      										");
		//24保费
		sb.append("        a.sumprem,                                    										");
		//25起付线
		sb.append("        a.getlimit,                                                                       ");
		//26日药费限额、27日费用限额、28日床位费限额、29日检查费限额
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		//30赔付比例
		sb.append("        a.getrate,                                                                        ");
		//31意外赔付比例
		sb.append("        '',                                                                               ");
		//32服务机构代码
		sb.append("        (select executecom                                                                ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//33 给付责任(其中MKK01,NIK12一对多)
		sb.append("        (select getdutycode from lmdutygetrela where dutycode=a.dutycode  and rownum=1 ), ");
		//34 ZT保全时间
		sb.append("        (select aa.EdorValiDate - 1                                                       ");
		sb.append("           from LPEdorItem aa                                                             ");
		sb.append("          where aa.edorno = b.edorno                                                      ");
		sb.append("            and aa.contno = b.contno                                                      ");
		sb.append("            and aa.insuredno = b.insuredno)                                               ");
		// added
		sb.append(" , ");
		//35 客户分支名称
		sb.append(" ( select organTable.grpname ");
		sb.append("     from lcorgan organTable, lbinsured insuredTable ");
		sb.append("    where organTable.grpcontno = insuredTable.grpcontno ");
		sb.append("      and organTable.organinnercode = insuredTable.organinnercode ");
		sb.append("      and insuredTable.insuredno = b.insuredno ");
		sb.append("      and insuredTable.contno = b.contno ) ");
		sb.append(" , ");
		//36 被保险人姓名
		sb.append(" (select name ");
		sb.append("    from lbinsured ");
		sb.append("   where insuredno = b.insuredno ");
		sb.append("     and contno = b.contno), ");
		//37 保全类型
		sb.append(" 'ZT' ");
		// 38 PolNo
		sb.append(" ,a.polno ");
		// 39 paytodate
		sb.append(" ,(a.paytodate-1) ");
		// 40 businesstypein
		sb.append(" ,(select businesstypein from lcgrpcont where grpcontno=b.grpcontno) as businesstypein ");
		//41
		sb.append(" ,(select gebclient from lcgrpcont where grpcontno=b.grpcontno) as gebclient ");
		//42
		sb.append(" ,(select departmentid from lcgrpcont where grpcontno=b.grpcontno) as departmentid ");
		//43
		sb.append(" ,(select OCCUPATIONCODE from lbinsured aa where aa.contno=b.contno) as OCCUPATIONCODE ");
		//44
//		sb.append(" ,GRPCONTNO_POLSTATE(b.grpcontno) as grpState ");
		// Condition
		sb.append("   from lbduty a, lbpol b                                                                 ");
		sb.append("  where a.polno = b.polno                                                                 ");
		sb.append("    and b.appflag = '1'                                                                   ");
		// filter
		sb.append( filter );
		//外籍
		sb.append(" and b.contno in  ");
		sb.append("        (select contno                                                           ");
		sb.append("           from lbinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%外籍%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2'                                     ");
		sb.append("         union                                                                            ");
		sb.append("         select contno                                                           ");
		sb.append("           from lbinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and bb.contplancode<>'2'  and aa.ContPlanCode<>'2' and  bb.contplanname not like '%%外籍%%')                        ");
		//
		sb.append("    and a.getstartdate <= a.enddate  ");
		//batch
		sb.append("    and a.contno > '"+minContNo+"'  ");
		sb.append("    and a.contno <= '"+maxContNo+"'  ");
		
//		sb.append(" and a.contno='600089648388' ");
		
		// 
		sb.append(" order by contno,13 desc");
		
		
		List list = new ArrayList();
//		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sb.toString());
		if ( ssrs.getMaxRow() <= 0) {
			return list;
		}
		// ljagetendorse
		HashMap ljaMap = new HashMap();
		HashMap grpStateHm = new HashMap();
		//Hashtable lcHt = this.getLpInsuredLC(grpContNo, minContNo, maxContNo);
		for (int a = 1; a <= ssrs.getMaxRow(); a++) {
			InsuredInfo insuredInfo = new InsuredInfo();
			if(countStr(ssrs.GetText(a, 20),"外籍")!=0){
				continue;
			}
			//1分公司
			insuredInfo.setBranchCode(ssrs.GetText(a, 1));
			//2保单号
			insuredInfo.setGrpContNo(ssrs.GetText(a, 2));
			//3保单生效日
			String polValidate = ssrs.GetText(a, 3);
			insuredInfo.setPolValiDate(FormatConverter.getDate(polValidate));
			//4保单终止日
			String polEndDate = ssrs.GetText(a, 4);
			insuredInfo.setPolEndDate(FormatConverter.getDate(polEndDate));
			//5是否续保
			insuredInfo.setRepeatBill(ssrs.GetText(a, 5));
			//6投保单位
			insuredInfo.setAppntName(FormatConverter.getString(ssrs.GetText(a, 6)));
			//7单位性质
			insuredInfo.setGrpNature(FormatConverter.getString(ssrs.GetText(a, 7)));
			//8行业类别
			insuredInfo.setBusinessType(FormatConverter.getString(ssrs.GetText(a, 8)));
			//9主营业务
			insuredInfo.setMainBussiness(FormatConverter.getString(ssrs.GetText(a, 9)));
			//10被保险人号
			insuredInfo.setCustomerNo(ssrs.GetText(a, 10));
			//11被保险人合同号
			insuredInfo.setContno(ssrs.GetText(a, 11));
			//12被保险人生效日 : 放入责任开始时间
			insuredInfo.setCValiDate(FormatConverter.getDate(ssrs.GetText(a, 12)));
			//13被保险人终止日: 放入责任结束时间
			insuredInfo.setCEndDate(FormatConverter.getDate(ssrs.GetText(a, 13)));
			//14被保险人职业等级
			insuredInfo.setOccupationType(ssrs.GetText(a, 14));
			//15在职或退休
			insuredInfo.setRetireFlag(ssrs.GetText(a, 15));
			//16与被保险人的关系
			insuredInfo.setRelationToMainInsured(ssrs.GetText(a, 16));
			//17出生年月
			insuredInfo.setBirthday(FormatConverter.getDate(ssrs.GetText(a, 17)));
			//18性别
			insuredInfo.setSex(ssrs.GetText(a, 18));
			//19计划 : 暂取当前，若有LC保全，在后面会调整
			insuredInfo.setContPlanCode(ssrs.GetText(a, 19));
			//20计划名称
			insuredInfo.setContPlanName(FormatConverter.getString(ssrs.GetText(a, 20)));
			//21险种
			insuredInfo.setRiskCode(ssrs.GetText(a, 21));
			//22责任
			insuredInfo.setDutyCode(ssrs.GetText(a, 22).substring(0, 6)); // 6位代码
			//23保额
			insuredInfo.setAmnt(FormatConverter.getDouble(ssrs.GetText(a, 23)));
			//24保费
			insuredInfo.setPrem(FormatConverter.getDouble(ssrs.GetText(a, 24)));
			//25起付线
			String tGetLimit = ssrs.GetText(a, 25);
			insuredInfo.setPayLine( ssrs.GetText(a, 25) );
			//26日药费限额、27日费用限额、28日床位费限额、29日检查费限额
			insuredInfo.setDrugDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 26)));
			insuredInfo.setFeeDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 27)));
			insuredInfo.setBedDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 28)));
			insuredInfo.setCheckDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 29)));
			//30赔付比例
			String tGetRate = ssrs.GetText(a, 30);
			insuredInfo.setPayRatio( ssrs.GetText(a, 30) );
			//31意外赔付比例
			insuredInfo.setAccidentPayRatio(FormatConverter.getDouble(ssrs.GetText(a, 31)));
			//32服务机构代码
			insuredInfo.setExecuteCom(ssrs.GetText(a, 32));
			//33赔付责任
			String tGetDutyCode = ssrs.GetText(a, 33);
			//34 ZT保全时间
			Date ztDate = FormatConverter.getDate( ssrs.GetText(a, 34) );
			
			if( ztDate != null  ) {
				if( ztDate.before( insuredInfo.getCEndDate() ) ){
					insuredInfo.setCEndDate( ztDate );
				}
			}
			
			//不通过ztdate来去掉数据,后面有去掉退保数据的地方
//			if( ztDate != null  ) {
//				if( insuredInfo.getCEndDate() == null ){
//					insuredInfo.setCEndDate( ztDate );
//				} else if( ztDate.before( insuredInfo.getCEndDate() ) ){
//					insuredInfo.setCEndDate( ztDate );
//				}
//				if( !insuredInfo.getCEndDate().after(insuredInfo.getCValiDate()) ){
//					continue;
//				}
//			}
			//35 客户分支名称
			insuredInfo.setAppntDept( FormatConverter.getString(ssrs.GetText(a, 35)) );
			//36 被保险人姓名
			insuredInfo.setCustomerName( FormatConverter.getString(ssrs.GetText(a, 36)) );
			//37 保全类型
			String posFlag = ssrs.GetText(a, 37);
			if( posFlag == null ) {
				insuredInfo.setPosFlag( "" );
			} else if( "".equals(posFlag) || "ZT".equals(posFlag) ){
				insuredInfo.setPosFlag( posFlag );
			} else {
				// 增人保全生效日与保单生效日相同的情况，视为操作失误，统计时候不算入NI
				Date edorValidate = FormatConverter.getDate(posFlag);
				if( edorValidate.after(insuredInfo.getPolValiDate()) ){
					insuredInfo.setPosFlag( "NI" );
				} else {
					insuredInfo.setPosFlag( "" );
				}
			}
			

			//
			if(insuredInfo.getPolEndDate()==null){
				String sqlPolEnd = "select edorvalidate from lpgrpedoritem where grpcontno='"+insuredInfo.getGrpContNo()+"' order by edorvalidate";
				SSRS ssrsPolEnd = exeSQL.execSQL(sqlPolEnd);
				if(ssrsPolEnd.getMaxRow()>0){
					insuredInfo.setPolEndDate(FormatConverter.getDate(ssrsPolEnd.GetText(1, 1)));
				}
			}
			if(insuredInfo.getCEndDate()==null || insuredInfo.getPolEndDate()==null || insuredInfo.getCValiDate()==null || insuredInfo.getPolValiDate()==null){
				continue;
			}
			
			//修正 19计划、 20计划名称
			if( lcHt != null ){
				
				Object o = lcHt.get( insuredInfo.getContno() );
				if( o != null ){
					List c = (ArrayList)o;
					Date t1;
					Date t2 = FormatConverter.getDate("2005-1-1");
					for(Iterator i = c.iterator(); i.hasNext(); ){
						Object[] element = (Object[])i.next();
						t1 = t2;
						t2 = (Date) element[2];
						// >=t1 && <t2
						if( (t1.before(insuredInfo.getCValiDate()) || t1.equals(insuredInfo.getCValiDate())) && (t2.after(insuredInfo.getCEndDate()) ) ) {
							insuredInfo.setContPlanCode( (String)element[0] );
							insuredInfo.setContPlanName( FormatConverter.getString( (String)element[1] ) );
							break;
						}
					}
				}
			}
			// 年龄 =（被保险人的个人生效日－生日＋1）/365
			int days_a_year = 365;
			
			insuredInfo.setAge(((int)((insuredInfo.getCValiDate().getTime()-insuredInfo.getBirthday().getTime())/(1000*60*60*24))+1)/days_a_year);// 年龄
			// 年龄段
			insuredInfo.setAgeBand( this.getAgeBand(insuredInfo.getAge()) );
			// <x/y> 暴露数 = (min(评估终止日,被保险人终止日)-被保险人生效日+1)/(保单终止日-保单生效日+1)
			//double a = ((double)(((assessDate.before(insuredInfo.getCEndDate())?assessDate:insuredInfo.getCEndDate()).getTime()-insuredInfo.getCValiDate().getTime())/(1000*60*60*24))+1);
			// <x/y> 暴露数 = (被保险人终止日-被保险人生效日+1)/(保单终止日-保单生效日+1)
			

			double x = (double)((insuredInfo.getCEndDate().getTime()-insuredInfo.getCValiDate().getTime())/(1000*60*60*24)) + 1;
			double y = (double)((insuredInfo.getPolEndDate().getTime()-insuredInfo.getPolValiDate().getTime())/(1000*60*60*24)) + 1;
			insuredInfo.setExposure(x/y);
			// 判断保单生效日-保单终止日之间是否有2月29日
			if( polValidate != null && !"".equals(polValidate) && polEndDate != null && !"".equals(polEndDate) ){
				String[] date1 = polValidate.split("-");
				String[] date2 = polEndDate.split("-");
				int month1 = FormatConverter.getInt( date1[1] );
//				int day1 = FormatConverter.getInt( date1[2] );
				if( month1 < 3 ){
					int year1 = FormatConverter.getInt( date1[0] );
					if(this.isLeapYear(year1)){
						days_a_year = 366;
					}
				}
				int month2 = FormatConverter.getInt( date2[1] );
				int day2 = FormatConverter.getInt( date2[2] );
				if( month2>2 || (month2==2&&day2==29) ){
					int year2 = FormatConverter.getInt( date2[0] );
					if(this.isLeapYear(year2)){
						days_a_year = 366;
					}
				}
				
			}
			insuredInfo.setExposure2(x/days_a_year);
			//25起付线、26日药费限额、27日费用限额、28日床位费限额、29日检查费限额、30赔付比例、31意外赔付比例
			String[] tPayRate = this.getPayRate( insuredInfo.getGrpContNo(), insuredInfo.getRiskCode(), insuredInfo.getContPlanCode(), insuredInfo.getDutyCode(), tGetDutyCode, tGetLimit, tGetRate );
			insuredInfo.setPayLine( tPayRate[0] );//免赔额(起付线)
    		insuredInfo.setDrugDailyLimit( FormatConverter.getDouble(tPayRate[3]) );//日药费限额
    		insuredInfo.setFeeDailyLimit( FormatConverter.getDouble(tPayRate[4]) );//日费用限额
    		insuredInfo.setBedDailyLimit( FormatConverter.getDouble(tPayRate[5]) );//日床位费限额
    		insuredInfo.setCheckDailyLimit( FormatConverter.getDouble(tPayRate[6]) );//日检查费限额
    		insuredInfo.setPayRatio( tPayRate[1] );//赔付比例
    		insuredInfo.setAccidentPayRatio( FormatConverter.getDouble(tPayRate[2]) );//意外赔付比例
    		insuredInfo.setPayToDate(FormatConverter.getDate(ssrs.GetText(a, 39)));//缴至日期
    		insuredInfo.setBusinesstypein(ssrs.GetText(a, 40));
    		insuredInfo.setGebclient(ssrs.GetText(a, 41));
    		insuredInfo.setDepartmentid(ssrs.GetText(a, 42));
    		insuredInfo.setOccupationcode(ssrs.GetText(a, 43));
//    		insuredInfo.setGrpState(ssrs.GetText(a, 44));
    		if(grpStateHm.containsKey(insuredInfo.getGrpContNo())){
    			insuredInfo.setGrpState((String) grpStateHm.get(insuredInfo.getGrpContNo()));
    		}else{
    			String grpState = exeSQL.getOneValue("select GRPCONTNO_POLSTATE('"+insuredInfo.getGrpContNo()+"') from dual");
    			grpStateHm.put(insuredInfo.getGrpContNo(), grpState);
    			insuredInfo.setGrpState(grpState);
    		}

			if( "ZT".equals(insuredInfo.getPosFlag()) ){
				String polNo = ssrs.GetText(a, 38);
				String dutyCode = insuredInfo.getDutyCode();
				String key = polNo + " " + dutyCode;
				if(ljaMap.containsKey(key)){
					InsuredInfo insuredInfo2 = (InsuredInfo) ljaMap.get(key);
					insuredInfo2.setPrem(insuredInfo.getPrem()+insuredInfo2.getPrem());//保费合并
					insuredInfo2.setAmnt(insuredInfo.getAmnt()>insuredInfo2.getAmnt()?insuredInfo.getAmnt():insuredInfo2.getAmnt());//保额取最大
					FDate fd = new FDate();
					insuredInfo2.setCValiDate(FormatConverter.getDate(PubFun.getBeforeDate(fd.getString(insuredInfo2.getCValiDate()), fd.getString(insuredInfo.getCValiDate()))));//合并生效日，取最早的
					insuredInfo2.setCEndDate(FormatConverter.getDate(PubFun.getLaterDate(fd.getString(insuredInfo2.getCEndDate()), fd.getString(insuredInfo.getCEndDate()))));//合并结束日，取最晚的
					insuredInfo2.setPayToDate(FormatConverter.getDate(PubFun.getLaterDate(fd.getString(insuredInfo2.getPayToDate()), fd.getString(insuredInfo.getPayToDate()))));//合并缴至日，取最晚的
					ljaMap.put(key, insuredInfo2);
				}else{
					ljaMap.put(key, insuredInfo);
				}
			} else {
				list.add(insuredInfo);
			}
		}
		// ljagetendorse
		st = new StringTokenizer(grpContNo, ",");
		filter = new StringBuffer();
		if(st.hasMoreTokens()){
			filter.append(" and ( grpcontno = '").append(st.nextToken()).append("' ");
		}
        while(st.hasMoreTokens()){
        	filter.append(" or grpcontno = '").append(st.nextToken()).append("' ");
        }
        filter.append(" ) ");
		String sql = "select polno,dutycode,getmoney from ljagetendorse ";
    	sql += " where FeeOperationtype in ('ZT', 'HT') and operator not like 'GRP%' and endorsementno not like '9046%' ";
    	sql = sql + filter.toString();
    	sql = sql + " and contno > '"+minContNo+"' ";
    	sql = sql + " and contno <= '"+maxContNo+"' ";
    	sql += " order by contno";
    	ssrs = exeSQL.execSQL(sql);
    	for (int a = 1; a <= ssrs.getMaxRow(); a++) {
    		String polNo = ssrs.GetText(a, 1);
    		String dutyCode = ssrs.GetText(a, 2);
    		double getMoney = FormatConverter.getDouble(ssrs.GetText(a, 3));
    		String key = polNo + " " + dutyCode;
    		Object o = ljaMap.get(key);
    		if( o == null ){
    			continue;
    		}
    		InsuredInfo insuredInfo = (InsuredInfo)o;
    		insuredInfo.setPrem( insuredInfo.getPrem() + getMoney );
    	}
		list.addAll(ljaMap.values());
		return list;
	}
	
	public List getClaimData(Hashtable lcHt){
		StringBuffer sql = new StringBuffer();
		// 1 managecom
		sql.append(" select a.mngcom,                                                           ");
		// 2 grpcontno
		sql.append("        b.grpcontno,                                                        ");
		// 3
		sql.append("        (SELECT CValiDate                                                   ");
		sql.append("           FROM grpcont_view                                                ");
		sql.append("          WHERE grpcontno = b.grpcontno                                     ");
		sql.append("            and rownum = 1),                                                ");
		// 4
		sql.append("        (SELECT MAX(EndDate) - 1 FROM lcpol WHERE grpcontno = b.grpcontno), ");
		// 5
		sql.append("        a.customerno,                                                       ");
		// 6
		sql.append("        a.custbirthday,                                                     ");
		// 7
		sql.append("        (CASE a.customersex                                                 ");
		sql.append("          WHEN '0' THEN                                                     ");
		sql.append("           '男'                                                              ");
		sql.append("          WHEN '1' THEN                                                     ");
		sql.append("           '女'                                                              ");
		sql.append("        END),                                                               ");
		// 8
		sql.append("        (select cvalidate                                                   ");
		sql.append("           from lcpol                                                       ");
		sql.append("          where polno = b.polno                                             ");
		sql.append("         union all                                                          ");
		sql.append("         select cvalidate from lbpol where polno = b.polno),                ");
		// 9
		sql.append("        (select enddate - 1                                                 ");
		sql.append("           from lcpol                                                       ");
		sql.append("          where polno = b.polno                                             ");
		sql.append("         union all                                                          ");
		sql.append("         select bb.EdorValiDate - 1                                         ");
		sql.append("           from lbpol aa, LPEdorItem bb                                     ");
		sql.append("          where aa.polno = b.polno                                          ");
		sql.append("            and aa.edorno = bb.edorno                                       ");
		sql.append("            and aa.contno = bb.contno                                       ");
		sql.append("            and aa.insuredno = bb.insuredno),                               ");
		// 10
		sql.append("        decode((select RetireFlag                                           ");
		sql.append("                 from ldperson                                              ");
		sql.append("                where customerno = a.customerno),                           ");
		sql.append("               '0',                                                         ");
		sql.append("               '在职',                                                       ");
		sql.append("               '1',                                                         ");
		sql.append("               '退休'),                                                      ");
		// 11与被保险人的关系
		sql.append("        (select decode(RelationToMainInsured,                                 ");
		sql.append("                       '00',                                                  ");
		sql.append("                       '员工',                                                ");
		sql.append("                       '01',                                                  ");
		sql.append("                       '配偶',                                                ");
		sql.append("                       '03',                                                  ");
		sql.append("                       '子女',                                                ");
		sql.append("                       '04',                                                  ");
		sql.append("                       '父母',                                                ");
		sql.append("                       '28',                                                  ");
		sql.append("                       '雇主',                                                ");
		sql.append("                       '29',                                                  ");
		sql.append("                       '雇员',                                                ");
		sql.append("                       '30',                                                  ");
		sql.append("                       '业务员',                                              ");
		sql.append("                       '其他')                                               ");
		sql.append("           from insured_view                                                ");
		sql.append("          where grpcontno = b.grpcontno                                     ");
		sql.append("            and contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and rownum = 1),                                                ");
		// 12
		sql.append("        (select contplancode                                                ");
		sql.append("           from insured_view                                                ");
		sql.append("          where grpcontno = b.grpcontno                                     ");
		sql.append("            and contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and rownum = 1),                                                ");
		// 13
		sql.append("        (select bb.contplanname                                             ");
		sql.append("           from insured_view aa, lccontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode                           ");
		sql.append("         union                                                              ");
		sql.append("         select bb.contplanname                                             ");
		sql.append("           from insured_view aa, lbcontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode),                         ");
		// 14
		sql.append("        '' t1,                                                              ");
		// 15
		sql.append("        '' t2,                                                              ");
		// 16
		sql.append("        '' t3,                                                              ");
		// 17
		sql.append("        '' t4,                                                              ");
		// 18
		sql.append("        '' t5,                                                              ");
		// 19
		sql.append("        '' t6,                                                              ");
		// 20
		sql.append("        a.caseno,                                                           ");
		// 21
		sql.append("        a.accidentdate,                                                     ");
		// 22
		sql.append("        '' t7,                                                              ");
		// 23
		sql.append("        '' t8,                                                              ");
		// 24
		sql.append("        '' t9,                                                              ");
		// 25
		sql.append("        '' t10,                                                             ");
		// 26
		sql.append("        (SELECT diseasecode                                                 ");
		sql.append("           FROM llcasecure                                                  ");
		sql.append("          WHERE caseno = a.caseno                                           ");
		sql.append("            AND ROWNUM = 1),                                                ");
		// 27
		sql.append("        (select ApplyDate from llregister where rgtno = a.rgtno),           ");
		// 28
		sql.append("        '' t11,                                                             ");
		// 29
		sql.append("        '' t12,                                                             ");
		// 30
		sql.append("        '0' t13,                                                            ");
		// 31
		sql.append("        '0' t14,                                                            ");
		// 32
		sql.append("        '0' t15,                                                            ");
		// 33
		sql.append("        '0' t16,                                                            ");
		// 34
		sql.append("        b.getdutycode,                                                      ");
		// 35
		sql.append("        (SELECT GetDutyName                                                 ");
		sql.append("           FROM LMDutyGetClm                                                ");
		sql.append("          WHERE GetDutyCode = b.getdutycode                                 ");
		sql.append("            AND GetDutyKind = b.getdutykind),                               ");
		// 36
		sql.append("        nvl(b.realpay, 0),                                                  ");
		// 37
		sql.append("        (SELECT max(confdate) FROM ljaget WHERE OtherNo = a.caseno),        ");
		// 38
		sql.append("        b.riskcode,                                                         ");
		// 39
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '01') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '02') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '03') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '04') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '05') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '06') ||                                       ");
		sql.append("        (select (select codename                                            ");
		sql.append("                   From ldcode                                              ");
		sql.append("                  where codetype = 'llrgtreason'                            ");
		sql.append("                    and code = reasoncode)                                  ");
		sql.append("           From llappclaimreason                                            ");
		sql.append("          where caseno = a.caseno                                           ");
		sql.append("            and reasoncode = '07'),                                         ");
		// 40
		sql.append("        llcase_preclaimmoney(a.caseno),                                     ");
		// 41
		sql.append("        b.ClmNo,                                                            ");
		// 42
		sql.append("        b.PolNo,                                                            ");
		// 43
		sql.append("        b.GetDutyCode t17,                                                  ");
		// 44
		sql.append("        b.GetDutyKind,                                                      ");
		// 45
		sql.append("        b.CaseNo t18,                                                       ");
		// 46
		sql.append("        b.CaseRelaNo,                                                       ");
		// 47
		sql.append("        '' t19,                                                             ");
		// 48
		sql.append("        b.contno,                                                           ");
		// 49
		sql.append("        b.dutycode,                                                         ");
		// 50
		sql.append("        b.outdutyamnt,                                                      ");
		// 51
		sql.append("        (select count(1)                                                    ");
		sql.append("           from lpedoritem                                                  ");
		sql.append("          where EdorType = 'LC'                                             ");
		sql.append("            and ContNo = b.contno                                           ");
		sql.append("            and InsuredNo = a.customerno                                    ");
		sql.append("            and EdorValiDate > a.accidentdate),                              ");
		// 52
		sql.append("        (select amnt                                                    ");
		sql.append("           from Lcduty                                                  ");
		sql.append("          where dutycode = b.dutycode                                       ");
		sql.append("            and PolNo = b.PolNo),                                           ");
		// 53 TPA
		sql.append("        '',                                           ");
		// 54
		sql.append("        decode(nvl(a.innetwork,'1'),'1','非TPA赔付','TPA赔付'),                                           ");
		// 55
		sql.append("  (select businesstypein from lcgrpcont where grpcontno=b.grpcontno)  businesstypein,");
		// 56 姓名
		sql.append("  a.customername");
		//57
		sql.append("  ,(select gebclient from lcgrpcont where grpcontno=b.grpcontno)  gebclient");
		//58
		sql.append("  ,(select departmentid from lcgrpcont where grpcontno=b.grpcontno)  departmentid");
		//59
		sql.append("  ,(select OCCUPATIONCODE from insured_view where contno=b.contno)  OCCUPATIONCODE");
		//60
		sql.append("  ,(select aa.givetypedesc from llclaim aa where aa.caseno=a.caseno)  givetypedesc");
		//61理算金额
		sql.append("  ,b.claimmoney");
		// 
		sql.append("   FROM llcase a, llclaimdetail b                                           ");
		sql.append("  WHERE a.caseno = b.caseno                                                 ");
//		sql.append("    and b.RiskCode <> 'NIK12'                                               ");
		sql.append("    and exists                                                              ");
		sql.append("  (select 1                                                                 ");
		sql.append("           from llclaim                                                     ");
		sql.append("          where clmstate in ('2', '3')                                      ");
		sql.append("            and clmno = a.caseno)                                           ");
		sql.append("    and ((a.signerdate >= '"+startDate+"' and                                    ");
		sql.append("        a.signerdate <= '"+endDate+"') or                                    ");
		sql.append("        (a.claimcaldate >= '"+startDate+"' and                                   ");
		sql.append("        a.claimcaldate <= '"+endDate+"' and                                  ");
		sql.append("        b.grpcontno in ('83000029300', '83000029400')))                     ");
		sql.append("    and ((select nvl(sum(fee), 0)                                           ");
		sql.append("            from llcasereceipt                                              ");
		sql.append("           where caseno = a.caseno                                          ");
		sql.append("             and feeitemcode in                                             ");
		sql.append("                 (select feecode                                            ");
		sql.append("                    from lmdutygetfeerela                                   ");
		sql.append("                   where getdutycode = b.getdutycode)) > 0 or b.realpay > 0)");
		// batch
		sql.append("    and b.contno > '"+minContNo+"'                                          ");
		sql.append("    and b.contno <= '"+maxContNo+"'                                         ");
//		sql.append("    and b.contno in ('600089648388')                         ");
		
		

		// GrpContNo
		StringTokenizer st = new StringTokenizer(grpContNo, ",");
		if(st.hasMoreTokens()){
			sql.append(" and ( b.grpcontno = '").append(st.nextToken()).append("' ");
		}
        while(st.hasMoreTokens()){
        	sql.append(" or b.grpcontno = '").append(st.nextToken()).append("' ");
        }
        sql.append(" ) ");
        // RiskCode
        if( riskCode != null && !"".equals(riskCode) ){
        	st = new StringTokenizer(riskCode, ",");
        	if(st.hasMoreTokens()){
    			sql.append(" and ( b.riskcode = '").append(st.nextToken()).append("' ");
    		}
            while(st.hasMoreTokens()){
            	sql.append(" or b.riskcode = '").append(st.nextToken()).append("' ");
            }
            sql.append(" ) ");
		}
        // manageCom
		if ( manageCom != null && !"".equals(manageCom) ) {
			sql.append(" and a.mngcom like '"+manageCom+"%' ");
 		}
		//机构
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("保单的个数和分支机构的组数不等！");
				return null;
			}
			String finSql = " ";
			for (int i = 0; i < grpcontnos.length; i++) {
				if("%".equals(orgsAll[i])){
					finSql += " (z.grpcontno='"+grpcontnos[i]+"') or";
				}else{
					String[] orgArray = orgsAll[i].split(",");
					String orgsLine = "";
					for (int j = 0; j < orgArray.length; j++) {
						orgsLine += "'"+orgArray[j]+"',";
					}
					orgsLine = orgsLine.substring(0,orgsLine.length()-1);
					finSql += " (z.grpcontno='"+grpcontnos[i]+"' and z.organcomcode in ("+orgsLine+")) or";
				}
			}
			finSql = finSql.substring(0,finSql.length()-2);
			sql.append(" and b.contno in (select z.contno from insured_view z where "+finSql+")");
		}		
		//生育
//		sql.append(" and b.dutycode  like '617002%'");
//		sql.append(" and b.contno in (select z.contno from insured_view z where z.RELATIONTOMAININSURED='00')");
		//外籍
		sql.append(" and a.customerno in  ");
		sql.append("        (select insuredno                                             ");
		sql.append("           from insured_view aa, lccontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode  and  bb.contplanname not like '%外籍%'                         ");
		sql.append("         union                                                              ");
		sql.append("         select insuredno                                             ");
		sql.append("           from insured_view aa, lbcontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%外籍%')                        ");
	
		HashMap hmTPA = new HashMap();
		String sqlTPA = "select a.grpcontno||'^'||a.contplancode,decode(a.tpamark,'1','是TPA计划','非TPA计划') from lccontplan a"
			+ " where exists (select '' from lcgrppol where grpcontno = a.grpcontno and appflag = '1')";
		// GrpContNo
		StringTokenizer stTPA = new StringTokenizer(grpContNo, ",");
		if(stTPA.hasMoreTokens()){
			sqlTPA += " and ( a.grpcontno = '"+stTPA.nextToken()+"' ";
		}
        while(stTPA.hasMoreTokens()){
        	sqlTPA += " or a.grpcontno = '"+stTPA.nextToken()+"' ";
        }
        sqlTPA += ")";
		SSRS ssrsTPA = exeSQL.execSQL(sqlTPA);
		for (int a = 1; a <= ssrsTPA.getMaxRow(); a++) {
			hmTPA.put(ssrsTPA.GetText(a, 1), ssrsTPA.GetText(a, 2));
		}
		
		
		List list = new ArrayList();

		SSRS ssrs = exeSQL.execSQL(sql.toString());

		if ( ssrs.getMaxRow() <= 0) {
			return list;
		}
		for (int a = 1; a <= ssrs.getMaxRow(); a++) {
			String tGetDutyCode = ssrs.GetText(a, 34);
			//
			String tPolNo = ssrs.GetText(a, 42);
            String tGetDutyKind = ssrs.GetText(a, 44); 
            String tCaseNo = ssrs.GetText(a, 45);
       		String tDutyCode = ssrs.GetText(a, 49);
			String tOutDutyAmnt = ssrs.GetText(a, 50);
			
			String tContNo = ssrs.GetText(a, 48);//个单号
			
			Date tAccidentDate = FormatConverter.getDate(ssrs.GetText(a, 21));
			
			String tContPlanCode = null;
			String tContPlanName = null;
			
			//A.修正 19计划、 20计划名称
			if( lcHt != null ){
				
				Object o = lcHt.get( tContNo );
				if( o != null ){
					List c = (ArrayList)o;
					Date t1;
					Date t2 = FormatConverter.getDate("2005-1-1");
					for(Iterator i = c.iterator(); i.hasNext(); ){
						Object[] element = (Object[])i.next();
						t1 = t2;
						t2 = (Date) element[2];
						// >=t1 && <t2
						if( (t1.before( tAccidentDate ) || t1.equals( tAccidentDate )) && (t2.after( tAccidentDate ) ) ) {
							tContPlanCode = (String)element[0];
							tContPlanName = (String)element[1];
							break;
						}
					}
				}
			}
			//B.14起付线、15日药费限额、16日费用限额、17日床位费限额、18日检查费限额、19赔付比例 
			//B1 LCContPlanFactory SSRS
			
			//B2 getPayRate()
//			LLClaimDetailSchema tLLClaimDetailSchema = new LLClaimDetailSchema();
//       		tLLClaimDetailSchema.setCaseNo(claimInfo.getCaseNo());
//       		tLLClaimDetailSchema.setContNo(claimInfo.getContNo());
//       		tLLClaimDetailSchema.setGrpContNo(grpContNo);
//       		tLLClaimDetailSchema.setRiskCode(claimInfo.getRiskCode());
//       		tLLClaimDetailSchema.setGetDutyCode(claimInfo.getGetDutyCode());
//       		tLLClaimDetailSchema.setOutDutyAmnt(tOutDutyAmnt);
//       		tLLClaimDetailSchema.setGetDutyKind(tGetDutyKind);
//       		tLLClaimDetailSchema.setPolNo(tPolNo);
//       		tLLClaimDetailSchema.setDutyCode(tDutyCode);
//
//       		LLCaseSchema tLLCaseSchema = new LLCaseSchema();
//       		tLLCaseSchema.setCaseNo(claimInfo.getCaseNo());
//       		tLLCaseSchema.setCustomerNo(claimInfo.getCustomerNo());
//       		tLLCaseSchema.setAccidentDate(claimInfo.getAccidentDate());
			//C.
			//补齐21-25数据:就诊（住院）发生日、住院结束日期、住院天数、就医医院代码、医院等级
       		//补齐28-33数据:费用项目代码、费用项目名称、费用金额、自费金额、部分自付金额、医保支付金额

			//C1 账单明细 tmpDataTable
			SSRS tmpSSRS = null;
			if(!tCaseNo.substring(0, 1).equals("9")) { //以9开头的案件号为旧系统数据，没有帐单明细
				String tmpSql = " select " +
//						"b.mainfeeno," +
						"b.feetype,b.feedate,b.hospstartdate,b.hospenddate,b.realhospdate,b.hospitalcode, "
       			+ " (select ldcode.codename from ldhospital,ldcode where  LDHospital.hospitcode = b.HospitalCode and ldcode.codetype='llhospitaltype' and code=LDHospital.BusiTypeCode )||decode((select ldcode.codename from LDHospital,ldcode where LDHospital.hospitcode=b.HospitalCode and ldcode.codetype='levelcode' and LDHospital.Levelcode=ldcode.code),null,'未评级','','未评级',(select ldcode.codename from LDHospital,ldcode where LDHospital.hospitcode=b.HospitalCode and ldcode.codetype='levelcode' and LDHospital.Levelcode=ldcode.code)), "
       			+ " a.feeitemcode,a.feeitemname,a.fee,a.selfamnt,a.refuseamnt,a.spayamnt "
       			+ " ,b.hospitalname "//医院名称
       			+ " from llfeemain b left join llcasereceipt a on a.caseno=b.caseno and a.mainfeeno=b.mainfeeno and a.fee>0 and a.feeitemcode in(select feecode from lmdutygetfeerela where getdutycode='"+tGetDutyCode+"')" 
       			+ " where b.caseno='"+tCaseNo+"'";

//				tmpDataTable = mDataAccess.executeDataTable(tmpSql);
				tmpSSRS = exeSQL.execSQL(tmpSql);
       		}
			
			int i=1;
			do{
				if(countStr(tContPlanName,"外籍")!=0||countStr(ssrs.GetText(a, 13),"外籍")!=0){
					continue;
				}
				ClaimInfo claimInfo = new ClaimInfo();
				//1分公司代码
				claimInfo.setBranchCode(ssrs.GetText(a, 1));
				//2团体保单号
				claimInfo.setGrpContNo(ssrs.GetText(a, 2));
				//3保单生效日
				claimInfo.setPolValiDate( FormatConverter.getDate(ssrs.GetText(a, 3)) );
				//4保单终止日
				claimInfo.setPolEndDate( FormatConverter.getDate(ssrs.GetText(a, 4)) );
				//5被保险人客户号
				claimInfo.setCustomerNo(ssrs.GetText(a, 5));
				//6出生年月
				claimInfo.setBirthday( FormatConverter.getDate(ssrs.GetText(a, 6)) );
				//7性别
				claimInfo.setSex(ssrs.GetText(a, 7));
				//8被保险人生效日
				claimInfo.setCValiDate( FormatConverter.getDate(ssrs.GetText(a, 8)) );
				//9被保险人终止日
				claimInfo.setCEndDate( FormatConverter.getDate(ssrs.GetText(a, 9)) );
				//10在职或退休
				claimInfo.setRetireFlag(ssrs.GetText(a, 10));
				//11与被保险人的关系
				claimInfo.setRelationToMainInsured(ssrs.GetText(a, 11));
				//12计划
				if( tContPlanCode == null ){
					claimInfo.setContPlanCode(ssrs.GetText(a, 12));
				} else {
					claimInfo.setContPlanCode(tContPlanCode);
				}
				//13计划名称
				if( tContPlanName == null ){
					claimInfo.setContPlanName(FormatConverter.getString(ssrs.GetText(a, 13)));
				} else {
					claimInfo.setContPlanName(FormatConverter.getString(tContPlanName));
				}
				//14起付线、15日药费限额、16日费用限额、17日床位费限额、18日检查费限额、19赔付比例 
				//20被保险人赔案号
				claimInfo.setCaseNo(ssrs.GetText(a, 20));
				//21就诊（住院）发生日(旧系统移植过来的案件,就诊日期修改为 出险日期)、22住院结束日期、23住院天数、24就医医院代码、25医院等级
				claimInfo.setAccidentDate( FormatConverter.getDate(ssrs.GetText(a, 21)) );
				/**
				 * AccidentDate 暂放 Case 的 出险日期，用于计算PayRates，之后会调整为 Case账单的 入院日期或门诊日期
				 */
				//26索赔原因（icd10名字）
				claimInfo.setDiseaseCode(ssrs.GetText(a, 26));
				//27申请赔付日期
				claimInfo.setApplyDate( FormatConverter.getDate(ssrs.GetText(a, 27)) );
				//28费用项目代码、29费用项目名称、30费用金额、31自费金额、32部分自付金额、33医保支付金额
				//34索赔项目代码
				claimInfo.setGetDutyCode(ssrs.GetText(a, 34));
				//35索赔项目名称
				claimInfo.setGetDutyName(ssrs.GetText(a, 35));
				//36实际赔付金额(同一责任第二条起置0)
				claimInfo.setRealPay( FormatConverter.getDouble(ssrs.GetText(a, 36)) );
				//37赔付日期
				claimInfo.setPayDate( FormatConverter.getDate(ssrs.GetText(a, 37)) );
				//38险种代码
				claimInfo.setRiskCode(ssrs.GetText(a, 38));
				//39出险类型
				//40索赔金额
				
				if(claimInfo.getRiskCode().equals("NIK03")||claimInfo.getRiskCode().equals("NIK07")||
						claimInfo.getRiskCode().equals("NIK08")||claimInfo.getRiskCode().equals("NIK09")||
						claimInfo.getRiskCode().equals("NIK10")||claimInfo.getRiskCode().equals("NIK11")||
						claimInfo.getRiskCode().equals("NIK01")||claimInfo.getRiskCode().equals("NIK02")||
						claimInfo.getRiskCode().equals("MIK01")||claimInfo.getRiskCode().equals("MIK02")||
						claimInfo.getRiskCode().equals("MKK01")||claimInfo.getRiskCode().equals("NIK12")){
					String[] tPayRate = this.getPayRate( claimInfo.getGrpContNo(), claimInfo.getRiskCode(), claimInfo.getContPlanCode(), tDutyCode, tGetDutyCode, null, null );
					//39门诊日限额
					claimInfo.setClinicMaxPayLimit(FormatConverter.getDouble(tPayRate[4]));
					//40检查费日限额
					claimInfo.setCheckDailyLimit(FormatConverter.getDouble(tPayRate[6]));
					//41保额
					claimInfo.setAmnt(FormatConverter.getDouble(ssrs.GetText(a, 52)));
					//42赔付比例
					try{
						claimInfo.setPayRatio(tPayRate[1]);
					}catch(Exception e){
						e.printStackTrace();
					}
					//43是否TPA标记
//					String tsql = "select decode(a.tpamark,'1','是TPA计划','非TPA计划') from lccontplan a"
//						+ " where exists (select '' from lcgrppol where grpcontno = a.grpcontno and appflag = '1')"
//						+ " and a.grpcontno='"+claimInfo.getGrpContNo()+"' and a.contplancode='"+claimInfo.getContPlanCode()+"'";
					claimInfo.setTpaflag((String)hmTPA.get(claimInfo.getGrpContNo()+"^"+claimInfo.getContPlanCode()));
					//44是否TPA赔付
					claimInfo.setInnetwork(ssrs.GetText(a, 54));
				}
				
				claimInfo.setContNo(ssrs.GetText(a, 48));//个单号
				
				claimInfo.setBusinesstypein(ssrs.GetText(a, 55));
				
				claimInfo.setCustomername(ssrs.GetText(a, 56));
				claimInfo.setGebclient(ssrs.GetText(a, 57));
				claimInfo.setDepartmentid(ssrs.GetText(a, 58));
				claimInfo.setOccupationcode(ssrs.GetText(a, 59));
				claimInfo.setGiveTypeDesc(ssrs.GetText(a, 60));
				//C2
				if (tmpSSRS!=null && tmpSSRS.getMaxRow()>0) {
					if( i>1 ) { //实际赔付金额(同一责任第二条起置0)
						claimInfo.setRealPay(0);
       				}
       			    //门诊帐单
       				if(tmpSSRS.GetText(i, 1)!=null && !tmpSSRS.GetText(i, 1).equals("") && tmpSSRS.GetText(i, 1).equals("1") ) { 
       					claimInfo.setAccidentDate( FormatConverter.getDate(tmpSSRS.GetText(i, 2)) );
           			} else {//住院帐单
           				claimInfo.setAccidentDate( "".equals(tmpSSRS.GetText(i, 3))?claimInfo.getAccidentDate():FormatConverter.getDate(tmpSSRS.GetText(i, 3)) );
           				claimInfo.setLeaveHospDate( FormatConverter.getDate(tmpSSRS.GetText(i, 4)) );
           				claimInfo.setHospStayLength( FormatConverter.getInt(tmpSSRS.GetText(i, 5)) );
           			}
       				String hospCode = tmpSSRS.GetText(i, 6);   //就医医院代码
       				claimInfo.setHospCode(hospCode.replaceAll(",", "@")); //医院代码中的','替换成其他字符'@' 
       				claimInfo.setHospClass(tmpSSRS.GetText(i, 7));
       				claimInfo.setExpenseCode( tmpSSRS.GetText(i, 8) );
       				claimInfo.setExpenseName( (tmpSSRS.GetText(i, 9)).replaceAll(",", "，") );
       				claimInfo.setExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 10)) );
       				claimInfo.setOwnExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 11)) );
       				claimInfo.setPartExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 12)) );
       				claimInfo.setSsExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 13)) );
       				//
       				
       				String hospName = tmpSSRS.GetText(i, 14);
       				hospName = hospName==null?"":hospName.replaceAll(",", "，");
       				claimInfo.sethospName( hospName );
       				
       				//因为只有总的理算金额，就只放第一条记录
       				if(i==1){
						claimInfo.setClaimmoney(FormatConverter.getDouble(ssrs.GetText(a, 61)));
       				}else{
						claimInfo.setClaimmoney(0);
       				}
					claimInfo.setPayable(claimInfo.getExpenseAmnt()-claimInfo.getOwnExpenseAmnt()-claimInfo.getPartExpenseAmnt()-claimInfo.getSsExpenseAmnt());
					claimInfo.setSsScope(claimInfo.getExpenseAmnt()-claimInfo.getOwnExpenseAmnt()-claimInfo.getPartExpenseAmnt());
       				
       				i++;
				}else{
					claimInfo.setClaimmoney(FormatConverter.getDouble(ssrs.GetText(a, 61)));
					claimInfo.setPayable(claimInfo.getExpenseAmnt()-claimInfo.getOwnExpenseAmnt()-claimInfo.getPartExpenseAmnt()-claimInfo.getSsExpenseAmnt());
					claimInfo.setSsScope(claimInfo.getExpenseAmnt()-claimInfo.getOwnExpenseAmnt()-claimInfo.getPartExpenseAmnt());
				}
				list.add(claimInfo);
			}while( tmpSSRS!=null && tmpSSRS.getMaxRow()>0 && i<=tmpSSRS.getMaxRow());
			
			
		}
//		try {
//			mDataAccess.close();
//			mDataAccess = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			mDataAccess = null;
//		}
		return list;
	}
	
	/**
	 * 年龄段
	 * @param age
	 * @return
	 */
	public String getAgeBand( int age ) {
		String ageBand = "";
		if( age >= 0 ) {
			if( age < 100 ) {
				int a = (age/5)*5;
				int b = a + 4;
				ageBand = a + " ~ " + b ;
			} else {
				ageBand = "> 100";
			}
		}
		return ageBand;
	}
	
	/**
	 * 累计 人-理赔责任-每次就诊 信息
	 * @param ahft
	 * @return
	 */
	public Hashtable queryClmPerVis( List ahft ) {
		Hashtable clmPerVisHt = new Hashtable();
		for(Iterator i = ahft.iterator(); i.hasNext(); ){
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			StringBuffer key = new StringBuffer();
			key.append(claimInfo.getCustomerNo());//客户号
			key.append("#");
			key.append(claimInfo.getContNo());//个单
			key.append("#");
			key.append(claimInfo.getRiskCode());//险种
			key.append("#");
			key.append(claimInfo.getContPlanCode());//计划
			key.append("#");
			key.append(claimInfo.getGetDutyCode());//理赔责任
			key.append("#");
			key.append(claimInfo.getAccidentDate());//就诊日
			key.append("#");
			key.append(claimInfo.getHospCode());//医院
			
			Object o = clmPerVisHt.get(key.toString());
			if( o == null ) {
				// opip
				if( ( "615201".equals(claimInfo.getGetDutyCode()) || "617201".equals(claimInfo.getGetDutyCode()) ) && claimInfo.getHospStayLength() == 0 ){
					claimInfo.setOpip(0);
				} else {
					if(claimInfo.getPayable()>0){
						claimInfo.setOpip(1);
					}else{
						claimInfo.setOpip(0);
					}
				}
//				ClaimInfo clm = (ClaimInfo)this.clone(claimInfo);
				ClaimInfo clm = claimInfo;
				clmPerVisHt.put(key.toString(), clm);
			} else {
				ClaimInfo clm = (ClaimInfo)o;
				clm.setExpenseAmnt( clm.getExpenseAmnt() + claimInfo.getExpenseAmnt() );// 费用金额
				clm.setOwnExpenseAmnt( clm.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt() );// 自费金额
				clm.setPartExpenseAmnt( clm.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt() );// 部分自付金额
				clm.setSsExpenseAmnt( clm.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt() );// 医保支付金额
				clm.setRealPay( clm.getRealPay() + claimInfo.getRealPay() );// 实际赔付金额
				clm.setClaimmoney( clm.getClaimmoney() + claimInfo.getClaimmoney() );// 理算金额
				clm.setPayable( clm.getPayable() + claimInfo.getPayable() );// Payable
				clm.setSsScope( clm.getSsScope() + claimInfo.getSsScope() );// 医保范围内
			}
		}

		return clmPerVisHt;
	}
	
	/**
	 * 累计 人-责任-年 信息
	 * @param clmPerVisHt
	 * @param lmDutyGetRelaHt
	 * @return
	 */
	public Hashtable queryClmBenAnnual( Hashtable clmPerVisHt ) {
		Hashtable clmBenAnnualHt = new Hashtable();
		for(Iterator i = clmPerVisHt.values().iterator(); i.hasNext(); ){
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			StringBuffer key = new StringBuffer();
			key.append(claimInfo.getCustomerNo());//客户号
			key.append("#");
			key.append(claimInfo.getContNo());//个单
			key.append("#");
			key.append(claimInfo.getRiskCode());//险种
			key.append("#");
			key.append(claimInfo.getContPlanCode());//计划
			key.append("#");
			//
			key.append(claimInfo.getGetDutyCode());//赔付责任
////			key.append(claimInfo.getDutyCode());//责任
//			String getDutyCode = claimInfo.getGetDutyCode();
//			Object obj = lmDutyGetRelaHt.get(getDutyCode);
//			if( obj == null ) {
//				
////				key.append(claimInfo.getGetDutyCode());
////				String misInfo = (misMatchList.size()+1)+" GetDutyCode out of range : ";
////				misInfo += key.toString();
////				misInfo += " 费用金额:" + claimInfo.getExpenseAmnt() ;
////				misInfo += " 自费金额:" + claimInfo.getOwnExpenseAmnt() ;
////				misInfo += " 部分自付金额:" + claimInfo.getPartExpenseAmnt() ;
////				misInfo += " 医保支付金额:" + claimInfo.getSsExpenseAmnt() ;
////				misInfo += " 实际赔付金额:" + claimInfo.getRealPay() ;
////				misInfo += "\n";
////				misMatchList.add(misInfo);
//				continue;
//			} 
//			String dutyCode = (String) obj;
//			key.append(dutyCode);//责任

			Object o = clmBenAnnualHt.get(key.toString());
			if( o == null ) {
//				ClaimInfo clm = (ClaimInfo)this.clone(claimInfo);
				ClaimInfo clm = claimInfo;
				if(clm.getPayable()>0){
					clm.setVisitTimes( 1 );
				}else{
					clm.setVisitTimes( 0 );
				}
				clmBenAnnualHt.put(key.toString(), clm);
			} else {
				ClaimInfo clm = (ClaimInfo)o;
				clm.setHospStayLength( clm.getHospStayLength() + claimInfo.getHospStayLength() );// 住院天数
				clm.setExpenseAmnt( clm.getExpenseAmnt() + claimInfo.getExpenseAmnt() );// 费用金额
				clm.setOwnExpenseAmnt( clm.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt() );// 自费金额
				clm.setPartExpenseAmnt( clm.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt() );// 部分自付金额
				clm.setSsExpenseAmnt( clm.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt() );// 医保支付金额
				clm.setRealPay( clm.getRealPay() + claimInfo.getRealPay() );// 实际赔付金额
				if(claimInfo.getPayable()>0){
					clm.setOpip( clm.getOpip() + claimInfo.getOpip() );// 门诊就诊次数
					clm.setVisitTimes( clm.getVisitTimes() + 1 );// 就诊次数
				}
				clm.setClaimmoney( clm.getClaimmoney() + claimInfo.getClaimmoney() );// 理算金额
				clm.setPayable( clm.getPayable() + claimInfo.getPayable() );// Payable
				clm.setSsScope( clm.getSsScope() + claimInfo.getSsScope() );// 医保范围内			
			}
		}
		return clmBenAnnualHt;
	}
	
	/**
	 * 人员责任-理赔  匹配
	 * @param insList
	 * @param clmHt
	 * @return
	 */
	public List matchCertClm( List insList, Hashtable claimAnnual ) {
		Hashtable clmHt = (Hashtable)claimAnnual.clone(); // matchCertClm 将配合的信息remove, 剩下的作为mismatch信息
		for(Iterator i = insList.iterator(); i.hasNext(); ) {
			InsuredInfo insuredInfo = (InsuredInfo)i.next();
			String dutyCode = insuredInfo.getDutyCode();
			if(dutyCode==null){
				continue;
			}
			Object obj = lmDutyGetRelaMap.get(dutyCode);
			if(obj==null){
				continue;
			}
			String[] getDutyCodes = ((String)obj).split("_");
			for(int j=0;j<getDutyCodes.length;j++){
				StringBuffer key = new StringBuffer();
				key.append(insuredInfo.getCustomerNo());//客户号
				key.append("#");
				key.append(insuredInfo.getContno());//个单
				key.append("#");
				key.append(insuredInfo.getRiskCode());//险种
				key.append("#");
				key.append(insuredInfo.getContPlanCode());//计划
				key.append("#");
				key.append( getDutyCodes[j] );//赔付责任
				Object o = clmHt.get(key.toString());
				
				if( o != null ) {
					ClaimInfo claimInfo = (ClaimInfo)o;
					//By Fang for 一个责任对应多个给付责任的情况需要将相关数据相加 20111213
					insuredInfo.setExpenseAmnt(insuredInfo.getExpenseAmnt() + claimInfo.getExpenseAmnt());
					insuredInfo.setOwnExpenseAmnt(insuredInfo.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt());
					insuredInfo.setPartExpenseAmnt(insuredInfo.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt());
					insuredInfo.setSsExpenseAmnt(insuredInfo.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt());
					insuredInfo.setRealPay(insuredInfo.getRealPay() + claimInfo.getRealPay());
					//End 20111213
					insuredInfo.setOpip(claimInfo.getOpip());
					insuredInfo.setVisitTimes(claimInfo.getVisitTimes());
					insuredInfo.setHospStayLength(claimInfo.getHospStayLength());
					
					insuredInfo.setClaimmoney(claimInfo.getClaimmoney());// 理算金额
					insuredInfo.setPayable(claimInfo.getPayable());// Payable
					insuredInfo.setSsScope(claimInfo.getSsScope());// 医保范围内		
					//
					clmHt.remove(key.toString());
				}
			}
			
		}
		
		// clmHt mismatch
		Set keySet = clmHt.keySet();
		for(Iterator i = keySet.iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			StringBuffer misInfo = new StringBuffer();
			
			String[] keyItems = (key).split("#");
//			misInfo.append( (misMatchList.size()+1)+" MisMatch! : " );
//			misInfo.append( key  + "," );
			ClaimInfo claimInfo = (ClaimInfo)clmHt.get(key);
			double tmp = Arith.round(claimInfo.getRealPay(), 2);
			if(tmp!=0){
				misInfo.append( claimInfo.getGrpContNo() + "," );
				misInfo.append( keyItems[0] + "," );
				misInfo.append( keyItems[1] + "," );
				misInfo.append( keyItems[2] + "," );
				misInfo.append( keyItems[3] + "," );
				misInfo.append( keyItems[4] + "," );
				misInfo.append( tmp + "\r\n" );
				misMatchList.add(misInfo.toString());
			}
		}
		
		return insList;
	}
	
	/**
     * 得到起付线、赔付比例,意外赔付比例,日药费限额、日费用限额、日床位费限额、日检查费限额、
     */
    public String[] getPayRate(String tGrpContNo,String tRiskCode,String tContPlanCode,String tDutyCode,String tGetDutyCode,String tGetLimit,String tGetRate){
    //得到起付线、赔付比例,意外赔付比例,日药费限额、日费用限额、日床位费限额、日检查费限额、
	 String GetRate[] = {"0","0","0","0","0","0","0"} ;
     if(tRiskCode.equals("MKK01"))
     {

     }
	 if(tRiskCode.equals("NIK12"))
	 {
    	 ExeSQL tExeSQL = new ExeSQL();
		 if(tDutyCode.equals("602001"))  //住院责任
		 {
			 String strSQL = "select params from LCContPlanFactory where grpcontno='"+tGrpContNo+"' "
				 + " and riskcode='NIK12' "
				 + " and ContPlanCode = '"+tContPlanCode+"' " 
				 + " and FactoryType='000004' and OtherNo in('602201','602202','602203','602204') and FactoryCode<>'000007' "
				 + " and factoryname='PayPercent' "
				 + " order by otherno ";
			 String tRate = "";
			 SSRS tSSRS = exeSQL.execSQL(strSQL);
			 if (tSSRS.getMaxRow() > 0) {
           	    for (int i = 1; i <= tSSRS.getMaxRow(); i++) { 
           	    	tRate = tRate+tSSRS.GetText(i, 1)+"|";
           	    }
           	    tRate = tRate.substring(0, tRate.length()-1);
			 }
			 if(tGetLimit!=null && !tGetLimit.equals(""))
	    	 {
	    	       GetRate[0] = tGetLimit;
	    	 }
			 if(tRate!=null && !tRate.equals(""))
	    	 {
	    	       GetRate[1] = tRate;
	    	 }
		 }
		 else if(tDutyCode.equals("602002"))  //门诊责任
		 {
			 String strSQL = "select params from LCContPlanFactory where grpcontno='"+tGrpContNo+"' "
			 + " and riskcode='NIK12' "
			 + " and ContPlanCode = '"+tContPlanCode+"' " 
			 + " and FactoryType='000004' and OtherNo in('602205','602206','602207') and FactoryCode<>'000007' "
			 + " and factoryname='PayPercent' "
			 + " order by otherno ";
			 String tRate = "";
			 SSRS tSSRS = exeSQL.execSQL(strSQL);
			 if (tSSRS.getMaxRow() > 0) {
				 for (int i = 1; i <= tSSRS.getMaxRow(); i++) { 
					 tRate = tRate+tSSRS.GetText(i, 1)+"|";
				 }
				 tRate = tRate.substring(0, tRate.length()-1);
			 }
			 if(tGetLimit!=null && !tGetLimit.equals(""))
			 {
				 GetRate[0] = tGetLimit;
			 }
			 if(tRate!=null && !tRate.equals(""))
			 {
				 GetRate[1] = tRate;
			 }
		 }
		 else if(tDutyCode.equals("602003"))  //自付二责任
		 {
			 String strSQL = "select params from LCContPlanFactory where grpcontno='"+tGrpContNo+"' "
			 + " and riskcode='NIK12' "
			 + " and ContPlanCode = '"+tContPlanCode+"' " 
			 + " and FactoryType='000004' and OtherNo in('602208') and FactoryCode<>'000007' "
			 + " and factoryname='PayPercent' "
			 + " order by otherno ";
			 String tRate = "";
			 SSRS tSSRS = exeSQL.execSQL(strSQL);
			 if (tSSRS.getMaxRow() > 0) {
				 for (int i = 1; i <= tSSRS.getMaxRow(); i++) { 
					 tRate = tRate+tSSRS.GetText(i, 1)+"|";
				 }
				 tRate = tRate.substring(0, tRate.length()-1);
			 }
			 if(tRate!=null && !tRate.equals(""))
			 {
				 GetRate[1] = tRate;
			 }
		 }
		 else
		 {
			 GetRate[1] = tGetRate;
		 }
	 }
     else
     {
      
        //取理赔要约要素 除了理算给付金算法
	    LCContPlanFactoryDB tLCContPlanFactoryDB = new LCContPlanFactoryDB();
	    LCContPlanFactorySet tLCContPlanFactorySet = new LCContPlanFactorySet();
	        
	    String strSQL = "select * from LCContPlanFactory where grpcontno='"+tGrpContNo+"' " +
	        	" and riskcode='"+tRiskCode+"' " +
	        	" and ContPlanCode = '"+tContPlanCode+"' " +
	        	" and FactoryType='000004' and OtherNo='"+tGetDutyCode+"' and FactoryCode<>'000007'";
	    tLCContPlanFactorySet = tLCContPlanFactoryDB.executeQuery(strSQL);
	    TransferData tTransferData = new TransferData();
//	    PubCalculator tempPubCalculator = new PubCalculator();
	    boolean DiseasePayPercentFlag = false;//疾病赔付比例
	    boolean SuddennessPayPercentFlag = false;//意外赔付比例
	    boolean PayPercentFlag = false; //NIK12的赔付比例
	    
	    boolean MedicineMaxPayDailyFlag = false;    //日药费控制标记
	    boolean HospitalMaxPayFlag = false; //住院日限额控制标记 1/2 
	    boolean ClinicMaxPayFlag = false; //门诊日限额控制标记  1/2
	    boolean DailyBedMaxPayFlag = false;  //日床位费限额
	    boolean CheckMaxPayDailyFlag = false; //门诊日检查费限额标记
	    
	    
	    boolean MinPayDailyFlag =false;    //次(日)免赔额
	    boolean MinPayAnnuallyFlag = false;  //年免赔额
	    for (int m = 1; m <= tLCContPlanFactorySet.size(); m++) {
	        LCContPlanFactorySchema tLCContPlanFactorySchema = new LCContPlanFactorySchema();
			tLCContPlanFactorySchema = tLCContPlanFactorySet.get(m);
//			tempPubCalculator.setCalSql(tLCContPlanFactorySchema.getCalSql());
//			String tResult = tempPubCalculator.calculate();
			String tResult =tLCContPlanFactorySchema.getParams();
			if (tResult != null && !tResult.trim().equals("")) {
				if (tTransferData.findIndexByName(tLCContPlanFactorySchema.getFactoryName()) == -1) {
					tTransferData.setNameAndValue(tLCContPlanFactorySchema.getFactoryName(), tResult);
				
					if(tLCContPlanFactorySchema.getFactoryName().equals("DiseasePayPercent")){  //疾病赔付比例
						DiseasePayPercentFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("SuddennessPayPercent")){  //意外赔付比例
						SuddennessPayPercentFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("PayPercent")){
						PayPercentFlag = true;
					}
					
					if(tLCContPlanFactorySchema.getFactoryName().equals("MinPayDaily")){
						MinPayDailyFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("MinPayAnnually")){
						MinPayAnnuallyFlag = true;
					}
					//日药费控制标记
					if(tLCContPlanFactorySchema.getFactoryName().equals("MedicineMaxPayDaily")){
						MedicineMaxPayDailyFlag = true;
					}
					//住院日限额控制标记
					if(tLCContPlanFactorySchema.getFactoryName().equals("HospitalMaxPay")){
						HospitalMaxPayFlag = true;
					}
					//门诊日限额控制标记
					if(tLCContPlanFactorySchema.getFactoryName().equals("ClinicMaxPay")){
						ClinicMaxPayFlag = true;
					}	
                    //日床位费限额
					if(tLCContPlanFactorySchema.getFactoryName().equals("DailyBedMaxPay")){
						DailyBedMaxPayFlag = true;
					}
                     //门诊日检查费限额标记
					if(tLCContPlanFactorySchema.getFactoryName().equals("CheckMaxPayDaily")){
						CheckMaxPayDailyFlag = true;
					}
					
				}
			}
		}
	    
	    
	    //起付线
//	    String tOutDutyAmnt = String.valueOf(tLLClaimDetailSchema.getOutDutyAmnt());     //1取llclaimdetial表的免赔额
//	    if(tOutDutyAmnt != null){
//	    	GetRate[0] = Double.parseDouble(tOutDutyAmnt);
//	    }
	    
//	    if(GetRate[0] == 0){     //2取计算要素的免赔额
	    	if(MinPayDailyFlag){
	    		GetRate[0] = (String)tTransferData.getValueByName("MinPayDaily");
	    	}
	    	if(MinPayAnnuallyFlag){
	    		GetRate[0] = (String)tTransferData.getValueByName("MinPayAnnually");
	    	}
//	    }
        //赔付比例
//	    String tKind = tLLClaimDetailSchema.getGetDutyKind().substring(2, 3);
//	    if(tKind != null && tKind.equals("1")){
	    	if(DiseasePayPercentFlag){
	    		GetRate[1] = (String)tTransferData.getValueByName("DiseasePayPercent");
	    	}
//	    }else if(tKind != null && tKind.equals("2")){
	    	if(SuddennessPayPercentFlag){
	    		GetRate[2] = (String)tTransferData.getValueByName("SuddennessPayPercent");
	    	}
//	    }
	    if(PayPercentFlag){
	    	GetRate[1] = (String)tTransferData.getValueByName("PayPercent");
	    }
	    
       if(GetRate[0].equals("0")){//3取责任上的起付线
	    	
//		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
//	        //查询责任项
//	        boolean dutyflag = tSynLCLBDutyBL.Query(tLLClaimDetailSchema.
//	                getPolNo(), tLLClaimDetailSchema.getDutyCode(),
//	                tLLCaseSchema.getAccidentDate(), tLLCaseSchema.getAccidentDate());
//
//	        if (dutyflag) {
//	        	GetRate[0] = tSynLCLBDutyBL.getGetLimit();
//	        }
    	   if(tGetLimit!=null && !tGetLimit.equals(""))
    	   {
    	       GetRate[0] = tGetLimit;
    	   }
	    }
	    
	    if(GetRate[1].equals("0")){//取责任上的给付比例
	    	
//		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
//	        //查询责任项
//	        boolean dutyflag = tSynLCLBDutyBL.Query(tLLClaimDetailSchema.
//	                getPolNo(), tLLClaimDetailSchema.getDutyCode(),
//	                tLLCaseSchema.getAccidentDate(), tLLCaseSchema.getAccidentDate());
//
//	        if (dutyflag) {
//	        	GetRate[1] = tSynLCLBDutyBL.getGetRate();
//	        }
	    	if(tGetRate!=null && !tGetRate.equals(""))
	    	{
	    	    GetRate[1] = tGetRate;
	    	}
	    	
	    }
	    if(PubFun.isNumeric(GetRate[0]))
	    {
	    	if(Double.parseDouble(GetRate[0]) < 0){
	    		GetRate[0] = "0";
	    	}
	    }
	    if(GetRate[1].equals("0")){
	    	GetRate[1] = "1";
	    }
	    
         //	  日药费控制标记
	    if(MedicineMaxPayDailyFlag){
    		GetRate[3] = (String)tTransferData.getValueByName("MedicineMaxPayDaily");
    	}
        //	  住院日限额控制标记
	    if(HospitalMaxPayFlag){
    		GetRate[4] = (String)tTransferData.getValueByName("HospitalMaxPay");
    	}
        //	  门诊日限额控制标记
	    if(ClinicMaxPayFlag){
    		GetRate[4] = (String)tTransferData.getValueByName("ClinicMaxPay");
    	}
        //	  日床位费限额
	    if(DailyBedMaxPayFlag){
    		GetRate[5] = (String)tTransferData.getValueByName("DailyBedMaxPay");
    	}
        //	  门诊日检查费限额标记
	    if(CheckMaxPayDailyFlag){
    		GetRate[6] = (String)tTransferData.getValueByName("CheckMaxPayDaily");
    	}
      }
      return GetRate;
    	
    }
    //是否闰年
    public boolean isLeapYear( int year ){
    	if( year % 400 == 0 ){
    		return true;
    	} else if( year % 100 == 0 ){
    		return false;
    	} else if( year % 4 == 0 ){
    		return true;
    	} 
    	return false;
    }
	
	synchronized static void addSuccThreadCount(){
		SuccThreadCount++;
		ThreadCount--;
	}
	synchronized static void addFailThreadCount(){
		FailThreadCount++;
		ThreadCount--;
	}
	synchronized static void addThreadCount(){
		System.out.println(ThreadCount+"@@@@@@@@@@@@@@@");
		ThreadCount++;
	}
	synchronized static int getThreadCount(){
		return ThreadCount;
	}
	
    public static void main(String[] args) {
    	String[] tPayRate = new DataDownloadThread_waiji().getPayRate( "83000029500", "NIK12", "B", "602001", "602202", null, null );
    	System.out.println(tPayRate);

    }
}
