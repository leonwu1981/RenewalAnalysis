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
        //���params�ҳ����еĲ����Ͳ�����ֵ
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
            //���û�õĲ����޸Ĵ��ڽ���
            for (int i = 0; i < vecParams.size(); i++)
            {
                /**��ʼ���ؼ�*/
                label[i] = new JLabel();
                box[i] = new JComboBox();
                box[i].setEditable(true);
                Map map = (Map) vecParams.elementAt(i);
                /**���ñ�ǩ����*/
                label[i].setText((String) map.get("tips"));
                /**���ؼ���ӵ�������*/
                jPanelParam.add(label[i], null);
                jPanelParam.add(box[i]);
                /**��ȡ�����Ĵ���*/
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
        //����showMode������ֵ��ȡ���в����Ĵ���
        //���û�õĴ����޸Ĵ��ڽ���
    }

    private void jbInit() throws Exception
    {
        jButtonCancel.setBounds(new Rectangle(138, 292, 63, 27));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCancel();
            }
        });
        jButtonOK.setBounds(new Rectangle(52, 292, 63, 27));
        jButtonOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOK.setText("ȷ��");
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
        jLabelParams.setText("����������");
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
        /**����Ͽ��ж�ȡ�������*/
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
        //����Report�����״̬
        this.dispose();
    }
}