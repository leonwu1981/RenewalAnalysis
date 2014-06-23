/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: 民生核心业务系统</p>
 * <p>Description: </p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: 中科软科技</p>
 * @author guoxiang
 * @version 1.0
 */
public interface CalService
{
    boolean submitData(VData cInputData, String cOperate);

    VData getResult();

    CErrors getErrors();
}
