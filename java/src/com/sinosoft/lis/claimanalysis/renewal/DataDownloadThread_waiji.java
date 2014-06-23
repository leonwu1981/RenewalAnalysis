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
//�⼮
	
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
	int threadNum; // �̱߳��
	
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
	
	static int SuccThreadCount;  //�ɹ��߳���
	static int FailThreadCount;  //ʧ���߳���
	static int ThreadCount;  //����ִ���߳���
	
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
		// ���� lcduty
		List dutyInfoBatch = this.getDutyData(lcHt);
		// ���� -�˵�
		List claimInfoBatch = this.getClaimData(lcHt);
		ClaimInfo.outputClaimInfoCsv(claimInfoBatch, file_path, "claim_"+threadNum, 'M');
		
		// ÿ��(�⸶����)
		Hashtable claimPerVis = this.queryClmPerVis(claimInfoBatch);
		// ÿ��(����)
		Hashtable claimAnnual = this.queryClmBenAnnual(claimPerVis );
		
		// ƥ������
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
	 * ��ȡ LpInsured ���� LC��ȫ ��Ϣ
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
		// ����ʽ�� ������ʱ����ȵ���
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
			//�⼮
			if(countStr(contPlanName,"�⼮")!=0){
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
		
        //����
//      filter.append(" and a.dutycode like '617002%'");
//		filter.append(" and b.contno in (select z.contno from insured_view z where z.RELATIONTOMAININSURED='00')");
		//����
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("�����ĸ����ͷ�֧�������������ȣ�");
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
        // c��
		
		//1�ֹ�˾
		sb.append(" select b.managecom as branchCode,                                                        ");
		//2������
		sb.append("        b.grpcontno as grpContNo,                                                         ");
		//3������Ч��
		sb.append("        (SELECT CValiDate FROM lcgrpcont WHERE grpcontno = b.grpcontno) as polValiDate,   ");
		//4������ֹ��
		sb.append("        (SELECT MAX(EndDate) - 1 FROM lcpol WHERE grpcontno = b.grpcontno) as polEndDate, ");
		//5�Ƿ�����
		sb.append("        (SELECT decode(RepeatBill, '1', '��', '2', '��')                                  ");
		sb.append("           FROM lcgrpcont                                                                 ");
		sb.append("          WHERE grpcontno = b.grpcontno) as repeatBill,                                   ");
		//6Ͷ����λ
		sb.append("        (SELECT AppntName FROM lcgrpcont WHERE grpcontno = b.grpcontno) as appntName,    ");
		//7��λ����
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
		//8��ҵ���
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
		//9��Ӫҵ��
		sb.append("        (select MainBussiness                                                             ");
		sb.append("           from LDGrp                                                                     ");
		sb.append("          where customerno = (select CustomerNo                                           ");
		sb.append("                                from LCGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno                              ");
		sb.append("                              union                                                       ");
		sb.append("                              select CustomerNo                                           ");
		sb.append("                                from LBGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno)) as mainBussiness,          ");
		//10�������˺�
		sb.append("        b.insuredno as customerNo,                                                        ");
		//11�������˺�ͬ��
		sb.append("        b.contno as contno,                                                               ");
		//12����������Ч�� : �������ο�ʼʱ��
		sb.append("        a.getstartdate as cValiDate,                                                      ");
		//13����������ֹ��: �������ν���ʱ��
		sb.append("        (a.enddate - 1) as cEndDate,                                                      ");
		//14��������ְҵ�ȼ�
		sb.append("        (select bb.codename                                                               ");
		sb.append("           from lcinsured aa, ldcode bb                                                   ");
		sb.append("          where aa.OccupationType = bb.code                                               ");
		sb.append("            and bb.codetype = 'occupationtype'                                            ");
		sb.append("            and insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as occupationType,                                     ");
		//15��ְ������
		sb.append("        (select decode(retireflag, '0', '��ְ', '1', '����', '')                          ");
		sb.append("           from lcinsured                                                                  ");
		sb.append("          where insuredno = b.insuredno and grpcontno = b.grpcontno and contno=b.contno) as retireFlag,                                  ");
		//16�뱻�����˵Ĺ�ϵ
		sb.append("        (select decode(RelationToMainInsured,                                             ");
		sb.append("                       '00',                                                              ");
		sb.append("                       'Ա��',                                                       ");
		sb.append("                       '01',                                                              ");
		sb.append("                       '��ż',                                                       ");
		sb.append("                       '03',                                                              ");
		sb.append("                       '��Ů',                                                       ");
		sb.append("                       '04',                                                              ");
		sb.append("                       '��ĸ',                                                       ");
		sb.append("                       '28',                                                              ");
		sb.append("                       '����',                                                       ");
		sb.append("                       '29',                                                              ");
		sb.append("                       '��Ա',                                                       ");
		sb.append("                       '30',                                                              ");
		sb.append("                       'ҵ��Ա',                                                       ");
		sb.append("                       '����')                                                     ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as relationToMainInsured,                              ");
		//17��������
		sb.append("        b.insuredbirthday as birthday,                                                    ");
		//18�Ա�
		sb.append("        decode(b.insuredsex, '0', '��', '1', 'Ů') as sex,                                ");
		//19�ƻ� : ��ȡ��ǰ������LC��ȫ���ں�������
		sb.append("        (select contplancode                                                              ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as contPlanCode,                                       ");
		//20�ƻ�����
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
		//21����
		sb.append("        b.riskcode as riskCode,                                                           ");
		//22����
		sb.append("        a.dutycode as dutyCode,                                                           ");
		//23����
		sb.append("        a.amnt,                                 ");
		//24����
		sb.append("        a.sumprem,                                 ");
		//25����
		sb.append("        a.getlimit as payLine,                                                            ");
		//26��ҩ���޶27�շ����޶28�մ�λ���޶29�ռ����޶�
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		//30�⸶����
		sb.append("        a.getrate as payRatio,                                                            ");
		//31�����⸶����
		sb.append("        '',                                                                               ");
		//32�����������
		sb.append("        (select executecom                                                                ");
		sb.append("           from lcinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno) as executeCom,                                         ");
		//33 ��������(����MKK01,NIK12һ�Զ�)
		sb.append("        (select getdutycode from lmdutygetrela where dutycode=a.dutycode  and rownum=1 ), ");
		//34 ZT��ȫʱ��
		sb.append("        null as ZTDate                                                                      ");
		// added
		sb.append(" , ");
		//35 �ͻ���֧����    
		sb.append(" ( select organTable.grpname ");
		sb.append("     from lcorgan organTable, lcinsured insuredTable ");
		sb.append("    where organTable.grpcontno = insuredTable.grpcontno ");
		sb.append("      and organTable.organinnercode = insuredTable.organinnercode ");
		sb.append("      and insuredTable.insuredno = b.insuredno ");
		sb.append("      and insuredTable.contno = b.contno ), ");
		//36 ������������
		sb.append(" (select name ");
		sb.append("    from lcinsured ");
		sb.append("   where insuredno = b.insuredno ");
		sb.append("     and contno = b.contno), ");
		//37 ��ȫ����
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
		//�⼮
		sb.append(" and b.contno in  ");
		sb.append("        (select contno                                                           ");
		sb.append("           from lcinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%�⼮%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2'                                  ");
		sb.append("         union                                                                            ");
		sb.append("         select contno                                                           ");
		sb.append("           from lcinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%�⼮%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2')                         ");
		
		//
		sb.append("    and a.getstartdate <= a.enddate  ");
		//batch
		sb.append("    and a.contno > '"+minContNo+"'  ");
		sb.append("    and a.contno <= '"+maxContNo+"'  ");
		
//		sb.append(" and a.contno='600089648388' ");
		
		// b��
		sb.append(" union                                                                                    ");
		//1�ֹ�˾
		sb.append(" select b.managecom,                                                                      ");
		//2������
		sb.append("        b.grpcontno,                                                                      ");
		//3������Ч��
		sb.append("        (SELECT CValiDate FROM lcgrpcont WHERE grpcontno = b.grpcontno),                  ");
		//4������ֹ��
		sb.append("        (SELECT MAX(EndDate) - 1 FROM lcpol WHERE grpcontno = b.grpcontno),               ");
		//5�Ƿ�����
		sb.append("        (SELECT decode(RepeatBill, '1', '��', '2', '��')                                  ");
		sb.append("           FROM lcgrpcont                                                                 ");
		sb.append("          WHERE grpcontno = b.grpcontno),                                                 ");
		//6Ͷ����λ
		sb.append("        (SELECT AppntName FROM lcgrpcont WHERE grpcontno = b.grpcontno),                  ");
		//7��λ����
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
		//8��ҵ���
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
		//9��Ӫҵ��
		sb.append("        (select MainBussiness                                                             ");
		sb.append("           from LDGrp                                                                     ");
		sb.append("          where customerno = (select CustomerNo                                           ");
		sb.append("                                from LCGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno                              ");
		sb.append("                              union                                                       ");
		sb.append("                              select CustomerNo                                           ");
		sb.append("                                from LBGrpAppnt                                           ");
		sb.append("                               where GrpContNo = b.grpcontno)),                           ");
		//10�������˺�
		sb.append("        b.insuredno,                                                                      ");
		//11�������˺�ͬ��
		sb.append("        b.contno,                                                                         ");
		//12����������Ч��: �������ο�ʼʱ��
		sb.append("        a.getstartdate,                                                                   ");
		//13����������ֹ��: �������ν���ʱ��
		sb.append("        (a.enddate - 1),                                                                  ");
		
		//14��������ְҵ�ȼ�
		sb.append("        (select bb.codename                                                               ");
		sb.append("           from lbinsured aa, ldcode bb                                                   ");
		sb.append("          where aa.OccupationType = bb.code                                               ");
		sb.append("            and bb.codetype = 'occupationtype'                                            ");
		sb.append("            and insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//15��ְ������
		sb.append("        (select decode(retireflag, '0', '��ְ', '1', '����', '')                          ");
		sb.append("           from lbinsured                                                                  ");
		sb.append("          where insuredno = b.insuredno and grpcontno = b.grpcontno and contno=b.contno),                                                ");
		//16�뱻�����˵Ĺ�ϵ
		sb.append("        (select decode(RelationToMainInsured,                                             ");
		sb.append("                       '00',                                                              ");
		sb.append("                       'Ա��',                                                       ");
		sb.append("                       '01',                                                              ");
		sb.append("                       '��ż',                                                       ");
		sb.append("                       '03',                                                              ");
		sb.append("                       '��Ů',                                                       ");
		sb.append("                       '04',                                                              ");
		sb.append("                       '��ĸ',                                                       ");
		sb.append("                       '28',                                                              ");
		sb.append("                       '����',                                                       ");
		sb.append("                       '29',                                                              ");
		sb.append("                       '��Ա',                                                       ");
		sb.append("                       '30',                                                              ");
		sb.append("                       'ҵ��Ա',                                                       ");
		sb.append("                       '����')                                                     ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//17��������
		sb.append("        b.insuredbirthday,                                                                ");
		//18�Ա�
		sb.append("        decode(b.insuredsex, '0', '��', '1', 'Ů'),                                       ");
		//19�ƻ� : ��ȡ��ǰ������LC��ȫ���ں�������
		sb.append("        (select contplancode                                                              ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//20�ƻ�����
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
		//21����
		sb.append("        b.riskcode,                                                                       ");
		//22����
		sb.append("        a.dutycode,                                                                       ");
		//23����
		sb.append("        a.amnt,                                      										");
		//24����
		sb.append("        a.sumprem,                                    										");
		//25����
		sb.append("        a.getlimit,                                                                       ");
		//26��ҩ���޶27�շ����޶28�մ�λ���޶29�ռ����޶�
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		sb.append("        '',                                                                               ");
		//30�⸶����
		sb.append("        a.getrate,                                                                        ");
		//31�����⸶����
		sb.append("        '',                                                                               ");
		//32�����������
		sb.append("        (select executecom                                                                ");
		sb.append("           from lbinsured                                                                 ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno),                                                       ");
		//33 ��������(����MKK01,NIK12һ�Զ�)
		sb.append("        (select getdutycode from lmdutygetrela where dutycode=a.dutycode  and rownum=1 ), ");
		//34 ZT��ȫʱ��
		sb.append("        (select aa.EdorValiDate - 1                                                       ");
		sb.append("           from LPEdorItem aa                                                             ");
		sb.append("          where aa.edorno = b.edorno                                                      ");
		sb.append("            and aa.contno = b.contno                                                      ");
		sb.append("            and aa.insuredno = b.insuredno)                                               ");
		// added
		sb.append(" , ");
		//35 �ͻ���֧����
		sb.append(" ( select organTable.grpname ");
		sb.append("     from lcorgan organTable, lbinsured insuredTable ");
		sb.append("    where organTable.grpcontno = insuredTable.grpcontno ");
		sb.append("      and organTable.organinnercode = insuredTable.organinnercode ");
		sb.append("      and insuredTable.insuredno = b.insuredno ");
		sb.append("      and insuredTable.contno = b.contno ) ");
		sb.append(" , ");
		//36 ������������
		sb.append(" (select name ");
		sb.append("    from lbinsured ");
		sb.append("   where insuredno = b.insuredno ");
		sb.append("     and contno = b.contno), ");
		//37 ��ȫ����
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
		//�⼮
		sb.append(" and b.contno in  ");
		sb.append("        (select contno                                                           ");
		sb.append("           from lbinsured aa, lccontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%%�⼮%%' and bb.contplancode<>'2'  and aa.ContPlanCode<>'2'                                     ");
		sb.append("         union                                                                            ");
		sb.append("         select contno                                                           ");
		sb.append("           from lbinsured aa, lbcontplan bb                                               ");
		sb.append("          where insuredno = b.insuredno                                                   ");
		sb.append("            and contno = b.contno                                                         ");
		sb.append("            and aa.grpcontno = bb.grpcontno                                               ");
		sb.append("            and aa.ContPlanCode = bb.contplancode and bb.contplancode<>'2'  and aa.ContPlanCode<>'2' and  bb.contplanname not like '%%�⼮%%')                        ");
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
			if(countStr(ssrs.GetText(a, 20),"�⼮")!=0){
				continue;
			}
			//1�ֹ�˾
			insuredInfo.setBranchCode(ssrs.GetText(a, 1));
			//2������
			insuredInfo.setGrpContNo(ssrs.GetText(a, 2));
			//3������Ч��
			String polValidate = ssrs.GetText(a, 3);
			insuredInfo.setPolValiDate(FormatConverter.getDate(polValidate));
			//4������ֹ��
			String polEndDate = ssrs.GetText(a, 4);
			insuredInfo.setPolEndDate(FormatConverter.getDate(polEndDate));
			//5�Ƿ�����
			insuredInfo.setRepeatBill(ssrs.GetText(a, 5));
			//6Ͷ����λ
			insuredInfo.setAppntName(FormatConverter.getString(ssrs.GetText(a, 6)));
			//7��λ����
			insuredInfo.setGrpNature(FormatConverter.getString(ssrs.GetText(a, 7)));
			//8��ҵ���
			insuredInfo.setBusinessType(FormatConverter.getString(ssrs.GetText(a, 8)));
			//9��Ӫҵ��
			insuredInfo.setMainBussiness(FormatConverter.getString(ssrs.GetText(a, 9)));
			//10�������˺�
			insuredInfo.setCustomerNo(ssrs.GetText(a, 10));
			//11�������˺�ͬ��
			insuredInfo.setContno(ssrs.GetText(a, 11));
			//12����������Ч�� : �������ο�ʼʱ��
			insuredInfo.setCValiDate(FormatConverter.getDate(ssrs.GetText(a, 12)));
			//13����������ֹ��: �������ν���ʱ��
			insuredInfo.setCEndDate(FormatConverter.getDate(ssrs.GetText(a, 13)));
			//14��������ְҵ�ȼ�
			insuredInfo.setOccupationType(ssrs.GetText(a, 14));
			//15��ְ������
			insuredInfo.setRetireFlag(ssrs.GetText(a, 15));
			//16�뱻�����˵Ĺ�ϵ
			insuredInfo.setRelationToMainInsured(ssrs.GetText(a, 16));
			//17��������
			insuredInfo.setBirthday(FormatConverter.getDate(ssrs.GetText(a, 17)));
			//18�Ա�
			insuredInfo.setSex(ssrs.GetText(a, 18));
			//19�ƻ� : ��ȡ��ǰ������LC��ȫ���ں�������
			insuredInfo.setContPlanCode(ssrs.GetText(a, 19));
			//20�ƻ�����
			insuredInfo.setContPlanName(FormatConverter.getString(ssrs.GetText(a, 20)));
			//21����
			insuredInfo.setRiskCode(ssrs.GetText(a, 21));
			//22����
			insuredInfo.setDutyCode(ssrs.GetText(a, 22).substring(0, 6)); // 6λ����
			//23����
			insuredInfo.setAmnt(FormatConverter.getDouble(ssrs.GetText(a, 23)));
			//24����
			insuredInfo.setPrem(FormatConverter.getDouble(ssrs.GetText(a, 24)));
			//25����
			String tGetLimit = ssrs.GetText(a, 25);
			insuredInfo.setPayLine( ssrs.GetText(a, 25) );
			//26��ҩ���޶27�շ����޶28�մ�λ���޶29�ռ����޶�
			insuredInfo.setDrugDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 26)));
			insuredInfo.setFeeDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 27)));
			insuredInfo.setBedDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 28)));
			insuredInfo.setCheckDailyLimit(FormatConverter.getDouble(ssrs.GetText(a, 29)));
			//30�⸶����
			String tGetRate = ssrs.GetText(a, 30);
			insuredInfo.setPayRatio( ssrs.GetText(a, 30) );
			//31�����⸶����
			insuredInfo.setAccidentPayRatio(FormatConverter.getDouble(ssrs.GetText(a, 31)));
			//32�����������
			insuredInfo.setExecuteCom(ssrs.GetText(a, 32));
			//33�⸶����
			String tGetDutyCode = ssrs.GetText(a, 33);
			//34 ZT��ȫʱ��
			Date ztDate = FormatConverter.getDate( ssrs.GetText(a, 34) );
			
			if( ztDate != null  ) {
				if( ztDate.before( insuredInfo.getCEndDate() ) ){
					insuredInfo.setCEndDate( ztDate );
				}
			}
			
			//��ͨ��ztdate��ȥ������,������ȥ���˱����ݵĵط�
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
			//35 �ͻ���֧����
			insuredInfo.setAppntDept( FormatConverter.getString(ssrs.GetText(a, 35)) );
			//36 ������������
			insuredInfo.setCustomerName( FormatConverter.getString(ssrs.GetText(a, 36)) );
			//37 ��ȫ����
			String posFlag = ssrs.GetText(a, 37);
			if( posFlag == null ) {
				insuredInfo.setPosFlag( "" );
			} else if( "".equals(posFlag) || "ZT".equals(posFlag) ){
				insuredInfo.setPosFlag( posFlag );
			} else {
				// ���˱�ȫ��Ч���뱣����Ч����ͬ���������Ϊ����ʧ��ͳ��ʱ������NI
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
			
			//���� 19�ƻ��� 20�ƻ�����
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
			// ���� =���������˵ĸ�����Ч�գ����գ�1��/365
			int days_a_year = 365;
			
			insuredInfo.setAge(((int)((insuredInfo.getCValiDate().getTime()-insuredInfo.getBirthday().getTime())/(1000*60*60*24))+1)/days_a_year);// ����
			// �����
			insuredInfo.setAgeBand( this.getAgeBand(insuredInfo.getAge()) );
			// <x/y> ��¶�� = (min(������ֹ��,����������ֹ��)-����������Ч��+1)/(������ֹ��-������Ч��+1)
			//double a = ((double)(((assessDate.before(insuredInfo.getCEndDate())?assessDate:insuredInfo.getCEndDate()).getTime()-insuredInfo.getCValiDate().getTime())/(1000*60*60*24))+1);
			// <x/y> ��¶�� = (����������ֹ��-����������Ч��+1)/(������ֹ��-������Ч��+1)
			

			double x = (double)((insuredInfo.getCEndDate().getTime()-insuredInfo.getCValiDate().getTime())/(1000*60*60*24)) + 1;
			double y = (double)((insuredInfo.getPolEndDate().getTime()-insuredInfo.getPolValiDate().getTime())/(1000*60*60*24)) + 1;
			insuredInfo.setExposure(x/y);
			// �жϱ�����Ч��-������ֹ��֮���Ƿ���2��29��
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
			//25���ߡ�26��ҩ���޶27�շ����޶28�մ�λ���޶29�ռ����޶30�⸶������31�����⸶����
			String[] tPayRate = this.getPayRate( insuredInfo.getGrpContNo(), insuredInfo.getRiskCode(), insuredInfo.getContPlanCode(), insuredInfo.getDutyCode(), tGetDutyCode, tGetLimit, tGetRate );
			insuredInfo.setPayLine( tPayRate[0] );//�����(����)
    		insuredInfo.setDrugDailyLimit( FormatConverter.getDouble(tPayRate[3]) );//��ҩ���޶�
    		insuredInfo.setFeeDailyLimit( FormatConverter.getDouble(tPayRate[4]) );//�շ����޶�
    		insuredInfo.setBedDailyLimit( FormatConverter.getDouble(tPayRate[5]) );//�մ�λ���޶�
    		insuredInfo.setCheckDailyLimit( FormatConverter.getDouble(tPayRate[6]) );//�ռ����޶�
    		insuredInfo.setPayRatio( tPayRate[1] );//�⸶����
    		insuredInfo.setAccidentPayRatio( FormatConverter.getDouble(tPayRate[2]) );//�����⸶����
    		insuredInfo.setPayToDate(FormatConverter.getDate(ssrs.GetText(a, 39)));//��������
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
					insuredInfo2.setPrem(insuredInfo.getPrem()+insuredInfo2.getPrem());//���Ѻϲ�
					insuredInfo2.setAmnt(insuredInfo.getAmnt()>insuredInfo2.getAmnt()?insuredInfo.getAmnt():insuredInfo2.getAmnt());//����ȡ���
					FDate fd = new FDate();
					insuredInfo2.setCValiDate(FormatConverter.getDate(PubFun.getBeforeDate(fd.getString(insuredInfo2.getCValiDate()), fd.getString(insuredInfo.getCValiDate()))));//�ϲ���Ч�գ�ȡ�����
					insuredInfo2.setCEndDate(FormatConverter.getDate(PubFun.getLaterDate(fd.getString(insuredInfo2.getCEndDate()), fd.getString(insuredInfo.getCEndDate()))));//�ϲ������գ�ȡ�����
					insuredInfo2.setPayToDate(FormatConverter.getDate(PubFun.getLaterDate(fd.getString(insuredInfo2.getPayToDate()), fd.getString(insuredInfo.getPayToDate()))));//�ϲ������գ�ȡ�����
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
		sql.append("           '��'                                                              ");
		sql.append("          WHEN '1' THEN                                                     ");
		sql.append("           'Ů'                                                              ");
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
		sql.append("               '��ְ',                                                       ");
		sql.append("               '1',                                                         ");
		sql.append("               '����'),                                                      ");
		// 11�뱻�����˵Ĺ�ϵ
		sql.append("        (select decode(RelationToMainInsured,                                 ");
		sql.append("                       '00',                                                  ");
		sql.append("                       'Ա��',                                                ");
		sql.append("                       '01',                                                  ");
		sql.append("                       '��ż',                                                ");
		sql.append("                       '03',                                                  ");
		sql.append("                       '��Ů',                                                ");
		sql.append("                       '04',                                                  ");
		sql.append("                       '��ĸ',                                                ");
		sql.append("                       '28',                                                  ");
		sql.append("                       '����',                                                ");
		sql.append("                       '29',                                                  ");
		sql.append("                       '��Ա',                                                ");
		sql.append("                       '30',                                                  ");
		sql.append("                       'ҵ��Ա',                                              ");
		sql.append("                       '����')                                               ");
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
		sql.append("        decode(nvl(a.innetwork,'1'),'1','��TPA�⸶','TPA�⸶'),                                           ");
		// 55
		sql.append("  (select businesstypein from lcgrpcont where grpcontno=b.grpcontno)  businesstypein,");
		// 56 ����
		sql.append("  a.customername");
		//57
		sql.append("  ,(select gebclient from lcgrpcont where grpcontno=b.grpcontno)  gebclient");
		//58
		sql.append("  ,(select departmentid from lcgrpcont where grpcontno=b.grpcontno)  departmentid");
		//59
		sql.append("  ,(select OCCUPATIONCODE from insured_view where contno=b.contno)  OCCUPATIONCODE");
		//60
		sql.append("  ,(select aa.givetypedesc from llclaim aa where aa.caseno=a.caseno)  givetypedesc");
		//61������
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
		//����
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("�����ĸ����ͷ�֧�������������ȣ�");
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
		//����
//		sql.append(" and b.dutycode  like '617002%'");
//		sql.append(" and b.contno in (select z.contno from insured_view z where z.RELATIONTOMAININSURED='00')");
		//�⼮
		sql.append(" and a.customerno in  ");
		sql.append("        (select insuredno                                             ");
		sql.append("           from insured_view aa, lccontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode  and  bb.contplanname not like '%�⼮%'                         ");
		sql.append("         union                                                              ");
		sql.append("         select insuredno                                             ");
		sql.append("           from insured_view aa, lbcontplan bb                              ");
		sql.append("          where contno = b.contno                                           ");
		sql.append("            and insuredno = a.customerno                                    ");
		sql.append("            and aa.grpcontno = bb.grpcontno                                 ");
		sql.append("            and aa.ContPlanCode = bb.contplancode and  bb.contplanname not like '%�⼮%')                        ");
	
		HashMap hmTPA = new HashMap();
		String sqlTPA = "select a.grpcontno||'^'||a.contplancode,decode(a.tpamark,'1','��TPA�ƻ�','��TPA�ƻ�') from lccontplan a"
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
			
			String tContNo = ssrs.GetText(a, 48);//������
			
			Date tAccidentDate = FormatConverter.getDate(ssrs.GetText(a, 21));
			
			String tContPlanCode = null;
			String tContPlanName = null;
			
			//A.���� 19�ƻ��� 20�ƻ�����
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
			//B.14���ߡ�15��ҩ���޶16�շ����޶17�մ�λ���޶18�ռ����޶19�⸶���� 
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
			//����21-25����:���סԺ�������ա�סԺ�������ڡ�סԺ��������ҽҽԺ���롢ҽԺ�ȼ�
       		//����28-33����:������Ŀ���롢������Ŀ���ơ����ý��Էѽ������Ը���ҽ��֧�����

			//C1 �˵���ϸ tmpDataTable
			SSRS tmpSSRS = null;
			if(!tCaseNo.substring(0, 1).equals("9")) { //��9��ͷ�İ�����Ϊ��ϵͳ���ݣ�û���ʵ���ϸ
				String tmpSql = " select " +
//						"b.mainfeeno," +
						"b.feetype,b.feedate,b.hospstartdate,b.hospenddate,b.realhospdate,b.hospitalcode, "
       			+ " (select ldcode.codename from ldhospital,ldcode where  LDHospital.hospitcode = b.HospitalCode and ldcode.codetype='llhospitaltype' and code=LDHospital.BusiTypeCode )||decode((select ldcode.codename from LDHospital,ldcode where LDHospital.hospitcode=b.HospitalCode and ldcode.codetype='levelcode' and LDHospital.Levelcode=ldcode.code),null,'δ����','','δ����',(select ldcode.codename from LDHospital,ldcode where LDHospital.hospitcode=b.HospitalCode and ldcode.codetype='levelcode' and LDHospital.Levelcode=ldcode.code)), "
       			+ " a.feeitemcode,a.feeitemname,a.fee,a.selfamnt,a.refuseamnt,a.spayamnt "
       			+ " ,b.hospitalname "//ҽԺ����
       			+ " from llfeemain b left join llcasereceipt a on a.caseno=b.caseno and a.mainfeeno=b.mainfeeno and a.fee>0 and a.feeitemcode in(select feecode from lmdutygetfeerela where getdutycode='"+tGetDutyCode+"')" 
       			+ " where b.caseno='"+tCaseNo+"'";

//				tmpDataTable = mDataAccess.executeDataTable(tmpSql);
				tmpSSRS = exeSQL.execSQL(tmpSql);
       		}
			
			int i=1;
			do{
				if(countStr(tContPlanName,"�⼮")!=0||countStr(ssrs.GetText(a, 13),"�⼮")!=0){
					continue;
				}
				ClaimInfo claimInfo = new ClaimInfo();
				//1�ֹ�˾����
				claimInfo.setBranchCode(ssrs.GetText(a, 1));
				//2���屣����
				claimInfo.setGrpContNo(ssrs.GetText(a, 2));
				//3������Ч��
				claimInfo.setPolValiDate( FormatConverter.getDate(ssrs.GetText(a, 3)) );
				//4������ֹ��
				claimInfo.setPolEndDate( FormatConverter.getDate(ssrs.GetText(a, 4)) );
				//5�������˿ͻ���
				claimInfo.setCustomerNo(ssrs.GetText(a, 5));
				//6��������
				claimInfo.setBirthday( FormatConverter.getDate(ssrs.GetText(a, 6)) );
				//7�Ա�
				claimInfo.setSex(ssrs.GetText(a, 7));
				//8����������Ч��
				claimInfo.setCValiDate( FormatConverter.getDate(ssrs.GetText(a, 8)) );
				//9����������ֹ��
				claimInfo.setCEndDate( FormatConverter.getDate(ssrs.GetText(a, 9)) );
				//10��ְ������
				claimInfo.setRetireFlag(ssrs.GetText(a, 10));
				//11�뱻�����˵Ĺ�ϵ
				claimInfo.setRelationToMainInsured(ssrs.GetText(a, 11));
				//12�ƻ�
				if( tContPlanCode == null ){
					claimInfo.setContPlanCode(ssrs.GetText(a, 12));
				} else {
					claimInfo.setContPlanCode(tContPlanCode);
				}
				//13�ƻ�����
				if( tContPlanName == null ){
					claimInfo.setContPlanName(FormatConverter.getString(ssrs.GetText(a, 13)));
				} else {
					claimInfo.setContPlanName(FormatConverter.getString(tContPlanName));
				}
				//14���ߡ�15��ҩ���޶16�շ����޶17�մ�λ���޶18�ռ����޶19�⸶���� 
				//20���������ⰸ��
				claimInfo.setCaseNo(ssrs.GetText(a, 20));
				//21���סԺ��������(��ϵͳ��ֲ�����İ���,���������޸�Ϊ ��������)��22סԺ�������ڡ�23סԺ������24��ҽҽԺ���롢25ҽԺ�ȼ�
				claimInfo.setAccidentDate( FormatConverter.getDate(ssrs.GetText(a, 21)) );
				/**
				 * AccidentDate �ݷ� Case �� �������ڣ����ڼ���PayRates��֮������Ϊ Case�˵��� ��Ժ���ڻ���������
				 */
				//26����ԭ��icd10���֣�
				claimInfo.setDiseaseCode(ssrs.GetText(a, 26));
				//27�����⸶����
				claimInfo.setApplyDate( FormatConverter.getDate(ssrs.GetText(a, 27)) );
				//28������Ŀ���롢29������Ŀ���ơ�30���ý�31�Էѽ�32�����Ը���33ҽ��֧�����
				//34������Ŀ����
				claimInfo.setGetDutyCode(ssrs.GetText(a, 34));
				//35������Ŀ����
				claimInfo.setGetDutyName(ssrs.GetText(a, 35));
				//36ʵ���⸶���(ͬһ���εڶ�������0)
				claimInfo.setRealPay( FormatConverter.getDouble(ssrs.GetText(a, 36)) );
				//37�⸶����
				claimInfo.setPayDate( FormatConverter.getDate(ssrs.GetText(a, 37)) );
				//38���ִ���
				claimInfo.setRiskCode(ssrs.GetText(a, 38));
				//39��������
				//40������
				
				if(claimInfo.getRiskCode().equals("NIK03")||claimInfo.getRiskCode().equals("NIK07")||
						claimInfo.getRiskCode().equals("NIK08")||claimInfo.getRiskCode().equals("NIK09")||
						claimInfo.getRiskCode().equals("NIK10")||claimInfo.getRiskCode().equals("NIK11")||
						claimInfo.getRiskCode().equals("NIK01")||claimInfo.getRiskCode().equals("NIK02")||
						claimInfo.getRiskCode().equals("MIK01")||claimInfo.getRiskCode().equals("MIK02")||
						claimInfo.getRiskCode().equals("MKK01")||claimInfo.getRiskCode().equals("NIK12")){
					String[] tPayRate = this.getPayRate( claimInfo.getGrpContNo(), claimInfo.getRiskCode(), claimInfo.getContPlanCode(), tDutyCode, tGetDutyCode, null, null );
					//39�������޶�
					claimInfo.setClinicMaxPayLimit(FormatConverter.getDouble(tPayRate[4]));
					//40�������޶�
					claimInfo.setCheckDailyLimit(FormatConverter.getDouble(tPayRate[6]));
					//41����
					claimInfo.setAmnt(FormatConverter.getDouble(ssrs.GetText(a, 52)));
					//42�⸶����
					try{
						claimInfo.setPayRatio(tPayRate[1]);
					}catch(Exception e){
						e.printStackTrace();
					}
					//43�Ƿ�TPA���
//					String tsql = "select decode(a.tpamark,'1','��TPA�ƻ�','��TPA�ƻ�') from lccontplan a"
//						+ " where exists (select '' from lcgrppol where grpcontno = a.grpcontno and appflag = '1')"
//						+ " and a.grpcontno='"+claimInfo.getGrpContNo()+"' and a.contplancode='"+claimInfo.getContPlanCode()+"'";
					claimInfo.setTpaflag((String)hmTPA.get(claimInfo.getGrpContNo()+"^"+claimInfo.getContPlanCode()));
					//44�Ƿ�TPA�⸶
					claimInfo.setInnetwork(ssrs.GetText(a, 54));
				}
				
				claimInfo.setContNo(ssrs.GetText(a, 48));//������
				
				claimInfo.setBusinesstypein(ssrs.GetText(a, 55));
				
				claimInfo.setCustomername(ssrs.GetText(a, 56));
				claimInfo.setGebclient(ssrs.GetText(a, 57));
				claimInfo.setDepartmentid(ssrs.GetText(a, 58));
				claimInfo.setOccupationcode(ssrs.GetText(a, 59));
				claimInfo.setGiveTypeDesc(ssrs.GetText(a, 60));
				//C2
				if (tmpSSRS!=null && tmpSSRS.getMaxRow()>0) {
					if( i>1 ) { //ʵ���⸶���(ͬһ���εڶ�������0)
						claimInfo.setRealPay(0);
       				}
       			    //�����ʵ�
       				if(tmpSSRS.GetText(i, 1)!=null && !tmpSSRS.GetText(i, 1).equals("") && tmpSSRS.GetText(i, 1).equals("1") ) { 
       					claimInfo.setAccidentDate( FormatConverter.getDate(tmpSSRS.GetText(i, 2)) );
           			} else {//סԺ�ʵ�
           				claimInfo.setAccidentDate( "".equals(tmpSSRS.GetText(i, 3))?claimInfo.getAccidentDate():FormatConverter.getDate(tmpSSRS.GetText(i, 3)) );
           				claimInfo.setLeaveHospDate( FormatConverter.getDate(tmpSSRS.GetText(i, 4)) );
           				claimInfo.setHospStayLength( FormatConverter.getInt(tmpSSRS.GetText(i, 5)) );
           			}
       				String hospCode = tmpSSRS.GetText(i, 6);   //��ҽҽԺ����
       				claimInfo.setHospCode(hospCode.replaceAll(",", "@")); //ҽԺ�����е�','�滻�������ַ�'@' 
       				claimInfo.setHospClass(tmpSSRS.GetText(i, 7));
       				claimInfo.setExpenseCode( tmpSSRS.GetText(i, 8) );
       				claimInfo.setExpenseName( (tmpSSRS.GetText(i, 9)).replaceAll(",", "��") );
       				claimInfo.setExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 10)) );
       				claimInfo.setOwnExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 11)) );
       				claimInfo.setPartExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 12)) );
       				claimInfo.setSsExpenseAmnt( FormatConverter.getDouble(tmpSSRS.GetText(i, 13)) );
       				//
       				
       				String hospName = tmpSSRS.GetText(i, 14);
       				hospName = hospName==null?"":hospName.replaceAll(",", "��");
       				claimInfo.sethospName( hospName );
       				
       				//��Ϊֻ���ܵ��������ֻ�ŵ�һ����¼
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
	 * �����
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
	 * �ۼ� ��-��������-ÿ�ξ��� ��Ϣ
	 * @param ahft
	 * @return
	 */
	public Hashtable queryClmPerVis( List ahft ) {
		Hashtable clmPerVisHt = new Hashtable();
		for(Iterator i = ahft.iterator(); i.hasNext(); ){
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			StringBuffer key = new StringBuffer();
			key.append(claimInfo.getCustomerNo());//�ͻ���
			key.append("#");
			key.append(claimInfo.getContNo());//����
			key.append("#");
			key.append(claimInfo.getRiskCode());//����
			key.append("#");
			key.append(claimInfo.getContPlanCode());//�ƻ�
			key.append("#");
			key.append(claimInfo.getGetDutyCode());//��������
			key.append("#");
			key.append(claimInfo.getAccidentDate());//������
			key.append("#");
			key.append(claimInfo.getHospCode());//ҽԺ
			
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
				clm.setExpenseAmnt( clm.getExpenseAmnt() + claimInfo.getExpenseAmnt() );// ���ý��
				clm.setOwnExpenseAmnt( clm.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt() );// �Էѽ��
				clm.setPartExpenseAmnt( clm.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt() );// �����Ը����
				clm.setSsExpenseAmnt( clm.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt() );// ҽ��֧�����
				clm.setRealPay( clm.getRealPay() + claimInfo.getRealPay() );// ʵ���⸶���
				clm.setClaimmoney( clm.getClaimmoney() + claimInfo.getClaimmoney() );// ������
				clm.setPayable( clm.getPayable() + claimInfo.getPayable() );// Payable
				clm.setSsScope( clm.getSsScope() + claimInfo.getSsScope() );// ҽ����Χ��
			}
		}

		return clmPerVisHt;
	}
	
	/**
	 * �ۼ� ��-����-�� ��Ϣ
	 * @param clmPerVisHt
	 * @param lmDutyGetRelaHt
	 * @return
	 */
	public Hashtable queryClmBenAnnual( Hashtable clmPerVisHt ) {
		Hashtable clmBenAnnualHt = new Hashtable();
		for(Iterator i = clmPerVisHt.values().iterator(); i.hasNext(); ){
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			StringBuffer key = new StringBuffer();
			key.append(claimInfo.getCustomerNo());//�ͻ���
			key.append("#");
			key.append(claimInfo.getContNo());//����
			key.append("#");
			key.append(claimInfo.getRiskCode());//����
			key.append("#");
			key.append(claimInfo.getContPlanCode());//�ƻ�
			key.append("#");
			//
			key.append(claimInfo.getGetDutyCode());//�⸶����
////			key.append(claimInfo.getDutyCode());//����
//			String getDutyCode = claimInfo.getGetDutyCode();
//			Object obj = lmDutyGetRelaHt.get(getDutyCode);
//			if( obj == null ) {
//				
////				key.append(claimInfo.getGetDutyCode());
////				String misInfo = (misMatchList.size()+1)+" GetDutyCode out of range : ";
////				misInfo += key.toString();
////				misInfo += " ���ý��:" + claimInfo.getExpenseAmnt() ;
////				misInfo += " �Էѽ��:" + claimInfo.getOwnExpenseAmnt() ;
////				misInfo += " �����Ը����:" + claimInfo.getPartExpenseAmnt() ;
////				misInfo += " ҽ��֧�����:" + claimInfo.getSsExpenseAmnt() ;
////				misInfo += " ʵ���⸶���:" + claimInfo.getRealPay() ;
////				misInfo += "\n";
////				misMatchList.add(misInfo);
//				continue;
//			} 
//			String dutyCode = (String) obj;
//			key.append(dutyCode);//����

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
				clm.setHospStayLength( clm.getHospStayLength() + claimInfo.getHospStayLength() );// סԺ����
				clm.setExpenseAmnt( clm.getExpenseAmnt() + claimInfo.getExpenseAmnt() );// ���ý��
				clm.setOwnExpenseAmnt( clm.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt() );// �Էѽ��
				clm.setPartExpenseAmnt( clm.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt() );// �����Ը����
				clm.setSsExpenseAmnt( clm.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt() );// ҽ��֧�����
				clm.setRealPay( clm.getRealPay() + claimInfo.getRealPay() );// ʵ���⸶���
				if(claimInfo.getPayable()>0){
					clm.setOpip( clm.getOpip() + claimInfo.getOpip() );// ����������
					clm.setVisitTimes( clm.getVisitTimes() + 1 );// �������
				}
				clm.setClaimmoney( clm.getClaimmoney() + claimInfo.getClaimmoney() );// ������
				clm.setPayable( clm.getPayable() + claimInfo.getPayable() );// Payable
				clm.setSsScope( clm.getSsScope() + claimInfo.getSsScope() );// ҽ����Χ��			
			}
		}
		return clmBenAnnualHt;
	}
	
	/**
	 * ��Ա����-����  ƥ��
	 * @param insList
	 * @param clmHt
	 * @return
	 */
	public List matchCertClm( List insList, Hashtable claimAnnual ) {
		Hashtable clmHt = (Hashtable)claimAnnual.clone(); // matchCertClm ����ϵ���Ϣremove, ʣ�µ���Ϊmismatch��Ϣ
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
				key.append(insuredInfo.getCustomerNo());//�ͻ���
				key.append("#");
				key.append(insuredInfo.getContno());//����
				key.append("#");
				key.append(insuredInfo.getRiskCode());//����
				key.append("#");
				key.append(insuredInfo.getContPlanCode());//�ƻ�
				key.append("#");
				key.append( getDutyCodes[j] );//�⸶����
				Object o = clmHt.get(key.toString());
				
				if( o != null ) {
					ClaimInfo claimInfo = (ClaimInfo)o;
					//By Fang for һ�����ζ�Ӧ����������ε������Ҫ������������ 20111213
					insuredInfo.setExpenseAmnt(insuredInfo.getExpenseAmnt() + claimInfo.getExpenseAmnt());
					insuredInfo.setOwnExpenseAmnt(insuredInfo.getOwnExpenseAmnt() + claimInfo.getOwnExpenseAmnt());
					insuredInfo.setPartExpenseAmnt(insuredInfo.getPartExpenseAmnt() + claimInfo.getPartExpenseAmnt());
					insuredInfo.setSsExpenseAmnt(insuredInfo.getSsExpenseAmnt() + claimInfo.getSsExpenseAmnt());
					insuredInfo.setRealPay(insuredInfo.getRealPay() + claimInfo.getRealPay());
					//End 20111213
					insuredInfo.setOpip(claimInfo.getOpip());
					insuredInfo.setVisitTimes(claimInfo.getVisitTimes());
					insuredInfo.setHospStayLength(claimInfo.getHospStayLength());
					
					insuredInfo.setClaimmoney(claimInfo.getClaimmoney());// ������
					insuredInfo.setPayable(claimInfo.getPayable());// Payable
					insuredInfo.setSsScope(claimInfo.getSsScope());// ҽ����Χ��		
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
     * �õ����ߡ��⸶����,�����⸶����,��ҩ���޶�շ����޶�մ�λ���޶�ռ����޶
     */
    public String[] getPayRate(String tGrpContNo,String tRiskCode,String tContPlanCode,String tDutyCode,String tGetDutyCode,String tGetLimit,String tGetRate){
    //�õ����ߡ��⸶����,�����⸶����,��ҩ���޶�շ����޶�մ�λ���޶�ռ����޶
	 String GetRate[] = {"0","0","0","0","0","0","0"} ;
     if(tRiskCode.equals("MKK01"))
     {

     }
	 if(tRiskCode.equals("NIK12"))
	 {
    	 ExeSQL tExeSQL = new ExeSQL();
		 if(tDutyCode.equals("602001"))  //סԺ����
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
		 else if(tDutyCode.equals("602002"))  //��������
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
		 else if(tDutyCode.equals("602003"))  //�Ը�������
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
      
        //ȡ����ҪԼҪ�� ��������������㷨
	    LCContPlanFactoryDB tLCContPlanFactoryDB = new LCContPlanFactoryDB();
	    LCContPlanFactorySet tLCContPlanFactorySet = new LCContPlanFactorySet();
	        
	    String strSQL = "select * from LCContPlanFactory where grpcontno='"+tGrpContNo+"' " +
	        	" and riskcode='"+tRiskCode+"' " +
	        	" and ContPlanCode = '"+tContPlanCode+"' " +
	        	" and FactoryType='000004' and OtherNo='"+tGetDutyCode+"' and FactoryCode<>'000007'";
	    tLCContPlanFactorySet = tLCContPlanFactoryDB.executeQuery(strSQL);
	    TransferData tTransferData = new TransferData();
//	    PubCalculator tempPubCalculator = new PubCalculator();
	    boolean DiseasePayPercentFlag = false;//�����⸶����
	    boolean SuddennessPayPercentFlag = false;//�����⸶����
	    boolean PayPercentFlag = false; //NIK12���⸶����
	    
	    boolean MedicineMaxPayDailyFlag = false;    //��ҩ�ѿ��Ʊ��
	    boolean HospitalMaxPayFlag = false; //סԺ���޶���Ʊ�� 1/2 
	    boolean ClinicMaxPayFlag = false; //�������޶���Ʊ��  1/2
	    boolean DailyBedMaxPayFlag = false;  //�մ�λ���޶�
	    boolean CheckMaxPayDailyFlag = false; //�����ռ����޶���
	    
	    
	    boolean MinPayDailyFlag =false;    //��(��)�����
	    boolean MinPayAnnuallyFlag = false;  //�������
	    for (int m = 1; m <= tLCContPlanFactorySet.size(); m++) {
	        LCContPlanFactorySchema tLCContPlanFactorySchema = new LCContPlanFactorySchema();
			tLCContPlanFactorySchema = tLCContPlanFactorySet.get(m);
//			tempPubCalculator.setCalSql(tLCContPlanFactorySchema.getCalSql());
//			String tResult = tempPubCalculator.calculate();
			String tResult =tLCContPlanFactorySchema.getParams();
			if (tResult != null && !tResult.trim().equals("")) {
				if (tTransferData.findIndexByName(tLCContPlanFactorySchema.getFactoryName()) == -1) {
					tTransferData.setNameAndValue(tLCContPlanFactorySchema.getFactoryName(), tResult);
				
					if(tLCContPlanFactorySchema.getFactoryName().equals("DiseasePayPercent")){  //�����⸶����
						DiseasePayPercentFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("SuddennessPayPercent")){  //�����⸶����
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
					//��ҩ�ѿ��Ʊ��
					if(tLCContPlanFactorySchema.getFactoryName().equals("MedicineMaxPayDaily")){
						MedicineMaxPayDailyFlag = true;
					}
					//סԺ���޶���Ʊ��
					if(tLCContPlanFactorySchema.getFactoryName().equals("HospitalMaxPay")){
						HospitalMaxPayFlag = true;
					}
					//�������޶���Ʊ��
					if(tLCContPlanFactorySchema.getFactoryName().equals("ClinicMaxPay")){
						ClinicMaxPayFlag = true;
					}	
                    //�մ�λ���޶�
					if(tLCContPlanFactorySchema.getFactoryName().equals("DailyBedMaxPay")){
						DailyBedMaxPayFlag = true;
					}
                     //�����ռ����޶���
					if(tLCContPlanFactorySchema.getFactoryName().equals("CheckMaxPayDaily")){
						CheckMaxPayDailyFlag = true;
					}
					
				}
			}
		}
	    
	    
	    //����
//	    String tOutDutyAmnt = String.valueOf(tLLClaimDetailSchema.getOutDutyAmnt());     //1ȡllclaimdetial��������
//	    if(tOutDutyAmnt != null){
//	    	GetRate[0] = Double.parseDouble(tOutDutyAmnt);
//	    }
	    
//	    if(GetRate[0] == 0){     //2ȡ����Ҫ�ص������
	    	if(MinPayDailyFlag){
	    		GetRate[0] = (String)tTransferData.getValueByName("MinPayDaily");
	    	}
	    	if(MinPayAnnuallyFlag){
	    		GetRate[0] = (String)tTransferData.getValueByName("MinPayAnnually");
	    	}
//	    }
        //�⸶����
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
	    
       if(GetRate[0].equals("0")){//3ȡ�����ϵ�����
	    	
//		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
//	        //��ѯ������
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
	    
	    if(GetRate[1].equals("0")){//ȡ�����ϵĸ�������
	    	
//		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
//	        //��ѯ������
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
	    
         //	  ��ҩ�ѿ��Ʊ��
	    if(MedicineMaxPayDailyFlag){
    		GetRate[3] = (String)tTransferData.getValueByName("MedicineMaxPayDaily");
    	}
        //	  סԺ���޶���Ʊ��
	    if(HospitalMaxPayFlag){
    		GetRate[4] = (String)tTransferData.getValueByName("HospitalMaxPay");
    	}
        //	  �������޶���Ʊ��
	    if(ClinicMaxPayFlag){
    		GetRate[4] = (String)tTransferData.getValueByName("ClinicMaxPay");
    	}
        //	  �մ�λ���޶�
	    if(DailyBedMaxPayFlag){
    		GetRate[5] = (String)tTransferData.getValueByName("DailyBedMaxPay");
    	}
        //	  �����ռ����޶���
	    if(CheckMaxPayDailyFlag){
    		GetRate[6] = (String)tTransferData.getValueByName("CheckMaxPayDaily");
    	}
      }
      return GetRate;
    	
    }
    //�Ƿ�����
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
