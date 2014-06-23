/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.db.ES_DOC_MAINDB;
import com.sinosoft.lis.db.ES_DOC_PAGESDB;
import com.sinosoft.lis.db.ES_SERVER_INFODB;
import com.sinosoft.lis.easyscan.EasyScanConfig;
import com.sinosoft.lis.easyscan.RelationConfig;
import com.sinosoft.lis.schema.ES_DOC_MAINSchema;
import com.sinosoft.lis.schema.ES_DOC_PAGESSchema;
import com.sinosoft.lis.schema.ES_SERVER_INFOSchema;
import com.sinosoft.lis.vschema.ES_DOC_MAINSet;
import com.sinosoft.lis.vschema.ES_DOC_PAGESSet;
import com.sinosoft.lis.vschema.ES_SERVER_INFOSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: ɨ���������</p>
 * <p>Description: BL��ҵ���߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim Liuqiang Modified 2005-03-27
 * @since 20050618 add < QUERY||3 > YangZhuo
 * @since 20050620 add < QUERY||4 > YangZhuo ���ⲿ�����ѯ����.����Ӱ���ѯ
 * @version 1.0
 * @since 2002-11-06
 */

public class EasyScanQueryBL
{
    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    /** �������� */
    public CErrors mErrors = new CErrors();

    /** ҵ������ر��� */
    private String DocID = "";
    private String BussNo = "";
    private String BussNoType = "";
    private String BussType = "";
    private String BussType2 = "";
    private String SubType = "";
    private String SubType2 = "";
    private String BussNo2 = "";
    private String BussNoType2 = "";
    private String mSQL = ""; //��ѯ���
    private String mClientUrl = ""; //��������ʵ�URL
    private String sqlStr = ""; // ����ΪQUERY||4��ʱ��, ���������sql��ѯ���.����ֱ�Ӳ�ѯ

    /** ɨ����� */
    private ES_DOC_MAINSet mES_DOC_MAINSet = new ES_DOC_MAINSet();
    private ES_DOC_MAINSchema mES_DOC_MAINSchema = new ES_DOC_MAINSchema();

    private ES_DOC_PAGESSet mES_DOC_PAGESSet;
    private ES_DOC_PAGESSchema mES_DOC_PAGESSchema = new ES_DOC_PAGESSchema();

    private ES_SERVER_INFOSet mES_SERVER_INFOSet;
    private ES_SERVER_INFOSchema mES_SERVER_INFOSchema = new ES_SERVER_INFOSchema();

    public EasyScanQueryBL()
    {
    }

