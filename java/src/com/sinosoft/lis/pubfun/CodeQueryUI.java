/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */

public class CodeQueryUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    public CodeQueryUI()
    {
    }

    /**
     * 传输数据的公共方法
     * <p><b>Example: </b><p>
     * <p>CodeQueryUI tCodeQueryUI=new CodeQueryUI();<p>
     * <p>VData tData=new VData();<p>
     * <p>LDCodeSchema tLDCodeSchema =new LDCodeSchema();<p>
     * <p>tLDCodeSchema.setCodeType("sex");<p>
     * <p>tData.add(tLDCodeSchema);<p>
     * <p>tCodeQueryUI.submitData(tData,"QUERY||MAIN");<p>
     * @param cInputData 作为数据容器的VData对象
     * @param cOperate 操作符标志
     * @return 如果后台数据处理操作成功，将结果存在内部VData对象中，返回true；处理失败则返回false
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;
        CodeQueryBL tCodeQueryBL = new CodeQueryBL();
        if (tCodeQueryBL.submitData(cInputData, mOperate))
        {
            mInputData = tCodeQueryBL.getResult();
            return true;
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tCodeQueryBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "CodeQueryUI";
            tError.functionName = "submitData";
            tError.errorMessage = "数据查询失败!";
            this.mErrors.addOneError(tError);
            mInputData.clear();
            return false;
        }
    }


    /**
     * 数据输出方法，供外界获取数据处理结果
     * @return 包含有数据查询结果字符串的VData对象
     */
    public VData getResult()
    {
        return mInputData;
    }

    public static void main(String[] args)
    {
//        GlobalInput tGlobalInput = new GlobalInput();
//        tGlobalInput.ManageCom = "001";
//        CodeQueryUI tCodeQueryUI = new CodeQueryUI();
//        VData tData = new VData();
//        LDCodeSchema tLDCodeSchema = new LDCodeSchema();
//        tLDCodeSchema.setCodeType("agentcode");
//        tData.add(tLDCodeSchema);
//        tData.add(tGlobalInput);
//        TransferData tTransferData = new TransferData();
//        tData.add(tTransferData);
//        tCodeQueryUI.submitData(tData, "QUERY||MAIN");
//        tData = tCodeQueryUI.getResult();
//        String tStr = "";
//        tStr = (String) tData.getObject(0);
//        tStr = StrTool.unicodeToGBK(tStr);
//        System.out.println("result:" + tStr);
    }
}
