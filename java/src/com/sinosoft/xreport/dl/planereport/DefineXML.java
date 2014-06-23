package com.sinosoft.xreport.dl.planereport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DefineXML
{

    private String rptBranch;
    private String rptCode;
    private String rptName;
    private String rptEdition;
    private String rptOperator;
    private String rptType;
    private String rptFile;
    private DataSrc rptDataSrc;
    private GlobalConditions rptGCondion;
    private Cols cols;
    private Cells cells;

    public DefineXML()
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
    public DataSrc getDataSrc()
    {
        return rptDataSrc;
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
    public void setDataSrc(DataSrc param)
    {
        rptDataSrc = param;
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
                      + "<<report_define>\r\n";
        sXML = sXML + "<info>\r\n"
               + "<branch>" + rptBranch + "</branch>\r\n"
               + "<code>" + rptCode + "</code>\r\n"
               + "<name>" + rptName + "</name>\r\n"
               + "<edition>" + rptEdition + "</edition>\r\n"
               + "<operator>" + rptOperator + "</operator>\r\n"
               + "</info>\r\n"
               + "<data type=\"" + rptType + "\">\r\n"
               + rptDataSrc.toXML()
               + rptGCondion.toXML()
               + cols.toXML()
               + cells.toXML()
               + "</data>\r\n<format>\r\n<file>" + rptFile +
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

    /**
     * ��������ȡ��SQL
     * @return �����ȡ��SQL
     */
    public String getSubjectSQL()
    {
        return rptDataSrc.getSubjectSQL();
    }

    /**
     * ��ø�������Դȡ��SQL
     * @return ��������Դȡ��SQL
     */
    public String[] getAppendSQL()
    {
        return rptDataSrc.getAppendSQL();
    }

    /**
     * ��������С��ȡ��SQL
     * @return �����С��ȡ��SQL
     */
    public String[] getSumSQL()
    {
        return rptDataSrc.getSumSQL();
    }

    /**
     * ��������ϼ�ȡ��SQL
     * @return �����ϼ�ȡ��SQL
     */
    public String getTotalSQL()
    {
        return rptDataSrc.getTotalSQL();
    }
}