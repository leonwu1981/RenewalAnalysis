/*
 * <p>ClassName: OLGWorkTypeBLS </p>
 * <p>Description: OLGWorkTypeBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-26 15:30:19
 */
package com.sinosoft.task;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import java.sql.*;
import com.sinosoft.lis.pubfun.*;
 public class OLGWorkTypeBLS {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
//����������
  private VData mInputData ;
 /** ���ݲ����ַ��� */
private String mOperate;
public OLGWorkTypeBLS() {
	}
public static void main(String[] args) {
}
 /**
 �������ݵĹ�������
*/
public boolean submitData(VData cInputData,String cOperate)
{
    //���������ݿ�����������
    this.mOperate =cOperate;
	  mInputData=(VData)cInputData.clone();
    if(this.mOperate.equals("INSERT||MAIN"))
    {if (!saveLGWorkType())
        return false;
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {if (!deleteLGWorkType())
        return false;
    }
    if (this.mOperate.equals("UPDATE||MAIN"))
    {if (!updateLGWorkType())
        return false;
    }
  return true;
}
 /**
* ���溯��
*/
private boolean saveLGWorkType()
{
  LGWorkTypeSchema tLGWorkTypeSchema = new LGWorkTypeSchema();
  tLGWorkTypeSchema = (LGWorkTypeSchema)mInputData.getObjectByObjectName("LGWorkTypeSchema",0);
  Connection conn;
  conn=null;
  conn=DBConnPool.getConnection();
  if (conn==null)
  {
      // @@������
      CError tError = new CError();
      tError.moduleName = "OLGWorkTypeBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "���ݿ�����ʧ��!";
      this.mErrors .addOneError(tError) ;
      return false;
   }
	try{
 	  conn.setAutoCommit(false);
    LGWorkTypeDB tLGWorkTypeDB=new LGWorkTypeDB(conn);
    tLGWorkTypeDB.setSchema(tLGWorkTypeSchema);
    if (!tLGWorkTypeDB.insert())
    {
      // @@������
      this.mErrors.copyAllErrors(tLGWorkTypeDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "OLGWorkTypeBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "���ݱ���ʧ��!";
      this.mErrors .addOneError(tError) ;
      conn.rollback();
      conn.close();
      return false;
    }
    conn.commit() ;
    conn.close();
  }
  catch (Exception ex)
  {
    // @@������
    CError tError =new CError();
    tError.moduleName="OLGWorkTypeBLS";
    tError.functionName="submitData";
    tError.errorMessage=ex.toString();
    this.mErrors .addOneError(tError);
      try{
      conn.rollback() ;
      conn.close();
      }
      catch(Exception e){}
    return false;
	}
  return true;
}
    /**
    * ���溯��
    */
    private boolean deleteLGWorkType()
    {
        LGWorkTypeSchema tLGWorkTypeSchema = new LGWorkTypeSchema();
        tLGWorkTypeSchema = (LGWorkTypeSchema)mInputData.getObjectByObjectName("LGWorkTypeSchema",0);
        System.out.println("Start Save...");
        Connection conn;
        conn=null;
        conn=DBConnPool.getConnection();
        if (conn==null)
        {
		// @@������
		CError tError = new CError();
           tError.moduleName = "OLGWorkTypeBLS";
           tError.functionName = "saveData";
           tError.errorMessage = "���ݿ�����ʧ��!";
           this.mErrors .addOneError(tError) ;
           return false;
        }
        try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGWorkTypeDB tLGWorkTypeDB=new LGWorkTypeDB(conn);
           tLGWorkTypeDB.setSchema(tLGWorkTypeSchema);
           if (!tLGWorkTypeDB.delete())
           {
		// @@������
		    this.mErrors.copyAllErrors(tLGWorkTypeDB.mErrors);
 		    CError tError = new CError();
		    tError.moduleName = "OLGWorkTypeBLS";
		    tError.functionName = "saveData";
		    tError.errorMessage = "����ɾ��ʧ��!";
		    this.mErrors .addOneError(tError) ;
               conn.rollback();
               conn.close();
               return false;
           }
               conn.commit() ;
               conn.close();
         }
       catch (Exception ex)
       {
      // @@������
          CError tError =new CError();
          tError.moduleName="OLGWorkTypeBLS";
          tError.functionName="submitData";
          tError.errorMessage=ex.toString();
          this.mErrors .addOneError(tError);
          try{conn.rollback() ;
          conn.close();} catch(Exception e){}
         return false;
         }
         return true;
}
/**
  * ���溯��
*/
private boolean updateLGWorkType()
{
     LGWorkTypeSchema tLGWorkTypeSchema = new LGWorkTypeSchema();
     tLGWorkTypeSchema = (LGWorkTypeSchema)mInputData.getObjectByObjectName("LGWorkTypeSchema",0);
     System.out.println("Start Save...");
     Connection conn;
     conn=null;
     conn=DBConnPool.getConnection();
     if (conn==null)
     {
	     CError tError = new CError();
        tError.moduleName = "OLGWorkTypeBLS";
        tError.functionName = "updateData";
        tError.errorMessage = "���ݿ�����ʧ��!";
        this.mErrors .addOneError(tError) ;
        return false;
     }
     try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGWorkTypeDB tLGWorkTypeDB=new LGWorkTypeDB(conn);
	tLGWorkTypeDB.setSchema(tLGWorkTypeSchema);
           if (!tLGWorkTypeDB.update())
           {
	          // @@������
	         this.mErrors.copyAllErrors(tLGWorkTypeDB.mErrors);
	         CError tError = new CError();
	         tError.moduleName = "OLGWorkTypeBLS";
	         tError.functionName = "saveData";
            tError.errorMessage = "���ݱ���ʧ��!";
            this.mErrors .addOneError(tError) ;
            conn.rollback();
            conn.close();
            return false;
            }
            conn.commit() ;
            conn.close();
       }
       catch (Exception ex)
       {
       // @@������
               CError tError =new CError();
               tError.moduleName="OLGWorkTypeBLS";
               tError.functionName="submitData";
               tError.errorMessage=ex.toString();
               this.mErrors .addOneError(tError);
               try{conn.rollback() ;
               conn.close();} catch(Exception e){}
               return false;
     }
               return true;
     }
}
