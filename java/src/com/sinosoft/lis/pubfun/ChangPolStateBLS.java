/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.schema.LCPolSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.VData;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ChangPolStateBLS
{
    //����������
    private VData mInputData;
    //�������࣬ÿ����Ҫ����������ж����ø���
    public CErrors mErrors = new CErrors();
    private LCPolSchema mLCPolSchema = new LCPolSchema();

    public ChangPolStateBLS()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        System.out.println("��ʼִ��changepolstate��submit");
        mInputData = (VData) cInputData.clone();
        String mOperate;
        mOperate = cOperate;
        mLCPolSchema = (LCPolSchema) mInputData.getObjectByObjectName(
                "LCPolSchema", 0);

        if (mOperate.equals("UPDATA"))
        {
            Connection conn = DBConnPool.getConnection();
            if (conn == null)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "ChangPolStateBLS";
                tError.functionName = "submitData";
                tError.errorMessage = "���ݿ�����ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            try
            {
                conn.setAutoCommit(false);

                // �޸ĸ��˱���״̬
                ExeSQL tExeSQL = new ExeSQL(conn);
                String sql = "update LCPol "
                             + "set polstate = '" + mLCPolSchema.getPolState() +
                             "'"
                             + "where polno = '" + mLCPolSchema.getPolNo() +
                             "' ";
                System.out.println("ִ�е�sql�����" + sql);

                if (tExeSQL.execUpdateSQL(sql) == false)
                {
                    // @@������
                    this.mErrors.copyAllErrors(tExeSQL.mErrors);
                    CError tError = new CError();
                    tError.moduleName = "ChangPolStateBLS";
                    tError.functionName = "submitData";
                    tError.errorMessage = "LCPolDBSet�޸�ʧ��!";
                    this.mErrors.addOneError(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
                conn.commit();
                conn.close();
                System.out.println("ִ�гɹ�");
            } // end of try
            catch (Exception ex)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "ChangPolStateBLS";
                tError.functionName = "submitData";
                tError.errorMessage = ex.toString();
                this.mErrors.addOneError(tError);
                try
                {
                    conn.rollback();
                    conn.close();
                }
                catch (Exception e)
                {}
                return false;
            }

        }
        return true;
    }
}
