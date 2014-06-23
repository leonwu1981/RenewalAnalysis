/*
 * <p>ClassName: OLGGroupBLS </p>
 * <p>Description: OLGGroupBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-22 17:32:49
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
 public class OLGGroupBLS {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
//����������
  private VData mInputData ;
 /** ���ݲ����ַ��� */
private String mOperate;
public OLGGroupBLS() {
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
    {if (!saveLGGroup())
        return false;
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {if (!deleteLGGroup())
        return false;
    }
    if (this.mOperate.equals("UPDATE||MAIN"))
    {if (!updateLGGroup())
        return false;
    }
  return true;
}
 /**
* ���溯��
*/
private boolean saveLGGroup()
{
  LGGroupSchema tLGGroupSchema = new LGGroupSchema();
  tLGGroupSchema = (LGGroupSchema)mInputData.getObjectByObjectName("LGGroupSchema",0);
  Connection conn;
  conn=null;
  conn=DBConnPool.getConnection();
  if (conn==null)
  {
      // @@������
      CError tError = new CError();
      tError.moduleName = "OLGGroupBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "���ݿ�����ʧ��!";
      this.mErrors .addOneError(tError) ;
      return false;
   }
	try{
 	  conn.setAutoCommit(false);
    LGGroupDB tLGGroupDB=new LGGroupDB(conn);
    tLGGroupDB.setSchema(tLGGroupSchema);
    if (!tLGGroupDB.insert())
    {
      // @@������
      this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "OLGGroupBLS";
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
    tError.moduleName="OLGGroupBLS";
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
    private boolean deleteLGGroup()
    {
        LGGroupSchema tLGGroupSchema = new LGGroupSchema();
        tLGGroupSchema = (LGGroupSchema)mInputData.getObjectByObjectName("LGGroupSchema",0);
        System.out.println("Start Save...");
        Connection conn;
        conn=null;
        conn=DBConnPool.getConnection();
        if (conn==null)
        {
		// @@������
		CError tError = new CError();
           tError.moduleName = "OLGGroupBLS";
           tError.functionName = "saveData";
           tError.errorMessage = "���ݿ�����ʧ��!";
           this.mErrors .addOneError(tError) ;
           return false;
        }
        try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGGroupDB tLGGroupDB=new LGGroupDB(conn);
           tLGGroupDB.setSchema(tLGGroupSchema);
           if (!tLGGroupDB.delete())
           {
		// @@������
		    this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
 		    CError tError = new CError();
		    tError.moduleName = "OLGGroupBLS";
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
          tError.moduleName="OLGGroupBLS";
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
private boolean updateLGGroup()
{
     LGGroupSchema tLGGroupSchema = new LGGroupSchema();
     tLGGroupSchema = (LGGroupSchema)mInputData.getObjectByObjectName("LGGroupSchema",0);
     System.out.println("Start Save...");
     Connection conn;
     conn=null;
     conn=DBConnPool.getConnection();
     if (conn==null)
     {
	     CError tError = new CError();
        tError.moduleName = "OLGGroupBLS";
        tError.functionName = "updateData";
        tError.errorMessage = "���ݿ�����ʧ��!";
        this.mErrors .addOneError(tError) ;
        return false;
     }
     try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGGroupDB tLGGroupDB=new LGGroupDB(conn);
	tLGGroupDB.setSchema(tLGGroupSchema);
           if (!tLGGroupDB.update())
           {
	          // @@������
	         this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
	         CError tError = new CError();
	         tError.moduleName = "OLGGroupBLS";
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
               tError.moduleName="OLGGroupBLS";
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
