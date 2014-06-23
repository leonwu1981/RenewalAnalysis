/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;

/**
 *
 * <p>Title:�������ڵ�����:������֪ͨ�� </p>
 * <p>Description: ������֪ͨ�鹤����AfterEnd������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class UWRReportAfterEndService implements AfterEndService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();


    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;


    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mMissionID;


    /** ����������ڵ��*/
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //�б��˹��˱���������ʼ�ڵ�


    public UWRReportAfterEndService()
    {
    }


    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        return true;
    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ�����֪ͨ����

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //�б��˱���������ʼ�ڵ�״̬�ı�
        if (prepareMission())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * ׼����ӡ��Ϣ��
     * @return
     */
    private boolean prepareMission()
    {
        //�޸ĳб��˹��˱�����ʼ�ڵ�״̬Ϊδ�ظ�
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        String tStr = "Select * from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID = '0000001100'";

        tLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (tLWMissionSet != null && tLWMissionSet.size() == 1)
        {
            mInitLWMissionSchema = tLWMissionSet.get(1);
            mInitLWMissionSchema.setActivityStatus("2");
            mInitLWMissionSchema.setDefaultOperator(mOperater);

        }
        else
        {
            tStr = "Select * from LWMission where MissionID = '" +
                    mMissionID + "' and ActivityID = '0000001061'";
            tLWMissionSet = tLWMissionDB.executeQuery(tStr);
            if (tLWMissionSet != null && tLWMissionSet.size() == 1)
            {
                mInitLWMissionSchema = tLWMissionSet.get(1);
                mInitLWMissionSchema.setActivityStatus("2");
                mInitLWMissionSchema.setDefaultOperator(mOperater);
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWRReportAfterEndService";
                tError.functionName = "prepareMission";
                tError.errorMessage = "��ѯ�������б��˹��˱�����ʼ����ڵ�ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        return true;
    }


    /**
     * ׼���ύ��̨����
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //��ӳб���������ʼ����ڵ������
        if (mInitLWMissionSchema != null)
        {
            map.put(mInitLWMissionSchema, "UPDATE");
        }

        mResult.add(map);
        return true;
    }


    /**
     * ���ش����Ľ��
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * ���ع������е�Lwfieldmap��������ֵ
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }


    /**
     * ���ش������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}
