package com.sinosoft.workflow.ask;

import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class AskFirstActivityCheck
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();

    public AskFirstActivityCheck()
    {
    }


    /**
     * submitData
     *
     * @param mInputData VData
     * @param string String
     * @return boolean
     */
    public boolean submitData(VData mInputData, String string)
    {
        return true;
    }


    /**
     * getResult
     *
     * @return VData
     */
    public VData getResult()
    {
        return null;
    }

}
