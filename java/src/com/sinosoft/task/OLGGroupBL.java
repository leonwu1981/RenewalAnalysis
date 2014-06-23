/*
 * <p>ClassName: OLGGroupBL </p>
 * <p>Description: OLGGroupBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-22 17:32:49
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
public class OLGGroupBL  {
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
private LGGroupSchema mLGGroupSchema=new LGGroupSchema();
//private LGGroupSet mLGGroupSet=new LGGroupSet();
public OLGGroupBL() {
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
          tError.moduleName = "OLGGroupBL";
          tError.functionName = "submitData";
          tError.errorMessage = "���ݴ���ʧ��OLGGroupBL-->dealData!";
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
    mInputData = null;
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
        mLGGroupSchema.setMakeDate(mCurrentDate);
        mLGGroupSchema.setMakeTime(mCurrentTime);
        mLGGroupSchema.setModifyDate(mCurrentDate);
        mLGGroupSchema.setModifyTime(mCurrentTime);
        mLGGroupSchema.setOperator(mGlobalInput.Operator);
        map.put(mLGGroupSchema, "INSERT"); //����
    }
    if (mOperate.equals("UPDATE||MAIN"))
    {
        String sql = "Update LGGroup set " +
                     "GroupName = '" + mLGGroupSchema.getGroupName() + "', " +
                     "GroupInfo = '" + mLGGroupSchema.getGroupInfo() + "', " +
                     "SuperGroupNo = '" + mLGGroupSchema.getSuperGroupNo() + "', " +
                     "WorkTypeNo = '" + mLGGroupSchema.getWorkTypeNo() + "', " +
                     "Operator = '" + mGlobalInput.Operator + "', " +
                     "ModifyDate = '" + mCurrentDate + "', " +
                     "ModifyTime = '" + mCurrentTime + "' " +
                     "Where  GroupNo = '" + mLGGroupSchema.getGroupNo() + "' ";
        map.put(sql, "UPDATE"); //�޸�
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {
        map.put(mLGGroupSchema, "DELETE"); //ɾ��
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
     this.mLGGroupSchema.setSchema((LGGroupSchema)cInputData.getObjectByObjectName("LGGroupSchema",0));
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
    LGGroupDB tLGGroupDB=new LGGroupDB();
    tLGGroupDB.setSchema(this.mLGGroupSchema);
		//�������Ҫ����Ĵ����򷵻�
		if (tLGGroupDB.mErrors.needDealError())
 		{
		  // @@������
 			this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "LGGroupBL";
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
            this.mInputData.add(this.mLGGroupSchema);
            mInputData.add(this.map);
            mResult.clear();
            mResult.add(this.mLGGroupSchema);
        }
	catch(Exception ex)
	{
 		// @@������
		CError tError =new CError();
 		tError.moduleName="LGGroupBL";
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
