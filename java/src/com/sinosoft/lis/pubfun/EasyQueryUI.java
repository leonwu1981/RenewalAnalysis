/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

//import com.sinosoft.lis.schema.LAAgentSchema;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;


/**
 * <p> Title: Webҵ��ϵͳ </p>
 * <p> Description: UI������ </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 * <p>Company: Sinosoft </p>
 * @author HST
 * @version 1.0
 * @date 2002-09-03
 */
public class EasyQueryUI
{
    /** �������ݵ����� */
//    private VData mInputData = new VData();


    /** �������ݵ����� */
    private VData mResult = new VData();


    /** ���ݲ����ַ��� */
    private String mOperate;


    /** �������� */
    public CErrors mErrors = new CErrors();


    // @Constructor
    public EasyQueryUI()
    {
    }

    /**
     * �����ύ�Ĺ����������ύ�ɹ��󽫷��ؽ���������ڲ�VData������
     * @param cInputData ���������,VData����
     * @param cOperate ���ݲ����ַ�������Ҫ����"QUERY||MAIN"��"QUERY||DETAIL"
     * @return ����ֵ��true--�ύ�ɹ�, false--�ύʧ�ܣ�
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // ���ݲ����ַ���������������
        this.mOperate = cOperate;

        EasyQueryBL tEasyQueryBL = new EasyQueryBL();

        //System.out.println("\n---EasyQuery BL BEGIN---");
        if (tEasyQueryBL.submitData(cInputData, mOperate))
        {
            mResult = tEasyQueryBL.getResult();
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tEasyQueryBL.mErrors);
            mResult.clear();
            return false;
        }
        //System.out.println("---EasyQuery BL END---\n");

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
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        String sql = "select ComCode,Name,Address,Phone,SatrapName from LDCom order by ComCode";
//        Integer start = new Integer(1);
//        String tSql = "0";
//        VData tVData = new VData();
//        VData tVData1 = new VData();
//        tVData.add(sql);
//        tVData.add(start);
//        tVData.add(tSql);
//        EasyQueryUI t = new EasyQueryUI();
//        t.submitData(tVData, "QUERY||MAIN");
//        tVData1 = t.getResult();
//        String result = "";
//        result = (String) tVData1.getObject(0);
//        System.out.println("testResult:" + result);
    }
}
