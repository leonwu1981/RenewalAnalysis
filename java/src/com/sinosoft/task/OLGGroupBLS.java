/*
 * <p>ClassName: OLGGroupBLS </p>
 * <p>Description: OLGGroupBLS类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate：2005-02-22 17:32:49
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
/** 错误处理类，每个需要错误处理的类中都放置该类 */
public  CErrors mErrors=new CErrors();
//传输数据类
  private VData mInputData ;
 /** 数据操作字符串 */
private String mOperate;
public OLGGroupBLS() {
	}
public static void main(String[] args) {
}
 /**
 传输数据的公共方法
*/
public boolean submitData(VData cInputData,String cOperate)
{
    //将操作数据拷贝到本类中
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
* 保存函数
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
      // @@错误处理
      CError tError = new CError();
      tError.moduleName = "OLGGroupBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "数据库连接失败!";
      this.mErrors .addOneError(tError) ;
      return false;
   }
	try{
 	  conn.setAutoCommit(false);
    LGGroupDB tLGGroupDB=new LGGroupDB(conn);
    tLGGroupDB.setSchema(tLGGroupSchema);
    if (!tLGGroupDB.insert())
    {
      // @@错误处理
      this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "OLGGroupBLS";
      tError.functionName = "saveData";
      tError.errorMessage = "数据保存失败!";
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
    // @@错误处理
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
    * 保存函数
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
		// @@错误处理
		CError tError = new CError();
           tError.moduleName = "OLGGroupBLS";
           tError.functionName = "saveData";
           tError.errorMessage = "数据库连接失败!";
           this.mErrors .addOneError(tError) ;
           return false;
        }
        try{
           conn.setAutoCommit(false);
           System.out.println("Start 保存...");
           LGGroupDB tLGGroupDB=new LGGroupDB(conn);
           tLGGroupDB.setSchema(tLGGroupSchema);
           if (!tLGGroupDB.delete())
           {
		// @@错误处理
		    this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
 		    CError tError = new CError();
		    tError.moduleName = "OLGGroupBLS";
		    tError.functionName = "saveData";
		    tError.errorMessage = "数据删除失败!";
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
      // @@错误处理
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
  * 保存函数
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
        tError.errorMessage = "数据库连接失败!";
        this.mErrors .addOneError(tError) ;
        return false;
     }
     try{
           conn.setAutoCommit(false);
           System.out.println("Start 保存...");
           LGGroupDB tLGGroupDB=new LGGroupDB(conn);
	tLGGroupDB.setSchema(tLGGroupSchema);
           if (!tLGGroupDB.update())
           {
	          // @@错误处理
	         this.mErrors.copyAllErrors(tLGGroupDB.mErrors);
	         CError tError = new CError();
	         tError.moduleName = "OLGGroupBLS";
	         tError.functionName = "saveData";
            tError.errorMessage = "数据保存失败!";
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
       // @@错误处理
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
