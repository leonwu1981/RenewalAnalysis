/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.util.Vector;

import com.sinosoft.lis.db.LMCheckFieldDB;
import com.sinosoft.lis.schema.LMCheckFieldSchema;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;


/**
 * <p>Title: 校验程序</p>
 * <p>Description: 通过描述方法实现校验功能</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft </p>
 * @author LL
 * @version 1.1
 */
public class PubCheckField
{
    /** 全局变量定义 */
    /** 用来存放计算SQL时用到的数据 */
    private TransferData mCalBaseValue = new TransferData();


    /** 用来存放查询LMCheckField表用到的数据*/
    private LMCheckFieldSchema mCalRelValue = new LMCheckFieldSchema();


    /** 用来存放校要用到的业务校验集合容器*/
    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();


    /** 用来存放检查过程中发现的错误信息，把此错误信息返回给前台*/
    private VData mResultMess = new VData();


    /** 用来存放检查过程中发现的错误信息容器，前台可以通过调用某方法获得此容器中数据*/
    private LMCheckFieldSet mResultMessSet = new LMCheckFieldSet();


    /** 存放前台传过来的数据容器 */
//    private VData mInputData;


    /** 数据操作字符串 */
    private String mOperate;


    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    public PubCheckField()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //保存操作符
        this.mOperate = cOperate;

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败PubCheckField-->getInputData!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //进行业务处理
        boolean tFlag = dealData();

