package com.sinosoft.xreport.pl;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;

import com.sinosoft.xreport.bl.BlockParams;
import com.sinosoft.xreport.bl.ReportMain;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.dl.DataSourceImpl;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XReader;
import com.sinosoft.xreport.util.XTLogger;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameCommitParams extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Report report;
    private JLabel jLabelParams = new JLabel();
    private JScrollPane jScrollPaneParam = new JScrollPane();
    private JButton jButtonOK = new JButton();
    private JButton jButtonCancel = new JButton();
    private JPanel jPanelParam = new JPanel();
    private GridLayout gridLayout = new GridLayout();

    private JLabel[] label;
    private JComboBox[] box;

    public FrameCommitParams()
    {
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public FrameCommitParams(Report report)
    {
        this.report = report;
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void open(String params)
    {
        if (!params.equals(""))
        {
            init(params);
            this.setSize(270, 380);
            this.show();
        }
        else
        {
            String stream = XReader.readCalculate(new HashMap());
            XTLogger.getLogger(this.getClass()).info(stream);
        }
    }

    private void init(String params)
    {
        Collection c;
        Object[] obj;
        //拆分params找出所有的参数和参数的值
        Vector vecParams = BlockParams.getParams(params);
        int intLength = vecParams.size();
        label = new JLabel[intLength];
        box = new JComboBox[intLength];
        if (intLength > 5)
        {
            gridLayout.setRows(2 * intLength);
        }
        try
        {
            DataSource data = new DataSourceImpl();
            //利用获得的参数修改窗口界面
            for (int i = 0; i < vecParams.size(); i++)
            {
                /**初始化控件*/
                label[i] = new JLabel();
                box[i] = new JComboBox();
                box[i].setEditable(true);
                Map map = (Map) vecParams.elementAt(i);
                /**设置标签内容*/
                label[i].setText((String) map.get("tips"));
                /**将控件添加到窗口中*/
                jPanelParam.add(label[i], null);
                jPanelParam.add(box[i]);
                /**读取参数的代码*/
                c = data.getDataSet((String) map.get("showMode"));
                obj = c.toArray();
                Vector vecValue = new Vector();
                for (int j = 0; j < obj.length; j++)
                {
                    String[] strValue = (String[]) obj[j];
                    vecValue.addElement(strValue[0]);
                }
                box[i].setModel(new DefaultComboBoxModel(vecValue));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //利用showMode参数的值读取所有参数的代码
        //利用获得的代码修改窗口界面
    }

    private void jbInit() throws Exception
    {
        jButtonCancel.setBounds(new Rectangle(138, 292, 63, 27));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCancel();
            }
        });
        jButtonOK.setBounds(new Rectangle(52, 292, 63, 27));
        jButtonOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOK.setText("确定");
        jButtonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonOk();
            }
        });
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        jLabelParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelParams.setText("报表计算参数");
        jLabelParams.setBounds(new Rectangle(33, 27, 72, 19));
        jScrollPaneParam.setBounds(new Rectangle(33, 56, 188, 215));
        jPanelParam.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelParam.setLayout(gridLayout);
        gridLayout.setColumns(1);
        gridLayout.setRows(10);
        this.getContentPane().add(jScrollPaneParam, null);
        jScrollPaneParam.getViewport().add(jPanelParam, null);
        this.getContentPane().add(jLabelParams, null);
        this.getContentPane().add(jButtonOK, null);
        this.getContentPane().add(jButtonCancel, null);
    }

    public static void main(String[] args)
    {
        FrameCommitParams param = new FrameCommitParams();
        ReportMain report = new ReportMain();
        report.setBranchId("330700");
        report.setReportId("js01");
        report.setReportEdition("200201");
        param.open(XReader.readPrecal(report));
    }

    void buttonCancel()
    {
        this.dispose();
    }

    void buttonOk()
    {
        Map map = new HashMap();
        String strKey = "", strValue = "";
        /**从组合框中读取计算参数*/
        for (int i = 0; i < box.length; i++)
        {
            strKey = (String) box[i].getModel().getElementAt(0);
            strValue = (String) box[i].getModel().getSelectedItem();
            map.put(strKey, strValue);
        }
        String stream = XReader.readCalculate(map);
        StringTokenizer token = new StringTokenizer(stream,
                SysConfig.SEPARATORONE);
        String flag = "", file = "";
        flag = (String) token.nextElement();
        file = (String) token.nextElement();
        if (flag.equals("ok"))
        {
            report.open(file);
        }
        else
        {
            XTLogger.getLogger(this.getClass()).info(file);
        }
        //设置Report对象的状态
        this.dispose();
    }
}