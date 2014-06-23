/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.db.LAAgentDB;
import com.sinosoft.lis.db.LDCodeDB;
import com.sinosoft.lis.db.LMRiskDB;

/*
 代理人名称
 险种名称
 管理机构名称
 生存领取方式名称
 缴费方式名称
 缴费间隔名称
 银行代码名称
 民族名称
 证件类型名称
 医院代码名称
 */
/**
 * 通过代码取得代码对应的中文名称
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ChangeCodetoName
{

    public ChangeCodetoName()
    {

    }

    public static void main(String[] args)
    {
//        ChangeCodetoName tChangeCodetoName = new ChangeCodetoName();
//        for (int i = 0; i < 9; i++)
//        {
//            tChangeCodetoName.getRiskName("212401");
//            tChangeCodetoName.getBankCodeName("0301");
//        }
    }

    /**
     * 得到代理人名称
     * @param AgentCode
     * @return
     */
    public static String getAgentName(String AgentCode)
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(AgentCode);
        if (!tLAAgentDB.getInfo())
        {
            return null;
        }
        return tLAAgentDB.getName();
    }

    /**
     * 得到险种名称
     * @param RiskCode
     * @return
     */
    public static String getRiskName(String RiskCode)
    {
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setRiskCode(RiskCode);
        if (!tLMRiskDB.getInfo())
        {
            return null;
        }
        return tLMRiskDB.getRiskName();
    }

    /**
     * 得到管理机构名称
     * @param RiskCode
     * @return
     */
    public static String getManageName(String ManageCode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(ManageCode);
        tLDCodeDB.setCodeType("station");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();

    }

    /**
     * 得到红利领取方式名称
     * @param RiskCode
     * @return
     */
    public static String getBonusGetModeName(String BonusGetMode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(BonusGetMode);
        tLDCodeDB.setCodeType("livegetmode");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到生存领取方式名称
     * @param RiskCode
     * @return
     */
    public static String getLiveGetModeName(String LiveGetMode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(LiveGetMode);
        tLDCodeDB.setCodeType("livegetmode");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到缴费方式名称
     * @param RiskCode
     * @return
     */
    public static String getPayModeName(String PayMode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(PayMode);
        tLDCodeDB.setCodeType("paymode");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到缴费间隔名称
     * @param RiskCode
     * @return
     */
    public static String getPayIntvName(String PayIntv)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(PayIntv);
        tLDCodeDB.setCodeType("payintv");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到银行代码名称
     * @param RiskCode
     * @return
     */
    public static String getBankCodeName(String BankCode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(BankCode);
        tLDCodeDB.setCodeType("bank");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到民族名称
     * @param RiskCode
     * @return
     */
    public static String getNationalityName(String Nationality)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(Nationality);
        tLDCodeDB.setCodeType("nationality");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到证件类型名称
     * @param RiskCode
     * @return
     */
    public static String getIdTypeName(String IdType)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(IdType);
        tLDCodeDB.setCodeType("idtype");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到医院代码名称
     * @param RiskCode
     * @return
     */
    public static String getHospitalCodeName(String HospitalCode)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(HospitalCode);
        tLDCodeDB.setCodeType("hospitalcode");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到交费位置名称
     * @param RiskCode
     * @return
     */
    public static String getPayLocationName(String PayLocation)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(PayLocation);
        tLDCodeDB.setCodeType("paylocation");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }

    /**
     * 得到 sex名称
     * @param RiskCode
     * @return
     */
    public static String getSexName(String Sex)
    {
        String sexName = "不详";
        if (Sex == null)
        {
            return sexName;
        }
        if (Sex.equals("0"))
        {
            return "男";
        }
        if (Sex.equals("1"))
        {
            return "女";
        }
        return sexName;
    }

    /**
     * 得到销售渠道
     * @param SaleChnl
     * @return
     */
    public static String getSaleChnl(String SaleChnl)
    {
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCode(SaleChnl);
        tLDCodeDB.setCodeType("salechnl");
        if (!tLDCodeDB.getInfo())
        {
            return null;
        }
        return tLDCodeDB.getCodeName();
    }
}
