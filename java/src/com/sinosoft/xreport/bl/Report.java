//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Report.java
/**
 * chang log:
 * yang   2003-4-7 11:27  修改了直接执行'行列sql'--recalCol & recalRow 中的 or 的优先级问题:必须加(),否则取出大量错误数值
 */

package com.sinosoft.xreport.bl;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import bsh.Interpreter;
import com.sinosoft.xreport.dl.BufferDataSourceImpl;
import com.sinosoft.xreport.dl.DBExecption;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;

/**
 * @todo:DataMap可能数据有问题,数据有问题:经常整行整列数据相同.
 * @todo:数值精度,科学计数的问题.
 */

/**
 * 报表类.包含定义和数据.
 * 1.计算前需要先设定环境Environment,设定报表定义的参数Parameter...
 * 2.
 * @todo 读取定义
 * @todo 解析所有函数和变量
 * @todo 形成数据对象
 * @todo 写数据
 * @todo 2003-03-24 Yang 作如下假设:
 * 规则报表是:
 * 1) 行与行之间(自上而下的行)仅有一个字段取值发生改变; e.g.明细表,险种发生改变
 * 2) 列与列之间,可能是与行一样,也可能是字段发生改变.
 * 根据以上假设,可以简化规则数据块的取数... select a,b,c from T where t in (x,y,z)
 * or select a from T where a in (row1,row2,) and b in (col1,col2);
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class Report implements DataSource
{

    //定义
    private ReportDefine reportDefine;
    //数据
    private ReportData reportData;

    //系统参数,环境
    private Environment runEnv;
    //表达式计算器...
    private Interpreter interpreter;

    //
    Logger log;

    //bq
    //sq
    //tq
    RE bqRE;
    RE sqRE;
    RE tqRE;


    /**
     * 根据几个信息创建定义对象.
     * @param defineBranch
     * @param code
     * @param edition
     */
    public Report(String defineBranch, String code, String edition)
    {
        reportDefine = new ReportDefine(defineBranch, code, edition);
    }

    /**
     * 创建一个Report对象.
     * @param defineFile 定义文件
     */
    public Report(String defineFile)
    {
        reportDefine = new ReportDefine(defineFile);
    }


    ////////////////////////////
    //假设所有数据准备完全
    ////////////////////////////

    /**
     * 计算入口.
     * @return 数据文件位置
     * @throws Exception 计算错误
     */
    public ReportData calculate() throws Exception
    {
        reportDefine.readDefine();
        DefineInfo defineInfo = reportDefine.getDefineInfo();
        DefineBlock[] defineBlockArr = reportDefine.getDefineBlocks();
//    reportDefine.getParams();

        //construct a DataInfo
        DataInfo dataInfo = new DataInfo();
        constructDataInfo(defineInfo, dataInfo);
        reportData.setDataInfo(dataInfo);

//    log.debug(reportData.getDataInfo().toXMLString());

        //build data by each DefineBlock
        Map dataBlocksMap = new HashMap();

        //初始化解析器环境...
        setInterpreterEnv();

        for (int i = 0; i < defineBlockArr.length; i++)
        {
            DefineBlock defineBlock = defineBlockArr[i];

            //read...
            Map globals = defineBlock.getGlobal();
            Map colHeaders = defineBlock.getCols();
            Map rowHeaders = defineBlock.getRows();
            Map spCells = defineBlock.getCells();
            Format format = defineBlock.getFormat();

            //全局参数...
//      String globleWhere=getGlobleWhere(globals);

            //colHeaders
            DataBlock dataBlock = dealBlock(defineBlock);

            dataBlocksMap.put(dataBlock.getName(), dataBlock);
        }

        reportData.setDataBlocks(dataBlocksMap);
        reportData.setFormat(reportDefine.getFormat());

        return reportData;
    }

    /**
     * 一块的计算.
     * @param colHeaders
     * @param rowHeaders
     * @param spCells
     * @throws Exception
     */
    private DataBlock dealBlock(DefineBlock defineBlock) throws Exception
    {

        //build DataBlock
        DataBlockBuilder dataBlockBuilder = new DataBlockBuilder();
        DataBlock dataBlock = dataBlockBuilder.createDataBlock(defineBlock);

        Map colHeaders = defineBlock.getCols();
        Map rowHeaders = defineBlock.getRows();
        Map spCells = defineBlock.getCells();

        //处理condition中的变量,函数
        dealConditionVarFunc(colHeaders, rowHeaders);

        Map tableMap = new HashMap();

        //整个报表的取数字段集合
        Set dataSourceSet = new HashSet();
        //整个报表的where字段集合
        Set wcSet = new HashSet();

        //colHeaders
        Iterator itCol = colHeaders.keySet().iterator();
        while (itCol.hasNext())
        {
            Object key = itCol.next();
            Object value = colHeaders.get(key); //一列头

            //表名,列名集合
            //..取formula
            Map col = (Map) value;
            String formula = (String) col.get(BlockElement.FORMULA);
            String condition = (String) col.get(BlockElement.CONDITION);

            //得到select 取数列
//      String[] selectItems=getSelectItems(formula); //
            XRRegExp.getAllDS(formula, dataSourceSet); //所有取数列放入

            //所有condition
            XRRegExp.getWhereClause(condition, wcSet);

        }

        //colHeaders
        Iterator itRow = rowHeaders.keySet().iterator();
        while (itRow.hasNext())
        {
            Object key = itRow.next();
            Object value = rowHeaders.get(key); //一列头

            //表名,列名集合
            //..取formula
            Map row = (Map) value;
            String formula = (String) row.get(BlockElement.FORMULA);
            String condition = (String) row.get(BlockElement.CONDITION);

///////////////////////////////////////////////////////
// 应用假设 1) 决大多数数据源在列头上,行上的到下次计算时再处理
///////////////////////////////////////////////////////

            //得到select 取数列
//      String[] selectItems=getSelectItems(formula); //
//      XRRegExp.getAllDS(formula,dataSourceSet); //所有取数列放入
            //块计算时不考虑行上数据源,主要是多个数据源需要合并,解决关联问题

            //所有condition
            XRRegExp.getWhereClause(condition, wcSet);

        }
        //行列ok

////////////////////////////////
// 1)第一轮计算:规则数据块.
///////////////////////////////


        //一块又一块数据呀
        Map selectItems = getSelectFrom(dataSourceSet);
//.......................
        //一个又一个条件
        getWhereClause(defineBlock, selectItems, wcSet);

        /////////////////////////////
        // 规则的sql组合完了,执行后放在便于查询的结构中
        ////////////////////////////
        DataMap dm = new DataMap(selectItems);
        dm.setDataSource(dbDataSource);
        dm.setTableRalation(tableRelations);
        //build DataMap complete.

//    log.debug(((ColData)dataBlock.getColDataMap().get("转回未到期责任准备金收入")).cells);
//    log.debug(dataBlock.getRowDataMap());

        //第一轮计算
        putData(defineBlock, dataBlock, dm);
        //将大多数数据放到Data类

        //第二轮...........................
        //计算不规则的行列
        //条件中有或的,有从其它报表取数的,行头上给我来数据源的,等着,第二轮干掉
        calculateSPColRowCell(defineBlock, dataBlock);

        //计算单独单元格

        //计算表间参照行列

        //caution: 表内行列参照是按命名,所以先替换命名.
        //表内行列参照不应是位置,支持?
        //所有中文-replace

        //计算表内参照行列
        setColRow2Interpreter(defineBlock, dataBlock);

        //从解析器中取出数据放入dataBlock中
        getDataFromInterpreter(dataBlock);

//    log.debug("\n\n"+dataBlock.toXMLString());

        return dataBlock;
        //写数据
    }

    /**数据格式*/
    java.text.DecimalFormat decimalFormat = new DecimalFormat(SysConfig.
            DECIMALPATTERN);

    private void getDataFromInterpreter(DataBlock dataBlock) throws Exception
    {
        //[位置->cell]Map
        Map locationCellMap = dataBlock.getLocationMap();

        Iterator itLocKey = locationCellMap.keySet().iterator();
        while (itLocKey.hasNext())
        {
            String loc = (String) itLocKey.next();

            Object object = locationCellMap.get(loc); //行头,列头也在里边,小心.

            if (!(object instanceof Cell))
            {
                continue;
            }

            Cell cell = (Cell) locationCellMap.get(loc);

            interpreter.eval("auto_temp=get$" + loc + "()");
            Object value = interpreter.get("auto_temp");
            String strValue;

            log.debug("inpreter's value:" + value);

            if (value == null || "".equals(value))
            {
                strValue = "0";
            }
            else
            {
                //数值格式化
                try
                {
                    double d = Double.parseDouble(value.toString());
                    strValue = decimalFormat.format(d);
                }
                catch (Exception e)
                {
                    strValue = value.toString();
                }
            }

            if ("NaN".equals(strValue)) //not a number!
            {
                strValue = "0.00";
            }

            if ("Infinity".equalsIgnoreCase(strValue))
            {
                strValue = "0.00";
            }

            log.debug("final value:" + loc + "->" + strValue);

            cell.setValue(strValue);
        } //while
    } //buildLocationIndex()

    DataSource dbDataSource;
    TableRelations tableRelations;

    public void init() throws Exception
    {
        reportData = new ReportData();

        log = XTLogger.getLogger(this.getClass());

        //DataSource由外部设置,可以由用户选择是否使用buffer
//    dbDataSource=new DataSourceImpl();

        //tableRelation由外部设置,可以由用户选择是否使用buffer
//    tableRelations=new TableRelations(dbDataSource);
        //表内参照的pattern
//    reRefSelf=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        reRefSelf = new RE("[^\\w\\.]*\\[([^\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        //表间参照的pattern
        reRefOther = new RE("[^\\w\\.]*\\[.*\\]\\.\\[.*\\][^\\w\\.]*");

        //表内单元格参照 //\[(.*?)\]\.\[(.*?)\]
        reRefSelfCell = new RE("\\[(.*?)\\]\\.\\[(.*?)\\]");

        /*
            re    =\$bq\(\[(.*?)\]\.\[(.*?)\](\.\[(.*)\])?\)
            test  =$bq([11].[22].[33])+$sq()
            result=Matches.

            $0 = $bq([11].[22].[33])
            $1 = 11
            $2 = 22
            $3 = .[33]
            $4 = 33

         */


        bqRE = new RE("\\$bq\\(\\[(.*?)\\]\\.\\[(.*?)\\](\\.\\[(.*)\\])?\\)");
        sqRE = new RE("\\$sq\\(\\[(.*?)\\]\\.\\[(.*?)\\](\\.\\[(.*)\\])?\\)");
        tqRE = new RE("\\$tq\\(\\[(.*?)\\]\\.\\[(.*?)\\](\\.\\[(.*)\\])?\\)");

        interpreter = new Interpreter();

        interpreter.source(SysConfig.FUNCTIONFILE);

//    dbDataSource.getDataSourceDefine();
    }

    /**
     * 第一轮计算.把第一次根据按块取数取出的数据放到各个格上去
     * @param defineBlock
     * @param dataBlock
     * @param dm
     * @throws Exception
     */
    private void putData(DefineBlock defineBlock, DataBlock dataBlock,
                         DataMap dm) throws Exception
    {
        Iterator itColKey = defineBlock.getCols().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            Map colHeaderDefine = (Map) defineBlock.getCols().get(colName);

            //get content
            String formula = (String) colHeaderDefine.get(BlockElement.FORMULA);
            String condition = (String) colHeaderDefine.get(BlockElement.
                    CONDITION);

            //init
            Set dsSet = new HashSet();
            Set wcSet = new HashSet();

            //确定数据源,条件s
            XRRegExp.getAllDS(formula, dsSet);
            XRRegExp.getWhereClause(condition, wcSet);

            //小心...................
            //按格设值了..............有点早么?? maybe...
            Iterator itRowKey = defineBlock.getRows().keySet().iterator();
            while (itRowKey.hasNext())
            {
                String rowName = (String) itRowKey.next();
                Map rowHeaderDefineMap = (Map) defineBlock.getRows().get(
                        rowName);

                //行上条件
                String rowCondition = (String) rowHeaderDefineMap.get(
                        BlockElement.CONDITION);
                //build it
                Set wcRowSet = new HashSet();
                XRRegExp.getWhereClause(rowCondition, wcRowSet);

                //组合行列条件
                wcRowSet.addAll(wcSet);

                //确定放数的cell
                Cell cell = (Cell) ((ColData) dataBlock.getColDataMap().get(
                        colName)).getCells().get(rowName);
                String cellValue = formula;

                //哇,一个中有多个数据源,e.g. summary_balance.balance_dest+summary_balance.balance_sour
                Object[] dsArr = dsSet.toArray();

                for (int i = 0; i < dsArr.length; i++)
                {
                    String value = dm.getValue((String) dsArr[i], wcRowSet);
//          RE re=new RE("\\b"+dsArr[i]+"\\b");

                    cellValue = StringUtility.replaceWord(cellValue,
                            dsArr[i].toString(), value);
                    //替换完了....
                }

                log.debug("putData: cell.location:" + cell.getLocation() +
                          ",current formula:" + formula);

                //下次再算...
                cell.setValue(cellValue);
            }
        }
    }

    /**
     * 第二轮计算,计算非规则行列:从其它报表表取数的,条件中有"or"的,是>=,<=,<>,>,<,!=,直接是sql函数的,特殊格.
     * @param defineBlock
     * @param DataBlock
     * @throws Exception
     */
    private void calculateSPColRowCell(DefineBlock defineBlock,
                                       DataBlock dataBlock) throws Exception
    {
        //找"or"条件,和非等于条件...
        //为什么"or"需要单独计算,而不使用已经取出数据相加获得,主要是防止嵌套条件,
        //通过调用DB的sql引擎直接计算...最大灵活性原则!!!
        Iterator itColKey = defineBlock.getCols().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            Map colHeaderDefine = (Map) defineBlock.getCols().get(colName);

            //get content
            String formula = (String) colHeaderDefine.get(BlockElement.FORMULA);
            String condition = (String) colHeaderDefine.get(BlockElement.
                    CONDITION);

            //非规则sql检查 顺便把表间取数做了吧,省得再循环..
            //重新计算这一列.
            recalcCol(colName, formula, condition, defineBlock, dataBlock);
        }

        //he ma
        //检查行上的特殊条件,formula
        //while... row key
