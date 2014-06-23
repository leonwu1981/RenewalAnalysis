package com.sinosoft.xreport.dl;

/**
 * 得到所有地单位代码和名称，得到所有地人员代码和名称
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.util.Collection;

public class Athorize
{

    private ConfigXMLParse config = null;
    private DataSourceImpl dsi = null;
    private String branch = null;
    private String person = null;

    public Athorize()
    {
    }

    /**
     * 得到所有的单位代码和名称
     * @param null
     * @return 返回单位的代码和名称 Collection
     * @throws null
     */
    public Collection getBranch()
    {
        Collection c = null;

        try
        {
            if (branch == null)
            {
                if (config == null)
                {
                    config = new ConfigXMLParse();
                    config = config.parse();
                }
                if (dsi == null)
                {
                    dsi = new DataSourceImpl();
                }
                branch = config.getManageCom();
            }
            c = dsi.getDataSet(branch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }


    /**
     * 得到所有的人员代码和名称
     * @param null
     * @return 返回人员的代码和名称 Collection
     * @throws null
     */
    public Collection getPerson()
    {
        Collection c = null;
        try
        {
            if (person == null)
            {
                if (config == null)
                {
                    config = new ConfigXMLParse();
                    config = config.parse();
                }
                if (dsi == null)
                {
                    dsi = new DataSourceImpl();
                }
                person = config.getPerson();
            }
            c = dsi.getDataSet(person);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }


    public static void main(String[] args)
    {
        Athorize ath = new Athorize();
        Collection c = ath.getBranch();
        Object[] obj = c.toArray();
        for (int i = 1; i < obj.length; i++)
        {
            String[] str = (String[]) obj[i];
            System.out.println(str[0] + ":" + str[1]);
        }
    }


}