/*
 * <p>ClassName: OLGGroupUI </p>
 * <p>Description: OLGGroupUI���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-22 17:32:49
 */
package com.sinosoft.task;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.pubfun.*;
public class OLGGroupUI {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
private VData mResult = new VData();
/** �����洫�����ݵ����� */
private VData mInputData =new VData();
/** ���ݲ����ַ��� */
private String mOperate;
//ҵ������ر���
 /** ȫ������ */
private LGGroupSchema mLGGroupSchema=new LGGroupSchema();
public OLGGroupUI ()
{
}
 /**
�������ݵĹ�������
*/
public boolean submitData(VData cInputData,String cOperate)
{
  //���������ݿ�����������
  this.mOperate =cOperate;
/*
  //�õ��ⲿ���������,�����ݱ��ݵ�������
  if (!getInputData(cInputData))
    return false;

  //����ҵ����
  if (!dealData())
    return false;

  //׼������̨������
  if (!prepareOutputData())
    return false;
*/
  OLGGroupBL tOLGGroupBL=new OLGGroupBL();

  //System.out.println("Start OLGGroup UI Submit...");
  tOLGGroupBL.submitData(cInputData,mOperate);
  //System.out.println("End OLGGroup UI Submit...");
  //�������Ҫ����Ĵ����򷵻�
  if (tOLGGroupBL.mErrors .needDealError() )
  {
     // @@������
     this.mErrors.copyAllErrors(tOLGGroupBL.mErrors);
     CError tError = new CError();
     tError.moduleName = "OLGGroupUI";
     tError.functionName = "submitData";
     tError.errorMessage = "�����ύʧ��!";
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
  * ׼��������������Ҫ������
  * ��������׼������ʱ���������򷵻�false,���򷵻�true
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
      // @@������
      CError tError =new CError();
      tError.moduleName="LGGroupUI";
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
  this.mLGGroupSchema.setSchema((LGGroupSchema)cInputData.getObjectByObjectName("LGGroupSchema",0));
  return true;
}
public VData getResult()
{
  return this.mResult;
}
}