    /**
     * ���ݴ���
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //����������ݿ�����������
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        // ���ⲿ��������ݷֽ⵽����������У�׼������
        if (!getInputData())
        {
            return false;
        }
//        System.out.println("---End getInputData---");

        // ��ѯ����,��ѯ�ķ�֧���Ը���ҵ��Ҫ��ŵ���ͬ�ĵ��ü�����

        if (!queryData())
        {
            return false;
        }
//        System.out.println("---End queryData---");

        return true;
    }

    /**
     * ���ⲿ��������ݷֽ⵽�����������
     * @return boolean
     */
    private boolean getInputData()
    {
        if (mOperate.equals("QUERY||0"))
        {
            DocID = (String) mInputData.getObject(0);
            mClientUrl = (String) mInputData.getObject(1);
            if (DocID == null || DocID.trim().equals(""))
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "getInputData"
                        , "û�д���DocID��֤����!"));
                return false;
            }
        }
        else if (mOperate.equals("QUERY||1"))
        {
            BussNo = (String) mInputData.getObject(0);
            BussNoType = (String) mInputData.getObject(1);
            BussType = (String) mInputData.getObject(2);
            SubType = (String) mInputData.getObject(3);
            mClientUrl = (String) mInputData.getObject(4);
            if (BussNo == null || BussNo.trim().equals(""))
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "getInputData"
                        , "û�д���BussNoҵ�����!"));
                return false;
            }
        }
        else if (mOperate.equals("QUERY||2"))
        {
            BussNo = (String) mInputData.getObject(0);
            BussNoType = (String) mInputData.getObject(1);
            BussNo2 = (String) mInputData.getObject(2);
            BussNoType2 = (String) mInputData.getObject(3);
            BussType = (String) mInputData.getObject(4);
            SubType = (String) mInputData.getObject(5);
            mClientUrl = (String) mInputData.getObject(6);
            if (BussNo == null || BussNo.trim().equals(""))
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "getInputData"
                        , "û�д���BussNoҵ�����!"));
                return false;
            }

        }
        else if (mOperate.equals("QUERY||3"))
        {
            BussNo = mInputData.getObject(0).toString();
            BussNoType = mInputData.getObject(1).toString();
            BussType = mInputData.getObject(2).toString();
            SubType = mInputData.getObject(3).toString();
            BussNoType2 = mInputData.getObject(4).toString();
            BussType2 = mInputData.getObject(5).toString();
            SubType2 = mInputData.getObject(6).toString();
            mClientUrl = mInputData.getObject(7).toString();
            if (BussNo == null || BussNo.trim().equals(""))
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "getInputData"
                        , "û�д���BussNoҵ�����!"));
                return false;
            }

        }
        else if (mOperate.equals("QUERY||4"))
        {
            sqlStr = mInputData.getObject(0).toString();
            mClientUrl = mInputData.getObject(1).toString();
            if (sqlStr == null || sqlStr.trim().equals(""))
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "getInputData"
                        , "û�д���sqlStr��ѯ���!"));
                return false;
            }

        }
        if (mClientUrl == null)
        {
            mClientUrl = "ClientURL is null aaaaaaaaaaaaaaaa";
        }
        return true;
    }

    /**
     * ��Ҫ��Ϣ��ѯ
     * @return boolean
     */
    private boolean queryData()
    {
//        ES_DOC_MAINSet tES_DOC_MAINSet = new ES_DOC_MAINSet();
//        ES_DOC_MAINSchema tES_DOC_MAINSchema = new ES_DOC_MAINSchema();
//        VData tVDataResult = new VData();
        int i, j;

        StringBuffer sb = new StringBuffer(128);
        //ES_DOC_MAIN Dealing*******************************************************
        //����ӡˢ�Ż�ȡDOC_ID��NUM_PAGES��DOC_FLAGE��ҪУ��DOC_FLAGE�Ƿ�Ϊ1����Ϊ1�����ɨ���ļ�������
        if (mOperate.equals("QUERY||0")) //ֱ��ͨ��doc_id��ѯ
        {
            sb.append("select a.* from ES_DOC_MAIN a where ");
            if (DocID != null && !DocID.equals(""))
            {
                sb.append("a.DocID = ").append(DocID);
            }
        }
        else if (mOperate.equals("QUERY||1")) //ͨ��һ���̶���ҵ��Ž��в�ѯ
        {
            RelationConfig mConfig = RelationConfig.getInstance();
            if (SubType != null && !SubType.equals(""))
            {
                if (mConfig.getrelation(SubType).equals(""))
                {
                    this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "��ȡ������Ϣ����"));
                    mES_DOC_MAINSet.clear();
                    return false;
                }
                else
                {
                    SubType = mConfig.getrelation(SubType);
                }
            }
            sb.append("select a.* from ES_DOC_MAIN a,ES_DOC_RELATION b where ");
            if (BussNo != null && !BussNo.equals(""))
            {
                sb.append("b.BUSSNO = '").append(BussNo).append("' and ");
            }
            if (BussNoType != null && !BussNoType.equals(""))
            {
                sb.append("b.BUSSNOTYPE = '").append(BussNoType).append("' and ");
            }
            if (BussType != null && !BussType.equals(""))
            {
                sb.append("b.BUSSTYPE = '").append(BussType).append("' and ");
            }
            if (SubType != null && !SubType.equals(""))
            {
                sb.append("b.SubType = '").append(SubType).append("' and ");
            }
            sb.append(" a.DocID = b.DocID ");
        }
        else if (mOperate.equals("QUERY||2"))
        {
            RelationConfig mConfig = RelationConfig.getInstance();
            if (mConfig.getrelation(SubType).equals(""))
            {
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "��ȡ������Ϣ����"));
                mES_DOC_MAINSet.clear();
                return false;
            }
            else
            {
                SubType = mConfig.getrelation(SubType);
            }

            sb.append("select a.* from ES_DOC_MAIN a,ES_DOC_RELATION b,ES_DOC_RELATION c where ");
            if (BussNo != null && !BussNo.equals(""))
            {
                sb.append("b.BUSSNO = '").append(BussNo).append("' and ");
            }
            if (BussNoType != null && !BussNoType.equals(""))
            {
                sb.append("b.BUSSNOTYPE = '").append(BussNoType).append("' and ");
            }
            if (BussType != null && !BussType.equals(""))
            {
                sb.append("b.BUSSTYPE = '").append(BussType).append("' and ");
            }
            if ((SubType != null) && (!SubType.equals("")))
            {
                sb.append("b.SubType = '").append(SubType).append("' and ");
            }
            if ((BussNo2 != null) && !BussNo2.equals(""))
            {
                sb.append("c.BUSSNO = '").append(BussNo2).append("' and ");
            }
            if (BussNoType2 != null && !BussNoType2.equals(""))
            {
                sb.append("c.BUSSNOTYPE = '").append(BussNoType2).append("' and ");
            }
            if (BussType != null && !BussType.equals(""))
            {
                sb.append("c.BUSSTYPE = '").append(BussType).append("' and ");
            }
            if ((SubType != null) && (!SubType.equals("")))
            {
                sb.append("c.SubType = '").append(SubType).append("' and ");
            }
            sb.append(" a.DocID = b.DocID and b.DocID = c.DocID ");
        }
        else if (mOperate.equals("QUERY||3")) //ͨ������ ҵ������,��֤ϸ�� ������ѯ
        {
            RelationConfig mConfig = RelationConfig.getInstance();
            //ͨ����ȡת�������ļ�esrelation.xml�������ⲿ�������ڲ������ת��
            if (mConfig.getrelation(SubType).equals(""))
            {
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "��ȡ������Ϣ����"));
                mES_DOC_MAINSet.clear();
                return false;
            }
            else
            {
                SubType = mConfig.getrelation(SubType);
            }
            //ͨ����ȡת�������ļ�esrelation.xml�������ⲿ�������ڲ������ת��
            if (mConfig.getrelation(SubType2).equals(""))
            {
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "��ȡ������Ϣ����"));
                mES_DOC_MAINSet.clear();
                return false;
            }
            else
            {
                SubType2 = mConfig.getrelation(SubType2);
            }

            sb.append("select a.* from ES_DOC_MAIN a,ES_DOC_RELATION b where ");
            if (BussNo != null && !BussNo.equals(""))
            {
                sb.append("b.BUSSNO = '").append(BussNo).append("' and ");
            }
            if (BussNoType != null && !BussNoType.equals(""))
            {
                sb.append("b.BUSSNOTYPE = '").append(BussNoType).append("' and ");
            }
            if (BussType != null && !BussType.equals(""))
            {
                sb.append("b.BUSSTYPE = '").append(BussType).append("' and ");
            }
            if (SubType != null && !SubType.equals(""))
            {
                sb.append("b.SubType = '").append(SubType).append("' and ");
            }

            sb.append(" a.DocID = b.DocID union ");
            sb.append("select c.* from ES_DOC_MAIN c,ES_DOC_RELATION d where ");

            if (BussNo != null && !BussNo.equals(""))
            {
                sb.append("d.BUSSNO = '").append(BussNo).append("' and ");
            }
            if (BussNoType2 != null && !BussNoType2.equals(""))
            {
                sb.append("d.BUSSNOTYPE = '").append(BussNoType2).append("' and ");
            }
            if (BussType2 != null && !BussType2.equals(""))
            {
                sb.append("d.BUSSTYPE = '").append(BussType2).append("' and ");
            }
            if (SubType2 != null && !SubType2.equals(""))
            {
                sb.append("d.SubType = '").append(SubType2).append("' and ");
            }
            sb.append(" c.DocID = d.DocID ");
        }
        else if (mOperate.equals("QUERY||4")) //ͨ�� sqlStrֱ�Ӳ�ѯ Ӱ�����ݿ�
        {
            sb.append(sqlStr);
        }

        mSQL = sb.toString();
        if (mSQL.trim().equals(""))
        {
            CError tError = new CError();
            tError.moduleName = "EasyScanQueryBL";
            tError.functionName = "DealData";
            tError.errorMessage = "�������������Ҫ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        ES_DOC_MAINDB tES_DOC_MAINDB = mES_DOC_MAINSchema.getDB();
        mES_DOC_MAINSet = tES_DOC_MAINDB.executeQuery(mSQL);

        if (tES_DOC_MAINDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tES_DOC_MAINDB.mErrors);
            this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "ES_DOC_MAIN��ѯʧ��!"));
            mES_DOC_MAINSet.clear();
            return false;
        }

        if (mES_DOC_MAINSet.size() == 0)
        {
            // @@������
            this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "δ�ҵ�ES_DOC_MAIN�������!"));
            mES_DOC_MAINSet.clear();
            return false;
        }

        //��һ��ӡˢ�Ŷ�Ӧ�����¼��ʱ��û�и�ҵ�������δ����
