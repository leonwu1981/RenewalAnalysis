package com.sinosoft.xreport.bl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.SysConfig;

/**
 * ����������Ϣ.
 * �Ƕ�����Ϣ������,���ӿ�ʼ����,��������...
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
     * �õ����ڱ���,��EZReport 4.0����
     * @param calculateBranch ���㵥λ! NOT ���嵥λ
     * @param code �������
     * @param edition ���
     * @return ���ڱ���
     * @throws Exception ����
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
     * �õ����㵥λ,����ڱ��ű�����ͬ�ı��ڱ���.
     * @param code �������
     * @return ���ڱ���
     * @throws Exception ʱ�����
     */
    public DataInfo bq(String code) throws Exception
    {
        //��������а����˵�λ�Ͱ��,�滻
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
     * ����ͬ��.
     * ���һ.
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

        //ͬ�ڽ���ʱ��=���ڽ���ʱ������-1
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SysConfig.SYSTEMDATEPATTERN);
        c.setTime(sdf.parse(getEndDate()));
        c.add(Calendar.YEAR, -1);
        //ͬ�ڽ���ʱ��
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
     * ����.
     * ����˼·:���ڵĿ�ʼʱ���һ����Ϊ���ڵĽ���ʱ��.
     *
     * @param branch ���㵥λ,�Ƕ��嵥λ
     * @param code
     * @param edition
     * @return
     * @throws Exception
     */
    public DataInfo sq(String branch, String code, String edition) throws
            Exception
    {
        DataInfo d = createDataInfo(calculateBranch, code, edition);

        //���ڽ���ʱ��=���ڿ�ʼʱ��-1
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(SysConfig.SYSTEMDATEPATTERN);
        c.setTime(sdf.parse(getStartDate()));
        c.add(Calendar.DATE, -1);
        //���ڽ���ʱ��
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
        //��������а����˵�λ�Ͱ��,�滻
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
        //��������а����˵�λ�Ͱ��,�滻
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
     * ����һ��DataInfo����
     * @param calculateBranch ���㵥λ
     * @param code ����
     * @param edition ���
     * @return �������
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
     * ����������Ϣ�õ��ļ�λ��.
     * ��ʽ=����λ��/data/���嵥λ/���㵥λ/����_���_��ʼʱ��_����ʱ��_����1_����1ֵ_����2_����2ֵ....xml
     * @return �����ļ�·��.
     * @throws Exception ϵͳ����
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
//        +getCalculateBranch() // Caution: ���㵥λ,���Ƕ��嵥λ...
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
     * �����еı���,����ֵ ƴ�� _����_ֵ_����_ֵ...
     * @return ƴ�ɵ��ַ���
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