/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */

package com.sinosoft.lis.pubfun;

import java.util.StringTokenizer;

import com.sinosoft.lis.bank.ReadTPABL;
import com.sinosoft.lis.db.LDUserDB;
import com.sinosoft.lis.schema.LCInsuredSchema;
import com.sinosoft.lis.schema.LDCodeSchema;
import com.sinosoft.lis.schema.LDUserSchema;
import com.sinosoft.utility.*;

/**
 * <p>
 * Title: Webҵ��ϵͳ
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Sinosoft
 * </p>
 *
 * @author Minim
 * @version 1.0
 */

public class CodeQueryBL {
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �洢��ѯ��� */
    private String mSQL = "";

    private StringBuffer mSBql = new StringBuffer(128);

    /** �洢ȫ�ֱ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** �洢��ѯ���� */
    private String mCodeCondition = "";

    private String mConditionField = "";

    /** ҵ������ر��� */
    private LDCodeSchema mLDCodeSchema = new LDCodeSchema();

    private ExeSQL mExeSQL = new ExeSQL();

    /** ���ص����� */
    private String mResultStr = "";

    public CodeQueryBL() {
    }
    

    /**
     * �������ݵĹ�������, ������û�к�����BLS�㣬�ʸ÷�������
     *
     * @param cInputData
     *            VData
     * @param cOperate
     *            String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate) {
        // �õ��ⲿ���������,�����ݱ��ݵ�������
        if (getInputData(cInputData)) {
            // ����ҵ����
            if (queryData()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * �������������������ȡ���ݴ�����
     *
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult() {
        return mResult;
    }

    /**
     * �����������еõ����ж��� ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     *
     * @param cInputData
     *            VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData) {
        // �����ѯ����
        try {
            // �ⲿ����codetype����
            mLDCodeSchema.setSchema((LDCodeSchema) cInputData.get(0));
            // �鿴�����session��Ϣ
            try {
                mGlobalInput.setSchema((GlobalInput) cInputData.get(1));
            } catch (Exception e) {
                mGlobalInput.ComCode = "";
                mGlobalInput.ManageCom = "";
                mGlobalInput.Operator = "";
            }
            // ��ȡ��������Ĳ�ѯ��Ϣ����ѯ�򡢲�ѯֵ��
            TransferData tTransferData = (TransferData) cInputData.get(2);

            // �õ���ѯֵ
            mCodeCondition = (String) tTransferData
                .getValueByName("codeCondition");
            // �����ѯֵ�а���#�ţ����滻#��Ϊ'�ţ���Ҫ��jsp�д���'�ֻ��쳣
            if (mCodeCondition.indexOf('#') == -1) {
                StringBuffer tSBql = new StringBuffer();
                tSBql.append("'");
                tSBql.append(mCodeCondition);
                tSBql.append("'");
                mCodeCondition = tSBql.toString();
            } else {
                mCodeCondition = mCodeCondition.replace('#', '\'');
            }
            // �õ���ѯ��
            mConditionField = (String) tTransferData
                .getValueByName("conditionField");
            // �����ѯ��Ϊ�գ����ѯ��Ͳ�ѯֵǿ����Ϊ1
            if (mConditionField.equals("")) {
                mCodeCondition = "1";
                mConditionField = "1";
            }
        } catch (Exception e) {
            System.out.println("CodeQueryBL throw Errors at getInputData !");
            mCodeCondition = "1";
            mConditionField = "1";
        }
        return true;
    }

    public void setGlobalInput(GlobalInput cGlobalInput) {
        mGlobalInput.setSchema(cGlobalInput);
    }

    /**
     * ��ѯ���������ı��� ��������׼������ʱ���������򷵻�false,���򷵻�true
     *
     * @return boolean Ŀǰ��������equalsIgnoreCase��toLowerCase�����Ǹ��� ʹ��60000�αȽϣ�����equalsIgnoreCase�������Ա�toLowerCaseҪ��
     */
    private boolean queryData() {
        mSQL = "";
        int executeType = 0;

        // ����������Ϊ�գ���Ĭ������Ϊ86
        if (mGlobalInput.ManageCom == null
                || mGlobalInput.ManageCom.trim().equals("")) {
            mGlobalInput.ManageCom = "86";
        }

        // ����ǩ��ƣ��Ա�������ж��ܹ���ʱ����
        SelectCode: try {
            // �����������˫����ѯ������
            // System.out.println("come here queryDataType is " + mLDCodeSchema.getCodeType());

            // ��ѯ�û���������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "relagrpcont")) {
                mSBql
                    .append("select distinct prtno,grpcontno,riskcode from lcgrppol where appflag='1' and riskcode='212401'");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ�ɷѹ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "payrulecode")) {
                mSBql
                    .append("select distinct PayRuleCode,PayRuleName from LCPayRuleFactory where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //��ѯ�Ӻ�ͬ�������ȫ�� Tracy Add
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub1")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub'  and code<>'0'  union select "
								+ "'1','ȫ��'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            
			// ��ѯ�Ӻ�ͬ�������ȫ������ҵ���� Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub2")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub'   union select "
								+ "'1','ȫ��'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
			
			// ��ѯ�Ӻ�ͬ��������ɷݵ��Ӻ�ͬ�ź�ȫ�� liujun Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub3")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub' and a.code not in ('0','00000000')  union select "
								+ "'1','ȫ��'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            // ��ѯ�����ʽ����ͣ������������Ҫ�� Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContFlag1")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'contflag'  union select '4' ordernum ,"
								+ "'ȫ��'  from dual union select '3' ordernum ,'���Źɷ�' "
								+ "  from dual  order by ordernum  ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
			 //��ѯ�����ʽ����ͣ���Թ�������ҵ�����Ա
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContFlag2")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'contflag' and a.code<>'2'  union select '4' ordernum ,"
								+ "'���Źɷ�'  from dual   order by ordernum  ");

