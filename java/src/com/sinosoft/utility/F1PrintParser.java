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
 * <p>Description: 解析模板vts文件，并用数据文件中的内容替换它，从而生成最后要打印的
 * vts文件</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author kevin
 * @version 1.0
 */
public class F1PrintParser
{
//    private static final char FLAG_FLAG = '$';
    private static final String FLAG_ACRMULTI_RECORD = "$+";
    private static final String FLAG_MULTI_RECORD = "$*"; //循环变量数据，不唯一变量
//    private static final String FLAG_ONE_RECORD = "$="; //默认的变量模式，唯一变量
    private static final String FLAG_DISPLAY_START = "$<";
    private static final String FLAG_DISPLAY_END = "$>";
    private static final String FLAG_MODEL_END = "$/"; //行列结束符号
    private static final String FLAG_PAGE_BREAK = "$B"; //添加分页符号
    private static final String FLAG_FULL_ROW = "$R"; //行变量解析
    private static final String FLAG_DATA_LIST = "$L"; //覆盖式插入数据
    
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
     * 解析并将结果输出到输出流中
     * @param out OutputStream
     * @return boolean
     */
    public boolean output(OutputStream out)
	    {
	//    	同步F1PrintParser，独占解析，防止由于并发引起问题
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
     * 解析操作
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

        //在这里进行显示控制的处理，去掉不需要显示的信息
        controlDisplay(bmTemplate, m_fp);
        //拷贝模板中的信息
        m_bmOutput.copyAll(bmTemplate);

        int nMaxRow = bmTemplate.getMaxRow(); //模板中的最大行数
        int nMaxCol = bmTemplate.getMaxCol(); //模板中的最大列数

        // we have get all format we needed, now get a blank vts file for output
        m_bmOutput.clearRange(0, 0, nMaxRow, nMaxCol, Constants.eClearValues);
        cellFormat = bmTemplate.getCellFormat(0, 0, 0, 0);

        int nNewRow = 0;
        int nNewCol = 0;
        int nListLength = 0;
        int nListLengthCol = 0;
        int nRowHeight = 0;
        int nColWidth = 0;
        int nNewMaxRow = 0; //合并数据后的最大行数
        int nNewMaxCol = 0;
        String strText, strSubText, strXPath, strNodeName;
        String[] strListVal = null;
        int nChildPos = 0;
        int nCurrentRow = 0;
        int nCurrentCol = 0;
        int nRow, nCol;

        // 在一行扫描解析过程中，最大的“动态增加的行”的数量（解析“$*”标签而出现“动态增加的行”）。
        int nMaxListLength = 0;
        //循环模板中的行记录信息
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
            //循环模板中的列记录信息
            for (nCol = 0; nCol < nMaxCol - 1; nCol++)
            {
                nColWidth = bmTemplate.getColWidth(nCol);
                nNewCol = nCol;
                nNewCol += nListLengthCol;
                nListLengthCol = 0;
                //获取单元格中的信息
                strText = bmTemplate.getText(nRow, nCol);
                bmTemplate.setSelection(nRow, nCol, nRow, nCol);
                bmTemplate.getCellFormat(cellFormat);
                cellFormat.useAllFormats();
                //判断信息的内容
                if (strText.length() > 1)
                {
                    //获取操作符，例如：$=、$*等
                    strSubText = strText.substring(0, 2);
                }
                else
                {
                    //否则操作符为空
                    strSubText = strText;
                }
                //判定是否为行列结束符
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
                    //循环解析插入数据，目前最基本的描述格式
                    strText = strText.substring(2);
                    //截取xml标签信息
                    nChildPos = strText.lastIndexOf("/");
                    //得到父标签信息，例如：/DATASET/LCPol/ROW
                    strXPath = "/DATASET" + strText.substring(0, nChildPos);
                    //得到实际标签信息，例如：COL1
                    strNodeName = strText.substring(nChildPos + 1);
                    //查询标签
                    m_fp.query(strXPath);
                    int nColIndex = m_fp.getColIndex(strNodeName);
//                    System.out.println("查询getColIndex(strNodeName)，结果：" + nColIndex);

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
                            //如果发现内容为$B，表示需要插入一个分页符号，并且不将$B信息放入到最终文件中
                            if (m_fp.getString(nColIndex).trim().equals("$B"))
                            {
                                setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol, "");
                                //在当前行的下面插入一个分页符号
                                m_bmOutput.addRowPageBreak(nCurrentRow + 1);
                            }
                            else
                            {
                                setValue(m_bmOutput, cellFormat, nCurrentRow, nNewCol
                                        , m_fp.getString(nColIndex));
                            }
                            //m_fp.getString(nColIndex)，获取实际的信息
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
                    //如果发现$B，则在改行插入分页符
                    m_bmOutput.addRowPageBreak(nNewRow);
                }
                else if (strSubText.equals(FLAG_FULL_ROW))
                {
                    // 一次解析一行的数据
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
                    //覆盖行而不插入行
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
                    //当strText不为nul，且不为空的时候，在进行赋值操作
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

        //按照合并后的vts最大行数来循环，可以确保所有的数据都进行处理
        for (int i = 1; i <= nNewMaxRow; i++)
        {
            //特殊设置，使得每一行的高度根据内容发生变化
            //朱向峰 2005-07-25 添加
            //可根据实际需要将这段代码放置不同的位置
            m_bmOutput.setRowHeightAutomatic(i, true);
        }

        //add by wangyc:处理条形码,最多有10个条形码
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
                //获得条形码参数
                if (sParam != null && !sParam.equalsIgnoreCase(""))
                {
                    String params[] = sParam.split("&");
                    for (int j = 0; j < params.length; j++)
                    {
                        //获得条形码宽度
                        if (params[j].toLowerCase().startsWith("barheight"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarHeight(Integer.parseInt(t_Str));
                            }
                        }
                        //获得条形码宽度
                        if (params[j].toLowerCase().startsWith("barwidth"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarWidth(Integer.parseInt(t_Str));
                            }
                        }
                        //获得条形码粗细线条比例
                        if (params[j].toLowerCase().startsWith("barratio"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setBarRatio(Integer.parseInt(t_Str));
                            }
                        }
                        //获得条形码图片背景色
                        if (params[j].toLowerCase().startsWith("bgcolor"))
                        {
                            t_Str = params[j].split("=")[1];
                            bcode.setBgColor(t_Str);
                        }
                        //获得条形码颜色
                        if (params[j].toLowerCase().startsWith("forecolor"))
                        {
                            t_Str = params[j].split("=")[1];
                            bcode.setForeColor(t_Str);
                        }
                        //获得条形码图片横向空白区长度
                        if (params[j].toLowerCase().startsWith("xmargin"))
                        {
                            t_Str = params[j].split("=")[1];
                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                bcode.setXMargin(Integer.parseInt(t_Str));
                            }
                        }
                        //获得条形码图片竖向空白区长度
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

		GRObject ggr = bmTemplate.getObject("LogoZhongYi1");// 加一张logo图，中意//获得模版上的对象
		if (ggr != null) {
			GRObjectPos pos = ggr.getPos();// 对象的位置参数
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

        m_bmOutput.setAllowSelections(false); //打印显示界面不能选中
        m_bmOutput.setAllowObjectSelections(false); //打印显示界面不能选中
        m_bmOutput.setShowGridLines(false); //去掉显示中的网格
        m_bmOutput.setShowColHeading(false); //去掉显示的列头
        m_bmOutput.setShowRowHeading(false); //去掉显示的行头
        //以下的属性，可以通过xls的设置恢复
        m_bmOutput.setShowEditBar(false); //去掉显示的bar
        m_bmOutput.setShowEditBarCellRef(false); //去掉显示的bar
        m_bmOutput.setShowTabs(Constants.eTabsOff); //去掉显示的sheet

        // 不显示选中的区域，缺省会用黑色的边框将选中的区域标注出来。
        m_bmOutput.setShowSelections(Constants.eShowOff);
        //m_bmOutput.setShowVScrollBar(com.f1j.ss.BookModelImpl.eShowOff);
        //m_bmOutput.setShowHScrollBar(com.f1j.ss.BookModelImpl.eShowOff);

        m_bmOutput.setPrintGridLines(false); //在打印时去掉网格
        m_bmOutput.setPrintHeader(""); //在打印时去掉头标题
//        m_bmOutput.setPrintFooter("第 &P 页  共 &N 页"); //在打印时去掉页码
        m_bmOutput.setPrintTopMargin(bmTemplate.getPrintTopMargin());
        m_bmOutput.setPrintFooter(bmTemplate.getPrintFooter());

        m_bmOutput.saveViewInfo();
        return true;
    }


    /**
     * 去掉模板中不显示的部分。模板中的显示控制用“$</name”和“$>”来表示，显示控制只
     * 能出现在第一列，如果在数据xml文件的“/DataSet/Control/”节点下没有找到对应的“name”
     * 值，则将“$</name”和“$>”之间的行全部删除，包括控制所在的两行。
     * 在目前的实现中，显示控制不支持嵌套。
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
     * 删除vts文件中的一行
     * @param cBookModelImpl 要操作的vts文件
     * @param cBeginRow 要删除的起始行
     * @param cEndRow 要删除的终止行
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
     * 解析字符串。
     * 由一个既包含普通字符串又包含控制字符串的字符串和数据xml文件生成最后的输出字符串。
     * @param cStr 需要解析的字符串
     * @param cF1Print 数据xml文件的句柄
     * @return
     * 例如：输入“测试数据$$=/f1$测试数据”，得到“测试数据$2002-11-11测试数据”
     * 由于采用的charAt的方法，即对每一个字符串进行判断，不推荐
     * 2005-09-13 朱向峰 修改
     * 采用改进算法，利用indexOf＆substring等函数实现
     */
    private static String parseString(String cStr, F1Print cF1Print)
    {
        //初始化传入字符串为StringBuffer模式
        StringBuffer tStr = new StringBuffer(cStr);
        //初始化准备返回的StringBuffer对象
        StringBuffer tReturnStr = new StringBuffer();
        //初始化控制变量
        boolean tFlag = true;

        //如果传入的字符串中没有我们期待的数据，则直接返回
        if (tStr.indexOf("$=") == -1)
        {
            return cStr;
        }
        else
        {
            while (tFlag)
            {
                //获取$=前面的数据，放入返回字符串中
                tReturnStr.append(tStr.substring(0, tStr.indexOf("$=")));
//                System.out.println(tReturnStr);
                //在原有字符串中删除第一个$=前（含$=）的数据
                tStr.delete(0, tStr.indexOf("$=") + 2);
//                System.out.println(tStr);
                //在cF1Print中查找xml对象，追加进返回字符串中
                try
                {
                    tReturnStr.append(cF1Print.getNodeValue("/DATASET" + tStr.substring(0
                            , tStr.indexOf("$"))));
                }
                catch (Exception ex)
                {
                    System.out.print("描述信息格式不对，请查看");
                    System.out.print(cStr);
                    System.out.println(" $=要和$配套！");
                    return cStr;
                }
                //去掉原有字符串中的xml元素信息
                tStr.delete(0, tStr.indexOf("$") + 1);
//                System.out.println(tStr);
                //判定原有字符串中是否还存在$=，如果没有则改变控制变量
                if (tStr.indexOf("$=") == -1)
                {
                    tFlag = false;
                }
            }
        }
        //把原有字符串的剩余部分追加进返回字符串中
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
     * 根据指定的格式设置输出的VTS文件中指定单元格的值。
     * 在原来的程序中，对于所有的赋值操作都只是调用BookModelImpl.setText()来实现。这样的话，
     * 如果你在VTS模板文件中指定了数值型的格式，将不会在输出的VTS文件中体现出来。因为，对于
     * 数值型的数据应该调用BookModelImpl.setNumber()来实现。
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

       //初始化传入字符串为StringBuffer模式
       StringBuffer tStr = new StringBuffer("统计时间：$=/StatDay$ 我是朱向峰 $=/StatDay$ 24：00");
        //初始化准备返回的StringBuffer对象
        StringBuffer tReturnStr = new StringBuffer();
        //初始化控制变量
        boolean tFlag = true;

        //如果传入的字符串中没有我们期待的数据，则直接返回
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
//  获取文件的路径到UI
	private String getRealPath(){
//		同过vts文件定位ui
		//主要是websphere比较麻烦需要这样处理
		File f = new File(mVTSFileName);
		File get = f.getParentFile().getParentFile();
		//vts文件夹再加一层
		if(!get.toString().toLowerCase().endsWith("ui"))
			get = get.getParentFile();
		System.out.println("文件存放的路径:"+get);
		return get+"" ;
	}
}
