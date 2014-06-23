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
 * <p>Title: �������ڵ�����:����Լ�����֪ͨ��</p>
 * <p>Description:�����������֪ͨ��AfterEnd������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author: HYQ
 * @version 1.0
 */

public class UWAutoHealthAfterEndService implements AfterEndService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;

    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mMissionID;

    /** ����������ڵ��*/
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //����Լ�˹��˱���������ʼ�ڵ�

    public UWAutoHealthAfterEndService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterEndService";
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
     * @return boolean
     */
    private boolean dealData()
    {
        //����Լ�˱���������ʼ�ڵ�״̬�ı�
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
     * @return boolean
     */
    private boolean prepareMission()
    {
        //�޸������˹��˱�����ʼ�ڵ�״̬Ϊδ�ظ�
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
//        String tStr = "Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001100'";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001100'");

        tLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
        if (tLWMissionSet != null && tLWMissionSet.size() == 1)
        {
            mInitLWMissionSchema = tLWMissionSet.get(1);
            mInitLWMissionSchema.setActivityStatus("2");
            mInitLWMissionSchema.setDefaultOperator(mOperater);
        }
        else
        {
//            tStr = "Select * from LWMission where MissionID = '" +
//                    mMissionID + "' and ActivityID = '0000001061'";
            tSBql = new StringBuffer(128);
            tSBql.append("Select * from LWMission where MissionID = '");
            tSBql.append(mMissionID);
            tSBql.append("' and ActivityID = '0000001061'");

            tLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
            if (tLWMissionSet != null && tLWMissionSet.size() == 1)
            {
                mInitLWMissionSchema = tLWMissionSet.get(1);
                mInitLWMissionSchema.setActivityStatus("2");
                mInitLWMissionSchema.setDefaultOperator(mOperater);
            }
            else
            {
                this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWAutoHealthAfterEndService";
                tError.functionName = "prepareMission";
                tError.errorMessage = "��ѯ�����������֪ͨ�����ʼ����ڵ�ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        return true;
    }

    /**
     * ׼����Ҫ���������
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //���������������ʼ����ڵ������
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
