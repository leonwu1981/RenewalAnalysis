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

public class FrameAddColumn3 extends FrameAddColumn
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**������*/
    FrameEditColumn frmParent = null;
    /**�����*/
    Code code = new Code();

    public FrameAddColumn3()
    {
        super();
    }

    public FrameAddColumn3(FrameEditColumn frmParent)
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
//        /**��ȡ��ѡ��ı��ֶ�*/
//        TreePath path=jTreeColumn.getSelectionPath();
//        if(path==null)
//            return;
//        if(!((DefaultMutableTreeNode)path.getLastPathComponent()).isLeaf())
//            return;
//        /**��ȡ����Ϣ*/
//        TableDefineScm scmTableDefine=(TableDefineScm)((DefaultMutableTreeNode)path.getPathComponent(1)).getUserObject();
//        /**��ȡ�ֶ���Ϣ*/
//        FieldDefineScm scmFieldDefine=(FieldDefineScm)((DefaultMutableTreeNode)path.getPathComponent(2)).getUserObject();
//        /**�򸸴��������ѡ����*/
//        String strId=scmTableDefine.getTableId()+"."+scmFieldDefine.getFieldId();
//        frmParent.jTextReplace.setText(strId);
//        this.dispose();
    }
}