//    Iterator itRowKey=defineBlock.getRows().keySet().iterator();
//    while(itRowKey)

        //从这一刻.就是这一刻: 2003-3-30 15:32. 我做一个重大的改变,决定使用函数处理来
        //代替循环的从库取出数值来替换(笨拙的方式),一切都有函数(行为)来做,我用程序来生成script,交给beanShell去做吧.
        //避免许多递归计算的问题,字符串分析等问题

        //至少还可以按列取数.
//    dataBlock.getCell()
        Iterator itRowKey = dataBlock.getRowDataMap().keySet().iterator();
        while (itRowKey.hasNext())
        {
            String rowName = (String) itRowKey.next();

            //get row data
            RowData rowData = (RowData) dataBlock.getRowDataMap().get(rowName);
            String formula = (String) rowData.getDefineContentMap().get(
                    BlockElement.FORMULA);
            String condition = (String) rowData.getDefineContentMap().get(
                    BlockElement.CONDITION);

            recalcRow(rowName, formula, condition, defineBlock, dataBlock);

        }

        //替换表内取数
        //表内取数可以是[列].[行];也可以是[列]或者[行]
//    Iterator itRowKey=dataBlock.getRowDataMap().keySet().iterator();
        //special cells
        //...放到了colRow2Int()中


    }

    //表内参照的pattern
    RE reRefSelf; //=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");
    //表间参照的pattern
    RE reRefOther; //=new RE("[^\\w\\.]*\\[.*\\]\\.\\[.*\\][^\\w\\.]*");

    /**
     * 计算特殊行.
     * 第二轮计算的行计算部分.
     * 计算非规则行列:有formula的直接替换formula,没有的判断条件是否是规则,有无表间
     * 取数,表内取数,go
     * @param colName
     * @param formula
     * @param condition
     * @param defineBlock
     * @param dataBlock
     * @throws Exception
     */
    private void recalcRow(String rowName, String formula, String condition,
                           DefineBlock defineBlock, DataBlock dataBlock) throws
            Exception
    {
        //有formula?
        boolean hasFormula = !(null == formula || "".equals(formula));
        //是特殊条件行?
        boolean isSPRow = !XRRegExp.onlyAndCondition(condition);

        if (!hasFormula && !isSPRow)
        {
            return;
        }

        if (hasFormula) //行上有formula,不逮,重算,还可以有表间取数,表内取数...
        {
            //有表间参照
            boolean hasRefOther = XRRegExp.hasRefOtherReports(formula);
            //有表内参照
            boolean hasRefSelf = XRRegExp.hasRefSelf(formula);

            //多数据源
            Set dsSet = new HashSet();
            XRRegExp.getAllDS(formula, dsSet);

            Iterator itColKey = dataBlock.getColDataMap().keySet().iterator();
            while (itColKey.hasNext())
            {
                String colName = (String) itColKey.next();
                Map colHeaderDefineMap = ((ColData) dataBlock.getColDataMap().
                                          get(colName)).getDefineContentMap();
                String colCondition = (String) colHeaderDefineMap.get(
                        BlockElement.CONDITION);
                String colFormula = (String) colHeaderDefineMap.get(
                        BlockElement.FORMULA);
                //放数的cell

                log.debug(colName + ":" + rowName);

                Cell cell = (Cell) ((RowData) dataBlock.getRowDataMap().get(
                        rowName)).getCells().get(colName);
                String cellValue = cell.getValue();
                if (cellValue == null || "".equals(cellValue))
                {
                    cellValue = "0";
                }

                //////////////////////////////
                //[?数据源?]动态数据源
                cellValue = StringUtility.replace(formula, SysConfig.DYNAMICDS,
                                                  cellValue);
                //There maybe are some bugs.
                /**@todo: 怎么:如果条件不规则,[?数据源?]会始终是0,这样会导致错误,
                 * 如果这样,应当重新计算[?数据源?]的值.very easy.但是没时间去做了.
                 */

                //处理数据源
                Object[] dsArr = dsSet.toArray();
                for (int i = 0; i < dsArr.length; i++)
                {
                    String ds = (String) dsArr[i];
                    String currentTable = StringUtility.getLeftWord(ds,
                            ds.indexOf("."));

                    StringBuffer sb = new StringBuffer();

                    sb.append("select ")
                            .append(ds)
                            .append(" from ")
                            .append(currentTable)
                            .append(getGlobalWhere(defineBlock, currentTable));

                    //加()
                    if (null != condition && !"".equals(condition))
                    {
                        sb.append(" and (")
                                .append(tableRelations.getActualCondition(
                                condition, currentTable))
                                .append(")");
                    }

                    if (null != colCondition && !"".equals(colCondition))
                    {
                        sb.append(" and (")
                                .append(tableRelations.getActualCondition(
                                colCondition, currentTable))
                                .append(")");
                    }

                    String value = getOneValueFromDB(sb.toString());

//          RE re=new RE("\\b"+dsArr[i]+"\\b");
                    cellValue = StringUtility.replaceWord(cellValue,
                            dsArr[i].toString(), value); //替换cellValue
                } //for
                // 数据源处理ok
                /////////////////////

                if (hasRefOther) //表间参照处理
                {
                    /**@todo: 根据实际的rql调整*/
//          ReportDataFactory rdf=ReportDataFactory.getInstance();
//          ReportData reportData=rdf.getReportData();
                    cellValue = replaceRef(cellValue, colName, ROWREF);
                }

                log.debug("in recalcRow cell.location:" + cell.getLocation() +
                          ",cellValue:" + cellValue);

                if (hasRefSelf)
                {
                    //表内
                    //取得目标单元格的位置...must
                    while (reRefSelf.match(cellValue))
                    {
                        String refRowName = reRefSelf.getParen(1); //参照的 行 名
                        String rql = "[" + colName + "].[" + refRowName + "]";
                        String location = (String) dataBlock.getCell(rql).
                                          getLocation();
                        String reStr = "get$" + location + "()";

                        cellValue = StringUtility.replace(cellValue,
                                "[" + refRowName + "]", reStr);
                    }
                } //替换表内参照over

                cell.setValue(cellValue);

            } //end while

        } //end if
        else //只是列上formula定义的特例,有可能有or,多个and...
        {
            if (isSPRow)
            {
                //shit,列上重新计算了,表间,表内又要重新替换了.
                //由于行上没有formula,使用列上的formula了啦.

                Iterator itColKey = dataBlock.getColDataMap().keySet().iterator();
                while (itColKey.hasNext())
                {
                    String colName = (String) itColKey.next();
                    Map colHeaderDefineMap = ((ColData) dataBlock.getColDataMap().
                                              get(colName)).getDefineContentMap();
                    String colCondition = (String) colHeaderDefineMap.get(
                            BlockElement.CONDITION);
                    String colFormula = (String) colHeaderDefineMap.get(
                            BlockElement.FORMULA);
                    //放数的cell
                    Cell cell = (Cell) ((RowData) dataBlock.getRowDataMap().get(
                            rowName)).getCells().get(colName);
//          String cellValue=cell.getValue();
//          if(cellValue==null||"".equals(cellValue))
//            cellValue="0";

                    //讨厌,需要重算了啦
                    String cellValue = colFormula;

                    Set dsSet = new HashSet();
                    XRRegExp.getAllDS(colFormula, dsSet); //取列的所有数据源

                    //处理数据源
                    Object[] dsArr = dsSet.toArray();
                    for (int i = 0; i < dsArr.length; i++)
                    {
                        String ds = (String) dsArr[i];
                        String currentTable = StringUtility.getLeftWord(ds,
                                ds.indexOf("."));

                        StringBuffer sb = new StringBuffer();

                        sb.append("select ")
                                .append(ds)
                                .append(" from ")
                                .append(currentTable)
                                .append(getGlobalWhere(defineBlock,
                                currentTable));

                        if (null != condition && !"".equals(condition))
                        {
                            sb.append(" and (")
//              .append(condition);  //行上条件需要转换.--yang at 2003-4-15 15:28 in MinSheng
                                    .append(tableRelations.getActualCondition(
                                    condition, currentTable))
                                    .append(")");
                        }

                        if (null != colCondition && !"".equals(colCondition))
                        {
                            sb.append(" and (")
//              .append(colCondition);  //使用的列上的formula,列上的条件无须转换?
                                    .append(tableRelations.getActualCondition(
                                    colCondition, currentTable)) //转换!不考虑效率
                                    .append(")");
                        }

                        String value = getOneValueFromDB(sb.toString());

//            RE re=new RE("\\b"+dsArr[i]+"\\b");
                        cellValue = StringUtility.replaceWord(cellValue,
                                dsArr[i].toString(), value); //替换cellValue
                    } //for
                    // 数据源处理ok
                    /////////////////////

                    if (XRRegExp.hasRefOtherReports(colFormula)) //表间参照处理
                    {
                        /**@todo: 根据实际的rql调整*/
//          ReportDataFactory rdf=ReportDataFactory.getInstance();
//          ReportData reportData=rdf.getReportData();
//            cellValue=replaceRef(cellValue,colName,ROWREF);
//错在行上没有FORMULA时使用的是列上的FOMULA,其实是列参照.
                        cellValue = replaceRef(cellValue, rowName, COLREF); //列参照
                    }

                    if (XRRegExp.hasRefSelf(colFormula))
                    {
                        log.debug(cellValue + ":colName:" + colName +
                                  ":rowName:" + rowName);

                        //表内
                        //取得目标单元格的位置...must
                        while (reRefSelf.match(cellValue))
                        {
//              String refRowName=reRefSelf.getParen(1); //参照的 行 名
//              String rql="["+colName+"].["+refRowName+"]";
                            //上面是逻辑错误,行上没有Formula,使用的是列上的Formula,所以,只可能是列上的参照
                            //..........................................
                            //
                            String refColName = reRefSelf.getParen(1); //参照的 行 名
                            String rql = "[" + refColName + "].[" + rowName +
                                         "]";
                            String location = (String) dataBlock.getCell(rql).
                                              getLocation();
                            String reStr = "get$" + location + "()";

                            cellValue = StringUtility.replace(cellValue,
                                    "[" + refColName + "]", reStr);
                        }
                    } //替换表内参照over

                    log.debug("in recalRow,isSPRow,loc:" + cell.getLocation() +
                              ",value:" + cellValue);

                    cell.setValue(cellValue);

                } //end while
            }
        }

    }


    /**
     * 计算列上特殊条件.
     * @param colName
     * @param formula
     * @param condition
     * @param defineBlock
     * @param dataBlock
     * @throws Exception
     */
    private void recalcCol(String colName, String formula, String condition,
                           DefineBlock defineBlock, DataBlock dataBlock) throws
            Exception
    {

        if (formula == null)
        {
            log.debug("NULL COL FORMULA,colName" + colName + ":" + condition);
            return;
        }

        //多数据源
        Set dsSet = new HashSet();
        XRRegExp.getAllDS(formula, dsSet);

        //是特殊列
        boolean isSPCol = !XRRegExp.onlyAndCondition(condition);
        //有指向其它表的取数
        boolean hasRefOther = XRRegExp.hasRefOtherReports(formula);
        //有无表内取数...?   added by yang at 2003-3-30 17:51
        boolean hasRefSelf = XRRegExp.hasRefSelf(formula); //行头,列头只做补齐,做成[列名].[行名],在到Cell中去取值.

        //都哞有
        if (!isSPCol && !hasRefOther && !hasRefSelf)
        {
            return;
        }

        Object[] dsArr = dsSet.toArray();

//    for(int i=0;i<dsArr.length;i++)
//    {
//      String currentDS=dsArr[i];
//      String currentTable=StringUtility.getLeftWord(currentDS,currentDS.indexOf("."));

        //condition有问题
        //按行循环
        Iterator itRowKey = defineBlock.getRows().keySet().iterator();
        while (itRowKey.hasNext())
        {
            String rowName = (String) itRowKey.next();
            Map rowHeaderDefineMap = (Map) defineBlock.getRows().get(rowName);

            //确定放数的cell
            Cell cell = (Cell) ((ColData) dataBlock.getColDataMap().get(colName)).
                        getCells().get(rowName);

            //
            String cellValue = cell.getValue();

            //没有值.very importtant
            if (cellValue == null)
            {
                cellValue = formula;
            }

            if (isSPCol)
            {

                /***************************************************************************/
                /**************************有数据源都应该重算*********************************/
                /***************************************************************************/
                cellValue = formula; //所有数据源都重来...

                for (int i = 0; i < dsArr.length; i++)
                {
                    String currentDS = (String) dsArr[i];
                    String currentTable = StringUtility.getLeftWord(currentDS,
                            currentDS.indexOf("."));

                    //行上条件
                    String rowCondition = (String) rowHeaderDefineMap.get(
                            BlockElement.CONDITION);
                    //build it
                    StringBuffer sb = new StringBuffer();

                    sb.append("select ")
                            .append(currentDS)
                            .append(" from ")
                            .append(currentTable) //表名
                            .append(" ")
                            .append(getGlobalWhere(defineBlock, currentTable));

                    //必须加()
                    if (null != condition && !"".equals(condition))
                    {
                        sb.append(" and (")
                                .append(condition) //列上条件
                                .append(")");
                    }

                    if (null != rowCondition && !"".equals(rowCondition))
                    {
//            //行上的条件需要转换,取条件左边表达式,转换为本表的
//            Set subWCSet=new HashSet();
//            XRRegExp.getWhereClause(rowCondition,subWCSet);
//            Iterator itSubKey=subWCSet.iterator();
//            while(itSubKey.hasNext())
//            {
//              WhereClause wc=(WhereClause) itSubKey.next();
//              String actualField=tableRelations.getActualField(wc.getLeft(),currentTable);
//
//              if(actualField==null) //非本表字段
//              {
//                rowCondition=StringUtility.replace(rowCondition,wc.context," 1=1 "); //取消条件,做成1=1
//              }
//
//              log.debug("HERE ERROR:"+wc);
//
//              RE re=new RE("\\b"+wc.getLeft()+"\\b");
//              rowCondition=StringUtility.replaceWord(rowCondition,wc.getLeft(),actualField);
//            }
//
//            sb.append(" and (")
//            .append(rowCondition) //行上条件
//            .append(")");

                        sb.append(" and (")
                                .append(tableRelations.getActualCondition(
                                rowCondition, currentTable)) //行上条件
                                .append(")");
                    }

                    String value = getOneValueFromDB(sb.toString());

//          RE re=new RE("\\b"+dsArr[i]+"\\b");
                    cellValue = StringUtility.replaceWord(cellValue,
                            dsArr[i].toString(), value); //替换cellValue
                } //特殊数据值ok
            }

//      if(hasRefOther)
//      {
//        //表间取数...
//        /**@todo: 定位其它表*/
//        ReportData otherReport=ReportDataFactory.getInstance().getReportData("");
//        /**@todo:为每行设置值*/
//        //替换cellValue
//        //替换为$bq("[].[].[]")即可
//      }

            //表间参照,当前支持$bq(),$tq(),$sq()
            cellValue = replaceRef(cellValue, rowName, COLREF);

            if (hasRefSelf)
            {
                //表内
                //取得目标单元格的位置...must
                while (reRefSelf.match(cellValue))
                {
                    String refColName = reRefSelf.getParen(1); //参照的列名
                    String rql = "[" + refColName + "].[" + rowName + "]";
                    String location = (String) dataBlock.getCell(rql).
                                      getLocation();
                    String reStr = "get$" + location + "()";

                    cellValue = StringUtility.replace(cellValue,
                            "[" + refColName + "]", reStr);
                }

            } //替换表内参照over

            log.debug("after recalCol:" + cell.getLocation() + ":" + cellValue);

            cell.setValue(cellValue); //设置值
        }
    }

