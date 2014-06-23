/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */

package com.sinosoft.audit;
import com.sinosoft.utility.*;
/**
 * <p>
 * ClassName: alService
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author guoxiang
 * @version 1.0
 */
public interface CalService {
	/**
	 * submitData
	 * 通用接口：接收传入数据，并对数据进行处理
	 * @param VData cInputData
	 * @param String cOperate
	 */
  public boolean submitData(VData cInputData, String cOperate);
	/**
	 * getResult
	 * 数据返回
	 */
  public VData getResult();
	/**
	 * getErrors
	 */
  public CErrors getErrors();
}
