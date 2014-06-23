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
 * <p>Title: JRptList F1�������</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Sinosoft</p>
 * <p>Company: Sinosoft Co.,Ltd.</p>
 * @author lwt , sangood , zhuxf
 * @version 1.0
 */
public class JRptList
{

    static int ConCols = 200; //����,����ֻ֧��100��������

    static int R_Format = 0; //��ʽ�ļ����ڵ�Sheet
    static int R_Define = 1; //�����ļ����ڵ�Sheet
    static int R_CellData = 2; //��Cell���������
    static int R_Head = 3; //�����ͷ�����ڵ�Sheet
    static int R_End = 4; //�����β�����ص�Sheet
    static int P_Head = 5; //�����ҳͷ���ڵ�Sheet
    static int P_End = 6; //�����ҳβ���ڵ�Sheet
    static int R_Data = 7; //������������ڵ�Sheet
    static int R_Report = 8; //���ƴ����ɺ��Sheet

    static int Row_DSN = 1;
    static int Row_Report_Head = 2;
    static int Row_Report_End = 3;
    static int Row_Page_Head = 4;
    static int Row_Page_End = 5;
    static int Row_Count_Per_Page = 6;
    static int Row_Visible = 7; //��7��Ϊ�����еĿɼ�
    static int Row_Data_Row = 8; //��8��Ϊ���������и�ʽ�������ڵ���
    static int Data_Cache = 9; //��9��Ϊ�������ݵĻ��巽ʽ
    static int Row_Data_Start = 10; //��ʾʵ�����ݵĿ�ʼ��
    static int conDataStart = 1; //����Sheet�Ŀ�ʼ����

    public BookModelImpl m_FV; //��Ÿ�ʽ�ļ���f1View
    public BookModelImpl m_DestFV; //������������Ҫ��ʾ������������������F1Book
    public BookModelImpl m_DestFBV; //�������µ�BookView��
    public BookModelImpl m_SysFBV; //ϵͳ����

    private Vector m_Visible; //������Ƿ�ɼ�
    public int m_Cur_Format_Row; //��ǰ����ĸ�ʽ�����е���
    public String m_ErrString = " "; //���������Ϣ

    public int m_DataBaseType; //������ݿ�����֧�ֵõ��ֶθ�����������Ϊ0��Ĭ���ǲ�֧��

    public int m_DataFormatRow; //�����и�ʽ���ڵ���
    public int m_Data_Row; //��ǰ�����������ڵ���
    public int m_Data_Col; //��ǰ�����������ڵ���
    public int m_DataRowHeight; //�����еĸ߶�

    public boolean m_NeedMerge; //���ڷ��飬�Ƿ���Ҫ�ϲ��Ӹ��ķ�������Ĭ��ΪFalse
    public String m_Row_Col_Text_Type = "";
    //����Row_Col_Text��Ҫ���͵�����,
    //���Ϊ"00"��ʾ������Row_Col_Text�����Ϊ"10"������Row_Col_Text�������������У�
    //���Ϊ"01"��������ڱ����ʽ�У����Ϊ"11"�������߶�����,Ĭ��ֵΪ"01"
    public int m_Row_Col_Text_Start; //����Row_Col_Text�Ŀ�ʼ��

    private Vector m_Vals; //��ű�����������С��Ŵ�0��ʼ

    int mFCol, mFCols, mFRow, mFRows; //���ù̶��У��е�����

    public boolean m_NeedProcess; //�����Ƿ���Ҫ��ʾ������,Ĭ��ΪTrue��ʾ������
    public int m_StartDisplayRow, m_StartDisplayCol; //������ʾʱ����Ŀ�ʼλ��
    public boolean m_Need_Display_Zero; //�����Ƿ���Ҫ��ʾ0ֵ,Ĭ��Ϊ����ʾ0ֵ����Ϊfalse
    public boolean m_Need_Preview; //���øñ����Ƿ���Ҫ��ʾ,Ĭ��Ϊtrue������ʾ
    public String m_SumH_Caption = ""; //�ϼƵı�ʾ��
    public String m_SumX_Caption = ""; //С�Ƶı�ʾ��
    public int m_Per_Page_Count; //���ÿҳ������
    public String m_FormatFileName = ""; //��ʽ�ļ�������

    public String m_Function_Type = ""; //��system_define�ж���ĺ�������
    public boolean m_Dsn_From_Func; //����Դȡ�Ա����Ǻ�����Ϊtrueȡ�Ժ���
    public String m_Dsn_From = ""; //Ĭ��ʱ������Դ��

    public String mWebReportWebDirectory = ""; //Web����Ķ�Ӧ��վ·��
    public String mWebReportDiskDirectory = ""; //Web����Ĵ��̴��·��
    public String mOutWebReportURL = ""; //Web��������
    public String mExcelOutWebReportURL = ""; //Web�����Excel��ʽ���
    public String mFormatFile = "";
    public String mConfigFilePath = ""; //system_define.vts ��Ŀ¼��б��

    boolean mReplaceCellValue_Numm; //���滻CellValue���Ƿ��п�ֵ����

    public boolean mNeedLog; //�ж��Ƿ���Ҫ��¼��־
    public boolean mNeedExcel; //�ж��Ƿ���Ҫת��ΪExcel
    public Date mTimeOutDate; //��ʱ����

    public int mCurPages; //��ƴ�ӱ���ʱ��ҳ��
    public String mAppPath;

    public JRptList()
    {
        initialize();
    }

    //����·�� add by wangyc 2005.1.24
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

