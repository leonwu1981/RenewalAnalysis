/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description:系统号码管理（长城人寿核心业务系统）生成系统号码 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Fanym
 * @version 1.0
 */

import java.sql.Connection;
import java.math.BigInteger;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoYiHe implements com.sinosoft.lis.pubfun.SysMaxNo
{

    public SysMaxNoYiHe()
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
     * 针对颐和的单证类型，需要一单证一版本流水的规则，LDMaxNo中的字段含义如下：
     * NoType:单证编码
     * NoLimit:单证版本
     */
    public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData)
    {
        //单证编码的总位数
        int serialLen = 16;
        String tReturn = null;
        if (cNoType==null  || cNoType.equals(""))
        {
          return null;
        }
        //划帐失败通知书
        else if(cNoType.equalsIgnoreCase("TRANSFER"))
        {
            tReturn = "1019";
        }
        //客户问题件通知书
        else if(cNoType.equalsIgnoreCase("CUSISSUE"))
        {
            tReturn = "1022";
        }
        //核保通知书
        else if(cNoType.equalsIgnoreCase("HEBAOHAN"))
        {
            tReturn = "1023";
        }
        //体检通知书
        else if(cNoType.equalsIgnoreCase("TIJIAN"))
        {
            tReturn = "1028";
        }
        //契调通知书
        else if(cNoType.equalsIgnoreCase("QIDIAO"))
        {
            tReturn = "1045";
        }
        //补费通知书
        else if(cNoType.equalsIgnoreCase("BUFEI"))
        {
            tReturn = "1048";
        }
        //业务员问题件
        else if(cNoType.equalsIgnoreCase("AGENTISSUE"))
        {
            tReturn = "1066";
        }
        //特殊件问题件
        else if(cNoType.equalsIgnoreCase("SPECISSUE"))
        {
            tReturn = "1067";
        }

        else
        {
            return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }
        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");
        try
        {
            //开始事务
            //查询结果有3个： -- added by Fanym
            //全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
            //如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
            conn.setAutoCommit(false);
            //查询单证描述表，获取该单证类型有效的最大版本号
            String strSQL = "select max(certifycode) from lmcertifydes where state = '0' and Certifycode like '"
                          + tReturn
                          + "%'";
            ExeSQL exeSQL = new ExeSQL(conn);
            String tVersion = exeSQL.getOneValue(strSQL).substring(4, 6);

          //如果版本号不存在，则证明没有此单证类型描述
            if (tVersion == null || tVersion.equals(""))
            {
                System.out.println("没有该" + cNoType + "单证类型描述！");
                conn.rollback();
                conn.close();
                return null;
            }
            //把版本作为限制条件，长度为2位，不足左补0
            cNoLimit = PubFun.LCh(tVersion, "0", 2);
            strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";
            exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            //测试返回错误
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("查询LDMaxNo出错，请稍后!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) || result.equals(""))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                         cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 插入失败，请重试!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = 1;
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 更新失败，请重试!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));
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
        //获取流水号，总位数-单证类型长度-版本位数-最后两位88
        String tStr = PubFun.LCh(tMaxNo.toString(), "0", serialLen - tReturn.length() - cNoLimit.length() - 2);

        tReturn = tReturn + cNoLimit + tStr + "88";
        //返回单证流水号
        return tReturn;
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
        if ((cNoType == null) || (cNoType.trim().length() <= 0) || (cNoLimit == null))
        {
            System.out.println("NoType长度错误或者NoLimit为空");
            return null;
        }

        int serialLen = 16; //长城号码总长度是16位
        String tReturn = null; //返回字符串
        cNoType = cNoType.toUpperCase(); //流水类型
        //String tBit = "0"; //校验位

        //cNoLimit如果是SN类型，则cNoType只能是下面类型中的一个
        if (cNoLimit.trim().equalsIgnoreCase("SN"))
        {
            //modify by yt 2002-11-04
            if (cNoType.equals("CUSTOMERNO") || cNoType.equals("GRPNO") || cNoType.equals("VCUSTOMERNO"))
            {
                serialLen = 12; //长城默认客户号是12位
            }
            else if (cNoType.equals("COMMISIONSN"))
            {
              serialLen = 20; //扎帐表流水号默认是20位
            }
            else
            {
                System.out.println("错误的NoLimit");
                return null;
            }
        }
        else
        {
            cNoLimit = "SYS";
        }

        //个人客户号
        if (cNoType.equals("CUSTOMERNO"))
        {
           tReturn = "1";
        }
        //团体客户号
        else if (cNoType.equals("GRPNO"))
        {
           tReturn = "9";
        }
        //虚拟客户：无名单、公共帐户
        else if (cNoType.equals("VCUSTOMERNO"))
        {
           tReturn = "0";
        }
        //个险投保单
        else if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "9010";
        }
        //团体下个险投保单
        else if (cNoType.equals("GPROPOSALNO"))
        {
            tReturn = "9011";
        }
        //团体投保单
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "9012";
        }
        //总单投保单号码，系统类型
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "9015";
        }
        //团体下合同投保单号码
        else if (cNoType.equals("GPROPOSALCONTNO"))
        {
            tReturn = "9016";
        }
        //集体投保单号码ProposalGrpContNo,LDMaxNo里最大长度为15，所以用ProGrpContNo代替
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "9018";
        }
        //保单险种号码
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "9020";
        }
        //团体下保单险种号码
        else if (cNoType.equals("GPOLNO"))
        {
            tReturn = "9021";
        }
        //集体保单险种号码
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "9022";
        }
        //个人保单号码
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = "9025";
        }
        //团体下个人保单号码
        else if (cNoType.equals("GCONTNO"))
        {
            tReturn = "9026";
        }
        //集体保单号
        else if (cNoType.equals("GRPCONTNO"))
        {
            tReturn = "9028";
        }
        //交通意外险保单号
        else if (cNoType.equals("AIRPOLNO"))
        {
            tReturn = "9029";
        }
        //核保通知书号