//  private String getGlobleWhere(String tableName)
//  {
//    return "where 1=1 ";
//  }

    /**
     * 得到每个表对应的where条件
     * @todo: 会有两个表关联的合并问题. important.
     * @param selectItems 表的前半部分
     * @param wcSet 所有where子句集合
     * @return 每个表的where子句
     * @throws Exception
     */
    private void getWhereClause(DefineBlock defineBlock, Map selectItems,
                                Set wcSet) throws Exception
    {
        Map result = new HashMap();
        // 每列的可能取值[表名.列名->取值Set]
        Map colValues = new HashMap();

        //处理wcSet,将每个字段取值都放入了colValues,以便构造in () 子句.
        Iterator itWc = wcSet.iterator();
        while (itWc.hasNext())
        {

            WhereClause wc = (WhereClause) itWc.next();

            Set valueSet = (Set) colValues.get(wc.left);
            if (valueSet == null)
            {
                valueSet = new HashSet();
                colValues.put(wc.left, valueSet);
            }

            ///////////////////////////////////////////////////
            // 规则的数据块只处理相等的情况
            // very important
            ///////////////////////////////////////////////////
            if ("=".equals(wc.cp))
            {
                valueSet.add(wc.right);
            }
        }
        //...ok,已经将字段取值都放入了colValues了


        Iterator itTabs = selectItems.keySet().iterator();

        //每个表找它满足的条件
        while (itTabs.hasNext())
        {
            String currentTable = (String) itTabs.next();
            //select 项目
            StringBuffer selectClause = (StringBuffer) selectItems.get(
                    currentTable);
            selectClause.append(getGlobalWhere(defineBlock, currentTable)); //放上where及全局的条件

            //字段取值集合
            Iterator it = colValues.keySet().iterator();
            while (it.hasNext()) //字段 集合
            {
                //表名.字段名
                String currentColumn = (String) it.next();
                String actualCon = ""; //字段组成的in子句
                String actualField = ""; //实际在本表的条件字段,到时候也需要放到select 中间去

                actualField = tableRelations.getActualField(currentColumn,
                        currentTable);

                if (actualField != null) //有匹配的字段
                {
                    if (!((Set) colValues.get(currentColumn)).isEmpty()) //而且有=的取值, a=b
                    {
                        actualCon = buildInClause(actualField,
                                                  (Set) colValues.
                                                  get(currentColumn));

                        selectClause.append(" and ")
                                .append(actualCon);
                        selectClause.insert("select ".length(),
                                            actualField + ","); //字段也放到select前面.
                    }
                }

            } //end while

        } //end while

        //将selectItmes中的where条件拼完了.

    }


    ///////////////////
    //表间参照的类型
    /**整列参照*/
    private static final int COLREF = 0;
    /**整行参照*/
    private static final int ROWREF = 1;
    /**单元格参照*/
    private static final int CELLREF = 2;

    /**
     * 计算本期,同期,上期的表间参照.
     * @param refName 变化的参照的名字,可能是行(when type==COLREF);可能是列(when type==REFREF);或CELL
     * @param cellValue 单元格的值
     * @param type
     * @return
     */
    private String replaceRef(String cellValue, String refName, int type) throws
            Exception
    {
        String result = cellValue;
        DataInfo targetDataInfo;

        //bq
        while (bqRE.match(result))
        {
            String rql;

            String $0 = bqRE.getParen(0);
            String $1 = bqRE.getParen(1); //报表编码
            String $2 = bqRE.getParen(2); //type==COLREF:参照列 type==ROWREF:参照行
            String $4 = bqRE.getParen(4);

            if ($4 != null) //参照到格了
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //按列参照,rql拼上行.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //按行参照,rql拼上列
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //目标报表
            targetDataInfo = reportData.getDataInfo().bq($1);

            log.debug("tagetDataInfo:\n" + targetDataInfo.getCalculateBranch()
                      + "|" + targetDataInfo.getCode() + "|" +
                      targetDataInfo.getDataFile());

            try
            {
                ReportData refReport = ReportDataFactory.getInstance().
                                       getReportData(targetDataInfo);
                log.debug(refReport.getDataInfo().getDataFile() +
                          "|ref file|||rql|" + rql);
                value = refReport.getValue(rql);
            }
            catch (Exception ex)
            {
                //可能加载报表出错,也可能查询数据出错,值为"0",加入警告
                log.error(ex.getMessage());
                value = "0";
            }

            //将值替换
            result = StringUtility.replace(result, $0, value);
        }

        //sq
        while (sqRE.match(result))
        {
            String rql;

            String $0 = sqRE.getParen(0);
            String $1 = sqRE.getParen(1); //报表编码
            String $2 = sqRE.getParen(2); //type==COLREF:参照列 type==ROWREF:参照行
            String $4 = sqRE.getParen(4);

            if ($4 != null) //参照到格了
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //按列参照,rql拼上行.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //按行参照,rql拼上列
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //目标报表
            targetDataInfo = reportData.getDataInfo().sq($1);
            try
            {
                ReportData refReport = ReportDataFactory.getInstance().
                                       getReportData(targetDataInfo);
                value = refReport.getValue(rql);
            }
            catch (Exception ex)
            {
                //可能加载报表出错,也可能查询数据出错,值为"0",加入警告
                log.error(ex.getMessage());
            }

            //将值替换
            result = StringUtility.replace(result, $0, value);
        }

        //tq
        while (tqRE.match(result))
        {
            String rql;

            String $0 = tqRE.getParen(0);
            String $1 = tqRE.getParen(1); //报表编码
            String $2 = tqRE.getParen(2); //type==COLREF:参照列 type==ROWREF:参照行
            String $4 = tqRE.getParen(4);

            if ($4 != null) //参照到格了
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //按列参照,rql拼上行.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //按行参照,rql拼上列
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //目标报表
            targetDataInfo = reportData.getDataInfo().tq($1);
            try
            {
                ReportData refReport = ReportDataFactory.getInstance().
                                       getReportData(targetDataInfo);
                value = refReport.getValue(rql);
            }
            catch (Exception ex)
            {
                //可能加载报表出错,也可能查询数据出错,值为"0",加入警告
                log.error(ex.getMessage());
            }

            //将值替换
            result = StringUtility.replace(result, $0, value);
        }

        return result;

    }


    //GOD....把所有函数,和表间取数交给BeanShell去做,有做过工业级测试么?...
    //how dare you do this? Yang!

    /**
     * 设置解析器的取数函数,与位置相关.
     * 设置规则:A1->get$A1()
     * @change log: 增加异常处理,使 n/0 = 0; 最快判断NaN,设置为0
     * @param defineBlock
     * @param dataBlock
     * @throws Exception
     */
    private void setColRow2Interpreter(DefineBlock defineBlock,
                                       DataBlock dataBlock) throws Exception
    {
        //数据块自动生成的脚本
        StringBuffer sbBlockAutoScript = new StringBuffer();

        //确信函数,变量均准备ok

        //表内取数的替换...ok


        Iterator itColKey = defineBlock.getCols().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
//      ColData colData=(ColData)

            Iterator itRowKey = defineBlock.getRows().keySet().iterator();
            while (itRowKey.hasNext())
            {
                String rowName = (String) itRowKey.next();
                Cell cell = (Cell) ((ColData) dataBlock.getColDataMap().get(
                        colName)).getCells().get(rowName);
                String loc = cell.getLocation();
                String value = cell.getValue();

                if (value == null)
                {
                    log.debug("c:" + colName + ",r:" + rowName + ",loc:" + loc);
                    value = "0"; //可能是空行,空列
                }

                value = value.replace('\'', '\"'); // "'" -> """,以便在java中处理

                sbBlockAutoScript.append("get$") //get$A1(){ try {value = value; return (value==value)value?:0.0d } catch(Exception ex){return 0;}}
                        .append(loc)
                        .append("(){ try{ value = ")
                        .append(value)
                        .append("; return (value==value)?value:0.0d; }catch(Exception exxxxxxxxxxx){return 0d;} }\n");
            }
        }

        //special cells parse, 放到这里最好了.
        Iterator itSPCellsKey = defineBlock.getCells().keySet().iterator();
        while (itSPCellsKey.hasNext())
        {
            String cellName = (String) itSPCellsKey.next();
            Map cellDefine = (Map) defineBlock.getCells().get(cellName);
            String location = (String) cellDefine.get(BlockElement.LOCATION);
            String context = (String) cellDefine.get(BlockElement.CONTEXT);

            context = context.replace('\'', '\"'); // "'" -> """,以便在java中处理

            //表间取数
            context = replaceRef(context, "", CELLREF);
            //表内取数
            context = replaceRefSelf(dataBlock, context);

            //增加异常处理.防止0除
            sbBlockAutoScript.append("get$") //get$A1(){return value;}
                    .append(location)
                    .append("(){ try { value= ")
                    .append(context)
                    .append("; return (value==value)?value:0.0d; }catch(Exception exxxxxxxxxxx){return 0d;} }\n");

        }

        String autoSt = dealSum(sbBlockAutoScript.toString());

        log.debug("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n" + autoSt);

        //根据这一规则将脚本放置到引擎,应用自动的递归函数调用,数值计算,脚本执行.
        interpreter.eval(autoSt);

    }

    RE reRefSelfCell;

    /**
     * 替换表内参照.
     * @param dataBlock 当前数据块.
     * @param context
     * @return
     * @throws Exception
     */
    private String replaceRefSelf(DataBlock dataBlock, String context) throws
            Exception
    {
        String result = context;
        if (null == result || "".equals(result))
        {
            return result;
        }

        while (reRefSelfCell.match(result))
        {
            String find = reRefSelfCell.getParen(0);
            String colName = reRefSelfCell.getParen(1);
            String rowName = reRefSelfCell.getParen(2);

            Cell cell = dataBlock.getCell(find);
            if (cell == null)
            {
                throw new Exception("定义文件[" +
                                    reportData.getDataInfo().getDefineFilePath() +
                                    "]出错:参照--" + find + "--没有定义.");
            }

            String loc = cell.getLocation();
            String replace = "get$" + loc + "()";

            result = StringUtility.replaceWord(result, find, replace);
        }
        return result;
    }


