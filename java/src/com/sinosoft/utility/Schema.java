/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/**
 * <p>ClassName: Schema 接口 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @author: HST
 * @version: 1.0
 * @date: 2002-06-17
 */
public interface Schema
{
    int TYPE_NOFOUND = -1; //
    int TYPE_MIN = -1; // the minimum value of type
    int TYPE_STRING = 0; // java.lang.String
    int TYPE_DATE = 1; // java.util.Date
    int TYPE_FLOAT = 2; // float
    int TYPE_INT = 3; // int
    int TYPE_DOUBLE = 4; // double
    int TYPE_MAX = 5; // the maximum value of type

    String[] getPK();

    // 按名字或索引返回值
    String getV(String FCode);

    String getV(int nIndex);

    // 按名字或索引返回字段的类型，如Schema.TYPE_STRING
    int getFieldType(String strFieldName);

    int getFieldType(int nFieldIndex);

    // 得到字段数
    int getFieldCount();

    // 名字和索引互查
    int getFieldIndex(String strFieldName);

    String getFieldName(int nFieldIndex);

    // 按照名字设置
    boolean setV(String strFieldName, String strFieldValue);
}
