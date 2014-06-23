/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.report.f1report;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.f1j.ss.*;
import com.f1j.util.Format;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.StrTool;

/**
 * <p>Title: JRptList F1表格处理类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Sinosoft</p>
 * <p>Company: Sinosoft Co.,Ltd.</p>
 * @author lwt , sangood , zhuxf
 * @version 1.0
 */
public class JRptList
{

    static int ConCols = 200; //列数,现在只支持100个数据列

    static int R_Format = 0; //格式文件所在的Sheet
    static int R_Define = 1; //定义文件所在的Sheet
    static int R_CellData = 2; //按Cell定义的数据
    static int R_Head = 3; //报表的头部所在的Sheet
    static int R_End = 4; //报表的尾部所载的Sheet
    static int P_Head = 5; //报表的页头所在的Sheet
    static int P_End = 6; //报表的页尾所在的Sheet
    static int R_Data = 7; //报表的数据所在的Sheet
    static int R_Report = 8; //最后拼接完成后的Sheet

    static int Row_DSN = 1;
    static int Row_Report_Head = 2;
    static int Row_Report_End = 3;
    static int Row_Page_Head = 4;
    static int Row_Page_End = 5;
    static int Row_Count_Per_Page = 6;
    static int Row_Visible = 7; //第7行为设置列的可见
    static int Row_Data_Row = 8; //第8行为设置数据行格式描述所在的行
    static int Data_Cache = 9; //第9行为设置数据的缓冲方式
    static int Row_Data_Start = 10; //表示实际数据的开始行
    static int conDataStart = 1; //数据Sheet的开始的行

    public BookModelImpl m_FV; //存放格式文件的f1View
    public BookModelImpl m_DestFV; //如果这个报表需要显示出来，则必须设置这个F1Book
    public BookModelImpl m_DestFBV; //拷贝到新的BookView中
    public BookModelImpl m_SysFBV; //系统描述

    private Vector m_Visible; //存放列是否可见
    public int m_Cur_Format_Row; //当前处理的格式描述中的行
    public String m_ErrString = " "; //保存错误信息

    public int m_DataBaseType; //如果数据库类型支持得到字段个数，则设置为0，默认是不支持

    public int m_DataFormatRow; //数据行格式所在的行
    public int m_Data_Row; //当前处理数据所在的行
    public int m_Data_Col; //当前处理数据所在的列
    public int m_DataRowHeight; //数据行的高度

    public boolean m_NeedMerge; //对于分组，是否需要合并从复的分组名，默认为False
    public String m_Row_Col_Text_Type = "";
    //设置Row_Col_Text需要解释的类型,
    //如果为"00"表示不解释Row_Col_Text，如果为"10"，则在Row_Col_Text解释在数据区中，
    //如果为"01"，则解释在报表格式中，如果为"11"，则两边都解释,默认值为"01"
    public int m_Row_Col_Text_Start; //保存Row_Col_Text的开始行

    private Vector m_Vals; //存放变量，参数最小序号从0开始

    int mFCol, mFCols, mFRow, mFRows; //设置固定行，列的数据

    public boolean m_NeedProcess; //设置是否需要显示进程条,默认为True显示进程条
    public int m_StartDisplayRow, m_StartDisplayCol; //设置显示时报表的开始位置
    public boolean m_Need_Display_Zero; //设置是否需要显示0值,默认为不显示0值，即为false
    public boolean m_Need_Preview; //设置该报表是否需要显示,默认为true，即显示
    public String m_SumH_Caption = ""; //合计的标示符
    public String m_SumX_Caption = ""; //小计的标示符
    public int m_Per_Page_Count; //存放每页的行数
    public String m_FormatFileName = ""; //格式文件的名称

    public String m_Function_Type = ""; //在system_define中定义的函数类型
    public boolean m_Dsn_From_Func; //数据源取自报表还是函数，为true取自函数
    public String m_Dsn_From = ""; //默认时的数据源名

    public String mWebReportWebDirectory = ""; //Web报表的对应网站路径
    public String mWebReportDiskDirectory = ""; //Web报表的磁盘存放路径
    public String mOutWebReportURL = ""; //Web报表的输出
    public String mExcelOutWebReportURL = ""; //Web报表的Excel格式输出
    public String mFormatFile = "";
    public String mConfigFilePath = ""; //system_define.vts 的目录带斜杠

    boolean mReplaceCellValue_Numm; //在替换CellValue中是否有空值出现

    public boolean mNeedLog; //判断是否需要纪录日志
    public boolean mNeedExcel; //判断是否需要转换为Excel
    public Date mTimeOutDate; //超时日期

    public int mCurPages; //在拼接报表时的页码
    public String mAppPath;

    public JRptList()
    {
        initialize();
    }

    //设置路径 add by wangyc 2005.1.24
    public void setup(PageContext pageContext, String reportName)
    {
        setup(
                pageContext.getServletContext(),
                (HttpServletRequest) pageContext.getRequest(),
                reportName);
    }

    public void setup(ServletContext sCtx, HttpServletRequest req,
            String reportName)
    {
        JRptConfig.setup(sCtx, req);
        setupPath(reportName);
    }

    //测试用
    public void setup(String configPath, String templatePath, String generatedPath
            , String urlPrefix, String appPath, String reportName)
    {
        this.mConfigFilePath = configPath;
        this.mWebReportDiskDirectory = generatedPath;
        this.mWebReportWebDirectory = urlPrefix;
        this.m_FormatFileName = templatePath + "/" + reportName + ".vts";
        this.mAppPath = appPath + "/";
    }

    public void setupPath(String reportName)
    {
        this.mConfigFilePath = JRptConfig.getConfigPath() + "/";
        this.mWebReportDiskDirectory = JRptConfig.getGeneratedPath() + "/";
        this.mWebReportWebDirectory = JRptConfig.getUrlPrefix() + "/";
        this.m_FormatFileName = JRptConfig.getTemplatePath() + "/" + reportName + ".vts";
        this.mAppPath = JRptConfig.getAppPath() + "/";
        File fsPath = new File(mWebReportDiskDirectory);
        if (!fsPath.exists())
        {
            //MkDir mWebReportDiskDirectory
            fsPath.mkdir();
        }

    }

    /**
     * 获取文件路径
     * @return 返回定义路径
     */
    public String getConfigFilePath()
    {
        return mConfigFilePath;
    }

    private void initialize()
    {
        m_ErrString = "";
        if (m_SumX_Caption.equalsIgnoreCase(""))
        {
            m_SumX_Caption = "小计：  ";
        }

        if (m_SumH_Caption.equalsIgnoreCase(""))
        {
            m_SumH_Caption = "合计：  ";
        }

        m_NeedMerge = false;

        //ReDim m_Visible(ConCols)        //初始化可见列
        m_Visible = new Vector();
        for (int i = 0; i <= ConCols; i++)
        {
            m_Visible.add(new Boolean(false));
        }
        m_Vals = new Vector(); //初始化参数数组

        this.AddVar("", "");

        m_Row_Col_Text_Type = "01";
        m_Need_Preview = false; //是否需要预览，如果不需要预览，则最后不会执行拷贝操作
        m_Need_Display_Zero = false; //不显示0值

        //Set m_FV = New F1BookView        //初始化一个BookView

        m_FV = new BookModelImpl();

        m_NeedProcess = false; //设置进程条需要显示
        m_StartDisplayRow = 1; //设置默认的显示报表的行列位置
        m_StartDisplayCol = 1; //设置默认的显示报表的行列位置
        m_Function_Type = "0";
    }

    /**
     * 增加一个参数
     * @param c_Name 参数名称
     * @param c_Value 参数值
     */
    public void AddVar(String c_Name, String c_Value)
    {
        StringBuffer sf = new StringBuffer();
        sf.append("?");
        sf.append(c_Name.trim());
        sf.append("?");

        String[] s = new String[2];

        s[0] = sf.toString();
        //如果传入的值为null，则置空
        if (c_Value == null)
        {
            c_Value = "";
        }
        s[1] = c_Value;

        m_Vals.add(s);
    }

