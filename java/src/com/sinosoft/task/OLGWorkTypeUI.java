/*
 * <p>ClassName: OLGWorkTypeUI </p>
 * <p>Description: OLGWorkTypeUI���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-26 15:30:19
 */
package com.sinosoft.task;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.pubfun.*;
public class OLGWorkTypeUI {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
private VData mResult = new VData();
/** �����洫�����ݵ����� */
private VData mInputData =new VData();
/** ���ݲ����ַ��� */
private String mOperate;
//ҵ������ر���
 /** ȫ������ */
private LGWorkTypeSchema mLGWorkTypeSchema=new LGWorkTypeSchema();
public OLGWorkTypeUI ()
{
}
 /**
�������ݵĹ�������
*/
public boolean submitData(VData cInputData,String cOperate)
{
  //���������ݿ�����������
  this.mOperate =cOperate;

  //�õ��ⲿ���������,�����ݱ��ݵ�������
  if (!getInputData(cInputData))
    return false;

  //����ҵ����
  if (!dealData())
    return false;

  //׼������̨������
  if (!prepareOutputData())
    return false;

  OLGWorkTypeBL tOLGWorkTypeBL=new OLGWorkTypeBL();

  //System.out.println("Start OLGWorkType UI Submit...");
  tOLGWorkTypeBL.submitData(cInputData,mOperate);
  //System.out.println("End OLGWorkType UI Submit...");
  //�������Ҫ����Ĵ����򷵻�
  if (tOLGWorkTypeBL.mErrors .needDealError() )
  {
     // @@������
     this.mErrors.copyAllErrors(tOLGWorkTypeBL.mErrors);
     CError tError = new CError();
     tError.moduleName = "OLGWorkTypeUI";
     tError.functionName = "submitData";
     tError.errorMessage = "�����ύʧ��!";
     this.mErrors .addOneError(tError) ;
     return false;
  }
  if (mOperate.equals("INSERT||MAIN"))
  {
     this.mResult.clear();
     this.mResult=tOLGWorkTypeBL.getResult();
  }
  mInputData=null;
  return true;
  }
  public static void main(String[] args)
  {
  }
  /**
  * ׼��������������Ҫ������
  * ��������׼������ʱ���������򷵻�false,���򷵻�true
  */
 private boolean prepareOutputData()
 {
    try
    {
      mInputData.clear();
      mInputData.add(this.mLGWorkTypeSchema);
    }
    catch(Exception ex)
    {
      // @@������
      CError tError =new CError();
      tError.moduleName="LGWorkTypeUI";
      tError.functionName="prepareData";
      tError.errorMessage="��׼������㴦������Ҫ������ʱ����";
      this.mErrors .addOneError(tError) ;
      return false;
    }
    return true;
}
/**
 * ����ǰ����������ݣ�����UI�߼�����
  * ����ڴ�������г����򷵻�false,���򷵻�true
  */
 private boolean dealData()
 {
      boolean tReturn =false;
      //�˴�����һЩУ�����
      tReturn=true;
      return tReturn ;
}
 /**
 * �����������еõ����ж���
 *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
 */
private boolean getInputData(VData cInputData)
{
  //ȫ�ֱ���
  this.mLGWorkTypeSchema.setSchema((LGWorkTypeSchema)cInputData.getObjectByObjectName("LGWorkTypeSchema",0));
  return true;
}
public VData getResult()
{
  return this.mResult;
}
}
