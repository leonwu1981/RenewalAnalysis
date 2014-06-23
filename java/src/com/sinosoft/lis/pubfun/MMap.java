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
 * <p>Title: Web业务系统</p>
 * <p>Description: 配合自动BLS的Map类，暂不支持Remove方法</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class MMap
{
    /** 数据的容器 Map Vector */
    private HashMap mapV = null;

    /** 排序的容器 Map Order */
    private HashMap mapO = null;
    
    /** mmap容量警告**/
    public static int Max_Warn_Size;
    private int mRepeatCount = 0;  //未解决put方法引起的索引冲突问题，增加本字段用来记录重复次数
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
     * 构造函数
     */
    public MMap()
    {
        mapV = new HashMap();
        mapO = new HashMap();
    }

    /**
     * 建立键－值对，序号从1开始
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
//            	errMsg = "repeatinfo 提示：重复往MMap中放入String对象，请确认是否有意重复！";
//            }else if(className.endsWith("Schema")){
//            	errMsg = "repeatinfo 提示：重复往MMap中放入"+className.substring(className.lastIndexOf(".")+1)+"对象，请确认是否有意重复！";
//            }else if(className.endsWith("Set")){
//            	errMsg = "repeatinfo 提示：重复往MMap中放入"+className.substring(className.lastIndexOf(".")+1)+"对象，请确认是否有意重复！";
//            }
//            m_log.error("MMap重复对象："+errMsg);
//        	try {
//        		throw new Exception(errMsg);
//            } catch (Exception ex) {
//        		m_log.error(ex.getMessage(), ex);
//            }
//        }
        if(mapV.size()>=MMap.Max_Warn_Size){
        	errMsg = "mmap的容量超过警告值"+MMap.Max_Warn_Size+"，实际值为"+mapV.size();
//        	m_log.error(errMsg);
        	try {
        		throw new Exception(errMsg);
            } catch (Exception ex) {
        		m_log.error(ex.getMessage(), ex);
            }
        }
        //Tracy add 处理重复放入map中无法提交问题，修改方式根据QA发送邮件的方式修改；
        if(mapV.containsKey(key)){
            mRepeatCount++;  //记录key重复的次数
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
     * 得到cOldSchema的克隆对象，若发生异常无法克隆，则返回原对象
     * 使用java反射机制，调用Schema的getSchema方法，生成一个新的Schema对象，
     * 这样可以做到新Schema的索引与原Schema的不一样
     * @param cOldSchema Schema：需要克隆的对象
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
            System.out.println("没有找到getSchema方法");
            ex.printStackTrace();
            return cOldSchema;
        }catch(InvocationTargetException ex){
            System.out.println("无法Clone Schema");
            ex.printStackTrace();
            return cOldSchema;
        }catch(IllegalAccessException ex){
            System.out.println("无法Clone Schema");
            ex.printStackTrace();
            return cOldSchema;
        }
    }
    
    /**
     * 得到cOldSchemaSet的克隆对象，若发生异常无法克隆，则返回原对象
     * 使用java反射机制，生成cOldSchemaSet类类型的新SchemaSet对象newSchemaSet，
     * 并将cOldSchemaSet中的每个Schema对象克隆到newSchemaSet
     * 这样可以做到新SchemaSet的索引与原SchemaSet不一样
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
            System.out.println("没有找到类" + schemaSetName);
            ex.printStackTrace();
            return cOldSchemaSet;
        }catch(IllegalAccessException ex){
            System.out.println("无法Clone Schema");
            ex.printStackTrace();
            return cOldSchemaSet;
        }catch(InstantiationException ex){
            System.out.println("无法Clone Schema");
            ex.printStackTrace();
            return cOldSchemaSet;
        }
    }

    /**
     * 获取键－值Set
     * @return Set
     */
    public Set keySet()
    {
        return mapV.keySet();
    }

    /**
     * 根据键获取值
     * @param key Object
     * @return Object
     */
    public Object get(Object key)
    {
        return mapV.get(key);
    }

    /**
     * 获取排序Map
     * @return HashMap
     */
    public HashMap getOrder()
    {
        return mapO;
    }

    /**
     * 通过序号获取键，序号即插入顺序，从1开始
     * @param order String
     * @return Object
     */
    public Object getKeyByOrder(String order)
    {
        return mapO.get(order);
    }

    /**
     * 添加一个MMap
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
