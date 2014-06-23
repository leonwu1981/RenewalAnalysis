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
 * <p>Title: �������ڵ�����:����Լ�˹��˱����ղ���֪ͨ�鹤���������� </p>
 * <p>Description:����Լ�˹��˱�����֪ͨ����չ�����AfterEnd������
 *                �Ի��ճɹ��������ڵ����ת���п���</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWTakeBackAutoAddFeeAfterEndService implements AfterEndService
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
    private String mSubMissionID;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000001011*/
    /** ����������ڵ��*/
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //��ȫ�˹��˱���������ʼ�ڵ�
    private LWMissionSet mLWMissionSet = new LWMissionSet();

    /**�����־λ**/
    private boolean FirstTrialFlaog = false;
    /** ����������ڵ㱸�ݱ�*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();

    public UWTakeBackAutoAddFeeAfterEndService()
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

        //У���Ƿ���δ��ӡ�����֪ͨ��
        if (!checkData())
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
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
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

        //��ӱ�ȫ��������ʼ����ڵ������
        if (mInitLWMissionSchema != null)
        {
            if (!FirstTrialFlaog) //added by tuqiang for FirstTrial
            {
                map.put(mInitLWMissionSchema, "UPDATE");
            }
        }

        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        //��ѯ��������ǰ����켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(mMissionID);
        tLWMissionDB.setActivityID("0000001130");
        tLWMissionDB.setSubMissionID(mSubMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ�������켣��LWMissionʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLWMissionSchema = tLWMissionSet.get(1);
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������������ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
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
        //�˱���������ʼ�ڵ�״̬�ı�
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
        //��ѯ��ͬһ���֪ͨ��(ԭ��ӡ��ˮ����ͬ��)�����յ�����ڵ�,������
        StringBuffer tSBql = new StringBuffer(256);
        tSBql.append("Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001130' and SubMissionID <> '");
        tSBql.append(mSubMissionID);
        tSBql.append("' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001131' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001109' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("'");
        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
        if (mLWMissionSet == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "�б���������ʼ����ڵ��ѯ����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mLWMissionSet.size() < 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "�б���������ʼ����ڵ�LWMission��ѯ����!";
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

        //�жθ�֪ͨ����պ�,�Ƿ�óб������Ѵ����˹��˱��ѻظ�״̬.
        tSBql = new StringBuffer(256);
        tSBql.append("Select count(1) from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID in ('0000001111','0000001112','0000001113','0000001106','0000001107','0000001108','0000001109','0000001114','0000001115','0000001116','0000001017','0000001018','0000001019','0000001020','0000001021','0000001002')");
        String tReSult = new String();
        ExeSQL tExeSQL = new ExeSQL();
        tReSult = tExeSQL.getOneValue(tSBql.toString());
        if (tExeSQL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "ִ��SQL���ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tReSult == null || tReSult.equals(""))
        {
            return false;
        }
        int tCount = 0;
        tCount = Integer.parseInt(tReSult); //�Ѱ����˱��νڵ㼰���ͬ���ڵ�
        if (tCount > (mLWMissionSet.size() + 1))
        { //���ں˱�δ�ظ�״̬,�����޸ĳб��˹��˱�����ʼ�ڵ�״
            mInitLWMissionSchema = null;
        }
        else
        {
            //���ں˱��ѻظ�״̬,�޸������˹��˱�����ʼ�ڵ�״̬Ϊ�ѻظ�
            LWMissionSet tLWMissionSet = new LWMissionSet();
            tSBql = new StringBuffer(128);
            tSBql.append("Select * from LWMission where MissionID = '");
            tSBql.append(mMissionID);
            tSBql.append("' and ActivityID = '0000001100'");
            tLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
            if (tLWMissionSet == null || tLWMissionSet.size() != 1)
            {
                FirstTrialFlaog = true;
//                // @@������
//                this.mErrors.copyAllErrors(tExeSQL.mErrors);
//                CError tError = new CError();
//                tError.moduleName = "UWTakeBackAutoHealthAfterEndService";
//                tError.functionName = "prepareMission";
//                tError.errorMessage = "��ѯ�����������˹��˱�����ʼ����ڵ�ʧ��!";
//                this.mErrors.addOneError(tError);
//                return false;
            }
            else
            {
                mInitLWMissionSchema = tLWMissionSet.get(1);
                mInitLWMissionSchema.setActivityStatus("3");
            }
        }
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
