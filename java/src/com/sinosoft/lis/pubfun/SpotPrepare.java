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
 * ��ȡ׼����
 * <p>Description: </p>
 * ׼��Ҫ��ȡ�����ݼ�¼�ͳ�ȡ��ʽ
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

    // ������Ϣ
    public CErrors mErrors = new CErrors();

    //�������ݼ�
    public LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();

    /**
     * �����ȡ����������׼��
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
        //������ظ���ȡ����ִ�й켣���ѯ
        if (!cFlag)
        {
            LDSpotTrackDB tLDSpotTrackDB = new LDSpotTrackDB();
            tLDSpotTrackDB.setOtherNo(cOtherNo);
            tLDSpotTrackDB.setOtherType(cOtherType);
            //����켣���д��ڣ��򷵻�false
            if (tLDSpotTrackDB.query().size() > 0)
            {
                return false;
            }
        }

        int tPercent = 0; //��ȡ����
        boolean tFlag; //��ȡ״̬
        String tUWType; //��ȡ����

        tUWType = cUWType.substring(0, 1) + "00000";
        /**
         * ���ݳ�ȡ����ȡ�ó�ȡ����
         */
        LDSpotUWRateDB tLDSpotUWRateDB = new LDSpotUWRateDB();
        LDSpotUWRateSet tLDSpotUWRateSet = new LDSpotUWRateSet();
        //�ɸ�����Ҫ��Ӻ˱�������Ϊ��ѯ����
        String tSql = "select * from LDSpotUWRate where UserCode = '" +
                      cUserCode + "' and (UWType = '" + cUWType +
                      "' or UWType = '" +
                      tUWType + "') order by UWType desc";
//        System.out.println(tSql) ;
        tLDSpotUWRateSet = tLDSpotUWRateDB.executeQuery(tSql);
        //�����ѯ�Ľ��Ϊ�գ����ʾ�ú˱�ʦ�ĳ�ȡ����û������
        if (tLDSpotUWRateSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "SpotPrepare";
            tError.functionName = "search";
            tError.errorMessage = "�˱�ʦû�г�ȡ��������!";
            this.mErrors.addOneError(tError);
            return false;
        }
        else
        {
            //����ѯ���Ψһʱ��ֱ��ȡ��ȡ����
            if (tLDSpotUWRateSet.size() == 1)
            {
                tPercent = tLDSpotUWRateSet.get(1).getUWRate();
            }
            else
            {
                //����ѯ�����Ψһ��ʱ��ȡ��ȡ���ͺʹ���������ͬ�ĳ�ȡ����
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

        //��ȡ����
        SpotCheck tspotcheck = new SpotCheck();
        //���ڸ��÷������ݼ��ķ�ʽ������ڳ�ȡ��ʱ�򲻻���ִ���
        tFlag = tspotcheck.RandomRate(cOtherNo, cOtherType, tPercent);
        //���۳�����񣬶���Ҫȡ�÷��ص����ݼ�
        mLDSpotTrackSet.set(tspotcheck.getLDSpotTrackSet());
        return tFlag;
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
//        SpotPrepare spotprepare = new SpotPrepare();
//        spotprepare.PrepareData("001", "001", "3", "200002", "GrpContNo");
    }
}
