/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.sql.Connection;

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

public class ReinDataBLS
{

    //�������࣬ÿ����Ҫ����������ж����ø���
    public CErrors mErrors = new CErrors();

    public ReinDataBLS()
    {
    }

    //�������ݵĹ�������
    public boolean submitData()
    {
        System.out.println("Start ReinData BLS Submit...");

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

        System.out.println("End ReinData BLS Submit...");

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
            tError.moduleName = "ReinDataBLS";
            tError.functionName = "save";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            //����XML���ܱ���ⲿ�����ϼ������ֶ�
//    String bSQL = "update LFXMLColl a set ParentComCodeISC=(select ParentComCodeISC from LFComISC b where a.ComCodeISC=b.ComCodeISC)";
//    ExeSQL tExeSQL = new ExeSQL(conn);
//    if(!tExeSQL.execUpdateSQL(bSQL))
//    {
//      CError tError =new CError();
//      tError.moduleName = "ReinDataBLS";
//      tError.functionName = "save";
//      tError.errorMessage = "������������Ӧ�����";
//      this.mErrors.addOneError(tError);
//      return false;
//    }

            //����XML���ܱ���ڲ���Ŀ�ϼ������ֶ�
            String tSQL1 = "update LFXMLColl a set UpItemCode=(select UpItemCode from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL1 = new ExeSQL(conn);
            if (!tExeSQL1.execUpdateSQL(tSQL1))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "�����ڲ���Ŀ�ϼ��������";
                this.mErrors.addOneError(tError);
                return false;
            }

            //����XML���ܱ�Ĳ㼶�ֶ�
            String tSQL2 = "update LFXMLColl a set Layer=(select Layer from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL2 = new ExeSQL(conn);
            if (!tExeSQL2.execUpdateSQL(tSQL2))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "����㼶����";
                this.mErrors.addOneError(tError);
                return false;
            }

            //����XML���ܱ�ı�ע�ֶ�
            String tSQL3 = "update LFXMLColl a set Remark=(select Remark from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL3 = new ExeSQL(conn);
            if (!tExeSQL3.execUpdateSQL(tSQL3))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "���뱸ע����";
                this.mErrors.addOneError(tError);
                return false;
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
            tError.moduleName = "ReinDataBLS";
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
        ReinDataBLS reinDataBLS1 = new ReinDataBLS();
    }
}
