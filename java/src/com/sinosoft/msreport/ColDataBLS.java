/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.ListIterator;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class ColDataBLS
{

    //�������࣬ÿ����Ҫ����������ж����ø���
    public CErrors mErrors = new CErrors();
    private LinkedList mLinkedList = new LinkedList();

    public ColDataBLS()
    {
    }

    //�������ݵĹ�������
    public boolean submitData(LinkedList cLinkedList)
    {

        //���Ƚ������ڱ�������һ������
        mLinkedList = (LinkedList) cLinkedList.clone();

        System.out.println("Start ColData BLS Submit...");

        boolean tReturn = false;
//�����ݿ���в�������������ݲ��뵽���ݿ���
        tReturn = save();
        if (tReturn)
        {
            System.out.println("Save sucessful");
        }
        else
        {
            System.out.println("Save failed");
        }

        System.out.println("End ColData BLS Submit...");

        return tReturn;
    }

    /**
     * ����ǰ����������ݣ�����BLS�߼�������������
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean save()
    {
        boolean tReturn = true;
        System.out.println("Start Save...");

//������ӳص�һ������
        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ColDataBLS";
            tError.functionName = "save";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
//ѭ������ÿһ����Ҫִ�е�SQL���
            ListIterator tIterator = mLinkedList.listIterator();
            while (tIterator.hasNext())
            {
                String tSQL = (String) tIterator.next();
                ExeSQL tExeSQL = new ExeSQL(conn);
//ִ��ȡ�õ�SQL���
                if (!tExeSQL.execUpdateSQL(tSQL))
                {
                    // @@������
                    this.mErrors.copyAllErrors(tExeSQL.mErrors);
                    CError tError = new CError();
                    tError.moduleName = "ColDataBLS";
                    tError.functionName = "save";
                    tError.errorMessage = "�������ݳ���!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }
            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception in BLS");
            ex.printStackTrace();
            // @@������
            CError tError = new CError();
            tError.moduleName = "ColDataBLS";
            tError.functionName = "save";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            tReturn = false;
        }

        return tReturn;
    }

    public static void main(String[] args)
    {
        ColDataBLS colDataBLS1 = new ColDataBLS();
    }
}
