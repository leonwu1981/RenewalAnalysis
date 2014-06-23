package com.sinosoft.lis.claimanalysis.claimratio;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class PolicyDataDownloadThread implements Runnable {

	int threadNum; // �̱߳��
	String grpContNo;
	int startRow;
	int endRow;
	String file_path;
	
	String startDate;
	String endDate;
	
	static int SuccThreadCount = 0;  //�ɹ��߳���
	static int FailThreadCount = 0;  //ʧ���߳���
	static int ThreadCount = 0;  //����ִ���߳���
	FileProcessor FileProcessor = new FileProcessor();
	
	PolicyDataDownloadThread() {
	} 
	PolicyDataDownloadThread( int threadNum, String startDate, String endDate, String grpContNo, int startRow, int endRow, String path ) {
		this.threadNum = threadNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.grpContNo = grpContNo;
		this.startRow = startRow;
		this.endRow = endRow;
		this.file_path = path;
	} 
	public void run() {
		addThreadCount();
		
		// output claim_threadNum.csv
		if( !outputPolData()){
			addFailThreadCount();
		} else {
			addSuccThreadCount();
		}
	}
	
	public boolean outputPolData(){
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from ( ");
		sql.append(" select c.*,rownum rowno from( ");
		// ��c
		// 1 MANAGECOM �ֹ�˾
		sql.append(" select a.ManageCom, ");
		// 2 GRPCONTNO ������
		sql.append("        a.GrpContNo, ");
//		// polstate
//		sql.append("        a.polstate, ");
		// 3 REPEATBILL �Ƿ�Ϊ����
		sql.append("        b.RepeatBill, ");
		// 4 APPNTNAME Ͷ����˾����
		sql.append("        a.AppntName, ");
		// 5 GEBCLIENT �Ƿ�Ϊgeb�ͻ�
		sql.append("        b.GEBClient, ");
		// 6 SALECHNL ��������
		sql.append("        (select codename from ldcode where codetype='agenttype' and code=b.SaleChnl), ");
		// 7 AGENTCODE �����˱���
		sql.append("        b.agentcode, ");
//		// agentname
//		sql.append("        (select name from laagent where agentcode = b.agentcode), ");
		// 8 INSUREDNO �������˺���
		sql.append("        a.insuredno, ");
//		// tpaflag
//		sql.append("        a.tpaflag, ");
		// 9 CVALIDATE �������˱�����ʼ��
		sql.append("        a.CValiDate, ");
		// 10 PAYENDATE �������˱�����ֹ��
		sql.append("        a.PaytoDate - 1 PaytoDate, ");
		// 11 CCDATE ������Ч��
		sql.append("        b.CValiDate ccdate, ");
		// 12 CRDATE ������ֹ��
		sql.append("        (select max(payenddate) - 1 from lcgrppol where grpcontno = a.grpcontno) crdate, ");
		// 13 RISKCODE ����
		sql.append("        a.riskcode, ");
		// 14 COMMRATE Ӷ��
		sql.append("        (select commissionrate from lcgrppol where grpcontno = a.grpcontno and riskcode = a.riskcode) commissionrate, ");
		// 15 AMNT
		sql.append("        a.amnt, ");
		// 16 SUMPREM
		sql.append("        a.sumprem, ");
		// 17 DEPT
		sql.append("        b.departmentid ");
		//����18�ƶ����ʺ�19�����ײ������ֶ�(ASR20123968 S-[�����޸�]-�����ʷ�������) 20120917
		sql.append(",(select tfeerate from lcgrppol where grpcontno = a.grpcontno and riskcode = a.riskcode) tfeerate,");
		sql.append("nvl(b.polpackageflag,'0') ");
		// where 
		sql.append("   from lcpol a, lcgrpcont b ");
		sql.append("  where a.grpcontno = b.grpcontno ");
		sql.append("    and a.prtno = b.prtno ");
		sql.append("    and b.appflag = '1' ");
		sql.append("    and b.grpcontno = '"+grpContNo+"' ");
		sql.append(" union all ");
		sql.append(" select a.ManageCom, ");
		sql.append("        a.GrpContNo, ");
//		sql.append("        a.polstate, ");
		sql.append("        b.RepeatBill, ");
		sql.append("        a.AppntName, ");
		sql.append("        b.GEBClient, ");
		sql.append("        (select codename from ldcode where codetype='agenttype' and code=b.SaleChnl), ");
		sql.append("        b.agentcode, ");
//		sql.append("        (select name from laagent where agentcode = b.agentcode), ");
		sql.append("        a.insuredno, ");
//		sql.append("        a.tpaflag, ");
		sql.append("        a.CValiDate, ");
		sql.append("        (select EdorValiDate from LPEdoritem where grpcontno = a.grpcontno and contno = a.contno and edortype = 'ZT' and oldedortype is null and rownum=1), ");
		sql.append("        b.CValiDate, ");
		sql.append("        (select max(payenddate) - 1 from lcgrppol where grpcontno = a.grpcontno), ");
		sql.append("        a.riskcode, ");
		sql.append("        (select commissionrate from lcgrppol where grpcontno = a.grpcontno and riskcode = a.riskcode), ");
		sql.append("        a.amnt, ");
		sql.append("        a.sumprem + (select nvl(sum(getmoney), 0) from ljagetendorse where grpcontno = a.grpcontno and contno = a.contno and riskcode = a.riskcode and polno = a.polno and FeeOperationtype in ('ZT', 'HT') and operator not like 'GRP%'), ");
		sql.append("        b.departmentid ");
		//����18�ƶ����ʺ�19�����ײ������ֶ�(ASR20123968 S-[�����޸�]-�����ʷ�������) 20120917
		sql.append(",(select tfeerate from lcgrppol where grpcontno = a.grpcontno and riskcode = a.riskcode) tfeerate,");
		sql.append("nvl(b.polpackageflag,'0') ");
		sql.append("   from lbpol a, lcgrpcont b ");
		sql.append("  where a.grpcontno = b.grpcontno ");
		sql.append("    and a.prtno = b.prtno ");
		sql.append("    and b.appflag = '1' ");
		sql.append("    and b.grpcontno = '"+grpContNo+"' ");
		//
		sql.append(" ) c ) "); //c
		sql.append(" where rowno >= ").append(startRow);
		sql.append(" and rowno <= ").append(endRow);
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		StringBuffer str = new StringBuffer();
		
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			//By Fang for ���Ϊ�������ٵı���Ӧ����������ɾ�� 20111207
			String grpName = ssrs.GetText(i, 4);
			if(grpName != null && grpName.startsWith("�������ٱ������޹�˾")){
				continue;
			}
			//End
			str.append( ssrs.GetText(i, 1) );
			for(int j = 2; j<ssrs.getMaxCol(); j++){
				String s = ssrs.GetText(i, j);
				if( j==4 && s!=null ){ // 4 APPNTNAME Ͷ����˾����
					//Deleted by Fang 20111207
//					if(s.startsWith("�������ٱ������޹�˾")){
//						continue;
//					}//APPNTNAMEΪǰ10����Ϊ�������ٱ������޹�˾����������ɾȥ�����а����������ٱ������޹�˾�����ֹ�˾�ȸ���֧��˾������
					s = s.replaceAll(",", "��");
				}
				//By Fang for 20120112(GEB�ͻ���־1:��GEB�ͻ�; 2:GEB�ͻ�-Pooling; 3:GEB�ͻ�-Captive; 4:GEB�ͻ�-�ٱ�)
				if (j==5 && s!=null && !s.equals(""))
				{
					if (s.equals("1"))
						str.append(",").append("2");
					else 
						str.append(",").append("1");  //ԭ1ΪGEB
				}else {
					str.append(",").append( s );
				}
				//End 20120112
			}
			str.append("\r\n");
		}
		FileProcessor.outputFile(str.toString(), file_path+"data/", "policy_"+threadNum, "csv");
		return true;
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
		ThreadCount++;
	}

}
