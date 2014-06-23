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
 * Title: Web业务系统
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
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mResult = new VData();

    /** 存储查询语句 */
    private String mSQL = "";

    private StringBuffer mSBql = new StringBuffer(128);

    /** 存储全局变量 */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** 存储查询条件 */
    private String mCodeCondition = "";

    private String mConditionField = "";

    /** 业务处理相关变量 */
    private LDCodeSchema mLDCodeSchema = new LDCodeSchema();

    private ExeSQL mExeSQL = new ExeSQL();

    /** 返回的数据 */
    private String mResultStr = "";

    public CodeQueryBL() {
    }
    

    /**
     * 传输数据的公共方法, 本处理没有后续的BLS层，故该方法无用
     *
     * @param cInputData
     *            VData
     * @param cOperate
     *            String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate) {
        // 得到外部传入的数据,将数据备份到本类中
        if (getInputData(cInputData)) {
            // 进行业务处理
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
     * 数据输出方法，供外界获取数据处理结果
     *
     * @return 包含有数据查询结果字符串的VData对象
     */
    public VData getResult() {
        return mResult;
    }

    /**
     * 从输入数据中得到所有对象 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     *
     * @param cInputData
     *            VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData) {
        // 代码查询条件
        try {
            // 外部传入codetype类型
            mLDCodeSchema.setSchema((LDCodeSchema) cInputData.get(0));
            // 查看传入的session信息
            try {
                mGlobalInput.setSchema((GlobalInput) cInputData.get(1));
            } catch (Exception e) {
                mGlobalInput.ComCode = "";
                mGlobalInput.ManageCom = "";
                mGlobalInput.Operator = "";
            }
            // 获取其他传入的查询信息（查询域、查询值）
            TransferData tTransferData = (TransferData) cInputData.get(2);

            // 得到查询值
            mCodeCondition = (String) tTransferData
                .getValueByName("codeCondition");
            // 如果查询值中包含#号，则替换#号为'号，主要是jsp中传递'分会异常
            if (mCodeCondition.indexOf('#') == -1) {
                StringBuffer tSBql = new StringBuffer();
                tSBql.append("'");
                tSBql.append(mCodeCondition);
                tSBql.append("'");
                mCodeCondition = tSBql.toString();
            } else {
                mCodeCondition = mCodeCondition.replace('#', '\'');
            }
            // 得到查询域
            mConditionField = (String) tTransferData
                .getValueByName("conditionField");
            // 如果查询域为空，则查询域和查询值强制设为1
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
     * 查询符合条件的保单 输出：如果准备数据时发生错误则返回false,否则返回true
     *
     * @return boolean 目前还不晓得equalsIgnoreCase和toLowerCase两个那个好 使用60000次比较，发现equalsIgnoreCase方法明显比toLowerCase要快
     */
    private boolean queryData() {
        mSQL = "";
        int executeType = 0;

        // 如果管理机构为空，则默认设置为86
        if (mGlobalInput.ManageCom == null
                || mGlobalInput.ManageCom.trim().equals("")) {
            mGlobalInput.ManageCom = "86";
        }

        // 做标签设计，以便下面的判定能够及时跳出
        SelectCode: try {
            // 这里输出的是双击查询的类型
            // System.out.println("come here queryDataType is " + mLDCodeSchema.getCodeType());

            // 查询用户关联保单
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "relagrpcont")) {
                mSBql
                    .append("select distinct prtno,grpcontno,riskcode from lcgrppol where appflag='1' and riskcode='212401'");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 查询缴费规则
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
            //查询子合同号码包含全部 Tracy Add
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub1")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub'  and code<>'0'  union select "
								+ "'1','全部'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            
			// 查询子合同号码包含全部，企业负担 Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub2")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub'   union select "
								+ "'1','全部'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
			
			// 查询子合同号码包含股份的子合同号和全部 liujun Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContNoSub3")) {
				mSBql
						.append("select a.code, a.codename codename from ldcode a where "
								+ "a.codetype = 'contnosub' and a.code not in ('0','00000000')  union select "
								+ "'1','全部'  codename from dual   order by codename ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            // 查询负担资金类型，按照年金中心要求 Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContFlag1")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'contflag'  union select '4' ordernum ,"
								+ "'全部'  from dual union select '3' ordernum ,'集团股份' "
								+ "  from dual  order by ordernum  ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
			 //查询负担资金类型，针对过渡性企业年金人员
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"ContFlag2")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'contflag' and a.code<>'2'  union select '4' ordernum ,"
								+ "'集团股份'  from dual   order by ordernum  ");

				mSQL = mSBql.toString();
				break SelectCode;
			}
   //专门针对年金领取下的负担资金类型而写的控件
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
			"ContFlag3")) {
		mSBql
				.append("select a.code ordernum , a.codename  from ldcode a where "
						+ "a.codetype = 'contflag' and a.code<>'2'  union select '3' ordernum ,"
						+ "'集团股份'  from dual   order by ordernum  ");

		mSQL = mSBql.toString();
		break SelectCode;
	}

			// 查询上市类型 Tracy Add
			if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
					"OrganType1")) {
				mSBql
						.append("select a.code ordernum , a.codename  from ldcode a where "
								+ "a.codetype = 'OrganType'  union select '3' ordernum ,"
								+ "'全部'  from dual order by ordernum  ");
				mSQL = mSBql.toString();
				break SelectCode;
			}
            // 查询付款银行编码 Tracy Add
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

            //查询收款银行编码 Tracy Add
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

            //查询分支机构 Tracy Add
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

            // 查询归属规则
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
            // 团单险种查询交费间隔payintv
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

            // 民族
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

            // 婚姻状态
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

            // 销售银代、团险品质管理 奖罚细则
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
            // 查询工作单位编码和名称(guoly 20070520edit)
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
            // 发放机构代码
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
            // 发放机构代码
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
            // 发放标准
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
            //发放代码
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

            // 出纳员查询
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
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }

            // 操作员查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "usercode")) {
                mSBql.append("select Usercode,Username from LDUser where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }
            
            // 操作员查询,根据usercode模糊查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "userbycode")) {
                mSBql.append("select Usercode,Username from LDUser where ");
                mSBql.append(mConditionField);
                mSBql.append(" like '");
                mSBql.append(mCodeCondition);
                mSBql.append("%%'");
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }
            
            // 流转部门查询，根据操作员所属部门查询可流转部门
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "deptcode")) {
                mSBql.append("select code1,(select codename from ldcode where codetype='zllzzl' and code=ldcode1.code1) from ldcode1 where codetype='zllzzl' and code=(select deptcode from lduser where  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" )");
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }
            // 流转部门查询，查询所有部门
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "alldeptcode")) {
                mSBql.append("select code,codename from ldcode where codetype='zllzzl' and  ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }
            // 资料流转组别查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "zllzzb")) {
                mSBql.append("select code1,codealias from  ldcode1 where codetype='zllzzb' and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }

            // 菜单查询
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
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }

            // 岗位查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "menugrp")) {
                mSBql
                    .append("select menugrpcode,menugrpname from ldmenugrp order by makedate,maketime ");
                executeType = 1;
                mSQL = mSBql.toString();
                // 一旦满足查询条件后，就跳出
                break SelectCode;
            }

            // 咨询专家
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

            // 重大疾病信息查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "seriousdiseas")) {
                mSQL = "select Name,Code,Typedesc, Description from LLMSerialsDiease order by Code";
                break SelectCode;
            }
            //mdy           
