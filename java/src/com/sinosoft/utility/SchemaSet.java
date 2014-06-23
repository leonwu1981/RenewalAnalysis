/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import org.apache.log4j.Logger;
/*
 * <p>ClassName: SchemaSet </p>
 * <p>Description: SchemaSet类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: LIS
 * @author: HST
 * @CreateDate：2002-06-28
 */
public class SchemaSet
{
    // @Field
    private Object elementData[];
    private int elementCount;
    private int capacityIncrement;
    public static int Max_Warn_Size;
    public static Logger m_log = Logger.getLogger(SchemaSet.class);
    static{
    	Max_Warn_Size = 100;
    	ExeSQL tExeSql = new ExeSQL();
    	String tMax_Warn_Size = tExeSql.getOneValue("select a.sysvarvalue from ldsysvar a where a.sysvar='schemaset_warn_size'");
    	if(tMax_Warn_Size!=null && !tMax_Warn_Size.equals("")){
    		Max_Warn_Size = Integer.parseInt(tMax_Warn_Size);
    	}
    }

    public CErrors mErrors; // 错误信息

    // @Constructor
    public SchemaSet(int initialCapacity, int capacityIncrement)
    {
        if (initialCapacity < 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SchemaSet";
            tError.functionName = "SchemaSet";
            tError.errorMessage = "Illegal Capacity: " + initialCapacity;
            this.mErrors.addOneError(tError);
        }
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
        this.elementCount = 0;

        mErrors = new CErrors();
    }

    public SchemaSet(int initialCapacity)
    {
        this(initialCapacity, 0);
    }

    public SchemaSet()
    {
        this(10);
    }

    // @Method
    public boolean add(Schema aSchema)
    {
        if (aSchema == null)
        {
            return false;
        }
        
        if(this.size()>=SchemaSet.Max_Warn_Size){
        	String errMsg = "schemaset的容量超过警告值"+SchemaSet.Max_Warn_Size+"，对应schema名字为："+aSchema.getClass().getName()+"，数量为"+this.size();
//        	m_log.error(errMsg);
        	try {
        		throw new Exception(errMsg);
            } catch (Exception ex) {
        		m_log.error(ex.getMessage(), ex);
            }
        }

        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount] = aSchema;
        elementCount++;
        return true;
    }

    public boolean add(SchemaSet aSet)
    {
        if (aSet == null)
        {
            return false;
        }
        int n = aSet.size();
        ensureCapacityHelper(elementCount + n);
        for (int i = 0; i < n; i++)
        {
            elementData[elementCount + i] = aSet.getObj(i + 1);
        }
        elementCount += n;
        return true;
    }

    public boolean remove(Schema aSchema)
    {
        if (aSchema == null)
        {
            return false;
        }
        for (int i = 0; i < elementCount; i++)
        {
            if (aSchema.equals(elementData[i]))
            {
                int j = elementCount - i - 1;
                if (j > 0)
                {
                    System.arraycopy(elementData, i + 1, elementData, i, j);
                }
                elementCount--;
                elementData[elementCount] = null;
                return true;
            } // end of if
        } // end of for
        return false;
    }

    public boolean removeRange(int begin, int end)
    {
        if (begin <= 0 || end > elementCount || begin > end)
        {
            return false;
        }
        int n = elementCount - end;
        if (n > 0)
        {
            System.arraycopy(elementData, end, elementData, begin - 1, elementCount - end);
        }
        int m = end - begin + 1;
        for (int i = 1; i <= m; i++)
        {
            int j = elementCount - i;
            elementData[j] = null;
        }
        elementCount -= m;
        return true;
    }

    public void clear()
    {
        for (int i = 0; i < elementCount; i++)
        {
            elementData[i] = null;
        }
        elementCount = 0;
    }

    public Object getObj(int index)
    {
        if (index > elementCount)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SchemaSet";
            tError.functionName = "get";
            tError.errorMessage = "Index out of bounds!";
            this.mErrors.addOneError(tError);
        }
        return elementData[index - 1];
    }

    public boolean set(int index, Schema aSchema)
    {
        if (index > elementCount)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SchemaSet";
            tError.functionName = "set";
            tError.errorMessage = "Index out of bounds!";
            this.mErrors.addOneError(tError);

            return false;
        }
        elementData[index - 1] = aSchema;
        return true;
    }

    public boolean set(SchemaSet aSet)
    {
        this.clear();
        return this.add(aSet);
    }

    public int size()
    {
        return elementCount;
    }

    private void ensureCapacityHelper(int minCapacity)
    {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity)
        {
            Object oldData[] = elementData;
            int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement)
                    : (oldCapacity * 2);
            if (newCapacity < minCapacity)
            {
                newCapacity = minCapacity;
            }
            elementData = new Object[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, elementCount);
        }
    }
}
