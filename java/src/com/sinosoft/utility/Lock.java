package com.sinosoft.utility;

import java.lang.ref.WeakReference;

/**
 * @author wyuch ������ڵ�
 */
class Lock
{
	public String Type;

	public String Key;

	public Lock Previous;

	public Lock Next;

	private WeakReference Locker;// �����ö����Ա������ռ�

	public Object getLocker()
	{
		return Locker.get();
	}

	public void setLocker(Object locker)
	{
		Locker = new WeakReference(locker);
	}
}