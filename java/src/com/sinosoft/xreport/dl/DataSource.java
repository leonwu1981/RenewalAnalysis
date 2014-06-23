package com.sinosoft.xreport.dl;

import java.util.Collection;
import java.util.Map;

public interface DataSource
{

    /**
     * 执行sql返回数据集.注意实现要取出全部数据后释放连接
     * @param sql sql查询语句
     * @return 结果集
     * @throws DBExecption 出错信息
     */
    public Collection getDataSet(String sql) throws DBExecption, Exception;


    /**
     * 得到所有的数据源定义
     * @return 以Collection形返回所有的数据源 Collection是Source型
     * @throws DBExecption 读取时发生的错误
     */
    public Collection getDataSourceDefine() throws DBExecption, Exception;

    /**
     * 得到所有的维定义
     * @return 以Collection形返回所有的维 Collection是Dim型
     * @throws DBExecption 读取时发生的错误
     */
    public Collection getDimensionDefine() throws Exception, DBExecption;

    /**
     * 得到以strDataSourceId为id号的数据源中所有的维定义
     * @return 以Collection形返回满足条件的维 Collection是Dim型
     * @throws DBExecption 读取时发生的错误
     */
    public Collection getDimensionDefine(String strDataSourceId) throws
            DBExecption, Exception;

    /**
     * 得到某个数据源字段的所有取值.Collection or TreeNode
     * @param tableColumn 表名.字段名
     * @param parent 参数,如果参数=null,则提示用户先选择维的上一层次的值
     * @return 返回所有取值 Vector型或TreeNode型
     * @throws DBExecption DB错误
     */
    public Object getValue(String tableColumn, Map parent) throws DBExecption,
            Exception;

    /**
     * 得到所有表间字段的关联关系 Collection
     * @return 返回所有关联 Collection
     * @throws DBExecption DB错误
     */
    public Collection getRelations() throws DBExecption, Exception;
}