        return tFlag;
    }


    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        if (this.mOperate.equals("CKBYFIELD"))
        {
            this.mCalBaseValue = (TransferData) cInputData.getObject(0);
            this.mCalRelValue = (LMCheckFieldSchema) cInputData.getObject(1);
            return true;
        }
        else if (this.mOperate.equals("CKBYSET"))
        {
            this.mCalBaseValue = (TransferData) cInputData.getObject(0);
            this.mLMCheckFieldSet = (LMCheckFieldSet) cInputData.getObject(1);
            //修改: 2004-11-12 LL
            //修改原因:如果校验集合中没有记录则直接返回true
//      if (this.mLMCheckFieldSet.size() == 0)
//      {
//        // @@错误处理
//        CError tError = new CError();
//        tError.moduleName = "PubCheckField";
//        tError.functionName = "dealData";
//        tError.errorMessage = "校验集合中没有任何校验数据！";
//        this.mErrors.addOneError(tError);
//        System.out.println("校验集合中没有任何校验数据！");
//        return false;
//      }
        }
        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        //1 - 通过CKBYFIELD获得校验数据
        if (this.mOperate.equals("CKBYFIELD"))
        {
            if (!this.getSetByField())
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "PubCheckField";
                tError.functionName = "dealData";
                tError.errorMessage = "数据处理失败PubCheckField-->setBaseValue!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        //修改: 2004-11-12 LL
        //修改原因:如果校验集合中没有记录则直接返回true
        if (this.mLMCheckFieldSet.size() == 0)
        {
            System.out.println("校验集合中没有任何校验数据！");
            return true;
        }

        //2 - 设置计算时要用到的参数的值
        Calculator tCal = new Calculator();
        if (!setBaseValue(tCal))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "dealData";
            tError.errorMessage = "数据处理失败PubCheckField-->setBaseValue!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //3 - 根据要检测的指标集合对指标逐个进行校验
        LMCheckFieldSchema tLMCheckFieldSchema;
        String tReturn = "";
        String tResult[];
        boolean tFlag = false;
        //用来记录在整个校验过程中是否发现错误标志位
        boolean tLastFlag = true;
        for (int i = 1; i <= this.mLMCheckFieldSet.size(); i++)
        {
            tFlag = false;
            tLMCheckFieldSchema = new LMCheckFieldSchema();
            tLMCheckFieldSchema.setSchema(this.mLMCheckFieldSet.get(i));
            tCal.setCalCode(tLMCheckFieldSchema.getCalCode());
            tReturn = tCal.calculate();
            System.out.println("计算结果为：" + tReturn);
            if (tCal.mErrors.needDealError())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tCal.mErrors);
                CError tError = new CError();
                tError.moduleName = "PubCheckField";
                tError.functionName = "dealData";
                tError.errorMessage = "考核指标SQL计算出错!";
                this.mErrors.addOneError(tError);
                System.out.println("Error:" + this.mErrors.getFirstError());
                return false;
            }

            //查询LMCheckField表的有效结果集字段ValiFlag，拆分结果集，查找结果是否在
            //结果集合之中
            System.out.println("结果集为：" + tLMCheckFieldSchema.getValiFlag());
            tResult = PubFun.split(tLMCheckFieldSchema.getValiFlag(), ";");
            //判断校验结果是否在结果集中
            for (int j = 0; j < tResult.length; j++)
            {
                if (tReturn != null && tReturn.equals(tResult[j]))
                {
                    tFlag = true; //指标集中有结果
                    break;
                }
            }
            //如果指标集中不能找到结果
            if (!tFlag)
            {
                tLastFlag = false;
            }
            //判断校验结果是否正确
            if (!tFlag)
            { //返回结果不在结果集中
                //判断是否需要记录错误信息
                if (tLMCheckFieldSchema.getMsgFlag().equals("Y"))
                {
                    String tStr = new String();
                    tStr = tLMCheckFieldSchema.getMsg();
                    //保存错误信息
                    this.mResultMess.add(tStr);
                    //保存错误信息到容器中
                    this.mResultMessSet.add(tLMCheckFieldSchema);
                    System.out.println("错误信息：" + tLMCheckFieldSchema.getMsg());
                }

                //判断是否需要进一步处理其他校验
                //如果不需要进一步处理其他校验，直接返回错误，不再进行其他校验
                if (tLMCheckFieldSchema.getReturnValiFlag().equals("N"))
                {
                    return tLastFlag;
                }
            }

        }

        return tLastFlag;
    }


    /**
     * 设置计算时要用到的参数的值
     * @param tCal Calculator
     * @return boolean
     */
    private boolean setBaseValue(Calculator tCal)
    {
        Vector tName = this.mCalBaseValue.getValueNames();
        for (int i = 0; i < tName.size(); i++)
        {
            tCal.addBasicFactor(tName.get(i).toString(),
                                this.mCalBaseValue.getValueByName(tName.get(i)).
                                toString());
        }

        return true;
    }


    /**
     * 返回处理过程中记录下来的不满足条件的记录
     * @return VData
     */
    public VData getResultMess()
    {
        return mResultMess;
    }


    /**
     * 返回处理过程中记录下来的不满足条件的记录容器
     * @return LMCheckFieldSet
     */
    public LMCheckFieldSet getResultMessSet()
    {
        return mResultMessSet;
    }

    /**
     * 返回处理过程中记录下来的不满足条件的记录
     * @return boolean
     */
    private boolean getSetByField()
    {
        //三个参数不能同时为空
        if (!((this.mCalRelValue.getRiskCode() != null &&
               !this.mCalRelValue.getRiskCode().trim().equals(""))
              ||
              (this.mCalRelValue.getRiskVer() != null &&
               !this.mCalRelValue.getRiskVer().trim().equals(""))
              ||
              (this.mCalRelValue.getFieldName() != null &&
               !this.mCalRelValue.getFieldName().trim().equals(""))
            )
                )
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "getSetByField";
            tError.errorMessage = "查询校验集合时必须录入相应条件！";
            this.mErrors.addOneError(tError);
            System.out.println("查询校验集合时必须录入相应条件！");
            return false;
        }
        //1 - 在LMCheckField表中查询要校验的记录集合
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        String tSql = "select * from LMCheckField where ";
        //根据录入条件拼写SQL语句
        if (this.mCalRelValue.getRiskCode() != null
            && !this.mCalRelValue.getRiskCode().trim().equals(""))
        {
            tSql = tSql + "RiskCode = '" + this.mCalRelValue.getRiskCode() +
                   "' and ";
        }
        if (this.mCalRelValue.getRiskVer() != null
            && !this.mCalRelValue.getRiskVer().trim().equals(""))
        {
            tSql = tSql + " RiskVer = '" + this.mCalRelValue.getRiskVer() +
                   "' and ";
        }
        if (this.mCalRelValue.getFieldName() != null
            && !this.mCalRelValue.getFieldName().trim().equals(""))
        {
            tSql = tSql + " FieldName = '" + this.mCalRelValue.getFieldName() +
                   "' ";
        }

        tSql += " order by SerialNo asc ";

        System.out.println("查询指标语句为：" + tSql);
        this.mLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldDB.mErrors.needDealError())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PubCheckField";
            tError.functionName = "getSetByField";
            tError.errorMessage = "数据处理失败PubCheckField-->getSetByField!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
        //编写测试用例
        //调用此校验类之前请初始化两个参数
        //1 - tTransferData：用来存放考核计算时要用到的值
        //2 - tLMCheckFieldSchema：用来存放描述数据
        //  - 或tLMCheckFieldSet
        PubCheckField checkField1 = new PubCheckField();
        VData cInputData = new VData();
        //设置计算时要用到的参数值
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("IndexCalNo", "200406");
        tTransferData.setNameAndValue("BranchType", "1");
        tTransferData.setNameAndValue("ManageCom", "86110000");
        //通过CKBYFIELD
        LMCheckFieldSchema tLMCheckFieldSchema = new LMCheckFieldSchema();
        tLMCheckFieldSchema.setRiskCode("000000");
        tLMCheckFieldSchema.setRiskVer("2004");
        tLMCheckFieldSchema.setFieldName("WageCalCheck");

        //通过 CKBYFIELD 方式校验
        cInputData.add(tTransferData);
        cInputData.add(tLMCheckFieldSchema);

        //通过 CKBYSET 方式校验
//    LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
//    LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
//    String tSql = "select * from lmcheckfield where riskcode = '000000'"
//                  +" and riskver = '2004' and fieldname = 'WageCalCheck' order by serialno asc";
//    tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
//
//    cInputData.add(tTransferData);
//    cInputData.add(tLMCheckFieldSet);

        if (!checkField1.submitData(cInputData, "CKBYFIELD"))
        {
            System.out.println("Enter Error Field!");
            //此判断是用来区分是程序处理过程中报的错误，还是校验时报的错误
            if (checkField1.mErrors.needDealError())
            {
                System.out.println("ERROR-S-" +
                                   checkField1.mErrors.getFirstError());
            }
            else
            {
                VData t = checkField1.getResultMess();
                LMCheckFieldSet ntLMCheckFieldSet = checkField1.
                        getResultMessSet();
                for (int i = 0; i < t.size(); i++)
                {
                    System.out.println("ERROR-C-" + i + ":" + t.get(i).toString());
                }
            }
        }
        else
        {
            System.out.println("Congratulattion!");
        }
    }
}
