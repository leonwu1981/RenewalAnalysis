/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.db.LDSpotTrackDB;
import com.sinosoft.lis.db.LDSpotUWRateDB;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.vschema.LDSpotUWRateSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;

/**
 * <p>Title: </p>
 * 抽取准备类
 * <p>Description: </p>
 * 准备要抽取的数据记录和抽取方式
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author ZHUXF
 * @version 1.0
 */
public class SpotPrepare
{
    public SpotPrepare()
    {
    }

    // 错误信息
    public CErrors mErrors = new CErrors();

    //返回数据集
    public LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();

    /**
     * 随机抽取比例的数据准备
     * @param cOtherNo String
     * @param cUserCode String
     * @param cUWGrade String
     * @param cUWType String
     * @param cOtherType String
     * @param cFlag boolean
     * @return boolean
     */
    public boolean PrepareData(String cOtherNo, String cUserCode,
                               String cUWGrade, String cUWType,
                               String cOtherType, boolean cFlag)
    {
        //如果不重复抽取，则执行轨迹表查询
        if (!cFlag)
        {
            LDSpotTrackDB tLDSpotTrackDB = new LDSpotTrackDB();
            tLDSpotTrackDB.setOtherNo(cOtherNo);
            tLDSpotTrackDB.setOtherType(cOtherType);
            //如果轨迹表中存在，则返回false
            if (tLDSpotTrackDB.query().size() > 0)
            {
                return false;
            }
        }

        int tPercent = 0; //抽取比例
        boolean tFlag; //抽取状态
        String tUWType; //抽取类型

        tUWType = cUWType.substring(0, 1) + "00000";
        /**
         * 根据抽取规则，取得抽取比例
         */
        LDSpotUWRateDB tLDSpotUWRateDB = new LDSpotUWRateDB();
        LDSpotUWRateSet tLDSpotUWRateSet = new LDSpotUWRateSet();
        //可根据需要添加核保级别作为查询条件
        String tSql = "select * from LDSpotUWRate where UserCode = '" +
                      cUserCode + "' and (UWType = '" + cUWType +
                      "' or UWType = '" +
                      tUWType + "') order by UWType desc";
//        System.out.println(tSql) ;
        tLDSpotUWRateSet = tLDSpotUWRateDB.executeQuery(tSql);
        //如果查询的结果为空，则表示该核保师的抽取比例没有设置
        if (tLDSpotUWRateSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "SpotPrepare";
            tError.functionName = "search";
            tError.errorMessage = "核保师没有抽取比例描述!";
            this.mErrors.addOneError(tError);
            return false;
        }
        else
        {
            //当查询结果唯一时，直接取抽取比例
            if (tLDSpotUWRateSet.size() == 1)
            {
                tPercent = tLDSpotUWRateSet.get(1).getUWRate();
            }
            else
            {
                //当查询结果不唯一的时候，取抽取类型和传入类型相同的抽取比例
                for (int i = 1; i < tLDSpotUWRateSet.size(); i++)
                {
                    System.out.println(tLDSpotUWRateSet.get(i).getUWType());
                    if (tLDSpotUWRateSet.get(i).getUWType().compareTo(cUWType) ==
                        0)
                    {
                        tPercent = tLDSpotUWRateSet.get(i).getUWRate();
                        break;
                    }
                }
            }
        }

        //抽取计算
        SpotCheck tspotcheck = new SpotCheck();
        //由于改用返回数据集的方式，因此在抽取的时候不会出现错误
        tFlag = tspotcheck.RandomRate(cOtherNo, cOtherType, tPercent);
        //无论抽中与否，都需要取得返回的数据集
        mLDSpotTrackSet.set(tspotcheck.getLDSpotTrackSet());
        return tFlag;
    }

    /**
     * 返回数据集
     * @return LDSpotTrackDBSet
     */
    public LDSpotTrackSet getLDSpotTrackSet()
    {
        return mLDSpotTrackSet;
    }

    public static void main(String[] args)
    {
//        SpotPrepare spotprepare = new SpotPrepare();
//        spotprepare.PrepareData("001", "001", "3", "200002", "GrpContNo");
    }
}
