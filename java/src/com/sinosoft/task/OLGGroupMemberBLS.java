/*
 * <p>ClassName: OLGGroupMemberBLS </p>
 * <p>Description: OLGGroupMemberBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-23 10:20:45
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
 public class OLGGroupMemberBLS {
/** �������࣬ÿ����Ҫ����������ж����ø��� */
public  CErrors mErrors=new CErrors();
//����������
  private VData mInputData ;
 /** ���ݲ����ַ��� */
private String mOperate;
public OLGGroupMemberBLS() {
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
    {if (!saveLGGroupMember())
        return false;
    }
    if (this.mOperate.equals("DELETE||MAIN"))
    {if (!deleteLGGroupMember())
        return false;
    }
    if (this.mOperate.equals("UPDATE||MAIN"))
    {if (!updateLGGroupMember())
        return false;
    }
  return true;
}
 /**
* ���溯��
*/
private boolean saveLGGroupMember()
{
  LGGroupMemberSchema tLGGroupMemberSchema = new LGGroupMemberSchema();
  tLGGroupMemberSchema = (LGGroupMemberSchema)mInputData.getObjectByObjectName("LGGroupMemberSchema",0);
  Connection conn;
  conn=null;
  conn=DBConnPool.getConnection();
  if (conn==null)
  {
      // @@������
      CError tError = new CError();
      tError.moduleName = "OLGGroupMemberBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "���ݿ�����ʧ��!";
      this.mErrors .addOneError(tError) ;
      return false;
   }
	try{
 	  conn.setAutoCommit(false);
    LGGroupMemberDB tLGGroupMemberDB=new LGGroupMemberDB(conn);
    tLGGroupMemberDB.setSchema(tLGGroupMemberSchema);
    if (!tLGGroupMemberDB.insert())
    {
      // @@������
      this.mErrors.copyAllErrors(tLGGroupMemberDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "OLGGroupMemberBLS";
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
    tError.moduleName="OLGGroupMemberBLS";
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
    private boolean deleteLGGroupMember()
    {
        LGGroupMemberSchema tLGGroupMemberSchema = new LGGroupMemberSchema();
        tLGGroupMemberSchema = (LGGroupMemberSchema)mInputData.getObjectByObjectName("LGGroupMemberSchema",0);
        System.out.println("Start Save...");
        Connection conn;
        conn=null;
        conn=DBConnPool.getConnection();
        if (conn==null)
        {
		// @@������
		CError tError = new CError();
           tError.moduleName = "OLGGroupMemberBLS";
           tError.functionName = "saveData";
           tError.errorMessage = "���ݿ�����ʧ��!";
           this.mErrors .addOneError(tError) ;
           return false;
        }
        try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGGroupMemberDB tLGGroupMemberDB=new LGGroupMemberDB(conn);
           tLGGroupMemberDB.setSchema(tLGGroupMemberSchema);
           if (!tLGGroupMemberDB.delete())
           {
		// @@������
		    this.mErrors.copyAllErrors(tLGGroupMemberDB.mErrors);
 		    CError tError = new CError();
		    tError.moduleName = "OLGGroupMemberBLS";
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
          tError.moduleName="OLGGroupMemberBLS";
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
private boolean updateLGGroupMember()
{
     LGGroupMemberSchema tLGGroupMemberSchema = new LGGroupMemberSchema();
     tLGGroupMemberSchema = (LGGroupMemberSchema)mInputData.getObjectByObjectName("LGGroupMemberSchema",0);
     System.out.println("Start Save...");
     Connection conn;
     conn=null;
     conn=DBConnPool.getConnection();
     if (conn==null)
     {
	     CError tError = new CError();
        tError.moduleName = "OLGGroupMemberBLS";
        tError.functionName = "updateData";
        tError.errorMessage = "���ݿ�����ʧ��!";
        this.mErrors .addOneError(tError) ;
        return false;
     }
     try{
           conn.setAutoCommit(false);
           System.out.println("Start ����...");
           LGGroupMemberDB tLGGroupMemberDB=new LGGroupMemberDB(conn);
	tLGGroupMemberDB.setSchema(tLGGroupMemberSchema);
           if (!tLGGroupMemberDB.update())
           {
	          // @@������
	         this.mErrors.copyAllErrors(tLGGroupMemberDB.mErrors);
	         CError tError = new CError();
	         tError.moduleName = "OLGGroupMemberBLS";
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
               tError.moduleName="OLGGroupMemberBLS";
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
