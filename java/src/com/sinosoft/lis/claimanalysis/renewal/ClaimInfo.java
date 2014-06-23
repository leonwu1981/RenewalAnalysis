package com.sinosoft.lis.claimanalysis.renewal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.FormatConverter;

//������Ϣ
public class ClaimInfo {
	private String branchCode;// �ֹ�˾����
	private String grpContNo;// ���屣����
	private Date polValiDate;// ������Ч��
	private Date polEndDate;// ������ֹ��
	private String customerNo;// �������˿ͻ���
	private Date birthday;// ��������
	private String sex;// �Ա�
	private Date cValiDate;// ����������Ч��
	private Date cEndDate;// ����������ֹ��
	private String retireFlag;// ��ְ������
	private String relationToMainInsured;// �뱻�����˵Ĺ�ϵ
	private String contPlanCode;// �ƻ�
	private String contPlanName;// �ƻ�����
	private double payLine;// ����
	private double drugDailyLimit;// ��ҩ���޶�
	private double feeDailyLimit;// �շ����޶�
	private double bedDailyLimit;// �մ�λ���޶�
	private double checkDailyLimit;// �ռ����޶�

	private String payRatio;// �⸶����
	private String caseNo;// ���������ⰸ��
	private Date accidentDate;// ���סԺ��������
	private Date leaveHospDate;// סԺ��������
	private int hospStayLength;// סԺ����
	private String hospCode;// ��ҽҽԺ����
	private String hospName;// ��ҽҽԺ����
	private String hospClass;// ҽԺ�ȼ�
	private String diseaseCode;// ����ԭ��icd10���֣�
	private Date applyDate;// �����⸶����
	private String expenseCode;// ������Ŀ����
	private String expenseName;// ������Ŀ����
	private double expenseAmnt;// ���ý��
	private double ownExpenseAmnt;// �Էѽ��
	private double partExpenseAmnt;// �����Ը����
	private double ssExpenseAmnt;// ҽ��֧�����
	private String getDutyCode;// ������Ŀ����
	private String getDutyName;// ������Ŀ����
	private double realPay;// ʵ���⸶���
	private Date payDate;// �⸶����
	private String riskCode;// ���ִ���
	// ��������
	// ������
	private double clinicMaxPayLimit;// �������޶�
	private double amnt;//����
	private String tpaflag;//�Ƿ�TPA���
	private String innetwork;//�Ƿ�TPA�⸶
	
	private int opip;// ����������
	private int visitTimes; // �������
	
	
	private String contNo;// ������
	
	private String businesstypein;
	private String customername;
	private String gebclient;
	private String departmentid;
	private String occupationcode;
	private String giveTypeDesc;
	private double claimmoney;// ������
	private double payable;// payable
	private double ssScope;// ҽ����Χ��
	
	public void setPayable(double payable){
		this.payable = payable;
	}
	
	public double getPayable(){
		return payable;
	}
	
	public void setSsScope(double ssScope){
		this.ssScope = ssScope;
	}
	
	public double getSsScope(){
		return ssScope;
	}
	
	public double getClaimmoney() {
		return claimmoney;
	}
	public void setClaimmoney(double claimmoney) {
		this.claimmoney = claimmoney;
	}
	
