package com.sinosoft.xreport.bl;

import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class DefineInfo
{

    // 报表类型
    /**为了使StringUtility.parseTime()正常,不能更改以下数值!!!*/
    public static final int TYPE_DAILY = 1;
    public static final int TYPE_MONTHLY = 2;
    public static final int TYPE_SEASONAL = 3;
    public static final int TYPE_YEARLY = 4;
    //暂时没有,If you need that, tell me.
    public static final int TYPE_WEEKLY = 5;

    private static String[][] TYPES =
                                      {
                                      {"日报", "1"},
                                      {"月报", "2"},
                                      {"季报", "3"},
                                      {"年报", "4"},
                                      {"周报", "5"}
    };


    private String branch;
    private String code;
    private String edition;
    private String name;
    private String operator;
    private String type;
    private String cycle;
    private String feature;
    private String currency;
    private String remark;


    public DefineInfo()
    {
    }

    public DefineInfo(String branch, String code, String edition)
    {
        setBranch(branch);
        setCode(code);
        setEdition(edition);
    }

    public static void main(String[] args) throws Exception
    {
        DefineInfo reportInfo1 = new DefineInfo();
    }

    public String getBranch()
    {
        return branch;
    }

    public void setBranch(String branch)
    {
        this.branch = branch;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getEdition()
    {
        return edition;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    /**
     * 根据报表信息确定定义文件位置.
     * @return 定义文件绝对位置
     */
    public String getDefineFilePath()
    {
        return SysConfig.FILEPATH
                + "define"
                + SysConfig.FILESEPARATOR
                + getBranch()
                + SysConfig.FILESEPARATOR
                + getBranch()
                + SysConfig.REPORTJOINCHAR
                + getCode()
                + SysConfig.REPORTJOINCHAR
                + getEdition()
                + ".xml";
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCycle()
    {
        return cycle;
    }

    public void setCycle(String cycle)
    {
        this.cycle = cycle;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getType()
    {
        if (type.charAt(0) > '0' && type.charAt(0) < '9') //是数值
        {
            return type;
        }
        else
        {
            for (int i = 0; i < TYPES.length; i++)
            {
                if (type.equals(TYPES[i][0]))
                {
                    return TYPES[i][1];
                }
            }
            return "1";
        }
    }

    public void setType(String type)
    {
        this.type = type;
    }

    protected String getAllPropertyString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append("\t\t<branch>")
                .append(getBranch())
                .append("</branch>\n");

        sb.append("\t\t<code>")
                .append(getCode())
                .append("</code>\n");

        sb.append("\t\t<name>")
                .append(getName())
                .append("</name>\n");

        sb.append("\t\t<edition>")
                .append(getEdition())
                .append("</edition>\n");

        sb.append("\t\t<operator>")
                .append(getOperator())
                .append("</operator>\n");

        sb.append("\t\t<type>")
                .append(getType())
                .append("</type>\n");

        sb.append("\t\t<cycle>")
                .append(getCycle())
                .append("</cycle>\n");

        sb.append("\t\t<feature>")
                .append(getFeature())
                .append("</feature>\n");

        sb.append("\t\t<currency>")
                .append(getCurrency())
                .append("</currency>\n");

        sb.append("\t\t<remark>")
                .append(getRemark())
                .append("</remark>\n");

        return sb.toString();
    }

    public String toXMLString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append("\t<info>\n");

        sb.append(getAllPropertyString());

        sb.append("\t</info>\n");

        return sb.toString();
    }
}