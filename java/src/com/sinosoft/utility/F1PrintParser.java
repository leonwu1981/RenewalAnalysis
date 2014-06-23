/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.*;

import com.f1j.ss.*;
import com.f1j.util.Format;
import com.sinosoft.report.f1report.BarCode;
import com.sinosoft.report.f1report.JRptUtility;
import org.apache.log4j.Logger;

/**
 * <p>Title: Life Information System</p>
 * <p>Description: ����ģ��vts�ļ������������ļ��е������滻�����Ӷ��������Ҫ��ӡ��
 * vts�ļ�</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author kevin
 * @version 1.0
 */
public class F1PrintParser
{
//    private static final char FLAG_FLAG = '$';
    private static final String FLAG_ACRMULTI_RECORD = "$+";
    private static final String FLAG_MULTI_RECORD = "$*"; //ѭ���������ݣ���Ψһ����
//    private static final String FLAG_ONE_RECORD = "$="; //Ĭ�ϵı���ģʽ��Ψһ����
    private static final String FLAG_DISPLAY_START = "$<";
    private static final String FLAG_DISPLAY_END = "$>";
    private static final String FLAG_MODEL_END = "$/"; //���н�������
    private static final String FLAG_PAGE_BREAK = "$B"; //��ӷ�ҳ����
    private static final String FLAG_FULL_ROW = "$R"; //�б�������
    private static final String FLAG_DATA_LIST = "$L"; //����ʽ��������
    
    private Logger m_log = Logger.getLogger(F1PrintParser.class);

    private F1Print m_fp = null;
    private String m_strTemplatePath = "";
    private String mVTSFileName = "";
    private com.f1j.ss.BookModelImpl m_bmOutput = new com.f1j.ss.BookModelImpl();

    public F1PrintParser(InputStream in, String strTemplatePath)
            throws FileNotFoundException, IOException
    {
        m_fp = new F1Print(in);
        m_strTemplatePath = strTemplatePath;
    }

    /**
     * �����������������������
     * @param out OutputStream
     * @return boolean
     */
    public boolean output(OutputStream out)
	    {
	//    	ͬ��F1PrintParser����ռ��������ֹ���ڲ�����������
	        //kevin 2006-10-12
	        synchronized(F1PrintParser.class)
	        {
	        try
	        {
	        	m_log.info("VTS Template = " + m_fp.getTemplate());
            	if (m_fp.getTemplate().equals("new.vts")) {
                	return true;
           		}

	            if (parse())
	            {
	                m_bmOutput.write(out, new com.f1j.ss.WriteParams(Constants.eFileCurrentFormat));
//	                this.destroy();
	                return true;
	            }
	            return false;
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	            m_log.error("output", ex);
	            return false;
	        }
	      }
	    }
	    
    