//          重大疾病信息查询
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
            
            // 认证级别
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
            // 卫生机构类别查询
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
            // 单位隶属关系查询
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
            // 业务类型代码查询
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

            /** *******************理赔相关查询 */
            // 理赔权限查询
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

            // 理赔检录中要实现一个手术的模糊查询
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
            // 理赔合同查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llcont")) {
                mSBql.append("select distinct contno,'' from lcinsured where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 理赔险种查询
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
                System.out.println("险种查询:" + mSQL);
                break SelectCode;
            }
            // 影像归类当中要使用的, busstype(业务类型)
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "BussType")) {
                mSBql
                    .append("select distinct BussType,BussTypename from es_doc_def ");
                mSBql.append(" order by BussType");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 影像归类当中要使用的, subtype(单证细类)
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
            // 出险疾病查询
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
            // 问题件返回对象查询
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
            //打印标记
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "NeedPrint")) {
            	mSBql.append("select CodeName,Code from ldcode where 1=1 and ");
            	mSBql.append("codetype='NeedPrint'");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //问题件状态
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "State")) {
            	mSBql.append("select CodeName,Code from ldcode where ");
            	mSBql.append("codetype='State'");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            //end add 20091104
            // 理赔查询
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
            // 理赔材料名称
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
            // 理赔材料类型
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
            // 理赔保单查询
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
            // 险种查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llclaimrisk")) {
                mSBql.append("select riskcode,riskname from lmrisk");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //责任查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrduty")) {
                mSBql.append("select dutycode,dutyname from lmduty");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            
            //根据险种查询责任
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
            
            // 责任给付类别
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
            // 给付类型
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llclaimdecision_1")) {
                mSBql
                    .append("select a.Code, a.CodeName, a.CodeAlias, a.ComCode, a.OtherSign from ldcode a where  trim(a.codetype)=(select trim(b.codeaLias) from ldcode b where b.codetype='llclaimdecision' and b.code=");
                mSBql.append(mCodeCondition);
                mSBql.append(")");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 告知版别总查询，不是很好
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impartver")) {
                mSBql
                    .append("select Code, CodeName, CodeAlias, ComCode, OtherSign from ldcode where codetype like '%impartver%'");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //伤残等级
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "disability")) {
                mSQL = " select code,codename from ldcode where codetype='disability' ";
                break SelectCode;
            }
            // 其它录入要素类型
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "llfactor")) {
                mSBql
                    .append("select a.codename, a.code from ldcode a where trim(a.codetype) =( select CODEALIAS from ldcode where codetype='llotherfactor' and code=");
                mSBql.append(mCodeCondition);
                mSBql.append(") order by a.code");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 核保师编码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "uwusercode")) {
                mSQL = "select UserCode, trim(UserName) from LDUser where cropcode is null  order by UserCode";
                executeType = 1;
                break SelectCode;
            }

            // 团单客户编码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "supcustomerno")) {
                mSQL = "select CustomerNo, trim(GrpName) from  LDGrp order by CustomerNo";
                break SelectCode;
            }

            // 健康险要素目标编码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "healthfactoryno")) {

                if (mCodeCondition.substring(1, 7).equals("000000")) {
                    // 基于保单的计算
                    mSQL = "select '__','请录入保单号' from dual";
                } else if (mCodeCondition.substring(1, 7).equals("000001")) {
                    // 基于保单的计算
                    mSBql
                        .append("select DutyCode,DutyName from LMDuty where DutyCode in(select DutyCode from LMRiskDuty where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by DutyCode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000002")) {
                    // 基于给付的计算
                    mSBql
                        .append("select getdutycode,getdutyname from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000003")) {
                    // 基于账户的计算
                    mSBql
                        .append("select insuaccno,insuaccname from LMRiskToAcc where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by insuaccno");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000004")) {
                    // 基于理赔责任的计算
                    mSBql
                        .append("select getdutycode,getdutyname from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }
            // 疾病代码查询ICDCode
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
            // 疾病代码查询ICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "diseascode")) {
                mSQL = "select icdcode, icdname from lddisease order by a.icdcode";
                break SelectCode;
            }

            // 疾病代码查询ICDCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "diseasname")) {
                mSQL = "select icdname,icdcode from lddisease order by icdname";
                break SelectCode;
            }

            // 医院代码，医院名称
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
            //医院代码，医院名称，级别
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospitallevel")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code,(select codename from ldcode where codetype='llhospitaltype' and code=a.BusiTypeCode)||decode((select codename from ldcode where codetype='levelcode' and code=a.levelcode ),null,'未评级','','未评级',(select codename from ldcode where codetype='levelcode' and code=a.levelcode )),a.LevelCode from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append("order by a.hospitcode");
                mSQL = mSBql.toString();
                //  executeType = 1;
                break SelectCode;
            }

            // 医院模糊查询
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
             // 医院模糊查询,增加医院等级
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "hospitlevel")) {
                mSBql
                    .append("select a.hospitcode, a.hospitname,b.codename,b.code,(select codename from ldcode where codetype='llhospitaltype' and code=a.BusiTypeCode)||decode((select codename from ldcode where codetype='levelcode' and code=a.levelcode ),null,'未评级','','未评级',(select codename from ldcode where codetype='levelcode' and code=a.levelcode )) from  LDHospital a ,ldcode b where b.codetype='llhospiflag' and trim(b.code)=trim(a.fixflag) and ");
                mSBql.append(mConditionField);
                mSBql.append(" like '%");
                mSBql.append(mCodeCondition.substring(1,
                    (mCodeCondition.length() - 1)).trim());
                mSBql.append("%' order by a.hospitcode");
                mSQL = mSBql.toString();
               // executeType = 1;
                break SelectCode;
            }

            // 意外代码查询ICDCode
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
            // 转分机构查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "SwitchCompany")) {
                mSQL = "select a.comcode,a.name from ldcom a where length(trim(comcode)) =4 order by comcode";
                break SelectCode;
            }
            // 转分人员查询
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
            // 推荐医院模糊查询
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

            // 医生代码
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

            // 健康险计算要素编码
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

            // 保监会管理机构信息
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "itemcode")) {
                mSQL = "select itemcode,trim(ItemName) from lfItemRela order by itemcode";
                executeType = 1;
                break SelectCode;
            }
            // 保监会管理机构信息
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "outitemcode")) {
                mSQL = "select outitemcode,trim(ItemName) from lfItemRela order by outitemcode";
                executeType = 1;
                break SelectCode;
            }
            // 地区编码查询
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
            // 保监会管理机构信息
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comcodeisc")) {
                mSQL = "select comcodeisc,trim(name) from LFComISC order by comcodeisc";
                executeType = 1;
                break SelectCode;
            }
            // 保监会管理机构信息 2005-10-13 huanglei
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comtoisccom")) {
                mSQL = "select distinct(LFComISC.comcodeisc),trim(LFComISC.name) from LFComISC ,lfcomtoisccom "
                        + "where LFComISC.Comcodeisc=lfcomtoisccom.comcodeisc and lfcomtoisccom.comcode like '"
                        + mGlobalInput.ManageCom + "%' order by comcodeisc";
                executeType = 1;
                break SelectCode;
            }

            // 保单状态导致原因PolState
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "polstate2")) {
                mSQL = "select code,codename,codealias from ldcode where codetype = 'polstate' order by code";
                break SelectCode;
            }
            // 责任领取类型DutyKind
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
            // 保全项目EdorType
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
            // 操作岗位OperateType
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
            // 核保上报级别UWPopedomCode
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
            // 核保上报级别UWPopedomCode1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "uwpopedomcode1")) {
                mSBql
                    .append("select usercode, username from lduser where usercode = (select UpUserCode from LDUWUser where usercode = '");
                mSBql.append(mGlobalInput.Operator.trim());
                mSBql.append("') order by usercode");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 银行分行渠道channel
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
            // 工种代码引用StaticGroup
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
            // 工种代码引用Depart
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

            // 引用BranchAttr
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

            // 代理人组别引用BranchCode
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

            // 代理人组别引用BranchCode
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

            // 员工属性引用BranchCodeType
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

            // 代理人组别引用HealthCode
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

            // 查询品质管理项目定义表 - xijiahui-申请
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

            // 查询品质管理项目定义表 - xijiahui-申请
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

            // 奖惩项目定义表 - xijiahui-申请
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

            // 奖惩项目定义表 - xijiahui-申请
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

            // 体检录入引用newHealthCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "newHealthCode")) {
                mSBql
                    .append("select code, codename from ldcode where Codetype='newhealthcode' ");
                mSBql.append(" order by to_number(code)");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 个险契调引用RReportCode
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

            // 团险契调引用RReportCode
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

            // 代理人组别引用AgentGroup
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

            // 代理人组别引用AgentGroup
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lcompanycodequery")) {
                mSBql
                    .append("select ComPanyNo,ComPanyName from reinsurancecompanyinfo order by companyno ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 退保类型引用EdorCode
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

            // 代理机构引用AgentCom
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

            // 险种编码引用RiskCode
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

//          险种编码引用riskenshortname
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
            
            // 险种编码引用RiskCode1
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).toLowerCase()
                .equalsIgnoreCase("riskcode1")) {
                mSQL = "select a.RiskCode, a.RiskName,b.SubRiskFlag,b.SubRiskFlag from LMRisk a,LMRiskApp b where a.RiskCode=b.RiskCode order by a.RiskCode";
                break SelectCode;
            }

            // 险种版本引用RiskVersion
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

            // 客户指南编码查询，根据指南类型查询
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

            // 机构编码引用ComCode
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
            // 资金管理平台引用ComCode  add by Bright
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
            //在综合查询-->投保信息查询/保单明细查询-->被保人清单显示中增加针对对保单号的服务机构.BUG:12563
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
                //System.out.println("查询服务机构："+mSQL);
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

            // 机构编码引用ComCodeAll
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

            // 2006.11.14 统括保单处理中的服务机构引用ComCodeAll
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

            // 银行险编码引用Riskbank
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

            // 团险编码引用RiskGrp
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

            // 个险编码引用RiskInd
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

            // 普通单证编码引用CertifyCode
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

            // 定额单证编码引用CardCode
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

            // 系统单证编码引用SysCertCode
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

            // 帐户查询InsuAccNo
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

            // 投连帐户查询 tl_insuaccno
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

            // 告知编码引用ImpartCode
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

            // 管理机构编码引用Station，仍然在使用
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
            // 管理机构编码引用Station，仍然在使用
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
            // 管理机构编码引用Station_logon，仅应用于登陆页面的下拉框
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
            // 工种代码引用OccupationCode
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

            // 交费方式代码引用PayYears
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

            // 团单险种查询GrpRisk
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

            // 团单险种查询GrpMainRisk主险
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

            //团单险种查询GrpMainRisk主险 GrpMainRisk1 套餐定制主险必须在ldplanrisk表中取得， cyj
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

            // 保险套餐RiskPlan
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskplan")) {
                mSQL = "select ContPlanCode,ContPlanName from LDPlan order by ContPlanCode";
                break SelectCode;
            }

            // 团单险种缴费规则查询RiskRuleFactoryType，Type编码默认为000005
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

            // 团单险种归属规则查询RiskAscriptionRuleFactoryType，Type编码默认为000006
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

            // 团单险种缴费规则查询RiskRuleFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskrulefactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000005")) {
                    // 没有加入任何限制条件，以后扩展
                    mSBql
                        .append("select PayPlanCode,PayPlanName from LMDutyPay where payplancode in (select payplancode from lmdutypayrela where dutycode in (select dutycode from lmriskduty where riskcode = '");
                    mSBql.append(mCodeCondition.substring(7, 12));
                    mSBql.append("'))");
                    mSQL = mSBql.toString();
                }
                break SelectCode;
            }

            // 团单险种归属规则查询RiskRuleFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "riskascriptionrulefactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000006")) {
                    // 没有加入任何限制条件，以后扩展
                    mSBql
                        .append("select PayPlanCode,PayPlanName,'' from LMDutyPay where AccPayClass in('4','7','8')"
                                + " and payplancode in (select payplancode from lmdutypayrela where dutycode"
                                + " in (select dutycode from lmriskduty where riskcode = '");
                    //针对险种是4位编码的情况。 20070528
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
            // 团单险种缴费规则查询RiskRuleFactory
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

            // 团单险种归属规则查询RiskRuleFactory
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

            // 团单保险计划下险种查询ImpRiskCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impriskcode")) {
                String[] a = new String[2];//用于记录保障层级和GrpContNo a[0]是保障层级 a[1]是GrpContNo
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
                //增加套餐的
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
             //团单保险计划下险种查询可以设置分段理算赔付比例的ImpRiskCode
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impllriskrate")) {
                String[] a = new String[2];//用于记录保障层级和GrpContNo a[0]是保障层级 a[1]是GrpContNo
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
            //团单保险计划下险种查询可以设置分段理算赔付比例的ImpRiskCode，保全用
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impllriskrateedor")) {
                String[] a = new String[4];//用于记录保障层级和GrpContNo a[0]是保障层级 a[1]是GrpContNo
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
            //团单保险计划下险种查询ImpRiskCode 保全版
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "impriskcodeedor")) {
                String[] a = new String[4];//用于记录保障层级和GrpContNo a[0]是保障层级 a[1]是GrpContNo a[2]edorno a[3] edortype
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
            // 团单保险计划下险种对应要素类别ImpFactoryType
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
                // 利用标签跳出操作
                break SelectCode;
            }
            // 团单保险计划下险种对应要素目标编码ImHealthFactoryNo
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imhealthfactoryno")) {
                if (mCodeCondition.substring(1, 7).equals("000000")) {
                    // 基于保单的计算
                    mSQL = "select '__','请录入保单号' from dual";
                } else if (mCodeCondition.substring(1, 7).equals("000001")) {
                    // 基于保单的计算
                    mSBql
                        .append("select DutyCode,DutyName,getdutycode||'"
                                + mCodeCondition.substring(7)
                                + "' from LMDuty where DutyCode in(select DutyCode from LMRiskDuty where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by DutyCode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000002")) {
                    // 基于给付的计算
                    mSBql
                        .append("select getdutycode,getdutyname ,getdutycode||'"
                                + mCodeCondition.substring(7)
                                + "' from lmdutygetrela where dutycode in (select dutycode from lmriskduty where riskcode ='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(") order by getdutycode");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000003")) {
                    // 基于账户的计算
                    mSBql.append("select insuaccno,insuaccname ,getdutycode||'"
                            + mCodeCondition.substring(7)
                            + "' from LMRiskToAcc where RiskCode='");
                    mSBql.append(mCodeCondition.substring(7));
                    mSBql.append(" order by insuaccno");
                    mSQL = mSBql.toString();
                } else if (mCodeCondition.substring(1, 7).equals("000004")) {
                    // 基于理赔责任的计算
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
            
            // 团单保险计划下险种对应给付责任代码ImGetDutyCode,分段理算用
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imgetdutycode")) {
            	
	            	 String[] a = new String[3];//用于记录保障层级和GrpContNo 
	                 StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
	                 for (int i = 0; st.hasMoreTokens(); i++) {
	                     a[i] = st.nextToken();
	                 }
                    // 基于理赔责任的计算
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
            // 团单保险计划下险种对应给付责任代码ImGetDutyCode,保全,分段理算用
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "imgetdutycodeedor")) {
            	
	            	 String[] a = new String[5];//用于记录保障层级和GrpContNo 
	                 StringTokenizer st = new StringTokenizer(mCodeCondition, "|");
	                 for (int i = 0; st.hasMoreTokens(); i++) {
	                     a[i] = st.nextToken();
	                 }
                    // 基于理赔责任的计算
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
            // 团单保险计划下险种对应要素计算编码ImHealthFactory
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

            // 团单保险计划下险种对应要素计算编码ImHealthFactory2,保障层级要约录入用 2007.5.8
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

            // 代理人编码引用AgentCode
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

            // 代理人编码引用AgentCode2 --liujw
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

            // 查找管理机构对应的离职手续定义 ---杨腾
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "dimissionitemdef")) {
                mSBql
                    .append("select ProcedureNo,ProcedureName,ProcLayer,ProcKind,(select codename from ldcode ");
                mSBql
                    .append(" where codetype='prockind' and code=ProcKind) from LDDimissionItemDef where proclayer = '1' and ");
                mSBql.append(mConditionField); // 管理机构
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by ProcedureNo");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "assessagentcode")) {
                // 考核特殊处理
                mSBql
                    .append("select a.AgentCode,g.Name,a.agentgrade1,CASE WHEN trim(a.agentgrade1)='00' THEN '清退' ELSE (select gradename from laagentgrade where gradecode=a.agentgrade1) END,b.name,c.name,a.agentgrade,f.gradename from LAAssess a,labranchgroup b,ldcom c,laagentgrade f,laagent g");
                mSBql
                    .append(" where b.agentGroup=a.agentgroup and c.comcode=a.managecom and f.gradecode=a.agentgrade and a.agentcode=g.agentcode and ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by a.AgentCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 培训菜单查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "trainmenu")) {
                mSBql
                    .append("select NodeKey,NodeName from LDMenu where NodeKey like 'Tr%' and ");
                mSBql.append(mConditionField); // 管理机构
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by NodeKey");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 培训教材查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "bookcode")) {
                mSBql
                    .append("select BookCode,Name,BookType,BookIntro from LABook where ");
                mSBql.append(mConditionField); // 管理机构
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by BookCode");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 员工制待遇级别查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "employeegrade")) {
                mSQL = "select gradecode,gradename from laagentgrade where GradeProperty6 = '1' order by gradecode";
                break SelectCode;
            }

            // 员工制待遇级别查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "employeegrade2")) {
                mSBql
                    .append("select lawelfareradix.agentgrade,ldcode.codename,100 from lawelfareradix ,ldcode,laagentgrade where  ldcode.codetype = 'employeeaclass' and ldcode.code = lawelfareradix.aclass and lawelfareradix.branchtype = '1' and laagentgrade.gradecode = lawelfareradix.agentgrade and laagentgrade.gradeproperty6 = '1' and lawelfareradix.aclass = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by lawelfareradix.agentgrade");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 个单合同无扫描件录入账号查询
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "accnum")) {
                mSBql.append("select BankAccNo,AccName from LCAccount where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            // 用户地址代码条件查询
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
            // 团体用户地址代码条件查询
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
            // 团单险种查询交费间隔payintv
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
            // 查询险种代码
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

            // 查询给付类型
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
            // 机构级别 branchlevel
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

            // 工单管理
            // 小组机构信息编码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "acceptcom")) {
                mSQL = "select GroupNo, GroupName from LGGroup order by GroupNo";
                break SelectCode;
            }

            // 业务分类编号
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "tasktoptypeno")) {
                // 顶级分类
                mSQL = "select WorkTypeNo, WorkTypeName from LGWorkType where SuperTypeNo = '00' order by WorkTypeNo ";
                break SelectCode;
            }

            // 业务分类编号
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

            // 人员编码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "taskmemberno")) {
                mSQL = "select UserCode, UserName from LDUser order by UserCode";
                break SelectCode;
            }

            // xjh Add,2005/02/24
            // 特殊险种 SpecRisk
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "specrisk")) {
                mSQL = "select riskcode,riskname from lmriskapp ";
                break SelectCode;
            }

            // 团单客户服务需求
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
                String a = mCodeCondition.substring(0, c); // 保险计划编码
                String b = mCodeCondition.substring(c + 1); // 合同号
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

            // 查找管理机构对应得基本法地区类型
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "comtoareatype")) {

                mSBql
                    .append("select distinct code2,rtrim(code2)||'类地区' from ldcoderela a where relatype = 'comtoareatype' and  ");
                mSBql.append(mConditionField); // 管理机构
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSBql.append(" order by code2");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            // 团险查找渠道经理
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "getagentmanager")) {

                mSBql
                    .append("select a.agentcode,a.name from laagent a,latree b where a.agentcode=b.agentcode and (b.agentgrade like 'J%' or b.agentgrade like 'K%')");

                mSQL = mSBql.toString();

                break SelectCode;
            }

            // 团险查询行业类别
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "businesstype")) {

                mSBql
                    .append("select code,codename From ldcode where codetype='businesstype' and ");
                mSBql.append(mConditionField); // 管理机构
                mSBql.append("  like '%");
                if (!mConditionField.equals("1")) {
                    // mCodeCondition = mCodeCondition; //如果传入mConditionField为空的时候会强制置为 1 此处如果为1 就不再Substring了 否则会报错。
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

            // 查询客户银行编码信息
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

            // 查询银行编码信息 理赔专用
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

            //          查询客户银行编码信息
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

            // 续期银行编码信息查询
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

            // 查询保险公司银行账户信息
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
            // 查询财务人员现金库信息
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
            // 查询契撤，解约，协议解约，减保原因
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

            // 查询保险公司开户银行信息
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

            // 查询中意年金险银行支付文件信息
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

            // 航意险代理机构查询
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

            // 航意险__代理机构分公司查询
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

            // 航意险__险种信息 查询
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

            // 理赔立案_险种信息 查询
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

            //理赔数据下载_险种信息查询（有保单号时加保单号的条件，没有时直接查险种编码）
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

            // 理赔立案_分支机构 查询
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

            // 财务接口_财务数据录入_财务科目选择
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "liaccount")) {
                mSBql
                    .append("select AccountCode,AccountName from liaccountconfig where ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                System.out.println(mSQL);
                break SelectCode;
            }

            // 保全项目
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "njedorcode")) {
                mSBql.append("select edorcode,edorname from lmedoritem where ");
                mSBql.append(mConditionField);
                mSBql.append(" = ");
                mSBql.append(mCodeCondition);
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //liujun 2010-04-14增加过渡年金保全编号
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
            //liujun 2006年10月
            //机构关系1
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
            //机构关系2
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
            //机构关系3
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
            //机构关系4
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

            //查询一级发放机构1
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
            //查询一级发放机构2
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
            //再保合同名称
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrcontno")) {
                mSBql
                    .append("select RIContNo, RIContName from RIBarGainInfo order by RIContNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }

            //再保业务帐单类型
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
            //再保报表类型
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
            //再保风险类别
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrisktypeno")) {
                mSBql
                    .append(" select RIContNo, RIContName from RIBarGainInfo order by RIContNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保产品名称
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
            //分保险种
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
            //再保统计期间
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrstaterm")) {
                mSBql
                    .append(" select distinct substr(BatchNo,1,4)||'-'||to_char(trunc((to_number(substr(BatchNo,5,6))-1)/3)+1) A,substr(BatchNo,1,4)||'年'||to_char(trunc((to_number(substr(BatchNo,5,6))-1)/3)+1)||'季度' B from ReserveCalLog where rownum <=24 order by A desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保提数月份
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrdistillserialno")) {
                mSBql
                    .append(" select A1,A2 from (select distinct BatchNo A1,substr(BatchNo,0,4)||'年'||substr(BatchNo,5)||'月' A2 from ReserveCalLog union select to_char(year(sysdate))||(case when month(sysdate)<10 then '0'||to_char(month(sysdate)) else to_char(month(sysdate)) end) A1,to_char(year(sysdate))||'年'||(case when month(sysdate)<10 then '0'||to_char(month(sysdate)) else to_char(month(sysdate)) end)||'月' A2 from dual union select to_char(year(trunc(add_months(last_day(sysdate)+1,-2),'dd')))||(case when month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))<10 then '0'||to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) else to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) end) A1 ,to_char(year(trunc(add_months(last_day(sysdate)+1,-2),'dd')))||'年'||(case when month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))<10 then '0'||to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) else to_char(month(trunc(add_months(last_day(sysdate)+1,-2),'dd'))) end)||'月' A2 from dual ) where rownum <=24 order by A1 desc ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保统计月份
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrstaserialno")) {
                mSBql
                    .append(" select distinct BatchNo,substr(BatchNo,0,4)||'年'||substr(BatchNo,5)||'月' from RIDistillLog where rownum <=24 order by batchno desc");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保联系人
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lzrelationer")) {
                mSBql
                    .append("select relacode,relaname from ReInsuranceLinkManInfo order by relacode ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保公司代码
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lcompanycode")) {
                mSBql
                    .append("select ComPanyNo, ComPanyName from ReInsuranceComPanyInfo order by ComPanyNo ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保方案编号
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lraccmucode")) {
                mSBql
                    .append("select AccumulateDefNO, AccumulateDefName from AccumulateDef order by AccumulateDefNO ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //方案级分保信息 要素名称
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrfactorname")) {
                mSBql
                    .append("select Factor, FactorName from RICalFactor where  Factor not in (select Factorcode from RICalFactorValue where ReContCode='"
                            + mConditionField
                            + "' and  RIPreceptNo='S000000000') ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保合同级分保信息 要素名称
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrfactor")) {
                mSBql
                    .append("select Factor, FactorName from RICalFactor where Factor not in(select Factorcode from RICalFactorValue where ReContCode='"
                            + mConditionField + "') ");

                mSQL = mSBql.toString();
                break SelectCode;
            }

            //再保业务帐单
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrcompanysta")) {
                mSBql
                    .append("select companyno,companyname from ReInsuranceComPanyInfo");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //再保明细报表帐单
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
            //再保临分算法
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrarithmetic")) {
                mSBql
                    .append("select arithmeticid,arithmeticname from RIMODEARITHMETIC where StandbyString3='03' order by arithmeticid");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //团单下责任
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrgrpcontduty")) {
                mSBql
                    .append("select distinct b.dutycode,(select dutyname from lmduty where dutycode=b.dutycode) from lcpol a,lcduty b where a.polno=b.polno and a.grpcontno='"
                            + mConditionField
                            + "' and b.dutycode in (select distinct associatedcode from AccumulateRDCode) ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //团单下险种
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "lrgrpcontrisk")) {
                mSBql
                    .append(" select distinct a.riskcode,(select riskname from lmrisk where riskcode=a.riskcode) from lcpol a,lcduty b where a.polno=b.polno and a.grpcontno='"
                            + mConditionField
                            + "' and b.dutycode in (select distinct associatedcode from AccumulateRDCode)  ");
                mSQL = mSBql.toString();
                break SelectCode;
            }
            //团单下保障计划
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
            //查询一级工作单位包括中石油
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

            //查询受益人列表
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

            //个单险种查询personrisk
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

            //与本人相关的信息   (9位置 'BnfLot'<==>'1')
            if (StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
                "RelationToPayee")) {
//                mSBql
//                    .append("select '00','受益人本人',Name,Sex,Birthday,IDType,IDNo,'00','1',CaseGetMode,BankAccNo,AccName,BankPrv,BankCity,BankInfo,BankCode from llbnf where ");
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
                
                mSQL = "select '00','受益人本人',Name,Sex,Birthday,IDType,IDNo,'00','1',CaseGetMode,BankAccNo,AccName,BankPrv,BankCity,BankInfo,BankCode,insuredno,'未确认' from llbnf where  "
                	+ " bnfno ='"+bnfno+"' and caseno='"+caseNo+"' ";
                executeType = 1;
                System.out.println(mSQL);
                break SelectCode;
            }
            //与受益人本人相关的信息
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
                    cError.errorMessage = "请输入险种名称！";
                    this.mErrors.addOneError(cError);
                    return false;
                }
                ReadTPABL readTPABL = new ReadTPABL();
                // 查找本人的情况，就被保险人本人
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
                    // 有银行账户信息
                    if (tSSRSC.GetText(1, 1) != null
                            && !"".equals(tSSRSC.GetText(1, 1))) {
                        mSQL = "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,5,d.accname,d.BankCode,d.BankAccNo,d.BankPrv,d.BankCity,d.BankInfo,'','1','1',d.RelationToMainInsured,'未确认' "
                                + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='"
                                + customerNo
                                + "' union "
                                + "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,5,d.accname,d.BankCode,d.BankAccNo,d.BankPrv,d.BankCity,d.BankInfo,'','1','1',d.RelationToMainInsured,'未确认'"
                                + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='" + customerNo + "'";
                        // 没有银行信息就把银行信息置空
                    } else {
                        mSQL = "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'未确认'"
                                + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='"
                                + customerNo
                                + "' union "
                                + "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'未确认'"
                                + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                + polNo
                                + "' and a.caseno = c.caseno and c.ContNo = '"
                                + contNo
                                + "' and a.CaseNo = '"
                                + caseNo
                                + "' and d.InsuredNo ='" + customerNo + "'";
                    }
                    // 以下通过连带被保险人查找主被保险人的过程
                } else {
                    String strQueryC = "select MainInsuredNo,grpcontno from lcinsured where MainInsuredNo is not null and "
                            + " ContNo ='"
                            + contNo
                            + "' and insuredNo = '"
                            + customerNo + "'";
                    SSRS sSRSC = exeSQL.execSQL(strQueryC);
                    // C表中存在
                    if (sSRSC.MaxRow > 0) {
                        LCInsuredSchema lcSchema = readTPABL
                            .getBankInfoOfInsured(sSRSC.GetText(1, 2), contNo,
                                sSRSC.GetText(1, 1), true);
                        // 有银行信息
                        if (lcSchema != null) {
                            String bankCode = lcSchema.getBankCode();
                            String bankAccNo = lcSchema.getBankAccNo();
                            String accName = lcSchema.getAccName();
                            String bankInfo = lcSchema.getBankInfo();
                            String bankPrv = lcSchema.getBankPrv();
                            String bankCity = lcSchema.getBankCity();
                            mSQL = "select '01','主被保险人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,a.IDNo,'5',"
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
                                    + "'','1','1',d.RelationToMainInsured,'未确认' from llcase a, llcasepolicy c, LCInsured d where d.ContNo = c.ContNo and c.polno = '"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and a.CustomerNo = d.insuredno and a.CustomerNo ='"
                                    + customerNo + "'"
                                    //增加连带被保人本人的帐户选项
                                    +" union select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                    + "a.IDNo,'5',d.accname,d.bankcode,d.bankaccno,d.bankprv,d.bankcity,d.bankinfo,'','1','1',d.RelationToMainInsured,'未确认'"
                                    + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and d.InsuredNo ='" + customerNo + "' and d.bankaccno is not null ";
                            
                            // 无银行信息
                        } else {
                            mSQL = "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                    + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'未确认'"
                                    + " from llcase a,llcasepolicy c,LCInsured d where d.ContNo=c.ContNo and c.polno='"
                                    + polNo
                                    + "' and a.caseno = c.caseno and c.ContNo = '"
                                    + contNo
                                    + "' and a.CaseNo = '"
                                    + caseNo
                                    + "' and d.InsuredNo ='" + customerNo + "'";
                        }
                        // B表中存在
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
                            // 有银行信息
                            if (lcSchema != null) {
                                String bankCode = lcSchema.getBankCode();
                                String bankAccNo = lcSchema.getBankAccNo();
                                String accName = lcSchema.getAccName();
                                String bankInfo = lcSchema.getBankInfo();
                                String bankPrv = lcSchema.getBankPrv();
                                String bankCity = lcSchema.getBankCity();
                                mSQL = "select '01','主被保险人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,a.IDNo,'5',"
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
                                        + "'','1','1',d.RelationToMainInsured,'未确认' from llcase a, llcasepolicy c, LBInsured d where d.ContNo = c.ContNo and c.polno = '"
                                        + polNo
                                        + "' and a.caseno = c.caseno and c.ContNo = '"
                                        + contNo
                                        + "' and a.CaseNo = '"
                                        + caseNo
                                        + "' and a.CustomerNo = d.insuredno and a.CustomerNo ='"
                                        + customerNo + "'"
                                        //增加连带被保人本人的帐户选项
                                        + " union select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                        + "a.IDNo,'5',d.accname,d.bankcode,d.bankaccno,d.bankprv,d.bankcity,d.bankinfo,'','1','1',d.RelationToMainInsured,'未确认'"
                                        + " from llcase a,llcasepolicy c,LBInsured d where d.ContNo=c.ContNo and c.polno='"
                                        + polNo
                                        + "' and a.caseno = c.caseno and c.ContNo = '"
                                        + contNo
                                        + "' and a.CaseNo = '"
                                        + caseNo
                                        + "' and d.InsuredNo ='"
                                        + customerNo + "' and d.bankaccno is not null ";
                                // 无银行信息
                            } else {
                                mSQL = "select '00','被保险人本人帐户','0',a.CustomerName,a.CustomerSex,a.CustBirthday,a.CustomerNo,a.IDType,"
                                        + "a.IDNo,a.CaseGetMode,'','','','','','','','1','1',d.RelationToMainInsured,'未确认'"
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
            //JiangZhen 领款人信息
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
				// 查找本人的情况，就被保险人本人
				mSQL = "select distinct '1', '现金', '', '', '', '', '', '', '' from dual " 
						+ " union select distinct '3', '支票', '', '', '', '', '', '', '' from dual " 
						+ " union select distinct '4', '投保人银行账户银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+" union select distinct '4', '投保人银行账户银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lborgan a,llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+" union select distinct '5', '个人银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, a.bankinfo, a.bankcode, a.polno " 
						    +" from lcpayee a, llcasepolicy c, llbnf d"
							+ " where a.polno = c.polno and c.polno = d.polno and d.caseno = c.caseno and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ "and a.bnfno = '"
							+ bnfno
							+ "' and a.name='" + name +"' "
						+ " union select distinct '5', '个人银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, a.bankinfo, a.bankcode, a.polno "
						    +" from llbnf a "
							+ " where a.CaseNo = '"
							+ caseNo
							+ "'  and a.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.name='" + name +"' and a.modireasondesc is null "
						+ " union select distinct '6', '分支机构银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a,llcasepolicy c, LCInsured d " 
						    +" where d.ContNo = c.ContNo and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and d.organcomcode = a.organcomcode and a.grpcontno = c.grpcontno " 
						+" union select distinct '6', '分支机构银行转账', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
							+" from lborgan a, llcasepolicy c, LCInsured d " 
							+" where d.ContNo = c.ContNo and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and d.organcomcode = a.organcomcode and a.grpcontno = c.grpcontno " 
						+" union select distinct '7', '加急支付', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+"union select distinct '7', '加急支付', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lborgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
						//Amended By Fang for ASR20107305 增加“已垫付”支付方式
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno"
						+" union select distinct '8', '已垫付', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
						    +" from lcorgan a, llcasepolicy c " 
						    +" where 1=1 and c.CaseNo = '"
							+ caseNo
							+ "'  and c.InsuredNo = '"
							+ customerNo
							+ "'"
							+ " and a.organcomcode = '0000' and a.grpcontno = c.grpcontno " 
						+"union select distinct '8', '已垫付', a.bankaccno, a.accname, a.bankprv, a.bankcity, '', a.bankcode, a.grpcontno " 
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
            // 多责任控制
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
            //照会项目
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
            //add by winnie ASR20107444 保单 业务性质 ClientSource
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
           	// add by winnie ASR20093243 统括保单比例配制方式 20090325
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
            //add by winnie ASR20093070 保全照会功能 
            //问题件列表
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
            
            //ASR20110918 geb标识
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
            
            //查询核查结果获取时间(edit by Jiyacheng)
            if(StrTool.cTrim(mLDCodeSchema.getCodeType()).equalsIgnoreCase(
            "batchno")){
            	mSBql
            		 .append("select distinct 0,'全部' from lpinvestigation union all select rownum,batchno from (select distinct batchno from lpinvestigation where ");
            	mSBql.append("investigation1 in ('0','1','2') and batchno is not null order by batchno desc)");
            	mSQL = mSBql.toString();
            	break SelectCode;
            }
            
            //end add 20090519
            // 其他LDCODE表中定义的引用
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
            System.out.println("原则上不会进入这个地方！");
        }
        // 由上面的SelectCode跳出后，开始走下面的程序

        // showcodelist查询输出，调试时使用，稳定后可注释，由于调用的是ExeSql，所以不需要在这里输出
        // System.out.println("CodeQueryBL:" + mSQL);
        if (mSQL.equals("")) {
            System.out.println("查询语句为空，请查看codequerybl中的设置" + mSBql.toString());
            return false;
        }
        if (executeType == 0) {
            // 使用截取方式查询
            mResultStr = mExeSQL.getEncodedResult(mSQL, 1);
        } else {
            // 目前只会出现截取查询和全部查询
            // else if (executeType == 1)
            // 全部数据查询
            // 数据量级在几千左右的时候，还是一次取出的效率高些
            mResultStr = mExeSQL.getEncodedResult(mSQL);
        }

        if (mExeSQL.mErrors.needDealError()) {
            // @@错误处理,在ExeSQL中已进行错误处理，这里直接返回即可。
            this.mErrors.copyAllErrors(mExeSQL.mErrors);
            // 如果sql执行错误，则返回sql描述，由于调用exesql来执行，所以必然会有出错sql输出
            // System.out.println("Code Query Error Sql:" + mSQL);
        }
        mResult.clear();
        mResult.add(mResultStr);
        return true;
    }

    /**
     * 测试函数
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
