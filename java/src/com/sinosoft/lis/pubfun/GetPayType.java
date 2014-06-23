/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.db.LJTempFeeClassDB;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.lis.vschema.LJTempFeeClassSet;
import com.sinosoft.utility.CErrors;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GetPayType
{

    private String payMode = ""; //���ѷ�ʽ
    private String BankCode = ""; //���б���
    private String BankAccNo = ""; //�����˺�
    private String AccName = ""; //����
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    public static void main(String[] args)
    {
//        GetPayType getPayType1 = new GetPayType();
    }

    /**
     * ����ӡˢ�Ų��Ҹ��˱������еĽ��ѷ�ʽ������(��֧Ʊ�����У��ֽ�����ȼ�����)
     * @param inPrtNo
     * @return
     */
    public boolean getPayTypeForLCPol(String inPrtNo)
    {
        if (inPrtNo == null)
        {
            return false;
        }
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPrtNo(inPrtNo);
        LCPolSet tLCPolSet = tLCPolDB.query();
        if (tLCPolSet == null || tLCPolSet.size() == 0)
        {
            mErrors.addOneError("û���ҵ���Ӧ�ı���");
            return false;
        }

        String sql = "select * from ljtempfeeclass where tempfeeno in ";
        sql = sql + "(select tempfeeno from ljtempfee where EnterAccDate is not null and otherno in ('" +
              inPrtNo + "'";

        for (int n = 1; n <= tLCPolSet.size(); n++)
        {
            sql = sql + ", '" + tLCPolSet.get(n).getPolNo() + "' ";
        }
        sql = sql + ") )";

        LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
        LJTempFeeClassSet tLJTempFeeClassSet = tLJTempFeeClassDB.executeQuery(
                sql);
        if (tLJTempFeeClassSet == null || tLJTempFeeClassSet.size() == 0)
        {
            mErrors.addOneError("û���ҵ���Ӧ���ݽ��ѷ����¼");
            return false;
        }
        for (int i = 1; i <= tLJTempFeeClassSet.size(); i++)
        {
            if (tLJTempFeeClassSet.get(i).getPayMode().equals("2") ||
                tLJTempFeeClassSet.get(i).getPayMode().equals("3"))
            {
                payMode = tLJTempFeeClassSet.get(i).getPayMode();
                BankCode = tLJTempFeeClassSet.get(i).getBankCode();
                BankAccNo = tLJTempFeeClassSet.get(i).getChequeNo();
                break;
            }
            if (tLJTempFeeClassSet.get(i).getPayMode().equals("4"))
            {
                payMode = tLJTempFeeClassSet.get(i).getPayMode();
                BankCode = tLJTempFeeClassSet.get(i).getBankCode();
                BankAccNo = tLJTempFeeClassSet.get(i).getBankAccNo();
                AccName = tLJTempFeeClassSet.get(i).getAccName();
            }
            else
            {
                payMode = "1";
            }
        }

        return true;
    }

    /**
     * ����ӡˢ�Ų��Ҹ��˱������еĽ��ѷ�ʽ������
     * @param inPrtNo
     * @param flag (0: ��֧Ʊ�����У��ֽ�����ȼ�����. 2 ����֧Ʊ�Ľ������� 4 �������еĽ������� )
     * @return
     */
    public boolean getPayTypeForLCPol(String inPrtNo, int flag)
    {
        if (inPrtNo == null)
        {
            return false;
        }
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPrtNo(inPrtNo);
        LCPolSet tLCPolSet = tLCPolDB.query();
        if (tLCPolSet == null || tLCPolSet.size() == 0)
        {
            mErrors.addOneError("û���ҵ���Ӧ�ı���");
            return false;
        }

        String sql = "select * from ljtempfeeclass where tempfeeno in ";
        sql = sql + "(select tempfeeno from ljtempfee where EnterAccDate is not null and otherno in ('" +
              inPrtNo + "'";

        for (int n = 1; n <= tLCPolSet.size(); n++)
        {
            sql = sql + ", '" + tLCPolSet.get(n).getPolNo() + "' ";
        }
        sql = sql + ") )";

        LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
        LJTempFeeClassSet tLJTempFeeClassSet = tLJTempFeeClassDB.executeQuery(
                sql);
        if (tLJTempFeeClassSet == null || tLJTempFeeClassSet.size() == 0)
        {
            mErrors.addOneError("û���ҵ���Ӧ���ݽ��ѷ����¼");
            return false;
        }
        for (int i = 1; i <= tLJTempFeeClassSet.size(); i++)
        {
            //������ѷ�ʽ��2��3 ��֧Ʊ�����Ҵ�����Ϊ0��2
            if ((tLJTempFeeClassSet.get(i).getPayMode().equals("2") ||
                 tLJTempFeeClassSet.get(i).getPayMode().equals("3")) &&
                (flag == 0 || flag == 2))
            {
                payMode = tLJTempFeeClassSet.get(i).getPayMode();
                BankCode = tLJTempFeeClassSet.get(i).getBankCode();
                BankAccNo = tLJTempFeeClassSet.get(i).getChequeNo();
                break;
            }
            if (tLJTempFeeClassSet.get(i).getPayMode().equals("4") &&
                (flag == 0 || flag == 4))
            {
                payMode = tLJTempFeeClassSet.get(i).getPayMode();
                BankCode = tLJTempFeeClassSet.get(i).getBankCode();
                BankAccNo = tLJTempFeeClassSet.get(i).getBankAccNo();
                AccName = tLJTempFeeClassSet.get(i).getAccName();
            }
            if (tLJTempFeeClassSet.get(i).getPayMode().equals("1") &&
                (flag == 0))
            {
                payMode = "1";
            }
        }

        return true;
    }

    public String getPayMode()
    {
        if (payMode == null)
        {
            payMode = "";
        }
        return payMode;
    }


    public String getBankCode()
    {
        if (BankCode == null)
        {
            BankCode = "";
        }
        return BankCode;
    }


    public String getBankAccNo()
    {
        if (BankAccNo == null)
        {
            BankAccNo = "";
        }
        return BankAccNo;
    }


    public String getAccName()
    {
        if (AccName == null)
        {
            AccName = "";
        }
        return AccName;
    }
}
