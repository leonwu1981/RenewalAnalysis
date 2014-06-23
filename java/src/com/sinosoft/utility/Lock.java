package com.sinosoft.utility;

import java.lang.ref.WeakReference;

/**
 * @author wyuch 锁链表节点
 */
class Lock
{
	public String Type;

	public String Key;

	public Lock Previous;

	public Lock Next;

	private WeakReference Locker;// 弱引用对象，以便垃圾收集

	public Object getLocker()
	{
		return Locker.get();
	}

	public void setLocker(Object locker)
	{
		Locker = new WeakReference(locker);
	}
}