package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class DBExecption extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DBExecption()
    {
        super();
    }

    public DBExecption(String reason)
    {
        super(reason);
    }

    public String toString()
    {
        return "DB²ã´íÎó:" + super.toString();
    }


    public static void main(String[] args)
    {
        DBExecption DBExecption1 = new DBExecption();
    }
}