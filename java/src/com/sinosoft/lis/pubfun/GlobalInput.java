/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ȫ�ֱ�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author YT
 * @version 1.0
 */

public class GlobalInput
{
    /** ��ǰ����Ա */
    public String Operator;
    /** ��ǰ����Ա���� */
    public String OperatorName;
    /** ��ǰ��½���� */
    public String ManageCom;
    /** ��ǰ��½�û��������� */
    public String ComCode; 
    /** ��ǰ�û����� */
    public String UserType;    
    /** ��ҵ�û����� */
    public String CropUserType;
    /** ��ǰ�û���ҵ���� */
    public String CropCode;
    /** ��ǰ�û�������λ */
    public String OrganComCode;
    /** ����Ȩ�ޱ�־ */
    public String SuperPopedomFlag;
    /** ���չ�˾�û����� */
    public String InsurerUserType;
    /** �Ƿ�Ϊ����û� 1 �� */
    public String OutUserFlag;
    
//  /** ��ǰ���� */
//  public String RiskCode;
//  /** ��ǰ���ְ汾 */
//  public String RiskVersion;

    public GlobalInput()
    {
    }

    /**
     * ����GlobalInput����֮���ֱ�Ӹ���
     * @param cGlobalInput �����о���ֵ��GlobalInput����
     */
    public void setSchema(GlobalInput cGlobalInput)
    {
        //��ȡ��½�û�������Ϣ���û����롢���������
        this.Operator = cGlobalInput.Operator;
        this.ComCode = cGlobalInput.ComCode;
        this.ManageCom = cGlobalInput.ManageCom;
        this.CropCode = cGlobalInput.CropCode;
        this.OrganComCode = cGlobalInput.OrganComCode;
        this.UserType = cGlobalInput.UserType;
        this.CropUserType = cGlobalInput.CropUserType;
        this.InsurerUserType = cGlobalInput.InsurerUserType;
        this.SuperPopedomFlag = cGlobalInput.SuperPopedomFlag;
        this.OperatorName = cGlobalInput.OperatorName;
        this.OutUserFlag = cGlobalInput.OutUserFlag;
    }
}
