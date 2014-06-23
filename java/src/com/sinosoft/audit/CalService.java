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
	 * ͨ�ýӿڣ����մ������ݣ��������ݽ��д���
	 * @param VData cInputData
	 * @param String cOperate
	 */
  public boolean submitData(VData cInputData, String cOperate);
	/**
	 * getResult
	 * ���ݷ���
	 */
  public VData getResult();
	/**
	 * getErrors
	 */
  public CErrors getErrors();
}