//        else if (cNoType.equals("UWNOTICENO"))
//        {
//            tReturn = "102301";
//        }
        //交费通知书号码
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "102101";
        }
        //交费收据号码
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "9032";
        }
        //给付通知书号码
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "9036";
        }
        //实付号码
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "9038";
        }
        //批改申请号码
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "9041";
        }
        //批单号码
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "9046";
        }
        //集体批单申请号码
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "9043";
        }
        //集体批单号码
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "9045";
        }
        //保全受理号码
        else if (cNoType.equals("EDORACCEPTNO"))
        {
            tReturn = "9048";
        }
        //报案编号
        else if (cNoType.equals("RPTNO"))
        {
            tReturn = "9050";
        }
        //立案编号
        else if (cNoType.equals("RGTNO"))
        {
            tReturn = "9051";
        }
        //赔案编号
        else if (cNoType.equals("CLMNO"))
        {
            tReturn = "9052";
        }
        //拒案编号
        else if (cNoType.equals("DECLINENO"))
        {
            tReturn = "9053";
        }
        //报案分案编号
        else if (cNoType.equals("SUBRPTNO"))
        {
            tReturn = "9054";
        }
        //立案分案编号
        else if (cNoType.equals("CASENO"))
        {
            tReturn = "9055";
        }
        //事件编号
        else if (cNoType.equals("CASERELANO"))
        {
            tReturn = "9056";
        }
        //打印管理流水号
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "9066";
        }
        //打印管理流水号
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "9068";
        }
        //回收清算单号
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "9061";
        }
        //银行代扣代付批次号
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "9063";
        }
        //扎帐表流水号
        else if (cNoType.equals("COMMISIONSN"))
        {
          tReturn = "9071";
        }
        //中介协议书号码
        else if (cNoType.equals("PROTOCOLNO"))
        {
          tReturn = "9078";
        }
        //建议书编码
        else if (cNoType.equals("SUGCODE"))
        {
          tReturn = "9088";
        }
        //建议书模版代码
        else if (cNoType.equals("SUGMODELCODE"))
        {
          tReturn = "9082";
        }
        //建议书项目编码
        else if (cNoType.equals("SUGITEMCODE"))
        {
          tReturn = "9083";
        }
        //建议书数据项目编码
        else if (cNoType.equals("SUGDATAITEMCODE"))
        {
          tReturn = "9085";
        }
        //流水号
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "9090";
        }
        //其他
        else
        {
            tReturn = "";
            //return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");

        try
        {
            //开始事务
            //查询结果有3个： -- added by Fanym
            //全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
            //如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";
            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            //测试返回null时
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("查询LDMaxNo出错，请稍后!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) || result.equals(""))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                        cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 插入失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 更新失败，请重试!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));

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

        //String tStr = String.valueOf(tMaxNo);
        String tStr = tMaxNo.toString();
        tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2); //还要留两位的88
        tReturn = tReturn + tStr + "88";
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
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLength <= 0))
        {
            System.out.println("NoType长度错误或NoLength错误");
            return null;
        }

        cNoType = cNoType.toUpperCase();
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
            System.out.println("该类型流水号，请采用CreateMaxNo('"+cNoType+"','SN')方式生成");
            return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");
        tReturn = cNoLimit;

        try
        {
            //开始事务
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo 查询失败，请稍后!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) ||(result.equals("")))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                        cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 插入失败，请重试!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = 1;
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo 更新失败，请重试!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));
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

        //String tStr = String.valueOf(tMaxNo);
        String tStr = tMaxNo.toString();
        tStr = PubFun.LCh(tStr, "0", cNoLength);
        tReturn = tStr;

        return tReturn;
    }

    public static void main(String[] args)
    {
        SysMaxNoYiHe tSysMaxNoYiHe = new SysMaxNoYiHe();
        VData tVData = new VData();
        System.out.println(tSysMaxNoYiHe.CreateMaxNo("HEBAOHAN","SN",tVData));
//        String a = "8611";
//        System.out.println(a.substring(0, 4));
    }
}
