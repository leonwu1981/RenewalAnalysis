package com.sinosoft.xreport.pl;

import java.awt.event.ActionEvent;

import com.sinosoft.xreport.bl.Code;


/**
 * <p>Title: test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author unascribed
 * @version 1.0
 */

public class FrameAddColumn1 extends FrameAddColumn
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**父窗口*/
    FrameEditColumn frmParent = null;
    /**代码表*/
    Code code = new Code();

    public FrameAddColumn1()
    {
        super();
    }

    public FrameAddColumn1(FrameEditColumn frmParent)
    {
//        super(frmParent.frmParent);
//        this.frmParent=frmParent;
    }

    public static void main(String args[])
    {
        FrameAddColumn1 frame = new FrameAddColumn1();
        frame.setSize(350, 500);
        frame.init();
        frame.show();
    }

    void jButtonOk_actionPerformed(ActionEvent e)
    {
//        /**读取被选择的表及字段*/
//        TreePath path=jTreeColumn.getSelectionPath();
//        if(path==null)
//            return;
//        if(!((DefaultMutableTreeNode)path.getLastPathComponent()).isLeaf())
//            return;
//        /**读取表信息*/
//        TableDefineScm scmTableDefine=(TableDefineScm)((DefaultMutableTreeNode)path.getPathComponent(1)).getUserObject();
//        /**读取字段信息*/
//        FieldDefineScm scmFieldDefine=(FieldDefineScm)((DefaultMutableTreeNode)path.getPathComponent(2)).getUserObject();
//        /**向父窗口中添加选择结果*/
//        String strId=scmTableDefine.getTableId()+"."+scmFieldDefine.getFieldId();
//        frmParent.jTextColumn.setText(strId);
//        this.dispose();
    }
}