//        if (mES_DOC_MAINSet.size() > 1)
//        {
//            System.out.println("��ӡˢ�Ŷ�Ӧ�ĵ�֤�ż�¼����1����δ����");
//            this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData",
//                                              "�õ�֤�ż�¼����1����δ����!"));
//            mES_DOC_MAINSet.clear();
//            return false;
//        }

        //����һ����ֻ֤��һ����¼

        //�ж�ɨ�������DOC_FLAGEΪɨ���������
//    if (!mES_DOC_MAINSchema.getDocFlag().equals("1")) {
//      this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData", "DOC_FLAGE��Ϊ1��ɨ�������!"));
//      mES_DOC_MAINSet.clear();
//      return false;
//    }

        //ES_SERVER_INFO Dealing****************************************************
        mSQL = "select * from ES_SERVER_INFO";
        ES_SERVER_INFODB tES_SERVER_INFODB = mES_SERVER_INFOSchema.getDB();
        mES_SERVER_INFOSet = tES_SERVER_INFODB.executeQuery(mSQL);

        if (tES_SERVER_INFODB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tES_SERVER_INFODB.mErrors);
            this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData",
                    "ES_SERVER_INFO��ѯʧ��!"));
            mES_SERVER_INFOSet.clear();
            return false;
        }

        if (mES_SERVER_INFOSet.size() == 0)
        {
            // @@������
            this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData",
                    "δ�ҵ�ES_SERVER_INFO�������!"));
            mES_SERVER_INFOSet.clear();
            return false;
        }
        //mResult.add(mES_SERVER_INFOSet.encode());
        //ȡ�����з���������Ϣ��ÿ��������ȡ����������IP��WEB��·��

        String[][] arrServerInfo = new String[mES_SERVER_INFOSet.size()][3];
        for (i = 1; i <= mES_SERVER_INFOSet.size(); i++)
        {
            ES_SERVER_INFOSchema tES_SERVER_INFOSchema = mES_SERVER_INFOSet.get(i);
            arrServerInfo[i - 1][0] = tES_SERVER_INFOSchema.getHostName();
            arrServerInfo[i - 1][1] = tES_SERVER_INFOSchema.getServerPort();
            arrServerInfo[i - 1][2] = tES_SERVER_INFOSchema.getPicPath();
        }

        //ES_DOC_PAGES Dealing******************************************************
        //��ȡPAGE_NAME��PAGE_PATH��SERVER_HOST��ҪУ��PAGE_FLAGE�Ƿ�Ϊ1����Ϊ1�����ɨ���ļ�������PAGE_FLAGE�ŵ�ǰ̨����У���ˣ�
        mResult.clear();
        VData Url = new VData();
        VData Pages = new VData();
        for (int k = 0; k < mES_DOC_MAINSet.size(); k++)
        {
            mES_DOC_MAINSchema = mES_DOC_MAINSet.get(k + 1);

            sb = new StringBuffer(128);
            sb.append("select * from ES_DOC_PAGES where DOCID = ");
            sb.append(mES_DOC_MAINSchema.getDocID());
            sb.append(" order by PAGECODE");
            mSQL = sb.toString();
//            System.out.println(mSQL);
            ES_DOC_PAGESDB tES_DOC_PAGESDB = mES_DOC_PAGESSchema.getDB();
            mES_DOC_PAGESSet = tES_DOC_PAGESDB.executeQuery(mSQL);

            if (tES_DOC_PAGESDB.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tES_DOC_PAGESDB.mErrors);
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData"
                        , "ES_DOC_PAGES��ѯʧ��!"));
                mES_DOC_MAINSet.clear();
                return false;
            }

            if (mES_DOC_PAGESSet.size() == 0)
            {
                // @@������
                this.mErrors.addOneError(throwErr("EasyScanQueryBL", "queryData"
                        , "δ�ҵ�ES_DOC_PAGES�������!"));
                mES_DOC_PAGESSet.clear();
                return false;
            }

