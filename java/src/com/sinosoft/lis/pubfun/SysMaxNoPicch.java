package com.sinosoft.lis.pubfun;

import java.util.*;
import java.sql.Connection;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoPicch implements com.sinosoft.lis.pubfun.SysMaxNo
{
    public SysMaxNoPicch()
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
        //Picch目前未启用
        return CreateMaxNo(cNoType, cNoLimit);
    }

    /**
     * 功能：产生指定长度的流水号，一个号码类型一个流水
     * @param cNoType：流水号的类型
     * @param cNoLength：流水号的长度
     * @return 返回产生的流水号码
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

        //System.out.println("type:"+cNoType+"   length:"+cNoLength);
        Connection conn = DBConnPool.getConnection();

        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");

            return null;
        }

        String tReturn = "";
        String cNoLimit = "SN";
        int tMaxNo = 0;
        tReturn = cNoLimit;

        try
        {
            //开始事务
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                            cNoType + "' and nolimit='" + cNoLimit +
                            "' for update";
            //如果数据库类型是ORACLE的话，需要添加nowait属性，以防止锁等待
            if (SysConst.DBTYPE.equals("ORACLE"))
            {
                strSQL = strSQL + " nowait";
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            if ((result == null) || exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo 资源忙，请稍后!");
                conn.rollback();
                conn.close();

                return null;
            }

            if (result.equals(""))
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
                    tMaxNo = 1;
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1" +
                         " where notype = '" + cNoType + "' and nolimit = '" +
                         cNoLimit + "'";
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

    /**
     *<p>生成流水号的函数,生成特定规则的流水号<p>
     * @param cNoType 为需要生成号码的类型
     * @param cInput  为生成号码需要传入的参数
     * @return 生成的符合条件的流水号，如果生成失败，返回空null
     */
    public String CreateMaxNo(String cNoType, String cInput)
    {
        //传入的参数不能为空，如果为空，则直接返回
        if ((cNoType == null) || (cNoType.trim().length() <= 0))
        {
            System.out.println("NoType长度错误");
            return null;
        }

        String tReturn = null;
        cNoType = cNoType.toUpperCase();
        //得到当前日期
        String tDate = PubFun.getCurrentDate();
        String tDateCode = tDate.substring(2, 4) + tDate.substring(5, 7) + tDate.substring(8, 10);

        //个险单家庭号
        if (cNoType.equals("FamilyNo"))
        {
            tReturn = "F" + CreateMaxNo(cNoType, 9);
        }

        //个险客户号
        else if (cNoType.equals("CUSTOMERNO"))
        {
            tReturn = CreateMaxNo(cNoType, 9);
        }

        //个险投保单号
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "13" + CreateMaxNo(cNoType, 9);
        }

        //个险投保单号
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "14" + CreateMaxNo(cNoType, 8);
        }

        //保单险种号码
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "21" +CreateMaxNo(cNoType, 9);
        }

        //集体保单险种号码
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "22" + CreateMaxNo(cNoType, 8);
        }

        //个单保单号为9位投保人客户号+2位保单号
        else if (cNoType.equals("CONTNO"))
        {
            if ((cInput == null) || (cInput.length() != 9))
            {
                System.out.println("请输入9位客户号!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //合同号码
        else if (cNoType.equals("GRPPERSONCONTNO"))
        {
            tReturn = "23" + CreateMaxNo(cNoType, 8);
        }

        //个险投保书印刷号
        else if (cNoType.equals("PRINTNO"))
        {
            tReturn = "16" + CreateMaxNo(cNoType, 9);
        }

        //团险客户号
        else if (cNoType.equals("GRPNO"))
        {
            tReturn = CreateMaxNo(cNoType, 8);
        }

        //团险被保险人客户号
        else if (cNoType.equals("GRPINSUREDNO"))
        {
            if ((cInput == null) || (cInput.length() != 9))
            {
                System.out.println("请输入9位客户号!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //团险保单号为8位客户号+2位保单号
        else if (cNoType.equals("GRPCONTNO"))
        {
            if ((cInput == null) || (cInput.length() != 8))
            {
                System.out.println("请输入8位客户号!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //团险询价号码为1位英文字母R+8位客户号+3位代表询价次数
        else if (cNoType.equals("GRPQUERYNO"))
        {
            if ((cInput == null) || (cInput.length() != 8))
            {
                System.out.println("请输入8位客户号!");
                return null;
            }
            tReturn = "R" + cInput + CreateMaxNo(cNoType, 3);
        }
        else if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "11" + CreateMaxNo(cNoType, 9);
        }

        //集体投保单险种号码
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "12" + CreateMaxNo(cNoType, 9);
        }

        //团险确认型投保书印刷号
        else if (cNoType.equals("GRPCOMFIRMPRTNO"))
        {
            tReturn = "18" + CreateMaxNo(cNoType, 9);
        }

        //团险询价型投保书印刷号
        else if (cNoType.equals("GRPQUERYPRTNO"))
        {
            tReturn = "19" + CreateMaxNo(cNoType, 9);
        }

        //单证条形码为类型码＋9位客户号＋2位随机号   --类型码?
        else if (cNoType.equals("BARCODENO"))
        {
            if (cInput == null)
            {
                System.out.println("请输入9位客户号!");
                return null;
            }
            Random random = new Random();
            tReturn = cInput + Math.abs(random.nextInt()) % 100;
        }

        //理赔号为理赔号分类编码+二级机构编码+三级机构编码+日期编码+当日流水号
        //理赔通知号
        else if (cNoType.equals("NOTICENO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "T" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }
    //理赔咨询号
        else if (cNoType.equals("COSULTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "Z" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔通知咨询号
        else if (cNoType.equals("CNNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "H" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔申请号
        else if (cNoType.equals("CASENO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "C" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔申诉号
        else if (cNoType.equals("APPEALNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "S" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔错误处理号
        else if (cNoType.equals("LLERRORNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "R" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔批次号
        else if (cNoType.equals("RGTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "P" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //理赔事件号
        else if (cNoType.equals("SUBRPTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("理赔号参数输入不正确!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "A" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //交费通知书号码
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "31" + CreateMaxNo(cNoType, 9);
        }

        //交费收据号码
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "32" + CreateMaxNo(cNoType, 9);
        }

        //给付通知书号码
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "36" + CreateMaxNo(cNoType, 9);
        }

        //给付通知书号码
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "37" + CreateMaxNo(cNoType, 9);
        }

        //批改申请号码
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "41" + CreateMaxNo(cNoType, 9);
        }

        //批单号码
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "42" + CreateMaxNo(cNoType, 9);
        }

        //集体批单申请号码
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "43" + CreateMaxNo(cNoType, 9);
        }

        //集体批单号码
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "44" + CreateMaxNo(cNoType, 9);
        }
        //合同号
        else if (cNoType.equals("PROTOCOLNO"))
        {
            tReturn = "71" + CreateMaxNo(cNoType, 9);
        }

        //单证印刷号码
        else if (cNoType.equals("PRTNO"))
        {
            tReturn = "80" + CreateMaxNo(cNoType, 9);
        }

        //打印管理流水号
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "81" + CreateMaxNo(cNoType, 9);
        }

        //打印管理流水号
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "82" + CreateMaxNo(cNoType, 9);
        }

        //回收清算单号
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "61" + CreateMaxNo(cNoType, 9);
        }

        //银行代扣代付批次号
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "62" + CreateMaxNo(cNoType, 9);
        }

        //接口凭证id号
        else if (cNoType.equals("VOUCHERIDNO"))
        {
            tReturn = "63" + CreateMaxNo(cNoType, 9);
        }

        //佣金号码
        else if (cNoType.equals("WAGENO"))
        {
            tReturn = "90" + CreateMaxNo(cNoType, 9);
        }

        //流水号
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "98" + CreateMaxNo(cNoType, 9);
        }

        //默认调用中英的规则
        else
        {
            SysMaxNoZhongYing zhongying = new SysMaxNoZhongYing();
            tReturn = zhongying.CreateMaxNo(cNoType, cInput);
        }

        return tReturn;
    }

   private String getClaimLimit(String MngComCode )
   {
        return MngComCode.substring(2,6);
   }

    public static void main(String[] args)
    {
        SysMaxNoPicch tSysMaxNoPicch = new SysMaxNoPicch();
//        System.out.println("HOMENO-" + tSysMaxNoPicch.CreateMaxNo("HOMENO", null));
//        System.out.println("CUSTOMERNO-" + tSysMaxNoPicch.CreateMaxNo("CUSTOMERNO", null));
//        System.out.println("PROPOSALCONTNO-" + tSysMaxNoPicch.CreateMaxNo("PROPOSALCONTNO", null));
//        System.out.println("CONTNO-" + tSysMaxNoPicch.CreateMaxNo("CONTNO", "111111111"));
//        System.out.println("PRINTNO-" + tSysMaxNoPicch.CreateMaxNo("PRINTNO", null));
//        System.out.println("GRPCUSTOMERNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCUSTOMERNO", null));
//        System.out.println("GRPINSUREDNO-" + tSysMaxNoPicch.CreateMaxNo("GRPINSUREDNO", "123456789"));
//        System.out.println("GRPCONTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCONTNO", "12345678"));
//        System.out.println("GRPQUERYNO-" + tSysMaxNoPicch.CreateMaxNo("GRPQUERYNO", "12345678"));
//        System.out.println("GRPCOMFIRMPRTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCOMFIRMPRTNO", null));
//        System.out.println("GRPQUERYPRTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPQUERYPRTNO", null));
//        System.out.println("BARCODENO-" + tSysMaxNoPicch.CreateMaxNo("BARCODENO", "123456789"));
//        System.out.println("RGTNO-" + tSysMaxNoPicch.CreateMaxNo("RGTNO", "0102"));
        System.out.println("CASERELANO" + tSysMaxNoPicch.CreateMaxNo("CASERELANO", "8600"));
    }

}

