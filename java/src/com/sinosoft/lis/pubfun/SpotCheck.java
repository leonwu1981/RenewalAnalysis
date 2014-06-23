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
 * �����ȡ����
 * <p>Description: </p>
 * �����ȡ��������
 * 1����������ȡ�����ڵ�����
 * 2���������ж��Ƿ񱻳�ȡ��
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

    //�������ݼ�
    public LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();

    /**
     * ���ѡȡ��
     * ���ٷ���ѡȡ�Ƿ�ѡ��
     * @param cOtherNo String
     * @param cOtherType String
     * @param cPercent int
     * @return boolean
     */
    public boolean RandomRate(String cOtherNo, String cOtherType, int cPercent)
    {
        //�����ȡ�켣��
        LDSpotTrackSchema tLDSpotTrackSchema = new LDSpotTrackSchema();

        //��ȡ��ȡ���κ�
        String tSpotID = PubFun1.CreateMaxNo("SpotCheck", 20);

        tLDSpotTrackSchema.setSpotID(tSpotID);
        tLDSpotTrackSchema.setOtherNo(cOtherNo);
        tLDSpotTrackSchema.setOtherType(cOtherType);
        tLDSpotTrackSchema.setSpotType("");
        tLDSpotTrackSchema.setMakeDate(PubFun.getCurrentDate());
        tLDSpotTrackSchema.setMakeTime(PubFun.getCurrentTime());

        Random rand = new Random();
//        //����������ʵó�����
//        int tBase = Math.round(100 / cPercent);
//        //����java�Դ�����random����0��������Χ�ڵ�һ�������
//        int tSelect = rand.nextInt(tBase);
//        if (tSelect == 1)
//        {
//            //�ж��Ƕε������¼�ǳ��е�
//            tLDSpotTrackSchema.setSpotFlag("1");
//        }
//        else
//        {
//            //�ж��Ƕε������¼�ǳ��е�
//            tLDSpotTrackSchema.setSpotFlag("0");
//        }
        /**
         * ����������㷨������ȷ������tBase����50����Զ�������У���˸Ľ��㷨
         */
        //����java�Դ�����random����1��100��Χ�ڵ�һ�������
        int tSelect = rand.nextInt(100);
        //�����ȡ��������ȳ�ȡ����С���ʾ���У��˷�����Ծ�ȷ
        //�����ǳ�ȡ����������������Ŀǰֻ֧�ְٷֱ�
        if (tSelect <= cPercent)
        {
            if (cPercent == 0)
            {
                //����Ϊ0����鲻������
                tLDSpotTrackSchema.setSpotFlag("0");
                //����Ҫ���������ݣ����뵽�������ݼ���
                mLDSpotTrackSet.add(tLDSpotTrackSchema);
                return false;
            }
            else
            {
                //�ж��Ƕε������¼�ǳ��е�
                tLDSpotTrackSchema.setSpotFlag("1");
                //����Ҫ���������ݣ����뵽�������ݼ���
                mLDSpotTrackSet.add(tLDSpotTrackSchema);
                return true;
            }
        }
        else
        {
            //�ж��Ƕε������¼�ǳ��е�
            tLDSpotTrackSchema.setSpotFlag("0");
            //����Ҫ���������ݣ����뵽�������ݼ���
            mLDSpotTrackSet.add(tLDSpotTrackSchema);
            return false;
        }
    }

    /**
     * ���ѡȡ��
     * �����ϵİٷֱ�ѡȡ
     * @param cGroup String[]
     * @param cOtherType String
     * @param cCount int
     * @return String[]
     */
    public String[] RandomPercent(String[] cGroup, String cOtherType, int cCount)
    {
        //����û�����ظ���ȡ��У�飬�����Ҫ�����Ժ����
        //�������ڴ�����Ƕ������������ж��ظ���ȡ�ķ���ֵ��Ҫ����

        String[] tPitchOn;
        tPitchOn = new String[cCount]; //׼�����صĳ�ȡ��������

        int tLength = cGroup.length - 1; //׼���ĳ�ȡ��������
        int tNumber; //���ѡ�������λ�ú�

        Random rand = new Random();
        for (int i = 0; i < cCount; i++)
        {
            tNumber = rand.nextInt(tLength); //��õ�ǰ������е��������
            tPitchOn[i] = cGroup[tNumber]; //����Ӧ�������Ϣ�ŵ�Ҫ���ص�������
            cGroup[tNumber] = cGroup[tLength]; //��ѡ�е���������е���Ϣ�滻
            cGroup[tLength] = tPitchOn[i]; //�����е���Ϣ�ŵ����һλ
            tLength -= 1; //������ķ�Χ��Сһ��
        }

        //�����ȡ�켣��
        LDSpotTrackSchema tLDSpotTrackSchema = new LDSpotTrackSchema();
//        LDSpotTrackSet tLDSpotTrackSet = new LDSpotTrackSet();
        //��ȡ��ȡ���κ�
        String tSpotID = PubFun1.CreateMaxNo("SpotCheck", 20);
        for (int j = 0; j < cGroup.length; j++)
        {
            tLDSpotTrackSchema = new LDSpotTrackSchema();
            tLDSpotTrackSchema.setSpotID(tSpotID);
            tLDSpotTrackSchema.setOtherNo(cGroup[j]);
            tLDSpotTrackSchema.setOtherType(cOtherType);
            tLDSpotTrackSchema.setSpotType("");
            //�ж��Ƕε������¼�ǳ��е�
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
            //����Ҫ���������ݣ����뵽�������ݼ���
            mLDSpotTrackSet.add(tLDSpotTrackSchema);
        }

        return tPitchOn;
    }

    /**
     * �������ݼ�
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
