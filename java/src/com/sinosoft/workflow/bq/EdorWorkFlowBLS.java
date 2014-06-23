package com.sinosoft.workflow.bq;

import java.sql.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.pubfun.*;
import java.lang.reflect.*;
import java.util.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EdorWorkFlowBLS
{
  //传输数据类
  private VData mInputData ;
  /** 错误处理类，每个需要错误处理的类中都放置该类 */
  public CErrors mErrors = new CErrors();
  /** 数据库连接  **/
  private Connection conn = null;
  /** 立即提交标志 **/
  private boolean commitFlag = true;

  public EdorWorkFlowBLS() {
  }

  /**
	* 传输数据的公共方法
	* @param: cInputData 输入的数据
	*         cOperate 数据操作
	* @return:
	*/
  public boolean submitData(VData cInputData,String cOperate)
  {
	//首先将数据在本类中做一个备份
	mInputData=(VData)cInputData.clone() ;

	System.out.println("Start EdorWorkFlowBLS Submit...");
	if (!this.saveData())
		return false;
	System.out.println("End EdorWorkFlowBLS Submit...");

	mInputData=null;
	return true;
  }

	/**
   * 数据库操作
   * @return: boolean
   */
  private boolean saveData()
  {
	System.out.println("---Start Save---");

	//建立数据库连接
	if (conn == null) conn = DBConnPool.getConnection();

	if (conn == null) {
	  // @@错误处理
	  CError tError = new CError();
	  tError.moduleName = "EdorWorkFlowBLS";
	  tError.functionName = "saveData";
	  tError.errorMessage = "数据库连接失败!";
	  this.mErrors.addOneError(tError) ;
	  return false;
	}

	try {
	  //开始事务，锁表
	  conn.setAutoCommit(false);

	  String action = ""; //操作方式，INSERT\UPDATE\DELETE
	  String className = ""; //类名
	  Object o = null; //Schema或Set对象
	  Object DBObject = null; //DB或DBSet对象
	  Method m = null; //方法
	  Constructor constructor = null; //构造函数
	  Class[] parameterC = new Class[1]; //调用方法的参数数组
	  Object[] parameterO = new Object[1]; //调用方法的对象数组
          System.out.println("mInputData.size():"+mInputData.size()) ;
	  System.out.println("mInputData :"+mInputData) ;
	  //通过MMap来传递每个Schema或Set的数据库操作方式，约定使用
	  for(int i = 0; i < mInputData.size() ;i++)
	  {
		VData tData = new VData();
		tData = (VData)mInputData.get(i) ;
		MMap map = (MMap)tData.getObjectByObjectName("MMap", 0);
		if( map != null && map.keySet().size() !=0 )
		{
		  Set set = map.keySet();
		  //Iterator iterator = map.keySet().iterator();
		  //while (iterator.hasNext()) {
		  for (int j=0; j<set.size(); j++){
			//获取操作对象Schema或Set或SQL
			//o = iterator.next();
            o = map.getOrder().get(String.valueOf(j+1));
			//获取操作方式
			action = (String)map.get(o);
			if (action == null) continue;
			System.out.println("\n" + o.getClass().getName() + " Operate DB: " + action);

			//构造相应的DB类名
			className = o.getClass().getName();
			System.out.println("className :"+className) ;
			System.out.println("action :"+action) ;

			if (className.endsWith("String")) {
			  className = "com.sinosoft.lis.db." + className.substring(className.lastIndexOf(".")+1, className.lastIndexOf("S")) + "DB";
			  String tSQL = (String)o;
			  ExeSQL tExeSQL = new ExeSQL(conn);
			  System.out.println("执行SQL语句:"+tSQL);
			  if (!tExeSQL.execUpdateSQL(tSQL))
			  {
				CError tError = new CError();
				tError.moduleName = "EdorFinaProduce";
				tError.functionName = "saveLJAGetserials";
				tError.errorMessage = "更新给付通知有误失败!";
				this.mErrors .addOneError(tError) ;
				conn.rollback();
				conn.close();
				return false;
			  }
			  continue;
			}
			else if (className.endsWith("Schema")) {
			  className = "com.sinosoft.lis.db." + className.substring(className.lastIndexOf(".")+1, className.lastIndexOf("S")) + "DB";
			}
			else if (className.endsWith("Set")) {
			  className = "com.sinosoft.lis.vdb." + className.substring(className.lastIndexOf(".")+1, className.lastIndexOf("S")) + "DBSet";
			}
			Class DBClass = Class.forName(className);

			//选择构造函数，构造相同事务的DB或DBSet对象
			parameterC[0] = Connection.class;
			constructor = DBClass.getConstructor(parameterC);
			parameterO[0] = conn;
			DBObject = constructor.newInstance(parameterO);

			//给DB对象付值，将传入的Schema或Set对象的内容复制到DB中
			parameterC[0] = o.getClass();
			if (o.getClass().getName().endsWith("Schema")) {
			  m = DBObject.getClass().getMethod("setSchema", parameterC);
			}
			else if (o.getClass().getName().endsWith("Set")) {
			  m = DBObject.getClass().getMethod("set", parameterC);
			}
			parameterO[0] = o;
			m.invoke(DBObject, parameterO);

			//进行数据库操作
			if (action.equals("INSERT")) {
			  m = DBObject.getClass().getMethod("insert", null);
			  Boolean b = (Boolean)m.invoke(DBObject, null);

			  if (!b.booleanValue()) {
				try { conn.rollback(); } catch (Exception e) {}
				conn.close();
				System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
				return false;
			  }
			}
			else if (action.equals("UPDATE")) {
			  m = DBObject.getClass().getMethod("update", null);
			  Boolean b = (Boolean)m.invoke(DBObject, null);

			  if (!b.booleanValue()) {
				try { conn.rollback(); } catch (Exception e) {}
				conn.close();
				System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
				return false;
			  }
			}
			else if (action.equals("DELETE")) {
			  m = DBObject.getClass().getMethod("delete", null);
			  Boolean b = (Boolean)m.invoke(DBObject, null);

			  if (!b.booleanValue()) {
				try { conn.rollback(); } catch (Exception e) {}
				conn.close();
				System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
				return false;
			  }
			}
			else if (action.equals("DELETE&INSERT")) {
			  //DELETE
			  m = DBObject.getClass().getMethod("delete", null);
			  Boolean b = (Boolean)m.invoke(DBObject, null);

			  if (!b.booleanValue()) {
				try { conn.rollback(); } catch (Exception e) {}
				conn.close();
				System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
				return false;
			  }

			  //INSERT
			  m = DBObject.getClass().getMethod("insert", null);
			  b = (Boolean)m.invoke(DBObject, null);

			  if (!b.booleanValue()) {
				try { conn.rollback(); } catch (Exception e) {}
				conn.close();
				System.out.println(DBObject.getClass().getName() + " " + action + " Failed");
				return false;
			  }
			}
		  }//end of while
	  }
	}
	//数据提交:为保正事务一致性所有数据准备完毕后一次性提交.
	if (commitFlag) {
	  conn.commit();
	  conn.close();
	  System.out.println("---End Committed---");
	}
	else {
	  System.out.println("---End Datebase Operation, but not Commit in AutoBLS---");
	}
   }
	catch (Exception e) {
	  // @@错误处理
	  e.printStackTrace();
	  CError tError = new CError();
	  tError.moduleName = "EdorWorkFlowBLS";
	  tError.functionName = "savaData";
	  tError.errorMessage = e.toString();
	  this.mErrors.addOneError(tError);
	  try{ conn.rollback() ;} catch(Exception ex){}
	  return false;
	}


	return true;
  }

}
