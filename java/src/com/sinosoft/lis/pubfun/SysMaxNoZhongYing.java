/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description:系统号码管理（民生人寿核心业务系统）生成系统号码 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Liuqiang
 * @version 1.0
 */

import java.sql.Connection;

import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoZhongYing implements com.sinosoft.lis.pubfun.SysMaxNo
{

    public SysMaxNoZhongYing()
    {
    }

    /**
     *<p>生成流水号的函数<p>
     *<p>号码规则：机构编码  日期年  校验位   类型    流水号<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType 为需要生成号码的类型
     * @param cNoLimit 为需要生成号码的限制条件（要么是SN，要么是机构编码）
     * @param cVData 为需要生成号码的业务相关限制条件
     * @return 生成的符合条件的流水号，如果生成失败，返回空字符串""
     */
    public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData)
    {
        //中英目前未启用
        return CreateMaxNo(cNoType, cNoLimit);
    }
    
    /**
     *<p>生成流水号的函数<p>
     *<p>号码规则：机构编码  日期年  校验位   类型    流水号<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType 为需要生成号码的类型
     * @param cNoLimit 为需要生成号码的限制条件（要么是SN，要么是机构编码）
     * @return 生成的符合条件的流水号，如果生成失败，返回空字符串""
     */
    public String CreateMaxNo(String cNoType, String cNoLimit)
    {
        //传入的参数不能为空，如果为空，则直接返回
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLimit == null))
        {
            System.out.println("NoType长度错误或者NoLimit为空");

            return null;
        }

        //默认流水号位数
        int serialLen = 10;
        String tReturn = null;
        cNoType = cNoType.toUpperCase();
        //System.out.println("-----------cNoType:"+cNoType+"  cNoLimit:"+cNoLimit);
      
        //cNoLimit如果是SN类型，则cNoType只能是下面类型中的一个
        if (cNoLimit.trim().toUpperCase().equals("SN"))
        { //modify by yt 2002-11-04
            // 		if (cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") || cNoType.equals("SugDataItemCode") || cNoType.equals("SugItemCode") || cNoType.equals("SugModelCode") || cNoType.equals("SugCode"))
            if (cNoType.equals("COMMISIONSN") ||
                    cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") ||
                    cNoType.equals("SUGDATAITEMCODE") ||
                    cNoType.equals("SUGITEMCODE") ||
                    cNoType.equals("SUGMODELCODE") ||
                    cNoType.equals("SUGCODE"))
            {
                serialLen = 9;
            }
            else
            {
                System.out.println("错误的NoLimit");

                return null;
            }
        }

        //中意团体年金机构内部编码,传入的cNoLimit必须为上级机构的内部编码
        if (cNoType.equals("ORGCOMNO")||cNoType.equals("SENDCOMNO"))
        {
            serialLen = 4;
        }
        //航意险6位流水号
        if (cNoType.equals("AIRPOLNO"))
        { //modify by yt 2002-11-04
            serialLen = 6;
        }
        //特殊险种流水号长度处理
        if (cNoType.equals("AGENTCODE") || cNoType.equals("MANAGECOM"))
        {
            serialLen = 4;
        }

        tReturn = cNoLimit.trim();
        //System.out.println("tReturn:"+tReturn);

        String tCom = ""; //两位机构
        if (tReturn.length() >= 4&&!cNoType.equals("ORGCOMNO")&&!cNoType.equals("SENDCOMNO"))
        {
            tCom = tReturn.substring(2, 4);
            tCom = "0" + tCom; //加一位较验位
            tReturn = tReturn.substring(0, 4);
        }

        //生成各种号码
        //投保单险种号码
        if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "11" + tCom;
        }

        //集体投保单险种号码
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "12" + tCom;
        }

        //总单投保单号码
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "13" + tCom;
        }

        //集体投保单号码ProposalGrpContNo,LDMaxNo里最大长度为15，所以用ProGrpContNo代替
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "14" + tCom;
        }

        //保单险种号码
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "21" + tCom;
        }

        //集体保单险种号码
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "22" + tCom;
        }

        //合同号码
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = "23" + tCom;
        }

        //集体合同号码
        else if (cNoType.equals("GRPCONTNO"))
        {
            tReturn = "24" + tCom;
        }

        //交费通知书号码
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "31" + tCom;
        }

        //交费收据号码
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "32" + tCom;
        }

        //给付通知书号码
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "36" + tCom;
        }

        //给付通知书号码
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "37" + tCom;
        }

        //批改申请号码
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "41" + tCom;
        }

        //批单号码
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "42" + tCom;
        }

        //集体批单申请号码
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "43" + tCom;
        }

        //集体批单号码
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "44" + tCom;
        }

        //报案编号
        else if (cNoType.equals("RPTNO"))
        {
            tReturn = "50" + tCom;
        }

        //立案编号
        else if (cNoType.equals("RGTNO"))
        {
            tReturn = "51" + tCom;
        }

        //赔案编号
        else if (cNoType.equals("CLMNO"))
        {
            tReturn = "52" + tCom;
        }

        //拒案编号
        else if (cNoType.equals("DECLINENO"))
        {
            tReturn = "53" + tCom;
        }

        //报案分案编号
        else if (cNoType.equals("SUBRPTNO"))
        {
            tReturn = "54" + tCom;
        }

        //立案分案编号
        else if (cNoType.equals("CASENO"))
        {
            tReturn = "55" + tCom;
        }

        //合同号
        else if (cNoType.equals("PROTOCOLNO"))
        {
            tReturn = "71" + tCom;
        }

        //单证印刷号码
        else if (cNoType.equals("PRTNO"))
        {
            tReturn = "80" + tCom;
        }

        //打印管理流水号
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "81" + tCom;
        }

        //打印管理流水号
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "82" + tCom;
        }

        //回收清算单号
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "61" + tCom;
        }

        //银行代扣代付批次号
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "62" + tCom;
        }

        //接口凭证id号
        else if (cNoType.equals("VOUCHERIDNO"))
        {
            tReturn = "63" + tCom;
        }

        //佣金号码
        else if (cNoType.equals("WAGENO"))
        {
            tReturn = "90" + tCom;
        }

        //流水号
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "98" + tCom;
        }

        if (tReturn.length() == 10)
        {
            tReturn += "99";
        }

        //其他
        Connection conn = DBConnPool.getConnection();

        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return tReturn;
        }

        int tMaxNo = 0;
        cNoLimit = tReturn;
        //System.out.println("cNoLimit:"+cNoLimit);

        try
        {
            //开始事务
            //查询结果有3个： -- added by Fanym
            //全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
            //如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
            conn.setAutoCommit(false);

//            String strSQL = "select MaxNo from LDMaxNo where notype='" +
//                    cNoType + "' and nolimit='" + cNoLimit +
//                    "' for update";
//            //如果数据库类型是ORACLE的话，需要添加nowait属性，以防止锁等待
//            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
//            {
//                strSQL = strSQL + " nowait";
//            }
            StringBuffer tSBql = new StringBuffer(128);
            tSBql.append("select MaxNo from LDMaxNo where notype='");
            tSBql.append(cNoType);
            tSBql.append("' and nolimit='");
            tSBql.append(cNoLimit);
            tSBql.append("'");
            //如果数据库类型是ORACLE的话，需要添加nowait属性，以防止锁等待
            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
            {
                tSBql.append(" for update nowait");
            }
            else
            {
                tSBql.append(" WITH RS");
//                tSBql.append(" for update");
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(tSBql.toString());

            //测试返回bull时
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("查询LDMaxNo出错，请稍后!");
                conn.rollback();
                conn.close();

                return null;
            }

            if ((result == null) || result.equals(""))
            {
//                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
//                        cNoType + "', '" + cNoLimit + "', 1)";
                tSBql = new StringBuffer(128);
                tSBql.append("insert into ldmaxno(notype, nolimit, maxno) values('");
                tSBql.append(cNoType);
                tSBql.append("', '");
                tSBql.append(cNoLimit);
                tSBql.append("', 1)");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo 插入失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = 1;
                }
            }
            else
            {
//                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
//                        + "' and nolimit = '" + cNoLimit + "'";
                tSBql = new StringBuffer(128);
                tSBql.append("update ldmaxno set maxno = maxno + 1 where notype = '");
                tSBql.append(cNoType);
                tSBql.append("' and nolimit = '");
                tSBql.append(cNoLimit);
                tSBql.append("'");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo 更新失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = Integer.parseInt(result) + 1;
                }
            }

            conn.commit();
            conn.close();
        }
        catch (Exception Ex)
        {
            try
            {
                conn.rollback();
                conn.close();

                return null;
            }
            catch (Exception e1)
            {
                e1.printStackTrace();

                return null;
            }
        }

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", serialLen);

        if (tReturn.equals("SN"))
        {
            tReturn = tStr.trim() + "0";
        }
        else
        {
            tReturn = tReturn.trim() + tStr.trim();
        }
        return tReturn;
    }

    /**
     * 功能：产生指定长度的流水号，一个号码类型一个流水
     * @param cNoType String 流水号的类型
     * @param cNoLength int 流水号的长度
     * @return String 返回产生的流水号码
     */
    public String CreateMaxNo(String cNoType, int cNoLength)
    {
        StringBuffer tSBql = null;
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLength <= 0))
        {
            System.out.println("NoType长度错误或NoLength错误");
            return null;
        }

        cNoType = cNoType.toUpperCase();

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        String tReturn = "";
        String cNoLimit = "SN";
        //对上面那种cNoLimit为SN的数据做一个校验，否则会导致数据干扰
        if (cNoType.equals("COMMISIONSN") ||
                cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") ||
                cNoType.equals("SUGDATAITEMCODE") ||
                cNoType.equals("SUGITEMCODE") ||
                cNoType.equals("SUGMODELCODE") ||
                cNoType.equals("SUGCODE"))
        {
            tSBql = new StringBuffer(128);
            tSBql.append("该类型流水号，请采用CreateMaxNo('");
            tSBql.append(cNoType);
            tSBql.append("','SN')方式生成");
            System.out.println(tSBql.toString());
            return null;
        }
        int tMaxNo = 0;
        tReturn = cNoLimit;

        try
        {
            //开始事务
            conn.setAutoCommit(false);

//            String strSQL = "select MaxNo from LDMaxNo where notype='" +
//                    cNoType + "' and nolimit='" + cNoLimit +
//                    "' for update";
//            //如果数据库类型是ORACLE的话，需要添加nowait属性，以防止锁等待
//            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
//            {
//                strSQL = strSQL + " nowait";
//            }
            tSBql = new StringBuffer(128);
            tSBql.append("select MaxNo from LDMaxNo where notype='");
            tSBql.append(cNoType);
            tSBql.append("' and nolimit='");
            tSBql.append(cNoLimit);
            tSBql.append("'");
            //如果数据库类型是ORACLE的话，需要添加nowait属性，以防止锁等待
            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
            {
                tSBql.append(" for update nowait");
            }
            else
            {
                //下面这个是标级锁，不合适
//                tSBql.append(" WITH RR USE AND KEEP UPDATE LOCKS");
                tSBql.append(" WITH RS");
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(tSBql.toString());

            if ((result == null) || exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo 资源忙，请稍后!");
                conn.rollback();
                conn.close();

                return null;
            }

            if (result.equals(""))
            {
//                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
//                        cNoType + "', '" + cNoLimit + "', 1)";
                tSBql = new StringBuffer(128);
                tSBql.append("insert into ldmaxno(notype, nolimit, maxno) values('");
                tSBql.append(cNoType);
                tSBql.append("', '");
                tSBql.append(cNoLimit);
                tSBql.append("', 1)");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo 插入失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = 1;
                }
            }
            else
            {
//                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
//                        + "' and nolimit = '" + cNoLimit + "'";
                tSBql = new StringBuffer(128);
                tSBql.append("update ldmaxno set maxno = maxno + 1 where notype = '");
                tSBql.append(cNoType);
                tSBql.append("' and nolimit = '");
                tSBql.append(cNoLimit);
                tSBql.append("'");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo 更新失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = Integer.parseInt(result) + 1;
                }
            }

            conn.commit();
            conn.close();
        }
        catch (Exception Ex)
        {
            try
            {
                conn.rollback();
                conn.close();

                return null;
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                return null;
            }
        }

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", cNoLength);
        tReturn = tStr.trim();

        return tReturn;
    }

    public static void main(String[] args)
    {
    	SysMaxNoZhongYing t = new SysMaxNoZhongYing();
        System.out.println(PubFun1.CreateMaxNo("ORGCOMNO", "86"));
    }
}