//  /**当前计算的定义块,为共享数据区,严格限制使用!!!*/
//  private DefineBlock currentDefineBlock; //no use

    /**
     * 根据全局设定得到各个表的where条件
     * 在函数中对字段作转换.
     * @param tableName 表名
     * @return where条件
     */
    private String getGlobalWhere(DefineBlock defineBlock, String tableName) throws
            Exception
    {
        DataBlock dataBlock = defineBlock.getDataBlock();
        Map dataGCMap = dataBlock.getGcMap();

        StringBuffer sb = new StringBuffer();
        sb.append(" where 1=1 ");
        Map gMap = defineBlock.getGlobal();

        Iterator itGKey = gMap.keySet().iterator();
        while (itGKey.hasNext())
        {
            String left = (String) itGKey.next();
            //p判断是否是本表字段
            String actualField = tableRelations.getActualField(left, tableName);
            if (actualField == null) //不是
            {
                continue;
            }

            String right = (String) gMap.get(left);
            String rightContent = "";
            String op = "";

            //判断第二个表达式是否含有比较符.
            if (right.startsWith(">")
                || right.startsWith("<")
                || right.startsWith("!")
                || right.startsWith("in")
                || right.startsWith("IN")
                || right.startsWith("=")
                    )
            {
            }
            else //没有比较符,拼上=
            {
                right = "=" + right;
            }

            right = right.replace('\'', '"'); //替换为一个字符串函数

            if (right.startsWith(">=")
                || right.startsWith("<=")
                || right.startsWith("<>")
                || right.startsWith("!=")
                    )
            {
                op = right.substring(0, 2);
                rightContent = right.substring(2);
            }
            else if (right.toLowerCase().startsWith("in"))
            {
                /**@todo: in(,,)可能工作不正常*/
//        int leftBPos=right.indexOf("(");
//        int rightBPos=right.lastIndexOf(")");
//        right=
            }
            else //以=,<,>开头
            {
//        right="\""+right.substring(0,1)+"\"'"+right.substring(1)+"'";
                op = right.substring(0, 1);
                rightContent = right.substring(1);
            }

            //执行它
            interpreter.eval("auto_temp=" + rightContent);
            String value = interpreter.get("auto_temp").toString();

            sb.append(" and ")
                    .append(actualField)
                    .append(op)
                    .append("'")
                    .append(value)
                    .append("'");

            dataGCMap.put(left, op + value); //把计算后的全局条件放到数据块的全局条件中.
        }

        return sb.toString();
    }

    /**
     * 处理全局条件Map.
     * 计算全局条件.将条件右边的表达式计算出来,放到DataBlock的gMap中.
     * @throws Exception
     */
