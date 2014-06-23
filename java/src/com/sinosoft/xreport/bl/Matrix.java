package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * 数据矩阵.
 * 将行头,列头,数据等都放入其中,形成交叉表,方便表内取数和位置对应.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class Matrix
{

    private Map colMap; //记录"A","B","C"...列
//  private Map rowMap;  //记录"1","2","3"...行
    //二维Map

    public Matrix()
    {
    }

    /**
     * 得到某个位置上的Object
     * @param location 位置,F1格式
     * @return 该位置上的对象.
     */
    public Object getObject(String location)
    {
        return getObjects(location)[0];
    }

    public Object[] getObjects(String location)
    {
        /**@todo: implements*/
        return null;
    }


    public static void main(String[] args)
    {
        Matrix matrix1 = new Matrix();
    }
}