				mSQL = mSBql.toString();
				break SelectCode;
			}
   //ר����������ȡ�µĸ����ʽ����Ͷ�д�Ŀؼ�
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
			"ContFlag3")) {
		mSBql
				.append("select a.code ordernum , a.codename  from ldcode a where "
						+ "a.codetype = 'contflag' and a.code<>'2'  union select '3' ordernum ,"
						+ "'���Źɷ�'  from dual   order by ordernum  ");

		mSQL = mSBql.toString();
		break SelectCode;
	}

			// ��ѯ�������� Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"OrganType1")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'OrganType'  union select '3' ordernum ,"
								+ "'ȫ��'  from dual order by ordernum  ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            // ��ѯ�������б��� Tracy Add
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "PayBankCode")) {
                mSBql
                    .append("select bankcode,bankname from ldbank where chksuccflag in ('B','C') and ");
                mSBql.append(mConditionField);
                mSBql.append(" like ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //��ѯ�տ����б��� Tracy Add
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "GetBankCode")) {
                mSBql
                    .append("select bankcode,bankname from ldbank where chksuccflag in ('A','C') and ");
                mSBql.append(mConditionField);
                mSBql.append(" like ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //��ѯ��֧���� Tracy Add
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "GetOrganCode")) {
                mSBql
                    .append("select organcomcode,a.grpname from lcorgan a ,lcgrpcont b "
                            + "where a.grpcontno=b.grpcontno and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ��������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ascriptionrulecode")) {
                mSBql
                    .append("select distinct AscriptionRuleCode,AscriptionRuleName from LCAscriptionRuleFactory where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �ŵ����ֲ�ѯ���Ѽ��payintv
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "orderpayintv")) {
                mSBql.append("select code,codename from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype='payintv' order by codealias ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "nationality")) {
                mSBql.append("select code,codename from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype='nationality' order by code ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ����״̬
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "nationality")) {
                mSBql.append("select code,codename from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype='marriage' order by code ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��������������Ʒ�ʹ��� ����ϸ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "qualitycode")) {
                mSBql
                    .append("select ItemCode,ItemContext from LAQualityItemDef where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ItemCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��ѯ������λ���������(guoly 20070520edit)
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcn")) {
                mSBql
                    .append("select OrganComCode,GrpName from LCOrgan  where 1=1 and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by organcomcode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }
            // Tracy add 2006/3/20
            // ���Ż�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcode")) {
                mSBql
                    .append("select distinct organcomcode,grpname from lcsendorgan where grouplevel='1' and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by organcomcode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }
            //          Tracy add 2006/3/20
            // ���Ż�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "NewOrgan")) {
                mSBql
                    .append("select distinct organcomcode,grpname from lcsendorgan "
                            + "where (organcomcode='0000' or grouplevel='1') and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by organcomcode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // huanglei add 2006/3/20
            // ���ű�׼
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "sendstandard")) {
                mSBql
                    .append("select contplancode,contplanname,(select b.calfactorvalue from lccontplandutyparam b where b.proposalgrpcontno=a.proposalgrpcontno and b.contplancode=a.contplancode and b.calfactor='GetLimit') as LowerLimit ,(select b.calfactorvalue from lccontplandutyparam b where b.proposalgrpcontno=a.proposalgrpcontno and b.contplancode=a.contplancode and b.calfactor='PeakLine') as UpperLimit from lccontplan a where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" order by substr(contplancode,1,1),substr(contplancode,2,3) desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //liujun add 2008070
            //���Ŵ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "sendstandard1")) {
            	if(mConditionField != null && mConditionField != "" && Integer.parseInt(mConditionField)<2006){
            		mConditionField = "2006";
            	}
                mSBql
                    .append("select contplancode,contplanname,(select b.calfactorvalue from lccontplandutyparam b where a.grpcontno = b.grpcontno and b.proposalgrpcontno=a.proposalgrpcontno and b.contplancode=a.contplancode and b.riskcode='");
                mSBql.append(mConditionField);
                mSBql
                	.append("' and b.calfactor='GetLimit') as LowerLimit ,(select b.calfactorvalue from lccontplandutyparam b where a.grpcontno = b.grpcontno and b.proposalgrpcontno=a.proposalgrpcontno and b.contplancode=a.contplancode and b.riskcode = '");
                mSBql.append(mConditionField);
                mSBql
                	.append("' and b.calfactor='PeakLine') as UpperLimit from lccontplan a where ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" order by substr(contplancode,1,1),substr(contplancode,2,3) desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ����Ա��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "cashier")) {
                mSBql
                    .append("select distinct b.Usercode,b.Username from ljtempfee a,lduser b where a.operator = b.usercode and b.InsurerUserType<>'1' and ");
                mSBql.append(mConditionField);
                mSBql.append(" like ");
                mSBql.append(mCodeCondition.substring(0, mCodeCondition
                    .length() - 1));
                mSBql.append("%'");
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }

            // ����Ա��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "usercode")) {
                mSBql.append("select Usercode,Username from LDUser where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }
            
            // ����Ա��ѯ,����usercodeģ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "userbycode")) {
                mSBql.append("select Usercode,Username from LDUser where ");
                mSBql.append(mConditionField);
                mSBql.append(" like '");
                mSBql.append(mCodeCondition);
                mSBql.append("%%'");
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }
            
            // ��ת���Ų�ѯ�����ݲ���Ա�������Ų�ѯ����ת����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "deptcode")) {
                mSBql.append("select code1,(select codename from ldcode where codetype='zllzzl' and code=ldcode1.code1) from ldcode1 where codetype='zllzzl' and code=(select deptcode from lduser where  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" )");
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }
            // ��ת���Ų�ѯ����ѯ���в���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "alldeptcode")) {
                mSBql.append("select code,codename from ldcode where codetype='zllzzl' and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }
            // ������ת����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "zllzzb")) {
                mSBql.append("select code1,codealias from  ldcode1 where codetype='zllzzb' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }

            // �˵���ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "menu")) {
                mSBql
                    .append("select d.nodecode,Case When a.nodename is null and b.nodename is null then c.nodename||'->'||d.nodename ");
                mSBql
                    .append("when  a.nodename is null   then b.nodename||'->'||c.nodename||'->'||d.nodename ");
                mSBql
                    .append("else a.nodename||'->'||b.nodename||'->'||c.nodename||'->'||d.nodename end ");
                mSBql
                    .append(",d.nodeorder  from ldmenu a,ldmenu b,ldmenu c,ldmenu d  ");
                mSBql
                    .append("where a.nodecode(+) = b.parentnodecode and b.nodecode(+) = c.parentnodecode and ");
                mSBql
                    .append("c.nodecode(+) = d.parentnodecode and d.childflag = '0'order by d.nodeorder ");
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }

            // ��λ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "menugrp")) {
                mSBql
                    .append("select menugrpcode,menugrpname from ldmenugrp order by makedate,maketime ");
                executeType = 1;
                mSQL = mSBql.toString();
                // һ�������ѯ�����󣬾�����
                break SelectCode;
            }

            // ��ѯר��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "familydoctorno")) {
                mSBql
                    .append("select DoctNo,DoctName from LDDoctor where 1=1 and CExportFlag='1' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by DoctNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ش󼲲���Ϣ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "seriousdiseas")) {
                mSQL = "select Name,Code,Typedesc, Description from LLMSerialsDiease order by Code";
                break SelectCode;
            }
            //mdy           
//          �ش󼲲���Ϣ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "contplancode")) {
                mSQL = "select a.contplancode,a.contplanname from ldplan a order by a.contplancode ";
                break SelectCode;
            }
            
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "moncom")) {
            mSQL = "select comcode,name from Ldcom";
            break SelectCode;
           }
            
            // ��֤����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ldattestleve")) {
                mSBql
                    .append("select  AttestLevelCode,AttestLevel from LDAttestLeve where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by AttestLevelCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ������������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ldhealthorganclass")) {
                mSBql
                    .append("select  HealthOrganClass,HealthOrganClassName from LDHealthOrganClass where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by HealthOrganClass");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��λ������ϵ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ldorganisubjec")) {
                mSBql
                    .append("select  SubjecCode,SubjecName from LDOrganiSubjec where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by SubjecCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ҵ�����ʹ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ldbusitype")) {
                mSBql
                    .append("select  BusiTypeCode,BusiType from LDBusiType where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by BusiTypeCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            /** *******************������ز�ѯ */
            // ����Ȩ�޲�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llusercode")) {
                mSBql
                    .append("select usercode ,username from Llclaimuser where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append("union select usercode,username from llclaimuser where usercode ");
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by usercode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �����¼��Ҫʵ��һ��������ģ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lloperation")) {
                mSBql
                    .append("select  IcdOpsName,ICDopsCode,OpsGrag from LDICDOps where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by ICDOpsName");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �����ͬ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llcont")) {
                mSBql.append("select distinct contno,'' from lcinsured where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �������ֲ�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llrisk")) {
                mSBql
                    .append("select distinct a.polno,b.riskname,a.riskcode,a.contno,c.caseno from lcpol a, lmrisk b, llcasepolicy c where ");
                // mSBql.append(mConditionField);
                // mSBql.append(" and b.riskcode=a.riskcode");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.polno = c.polno and b.riskcode=a.riskcode ");
                mSBql
                    .append(" union select distinct a.polno,b.riskname,a.riskcode,a.contno,c.caseno from lbpol a, lmrisk b, llcasepolicy c where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.polno = c.polno and b.riskcode=a.riskcode ");
                mSQL = mSBql.toString();
                System.out.println("���ֲ�ѯ:" + mSQL);
                break SelectCode;
            }
            // Ӱ����൱��Ҫʹ�õ�, busstype(ҵ������)
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "BussType")) {
                mSBql
                    .append("select distinct BussType,BussTypename from es_doc_def ");
                mSBql.append(" order by BussType");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // Ӱ����൱��Ҫʹ�õ�, subtype(��֤ϸ��)
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "subtype")) {
                mSBql
                    .append("select distinct subtype,subtypename from es_doc_def where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by subtype");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���ռ�����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lldiseas")) {
                mSBql
                    .append("select  ICDName,ICDCode from LDDisease where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by ICDCode");
                mSQL = mSBql.toString();
                executeType = 0;
                break SelectCode;
            }
            //add by winnie ASR20093070
            // ��������ض����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "QuestBackObj")) {
                mSBql
                    .append("select CodeName,Code from ldcode where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                        (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' and ");
                mSBql.append("codetype='QuestBackObj'");
                mSQL = mSBql.toString();
                executeType = 0;
                break SelectCode;
            }
            //��ӡ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "NeedPrint")) {
            	mSBql.append("select CodeName,Code from ldcode where 1=1 and ");
            	mSBql.append("codetype='NeedPrint'");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //�����״̬
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "State")) {
            	mSBql.append("select CodeName,Code from ldcode where ");
            	mSBql.append("codetype='State'");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //end add 20091104
            // �����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "optname")) {
                mSBql
                    .append("select username,usercode,claimpopedom from llclaimuser where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by claimpopedom desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �����������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llmaffix")) {
                mSBql
                    .append("select  affixcode,affixname from llmaffix where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by affixcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �����������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llmaffixtype")) {
                mSBql
                    .append("select distinct affixtypecode,affixtypename from llmaffix where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by affixtypecode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���Ᵽ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llclaimpolicy")) {
                mSBql
                    .append("select distinct a.contno,'' from llcasepolicy a where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by a.contno");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���ֲ�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llclaimrisk")) {
                mSBql.append("select riskcode,riskname from lmrisk");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //���β�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrduty")) {
                mSBql.append("select dutycode,dutyname from lmduty");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            
            //�������ֲ�ѯ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lmriskduty")) {
                mSBql.append("select dutycode,dutyname from LMDuty where DutyCode in (select DutyCode from LMRiskDuty where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(") order by DutyCode");
                mSQL = mSBql.toString();
                
                //System.out.println(mSQL + mConditionField);
                break SelectCode;
            }
            
            // ���θ������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llgetdutykind")) {
                mSBql
                    .append("select distinct a.GetDutyKind,b.codeName from LMDutyGetClm a , ldcode b where getdutycode in (select getdutycode from lmdutygetrela where dutycode in (select dutycode from lmriskduty where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" )) and b.codetype='getdutykind' and code=a.getdutykind order by getdutykind");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llclaimdecision_1")) {
                mSBql
                    .append("select a.Code, a.CodeName, a.CodeAlias, a.ComCode, a.OtherSign from ldcode a where  trim(a.codetype)=(select trim(b.codeaLias) from ldcode b where b.codetype='llclaimdecision' and b.code=");
                mSBql.append(mCodeCondition);
                mSBql.append(")");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��֪����ܲ�ѯ�����Ǻܺ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impartver")) {
                mSBql
                    .append("select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where codetype like '%impartver%'");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�˲еȼ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "disability")) {
                mSQL = " select code,codename from ldcode where codetype='disability' ";
                break SelectCode;
            }
            // ����¼��Ҫ������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llfactor")) {
                mSBql
                    .append("select a.codename, a.code from ldcode a where trim(a.codetype) =( select CODEALIAS from ldcode where codetype='llotherfactor' and code=");
                mSBql.append(mCodeCondition);
                mSBql.append(") order by a.code");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �˱�ʦ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "uwusercode")) {
                mSQL = "select UserCode, trim(UserName) from LDUser where cropcode is null  order by UserCode";
                executeType = 1;
                break SelectCode;
            }

            // �ŵ��ͻ�����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "supcustomerno")) {
                mSQL = "select CustomerNo, trim(GrpName) from  LDGrp order by CustomerNo";
                break SelectCode;
            }

            // ������Ҫ��Ŀ�����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "healthfactoryno")) {

                if (mCodeCondition.substring(1, 7).equals("000000")) {
                    // ���ڱ����ļ���
                    mSQL = "select '__','��¼�뱣����' from dual";
                } else if (mCodeCondition.substring(1, 7).equals("000001")) {
                    // ���ڱ����ļ���
                    mSBql
                        .append("select DutyCode,DutyName from LMDuty where DutyCode in(select DutyCode from LMRiskDuty where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by DutyCode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000002")) {
                    // ���ڸ����ļ���
                    mSBql
                        .append("select getdutycode,getdutyname from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000003")) {
                    // �����˻��ļ���
                    mSBql
                        .append("select insuaccno,insuaccname from LMRiskToAcc where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by insuaccno");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000004")) {
                    // �����������εļ���
                    mSBql
                        .append("select getdutycode,getdutyname from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }
            // ���������ѯICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "icdcode")) {
                mSBql
                    .append("select a.icdcode, a.icdname from  lddisease a where ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by a.icdcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���������ѯICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "diseascode")) {
                mSQL = "select icdcode, icdname from lddisease order by a.icdcode";
                break SelectCode;
            }

            // ���������ѯICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "diseasname")) {
                mSQL = "select icdname,icdcode from lddisease order by icdname";
                break SelectCode;
            }

            // ҽԺ���룬ҽԺ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospital")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append("order by a.hospitcode");
                mSQL = mSBql.toString();
                //  executeType = 1;
                break SelectCode;
            }
            //ҽԺ���룬ҽԺ���ƣ�����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospitallevel")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code,(select codename from ldcode where codetype='llhospitaltype' and code=a.BusiTypeCode)||decode((select codename from ldcode where codetype='levelcode' and code=a.levelcode ),null,'δ����','','δ����',(select codename from ldcode where codetype='levelcode' and code=a.levelcode )),a.LevelCode from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append("order by a.hospitcode");
                mSQL = mSBql.toString();
                //  executeType = 1;
                break SelectCode;
            }

            // ҽԺģ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospit")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by a.hospitcode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }
             // ҽԺģ����ѯ,����ҽԺ�ȼ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospitlevel")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code,(select codename from ldcode where codetype='llhospitaltype' and code=a.BusiTypeCode)||decode((select codename from ldcode where codetype='levelcode' and code=a.levelcode ),null,'δ����','','δ����',(select codename from ldcode where codetype='levelcode' and code=a.levelcode )) from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by a.hospitcode");
                mSQL = mSBql.toString();
               // executeType = 1;
                break SelectCode;
            }

            // ��������ѯICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llacci")) {
                mSBql
                    .append("select a.accidentno, a.accname from  llaccidenttype a where ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by a.accidentno");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ת�ֻ�����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "SwitchCompany")) {
                mSQL = "select a.comcode,a.name from ldcom a where length(trim(comcode)) =4 order by comcode";
                break SelectCode;
            }
            // ת����Ա��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "despatcher")) {
                mSBql
                    .append("select a.usercode,a.username from llclaimuser a where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �Ƽ�ҽԺģ����ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "Hostipalname")) {
                mSBql
                    .append("select a.hospitname,a.hospitcode,b.codename,b.code from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql
                    .append("%' and trim(b.code)=trim(a.fixflag) order by a.hospitcode");
                mSQL = mSBql.toString();
                break SelectCode;

            }

            // ҽ������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "doctor")) {
                mSBql
                    .append("select a.doctname,a.doctno from  lddoctor a where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by a.doctname");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �����ռ���Ҫ�ر���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "healthfactory")) {
                mSBql
                    .append("select FactoryCode||to_char(FactorySubCode),CalRemark,Params from LMFactoryMode where FactoryType = '");
                mSBql.append(mCodeCondition.substring(1, 7));
                mSBql.append("' and RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql.append(" order by FactoryCode,FactorySubCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �������������Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "itemcode")) {
                mSQL = "select itemcode,trim(ItemName) from lfItemRela order by itemcode";
                executeType = 1;
                break SelectCode;
            }
            // �������������Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "outitemcode")) {
                mSQL = "select outitemcode,trim(ItemName) from lfItemRela order by outitemcode";
                executeType = 1;
                break SelectCode;
            }
            // ���������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "stati")) {
                // mSBql.append(
                // "select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where ");
                // mSBql.append(mConditionField);
                // mSBql.append(" = ");
                // mSBql.append(mCodeCondition);
                // mSBql.append(" and codetype = 'station' order by code");
                // mSQL = mSBql.toString();
                mSBql.append("select ComCode, Name from LDCom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ComCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �������������Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comcodeisc")) {
                mSQL = "select comcodeisc,trim(name) from LFComISC order by comcodeisc";
                executeType = 1;
                break SelectCode;
            }
            // �������������Ϣ 2005-10-13 huanglei
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comtoisccom")) {
                mSQL = "select distinct(LFComISC.comcodeisc),trim(LFComISC.name) from LFComISC ,lfcomtoisccom "
                        + "where LFComISC.Comcodeisc=lfcomtoisccom.comcodeisc and lfcomtoisccom.comcode like '"
                        + mGlobalInput.ManageCom + "%' order by comcodeisc";
                executeType = 1;
                break SelectCode;
            }

            // ����״̬����ԭ��PolState
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "polstate2")) {
                mSQL = "select code,codename,codealias from ldcode where codetype = 'polstate' order by code";
                break SelectCode;
            }
            // ������ȡ����DutyKind
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "dutykind")) {
                mSBql
                    .append("select GetDutyKind, GetDutyName from LMDutyGetAlive where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by GetDutyKind");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��ȫ��ĿEdorType
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "edortype")) {
                mSBql
                    .append("select distinct EdorCode, EdorName from LMRiskEdoritem where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by EdorCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ������λOperateType
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "operatetype")) {
                mSBql
                    .append("select distinct OperateType,Remark from LDRiskComOperate where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by OperateType");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �˱��ϱ�����UWPopedomCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "uwpopedomcode")) {
                mSBql.append("select usercode, username from lduser where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by usercode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �˱��ϱ�����UWPopedomCode1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "uwpopedomcode1")) {
                mSBql
                    .append("select usercode, username from lduser where usercode = (select UpUserCode from LDUWUser where usercode = '");
                mSBql.append(mGlobalInput.Operator.trim());
                mSBql.append("') order by usercode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���з�������channel
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "channel")) {
                mSBql.append("select agentcom,name from lacom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append("and  banktype ='01' order by agentcom");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ���ִ�������StaticGroup
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "staticgroup")) {
                mSBql.append("select comcode,shortname from ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql
                    .append("%' union select branchattr,name from labranchgroup where ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' and branchlevel='03' and branchtype='1'");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }
            // ���ִ�������Depart
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "depart")) {
                mSBql
                    .append("select branchattr,name from labranchgroup where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql
                    .append("%'and branchlevel>='02' and branchtype='1' order by branchattr");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // ����BranchAttr
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "branchattr")) {
                mSBql
                    .append("select BranchAttr, Name from LABranchGroup where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by BranchAttr");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // �������������BranchCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "branchcode")) {
                mSBql
                    .append("select agentgroup, name from labranchgroup where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by branchattr");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // �������������BranchCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "allbranch")) {
                mSBql.append("select comcode,name from ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append("and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql
                    .append("%' union select branchattr,name from labranchgroup where branchtype='1' and (branchlevel='03' or branchlevel='02') and managecom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql
                    .append("%' and (state<>1 or state is null) order by comcode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // Ա����������BranchCodeType
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "branchcodetype")) {
                mSBql
                    .append("select gradecode, gradename from laagentgrade where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and branchtype='1' and gradeproperty6='1' order by gradecode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �������������HealthCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "healthcode")) {
                mSBql
                    .append("select distinct HealthCode, HealthName from LDHealth where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by HealthCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯƷ�ʹ�����Ŀ����� - xijiahui-����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "laqualityitemdef")) {
                mSBql
                    .append("select ItemCode,ItemContext from LAQualityItemDef where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ItemCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯƷ�ʹ�����Ŀ����� - xijiahui-����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "laqualityitemdef1")) {
                mSBql
                    .append("select ItemCode,ItemContext from LAQualityItemDef where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ItemCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������Ŀ����� - xijiahui-����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "larewardpunishitem")) {
                mSBql.append("select RPNo,Name from LARewardPunishItem where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by RPNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������Ŀ����� - xijiahui-����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "larewardpunishitem1")) {
                mSBql.append("select RPNo,Name from LARewardPunishItem where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by RPNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���¼������newHealthCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "newHealthCode")) {
                mSBql
                    .append("select code, codename from ldcode where Codetype='newhealthcode' ");
                mSBql.append(" order by to_number(code)");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������������RReportCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "rreportcode1")) {
                mSBql
                    .append("select rreportcode, RReportName from LDRReport where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and rreportclass = '1' order by rreportcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������������RReportCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "rreportcode2")) {
                mSBql
                    .append("select rreportcode, RReportName from LDRReport where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and rreportclass = '2' order by rreportcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �������������AgentGroup
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "agentgroup")) {
                mSBql
                    .append("select AgentGroup, Name from LABranchGroup where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and BranchLevel = '01' and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by AgentGroup");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �������������AgentGroup
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lcompanycodequery")) {
                mSBql
                    .append("select ComPanyNo,ComPanyName from reinsurancecompanyinfo order by companyno ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �˱���������EdorCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "edorcode")) {
                LDUserSchema tLDUserSchema = new LDUserSchema();
                try {
                    LDUserDB tLDUserDB = new LDUserDB();
                    tLDUserDB.setUserCode(this.mGlobalInput.Operator);
                    if (!tLDUserDB.getInfo()) {
                        System.out.println("select error");
                    }

                    tLDUserSchema = tLDUserDB.getSchema();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (tLDUserSchema.getEdorPopedom() == null) {
                    return false;
                }

                mSBql
                    .append("select distinct b.EdorCode, b.EdorName from LMRiskEdoritem  a,LMEdorItem b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by EdorCode");
                mSQL = mSBql.toString();
                while (mSQL.indexOf("@") != -1) {
                    int indexAsterisk = mSQL.indexOf("@");
                    String tPreStr = mSQL.substring(0, indexAsterisk);
                    String tPostStr = mSQL.substring(indexAsterisk + 1);
                    mSQL = tPreStr + tLDUserSchema.getEdorPopedom() + tPostStr;
                }
                break SelectCode;
            }

            // �����������AgentCom
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "agentcom")) {
                mSBql
                    .append("select AgentCom, Name, UpAgentCom, AreaType, ChannelType from LACom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by AgentCom");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "CNPCPrtNo")) {
                mSBql
                    .append(" select prtno,grpcontno from lcgrpcont where prtno in ");
                mSBql
                    .append("  (select code from ldcode where codetype = 'CNPCPrtNo') ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���ֱ�������RiskCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskcode")) {
                mSBql.append("select RiskCode,RiskName from LMRisk where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

//          ���ֱ�������riskenshortname
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskenshortname")) {
                mSBql.append("select RiskCode,RiskName,riskenshortname from LMRisk where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            
            // ���ֱ�������RiskCode1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).toLowerCase()
                .equalsIgnoreCase("riskcode1")) {
                mSQL = "select a.RiskCode, a.RiskName,b.SubRiskFlag,b.SubRiskFlag from LMRisk a,LMRiskApp b where a.RiskCode=b.RiskCode order by a.RiskCode";
                break SelectCode;
            }

            // ���ְ汾����RiskVersion
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskversion")) {
                mSBql.append("select RiskVer from LMRisk where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by RiskVer");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ͻ�ָ�ϱ����ѯ������ָ�����Ͳ�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "guidecode")) {
                ExeSQL tExeSQL = new ExeSQL();
                String tRiskProp = tExeSQL
                    .getOneValue("select RiskProp from LMRiskApp where RiskCode = '"
                            + mConditionField + "'");
                mSBql
                    .append("select GuideCode,ItemName from LDGuide where GuideType = ");
                mSBql.append(mCodeCondition);
                // System.out.println("RiskProp is : " + tRiskProp);
                if (tRiskProp.equals("G")) {
                    mSBql.append(" and substr(GuideCode,1,1) = 'G'");
                } else {
                    mSBql.append(" and substr(GuideCode,1,1) = 'P'");
                }
                mSBql.append(" order by GuideCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������������ComCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comcode")) {
                mSBql
                    .append("select ComCode, Name, ShortName, Address, Sign from ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by comcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �ʽ����ƽ̨����ComCode  add by Bright
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "zcomcode")) {
                mSBql
                    .append("select ComCode, Name from ldcom where (length(comcode)=2 or length(comcode)=4 or length(comcode)=8 )and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by comcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //���ۺϲ�ѯ-->Ͷ����Ϣ��ѯ/������ϸ��ѯ-->�������嵥��ʾ��������ԶԱ����ŵķ������.BUG:12563
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "executecom")) {
                mSBql
                    .append("select distinct d.ExecuteCom,(select m.name  from ldcom m where m.comcode=d.ExecuteCom) ");
                mSBql.append(" from insured_view d where d.grpcontno=");
                mSBql.append(mCodeCondition);
                mSBql.append(" and d.ExecuteCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by d.ExecuteCom");
                mSQL = mSBql.toString();
                //System.out.println("��ѯ���������"+mSQL);
                break SelectCode;
            }

            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comcode4")) {
                mSBql
                    .append("select ComCode, Name, ShortName, Address, Sign from ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' and length(trim(comcode))=4 order by comcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������������ComCodeAll
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comcodeall")) {
                mSBql
                    .append("select ComCode, Name, ShortName, Address, Sign from ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by comcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 2006.11.14 ͳ�����������еķ����������ComCodeAll
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "ExecuteCom3")) {
                mSBql
                    .append("select ComCode, Name, ShortName, Address, Sign from ldcom where length(comcode)=8 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by comcode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �����ձ�������Riskbank
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskbank")) {
                if (mCodeCondition.trim().equals("'1' and branchtype=2")) {
                    mCodeCondition = "1";
                    mSBql
                        .append("select RiskCode, RiskName from LMRiskApp where ");
                    mSBql.append(mConditionField);
                    mSBql.append(" = ");
                    mSBql.append(mCodeCondition);
                    mSBql
                        .append(" and RiskProp in ('A','B','G','D') order by RiskCode");
                    mSQL = mSBql.toString();
                } else {
                    mCodeCondition = "1";
                    mSBql
                        .append("select RiskCode, RiskName from LMRiskApp where ");
                    mSBql.append(mConditionField);
                    mSBql.append(" = ");
                    mSBql.append(mCodeCondition);
                    mSBql
                        .append(" and RiskProp in ('Y','B','C','D') order by RiskCode");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }

            // ���ձ�������RiskGrp
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskgrp")) {
                mSBql.append("select RiskCode, RiskName from LMRiskApp where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and RiskProp in ('G','A','B','D') order by RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���ձ�������RiskInd
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskind")) {
                mSBql.append("select RiskCode, RiskName from LMRiskApp where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and RiskProp in ('I','A','C','D') order by RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ͨ��֤��������CertifyCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "certifycode")) {
                mSBql
                    .append("SELECT CertifyCode, CertifyName FROM LMCertifyDes WHERE ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and CertifyClass = 'S' AND State = '0' order by CertifyCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���֤��������CardCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "cardcode")) {
                mSBql
                    .append("SELECT CertifyCode, CertifyName FROM LMCertifyDes WHERE ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and CertifyClass = 'D' AND State = '0' order by CertifyCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ϵͳ��֤��������SysCertCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "syscertcode")) {
                mSBql
                    .append("SELECT CertifyCode, CertifyName FROM LMCertifyDes WHERE ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and CertifyClass = 'S' AND State = '0' order by CertifyCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ʻ���ѯInsuAccNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "insuaccno")) {
                mSBql
                    .append("SELECT InsuAccNo, InsuAccName FROM LMRiskInsuAcc WHERE ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by InsuAccNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // Ͷ���ʻ���ѯ tl_insuaccno
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "tl_insuaccno")) {
            mSBql
                .append("select distinct investtype, InsuAccName from LMRiskInsuAcc where AccKind='2' and ");
            mSBql.append(mConditionField);
            mSBql.append(" = ");
            mSBql.append(mCodeCondition);
            mSBql.append(" order by investtype");
            mSQL = mSBql.toString();
            break SelectCode;
        }

            // ��֪��������ImpartCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impartcode")) {
                mSBql
                    .append("SELECT ImpartCode, ImpartContent,ImpartParamModle FROM LDImpart WHERE ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ImpartCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���������������Station����Ȼ��ʹ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "allstation")) {
                mSBql.append("select comcode,name from Ldcom where ");
                mSBql.append(mConditionField);
                mSBql.append(" like ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ComCode");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            // ���������������Station����Ȼ��ʹ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "station")) {
                // mSBql.append(
                // "select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where ");
                // mSBql.append(mConditionField);
                // mSBql.append(" = ");
                // mSBql.append(mCodeCondition);
                // mSBql.append(" and codetype = 'station' and code like '");
                // mSBql.append(mGlobalInput.ManageCom);
                // mSBql.append("%' order by code");
                // mSQL = mSBql.toString();
                mSBql.append("select ComCode, Name from LDCom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ComCode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by ComCode");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            // ���������������Station_logon����Ӧ���ڵ�½ҳ���������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "station_logon")) {
                mSBql.append("select ComCode, Name from LDCom where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ComCode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' order by ComCode");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            // ���ִ�������OccupationCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "occupationcode")) {
                mSBql
                    .append("select OccupationCode, trim(OccupationName)||'-'||trim(workname), OccupationType from LDOccupation where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by OccupationCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // ���ѷ�ʽ��������PayYears
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "payyears")) {
                mSBql
                    .append("select trim(PayEndYearFlag)||PayEndYear||'*'||PayIntv,ShowInfo from LMPayMode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // �ŵ����ֲ�ѯGrpRisk
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "grprisk")) {
                mSBql
                    .append("select a.RiskCode, a.RiskName,b.GrpPolNo,a.SubRiskFlag from LMRiskApp a,grppol_view b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.RiskCode = b.RiskCode order by a.RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ֲ�ѯGrpMainRisk����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "grpmainrisk")) {
                mSBql
                    .append("select a.RiskCode, a.RiskName,b.GrpPolNo from LMRiskApp a,LCGrpPol b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.RiskCode = b.RiskCode and a.SubRiskFlag <> 'S' order by a.RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //�ŵ����ֲ�ѯGrpMainRisk���� GrpMainRisk1 �ײͶ������ձ�����ldplanrisk����ȡ�ã� cyj
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "grpmainrisk1")) {
              //  mSQL = "select a.RiskCode, a.RiskName from LMRiskApp a where a.SubRiskFlag <> 'S' order by a.RiskCode";
              // mSQL = "select distinct b.MainRiskCode, a.RiskName from LMRiskApp a,ldplanrisk b where a.SubRiskFlag <> 'S' order by a.RiskCode";
                mSBql
                .append("select distinct b.MainRiskCode, a.RiskName from LMRiskApp a,ldplanrisk b where ");
            mSBql.append(mConditionField);
            mSBql.append(" = ");
            mSBql.append(mCodeCondition);
            mSBql
                .append(" and a.RiskCode=b.MainRiskCode order by b.MainRiskCode");
            mSQL = mSBql.toString();
            break SelectCode;
            }

            // �����ײ�RiskPlan
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskplan")) {
                mSQL = "select ContPlanCode,ContPlanName from LDPlan order by ContPlanCode";
                break SelectCode;
            }

            // �ŵ����ֽɷѹ����ѯRiskRuleFactoryType��Type����Ĭ��Ϊ000005
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskrulefactorytype")) {
                mSBql
                    .append("select distinct a.FactoryType,b.FactoryTypeName,trim(a.FactoryType)||trim(a.RiskCode) from LMFactoryMode a,LMFactoryType b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.FactoryType = b.FactoryType and a.FactoryType = '000005'");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ֹ��������ѯRiskAscriptionRuleFactoryType��Type����Ĭ��Ϊ000006
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskascriptionrulefactorytype")) {
                mSBql
                    .append("select distinct a.FactoryType,b.FactoryTypeName,trim(a.FactoryType)||trim(a.RiskCode),'' from LMFactoryMode a,LMFactoryType b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.FactoryType = b.FactoryType and a.FactoryType = '000006'");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ֽɷѹ����ѯRiskRuleFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskrulefactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000005")) {
                    // û�м����κ������������Ժ���չ
                    mSBql
                        .append("select PayPlanCode,PayPlanName from LMDutyPay where payplancode in (select payplancode from lmdutypayrela where dutycode in (select dutycode from lmriskduty where riskcode = '");
                    mSBql.append(mCodeCondition.substring(7, 12));
                    mSBql.append("'))");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }

            // �ŵ����ֹ��������ѯRiskRuleFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskascriptionrulefactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000006")) {
                    // û�м����κ������������Ժ���չ
                    mSBql
                        .append("select PayPlanCode,PayPlanName,'' from LMDutyPay where AccPayClass in('4','7','8')"
                                + " and payplancode in (select payplancode from lmdutypayrela where dutycode"
                                + " in (select dutycode from lmriskduty where riskcode = '");
                    //���������4λ���������� 20070528
                    if (mCodeCondition.length() == 12) {
                        mSBql.append(mCodeCondition.substring(7, 11));
                    } else {
                        mSBql.append(mCodeCondition.substring(7, 12));
                    }
                    mSBql.append("'))");
                    mSQL = mSBql.toString();
                    //System.out.println("riskascriptionrulefactoryno="+mSQL);
                }
                break SelectCode;
            }
            // �ŵ����ֽɷѹ����ѯRiskRuleFactory
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskrulefactory")) {
                mSBql
                    .append("select FactoryCode||to_char(FactorySubCode),CalRemark,Params,FactoryName from LMFactoryMode where FactoryType = '");
                mSBql.append(mCodeCondition.substring(1, 7));
                mSBql.append("' and RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql.append(" order by FactoryCode,FactorySubCode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ֹ��������ѯRiskRuleFactory
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskascriptionrulefactory")) {
                mSBql
                    .append("select FactoryCode||to_char(FactorySubCode),CalRemark,Params,FactoryName from LMFactoryMode where FactoryType = '");
                mSBql.append(mCodeCondition.substring(1, 7));
                mSBql.append("' and RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql.append(" order by FactoryCode,FactorySubCode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ռƻ������ֲ�ѯImpRiskCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impriskcode")) {
                String[] a = new String[2];//���ڼ�¼���ϲ㼶��GrpContNo a[0]�Ǳ��ϲ㼶 a[1]��GrpContNo
                StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
                for (int i = 0; st.hasMoreTokens(); i++) {
                    a[i] = st.nextToken();
                }

                mSBql
                    .append("select a.RiskCode, a.RiskName, a.RiskVer,b.MainRiskCode,b.MainRiskVersion,'' from LMRiskApp a,LCContPlanRisk b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a[0]);
                mSBql.append("' and GrpContNo ='");
                mSBql.append(a[1]);
                mSBql
                    .append(" and a.RiskCode = b.RiskCode order by a.RiskCode");
                //�����ײ͵�
                if ("TC'".equals(a[1])) {
					mSBql = new StringBuffer(128);
					mSBql
							.append("select a.RiskCode, a.RiskName, a.RiskVer,b.MainRiskCode,b.MainRiskVersion,'' from LMRiskApp a,LDPlanRisk b where ");
					mSBql.append(mConditionField);
					mSBql.append(" = ");
					mSBql.append(a[0]);
					mSBql
							.append("' and a.RiskCode = b.RiskCode order by a.RiskCode");
				}

                mSQL = mSBql.toString();
                break SelectCode;
            }
             //�ŵ����ռƻ������ֲ�ѯ�������÷ֶ������⸶������ImpRiskCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impllriskrate")) {
                String[] a = new String[2];//���ڼ�¼���ϲ㼶��GrpContNo a[0]�Ǳ��ϲ㼶 a[1]��GrpContNo
                StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
                for (int i = 0; st.hasMoreTokens(); i++) {
                    a[i] = st.nextToken();
                }

                mSBql
                    .append("select a.RiskCode, a.RiskName, a.RiskVer,b.MainRiskCode,b.MainRiskVersion,'','1' from LMRiskApp a,LCContPlanRisk b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a[0]);
                mSBql.append("' and GrpContNo ='");
                mSBql.append(a[1]);
                mSBql
                    .append(" and a.RiskCode = b.RiskCode and exists(select 1 from ldcode where codetype='llriskrate' and code=a.riskcode) order by a.RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ŵ����ռƻ������ֲ�ѯ�������÷ֶ������⸶������ImpRiskCode����ȫ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impllriskrateedor")) {
                String[] a = new String[4];//���ڼ�¼���ϲ㼶��GrpContNo a[0]�Ǳ��ϲ㼶 a[1]��GrpContNo
                StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
                for (int i = 0; st.hasMoreTokens(); i++) {
                    a[i] = st.nextToken();
                }

                mSBql
                    .append("select a.RiskCode, a.RiskName, a.RiskVer,b.MainRiskCode,b.MainRiskVersion,'','1' from LMRiskApp a,LPContPlanRisk b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a[0]);
                mSBql.append("' and GrpContNo ='");
                mSBql.append(a[1]);
                mSBql.append("' and b.edorno ='");
                mSBql.append(a[2]);
                mSBql.append("' and b.edortype ='");
                mSBql.append(a[3]);
                mSBql.append(" and a.RiskCode = b.RiskCode and exists(select 1 from ldcode where codetype='llriskrate' and code=a.riskcode) order by a.RiskCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ŵ����ռƻ������ֲ�ѯImpRiskCode ��ȫ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impriskcodeedor")) {
                String[] a = new String[4];//���ڼ�¼���ϲ㼶��GrpContNo a[0]�Ǳ��ϲ㼶 a[1]��GrpContNo a[2]edorno a[3] edortype
                StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
                for (int i = 0; st.hasMoreTokens(); i++) {
                    a[i] = st.nextToken();
                }

                mSBql
                    .append("select a.RiskCode, a.RiskName, a.RiskVer,b.MainRiskCode,b.MainRiskVersion,'' from LMRiskApp a,LCContPlanRisk b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a[0]);
                mSBql.append("' and GrpContNo ='");
                mSBql.append(a[1]);
                mSBql.append("' and a.RiskCode = b.RiskCode");
                mSBql
                    .append(" union select a.RiskCode, a.RiskName, a.RiskVer,c.MainRiskCode,c.MainRiskVersion,'' from LMRiskApp a,LPContPlanRisk c where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a[0]);
                mSBql.append("' and GrpContNo ='");
                mSBql.append(a[1]);
                mSBql.append("' and a.RiskCode = c.RiskCode and c.edorno = '"
                        + a[2] + "' and c.edortype = '" + a[3]);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �ŵ����ռƻ������ֶ�ӦҪ�����ImpFactoryType
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impfactorytype")) {
                mSBql
                    .append("select distinct a.FactoryType,b.FactoryTypeName,trim(a.FactoryType)||trim(a.RiskCode) from LMFactoryMode a,LMFactoryType b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.FactoryType = b.FactoryType and a.FactoryType < '000005'");
                mSQL = mSBql.toString();
                // ���ñ�ǩ��������
                break SelectCode;
            }
            // �ŵ����ռƻ������ֶ�ӦҪ��Ŀ�����ImHealthFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imhealthfactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000000")) {
                    // ���ڱ����ļ���
                    mSQL = "select '__','��¼�뱣����' from dual";
                } else if (mCodeCondition.substring(1, 7).equals("000001")) {
                    // ���ڱ����ļ���
                    mSBql
                        .append("select DutyCode,DutyName,getdutycode||'"
                                + mCodeCondition.substring(7)
                                + "' from LMDuty where DutyCode in(select DutyCode from LMRiskDuty where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by DutyCode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000002")) {
                    // ���ڸ����ļ���
                    mSBql
                        .append("select getdutycode,getdutyname ,getdutycode||'"
                                + mCodeCondition.substring(7)
                                + "' from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000003")) {
                    // �����˻��ļ���
                    mSBql.append("select insuaccno,insuaccname ,getdutycode||'"
                            + mCodeCondition.substring(7)
                            + "' from LMRiskToAcc where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(" order by insuaccno");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000004")) {
                    // �����������εļ���
                    mSBql
                        .append("select a.getdutycode,a.getdutyname,a.getdutycode||'"
                                + mCodeCondition.substring(7)
                                + " from lmdutygetrela a where a.dutycode in ( select b.dutycode from lmriskduty b where b.riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") ");
                    mSBql.append("and exists");
                    mSBql.append(" (select * from LMFactoryMode c");
                    mSBql.append(" where c.RiskCode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql
                        .append(" and c.factorycode || to_char(c.FactorySubCode) in");
                    mSBql
                        .append(" (select d.FactoryCode || to_char(d.FactorySubCode) from Lmfactoryrela d");
                    mSBql.append(" where d.RiskCode = '");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(" and d.objectcode = a.getdutycode))");
                    mSBql.append(" order by getdutycode");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }
            
            // �ŵ����ռƻ������ֶ�Ӧ�������δ���ImGetDutyCode,�ֶ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imgetdutycode")) {
            	
	            	 String[] a = new String[3];//���ڼ�¼���ϲ㼶��GrpContNo 
	                 StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
	                 for (int i = 0; st.hasMoreTokens(); i++) {
	                     a[i] = st.nextToken();
	                 }
                    // �����������εļ���
                    mSBql.append("select a.getdutycode,a.getdutyname,'0','99999999','1' from lmdutygetrela a where a.dutycode in ( select b.dutycode from lmriskduty b where b.riskcode = ");
                    mSBql.append(a[0]);
                    mSBql.append("') ");
                    mSBql.append(" and a.dutycode in (select distinct dutycode from lccontplandutyparam where grpcontno= '");
                    mSBql.append(a[2]);
                    mSBql.append(" and contplancode ='");
                    mSBql.append(a[1]);
                    mSBql.append("') ");
                    mSBql.append(" and getdutycode in(select code From ldcode where codetype='llgetdutyrate') ");
                    mSBql.append(" order by a.getdutycode");
                    mSQL = mSBql.toString();
                
                break SelectCode;
            }
            // �ŵ����ռƻ������ֶ�Ӧ�������δ���ImGetDutyCode,��ȫ,�ֶ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imgetdutycodeedor")) {
            	
	            	 String[] a = new String[5];//���ڼ�¼���ϲ㼶��GrpContNo 
	                 StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
	                 for (int i = 0; st.hasMoreTokens(); i++) {
	                     a[i] = st.nextToken();
	                 }
                    // �����������εļ���
                    mSBql.append("select a.getdutycode,a.getdutyname,'0','99999999','1' from lmdutygetrela a where a.dutycode in ( select b.dutycode from lmriskduty b where b.riskcode = ");
                    mSBql.append(a[0]);
                    mSBql.append("') ");
                    mSBql.append(" and a.dutycode in (select distinct dutycode from lpcontplandutyparam where grpcontno= '");
                    mSBql.append(a[2]);
                    mSBql.append("' and contplancode ='");
                    mSBql.append(a[1]);
                    mSBql.append("' and edorno ='");
                    mSBql.append(a[3]);
                    mSBql.append("' and edortype ='");
                    mSBql.append(a[4]);
                    mSBql.append(") ");
                    mSBql.append(" and getdutycode in(select code From ldcode where codetype='llgetdutyrate') ");
                    mSBql.append(" order by a.getdutycode");
                    mSQL = mSBql.toString();
                
                break SelectCode;
            }
            // �ŵ����ռƻ������ֶ�ӦҪ�ؼ������ImHealthFactory
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imhealthfactory")) {
                mSBql
                    .append("select FactoryCode||to_char(FactorySubCode),CalRemark,Params,FactoryName from LMFactoryMode where FactoryType = '");
                mSBql.append(mCodeCondition.substring(1, 7));
                mSBql.append("' and RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql.append(" order by FactoryCode,FactorySubCode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �ŵ����ռƻ������ֶ�ӦҪ�ؼ������ImHealthFactory2,���ϲ㼶ҪԼ¼���� 2007.5.8
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imhealthfactory2")) {
                mSBql
                    .append("select FactoryCode||to_char(FactorySubCode),CalRemark,Params,FactoryName from LMFactoryMode where FactoryType = '");
                mSBql.append("000004");
                mSBql.append("' and RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql
                    .append(" and factorycode||to_char(FactorySubCode) in(select a.FactoryCode || to_char(a.FactorySubCode) from "
                            + "Lmfactoryrela a where a.RiskCode='");
                mSBql.append(mCodeCondition.substring(7));
                mSBql.append(" and a.objectcode = '");
                mSBql.append(mCodeCondition.substring(1, 7));
                mSBql.append("')");
                mSBql.append(" order by FactoryCode,FactorySubCode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �����˱�������AgentCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "agentcode")) {
                ExeSQL tExeSQL = new ExeSQL();
                String agent = tExeSQL
                    .getEncodedResult(
                        "select Sysvarvalue from ldsysvar where Sysvar = 'LAAgent'",
                        1);
                agent = agent.substring(agent.indexOf("^") + 1);

                mSBql
                    .append("select AgentCode, Name, BranchCode from LAAgent where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' and ");
                mSBql.append(agent);
                mSBql.append(" order by AgentCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // �����˱�������AgentCode2 --liujw
            // Update by Yuanaq
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "agentcode2")) {
                ExeSQL tExeSQL = new ExeSQL();
                String agent = tExeSQL
                    .getEncodedResult(
                        "select Sysvarvalue from ldsysvar where Sysvar = 'LAAgent'",
                        1);
                agent = agent.substring(agent.indexOf("^") + 1);

                mSBql
                    .append("select a.AgentCode, a.Name, b.branchattr,b.Name from LAAgent a left join lABranchGroup b on a.agentgroup=b.agentgroup where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and a.ManageCom like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' and ");
                mSBql.append(agent);
                mSBql.append(" order by a.AgentCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // ���ҹ��������Ӧ����ְ�������� ---����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "dimissionitemdef")) {
                mSBql
                    .append("select ProcedureNo,ProcedureName,ProcLayer,ProcKind,(select codename from ldcode ");
                mSBql
                    .append(" where codetype='prockind' and code=ProcKind) from LDDimissionItemDef where proclayer = '1' and ");
                mSBql.append(mConditionField); // �������
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ProcedureNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "assessagentcode")) {
                // �������⴦��
                mSBql
                    .append("select a.AgentCode,g.Name,a.agentgrade1,CASE WHEN trim(a.agentgrade1)='00' THEN '����' ELSE (select gradename from laagentgrade where gradecode=a.agentgrade1) END,b.name,c.name,a.agentgrade,f.gradename from LAAssess a,labranchgroup b,ldcom c,laagentgrade f,laagent g");
                mSBql
                    .append(" where b.agentGroup=a.agentgroup and c.comcode=a.managecom and f.gradecode=a.agentgrade and a.agentcode=g.agentcode and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by a.AgentCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѵ�˵���ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "trainmenu")) {
                mSBql
                    .append("select NodeKey,NodeName from LDMenu where NodeKey like 'Tr%' and ");
                mSBql.append(mConditionField); // �������
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by NodeKey");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѵ�̲Ĳ�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "bookcode")) {
                mSBql
                    .append("select BookCode,Name,BookType,BookIntro from LABook where ");
                mSBql.append(mConditionField); // �������
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by BookCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // Ա���ƴ��������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "employeegrade")) {
                mSQL = "select gradecode,gradename from laagentgrade where GradeProperty6 = '1' order by gradecode";
                break SelectCode;
            }

            // Ա���ƴ��������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "employeegrade2")) {
                mSBql
                    .append("select lawelfareradix.agentgrade,ldcode.codename,100 from lawelfareradix ,ldcode,laagentgrade where  ldcode.codetype = 'employeeaclass' and ldcode.code = lawelfareradix.aclass and lawelfareradix.branchtype = '1' and laagentgrade.gradecode = lawelfareradix.agentgrade and laagentgrade.gradeproperty6 = '1' and lawelfareradix.aclass = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by lawelfareradix.agentgrade");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ������ͬ��ɨ���¼���˺Ų�ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "accnum")) {
                mSBql.append("select BankAccNo,AccName from LCAccount where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �û���ַ����������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "getaddressno")) {
                mSBql
                    .append("select AddressNo,PostalAddress from LCAddress where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �����û���ַ����������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "getgrpaddressno")) {
                mSBql
                    .append("select AddressNo,GrpAddress from LCGrpAddress where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // �ŵ����ֲ�ѯ���Ѽ��payintv
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskpayintv")) {
                mSBql
                    .append("select a.PayIntv, b.CodeName from LMRiskPayIntv a,LDCode b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.ChooseFlag = '1'  and b.CodeType = 'payintv'  and a.PayIntv = b.Code order by a.PayIntv");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��ѯ���ִ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "dutycode")) {
                mSBql
                    .append("select DutyCode,DutyName from LMDuty where  dutycode in (select dutycode from lmriskduty where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(") order by DutyCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ��������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "getdutykind")) {
                mSBql
                    .append("select GetDutyKind,GetDutyName from LMDutyGetAlive where getdutycode in (select getdutycode from lmdutygetrela where dutycode in (select dutycode from lmriskduty where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" )) order by getdutykind");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // xjh Add,2005/02/18
            // �������� branchlevel
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "branchlevel")) {
                mSBql
                    .append("select BranchLevelCode,BranchLevelName from LABranchLevel where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by BranchLevelCode,BranchLevelID");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // xjh Modify 2005/3/22
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "agentgrade")) {
                mSBql
                    .append("select GradeCode,GradeName from LAAgentGrade where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and substr(rtrim(gradecode),length(rtrim(gradecode))-1) >'00' order by GradeID");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // liuyan Modify 2006
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "OrganComCode")) {
                mSBql
                    .append("select organcomcode,grpname from lcsendorgan where 1=1 and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by organcomcode");
                // mSBql.append(
                // " and substr(rtrim(gradecode),length(rtrim(gradecode))-1) >'00' order by GradeID");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��������
            // С�������Ϣ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "acceptcom")) {
                mSQL = "select GroupNo, GroupName from LGGroup order by GroupNo";
                break SelectCode;
            }

            // ҵ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "tasktoptypeno")) {
                // ��������
                mSQL = "select WorkTypeNo, WorkTypeName from LGWorkType where SuperTypeNo = '00' order by WorkTypeNo ";
                break SelectCode;
            }

            // ҵ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "tasktypeno")) {
                mSBql
                    .append("select WorkTypeNo, WorkTypeName from LGWorkType where SuperTypeNo != '00' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by WorkTypeNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��Ա����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "taskmemberno")) {
                mSQL = "select UserCode, UserName from LDUser order by UserCode";
                break SelectCode;
            }

            // xjh Add,2005/02/24
            // �������� SpecRisk
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "specrisk")) {
                mSQL = "select riskcode,riskname from lmriskapp ";
                break SelectCode;
            }

            // �ŵ��ͻ���������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "serverinfotype")) {
                mSQL = "select ServKind,ServKindRemark from LDServKindInfo order by servkind";
                break SelectCode;
            }
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "serverinfocode")) {
                mSBql
                    .append("select ServDetail,ServDetailRemark,trim(servkind)||'-'||trim(servdetail) from LDServDetailInfo where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ServDetail");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "serverinfochoosecode")) {
                int c = mCodeCondition.indexOf("-");
                String a = mCodeCondition.substring(0, c); // ���ռƻ�����
                String b = mCodeCondition.substring(c + 1); // ��ͬ��
                mSBql
                    .append("select ServChoose,ServChooseRemark from LDServChooseInfo where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(a);
                mSBql.append("' and servdetail = '");
                mSBql.append(b);
                mSBql.append(" order by ServChoose");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���ҹ��������Ӧ�û�������������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comtoareatype")) {

                mSBql
                    .append("select distinct code2,rtrim(code2)||'�����' from ldcoderela a where relatype = 'comtoareatype' and  ");
                mSBql.append(mConditionField); // �������
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by code2");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ���ղ�����������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "getagentmanager")) {

                mSBql
                    .append("select a.agentcode,a.name from laagent a,latree b where a.agentcode=b.agentcode and (b.agentgrade like 'J%' or b.agentgrade like 'K%')");

                mSQL = mSBql.toString();

                break SelectCode;
            }

            // ���ղ�ѯ��ҵ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "businesstype")) {

                mSBql
                    .append("select code,codename From ldcode where codetype='businesstype' and ");
                mSBql.append(mConditionField); // �������
                mSBql.append("  like '%");
                if (!mConditionField.equals("1")) {
                    // mCodeCondition = mCodeCondition; //�������mConditionFieldΪ�յ�ʱ���ǿ����Ϊ 1 �˴����Ϊ1 �Ͳ���Substring�� ����ᱨ��
                    // }
                    // else
                    // {
                    mCodeCondition = mCodeCondition.substring(1, mCodeCondition
                        .length() - 1);
                }

                mSBql.append(mCodeCondition);
                mSBql.append("%'");
                mSQL = mSBql.toString();
                break SelectCode;

            }

            // ��ѯ�ͻ����б�����Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "bank")) {
                mSBql
                    .append("select distinct BankCode, BankName from LDBank a where 1=1 and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' ");
                if (mConditionField == null || mConditionField.length() >= 6) {
                    mSBql
                        .append(" and exists (select 1 from lcsendorganbankrela where sendinnercode='");
                    mSBql.append(mConditionField.substring(0, 6));
                    mSBql.append("' and bankcode=a.bankcode and prtno=");
                    mSBql.append(mCodeCondition);
                    mSBql.append(")");
                }
                mSBql.append(" order by BankCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // ��ѯ���б�����Ϣ ����ר��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llbank")) {
                mSBql
                    .append("select distinct BankCode, BankName from LDBank a where 1=1 and comcode like '");
                mSBql.append(mGlobalInput.ManageCom);
                mSBql.append("%' and chkfailflag='F' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by BankCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            //          ��ѯ�ͻ����б�����Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "njbank")) {
                mSBql
                    .append("select distinct a.BankCode, a.BankName from LDBank a");
                if (mConditionField != null || mConditionField.length() >= 6) {
                    mSBql
                        .append(",lcsendorganbankrela b where a.comcode = b.sendcomcode and b.sendinnercode = '");
                    mSBql.append(mConditionField.substring(0, 6));
                    mSBql
                        .append("' and a.bankcode = b.bankcode and b.prtno = ");
                    mSBql.append(mCodeCondition);
                }
                mSBql.append(" order by a.BankCode");
                mSQL = mSBql.toString();
                executeType = 1;
                break SelectCode;
            }

            // �������б�����Ϣ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "operbank")) {
                mSBql
                    .append("select distinct BankCode, BankName from LDBank where ");
                mSBql.append(mConditionField);
                mSBql.append(" like '");
                mSBql.append(mCodeCondition.substring(1, mCodeCondition
                    .length() - 1));
                mSBql.append("%' order by BankCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ���չ�˾�����˻���Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "bankaccno")) {
                mSBql.append("select Code, CodeName from ldcode1 where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype = '");
                mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType())
                    .toLowerCase());
                mSBql.append("' order by Code");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��ѯ������Ա�ֽ����Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "cashstoreroom")) {
                mSBql.append("select Code, CodeName from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by Code");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // ��ѯ��������Լ��Э���Լ������ԭ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "tb_reason")) {
                mSBql
                    .append("select Code, CodeName from ldcode where codetype='tb_reason' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by to_number(Code)");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ���չ�˾����������Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "insbankcode")) {
                mSBql.append("select Code, CodeName from ldcode1 where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype = '");
                mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType())
                    .toLowerCase());
                mSBql.append("' order by Code");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��ѯ�������������֧���ļ���Ϣ
            // Tracy add 2006-3-28
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "njbankcode")) {
                mSBql
                    .append("select a.BankCode, b.BankName from LDBank b,"
                    		+ "LCSendOrganBankRela a where a.bankcode=b.bankcode and a.sendcomcode=b.comcode and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                // mSBql.append(" and codetype = '");
                // mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType()).toLowerCase());
                mSBql.append(" order by a.BankCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // �����մ��������ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "airpolagent")) {
                mSBql.append("select agentcom,name from lacom  where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and  branchtype='2' and UpAgentCom is  null  ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������__��������ֹ�˾��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "airpolagentcom")) {
                mSBql.append("select agentcom,name from lacom  where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and  branchtype='2' ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ������__������Ϣ ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "airriskcode")) {
                mSBql.append("select riskcode,riskname from LMRiskApp  where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and risktype8='H' union select contplancode,contplanname from  ldplan where remark2='H' ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��������_������Ϣ ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llgetriskcode")) {
                mSBql
                    .append("select a.riskcode,a.riskname from LMRisk a,LCGrpPol b where ");
                mSBql.append("b.GrpContNo");
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and a.riskcode = b.riskcode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //������������_������Ϣ��ѯ���б�����ʱ�ӱ����ŵ�������û��ʱֱ�Ӳ����ֱ��룩
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llgetriskcode1")) {
                mSBql
                    .append("select a.riskcode,a.riskname from LMRisk a,LCGrpPol b where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and a.riskcode = b.riskcode ");

                int n = Integer
                    .parseInt(mExeSQL
                        .getOneValue(" select count(1) from LMRisk a,LCGrpPol b where "
                                + mConditionField
                                + " = "
                                + mCodeCondition
                                + " and a.riskcode = b.riskcode "));
                if (n < 1) {
                    mSBql = new StringBuffer(128);
                    mSBql.append("select riskcode,riskname from LMRisk ");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ��������_��֧���� ��ѯ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llgetorgancomcode")) {
                mSBql.append("select OrganComCode,GrpName from LCOrgan where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by OrganComCode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // ����ӿ�_��������¼��_�����Ŀѡ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "liaccount")) {
                mSBql
                    .append("select AccountCode,AccountName from liaccountconfig where ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }

            // ��ȫ��Ŀ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "njedorcode")) {
                mSBql.append("select edorcode,edorname from lmedoritem where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //liujun 2010-04-14���ӹ������ȫ���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
	            "annuityEdorNo")) {
	            mSBql.append("select code,codename from ldcode where codetype='annuityEdorNo' and ");
	            mSBql.append(mConditionField);
	            mSBql.append(" = ");
	            mSBql.append(mCodeCondition);
	            mSBql.append(" order by codename desc");
	            mSQL = mSBql.toString();
	            break SelectCode;
            }
            //liujun 2006��10��
            //������ϵ1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcomcode1to2ltorgan")) {
                mSBql
                    .append("select distinct organcomcode,grpname from ltorgan where organcomcode in(select organcomcode2 from lcorganrela where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" ) and childflag='0' ");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            //������ϵ2
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcomcode1to2lcorgan")) {
                mSBql
                    .append("select distinct organcomcode,grpname from lcorgan where organcomcode in(select organcomcode2 from lcorganrela where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" ) and childflag='0'");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            //������ϵ3
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcomcode2to1ltorgan")) {
                mSBql
                    .append("select distinct organcomcode,grpname from ltsendorgan where organcomcode in(select organcomcode1 from lcorganrela where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" ) and childflag='0'");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            //������ϵ4
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcomcode2to1lcorgan")) {
                mSBql
                    .append("select distinct organcomcode,grpname from lcsendorgan where organcomcode in(select organcomcode1 from lcorganrela where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" ) and childflag='0'");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }

            //��ѯһ�����Ż���1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "sendfstcode_t")) {
                mSBql.append("select distinct organcomcode,grpname from(");
                mSBql
                    .append("(select a.organcomcode,a.grpname from ltsendorgan a where a.");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.grouplevel='1' and exists(select 'x' from lcannualgetlog ");
                mSBql
                    .append("b where b.sendcomcode=a.organcomcode and b.calmonth =a.calmonth))");
                mSBql
                    .append(" union (select organcomcode,grpname from lcsendorgan where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and (grouplevel='1' or organcomcode='0000')))order by organcomcode");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            //��ѯһ�����Ż���2
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "sendfstcode_c")) {
                mSBql
                    .append("select organcomcode,grpname from lcsendorgan where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and grouplevel='1'");
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }
            //�ٱ���ͬ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrcontno")) {
                mSBql
                    .append("select RIContNo, RIContName from RIBarGainInfo order by RIContNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //�ٱ�ҵ���ʵ�����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lraccountstyle")) {
                if (mConditionField.trim().equals("1")) {
                    mSBql
                        .append("select code,codename from ldcode where codetype='accouttype' order by code");
                } else {
                    mSBql
                        .append("select code,codename from ldcode where codetype='accouttype' and othersign='"
                                + mConditionField + "' order by code");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lreport")) {
                if (mConditionField.trim().equals("1")) {
                    mSBql
                        .append("select code,codename from ldcode where codetype ='rireporttype' order by code ");
                } else {
                    mSBql
                        .append("select code,codename from ldcode where codetype ='rireporttype' and othersign='"
                                + mConditionField + "' order by code ");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ��������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrisktypeno")) {
                mSBql
                    .append(" select RIContNo, RIContName from RIBarGainInfo order by RIContNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���Ʒ����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrproduct")) {
                if (mConditionField.trim().equals("1")) {
                    mSBql
                        .append(" select riskcode,riskname from lmrisk where riskcode in (select riskcode From lmriskduty where dutycode in (select associatedcode from accumulaterdcode)) ");
                } else {
                    mSBql
                        .append("select riskcode,riskname from lmrisk where riskcode in (select riskcode From lmriskduty where dutycode  in  (select associatedcode from accumulaterdcode where accumulatedefno in (select accumulatedefno from RIPrecept where RIContNo ='"
                                + mConditionField + "')))");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ֱ�����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrriskcode")) {
                if (mConditionField.trim().equals("1")) {
                    mSBql
                        .append(" select riskcode,riskname from lmrisk where riskcode in (select riskcode From lmriskduty where dutycode in (select associatedcode from accumulaterdcode)) ");
                } else {
                    mSBql
                        .append("select riskcode,riskname from lmrisk where riskcode in (select riskcode From lmriskduty where dutycode in (select associatedcode from accumulaterdcode where accumulatedefno in (select accumulatedefno from RIPrecept where RIContNo='"
                                + mConditionField + "')))");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ�ͳ���ڼ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrstaterm")) {
                mSBql
                    .append(" select distinct substr(BatchNo,1,4)||'-'||to_char(trunc((to_number(substr(BatchNo,5,6))-1)/3)+1) A,substr(BatchNo,1,4)||'��'||to_char(trunc((to_number(substr(BatchNo,5,6))-1)/3)+1)||'����' B from ReserveCalLog where rownum <=24 order by A desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ������·�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrdistillserialno")) {
                mSBql
                    .append(" select A1,A2 from (select distinct BatchNo A1,substr(BatchNo,0,4)||'��'||substr(BatchNo,5)||'��' A2 from ReserveCalLog union select to_char(year(sysdate))||(case when month(sysdate)<10 then '0'||to_char(month(sysdate)) else to_char(month(sysdate)) end) A1,to_char(year(sysdate))||'��'||(case when month(sysdate)<10 then '0'||to_char(month(sysdate)) else to_char(month(sysdate)) end)||'��' A2 from dual union select to_char(year(trunc(add_months(last_day(sysdate)+1,-2),'dd')))||(case when month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))<10 then '0'||to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) else to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) end) A1 ,to_char(year(trunc(add_months(last_day(sysdate)+1,-2),'dd')))||'��'||(case when month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))<10 then '0'||to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) else to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) end)||'��' A2 from dual ) where rownum <=24 order by A1 desc ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ�ͳ���·�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrstaserialno")) {
                mSBql
                    .append(" select distinct BatchNo,substr(BatchNo,0,4)||'��'||substr(BatchNo,5)||'��' from RIDistillLog where rownum <=24 order by batchno desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���ϵ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lzrelationer")) {
                mSBql
                    .append("select relacode,relaname from ReInsuranceLinkManInfo order by relacode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���˾����
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lcompanycode")) {
                mSBql
                    .append("select ComPanyNo, ComPanyName from ReInsuranceComPanyInfo order by ComPanyNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ��������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lraccmucode")) {
                mSBql
                    .append("select AccumulateDefNO, AccumulateDefName from AccumulateDef order by AccumulateDefNO ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�������ֱ���Ϣ Ҫ������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrfactorname")) {
                mSBql
                    .append("select Factor, FactorName from RICalFactor where  Factor not in (select Factorcode from RICalFactorValue where ReContCode='"
                            + mConditionField
                            + "' and  RIPreceptNo='S000000000') ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���ͬ���ֱ���Ϣ Ҫ������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrfactor")) {
                mSBql
                    .append("select Factor, FactorName from RICalFactor where Factor not in(select Factorcode from RICalFactorValue where ReContCode='"
                            + mConditionField + "') ");

                mSQL = mSBql.toString();
                break SelectCode;
            }

            //�ٱ�ҵ���ʵ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrcompanysta")) {
                mSBql
                    .append("select companyno,companyname from ReInsuranceComPanyInfo");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ���ϸ�����ʵ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrcompanystarry")) {
                if (mConditionField.trim().equals("1")) {
                    mSBql
                        .append("select companyno,companyname from ReInsuranceComPanyInfo  where companyno<>'R000000001' order by companyno");
                } else {
                    mSBql
                        .append("select ComPanyNo,ComPanyName from ReInsuranceComPanyInfo where ComPanyNo in (select distinct a.incomecompanyno from IncomeCompany a where RIContNo='"
                                + mConditionField
                                + "' and IncomeCompanyNo<>'R000000001') ");
                }
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ٱ��ٷ��㷨
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrarithmetic")) {
                mSBql
                    .append("select arithmeticid,arithmeticname from RIMODEARITHMETIC where StandbyString3='03' order by arithmeticid");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ŵ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrgrpcontduty")) {
                mSBql
                    .append("select distinct b.dutycode,(select dutyname from lmduty where dutycode=b.dutycode) from lcpol a,lcduty b where a.polno=b.polno and a.grpcontno='"
                            + mConditionField
                            + "' and b.dutycode in (select distinct associatedcode from AccumulateRDCode) ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ŵ�������
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrgrpcontrisk")) {
                mSBql
                    .append(" select distinct a.riskcode,(select riskname from lmrisk where riskcode=a.riskcode) from lcpol a,lcduty b where a.polno=b.polno and a.grpcontno='"
                            + mConditionField
                            + "' and b.dutycode in (select distinct associatedcode from AccumulateRDCode)  ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //�ŵ��±��ϼƻ�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrgrpcontplan")) {
                mSBql
                    .append(" select distinct a.contplancode,(select ContPlanName from lccontplan where grpcontno=a.GrpContNo and ContPlanCode=a.ContPlanCode) from lcinsured a where exists (select * from lccont where a.contno=contno and grpcontno='"
                            + mConditionField + "') ");
                mSQL = mSBql.toString();
                System.out.println(" mSQL : " + mSQL);
                break SelectCode;
            }
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
        		"lrgrpcontplan1")) {
	        	mSBql.append("select ff.riskplancode ,ff.riskplanname  from lcrisktocontplan ff where grpcontno = '"+mConditionField
	        			+"'  and contplancode in(select distinct a.contplancode from lcinsured a where exists (select * from lccont where a.contno = contno and grpcontno = '"
	        			+mConditionField+"'))");
	            mSQL = mSBql.toString();
	            System.out.println(" mSQL : " + mSQL);
	            break SelectCode;
            }
            //��ѯһ��������λ������ʯ��
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "organcomcode_1")) {
                mSBql.append("select organcomcode,grpname from lcorgan where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and grouplevel='1' or organcomcode='0000' order by organcomcode");
                mSQL = mSBql.toString();
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }

            //��ѯ�������б�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "payeeName")) {
                mSBql.append("select BnfNo,Name from llbnf where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }

            //�������ֲ�ѯpersonrisk
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).toLowerCase()
                .compareTo("personrisk") == 0) {
                mSBql
                    .append("select a.RiskCode, a.RiskName,b.PolNo from LMRiskApp a,LCPol b where 1=1 and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql
                    .append(" and a.RiskCode = b.RiskCode order by a.RiskCode");
                mSQL = mSBql.toString();
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }

            //�뱾����ص���Ϣ   (9λ�� 'BnfLot'<==>'1')
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "RelationToPayee")) {
//                mSBql
//                    .append("select '00','�����˱���',Name,Sex,Birthday,IDType,IDNo,'00','1',CaseGetMode,BankAccNo,AccName,BankPrv,BankCity,BankInfo,BankCode from llbnf where ");
           //     mSBql.append(mConditionField);
            //    mSBql.append(" = ");
            //    mSBql.append(mCodeCondition);
                
                
                StringBuffer tBuffer = new StringBuffer(128);
                tBuffer.append(mConditionField);
                tBuffer.append(" = ");
                tBuffer.append(mCodeCondition);
                String strWhere = tBuffer.toString();
                String SQL = strWhere.substring(tBuffer.indexOf(" = ") + 4,
                    tBuffer.length() - 1);
                String[] str = SQL.split("\\|");

                String bnfno = str[0];
                String caseNo = str[1];
                
                mSQL = "select '00','�����˱���',Name,Sex,Birthday,IDType,IDNo,'00','1',CaseGetMode,BankAccNo,AccName,BankPrv,BankCity,BankInfo,BankCode,insuredno,'δȷ��' from llbnf where  "
                	+ " bnfno ='"+bnfno+"' and caseno='"+caseNo+"' ";
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }
            //�������˱�����ص���Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "RelationToBnf")) {
                StringBuffer tBuffer = new StringBuffer(128);
                ExeSQL exeSQL = new ExeSQL();
                tBuffer.append(mConditionField);
                tBuffer.append(" = ");
                tBuffer.append(mCodeCondition);
                String strWhere = tBuffer.toString();
                String SQL = strWhere.substring(tBuffer.indexOf(" = ") + 4,
                    tBuffer.length() - 1);
                String[] str = SQL.split("\\|");

                String contNo = str[0];
                String caseNo = str[1];
                String customerNo = str[2];
                String polNo = str[3];
                if ("".equals(contNo)) {
                    CError cError = new CError();
                    cError.moduleName = "CodeQueryBL";
                    cError.functionName = "RelationToBnf";
                    cError.errorMessage = "�������������ƣ�";
                    this.mErrors.addOneError(cError);
                    return false;
                }
                ReadTPABL readTPABL = new ReadTPABL();
                // ���ұ��˵�������ͱ������˱���
                String strC = "select BankAccNo from lcinsured where RelationToMainInsured = '00' and "
                        + " ContNo ='"
                        + contNo
                        + "' and insuredNo = '"
                        + customerNo
                        + "' union select BankAccNo from lbinsured where RelationToMainInsured = '00' and "
                        + " ContNo ='"
                        + contNo
                        + "' and insuredNo = '"
                        + customerNo + "'";
                SSRS tSSRSC = exeSQL.execSQL(strC);
                if (tSSRSC.MaxRow > 0) {
                    // �������˻���Ϣ
                    if (tSSRSC.GetText(1, 1) != null
                            && !"".equals(tSSRSC.GetText(1, 1))) {
                        mSQL = "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,5,d.accname,d.BankCode,d.BankAccNo,d.BankPrv,d.BankCity,d.BankInfo,'','1','1',d.RelationToMainInsured,'δȷ��' "
                                + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='"
                                + customerNo
                                + "' union "
                                + "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,5,d.accname,d.BankCode,d.BankAccNo,d.BankPrv,d.BankCity,d.BankInfo,'','1','1',d.RelationToMainInsured,'δȷ��'"
                                + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='" + customerNo + "'";
                        // û��������Ϣ�Ͱ�������Ϣ�ÿ�
                    } else {
                        mSQL = "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'δȷ��'"
                                + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='"
                                + customerNo
                                + "' union "
                                + "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'δȷ��'"
                                + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='" + customerNo + "'";
                    }
                    // ����ͨ�������������˲������������˵Ĺ���
                } else {
                    String strQueryC = "select MainInsuredNo,grpcontno from lcinsured where MainInsuredNo is not null and "
                            + " ContNo ='"
                            + contNo
                            + "' and insuredNo = '"
                            + customerNo + "'";
                    SSRS sSRSC = exeSQL.execSQL(strQueryC);
                    // C���д���
                    if (sSRSC.MaxRow > 0) {
                        LCInsuredSchema lcSchema = readTPABL
                            .getBankInfoOfInsured(sSRSC.GetText(1, 2), contNo,
                                sSRSC.GetText(1, 1), true);
                        // ��������Ϣ
                        if (lcSchema != null) {
                            String bankCode = lcSchema.getBankCode();
                            String bankAccNo = lcSchema.getBankAccNo();
                            String accName = lcSchema.getAccName();
                            String bankInfo = lcSchema.getBankInfo();
                            String bankPrv = lcSchema.getBankPrv();
                            String bankCity = lcSchema.getBankCity();
                            mSQL = "select '01','�����������ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,a.IDNo,'5',"
                                    + "'"
                                    + accName
                                    + "','"
                                    + bankCode
                                    + "','"
                                    + bankAccNo
                                    + "','"
                                    + bankPrv
                                    + "','"
                                    + bankCity
                                    + "','"
                                    + bankInfo
                                    + "',"
                                    + "'','1','1',d.RelationToMainInsured,'δȷ��' from llcase a, llcasepolicy c, LCInsured d where d.ContNo = c.ContNo and c.polno = '"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and a.CustomerNo = d.insuredno and a.CustomerNo ='"
                                    + customerNo + "'"
                                    //�������������˱��˵��ʻ�ѡ��
                                    +" union select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                    + "a.IDNo,'5',d.accname,d.bankcode,d.bankaccno,d.bankprv,d.bankcity,d.bankinfo,'','1','1',d.RelationToMainInsured,'δȷ��'"
                                    + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and d.InsuredNo ='" + customerNo + "' and d.bankaccno is not null ";
                            
                            // ��������Ϣ
                        } else {
                            mSQL = "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                    + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'δȷ��'"
                                    + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and d.InsuredNo ='" + customerNo + "'";
                        }
                        // B���д���
                    } else {
                        String strQueryB = "select MainInsuredNo,grpcontno from lbinsured where MainInsuredNo is not null and "
                                + " ContNo = '"
                                + contNo
                                + "' and insuredNo = '" + customerNo + "'";
                        SSRS sSRSB = exeSQL.execSQL(strQueryB);
                        if (sSRSB.MaxRow > 0) {
                            LCInsuredSchema lcSchema = readTPABL
                                .getBankInfoOfInsured(sSRSB.GetText(1, 2),
                                    contNo, sSRSB.GetText(1, 1), true);
                            // ��������Ϣ
                            if (lcSchema != null) {
                                String bankCode = lcSchema.getBankCode();
                                String bankAccNo = lcSchema.getBankAccNo();
                                String accName = lcSchema.getAccName();
                                String bankInfo = lcSchema.getBankInfo();
                                String bankPrv = lcSchema.getBankPrv();
                                String bankCity = lcSchema.getBankCity();
                                mSQL = "select '01','�����������ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,a.IDNo,'5',"
                                        + "'"
                                        + accName
                                        + "','"
                                        + bankCode
                                        + "','"
                                        + bankAccNo
                                        + "','"
                                        + bankPrv
                                        + "','"
                                        + bankCity
                                        + "','"
                                        + bankInfo
                                        + "',"
                                        + "'','1','1',d.RelationToMainInsured,'δȷ��' from llcase a, llcasepolicy c, LBInsured d where d.ContNo = c.ContNo and c.polno = '"
                                        + polNo
                                        + "' and a.caseno = c.caseno and c.ContNo = '"
                                        + contNo
                                        + "' and a.CaseNo = '"
                                        + caseNo
                                        + "' and a.CustomerNo = d.insuredno and a.CustomerNo ='"
                                        + customerNo + "'"
                                        //�������������˱��˵��ʻ�ѡ��
                                        + " union select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                        + "a.IDNo,'5',d.accname,d.bankcode,d.bankaccno,d.bankprv,d.bankcity,d.bankinfo,'','1','1',d.RelationToMainInsured,'δȷ��'"
                                        + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                        + polNo
                                        + "' and a.caseno = c.caseno and c.ContNo = '"
                                        + contNo
                                        + "' and a.CaseNo = '"
                                        + caseNo
                                        + "' and d.InsuredNo ='"
                                        + customerNo + "' and d.bankaccno is not null ";
                                // ��������Ϣ
                            } else {
                                mSQL = "select '00','�������˱����ʻ�','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                        + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'δȷ��'"
                                        + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                        + polNo
                                        + "' and a.caseno = c.caseno and c.ContNo = '"
                                        + contNo
                                        + "' and a.CaseNo = '"
                                        + caseNo
                                        + "' and d.InsuredNo ='"
                                        + customerNo + "'";
                            }
                        }
                    }
                }
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }
            //JiangZhen �������Ϣ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "AccToPayee")) {
				StringBuffer tBuffer = new StringBuffer(128);
				ExeSQL exeSQL = new ExeSQL();
				tBuffer.append(mConditionField);
				tBuffer.append(" = ");
				tBuffer.append(mCodeCondition);
				String strWhere = tBuffer.toString();
				String SQL = strWhere.substring(tBuffer.indexOf(" = ") + 4,
						tBuffer.length() - 1);
				String[] str = SQL.split("\\|");

				String bnfno = str[0];
				String customerNo = str[1];
				String caseNo = str[2];
				String name = str[3];
				// ���ұ��˵�������ͱ������˱���
				mSQL = "select distinct '1', '�ֽ�', '', '', '', '', '', '', '' from dual " 
						+ " union select distinct '3', '֧Ʊ', '', '', '', '', '', '', '' from dual " 
						+ " union select distinct '4', 'Ͷ���������˻�����ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+" union select distinct '4', 'Ͷ���������˻�����ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lborgan a,llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+" union select distinct '5', '��������ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, a.bankinfo, a.bankcode, a.polno " 
						    +" from lcpayee a, llcasepolicy c, llbnf d"
							+ " where a.polno = c.polno and c.polno = d.polno and d.caseno = c.caseno and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ "and a.bnfno = '"
							+ bnfno
							+ "' and a.name='" + name +"' "
						+ " union select distinct '5', '��������ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, a.bankinfo, a.bankcode, a.polno "
						    +" from llbnf a "
							+ " where a.CaseNo = '"
							+ caseNo
							+ "'  and a.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.name='" + name +"' and a.modireasondesc is null "
						+ " union select distinct '6', '��֧��������ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a,llcasepolicy c, LCInsured d " 
						    +" where d.ContNo = c.ContNo and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and d.organcomcode = a.organcomcode and a.grpcontno = c.grpcontno " 
						+" union select distinct '6', '��֧��������ת��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
							+" from lborgan a, llcasepolicy c, LCInsured d " 
							+" where d.ContNo = c.ContNo and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and d.organcomcode = a.organcomcode and a.grpcontno = c.grpcontno " 
						+" union select distinct '7', '�Ӽ�֧��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+"union select distinct '7', '�Ӽ�֧��', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lborgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
						//Amended By Fang for ASR20107305 ���ӡ��ѵ渶��֧����ʽ
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno"
						+" union select distinct '8', '�ѵ渶', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+"union select distinct '8', '�ѵ渶', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lborgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno"
						;
				        //Ended ASR20107305
				executeType = 1;
				System.out.println(mSQL);
				break SelectCode;
			}

            // guoxq add 2007/4/20
            // �����ο���
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "muldutyctrl")) {
                mSBql.append("select Code, CodeName from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype = '");
                mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType())
                    .toLowerCase());
                mSBql.append("' order by Code");
                mSQL = mSBql.toString();
                break SelectCode;
            }
     
            //end add
            //�ջ���Ŀ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llcasenotetype")) {
                mSBql.append("select Code, CodeName from ldcode where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" and codetype = 'llcasenotetype'");
                mSBql.append(" order by to_number(Code)");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //add by winnie ASR20107444 ���� ҵ������ ClientSource
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "clientsource")) {
            	mSBql.append("select Code, CodeName from ldcode where ");
            	mSBql.append(mConditionField);
            	mSBql.append(" = ");
            	mSBql.append(mCodeCondition);
            	mSBql.append(" and codetype = 'clientsource'");
            	mSBql.append(" order by to_number(Code)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //end add20101115
           	// add by winnie ASR20093243 ͳ�������������Ʒ�ʽ 20090325
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "prodistributetype")) {
            	mSBql.append("select Code, CodeName from ldcode where ");
            	mSBql.append(mConditionField);
            	mSBql.append(" = ");
            	mSBql.append(mCodeCondition);
            	mSBql.append(" and codetype = 'TKScaleType'");
            	mSBql.append(" order by to_number(Code)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //add by winnie ASR20093070 ��ȫ�ջṦ�� 
            //������б�
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "Quest")) {
            	mSBql.append("select code,cont from ldcodemod where ");
            	mSBql.append(mConditionField);
            	mSBql.append(" = ");
            	mSBql.append(mCodeCondition);
            	mSBql.append(" and codetype = 'Question'");
            	mSBql.append(" order by to_number(code)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            
            //ASR20110918 geb��ʶ
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "gebclient")) {
            	mSBql.append("select Code, CodeName from ldcode where ");
            	mSBql.append(mConditionField);
            	mSBql.append(" = ");
            	mSBql.append(mCodeCondition);
            	mSBql.append(" and codetype = 'GEBCode'");
            	mSBql.append(" order by to_number(Code)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            
            if(StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "IDType") && "1".equals(mGlobalInput.InsurerUserType)){
            	mSBql
                .append("select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where code in ('0','1','2','4','8') and codetype = '");
            mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType())
                .toLowerCase());
            mSBql.append("' and ");
            mSBql.append(mConditionField);
            mSBql.append(" = ");
            mSBql.append(mCodeCondition);
            mSBql.append(" order by Code");
            mSQL = mSBql.toString();
            break SelectCode;
            }
            
            //��ѯ�˲�����ȡʱ��(edit by Jiyacheng)
            if(StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "batchno")){
            	mSBql
            		 .append("select distinct 0,'ȫ��' from lpinvestigation union all select rownum,batchno from (select distinct batchno from lpinvestigation where ");
            	mSBql.append("investigation1 in ('0','1','2') and batchno is not null order by batchno desc)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            
            //end add 20090519
            // ����LDCODE���ж��������
            // if (mSQL.equals(""))
            // {
            mSBql
                .append("select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where codetype = '");
            mSBql.append(StrTool.cTrim(mLDCodeSchema.getCodeType())
                .toLowerCase());
            mSBql.append("' and ");
            mSBql.append(mConditionField);
            mSBql.append(" = ");
            mSBql.append(mCodeCondition);
            mSBql.append(" order by Code");
            mSQL = mSBql.toString();
            executeType = 1;
            break SelectCode;
            // }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ԭ���ϲ����������ط���");
        }
        // �������SelectCode�����󣬿�ʼ������ĳ���

        // showcodelist��ѯ���������ʱʹ�ã��ȶ����ע�ͣ����ڵ��õ���ExeSql�����Բ���Ҫ���������
        // System.out.println("CodeQueryBL:" + mSQL);
        if (mSQL.equals("")) {
            System.out.println("��ѯ���Ϊ�գ���鿴codequerybl�е�����" + mSBql.toString());
            return false;
        }
        if (executeType == 0) {
            // ʹ�ý�ȡ��ʽ��ѯ
            mResultStr = mExeSQL.getEncodedResult(mSQL, 1);
        } else {
            // Ŀǰֻ����ֽ�ȡ��ѯ��ȫ����ѯ
            // else if (executeType == 1)
            // ȫ�����ݲ�ѯ
            // ���������ڼ�ǧ���ҵ�ʱ�򣬻���һ��ȡ����Ч�ʸ�Щ
            mResultStr = mExeSQL.getEncodedResult(mSQL);
        }

        if (mExeSQL.mErrors.needDealError()) {
            // @@������,��ExeSQL���ѽ��д���������ֱ�ӷ��ؼ��ɡ�
            this.mErrors.copyAllErrors(mExeSQL.mErrors);
            // ���sqlִ�д����򷵻�sql���������ڵ���exesql��ִ�У����Ա�Ȼ���г���sql���
            // System.out.println("Code Query Error Sql:" + mSQL);
        }
        mResult.clear();
        mResult.add(mResultStr);
        return true;
    }

    /**
     * ���Ժ���
     *
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        // long l = System.currentTimeMillis();
        // for (int i = 1; i < 60; i++)
        // {
        // CodeQueryBL tCodeQueryBL = new CodeQueryBL();
        // VData tData = new VData();
        // LDCodeSchema tLDCodeSchema = new LDCodeSchema();
        // tLDCodeSchema.setCodeType("station");
        // GlobalInput tGlobalInput = new GlobalInput();
        // tGlobalInput.ManageCom = "86";
        // tData.add(tLDCodeSchema);
        // tData.add(tGlobalInput);
        // TransferData tTransferData = new TransferData();
        // tData.add(tTransferData);
        // tCodeQueryBL.submitData(tData, "QUERY||MAIN");
        // }
        // System.out.println(System.currentTimeMillis() - l);
        // tData = tCodeQueryBL.getResult();
        // String tStr = "";
        // tStr = (String) tData.getObject(0);
        // tStr = StrTool.unicodeToGBK(tStr);
        // System.out.println(tStr);
        // System.out.println("result:" + String.valueOf(System.currentTimeMillis() - l));
    }
}