//  private void dealGlobleMap(DefineBlock defineBlock,DataBlock dataBlock) throws Exception
//  {
//    if()
//    {}
//  }


    private void constructDataInfo(DefineInfo defineInfo, DataInfo dataInfo) throws
            Exception
    {
        dataInfo.setBranch(defineInfo.getBranch());
//    dataInfo.setCalculateBranch(defineInfo.getBranch());
        dataInfo.setCode(defineInfo.getCode());
        dataInfo.setCurrency(defineInfo.getCurrency());
        dataInfo.setCycle(defineInfo.getCycle());
        dataInfo.setEdition(defineInfo.getEdition());
//    dataInfo.setEndDate(defineInfo.getBranch());
        dataInfo.setFeature(defineInfo.getFeature());
        dataInfo.setName(defineInfo.getName());
        dataInfo.setOperator(defineInfo.getOperator());
        dataInfo.setRemark(defineInfo.getRemark());
//    dataInfo.setStartDate(defineInfo.get());
        dataInfo.setType(defineInfo.getType());

        dataInfo.setCalculateBranch(runEnv.getEnv(Environment.CALCBRANCH));
        dataInfo.setCalculateDate(runEnv.getEnv(Environment.CALCDATE));

    }

    /**
     * 处理condion中的变量,函数.
     * @todo: 当前只替换变量,函数可能有问题.
     * 在condition中的变量和函数规则:必须以$开头!!! 因为存储过程也会有函数,不易区分.
     * @param colHeader 列头定义
     * @param rowHeader 行头定义
     */
    private void dealConditionVarFunc(Map colHeaders, Map rowHeaders) throws
            Exception
    {
        //函数
        //\$[\w_]*\(.*?\)
        RE reFunc = new RE("\\$[\\w_]*\\(.*?\\)");

        //变量
        //\$[\w_]*
        RE reVar = new RE("\\$[\\w_]*");

        //colHeaders
        Iterator itCol = colHeaders.keySet().iterator();
        while (itCol.hasNext())
        {
            Object key = itCol.next();
            Object value = colHeaders.get(key); //一列头

            //表名,列名集合
            //..取condition
            Map col = (Map) value;
            String condition = (String) col.get(BlockElement.CONDITION);

            //函数
            if (condition == null || "".equals(condition))
            {
                continue;
            }

            while (reFunc.match(condition))
            {
                String find = reFunc.getParen(0);
                interpreter.eval("auto_temp=" + find);
                String findValue = interpreter.get("auto_temp").toString();
                //替换函数执行结果
                condition = StringUtility.replaceWord(condition, find,
                        findValue);
            }

            //变量
            if (condition == null || "".equals(condition))
            {
            }
            else
            {
                while (reVar.match(condition))
                {
                    String find = reVar.getParen(0);
                    interpreter.eval("auto_temp=" + find);
                    String findValue = interpreter.get("auto_temp").toString();
                    //替换变量执行结果
                    condition = StringUtility.replaceWord(condition, find,
                            findValue);
                }
            }
            //放回条件定义中
            col.put(BlockElement.CONDITION, condition);
        }

        //colHeaders
        Iterator itRow = rowHeaders.keySet().iterator();
        while (itRow.hasNext())
        {
            Object key = itRow.next();
            Object value = rowHeaders.get(key); //一列头

            //表名,列名集合
            //..取formula
            Map row = (Map) value;

            String condition = (String) row.get(BlockElement.CONDITION);

            //函数
            if (condition == null || "".equals(condition))
            {
                continue;
            }

            while (reFunc.match(condition))
            {
                String find = reFunc.getParen(0);
                interpreter.eval("auto_temp=" + find);
                String findValue = interpreter.get("auto_temp").toString();
                //替换函数执行结果
                condition = StringUtility.replaceWord(condition, find,
                        findValue);
            }

            //变量
            if (condition == null || "".equals(condition))
            {
            }
            else
            {
                while (reVar.match(condition))
                {
                    String find = reVar.getParen(0);
                    interpreter.eval("auto_temp=" + find);
                    String findValue = interpreter.get("auto_temp").toString();
                    //替换变量执行结果
                    condition = StringUtility.replaceWord(condition, find,
                            findValue);
                }
            }
            //放回条件定义中
            row.put(BlockElement.CONDITION, condition);

        }
    }

    /**
     * 创建where中的in子句
     * @param field 字段
     * @param valueSet 值集合
     * @return in子句
     */
    public static String buildInClause(String field, Set valueSet)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(" ")
                .append(field)
                .append(" in (");

        Iterator it = valueSet.iterator();
        while (it.hasNext())
        {
            String value = (String) it.next();
            if (value.startsWith("'")) //已包含''
            {
                sb.append(value)
                        .append(",");
            }
            else //没有包含'',拼上
            {
                sb.append("'")
                        .append(value)
                        .append("',");
            }
        }
        sb.delete(sb.length() - 1, sb.length()); //trim last ,
        sb.append(") ");

        return sb.toString();

    }


    /**
     * 根据数据源集合得到相应的select a,b from c 子句.
     * 主要解决相同表,不同字段的一次取数.
     * @param set 数据源集合 [c.a,c.b]
     * @return sql前半部分
     */
    private Map getSelectFrom(Set set)
    {
        //记录各个表的select clause
        Vector v = new Vector();
        //记录各个位置上的表名.
        Vector tableLocation = new Vector();

        Object[] tc = set.toArray();

        //几个表?
        for (int i = 0; i < tc.length; i++)
        {
            String tableColumn = (String) tc[i];
            String tableName = StringUtility.getLeftWord(tableColumn,
                    tableColumn.indexOf("."));
            String columnName = StringUtility.getRightWord(tableColumn,
                    tableColumn.indexOf("."));

            //判断该表的select clause 是否存在
            int loc = tableLocation.indexOf(tableName);
            //不存在...新加一个select
            if (loc == -1)
            {
                StringBuffer sb = new StringBuffer("select ");
                sb.append(columnName)
                        .append(",");
                v.add(sb);
                tableLocation.add(tableName); //相应sql对应表名放在相同索引上
            }
            else //存在,将 字段附在末尾.主要是针对一个表各列数据对应报表各列的情形.
            {
                StringBuffer sb = (StringBuffer) v.get(loc);
                sb.append(columnName)
                        .append(",");
            }
        }
        //...放完了

        Map result = new HashMap();

        //去掉最后一个',',附上表名
        for (int i = 0; i < v.size(); i++)
        {
            StringBuffer sb = (StringBuffer) v.get(i);

            sb.delete(sb.length() - 1, sb.length());
            sb.append(" from ")
                    .append(tableLocation.get(i));

            //表名->sql的select部分 映射表
            result.put(tableLocation.get(i), sb);
        }

        return result;

    }


    /**
     * 得到全局设定的where条件.
     * @param gMap 全局条件
     * @return where条件
     * @throws Exception
     */
