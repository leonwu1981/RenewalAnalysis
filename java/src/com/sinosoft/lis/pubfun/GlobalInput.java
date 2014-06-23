/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 全局变量区</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author YT
 * @version 1.0
 */

public class GlobalInput
{
    /** 当前操作员 */
    public String Operator;
    /** 当前操作员姓名 */
    public String OperatorName;
    /** 当前登陆机构 */
    public String ManageCom;
    /** 当前登陆用户所属机构 */
    public String ComCode; 
    /** 当前用户类型 */
    public String UserType;    
    /** 企业用户类型 */
    public String CropUserType;
    /** 当前用户企业代码 */
    public String CropCode;
    /** 当前用户工作单位 */
    public String OrganComCode;
    /** 超级权限标志 */
    public String SuperPopedomFlag;
    /** 保险公司用户类型 */
    public String InsurerUserType;
    /** 是否为外包用户 1 是 */
    public String OutUserFlag;
    
//  /** 当前险种 */
//  public String RiskCode;
//  /** 当前险种版本 */
//  public String RiskVersion;

    public GlobalInput()
    {
    }

    /**
     * 两个GlobalInput对象之间的直接复制
     * @param cGlobalInput 包含有具体值的GlobalInput对象
     */
    public void setSchema(GlobalInput cGlobalInput)
    {
        //获取登陆用户基础信息：用户编码、管理机构等
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
