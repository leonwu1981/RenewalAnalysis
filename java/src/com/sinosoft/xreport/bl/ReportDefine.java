//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ReportDefine.java

package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bsh.Interpreter;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.util.ExprEval;


public class ReportDefine
{

    DefineReader defineReader;
    String defineFile;
    ExprEval exprEval = new ExprEval();

    public ReportDefine()
    {
        this("");
    }

    public ReportDefine(DefineInfo ri)
    {
        defineFile = ri.getDefineFilePath();
    }

    public ReportDefine(String defineFile)
    {
        this.defineFile = defineFile;
    }

//   public ReportDefine()
//   {}

    public ReportDefine(String branch, String code, String edition)
    {
        this(new DefineInfo(branch, code, edition));
    }


    /**
     * ��Ҫ�ڽ����ƽ���������ʵ��
     * @throws Exception
     */
    public void readDefine() throws Exception
    {
        defineReader = new DefineReader(this.defineFile);
    }

    /**
     * �����û��������.������ʾ��jspҳ����߿ͻ���...
     * @return ��Ҫ�û�ȷ��ֵ�Ĳ�����ѡ���б�
     * @throws Exception
     */
    private Map preCalculate() throws Exception
    {
        return null;
    }

    /**
     * �������
     * @throws Exception
     */
    public void caculate() throws Exception
    {

    }


    ///////////////////////////////////////////////////////
    // ͨ����������confirmParam(Map)
    // �������û��������������userConfirmedParams��
    ///////////////////////////////////////////////////////

    //�û��ύ�����в���
    private Map userConfirmedParams;

    //��������в���,��ʾ���û���Ϊ definedParams-userConfirmedParams
    private Map definedParams;

    /**
     * �û�ȷ��������ֵ.
     * ���û��ȷ��ȫ���Ĳ���,��������������Ҫ�û�ȷ���Ĳ���.ȷ�����,����Ϊnull
     * @param params �û�ȷ���Ĳ���.
     * @return ��û��ȷ���Ĳ���,û�оͷ���Ϊnull
     */
    public Map confirmParams(Map params)
    {
        if (params != null)
        {
            userConfirmedParams.putAll(params);
        }

        //�������
        Map result = new HashMap();
        result.putAll(definedParams);

        Iterator it = userConfirmedParams.keySet().iterator();
        while (it.hasNext())
        {
            result.remove(it.next());
        }

        //clear...
        if (result.isEmpty())
        {
            result = null;
        }

        return result;
    }

    /**
     * �����
     */
    DataBlock[] dataBlockArr;

    /**
     * ���ݿ�ļ���.
     * ���������ʵ�������ݿ�ļ���
     * @throws Exception
     */
    private void dealBlocks() throws Exception
    {
        DefineBlock[] dbArr = defineReader.getDefineBlocks();

        for (int i = 0; i < dbArr.length; i++)
        {
            DefineBlock db = dbArr[i];

            Map global = db.getGlobal();
            Map cols = db.getCols();
            Map rows = db.getRows();
            Map cells = db.getCells();

            calulateBlock(db);

//       cols.get()
        }
    }

    private DataBlock calulateBlock(DefineBlock defineBlock) throws Exception
    {
        return null;
    }


    /**
     * @todo ��ѡ����Դ,����...
     */
    public DataSource[] getAllDataSource()
    {
        DataSource[] dsArr = null;

        return dsArr;
    }


    public DefineInfo getDefineInfo() throws Exception
    {
        return defineReader.getReportInfo();
    }

    public Map getParams() throws Exception
    {
        return defineReader.getParams();
    }

    public DefineBlock[] getDefineBlocks() throws Exception
    {
        return defineReader.getDefineBlocks();
    }

    public Format getFormat() throws Exception
    {
        return defineReader.getFormat();
    }

    /**
     * �滻�û�����
     * ����û�в��,ֻ��ֵ
     * @todo:
     */
    private void replaceParams()
    {

    }

    private void getGlobleString()
    {}

    private void getCols()
    {}

    public static void main(String[] args) throws Exception
    {
        ReportDefine rd = new ReportDefine();
        Interpreter ip = rd.exprEval.getInterpreter();
//     ip.set("a","sum(1+2,3)");
        ip.eval("��=1sum(1+2+3,4);");
        System.out.println(ip.get("��"));
        System.out.println(ip.get("a=sum(1+2,3)"));
    }
}
