package com.sinosoft.xreport.util;

import java.io.File;

import bsh.Interpreter;

/**
 * 表达式处理类.查看#main()里的使用方法.
 *
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class ExprEval
{
    private Interpreter parser = new Interpreter();
    private File funcDefFile;

    static String FUNCTIONFILE = SysConfig.FUNCTIONFILE;

    /**
     * 构造函数
     */
    public ExprEval()
    {
        this(FUNCTIONFILE);
//        try{
//            funcDefFile = parser.pathToFile("function.def/function.bsh");
//            parser.source(funcDefFile.getAbsolutePath());
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * 指明funtion.bsh的位置
     * @param fileDefName 函数定义文件位置
     */
    public ExprEval(String fileDefName)
    {
        try
        {
//            funcDefFile = parser.pathToFile(fileDefName);
            parser.source(fileDefName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 得到计算结果.
     * @param expr 表达式
     * @return 计算结果
     * @throws Exception
     */
    public Object calculate(String expr) throws Exception
    {
//        System.out.println(expr);
        return parser.eval(expr);
//        Vector vRet = new Vector();
//        String sRet;
//        if (result instanceof Integer){
//            sRet = ((Integer)result).toString();
//            vRet.add(((Integer)result).toString());
//        }else if (result instanceof Long){
//            vRet.add(((Long)result).toString());
//        }else if (result instanceof Float){
//            vRet.add(((Float)result).toString());
//        }else if (result instanceof Double){
//            vRet.add(((Double)result).toString());
//        }else if (result instanceof String){
//            vRet.add(result);
//        }else if (result instanceof Vector){
//            vRet = (Vector)result;
//        }else{
//            vRet.add("0");
//        }
//        return vRet;
    }

    /*
         private getSQLResultSet()
         {

         }*/

    public Interpreter getInterpreter()
    {
        return parser;
    }


    public static void main(String[] args) throws Exception
    {
        ExprEval parser = new ExprEval();
//        Vector v, r;
//        String a;
//        v = parser.calculate("sum(3,4)");
//        String sql = "select count(*) a from branch where branchid='000000'";
//        v = parser.calculate("execSQL(\"" + sql + "\")");
//        for(int i = 0; i < v.size(); i++){
//            if (v.elementAt(i) instanceof String){
//                a = (String)v.elementAt(i);
//                System.out.println(a);
//            }else if (v.elementAt(i) instanceof Vector){
//                r = (Vector)v.elementAt(i);
//                for(int j = 0; j< r.size(); j++){
//                    String colVal = (String)r.elementAt(j);
//                    System.out.print(colVal + "\t");
//                }
//                System.out.println();
//            }
//        }
    }
}