    /**
     * 转换列代码
     * @param cDestCol String 转换列
     * @param cStartRow String 开始行
     * @param cChangeSQL String 转换的SQL
     * @param cType String
     * @param cMethodName String
     * @throws Exception
     */
    public void Change_Col_Code(String cDestCol, String cStartRow, String cChangeSQL, String cType
            , String cMethodName)
            throws Exception
    {
//        System.out.println("=====开始列转换======");
        int i, iMax;
        String tSQL = "", tStr = "";
        int t_Sheet;
//        int t_Row_Count;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        //GetDefaultConnection
        iMax = m_FV.getLastRow() + 1;
        if (cStartRow.equalsIgnoreCase(""))
        {
            cStartRow = "1";
        }
        //动态调用java方法
        if (!(cType.equalsIgnoreCase("") || cMethodName.equalsIgnoreCase("")))
        {
            Class c = Class.forName(cChangeSQL);
            //在这里，cChangeSQL存放要调用的方法的类名，cMethodName存放要调用的代码转换方法名，cType存放转换类型
            Object o = c.newInstance();
            Class[] parameterC = new Class[2];
            parameterC[0] = Class.forName("java.lang.String");
            parameterC[1] = Class.forName("java.lang.String");

            Object[] parameterO = new Object[2];
            parameterO[0] = cType;
            Method m = c.getMethod(cMethodName, parameterC);
            for (i = Integer.parseInt(cStartRow); i <= iMax; i++)
            {
                m_Data_Row = i;
                parameterO[1] = m_FV.getText(i - 1, Integer.parseInt(cDestCol) - 1);

                if (parameterO[1].equals(""))
                {
                    tStr = "";
                }
                else
                {
                    //只有当在替换行列变量过程中没有出现空值时才执行替换代码
                    tStr = (String) m.invoke(o, parameterO);
                }
                m_FV.setText(i - 1, Integer.parseInt(cDestCol) - 1, tStr);
            }
        }
        else
        {
            //执行SQL语句
            for (i = Integer.parseInt(cStartRow); i <= iMax; i++)
            {
                m_Data_Row = i;
                tSQL = ReplaceValSQL(cChangeSQL, m_Vals);

                if (mReplaceCellValue_Numm)
                {
                    tStr = "";
                }
                else
                {
                    //只有当在替换行列变量过程中没有出现空值时才执行代码替换
                    tStr = JRptUtility.GetOneValueBySQL(tSQL);
                }

                m_FV.setText(i - 1, Integer.parseInt(cDestCol) - 1, tStr);
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 转换行代码
     * @param cDestRow 目标行
     * @param cStartCol 开始的列
     * @param cChangeSQL 转化的SQL
     * @throws Exception 异常
     */
    public void Change_Row_Code(String cDestRow, String cStartCol, String cChangeSQL)
            throws Exception
    {
        int i, iMax;
        String tSQL = "", tStr = "";
        int t_Sheet;
//        int t_Row_Count;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        //GetDefaultConnection
        iMax = m_FV.getLastCol() + 1;
        if (cStartCol.equalsIgnoreCase(""))
        {
            cStartCol = "1";
        }
        int iStartCol = Integer.parseInt(cStartCol);

        for (i = iStartCol; i <= iMax; i++)
        {
            m_Data_Col = i;
            tSQL = ReplaceValSQL(cChangeSQL, m_Vals);
            //DoEvents
            if (mReplaceCellValue_Numm)
            {
                tStr = "";
            }
            else
            {
                //只有当在替换行列变量过程中没有出现空值时才执行代码替换
                tStr = JRptUtility.GetOneValueBySQL(tSQL);
            }
            m_FV.setText(Integer.parseInt(cDestRow) - 1, i - 1, tStr);
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 校验在某个区域内是否有数据，如果没有数据自动收缩
     * @param cX x坐标
     * @param cY y坐标
     * @param cEX x坐标
     * @param cEY y坐标
     * @return 返回true or false
     * @throws Exception
     */
    private boolean CheckZero(int cX, int cY, int cEX, int cEY)
            throws Exception
    {
        int t_Sheet;
        int i, j;
        String tData = "";
        boolean bCheckZero = false;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        for (i = cX; i <= cEX; i++)
        {
            for (j = cY; j <= cEY; j++)
            {
                tData = m_FV.getText(i - 1, j - 1).trim();
                if (!tData.equalsIgnoreCase(""))
                {
                    if (JRptUtility.IsNumeric(tData))
                    {
                        if (!tData.trim().equalsIgnoreCase("0"))
                        {
                            return bCheckZero;
                        }
                    }
                }
            }
        }
        bCheckZero = true;
        m_FV.setSheet(t_Sheet);
        return bCheckZero;
    }

    /**
     * 对小计和合计进行解释
     * @throws Exception
     */
    public void Compute_Sum()
            throws Exception
    {

        int t_Cur_Row;
        int t_Sheet;
//        int t_Row_Count;
        String t_Str = "";
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);
        t_Cur_Row = m_Cur_Format_Row;
//        t_Row_Count = m_FV.getLastRow() + 1;

        //With m_FV
        //解释行函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().equalsIgnoreCase("ROW_FUNCTION"))
                {
                    break;
                }

                //解释行函数
                Put_Row_FunctionData(m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }

        //解释行列函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {

                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().equalsIgnoreCase("ROW_COL_FUNCTION"))
                {
                    break;
                }
                //解释行函数
                Put_Row_Col_FunctionData(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                        , 2), m_FV.getText(t_Cur_Row - 1, 3));
            }
            t_Cur_Row += 1;
        }

        //解释列隐藏函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim()
                        .equalsIgnoreCase("HIDE_COL"))
                {
                    break;
                }
                Hide_Col(m_FV.getText(t_Cur_Row - 1, 1));
                //解释行函数
                //Put_Row_Col_FunctionData .TextRC(t_Cur_Row-1, 2), .TextRC(t_Cur_Row-1, 3), .TextRC(t_Cur_Row-1, 4)
            }
            t_Cur_Row += 1;
        }
        //解释行隐藏函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().equalsIgnoreCase("HIDE_ROW"))
                {
                    break;
                }
                //解释行函数
                Hide_Row(m_FV.getText(t_Cur_Row - 1, 1));
                //Put_Row_Col_FunctionData .TextRC(t_Cur_Row, 2), .TextRC(t_Cur_Row, 3), .TextRC(t_Cur_Row, 4)
            }
            t_Cur_Row += 1;
        }

        //转换行代码
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().equalsIgnoreCase("CHANGE_ROW_CODE"))
                {
                    break;
                }
                //转换行代码
                Change_Row_Code(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 3)
                        , m_FV.getText(t_Cur_Row - 1, 2));
            }
            t_Cur_Row += 1;
        }

        //转换列代码
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().equalsIgnoreCase("CHANGE_COL_CODE"))
                {
                    break;
                }

                //转换列代码
                Change_Col_Code(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 3)
                        , m_FV.getText(t_Cur_Row - 1, 2), m_FV.getText(t_Cur_Row - 1, 4)
                        , m_FV.getText(t_Cur_Row - 1, 5));
            }
            t_Cur_Row += 1;
        }

        /*********************************************************************
          '将合并操作放到最后进行
           '    ''''''''''''''''''合并行单元格
           '    Do While True
           '        t_Str = Trim(UCase(.TextRC(t_Cur_Row, 1)))
           '        If Left(t_Str, 1) = "#" Then
           '        Else
         '            If UCase(Trim(.TextRC(t_Cur_Row, 1))) <> "UNITE_ROW" Then Exit Do
           '            Unite_Row .TextRC(t_Cur_Row, 2) '合并行单元格
           '        End If
           '        t_Cur_Row += 1
           '    Loop
           '    ''''''''''''''''''合并列单元格
           '    Do While True
           '        t_Str = Trim(UCase(.TextRC(t_Cur_Row, 1)))
           '        If Left(t_Str, 1) = "#" Then
           '        Else
         '            If UCase(Trim(.TextRC(t_Cur_Row, 1))) <> "UNITE_COL" Then Exit Do
           '            Unite_Col .TextRC(t_Cur_Row, 2) '合并行列元格
           '        End If
           '        t_Cur_Row += 1
           '    Loop
         **************************************************************/

        //解释小计函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().
                        equalsIgnoreCase("SUMX"))
                {
                    break;
                }
                //解释小计函数
                Put_Sum_X(t_Cur_Row, m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }
        //解释合计函数
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            //#号开头表示此行是注释
            if (t_Str.startsWith("#"))
            {
            }
            else
            {

                if (!m_FV.getText(t_Cur_Row - 1, 0).trim().
                        equalsIgnoreCase("SUMH"))
                {
                    break;
                }
                //解释合计函数
                Put_Sum_H(t_Cur_Row, m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }

        m_Cur_Format_Row = t_Cur_Row;

        //删除不可见的列
        //Delete_Invisible_Cols
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 将拷贝页尾
     * @param c_Type 类型
     * @param c_Row 行
     * @param c_Col 列
     * @throws Exception
     */
    public void Copy_Page_End(String c_Type, int c_Row, int c_Col)
            throws Exception
    {
        int t_Sheet;
        String t_Str = "";
        CRage t_Rage = new CRage();
        CRage t_Rage1 = new CRage();

        t_Sheet = m_FV.getSheet();
        if (!c_Type.trim().equalsIgnoreCase(""))
        {
            //从R_Head这个Sheet拷贝报表头到报表数据区的制定区域
            m_FV.setSheet(P_End);

            t_Rage.m_Left = 1;
            t_Rage.m_Top = 1;
            t_Rage.m_Buttom = m_FV.getLastRow() + 1;
            t_Rage.m_Right = m_FV.getLastCol() + 1;

            t_Rage1.m_Left = c_Col;
            t_Rage1.m_Top = c_Row;

            Copy_Sheet_Data(t_Rage, m_FV, P_End, t_Rage1, m_FV, R_Report, 0);

            t_Rage1.m_Buttom = t_Rage1.m_Top + t_Rage.m_Buttom - 1;
            t_Rage1.m_Right = t_Rage1.m_Right + t_Rage.m_Right - 1;

            DEBUG_OUT("Copy_Page_End");
            Set_Sys_Val_In_Report(t_Rage1);
        }
        else
        {
            //从格式文件到报表头拷贝数据
            m_FV.setSheet(R_Define);
            //得到报表头的区域
            t_Str = m_FV.getText(Row_Page_End - 1, 1);

            if (!t_Str.trim().equalsIgnoreCase(""))
            {
                t_Rage.m_Left = Integer.parseInt(JRptUtility.Get_Str(t_Str, 2, ":"));
                t_Rage.m_Top = Integer.parseInt(JRptUtility.Get_Str(t_Str, 1, ":"));
                t_Rage.m_Right = Integer.parseInt(JRptUtility.Get_Str(t_Str, 4, ":"));
                t_Rage.m_Buttom = Integer.parseInt(JRptUtility.Get_Str(t_Str, 3, ":"));

                t_Rage1.m_Left = 1;
                t_Rage1.m_Top = 1;

                Copy_Sheet_Data(t_Rage, m_FV, R_Format, t_Rage1, m_FV, P_End, 0);

            }
        }

        m_FV.setSheet(t_Sheet);
        t_Rage = null;
        t_Rage1 = null;

    }

    /**
     * 将拷贝页头
     * @param c_Type 类型
     * @param c_Row 行
     * @param c_Col 列
     * @throws Exception
     */
    public void Copy_Page_Head(String c_Type, int c_Row, int c_Col)
            throws Exception
    {

        int t_Sheet;
        String t_Str = "";
        CRage t_Rage = new CRage();
        CRage t_Rage1 = new CRage();
        t_Sheet = m_FV.getSheet();

        if (!c_Type.trim().equalsIgnoreCase(""))
        {
            //从P_Head这个Sheet拷贝报表头到报表数据区的制定区域

            m_FV.setSheet(P_Head);
            t_Rage.m_Left = 1;
            t_Rage.m_Top = 1;
            t_Rage.m_Buttom = m_FV.getLastRow() + 1;
            t_Rage.m_Right = m_FV.getLastCol() + 1;
            t_Rage1.m_Left = c_Col;
            t_Rage1.m_Top = c_Row;

            Copy_Sheet_Data(t_Rage, m_FV, P_Head, t_Rage1, m_FV, R_Report, 0);

            t_Rage1.m_Buttom = t_Rage1.m_Top + t_Rage.m_Buttom - 1;
            t_Rage1.m_Right = t_Rage1.m_Right + t_Rage.m_Right - 1;
            DEBUG_OUT("Copy_Page_Head");
            Set_Sys_Val_In_Report(t_Rage1);
        }
        else
        {
            //从格式文件到报表头拷贝数据
            m_FV.setSheet(R_Define);
            //得到页头的区域
            t_Str = m_FV.getText(Row_Page_Head - 1, 1);

            if (!t_Str.trim().equalsIgnoreCase(""))
            {
                t_Rage.m_Left = Integer.parseInt(JRptUtility.Get_Str(t_Str, 2, ":"));
                t_Rage.m_Top = Integer.parseInt(JRptUtility.Get_Str(t_Str, 1, ":"));
                t_Rage.m_Right = Integer.parseInt(JRptUtility.Get_Str(t_Str, 4, ":"));
                t_Rage.m_Buttom = Integer.parseInt(JRptUtility.Get_Str(t_Str, 3, ":"));
                t_Rage1.m_Left = 1;
                t_Rage1.m_Top = 1;
                Copy_Sheet_Data(t_Rage, m_FV, R_Format, t_Rage1, m_FV, P_Head, 0);
            }
        }
        m_FV.setSheet(t_Sheet);
        t_Rage = null;
        t_Rage1 = null;
    }

    /**
     * 将拷贝报表尾
     * @param c_Type 类型
     * @param c_Row 行
     * @param c_Col 列
     * @throws Exception
     */
    public void Copy_Report_End(String c_Type, int c_Row, int c_Col)
            throws Exception
    {
        int t_Sheet;
        String t_Str = "";
        CRage t_Rage = new CRage();
        CRage t_Rage1 = new CRage();
        t_Sheet = m_FV.getSheet();

        if (!c_Type.trim().equalsIgnoreCase(""))
        {
            //从R_End这个Sheet拷贝报表头到报表数据区的制定区域
            m_FV.setSheet(R_End);
            t_Rage.m_Left = 1;
            t_Rage.m_Top = 1;
            t_Rage.m_Buttom = m_FV.getLastRow() + 1;
            t_Rage.m_Right = m_FV.getLastCol() + 1;
            t_Rage1.m_Left = c_Col;
            t_Rage1.m_Top = c_Row;

            Copy_Sheet_Data(t_Rage, m_FV, R_End, t_Rage1, m_FV, R_Report, 0);

            t_Rage1.m_Buttom = t_Rage1.m_Top + t_Rage.m_Buttom - 1;
            t_Rage1.m_Right = t_Rage1.m_Right + t_Rage.m_Right - 1;
            DEBUG_OUT("Copy_Report_End");
            Set_Sys_Val_In_Report(t_Rage1);
        }
        else
        {
            //从格式文件到报表头拷贝数据
            m_FV.setSheet(R_Define);
            t_Str = m_FV.getText(Row_Report_End - 1, 1);

            //得到报表尾的区域
            if (!t_Str.trim().equalsIgnoreCase(""))
            {
                t_Rage.m_Left = Integer.parseInt(JRptUtility.Get_Str(t_Str, 2, ":"));
                t_Rage.m_Top = Integer.parseInt(JRptUtility.Get_Str(t_Str, 1, ":"));
                t_Rage.m_Right = Integer.parseInt(JRptUtility.Get_Str(t_Str, 4, ":"));
                t_Rage.m_Buttom = Integer.parseInt(JRptUtility.Get_Str(t_Str, 3, ":"));
                t_Rage1.m_Left = 1;
                t_Rage1.m_Top = 1;

                Copy_Sheet_Data(t_Rage, m_FV, R_Format, t_Rage1, m_FV, R_End, 0);

            }
        }
        m_FV.setSheet(t_Sheet);
        t_Rage = null;
        t_Rage1 = null;
    }

    /**
     * 拷贝报表头
     * @param c_Type 类型
     * @param c_Row 行
     * @param c_Col 列
     * @throws Exception
     */
    public void Copy_Report_Head(String c_Type, int c_Row, int c_Col)
            throws Exception
    {
        int t_Sheet;
        String t_Str = "";
        CRage t_Rage = new CRage();
        CRage t_Rage1 = new CRage();
        t_Sheet = m_FV.getSheet();

        if (!c_Type.trim().equalsIgnoreCase(""))
        {
            //从R_Head这个Sheet拷贝报表头到报表数据区的制定区域
            m_FV.setSheet(R_Head);
            t_Rage.m_Left = 1;
            t_Rage.m_Top = 1;
            t_Rage.m_Buttom = m_FV.getLastRow() + 1;
            t_Rage.m_Right = m_FV.getLastCol() + 1;
            t_Rage1.m_Left = c_Col;
            t_Rage1.m_Top = c_Row;

            Copy_Sheet_Data(t_Rage, m_FV, R_Head, t_Rage1, m_FV, R_Report, 0);

            t_Rage1.m_Buttom = t_Rage1.m_Top + t_Rage.m_Buttom - 1;
            t_Rage1.m_Right = t_Rage1.m_Right + t_Rage.m_Right - 1;
            DEBUG_OUT("Copy_Report_Head");
            Set_Sys_Val_In_Report(t_Rage1);
        }
        else
        {
            //从格式文件到报表头拷贝数据
            m_FV.setSheet(R_Define);
            t_Str = m_FV.getText(Row_Report_Head - 1, 1);
            //得到报表头的区域
            if (!t_Str.trim().equalsIgnoreCase(""))
            {
                t_Rage.m_Left = Integer.parseInt(JRptUtility.Get_Str(t_Str, 2, ":"));
                t_Rage.m_Top = Integer.parseInt(JRptUtility.Get_Str(t_Str, 1, ":"));
                t_Rage.m_Right = Integer.parseInt(JRptUtility.Get_Str(t_Str, 4, ":"));
                t_Rage.m_Buttom = Integer.parseInt(JRptUtility.Get_Str(t_Str, 3, ":"));
                t_Rage1.m_Left = 1;
                t_Rage1.m_Top = 1;
                Copy_Sheet_Data(t_Rage, m_FV, R_Format, t_Rage1, m_FV, R_Head, 0);
            }
        }
        m_FV.setSheet(t_Sheet);
        t_Rage = null;
        t_Rage1 = null;
    }

    /**
     * 将指定区域的数据从源Sheet拷贝到目的Sheet中
     * @param c_SurRage 指定区域
     * @param c_SurFV 源Sheet
     * @param c_SurFVIndex 源Sheet的编号
     * @param c_DestRage 目标区域
     * @param c_DestFV 目标Sheet
     * @param c_DestIndex 目标Sheet的编号
     * @param c_NeedRowHeight 保持行高度
     * @throws Exception
     */
    public void Copy_Sheet_Data(CRage c_SurRage, BookModelImpl c_SurFV, int c_SurFVIndex
            , CRage c_DestRage, BookModelImpl c_DestFV, int c_DestIndex, int c_NeedRowHeight)
            throws Exception
    {
        int t_DstR2, t_DstC2;
        int tRowHeight, tColWidth;
        int tSheet1, tSheet2;
        tSheet1 = c_SurFV.getSheet();
        tSheet2 = c_DestFV.getSheet();

        t_DstR2 = c_DestRage.m_Top + c_SurRage.m_Buttom - c_SurRage.m_Top;
        t_DstC2 = c_DestRage.m_Left + c_SurRage.m_Right - c_SurRage.m_Left;

        c_SurFV.setSheet(c_SurFVIndex);
        c_DestFV.setSheet(c_DestIndex);

//        System.out.println("源Sheet:" + c_SurFVIndex);
//        System.out.println("目标Sheet:" + c_DestIndex);
//        System.out.println("源范围:" + (c_SurRage.m_Top - 1) + "," +
//                (c_SurRage.m_Left - 1)
//                + "," + (c_SurRage.m_Buttom - 1) + "," +
//                (c_SurRage.m_Right - 1));
//        System.out.println("目标范围:" + (c_DestRage.m_Top - 1) + "," +
//                (c_DestRage.m_Left - 1)
//                + "," + (c_DestRage.m_Buttom - 1) + "," +
//                (c_DestRage.m_Right - 1));

        if (c_SurRage.m_Buttom > 0 && c_SurRage.m_Right > 0)
        {
            c_DestFV.copyRange(c_DestIndex, c_DestRage.m_Top - 1, c_DestRage.m_Left - 1
                    , t_DstR2 - 1, t_DstC2 - 1, c_SurFV, c_SurFVIndex, c_SurRage.m_Top - 1
                    , c_SurRage.m_Left - 1, c_SurRage.m_Buttom - 1, c_SurRage.m_Right - 1);

            //Web上不能用EditCopy

            c_SurFV.setSheet(c_SurFVIndex);

            //.SetSelection c_SurRage.m_Top, c_SurRage.m_Left, c_SurRage.m_Buttom, c_SurRage.m_Right
            //.SetSelection c_SurRage.m_Top, -1, c_SurRage.m_Buttom, -1
            //.EditCopy

            c_DestFV.setSheet(c_DestIndex);
            //.SetSelection c_DestRage.m_Top, -1, c_DestRage.m_Buttom, -1
            //.SetActiveCell c_DestRage.m_Top, c_DestRage.m_Left
            //.EditPaste
            if (c_NeedRowHeight == 1)
            {
                c_DestFV.setRowHeight(c_DestRage.m_Top - 1, t_DstR2 - 1, m_DataRowHeight - 1, false, false);
            }

            int i, j;
            for (i = c_SurRage.m_Left; i <= c_SurRage.m_Right; i++)
            {
                c_SurFV.setSheet(c_SurFVIndex);
                tColWidth = c_SurFV.getColWidth(i);
                j = c_DestRage.m_Left + i - c_SurRage.m_Left;
                c_DestFV.setSheet(c_DestIndex);
                c_DestFV.setColWidth(j - 1, j - 1, tColWidth, false);
            }

            for (i = c_SurRage.m_Top; i <= c_SurRage.m_Buttom; i++)
            {
                c_SurFV.setSheet(c_SurFVIndex);
                tRowHeight = c_SurFV.getRowHeight(i - 1);
                j = c_DestRage.m_Top + i - c_SurRage.m_Top;
                c_DestFV.setSheet(c_DestIndex);
                c_DestFV.setRowHeight(j - 1, j - 1, tRowHeight, false, false);
            }
        }
        c_SurFV.setSheet(c_SurFVIndex);

        c_DestFV.setSheet(c_DestIndex);

        c_SurFV.setSheet(tSheet1);
        c_DestFV.setSheet(tSheet2);

    }

    /**
     * 将生成的报表放到m_DestFV这个F1Book中
     * @throws Exception
     */
    public void Copy_To_F1Book()
            throws Exception
    {

        int t_Row;
        int t_Sheet;
        int i;

        //拷贝所有的数据
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Report);

        if (m_StartDisplayRow > 0 || m_StartDisplayCol > 0)
        {
            m_DestFV.setSelection(m_StartDisplayRow - 1, m_StartDisplayCol - 1
                    , m_StartDisplayRow - 1, m_StartDisplayCol - 1);
            m_FV.setSelection( -1, 0, -1, m_FV.getLastCol());

//            m_FV.editCopy();

            m_DestFV.setActiveCell(m_StartDisplayRow - 1, m_StartDisplayCol - 1);

//            m_DestFV.editPaste();
            m_DestFV.copyRange( -1, 0, -1, m_FV.getLastCol(), m_FV, m_StartDisplayRow - 1
                    , m_StartDisplayCol - 1, -1, -1);
//            m_DestFV.copyAll(m_FV);

            for (i = 1; i <= m_FV.getLastCol() + 1; i++)
            {
                m_DestFV.setColWidth(i + m_StartDisplayCol - 2, m_FV.getColWidth(i - 1));
            }

            for (i = 1; i <= m_FV.getLastRow() + 1; i++)
            {
                m_DestFV.setRowHeight(i + m_StartDisplayRow - 2, m_FV.getRowHeight(i - 1));
            }
            //拷贝冻结列
            m_DestFV.setFixedCol(mFCol - 1);
            m_DestFV.setFixedCols(mFCols + m_StartDisplayCol - 1);
            m_DestFV.setFixedRow(mFRow - 1);
            m_DestFV.setFixedRows(mFRows + m_StartDisplayRow - 1);
            //End拷贝冻结列
        }
        else
        {
            m_FV.setSelection( -1, -1, -1, -1);
//            m_FV.editCopy();
            m_DestFV.setSelection( -1, -1, -1, -1);
//            m_DestFV.editPaste();
            m_DestFV.copyAll(m_FV);
            //拷贝冻结列
            m_DestFV.setFixedCol(mFCol - 1);
            m_DestFV.setFixedCols(mFCols);
            m_DestFV.setFixedRow(mFRow - 1);
            m_DestFV.setFixedRows(mFRows);
            //End拷贝冻结列
        }

        //End拷贝所有的数据
        m_DestFV.setSelection(0, 0, 0, 0);
        //拷贝其他的格式

        //拷贝分页标志
        t_Row = 1;
        while (true)
        {
            t_Row = m_FV.getNextRowPageBreak(t_Row - 1); //此处-1
            if (t_Row <= 0)
            {
                break;
            }
            m_DestFV.addRowPageBreak(t_Row - 1); //此处-1
            t_Row += 1;
        }
        //End拷贝分页标志

        //End拷贝其他的格式
        m_FV.setSheet(t_Sheet);

    }

    /**
     * 根据当前的输入变量和报表模版，得到一个报表日至名
     * @return 一个报表日志名
     */
    public String CreateReportVarName()
    {
        String t_Return;
        int i_Max;

        t_Return = JRptUtility.Get_OnlyFileNameEx(m_FormatFileName.trim());

        i_Max = m_Vals.size() - 1;
        for (int i = 1; i <= i_Max; i++)
        {
            String sArr[] = (String[]) m_Vals.get(i);
            t_Return += sArr[0].trim() + sArr[1].trim();
        }

        t_Return = t_Return.replaceAll("\\?", "");
        t_Return = t_Return.replaceAll(" ", "");
        t_Return = t_Return.replaceAll("'", "");
        t_Return = t_Return.replaceAll("\"\"", "");
        t_Return = t_Return.replaceAll("-", "");
        t_Return = t_Return.replaceAll("_", "");
        t_Return = t_Return.replaceAll(":", "");
        t_Return = t_Return.replaceAll("\\/", "");

        return t_Return;
    }

    /**
     * 从数据区开始读区数据
     * @throws Exception
     */
    public void DealData()
            throws Exception
    {
        int t_Cur_Row;
//        int t_Col;
//        String t_Sql = "", t_DSN = "", t_Key = "", t_Str = "";
//        int t_Row;
//        int t_FldCount;
//        int t_Sheet;
        int t_Row_Count;

//        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);
        t_Cur_Row = m_Cur_Format_Row;
        t_Row_Count = m_FV.getLastRow() + 1;

        while (true)
        {
            //数据读取
            Read_Data();
            //小计、合计部分
            Compute_Sum();
            if (m_Cur_Format_Row > t_Row_Count)
            {
                break;
            }

            //m_FV.WriteEx "c:\tt.vts", F1FileFormulaOne6

            if (t_Cur_Row == m_Cur_Format_Row)
            {
                m_Cur_Format_Row += 1;
            }

            t_Cur_Row = m_Cur_Format_Row;
        }
    }

    /**
     * 根据m_Visible(),删除不可见的列
     * @throws Exception
     */
    public void Delete_Invisible_Cols()
            throws Exception
    {
        int i_Max, i, j;
        int t_Sheet;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        //i_Max = UBound(m_Visible);
        i_Max = m_Visible.size() - 1;
        j = 0;
        for (i = 1; i <= i_Max; i++)
        {
            if ((m_Visible.get(i)).equals(Boolean.FALSE))
            {
                m_FV.deleteRange(0, i - j - 1, 0, i - j - 1,
                        Constants.eShiftColumns);
                j += 1;
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 执行函数，其中c_FunName是函数名，c_FunParameter是函数的参数
     * RowSum("2,3")表示将2，3列求和，放到第4列
     * @param c_FunName 函数名
     * @param c_FunParameter 函数的参数
     * @param c_Row_Number  行号
     * @return 字符串
     * @throws Exception
     */
    public String Execute_Row_Function(String c_FunName, String c_FunParameter, int c_Row_Number)
            throws Exception
    {
        String t_FunName;
        String[] t_P;
//        int t_PCount;
        String t_Return = "", t_FunParameter;
        t_P = c_FunParameter.split(",");
        t_FunName = c_FunName;

        if (t_FunName.equalsIgnoreCase("ROWSUM"))
        {
            //得到实际的参数值
            t_FunParameter = Get_Row_Text(t_P, c_Row_Number);
            t_P = t_FunParameter.split(",");
            t_Return = Get_Sum_By_Name(t_P);
        }
        else if (t_FunName.equalsIgnoreCase("ROWMUL"))
        {
            t_FunParameter = Get_Row_Text(t_P, c_Row_Number);
            t_P = t_FunParameter.split(",");
            t_Return = Get_Mul_By_Name(t_P);
        }
        else if (t_FunName.equalsIgnoreCase("FORMULA"))
        {
            t_Return = Get_Formula(t_P, c_Row_Number);
        }
        else if (t_FunName.equalsIgnoreCase("EXECSQL"))
        {
            t_Return = Get_ExecSql(c_FunParameter, c_Row_Number);
        }
        else
        {
            t_Return = "NO_FUNCTION" + t_FunName;
        }

        return t_Return;
    }

    /**
     * 格式化数据区
     * @throws Exception
     */
    public void Format_Cell()
            throws Exception
    {
        int i, i_Max, t_Sheet;
//        boolean pWordwrap;
//        int pVertial;
//        int pHorizontal, pOrientation;
//        int pLeft;
//        int pRight, pTop, pBottom, pShade;
//        int pcrLeft;
//        int pcrRight, pcrTop, pcrBottom;
        int t_RowHeight, t_ColWidth, t_RowCount;
        CellFormat tCF;

        int j, j_Max;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Format);

        mFCol = m_FV.getFixedCol() + 1;
        mFCols = m_FV.getFixedCols();
        mFRow = m_FV.getFixedRow() + 1;
        mFRows = m_FV.getFixedRows();

        //i_Max = m_FV.LastCol
        t_RowHeight = m_FV.getRowHeight(m_DataFormatRow - 1);
        m_DataRowHeight = t_RowHeight;
        m_FV.setSheet(R_Data);
        t_RowCount = m_FV.getLastRow() + 1;
        i_Max = m_FV.getLastCol() + 1; //sangood +1
        j_Max = t_RowCount;
        if (t_RowCount <= 0)
        {
            t_RowCount = 1;
        }

        for (i = 1; i <= i_Max; i++)
        {
            m_FV.setSheet(R_Format);
            m_FV.setRow(m_DataFormatRow - 1);
            m_FV.setCol(i - 1);
            m_FV.setSelection(m_DataFormatRow - 1, i - 1, m_DataFormatRow - 1, i - 1);
            tCF = m_FV.getCellFormat();

            //tCF.setTopBorderColor(255);
            tCF.setTopBorder(tCF.getTopBorder());

            //m_FV.GetAlignment pHorizontal, pWordwrap, pVertial, pOrientation
            //m_FV.GetFont pname, psize, pbold, pitalic, punderline, pstrikeout, pcrcolor, poutline, pshadow
            //m_FV.GetBorder pLeft, pRight, pTop, pBottom, pShade, pcrLeft, pcrRight, pcrTop, pcrBottom
            //Debug.Print tCF.MergeCells
            m_DataRowHeight = t_RowHeight;
            t_ColWidth = m_FV.getColWidth(i - 1);
            //tCF.setWordWrap(true);


            m_FV.setSheet(R_Data);

            for (j = 1; j <= j_Max; j++)
            {
                m_FV.setSelection(j - 1, i - 1, j - 1, i - 1);
                //m_FV.SetAlignment pHorizontal, pWordwrap, pVertial, pOrientation
                //m_FV.SetBorder -1, pLeft, pRight, pTop, pBottom, pShade, -1, pcrLeft, pcrRight, pcrTop, pcrBottom
                m_FV.setCellFormat(tCF);
            }

            m_FV.setRowHeight(0, t_RowCount - 1, t_RowHeight, false, false);
            m_FV.setColWidth(i - 1, i - 1, t_ColWidth, false);

            m_FV.setSheet(R_Report);
            m_FV.setColWidth(i - 1, i - 1, t_ColWidth, false);
        }

        m_FV.setSheet(R_Report);
        //设置固定行列
        m_FV.setFixedCol(mFCol - 1);
        m_FV.setFixedCols(mFCols);
        m_FV.setFixedRow(mFRow - 1);
        m_FV.setFixedRows(mFRows);

        UniteData();

        m_FV.setSheet(t_Sheet);
    }

    /**
     * 根据c_Text的值，返回当前行
     * @param c_Text 值
     * @param c_Row_Number 行号
     * @return 当前行号
     */
    public String Get_Cur_Row(String c_Text, int c_Row_Number)
    {
        String t_Return = "";
        if (c_Text.trim().equalsIgnoreCase("$"))
        {
            t_Return = String.valueOf(c_Row_Number).trim();
        }
        else
        {
            t_Return = c_Text.trim();
        }
        return t_Return;
    }

    /**
     * 本函数的功能是根据参数c_FunParameter生成一个字符串
     * @param c_FunParameter 参数
     * @param c_Row 行号
     * @return 字符串
     * @throws Exception
     */
    private String Get_ExecSql(String c_FunParameter, int c_Row)
            throws Exception
    {
        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();
//        String sSQL = "";

        String t_Str;
        String t_Find;
        String t_Replace;
//        String t_DSN;
//        BookModelImpl  t_F1v;
        String t_P[];
        int i = 1;

        t_P = c_FunParameter.split(";"); //(, ";", -1, vbBinaryCompare)

        t_Str = t_P[0]; //取得sql语句

        //如果还有变量，取其名
        t_Find = JRptUtility.Get_Str(t_Str, i * 2, "$");
        //替换之
        t_Replace = "?" + String.valueOf(c_Row) + ":" + t_Find + "?";
        //把形如$6$的变量替换为形如?2:6?的变量

        //t_Str = t_Str.replaceAll( "$" + t_Find + "$", t_Replace );
        t_Str = StrTool.replace(t_Str, "$" + t_Find + "$", t_Replace);
        while (!t_Find.equalsIgnoreCase(""))
        {
            //替换固定变量，不定变量，系统变量和？row:col？型的变量
            t_Str = ReplaceValSQL(t_Str, m_Vals);
            i += 1; //继续查找变量
            t_Find = JRptUtility.Get_Str(t_Str, i * 2, "$");
            //替换之
            t_Replace = "?" + String.valueOf(c_Row) + ":" + t_Find + "?";
            //t_Str = t_Str.replaceAll( "?" + t_Find + "?", t_Replace );
            t_Str = StrTool.replace(t_Str, "?" + t_Find + "?", t_Replace);
        }

        tSSRS = tExeSQL.execSQL(t_Str);
        t_Str = "";

        if (tSSRS.MaxRow < 1)
        {
            return "";
        }
        else
        {
            for (int r = 1; r <= tSSRS.MaxRow; r++)
            {
                //查询结果生成字符串，行间用“^~”分割，一行的字段间用"^$"分割
                for (i = 1; i <= tSSRS.MaxCol; i++)
                {
                    t_Str = t_Str + tSSRS.GetText(r, i) + "^$";
                }
                t_Str += "^~"; //换行
            }

        }

        return t_Str;
    }

    /**
     * 解释Formula公式
     * @param c_P 数组
     * @param c_Row_Number 行号
     * @return 字符串
     */
    public String Get_Formula(String[] c_P, int c_Row_Number)
    {
        String t_Return = "";
//        int i, i_Max;
        //i_Max = UBound(c_P)
        t_Return = "=";
        t_Return += c_P[0].trim() + "(";
        t_Return += Get_Cur_Row(c_P[1], c_Row_Number);
        t_Return += Get_Cur_Row(c_P[2], c_Row_Number) + ":";
        t_Return += Get_Cur_Row(c_P[3], c_Row_Number);
        t_Return += Get_Cur_Row(c_P[4], c_Row_Number) + ")";

        return t_Return;
    }

    /**
     * 得到c_P参数的积,如果需要指定精度，则最后一个参数指定精度
     * JD1表示精确到小数点1位，JD2表示精确到2位....
     * 如果包含非数字字段，则该字段为0
     * @param c_P 传入数组
     * @return 计算结果
     */
    private String Get_Mul_By_Name(String[] c_P)
    {
        int t_I, t_Max;
        double t_Mul, t_Temp;
        int t_JD;
        String t_JDStr;
//        String t_Format;
        String t_return = "";

        t_JD = 0;
        t_Max = c_P.length - 1;

        if (t_Max > 1)
        {
            //如果参数大于2个，则判断最后一个参数是否表示精度
            t_JDStr = c_P[t_Max].trim();

            if (t_JDStr.startsWith("JD"))
            {
                t_JDStr = t_JDStr.substring(2);
                if (JRptUtility.IsNumeric(t_JDStr))
                {
                    t_JD = Integer.parseInt(t_JDStr);
                    t_Max -= 1;
                }
            }
        }

        t_Mul = 1;
        t_Temp = 1;

        for (t_I = 0; t_I <= t_Max; t_I++)
        {
            //t_Temp = Get_Value_Of_Cell(c_P(t_I))
            try
            {
                t_Temp = Double.valueOf(c_P[t_I]).doubleValue();
            }
            catch (NumberFormatException nfe)
            {
                t_Temp = 0;
            }

            if (JRptUtility.IsNumeric(new Double(t_Temp).toString()))
            {
                t_Mul *= t_Temp;
            }
        }

        if (t_Mul == 0 && !m_Need_Display_Zero)
        {
            t_return = "";
        }
        else
        {
            t_return = JRptUtility.My_Format(t_Mul, t_JD);
        }

        return t_return;
    }

    /**
     * 本过程的功能是把系统定义文件中sheet=1的变量全部替换
     * 系统定义文件中sheet=1的变量包括两种类型，固定变量和不定变量
     * @param c_FileName 文件名
     * @throws Exception
     */
    private void Get_Ord_Val(String c_FileName)
            throws Exception
    {
        File fs;
        String t_Str;
        String sVtsFile = getConfigFilePath() + "system_define.vts";
        int i;

        fs = new File(sVtsFile);
        if (!fs.exists())
        {
            return;
        }

        m_SysFBV = new BookModelImpl();
        //打开系统定义文件
        m_SysFBV.read(sVtsFile);
        m_SysFBV.setSheet(0);

        for (i = 0; i <= m_SysFBV.getLastRow(); i++)
        {
            if (m_SysFBV.getText(i, 1).equalsIgnoreCase("1"))
            {
                //替换固定变量
                AddVar(m_SysFBV.getText(i, 0), m_SysFBV.getText(i, 2));
            }
            if (m_SysFBV.getText(i, 1).equalsIgnoreCase("2"))
            {
                //替换sql语句返回值的变量
                t_Str = Get_Sql_Val(m_SysFBV.getText(i, 0), m_SysFBV.getText(i, 2)
                        , m_SysFBV.getText(i, 3), c_FileName);
            }

        }
        //原来是从System_Define.vts中得到这两个值，现在改为JRptConfig类从JRptConfig.xml中得到
        //m_SysFBV.setSheet(2);
        //mWebReportWebDirectory = m_SysFBV.getText(1, 1);
        //mWebReportDiskDirectory = m_SysFBV.getText(2, 1);
    }

    /**
     * 得到c_P中所有的指定列的值,并且将各个字段拼成一个字符串，用","分开
     * @param c_P 传入数组
     * @param c_Row 行号
     * @return 字符串
     * @throws Exception
     */
    public String Get_Row_Text(String[] c_P, int c_Row)
            throws Exception
    {
        int t_Sheet;
        int i, i_Max;
        String t_Return = "", t_Str;

        i_Max = c_P.length - 1;
        if (i_Max < 0)
        {
            return t_Return;
        }
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        for (i = 0; i <= i_Max; i++)
        {
            t_Str = m_FV.getText(c_Row, Integer.parseInt(c_P[i]));
            t_Return += t_Str.trim() + ",";
        }
        t_Return = t_Return.substring(0, t_Return.length() - 1);
        m_FV.setSheet(t_Sheet);
        return t_Return;
    }

    /**
     * 本函数的功能是采用递归法替换sql语句返回值的变量
     * @param c_VName 变量名
     * @param c_SQL SQL语句
     * @param c_DSN DSN
     * @param c_FileName 文件名
     * @return 字符串
     * @throws Exception
     */
    private String Get_Sql_Val(String c_VName, String c_SQL, String c_DSN, String c_FileName)
            throws Exception
    {

//        BookModelImpl  t_F1v;
        BookModelImpl t_F1Val;
        String t_ValName;
        String[] t_Name;
        String t_Sql;
        String t_DSN;
        String t_VName;
        String t_return = "";
        int i, j;
        String t_n_sql;

        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();

        t_Sql = c_SQL;
        t_DSN = c_DSN;
        t_VName = c_VName;

        //替换该sql语句中的系统变量和固定变量
        //t_Sql = ReplaceValSQL(t_Sql, m_Vals)

        if (c_VName.startsWith("#"))
        {
            return "";
        }

        //替换该sql语句中的系统变量和固定变量
        t_Sql = Replace_SysVal_InString(t_Sql, 0);

        if (t_Sql.indexOf("?") >= 0)
        {
            //替换后还有变量，说明该sql语句还有不定变量
            t_Name = t_Sql.split("[?]");

            for (j = 1; j <= (t_Name.length - 1) / 2; j++)
            {
                //得到第i/2个变量的名称
                t_ValName = JRptUtility.Get_Str(t_Sql, j * 2, "?").trim();

                if (!new File(getConfigFilePath() + "system_define.vts").exists())
                {
                    t_F1Val = new BookModelImpl();
                    t_F1Val.read(getConfigFilePath() + "system_define.vts");
                    t_F1Val.setSheet(0);
                    for (i = 0; i <= t_F1Val.getLastRow(); i++)
                    {
                        //查找变量
                        if (t_F1Val.getText(i, 0).equalsIgnoreCase(t_ValName))
                        {
                            //取得sql语句
                            t_Sql = t_F1Val.getText(i, 2);
                            t_VName = t_F1Val.getText(i, 0);

                            break;
                        }
                    }
                    //递归调用
                    t_n_sql = c_SQL;
                    t_n_sql = StrTool.replace(t_n_sql, "?" + t_ValName + "?", Get_Sql_Val(t_VName
                            , t_Sql, t_DSN, c_FileName));

                    t_return = Get_Sql_Val(c_VName, t_n_sql, c_DSN, c_FileName);

                    return t_return;

                }
            }
        }
        else
        {
            //替换后没有变量，说明为递归的最底层或者不用递归，则执行该sql语句
//            System.out.println("replaceSQL:" + t_Sql);
            tSSRS = tExeSQL.execSQL(t_Sql);

            if (tSSRS.MaxRow < 1)
            {
                t_return = "";
            }
            else
            {
                t_return = JRptUtility.ChgValue(tSSRS.GetText(1, 1));
                //刚取得的变量加入m_vals()数组
                AddVar(t_VName, t_return);
            }
        }
        return t_return;
    }

    /**
     * 得到c_P参数的和,如果需要指定精度，则最后一个参数指定精度，
     * @param c_P 数组
     * @return 字符串
     */
    private String Get_Sum_By_Name(String[] c_P)
    {
        int t_I, t_Max;
        double t_Sum;
        String t_Temp;
        int t_JD;
        String t_JDStr;
        String t_return = "";

        t_JD = 0;
        t_Max = c_P.length - 1;

        if (t_Max > 1)
        {
            //如果参数大于2个，则判断最后一个参数是否表示精度
            t_JDStr = c_P[t_Max].trim();
            if (t_JDStr.startsWith("JD"))
            {
                t_JDStr = t_JDStr.substring(2); //Mid(t_JDStr, 3, Len(t_JDStr) - 2)
                if (JRptUtility.IsNumeric(t_JDStr))
                {
                    t_JD = Integer.parseInt(t_JDStr);
                    t_Max -= 1;
                }
            }
        }

        t_Sum = 0;
        t_Temp = "0";

        for (t_I = 0; t_I <= t_Max; t_I++)
        {
            t_Temp = c_P[t_I];

            if (JRptUtility.IsNumeric(t_Temp))
            {
                t_Sum += Double.parseDouble(t_Temp);
            }
            else
            {
                if (!t_Temp.equalsIgnoreCase(""))
                {
                    break;
                }
                //t_Sum = t_Temp
                //Exit For
            }
        }

        if (t_Sum == 0 && !m_Need_Display_Zero)
        {
            t_return = "";
        }
        else
        {
            if (JRptUtility.IsNumeric(t_Temp))
            {
                t_return = JRptUtility.My_Format(t_Sum, t_JD);
            }
            else
            {
                t_return = t_Temp.trim();
            }
        }

        return t_return;
    }

    /**
     * 得到系统的变量
     * @param c_Val_Name 变量名
     * @param c_Index 编号
     * @return 字符串
     */
    public String Get_Sys_Val_Value(String c_Val_Name, int c_Index)
    {
        String t_Return;
        boolean t_Found;
        String t_Val_Name;
//        String t_Value;
        t_Return = "";

        t_Val_Name = c_Val_Name;
        t_Found = false;

        t_Return = Get_Val_In_User_Add(c_Val_Name, t_Found);

//        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        if (t_Return.equalsIgnoreCase(""))
        {
            if (t_Val_Name.equalsIgnoreCase("DATE"))
            {
                t_Return = JRptUtility.getDate();
            }
            else if (t_Val_Name.equalsIgnoreCase("TIME"))
            {
                t_Return = JRptUtility.getTime();
            }
            else if (t_Val_Name.equalsIgnoreCase("NOW"))
            {
                t_Return = JRptUtility.getNow();
            }
            else if (t_Val_Name.equalsIgnoreCase("PAGES"))
            {
                t_Return = String.valueOf(mCurPages).trim();
            }
            else if (t_Val_Name.equalsIgnoreCase("MAXROW"))
            {
                t_Return = String.valueOf(m_Data_Row);
            }
            else
            {
                t_Return = "";
            }
        }

        return t_Return;
    }

    /**
     * 从用户自己定义的变量查找该变量是否存在
     * @param c_ValName 变量名
     * @param c_Found 是否存在
     * @return 字符串
     */
    public String Get_Val_In_User_Add(String c_ValName, boolean c_Found)
    {
        String t_Return;
        String t_Val_Name;
        int i, i_Max;

        t_Return = "";
        t_Val_Name = "?" + c_ValName + "?";
        i_Max = m_Vals.size() - 1;

        for (i = 1; i <= i_Max; i++)
        {
            if (((String[]) m_Vals.get(i))[0].equalsIgnoreCase(t_Val_Name))
            {
                t_Return = ((String[]) m_Vals.get(i))[1];
                break;
            }
        }

        return t_Return;
    }

    void GetDefaultConnection()
    {

    }

    /**
     * 从历史中读取相关的报表，如果存在，则直接读取
     * @param cFileName 报表文件名
     * @return 字符串
     * @throws Exception
     */
    public String GetURLFromHistory(String cFileName)
            throws Exception
    {
        BookModelImpl tFV = new BookModelImpl();
        int tTimeOutDay;
        String tReportVarName, t_return = "";

        tFV.read(cFileName);
        tFV.setSheet(R_Define);

        mNeedLog = false;

        if (!tFV.getText(8, 0).trim().equalsIgnoreCase("timeoutsetting"))
        {
            return "";
        }
        if (!tFV.getText(8, 1).trim().equalsIgnoreCase("1"))
        {
            //只有当该值设置为1时才从历史中读取
            return "";
        }

        if (tFV.getText(8, 2).equalsIgnoreCase(""))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            mTimeOutDate = formatter.parse("3003-1-1");
        }
        else
        {
            tTimeOutDay = Integer.parseInt(m_FV.getText(8, 2)); //得到天数
            Calendar nowdate = Calendar.getInstance();

            nowdate.add(Calendar.DAY_OF_YEAR, tTimeOutDay); //将天数相加
            mTimeOutDate = nowdate.getTime(); //得到超时日期
        }

        mNeedLog = true;

        CReportLog tRL = new CReportLog();

        tReportVarName = CreateReportVarName();
        t_return = tRL.GetHistoryFile(tReportVarName);
        m_ErrString = tRL.m_ErrString;

        return t_return;
    }

    /**
     * 隐藏列,row:s_x:s_y:e_x:e_y:含义为按照row相同进行隐藏，
     * 校验数据从s_x:s_y:e_x:e_y区域内
     * @param cControlString 传入参数
     * @throws Exception
     */
    void Hide_Col(String cControlString)
            throws Exception
    {

        int tRow;
        int tStartRow, tStartCol, tEndRow, tEndCol;
        int tCurCol;
        String[] tStr;
        String tSameStr;
        int t_Sheet;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        tStr = cControlString.split(":");
        tRow = Integer.parseInt(tStr[0]);
        tStartRow = Integer.parseInt(tStr[1]);
        tStartCol = Integer.parseInt(tStr[2]);
        tEndRow = Integer.parseInt(tStr[3]);
        tEndCol = Integer.parseInt(tStr[4]);
        if (tEndRow == -1)
        {
            tEndRow = m_FV.getLastRow() + 1;
        }
        if (tEndCol == -1)
        {
            tEndCol = m_FV.getLastCol() + 1;
        }

        tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);

        tCurCol = tStartCol;

        while (true)
        {
            //当隐藏校验的左指针超出报表时退出
            if (tStartCol > m_FV.getLastCol() + 1)
            {
                break;
            }
            if (tCurCol > m_FV.getLastCol() + 1 + 1)
            {
                break;
            }

            if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(tRow - 1,
                    tCurCol - 1).trim()))
            {
                tCurCol += 1;
            }
            else
            {
                if (CheckZero(tStartRow, tStartCol, tEndRow, tCurCol - 1))
                {
                    //On Error Resume Next
                    m_FV.deleteRange( -1, tStartCol - 1, 0, tCurCol - 1 - 1
                            , Constants.eShiftHorizontal);
                    //On Error GoTo ErrDel
                    tCurCol = tStartCol; //删除后，列的指针仍旧回去，不增加
                    tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
                }
                else
                {
                    tStartCol = tCurCol;
                    tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
                }
            }
        }

        m_FV.setSheet(t_Sheet);
    }

    /**
     *隐藏行,col:s_x:s_y:e_x:e_y:含义为按照row相同进行隐藏，校验数据从s_x:s_y:e_x:e_y区域内
     * @param cControlString 传入参数
     * @throws Exception
     */
    void Hide_Row(String cControlString)
            throws Exception
    {
        int tCol;
        int tStartRow, tStartCol, tEndRow, tEndCol;
        int tCurRow;
        String[] tStr;
        String tSameStr = "";
        int t_Sheet;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        tStr = cControlString.split(":");
        tCol = Integer.parseInt(tStr[0]);
        tStartRow = Integer.parseInt(tStr[1]);
        tStartCol = Integer.parseInt(tStr[2]);
        tEndRow = Integer.parseInt(tStr[3]);
        tEndCol = Integer.parseInt(tStr[4]);

        if (tEndRow == -1)
        {
            tEndRow = m_FV.getLastRow() + 1;
        }
        if (tEndCol == -1)
        {
            tEndCol = m_FV.getLastCol() + 1;
        }

        tSameStr = m_FV.getText(tCol - 1, tStartCol - 1);
        tCurRow = tStartRow;
        while (true)
        {
            //当隐藏校验的左指针超出报表时退出
            if (tStartRow > m_FV.getLastRow() + 1)
            {
                break;
            }
            if (tCurRow > m_FV.getLastRow() + 1 + 1)
            {
                break;
            }

            if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(tCol - 1, tCurRow - 1).trim()))
            {
                tCurRow += 1;
            }
            else
            {
                if (CheckZero(tStartRow, tStartCol, tCurRow - 1, tEndCol))
                {
                    // On Error Resume Next
                    m_FV.deleteRange(tStartRow - 1, -1, tCurRow - 1 - 1, -1
                            , Constants.eShiftVertical);
                    // On Error GoTo ErrDel
                    tCurRow = tStartRow; //删除后，列的指针仍旧回去，不增加
                    tSameStr = m_FV.getText(tCol - 1, tStartRow - 1);
                }
                else
                {
                    tStartRow = tCurRow;
                    tSameStr = m_FV.getText(tCol - 1, tStartRow - 1);
                }
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 初始化6个sheet
     * @throws Exception
     */
    private void InitSheets()
            throws Exception
    {
        m_FV.insertSheets(3, 6);
    }

    /**
     * 打印报表
     * @param FileName 文件名
     * @param pageContext 从JSP中传入的上下文
     * @throws Exception
     */
    public void Prt_RptList(PageContext pageContext, String FileName)
            throws Exception
    {
        this.setup(pageContext, FileName);
        Prt_RptList1();
    }

    public void Prt_RptList(ServletContext sCtx, HttpServletRequest req, String FileName)
            throws Exception
    {
        this.setup(sCtx, req, FileName);
        Prt_RptList1();
    }

    //测试用
    public void Prt_RptList(String configPath, String templatePath, String generatedPath
            , String urlPrefix, String appPath, String FileName)
            throws Exception
    {
        this.setup(configPath, templatePath, generatedPath, urlPrefix, appPath, FileName);
        Prt_RptList1();
    }

    public void Prt_RptList1()
            throws Exception
    {

        String c_FileName = m_FormatFileName;

        Get_Ord_Val(c_FileName); //取非实时变量

        Read_Format(c_FileName); //读取格式

        mOutWebReportURL = GetURLFromHistory(c_FileName);
//        System.out.println("WebReportURL:" + mOutWebReportURL);
        if (!mOutWebReportURL.equalsIgnoreCase(""))
        {
            File f = new File(mWebReportDiskDirectory.trim() + mOutWebReportURL);
            if (f.exists())
            {
//                System.out.println("--exists--");
                //文件存在，则返回，不用重新生成
                mOutWebReportURL = mWebReportWebDirectory.trim() + mOutWebReportURL;
//                System.out.println("direct+url:" + mOutWebReportURL);
                //如果从历史中读取，则直接返回
                return;
            }
        }

        DealData();

        Delete_Invisible_Cols(); //删除不可见的列

        Format_Cell(); //对数据的格式进行解释

        Raise_Report(); //生成最后的报表，解释每页的格式

        SetReportDataEX(); //对设置特殊的报表区域格式和数据

        //begin 确保列宽度正确 add by wangyc 2005.1.21
        m_FV.setSheet(R_Format);
        int i_Max = m_FV.getLastCol() + 1;
        for (int i = 1; i < i_Max; i++)
        {
            m_FV.setSheet(R_Data);
            int t_ColWidth = m_FV.getColWidth(i - 1);
            m_FV.setSheet(R_Report);
            m_FV.setColWidth(i - 1, i - 1, t_ColWidth, false);
        }
        m_FV.setSheet(R_Report);
        m_FV.setPrintHeader("");
        m_FV.setPrintFooter("");
        m_FV.setPrintGridLines(false);
        m_FV.setPrintLandscape(true);
        //end

        AddPicture(); //向最终报表添加图片及生成条形码

        if (m_Need_Preview)
        {
            Copy_To_F1Book(); //拷贝到m_DestFV
        }

        //输出到文件，文件名为原来的格式文件名+Out.vts
        String tOutFileName, tOutFileName1;
        String tFileNameNoPath;

        tFileNameNoPath = JRptUtility.Get_OnlyFileNameEx(c_FileName);
        m_FV.deleteSheets(0, 8);
        String sTime = "";

        sTime = "_" + JRptUtility.getDate() + "_" + JRptUtility.getTime();
        sTime = sTime.replaceAll(":", "-");

        tOutFileName1 = tFileNameNoPath.trim() + sTime + ".vts";
        tOutFileName = mWebReportDiskDirectory.trim() + tOutFileName1;

        //按指定格式生成文件
        m_FV.write(tOutFileName, new com.f1j.ss.WriteParams(Constants.eFileCurrentFormat));

        if (StrTool.isUnicodeString(mWebReportWebDirectory.trim()))
        {
            mWebReportWebDirectory = StrTool.unicodeToGBK(mWebReportWebDirectory.trim());
        }

        if (StrTool.isUnicodeString(tOutFileName1.trim()))
        {
            tOutFileName1 = StrTool.unicodeToGBK(tOutFileName1.trim());
        }

        mOutWebReportURL = mWebReportWebDirectory.trim() + tOutFileName1;
        if (StrTool.isUnicodeString(mOutWebReportURL.trim()))
        {
            mOutWebReportURL = StrTool.unicodeToGBK(mOutWebReportURL.trim());
        }

        if (mNeedLog)
        {
            CReportLog tRL = new CReportLog();
            tRL.WriteHistoryFile(tOutFileName1, CreateReportVarName(), mTimeOutDate);
        }

        //是否生成excel文件
        //mNeedExcel = true;
        if (mNeedExcel)
        {
            tOutFileName1 = tFileNameNoPath.trim() + sTime + ".xls";
            tOutFileName = mWebReportDiskDirectory.trim() + tOutFileName1;
            m_FV.write(tOutFileName, new com.f1j.ss.WriteParams(Constants.eFileExcel97));
            if (StrTool.isUnicodeString(tOutFileName1.trim()))
            {
                tOutFileName1 = StrTool.unicodeToGBK(tOutFileName1.trim());
            }
            mExcelOutWebReportURL = mWebReportWebDirectory.trim() + tOutFileName1;
            if (StrTool.isUnicodeString(mExcelOutWebReportURL.trim()))
            {
                mExcelOutWebReportURL = StrTool.unicodeToGBK(mExcelOutWebReportURL.trim());
            }
        }
    }

    /**
     * 处理定义文件中picture及barcode行
     *
     * @throws Exception
     */
    public void AddPicture()
            throws Exception
    {
        int t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        //BufferedImage image = bCode.getImage();

        m_FV.setSheet(R_Define);
        for (int i = 0; i < m_FV.getLastRow(); i++)
        {
            //处理picture行
            if (m_FV.getText(i, 0).equalsIgnoreCase("picture"))
            {
                String objectName = m_FV.getText(i, 1);
                String strURL = m_FV.getText(i, 2);
                String urlType = m_FV.getText(i, 3);
                m_FV.setSheet(R_Format);
                GRObject gr = m_FV.getObject(objectName);
                if (gr == null)
                {
                    DEBUG_OUT("错误的PICTURE行：图片不存在或图片名" + objectName + "不正确");
                }
                else
                {
                    java.net.URL url = null;
                    if (urlType.trim().equalsIgnoreCase("1"))
                    {
                        url = new java.net.URL(this.mAppPath + strURL);
                    }
                    else
                    {
                        url = new java.net.URL(strURL);
                    }
                    DEBUG_OUT(url.toString());
                    m_FV.setSheet(R_Report);
                    GRObjectPos pos = gr.getPos();
                    m_FV.addPicture(pos.getX1(), pos.getY1(), pos.getX2(), pos.getY2()
                            , JRptUtility.getGifBytes(url));
                }
                m_FV.setSheet(R_Define);
            }
            //处理barcode行
            if (m_FV.getText(i, 0).equalsIgnoreCase("barcode"))
            {
                String objectName = m_FV.getText(i, 1);
                String strParameter = m_FV.getText(i, 2);
                String strCodeVar = m_FV.getText(i, 3);

                m_FV.setSheet(R_Format);
                GRObject gr = m_FV.getObject(objectName);
                if (gr == null)
                {
                    DEBUG_OUT("错误的BarCode行：图片不存在或图片名" + objectName + "不正确");
                }
                else
                {
                    strCodeVar = strCodeVar.replace('?', ' ').trim();
                    String params[] = strParameter.split("&");
                    String t_Str = "";
                    String strCode = this.Get_Val_In_User_Add(strCodeVar, false);
                    BarCode bcode = new BarCode(strCode);
                    //获得条形码参数
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

                    bcode.setFormatType(BarCode.FORMAT_GIF);
                    m_FV.setSheet(R_Report);
                    GRObjectPos pos = gr.getPos();
                    m_FV.addPicture(pos.getX1(), pos.getY1(), pos.getX2(), pos.getY2()
                            , bcode.getBytes());
                }
                m_FV.setSheet(R_Define);
            }
        }
        m_FV.objectBringToFront();

        m_FV.setSheet(t_Sheet);
    }

    /**
     * 读取数据
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key 键值
     * @param c_FldCount 字段个数
     * @param c_T8 转制格式参数
     * @throws Exception
     */

    public void Put_Data(int c_SRow, int c_SCol, String c_SQL, String c_DSN, String c_Key
            , int c_FldCount, String c_T8)
            throws Exception
    {
        //Debug.Print c_SQL
        if (!c_Key.equalsIgnoreCase(""))
        {
            if (!c_T8.equalsIgnoreCase(""))
            {
                Put_Data_By_Key1(c_SRow, c_SCol, c_SQL, c_DSN, c_Key, c_FldCount, c_T8);
            }
            else
            {
                Put_Data_By_Key(c_SRow, c_SCol, c_SQL, c_DSN, c_Key, c_FldCount);
            }
        }
        else
        {
            if (c_T8.equalsIgnoreCase("1"))
            {
                //按行列数据都转置
                Put_Row_Data_NOKey(c_SRow, c_SCol, c_SQL, c_DSN, c_FldCount);
            }
            else if (c_T8.equalsIgnoreCase("2"))
            {
                //按所有列一起进行转置
                Put_Row_Data_NOKey1(c_SRow, c_SCol, c_SQL, c_DSN, c_FldCount);
            }
            else
            {
                Put_Data_NOKey(c_SRow, c_SCol, c_SQL, c_DSN, c_FldCount);
            }
        }
        //m_FV.WriteEx "c:\test.vts", F1FileFormulaOne6
    }

    /**
     * 分组数据，并保存
     * @param c_Group 分组列
     * @param c_Src_F1 源Sheet
     * @param c_Dst_F1 目标Sheet
     * @param c_Count 行数
     * @throws Exception
     */

    public void Order0(String[] c_Group, BookModelImpl c_Src_F1, BookModelImpl c_Dst_F1
            , int c_Count)
            throws Exception
    {
        int t_Next_Start = 1; //保存写到目标sheet的行,初始为1
        String t_Base; //保存分组标志
        int i = 1;
        int i_Group = Integer.parseInt(c_Group[0]);
        while (i <= c_Count)
        {
            if (c_Src_F1.getText(i - 1, i_Group - 1).trim().equalsIgnoreCase(""))
            {
                c_Src_F1.deleteRange(i - 1, 0, i - 1, c_Src_F1.getLastCol(), Constants.eShiftRows);
                c_Count -= 1;
                i -= 1;
            }
            i += 1;
        }
        c_Dst_F1.clearRange(0, 0, c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol(), Constants.eClearAll);

        //初始为第一行，第一个分组列的值
        t_Base = c_Src_F1.getText(0, i_Group - 1);

        for (i = 1; i <= c_Count; i++)
        {
            //c_Count为源sheet中的剩余行，越来越小，直到为0
            if (c_Count == 0)
            {
                break;
            }
            int j = i;
            while (j <= c_Count)
            {
                //全部搜索完毕否，若没有则继续
                if (!t_Base.equalsIgnoreCase("") && c_Src_F1.getText(j - 1
                        , i_Group - 1).trim().equalsIgnoreCase(t_Base))
                {
                    //移动到目的地
                    c_Dst_F1.copyRange(t_Next_Start - 1, 0, t_Next_Start - 1, c_Src_F1.getLastCol()
                            , c_Src_F1, j - 1, 0, j - 1, c_Src_F1.getLastCol());
                    //接着在源中删除之
                    c_Src_F1.deleteRange(j - 1, 0, j - 1, c_Src_F1.getLastCol()
                            , Constants.eShiftRows);

                    c_Count -= 1;
                    j--;
                    t_Next_Start += 1;
                }
                j++;
            }
            //全部搜索完毕，更新分组标志
            t_Base = c_Src_F1.getText(i - 1, i_Group - 1);
            i--;
        }
        //end for
    }

    /**
     * 多次分组
     * @param c_Group 分组参数
     * @param c_Src_F1 源Sheet
     * @param c_Dst_F1 目标Sheet
     * @throws Exception
     */

    public void Order1(String[] c_Group, BookModelImpl c_Src_F1, BookModelImpl c_Dst_F1)
            throws Exception
    {
        int t_Next_Start = 1; //保存写到目标sheet的行,初始为1
        String t_Base; //保存分组标志
        String t_Comp; //为当前分组列的前一个分组列的值，如果当前分组列值和t_Comp相同，
        //说明可以继续查找，否则不必搜索到最后，现在就退出当前查找
        int t_Count;
        int i = 1;

        for (int m = 1; m <= c_Group.length - 1; m++)
        {
            t_Next_Start = 1;

            //移动到目的地
            c_Dst_F1.copyRange(0, 0, c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol(), c_Dst_F1, 0, 0
                    , c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol());

            //接着在源中删除之
            c_Src_F1.clearRange(0, 0, c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol()
                    , Constants.eClearValues);

            t_Comp = c_Src_F1.getText(0, Integer.parseInt(c_Group[m - 1]) - 1);
            t_Count = c_Src_F1.getLastRow() + 1;

            i = 1;
            while (i <= t_Count)
            {
                for (int j = 1; j <= c_Group.length - 1; j++)
                {
                    if (c_Src_F1.getText(i - 1
                            , Integer.parseInt(c_Group[j]) - 1).trim().equalsIgnoreCase(""))
                    {
                        c_Src_F1.deleteRange(i - 1, 0, i - 1, c_Src_F1.getLastCol()
                                , Constants.eShiftRows);
                        t_Count--;
                        i--;
                    }
                }
                i++;
            } //end while

            //初始为第一行，第一个分组列的值
            t_Base = c_Src_F1.getText(0, Integer.parseInt(c_Group[m]) - 1);

            for (i = 1; i <= t_Count; i--)
            {
                if (t_Count == 0)
                {
                    break;
                }
                int j = i;
                while (j <= t_Count)
                {
                    //全部搜索完毕否，若没有则继续
                    if (!t_Base.equalsIgnoreCase("") && c_Src_F1.getText(j - 1
                            , Integer.parseInt(c_Group[m]) - 1).trim().equalsIgnoreCase(t_Base))
                    {
                        //移动到目的地
                        c_Dst_F1.copyRange(t_Next_Start - 1, 0, t_Next_Start - 1
                                , c_Src_F1.getLastCol(), c_Src_F1, j - 1, 0, j - 1
                                , c_Src_F1.getLastCol());
                        //接着在源中删除之
                        c_Src_F1.deleteRange(j - 1, 0, j - 1, c_Src_F1.getLastCol()
                                , Constants.eShiftRows);
                        t_Count--;
                        j--;
                        t_Next_Start++;
                    }
                    else
                    {
                        t_Comp = c_Src_F1.getText(j - 1, Integer.parseInt(c_Group[m - 1]) - 1).trim();
                        break;
                    }
                    j++;
                    if (!c_Src_F1.getText(j - 1
                            , Integer.parseInt(c_Group[m - 1]) - 1).trim().equalsIgnoreCase(t_Comp))
                    {
                        break;
                    }
                }
                //全部搜索完毕，更新分组标志
                t_Base = c_Src_F1.getText(i - 1, Integer.parseInt(c_Group[m]) - 1);
                i--;
            }
            //end for
        }
        //end for
    }

    /**
     * 读取数据，放到R_Data中,
     * c_Key表示该查询语句要按某些列为健值来填写数据格式必须为1,2:1,2:3,4
     * 其中":"前面的1,2表示对应到现在已经有的数据，后面的1,2表示对应到查询结果的1，2字段,
     * 最后的3,4表示要填写3，4两个字段
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key 表示该查询语句要按某些列为健值来填写,数据格式必须为1,2:1,2:3,4
     * @param c_FldCount 字段数
     * @return 字符串
     * @throws Exception
     */
    public String Put_Data_By_Key(int c_SRow, int c_SCol, String c_SQL, String c_DSN, String c_Key
            , int c_FldCount)
            throws Exception
    {
        //在填写数据前会标记该列是否已经填写过
        Vector t_HavePut = new Vector();
//        String t_DSN;
//        String t_Conn;

        SSRS t_Rs = new SSRS(); //替换t_Rs
        int t_Row, t_Col, t_PP_Row;
//        int t_FldCount;
        int t_RowCount;
        int j;
        Vector t_FldIndex = new Vector();
        Vector t_FldKey = new Vector();
        Vector t_TextCol = new Vector();

        int t_FldDisplayCount;
        int t_KeyCount;
        String[] t_Data;
        int t_Sheet;
        String[] t_Temp;
        String t_Str;
        String rtvalue = "";

        t_Row = c_SRow;
        t_Col = c_SCol;

        ExeSQL tExeSQL = new ExeSQL();
        t_Rs = tExeSQL.execSQL(c_SQL);
        /****************设置显示列对应RecordSet中的字段值*****************************/

        t_Data = c_Key.split(":");

        if (!t_Data[2].equalsIgnoreCase(""))
        {
            if (JRptUtility.IsNumeric(t_Data[2]) && t_Data[2].indexOf(",") < 0)
            {
                t_FldIndex.setElementAt(t_Data[2], 0);
            }
            else
            {
                t_Temp = t_Data[2].split(",");

                for (int k = 0; k < t_Temp.length; k++)
                {
                    t_FldIndex.setElementAt(t_Temp[k], k);
                }
            }
        }
        /******************************End设置显示列对应RecordSet中的字段值*************/

        /*********************************设置需要参照的Formula One列*******************/
        if (!t_Data[0].trim().equalsIgnoreCase(""))
        {
            if (JRptUtility.IsNumeric(t_Data[0]) && t_Data[0].indexOf(",") < 0)
            {
                t_TextCol.setElementAt(t_Data[0], 0);
            }
            else
            {
                t_Temp = t_Data[0].split(",");

                for (int k = 0; k < t_Temp.length; k++)
                {
                    t_TextCol.setElementAt(t_Temp[k], k);
                }
            }
        }
        /************************************End设置需要参照的Formula One列***************/

        /*********************************设置Recordset的列******************************/
        if (!t_Data[1].equalsIgnoreCase(""))
        {
            if (JRptUtility.IsNumeric(t_Data[1]) && t_Data[1].indexOf(",") < 0)
            {
                t_FldKey.setElementAt(t_Data[1], 0);
            }
            else
            {
                t_Temp = t_Data[1].split(",");

                for (int k = 0; k < t_Temp.length; k++)
                {
                    t_FldKey.setElementAt(t_Temp[k], k);
                }
            }
        }
        /*****************************End设置Recordset的列***************************/

        /****************************************************************************/

        //得到显示的字段的个数
        t_FldDisplayCount = t_FldIndex.size() - 1;
        t_KeyCount = t_TextCol.size() - 1;
        if (!(t_KeyCount == t_FldKey.size() - 1))
        {
            DEBUG_OUT(
                    "Put_Data_By_Key()方法发生错误：定义的字段中的Key个数和格式中的Key格式不同！");
            return "";
        }

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_RowCount = m_FV.getLastRow() + 1;

        for (int i = 0; i <= t_RowCount; i++)
        {
            t_HavePut.addElement(Boolean.FALSE);
        }

        for (int rowcount = 0; rowcount < t_Rs.getMaxRow(); rowcount++)
        {
            //得到匹配的行
            t_PP_Row = 0;
            for (int i = t_Row; i <= t_RowCount; i++)
            {
                if (t_HavePut.get(i).equals(Boolean.FALSE))
                {
                    for (j = 0; j <= t_KeyCount; j++)
                    {
                        if (m_FV.getText(i - 1
                                , Integer.parseInt(t_TextCol.get(j).toString())
                                - 1).trim().equalsIgnoreCase(JRptUtility.ChgValue(t_Rs.GetText(
                                rowcount + 1
                                , Integer.parseInt(t_FldKey.get(j).toString()) + 1)).trim()))
                        {
                            break;
                        }
                    }

                    if (j == t_KeyCount + 1)
                    {
                        t_PP_Row = i;
                        break;
                    }
                }
            }

            if (t_PP_Row != 0)
            {
                for (int i = 0; i <= t_FldDisplayCount; i++)
                {
                    t_Str = JRptUtility.ChgValue(t_Rs.GetText(rowcount + 1
                            , Integer.parseInt(t_FldIndex.get(i).toString()) + 1));

                    if (JRptUtility.IsNumeric(t_Str))
                    {
                        m_FV.setNumber(t_PP_Row - 1, t_Col + i - 1, Double.parseDouble(t_Str));
                    }
                    else
                    {
                        m_FV.setText(t_PP_Row - 1, t_Col + i - 1, t_Str);
                    }

                    if (m_Function_Type.equalsIgnoreCase("1"))
                    {
                        m_Function_Type = String.valueOf(0);
                        return t_Str;
                    }
                }
                t_HavePut.setElementAt(Boolean.TRUE, t_PP_Row);
            }
            t_Row += 1;
        }

        m_FV.setSheet(t_Sheet);
        return rtvalue;
    }

    /**
     * 读取数据，放到R_Data中,
     * c_Key表示该查询语句要按某些列为健值来填写数据格式必须为1,2:1,2:3,4
     * 其中":"前面的1,2表示对应到现在已经有的数据，后面的1,2表示对应到查询结果的1，2字段,最后的3,4表示要填写3，4两个字段
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key 表示该查询语句要按某些列为健值来填写数据格式必须为1,2:1,2:3,4
     * @param c_FldCount 字段数
     * @param c_T8 转换格式参数
     * @throws Exception
     */
    public void Put_Data_By_Key1(int c_SRow, int c_SCol, String c_SQL, String c_DSN, String c_Key
            , int c_FldCount, String c_T8)
            throws Exception
    {
        Vector t_HavePut = new Vector(); //在填写数据前会标记该列是否已经填写过
//        String t_DSN;
//        String t_Conn;
        SSRS t_Rs = new SSRS();
        int t_Row, t_Col;
//        int t_PP_Row;
//        int t_FldCount;
        int t_RowCount = 0;
        Vector t_FldIndex = new Vector();
        Vector t_FldKey = new Vector();
        Vector t_TextCol = new Vector();
        int t_FldDisplayCount;
        int t_KeyCount;
        String[] t_Data;
        int t_Sheet;
        String[] t_Temp;
        String t_Str;
        int t_Next_Line;
        BookModelImpl t_F1v;
        String[] t_Group;
        int j;

        t_Row = c_SRow;
        t_Col = c_SCol;
        ExeSQL tExeSQL = new ExeSQL();
        t_Rs = tExeSQL.execSQL(c_SQL);
        /*******************************设置显示列对应RecordSet中的字段值**************/

        t_Data = c_Key.split(";");

        if (!t_Data[2].trim().equalsIgnoreCase(""))
        {
            if (JRptUtility.IsNumeric(t_Data[2]) && t_Data[2].indexOf(",") < 0)
            {
                t_FldIndex.setElementAt(t_Data[2], 0);
            }
            else
            {
                t_Temp = t_Data[2].split(",");

                for (j = 0; j < t_Temp.length; j++)
                {
                    t_FldIndex.setElementAt(t_Temp[j], j);
                }
            }
        }

        /*********************************End设置显示列对应RecordSet中的字段值*****************/

        /*****************************************取得待分组列值，并存入书组********************/

        StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < t_FldIndex.size(); i++)
        {
            sbuffer.append(t_FldIndex.elementAt(i).toString());
            sbuffer.append(" ");
        }

        t_Group = sbuffer.toString().split(" ");

        /*****************************'设置需要参照的Formula One列*****************************/
        if (!t_Data[0].trim().equalsIgnoreCase(" "))
        {

            if (JRptUtility.IsNumeric(t_Data[0]) && t_Data[0].indexOf(",") < 0)
            {
                t_TextCol.setElementAt(t_Data[0], 0);
            }
            else
            {
                t_Temp = t_Data[0].split(",");

                for (j = 0; j < t_Temp.length; j++)
                {
                    t_TextCol.setElementAt(t_Temp[j], j);
                }
            }
        }
        /******************************End设置需要参照的Formula One列*************************/

        /*********************************设置Recordset的列**********************************/

        if (!t_Data[1].trim().equalsIgnoreCase(""))
        {

            if (JRptUtility.IsNumeric(t_Data[1]) && t_Data[1].indexOf(",") < 0)
            {
                t_FldKey.setElementAt(t_Data[1], 0);
            }
            else
            {
                t_Temp = t_Data[1].split(",");

                for (j = 0; j < t_Temp.length; j++)
                {
                    t_FldKey.setElementAt(t_Temp[j], j);
                }
            }
        }
        /***********************************End设置Recordset的列***********************/

        t_F1v = new BookModelImpl();
        t_FldDisplayCount = t_FldIndex.size() - 1; //得到显示的字段的个数
        t_KeyCount = t_TextCol.size() - 1;
        if (t_KeyCount == t_FldKey.size() - 1)
        {
            // m_ErrString = "Put_Data_By_Key:定义的字段中的Key个数和格式中的Key格式不同";
            return;
        }

        t_Sheet = m_FV.getSheet();
        t_RowCount = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_Data); //初始化数据填写标志变量
        for (int i = 0; i <= t_RowCount; i++)
        {
            t_HavePut.setElementAt(Boolean.FALSE, i);
        }
        if (t_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1 + 1;
        }
        if (t_Col == -1)
        {
            t_Col = m_FV.getLastCol() + 1 + 1;
        }
        t_Next_Line = 1;

        for (int rowcount = 0; rowcount < t_Rs.getMaxRow(); rowcount++)
        {
            //得到匹配的行
            //t_PP_Row = 0;
            for (int i = t_Row; i <= t_RowCount; i++)
            {
                if (t_HavePut.get(i).equals(Boolean.FALSE))
                {

                    for (j = 0; j <= t_KeyCount; j++)
                    {
                        if (m_FV.getText(i - 1
                                , Integer.parseInt(t_TextCol.get(j).toString())
                                - 1).trim().equalsIgnoreCase(JRptUtility.ChgValue(t_Rs.GetText(
                                rowcount + 1
                                , Integer.parseInt(t_FldKey.get(j).toString()) + 1)).trim()))
                        {
                            break;
                        }
                    }
                    if (j == t_KeyCount + 1)
                    {
                        //匹配否
                        //t_PP_Row = i;
                        //匹配则在此插入一行
                        for (j = 1; j <= m_FV.getLastCol() + 1; j++)
                        {
                            t_F1v.setText(t_Next_Line - 1, j - 1, m_FV.getText(i - 1, j - 1));
                        }

                        for (j = 0; j <= t_FldDisplayCount; j++)
                        {
                            t_Str = JRptUtility.ChgValue(t_Rs.GetText(rowcount + 1
                                    , Integer.parseInt(t_FldIndex.get(j).toString())));

                            if (JRptUtility.IsNumeric(t_Str))
                            {
                                //在t_F1V组装，完成后再返回
                                t_F1v.setNumber(t_Next_Line - 1, t_Col + j - 1
                                        , Double.parseDouble(t_Str));
                            }
                            else
                            {
                                t_F1v.setText(t_Next_Line - 1, t_Col + j - 1, t_Str);
                            }
                            if (m_Function_Type.equalsIgnoreCase("1"))
                            {
                                m_Function_Type = "0";
                                break;
                            }

                        }

                        t_Next_Line += 1; //结束插入
                    }
                }
            }
        } //end while

        int t_Count = t_F1v.getLastRow() + 1;
        if (t_Group.length > 0)
        {
            Order0(t_Group, t_F1v, m_FV, t_F1v.getLastRow() + 1); //第一次分组
        }

        if (t_Group.length > 1)
        {
            Order1(t_Group, t_F1v, m_FV); //多次分组
            m_FV.setSheet(t_Sheet);
        }
    }

    /**
     * 读取数据，放到R_Data中
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount 字段数
     * @return 字符串
     * @throws Exception
     */
    public String Put_Data_NOKey(int c_SRow, int c_SCol, String c_SQL, String c_DSN, int c_FldCount)
            throws Exception
    {
//        String t_DSN;
        SSRS t_Rs = new SSRS();
        int t_Row, t_Col;
        int t_FldCount;
        int t_Sheet;
        String t_Str;
        String returnvalue = "";

        t_Row = c_SRow;
        t_Col = c_SCol; //填写数据的开始行列

        ExeSQL tExeSQL = new ExeSQL();
        t_Rs = tExeSQL.execSQL(c_SQL);

        if (m_DataBaseType != 0)
        {
            t_FldCount = t_Rs.getMaxRow();
        }
        else
        {
            t_FldCount = 1;
        }
        m_DataBaseType = 0; //恢复其初始值，因其为全局变量，对其的赋值操作将影响到后来的操作
        if (c_FldCount > 1)
        {
            t_FldCount = c_FldCount;
        }
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);

        if (t_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1 + 1;
        }
        if (t_Col == -1)
        {
            t_Col = m_FV.getLastCol() + 1 + 1;
        }

        for (int rowcount = 0; rowcount < t_Rs.getMaxRow(); rowcount++)
        {
            for (int i = 1; i <= t_FldCount; i++)
            {
                //DEBUG_OUT( " Put_Data_NOKey ====== 01");
                //DEBUG_OUT( " i= " + i + " rowcount= " +rowcount);
                t_Str = JRptUtility.ChgValue(t_Rs.GetText(rowcount + 1, i));
                m_FV.setText(t_Row - 1, t_Col + i - 1 - 1, t_Str);
                if (m_Function_Type.equalsIgnoreCase("1"))
                {
                    m_Function_Type = "0";
                    return t_Str;
                }
            }
            t_Row += 1;
        }
        m_FV.setSheet(t_Sheet);
        return returnvalue;
    }

    /**
     * 解释行函数,如RowSum("2,3:4")表示将2，3列求和，放到第4列
     * @param cArea 区域
     * @param c_FunctionScript 函数
     * @param cExeType 执行类型
     * @throws Exception
     */
    public void Put_Row_Col_FunctionData(String cArea, String c_FunctionScript, String cExeType)
            throws Exception
    {
        int t_RowCount;
//        String t_Return, t_Str1;
        String t_Str;
        String tSQL;
        int t_Sheet;
        String[] tPos;
        int tMaxRow, tMaxCol;
        int tMinRow, tMinCol;
//        BookModelImpl  t_F1v;

        tPos = cArea.split(":"); //分割字符串

        t_Sheet = m_FV.getSheet();
//        m_FV.setSheet(R_Define);
        m_FV.setSheet(R_Data);
        t_RowCount = m_FV.getLastRow() + 1;

        if (!tPos[0].toLowerCase().trim().equalsIgnoreCase("x"))
        {
            this.m_Data_Row = Integer.parseInt(tPos[0]);
        }
        else
        {
            DEBUG_OUT("tPos[0]:" + tPos[0]);
        }

        if (!tPos[1].toLowerCase().trim().equalsIgnoreCase("x"))
        {
            this.m_Data_Col = Integer.parseInt(tPos[1]);
        }

        tMinRow = this.m_Data_Row;
        tMinCol = this.m_Data_Col;

        if (tPos[2].trim().equalsIgnoreCase("-1"))
        {
            tMaxRow = m_FV.getLastRow() + 1;
//            tMaxRow = t_RowCount;
        }
        else if (!tPos[2].toLowerCase().trim().equalsIgnoreCase("x"))
        {
            tMaxRow = Integer.parseInt(tPos[2]);
        }
        else
        {
            tMaxRow = m_FV.getLastRow() + 1;
        }

        if (tPos[3].trim().equalsIgnoreCase("-1"))
        {
            tMaxCol = m_FV.getLastCol() + 1;
        }
        else if (!tPos[3].toLowerCase().trim().equalsIgnoreCase("x"))
        {
            tMaxCol = Integer.parseInt(tPos[3]);
        }
        else
        {
            tMaxCol = m_FV.getLastCol() + 1;
        }

        /**********************************************************************/
        ////////////////////数据库操作///////////////////////
        /***********************************************************************/

        SSRS t_Rs = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();

        if (cExeType.toLowerCase().equalsIgnoreCase("rtof"))
        {
            for (int i = tMinRow; i <= tMaxRow; i++)
            {
                this.m_Data_Row = i;

                for (int j = tMinCol; j <= tMaxCol; j++)
                {
                    this.m_Data_Col = j;
                    //tSQL = Replace_SysVal_InString(c_FunctionScript, 0);
                    tSQL = ReplaceValSQL(c_FunctionScript, m_Vals);
                    tSQL = ReplaceFunc1(tSQL.toString());

                    t_Rs = tExeSQL.execSQL(tSQL);
                    t_Str = JRptUtility.ChgValue(t_Rs.GetText(1, 1));
                    m_FV.setText(i - 1, j - 1, t_Str);
                }
            }
        }
        else
        {
            for (int j = tMinCol; j <= tMaxCol; j++)
            {
                this.m_Data_Col = j;
                for (int i = tMinRow; i <= tMaxRow; i++)
                {
                    this.m_Data_Row = i;
                    tSQL = ReplaceValSQL(c_FunctionScript, m_Vals);
                    tSQL = ReplaceFunc1(tSQL);
                    t_Rs = tExeSQL.execSQL(tSQL);
                    t_Str = JRptUtility.ChgValue(t_Rs.GetText(1, 1));
                    //m_FV.setFormula(i, J, t_Str);
                    m_FV.setText(i - 1, j - 1, t_Str);
                }
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 横向扩展的放数据,读取数据，放到R_Data中
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount 字段数
     * @return 字符串
     * @throws Exception
     */
    public String Put_Row_Data_NOKey(int c_SRow, int c_SCol, String c_SQL, String c_DSN
            , int c_FldCount)
            throws Exception
    {
        SSRS t_Rs = new SSRS();
        int t_Row, t_Col;
        int t_FldCount;
        int t_Sheet;
        String t_Str;
        String returnvalue = "";

        t_Row = c_SRow;
        t_Col = c_SCol; //填写数据的开始行列

        ExeSQL tExeSQL = new ExeSQL();
        t_Rs = tExeSQL.execSQL(c_SQL);

        if (m_DataBaseType != 0)
        {
            t_FldCount = t_Rs.getMaxCol();
        }
        else
        {
            t_FldCount = 1;
        }
        m_DataBaseType = 0; //恢复其初始值，因其为全局变量，对其的赋值操作将影响到后来的操作
        if (c_FldCount > 1)
        {
            t_FldCount = c_FldCount;
        }

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);

        if (t_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1 + 1;
        }
        if (t_Col == -1)
        {
            t_Col = m_FV.getLastCol() + 1 + 1;
        }

        for (int rcount = 0; rcount < t_Rs.getMaxRow(); rcount++)
        {

            for (int i = 1; i <= t_FldCount; i++)
            {
                t_Str = JRptUtility.ChgValue(t_Rs.GetText(rcount + 1, i));

                //if(JRptUtility.IsNumeric(t_Str)){//去掉字符串和数字型的判断，任何时候都认为是字符串
                //  m_FV.setNumber(t_Row, t_Col + i - 1,Double.parseDouble(t_Str));
                // }else{
                m_FV.setText(t_Row + i - 1 - 1, t_Col - 1, t_Str);
                // }
                if (m_Function_Type.equalsIgnoreCase("1"))
                {
                    m_Function_Type = "0";
                    return t_Str;
                }
            }
            t_Col += 1;
        }
        m_FV.setSheet(t_Sheet);
        return returnvalue;
    }

    /**
     * 横向扩展成一行数据
     * @param c_SRow 源行
     * @param c_SCol 源列
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount 字段数
     * @return 字符串
     * @throws Exception
     */
    public String Put_Row_Data_NOKey1(int c_SRow, int c_SCol, String c_SQL, String c_DSN
            , int c_FldCount)
            throws Exception
    {
        SSRS t_Rs = new SSRS();
        int t_Row, t_Col;
        int t_FldCount;
        int t_Sheet;
        String t_Str;
        String returnvalue = "";

        t_Row = c_SRow;
        t_Col = c_SCol; //填写数据的开始行列

        ExeSQL tExeSQL = new ExeSQL();
        t_Rs = tExeSQL.execSQL(c_SQL);

        if (m_DataBaseType != 0)
        {
            t_FldCount = t_Rs.getMaxCol() + 1;
        }
        else
        {
            t_FldCount = 1;
        }
        m_DataBaseType = 0; //恢复其初始值，因其为全局变量，对其的赋值操作将影响到后来的操作
        if (c_FldCount > 1)
        {
            t_FldCount = c_FldCount;
        }

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);

        if (t_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1 + 1;
        }
        if (t_Col == -1)
        {
            t_Col = m_FV.getLastCol() + 1 + 1;
        }

        for (int rcount = 0; rcount < t_Rs.getMaxRow(); rcount++)
        {

            for (int i = 1; i <= t_FldCount; i++)
            {
                t_Str = JRptUtility.ChgValue(t_Rs.GetText(rcount + 1, i));

                // if(JRptUtility.IsNumeric(t_Str)){
                //m_FV.setNumber(t_Row, t_Col + i - 1 ,Double.parseDouble(t_Str));
                //}else{
                m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                // }
                //if( m_Function_Type.equalsIgnoreCase("1")){
                //m_Function_Type = "0";
                //returnvalue = t_Str;
                //}
                t_Col += 1;
            }
            // t_Col  += 1;

        }
        m_FV.setSheet(t_Sheet);
        return returnvalue;
    }

    /**
     * 解释行函数,如RowSum("2,3:4")表示将2，3列求和，放到第4列
     * @param c_FunctionScript 函数参数
     * @throws Exception
     */
    public void Put_Row_FunctionData(String c_FunctionScript)
            throws Exception
    {
        int t_RowCount;
//        StringBuffer t_Return;
        String t_Str, t_Str1;
        int i, t_Sheet;
        int t_Col;
        String[] t_Data;
        int t_Next_Line;
        BookModelImpl t_F1v = new BookModelImpl();
        String[] t_Group;
        String[] t_ALine;
        boolean t_EnableSql = false;

        t_Str = JRptUtility.Get_Str(c_FunctionScript, 2, "(%"); //得到函数的参数
        t_Str = t_Str.substring(0, t_Str.length() - 2); //返回除最后2个字符的截取串
        t_Str1 = JRptUtility.Get_Str(c_FunctionScript, 1, "(%"); //得到函数名

//        BookModelImpl  t_Flv;
        t_Data = t_Str.split(":"); //分割字符串

        t_Col = Integer.parseInt(t_Data[1]);
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_RowCount = m_FV.getLastRow() + 1;
        t_Next_Line = 1;

        for (i = conDataStart; i <= t_RowCount; i++)
        {
            t_Str = Execute_Row_Function(t_Str1, t_Data[0], i);

            if (t_Str.substring(0, 1).equalsIgnoreCase("="))
            {
                m_FV.setFormula(i - 1, t_Col - 1, t_Str.substring(1));
            }
            else
            {
                if (t_Str.indexOf("^$") < 0)
                {
                    m_FV.setText(i - 1, t_Col - 1, t_Str);
                }
                else
                {
                    t_EnableSql = true;

                    t_Group = t_Str.split("\\^~"); //分割字符串，以^~为间隔

                    for (int m = 0; m < t_Group.length; m++)
                    {
                        if (!t_Group[m].equalsIgnoreCase(""))
                        {
                            for (int j = 0; j < m_FV.getLastCol() + 1; j++)
                            {
                                t_F1v.setText(t_Next_Line - 1, j - 1, m_FV.getText(i - 1, j - 1));
                            }

                            t_ALine = t_Group[m].split("\\^\\$"); //分割字符串，以^~为间隔

                            for (int j = 0; j < t_ALine.length; j++)
                            {
                                t_F1v.setText(t_Next_Line - 1, t_Col + j - 1, t_ALine[j]);
                            }
                            t_Next_Line += 1;
                        } //end if
                    } //end for
                } //end else
            } //end else
        } //end for

        if (t_EnableSql)
        {
            m_FV.copyRange(0, 0, t_F1v.getLastRow(), t_F1v.getLastCol(), t_F1v, 0, 0
                    , t_F1v.getLastRow(), t_F1v.getLastCol());
            //'m_FV.WriteEx "c:\test12.vts", F1FileFormulaOne6
        }

        m_FV.setSheet(t_Sheet);

    }

    /**
     * 解释合计函数,如SUM_H(3:4),表示对第三列进行合计，放在第4列
     * SUM_H(3),表示对第三列进行合计，放在第3列
     * @param c_Row 行
     * @param c_Text 值
     * @throws Exception
     */
    public void Put_Sum_H(int c_Row, String c_Text)
            throws Exception
    {
        String[] t_Data;
        int i, t_RowCount, t_Sheet;
        String t_Str;
        String t_ColStr = "";
        int t_ComputeCol;
        double t_Sum;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_Str = c_Text;

        t_Data = t_Str.split(" "); //分割字符串,按空格分割

        if (t_Data.length > 1)
        {
            t_ColStr = t_Data[1];
        }
        t_ComputeCol = Integer.parseInt(t_Data[0]);
        t_RowCount = m_FV.getLastRow() + 1;
        t_Sum = 0;

        for (i = conDataStart; i <= t_RowCount; i++)
        {
            t_Str = m_FV.getText(i - 1, t_ComputeCol - 1);
            if (JRptUtility.IsNumeric(t_Str))
            {
                t_Sum += Double.parseDouble(t_Str);
            }
        }

        StringBuffer bf = new StringBuffer();
        bf.append(m_SumH_Caption);
        bf.append(t_Sum);
        if (t_ColStr.equalsIgnoreCase(""))
        {
            m_FV.setText(t_RowCount + 1 - 1, t_ComputeCol - 1, bf.toString());
        }
        else
        {
            m_FV.setText(t_RowCount + 1 - 1, Integer.parseInt(t_ColStr) - 1, bf.toString());
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 解释小计函数,如SUM_X(1,2:3:R5)表示按照1，2列分组进行小计，小计放在第5列的右边
     * SUM_X(1,2:3,B)则表示小计放在统计列的下面（即插入一行）
     * @param c_Row 行
     * @param c_Text 值
     * @throws Exception
     */
    public void Put_Sum_X(int c_Row, String c_Text)
            throws Exception
    {
//        int t_CurRow;
        int t_RowCount, t_Sheet;
        double t_Return = 0.0;
        int i, t_GroupColCount, j;
        String t_Str, t_Group, t_Str1;
        String[] t_Temp;
        String[] t_Data;
        int t_ComputeCol;
        long[] t_GroupCol;
        String t_Type;

        t_Data = c_Text.split(":"); //分割字符串,按":"分割

        t_Type = t_Data[2];
        t_ComputeCol = Integer.parseInt(t_Data[1]);

        t_Temp = t_Data[0].split(","); //分割字符串,按","分割

        j = t_Temp.length - 1;
        t_GroupCol = new long[j + 1];
        for (i = 0; i <= j; i++)
        {
            t_GroupCol[i] = Long.parseLong(t_Temp[i]);
        }

        t_GroupColCount = j;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_RowCount = m_FV.getLastRow() + 1;
        t_Group = "";
        i = conDataStart;
        while (i <= t_RowCount)
        {
            t_Str = "";
            StringBuffer strbuff = new StringBuffer();
            strbuff.append(t_Str);
            for (j = 0; j <= t_GroupColCount; j++)
            {
                strbuff.append(":");
                strbuff.append(m_FV.getText(i - 1, (int) t_GroupCol[j] - 1).toUpperCase().trim());
            }
            t_Str = strbuff.toString();

            t_Str1 = m_FV.getText(i - 1, t_ComputeCol - 1);
            if (t_Group.equalsIgnoreCase(""))
            {
                //如果是第一个小计列
                t_Group = t_Str;
                if (JRptUtility.IsNumeric(t_Str1))
                {
                    t_Return = Double.parseDouble(t_Str1);
                }
                else
                {
                    t_Return = 0.0;
                }
            }
            else
            {
                if (t_Group.equalsIgnoreCase(t_Str))
                {
                    //如果小计列相等，则继续小计
                    if (JRptUtility.IsNumeric(t_Str1))
                    {
                        t_Return += Double.parseDouble(t_Str1);
                    }

                    //如果需要删除从复分组名
                    for (j = 0; j <= t_GroupColCount; j++)
                    {
                        //m_FV.deleteRange( i, (int)t_GroupCol[j], i,(int)t_GroupCol[j], F1FixupAppend);
                        m_FV.clearRange(i - 1, (int) t_GroupCol[j] - 1, i - 1
                                , (int) t_GroupCol[j] - 1, Constants.eClearValues);
                    }

                }
                else
                {
                    //如果不相等，则表示这里需要增加一个小计
                    Write_Sum_X_To_Data(i - 1, t_ComputeCol, t_Return, t_Type, (int) t_GroupCol[0]);

                    if ((m_FV.getLastRow() + 1) != t_RowCount)
                    {
                        //如果是在当前列中插入一行，则数据也向下走一行
                        t_RowCount += 1;
                        i += 1;
                    }

                    if (t_Type.equalsIgnoreCase("BN"))
                    {
                        t_Str = "";
                    }
                    //使小计从新开始
                    t_Group = t_Str;
                    if (JRptUtility.IsNumeric(t_Str1))
                    {
                        t_Return = Double.parseDouble(t_Str1);
                    }
                    else
                    {
                        if ((m_FV.getLastRow() + 1) == i && t_Type.equalsIgnoreCase("BN"))
                        {
                            //如果是在当前列中插入一行，则数据也乡下走一行
                            i += 1;
                        }
                        else
                        {
                            t_Return = 0;
                        }
                    }
                }
            }
            i += 1;
        } //end while

        if (t_Type.equalsIgnoreCase("BN"))
        {
            Write_Sum_X_To_Data(m_FV.getLastRow() + 1 - 1, t_ComputeCol, t_Return, t_Type
                    , (int) t_GroupCol[0]);
        }
        else
        {
            Write_Sum_X_To_Data(m_FV.getLastRow() + 1, t_ComputeCol, t_Return, t_Type
                    , (int) t_GroupCol[0]);
        }

        m_FV.setSheet(t_Sheet);
    }

    /**
     * 将报表进行拼接
     * @throws Exception
     */
    public void Raise_Report()
            throws Exception
    {
        int t_Data_Count, t_Cur_Data_Row;
        int t_P_End_Count, t_P_Head_Count, t_Page_Data_Count;
        int t_R_Head_Count, t_R_End_Count;
        int t_CurRow; //报表格式的当前的行
        int t_Sheet, t_DataColCount, T1;
        CRage t_Rage1 = new CRage();
        CRage t_Rage2 = new CRage();

        //初始化变量
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_Data_Count = m_FV.getLastRow() + 1;
        t_DataColCount = m_FV.getLastCol() + 1;
        System.out.println("数据行数" + t_Data_Count);

        t_Cur_Data_Row = conDataStart;
        m_FV.setSheet(P_End);
        t_P_End_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(P_Head);
        t_P_Head_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_Head);
        t_R_Head_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_End);
        t_R_End_Count = m_FV.getLastRow() + 1;
        //End初始化变量

        Copy_Report_Head("ToReport", 1, 1);
        t_CurRow = t_R_Head_Count + 1;
        mCurPages = 1;

        if (m_Per_Page_Count <= 0)
        {
            //如果不需要分页，则直接拷贝数据
            //拷贝页头
            Copy_Page_Head("ToReport", t_CurRow, 1);
            t_CurRow += t_P_Head_Count;
            //End拷贝页头

            //拷贝数据
            t_Rage1.m_Top = 1;
            t_Rage1.m_Left = 1;
            t_Rage1.m_Buttom = t_Data_Count;
            t_Rage1.m_Right = t_DataColCount;
            System.out.println("数据行数" + t_Data_Count);

            t_Rage2.m_Left = 1;
            t_Rage2.m_Top = t_CurRow;
            Copy_Sheet_Data(t_Rage1, m_FV, R_Data, t_Rage2, m_FV, R_Report, 1);
            m_FV.setSheet(R_Report);
            t_CurRow += m_FV.getLastRow() + 1 + 1;
            //End拷贝数据
            //拷贝页尾
            Copy_Page_End("ToReport", t_CurRow, 1);
            t_CurRow += t_P_End_Count;
            //End拷贝尾头
            //拷贝报表尾
            Copy_Report_End("ToReport", t_CurRow, 1);
            //End拷贝报表尾

        }
        else
        { //否则，添加页头，页尾
            while (true)
            {
                //拷贝页头
                Copy_Page_Head("ToReport", t_CurRow, 1);
                t_CurRow += t_P_Head_Count;
                //End拷贝页头

                T1 = t_Data_Count - t_Cur_Data_Row + 1;
                if (t_Cur_Data_Row == conDataStart)
                {
                    t_Page_Data_Count = m_Per_Page_Count - t_R_Head_Count - t_P_Head_Count
                            - t_P_End_Count;
                }
                else
                {
                    t_Page_Data_Count = m_Per_Page_Count - t_P_Head_Count - t_P_End_Count;
                }

                if (t_Page_Data_Count > T1)
                {
                    t_Page_Data_Count = T1;
                }
                //WriteEx "c:\t1.vts", F1FileFormulaOne6
                //拷贝数据
                t_Rage1.m_Top = t_Cur_Data_Row;
                t_Rage1.m_Left = 1;
                t_Rage1.m_Buttom = t_Cur_Data_Row + t_Page_Data_Count - 1;
                t_Rage1.m_Right = t_DataColCount;
                t_Rage2.m_Left = 1;
                t_Rage2.m_Top = t_CurRow;
                Copy_Sheet_Data(t_Rage1, m_FV, R_Data, t_Rage2, m_FV, R_Report,
                        1);
                //End拷贝数据
                //WriteEx "c:\t2.vts", F1FileFormulaOne6
                t_CurRow += t_Page_Data_Count;

                //拷贝页尾
                Copy_Page_End("ToReport", t_CurRow, 1);
                t_CurRow += t_P_End_Count;
                //End拷贝页尾
                //WriteEx "c:\t3.vts", F1FileFormulaOne6
                if (t_Data_Count - t_Cur_Data_Row + 1 <= t_Page_Data_Count)
                {
                    //拷贝报表尾
                    Copy_Report_End("ToReport", t_CurRow, 1);
                    //End拷贝报表尾
                    break; //exit while
                }

                //加分页标志
                m_FV.addRowPageBreak(t_CurRow);
                //End加分页标志
                //WriteEx "c:\t4.vts", F1FileFormulaOne6
                t_Cur_Data_Row += t_Page_Data_Count;
                mCurPages += 1;
            } //end while

        } //end if
        t_Rage1 = null;
        t_Rage2 = null;
        m_FV.setSheet(t_Sheet);

        Set_Row_Col_Text_At_Report_End();
    }

    static void DEBUG_OUT(String sMsg)
    {
        //System.err.println(sMsg);
        System.out.println(sMsg);
    }

    /**
     * 从数据区开始读区数据
     * @throws Exception
     */
    public void Read_Data()
            throws Exception
    {

        /***************************从数据区开始读区数据*****************************/
        int t_Cur_Row, t_Col;
        String t_Sql, t_DSN, t_Key;
        int t_Row, t_FldCount;
        String t_Str;
        int t_Sheet, t_Row_Count;
        String t_TempS1; //取x:x,x:n,n:x,下同
        String t_TempS2;
        String t_T8; //是否嵌入

        m_ErrString = "";
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);
        t_Cur_Row = m_Cur_Format_Row;
        t_Row_Count = m_FV.getLastRow() + 1;

        /*****************处理COL_DATA这样的数据***************/

        DEBUG_OUT("开始处理COL_DATA数据。。。");
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            if (t_Str == null)
            {
                break;
            }
            if (t_Str.length() < 1)
            {
                break;
            }

            if (!t_Str.substring(0, 1).equalsIgnoreCase("#"))
            {
                if (!t_Str.equalsIgnoreCase("COL_DATA"))
                {
                    break; //exit while
                }

                t_Col = Integer.parseInt(m_FV.getText(t_Cur_Row - 1, 1));
                StringBuffer bf = new StringBuffer();
                bf.append(m_FV.getText(t_Cur_Row - 1, 2).trim());
                bf.append(m_FV.getText(t_Cur_Row - 1, 3).trim());
                t_Sql = bf.toString();

                if (t_Sql.substring(0, 1).equalsIgnoreCase("!") && t_Sql.endsWith("!"))
                {
                    t_Sql = ReplaceFunc(t_Sql);
                }
                else
                {
                    StringBuffer bf1 = new StringBuffer();
                    bf1.append(m_FV.getText(t_Cur_Row - 1, 2).trim());
                    bf1.append(m_FV.getText(t_Cur_Row - 1, 3).trim());

                    t_Sql = ReplaceValSQL(bf1.toString(), m_Vals);

                    //此处需要修改，处理变量的替换
                }
                //替换函数变量
                t_Sql = ReplaceFunc1(t_Sql);

                t_DSN = "";
                //m_FV.getText(t_Cur_Row,4);
                t_Key = m_FV.getText(t_Cur_Row - 1, 5);
                t_Str = m_FV.getText(t_Cur_Row - 1, 6);
                t_T8 = m_FV.getText(t_Cur_Row - 1, 7);

                if (!(t_Str.equalsIgnoreCase("")) && JRptUtility.IsNumeric(t_Str))
                {
                    t_FldCount = Integer.parseInt(t_Str);
                }
                else
                {
                    t_FldCount = 1;
                }

                Put_Data(conDataStart, t_Col, t_Sql, t_DSN, t_Key, t_FldCount, t_T8);
            } //end if
            t_Cur_Row += 1;
            DEBUG_OUT("结束处理COL_DATA数据。。。");
        } //end while
        /*********************End处理COL_DATA这样的数据***********************/

        /*****************处理ROW_COL_DATA这样的数据*********************/
        DEBUG_OUT("开始处理ROW_COL_DATA这样的数据。。。");
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
//            System.out.println(t_Str + "-----");

            if (t_Str == null)
            {
                break;
            }
            if (t_Str.length() < 1)
            {
                break;
            }

            if (!t_Str.substring(0, 1).equalsIgnoreCase("#"))
            {
                if (!t_Str.equalsIgnoreCase("ROW_COL_DATA"))
                {
                    break; //exit while
                }

                t_TempS1 = JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 1, ":");
                t_TempS2 = JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 2, ":");

                if (JRptUtility.IsNumeric(t_TempS1))
                {
                    //预处理x:x,x:n,n:x之类
                    t_Row = Integer.parseInt(t_TempS1);
                }
                else
                {
                    //t_Row= -1;
                    m_FV.setSheet(R_Data);
                    t_Row = m_FV.getLastRow() + 1 + 1;
                    m_FV.setSheet(R_Define);
                }

                if (JRptUtility.IsNumeric(t_TempS2))
                {
                    t_Col = Integer.parseInt(t_TempS2);
                }
                else
                {
                    //t_Col= -1;
                    m_FV.setSheet(R_Data);
                    t_Col = m_FV.getLastCol() + 1 + 1;
                    m_FV.setSheet(R_Define);
                }
                this.m_Data_Row = t_Row;
                this.m_Data_Col = t_Col;
                StringBuffer bf = new StringBuffer();
                bf.append(m_FV.getText(t_Cur_Row - 1, 2).trim());
                bf.append(m_FV.getText(t_Cur_Row - 1, 3).trim());
                t_Sql = bf.toString();

                if (t_Sql.substring(0, 1).equalsIgnoreCase("!") && t_Sql.endsWith("!"))
                {
                    t_Sql = ReplaceFunc(t_Sql);
                }
                else
                {
                    t_Sql = ReplaceValSQL(t_Sql, m_Vals);
                }
                //替换函数变量
                t_Sql = ReplaceFunc1(t_Sql);

                //此处需要修改，处理变量的替换
                t_DSN = ""; //m_FV.getText(t_Cur_Row, 4);
                t_Key = m_FV.getText(t_Cur_Row - 1, 5);
                t_Str = m_FV.getText(t_Cur_Row - 1, 6);
                t_T8 = m_FV.getText(t_Cur_Row - 1, 7);
                if (!t_Str.equalsIgnoreCase(""))
                {
                    t_FldCount = Integer.parseInt(t_Str);
                }
                else
                {
                    t_FldCount = 1;
                }

                Put_Data(t_Row, t_Col, t_Sql, t_DSN, t_Key, t_FldCount, t_T8);
            } //end if
            t_Cur_Row += 1;
            DEBUG_OUT("结束处理ROW_COL_DATA数据。。。");

        } //end while

        /*********************End处理ROW_COL_DATA这样的数据********************/

        /**************处理Row_COL_Text这样的数据*************************/
        DEBUG_OUT("开始处理Row_COL_Text这样的数据..........");
        //对于Row_Col_Text这样的数据，直接放在R_Report这个Sheet中
        m_FV.setSheet(R_Define);
        m_Row_Col_Text_Start = t_Cur_Row;
        //if(m_Row_Col_Text_Type.substring(0,1).equalsIgnoreCase("1")){
        //如果需要在数据区显示Row_Col_Text
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            if (t_Str == null)
            {
                break;
            }
            if (t_Str.length() < 1)
            {
                break;
            }
            if (!t_Str.substring(0, 1).equalsIgnoreCase("#"))
            {

                if (!t_Str.equalsIgnoreCase("ROW_COL_TEXT"))
                {
                    break; //exit while
                }
                t_Row = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 1, ":"));
                t_Col = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 2, ":"));
                t_Sql = m_FV.getText(t_Cur_Row - 1, 2).trim();

                if (t_Sql.substring(0, 1).equalsIgnoreCase("!") && t_Sql.endsWith("!"))
                {
                    t_Sql = JRptUtility.Get_Str(t_Sql, 2, "!");
                    //替换函数变量
                    t_Sql = ReplaceValSQL(t_Sql, m_Vals);
                    //数据库操作
                    t_Str = JRptUtility.GetOneValueBySQL(t_Sql);

                }
                else
                {
                    t_Str = ReplaceValSQL(m_FV.getText(t_Cur_Row - 1, 2).trim(), m_Vals);

                }
                if (!t_Str.trim().equalsIgnoreCase(""))
                {
                    String t_Str1; //得到第4列的值
                    String t_Type, t_Type1;
                    t_Str1 = m_FV.getText(t_Cur_Row - 1, 3);
                    t_Type = "";

                    if (!t_Str1.equalsIgnoreCase(""))
                    {
                        t_Type = t_Str1.substring(0, 1);
                        t_Type1 = t_Str1.substring(1, 2);
                    }
                    else
                    {
                        t_Type = m_Row_Col_Text_Type.substring(0, 1);
                        t_Type1 = m_Row_Col_Text_Type.substring(1, 2);
                    }
                    if (t_Type.equalsIgnoreCase("1"))
                    {
                        //如果需要在数据区显示Row_Col_Text
                        if (!t_Str.trim().equalsIgnoreCase(""))
                        {
                            m_FV.setSheet(R_Data);
                            m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                            m_FV.setSheet(R_Define);
                        }
                    }
                    if (t_Type.equalsIgnoreCase("1"))
                    {
                        //如果需要在报表拼接区显示Row_Col_Text
                        if (!t_Str.trim().equalsIgnoreCase(""))
                        {
                            m_FV.setSheet(R_Report);
                            m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                            m_FV.setSheet(R_Define);
                        }
                    }
                }
            }
            t_Cur_Row += 1;
            DEBUG_OUT("结束处理Row_COL_TEXT数据..........");

        } //end while
        //}end if
        /**************End处理Row_COL_Text这样的数据*********************/

        m_Cur_Format_Row = t_Cur_Row;
        m_FV.setSheet(t_Sheet);

    }

    /**
     * 读取格式到内存中的m_FV中
     * @param c_FileName 文件名
     * @return int型
     * @throws Exception
     */
    public int Read_Format(String c_FileName)
            throws Exception
    {
        String t_Str;
        m_ErrString = "";
//        m_FV.setSheet(R_Format);
        File f = new File(c_FileName);
        if (!f.exists())
        {
            m_ErrString = "模版文件不存在,请您检查文件" + c_FileName + "是否在存在！";
            return 0;
        }

//        m_FV.setSheet(R_Format);
        m_FV.read(c_FileName);
        System.out.println("读入数据摸班后的sheet个数" + m_FV.getNumSheets());
        InitSheets();
        m_FV.setSheet(R_Define);

        //能在数据源中增加变量的替换操作
        // m_ConnectString = ReplaceValSQL(m_ConnectString, m_Vals);

        Copy_Report_Head("", 1, 1);
        Copy_Report_End("", 1, 1);
        Copy_Page_Head("", 1, 1);
        Copy_Page_End("", 1, 1);

        t_Str = m_FV.getText(Row_Count_Per_Page - 1, 1).trim();

        if (!t_Str.equalsIgnoreCase("") && JRptUtility.IsNumeric(t_Str))
        {
            m_Per_Page_Count = Integer.parseInt(m_FV.getText(
                    Row_Count_Per_Page - 1, 1)); //得到每页的行数
        }
        else
        {
            m_Per_Page_Count = 0;
        }
        for (int i = 1; i <= ConCols; i++)
        {
            if (m_FV.getText(Row_Visible - 1,
                    i - 1).trim().equalsIgnoreCase("1"))
            {
                m_Visible.setElementAt(new Boolean(false), i);
            }
            else
            {
                m_Visible.setElementAt(new Boolean(true), i);
            }
        }
        t_Str = m_FV.getText(Row_Data_Row - 1, 1);
        if (JRptUtility.IsNumeric(t_Str))
        {
            m_DataFormatRow = Integer.parseInt(t_Str);
        }
        else
        {
            m_ErrString = "读格式时发生错误:错误的数据行标示";
            DEBUG_OUT("读格式时发生错误:错误的数据行标示");
            return 0;
        }
        m_Cur_Format_Row = Row_Data_Start;
        return m_Cur_Format_Row; //此处是返回这个值
    }

    /**
     * 代替所有在c_Str中的系统变量(从系统的m_Vars 或系统自定义时间变量)
     * @param c_Str 传入字符串
     * @param c_Index 编号
     * @return 字符串
     */
    private String Replace_SysVal_InString(String c_Str, int c_Index)
    {
        String t_Str, t_Return = "";
        String t_ValName;
//        String t_ValValue;
        int i = 2;

        t_Str = c_Str.trim();
        t_Return = t_Str;

        //循环查找在 t_Str 中以 "?XXXX?"格式存在的变量
        while (true)
        {
            //得到第 i step 2 个变量的名称
            //变量以  "?AAA?"形式存在字符串中
            //该函数得到c_Str中的第c_i个以c_Split分割的字符串
            t_ValName = JRptUtility.Get_Str(t_Str, i, "?");

            //如果没找到,说明已经查完,则退出
            if (t_ValName.trim().equalsIgnoreCase(""))
            {
                break;
            }

            //如果已经查到,则替换相应的变量
            t_Return = StrTool.replace(StrTool.unicodeToGBK(t_Return)
                    , StrTool.unicodeToGBK("?" + t_ValName + "?")
                    , StrTool.unicodeToGBK(Get_Sys_Val_Value(t_ValName, c_Index)));

            String ss = Get_Sys_Val_Value(t_ValName, c_Index);

            //查找下一个位置
            i += 2;
        }
        return t_Return;
    }

    /**
     * 本函数的功能是替换形如?row:col?的变量
     * 此时已经从数据库里取出数据存在指定的sheet,用row:col指定其值，并返回
     * @param c_SQL SQL
     * @return 字符串
     * @throws Exception
     */
    private String ReplaceCellVal(String c_SQL)
            throws Exception
    {
        int t_Currentsheet;
        String t_Val;
        String[] t_Row_Col;
        String t_Value;

        t_Currentsheet = m_FV.getSheet();
        mReplaceCellValue_Numm = false;

        //取得第一个?XXX? 分割的字符串
        t_Val = JRptUtility.Get_Str(c_SQL, 2, "?").trim();

        //如果形如 ?XXX:XXX?
        if (t_Val.indexOf(":") > 0)
        {
            m_FV.setSheet(R_Data);

            //循环处理
            while (!t_Val.equalsIgnoreCase(""))
            {
                //进行分割坐标
                t_Row_Col = t_Val.split(":");

                if (t_Row_Col.length < 1)
                {
                    //不是row:col的形式
                    m_FV.setSheet(t_Currentsheet);
                    return c_SQL;
                }
                else
                {
                    //如果是 "currow"
                    if (t_Row_Col[0].toLowerCase().equalsIgnoreCase("currow"))
                    {
                        t_Row_Col[0] = String.valueOf(this.m_Data_Row);
                    }

                    //如果是 "curcol"
                    if (t_Row_Col[1].toLowerCase().equalsIgnoreCase("curcol"))
                    {
                        t_Row_Col[1] = String.valueOf(this.m_Data_Col);
                    }

                    //取指定坐标的值
                    t_Value = m_FV.getText(Integer.parseInt(t_Row_Col[0]) - 1
                            , Integer.parseInt(t_Row_Col[1]) - 1);

                    //如果取到的值为空,设置标志
                    if (t_Value.trim().equalsIgnoreCase(""))
                    {
                        mReplaceCellValue_Numm = true;
                    }

                    //把取到的值替换掉相应的变量

                    c_SQL = StrTool.replace(c_SQL, "?" + t_Val + "?", t_Value);
                    //取下一个 ?XXXX? 字符串
                    t_Val = "";
                    t_Val = JRptUtility.Get_Str(c_SQL, 2, "?").trim();

                } //end if
            } //end while
        } //end if

        m_FV.setSheet(t_Currentsheet);
        return c_SQL;
    }

    /**
     * 函数功能是把一个在"system_define.vts"中定义的函数，
     * 用具体的参数替换其定义的sql语句，然后返回
     * @param c_Func 函数
     * @return 字符串
     * @throws Exception
     */
    private String ReplaceFunc(String c_Func)
            throws Exception
    {
        String[] t_Val = null;
        String t_FuncName;
        BookModelImpl t_F1Val;
        String t_Sql = "";
        String[] t_f1;
        String t_Temp;

        t_FuncName = JRptUtility.Get_Str(c_Func, 2, "!").trim().toUpperCase(); //得到第i/2个变量的名称

        if (!t_FuncName.equalsIgnoreCase(""))
        {
            t_f1 = t_FuncName.split("[(]");
            t_FuncName = t_f1[0]; //得到函数名
            t_Temp = t_f1[1].substring(0, t_f1.length - 1);
            t_Val = t_Temp.split(","); //得到具体参数值并存入t_Val数组
        }

        File f = new File(getConfigFilePath() + "system_define.vts");
        if (f.exists())
        {
            t_F1Val = new BookModelImpl();
            t_F1Val.read(f.getName());
            t_F1Val.setSheet(1);

            for (int i = 1; i <= t_F1Val.getLastRow() + 1; i++)
            {
                if (t_FuncName.equalsIgnoreCase(t_F1Val.getText(i - 1, 0)))
                {
                    t_Sql = t_F1Val.getText(i - 1, 2);
                    m_Function_Type = t_F1Val.getText(i - 1, 1); //取得函数类型，在执行sql语句时用到
                    break;
                }
            }

            if (!t_Sql.equalsIgnoreCase(""))
            {
                //判断是否有参数
                for (int i = 0; i < t_Val.length; i++)
                {
                    if (!t_Val[i].equalsIgnoreCase(""))
                    {
                        t_Sql = StrTool.replace(t_Sql, "$" + String.valueOf(i + 1), t_Val[i]);
                    }
                    else
                    {
                        AddVar("$" + String.valueOf(i + 1), "");
                        t_Sql = StrTool.replace(t_Sql, "$" + String.valueOf(i + 1)
                                , "?$" + String.valueOf(i + 1) + "?");
                    }
                }
                ReplaceValSQL(t_Sql, m_Vals);
            }
            else
            {
                ReplaceValSQL(t_Sql, m_Vals);
            }
        }
        return t_Sql;
    }

    /**
     * 替换函数参数
     * @param c_Func 函数
     * @return 字符串
     * @throws Exception
     */
    private String ReplaceFunc1(String c_Func)
            throws Exception
    {
        String t_ValName;
        String t_Sql;
        String tR;
        String[] t_f1;
        String[] t_F2;
        String[] tP;
        int i;
        //如果发现这个全局的变量为null的时候，则再次读入系统配置文件
        if (m_SysFBV == null)
        {
            String sVtsFile = getConfigFilePath() + "system_define.vts";
            m_SysFBV = new BookModelImpl();
            //打开系统定义文件
            m_SysFBV.read(sVtsFile);
        }
        m_SysFBV.setSheet(1);

        while (true)
        {
            t_ValName = JRptUtility.Get_Str(c_Func, 2, "!").trim().toUpperCase(); //得到第i/2个变量的名称
            if (!t_ValName.equalsIgnoreCase(""))
            {
                t_f1 = t_ValName.split("[(]");
                for (i = 1; i < m_SysFBV.getLastRow() + 1; i++)
                {
                    t_F2 = m_SysFBV.getText(i - 1, 0).split("[(]");

                    if (t_f1[0].trim().toLowerCase().equalsIgnoreCase(t_F2[0].trim().toLowerCase()))
                    {
                        t_Sql = m_SysFBV.getText(i - 1, 2);
                        m_Function_Type = m_SysFBV.getText(i - 1, 1); //函数类型
                        if (t_f1[1].trim().length() > 1)
                        {
                            //判断是否有参数
                            tP = t_f1[1].substring(0, t_f1[1].length() - 1).split(",");
                            for (int j = 0; j < tP.length; j++)
                            {
                                t_Sql = StrTool.replace(t_Sql, "\\$" + String.valueOf(j + 1)
                                        , tP[j].trim());
                            }
                        }
                        t_Sql = Replace_SysVal_InString(t_Sql, 0);
                        if (m_Function_Type.equalsIgnoreCase("2"))
                        {
                            //只有当需要执行数据库操作时才执行
                            //DoEvents//将执行让给操作系统
                            tR = JRptUtility.GetOneValueBySQL(t_Sql);
                            c_Func = StrTool.replace(c_Func, "\\!" + t_ValName + "\\!", tR);
                        }
                    }
                }
                if (i > m_SysFBV.getLastRow() + 1)
                {
                    break;
                }
            }
            else
            {
                break;
            }
        } //end while
        return c_Func;
    }

    /**
     * 将SQL语句的参数进行替换,不区分大小写
     * @param c_SQL SQL
     * @param c_Vals 变量
     * @return 替换后的SQL
     * @throws Exception
     */
    public String ReplaceValSQL(String c_SQL, Vector c_Vals)
            throws Exception
    {
//        String t_Return;
        String t_Str;
//        String t_Temp;
        String t_Find;
        String t_Replace;
        int i, j;
//        int t_positon;
        String[] t_StrArray, t_StrArray_tmp;
        int t_Start, t_End;
        int t_J;
        int t_I;

        if (c_SQL.indexOf("?") < 0)
        {
            //没有需要替换的参数，则返回原串
            return c_SQL;

        }

        //对串进行大写，删除多余空格，和括号等号的处理
        t_Str = c_SQL;
        t_Str = JRptUtility.Kill_Blank(t_Str);

        t_Str = JRptUtility.Last_Pro_Str(t_Str);
        t_Str = JRptUtility.Kill_Blank(t_Str);

        j = c_Vals.size() - 1;

        t_I = 0;
        //字符串按照空格拆分，存入数组t_StrArray
        t_StrArray = t_Str.split(" ");

        //t_J = UBound(t_StrArray) + 8
        t_J = t_StrArray.length - 1 + 8;

        //ReDim Preserve t_StrArray(t_J)
        t_StrArray_tmp = new String[t_J + 1];
        t_StrArray_tmp[0] = t_StrArray[0];
        for (i = 0; i < t_StrArray.length; i++)
        {
            t_StrArray_tmp[i + 1] = t_StrArray[i];
        }
        for (i = t_StrArray.length + 1; i < t_StrArray_tmp.length; i++)
        {
            t_StrArray_tmp[i] = null;
        }
        t_StrArray = t_StrArray_tmp;

        t_I = t_J;

        t_Start = t_I;

        //替换from前的变量，如果是过程，也在这里处理
        for (i = 1; i <= j; i++)
        {

            if (((String[]) (c_Vals.get(i)))[1] == null
                    || ((String[]) (c_Vals.get(i)))[1].equalsIgnoreCase(""))
            {
                t_I = 1;

                while (t_I <= t_Start)
                {

                    if (t_StrArray[t_I].equalsIgnoreCase("FROM"))
                    {
                        break;
                    }

                    if (t_StrArray[t_I].indexOf(((String[]) c_Vals.get(i))[0]) >= 0)
                    {
                        //Select Case t_StrArray[t_I - 1]
                        if (t_StrArray[t_I - 1].equalsIgnoreCase(","))
                        {
                            for (t_End = t_I - 1; t_End <= t_Start - 2; t_End++)
                            {
                                t_StrArray[t_End] = t_StrArray[t_End + 2];
                            }
                            t_Start -= 2;
                            t_I -= 2;
                        }
                        else
                        {
                            if (t_StrArray[t_I + 1].equalsIgnoreCase(","))
                            {
                                for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                {
                                    t_StrArray[t_End] = t_StrArray[t_End + 2];
                                }
                                t_Start -= 2;
                                t_I -= 1;

                            }
                            else
                            {
                                for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                {
                                    t_StrArray[t_End] = t_StrArray[t_End + 1];
                                }
                                t_Start -= 1;
                                t_I -= 1;

                            }
                        }
                    }
                    t_I += 1;
                }
            }
            else
            {
                t_I = 1;
                while (t_I <= t_Start)
                {

                    //t_StrArray[t_I] = t_StrArray[t_I].replaceAll( ( ( String[] ) (
                    //    c_Vals.get( i ) ) )[0],

                    //                  ( ( String[] ) ( c_Vals.get( i ) ) )[1] );

                    t_StrArray[t_I] = StrTool.replace(t_StrArray[t_I]
                            , ((String[]) (c_Vals.get(i)))[0], ((String[]) (c_Vals.get(i)))[1]);
                    t_I += 1;
                }
            }

        }

        for (i = 1; i <= t_Start; i++)
        {
            //查找"GROUP"等的位置
            if (t_StrArray[i].equalsIgnoreCase("GROUP")
                    || t_StrArray[i].equalsIgnoreCase("ORDER")
                    || t_StrArray[i].equalsIgnoreCase("HAVING"))
            {
                t_J = i;
                break;
            }
        }

        if (t_J < t_Start)
        {
            //没有group,order 则


            //替换group,order,having后的变量，处理同from前的变量处理
            for (i = 1; i <= j; i++)
            {

                if (((String[]) (c_Vals.get(i)))[1] == null ||
                        ((String[]) (c_Vals.get(i)))[1].equalsIgnoreCase(""))
                {

                    t_I = t_J;
                    while (t_I <= t_Start)
                    {

                        if (t_StrArray[t_I].indexOf(((String[]) c_Vals.get(i))[0]) >= 0)
                        {
                            if (t_StrArray[t_I - 1].equalsIgnoreCase(","))
                            {
                                for (t_End = t_I - 1; t_End <= t_Start - 2; t_End++)
                                {
                                    t_StrArray[t_End] = t_StrArray[t_End + 2];
                                }
                                t_Start -= 2;
                                t_I -= 2;
                                //'DelnoUseChar 1, 2, t_strarray

                            }
                            else
                            {
                                if (t_StrArray[t_I + 1].equalsIgnoreCase(","))
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    t_Start -= 2;
                                    t_I -= 1;
                                    //'DelnoUseChar 0, 2, t_strarray
                                }
                                else
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 1];
                                    }
                                    t_Start -= 1;
                                    t_I -= 1;
                                    //'DelnoUseChar 0, 2, t_strarray
                                }
                            }
                        }
                        t_I += 1;
                    }
                }
                else
                {
                    t_I = 1;

                    while (t_I <= t_Start)
                    {
                        t_StrArray[t_I] = StrTool.replace(t_StrArray[t_I]
                                , ((String[]) (c_Vals.get(i)))[0], ((String[]) (c_Vals.get(i)))[1]);
                        t_I += 1;
                    }
                }

            }
            //'结束替换
        }

        t_Str = "";
        for (i = 1; i <= t_Start; i++)
        {
            t_Str = t_Str + " " + t_StrArray[i];
        }
        t_Str = JRptUtility.Kill_Blank(t_Str);

        if (t_Str.indexOf(" FROM ") < 0)
        { //'is procedure
        }
        else
        {
            //'处理from后，group,order,having前的变量为空情况

            for (i = 1; i <= j; i++)
            {

                t_I = 1;
                if (((String[]) (c_Vals.get(i)))[1] == null ||
                        ((String[]) (c_Vals.get(i)))[1].equalsIgnoreCase(""))
                {
                    while (t_I <= t_Start)
                    {
                        if (t_StrArray[t_I].indexOf(((String[]) c_Vals.get(i))[0].trim()) >= 0)
                        {
                            if (t_StrArray[t_I - 1].equalsIgnoreCase("("))
                            {
                                if (t_StrArray[t_I + 1].equalsIgnoreCase(")"))
                                {
                                    if (t_StrArray[t_I - 2].equalsIgnoreCase("NOT"))
                                    {

                                        for (t_End = t_I - 2; t_End <= t_Start - 4; t_End++)
                                        {
                                            t_StrArray[t_End] = t_StrArray[t_End + 4];
                                        }
                                        for (t_End = t_Start - 3; t_End <= t_Start; t_End++)
                                        {
                                            t_StrArray[t_End] = "";
                                        }
                                        t_Start -= 4;
                                        t_I -= 3;
                                    }
                                    else
                                    {
                                        for (t_End = t_I - 1; t_End <= t_Start - 3; t_End++)
                                        {
                                            t_StrArray[t_End] = t_StrArray[t_End + 3];
                                        }
                                        for (t_End = t_Start - 2; t_End <= t_Start; t_End++)
                                        {
                                            t_StrArray[t_End] = "";
                                        }
                                        t_Start -= 3;
                                        t_I -= 2;
                                    }
                                }
                                else
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 1;
                                }
                            }
                            else if (t_StrArray[t_I - 1].equalsIgnoreCase("NOT"))
                            {
                                for (t_End = t_I - 2; t_End <= t_Start - 3; t_End++)
                                {
                                    t_StrArray[t_End] = t_StrArray[t_End + 3];
                                }
                                for (t_End = t_Start - 2; t_End <= t_Start; t_End++)
                                {
                                    t_StrArray[t_End] = "";
                                }
                                t_Start -= 3;
                                t_I -= 3;
                            }
                            else if (t_StrArray[t_I - 1].equalsIgnoreCase("AND"))
                            {
                                if (t_StrArray[t_I + 1].equalsIgnoreCase(")")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("OR")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("ORDER")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("GROUP")
                                        || t_StrArray[t_I + 1].equalsIgnoreCase("")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("HAVING"))
                                {

                                    for (t_End = t_I - 1; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 2;
                                }
                                else
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 1;
                                }
                            }
                            else if (t_StrArray[t_I - 1].equalsIgnoreCase("OR"))
                            {
                                if (t_StrArray[t_I + 1].equalsIgnoreCase(")")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("ORDER")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("GROUP")
                                        ||
                                        t_StrArray[t_I + 1].equalsIgnoreCase("HAVING"))
                                {
                                    for (t_End = t_I - 1; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 2;
                                }
                                else
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 2;
                                }
                            }
                            else if (t_StrArray[t_I - 1].equalsIgnoreCase(")"))
                            {
                                for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                {
                                    t_StrArray[t_End] = t_StrArray[t_End + 2];
                                }
                                for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                {
                                    t_StrArray[t_End] = "";
                                }
                                t_Start -= 2;
                                t_I -= 2;
                            }
                            else
                            {
                                if (!t_StrArray[t_I + 1].equalsIgnoreCase("AND")
                                        &&
                                        !t_StrArray[t_I + 1].equalsIgnoreCase("OR")
                                        &&
                                        !t_StrArray[t_I + 1].equalsIgnoreCase("OR"))
                                {
                                    for (t_End = t_I; t_End <= t_Start - 1; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 1];
                                    }
                                    for (t_End = t_Start; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 1;
                                    t_I -= 1;
                                }
                                else
                                {
                                    for (t_End = t_I; t_End <= t_Start - 2; t_End++)
                                    {
                                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                                    }
                                    for (t_End = t_Start - 1; t_End <= t_Start; t_End++)
                                    {
                                        t_StrArray[t_End] = "";
                                    }
                                    t_Start -= 2;
                                    t_I -= 1;
                                }
                            }
                        }
                        t_I += 1;
                    }
                }
            }
        }

        while (i <= t_Start)
        {
            //Select Case t_StrArray(i)
            if (t_StrArray[i].equalsIgnoreCase("("))
            {

                /**********************************
                                          'If t_strarray(i + 2) = ")" Then
                 '        t_strarray(i) = t_strarray(i + 1)
                 '        For t_end = i + 1 To t_start - 2
                 '        t_strarray(t_end) = t_strarray(t_end + 2)
                                          '        Next
                                          '        t_Start -= 2
                                          '        i -= 1
                                          ' Else
                 ******************************/
                if (t_StrArray[i + 1].equalsIgnoreCase(")"))
                {
                    for (t_End = i; t_End <= t_Start - 2; t_End++)
                    {
                        t_StrArray[t_End] = t_StrArray[t_End + 2];
                    }
                    t_Start -= 2;
                    i -= 1;
                }
                //'End If
            }
            else if (t_StrArray[i].equalsIgnoreCase("WHERE"))
            {
                if (t_StrArray[i + 1].equalsIgnoreCase("AND")
                        || t_StrArray[i + 1].equalsIgnoreCase("OR"))
                {
                    for (t_End = i + 1; t_End <= t_Start - 1; t_End++)
                    {
                        t_StrArray[t_End] = t_StrArray[t_End + 1];
                    }
                    t_Start -= 1;
                    i -= 1;
                }
                else
                {
                    if (t_StrArray[i + 1].equalsIgnoreCase(""))
                    {
                        for (t_End = i; t_End <= t_Start - 1; t_End++)
                        {
                            t_StrArray[t_End] = t_StrArray[t_End + 1];
                        }
                        t_Start -= 1;
                        i -= 1;
                    }
                    else
                    {
                        if (t_StrArray[i + 1].equalsIgnoreCase("GROUP")
                                || t_StrArray[i + 1].equalsIgnoreCase("ORDER"))
                        {
                            for (t_End = i; t_End <= t_Start - 1; t_End++)
                            {
                                t_StrArray[t_End] = t_StrArray[t_End + 1];
                            }
                            t_Start -= 1;
                            i -= 1;
                        }
                    }

                }
            }
            else if (t_StrArray[i].equalsIgnoreCase("AND"))
            {
            }
            else if (t_StrArray[i].equalsIgnoreCase("OR"))
            {

            }
            else if (t_StrArray[i].equalsIgnoreCase("NOT"))
            {
            }
            i += 1;
        }

        t_Str = "";
        for (i = 1; i <= t_Start; i++)
        {
            t_Str = t_Str + " " + t_StrArray[i];
        }
        t_Str = JRptUtility.Kill_Blank(t_Str);

        //'进行其他处理

        if (t_Str == null)
        {
            t_Str = "";
        }

        t_Str = t_Str.trim();
        t_Str = ReplaceCellVal(t_Str).trim();
        i = 1;

        //'如果还有变量，取其名
        t_Find = JRptUtility.Get_Str(t_Str, i * 2, "?");
        //'如果是系统变量，则替换之
        t_Replace = Get_Sys_Val_Value(t_Find, 1);

        while (!t_Find.equalsIgnoreCase(""))
        {
            if (!t_Replace.trim().equalsIgnoreCase(""))
            {
//                t_Str = t_Str.replaceAll( "?" + t_Find + "?", t_Replace );
                t_Str = StrTool.replace(t_Str, "?" + t_Find + "?", t_Replace);
            }
            //'继续查找变量
            i += 1;
            t_Find = JRptUtility.Get_Str(t_Str, i * 2, "?");
            t_Replace = Get_Sys_Val_Value(t_Find, 1);
        }

        return t_Str;
    }

    /**
     * 设置报表指定的区域的格式并合并单元格
     * @param cDestArea 目的区域
     * @param cRefArea 参考区域
     * @throws Exception
     */
    public void Report_Merge(String cDestArea, String cRefArea)
            throws Exception
    {
        int t_Sheet;
        int tStartRow, tStartCol, tEndRow, tEndCol;
        int tRefRow, tRefCol;
        CellFormat tCF;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Report);

        //设置合并的单元格区域 ROW1:COL1:ROW2:COL2
        String[] tStr = cDestArea.split(":");

        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);
        tEndRow = TransferRowPos(tStr[2], m_FV);
        tEndCol = TransferColPos(tStr[3], m_FV);

        //取得 FORMAT 页 相应格式单元格的坐标
        String[] str1 = cRefArea.split(":");

        tRefRow = Integer.parseInt(str1[0]);
        tRefCol = Integer.parseInt(str1[1]);

        //根据指定坐标取得单元格的格式
        m_FV.setSheet(R_Format);
        m_FV.setSelection(tRefRow - 1, tRefCol - 1, tRefRow - 1, tRefCol - 1);
        tCF = m_FV.getCellFormat();

        //设置指定的区域的格式并合并单元格
        m_FV.setSheet(R_Report);
        tCF.setMergeCells(true);
        m_FV.setSelection(tStartRow - 1, tStartCol - 1, tEndRow - 1, tEndCol - 1);
        m_FV.setCellFormat(tCF);
        m_FV.setSheet(t_Sheet);

    }

    /**
     * cDestArea需要设置的数据（在第一页中定义）：sX:sY:eX:eY
     * 合并的报表区域,cRefArea:设置数据的位置（其中maxrow,maxcol为可以引用的变量）
     * @param cRefArea 参考区域
     * @param cDestArea 目标区域
     * @throws Exception
     */
    public void Set_Report_Text(String cRefArea, String cDestArea)
            throws Exception
    {
        int t_Sheet;
        int tStartRow, tStartCol, tEndRow, tEndCol;
        int tRefRow, tRefCol;
        CRage tSurRage = new CRage();
        CRage tDestRage = new CRage();

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Report);

        // 取得源区域单元格坐标
        String[] tStr = cDestArea.split(":");

        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);
        tEndRow = TransferRowPos(tStr[2], m_FV);
        tEndCol = TransferColPos(tStr[3], m_FV);

        // 取得目标区域左上角单元格坐标
        String[] str1 = cRefArea.split(":");

        tRefRow = TransferRowPos(str1[0], m_FV);
        tRefCol = TransferColPos(str1[1], m_FV);

        // 设置源区域
        tSurRage.m_Left = tStartCol;
        tSurRage.m_Top = tStartRow;
        tSurRage.m_Right = tEndCol;
        tSurRage.m_Buttom = tEndRow;

        // 设置目标区域
        tDestRage.m_Left = tRefCol;
        tDestRage.m_Top = tRefRow;
        tDestRage.m_Right = tRefCol + (tEndCol - tStartCol);
        tDestRage.m_Buttom = tRefRow + (tEndRow - tStartRow);

        //将报表数据从 tSurRage 区域复制到 tDestRage 区域
        Copy_Sheet_Data(tSurRage, m_FV, R_Format, tDestRage, m_FV, R_Report, 1);

        // 对复制后的目标区域数据进行 Replace_SysVal_InString 处理
        int i, j;