//  private String getGlobleWhere(Map gMap) throws Exception
//  {
//    String result="1=1";
//
//    Iterator it=gMap.keySet().iterator();
//
//    while(it.hasNext())
//    {
//      String key=(String)it.next();
//      String value=(String) gMap.get(key);
//
//      //caluatable?
//
//
//      //
//      interpreter.eval(key+"="+value);
//    }
//
//    interpreter.
//
//    return result;
//  }

//将当前运行环境(当前定义/当前报表/变量)设置到解析器...
    private void setInterpreterEnv() throws Exception
    {
        //变量顺序设入...
//    interpreter.set("$branch","330700");
//    interpreter.set("$operator","ya....");
//    System.out.println(interpreter.eval("$branch+$operator"));

        //当前定义,当前数据(Report)

//    interpreter.set("$branch","330700");
//    interpreter.set("$code","js0207h");
//    interpreter.set("$operator","ya....");
//    interpreter.set("$name","杭州分公司");
//    interpreter.set("$date","\"'"+getRunEnv().getEnv(Environment.CALCDATE)+"'\");");  //程序生成程序,万分小心.
        interpreter.set("$date",
                        "'" + getRunEnv().getEnv(Environment.CALCDATE) + "'");
        interpreter.eval("temp=$year_firstday();");
        interpreter.set("$year_firstday", interpreter.get("temp"));
        interpreter.set("$branch", getRunEnv().getEnv(Environment.CALCBRANCH));
        interpreter.set("$operatorID",
                        getRunEnv().getEnv(Environment.OPERATORID));

//    interpreter.eval("t=getDate();");
        log.debug("setDate(\"'" + getRunEnv().getEnv(Environment.CALCDATE) +
                  "'\");" + "+++++++" + interpreter.get("temp"));

        interpreter.set("data", (Object) dbDataSource);
//    System.out.println(interpreter.eval("$branch+$operator"));

    }

    /**
     * 判断是否需要解析
     * 通过是否有'$',类似函数的来判断
     * @param txt 字符串
     * @return
     * @throws Exception
     */
    private boolean needParse(String txt) throws Exception
    {
        //变量
        if (txt.indexOf("$") > -1)
        {
            return true;
        }
        else
        {
            //包含函数?
            String sub = txt;

            for (int i = sub.indexOf("("); i > 0; sub = sub.substring(i + 1))
            {
                if (!"".equals(StringUtility.getLeftWord(sub, i)))
                {
                    return true;
                }
            }

            //包含操作符?
            return txt.indexOf("+") > -1
                    || txt.indexOf("-") > -1
                    || txt.indexOf("*") > -1
                    || txt.indexOf("/") > -1;

        }
    }

    /**
     * 将sum(get$A1():get$A5())替换成(get$A1()+...+get$A5)
     * @param content 待替换内容
     * @return 替换结果
     * @throws Exception 错误
     */
    private String dealSum(String content) throws Exception
    {
        String result = content;

        //(sum|SUM|sum)\s*\(\s*get\$(\w*)\s*\(\s*\)\s*:\s*get\$(\w*)\s*\(\s*\)\s*\)
        RE re = new RE("([sS][uU][mM])\\s*\\(\\s*get\\$(\\w*)\\s*\\(\\s*\\)\\s*:\\s*get\\$(\\w*)\\s*\\(\\s*\\)\\s*\\)");

        while (re.match(result))
        {
            String replaceStr = re.getParen(0); //找到的等式
            String startLoc = re.getParen(2); //开始位置
            String endLoc = re.getParen(3); //终止位置
            StringBuffer sb = new StringBuffer("(");

            int startRow = StringUtility.Cell2Row(startLoc);
            int startCol = StringUtility.Cell2Col(startLoc);

            int endRow = StringUtility.Cell2Row(endLoc);
            int endCol = StringUtility.Cell2Col(endLoc);

            for (int i = Math.abs(startRow - endRow); i >= 0; i--)
            {
                int row = Math.min(startRow, endRow) + i;
                for (int j = Math.abs(startCol - endCol); j >= 0; j--)
                {
                    int col = Math.min(startCol, endCol) + j;
                    String cell = StringUtility.rowCol2Cell(row, col);

                    sb.append("get$")
                            .append(cell)
                            .append("()+");
                }
            }
            //去掉最后一个"+"
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")"); //加()防止运算次序出错

            result = StringUtility.replace(result, replaceStr, sb.toString());
        }

        return result;
    }


    /**
     * 得到参数/变量替换结果.
     * @return 替换结果
     * @todo: 从系统设置,或者用户传入,通过函数解析实现
     */
    public Map getParameterValue()
    {
        return null;
    }

    public Collection getDataSet(String sql) throws DBExecption
    {
        /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
        throw new java.lang.UnsupportedOperationException(
                "Method getDataSet() not yet implemented.");
    }

    public TreeNode getDefine() throws DBExecption
    {
        /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
        throw new java.lang.UnsupportedOperationException(
                "Method getDefine() not yet implemented.");
    }

