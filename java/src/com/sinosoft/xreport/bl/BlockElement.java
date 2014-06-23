//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\CalculatableSet.java

package com.sinosoft.xreport.bl;

import java.util.Map;


public interface BlockElement
{

    //���ݿ�Ĺؼ���,��ʶ

    public static final String CONDITION = "condition";
    public static final String FORMULA = "formula";
    public static final String LOCATION = "location";
    public static final String CONTEXT = "context";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    //row header
    public static final String TYPE_ROWH = "0";
    //col header
    public static final String TYPE_COLH = "1";
    //special cell
    public static final String TYPE_CELL = "2";


    /**
     * �õ���ͷ,��ͷ,������Ԫ�����������
     * @return ��������,������ʽ"condition"->value,"formula"->value, "location"
     *         "context"->value,"type"->0-RowHeader;1-ColHeader;2-DefineCell
     */
    public Map getCompleteDefine();

    /**
     * ���ö���.
     * ���ڶ�ȡ�����ļ�,��ֵ������ͷ,��ͷ,��Ԫ��...
     * @param define ����,ͬ#getCompleteDefine()�ķ���ֵ.
     */
    public void setCompleteDefine(Map define);


    /**
     * ���ݶ���õ���Ӧ��xml����.
     * @return if type==0 return "<RowHeader condition=? formula=? location=?>?</RowHeader>"
     *         else if type==1   "<ColHeader condition=? formula=? >?</ColHeader>"
     *         else if type==2   "<ColHeader condition=? formula=?>?</ColHeader>"
     *
     */
    public String toXMLString();

    //calculate

}
