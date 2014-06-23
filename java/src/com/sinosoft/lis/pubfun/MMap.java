/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.Schema;
import com.sinosoft.utility.SchemaSet;
import org.apache.log4j.Logger;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ����Զ�BLS��Map�࣬�ݲ�֧��Remove����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class MMap
{
    /** ���ݵ����� Map Vector */
    private HashMap mapV = null;

    /** ��������� Map Order */
    private HashMap mapO = null;
    
    /** mmap��������**/
    public static int Max_Warn_Size;
    private int mRepeatCount = 0;  //δ���put���������������ͻ���⣬���ӱ��ֶ�������¼�ظ�����
    public static Logger m_log = Logger.getLogger(MMap.class);
    static{
    	Max_Warn_Size = 100;
    	ExeSQL tExeSql = new ExeSQL();
    	String tMax_Warn_Size = tExeSql.getOneValue("select a.sysvarvalue from ldsysvar a where a.sysvar='map_warn_size'");
    	if(tMax_Warn_Size!=null && !tMax_Warn_Size.equals("")){
    		Max_Warn_Size = Integer.parseInt(tMax_Warn_Size);
    	}
    }

    /**
     * ���캯��
     */
    public MMap()
    {
        mapV = new HashMap();
        mapO = new HashMap();
    }

    /**
     * ��������ֵ�ԣ���Ŵ�1��ʼ
     * @param key Object
     * @param value Object
     */
    public void put(Object key, Object value)
    {
    	if (key == null || value == null)
            return;
        
        String errMsg=null;
//        if(mapV.containsKey(key)){
//            String className = key.getClass().getName();
//            if(className.endsWith("String")){
//            	errMsg = "repeatinfo ��ʾ���ظ���MMap�з���String������ȷ���Ƿ������ظ���";
//            }else if(className.endsWith("Schema")){
//            	errMsg = "repeatinfo ��ʾ���ظ���MMap�з���"+className.substring(className.lastIndexOf(".")+1)+"������ȷ���Ƿ������ظ���";
//            }else if(className.endsWith("Set")){
//            	errMsg = "repeatinfo ��ʾ���ظ���MMap�з���"+className.substring(className.lastIndexOf(".")+1)+"������ȷ���Ƿ������ظ���";
//            }
//            m_log.error("MMap�ظ�����"+errMsg);
//        	try {
//        		throw new Exception(errMsg);
//            } catch (Exception ex) {
//        		m_log.error(ex.getMessage(), ex);
//            }
//        }
        if(mapV.size()>=MMap.Max_Warn_Size){
        	errMsg = "mmap��������������ֵ"+MMap.Max_Warn_Size+"��ʵ��ֵΪ"+mapV.size();
//        	m_log.error(errMsg);
        	try {
        		throw new Exception(errMsg);
            } catch (Exception ex) {
        		m_log.error(ex.getMessage(), ex);
            }
        }
        //Tracy add �����ظ�����map���޷��ύ���⣬�޸ķ�ʽ����QA�����ʼ��ķ�ʽ�޸ģ�
        if(mapV.containsKey(key)){
            mRepeatCount++;  //��¼key�ظ��Ĵ���
            String className = key.getClass().getName();

            if(className.endsWith("String")){

                String keyStr = (String)key;
                for(int i = 0; i < mRepeatCount; i++){
                    keyStr += " ";
                }
                key = keyStr;
            }
            else if(className.endsWith("Schema")){
                key = cloneSchema((Schema)key);
            }else if(className.endsWith("Set")){
                key = cloneSchemSet((SchemaSet) key);
            }
        }
        //End
        mapV.put(key, value);
        mapO.put(String.valueOf(mapV.size()), key);
    }
    
    /**
     * cloneSchema
     * �õ�cOldSchema�Ŀ�¡�����������쳣�޷���¡���򷵻�ԭ����
     * ʹ��java������ƣ�����Schema��getSchema����������һ���µ�Schema����
     * ��������������Schema��������ԭSchema�Ĳ�һ��
     * @param cOldSchema Schema����Ҫ��¡�Ķ���
     * @return Schema
     */
    private Schema cloneSchema(Schema cOldSchema){
        String methodName = "getSchema";
        Class[] paramType = new Class[0];
        Method method = null;

        try{
            method = cOldSchema.getClass().getMethod(methodName, paramType);
            Object[] args = new Object[0];
            return (Schema) method.invoke(cOldSchema, args);
        }catch(NoSuchMethodException ex){
            System.out.println("û���ҵ�getSchema����");
            ex.printStackTrace();
            return cOldSchema;
        }catch(InvocationTargetException ex){
            System.out.println("�޷�Clone Schema");
            ex.printStackTrace();
            return cOldSchema;
        }catch(IllegalAccessException ex){
            System.out.println("�޷�Clone Schema");
            ex.printStackTrace();
            return cOldSchema;
        }
    }
    
    /**
     * �õ�cOldSchemaSet�Ŀ�¡�����������쳣�޷���¡���򷵻�ԭ����
     * ʹ��java������ƣ�����cOldSchemaSet�����͵���SchemaSet����newSchemaSet��
     * ����cOldSchemaSet�е�ÿ��Schema�����¡��newSchemaSet
     * ��������������SchemaSet��������ԭSchemaSet��һ��
     * @param cOldSchemaSet SchemaSet
     * @return SchemaSet
     */
    private SchemaSet cloneSchemSet(SchemaSet cOldSchemaSet){
        String schemaSetName = cOldSchemaSet.getClass().getName();
        try{
            Class schemaSetClass = Class.forName(schemaSetName);
            SchemaSet newSchemaSet = (SchemaSet) schemaSetClass.newInstance();
            newSchemaSet.add(cOldSchemaSet);
            return newSchemaSet;
        }catch(ClassNotFoundException ex){
            System.out.println("û���ҵ���" + schemaSetName);
            ex.printStackTrace();
            return cOldSchemaSet;
        }catch(IllegalAccessException ex){
            System.out.println("�޷�Clone Schema");
            ex.printStackTrace();
            return cOldSchemaSet;
        }catch(InstantiationException ex){
            System.out.println("�޷�Clone Schema");
            ex.printStackTrace();
            return cOldSchemaSet;
        }
    }

    /**
     * ��ȡ����ֵSet
     * @return Set
     */
    public Set keySet()
    {
        return mapV.keySet();
    }

    /**
     * ���ݼ���ȡֵ
     * @param key Object
     * @return Object
     */
    public Object get(Object key)
    {
        return mapV.get(key);
    }

    /**
     * ��ȡ����Map
     * @return HashMap
     */
    public HashMap getOrder()
    {
        return mapO;
    }

    /**
     * ͨ����Ż�ȡ������ż�����˳�򣬴�1��ʼ
     * @param order String
     * @return Object
     */
    public Object getKeyByOrder(String order)
    {
        return mapO.get(order);
    }

    /**
     * ���һ��MMap
     * @param srcMap MMap
     */
    public void add(MMap srcMap)
    {
        if (srcMap == null)
        {
            return;
        }
        for (int i = 0; i < srcMap.keySet().size(); i++)
        {
            Object key = srcMap.getKeyByOrder(String.valueOf(i + 1));
            this.put(key, srcMap.get(key));
        }
    }

    public static void main(String[] args)
    {
//        MMap amap = new MMap();
//        amap.put("key1", "value1");
//        amap.put("key2", "value2");
//        MMap bmap = new MMap();
//        bmap.put("keyb1", "valueb1");
//        bmap.put("keyb2", "valueb2");
//        amap.add(bmap);
//        for (int i = 0; i < amap.keySet().size(); i++)
//        {
//            Object key = amap.getKeyByOrder(String.valueOf(i + 1));
//            System.out.println(amap.get(key).toString());
//        }
    }
}