    /**
     * ��������
     * @return boolean
     * @throws Exception
     */
    private boolean parse()
            throws Exception
    {
        String strTemplateFile = m_strTemplatePath + m_fp.getTemplate();
        this.mVTSFileName = strTemplateFile;
        com.f1j.ss.CellFormat cellFormat = null;
        com.f1j.ss.BookModelImpl bmTemplate = new com.f1j.ss.BookModelImpl();

        bmTemplate.read(strTemplateFile, new com.f1j.ss.ReadParams());

        //�����������ʾ���ƵĴ���ȥ������Ҫ��ʾ����Ϣ
        controlDisplay(bmTemplate, m_fp);
        //����ģ���е���Ϣ
        m_bmOutput.copyAll(bmTemplate);

        int nMaxRow = bmTemplate.getMaxRow(); //ģ���е��������
        int nMaxCol = bmTemplate.getMaxCol(); //ģ���е��������

        // we have get all format we needed, now get a blank vts file for output
        m_bmOutput.clearRange(0, 0, nMaxRow, nMaxCol, Constants.eClearValues);
        cellFormat = bmTemplate.getCellFormat(0, 0, 0, 0);

        int nNewRow = 0;
        int nNewCol = 0;
        int nListLength = 0;
        int nListLengthCol = 0;
        int nRowHeight = 0;
        int nColWidth = 0;
        int nNewMaxRow = 0; //�ϲ����ݺ���������
        int nNewMaxCol = 0;
        String strText, strSubText, strXPath, strNodeName;
        String[] strListVal = null;
        int nChildPos = 0;
        int nCurrentRow = 0;
        int nCurrentCol = 0;
        int nRow, nCol;

        // ��һ��ɨ����������У����ġ���̬���ӵ��С���������������$*����ǩ�����֡���̬���ӵ��С�����
        int nMaxListLength = 0;
        //ѭ��ģ���е��м�¼��Ϣ
        for (nRow = 0; nRow < nMaxRow - 1; nRow++)
        {
            nNewRow += nListLength;
            nListLength = 0;
            nRowHeight = bmTemplate.getRowHeight(nRow);
//            nNewMaxRow = nNewMaxRow < nNewRow + 1 ? nNewRow + 1 : nNewMaxRow;
            if (nNewMaxRow < nNewRow + 1)
            {
                nNewMaxRow = nNewRow + 1;
            }

            nMaxListLength = 0;
            //ѭ��ģ���е��м�¼��Ϣ
            for (nCol = 0; nCol < nMaxCol - 1; nCol++)
            {
                nColWidth = bmTemplate.getColWidth(nCol);
                nNewCol = nCol;
                nNewCol += nListLengthCol;
                nListLengthCol = 0;
                //��ȡ��Ԫ���е���Ϣ
                strText = bmTemplate.getText(nRow, nCol);
                bmTemplate.setSelection(nRow, nCol, nRow, nCol);
                bmTemplate.getCellFormat(cellFormat);
                cellFormat.useAllFormats();
                //�ж���Ϣ������
                if (strText.length() > 1)
                {
                    //��ȡ�����������磺$=��$*��
                    strSubText = strText.substring(0, 2);
                }
                else
                {
                    //���������Ϊ��
                    strSubText = strText;
                }
                //�ж��Ƿ�Ϊ���н�����
                if (strSubText.equals(FLAG_MODEL_END))
                {
                    if (nNewMaxCol < nNewCol + 1)
                    {
                        nNewMaxCol = nNewCol + 1;
                    }
                    break;
                }
                else if (strSubText.equals(FLAG_MULTI_RECORD))
                {
                    //ѭ�������������ݣ�Ŀǰ�������������ʽ
                    strText = strText.substring(2);
                    //��ȡxml��ǩ��Ϣ
                    nChildPos = strText.lastIndexOf("/");
                    //�õ�����ǩ��Ϣ�����磺/DATASET/LCPol/ROW
                    strXPath = "/DATASET" + strText.substring(0, nChildPos);
                    //�õ�ʵ�ʱ�ǩ��Ϣ�����磺COL1
                    strNodeName = strText.substring(nChildPos + 1);
                    //��ѯ��ǩ
                    m_fp.query(strXPath);
                    int nColIndex = m_fp.getColIndex(strNodeName);
//                    System.out.println("��ѯgetColIndex(strNodeName)�������" + nColIndex);

                    nCurrentRow = nNewRow;
                    int nListLen = 0;
                    while (m_fp.next())
                    {
                        if (nListLen > nMaxListLength)
                        {
                            m_bmOutput.insertRange(nCurrentRow, nNewCol, nCurrentRow, nNewCol
                                    , Constants.eShiftRows);
                        }
                        if (strText.length() > 0)
                        {
                            //�����������Ϊ$B����ʾ��Ҫ����һ����ҳ���ţ����Ҳ���$B��Ϣ���뵽�����ļ���
                            if (m_fp.getString(nColIndex).trim().equals("$B"))
                            {
                                setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol, "");
                                //�ڵ�ǰ�е��������һ����ҳ����
                                m_bmOutput.addRowPageBreak(nCurrentRow + 1);
                            }
                            else
                            {
                                setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol
                                        , m_fp.getString(nColIndex));
                            }
                            //m_fp.getString(nColIndex)����ȡʵ�ʵ���Ϣ
                        }
                        m_bmOutput.setRowHeight(nCurrentRow, nRowHeight);
                        m_bmOutput.setSelection(nCurrentRow, nNewCol, nCurrentRow, nNewCol);
                        m_bmOutput.setCellFormat(cellFormat);
                        nListLen++;
                        nCurrentRow++;
                    }

                    if (nListLen > nListLength)
                    {
                        nListLength = nListLen;
                    }

                    if (nListLen > nMaxListLength)
                    {
                        nMaxListLength = nListLen;
                    }
                }
                else if (strSubText.equals(FLAG_ACRMULTI_RECORD))
                {
                    strText = strText.substring(2);
                    nChildPos = strText.lastIndexOf("/");
                    strXPath = "/DATASET" + strText.substring(0, nChildPos);
                    strNodeName = strText.substring(nChildPos + 1);
                    strListVal = m_fp.getNodeListValue(strXPath, strNodeName);
                    if (nListLengthCol < strListVal.length)
                    {
                        nListLengthCol = strListVal.length;
                    }
                    for (int k = 0; k < strListVal.length; k++)
                    {
                        nCurrentCol = nNewCol + k;
                        if (k > 0 && nNewRow == 0)
                        {
                            m_bmOutput.insertRange(nNewRow, nCurrentCol, nNewRow, nCurrentCol
                                    , Constants.eShiftRows);
                        }
                        if (strText.length() > 0)
                        {
                            setValue(m_bmOutput, cellFormat, nNewRow, nCurrentCol, strListVal[k]);
                        }
                        m_bmOutput.setColWidth(nCurrentCol, nColWidth);
                        //m_bmOutput.setRowHeight(nCurrentRow, nRowHeight);
                        m_bmOutput.setSelection(nNewRow, nCurrentCol, nNewRow, nCurrentCol);
                        m_bmOutput.setCellFormat(cellFormat);
                    }
                }
                else if (strSubText.equals(FLAG_PAGE_BREAK))
                {
                    //�������$B�����ڸ��в����ҳ��
                    m_bmOutput.addRowPageBreak(nNewRow);
                }
                else if (strSubText.equals(FLAG_FULL_ROW))
                {
                    // һ�ν���һ�е�����
                    strXPath = "/DATASET" + strText.substring(2);

                    m_fp.query(strXPath);

                    int nColCount = m_fp.getColCount();
                    int nColIndex = 0;

                    nCurrentRow = nNewRow;

                    int nListLen = 0;

                    while (m_fp.next())
                    {
                        if (nListLen > nMaxListLength)
                        {
                            m_bmOutput.insertRange(nCurrentRow, nNewCol, nCurrentRow
                                    , nNewCol + nColCount, Constants.eShiftRows);
                        }

                        for (nColIndex = 0; nColIndex < nColCount; nColIndex++)
                        {

                            bmTemplate.setSelection(nRow, nCol + nColIndex, nRow, nCol + nColIndex);
                            cellFormat = bmTemplate.getCellFormat();
                            cellFormat.useAllFormats();

                            setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol + nColIndex
                                    , m_fp.getString(nColIndex));

                            m_bmOutput.setSelection(nCurrentRow, nNewCol + nColIndex, nCurrentRow
                                    , nNewCol + nColIndex);
                            m_bmOutput.setCellFormat(cellFormat);

                        }

                        nListLen++;
                        nCurrentRow++;
                        m_bmOutput.setRowHeight(nCurrentRow, nRowHeight);
                    }

                    nListLengthCol = nColCount;

                    if (nListLen > nListLength)
                    {
                        nListLength = nListLen;
                    }

                    if (nListLen > nMaxListLength)
                    {
                        nMaxListLength = nListLen;
                    }
                }
                else if (strSubText.equals(FLAG_DATA_LIST))
                {
                    //�����ж���������
                    strText = strText.substring(2);
                    nChildPos = strText.lastIndexOf("/");
                    strXPath = "/DATASET" + strText.substring(0, nChildPos);
                    strNodeName = strText.substring(nChildPos + 1);

                    m_fp.query(strXPath);
                    int nColIndex = m_fp.getColIndex(strNodeName);

                    nCurrentRow = nNewRow;

                    int nListLen = 0;

                    while (m_fp.next())
                    {

                        if (strText.length() > 0)
                        {
                            setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol
                                    , m_fp.getString(nColIndex));
                        }

                        m_bmOutput.setRowHeight(nCurrentRow, nRowHeight);
                        m_bmOutput.setSelection(nCurrentRow, nNewCol, nCurrentRow, nNewCol);
                        m_bmOutput.setCellFormat(cellFormat);

                        nListLen++;
                        nCurrentRow++;
                    }

