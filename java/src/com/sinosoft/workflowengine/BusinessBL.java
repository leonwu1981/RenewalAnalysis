/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflowengine;

//import com.sinosoft.lis.schema.LWLockSchema;
//import com.sinosoft.lis.schema.LWMissionSchema;
//import com.sinosoft.lis.schema.LWProcessSchema;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class BusinessBL
{

    public BusinessBL()
    {
    }

    /**
     * Entry from other BL
     * @return boolean
     */
    public boolean Submit()
    {
        VData tV = new VData();
        Deal(tV);
        return true;
    }

    /**
     * Deal Business Logic
     * @param tV VData
     * @return boolean
     */
    public boolean Deal(VData tV)
    {
        //deal BL

        //����׼����ҵ�����Լ������ݡ�

        //׼������һ������������Ҫ������
//        LWProcessSchema tLWP = new LWProcessSchema();
//        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//        LWLockSchema tLWLock = new LWLockSchema();

        ActivityOperator tA = new ActivityOperator();
        //�ýӿ�Ϊ���������ⲿ�ӿ�
        //tA.CreateNewMission(tLWP) ;or tA.CreateNextMission(tLWP)
        //tA.ExecuteMission();
        //���ִ�н��tA.getResult();

        //1��ִ��ĳ�����е�ҵ���߼�
        //�˴�������Ӧҵ���BLS�������ڸ�BLS�д�������͸��������
        //��BLS�е���ͨ�õ�BLS����������֤�����һ����

        //2��ִ�й�������Finished������
        //tA.mLWLockSchema Ϊ�������
        //tA.mLWProcessSchema Ϊ����ѡ��Ĳ���������ò�����mLWLockSchema�еĲ���ƥ�䣬���ٴ����ݿ��в�ѯ������
        //����������LWProcess���в�ѯ��������ݡ�
        //�ýӿ�Ϊ���������ⲿ�ӿ�
        tA.ActivityFinished();
        return true;
    }
}