	public String getGiveTypeDesc() {
		return giveTypeDesc;
	}
	public void setGiveTypeDesc(String giveTypeDesc) {
		this.giveTypeDesc = giveTypeDesc;
	}
	public String getOccupationcode() {
		return occupationcode;
	}
	public void setOccupationcode(String occupationcode) {
		this.occupationcode = occupationcode;
	}
	public String getGebclient() {
		return gebclient;
	}
	public void setGebclient(String gebclient) {
		this.gebclient = gebclient;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	
	public String getCustomername() {
		return customername;
	}
	public void setCustomername(String customername) {
		this.customername = customername;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public String getGrpContNo() {
		return grpContNo;
	}
	public void setGrpContNo(String grpContNo) {
		this.grpContNo = grpContNo;
	}
	public Date getPolValiDate() {
		return polValiDate;
	}
	public void setPolValiDate(Date polValiDate) {
		this.polValiDate = polValiDate;
	}
	public Date getPolEndDate() {
		return polEndDate;
	}
	public void setPolEndDate(Date polEndDate) {
		this.polEndDate = polEndDate;
	}
	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getCValiDate() {
		return cValiDate;
	}
	public void setCValiDate(Date valiDate) {
		cValiDate = valiDate;
	}
	public Date getCEndDate() {
		return cEndDate;
	}
	public void setCEndDate(Date endDate) {
		cEndDate = endDate;
	}
	public String getRetireFlag() {
		return retireFlag;
	}
	public void setRetireFlag(String retireFlag) {
		this.retireFlag = retireFlag;
	}
	public String getRelationToMainInsured() {
		return relationToMainInsured;
	}
	public void setRelationToMainInsured(String relationToMainInsured) {
		this.relationToMainInsured = relationToMainInsured;
	}
	public String getContPlanCode() {
		return contPlanCode;
	}
	public void setContPlanCode(String contPlanCode) {
		this.contPlanCode = contPlanCode;
	}
	public String getContPlanName() {
		return contPlanName;
	}
	public void setContPlanName(String contPlanName) {
		this.contPlanName = contPlanName;
	}
	public double getPayLine() {
		return payLine;
	}
	public void setPayLine(double payLine) {
		this.payLine = payLine;
	}
	public double getDrugDailyLimit() {
		return drugDailyLimit;
	}
	public void setDrugDailyLimit(double drugDailyLimit) {
		this.drugDailyLimit = drugDailyLimit;
	}
	public double getFeeDailyLimit() {
		return feeDailyLimit;
	}
	public void setFeeDailyLimit(double feeDailyLimit) {
		this.feeDailyLimit = feeDailyLimit;
	}
	public double getBedDailyLimit() {
		return bedDailyLimit;
	}
	public void setBedDailyLimit(double bedDailyLimit) {
		this.bedDailyLimit = bedDailyLimit;
	}
	public double getCheckDailyLimit() {
		return checkDailyLimit;
	}
	public void setCheckDailyLimit(double checkDailyLimit) {
		this.checkDailyLimit = checkDailyLimit;
	}
	public double getClinicMaxPayLimit() {
		return clinicMaxPayLimit;
	}
	public void setClinicMaxPayLimit(double clinicMaxPayLimit) {
		this.clinicMaxPayLimit = clinicMaxPayLimit;
	}
	public double getAmnt() {
		return amnt;
	}
	public void setAmnt(double amnt) {
		this.amnt = amnt;
	}
	public String getTpaflag() {
		return tpaflag;
	}
	public void setTpaflag(String tpaflag) {
		this.tpaflag = tpaflag;
	}
	public String getInnetwork() {
		return innetwork;
	}
	public void setInnetwork(String innetwork) {
		this.innetwork = innetwork;
	}
	public String getPayRatio() {
		return payRatio;
	}
	public void setPayRatio(String payRatio) {
		this.payRatio = payRatio;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public Date getAccidentDate() {
		return accidentDate;
	}
	public void setAccidentDate(Date accidentDate) {
		this.accidentDate = accidentDate;
	}
	public Date getLeaveHospDate() {
		return leaveHospDate;
	}
	public void setLeaveHospDate(Date leaveHospDate) {
		this.leaveHospDate = leaveHospDate;
	}
	public int getHospStayLength() {
		return hospStayLength;
	}
	public void setHospStayLength(int hospStayLength) {
		this.hospStayLength = hospStayLength;
	}
	public String getHospCode() {
		return hospCode;
	}
	public void setHospCode(String hospCode) {
		this.hospCode = hospCode;
	}
	public String getHospName() {
		return hospName;
	}
	public void sethospName(String hospName) {
		this.hospName = hospName;
	}
	public String getHospClass() {
		return hospClass;
	}
	public void setHospClass(String hospClass) {
		this.hospClass = hospClass;
	}
	public String getDiseaseCode() {
		return diseaseCode;
	}
	public void setDiseaseCode(String diseaseCode) {
		this.diseaseCode = diseaseCode;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getExpenseCode() {
		return expenseCode;
	}
	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}
	public String getExpenseName() {
		return expenseName;
	}
	public void setExpenseName(String expenseName) {
		this.expenseName = expenseName;
	}
	public double getExpenseAmnt() {
		return expenseAmnt;
	}
	public void setExpenseAmnt(double expenseAmnt) {
		this.expenseAmnt = expenseAmnt;
	}
	public double getOwnExpenseAmnt() {
		return ownExpenseAmnt;
	}
	public void setOwnExpenseAmnt(double ownExpenseAmnt) {
		this.ownExpenseAmnt = ownExpenseAmnt;
	}
	public double getPartExpenseAmnt() {
		return partExpenseAmnt;
	}
	public void setPartExpenseAmnt(double partExpenseAmnt) {
		this.partExpenseAmnt = partExpenseAmnt;
	}
	public double getSsExpenseAmnt() {
		return ssExpenseAmnt;
	}
	public void setSsExpenseAmnt(double ssExpenseAmnt) {
		this.ssExpenseAmnt = ssExpenseAmnt;
	}
	public String getGetDutyCode() {
		return getDutyCode;
	}
	public void setGetDutyCode(String getDutyCode) {
		this.getDutyCode = getDutyCode;
	}
	public String getGetDutyName() {
		return getDutyName;
	}
	public void setGetDutyName(String getDutyName) {
		this.getDutyName = getDutyName;
	}
	public double getRealPay() {
		return realPay;
	}
	public void setRealPay(double realPay) {
		this.realPay = realPay;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getRiskCode() {
		return riskCode;
	}
	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}
	public void setOpip(int opip) {
		this.opip = opip;
	}
	public int getOpip() {
		return opip;
	}
	public void setContNo(String contNo) {
		this.contNo = contNo;
	}
	public String getContNo() {
		return contNo;
	}
	public void setVisitTimes(int visitTimes) {
		this.visitTimes = visitTimes;
	}
	public int getVisitTimes() {
		return visitTimes;
	}
	
	public String getBusinesstypein() {
		return businesstypein;
	}
	public void setBusinesstypein(String businesstypein) {
		this.businesstypein = businesstypein;
	}
	public static boolean outputClaimInfoCsv( List list, String file_path, String file_name, char mPeriodFlag ){
    	
    	StringBuffer strBuff = new StringBuffer();
//	    SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
//	    java.text.DecimalFormat decimalFm = new java.text.DecimalFormat( "#.##");
    	for(Iterator i = list.iterator(); i.hasNext(); ) {
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			//1�ֹ�˾����
			strBuff.append( claimInfo.getBranchCode() ).append(",");
			//2���屣����
			strBuff.append( claimInfo.getGrpContNo() ).append(",");
			//3������Ч��
			String cValidate = FormatConverter.dateFm(claimInfo.getPolValiDate());
			strBuff.append( cValidate ).append(",");
			//4������ֹ��
			strBuff.append( FormatConverter.dateFm(claimInfo.getPolEndDate()) ).append(",");
			//5�������˿ͻ���
			strBuff.append( claimInfo.getCustomerNo() ).append(",");
			//6��������
			strBuff.append( FormatConverter.dateFm(claimInfo.getBirthday()) ).append(",");
			//7�Ա�
			strBuff.append( claimInfo.getSex() ).append(",");
			//8����������Ч��
			strBuff.append( FormatConverter.dateFm(claimInfo.getCValiDate()) ).append(",");
			//9����������ֹ��
			strBuff.append( FormatConverter.dateFm(claimInfo.getCEndDate()) ).append(",");
			//10��ְ������
			strBuff.append( claimInfo.getRetireFlag() ).append(",");
			//11�뱻�����˵Ĺ�ϵ
			strBuff.append( claimInfo.getRelationToMainInsured() ).append(",");
			//12�ƻ� : �������
			strBuff.append( claimInfo.getContPlanCode() ).append(",");
			//13�ƻ�����
			strBuff.append( claimInfo.getContPlanName() ).append(",");
			//14���ߡ�15��ҩ���޶16�շ����޶17�մ�λ���޶18�ռ����޶19�⸶���� 
			//20���������ⰸ��
			strBuff.append( claimInfo.getCaseNo() ).append(",");
			//21���סԺ��������(��ϵͳ��ֲ�����İ���,���������޸�Ϊ ��������)
			strBuff.append( FormatConverter.dateFm(claimInfo.getAccidentDate()) ).append(",");
			//22סԺ��������
			strBuff.append( FormatConverter.dateFm(claimInfo.getLeaveHospDate()) ).append(",");
			//23סԺ����
			strBuff.append( claimInfo.getHospStayLength() ).append(",");
			//24��ҽҽԺ����
			strBuff.append( claimInfo.getHospCode() ).append(",");
			//24.5 ����ӣ���ҽҽԺ����
			strBuff.append( claimInfo.getHospName() ).append(",");
			//25ҽԺ�ȼ�
			strBuff.append( claimInfo.getHospClass() ).append(",");
			
			//26����ԭ��icd10���֣�
			strBuff.append( claimInfo.getDiseaseCode() ).append(",");
			//27�����⸶����
			strBuff.append( FormatConverter.dateFm(claimInfo.getApplyDate()) ).append(",");
			//28������Ŀ���롢29������Ŀ���ơ�30���ý�31�Էѽ�32�����Ը���33ҽ��֧�����
			strBuff.append( claimInfo.getExpenseCode() ).append(",");
			strBuff.append( claimInfo.getExpenseName() ).append(",");
			strBuff.append( claimInfo.getExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getOwnExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getPartExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getSsExpenseAmnt() ).append(",");
			//34������Ŀ����
			strBuff.append( claimInfo.getGetDutyCode() ).append(",");
			//35������Ŀ����
			strBuff.append( claimInfo.getGetDutyName() ).append(",");
			//36ʵ���⸶���(ͬһ���εڶ�������0)
			strBuff.append( claimInfo.getRealPay() ).append(",");
			//37�⸶����
			strBuff.append( FormatConverter.dateFm(claimInfo.getPayDate()) ).append(",");
			//38���ִ���
			strBuff.append( claimInfo.getRiskCode() );
			//39��������
			//40������
			
			// ����-���� ����
			String[] s = ClaimInfo.getPeriodInfo(claimInfo.getAccidentDate(), claimInfo.getPayDate(), mPeriodFlag);
			if( s == null ) {
				strBuff.append(",,,0");
			} else {
				strBuff.append(",");
				//acc date
				strBuff.append( s[0] );
				strBuff.append(",");
				//pay date
				strBuff.append( s[1] );
				strBuff.append(",");
				//length
				strBuff.append( s[2] );
			}
			
			// ���
			strBuff.append(",").append( cValidate.substring(0, 4) );
			// ������
			strBuff.append(",").append( getRiskName( claimInfo.getRiskCode() ) ).append(",");
			
			//39�������޶�
			strBuff.append( claimInfo.getClinicMaxPayLimit() ).append(",");
			//40�������޶�
			strBuff.append( claimInfo.getCheckDailyLimit() ).append(",");
			//41����
			strBuff.append( claimInfo.getAmnt() ).append(",");
			//42�⸶����
			strBuff.append( claimInfo.getPayRatio() ).append(",");
			//43�Ƿ�TPA���
			strBuff.append( claimInfo.getTpaflag() ).append(",");
			//44�Ƿ�TPA�⸶
			strBuff.append( claimInfo.getInnetwork() ).append(",");
			
			strBuff.append( claimInfo.getBusinesstypein() ).append(",");
			strBuff.append( claimInfo.getCustomername().replaceAll(",", "��") ).append(",");
			strBuff.append( claimInfo.getGebclient() ).append(",");
			strBuff.append( claimInfo.getDepartmentid() ).append(",");;
			strBuff.append( claimInfo.getOccupationcode() ).append(",");
			strBuff.append( claimInfo.getGiveTypeDesc() ).append(",");
			strBuff.append( claimInfo.getClaimmoney() ).append(",");
			strBuff.append( claimInfo.getPayable() ).append(",");
			strBuff.append( claimInfo.getSsScope() );
			strBuff.append("\r\n");// ����
    	}
    	FileProcessor FileProcessor = new FileProcessor();
    	if( !FileProcessor.outputFile(strBuff.toString(), file_path, file_name, "csv") ){
	    	return false;
	    }
    	return true;
    }
	
	public static String[] getPeriodInfo( Date date1, Date date2, char mPeriodFlag ){
		if(date1==null){
			return null;
		}
		String[] r = new String[3];
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy/MM");
		if (date2==null) {
			String s1 = dateFm.format(date1);
			String[] dateTime1 = s1.split("/");
			int m1 = Integer.parseInt( dateTime1[1] );

			if( mPeriodFlag == 'M' ){
				r[0] = s1;
				r[1] = "";
				r[2] = "0";
			} else if( mPeriodFlag == 'Y' ){
				r[0] = dateTime1[0];
				r[1] = "";
				r[2] = "0";
			} else if( mPeriodFlag == 'Q' ){
				int q1 = (m1-1) / 3 + 1;
				r[0] = dateTime1[0]+"Q"+q1;
				r[1] = "";
				r[2] = "0";
			} else {
				return null;
			}
		}else {
			String s1 = dateFm.format(date1);
			String s2 = dateFm.format(date2);
			String[] dateTime1 = s1.split("/");
			String[] dateTime2 = s2.split("/");
			int y1 = Integer.parseInt( dateTime1[0] );
			int y2 = Integer.parseInt( dateTime2[0] );
			int m1 = Integer.parseInt( dateTime1[1] );
			int m2 = Integer.parseInt( dateTime2[1] );

			if( mPeriodFlag == 'M' ){
				r[0] = s1;
				r[1] = s2;
				int l = (y2-y1)*12 + m2 - m1;
				if(l<0){
					l=0;
				}
				r[2] = Integer.toString(l);
			} else if( mPeriodFlag == 'Y' ){
				r[0] = dateTime1[0];
				r[1] = dateTime2[0];
				int l = y2-y1;
				if(l<0){
					l=0;
				}
				r[2] = Integer.toString(l);
			} else if( mPeriodFlag == 'Q' ){
				int q1 = (m1-1) / 3 + 1;
				int q2 = (m2-1) / 3 + 1;
				r[0] = dateTime1[0]+"Q"+q1;
				r[1] = dateTime2[0]+"Q"+q2;
				int l = (y2-y1)*4 + q2 - q1;
				r[2] = Integer.toString(l);
			} else {
				return null;
			}
		}
		return r;
	}
	
	public static String getRiskName( String riskCode ){
		if( "MGK01".equals(riskCode) ) return "DD";
		else if( "MGK02".equals(riskCode) ) return "JDD";
		else if( "MGK03".equals(riskCode) ) return "JDD";
		else if( "MGK04".equals(riskCode) ) return "FDD";
		else if( "MIK01".equals(riskCode) ) return "AMR";
		else if( "MKK01".equals(riskCode) ) return "HI";
		else if( "MKK02".equals(riskCode) ) return "TPDI";
		else if( "MMK01".equals(riskCode) ) return "DI";
		else if( "MOK01".equals(riskCode) ) return "STAMR";
		else if( "MOK02".equals(riskCode) ) return "OEA";
		else if( "NAK01".equals(riskCode) ) return "TL";
		else if( "NAK02".equals(riskCode) ) return "TL";
		else if( "NHK01".equals(riskCode) ) return "AKM";
		else if( "NIK01".equals(riskCode) ) return "CMM";
		else if( "NIK02".equals(riskCode) ) return "SMM";
		else if( "NIK03".equals(riskCode) ) return "WMP";
		else if( "NIK04".equals(riskCode) ) return "FMM";
		else if( "NIK05".equals(riskCode) ) return "OEA-B";
		else if( "NIK06".equals(riskCode) ) return "OEA-C";
		else if( "NIK07".equals(riskCode) ) return "WMP-Exp";
		else if( "NIK08".equals(riskCode) ) return "WMP-HL1";
		else if( "NIK09".equals(riskCode) ) return "WMP-HL2";
		else if( "NIK10".equals(riskCode) ) return "WMP-Exp";
		else if( "NIK11".equals(riskCode) ) return "WMP-HL1";
		else if( "NIK12".equals(riskCode) ) return "SMM-BJ";
		else if( "NL05".equals(riskCode) ) return "STPCA";
		else if( "NLK01".equals(riskCode) ) return "STADD";
		else if( "NLK02".equals(riskCode) ) return "PCA";
		else if( "NLK03".equals(riskCode) ) return "OTADD";
		else if( "NMK01".equals(riskCode) ) return "ADD";
		else if( "NMK02".equals(riskCode) ) return "PAC-A";
		else if( "NMK03".equals(riskCode) ) return "PCA";
		else if( "NMK04".equals(riskCode) ) return "SWC";
		else if( "NMK05".equals(riskCode) ) return "ADD_loan";
		else if( "NMK06".equals(riskCode) ) return "NWRADD";
		else if( "NOK01".equals(riskCode) ) return "OEA";
		else if( "NOK02".equals(riskCode) ) return "OEA-A";
		return riskCode;
	}
	
	public static String getDutyName( String dutyCode ){
		if( "613001".equals(dutyCode) ) return "�ش󼲲�(10��)����";
		else if( "613002".equals(dutyCode) ) return "�ش󼲲�(20��)����";
		else if( "613003".equals(dutyCode) ) return "�ش󼲲�(30��)����";
		else if( "616001".equals(dutyCode) ) return "�ش󼲲����ս�";
		else if( "623001".equals(dutyCode) ) return "�ش󼲲����ս�";
		else if( "634001".equals(dutyCode) ) return "Ů���ض��������ս�2����";
		else if( "634001".equals(dutyCode) ) return "Ů���ض��������ս�1����";
		else if( "634001".equals(dutyCode) ) return "Ů�԰�֢���ս����";
		else if( "634001".equals(dutyCode) ) return "Ů��ԭλ�����ս����";
		else if( "610001".equals(dutyCode) ) return "����ҽҩ�������ս�";
		else if( "609001".equals(dutyCode) ) return "ҽԺ�������θ���";
		else if( "609002".equals(dutyCode) ) return "�����˺���֢�໤��������";
		else if( "608001".equals(dutyCode) ) return "ȫ�����θ���";
		else if( "607001".equals(dutyCode) ) return "�ض������¹���������";
		else if( "605001".equals(dutyCode) ) return "����������θ���";
		else if( "605002".equals(dutyCode) ) return "����������θ���";
		else if( "605002".equals(dutyCode) ) return "����������θ���";
		else if( "605003".equals(dutyCode) ) return "ȫ�����θ���";
		else if( "605004".equals(dutyCode) ) return "����������θ���";
		else if( "605004".equals(dutyCode) ) return "ȫ�����θ���";
		else if( "605004".equals(dutyCode) ) return "����������θ���";
		else if( "640001".equals(dutyCode) ) return "������θ���";
		else if( "640001".equals(dutyCode) ) return "ȫ�����θ���";
		else if( "615001".equals(dutyCode) ) return "סԺ�������ض���Ŀ���α��ս�";
		else if( "615002".equals(dutyCode) ) return "�������α��ս�";
		else if( "615003".equals(dutyCode) ) return "�����Ը���Ŀ���α��ս�";
		else if( "615004".equals(dutyCode) ) return "��ȫ�Է���Ŀ���α��ս�";
		else if( "615005".equals(dutyCode) ) return "��ͨ�������α��ս�";
		else if( "615006".equals(dutyCode) ) return "�ر�Լ���������α��ս�";
		else if( "615007".equals(dutyCode) ) return "���ƻ������α��ս�";
		else if( "615008".equals(dutyCode) ) return "�����������α��ս�";
		else if( "615009".equals(dutyCode) ) return "�����������α��ս�";
		else if( "615010".equals(dutyCode) ) return "����Լ��ҽ�Ʒ������α��ս�";
		else if( "617001".equals(dutyCode) ) return "סԺ�������ض���Ŀ���α��ս�";
		else if( "617002".equals(dutyCode) ) return "�������α��ս�";
		else if( "617003".equals(dutyCode) ) return "�����Ը���Ŀ���α��ս�";
		else if( "617004".equals(dutyCode) ) return "��ȫ�Է���Ŀ���α��ս�";
		else if( "617005".equals(dutyCode) ) return "��ͨ�������α��ս�";
		else if( "617006".equals(dutyCode) ) return "�ر�Լ���������α��ս�";
		else if( "617007".equals(dutyCode) ) return "���ƻ������α��ս�";
		else if( "617008".equals(dutyCode) ) return "�����������α��ս�";
		else if( "617009".equals(dutyCode) ) return "�����������α��ս�";
		else if( "617010".equals(dutyCode) ) return "����Լ��ҽ�Ʒ������α��ս�";
		else if( "612001".equals(dutyCode) ) return "סԺҽ�Ʊ��ս����";
		else if( "612002".equals(dutyCode) ) return "����ҽ�Ʊ��ս����";
		else if( "612003".equals(dutyCode) ) return "��ҽҽ�Ʊ��ս����";
		else if( "612004".equals(dutyCode) ) return "����ҽ�Ʊ��ս����";
		else if( "612005".equals(dutyCode) ) return "����ҽ�Ʊ��ս����";
		else if( "612006".equals(dutyCode) ) return "��չҽ�Ʊ��ս����";
		else if( "637001".equals(dutyCode) ) return "סԺ����";
		else if( "637002".equals(dutyCode) ) return "�������踨����������";
		else if( "637003".equals(dutyCode) ) return "���񼲲�סԺ����";
		else if( "637004".equals(dutyCode) ) return "���̲����䲢��֢סԺ����";
		else if( "637005".equals(dutyCode) ) return "�ش�������ֲ����";
		else if( "637006".equals(dutyCode) ) return "��������";
		else if( "637007".equals(dutyCode) ) return "���񼲲���������";
		else if( "637008".equals(dutyCode) ) return "���̲����䲢��֢��������";
		else if( "637009".equals(dutyCode) ) return "��ҽ����";
		else if( "637010".equals(dutyCode) ) return "��ͥ��������";
		else if( "637011".equals(dutyCode) ) return "����ҽ������";
		else if( "637012".equals(dutyCode) ) return "������������";
		else if( "637013".equals(dutyCode) ) return "�������Ƽ�Ԥ������������";
		else if( "637014".equals(dutyCode) ) return "������������";
		else if( "637015".equals(dutyCode) ) return "�ۿƻ�������";
		else if( "637016".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "641001".equals(dutyCode) ) return "סԺ����";
		else if( "641002".equals(dutyCode) ) return "���񼲲�סԺ����";
		else if( "641003".equals(dutyCode) ) return "���̲����䲢��֢סԺ����";
		else if( "641004".equals(dutyCode) ) return "��������";
		else if( "641005".equals(dutyCode) ) return "���񼲲���������";
		else if( "641006".equals(dutyCode) ) return "���̲����䲢��֢��������";
		else if( "641007".equals(dutyCode) ) return "��ҽ��������";
		else if( "641008".equals(dutyCode) ) return "����ҽ������";
		else if( "641009".equals(dutyCode) ) return "����ҽ������";
		else if( "641010".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "642001".equals(dutyCode) ) return "סԺ����";
		else if( "642002".equals(dutyCode) ) return "���סԺ����ҽ������";
		else if( "642003".equals(dutyCode) ) return "��������";
		else if( "642004".equals(dutyCode) ) return "ר����������";
		else if( "642005".equals(dutyCode) ) return "��ҽ��������";
		else if( "642006".equals(dutyCode) ) return "�����׵ָѹ��������";
		else if( "642007".equals(dutyCode) ) return "��������������";
		else if( "642008".equals(dutyCode) ) return "����ҽ������";
		else if( "642009".equals(dutyCode) ) return "����ҽ������";
		else if( "642010".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "622001".equals(dutyCode) ) return "�ɻ��¹ʲм�����";
		else if( "622001".equals(dutyCode) ) return "�ɻ��¹���������";
		else if( "622002".equals(dutyCode) ) return "���¹ʲм�����";
		else if( "622002".equals(dutyCode) ) return "���¹���������";
		else if( "622003".equals(dutyCode) ) return "�ִ��¹ʲм�����";
		else if( "622003".equals(dutyCode) ) return "�ִ��¹���������";
		else if( "622004".equals(dutyCode) ) return "�����¹���������";
		else if( "622004".equals(dutyCode) ) return "�����¹ʲм�����";
		else if( "624001".equals(dutyCode) ) return "�����¹���ʱ��ս�";
		else if( "624001".equals(dutyCode) ) return "�����¹ʲм����ս�";
		else if( "627001".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "627001".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "627002".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "627002".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "627003".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "627003".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "627004".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "627004".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "627005".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "627005".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "636001".equals(dutyCode) ) return "�����¹���ʱ��ս�";
		else if( "636001".equals(dutyCode) ) return "�����¹ʲм����ս�";
		else if( "604001".equals(dutyCode) ) return "������ʱ��ս�";
		else if( "604002".equals(dutyCode) ) return "����м������˱��ս�";
		else if( "604003".equals(dutyCode) ) return "������ʱ��ս�";
		else if( "604003".equals(dutyCode) ) return "����м������˱��ս�";
		else if( "606001".equals(dutyCode) ) return "�ɻ��¹ʲм�����";
		else if( "606001".equals(dutyCode) ) return "�ɻ��¹���������";
		else if( "606002".equals(dutyCode) ) return "�𳵼��ִ��¹���������";
		else if( "606002".equals(dutyCode) ) return "�𳵼��ִ��¹ʲм�����";
		else if( "606003".equals(dutyCode) ) return "�����¹ʲм�����";
		else if( "606003".equals(dutyCode) ) return "�����¹���������";
		else if( "628001".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "628001".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "628002".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "628002".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "628003".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "628003".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "628004".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "628004".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "628005".equals(dutyCode) ) return "��ͨ������ʸ���";
		else if( "628005".equals(dutyCode) ) return "��ͨ���߲м�����";
		else if( "632001".equals(dutyCode) ) return "һ�����˲о�ҵ������һ���Թ���ҽ�Ʋ�����";
		else if( "632002".equals(dutyCode) ) return "һ�����˲о�ҵ������";
		else if( "632003".equals(dutyCode) ) return "һ���Թ���ҽ�Ʋ�����";
		else if( "632004".equals(dutyCode) ) return "һ�����˲о�ҵ������(�����ڽ���)";
		else if( "632005".equals(dutyCode) ) return "һ���Թ���ҽ�Ʋ�����(�����ڽ���)";
		return dutyCode;
	}
	
}
