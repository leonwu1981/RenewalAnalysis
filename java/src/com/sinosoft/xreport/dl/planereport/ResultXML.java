package com.sinosoft.xreport.dl.planereport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResultXML
{

    private String rptBranch;
    private String rptCode;
    private String rptName;
    private String rptEdition;
    private String rptOperator;
    private String rptType;
    private String rptFile;
    private DataResults rptDatas = null;
    private GlobalConditions rptGCondion = null;
    private Cols cols = null;
    private Cells cells = null;

    public ResultXML()
    {}

    /**
     * ȡ����ͷ��Ϣ
     * @return ��ͷ��Ϣ
     */
    public Cols getCols()
    {
        return cols;
    }

    /**
     * ȡ������Դ��Ϣ
     * @return ����Դ��Ϣ
     */
    public DataResults getDataResults()
    {
        return rptDatas;
    }

    /**
     * ȡ��ȫ�ֲ�ѯ������Ϣ
     * @return ȫ�ֲ�ѯ������Ϣ
     */
    public GlobalConditions getGCondition()
    {
        return rptGCondion;
    }

    /**
     * ��������Դ��Ϣ
     * @param param ����Դ��Ϣ
     */
    public void setDataResults(DataResults param)
    {
        rptDatas = param;
    }

    /**
     * ����ȫ�ֲ�ѯ������Ϣ
     * @param param ȫ�ֲ�ѯ������Ϣ
     */
    public void setGCondition(GlobalConditions param)
    {
        rptGCondion = param;
    }

    /**
     * ������ͷ��Ϣ
     * @param param ��ͷ��Ϣ
     */
    public void setCols(Cols param)
    {
        cols = param;
    }

    /**
     * ȡ�õ�Ԫ����Ϣ
     * @return ��Ԫ����Ϣ
     */
    public Cells getCells()
    {
        return cells;
    }

    /**
     * ���õ�Ԫ����Ϣ
     * @param param ��Ԫ����Ϣ
     */
    public void setCells(Cells param)
    {
        cells = param;
    }

    /**
     * ȡ�ñ���ʹ�õ�λ
     * @return ����ʹ�õ�λ
     */
    public String getRptBranch()
    {
        return rptBranch;
    }

    /**
     * ȡ�ñ������
     * @return �������
     */
    public String getRptCode()
    {
        return rptCode;
    }

    /**
     * ȡ�ñ�������
     * @return ��������
     */
    public String getRptName()
    {
        return rptName;
    }

    /**
     * ȡ�ñ�����
     * @return ������
     */
    public String getRptEdition()
    {
        return rptEdition;
    }

    /**
     * ȡ�ñ���ʹ����
     * @return ����ʹ����
     */
    public String getRptOperator()
    {
        return rptOperator;
    }

    /**
     * ȡ�ñ�������
     * @return ��������
     */
    public String getRptType()
    {
        return rptType;
    }

    /**
     * ȡ�ñ����ʽ�ļ�
     * @return �����ʽ�ļ�
     */
    public String getRptFile()
    {
        return rptFile;
    }

    /**
     * ȡ�ñ�������Դ
     * @return ��������Դ
     */
    public DataResults getRptDataResults()
    {
        return rptDatas;
    }

    /**
     * ȡ�ñ���ȫ�ֲ�ѯ����
     * @return ����ȫ�ֲ�ѯ����
     */
    public GlobalConditions getRptGlobalCondition()
    {
        return rptGCondion;
    }

    /**
     * ���ñ���ʹ�õ�λ
     * @param param ����ʹ�õ�λ
     */
    public void setRptBranch(String param)
    {
        rptBranch = param;
    }

    /**
     * ���ñ������
     * @param param �������
     */
    public void setRptCode(String param)
    {
        rptCode = param;
    }

    /**
     * ���ñ�������
     * @param param ��������
     */
    public void setRptName(String param)
    {
        rptName = param;
    }

    /**
     * ���ñ�����
     * @param param ������
     */
    public void setRptEdition(String param)
    {
        rptEdition = param;
    }

    /**
     * ���ñ���ʹ����
     * @param param ����ʹ����
     */
    public void setRptOperator(String param)
    {
        rptOperator = param;
    }

    /**
     * ���ñ�������
     * @param param ��������
     */
    public void setRptType(String param)
    {
        rptType = param;
    }

    /**
     * ���ñ����ʽ�ļ�
     * @param param �����ʽ�ļ�
     */
    public void setRptFile(String param)
    {
        rptFile = param;
    }

    /**
     * ���ñ���ȫ�ֲ�ѯ����
     * @param param ����ȫ�ֲ�ѯ����
     */
    public void setRptGlobalCondition(GlobalConditions param)
    {
        rptGCondion = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<?xml version=\"1.0\" encoding=\"gb2312\"?>\r\n"
                      + "<report_define>\r\n";
        sXML = sXML + "<info>\r\n";
        if (rptBranch != null)
        {
            sXML = sXML + "<branch>" + rptBranch + "</branch>\r\n";
        }
        else
        {
            sXML = sXML + "<branch></branch>\r\n";
        }
        if (rptCode != null)
        {
            sXML = sXML + "<code>" + rptCode + "</code>\r\n";
        }
        else
        {
            sXML = sXML + "<code></code>\r\n";
        }
        if (rptName != null)
        {
            sXML = sXML + "<name>" + rptName + "</name>\r\n";
        }
        else
        {
            sXML = sXML + "<name></name>\r\n";
        }
        if (rptEdition != null)
        {
            sXML = sXML + "<edition>" + rptEdition + "</edition>\r\n";
        }
        else
        {
            sXML = sXML + "<edition></edition>\r\n";
        }
        if (rptOperator != null)
        {
            sXML = sXML + "<operator>" + rptOperator + "</operator>\r\n";
        }
        else
        {
            sXML = sXML + "<operator></operator>\r\n";
        }
        sXML = sXML + "</info>\r\n";
        if (rptType != null)
        {
            sXML = sXML + "<data type=\"" + rptType + "\">\r\n";
        }
        else
        {
            sXML = sXML + "<data type=\"\">\r\n";
        }
        if (rptGCondion != null)
        {
            sXML = sXML + rptGCondion.toXML();
        }
        else
        {
            sXML = sXML + "<global/>";
        }
        if (cols != null)
        {
            sXML = sXML + cols.toXML();
        }
        else
        {
            sXML = sXML + "<cols/>\r\n";
        }
        if (cells != null)
        {
            sXML = sXML + cells.toXML();
        }
        else
        {
            sXML = sXML + "<cells/>\r\n";
        }
        if (rptDatas != null)
        {
            sXML = sXML + rptDatas.toXML();
        }
        else
        {
            sXML = sXML + "<dataResults/>\r\n";
        }
        sXML = sXML + "</data>\r\n<format>\r\n<file>" + rptFile +
               "</file>\r\n</format>\r\n"
               + "</report_define>\r\n";
        return sXML;
    }

    /**
     * �ѵ�ǰ�������д���ļ�
     * @param fName �ļ���
     */
    public void toFile(String fName)
    {
        try
        {
            FileOutputStream aFileOS = new FileOutputStream(fName);
            String content = this.toXML();
            aFileOS.write(content.getBytes());
            aFileOS.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }
}