//  public TreeNode getValue(String tableColumn, String parent) throws DBExecption
//  {
//    /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
//    throw new java.lang.UnsupportedOperationException("Method getValue() not yet implemented.");
//  }
    public Collection getDataSourceDefine() throws Exception, DBExecption
            {
            /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
            throw new java.lang.UnsupportedOperationException(
            "Method getDataSourceDefine() not yet implemented.");
    }
            public Collection getDimensionDefine() throws Exception,
            DBExecption
            {
            /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
            throw new java.lang.UnsupportedOperationException(
            "Method getDimensionDefine() not yet implemented.");
    }
            public Collection getDimensionDefine(String strDataSourceId) throws
            Exception, DBExecption
            {
            /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
            throw new java.lang.UnsupportedOperationException(
            "Method getDimensionDefine() not yet implemented.");
    }
            public Object getValue(String tableColumn, Map parent) throws
            Exception, DBExecption
            {
            /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
            throw new java.lang.UnsupportedOperationException(
            "Method getValue() not yet implemented.");
    }

            public Collection getRelations() throws Exception, DBExecption
            {
            /**@todo Implement this com.sinosoft.xreport.dl.DataSource method*/
            throw new java.lang.UnsupportedOperationException(
            "Method getValue() not yet implemented.");
    }

            public ReportData getReportData()
    {
        return reportData;
    }

    public ReportDefine getReportDefine()
    {
        return reportDefine;
    }

    public void setReportData(ReportData reportData)
    {
        this.reportData = reportData;
    }

    public void setReportDefine(ReportDefine reportDefine)
    {
        this.reportDefine = reportDefine;
    }

    public Interpreter getInterpreter()
    {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }


    public static void main(String[] args) throws Exception
    {
//    Report r=new Report("");
        //...测试在bsh能不能引用script中的object
//  r.setInterpreter(new bsh.Interpreter());
//    r.setInterpreterEnv();
//  r.interpreter.eval("a=this");
//  System.out.println(r.interpreter.get("a"));
        //...pass

        //...测试select的组合
//  Set set=new HashSet();
//  set.add("a.b");
//  set.add("b.c");
//  set.add("a.c");
//
//  System.out.println(r.getSelectFrom(set));
        //result: [select a.c,a.b from a, select b.c from b]
        //...pass

        //测试关联字段查找
//  Vector v=new Vector();
//  r.tr=new TableRelations(v);
//  v.add("target.branch=sourcetable.con");
//  v.add("sourcetable.con=target.branch");
//  String s = r.tr.getConvertField("sourcetable.con","target");
//  System.out.println(s);
        //...pass

        //测试解析器递归函数调用
//    Interpreter i=new Interpreter();
//    i.eval("getA1(){return getA2()+2;}");
//    i.eval("getA2(){return 3+3;}");
//    i.eval("i=getA1();");
//    System.out.println(i.get("i"));
        //...pass

//    Report r=new Report("330700","js0207h","20020101");
//    System.err.println(r.dealSum("sum(get$AB1():get$B2())"));


        //2003-3-31 15:54 模块测试
        long time = Calendar.getInstance().getTime().getTime();

        Environment env = new EnvironmentImpl();
        env.setEnv(Environment.CALCBRANCH, "86");
        env.setEnv(Environment.CALCDATE, "2003-2-25");
        env.setEnv(Environment.OPERATORID, "001");

        Report r = new Report("86", "AgentGroupInfo", "20050105");
        r.setRunEnv(env);

        //改用BufferDataSource实现
        DataSource dataSource = new BufferDataSourceImpl();
        r.setDbDataSource(dataSource);

        TableRelations tableRelations = new TableRelations(dataSource);

        r.setTableRelations(tableRelations);

        r.init();

        ReportData rd = r.calculate();
        ReportWriter rw = new ExcelWriter();
        ReportWriter rrw = new ReportWriter();
        rw.setEnvironment(env);
        rrw.setEnvironment(env);

        r.write(rrw);
        r.write(rw);

//    rw.write(rd);

        time = Calendar.getInstance().getTime().getTime() - time;
        r.log.debug(".............time:" + time);

    }


    private String getOneValueFromDB(String sql) throws Exception
    {
        /////////////////////////////////////////////////
        //debug
//    if(true)
//      return "0";

        log.debug("one value sql:" + sql);

        Vector result = (Vector) dbDataSource.getDataSet(sql);

        double dValue = 0d;

        for (int i = 1; i < result.size(); i++)
        {
            String value = ((String[]) result.get(i))[0];
            if (!StringUtility.isNumber(value))
            {
                return value; //非数字,直接返回
            }

            dValue += Double.parseDouble(value);
        }

        return decimalFormat.format(dValue);
    }


    public Environment getRunEnv()
    {
        return runEnv;
    }

    public void setRunEnv(Environment runEnv)
    {
        this.runEnv = runEnv;
    }

    public DataSource getDbDataSource()
    {
        return dbDataSource;
    }

    /**
     * 设置数据源,可以使用bufferData
     * @param dbDataSource
     */
    public void setDbDataSource(DataSource dbDataSource)
    {
        this.dbDataSource = dbDataSource;
    }

    public void write(ReportWriter reportWriter) throws Exception
    {
        reportWriter.write(getReportData());
    }

    public TableRelations getTableRelations()
    {
        return tableRelations;
    }

    public void setTableRelations(TableRelations tableRelations)
    {
        this.tableRelations = tableRelations;
    }

}
