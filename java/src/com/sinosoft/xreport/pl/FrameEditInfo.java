package com.sinosoft.xreport.pl;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.sinosoft.xreport.bl.Code;

/**
 * <p>Title: test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class FrameEditInfo extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**代码表*/
    private Code code = new Code();
    /**父窗口句柄*/
    private FrameGlobalInfo frmParent;


    public FrameEditInfo()
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

    public FrameEditInfo(FrameGlobalInfo frmParent)
    {
        super(frmParent);
        this.frmParent = frmParent;
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        this.setTitle("全局信息");
        this.getContentPane().setLayout(null);
    }

    public void init()
    {

    }

    public static void main(String[] args)
    {
        FrameEditInfo info = new FrameEditInfo();
        info.setSize(360, 420);
        info.init();
        info.show();
    }

    void commit()
    {
        this.dispose();
    }

    void cancel()
    {
        this.dispose();
    }

}