//            System.out.println(mES_DOC_PAGESSet.encode());

            //ƴURL��ʹVECTOR��ÿһ��Ԫ����һ��URL
//            System.out.println("--mClientURL=" + mClientUrl + "--");
            EasyScanConfig tConfig = EasyScanConfig.getInstance();
            StringBuffer serverUrlBuf = new StringBuffer();
            for (i = 1; i <= mES_DOC_PAGESSet.size(); i++)
            {
                ES_DOC_PAGESSchema tES_DOC_PAGESSchema = mES_DOC_PAGESSet.get(i);
                for (j = 0; j < arrServerInfo.length; j++)
                {
                    if (arrServerInfo[j][0].equals(tES_DOC_PAGESSchema.getHostName()))
                    {
                        String strUrl = "http://" + arrServerInfo[j][1] + "/";
                        serverUrlBuf.delete(0, serverUrlBuf.length());
                        serverUrlBuf.append(strUrl);
                        tConfig.isForward(mClientUrl, serverUrlBuf);

                        Url.add(serverUrlBuf + tES_DOC_PAGESSchema.getPicPath()
                                + tES_DOC_PAGESSchema.getPageName() + ".gif");
                        //+ arrServerInfo[j][2]
                        Pages.add(Integer.toString(mES_DOC_PAGESSet.size()) + "_"
                                + Integer.toString(i));
                        break;
                    }
                }
            }
            mResult.add(Url);
            mResult.add(Pages);
        }
        return true;
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * �򻯴������ึֵ
     * @param cModuleName String
     * @param cFunctionName String
     * @param cErrorMessage String
     * @return CError
     */
    private static CError throwErr(String cModuleName, String cFunctionName,
            String cErrorMessage)
    {
        CError tError = new CError();
        tError.moduleName = cModuleName;
        tError.functionName = cFunctionName;
        tError.errorMessage = cErrorMessage;
        return tError;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        EasyScanQueryBL easyScanQueryBL = new EasyScanQueryBL();
//        VData mVData = new VData();
//        mVData.add("1921212121");
//        mVData.add("11");
//        mVData.add("TB");
//        mVData.add("TB01");
//        easyScanQueryBL.submitData(mVData, "");
    }
}
