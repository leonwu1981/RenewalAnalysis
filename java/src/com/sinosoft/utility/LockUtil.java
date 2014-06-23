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
public class LockUtil
{
	/**
	 * @param lockType ������
	 * @param lockKey ��Key
	 * @param locker ��������
	 * @return trueΪ�����ɹ���falseΪʧ��
	 */
	public static boolean lock(String lockType, String lockKey, Object locker)
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
	        pstmt = con.prepareStatement("insert into ldlock (lockType,lockvalue,lockuser,information,makedate,maketime) values ( ? , ? , ? , ? , ? , ? ) ");
	        pstmt.setString(1, lockType);
	        pstmt.setString(2, lockKey);
	        pstmt.setString(3, "System");
	        pstmt.setString(4, "" );
	        pstmt.setString(5, tdate);
	        pstmt.setString(6, ttime );
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

//fengyan ����lock����	
	public static boolean lock(String lockType, String lockKey, String lockuser, String info)
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
	        pstmt = con.prepareStatement("insert into ldlock (lockType,lockvalue,lockuser,information,makedate,maketime) values ( ? , ? , ? , ? , ? , ? ) ");
	        pstmt.setString(1, lockType);
	        pstmt.setString(2, lockKey);
	        pstmt.setString(3, lockuser);
	        pstmt.setString(4, info );
	        pstmt.setString(5, tdate);
	        pstmt.setString(6, ttime );
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
	public static boolean isLocked(String lockType, String lockKey)
	{
		boolean tFlag=true;
		Connection con =null;
		PreparedStatement pstmt =null;
		try
		{
			con= DBConnPool.getConnection();
			ResultSet rs = null;
			pstmt=con.prepareStatement("select count(1) from ldlock where lockType=? and lockvalue =? ");
			pstmt.setString(1, lockType);
			pstmt.setString(2, lockKey);
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
	public static boolean unlock(String lockType, String lockKey, Object locker)
	{
		boolean tFlag=true;
		Connection con =null;
		PreparedStatement pstmt=null;
		try
		{
			if(!isLocked(lockType,lockKey))
			{
				return false;
			}
			
				con= DBConnPool.getConnection();
		        pstmt = con.prepareStatement("delete from ldlock where locktype=? and lockvalue=?");
		        pstmt.setString(1, lockType);
		        pstmt.setString(2, lockKey);
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
		int times=2000;
		long t = System.currentTimeMillis();

		System.out.println("��������");
		for (int i = 0; i < times; i++)
		{
			System.out.println(LockUtil.lock("TD", String.valueOf(i), LockUtil.class));
		}
		System.out.println("��������");
		for (int i = 0; i < 100; i++)
		{
			LockUtil.lock("TD", String.valueOf(i), LockUtil.class);
		}

		System.out.println("�жϿ�ʼ");
		for (int i =1000; i < times+1000; i++)
		{
			System.out.println(LockUtil.isLocked("TD", String.valueOf(i)));
		}
		System.out.println("������ʼ");
		for (int i = 0; i < times; i++)
		{
			System.out.println(LockUtil.unlock("TD", String.valueOf(i), LockUtil.class));
		}
		System.out.println("������ʱ(����)��" + (System.currentTimeMillis() - t));
	}
}
