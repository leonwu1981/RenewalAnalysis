/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Vector;

/**
 * <p>Title:�������� </p>
 * <p>Description:
 *  ���Խ�ǰ̨����ͨ��Schema���ݵ����ݴ��ݵ���̨����.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author hzm
 * @version 1.0
 */

public class TransferData
{
    public TransferData()
    {
    }

    private Vector nameVData = new Vector(); //��ű�����
    private Vector valueVData = new Vector(); //��Ŷ�Ӧ�ñ�����ֵ

    /**
     * ����Ҫ����ı��������ֺͶ�Ӧ��ֵ(����)
     * ��Ҫ����ı��������ִ��� nameVData������
     * ��Ҫ����ı�����ֵ���� valueVData������
     * @param name Object ����������
     * @param value Object ������ֵ
     */
    public void setNameAndValue(Object name, Object value)
    {
        nameVData.add(name);
        valueVData.add(value);
    }

    /**
     * ����Ҫ����ı��������ֺͶ�Ӧ��ֵ(float)
     * ��Ҫ����ı��������ִ��� nameVData������
     * ��Ҫ����ı�����ֵ���� valueVData������
     * @param name Object ����������
     * @param value float ������ֵfloat��
     */
    public void setNameAndValue(Object name, float value)
    {
        nameVData.add(name);
        valueVData.add(new Float(value));
    }

    /**
     * ����Ҫ����ı��������ֺͶ�Ӧ��ֵ(double)
     * ��Ҫ����ı��������ִ��� nameVData������
     * ��Ҫ����ı�����ֵ���� valueVData������
     * @param name Object ����������
     * @param value double ������ֵdouble��
     */
    public void setNameAndValue(Object name, double value)
    {
        nameVData.add(name);
        valueVData.add(new Double(value));
    }

    /**
     * ����Ҫ����ı��������ֺͶ�Ӧ��ֵ(int)
     * ��Ҫ����ı��������ִ��� nameVData������
     * ��Ҫ����ı�����ֵ���� valueVData������
     * @param name Object ����������
     * @param value int ������ֵint��
     */
    public void setNameAndValue(Object name, int value)
    {
        nameVData.add(name);
        valueVData.add(new Integer(value));
    }

    /**
     * ���غʹ���ı��������ֶ�Ӧ��ֵ
     * @param name Object ����������
     * @return Object
     */
    public Object getValueByName(Object name)
    {
        for (int i = 0; i < nameVData.size(); i++)
        {
            if (nameVData.elementAt(i).equals(name))
            {
                return valueVData.get(i);
            }
        }
        return null;
    }

    /**
     * ���ش���ı������ֶ�Ӧ�ڼ����е�λ��
     * @param name Object ����������
     * @return int �ڼ����е�λ��,��0��ʼ��û���򷵻�-1
     */
    public int findIndexByName(Object name)
    {
        for (int i = 0; i < nameVData.size(); i++)
        {
            if (nameVData.elementAt(i).equals(name))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * ɾ�������еĶ�Ӧ���������ֵ
     * @param name Object ����������
     * @return boolean
     */
    public boolean removeByName(Object name)
    {
        for (int i = 0; i < nameVData.size(); i++)
        {
            if (nameVData.elementAt(i).equals(name))
            {
                valueVData.remove(i);
                nameVData.remove(i);
            }
        }
        return true;
    }

    /**
     * �������еı�������ֵ
     * @return Vector
     */
    public Vector getValueNames()
    {
        return nameVData;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        TransferData transferData1 = new TransferData();
//        transferData1.setNameAndValue("int", 200);
//        Integer value = (Integer) transferData1.getValueByName("int");
//        System.out.println("value:" + value);
//        TransferData test1 = transferData1;
//        Integer value2 = (Integer) transferData1.getValueByName("int");
//        System.out.println("value2:" + value2);
    }
}
