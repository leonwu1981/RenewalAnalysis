/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import com.sinosoft.lis.schema.LMCheckFieldSchema;
import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description:
 * 公共类：根据描述来检验传入的参数中包含的字段。
 * 传入唯一的参数VData,目前VData中包含一个或多个成员即FieldCarrier类，该FieldCarrier类作为所有要检验的字段的载体.
 * 如果需要，可以在VData中放置其它成员，以便扩展使用
 * </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author liujw
 * @version 1.0
 */
public class CheckFieldCom
{
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();
    /**返回数据集合*/
//    private VData mResult = new VData();
    /**存放提示信息的数据集合*/
//    private VData mResultMsg = new VData();
    /**计算基础要素类,用于计算*/
    private Calculator mCalculator = new Calculator();
    /**保存计算要素的载体*/
    private FieldCarrier mFieldCarrier = new FieldCarrier();
    /**保存传入的描述校验信息的数据纪录*/
    private LMCheckFieldSchema mLMCheckFieldSchema = new LMCheckFieldSchema();
    /**保存根据传入的描述校验信息的数据从数据库中查询到的集合*/
    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();

    /**
     * 检验字段的主方法
     * @param InputData VData
     * @return boolean
     */
    public boolean CheckField(VData InputData)
    {
        if (!InitData(InputData))
        {
            return false;
        }
        if (!SaveFactor())
        {
            return false;
        }
        if (!QueryLMCheckFieldSet())
        {
            return false;
        }
        if (!CheckProcedure())
        {
            return false;
        }

        return true;
    }

    /**
     * 初始化传入的数据
     * @param InputData VData
     * @return boolean
     */
    private boolean InitData(VData InputData)
    {
        if (InputData == null)
        {
            return SetError("InitData", "传入参数(VData)不能为空！");
        }
        //参数载体传入
        mFieldCarrier = (FieldCarrier) InputData.getObjectByObjectName(
                "FieldCarrier", 0);
        if (mFieldCarrier == null)
        {

            return SetError("InitData", "传入参数(FieldCarrier)不能为空！");
        }
        //参数载体传入
        mLMCheckFieldSchema = (LMCheckFieldSchema) InputData.
                              getObjectByObjectName("LMCheckFieldSchema", 0);
        if (mLMCheckFieldSchema == null)
        {

            return SetError("InitData", "传入参数(LMCheckFieldSchema)不能为空！");
        }

        return true;
    }

    /**
     * 将传入参数FieldCarrier中的所有的字段-保存到计算要素类中。作用见附录A(查找"附录A"即可)
     * @return boolean
     */
    private boolean SaveFactor()
    {
        Class ClassOfFieldCarrier = mFieldCarrier.getClass(); //得到类对象
        Field[] fields = ClassOfFieldCarrier.getDeclaredFields(); //得到所有字段对象的集合
        AccessibleObject.setAccessible(fields, true); //设置所有字段都是可操作的（public,protect,private）
        for (int n = 0; n < fields.length; n++)
        {
            String fieldName = fields[n].getName(); //得到当前字段的名称
            Class classOfFieldType = fields[n].getType(); //得到当前字段的类型对象
            String fieldTypeName = classOfFieldType.getName(); //得到当前字段的类型对象的名字(Long,String...)
            if (fields[n].isAccessible()) //如果当前字段的可存取属性为真
            {
                try
                {
                    //得到当前字段的值的对象。注意用法，括号内为字段所属的实例对象
                    Object objectOfFieldValue = fields[n].get(mFieldCarrier);
                    if (objectOfFieldValue != null)
                    {
                        mCalculator.addBasicFactor(fieldName,
                                objectOfFieldValue.toString());
                    }
                }
                catch (Exception ex)
                {
                    //ex.printStackTrace();
                    System.out.println("抛出例外：" + ex.toString());
                    return SetError("SaveField", ex.toString());
                }
            }
            else
            {
                return SetError("SaveField", "传入参数的字段" + fieldName + "属性为不能存取！");
            }
        }
        return true;
    }

    private boolean QueryLMCheckFieldSet()
    {
        CachedRiskInfo cri = CachedRiskInfo.getInstance();

        mLMCheckFieldSet =
                cri.findCheckFieldByRiskCodeClone(
                        mLMCheckFieldSchema.getRiskCode(),
                        mLMCheckFieldSchema.getFieldName());

        if (mLMCheckFieldSet == null)
        {
            return SetError("GetLMCheckFieldSet", "查询CheckField描述失败！");
        }
        return true;
    }

