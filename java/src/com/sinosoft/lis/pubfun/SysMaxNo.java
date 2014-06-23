/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.*;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 系统号码管理接口</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Liuqiang
 * @version 1.0
 */

public interface SysMaxNo
{

    /**
     *<p>生成流水号的函数<p>
     *<p>号码规则：机构编码  日期年  校验位   类型    流水号<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType String 为需要生成号码的类型
     * @param cNoLength String 为需要生成号码的限制条件
     * @param cVData VData 业务相关数据
     * @return String 生成的符合条件的流水号，如果生成失败，返回空字符串""
     */
    String CreateMaxNo(String cNoType, String cNoLength, VData cVData);

    /**
     * 功能：产生指定长度的流水号，一个号码类型一个流水
     * @param cNoType String 流水号的类型
     * @param cNoLength int 流水号的长度
     * @return String 返回产生的流水号码
     */
    String CreateMaxNo(String cNoType, int cNoLength);

    /**
     *<p>生成流水号的函数<p>
     *<p>号码规则：机构编码  日期年  校验位   类型    流水号<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType String 为需要生成号码的类型
     * @param cNoLimit String 为需要生成号码的限制条件
     * @return String 生成的符合条件的流水号，如果生成失败，返回空字符串""
     */
    String CreateMaxNo(String cNoType, String cNoLimit);

}
