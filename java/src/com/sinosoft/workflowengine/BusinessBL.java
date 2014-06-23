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

        //首先准备好业务处理自己的数据。

        //准备创建一个新任务所需要的数据
//        LWProcessSchema tLWP = new LWProcessSchema();
//        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//        LWLockSchema tLWLock = new LWLockSchema();

        ActivityOperator tA = new ActivityOperator();
        //该接口为工作流的外部接口
        //tA.CreateNewMission(tLWP) ;or tA.CreateNextMission(tLWP)
        //tA.ExecuteMission();
        //获得执行结果tA.getResult();

        //1。执行某个类中的业务逻辑
        //此处调用相应业务的BLS，并且在该BLS中创建任务和该任务的锁
        //在BLS中调用通用的BLS处理类来保证事务的一致性

        //2。执行工作流的Finished操作。
        //tA.mLWLockSchema 为必须参数
        //tA.mLWProcessSchema 为可以选择的参数，如果该参数和mLWLockSchema中的参数匹配，则不再从数据库中查询，否则
        //根据主键在LWProcess表中查询到相关数据。
        //该接口为工作流的外部接口
        tA.ActivityFinished();
        return true;
    }
}
