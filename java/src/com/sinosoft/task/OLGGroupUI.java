/*
 * <p>ClassName: OLGGroupUI </p>
 * <p>Description: OLGGroupUI类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate：2005-02-22 17:32:49
 */
package com.sinosoft.task;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.pubfun.*;
public class OLGGroupUI {
/** 错误处理类，每个需要错误处理的类中都放置该类 */
public  CErrors mErrors=new CErrors();
private VData mResult = new VData();
/** 往后面传输数据的容器 */
private VData mInputData =new VData();
/** 数据操作字符串 */
private String mOperate;
//业务处理相关变量
 /** 全局数据 */
private LGGroupSchema mLGGroupSchema=new LGGroupSchema();
public OLGGroupUI ()
{
}
 /**
传输数据的公共方法
*/
public boolean submitData(VData cInputData,String cOperate)
{
  //将操作数据拷贝到本类中
  this.mOperate =cOperate;
/*
  //得到外部传入的数据,将数据备份到本类中
  if (!getInputData(cInputData))
    return false;

  //进行业务处理
  if (!dealData())
    return false;

  //准备往后台的数据
  if (!prepareOutputData())
    return false;
*/
  OLGGroupBL tOLGGroupBL=new OLGGroupBL();

  //System.out.println("Start OLGGroup UI Submit...");
  tOLGGroupBL.submitData(cInputData,mOperate);
  //System.out.println("End OLGGroup UI Submit...");
  //如果有需要处理的错误，则返回
  if (tOLGGroupBL.mErrors .needDealError() )
  {
     // @@错误处理
     this.mErrors.copyAllErrors(tOLGGroupBL.mErrors);
     CError tError = new CError();
     tError.moduleName = "OLGGroupUI";
     tError.functionName = "submitData";
     tError.errorMessage = "数据提交失败!";
     this.mErrors .addOneError(tError) ;
     return false;
  }
  if (mOperate.equals("INSERT||MAIN"))
  {
     this.mResult.clear();
     this.mResult=tOLGGroupBL.getResult();
  }
  mInputData=null;
  return true;
  }
  public static void main(String[] args)
  {
  }
  /**
  * 准备往后层输出所需要的数据
  * 输出：如果准备数据时发生错误则返回false,否则返回true
  */
 private boolean prepareOutputData()
 {
    try
    {
      mInputData.clear();
      mInputData.add(this.mLGGroupSchema);
    }
    catch(Exception ex)
    {
      // @@错误处理
      CError tError =new CError();
      tError.moduleName="LGGroupUI";
      tError.functionName="prepareData";
      tError.errorMessage="在准备往后层处理所需要的数据时出错。";
      this.mErrors .addOneError(tError) ;
      return false;
    }
    return true;
}
/**
 * 根据前面的输入数据，进行UI逻辑处理
  * 如果在处理过程中出错，则返回false,否则返回true
  */
 private boolean dealData()
 {
      boolean tReturn =false;
      //此处增加一些校验代码
      tReturn=true;
      return tReturn ;
}
 /**
 * 从输入数据中得到所有对象
 *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
 */
private boolean getInputData(VData cInputData)
{
  //全局变量
  this.mLGGroupSchema.setSchema((LGGroupSchema)cInputData.getObjectByObjectName("LGGroupSchema",0));
  return true;
}
public VData getResult()
{
  return this.mResult;
}
}
