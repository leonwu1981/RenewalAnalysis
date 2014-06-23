/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author
 * @version 1.0
 */
public class DBPack
{
    private DBOper db = null;

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    public DBPack()
    {
    }

    public DBPack(String aSchName)
    {
        db = new DBOper(aSchName);
    }

    public boolean update(Schema s)
    {
        if (!db.update(s))
        {
            mErrors.copyAllErrors(db.mErrors);
            return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
        DBPack DBPack1 = new DBPack();
    }
}
