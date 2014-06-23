//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\PlaneReport.java
//��������ҵ��ͳ���±��ı��Ƶ����⴦�����

package com.sinosoft.xreport.bl;

import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import com.f1j.ss.CellFormat;
import com.f1j.ss.ReadParams;
import com.f1j.ss.WriteParams;
import com.f1j.swing.JBook;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.dl.DataSourceImpl;
import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XReader;
import com.sinosoft.xreport.util.XTLogger;
import com.sinosoft.xreport.util.XWriter;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ƽ���/�嵥����(��Խ�������)��
 * ְ��:
 *  1.�����������(��ѯ)����
 *  2.֧��sum(),count()
 *  3.֧�ֶ༶����С��,�ϼ�
 *  4.�����ݿ�֧��
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class PlaneReport extends ReportModel
{

    public PlaneReport()
    {

    }

    public String readDefine(ReportMain tReportMain)
    {
        return null;
    }

    public String saveDefine()
    {
        return null;
    }

    public static void makeDefine()
    {
        String cell = "A8";
        try
        {
            String formulaCol = "", nameCol = "", locationCol = "";
            String formulaCell = "", nameRow = "", locationRow = "";
            int intRowRow = 0, intRowCol = 0, intColRow = 0, intColCol = 0;
            /**��ȡ�������ļ�*/
            Document doc = XMLPathTool.parseText(XReader.readDefine(
                    "86_month4_20030401.xml"));
            /**��ȡ���ֶ���*/
            String sql = "select RiskName,RiskCode from LMRisk ";
            DataSource data = new DataSourceImpl();
            Collection cRow = data.getDataSet(sql);
            Node root = doc.getDocumentElement();
            Object[] obj = cRow.toArray();
            Vector vecObj = new Vector();
            for (int i = 0; i < obj.length - 1; i++)
            {
                vecObj.addElement(obj[i + 1]);
            }
            int intStartRow = StringUtility.Cell2Row(cell);
            int intCol = StringUtility.Cell2Col(cell);
            /**����н��*/
            for (int i = 0; i < vecObj.size(); i++)
            {
                XMLPathTool.appendNode(root, "row", "/report_define/data/rows");
            }
            /**��Ӻϼ���*/
            XMLPathTool.appendNode(root, "row", "/report_define/data/rows");
            NodeList listRow = XPathAPI.selectNodeList(root,
                    "/report_define/data/rows/row");
            /**�����н�������*/
            for (int i = 0; i < listRow.getLength(); i++)
            {
                if (i != listRow.getLength() - 1)
                {
                    String[] row = (String[]) vecObj.elementAt(i);
                    XMLPathTool.setAttrValue(listRow.item(i), "name",
                                             row[0].trim());
                    XMLPathTool.setAttrValue(listRow.item(i), "condition",
                                             "LJAPayPerson_LCPol_LMRisk.LJAPayPerson_RiskCode='" +
                                             row[1] + "'");
                    XMLPathTool.setAttrValue(listRow.item(i), "formula", "");
                    XMLPathTool.setAttrValue(listRow.item(i), "location",
                                             StringUtility.rowCol2Cell(
                            intStartRow++, intCol));
                    XMLPathTool.setValue(listRow.item(i), row[0].trim());
                }
                else
                {
                    XMLPathTool.setAttrValue(listRow.item(i), "name", "�ܼ�");
                    XMLPathTool.setAttrValue(listRow.item(i), "condition",
                                             "");
                    XMLPathTool.setAttrValue(listRow.item(i), "formula",
                                             "sum([" +
                                             XMLPathTool.getAttrValue(listRow.
                            item(0), "name") +
                                             "]:[" +
                                             XMLPathTool.getAttrValue(listRow.
                            item(listRow.getLength() - 2), "name") +
                                             "])");
                    XMLPathTool.setAttrValue(listRow.item(i), "location", "A7");
                    XMLPathTool.setValue(listRow.item(i), "�ϼ�");
                }
            }
            /**������*/
            /**F�С�G�С�H�С�M���Ǻ���*/
            for (int j = 0; j < 4; j++)
            {
                for (int i = 0; i < listRow.getLength() - 1; i++)
                {
                    XMLPathTool.appendNode(root, "cell",
                                           "/report_define/data/cells");
                }
            }
            for (int j = 0; j < 4; j++)
            {
                /**����cell��������*/
                NodeList listCell = XPathAPI.selectNodeList(root,
                        "/report_define/data/cells/cell");
                for (int i = 0; i < listRow.getLength() - 1; i++)
                {
                    /**��ȡ��ͷ��Ϣ*/
                    nameRow = XMLPathTool.getAttrValue(listRow.item(i), "name");
                    locationRow = XMLPathTool.getAttrValue(listRow.item(i),
                            "location");
                    intRowRow = StringUtility.Cell2Row(locationRow);
                    intRowCol = StringUtility.Cell2Col(locationRow);
                    /**�ϲ���ͷ����ͷ����Ϣ���Cell*/
                    switch (j)
                    {
                        case 0:
                        {
                            XMLPathTool.setValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                                 "$bq_qjsn_sq_product('" +
                                                 ((String[]) vecObj.elementAt(i))[
                                                 1] + "')");
                            XMLPathTool.setAttrValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                    "location", "F" + (intRowRow + 1));
                            break;
                        }
                        case 1:
                        {
                            XMLPathTool.setValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                                 "[�����ڽ��������ڱ���].[" + nameRow +
                                                 "]"
                                                 +
                                    "+$sq([86_month4_20030401].[�ۼ��ڽ��������ڱ���].[" +
                                                 nameRow + "])" +
                                                 "-$lj_qjsn_sq_product('" +
                                                 ((String[]) vecObj.elementAt(i))[
                                                 1] + "')");
                            XMLPathTool.setAttrValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                    "location", "G" + (intRowRow + 1));
                            break;
                        }
                        case 2:
                        {
                            XMLPathTool.setValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                                 "$bq_qjsn_xq_product('" +
                                                 ((String[]) vecObj.elementAt(i))[
                                                 1] + "')");
                            XMLPathTool.setAttrValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                    "location", "H" + (intRowRow + 1));
                            break;
                        }
                        case 3:
                        {
                            XMLPathTool.setValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                                 "$bq_xq_product('" +
                                                 ((String[]) vecObj.elementAt(i))[
                                                 1] + "')");
                            XMLPathTool.setAttrValue(listCell.item(9 +
                                    j * (listRow.getLength() - 1) + (i)),
                                    "location", "M" + (intRowRow + 1));
                            break;
                        }
                    }
                }
            }
            /**�ϴ��ļ�*/
            XWriter.writeDefine(XMLPathTool.toString(root),
                                "86_month4_20030401_gens.xml");
//           XMLPathTool.toFile(root,"d:\\1.xml");
            /**��ȡ�����ʽ�ļ�*/
            JBook jbook = new JBook();
            jbook.readURL(SysConfig.TRUEHOST +
                          "down.jsp?type=define&file=86_month4_20030401.xls",
                          new ReadParams());
            intStartRow = StringUtility.Cell2Row(cell);
            /**�޸ĸ�ʽ�ļ�*/
            for (int i = vecObj.size() - 1; i >= 0; i--)
            {
                String[] row = (String[]) vecObj.elementAt(i);
                jbook.setActiveCell(intStartRow, intCol);
                CellFormat cellFormat = jbook.getCellFormat();
                jbook.editInsert((short) 3);
                jbook.setCellFormat(cellFormat);
                jbook.setText(intStartRow, intCol, row[0].trim());
            }
            /**�ϴ���ʽ�ļ�*/
            jbook.writeURL(new URL(SysConfig.TRUEHOST +
                    "up.jsp?type=define&file=86_month4_20030401_gens.xls"),
                           new WriteParams(JBook.eFileExcel97));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            XTLogger.getLogger("PlaneReport").debug(e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        PlaneReport.makeDefine();
    }
}
