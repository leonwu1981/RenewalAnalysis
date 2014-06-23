package com.sinosoft.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.sinosoft.lis.pubfun.PubFun;

/**
 * <b>����������</b><br>
 */
public class LockUtilAnnuity
{
	/**
	 * @param lockType ������
	 * @param lockKey ��Key
	 * @param lockuser ��������
	 * @return trueΪ�����ɹ���falseΪʧ��
	 */
	

	public static boolean lock(String OperateType,String EdorType,String lockuser,String Information)
	{
		boolean tFlag=true;
		Connection con =null;
		PreparedStatement pstmt=null;
		try
		{
			con= DBConnPool.getConnection();
			Timestamp tTime=new Timestamp(System.currentTimeMillis());
	        tTime.setTime(System.currentTimeMillis());
	        String tdate = PubFun.getCurrentDate();
			String ttime = PubFun.getCurrentTime();
	        pstmt = con.prepareStatement("insert into ldlockannuity (makedate,maketime,OperateType,EdorType,lockuser,Information) values ( ? , ? , ? , ? , ?, ? ) ");
	        pstmt.setString(1, tdate);
	        pstmt.setString(2, ttime );
	        pstmt.setString(3, OperateType ); 
	        pstmt.setString(4, EdorType ); 
	        pstmt.setString(5, lockuser ); 
	        pstmt.setString(6, Information );
            pstmt.executeUpdate();
            pstmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			}
			catch (SQLException e1) 
			{
				
				e1.printStackTrace();
			}			
			tFlag= false;
		}
		finally
		{
			try 
			{
				if(pstmt!=null)
					pstmt.close();
				if (con != null)
					con.close();
			}
			catch (SQLException e) 
			{
				
				e.printStackTrace();
			}
		}
		return tFlag;
	}

	/**
	 * ��������������key���������Ƿ��Ѿ�����ͬ��������
	 * @param lockType ������
	 * @param lockKey ��Key
	 * @return trueΪ���Ѿ����ڣ�false��Ϊ����
	 */
	public static boolean isLocked(String OperateType,String EdorType,String lockuser)
	{
		boolean tFlag=true;
		Connection con =null;
		PreparedStatement pstmt =null;
		try
		{
			con= DBConnPool.getConnection();
			ResultSet rs = null;
			pstmt=con.prepareStatement("select count(1) from ldlockannuity where OperateType=? and EdorType=? and  LockUser=? ");
			pstmt.setString(1, OperateType);
			pstmt.setString(2, EdorType);
			pstmt.setString(3, lockuser);
			rs = pstmt.executeQuery();
			String mValue=null;				
			while (rs.next())

			{

				mValue = rs.getString(1);

				break;

			}
			if("0".equals(mValue))
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			}
			catch (SQLException e1) 
			{
				
				e1.printStackTrace();
			}			
			tFlag= false;
		}
		finally
		{
			try 
			{
				if(pstmt!=null)
					pstmt.close();
				if (con != null)
					con.close();
			}
			catch (SQLException e) 
			{
				
				e.printStackTrace();
			}
		}
		return tFlag;
	}


	/**
	 * @param lockType ������
	 * @param lockKey ��Key
	 * @param lcoker ��������
	 * @return trueΪ�����ɹ�
	 */
	public static boolean unlock(String OperateType,String EdorType,String lockuser)
	{
		boolean tFlag=true;
		Connection con =null;
		PreparedStatement pstmt=null;
		try
		{
			if(!isLocked(OperateType,EdorType,lockuser))
			{
				return false;
			}
			
				con= DBConnPool.getConnection();
		        pstmt = con.prepareStatement("delete from ldlockannuity  where OperateType=? and EdorType=? and  LockUser=? ");
				pstmt.setString(1, OperateType);
				pstmt.setString(2, EdorType);
				pstmt.setString(3, lockuser);
	          pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			}
			catch (SQLException e1) 
			{
				
				e1.printStackTrace();
			}		
			tFlag= false;
		}
		finally
		{
			try 
			{
				if(pstmt!=null)
					pstmt.close();
				if (con != null)
					con.close();
			}
			catch (SQLException e) 
			{
				
				e.printStackTrace();
			}
		}
		return tFlag;
	}


	public static void main(String[] args)
	{
		int times=200;
		long t = System.currentTimeMillis();

		System.out.println("��������");
		for (int i = 0; i < times; i++)
		{
			System.out.println(LockUtilAnnuity.lock(String.valueOf(i) ,"sys","sys", "sys"));
		}
		System.out.println("��������");
		for (int i = 0; i < 100; i++)
		{
			LockUtilAnnuity.lock( String.valueOf(i) ,"sys","sys", "sys");
		}

		System.out.println("�жϿ�ʼ");
		for (int i =1000; i < times+1000; i++)
		{
			System.out.println(LockUtilAnnuity.isLocked(String.valueOf(i) ,"sys","sys"));
		}
		System.out.println("������ʼ");
		for (int i = 0; i < times; i++)
		{
			System.out.println(LockUtilAnnuity.unlock(String.valueOf(i) ,"sys","sys"));
		}
		System.out.println("������ʱ(����)��" + (System.currentTimeMillis() - t));
	}
}
