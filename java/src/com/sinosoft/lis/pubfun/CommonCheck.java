package com.sinosoft.lis.pubfun;

import java.util.*;
import com.sinosoft.utility.*;

public class CommonCheck
{
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();
    /**
     * 校验元素是否非空
     * @param name String   元素名，用于打印错误信息
     * @param ckobj Object  被校验的元素
     * @return boolean  非空--true  空--false
     */
    public boolean checknoempty(String name, Object ckobj)
    {
        if (ckobj == null || ckobj.toString().equals(""))
        {
            mErrors.addOneError("发生错误: " + name + " 不允许空");
            return false;
        }
        return true;
    }

    /**
     * 校验元素是否有重复
     * @param name String    元素名称，用于打印错误信息
     * @param ckobj Object   被校验元素
     * @param source Vector  与被校验元素对比的元素集合
     * @return boolean  重复--false  无重复--true
     */
    public boolean checksingle(String name, Object ckobj, Vector source)
    {
        if (source == null)
        {
            return true;
        }
        for (int i = 0; i < source.size(); i++)
        {
            if (ckobj.equals(source.elementAt(i)))
            {
                mErrors.addOneError("发生错误: " + name + " 存在重复值");
                return false;
            }
        }
        return true;
    }

    /**
     * 校验元素是否有与之匹配的值
     * @param name String   元素名称，用于打印错误信息
     * @param ckobj Object  被校验元素
     * @param source Vector 与被校验元素对比的元素集合
     * @return boolean    有--true 没有--false
     */
    public boolean checkmatch(String name, Object ckobj, Vector source)
    {
        if (source == null)
        {
            mErrors.addOneError("发生错误: " + name + " 不存在匹配值");
            return false;
        }
        for (int i = 0; i < source.size(); i++)
        {
            if (ckobj.equals(source.elementAt(i)))
            {
                return true;
            }
        }
        mErrors.addOneError("发生错误: " + name + " 不存在匹配值");
        return false;
    }

}