    /**
     * 检验字段的过程(扩展时可以根据不同情况扩展)
     * @return boolean
     */
    private boolean CheckProcedure()
    {
        for (int n = 1; n <= mLMCheckFieldSet.size(); n++)
        {
            LMCheckFieldSchema tLMCheckFieldSchema = mLMCheckFieldSet.get(n);
            mCalculator.setCalCode(tLMCheckFieldSchema.getCalCode()); //添加计算编码
            String RSTR = mCalculator.calculate(); //得到计算后的结果
            //如果计算结果为空，则说明待校验字段的值错误,因为在正确的取值范围中没有取到值
            if (RSTR == null || RSTR.equals(""))
            {
                //出现错误将返回值有效标记置为N,直接跳出
                tLMCheckFieldSchema.setReturnValiFlag("N");
                mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                break;
            }
            else
            {
                //如果不需要对计算后得到的值在ValiFlag中校验
                if (tLMCheckFieldSchema.getValiFlag() == null ||
                    tLMCheckFieldSchema.getValiFlag().equals(""))
                {
                    //将返回值有效标记置为Y
                    tLMCheckFieldSchema.setReturnValiFlag("Y");
                    mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                    continue;
                }
                else //如果需要对计算后得到的值在ValiFlag中校验
                {
                    boolean valiflag = false;
//                    PubFun tPub = new PubFun();
                    String valiData = tLMCheckFieldSchema.getValiFlag(); //得到有效值
                    String[] arrValiData;
                    int index = valiData.indexOf(";");
                    if (index == -1) //如果没有分号,则认为是单独一个数.
                    {
                        arrValiData = new String[1];
                        arrValiData[0] = valiData;
                    }
                    else
                    {
                        arrValiData = PubFun.split(valiData, ";");
                    }
                    for (int i = 0; i < arrValiData.length; i++)
                    {
                        if (RSTR.trim().equals(arrValiData[i]))
                        {
                            valiflag = true;
                            tLMCheckFieldSchema.setReturnValiFlag("Y");
                            mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                            break;
                        }
                    }
                    if (!valiflag)
                    {
                        //将返回值有效标记置为N.直接跳出
                        tLMCheckFieldSchema.setReturnValiFlag("N");
                        mLMCheckFieldSet.set(n, tLMCheckFieldSchema);
                        break;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 返回LMCheckFieldSet结果集合
     * @return LMCheckFieldSet
     */
    public LMCheckFieldSet GetCheckFieldSet()
    {
        return mLMCheckFieldSet;
    }

//    /**
//     * 返回提示信息结果集合
//     * @return VData
//     */
//    private VData GetResultMsg()
//    {
//        return mResultMsg;
//    }
//
//    /**
//     * 返回所有信息结果集合--暂不用
//     * @return VData
//     */
//    private VData GetResult()
//    {
//        return mResult;
//    }


    /**
     * 错误生成
     * @param funName String
     * @param errMsg String
     * @return boolean
     */
    private boolean SetError(String funName, String errMsg)
    {
        // @@错误处理
        CError tError = new CError();
        tError.moduleName = "CheckFieldCom";
        tError.functionName = funName;
        tError.errorMessage = errMsg;
        this.mErrors.addOneError(tError);
        return false;
    }

    public static void main(String args[])
    {
//        CheckFieldCom tCheckFieldCom = new CheckFieldCom();
//        VData tVData = new VData();
//        FieldCarrier tFieldCarrier = new FieldCarrier();
//        tFieldCarrier.setPayEndYear(5); //交费期间-88|趸交
//        tFieldCarrier.setPayIntv(12); //缴费间隔-12年交
//        tFieldCarrier.setPolNo("86110020030110001111");
//        tFieldCarrier.setMult(2);
//        tFieldCarrier.setAppAge(17);
//        tFieldCarrier.setIDNo("340102650117351");
//        tFieldCarrier.setIDType("0");
//        tFieldCarrier.setRiskCode("121703");
//        tFieldCarrier.setMainPolNo("86110020030110004205");

//        tVData.add(tFieldCarrier);
//        LMCheckFieldSchema tLMCheckFieldSchema = new LMCheckFieldSchema();
//        tLMCheckFieldSchema.setRiskCode("121703");
//        tLMCheckFieldSchema.setFieldName("TB"); //投保
//        tVData.add(tLMCheckFieldSchema);
//        if (tCheckFieldCom.CheckField(tVData) == false)
//        {
//            System.out.println("error");
//        }
//        else
//        {
//            LMCheckFieldSet mLMCheckFieldSet = tCheckFieldCom.GetCheckFieldSet();
//            for (int n = 1; n <= mLMCheckFieldSet.size(); n++)
//            {
//                LMCheckFieldSchema t = mLMCheckFieldSet.get(n);
//                if (t.getReturnValiFlag().equals("N"))
//                {
//                    if (t.getMsgFlag().equals("Y"))
//                    {
//                        System.out.println(t.getMsg());
//                    }
//                }
//            }
//        }
    }
}

//附录A：
//增加基本要素,如果是手工方式
//      mCalculator.addBasicFactor("Get", mFieldCarrier.getGet() );         /** 保额 */
//      mCalculator.addBasicFactor("Mult", mFieldCarrier.getMult() );       /** 份数 */
//      mCalculator.addBasicFactor("Prem", mFieldCarrier.getPrem() );       /** 保费 */
//      mCalculator.addBasicFactor("PayIntv", mFieldCarrier.getPayIntv() ); /** 缴费间隔 */
//      mCalculator.addBasicFactor("GetIntv", mFieldCarrier.getGetIntv() ); /** 领取间隔 */
//      mCalculator.addBasicFactor("AppAge", mFieldCarrier.getAppAge() );   /** 被保人投保年龄 */
//      mCalculator.addBasicFactor("Sex", mFieldCarrier.getSex() );         /** 被保人性别 */
//      mCalculator.addBasicFactor("Job", mFieldCarrier.getJob() );         /** 被保人工种 */
//
//      mCalculator.addBasicFactor("PayEndYear", mFieldCarrier.getPayEndYear() );/** 缴费终止年期或年龄 */
//      mCalculator.addBasicFactor("PayEndYearFlag", mFieldCarrier.getPayEndYearFlag() );/** 缴费终止年期或年龄标记 */
//      mCalculator.addBasicFactor("GetYear", mFieldCarrier.getGetYear() );/** 领取开始年期或年龄 */
//      mCalculator.addBasicFactor("GetYearFlag", mFieldCarrier.getGetYearFlag() );/** 领取开始年期或年龄标记 */
//      mCalculator.addBasicFactor("Years", mFieldCarrier.getYears() );  /** 保险期间 */
//      mCalculator.addBasicFactor("InsuYear", mFieldCarrier.getInsuYear() );/** 保险期间 */
//      mCalculator.addBasicFactor("InsuYearFlag", mFieldCarrier.getInsuYearFlag() );/** 保险期间标记 */
//
//      mCalculator.addBasicFactor("Count", mFieldCarrier.getCount() );       /** 投保人数 */
//      mCalculator.addBasicFactor("RnewFlag", mFieldCarrier.getRnewFlag() ); /** 续保次数 */
//      mCalculator.addBasicFactor("AddRate", mFieldCarrier.getAddRate() );   /** 递增率 */
//      mCalculator.addBasicFactor("GDuty", mFieldCarrier.getGDuty() );      /** 责任给付编码 */
//      mCalculator.addBasicFactor("PolNo", mFieldCarrier.getPolNo() );      /** 保单号 */
//      mCalculator.addBasicFactor("FRate", mFieldCarrier.getFloatRate() );  /** 浮动费率 */
//      mCalculator.addBasicFactor("GetDutyKind", mFieldCarrier.getGetDutyKind() );/** 责任领取类型 */
//      mCalculator.addBasicFactor("RiskCode",mFieldCarrier.getRiskCode() );  /**险种编码*/
//
//      mCalculator.addBasicFactor("Interval",mFieldCarrier.getInterval() );  /**时间间隔*/
//      mCalculator.addBasicFactor("GetMoney",mFieldCarrier.getGetMoney() );  /**交退费金额*/
//      mCalculator.addBasicFactor("EdorNo",mFieldCarrier.getEdorNo() );      /**保全申请号*/
//      mCalculator.addBasicFactor("EdorType",mFieldCarrier.getEdorType() );  /**保全类型*/
//      mCalculator.addBasicFactor("GrpPolNo",mFieldCarrier.getGrpPolNo() );  /**集体保单号 */
//
//      mCalculator.addBasicFactor("Amnt",mFieldCarrier.getAmnt() );          /** 原保额--风险保额 */
//
//      mCalculator.addBasicFactor("GetStartDate", "" );                      /**起领日期*/
//      mCalculator.addBasicFactor("Grp","" );
//      mCalculator.addBasicFactor("GetFlag","" );                            /**领取标记*/
//      mCalculator.addBasicFactor("CValiDate","" );                          /**保单生效日期*/
//      mCalculator.addBasicFactor("FirstPayDate","" );                       /**首期交费日期*/
