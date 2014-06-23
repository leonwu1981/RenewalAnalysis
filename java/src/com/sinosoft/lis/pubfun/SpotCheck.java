/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.util.Random;

import com.sinosoft.lis.schema.LDSpotTrackSchema;
import com.sinosoft.lis.vschema.LDSpotTrackSet;

/**
 * <p>Title: </p>
 * 随机抽取核心
 * <p>Description: </p>
 * 随机抽取方法描述
 * 1。按比例抽取集合内的数据
 * 2。按比率判定是否被抽取到
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author ZHUXF
 * @version 1.0
 */
public class SpotCheck
{
    public SpotCheck()
    {
    }

    //返回数据集
    public LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();

    /**
     * 随机选取数
     * 按百分率选取是否选中
     * @param cOtherNo String
     * @param cOtherType String
     * @param cPercent int
     * @return boolean
     */
    public boolean RandomRate(String cOtherNo, String cOtherType, int cPercent)
    {
        //随机抽取轨迹表
        LDSpotTrackSchema tLDSpotTrackSchema = new LDSpotTrackSchema();

        //获取抽取批次号
        String tSpotID = PubFun1.CreateMaxNo("SpotCheck", 20);

        tLDSpotTrackSchema.setSpotID(tSpotID);
        tLDSpotTrackSchema.setOtherNo(cOtherNo);
        tLDSpotTrackSchema.setOtherType(cOtherType);
        tLDSpotTrackSchema.setSpotType("");
        tLDSpotTrackSchema.setMakeDate(PubFun.getCurrentDate());
        tLDSpotTrackSchema.setMakeTime(PubFun.getCurrentTime());

        Random rand = new Random();
//        //根据随机比率得出基数
//        int tBase = Math.round(100 / cPercent);
//        //调用java自带函数random生成0－基数范围内的一个随机数
//        int tSelect = rand.nextInt(tBase);
//        if (tSelect == 1)
//        {
//            //判定那段的数组记录是抽中的
//            tLDSpotTrackSchema.setSpotFlag("1");
//        }
//        else
//        {
//            //判定那段的数组记录是抽中的
//            tLDSpotTrackSchema.setSpotFlag("0");
//        }
        /**
         * 由于上面的算法不够精确，导致tBase超过50后，永远都被抽中，因此改进算法
         */
        //调用java自带函数random生成1－100范围内的一个随机数
        int tSelect = rand.nextInt(100);
        //如果抽取的随机数比抽取比例小则表示抽中，此方法相对精确
        //坏处是抽取比例必须是整数，目前只支持百分比
        if (tSelect <= cPercent)
        {
            if (cPercent == 0)
            {
                //比例为0，则抽不中数据
                tLDSpotTrackSchema.setSpotFlag("0");
                //将需要操作的数据，放入到返回数据集中
                mLDSpotTrackSet.add(tLDSpotTrackSchema);
                return false;
            }
            else
            {
                //判定那段的数组记录是抽中的
                tLDSpotTrackSchema.setSpotFlag("1");
                //将需要操作的数据，放入到返回数据集中
                mLDSpotTrackSet.add(tLDSpotTrackSchema);
                return true;
            }
        }
        else
        {
            //判定那段的数组记录是抽中的
            tLDSpotTrackSchema.setSpotFlag("0");
            //将需要操作的数据，放入到返回数据集中
            mLDSpotTrackSet.add(tLDSpotTrackSchema);
            return false;
        }
    }

    /**
     * 随机选取数
     * 按集合的百分比选取
     * @param cGroup String[]
     * @param cOtherType String
     * @param cCount int
     * @return String[]
     */
    public String[] RandomPercent(String[] cGroup, String cOtherType, int cCount)
    {
        //这里没有做重复抽取的校验，如果需要可在以后添加
        //而且由于传入的是多条，因此如何判定重复抽取的返回值需要讨论

        String[] tPitchOn;
        tPitchOn = new String[cCount]; //准备返回的抽取集合数组

        int tLength = cGroup.length - 1; //准备的抽取集合数组
        int tNumber; //随机选择数组的位置号

        Random rand = new Random();
        for (int i = 0; i < cCount; i++)
        {
            tNumber = rand.nextInt(tLength); //获得当前随机抽中的数组序号
            tPitchOn[i] = cGroup[tNumber]; //将对应数组的信息放到要返回的数组中
            cGroup[tNumber] = cGroup[tLength]; //将选中的数组序号中的信息替换
            cGroup[tLength] = tPitchOn[i]; //将抽中的信息放到最后一位
            tLength -= 1; //将数组的范围缩小一个
        }

        //随机抽取轨迹表
        LDSpotTrackSchema tLDSpotTrackSchema = new LDSpotTrackSchema();
//        LDSpotTrackSet tLDSpotTrackSet = new LDSpotTrackSet();
        //获取抽取批次号
        String tSpotID = PubFun1.CreateMaxNo("SpotCheck", 20);
        for (int j = 0; j < cGroup.length; j++)
        {
            tLDSpotTrackSchema = new LDSpotTrackSchema();
            tLDSpotTrackSchema.setSpotID(tSpotID);
            tLDSpotTrackSchema.setOtherNo(cGroup[j]);
            tLDSpotTrackSchema.setOtherType(cOtherType);
            tLDSpotTrackSchema.setSpotType("");
            //判定那段的数组记录是抽中的
            if (j >= cGroup.length - cCount)
            {
                tLDSpotTrackSchema.setSpotFlag("1");
            }
            else
            {
                tLDSpotTrackSchema.setSpotFlag("0");
            }
            tLDSpotTrackSchema.setMakeDate(PubFun.getCurrentDate());
            tLDSpotTrackSchema.setMakeTime(PubFun.getCurrentTime());
            //将需要操作的数据，放入到返回数据集中
            mLDSpotTrackSet.add(tLDSpotTrackSchema);
        }

        return tPitchOn;
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
//        Random rand = new Random();
//        for (int i=51;i<=100;i++){
//            System.out.print(i+"  =  ");
//            System.out.println(Math.round(100 /i));
//        }
//        SpotCheck spotcheck = new SpotCheck();
//        spotcheck.RandomRate("11", "dff", 100);
//        float f;
//        f = (float) 5;
//        String[] tPitchOn;
//        tPitchOn = new String[20];
//        for (int i = 0; i < 20; i++)
//        {
//            tPitchOn[i] = String.valueOf(i);
//        }
//        String[] a;
    }
}
