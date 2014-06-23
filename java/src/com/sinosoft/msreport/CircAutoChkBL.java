/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.db.LMCalModeDB;
import com.sinosoft.lis.pubfun.Calculator;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LFXMLCollSchema;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统CIRC查询部分</p>
 * <p>Description: 逻辑处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircAutoChkBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;
//    private String mpassflag; //通过标记
//    private int merrcount; //错误条数
    private String mCalCode; //计算编码
//    private String mUser;
//    private FDate fDate = new FDate();
    private float mValue;

    LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
    /**计算公式表**/
//    private LMUWSchema mLMUWSchema = new LMUWSchema();
//    private LMUWSet mLMUWSet = new LMUWSet();

    private LMCalModeSet mmLMCalModeSet = new LMCalModeSet();
    private LMCalModeSet mLMCalModeSet = new LMCalModeSet();

//    private CalBase mCalBase = new CalBase();

    public CircAutoChkBL()
    {}

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //判断是不是所有数据都不成功
        int j = 0; //符合条件数据个数

        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        //得到外部传入的数据,将数据备份到本类中

        if (!getInputData(cInputData))
        {
            return false;
        }
        System.out.println("---CircAutoChkBL getInputData---");

        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("---CircAutoChkBL dealData---");
        //准备返回的数据
        prepareOutputData();

        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        //准备校验算法集合
        if (!CheckKinds())
        {
            return false;
        }

        //取保单信息
        int n = mmLMCalModeSet.size();
        if (n > 0)
        {
            int j = 0;
            mLMCalModeSet.clear();
            for (int i = 1; i <= n; i++)
            {
                //取计算编码
                LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
                tLMCalModeSchema = mmLMCalModeSet.get(i);
                mCalCode = tLMCalModeSchema.getCalCode();
                if (CheckPol() == 0)
                {
                }
                else
                {
                    j++;
                    mLMCalModeSet.add(tLMCalModeSchema);
                }
            }
        }

        return true;
    }


    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mOperate = tGlobalInput.Operator;

        mLFXMLCollSchema = (LFXMLCollSchema) cInputData.getObjectByObjectName(
                "LFXMLCollSchema", 0);

        return true;
    }

    /**
     * 核保险种信息校验,准备核保算法
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean CheckKinds()
    {
        String tsql = "";
        mmLMCalModeSet = new LMCalModeSet();
//        LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
        //查询算法编码
        tsql = "select * from LMCalMode where riskcode = '" +
               mLFXMLCollSchema.getItemCode() +
               "' and type ='R'  order by calcode";
        System.out.println(tsql);
        LMCalModeDB tLMCalModeDB = new LMCalModeDB();
        mmLMCalModeSet = tLMCalModeDB.executeQuery(tsql);
        if (tLMCalModeDB == null)
        {
            this.mErrors.copyAllErrors(tLMCalModeDB.mErrors);
            return false;
        }

        return true;
    }


    /**
     * 个人单核保
     * 输出：如果发生错误则返回false,否则返回true
     * @return float
     */
    private float CheckPol()
    {
        // 计算
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //增加基本要素
        mCalculator.addBasicFactor("ComCodeISC", mLFXMLCollSchema.getComCodeISC());
        mCalculator.addBasicFactor("ItemCode", mLFXMLCollSchema.getItemCode());
        mCalculator.addBasicFactor("RepType", mLFXMLCollSchema.getRepType());
        mCalculator.addBasicFactor("StatYear",
                                   Integer.toString(mLFXMLCollSchema.
                getStatYear()));
        mCalculator.addBasicFactor("StatMon",
                                   Integer.toString(mLFXMLCollSchema.getStatMon()));
        mCalculator.addBasicFactor("StatValue",
                                   Double.toString(mLFXMLCollSchema.
                getStatValue()));

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr == null || tStr.trim().equals(""))
        {
            mValue = 0;
        }
        else
        {
            mValue = Float.parseFloat(tStr);
        }

        System.out.println(mValue);
        return mValue;
    }

    /**
     * 准备需要保存的数据
     */
    private void prepareOutputData()
    {
        mResult.clear();
        mResult.add(mLMCalModeSet);
    }

    public VData getResult()
    {
        return mResult;
    }

}