//        int iMax,jMax;
        for (i = tDestRage.m_Left; i <= tDestRage.m_Right; i++)
        {
            for (j = tDestRage.m_Top; j <= tDestRage.m_Buttom; j++)
            {
                String ss1 = "", ss2 = "";
                ss1 = m_FV.getText(i - 1, j - 1);
                ss2 = Replace_SysVal_InString(ss1, 0);
                m_FV.setText(i - 1, j - 1, ss2);
            }
        }

        m_FV.setSheet(t_Sheet);
    }

    /**
     * 在报表生成时设置单个单元格的值
     * @param cRefArea 参考区域
     * @param cDataValue 数据值
     * @throws Exception
     */
    public void Set_Report_Text1(String cRefArea, String cDataValue)
            throws Exception
    {
        //在报表生成时设置单个值

        int t_Sheet;
        int tStartRow, tStartCol;
//       int tEndRow, tEndCol;
//        int tRefRow;
//        int tRefCol;
        String[] tStr;
        String t_Sql, t_Str;
        CRage tSurRage = new CRage();
        CRage tDestRage = new CRage();

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Report);

        //设置引用数据
        tStr = cRefArea.split(":");
        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);

        if (cDataValue.startsWith("!"))
        {
            // 以"!"号开头的是 SQL 语句
            t_Sql = JRptUtility.Get_Str(cDataValue, 2, "!");
            //替换函数变量
            t_Sql = ReplaceValSQL(t_Sql, m_Vals);
            //根据SQL语句取单个值
            t_Str = JRptUtility.GetOneValueBySQL(t_Sql);
        }
        else
        {
            // 不以"!"开头的是单元格的直接值
            t_Str = cDataValue;
        }
        //设置目标单元格数据
        m_FV.setText(tStartRow - 1, tStartCol - 1, t_Str);
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 处理Row_COL_Text这样的数据
     * @throws Exception
     */
    public void Set_Row_Col_Text_At_Report_End()
            throws Exception
    {
        /*********************************处理Row_COL_Text这样的数据******************/
        //对于Row_Col_Text这样的数据，直接放在R_Report这个Sheet中
        int t_Cur_Row, t_Col;
//        String t_Sql;
//        String t_DSN, t_Key;
        int t_Row;
//        int t_FldCount;
        String t_Str;
        int t_Sheet;
//        int t_Row_Count;

        t_Sheet = m_FV.getSheet();

        m_FV.setSheet(R_Define);

        t_Cur_Row = m_Row_Col_Text_Start;

        while (true)
        {
            //取得单元格( m_Row_Col_Text_Start后面行 , 第一列)的值
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            if (!t_Str.startsWith("#"))
            {
                //如果不是 ROW_COL_TEXT 类型,则退出
                if (!t_Str.equalsIgnoreCase("ROW_COL_TEXT"))
                {
                    break;
                }

                //取得单元格坐标 (第2列)
                t_Row = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 1, ":"));
                t_Col = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 2, ":"));

                //取得数据 (第3列)
                t_Str = m_FV.getText(t_Cur_Row - 1, 2).trim();

                //得到第4列的值 (类型)
                String t_Str1;
                String t_Type;
                t_Str1 = m_FV.getText(t_Cur_Row - 1, 3);
                t_Type = "";

                if (!t_Str1.equalsIgnoreCase(""))
                {
                    t_Type = t_Str1.substring(0, 1);
                }
                else
                {
                    t_Type = m_Row_Col_Text_Type.substring(0, 1);
                }

                //如果为"1"代表需要显示
                if (t_Type.equalsIgnoreCase("1"))
                {
                    //如果需要在数据区显示Row_Col_Text
                    if (!t_Str.equalsIgnoreCase(""))
                    {
                        m_FV.setSheet(R_Report);
                        m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                        m_FV.setSheet(R_Define);
                    }
                }

            }
        }

        /*********************************End 处理Row_COL_Text这样的数据*****************/
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 设置在R_Report中的c_Rage区域中的系统变量
     * @param c_Rage 区域
     * @throws Exception
     */
    public void Set_Sys_Val_In_Report(CRage c_Rage)
            throws Exception
    {
        int i, j;
        int t_Sheet;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Report);

        for (i = c_Rage.m_Top; i <= c_Rage.m_Buttom; i++)
        {
            for (j = c_Rage.m_Left; j <= c_Rage.m_Right; j++)
            {
                if (!m_FV.getText(i - 1, j - 1).trim().equalsIgnoreCase(""))
                {
                    String s1 = "", s2 = "";
                    s1 = m_FV.getText(i - 1, j - 1).trim();

                    s2 = Replace_SysVal_InString(s1, i);
                    m_FV.setText(i - 1, j - 1, s2);
                    //m_FV.setText( i-1, j-1,Replace_SysVal_InString(m_FV.getText(i-1,j-1),i) );
                }
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 对设置特殊的报表区域格式和数据
     * @throws Exception
     */
    public void SetReportDataEX()
            throws Exception
    {
        int t_Cur_Row;
        int t_Sheet, t_Row_Count;
//        int i;
        String t_Str;
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);

        t_Cur_Row = 1;
        t_Row_Count = m_FV.getLastRow() + 1;

        for (t_Cur_Row = 1; t_Cur_Row <= t_Row_Count; t_Cur_Row++)
        {
            /****************合并的报表区域*********/
            //得到第1列数据
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            //如果不是以"#"开头
            if (!t_Str.startsWith("#"))
            {
                if (t_Str.equalsIgnoreCase("REPORT_MERGE"))
                {
                    //合并的报表区域
                    Report_Merge(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 2));
                }
            }

            /*****************设置报表数据**********************/
            if (!t_Str.startsWith("#"))
            {
                if (t_Str.equalsIgnoreCase("SET_REPORT_TEXT"))
                {
                    if (m_FV.getText(t_Cur_Row - 1, 2).indexOf(":") < 0)
                    {
                        //如果没有包含":"，则表示取单值
                        //设置报表数据
                        Set_Report_Text1(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                                , 2));
                    }
                    else
                    {
                        //设置报表数据
                        Set_Report_Text(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                                , 2));
                    }
                }
            }

        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * 转换列坐标
     * @param cPosString 位置
     * @param cFV BookModelImpl
     * @return int
     */

    private int TransferColPos(String cPosString, BookModelImpl cFV)
    {
        String tStr;
        int returnvalue;
        tStr = cPosString.toLowerCase().trim();
        tStr = StrTool.replace(tStr, "curcol", String.valueOf(this.m_Data_Col).trim());
        tStr = StrTool.replace(tStr, "maxcol", String.valueOf(cFV.getLastCol() + 1).trim());
        returnvalue = (int) JRptUtility.GetValue(tStr);
        if (returnvalue == -1)
        {
            returnvalue = cFV.getLastCol() + 1;
        }
        return returnvalue;
    }

    /**
     * 转换行坐标
     * @param cPosString 位置
     * @param cFV BookModelImpl
     * @return int
     */
    private int TransferRowPos(String cPosString, BookModelImpl cFV)
    {
        String tStr;
        int returnvalue;
        Vector tStr1 = new Vector();
//        DEBUG_OUT("***************TransferRowPos ****************");
        tStr = cPosString.toLowerCase().trim();

        tStr = StrTool.replace(tStr, "currow", String.valueOf(this.m_Data_Row).trim());

        tStr = StrTool.replace(tStr, "maxrow", String.valueOf(cFV.getLastRow() + 1).trim());
        returnvalue = (int) JRptUtility.GetValue(tStr);
        if (returnvalue == -1)
        {
            returnvalue = cFV.getLastRow() + 1;
        }
        return returnvalue;

    }

    /**
     * 合并"行"单元格
     * 根据指定的坐标范围,合并指定列的顺序排列的数据相同的行
     * @param cControlString 字符
     * @throws Exception
     */
    public void Unite_Col(String cControlString)
            throws Exception
    {
        int t_Sheet;
        int tCol;
        int tStartRow, tEndRow;
//        int tStartCol,tEndCol;
        int tCurRow;
        String[] tStr;
        String tSameStr;
        CellFormat tCF;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);

        //取得各坐标值
        tStr = cControlString.split(":");
        if (tStr.length > 1)
        {
            //取得列坐标
            tCol = Integer.parseInt(tStr[0]);
            //取得起始行
            tStartRow = Integer.parseInt(tStr[1]);
            //取得结束行
            tEndRow = Integer.parseInt(tStr[2]);

            //-1 代表最后一行
            if (tEndRow == -1)
            {
                tEndRow = m_FV.getLastRow() + 1;
            }
        }
        else
        {
            //如果只指定列,缺省认为所有行
            tCol = Integer.parseInt(cControlString);
            tStartRow = 1;
            tEndRow = m_FV.getLastRow() + 1;
        }

        //取该列第一行的值
        tSameStr = m_FV.getText(tStartRow - 1, tCol - 1);
        tCurRow = tStartRow;

        while (true)
        {
            //当隐藏校验的左指针超出报表时退出
            if (tStartRow > m_FV.getLastRow() + 1 || tStartRow > tEndRow)
            {
                break;
            }
            if (tCurRow > m_FV.getLastRow() + 1 + 1 || tCurRow > tEndRow + 1)
            {
                break;
            }

            if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(tCurRow - 1, tCol - 1).trim()))
            {
                //如果当前开始行取的值等于新的当前行,则当前行向下移动一行
                tCurRow += 1;
            }
            else
            {
                //如果当前开始行取的值不等于新的当前行,说明要开始另外一个新的开始行

                //如果当前行距离当前开始行超过1行,则合并当前开始行到当前行的单元格
                if (tStartRow != tCurRow - 1)
                {
                    //取当前开始行的单元格格式
                    m_FV.setSelection(tStartRow - 1, tCol - 1, tStartRow - 1, tCol - 1);
                    tCF = m_FV.getCellFormat();
                    //设置及合并从当前开始行到当前行的上一行的单元格格式
                    m_FV.setSelection(tStartRow - 1, tCol - 1, tCurRow - 1 - 1, tCol - 1);
                    tCF.setMergeCells(true);
                    tCF.setHorizontalAlignment(Format.eHorizontalAlignmentCenter);
                    tCF.setVerticalAlignment(Format.eVerticalAlignmentCenter);
                    tCF.setFontBold(true);
                    m_FV.setCellFormat(tCF);
                }

                //设置开始行为当前行,并且取得当前开始行的值
                tStartRow = tCurRow;
                tSameStr = m_FV.getText(tStartRow - 1, tCol - 1);
            }
        }

        m_FV.setSheet(t_Sheet);
    }

    /**
     * 合并"列"单元格
     * @param cControlString 字符
     * @throws Exception
     */
    public void Unite_Row(String cControlString)
            throws Exception
    {

        int t_Sheet;
        int tRow;
//        int tStartRow,  tEndRow;
        int tStartCol, tEndCol;
        int tCurCol;
        String[] tStr;
        String tSameStr;
        CellFormat tCF;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);

        //取得合并的单元格的坐标
        tStr = cControlString.split(":");
        if (tStr.length > 1)
        {
            //取得需要合并列的行坐标
            if (tStr[0].compareTo("#") == 0)
            {
                //如果是#号表示最后一行进行单元格合并
                tRow = m_Data_Row;
            }
            else
            {
                tRow = Integer.parseInt(tStr[0]);
            }
            //取得起始列坐标
            tStartCol = Integer.parseInt(tStr[1]);
            //取得结束列坐标
            tEndCol = Integer.parseInt(tStr[2]);

            //如果结束列为 -1 代表到末尾
            if (tEndCol == -1)
            {
                tEndCol = m_FV.getLastCol() + 1;
            }
        }
        else
        {
            //取得需要合并列的行坐标
            tRow = Integer.parseInt(cControlString);
            //默认第一列开始
            tStartCol = 1;
            //默认到最后一列结束
            tEndCol = m_FV.getLastCol() + 1;
        }

        //如果在这里做一个循环处理，大概可以实现我们需要的动态合并单元格
        if (tRow == -1)
        {
            for (int z = 1; z <= m_Data_Row; z++)
            {
                tStartCol = Integer.parseInt(tStr[1]);
                //初始化开始值
                tSameStr = m_FV.getText(z - 1, tStartCol - 1);
                tCurCol = tStartCol;

                while (true)
                {
                    //当隐藏校验的左指针超出报表时退出
                    if (tStartCol > m_FV.getLastCol() + 1 || tStartCol > tEndCol)
                    {
                        break;
                    }
                    if (tCurCol > m_FV.getLastCol() + 1 + 1 || tCurCol > tEndCol + 1)
                    {
                        break;
                    }

                    //如果新的当前列的值与当前合并开始列的值不相同,说明要开始新的合并列
                    if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(z - 1, tCurCol - 1).trim()))
                    {
                        //如果与当前合并列的值相同,则移动到下一列
                        tCurCol += 1;
                    }
                    else
                    {
                        //如果不相同,合并当前的合并列
                        if (tStartCol != tCurCol - 1)
                        {
                            m_FV.setSelection(z - 1, tStartCol - 1, z - 1, tCurCol - 1 - 1);

                            tCF = m_FV.getCellFormat();
                            tCF.setMergeCells(true);

                            tCF.setHorizontalAlignment(Format.eHorizontalAlignmentCenter);
                            tCF.setVerticalAlignment(Format.eVerticalAlignmentCenter);

                            tCF.setFontBold(true);
                            m_FV.setCellFormat(tCF);
                        }

                        //开始取新的合并开始列的值
                        tStartCol = tCurCol;
                        tSameStr = m_FV.getText(z - 1, tStartCol - 1);
                    }
                }
            }
        }
        else
        {
            //初始化开始值
            tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
            tCurCol = tStartCol;

            while (true)
            {
                //当隐藏校验的左指针超出报表时退出
                if (tStartCol > m_FV.getLastCol() + 1 || tStartCol > tEndCol)
                {
                    break;
                }
                if (tCurCol > m_FV.getLastCol() + 1 + 1 ||
                        tCurCol > tEndCol + 1)
                {
                    break;
                }

                //如果新的当前列的值与当前合并开始列的值不相同,说明要开始新的合并列
                if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(tRow - 1, tCurCol - 1).trim()))
                {
                    //如果与当前合并列的值相同,则移动到下一列
                    tCurCol += 1;
                }
                else
                {
                    //如果不相同,合并当前的合并列
                    if (tStartCol != tCurCol - 1)
                    {
                        m_FV.setSelection(tRow - 1, tStartCol - 1, tRow - 1, tCurCol - 1 - 1);

                        tCF = m_FV.getCellFormat();
                        tCF.setMergeCells(true);

                        tCF.setHorizontalAlignment(Format.eHorizontalAlignmentCenter);
                        tCF.setVerticalAlignment(Format.eVerticalAlignmentCenter);

                        tCF.setFontBold(true);
                        m_FV.setCellFormat(tCF);
                    }

                    //开始取新的合并开始列的值
                    tStartCol = tCurCol;
                    tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
                }
            }
        }

        m_FV.setSheet(t_Sheet);

    }

    /**
     * 对数据区域进行单元格合并
     * @throws Exception
     */
    public void UniteData()
            throws Exception
    {

        int t_Cur_Row;
        int t_Sheet, t_Row_Count;
//        int i;
        String t_Str;

        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);

        t_Row_Count = m_FV.getLastRow() + 1;

        //循环处理所有定义合并信息行数据
        for (t_Cur_Row = 1; t_Cur_Row <= t_Row_Count; t_Cur_Row++)
        {
            //取得当前定义行的第一列信息
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            //合并行单元格
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                //如果定义的是行合并信息
                if (m_FV.getText(t_Cur_Row - 1,
                        0).trim().equalsIgnoreCase("UNITE_ROW"))
                {
                    //合并行单元格
                    Unite_Row(m_FV.getText(t_Cur_Row - 1, 1));
                }
            }

            //合并列单元格
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                //如果定义的是列合并信息
                if (m_FV.getText(t_Cur_Row - 1,
                        0).trim().equalsIgnoreCase("UNITE_COL"))
                {
                    //合并列元格
                    Unite_Col(m_FV.getText(t_Cur_Row - 1, 1));
                }
            }
        }

        m_FV.setSheet(t_Sheet);

    }

    /**
     * 写入一个小计,如果c_Row=-1,则表示这使最后一行的数据
     * @param c_Row 行
     * @param c_ComputeCol 小计列
     * @param c_Data 数据
     * @param c_Type     描述信息
     * @param cGroupCol 列
     * @throws Exception
     */
    public void Write_Sum_X_To_Data(int c_Row, int c_ComputeCol, double c_Data, String c_Type
            , int cGroupCol)
            throws Exception
    {

        String t_Type, t_ColStr;
        int t_Col, t_Row;

        //取得描述类型
        t_Type = c_Type.substring(0, 1).trim();

        //得到描述的列值
        t_ColStr = c_Type.substring(1);

        //取得行信息,如果为 -1 代表最后一行
        if (c_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1;
        }
        else
        {
            t_Row = c_Row;
        }

        // 1. 如果描述信息为"B"
        if (c_Type.trim().equalsIgnoreCase("B"))
        {
            //在c_Row行的后面插入一行
            m_FV.insertRange(t_Row - 1 + 1, 0, t_Row - 1 + 1, 0, Constants.eShiftRows);
            //设置小计信息
            m_FV.setText(t_Row - 1 + 1, c_ComputeCol - 1, m_SumX_Caption + String.valueOf(c_Data));
        }
        // 2. 如果描述信息为"BN"
        else if (c_Type.trim().equalsIgnoreCase("BN"))
        {
            m_FV.setText(t_Row - 1 + 1, c_ComputeCol - 1, m_SumX_Caption + String.valueOf(c_Data));
        }
        // 3. 如果描述信息为"R+列号"
        else if (c_Type.trim().equalsIgnoreCase("R"))
        {
            if (JRptUtility.IsNumeric(t_ColStr))
            {
                //取得列号
                t_Col = Integer.parseInt(t_ColStr);
            }
            else
            {
                m_ErrString = "Write_Sum_X_To_Data: RX 列值不对，必须是数字";
                return;
            }
            //设置指定行列的数据
            m_FV.setText(t_Row - 1, t_Col - 1, String.valueOf(c_Data));
        }

    }

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

    public void test2()
    {
        try
        {
            String f = "";
            m_FV = new BookModelImpl();
            m_FV.read(f);
            m_FV.insertSheets(3, 6);

            m_FV.setSheet(0);

            m_FV.copyRange(5, 0, 0, 0, 40, m_FV, 0, 0, 0, 0, 40);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        JRptList t_Rpt = new JRptList();

        try
        {
            t_Rpt.m_NeedProcess = false;
            t_Rpt.m_Need_Preview = false;

            t_Rpt.m_NeedProcess = false;
            t_Rpt.m_Need_Preview = false;

//            t_Rpt.AddVar("ManageCom", "86");
//            t_Rpt.AddVar("inputtime", "2003年1月");
//            t_Rpt.AddVar("grpname", "CDC北京分公司");
//            t_Rpt.AddVar("date", "2004-12-17");
            t_Rpt.AddVar("StartDate", "2004-11-01");
            t_Rpt.AddVar("EndDate", "2005-11-30");
            t_Rpt.AddVar("ManageCom", "86110000");
            t_Rpt.AddVar("BranchLevel", "03");
            t_Rpt.AddVar("CurrentDate", "2005-07-08");

//            t_Rpt.AddVar("ManageComName", "86");
//            t_Rpt.AddVar("MakeDate", "2005-01-13");
//            t_Rpt.AddVar("ActuGetNo", "370110000000023");
//            t_Rpt.AddVar("GrpContNo", "240110000000006");
//            t_Rpt.AddVar("GrpPolNo", "220110000000013");
//            t_Rpt.AddVar("Money", "78.14");
//            t_Rpt.AddVar("Moneym", "78.14");
//            t_Rpt.AddVar("YYMMDD", "2005-01-20");
//            t_Rpt.AddVar("PersonNumber", "1");
//            t_Rpt.AddVar("GrpName", "美国");
//            t_Rpt.AddVar("StartDateN", "2005-01-20");
//            t_Rpt.AddVar("EndDateN", "2005-01-20");
//            t_Rpt.AddVar("MakeDate", "2005-01-20");
//            t_Rpt.AddVar("BarCode", "SINOSOFT2324");

            //t_Rpt.setConfigFilePath("D:\\");

            t_Rpt.Prt_RptList("e:\\LIS\\ui\\WEB\\Config\\",
                    "e:\\LIS\\ui\\f1print\\NCLtemplate\\",
                    "e:\\LIS\\ui\\web\\Generated\\",
                    "", "",
                    "gradeCountInheritList");
            // t_Rpt.Prt_RptList("D:\\2.vts");
            //t_Rpt.Prt_RptList( "D:\\e_test.vts" );

            DEBUG_OUT("*********** FINISH! *******************");
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
