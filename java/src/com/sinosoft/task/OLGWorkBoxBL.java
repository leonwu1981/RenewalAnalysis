/*
 * <p>ClassName: OLGWorkBoxBL </p>
 * <p>Description: OLGWorkBoxBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-23 09:59:56
 */
package com.sinosoft.task;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
public class OLGWorkBoxBL  {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
private VData mResult = new VData();
/** �����洫�����ݵ����� */
private VData mInputData= new VData();
private MMap map = new MMap();
/** ͳһ�������� */
private String mCurrentDate = PubFun.getCurrentDate();
/** ͳһ����ʱ�� */
private String mCurrentTime = PubFun.getCurrentTime();
/** ȫ������ */
private GlobalInput mGlobalInput =new GlobalInput() ;
/** ���ݲ����ַ��� */
private String mOperate;
/** ҵ������ر��� */
private LGWorkBoxSchema mLGWorkBoxSchema=new LGWorkBoxSchema();
//private LGWorkBoxSet mLGWorkBoxSet=new LGWorkBoxSet();
public OLGWorkBoxBL() {
}
public static void main(String[] args) {
}
/**
* �������ݵĹ�������
	* @param: cInputData ���������
   *         cOperate ���ݲ���
* @return:
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
    {
          // @@������
          CError tError = new CError();
          tError.moduleName = "OLGWorkBoxBL";
          tError.functionName = "submitData";
          tError.errorMessage = "���ݴ���ʧ��OLGWorkBoxBL-->dealData!";
          this.mErrors .addOneError(tError) ;
          return false;
    }
    //׼������̨������
    if (!prepareOutputData())
      return false;
    if (this.mOperate.equals("QUERY||MAIN"))
    {
      this.submitquery();
    }
    else
    {
        PubSubmit tPubSubmit = new PubSubmit();
        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);
            CError tError = new CError();
            tError.moduleName = "OLDDiseaseBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";

            this.mErrors.addOneError(tError);
            return false;
        }
    }
    mInputData=null;
    return true;
}
 /**
 * ����ǰ����������ݣ�����BL�߼�����
 * ����ڴ�������г����򷵻�false,���򷵻�true
*/
private boolean dealData()
{
    if (mOperate.equals("INSERT||MAIN"))
    {
        mLGWorkBoxSchema.setMakeDate(mCurrentDate);
        mLGWorkBoxSchema.setMakeTime(mCurrentTime);
        mLGWorkBoxSchema.setModifyDate(mCurrentDate);
        mLGWorkBoxSchema.setModifyTime(mCurrentTime);
        mLGWorkBoxSchema.setOperator(mGlobalInput.Operator);
        map.put(mLGWorkBoxSchema, "INSERT"); //����
    }
    if (mOperate.equals("UPDATE||MAIN"))
    {
        System.out.println("update");
        String sql = "Update LGWorkBox set " +
                     "OwnerTypeNo = '" + mLGWorkBoxSchema.getOwnerTypeNo() +"', " +
                     "OwnerNo = '" + mLGWorkBoxSchema.getOwnerNo() +"', " +
                     "Operator = '" + mGlobalInput.Operator + "', " +
                     "ModifyDate = '" + mCurrentDate + "', " +
                     "ModifyTime = '" + mCurrentTime + "' " +
                     "Where  WorkBoxNo = '" + mLGWorkBoxSchema.getWorkBoxNo() +"' ";
        map.put(sql, "UPDATE"); //�޸�
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {
        map.put(mLGWorkBoxSchema, "DELETE"); //ɾ��
    }

    return true;
}
/**
* ����ǰ����������ݣ�����BL�߼�����
* ����ڴ�������г����򷵻�false,���򷵻�true
*/
private boolean updateData()
{
   return true;
}
/**
* ����ǰ����������ݣ�����BL�߼�����
* ����ڴ�������г����򷵻�false,���򷵻�true
*/
private boolean deleteData()
{
  return true;
}
 /**
 * �����������еõ����ж���
 *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
 */
private boolean getInputData(VData cInputData)
{
         this.mLGWorkBoxSchema.setSchema((LGWorkBoxSchema)cInputData.getObjectByObjectName("LGWorkBoxSchema",0));
         this.mGlobalInput.setSchema((GlobalInput)cInputData.getObjectByObjectName("GlobalInput",0));
         return true;
}
/**
* ׼��������������Ҫ������
* ��������׼������ʱ���������򷵻�false,���򷵻�true
*/
 private boolean submitquery()
{
    this.mResult.clear();
    LGWorkBoxDB tLGWorkBoxDB=new LGWorkBoxDB();
    tLGWorkBoxDB.setSchema(this.mLGWorkBoxSchema);
		//�������Ҫ����Ĵ����򷵻�
		if (tLGWorkBoxDB.mErrors.needDealError())
 		{
		  // @@������
 			this.mErrors.copyAllErrors(tLGWorkBoxDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "LGWorkBoxBL";
			tError.functionName = "submitData";
			tError.errorMessage = "�����ύʧ��!";
			this.mErrors .addOneError(tError) ;
 			return false;
    }
    mInputData=null;
    return true;
}
 private boolean prepareOutputData()
 {
   try
	{
            this.mInputData.clear();
            this.mInputData.add(this.mLGWorkBoxSchema);
            mInputData.add(this.map);
            mResult.clear();
            mResult.add(this.mLGWorkBoxSchema);
        }
	catch(Exception ex)
	{
 		// @@������
		CError tError =new CError();
 		tError.moduleName="LGWorkBoxBL";
 		tError.functionName="prepareData";
 		tError.errorMessage="��׼������㴦������Ҫ������ʱ����";
 		this.mErrors .addOneError(tError) ;
		return false;
	}
	return true;
	}
	public VData getResult()
	{
  	return this.mResult;
	}
}
