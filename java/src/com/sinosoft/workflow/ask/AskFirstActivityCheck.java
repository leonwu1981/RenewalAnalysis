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
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
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
