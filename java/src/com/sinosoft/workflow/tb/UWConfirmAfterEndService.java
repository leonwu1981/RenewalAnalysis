/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.LBMissionSchema;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;

/**
 * <p>Title:�������ڵ�����:����Լ�˹��˱�ȷ�� </p>
 * <p>Description:�˹��˱�ȷ�Ϲ�����AfterEnd������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */
public class UWConfirmAfterEndService implements AfterEndService
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
    private String mUWFlag;
    private String mMissionID;
    private Reflections mReflections = new Reflections();

    private LWMissionSet mLWMissionSet = new LWMissionSet();

    /** ����������ڵ㱸�ݱ�*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();

    public UWConfirmAfterEndService()
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterEndService";
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
            tError.moduleName = "UWConfirmAfterEndService";
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
            tError.moduleName = "UWConfirmAfterEndService";
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
            tError.moduleName = "UWConfirmAfterEndService";
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
            tError.moduleName = "UWConfirmAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWFlag = (String) mTransferData.getValueByName("UWFlag");
        if (mUWFlag == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������UWFlagʧ��!";
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
        //��ȫ�˱���������ʼ�ڵ�״̬�ı�
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
        //�޸ı�ȫ�˹��˱�����ʼ�ڵ�״̬Ϊδ�ظ�
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        String tStr = "";
////////////////����Ǿܱ����������ڵĻ������˺˱���������Ȼ���Դ�ӡ�˱�����//////////////////////////////////////////////////////
     //׼��Ҫɾ���ĺ˱���ع���������ڵ�
        if (mUWFlag.equals("1") || mUWFlag.equals("8") || mUWFlag.equals("a")) {
          tStr = "Select * from LWMission where MissionID = '" + mMissionID + "' and ActivityID not in('0000001149','0000001107')";
        }
        else
        {
          //tStr = "Select * from LWMission where MissionID = '" + mMissionID + "' and ActivityID in('0000001100','0000001111','0000001112','0000001113','0000001106','0000001108','0000001114','0000001115','0000001116','0000001017','0000001018','0000001019','0000001020','0000001021','0000001002','0000001120','0000001121','0000001122','0000001123','0000001124','0000001125')";
          tStr = "Select * from LWMission where MissionID = '" + mMissionID + "' and ActivityID not in('0000001149','0000001150')";
        }
        mLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (mLWMissionSet == null || mLWMissionSet.size() < 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "��ѯ�����������˹��˱��ļӷѺ���Լ����ڵ�ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        for (int i = 1; i <= mLWMissionSet.size(); i++)
        {
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            LBMissionSchema tLBMissionSchema = new LBMissionSchema();

            tLWMissionSchema = mLWMissionSet.get(i);
            String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
            mReflections.transFields(tLBMissionSchema, tLWMissionSchema);
            tLBMissionSchema.setSerialNo(tSerielNo);
            tLBMissionSchema.setActivityStatus("3"); //�ڵ�����ִ�����
            tLBMissionSchema.setLastOperator(mOperater);
            tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
            tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
            mLBMissionSet.add(tLBMissionSchema);
        }

        return true;
    }

    /**
     * ׼���ύ����
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //�����ع�����ͬ��ִ����ϵ�����ڵ������
        if (mLWMissionSet != null && mLWMissionSet.size() > 0)
        {
            map.put(mLWMissionSet, "DELETE");
        }

        //�����ع�����ͬ��ִ����ϵ�����ڵ㱸�ݱ�����
        if (mLBMissionSet != null && mLBMissionSet.size() > 0)
        {
            map.put(mLBMissionSet, "INSERT");
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
