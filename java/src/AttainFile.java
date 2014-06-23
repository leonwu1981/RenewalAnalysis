/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

import com.sinosoft.utility.DBConnPool;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * <p>Title: </p>
 * 文件获取及表结构生成
 * <p>Description: </p>
 * 获取必要的schema信息，及表结构更新
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author 朱向峰
 * @version 1.0
 */
public class AttainFile
{
    /**
     * 把拷贝相关信息写死了
     * 而且在jb中使用还算方便
     * 暂时不会添加界面来作配置
     */
    private static final String[] mSpecPostfix =
            {
            "DB.java", "DBSet.java", "Schema.java", "Set.java"};
    private static final String[] mSpecDir =
            {
            "db", "vdb", "schema", "vschema"};

    /**
     * 由于在程序中调用了dos命令，所以写目录地址的时候"\"用"\\"表示，而不能写成"/"
     * 这个问题需要以后添加转换代码来解决
     */
    private static final String mFile = "f:\\work\\temp\\oracle.sql"; //sql脚本
    static final String mInputDir = "f:\\work\\lis\\"; //包含db vdb schema vschema的源目录
    static final String mOutputDir = "f:\\work\\temp\\"; //目标目录
    static final String mLog4jConf = "f:\\work\\AttainFile.properties"; //log4j 配置属性文件

    static Logger traceLogger = Logger.getLogger("trace.sqltrace"); //记录轨迹
    static Logger errorLogger = Logger.getLogger("error.sqlerror"); //记录错误

    public AttainFile()
    {
        PropertyConfigurator.configure(mLog4jConf); //读取配置属性文件设置Logger
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 连接数据库，执行sql语句，拷贝schema
     * @param cFile String
     */
    public void FileList(String cFile)
    {
        //循环处理文件
        Vector tVector = null; //存储表名
        tVector = getTNFromFile(mFile);
        for (int i = 0; i < tVector.size(); i++)
        {
            Connection conn = DBConnPool.getConnection();
            try
            {
                conn.setAutoCommit(false);
                Statement tSm = conn.createStatement();
                //获得执行脚本，需要去除后面的;
                String tSql = tVector.get(i).toString().substring(0
                        , tVector.get(i).toString().length() - 1);
                //判定操作类型
                if (tSql.toLowerCase().indexOf("drop table") != -1)
                {
                    System.out.println("不允许执行drop操作，请手工执行");
                    //记得关闭连接
                    conn.close();
                    break;
                }
                //执行脚本
                tSm.executeUpdate(tSql);
                traceLogger.info("executed sql:" + tSql);
                //截取脚本，获得表名
                String a = tSql.substring(tSql.toLowerCase().indexOf("table ") +
                                          6);
                //执行文件拷贝
                if (FileSearch(a.substring(0, a.indexOf(" "))))
                {
                    //如果成功则提交此次更新
                    conn.commit();
                    traceLogger.info("commit");
                }
                else
                {
                    //如果失败则回滚此次更新
                    conn.rollback();
                    errorLogger.error("error!!rollback");
                }
                conn.close();
            }
            catch (Exception ex)
            {
                errorLogger.error(ex.getMessage());
                try
                {
                    conn.close();
                    break;
                }
                catch (Exception exc)
                {
                    errorLogger.error(exc.getMessage());
                    break;
                }
            }
        }
    }

    /**
     * 查找表所对应的文件
     * @param cTableName String 拷贝的表名
     * @return boolean
     */
    public boolean FileSearch(String cTableName)
    {
        boolean tFlag = true;
        for (int j = 0; j < 4; j++)
        {
            tFlag = true;
            //设定查找目录
            File tFile = new File(mInputDir + mSpecDir[j]);
            //设定查找的文件
            String tFileName = cTableName + mSpecPostfix[j];
            //获得查找目录下的全部文件列表
            String[] tSA = tFile.list();
            //循环列表文件
            for (int m = 0; m < tSA.length; m++)
            {
                //匹配更新的文件
                if (tSA[m].toLowerCase().indexOf(tFileName.toLowerCase()) != -1)
                {
                    //用来保证拷贝的文件名字不发生变化
                    tFileName = tSA[m];
                    //调用Dos命令执行拷贝
                    if (!copyFile(mSpecDir[j] + "\\" + tFileName))
                    {
                        //只有当拷贝文件报错的时候，才返回一个失败状态
                        return false;
                    }
                    else
                    {
                        traceLogger.info("copy file: " + mInputDir + mSpecDir[j] +
                                         "\\" + tFileName);
                    }

                    tFlag = false;
                    break;
                }
            }
            if (tFlag)
            {
                StringBuffer tSBql = new StringBuffer();
                tSBql.append("没有找到要更新的");
                tSBql.append(cTableName);
                tSBql.append("表的文件信息");
                //System.out.println(tSBql.toString());
                errorLogger.error(tSBql.toString());
                break;
            }
        }
        return true;
    }


    /**
     * 文件拷贝 调用dos名令copy拷贝文件
     * @param cFile String
     * @return boolean
     */
    public boolean copyFile(String cFile)
    {
        String copyCommand = "cmd /c copy /y " + mInputDir + cFile +
                             " " + mOutputDir
                             + cFile;
        try
        {
            String dir = copyCommand.substring(copyCommand.lastIndexOf(":\\") -
                                               1,
                                               copyCommand.lastIndexOf("\\")) +
                         "\\";
            System.out.println(dir);
            File mdir = new File(dir);
            if (!mdir.exists())
            {
                mdir.mkdir();
            }
        }
        catch (Exception ex)
        {
            errorLogger.error(ex.getMessage());
        }
        try
        {
            Runtime.getRuntime().exec(copyCommand);

            return true;
        }
        catch (IOException ioe)
        {
            errorLogger.error(mInputDir + cFile + "拷贝文件出错");
            System.out.println("拷贝文件出错");
            return false;
        }
    }


    /**
     * 读取sql语句并取得表名
     * @param cFileName String sql脚本文件
     * @return Vector 存储表名列表
     */
    public Vector getTNFromFile(String cFileName)
    {
        FileReader tFileReader = null;
        BufferedReader tBufferReader = null;
        Vector vector = new Vector();
        try
        {
            tFileReader = new FileReader(cFileName);
            tBufferReader = new BufferedReader(tFileReader);
            String line = tBufferReader.readLine();
            StringBuffer tSBql = new StringBuffer();
            tSBql.append(line);
            while (line != null)
            {
                if (line.indexOf(";") != -1)
                {
                    vector.add(tSBql.toString());
                    tSBql = new StringBuffer();
                    line = tBufferReader.readLine();
                    tSBql.append(line);
                }
                else
                {
                    line = tBufferReader.readLine();
                    tSBql.append(line);
                }
            }
            tBufferReader.close();
            tFileReader.close();
        }
        catch (Exception e)
        {
            System.out.println("sql脚本内容为空。");
        }
        return vector;
    }

    /**
     * 主函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
        AttainFile tAttainFile = new AttainFile();
        try
        {
            tAttainFile.FileList("LDCom");
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
    }
}