    //������
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
     * ��ȡ�ļ�·��
     * @return ���ض���·��
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
            m_SumX_Caption = "С�ƣ�  ";
        }

        if (m_SumH_Caption.equalsIgnoreCase(""))
        {
            m_SumH_Caption = "�ϼƣ�  ";
        }

        m_NeedMerge = false;

        //ReDim m_Visible(ConCols)        //��ʼ���ɼ���
        m_Visible = new Vector();
        for (int i = 0; i <= ConCols; i++)
        {
            m_Visible.add(new Boolean(false));
        }
        m_Vals = new Vector(); //��ʼ����������

        this.AddVar("", "");

        m_Row_Col_Text_Type = "01";
        m_Need_Preview = false; //�Ƿ���ҪԤ�����������ҪԤ��������󲻻�ִ�п�������
        m_Need_Display_Zero = false; //����ʾ0ֵ

        //Set m_FV = New F1BookView        //��ʼ��һ��BookView

        m_FV = new BookModelImpl();

        m_NeedProcess = false; //���ý�������Ҫ��ʾ
        m_StartDisplayRow = 1; //����Ĭ�ϵ���ʾ���������λ��
        m_StartDisplayCol = 1; //����Ĭ�ϵ���ʾ���������λ��
        m_Function_Type = "0";
    }

    /**
     * ����һ������
     * @param c_Name ��������
     * @param c_Value ����ֵ
     */
    public void AddVar(String c_Name, String c_Value)
    {
        StringBuffer sf = new StringBuffer();
        sf.append("?");
        sf.append(c_Name.trim());
        sf.append("?");

        String[] s = new String[2];

        s[0] = sf.toString();
        //��������ֵΪnull�����ÿ�
        if (c_Value == null)
        {
            c_Value = "";
        }
        s[1] = c_Value;

        m_Vals.add(s);
    }

    /**
     * ת���д���
     * @param cDestCol String ת����
     * @param cStartRow String ��ʼ��
     * @param cChangeSQL String ת����SQL
     * @param cType String
     * @param cMethodName String
     * @throws Exception
     */
    public void Change_Col_Code(String cDestCol, String cStartRow, String cChangeSQL, String cType
            , String cMethodName)
            throws Exception
    {
//        System.out.println("=====��ʼ��ת��======");
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
        //��̬����java����
        if (!(cType.equalsIgnoreCase("") || cMethodName.equalsIgnoreCase("")))
        {
            Class c = Class.forName(cChangeSQL);
            //�����cChangeSQL���Ҫ���õķ�����������cMethodName���Ҫ���õĴ���ת����������cType���ת������
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
                    //ֻ�е����滻���б���������û�г��ֿ�ֵʱ��ִ���滻����
                    tStr = (String) m.invoke(o, parameterO);
                }
                m_FV.setText(i - 1, Integer.parseInt(cDestCol) - 1, tStr);
            }
        }
        else
        {
            //ִ��SQL���
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
                    //ֻ�е����滻���б���������û�г��ֿ�ֵʱ��ִ�д����滻
                    tStr = JRptUtility.GetOneValueBySQL(tSQL);
                }

                m_FV.setText(i - 1, Integer.parseInt(cDestCol) - 1, tStr);
            }
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * ת���д���
     * @param cDestRow Ŀ����
     * @param cStartCol ��ʼ����
     * @param cChangeSQL ת����SQL
     * @throws Exception �쳣
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
                //ֻ�е����滻���б���������û�г��ֿ�ֵʱ��ִ�д����滻
                tStr = JRptUtility.GetOneValueBySQL(tSQL);
            }
            m_FV.setText(Integer.parseInt(cDestRow) - 1, i - 1, tStr);
        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * У����ĳ���������Ƿ������ݣ����û�������Զ�����
     * @param cX x����
     * @param cY y����
     * @param cEX x����
     * @param cEY y����
     * @return ����true or false
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
     * ��С�ƺͺϼƽ��н���
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
        //�����к���
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

                //�����к���
                Put_Row_FunctionData(m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }

        //�������к���
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
                //�����к���
                Put_Row_Col_FunctionData(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                        , 2), m_FV.getText(t_Cur_Row - 1, 3));
            }
            t_Cur_Row += 1;
        }

        //���������غ���
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
                //�����к���
                //Put_Row_Col_FunctionData .TextRC(t_Cur_Row-1, 2), .TextRC(t_Cur_Row-1, 3), .TextRC(t_Cur_Row-1, 4)
            }
            t_Cur_Row += 1;
        }
        //���������غ���
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
                //�����к���
                Hide_Row(m_FV.getText(t_Cur_Row - 1, 1));
                //Put_Row_Col_FunctionData .TextRC(t_Cur_Row, 2), .TextRC(t_Cur_Row, 3), .TextRC(t_Cur_Row, 4)
            }
            t_Cur_Row += 1;
        }

        //ת���д���
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
                //ת���д���
                Change_Row_Code(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 3)
                        , m_FV.getText(t_Cur_Row - 1, 2));
            }
            t_Cur_Row += 1;
        }

        //ת���д���
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

                //ת���д���
                Change_Col_Code(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 3)
                        , m_FV.getText(t_Cur_Row - 1, 2), m_FV.getText(t_Cur_Row - 1, 4)
                        , m_FV.getText(t_Cur_Row - 1, 5));
            }
            t_Cur_Row += 1;
        }

        /*********************************************************************
          '���ϲ������ŵ�������
           '    ''''''''''''''''''�ϲ��е�Ԫ��
           '    Do While True
           '        t_Str = Trim(UCase(.TextRC(t_Cur_Row, 1)))
           '        If Left(t_Str, 1) = "#" Then
           '        Else
         '            If UCase(Trim(.TextRC(t_Cur_Row, 1))) <> "UNITE_ROW" Then Exit Do
           '            Unite_Row .TextRC(t_Cur_Row, 2) '�ϲ��е�Ԫ��
           '        End If
           '        t_Cur_Row += 1
           '    Loop
           '    ''''''''''''''''''�ϲ��е�Ԫ��
           '    Do While True
           '        t_Str = Trim(UCase(.TextRC(t_Cur_Row, 1)))
           '        If Left(t_Str, 1) = "#" Then
           '        Else
         '            If UCase(Trim(.TextRC(t_Cur_Row, 1))) <> "UNITE_COL" Then Exit Do
           '            Unite_Col .TextRC(t_Cur_Row, 2) '�ϲ�����Ԫ��
           '        End If
           '        t_Cur_Row += 1
           '    Loop
         **************************************************************/

        //����С�ƺ���
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
                //����С�ƺ���
                Put_Sum_X(t_Cur_Row, m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }
        //���ͺϼƺ���
        while (true)
        {
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();
            //#�ſ�ͷ��ʾ������ע��
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
                //���ͺϼƺ���
                Put_Sum_H(t_Cur_Row, m_FV.getText(t_Cur_Row - 1, 1));
            }
            t_Cur_Row += 1;
        }

        m_Cur_Format_Row = t_Cur_Row;

        //ɾ�����ɼ�����
        //Delete_Invisible_Cols
        m_FV.setSheet(t_Sheet);
    }

    /**
     * ������ҳβ
     * @param c_Type ����
     * @param c_Row ��
     * @param c_Col ��
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
            //��R_Head���Sheet��������ͷ���������������ƶ�����
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
            //�Ӹ�ʽ�ļ�������ͷ��������
            m_FV.setSheet(R_Define);
            //�õ�����ͷ������
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
     * ������ҳͷ
     * @param c_Type ����
     * @param c_Row ��
     * @param c_Col ��
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
            //��P_Head���Sheet��������ͷ���������������ƶ�����

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
            //�Ӹ�ʽ�ļ�������ͷ��������
            m_FV.setSheet(R_Define);
            //�õ�ҳͷ������
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
     * ����������β
     * @param c_Type ����
     * @param c_Row ��
     * @param c_Col ��
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
            //��R_End���Sheet��������ͷ���������������ƶ�����
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
            //�Ӹ�ʽ�ļ�������ͷ��������
            m_FV.setSheet(R_Define);
            t_Str = m_FV.getText(Row_Report_End - 1, 1);

            //�õ�����β������
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
     * ��������ͷ
     * @param c_Type ����
     * @param c_Row ��
     * @param c_Col ��
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
            //��R_Head���Sheet��������ͷ���������������ƶ�����
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
            //�Ӹ�ʽ�ļ�������ͷ��������
            m_FV.setSheet(R_Define);
            t_Str = m_FV.getText(Row_Report_Head - 1, 1);
            //�õ�����ͷ������
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
     * ��ָ����������ݴ�ԴSheet������Ŀ��Sheet��
     * @param c_SurRage ָ������
     * @param c_SurFV ԴSheet
     * @param c_SurFVIndex ԴSheet�ı��
     * @param c_DestRage Ŀ������
     * @param c_DestFV Ŀ��Sheet
     * @param c_DestIndex Ŀ��Sheet�ı��
     * @param c_NeedRowHeight �����и߶�
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

//        System.out.println("ԴSheet:" + c_SurFVIndex);
//        System.out.println("Ŀ��Sheet:" + c_DestIndex);
//        System.out.println("Դ��Χ:" + (c_SurRage.m_Top - 1) + "," +
//                (c_SurRage.m_Left - 1)
//                + "," + (c_SurRage.m_Buttom - 1) + "," +
//                (c_SurRage.m_Right - 1));
//        System.out.println("Ŀ�귶Χ:" + (c_DestRage.m_Top - 1) + "," +
//                (c_DestRage.m_Left - 1)
//                + "," + (c_DestRage.m_Buttom - 1) + "," +
//                (c_DestRage.m_Right - 1));

        if (c_SurRage.m_Buttom > 0 && c_SurRage.m_Right > 0)
        {
            c_DestFV.copyRange(c_DestIndex, c_DestRage.m_Top - 1, c_DestRage.m_Left - 1
                    , t_DstR2 - 1, t_DstC2 - 1, c_SurFV, c_SurFVIndex, c_SurRage.m_Top - 1
                    , c_SurRage.m_Left - 1, c_SurRage.m_Buttom - 1, c_SurRage.m_Right - 1);

            //Web�ϲ�����EditCopy

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
     * �����ɵı���ŵ�m_DestFV���F1Book��
     * @throws Exception
     */
    public void Copy_To_F1Book()
            throws Exception
    {

        int t_Row;
        int t_Sheet;
        int i;

        //�������е�����
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
            //����������
            m_DestFV.setFixedCol(mFCol - 1);
            m_DestFV.setFixedCols(mFCols + m_StartDisplayCol - 1);
            m_DestFV.setFixedRow(mFRow - 1);
            m_DestFV.setFixedRows(mFRows + m_StartDisplayRow - 1);
            //End����������
        }
        else
        {
            m_FV.setSelection( -1, -1, -1, -1);
//            m_FV.editCopy();
            m_DestFV.setSelection( -1, -1, -1, -1);
//            m_DestFV.editPaste();
            m_DestFV.copyAll(m_FV);
            //����������
            m_DestFV.setFixedCol(mFCol - 1);
            m_DestFV.setFixedCols(mFCols);
            m_DestFV.setFixedRow(mFRow - 1);
            m_DestFV.setFixedRows(mFRows);
            //End����������
        }

        //End�������е�����
        m_DestFV.setSelection(0, 0, 0, 0);
        //���������ĸ�ʽ

        //������ҳ��־
        t_Row = 1;
        while (true)
        {
            t_Row = m_FV.getNextRowPageBreak(t_Row - 1); //�˴�-1
            if (t_Row <= 0)
            {
                break;
            }
            m_DestFV.addRowPageBreak(t_Row - 1); //�˴�-1
            t_Row += 1;
        }
        //End������ҳ��־

        //End���������ĸ�ʽ
        m_FV.setSheet(t_Sheet);

    }

    /**
     * ���ݵ�ǰ����������ͱ���ģ�棬�õ�һ������������
     * @return һ��������־��
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
     * ����������ʼ��������
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
            //���ݶ�ȡ
            Read_Data();
            //С�ơ��ϼƲ���
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
     * ����m_Visible(),ɾ�����ɼ�����
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
     * ִ�к���������c_FunName�Ǻ�������c_FunParameter�Ǻ����Ĳ���
     * RowSum("2,3")��ʾ��2��3����ͣ��ŵ���4��
     * @param c_FunName ������
     * @param c_FunParameter �����Ĳ���
     * @param c_Row_Number  �к�
     * @return �ַ���
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
            //�õ�ʵ�ʵĲ���ֵ
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
     * ��ʽ��������
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
        //���ù̶�����
        m_FV.setFixedCol(mFCol - 1);
        m_FV.setFixedCols(mFCols);
        m_FV.setFixedRow(mFRow - 1);
        m_FV.setFixedRows(mFRows);

        UniteData();

        m_FV.setSheet(t_Sheet);
    }

    /**
     * ����c_Text��ֵ�����ص�ǰ��
     * @param c_Text ֵ
     * @param c_Row_Number �к�
     * @return ��ǰ�к�
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
     * �������Ĺ����Ǹ��ݲ���c_FunParameter����һ���ַ���
     * @param c_FunParameter ����
     * @param c_Row �к�
     * @return �ַ���
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

        t_Str = t_P[0]; //ȡ��sql���

        //������б�����ȡ����
        t_Find = JRptUtility.Get_Str(t_Str, i * 2, "$");
        //�滻֮
        t_Replace = "?" + String.valueOf(c_Row) + ":" + t_Find + "?";
        //������$6$�ı����滻Ϊ����?2:6?�ı���

        //t_Str = t_Str.replaceAll( "$" + t_Find + "$", t_Replace );
        t_Str = StrTool.replace(t_Str, "$" + t_Find + "$", t_Replace);
        while (!t_Find.equalsIgnoreCase(""))
        {
            //�滻�̶�����������������ϵͳ�����ͣ�row:col���͵ı���
            t_Str = ReplaceValSQL(t_Str, m_Vals);
            i += 1; //�������ұ���
            t_Find = JRptUtility.Get_Str(t_Str, i * 2, "$");
            //�滻֮
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
                //��ѯ��������ַ������м��á�^~���ָһ�е��ֶμ���"^$"�ָ�
                for (i = 1; i <= tSSRS.MaxCol; i++)
                {
                    t_Str = t_Str + tSSRS.GetText(r, i) + "^$";
                }
                t_Str += "^~"; //����
            }

        }

        return t_Str;
    }

    /**
     * ����Formula��ʽ
     * @param c_P ����
     * @param c_Row_Number �к�
     * @return �ַ���
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
     * �õ�c_P�����Ļ�,�����Ҫָ�����ȣ������һ������ָ������
     * JD1��ʾ��ȷ��С����1λ��JD2��ʾ��ȷ��2λ....
     * ��������������ֶΣ�����ֶ�Ϊ0
     * @param c_P ��������
     * @return ������
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
            //�����������2�������ж����һ�������Ƿ��ʾ����
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
     * �����̵Ĺ����ǰ�ϵͳ�����ļ���sheet=1�ı���ȫ���滻
     * ϵͳ�����ļ���sheet=1�ı��������������ͣ��̶������Ͳ�������
     * @param c_FileName �ļ���
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
        //��ϵͳ�����ļ�
        m_SysFBV.read(sVtsFile);
        m_SysFBV.setSheet(0);

        for (i = 0; i <= m_SysFBV.getLastRow(); i++)
        {
            if (m_SysFBV.getText(i, 1).equalsIgnoreCase("1"))
            {
                //�滻�̶�����
                AddVar(m_SysFBV.getText(i, 0), m_SysFBV.getText(i, 2));
            }
            if (m_SysFBV.getText(i, 1).equalsIgnoreCase("2"))
            {
                //�滻sql��䷵��ֵ�ı���
                t_Str = Get_Sql_Val(m_SysFBV.getText(i, 0), m_SysFBV.getText(i, 2)
                        , m_SysFBV.getText(i, 3), c_FileName);
            }

        }
        //ԭ���Ǵ�System_Define.vts�еõ�������ֵ�����ڸ�ΪJRptConfig���JRptConfig.xml�еõ�
        //m_SysFBV.setSheet(2);
        //mWebReportWebDirectory = m_SysFBV.getText(1, 1);
        //mWebReportDiskDirectory = m_SysFBV.getText(2, 1);
    }

    /**
     * �õ�c_P�����е�ָ���е�ֵ,���ҽ������ֶ�ƴ��һ���ַ�������","�ֿ�
     * @param c_P ��������
     * @param c_Row �к�
     * @return �ַ���
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
     * �������Ĺ����ǲ��õݹ鷨�滻sql��䷵��ֵ�ı���
     * @param c_VName ������
     * @param c_SQL SQL���
     * @param c_DSN DSN
     * @param c_FileName �ļ���
     * @return �ַ���
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

        //�滻��sql����е�ϵͳ�����͹̶�����
        //t_Sql = ReplaceValSQL(t_Sql, m_Vals)

        if (c_VName.startsWith("#"))
        {
            return "";
        }

        //�滻��sql����е�ϵͳ�����͹̶�����
        t_Sql = Replace_SysVal_InString(t_Sql, 0);

        if (t_Sql.indexOf("?") >= 0)
        {
            //�滻���б�����˵����sql��仹�в�������
            t_Name = t_Sql.split("[?]");

            for (j = 1; j <= (t_Name.length - 1) / 2; j++)
            {
                //�õ���i/2������������
                t_ValName = JRptUtility.Get_Str(t_Sql, j * 2, "?").trim();

                if (!new File(getConfigFilePath() + "system_define.vts").exists())
                {
                    t_F1Val = new BookModelImpl();
                    t_F1Val.read(getConfigFilePath() + "system_define.vts");
                    t_F1Val.setSheet(0);
                    for (i = 0; i <= t_F1Val.getLastRow(); i++)
                    {
                        //���ұ���
                        if (t_F1Val.getText(i, 0).equalsIgnoreCase(t_ValName))
                        {
                            //ȡ��sql���
                            t_Sql = t_F1Val.getText(i, 2);
                            t_VName = t_F1Val.getText(i, 0);

                            break;
                        }
                    }
                    //�ݹ����
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
            //�滻��û�б�����˵��Ϊ�ݹ����ײ���߲��õݹ飬��ִ�и�sql���
//            System.out.println("replaceSQL:" + t_Sql);
            tSSRS = tExeSQL.execSQL(t_Sql);

            if (tSSRS.MaxRow < 1)
            {
                t_return = "";
            }
            else
            {
                t_return = JRptUtility.ChgValue(tSSRS.GetText(1, 1));
                //��ȡ�õı�������m_vals()����
                AddVar(t_VName, t_return);
            }
        }
        return t_return;
    }

    /**
     * �õ�c_P�����ĺ�,�����Ҫָ�����ȣ������һ������ָ�����ȣ�
     * @param c_P ����
     * @return �ַ���
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
            //�����������2�������ж����һ�������Ƿ��ʾ����
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
     * �õ�ϵͳ�ı���
     * @param c_Val_Name ������
     * @param c_Index ���
     * @return �ַ���
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
     * ���û��Լ�����ı������Ҹñ����Ƿ����
     * @param c_ValName ������
     * @param c_Found �Ƿ����
     * @return �ַ���
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
     * ����ʷ�ж�ȡ��صı���������ڣ���ֱ�Ӷ�ȡ
     * @param cFileName �����ļ���
     * @return �ַ���
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
            //ֻ�е���ֵ����Ϊ1ʱ�Ŵ���ʷ�ж�ȡ
            return "";
        }

        if (tFV.getText(8, 2).equalsIgnoreCase(""))
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            mTimeOutDate = formatter.parse("3003-1-1");
        }
        else
        {
            tTimeOutDay = Integer.parseInt(m_FV.getText(8, 2)); //�õ�����
            Calendar nowdate = Calendar.getInstance();

            nowdate.add(Calendar.DAY_OF_YEAR, tTimeOutDay); //���������
            mTimeOutDate = nowdate.getTime(); //�õ���ʱ����
        }

        mNeedLog = true;

        CReportLog tRL = new CReportLog();

        tReportVarName = CreateReportVarName();
        t_return = tRL.GetHistoryFile(tReportVarName);
        m_ErrString = tRL.m_ErrString;

        return t_return;
    }

    /**
     * ������,row:s_x:s_y:e_x:e_y:����Ϊ����row��ͬ�������أ�
     * У�����ݴ�s_x:s_y:e_x:e_y������
     * @param cControlString �������
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
            //������У�����ָ�볬������ʱ�˳�
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
                    tCurCol = tStartCol; //ɾ�����е�ָ���Ծɻ�ȥ��������
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
     *������,col:s_x:s_y:e_x:e_y:����Ϊ����row��ͬ�������أ�У�����ݴ�s_x:s_y:e_x:e_y������
     * @param cControlString �������
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
            //������У�����ָ�볬������ʱ�˳�
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
                    tCurRow = tStartRow; //ɾ�����е�ָ���Ծɻ�ȥ��������
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
     * ��ʼ��6��sheet
     * @throws Exception
     */
    private void InitSheets()
            throws Exception
    {
        m_FV.insertSheets(3, 6);
    }

    /**
     * ��ӡ����
     * @param FileName �ļ���
     * @param pageContext ��JSP�д����������
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

    //������
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

        Get_Ord_Val(c_FileName); //ȡ��ʵʱ����

        Read_Format(c_FileName); //��ȡ��ʽ

        mOutWebReportURL = GetURLFromHistory(c_FileName);
//        System.out.println("WebReportURL:" + mOutWebReportURL);
        if (!mOutWebReportURL.equalsIgnoreCase(""))
        {
            File f = new File(mWebReportDiskDirectory.trim() + mOutWebReportURL);
            if (f.exists())
            {
//                System.out.println("--exists--");
                //�ļ����ڣ��򷵻أ�������������
                mOutWebReportURL = mWebReportWebDirectory.trim() + mOutWebReportURL;
//                System.out.println("direct+url:" + mOutWebReportURL);
                //�������ʷ�ж�ȡ����ֱ�ӷ���
                return;
            }
        }

        DealData();

        Delete_Invisible_Cols(); //ɾ�����ɼ�����

        Format_Cell(); //�����ݵĸ�ʽ���н���

        Raise_Report(); //�������ı�������ÿҳ�ĸ�ʽ

        SetReportDataEX(); //����������ı��������ʽ������

        //begin ȷ���п����ȷ add by wangyc 2005.1.21
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

        AddPicture(); //�����ձ������ͼƬ������������

        if (m_Need_Preview)
        {
            Copy_To_F1Book(); //������m_DestFV
        }

        //������ļ����ļ���Ϊԭ���ĸ�ʽ�ļ���+Out.vts
        String tOutFileName, tOutFileName1;
        String tFileNameNoPath;

        tFileNameNoPath = JRptUtility.Get_OnlyFileNameEx(c_FileName);
        m_FV.deleteSheets(0, 8);
        String sTime = "";

        sTime = "_" + JRptUtility.getDate() + "_" + JRptUtility.getTime();
        sTime = sTime.replaceAll(":", "-");

        tOutFileName1 = tFileNameNoPath.trim() + sTime + ".vts";
        tOutFileName = mWebReportDiskDirectory.trim() + tOutFileName1;

        //��ָ����ʽ�����ļ�
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

        //�Ƿ�����excel�ļ�
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
     * �������ļ���picture��barcode��
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
            //����picture��
            if (m_FV.getText(i, 0).equalsIgnoreCase("picture"))
            {
                String objectName = m_FV.getText(i, 1);
                String strURL = m_FV.getText(i, 2);
                String urlType = m_FV.getText(i, 3);
                m_FV.setSheet(R_Format);
                GRObject gr = m_FV.getObject(objectName);
                if (gr == null)
                {
                    DEBUG_OUT("�����PICTURE�У�ͼƬ�����ڻ�ͼƬ��" + objectName + "����ȷ");
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
            //����barcode��
            if (m_FV.getText(i, 0).equalsIgnoreCase("barcode"))
            {
                String objectName = m_FV.getText(i, 1);
                String strParameter = m_FV.getText(i, 2);
                String strCodeVar = m_FV.getText(i, 3);

                m_FV.setSheet(R_Format);
                GRObject gr = m_FV.getObject(objectName);
                if (gr == null)
                {
                    DEBUG_OUT("�����BarCode�У�ͼƬ�����ڻ�ͼƬ��" + objectName + "����ȷ");
                }
                else
                {
                    strCodeVar = strCodeVar.replace('?', ' ').trim();
                    String params[] = strParameter.split("&");
                    String t_Str = "";
                    String strCode = this.Get_Val_In_User_Add(strCodeVar, false);
                    BarCode bcode = new BarCode(strCode);
                    //������������
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
     * ��ȡ����
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key ��ֵ
     * @param c_FldCount �ֶθ���
     * @param c_T8 ת�Ƹ�ʽ����
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
                //���������ݶ�ת��
                Put_Row_Data_NOKey(c_SRow, c_SCol, c_SQL, c_DSN, c_FldCount);
            }
            else if (c_T8.equalsIgnoreCase("2"))
            {
                //��������һ�����ת��
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
     * �������ݣ�������
     * @param c_Group ������
     * @param c_Src_F1 ԴSheet
     * @param c_Dst_F1 Ŀ��Sheet
     * @param c_Count ����
     * @throws Exception
     */

    public void Order0(String[] c_Group, BookModelImpl c_Src_F1, BookModelImpl c_Dst_F1
            , int c_Count)
            throws Exception
    {
        int t_Next_Start = 1; //����д��Ŀ��sheet����,��ʼΪ1
        String t_Base; //��������־
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

        //��ʼΪ��һ�У���һ�������е�ֵ
        t_Base = c_Src_F1.getText(0, i_Group - 1);

        for (i = 1; i <= c_Count; i++)
        {
            //c_CountΪԴsheet�е�ʣ���У�Խ��ԽС��ֱ��Ϊ0
            if (c_Count == 0)
            {
                break;
            }
            int j = i;
            while (j <= c_Count)
            {
                //ȫ��������Ϸ���û�������
                if (!t_Base.equalsIgnoreCase("") && c_Src_F1.getText(j - 1
                        , i_Group - 1).trim().equalsIgnoreCase(t_Base))
                {
                    //�ƶ���Ŀ�ĵ�
                    c_Dst_F1.copyRange(t_Next_Start - 1, 0, t_Next_Start - 1, c_Src_F1.getLastCol()
                            , c_Src_F1, j - 1, 0, j - 1, c_Src_F1.getLastCol());
                    //������Դ��ɾ��֮
                    c_Src_F1.deleteRange(j - 1, 0, j - 1, c_Src_F1.getLastCol()
                            , Constants.eShiftRows);

                    c_Count -= 1;
                    j--;
                    t_Next_Start += 1;
                }
                j++;
            }
            //ȫ��������ϣ����·����־
            t_Base = c_Src_F1.getText(i - 1, i_Group - 1);
            i--;
        }
        //end for
    }

    /**
     * ��η���
     * @param c_Group �������
     * @param c_Src_F1 ԴSheet
     * @param c_Dst_F1 Ŀ��Sheet
     * @throws Exception
     */

    public void Order1(String[] c_Group, BookModelImpl c_Src_F1, BookModelImpl c_Dst_F1)
            throws Exception
    {
        int t_Next_Start = 1; //����д��Ŀ��sheet����,��ʼΪ1
        String t_Base; //��������־
        String t_Comp; //Ϊ��ǰ�����е�ǰһ�������е�ֵ�������ǰ������ֵ��t_Comp��ͬ��
        //˵�����Լ������ң����򲻱�������������ھ��˳���ǰ����
        int t_Count;
        int i = 1;

        for (int m = 1; m <= c_Group.length - 1; m++)
        {
            t_Next_Start = 1;

            //�ƶ���Ŀ�ĵ�
            c_Dst_F1.copyRange(0, 0, c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol(), c_Dst_F1, 0, 0
                    , c_Dst_F1.getLastRow(), c_Dst_F1.getLastCol());

            //������Դ��ɾ��֮
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

            //��ʼΪ��һ�У���һ�������е�ֵ
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
                    //ȫ��������Ϸ���û�������
                    if (!t_Base.equalsIgnoreCase("") && c_Src_F1.getText(j - 1
                            , Integer.parseInt(c_Group[m]) - 1).trim().equalsIgnoreCase(t_Base))
                    {
                        //�ƶ���Ŀ�ĵ�
                        c_Dst_F1.copyRange(t_Next_Start - 1, 0, t_Next_Start - 1
                                , c_Src_F1.getLastCol(), c_Src_F1, j - 1, 0, j - 1
                                , c_Src_F1.getLastCol());
                        //������Դ��ɾ��֮
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
                //ȫ��������ϣ����·����־
                t_Base = c_Src_F1.getText(i - 1, Integer.parseInt(c_Group[m]) - 1);
                i--;
            }
            //end for
        }
        //end for
    }

    /**
     * ��ȡ���ݣ��ŵ�R_Data��,
     * c_Key��ʾ�ò�ѯ���Ҫ��ĳЩ��Ϊ��ֵ����д���ݸ�ʽ����Ϊ1,2:1,2:3,4
     * ����":"ǰ���1,2��ʾ��Ӧ�������Ѿ��е����ݣ������1,2��ʾ��Ӧ����ѯ�����1��2�ֶ�,
     * ����3,4��ʾҪ��д3��4�����ֶ�
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key ��ʾ�ò�ѯ���Ҫ��ĳЩ��Ϊ��ֵ����д,���ݸ�ʽ����Ϊ1,2:1,2:3,4
     * @param c_FldCount �ֶ���
     * @return �ַ���
     * @throws Exception
     */
    public String Put_Data_By_Key(int c_SRow, int c_SCol, String c_SQL, String c_DSN, String c_Key
            , int c_FldCount)
            throws Exception
    {
        //����д����ǰ���Ǹ����Ƿ��Ѿ���д��
        Vector t_HavePut = new Vector();
//        String t_DSN;
//        String t_Conn;

        SSRS t_Rs = new SSRS(); //�滻t_Rs
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
        /****************������ʾ�ж�ӦRecordSet�е��ֶ�ֵ*****************************/

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
        /******************************End������ʾ�ж�ӦRecordSet�е��ֶ�ֵ*************/

        /*********************************������Ҫ���յ�Formula One��*******************/
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
        /************************************End������Ҫ���յ�Formula One��***************/

        /*********************************����Recordset����******************************/
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
        /*****************************End����Recordset����***************************/

        /****************************************************************************/

        //�õ���ʾ���ֶεĸ���
        t_FldDisplayCount = t_FldIndex.size() - 1;
        t_KeyCount = t_TextCol.size() - 1;
        if (!(t_KeyCount == t_FldKey.size() - 1))
        {
            DEBUG_OUT(
                    "Put_Data_By_Key()�����������󣺶�����ֶ��е�Key�����͸�ʽ�е�Key��ʽ��ͬ��");
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
            //�õ�ƥ�����
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
     * ��ȡ���ݣ��ŵ�R_Data��,
     * c_Key��ʾ�ò�ѯ���Ҫ��ĳЩ��Ϊ��ֵ����д���ݸ�ʽ����Ϊ1,2:1,2:3,4
     * ����":"ǰ���1,2��ʾ��Ӧ�������Ѿ��е����ݣ������1,2��ʾ��Ӧ����ѯ�����1��2�ֶ�,����3,4��ʾҪ��д3��4�����ֶ�
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_Key ��ʾ�ò�ѯ���Ҫ��ĳЩ��Ϊ��ֵ����д���ݸ�ʽ����Ϊ1,2:1,2:3,4
     * @param c_FldCount �ֶ���
     * @param c_T8 ת����ʽ����
     * @throws Exception
     */
    public void Put_Data_By_Key1(int c_SRow, int c_SCol, String c_SQL, String c_DSN, String c_Key
            , int c_FldCount, String c_T8)
            throws Exception
    {
        Vector t_HavePut = new Vector(); //����д����ǰ���Ǹ����Ƿ��Ѿ���д��
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
        /*******************************������ʾ�ж�ӦRecordSet�е��ֶ�ֵ**************/

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

        /*********************************End������ʾ�ж�ӦRecordSet�е��ֶ�ֵ*****************/

        /*****************************************ȡ�ô�������ֵ������������********************/

        StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < t_FldIndex.size(); i++)
        {
            sbuffer.append(t_FldIndex.elementAt(i).toString());
            sbuffer.append(" ");
        }

        t_Group = sbuffer.toString().split(" ");

        /*****************************'������Ҫ���յ�Formula One��*****************************/
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
        /******************************End������Ҫ���յ�Formula One��*************************/

        /*********************************����Recordset����**********************************/

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
        /***********************************End����Recordset����***********************/

        t_F1v = new BookModelImpl();
        t_FldDisplayCount = t_FldIndex.size() - 1; //�õ���ʾ���ֶεĸ���
        t_KeyCount = t_TextCol.size() - 1;
        if (t_KeyCount == t_FldKey.size() - 1)
        {
            // m_ErrString = "Put_Data_By_Key:������ֶ��е�Key�����͸�ʽ�е�Key��ʽ��ͬ";
            return;
        }

        t_Sheet = m_FV.getSheet();
        t_RowCount = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_Data); //��ʼ��������д��־����
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
            //�õ�ƥ�����
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
                        //ƥ���
                        //t_PP_Row = i;
                        //ƥ�����ڴ˲���һ��
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
                                //��t_F1V��װ����ɺ��ٷ���
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

                        t_Next_Line += 1; //��������
                    }
                }
            }
        } //end while

        int t_Count = t_F1v.getLastRow() + 1;
        if (t_Group.length > 0)
        {
            Order0(t_Group, t_F1v, m_FV, t_F1v.getLastRow() + 1); //��һ�η���
        }

        if (t_Group.length > 1)
        {
            Order1(t_Group, t_F1v, m_FV); //��η���
            m_FV.setSheet(t_Sheet);
        }
    }

    /**
     * ��ȡ���ݣ��ŵ�R_Data��
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount �ֶ���
     * @return �ַ���
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
        t_Col = c_SCol; //��д���ݵĿ�ʼ����

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
        m_DataBaseType = 0; //�ָ����ʼֵ������Ϊȫ�ֱ���������ĸ�ֵ������Ӱ�쵽�����Ĳ���
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
     * �����к���,��RowSum("2,3:4")��ʾ��2��3����ͣ��ŵ���4��
     * @param cArea ����
     * @param c_FunctionScript ����
     * @param cExeType ִ������
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

        tPos = cArea.split(":"); //�ָ��ַ���

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
        ////////////////////���ݿ����///////////////////////
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
     * ������չ�ķ�����,��ȡ���ݣ��ŵ�R_Data��
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount �ֶ���
     * @return �ַ���
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
        t_Col = c_SCol; //��д���ݵĿ�ʼ����

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
        m_DataBaseType = 0; //�ָ����ʼֵ������Ϊȫ�ֱ���������ĸ�ֵ������Ӱ�쵽�����Ĳ���
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

                //if(JRptUtility.IsNumeric(t_Str)){//ȥ���ַ����������͵��жϣ��κ�ʱ����Ϊ���ַ���
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
     * ������չ��һ������
     * @param c_SRow Դ��
     * @param c_SCol Դ��
     * @param c_SQL SQL
     * @param c_DSN DSN
     * @param c_FldCount �ֶ���
     * @return �ַ���
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
        t_Col = c_SCol; //��д���ݵĿ�ʼ����

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
        m_DataBaseType = 0; //�ָ����ʼֵ������Ϊȫ�ֱ���������ĸ�ֵ������Ӱ�쵽�����Ĳ���
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
     * �����к���,��RowSum("2,3:4")��ʾ��2��3����ͣ��ŵ���4��
     * @param c_FunctionScript ��������
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

        t_Str = JRptUtility.Get_Str(c_FunctionScript, 2, "(%"); //�õ������Ĳ���
        t_Str = t_Str.substring(0, t_Str.length() - 2); //���س����2���ַ��Ľ�ȡ��
        t_Str1 = JRptUtility.Get_Str(c_FunctionScript, 1, "(%"); //�õ�������

//        BookModelImpl  t_Flv;
        t_Data = t_Str.split(":"); //�ָ��ַ���

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

                    t_Group = t_Str.split("\\^~"); //�ָ��ַ�������^~Ϊ���

                    for (int m = 0; m < t_Group.length; m++)
                    {
                        if (!t_Group[m].equalsIgnoreCase(""))
                        {
                            for (int j = 0; j < m_FV.getLastCol() + 1; j++)
                            {
                                t_F1v.setText(t_Next_Line - 1, j - 1, m_FV.getText(i - 1, j - 1));
                            }

                            t_ALine = t_Group[m].split("\\^\\$"); //�ָ��ַ�������^~Ϊ���

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
     * ���ͺϼƺ���,��SUM_H(3:4),��ʾ�Ե����н��кϼƣ����ڵ�4��
     * SUM_H(3),��ʾ�Ե����н��кϼƣ����ڵ�3��
     * @param c_Row ��
     * @param c_Text ֵ
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

        t_Data = t_Str.split(" "); //�ָ��ַ���,���ո�ָ�

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
     * ����С�ƺ���,��SUM_X(1,2:3:R5)��ʾ����1��2�з������С�ƣ�С�Ʒ��ڵ�5�е��ұ�
     * SUM_X(1,2:3,B)���ʾС�Ʒ���ͳ���е����棨������һ�У�
     * @param c_Row ��
     * @param c_Text ֵ
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

        t_Data = c_Text.split(":"); //�ָ��ַ���,��":"�ָ�

        t_Type = t_Data[2];
        t_ComputeCol = Integer.parseInt(t_Data[1]);

        t_Temp = t_Data[0].split(","); //�ָ��ַ���,��","�ָ�

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
                //����ǵ�һ��С����
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
                    //���С������ȣ������С��
                    if (JRptUtility.IsNumeric(t_Str1))
                    {
                        t_Return += Double.parseDouble(t_Str1);
                    }

                    //�����Ҫɾ���Ӹ�������
                    for (j = 0; j <= t_GroupColCount; j++)
                    {
                        //m_FV.deleteRange( i, (int)t_GroupCol[j], i,(int)t_GroupCol[j], F1FixupAppend);
                        m_FV.clearRange(i - 1, (int) t_GroupCol[j] - 1, i - 1
                                , (int) t_GroupCol[j] - 1, Constants.eClearValues);
                    }

                }
                else
                {
                    //�������ȣ����ʾ������Ҫ����һ��С��
                    Write_Sum_X_To_Data(i - 1, t_ComputeCol, t_Return, t_Type, (int) t_GroupCol[0]);

                    if ((m_FV.getLastRow() + 1) != t_RowCount)
                    {
                        //������ڵ�ǰ���в���һ�У�������Ҳ������һ��
                        t_RowCount += 1;
                        i += 1;
                    }

                    if (t_Type.equalsIgnoreCase("BN"))
                    {
                        t_Str = "";
                    }
                    //ʹС�ƴ��¿�ʼ
                    t_Group = t_Str;
                    if (JRptUtility.IsNumeric(t_Str1))
                    {
                        t_Return = Double.parseDouble(t_Str1);
                    }
                    else
                    {
                        if ((m_FV.getLastRow() + 1) == i && t_Type.equalsIgnoreCase("BN"))
                        {
                            //������ڵ�ǰ���в���һ�У�������Ҳ������һ��
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
     * ���������ƴ��
     * @throws Exception
     */
    public void Raise_Report()
            throws Exception
    {
        int t_Data_Count, t_Cur_Data_Row;
        int t_P_End_Count, t_P_Head_Count, t_Page_Data_Count;
        int t_R_Head_Count, t_R_End_Count;
        int t_CurRow; //�����ʽ�ĵ�ǰ����
        int t_Sheet, t_DataColCount, T1;
        CRage t_Rage1 = new CRage();
        CRage t_Rage2 = new CRage();

        //��ʼ������
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Data);
        t_Data_Count = m_FV.getLastRow() + 1;
        t_DataColCount = m_FV.getLastCol() + 1;
        System.out.println("��������" + t_Data_Count);

        t_Cur_Data_Row = conDataStart;
        m_FV.setSheet(P_End);
        t_P_End_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(P_Head);
        t_P_Head_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_Head);
        t_R_Head_Count = m_FV.getLastRow() + 1;
        m_FV.setSheet(R_End);
        t_R_End_Count = m_FV.getLastRow() + 1;
        //End��ʼ������

        Copy_Report_Head("ToReport", 1, 1);
        t_CurRow = t_R_Head_Count + 1;
        mCurPages = 1;

        if (m_Per_Page_Count <= 0)
        {
            //�������Ҫ��ҳ����ֱ�ӿ�������
            //����ҳͷ
            Copy_Page_Head("ToReport", t_CurRow, 1);
            t_CurRow += t_P_Head_Count;
            //End����ҳͷ

            //��������
            t_Rage1.m_Top = 1;
            t_Rage1.m_Left = 1;
            t_Rage1.m_Buttom = t_Data_Count;
            t_Rage1.m_Right = t_DataColCount;
            System.out.println("��������" + t_Data_Count);

            t_Rage2.m_Left = 1;
            t_Rage2.m_Top = t_CurRow;
            Copy_Sheet_Data(t_Rage1, m_FV, R_Data, t_Rage2, m_FV, R_Report, 1);
            m_FV.setSheet(R_Report);
            t_CurRow += m_FV.getLastRow() + 1 + 1;
            //End��������
            //����ҳβ
            Copy_Page_End("ToReport", t_CurRow, 1);
            t_CurRow += t_P_End_Count;
            //End����βͷ
            //��������β
            Copy_Report_End("ToReport", t_CurRow, 1);
            //End��������β

        }
        else
        { //�������ҳͷ��ҳβ
            while (true)
            {
                //����ҳͷ
                Copy_Page_Head("ToReport", t_CurRow, 1);
                t_CurRow += t_P_Head_Count;
                //End����ҳͷ

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
                //��������
                t_Rage1.m_Top = t_Cur_Data_Row;
                t_Rage1.m_Left = 1;
                t_Rage1.m_Buttom = t_Cur_Data_Row + t_Page_Data_Count - 1;
                t_Rage1.m_Right = t_DataColCount;
                t_Rage2.m_Left = 1;
                t_Rage2.m_Top = t_CurRow;
                Copy_Sheet_Data(t_Rage1, m_FV, R_Data, t_Rage2, m_FV, R_Report,
                        1);
                //End��������
                //WriteEx "c:\t2.vts", F1FileFormulaOne6
                t_CurRow += t_Page_Data_Count;

                //����ҳβ
                Copy_Page_End("ToReport", t_CurRow, 1);
                t_CurRow += t_P_End_Count;
                //End����ҳβ
                //WriteEx "c:\t3.vts", F1FileFormulaOne6
                if (t_Data_Count - t_Cur_Data_Row + 1 <= t_Page_Data_Count)
                {
                    //��������β
                    Copy_Report_End("ToReport", t_CurRow, 1);
                    //End��������β
                    break; //exit while
                }

                //�ӷ�ҳ��־
                m_FV.addRowPageBreak(t_CurRow);
                //End�ӷ�ҳ��־
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
     * ����������ʼ��������
     * @throws Exception
     */
    public void Read_Data()
            throws Exception
    {

        /***************************����������ʼ��������*****************************/
        int t_Cur_Row, t_Col;
        String t_Sql, t_DSN, t_Key;
        int t_Row, t_FldCount;
        String t_Str;
        int t_Sheet, t_Row_Count;
        String t_TempS1; //ȡx:x,x:n,n:x,��ͬ
        String t_TempS2;
        String t_T8; //�Ƿ�Ƕ��

        m_ErrString = "";
        t_Sheet = m_FV.getSheet();
        m_FV.setSheet(R_Define);
        t_Cur_Row = m_Cur_Format_Row;
        t_Row_Count = m_FV.getLastRow() + 1;

        /*****************����COL_DATA����������***************/

        DEBUG_OUT("��ʼ����COL_DATA���ݡ�����");
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

                    //�˴���Ҫ�޸ģ�����������滻
                }
                //�滻��������
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
            DEBUG_OUT("��������COL_DATA���ݡ�����");
        } //end while
        /*********************End����COL_DATA����������***********************/

        /*****************����ROW_COL_DATA����������*********************/
        DEBUG_OUT("��ʼ����ROW_COL_DATA���������ݡ�����");
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
                    //Ԥ����x:x,x:n,n:x֮��
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
                //�滻��������
                t_Sql = ReplaceFunc1(t_Sql);

                //�˴���Ҫ�޸ģ�����������滻
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
            DEBUG_OUT("��������ROW_COL_DATA���ݡ�����");

        } //end while

        /*********************End����ROW_COL_DATA����������********************/

        /**************����Row_COL_Text����������*************************/
        DEBUG_OUT("��ʼ����Row_COL_Text����������..........");
        //����Row_Col_Text���������ݣ�ֱ�ӷ���R_Report���Sheet��
        m_FV.setSheet(R_Define);
        m_Row_Col_Text_Start = t_Cur_Row;
        //if(m_Row_Col_Text_Type.substring(0,1).equalsIgnoreCase("1")){
        //�����Ҫ����������ʾRow_Col_Text
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
                    //�滻��������
                    t_Sql = ReplaceValSQL(t_Sql, m_Vals);
                    //���ݿ����
                    t_Str = JRptUtility.GetOneValueBySQL(t_Sql);

                }
                else
                {
                    t_Str = ReplaceValSQL(m_FV.getText(t_Cur_Row - 1, 2).trim(), m_Vals);

                }
                if (!t_Str.trim().equalsIgnoreCase(""))
                {
                    String t_Str1; //�õ���4�е�ֵ
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
                        //�����Ҫ����������ʾRow_Col_Text
                        if (!t_Str.trim().equalsIgnoreCase(""))
                        {
                            m_FV.setSheet(R_Data);
                            m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                            m_FV.setSheet(R_Define);
                        }
                    }
                    if (t_Type.equalsIgnoreCase("1"))
                    {
                        //�����Ҫ�ڱ���ƴ������ʾRow_Col_Text
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
            DEBUG_OUT("��������Row_COL_TEXT����..........");

        } //end while
        //}end if
        /**************End����Row_COL_Text����������*********************/

        m_Cur_Format_Row = t_Cur_Row;
        m_FV.setSheet(t_Sheet);

    }

    /**
     * ��ȡ��ʽ���ڴ��е�m_FV��
     * @param c_FileName �ļ���
     * @return int��
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
            m_ErrString = "ģ���ļ�������,��������ļ�" + c_FileName + "�Ƿ��ڴ��ڣ�";
            return 0;
        }

//        m_FV.setSheet(R_Format);
        m_FV.read(c_FileName);
        System.out.println("��������������sheet����" + m_FV.getNumSheets());
        InitSheets();
        m_FV.setSheet(R_Define);

        //��������Դ�����ӱ������滻����
        // m_ConnectString = ReplaceValSQL(m_ConnectString, m_Vals);

        Copy_Report_Head("", 1, 1);
        Copy_Report_End("", 1, 1);
        Copy_Page_Head("", 1, 1);
        Copy_Page_End("", 1, 1);

        t_Str = m_FV.getText(Row_Count_Per_Page - 1, 1).trim();

        if (!t_Str.equalsIgnoreCase("") && JRptUtility.IsNumeric(t_Str))
        {
            m_Per_Page_Count = Integer.parseInt(m_FV.getText(
                    Row_Count_Per_Page - 1, 1)); //�õ�ÿҳ������
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
            m_ErrString = "����ʽʱ��������:����������б�ʾ";
            DEBUG_OUT("����ʽʱ��������:����������б�ʾ");
            return 0;
        }
        m_Cur_Format_Row = Row_Data_Start;
        return m_Cur_Format_Row; //�˴��Ƿ������ֵ
    }

    /**
     * ����������c_Str�е�ϵͳ����(��ϵͳ��m_Vars ��ϵͳ�Զ���ʱ�����)
     * @param c_Str �����ַ���
     * @param c_Index ���
     * @return �ַ���
     */
    private String Replace_SysVal_InString(String c_Str, int c_Index)
    {
        String t_Str, t_Return = "";
        String t_ValName;
//        String t_ValValue;
        int i = 2;

        t_Str = c_Str.trim();
        t_Return = t_Str;

        //ѭ�������� t_Str ���� "?XXXX?"��ʽ���ڵı���
        while (true)
        {
            //�õ��� i step 2 ������������
            //������  "?AAA?"��ʽ�����ַ�����
            //�ú����õ�c_Str�еĵ�c_i����c_Split�ָ���ַ���
            t_ValName = JRptUtility.Get_Str(t_Str, i, "?");

            //���û�ҵ�,˵���Ѿ�����,���˳�
            if (t_ValName.trim().equalsIgnoreCase(""))
            {
                break;
            }

            //����Ѿ��鵽,���滻��Ӧ�ı���
            t_Return = StrTool.replace(StrTool.unicodeToGBK(t_Return)
                    , StrTool.unicodeToGBK("?" + t_ValName + "?")
                    , StrTool.unicodeToGBK(Get_Sys_Val_Value(t_ValName, c_Index)));

            String ss = Get_Sys_Val_Value(t_ValName, c_Index);

            //������һ��λ��
            i += 2;
        }
        return t_Return;
    }

    /**
     * �������Ĺ������滻����?row:col?�ı���
     * ��ʱ�Ѿ������ݿ���ȡ�����ݴ���ָ����sheet,��row:colָ����ֵ��������
     * @param c_SQL SQL
     * @return �ַ���
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

        //ȡ�õ�һ��?XXX? �ָ���ַ���
        t_Val = JRptUtility.Get_Str(c_SQL, 2, "?").trim();

        //������� ?XXX:XXX?
        if (t_Val.indexOf(":") > 0)
        {
            m_FV.setSheet(R_Data);

            //ѭ������
            while (!t_Val.equalsIgnoreCase(""))
            {
                //���зָ�����
                t_Row_Col = t_Val.split(":");

                if (t_Row_Col.length < 1)
                {
                    //����row:col����ʽ
                    m_FV.setSheet(t_Currentsheet);
                    return c_SQL;
                }
                else
                {
                    //����� "currow"
                    if (t_Row_Col[0].toLowerCase().equalsIgnoreCase("currow"))
                    {
                        t_Row_Col[0] = String.valueOf(this.m_Data_Row);
                    }

                    //����� "curcol"
                    if (t_Row_Col[1].toLowerCase().equalsIgnoreCase("curcol"))
                    {
                        t_Row_Col[1] = String.valueOf(this.m_Data_Col);
                    }

                    //ȡָ�������ֵ
                    t_Value = m_FV.getText(Integer.parseInt(t_Row_Col[0]) - 1
                            , Integer.parseInt(t_Row_Col[1]) - 1);

                    //���ȡ����ֵΪ��,���ñ�־
                    if (t_Value.trim().equalsIgnoreCase(""))
                    {
                        mReplaceCellValue_Numm = true;
                    }

                    //��ȡ����ֵ�滻����Ӧ�ı���

                    c_SQL = StrTool.replace(c_SQL, "?" + t_Val + "?", t_Value);
                    //ȡ��һ�� ?XXXX? �ַ���
                    t_Val = "";
                    t_Val = JRptUtility.Get_Str(c_SQL, 2, "?").trim();

                } //end if
            } //end while
        } //end if

        m_FV.setSheet(t_Currentsheet);
        return c_SQL;
    }

    /**
     * ���������ǰ�һ����"system_define.vts"�ж���ĺ�����
     * �þ���Ĳ����滻�䶨���sql��䣬Ȼ�󷵻�
     * @param c_Func ����
     * @return �ַ���
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

        t_FuncName = JRptUtility.Get_Str(c_Func, 2, "!").trim().toUpperCase(); //�õ���i/2������������

        if (!t_FuncName.equalsIgnoreCase(""))
        {
            t_f1 = t_FuncName.split("[(]");
            t_FuncName = t_f1[0]; //�õ�������
            t_Temp = t_f1[1].substring(0, t_f1.length - 1);
            t_Val = t_Temp.split(","); //�õ��������ֵ������t_Val����
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
                    m_Function_Type = t_F1Val.getText(i - 1, 1); //ȡ�ú������ͣ���ִ��sql���ʱ�õ�
                    break;
                }
            }

            if (!t_Sql.equalsIgnoreCase(""))
            {
                //�ж��Ƿ��в���
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
     * �滻��������
     * @param c_Func ����
     * @return �ַ���
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
        //����������ȫ�ֵı���Ϊnull��ʱ�����ٴζ���ϵͳ�����ļ�
        if (m_SysFBV == null)
        {
            String sVtsFile = getConfigFilePath() + "system_define.vts";
            m_SysFBV = new BookModelImpl();
            //��ϵͳ�����ļ�
            m_SysFBV.read(sVtsFile);
        }
        m_SysFBV.setSheet(1);

        while (true)
        {
            t_ValName = JRptUtility.Get_Str(c_Func, 2, "!").trim().toUpperCase(); //�õ���i/2������������
            if (!t_ValName.equalsIgnoreCase(""))
            {
                t_f1 = t_ValName.split("[(]");
                for (i = 1; i < m_SysFBV.getLastRow() + 1; i++)
                {
                    t_F2 = m_SysFBV.getText(i - 1, 0).split("[(]");

                    if (t_f1[0].trim().toLowerCase().equalsIgnoreCase(t_F2[0].trim().toLowerCase()))
                    {
                        t_Sql = m_SysFBV.getText(i - 1, 2);
                        m_Function_Type = m_SysFBV.getText(i - 1, 1); //��������
                        if (t_f1[1].trim().length() > 1)
                        {
                            //�ж��Ƿ��в���
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
                            //ֻ�е���Ҫִ�����ݿ����ʱ��ִ��
                            //DoEvents//��ִ���ø�����ϵͳ
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
     * ��SQL���Ĳ��������滻,�����ִ�Сд
     * @param c_SQL SQL
     * @param c_Vals ����
     * @return �滻���SQL
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
            //û����Ҫ�滻�Ĳ������򷵻�ԭ��
            return c_SQL;

        }

        //�Դ����д�д��ɾ������ո񣬺����ŵȺŵĴ���
        t_Str = c_SQL;
        t_Str = JRptUtility.Kill_Blank(t_Str);

        t_Str = JRptUtility.Last_Pro_Str(t_Str);
        t_Str = JRptUtility.Kill_Blank(t_Str);

        j = c_Vals.size() - 1;

        t_I = 0;
        //�ַ������տո��֣���������t_StrArray
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

        //�滻fromǰ�ı���������ǹ��̣�Ҳ�����ﴦ��
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
            //����"GROUP"�ȵ�λ��
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
            //û��group,order ��


            //�滻group,order,having��ı���������ͬfromǰ�ı�������
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
            //'�����滻
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
            //'����from��group,order,havingǰ�ı���Ϊ�����

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

        //'������������

        if (t_Str == null)
        {
            t_Str = "";
        }

        t_Str = t_Str.trim();
        t_Str = ReplaceCellVal(t_Str).trim();
        i = 1;

        //'������б�����ȡ����
        t_Find = JRptUtility.Get_Str(t_Str, i * 2, "?");
        //'�����ϵͳ���������滻֮
        t_Replace = Get_Sys_Val_Value(t_Find, 1);

        while (!t_Find.equalsIgnoreCase(""))
        {
            if (!t_Replace.trim().equalsIgnoreCase(""))
            {
//                t_Str = t_Str.replaceAll( "?" + t_Find + "?", t_Replace );
                t_Str = StrTool.replace(t_Str, "?" + t_Find + "?", t_Replace);
            }
            //'�������ұ���
            i += 1;
            t_Find = JRptUtility.Get_Str(t_Str, i * 2, "?");
            t_Replace = Get_Sys_Val_Value(t_Find, 1);
        }

        return t_Str;
    }

    /**
     * ���ñ���ָ��������ĸ�ʽ���ϲ���Ԫ��
     * @param cDestArea Ŀ������
     * @param cRefArea �ο�����
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

        //���úϲ��ĵ�Ԫ������ ROW1:COL1:ROW2:COL2
        String[] tStr = cDestArea.split(":");

        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);
        tEndRow = TransferRowPos(tStr[2], m_FV);
        tEndCol = TransferColPos(tStr[3], m_FV);

        //ȡ�� FORMAT ҳ ��Ӧ��ʽ��Ԫ�������
        String[] str1 = cRefArea.split(":");

        tRefRow = Integer.parseInt(str1[0]);
        tRefCol = Integer.parseInt(str1[1]);

        //����ָ������ȡ�õ�Ԫ��ĸ�ʽ
        m_FV.setSheet(R_Format);
        m_FV.setSelection(tRefRow - 1, tRefCol - 1, tRefRow - 1, tRefCol - 1);
        tCF = m_FV.getCellFormat();

        //����ָ��������ĸ�ʽ���ϲ���Ԫ��
        m_FV.setSheet(R_Report);
        tCF.setMergeCells(true);
        m_FV.setSelection(tStartRow - 1, tStartCol - 1, tEndRow - 1, tEndCol - 1);
        m_FV.setCellFormat(tCF);
        m_FV.setSheet(t_Sheet);

    }

    /**
     * cDestArea��Ҫ���õ����ݣ��ڵ�һҳ�ж��壩��sX:sY:eX:eY
     * �ϲ��ı�������,cRefArea:�������ݵ�λ�ã�����maxrow,maxcolΪ�������õı�����
     * @param cRefArea �ο�����
     * @param cDestArea Ŀ������
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

        // ȡ��Դ����Ԫ������
        String[] tStr = cDestArea.split(":");

        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);
        tEndRow = TransferRowPos(tStr[2], m_FV);
        tEndCol = TransferColPos(tStr[3], m_FV);

        // ȡ��Ŀ���������Ͻǵ�Ԫ������
        String[] str1 = cRefArea.split(":");

        tRefRow = TransferRowPos(str1[0], m_FV);
        tRefCol = TransferColPos(str1[1], m_FV);

        // ����Դ����
        tSurRage.m_Left = tStartCol;
        tSurRage.m_Top = tStartRow;
        tSurRage.m_Right = tEndCol;
        tSurRage.m_Buttom = tEndRow;

        // ����Ŀ������
        tDestRage.m_Left = tRefCol;
        tDestRage.m_Top = tRefRow;
        tDestRage.m_Right = tRefCol + (tEndCol - tStartCol);
        tDestRage.m_Buttom = tRefRow + (tEndRow - tStartRow);

        //���������ݴ� tSurRage �����Ƶ� tDestRage ����
        Copy_Sheet_Data(tSurRage, m_FV, R_Format, tDestRage, m_FV, R_Report, 1);

        // �Ը��ƺ��Ŀ���������ݽ��� Replace_SysVal_InString ����
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
     * �ڱ�������ʱ���õ�����Ԫ���ֵ
     * @param cRefArea �ο�����
     * @param cDataValue ����ֵ
     * @throws Exception
     */
    public void Set_Report_Text1(String cRefArea, String cDataValue)
            throws Exception
    {
        //�ڱ�������ʱ���õ���ֵ

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

        //������������
        tStr = cRefArea.split(":");
        tStartRow = TransferRowPos(tStr[0], m_FV);
        tStartCol = TransferColPos(tStr[1], m_FV);

        if (cDataValue.startsWith("!"))
        {
            // ��"!"�ſ�ͷ���� SQL ���
            t_Sql = JRptUtility.Get_Str(cDataValue, 2, "!");
            //�滻��������
            t_Sql = ReplaceValSQL(t_Sql, m_Vals);
            //����SQL���ȡ����ֵ
            t_Str = JRptUtility.GetOneValueBySQL(t_Sql);
        }
        else
        {
            // ����"!"��ͷ���ǵ�Ԫ���ֱ��ֵ
            t_Str = cDataValue;
        }
        //����Ŀ�굥Ԫ������
        m_FV.setText(tStartRow - 1, tStartCol - 1, t_Str);
        m_FV.setSheet(t_Sheet);
    }

    /**
     * ����Row_COL_Text����������
     * @throws Exception
     */
    public void Set_Row_Col_Text_At_Report_End()
            throws Exception
    {
        /*********************************����Row_COL_Text����������******************/
        //����Row_Col_Text���������ݣ�ֱ�ӷ���R_Report���Sheet��
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
            //ȡ�õ�Ԫ��( m_Row_Col_Text_Start������ , ��һ��)��ֵ
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            if (!t_Str.startsWith("#"))
            {
                //������� ROW_COL_TEXT ����,���˳�
                if (!t_Str.equalsIgnoreCase("ROW_COL_TEXT"))
                {
                    break;
                }

                //ȡ�õ�Ԫ������ (��2��)
                t_Row = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 1, ":"));
                t_Col = Integer.parseInt(JRptUtility.Get_Str(m_FV.getText(t_Cur_Row - 1, 1), 2, ":"));

                //ȡ������ (��3��)
                t_Str = m_FV.getText(t_Cur_Row - 1, 2).trim();

                //�õ���4�е�ֵ (����)
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

                //���Ϊ"1"������Ҫ��ʾ
                if (t_Type.equalsIgnoreCase("1"))
                {
                    //�����Ҫ����������ʾRow_Col_Text
                    if (!t_Str.equalsIgnoreCase(""))
                    {
                        m_FV.setSheet(R_Report);
                        m_FV.setText(t_Row - 1, t_Col - 1, t_Str);
                        m_FV.setSheet(R_Define);
                    }
                }

            }
        }

        /*********************************End ����Row_COL_Text����������*****************/
        m_FV.setSheet(t_Sheet);
    }

    /**
     * ������R_Report�е�c_Rage�����е�ϵͳ����
     * @param c_Rage ����
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
     * ����������ı��������ʽ������
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
            /****************�ϲ��ı�������*********/
            //�õ���1������
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            //���������"#"��ͷ
            if (!t_Str.startsWith("#"))
            {
                if (t_Str.equalsIgnoreCase("REPORT_MERGE"))
                {
                    //�ϲ��ı�������
                    Report_Merge(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1, 2));
                }
            }

            /*****************���ñ�������**********************/
            if (!t_Str.startsWith("#"))
            {
                if (t_Str.equalsIgnoreCase("SET_REPORT_TEXT"))
                {
                    if (m_FV.getText(t_Cur_Row - 1, 2).indexOf(":") < 0)
                    {
                        //���û�а���":"�����ʾȡ��ֵ
                        //���ñ�������
                        Set_Report_Text1(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                                , 2));
                    }
                    else
                    {
                        //���ñ�������
                        Set_Report_Text(m_FV.getText(t_Cur_Row - 1, 1), m_FV.getText(t_Cur_Row - 1
                                , 2));
                    }
                }
            }

        }
        m_FV.setSheet(t_Sheet);
    }

    /**
     * ת��������
     * @param cPosString λ��
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
     * ת��������
     * @param cPosString λ��
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
     * �ϲ�"��"��Ԫ��
     * ����ָ�������귶Χ,�ϲ�ָ���е�˳�����е�������ͬ����
     * @param cControlString �ַ�
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

        //ȡ�ø�����ֵ
        tStr = cControlString.split(":");
        if (tStr.length > 1)
        {
            //ȡ��������
            tCol = Integer.parseInt(tStr[0]);
            //ȡ����ʼ��
            tStartRow = Integer.parseInt(tStr[1]);
            //ȡ�ý�����
            tEndRow = Integer.parseInt(tStr[2]);

            //-1 �������һ��
            if (tEndRow == -1)
            {
                tEndRow = m_FV.getLastRow() + 1;
            }
        }
        else
        {
            //���ָֻ����,ȱʡ��Ϊ������
            tCol = Integer.parseInt(cControlString);
            tStartRow = 1;
            tEndRow = m_FV.getLastRow() + 1;
        }

        //ȡ���е�һ�е�ֵ
        tSameStr = m_FV.getText(tStartRow - 1, tCol - 1);
        tCurRow = tStartRow;

        while (true)
        {
            //������У�����ָ�볬������ʱ�˳�
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
                //�����ǰ��ʼ��ȡ��ֵ�����µĵ�ǰ��,��ǰ�������ƶ�һ��
                tCurRow += 1;
            }
            else
            {
                //�����ǰ��ʼ��ȡ��ֵ�������µĵ�ǰ��,˵��Ҫ��ʼ����һ���µĿ�ʼ��

                //�����ǰ�о��뵱ǰ��ʼ�г���1��,��ϲ���ǰ��ʼ�е���ǰ�еĵ�Ԫ��
                if (tStartRow != tCurRow - 1)
                {
                    //ȡ��ǰ��ʼ�еĵ�Ԫ���ʽ
                    m_FV.setSelection(tStartRow - 1, tCol - 1, tStartRow - 1, tCol - 1);
                    tCF = m_FV.getCellFormat();
                    //���ü��ϲ��ӵ�ǰ��ʼ�е���ǰ�е���һ�еĵ�Ԫ���ʽ
                    m_FV.setSelection(tStartRow - 1, tCol - 1, tCurRow - 1 - 1, tCol - 1);
                    tCF.setMergeCells(true);
                    tCF.setHorizontalAlignment(Format.eHorizontalAlignmentCenter);
                    tCF.setVerticalAlignment(Format.eVerticalAlignmentCenter);
                    tCF.setFontBold(true);
                    m_FV.setCellFormat(tCF);
                }

                //���ÿ�ʼ��Ϊ��ǰ��,����ȡ�õ�ǰ��ʼ�е�ֵ
                tStartRow = tCurRow;
                tSameStr = m_FV.getText(tStartRow - 1, tCol - 1);
            }
        }

        m_FV.setSheet(t_Sheet);
    }

    /**
     * �ϲ�"��"��Ԫ��
     * @param cControlString �ַ�
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

        //ȡ�úϲ��ĵ�Ԫ�������
        tStr = cControlString.split(":");
        if (tStr.length > 1)
        {
            //ȡ����Ҫ�ϲ��е�������
            if (tStr[0].compareTo("#") == 0)
            {
                //�����#�ű�ʾ���һ�н��е�Ԫ��ϲ�
                tRow = m_Data_Row;
            }
            else
            {
                tRow = Integer.parseInt(tStr[0]);
            }
            //ȡ����ʼ������
            tStartCol = Integer.parseInt(tStr[1]);
            //ȡ�ý���������
            tEndCol = Integer.parseInt(tStr[2]);

            //���������Ϊ -1 ����ĩβ
            if (tEndCol == -1)
            {
                tEndCol = m_FV.getLastCol() + 1;
            }
        }
        else
        {
            //ȡ����Ҫ�ϲ��е�������
            tRow = Integer.parseInt(cControlString);
            //Ĭ�ϵ�һ�п�ʼ
            tStartCol = 1;
            //Ĭ�ϵ����һ�н���
            tEndCol = m_FV.getLastCol() + 1;
        }

        //�����������һ��ѭ��������ſ���ʵ��������Ҫ�Ķ�̬�ϲ���Ԫ��
        if (tRow == -1)
        {
            for (int z = 1; z <= m_Data_Row; z++)
            {
                tStartCol = Integer.parseInt(tStr[1]);
                //��ʼ����ʼֵ
                tSameStr = m_FV.getText(z - 1, tStartCol - 1);
                tCurCol = tStartCol;

                while (true)
                {
                    //������У�����ָ�볬������ʱ�˳�
                    if (tStartCol > m_FV.getLastCol() + 1 || tStartCol > tEndCol)
                    {
                        break;
                    }
                    if (tCurCol > m_FV.getLastCol() + 1 + 1 || tCurCol > tEndCol + 1)
                    {
                        break;
                    }

                    //����µĵ�ǰ�е�ֵ�뵱ǰ�ϲ���ʼ�е�ֵ����ͬ,˵��Ҫ��ʼ�µĺϲ���
                    if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(z - 1, tCurCol - 1).trim()))
                    {
                        //����뵱ǰ�ϲ��е�ֵ��ͬ,���ƶ�����һ��
                        tCurCol += 1;
                    }
                    else
                    {
                        //�������ͬ,�ϲ���ǰ�ĺϲ���
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

                        //��ʼȡ�µĺϲ���ʼ�е�ֵ
                        tStartCol = tCurCol;
                        tSameStr = m_FV.getText(z - 1, tStartCol - 1);
                    }
                }
            }
        }
        else
        {
            //��ʼ����ʼֵ
            tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
            tCurCol = tStartCol;

            while (true)
            {
                //������У�����ָ�볬������ʱ�˳�
                if (tStartCol > m_FV.getLastCol() + 1 || tStartCol > tEndCol)
                {
                    break;
                }
                if (tCurCol > m_FV.getLastCol() + 1 + 1 ||
                        tCurCol > tEndCol + 1)
                {
                    break;
                }

                //����µĵ�ǰ�е�ֵ�뵱ǰ�ϲ���ʼ�е�ֵ����ͬ,˵��Ҫ��ʼ�µĺϲ���
                if (tSameStr.trim().equalsIgnoreCase(m_FV.getText(tRow - 1, tCurCol - 1).trim()))
                {
                    //����뵱ǰ�ϲ��е�ֵ��ͬ,���ƶ�����һ��
                    tCurCol += 1;
                }
                else
                {
                    //�������ͬ,�ϲ���ǰ�ĺϲ���
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

                    //��ʼȡ�µĺϲ���ʼ�е�ֵ
                    tStartCol = tCurCol;
                    tSameStr = m_FV.getText(tRow - 1, tStartCol - 1);
                }
            }
        }

        m_FV.setSheet(t_Sheet);

    }

    /**
     * ������������е�Ԫ��ϲ�
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

        //ѭ���������ж���ϲ���Ϣ������
        for (t_Cur_Row = 1; t_Cur_Row <= t_Row_Count; t_Cur_Row++)
        {
            //ȡ�õ�ǰ�����еĵ�һ����Ϣ
            t_Str = m_FV.getText(t_Cur_Row - 1, 0).trim();

            //�ϲ��е�Ԫ��
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                //�����������кϲ���Ϣ
                if (m_FV.getText(t_Cur_Row - 1,
                        0).trim().equalsIgnoreCase("UNITE_ROW"))
                {
                    //�ϲ��е�Ԫ��
                    Unite_Row(m_FV.getText(t_Cur_Row - 1, 1));
                }
            }

            //�ϲ��е�Ԫ��
            if (t_Str.startsWith("#"))
            {
            }
            else
            {
                //�����������кϲ���Ϣ
                if (m_FV.getText(t_Cur_Row - 1,
                        0).trim().equalsIgnoreCase("UNITE_COL"))
                {
                    //�ϲ���Ԫ��
                    Unite_Col(m_FV.getText(t_Cur_Row - 1, 1));
                }
            }
        }

        m_FV.setSheet(t_Sheet);

    }

    /**
     * д��һ��С��,���c_Row=-1,���ʾ��ʹ���һ�е�����
     * @param c_Row ��
     * @param c_ComputeCol С����
     * @param c_Data ����
     * @param c_Type     ������Ϣ
     * @param cGroupCol ��
     * @throws Exception
     */
    public void Write_Sum_X_To_Data(int c_Row, int c_ComputeCol, double c_Data, String c_Type
            , int cGroupCol)
            throws Exception
    {

        String t_Type, t_ColStr;
        int t_Col, t_Row;

        //ȡ����������
        t_Type = c_Type.substring(0, 1).trim();

        //�õ���������ֵ
        t_ColStr = c_Type.substring(1);

        //ȡ������Ϣ,���Ϊ -1 �������һ��
        if (c_Row == -1)
        {
            t_Row = m_FV.getLastRow() + 1;
        }
        else
        {
            t_Row = c_Row;
        }

        // 1. ���������ϢΪ"B"
        if (c_Type.trim().equalsIgnoreCase("B"))
        {
            //��c_Row�еĺ������һ��
            m_FV.insertRange(t_Row - 1 + 1, 0, t_Row - 1 + 1, 0, Constants.eShiftRows);
            //����С����Ϣ
            m_FV.setText(t_Row - 1 + 1, c_ComputeCol - 1, m_SumX_Caption + String.valueOf(c_Data));
        }
        // 2. ���������ϢΪ"BN"
        else if (c_Type.trim().equalsIgnoreCase("BN"))
        {
            m_FV.setText(t_Row - 1 + 1, c_ComputeCol - 1, m_SumX_Caption + String.valueOf(c_Data));
        }
        // 3. ���������ϢΪ"R+�к�"
        else if (c_Type.trim().equalsIgnoreCase("R"))
        {
            if (JRptUtility.IsNumeric(t_ColStr))
            {
                //ȡ���к�
                t_Col = Integer.parseInt(t_ColStr);
            }
            else
            {
                m_ErrString = "Write_Sum_X_To_Data: RX ��ֵ���ԣ�����������";
                return;
            }
            //����ָ�����е�����
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
//            t_Rpt.AddVar("inputtime", "2003��1��");
//            t_Rpt.AddVar("grpname", "CDC�����ֹ�˾");
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
//            t_Rpt.AddVar("GrpName", "����");
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