                    if (nListLen > nMaxListLength)
                    {
                        nMaxListLength = nListLen;
                    }
                }
                else
                {
                    //��strText��Ϊnul���Ҳ�Ϊ�յ�ʱ���ڽ��и�ֵ����
                    if (strText != null && !strText.equals(""))
                    {
                        setValue(m_bmOutput, cellFormat, nNewRow, nNewCol, parseString(strText
                                , m_fp));
                    }
                    m_bmOutput.setSelection(nNewRow, nNewCol, nNewRow, nNewCol);
                    m_bmOutput.setCellFormat(cellFormat);
                    if (nListLength < 1)
                    {
                        nListLength = 1;
                    }
                }
            }
            // end of for(nCol = 0; nCol < nMaxCol - 1; nCol ++)
            if (0 == nCol)
            {
                break;
            }
        }

        //���պϲ����vts���������ѭ��������ȷ�����е����ݶ����д���
        for (int i = 1; i <= nNewMaxRow; i++)
        {
            //�������ã�ʹ��ÿһ�еĸ߶ȸ������ݷ����仯
            //����� 2005-07-25 ���
            //�ɸ���ʵ����Ҫ����δ�����ò�ͬ��λ��
            m_bmOutput.setRowHeightAutomatic(i, true);
        }

        //add by wangyc:����������,�����10��������
        GRObject gr = null;
        String sCode = null;
        String sParam = null;
        for (int k = 1; k <= 10; k++)
        {
            gr = bmTemplate.getObject("BarCode" + k);
            sCode = m_fp.getNodeValue("/DATASET/BarCode" + k);
            sParam = m_fp.getNodeValue("/DATASET/BarCodeParam" + k);

            if (gr != null && sCode != null && !sCode.equalsIgnoreCase(""))
            {
                String t_Str = "";
                BarCode bcode = new BarCode(sCode);
                //������������
                if (sParam != null && !sParam.equalsIgnoreCase(""))
                {
                    String params[] = sParam.split("&");
                    for (int j = 0; j < params.length; j++)
                    {
                        //�����������
                        if (params[j].toLowerCase().startsWith("barheight"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarHeight(Integer.parseInt(t_Str));
                            }
                        }
                        //�����������
                        if (params[j].toLowerCase().startsWith("barwidth"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarWidth(Integer.parseInt(t_Str));
                            }
                        }
                        //����������ϸ��������
                        if (params[j].toLowerCase().startsWith("barratio"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarRatio(Integer.parseInt(t_Str));
                            }
                        }
                        //���������ͼƬ����ɫ
                        if (params[j].toLowerCase().startsWith("bgcolor"))
                        {
                            t_Str = params[j].split("=")[1];
                            bcode.setBgColor(t_Str);
                        }
                        //�����������ɫ
                        if (params[j].toLowerCase().startsWith("forecolor"))
                        {
                            t_Str = params[j].split("=")[1];
                            bcode.setForeColor(t_Str);
                        }
                        //���������ͼƬ����հ�������
                        if (params[j].toLowerCase().startsWith("xmargin"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setXMargin(Integer.parseInt(t_Str));
                            }
                        }
                        //���������ͼƬ����հ�������
                        if (params[j].toLowerCase().startsWith("ymargin"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setYMargin(Integer.parseInt(t_Str));
                            }
                        }
                    }
                }
                bcode.setFormatType(BarCode.FORMAT_GIF);
                GRObjectPos pos = gr.getPos();
                m_bmOutput.addPicture(pos.getX1(), pos.getY1(), pos.getX2(), pos.getY2()
                        , bcode.getBytes());
            }
        }

		GRObject ggr = bmTemplate.getObject("LogoZhongYi1");// ��һ��logoͼ������//���ģ���ϵĶ���
		if (ggr != null) {
			GRObjectPos pos = ggr.getPos();// �����λ�ò���
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				String tPath = this.getRealPath();
				tPath = tPath.replace('\\', File.separatorChar);
				String tFileName = tPath + "/common/images/LogoZhongYi1."
						+ BarCode.FORMAT_BMP;

				byte[] b = new byte[1000];
				int len;
				FileInputStream fis = new FileInputStream(tFileName);
				while ((len = fis.read(b)) > 0)
					out.write(b, 0, len);
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw ex;
			}
			m_bmOutput.addPicture(pos.getX1(), pos.getY1(), pos.getX2(), pos
					.getY2(), out.toByteArray());
		}
        
        m_bmOutput.objectBringToFront();

        m_bmOutput.setSelection(0, 0, nNewMaxRow - 1, nNewMaxCol - 1);
        m_bmOutput.setPrintArea();

        m_bmOutput.setAllowSelections(false); //��ӡ��ʾ���治��ѡ��
        m_bmOutput.setAllowObjectSelections(false); //��ӡ��ʾ���治��ѡ��
        m_bmOutput.setShowGridLines(false); //ȥ����ʾ�е�����
        m_bmOutput.setShowColHeading(false); //ȥ����ʾ����ͷ
        m_bmOutput.setShowRowHeading(false); //ȥ����ʾ����ͷ
        //���µ����ԣ�����ͨ��xls�����ûָ�
        m_bmOutput.setShowEditBar(false); //ȥ����ʾ��bar
        m_bmOutput.setShowEditBarCellRef(false); //ȥ����ʾ��bar
        m_bmOutput.setShowTabs(Constants.eTabsOff); //ȥ����ʾ��sheet

        // ����ʾѡ�е�����ȱʡ���ú�ɫ�ı߿�ѡ�е������ע������
        m_bmOutput.setShowSelections(Constants.eShowOff);
        //m_bmOutput.setShowVScrollBar(com.f1j.ss.BookModelImpl.eShowOff);
        //m_bmOutput.setShowHScrollBar(com.f1j.ss.BookModelImpl.eShowOff);

        m_bmOutput.setPrintGridLines(false); //�ڴ�ӡʱȥ������
        m_bmOutput.setPrintHeader(""); //�ڴ�ӡʱȥ��ͷ����
