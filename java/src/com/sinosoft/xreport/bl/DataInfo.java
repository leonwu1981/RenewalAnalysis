package com.sinosoft.xreport.bl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.SysConfig;

/**
 * 报表数据信息.
 * 是定义信息的子类,增加开始日期,结束日期...
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class DataInfo extends DefineInfo
{

    private String startDate;
    private String endDate;
    private String calculateDate;

    private String calculateBranch;
    private Map parametersValueMap;


    public DataInfo()
    {
    }


    private void calDate() throws Exception
    {
        //parse time
        String[] seDate = StringUtility.parseTime(getCalculateDate(),
                                                  Integer.parseInt(getType()),
                                                  Integer.parseInt(getCycle()),
                                                  SysConfig.SYSTEMDATEPATTERN,
                                                  SysConfig.SYSTEMDATEPATTERN);

        startDate = seDate[0];
        endDate = seDate[1];
    }

    public static void main(String[] args) throws Exception
    {
        DataInfo di = new DataInfo();
        di.setBranch("330700");
        di.setCode("js0207h");
        di.setEdition("20020101");
        di.setCalculateBranch("111111");
        di.setCalculateDate("2004-01-01");
        di.setType("" + TYPE_DAILY);
        di.setCycle("2");

        DataInfo bq = di.bq("js0207");
        DataInfo sq = di.sq("js0207");

        System.out.println(bq.getDataFile() + "\n" + sq.getDataFile());
    }

    public String getEndDate() throws Exception
    {
        if (endDate == null)
        {
            calDate();
        }
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getStartDate() throws Exception
    {
        if (startDate == null)
        {
            calDate();
        }
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }


    /**
     * 得到本期报表,与EZReport 4.0兼容
     * @param calculateBranch 计算单位! NOT 定义单位
     * @param code 报表代码
     * @param edition 版别
     * @return 本期报表
     * @throws Exception 错误
     */
    public DataInfo bq(String calculateBranch, String code, String edition) throws
            Exception
    {
        DataInfo d = createDataInfo(calculateBranch, code, edition);

        //parse time
        String[] seDate = StringUtility.parseTime(getEndDate(),
                                                  Integer.parseInt(getType()),
                                                  Integer.parseInt(getCycle()),
                                                  SysConfig.SYSTEMDATEPATTERN,
                                                  SysConfig.SYSTEMDATEPATTERN);

        d.setStartDate(seDate[0]);
        d.setEndDate(seDate[1]);

        return d;
    }

    /**
     * 得到计算单位,版别于本张报表相同的本期报表.
     * @param code 报表编码
     * @return 本期报表
     * @throws Exception 时间错误
     */
    public DataInfo bq(String code) throws Exception
    {
        //如果参数中包含了单位和版别,替换
        int _pos = code.indexOf("_");
        int _2pos = code.indexOf("_", _pos + 1);
        if (_pos > 0 && _2pos > 0)
        {
            String branch = code.substring(0, _pos);
            String sCode = code.substring(_pos + 1, _2pos);
            String edition = code.substring(_2pos + 1);

            return bq(branch, sCode, edition);
        }

        return bq(getCalculateBranch(), code, getEdition());
    }

    /**
     * 上年同期.
     * 年减一.
     * @param branch
     * @param code
     * @param edition
     * @return
     * @throws Exception
     */
    public DataInfo tq(String branch, String code, String edition) throws
            Exception
    {
        DataInfo d = createDataInfo(calculateBranch, code, edition);

        //同期结束时间=本期结束时间的年份-1
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SysConfig.SYSTEMDATEPATTERN);
        c.setTime(sdf.parse(getEndDate()));
        c.add(Calendar.YEAR, -1);
        //同期结束时间
        String tqEndDate = sdf.format(c.getTime());

        //parse time
        String[] seDate = StringUtility.parseTime(tqEndDate,
                                                  Integer.parseInt(getType()),
                                                  Integer.parseInt(getCycle()),
                                                  SysConfig.SYSTEMDATEPATTERN,
                                                  SysConfig.SYSTEMDATEPATTERN);

        d.setStartDate(seDate[0]);
        d.setEndDate(seDate[1]);

        return d;
    }

    /**
     * 上期.
     * 计算思路:本期的开始时间减一天作为上期的结束时间.
     *
     * @param branch 计算单位,非定义单位
     * @param code
     * @param edition
     * @return
     * @throws Exception
     */
    public DataInfo sq(String branch, String code, String edition) throws
            Exception
    {
        DataInfo d = createDataInfo(calculateBranch, code, edition);

        //上期结束时间=本期开始时间-1
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SysConfig.SYSTEMDATEPATTERN);
        c.setTime(sdf.parse(getStartDate()));
        c.add(Calendar.DATE, -1);
        //上期结束时间
        String sqEndDate = sdf.format(c.getTime());

        //parse time
        String[] seDate = StringUtility.parseTime(sqEndDate,
                                                  Integer.parseInt(getType()),
                                                  Integer.parseInt(getCycle()),
                                                  SysConfig.SYSTEMDATEPATTERN,
                                                  SysConfig.SYSTEMDATEPATTERN);

        d.setStartDate(seDate[0]);
        d.setEndDate(seDate[1]);

        return d;
    }

    public DataInfo tq(String code) throws Exception
    {
        //如果参数中包含了单位和版别,替换
        int _pos = code.indexOf("_");
        int _2pos = code.indexOf("_", _pos + 1);
        if (_pos > 0 && _2pos > 0)
        {
            String branch = code.substring(0, _pos);
            String sCode = code.substring(_pos + 1, _2pos);
            String edition = code.substring(_2pos + 1);

            return tq(branch, sCode, edition);
        }

        return tq(getBranch(), code, getEdition());
    }

    public DataInfo sq(String code) throws Exception
    {
        //如果参数中包含了单位和版别,替换
        int _pos = code.indexOf("_");
        int _2pos = code.indexOf("_", _pos + 1);
        if (_pos > 0 && _2pos > 0)
        {
            String branch = code.substring(0, _pos);
            String sCode = code.substring(_pos + 1, _2pos);
            String edition = code.substring(_2pos + 1);

            return sq(branch, sCode, edition);
        }

        return sq(getBranch(), code, getEdition());
    }


    /**
     * 创建一个DataInfo对象
     * @param calculateBranch 计算单位
     * @param code 代码
     * @param edition 版别
     * @return 计算对象
     */
    private DataInfo createDataInfo(String calculateBranch, String code,
                                    String edition)
    {
        DataInfo di = new DataInfo();
        di.setCalculateBranch(calculateBranch);
        di.setCode(code);
        di.setEdition(edition);
        di.setBranch(getBranch());

        return di;
    }


    /**
     * 根据数据信息得到文件位置.
     * 格式=数据位置/data/定义单位/计算单位/代码_版别_开始时间_结束时间_参数1_参数1值_参数2_参数2值....xml
     * @return 数据文件路径.
     * @throws Exception 系统错误
     */
    public String getDataFile() throws Exception
    {
        return SysConfig.FILEPATH
                + "data"
                + SysConfig.FILESEPARATOR
                + getBranch()
                + SysConfig.FILESEPARATOR
                + getCalculateBranch()
                + SysConfig.FILESEPARATOR
//        +getBranch()
//        +getCalculateBranch() // Caution: 计算单位,而非定义单位...
//
//        +SysConfig.REPORTJOINCHAR
                + getCode()
                + SysConfig.REPORTJOINCHAR
                + getEdition()
                + SysConfig.REPORTJOINCHAR
                +
                StringUtility.convertDate(getStartDate(), SysConfig.getSystemDatePattern(),
                                          "yyyyMMdd")
                + SysConfig.REPORTJOINCHAR
                +
                StringUtility.convertDate(getEndDate(), SysConfig.getSystemDatePattern(),
                                          "yyyyMMdd")
                + getParamString()
                + ".xml";
    }

    public String getCalculateBranch()
    {
        return calculateBranch;
    }

    public void setCalculateBranch(String calculateBranch)
    {
        this.calculateBranch = calculateBranch;
    }

    /**
     * 把所有的变量,变量值 拼成 _变量_值_变量_值...
     * @return 拼成的字符串
     */
    private String getParamString()
    {
        StringBuffer result = new StringBuffer("");
        if (parametersValueMap == null || parametersValueMap.isEmpty())
        {

        }
        else
        {
            Iterator it = parametersValueMap.keySet().iterator();
            while (it.hasNext())
            {
                Object key = it.next();

                result.append(SysConfig.REPORTJOINCHAR)
                        .append(key)
                        .append(SysConfig.REPORTJOINCHAR)
                        .append(parametersValueMap.get(key));
            }
        }

        return result.toString();
    }

    public String toXMLString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append("\t<info>\n");

        sb.append(getAllPropertyString());

        sb.append("\t</info>\n");

        return sb.toString();
    }

    protected String getAllPropertyString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append(super.getAllPropertyString());

        sb.append("\t\t<start_date>")
                .append(getStartDate())
                .append("</start_date>\n");
        sb.append("\t\t<end_date>")
                .append(getEndDate())
                .append("</end_date>\n");
        sb.append("\t\t<calculate_branch>")
                .append(getCalculateBranch())
                .append("</calculate_branch>\n");

        return sb.toString();
    }

    public Map getParametersValueMap()
    {
        return parametersValueMap;
    }

    public void setParametersValueMap(Map parametersValueMap)
    {
        this.parametersValueMap = parametersValueMap;
    }

    public String getCalculateDate()
    {
        return calculateDate;
    }

    public void setCalculateDate(String calculateDate)
    {
        this.calculateDate = calculateDate;
    }
}