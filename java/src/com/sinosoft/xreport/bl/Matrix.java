package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * ���ݾ���.
 * ����ͷ,��ͷ,���ݵȶ���������,�γɽ����,�������ȡ����λ�ö�Ӧ.
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

    private Map colMap; //��¼"A","B","C"...��
//  private Map rowMap;  //��¼"1","2","3"...��
    //��άMap

    public Matrix()
    {
    }

    /**
     * �õ�ĳ��λ���ϵ�Object
     * @param location λ��,F1��ʽ
     * @return ��λ���ϵĶ���.
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