//        m_bmOutput.setPrintFooter("�� &P ҳ  �� &N ҳ"); //�ڴ�ӡʱȥ��ҳ��
        m_bmOutput.setPrintTopMargin(bmTemplate.getPrintTopMargin());
        m_bmOutput.setPrintFooter(bmTemplate.getPrintFooter());

        m_bmOutput.saveViewInfo();
        return true;
    }


    /**
     * ȥ��ģ���в���ʾ�Ĳ��֡�ģ���е���ʾ�����á�$</name���͡�$>������ʾ����ʾ����ֻ
     * �ܳ����ڵ�һ�У����������xml�ļ��ġ�/DataSet/Control/���ڵ���û���ҵ���Ӧ�ġ�name��
     * ֵ���򽫡�$</name���͡�$>��֮�����ȫ��ɾ���������������ڵ����С�
     * ��Ŀǰ��ʵ���У���ʾ���Ʋ�֧��Ƕ�ס�
     * @param bmTemplate BookModelImpl
     * @param fp F1Print
     * @throws Exception
     */
    private static void controlDisplay(BookModelImpl bmTemplate, F1Print fp)
            throws Exception
    {
        int nRow = 0;
        int nEndRow = 0; // end row of delete range
        boolean bNeedDelete = false;
        char[] cDelCtrl = null;

        cDelCtrl = new char[bmTemplate.getLastRow() + 1];

        // In the first scan, we keep track whether a line should be deleted in
        // cDelCtrl. 'D' for delete; 'R' for reserve.
        nRow = 0;

        for (nRow = 0; nRow < bmTemplate.getMaxRow(); nRow++)
        {
            // First, we assume that this line will be reserved
            cDelCtrl[nRow] = 'R';

            String strText = bmTemplate.getText(nRow, 0);
            if (strText != null && strText.length() > 1)
            {
                String strSubText = strText.substring(0, 2);
                if (strSubText.equals(FLAG_DISPLAY_START))
                {
                    if (fp.getDisplayControl(strText.substring(2)).equals(""))
                    {
                        bNeedDelete = true;
                    }

                    // The display control should be deleted itself.
                    cDelCtrl[nRow] = 'D';
                }
                else if (strSubText.equals(FLAG_DISPLAY_END))
                {
                    bNeedDelete = false;

                    // The display control should be deleted itself.
                    cDelCtrl[nRow] = 'D';

                }
                else if (strSubText.equals(FLAG_MODEL_END))
                {
                    break; // When end of model is found, jump out of loop

                }
                else
                {
                    if (bNeedDelete)
                    {
                        cDelCtrl[nRow] = 'D';
                    }
                }
            }
            else
            {
                // end of if( strText != null && strText.length() > 2 )
                if (bNeedDelete)
                {
                    cDelCtrl[nRow] = 'D';
                }
            }
        }

        // Then, we analysis cDelCtrl, delete lines
        //
        // TIP : When deleting lines, we begin with the last line and end with
        // the first line. It is so boring that deleting lines by begining with
        // the first line.
        nEndRow = -1;
        while (nRow-- > 0)
        {
            // We want the end row of the delete range. Attention, we begin with
            // the last line.
            if (nEndRow == -1)
            {
                if (cDelCtrl[nRow] == 'D')
                {
                    nEndRow = nRow;
                }
            }
            else
            {
                // If we get nEndRow already, we want nBeginRow.
                if (cDelCtrl[nRow] == 'R')
                {
                    deleteRow(bmTemplate, nRow + 1, nEndRow);
                    nEndRow = -1;
                }
            }
        }

        // boundary check.
        if (nEndRow != -1)
        {
            deleteRow(bmTemplate, 0, nEndRow);
        }
    }


    /**
     * ɾ��vts�ļ��е�һ��
     * @param cBookModelImpl Ҫ������vts�ļ�
     * @param cBeginRow Ҫɾ������ʼ��
     * @param cEndRow Ҫɾ������ֹ��
     * @throws Exception
     */
    private static void deleteRow(com.f1j.ss.BookModelImpl cBookModelImpl, int cBeginRow,
            int cEndRow)
            throws Exception
    {
        cBookModelImpl.deleteRange(cBeginRow, cBookModelImpl.getMaxCol(), cEndRow
                , cBookModelImpl.getMaxCol(), Constants.eShiftRows);
    }


    /**
     * �����ַ�����
     * ��һ���Ȱ�����ͨ�ַ����ְ��������ַ������ַ���������xml�ļ�������������ַ�����
     * @param cStr ��Ҫ�������ַ���
     * @param cF1Print ����xml�ļ��ľ��
     * @return
     * ���磺���롰��������$$=/f1$�������ݡ����õ�����������$2002-11-11�������ݡ�
     * ���ڲ��õ�charAt�ķ���������ÿһ���ַ��������жϣ����Ƽ�
     * 2005-09-13 ����� �޸�
     * ���øĽ��㷨������indexOf��substring�Ⱥ���ʵ��
     */
    private static String parseString(String cStr, F1Print cF1Print)
    {
        //��ʼ�������ַ���ΪStringBufferģʽ
        StringBuffer tStr = new StringBuffer(cStr);
        //��ʼ��׼�����ص�StringBuffer����
        StringBuffer tReturnStr = new StringBuffer();
        //��ʼ�����Ʊ���
        boolean tFlag = true;

        //���������ַ�����û�������ڴ������ݣ���ֱ�ӷ���
        if (tStr.indexOf("$=") == -1)
        {
            return cStr;
        }
        else
        {
            while (tFlag)
            {
                //��ȡ$=ǰ������ݣ����뷵���ַ�����
                tReturnStr.append(tStr.substring(0, tStr.indexOf("$=")));
//                System.out.println(tReturnStr);
                //��ԭ���ַ�����ɾ����һ��$=ǰ����$=��������
                tStr.delete(0, tStr.indexOf("$=") + 2);
//                System.out.println(tStr);
                //��cF1Print�в���xml����׷�ӽ������ַ�����
                try
                {
                    tReturnStr.append(cF1Print.getNodeValue("/DATASET" + tStr.substring(0
                            , tStr.indexOf("$"))));
                }
                catch (Exception ex)
                {
                    System.out.print("������Ϣ��ʽ���ԣ���鿴");
                    System.out.print(cStr);
                    System.out.println(" $=Ҫ��$���ף�");
                    return cStr;
                }
                //ȥ��ԭ���ַ����е�xmlԪ����Ϣ
                tStr.delete(0, tStr.indexOf("$") + 1);
//                System.out.println(tStr);
                //�ж�ԭ���ַ������Ƿ񻹴���$=�����û����ı���Ʊ���
                if (tStr.indexOf("$=") == -1)
                {
                    tFlag = false;
                }
            }
        }
        //��ԭ���ַ�����ʣ�ಿ��׷�ӽ������ַ�����
        tReturnStr.append(tStr);
        return tReturnStr.toString();

//        String strNew = "";
//        String strTemp = "";
//        int nFlag = 0;
//        for (int nIndex = 0; nIndex < cStr.length(); nIndex++)
//        {
//            if (FLAG_FLAG == cStr.charAt(nIndex))
//            {
//                switch (nFlag)
//                {
//                    case 0:
//
//                        // ordinary string
//                        nFlag = 1;
//                        strNew += strTemp;
//                        System.out.println(strNew);
//                        strTemp = "";
//                        break;
//                    case 1:
//
//                        // special string
//                        nFlag = 0;
//                        if (strTemp.equals(""))
//                        {
//                            strNew += FLAG_FLAG;
//                        }
//                        else
//                        {
//                            if (strTemp.length() > 1 && '=' == strTemp.charAt(0))
//                            {
//                                strTemp = strTemp.substring(1);
//                                strTemp = cF1Print.getNodeValue("/DATASET" + strTemp);
//                            }
//                            strNew += strTemp;
//                            System.out.println(strNew);
//                        }
//                        strTemp = "";
//                        break;
//                }
//            }
//            else
//            {
//                strTemp += cStr.charAt(nIndex);
//            }
//        }
//
//        if (1 == nFlag)
//        {
//            // special string
//            if (strTemp.length() > 1 && '=' == strTemp.charAt(0))
//            {
//                strTemp = strTemp.substring(1);
//                strTemp = cF1Print.getNodeValue("/DATASET" + strTemp);
//            }
//        }
//        return strNew + strTemp;
    }


    /**
     * Kevin 2003-04-01
     * ����ָ���ĸ�ʽ���������VTS�ļ���ָ����Ԫ���ֵ��
     * ��ԭ���ĳ����У��������еĸ�ֵ������ֻ�ǵ���BookModelImpl.setText()��ʵ�֡������Ļ���
     * �������VTSģ���ļ���ָ������ֵ�͵ĸ�ʽ���������������VTS�ļ������ֳ�������Ϊ������
     * ��ֵ�͵�����Ӧ�õ���BookModelImpl.setNumber()��ʵ�֡�
     * @param bm BookModelImpl
     * @param cf CellFormat
     * @param nRow int
     * @param nCol int
     * @param strValue String
     * @throws Exception
     */
    private static void setValue(BookModelImpl bm, CellFormat cf, int nRow, int nCol
            , String strValue)
            throws Exception
    {
        if (strValue == null || strValue.equals(""))
        {
            return;
        }
        if (cf.getValueFormatType() == Format.eValueFormatTypeNumber)
        {
        	if(strValue.indexOf(',')!=-1){
        	 bm.setText(nRow, nCol, strValue);
        	}else{
        	 bm.setNumber(nRow, nCol, Double.parseDouble(strValue));
        	}
        }else{
        	bm.setText(nRow, nCol, strValue);
        }
    }

    public static void main(String[] args)
    {
//        String strText = "$*/LCPol/ROW/COL1";
//        strText = strText.substring(2);
//        int nChildPos = strText.lastIndexOf("/");
//        System.out.println("/DATASET" + strText.substring(0, nChildPos));
//        System.out.println(strText.substring(nChildPos + 1));

       //��ʼ�������ַ���ΪStringBufferģʽ
       StringBuffer tStr = new StringBuffer("ͳ��ʱ�䣺$=/StatDay$ ��������� $=/StatDay$ 24��00");
        //��ʼ��׼�����ص�StringBuffer����
        StringBuffer tReturnStr = new StringBuffer();
        //��ʼ�����Ʊ���
        boolean tFlag = true;

        //���������ַ�����û�������ڴ������ݣ���ֱ�ӷ���
        if (tStr.indexOf("$=") > -1)
        {
            while (tFlag)
            {
                tReturnStr.append(tStr.substring(0, tStr.indexOf("$=")));
                System.out.println(tReturnStr);
                tStr.delete(0, tStr.indexOf("$=") + 2);
                System.out.println(tStr);
                System.out.println(tStr.substring(0, tStr.indexOf("$")));
                tStr.delete(0, tStr.indexOf("$") + 1);
                System.out.println(tStr);

                if (tStr.indexOf("$=") == -1)
                {
                    tFlag = false;
                }
            }
        }
        tReturnStr.append(tStr);
        System.out.println(tReturnStr);
    }
//  ��ȡ�ļ���·����UI
	private String getRealPath(){
//		ͬ��vts�ļ���λui
		//��Ҫ��websphere�Ƚ��鷳��Ҫ��������
		File f = new File(mVTSFileName);
		File get = f.getParentFile().getParentFile();
		//vts�ļ����ټ�һ��
		if(!get.toString().toLowerCase().endsWith("ui"))
			get = get.getParentFile();
		System.out.println("�ļ���ŵ�·��:"+get);
		return get+"" ;
	}
}
