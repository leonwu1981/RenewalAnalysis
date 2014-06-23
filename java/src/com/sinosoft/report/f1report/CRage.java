/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.report.f1report;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004.11.2</p>
 * <p>Company: sinosoft</p>
 * @author sangood
 * @version 1.0
 */
public class CRage
{
    public int m_Left;
    public int m_Top;
    public int m_Right;
    public int m_Buttom;


    /**
     *  当=0时有错误，因为作为sheet的一个cell，其left,top,,right,buttom应改初始化为而非默认的0
     **/
    public CRage()
    {
        m_Left = 1;
        m_Top = 1;
        m_Right = 1;
        m_Buttom = 1;
    }
}
