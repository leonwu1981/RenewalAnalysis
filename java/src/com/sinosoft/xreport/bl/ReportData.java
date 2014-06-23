//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ReportData.java

package com.sinosoft.xreport.bl;

import java.util.Iterator;
import java.util.Map;


public class ReportData
{
    /**������Ϣ*/
    private DataInfo dataInfo;

    /**���ݿ�*/
    private Map dataBlocks;

    private Format format;


    public ReportData()
    {

    }


    public Map getDataBlocks()
    {
        return dataBlocks;
    }

    public void setDataBlocks(Map dataBlocks)
    {
        this.dataBlocks = dataBlocks;
    }

    /**
     * �������ݲ�ѯ
     * �����û��Ĳ�ѯ���õ�����(��Ԫ������)����
     * @param name ��ѯ���,Ʃ�� [ĳ����].[ĳ��] ���� [ĳ����].[ĳ��] ���� [��].[��].[��]
     * ����Ƕ����ݿ�:[��].[��].[��].[��],��������ʡ��[��]
     * @return ���ݼ�
     * @throws Exception
     */
    public DataBlock getDataBlock(String name) throws Exception
    {
        return (DataBlock) dataBlocks.get(name);
    }

    public DataInfo getDataInfo()
    {
        return dataInfo;
    }

    public void setDataInfo(DataInfo dataInfo)
    {
        this.dataInfo = dataInfo;
    }

    /**
     * ��ѯ�������ݵ�ֵ.
     * ���Ե���������Ч.
     * @todo: ������ݵ�ʵ��.
     * @param rql
     * @return
     * @throws Exception
     */
    public String getValue(String rql) throws Exception
    {
        Iterator it = getDataBlocks().keySet().iterator();
        return getDataBlock((String) it.next()).getValue(rql);
    }

    /**
     * �õ�ָ���ĵ�Ԫ��.
     * @param rql
     * @return
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        Iterator it = getDataBlocks().keySet().iterator();
        return getDataBlock((String) it.next()).getCell(rql);
    }

    public String toXMLString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        //process instruction
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
        //start tag
        sb.append("<" + DataReader.START_TAG + ">\n");
        //data info
        sb.append(getDataInfo().toXMLString());

        //blocks' xmlString
        Object[] dataBlockArr = dataBlocks.values().toArray();
        for (int i = 0; i < dataBlockArr.length; i++)
        {
            DataBlock dataBlock = (DataBlock) dataBlockArr[i];

            sb.append(dataBlock.toXMLString());
        }

        //format
        sb.append(getFormat().toXMLString());

        //end report_data tag
        sb.append("</" + DataReader.START_TAG + ">");

        return sb.toString();
    }


    public Format getFormat()
    {
        return format;
    }

    public void setFormat(Format format)
    {
        this.format = format;
    }

}

