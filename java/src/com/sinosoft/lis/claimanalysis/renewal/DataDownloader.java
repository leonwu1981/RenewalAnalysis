package com.sinosoft.lis.claimanalysis.renewal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class DataDownloader {
	final static int MaxThreadCount = 10;   //����߳���
	final static int BatchNum = 100;   //ÿ�δ���ı���
	private final static int CONT_BATCH_SIZE = 2000;
	
	ExeSQL exeSQL = new ExeSQL();
	FileProcessor FileProcessor = new FileProcessor();
	private String file_path;
	
	// download claim data loop condition
	private String minContNo = "";
	private String maxContNo = "";
	private int contBatchCount = CONT_BATCH_SIZE;
	
	public void downloadData(String filePath, String grpContNo, String startDate, String endDate, String manageCom, String riskCode,String orgs){
		int threadNum = 1;
		file_path = filePath;
		HashMap lmDutyGetRelaHt = this.getLmDutyGetRelaMap();
		DataDownloadThread.FailThreadCount = 0;
		DataDownloadThread.SuccThreadCount = 0;
		DataDownloadThread.ThreadCount = 0;
		
		//�������̫�࣬�ᵼ��ϵͳ������ΪÿBatchNum����������һ��
		String[] grpcontnos = grpContNo.split(",");
		String[] orgss = null;
		if(orgs!=null&&orgs.length()>0){
			orgss = orgs.split("\\|");
		}

		for (int i = 0; i < grpcontnos.length/BatchNum+1; i++) {
			int tmp = threadNum;
			//ÿ��ѭ��ǰ���˱����Ź�0
			minContNo = "";
			maxContNo = "";
			int leave = grpcontnos.length-BatchNum*i;
			
			String dealingGrpContNo = "";
			String dealingOrgs = "";
			for (int j = 0; j < (leave>BatchNum?BatchNum:leave); j++) {
				dealingGrpContNo += grpcontnos[i*BatchNum+j]+",";
				if(orgss!=null){
					dealingOrgs += orgss[i*BatchNum+j]+"|";
				}
			}
			dealingGrpContNo = dealingGrpContNo.substring(0,dealingGrpContNo.length()-1);
			if(orgss!=null){
				dealingOrgs = dealingOrgs.substring(0,dealingOrgs.length()-1);
			}
			
			do {
				if( !getContBatchInfo(dealingGrpContNo, startDate, endDate, manageCom, riskCode,dealingOrgs) ){
					continue;
				}
				boolean flag = true;
				
				while(flag){
					if( DataDownloadThread.getThreadCount() < MaxThreadCount ){
//						System.out.println("�߳�"+threadNum+"��ʼ���С�����  ��"+DataDownloadThread.getThreadCount()+"������");
//						Thread thread = new Thread(new DataDownloadThread(threadNum, minContNo, maxContNo, file_path+"data/", grpContNo, startDate, endDate, manageCom, riskCode, lmDutyGetRelaHt)); 
						Thread thread = new Thread(new DataDownloadThread(threadNum, minContNo, maxContNo, filePath, dealingGrpContNo, startDate, endDate, manageCom, riskCode, lmDutyGetRelaHt,dealingOrgs)); 
						thread.start(); 
						flag = false;
					} else {
						try {
							Thread.sleep(1000*10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				// loop
				threadNum++;
				minContNo = maxContNo;

			} while( contBatchCount == CONT_BATCH_SIZE );
			while( (DataDownloadThread.FailThreadCount + DataDownloadThread.SuccThreadCount ) < (threadNum-1) )
			{
				try {
					Thread.sleep(1000*30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("��ɵ�"+i+"����"+dealingGrpContNo+"��\r\n��ʼ��"+tmp+"��������"+(threadNum-1));
		}
	
		


		System.out.println("fail:"+DataDownloadThread.FailThreadCount);
		System.out.println("succ:"+DataDownloadThread.SuccThreadCount);
		System.out.println("threadNum:"+threadNum);
		// head
		String claim_head = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�������˿ͻ���,��������,�Ա�,����������Ч��,����������ֹ��,��ְ������,�뱻�����˵Ĺ�ϵ,�ƻ�����,�ƻ�����,���������ⰸ��,��������,סԺ��������,סԺ����,��ҽҽԺ����,��ҽҽԺ����,ҽԺ�ȼ�,����ԭ��,�����⸶����,������Ŀ����,������Ŀ����,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,������Ŀ����,������Ŀ����,ʵ���⸶���,�⸶����,���ִ���,����ʱ���,����ʱ���,�����ӳ�ʱ��,���,������,�������޶�,�������޶�,����,�⸶����,�Ƿ�TPA���,�Ƿ�TPA�⸶,��ҵ,����,GEB�ͻ�,���ű���,ְҵ����,����״̬,������,Payable,ҽ����Χ��\r\n";
		FileProcessor.outputFile(claim_head, file_path, "claim_0", "csv");
		String insured_head = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�Ƿ�����,Ͷ����λ,Ͷ����λ����,��λ����,��ҵ���,��Ӫҵ��,�������˿ͻ���,������������,����������Ч��,����������ֹ��,��������ְҵ�ȼ�,��ְ������,�뱻�����˵Ĺ�ϵ,��������,�Ա�,�ƻ�����,�ƻ�����,����,����,����,����,����,��ҩ���޶�,�շ����޶�,�մ�λ���޶�,�ռ����޶�,�⸶����,�����⸶����,�������,����,�����,�ع���,�ع���2,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,ʵ���⸶���,����������,�������,��������,סԺ����,��ȫ����,���,������,������,��������,��ҵ,GEB�ͻ�,���ű���,ְҵ����,����״̬,������,Payable,ҽ����Χ��\r\n";
		FileProcessor.outputFile(insured_head, file_path, "match_0", "csv");
		String misMatch_head = "���屣����,�������˿ͻ���,���˱�����,����,�ƻ�,�⸶����,ʵ���⸶���\r\n";
		FileProcessor.outputFile(misMatch_head, file_path, "misMatch_0", "csv");

		// mergeFiles
		FileProcessor.mergeFiles(file_path, "claim", 0, threadNum-1, "csv", "claim" );
		FileProcessor.mergeFiles(file_path, "match", 0, threadNum-1, "csv", "match" );
		FileProcessor.mergeFiles(file_path, "misMatch", 0, threadNum-1, "csv", "misMatch" );
		
		// 
		FileProcessor.newFolder( file_path + "Incurred-Pay" );
		FileProcessor.newFolder( file_path + "IBNR" );
	}
	
	public boolean getContBatchInfo(String grpContNo, String startDate, String endDate, String manageCom, String riskCode,String orgs){
		StringBuffer filter = new StringBuffer();
		
		// GrpContNo
		StringTokenizer st = new StringTokenizer(grpContNo, ",");
		if(st.hasMoreTokens()){
			filter.append(" and ( b.grpcontno = '").append(st.nextToken()).append("' ");
			while(st.hasMoreTokens()){
	        	filter.append(" or b.grpcontno = '").append(st.nextToken()).append("' ");
	        }
	        filter.append(" ) ");
		}
        
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
		
		//��������
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("�����ĸ����ͷ�֧�������������ȣ�");
				return false;
			}
			String finSql = " and (";
			for (int i = 0; i < grpcontnos.length; i++) {
				if("%".equals(orgsAll[i])){
					finSql += " (b.grpcontno='"+grpcontnos[i]+"') or";
				}else{
					String[] orgArray = orgsAll[i].split(",");
					String orgsLine = "";
					for (int j = 0; j < orgArray.length; j++) {
						orgsLine += "'"+orgArray[j]+"',";
					}
					orgsLine = orgsLine.substring(0,orgsLine.length()-1);
					finSql += " (b.grpcontno='"+grpcontnos[i]+"' and b.organcomcode in ("+orgsLine+")) or";
				}
			}
			finSql = finSql.substring(0,finSql.length()-2) + ")";
			filter.append(finSql);
		}
		
//		filter.append(" and b.relationtomaininsured<>'00'");
		
		// minContNo
		if("".equals(minContNo) || minContNo==null){
			// minContNo
    		StringBuffer minContSql = new StringBuffer();
    		minContSql.append(" select min(contno) from (");
    		
    		//edit by wuk,�����˴�lbinsured�������С������,�����©��������
    		minContSql.append(" select min(a.contno) contno                  ");
    		minContSql.append("   from (select b.contno                      ");
    		minContSql.append("           from lcinsured b                ");
    		minContSql.append("          where b.grpcontno > '0' ");
    		minContSql.append( filter );
    		minContSql.append("    ) a                ");
    		minContSql.append(" union               ");
    		minContSql.append(" select min(a.contno) contno                  ");
    		minContSql.append("   from (select b.contno                      ");
    		minContSql.append("           from lbinsured b                ");
    		minContSql.append("          where b.grpcontno > '0' ");
    		minContSql.append( filter );
    		minContSql.append("    ) a                ");
    		minContSql.append("    ) ");
//    		System.out.println(minContSql);
    		SSRS minContSSRS = exeSQL.execSQL(minContSql.toString());
    		if ( minContSSRS.getMaxRow() <= 0) {
    			return false;
    		}
    		
    		// minContNo < contNo <= maxContNo
    		char[] c = minContSSRS.GetText(1, 1).toCharArray();
    		if(c.length>0){
    			c[c.length-1] = (char)( c[c.length-1] - 1 );
        		minContNo = new String(c);  // minContNo ǰһ��
        		maxContNo = minContSSRS.GetText(1, 1);
    		}else{
    			minContNo = "0";
    			maxContNo = "1";
    		}
    		
		}
		
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select max(a.contno), count(1)             ");    
		
		//edit by wuk,�����˴�lbinsured�������������,�����©��������
		sql.append("   from (select * from (select b.contno                      ");
		sql.append("           from lcinsured b                 ");
		sql.append("          where b.contno > '"+minContNo+"'    ");
		sql.append(filter);
		sql.append(" union ");
		sql.append(" select b.contno                      ");
		sql.append("           from lbinsured b                 ");
		sql.append("          where b.contno > '"+minContNo+"'    ");
		sql.append(filter);
		sql.append(" )         order by contno) a                ");
		sql.append("  where rownum <= "+CONT_BATCH_SIZE+"      ");
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if( ssrs.getMaxRow() > 0 ){
			maxContNo = ssrs.GetText(1, 1);
			contBatchCount = Integer.parseInt(ssrs.GetText(1, 2));
			return true;
		} else {
			maxContNo = "";
			contBatchCount = 0;
			return false;
		}
	}
	
	/**
	 * ��������-����  ��Ӧ��ϵ
	 * @return Hashtable - key : GetDutyCode  value : DutyCode
	 */
	public Hashtable getLmDutyGetRelaHt() {
		Hashtable ht = new Hashtable();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.dutycode, b.getdutycode ");
		sql.append("from lmriskduty a, lmdutygetrela b ");
		sql.append("where a.dutycode = b.dutycode ");
//		sql.append("and a.riskcode in(select code from ldcode where codetype='filedownriskcode') ");
//		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if ( ssrs.getMaxRow() <= 0) {
			return ht;
		}
		for( int r = 1; r < ssrs.getMaxRow() + 1 ; r++ ) {
			String dutyCode = ssrs.GetText( r, 1 );
			String getDutyCode = ssrs.GetText( r, 2 );
			if( dutyCode != null && getDutyCode != null ){
				ht.put(getDutyCode, dutyCode);
			}
		}
		return ht;
	}
	
	public HashMap getLmDutyGetRelaMap(){
		HashMap map = new HashMap();
		String sql = "select a.dutycode, b.getdutycode from lmriskduty a, lmdutygetrela b where a.dutycode = b.dutycode";
		SSRS ssrs = exeSQL.execSQL(sql);
		if ( ssrs.getMaxRow() <= 0) {
			return map;
		}
		for( int r = 1; r < ssrs.getMaxRow() + 1 ; r++ ) {
			String dutyCode = ssrs.GetText( r, 1 );
			String getDutyCode = ssrs.GetText( r, 2 );
			if( dutyCode == null || getDutyCode == null ){
				continue;
			}
			Object obj = map.get(dutyCode);
			if( obj == null ){
				map.put(dutyCode, getDutyCode);
			} else {
				String getDutyCodes = (String)obj;
				map.put(dutyCode, getDutyCodes+"_"+getDutyCode);
			}
		}
		return map;
	}

}
