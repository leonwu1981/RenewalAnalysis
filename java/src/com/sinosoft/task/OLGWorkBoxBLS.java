/*
 * <p>ClassName: OLGWorkBoxBLS </p>
 * <p>Description: OLGWorkBoxBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-23 09:59:56
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
 public class OLGWorkBoxBLS {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
//����������
  private VData mInputData ;
 /** ���ݲ����ַ��� */
private String mOperate;
public OLGWorkBoxBLS() {
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
    {if (!saveLGWorkBox())
        return false;
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {if (!deleteLGWorkBox())
        return false;
    }
    if (this.mOperate.equals("UPDATE||MAIN"))
    {if (!updateLGWorkBox())
        return false;
    }
  return true;
}
 /**
* ���溯��
*/
private boolean saveLGWorkBox()
{
  LGWorkBoxSchema tLGWorkBoxSchema = new LGWorkBoxSchema();
  tLGWorkBoxSchema = (LGWorkBoxSchema)mInputData.getObjectByObjectName("LGWorkBoxSchema",0);
  Connection conn;
  conn=null;
  conn=DBConnPool.getConnection();
  if (conn==null)
  {
      // @@������
      CError tError = new CError();
      tError.moduleName = "OLGWorkBoxBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "���ݿ�����ʧ��!";
      this.mErrors .addOneError(tError) ;
      return false;
   }
	try{
 	  conn.setAutoCommit(false);
    LGWorkBoxDB tLGWorkBoxDB=new LGWorkBoxDB(conn);
    tLGWorkBoxDB.setSchema(tLGWorkBoxSchema);
    if (!tLGWorkBoxDB.insert())
    {
      // @@������
      this.mErrors.copyAllErrors(tLGWorkBoxDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "OLGWorkBoxBLS";
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
    tError.moduleName="OLGWorkBoxBLS";
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
    private boolean deleteLGWorkBox()
    {
        LGWorkBoxSchema tLGWorkBoxSchema = new LGWorkBoxSchema();
        tLGWorkBoxSchema = (LGWorkBoxSchema)mInputData.getObjectByObjectName("LGWorkBoxSchema",0);
        System.out.println("Start Save...");
        Connection conn;
        conn=null;
        conn=DBConnPool.getConnection();
        if (conn==null)
        {
		// @@������
		CError tError = new CError();
           tError.moduleName = "OLGWorkBoxBLS";
           tError.functionName = "saveData";
           tError.errorMessage = "���ݿ�����ʧ��!";
           this.mErrors .addOneError(tError) ;
           return false;
        }
        try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGWorkBoxDB tLGWorkBoxDB=new LGWorkBoxDB(conn);
           tLGWorkBoxDB.setSchema(tLGWorkBoxSchema);
           if (!tLGWorkBoxDB.delete())
           {
		// @@������
		    this.mErrors.copyAllErrors(tLGWorkBoxDB.mErrors);
 		    CError tError = new CError();
		    tError.moduleName = "OLGWorkBoxBLS";
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
          tError.moduleName="OLGWorkBoxBLS";
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
private boolean updateLGWorkBox()
{
     LGWorkBoxSchema tLGWorkBoxSchema = new LGWorkBoxSchema();
     tLGWorkBoxSchema = (LGWorkBoxSchema)mInputData.getObjectByObjectName("LGWorkBoxSchema",0);
     System.out.println("Start Save...");
     Connection conn;
     conn=null;
     conn=DBConnPool.getConnection();
     if (conn==null)
     {
	     CError tError = new CError();
        tError.moduleName = "OLGWorkBoxBLS";
        tError.functionName = "updateData";
        tError.errorMessage = "���ݿ�����ʧ��!";
        this.mErrors .addOneError(tError) ;
        return false;
     }
     try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGWorkBoxDB tLGWorkBoxDB=new LGWorkBoxDB(conn);
	tLGWorkBoxDB.setSchema(tLGWorkBoxSchema);
           if (!tLGWorkBoxDB.update())
           {
	          // @@������
	         this.mErrors.copyAllErrors(tLGWorkBoxDB.mErrors);
	         CError tError = new CError();
	         tError.moduleName = "OLGWorkBoxBLS";
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
               tError.moduleName="OLGWorkBoxBLS";
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
