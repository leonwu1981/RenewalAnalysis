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
 * �ļ���ȡ����ṹ����
 * <p>Description: </p>
 * ��ȡ��Ҫ��schema��Ϣ������ṹ����
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author �����
 * @version 1.0
 */
public class AttainFile
{
    /**
     * �ѿ��������Ϣд����
     * ������jb��ʹ�û��㷽��
     * ��ʱ������ӽ�����������
     */
    private static final String[] mSpecPostfix =
            {
            "DB.java", "DBSet.java", "Schema.java", "Set.java"};
    private static final String[] mSpecDir =
            {
            "db", "vdb", "schema", "vschema"};

    /**
     * �����ڳ����е�����dos�������дĿ¼��ַ��ʱ��"\"��"\\"��ʾ��������д��"/"
     * ���������Ҫ�Ժ����ת�����������
     */
    private static final String mFile = "f:\\work\\temp\\oracle.sql"; //sql�ű�
    static final String mInputDir = "f:\\work\\lis\\"; //����db vdb schema vschema��ԴĿ¼
    static final String mOutputDir = "f:\\work\\temp\\"; //Ŀ��Ŀ¼
    static final String mLog4jConf = "f:\\work\\AttainFile.properties"; //log4j ���������ļ�

    static Logger traceLogger = Logger.getLogger("trace.sqltrace"); //��¼�켣
    static Logger errorLogger = Logger.getLogger("error.sqlerror"); //��¼����

    public AttainFile()
    {
        PropertyConfigurator.configure(mLog4jConf); //��ȡ���������ļ�����Logger
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
     * �������ݿ⣬ִ��sql��䣬����schema
     * @param cFile String
     */
    public void FileList(String cFile)
    {
        //ѭ�������ļ�
        Vector tVector = null; //�洢����
        tVector = getTNFromFile(mFile);
        for (int i = 0; i < tVector.size(); i++)
        {
            Connection conn = DBConnPool.getConnection();
            try
            {
                conn.setAutoCommit(false);
                Statement tSm = conn.createStatement();
                //���ִ�нű�����Ҫȥ�������;
                String tSql = tVector.get(i).toString().substring(0
                        , tVector.get(i).toString().length() - 1);
                //�ж���������
                if (tSql.toLowerCase().indexOf("drop table") != -1)
                {
                    System.out.println("������ִ��drop���������ֹ�ִ��");
                    //�ǵùر�����
                    conn.close();
                    break;
                }
                //ִ�нű�
                tSm.executeUpdate(tSql);
                traceLogger.info("executed sql:" + tSql);
                //��ȡ�ű�����ñ���
                String a = tSql.substring(tSql.toLowerCase().indexOf("table ") +
                                          6);
                //ִ���ļ�����
                if (FileSearch(a.substring(0, a.indexOf(" "))))
                {
                    //����ɹ����ύ�˴θ���
                    conn.commit();
                    traceLogger.info("commit");
                }
                else
                {
                    //���ʧ����ع��˴θ���
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
     * ���ұ�����Ӧ���ļ�
     * @param cTableName String �����ı���
     * @return boolean
     */
    public boolean FileSearch(String cTableName)
    {
        boolean tFlag = true;
        for (int j = 0; j < 4; j++)
        {
            tFlag = true;
            //�趨����Ŀ¼
            File tFile = new File(mInputDir + mSpecDir[j]);
            //�趨���ҵ��ļ�
            String tFileName = cTableName + mSpecPostfix[j];
            //��ò���Ŀ¼�µ�ȫ���ļ��б�
            String[] tSA = tFile.list();
            //ѭ���б��ļ�
            for (int m = 0; m < tSA.length; m++)
            {
                //ƥ����µ��ļ�
                if (tSA[m].toLowerCase().indexOf(tFileName.toLowerCase()) != -1)
                {
                    //������֤�������ļ����ֲ������仯
                    tFileName = tSA[m];
                    //����Dos����ִ�п���
                    if (!copyFile(mSpecDir[j] + "\\" + tFileName))
                    {
                        //ֻ�е������ļ������ʱ�򣬲ŷ���һ��ʧ��״̬
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
                tSBql.append("û���ҵ�Ҫ���µ�");
                tSBql.append(cTableName);
                tSBql.append("����ļ���Ϣ");
                //System.out.println(tSBql.toString());
                errorLogger.error(tSBql.toString());
                break;
            }
        }
        return true;
    }


    /**
     * �ļ����� ����dos����copy�����ļ�
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
            errorLogger.error(mInputDir + cFile + "�����ļ�����");
            System.out.println("�����ļ�����");
            return false;
        }
    }


    /**
     * ��ȡsql��䲢ȡ�ñ���
     * @param cFileName String sql�ű��ļ�
     * @return Vector �洢�����б�
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
            System.out.println("sql�ű�����Ϊ�ա�");
        }
        return vector;
    }

    /**
     * ������
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
