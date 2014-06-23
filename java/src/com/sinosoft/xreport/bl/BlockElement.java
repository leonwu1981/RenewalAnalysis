//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\CalculatableSet.java

package com.sinosoft.xreport.bl;

import java.util.Map;


public interface BlockElement
{

    //数据块的关键字,标识

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
     * 得到行头,列头,独立单元格的完整定义
     * @return 完整定义,数据形式"condition"->value,"formula"->value, "location"
     *         "context"->value,"type"->0-RowHeader;1-ColHeader;2-DefineCell
     */
    public Map getCompleteDefine();

    /**
     * 设置定义.
     * 用于读取定义文件,将值赋予行头,列头,单元格...
     * @param define 定义,同#getCompleteDefine()的返回值.
     */
    public void setCompleteDefine(Map define);


    /**
     * 根据定义得到相应的xml定义.
     * @return if type==0 return "<RowHeader condition=? formula=? location=?>?</RowHeader>"
     *         else if type==1   "<ColHeader condition=? formula=? >?</ColHeader>"
     *         else if type==2   "<ColHeader condition=? formula=?>?</ColHeader>"
     *
     */
    public String toXMLString();

    //calculate

}
