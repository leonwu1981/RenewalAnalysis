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
     * 需要在交叉和平面表中重新实现
     * @throws Exception
     */
    public void readDefine() throws Exception
    {
        defineReader = new DefineReader(this.defineFile);
    }

    /**
     * 处理用户传入参数.可以提示到jsp页面或者客户端...
     * @return 需要用户确定值的参数和选择列表
     * @throws Exception
     */
    private Map preCalculate() throws Exception
    {
        return null;
    }

    /**
     * 计算入口
     * @throws Exception
     */
    public void caculate() throws Exception
    {

    }


    ///////////////////////////////////////////////////////
    // 通过反复调用confirmParam(Map)
    // 将所有用户传入参数保存在userConfirmedParams中
    ///////////////////////////////////////////////////////

    //用户提交的所有参数
    private Map userConfirmedParams;

    //定义的所有参数,提示给用户的为 definedParams-userConfirmedParams
    private Map definedParams;

    /**
     * 用户确定参数的值.
     * 如果没有确定全部的参数,继续返回所有需要用户确定的参数.确定完后,返回为null
     * @param params 用户确定的参数.
     * @return 还没有确定的参数,没有就返回为null
     */
    public Map confirmParams(Map params)
    {
        if (params != null)
        {
            userConfirmedParams.putAll(params);
        }

        //集合相减
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
     * 结果块
     */
    DataBlock[] dataBlockArr;

    /**
     * 数据块的计算.
     * 报表计算其实就是数据块的计算
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
     * @todo 遴选数据源,条件...
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
     * 替换用户参数
     * 假设没有层次,只有值
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
        ip.eval("陀=1sum(1+2+3,4);");
        System.out.println(ip.get("陀"));
        System.out.println(ip.get("a=sum(1+2,3)"));
    }
}
