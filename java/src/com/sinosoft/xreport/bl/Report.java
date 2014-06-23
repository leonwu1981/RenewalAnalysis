//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Report.java
/**
 * chang log:
 * yang   2003-4-7 11:27  �޸���ֱ��ִ��'����sql'--recalCol & recalRow �е� or �����ȼ�����:�����(),����ȡ������������ֵ
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
 * @todo:DataMap��������������,����������:������������������ͬ.
 * @todo:��ֵ����,��ѧ����������.
 */

/**
 * ������.�������������.
 * 1.����ǰ��Ҫ���趨����Environment,�趨������Ĳ���Parameter...
 * 2.
 * @todo ��ȡ����
 * @todo �������к����ͱ���
 * @todo �γ����ݶ���
 * @todo д����
 * @todo 2003-03-24 Yang �����¼���:
 * ���򱨱���:
 * 1) ������֮��(���϶��µ���)����һ���ֶ�ȡֵ�����ı�; e.g.��ϸ��,���ַ����ı�
 * 2) ������֮��,����������һ��,Ҳ�������ֶη����ı�.
 * �������ϼ���,���Լ򻯹������ݿ��ȡ��... select a,b,c from T where t in (x,y,z)
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

    //����
    private ReportDefine reportDefine;
    //����
    private ReportData reportData;

    //ϵͳ����,����
    private Environment runEnv;
    //���ʽ������...
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
     * ���ݼ�����Ϣ�����������.
     * @param defineBranch
     * @param code
     * @param edition
     */
    public Report(String defineBranch, String code, String edition)
    {
        reportDefine = new ReportDefine(defineBranch, code, edition);
    }

    /**
     * ����һ��Report����.
     * @param defineFile �����ļ�
     */
    public Report(String defineFile)
    {
        reportDefine = new ReportDefine(defineFile);
    }


    ////////////////////////////
    //������������׼����ȫ
    ////////////////////////////

    /**
     * �������.
     * @return �����ļ�λ��
     * @throws Exception �������
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

        //��ʼ������������...
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

            //ȫ�ֲ���...
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
     * һ��ļ���.
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

        //����condition�еı���,����
        dealConditionVarFunc(colHeaders, rowHeaders);

        Map tableMap = new HashMap();

        //���������ȡ���ֶμ���
        Set dataSourceSet = new HashSet();
        //���������where�ֶμ���
        Set wcSet = new HashSet();

        //colHeaders
        Iterator itCol = colHeaders.keySet().iterator();
        while (itCol.hasNext())
        {
            Object key = itCol.next();
            Object value = colHeaders.get(key); //һ��ͷ

            //����,��������
            //..ȡformula
            Map col = (Map) value;
            String formula = (String) col.get(BlockElement.FORMULA);
            String condition = (String) col.get(BlockElement.CONDITION);

            //�õ�select ȡ����
//      String[] selectItems=getSelectItems(formula); //
            XRRegExp.getAllDS(formula, dataSourceSet); //����ȡ���з���

            //����condition
            XRRegExp.getWhereClause(condition, wcSet);

        }

        //colHeaders
        Iterator itRow = rowHeaders.keySet().iterator();
        while (itRow.hasNext())
        {
            Object key = itRow.next();
            Object value = rowHeaders.get(key); //һ��ͷ

            //����,��������
            //..ȡformula
            Map row = (Map) value;
            String formula = (String) row.get(BlockElement.FORMULA);
            String condition = (String) row.get(BlockElement.CONDITION);

///////////////////////////////////////////////////////
// Ӧ�ü��� 1) �����������Դ����ͷ��,���ϵĵ��´μ���ʱ�ٴ���
///////////////////////////////////////////////////////

            //�õ�select ȡ����
//      String[] selectItems=getSelectItems(formula); //
//      XRRegExp.getAllDS(formula,dataSourceSet); //����ȡ���з���
            //�����ʱ��������������Դ,��Ҫ�Ƕ������Դ��Ҫ�ϲ�,�����������

            //����condition
            XRRegExp.getWhereClause(condition, wcSet);

        }
        //����ok

////////////////////////////////
// 1)��һ�ּ���:�������ݿ�.
///////////////////////////////


        //һ����һ������ѽ
        Map selectItems = getSelectFrom(dataSourceSet);
//.......................
        //һ����һ������
        getWhereClause(defineBlock, selectItems, wcSet);

        /////////////////////////////
        // �����sql�������,ִ�к���ڱ��ڲ�ѯ�Ľṹ��
        ////////////////////////////
        DataMap dm = new DataMap(selectItems);
        dm.setDataSource(dbDataSource);
        dm.setTableRalation(tableRelations);
        //build DataMap complete.

//    log.debug(((ColData)dataBlock.getColDataMap().get("ת��δ��������׼��������")).cells);
//    log.debug(dataBlock.getRowDataMap());

        //��һ�ּ���
        putData(defineBlock, dataBlock, dm);
        //����������ݷŵ�Data��

        //�ڶ���...........................
        //���㲻���������
        //�������л��,�д���������ȡ����,��ͷ�ϸ���������Դ��,����,�ڶ��ָɵ�
        calculateSPColRowCell(defineBlock, dataBlock);

        //���㵥����Ԫ��

        //�������������

        //caution: �������в����ǰ�����,�������滻����.
        //�������в��ղ�Ӧ��λ��,֧��?
        //��������-replace

        //������ڲ�������
        setColRow2Interpreter(defineBlock, dataBlock);

        //�ӽ�������ȡ�����ݷ���dataBlock��
        getDataFromInterpreter(dataBlock);

//    log.debug("\n\n"+dataBlock.toXMLString());

        return dataBlock;
        //д����
    }

    /**���ݸ�ʽ*/
    java.text.DecimalFormat decimalFormat = new DecimalFormat(SysConfig.
            DECIMALPATTERN);

    private void getDataFromInterpreter(DataBlock dataBlock) throws Exception
    {
        //[λ��->cell]Map
        Map locationCellMap = dataBlock.getLocationMap();

        Iterator itLocKey = locationCellMap.keySet().iterator();
        while (itLocKey.hasNext())
        {
            String loc = (String) itLocKey.next();

            Object object = locationCellMap.get(loc); //��ͷ,��ͷҲ�����,С��.

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
                //��ֵ��ʽ��
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

        //DataSource���ⲿ����,�������û�ѡ���Ƿ�ʹ��buffer
//    dbDataSource=new DataSourceImpl();

        //tableRelation���ⲿ����,�������û�ѡ���Ƿ�ʹ��buffer
//    tableRelations=new TableRelations(dbDataSource);
        //���ڲ��յ�pattern
//    reRefSelf=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        reRefSelf = new RE("[^\\w\\.]*\\[([^\\[\\]]*)\\][^\\w\\[\\]\\.]*");
        //�����յ�pattern
        reRefOther = new RE("[^\\w\\.]*\\[.*\\]\\.\\[.*\\][^\\w\\.]*");

        //���ڵ�Ԫ����� //\[(.*?)\]\.\[(.*?)\]
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
     * ��һ�ּ���.�ѵ�һ�θ��ݰ���ȡ��ȡ�������ݷŵ���������ȥ
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

            //ȷ������Դ,����s
            XRRegExp.getAllDS(formula, dsSet);
            XRRegExp.getWhereClause(condition, wcSet);

            //С��...................
            //������ֵ��..............�е���ô?? maybe...
            Iterator itRowKey = defineBlock.getRows().keySet().iterator();
            while (itRowKey.hasNext())
            {
                String rowName = (String) itRowKey.next();
                Map rowHeaderDefineMap = (Map) defineBlock.getRows().get(
                        rowName);

                //��������
                String rowCondition = (String) rowHeaderDefineMap.get(
                        BlockElement.CONDITION);
                //build it
                Set wcRowSet = new HashSet();
                XRRegExp.getWhereClause(rowCondition, wcRowSet);

                //�����������
                wcRowSet.addAll(wcSet);

                //ȷ��������cell
                Cell cell = (Cell) ((ColData) dataBlock.getColDataMap().get(
                        colName)).getCells().get(rowName);
                String cellValue = formula;

                //��,һ�����ж������Դ,e.g. summary_balance.balance_dest+summary_balance.balance_sour
                Object[] dsArr = dsSet.toArray();

                for (int i = 0; i < dsArr.length; i++)
                {
                    String value = dm.getValue((String) dsArr[i], wcRowSet);
//          RE re=new RE("\\b"+dsArr[i]+"\\b");

                    cellValue = StringUtility.replaceWord(cellValue,
                            dsArr[i].toString(), value);
                    //�滻����....
                }

                log.debug("putData: cell.location:" + cell.getLocation() +
                          ",current formula:" + formula);

                //�´�����...
                cell.setValue(cellValue);
            }
        }
    }

    /**
     * �ڶ��ּ���,����ǹ�������:�����������ȡ����,��������"or"��,��>=,<=,<>,>,<,!=,ֱ����sql������,�����.
     * @param defineBlock
     * @param DataBlock
     * @throws Exception
     */
    private void calculateSPColRowCell(DefineBlock defineBlock,
                                       DataBlock dataBlock) throws Exception
    {
        //��"or"����,�ͷǵ�������...
        //Ϊʲô"or"��Ҫ��������,����ʹ���Ѿ�ȡ��������ӻ��,��Ҫ�Ƿ�ֹǶ������,
        //ͨ������DB��sql����ֱ�Ӽ���...��������ԭ��!!!
        Iterator itColKey = defineBlock.getCols().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            Map colHeaderDefine = (Map) defineBlock.getCols().get(colName);

            //get content
            String formula = (String) colHeaderDefine.get(BlockElement.FORMULA);
            String condition = (String) colHeaderDefine.get(BlockElement.
                    CONDITION);

            //�ǹ���sql��� ˳��ѱ��ȡ�����˰�,ʡ����ѭ��..
            //���¼�����һ��.
            recalcCol(colName, formula, condition, defineBlock, dataBlock);
        }

        //he ma
        //������ϵ���������,formula
        //while... row key
//    Iterator itRowKey=defineBlock.getRows().keySet().iterator();
//    while(itRowKey)

        //����һ��.������һ��: 2003-3-30 15:32. ����һ���ش�ĸı�,����ʹ�ú���������
        //����ѭ���Ĵӿ�ȡ����ֵ���滻(��׾�ķ�ʽ),һ�ж��к���(��Ϊ)����,���ó���������script,����beanShellȥ����.
        //�������ݹ���������,�ַ�������������

        //���ٻ����԰���ȡ��.
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

        //�滻����ȡ��
        //����ȡ��������[��].[��];Ҳ������[��]����[��]
//    Iterator itRowKey=dataBlock.getRowDataMap().keySet().iterator();
        //special cells
        //...�ŵ���colRow2Int()��


    }

    //���ڲ��յ�pattern
    RE reRefSelf; //=new RE("[^\\w\\.]*\\[([^\\.\\[\\]]*)\\][^\\w\\[\\]\\.]*");
    //�����յ�pattern
    RE reRefOther; //=new RE("[^\\w\\.]*\\[.*\\]\\.\\[.*\\][^\\w\\.]*");

    /**
     * ����������.
     * �ڶ��ּ�����м��㲿��.
     * ����ǹ�������:��formula��ֱ���滻formula,û�е��ж������Ƿ��ǹ���,���ޱ��
     * ȡ��,����ȡ��,go
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
        //��formula?
        boolean hasFormula = !(null == formula || "".equals(formula));
        //������������?
        boolean isSPRow = !XRRegExp.onlyAndCondition(condition);

        if (!hasFormula && !isSPRow)
        {
            return;
        }

        if (hasFormula) //������formula,����,����,�������б��ȡ��,����ȡ��...
        {
            //�б�����
            boolean hasRefOther = XRRegExp.hasRefOtherReports(formula);
            //�б��ڲ���
            boolean hasRefSelf = XRRegExp.hasRefSelf(formula);

            //������Դ
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
                //������cell

                log.debug(colName + ":" + rowName);

                Cell cell = (Cell) ((RowData) dataBlock.getRowDataMap().get(
                        rowName)).getCells().get(colName);
                String cellValue = cell.getValue();
                if (cellValue == null || "".equals(cellValue))
                {
                    cellValue = "0";
                }

                //////////////////////////////
                //[?����Դ?]��̬����Դ
                cellValue = StringUtility.replace(formula, SysConfig.DYNAMICDS,
                                                  cellValue);
                //There maybe are some bugs.
                /**@todo: ��ô:�������������,[?����Դ?]��ʼ����0,�����ᵼ�´���,
                 * �������,Ӧ�����¼���[?����Դ?]��ֵ.very easy.����ûʱ��ȥ����.
                 */

                //��������Դ
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

                    //��()
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
                            dsArr[i].toString(), value); //�滻cellValue
                } //for
                // ����Դ����ok
                /////////////////////

                if (hasRefOther) //�����մ���
                {
                    /**@todo: ����ʵ�ʵ�rql����*/
//          ReportDataFactory rdf=ReportDataFactory.getInstance();
//          ReportData reportData=rdf.getReportData();
                    cellValue = replaceRef(cellValue, colName, ROWREF);
                }

                log.debug("in recalcRow cell.location:" + cell.getLocation() +
                          ",cellValue:" + cellValue);

                if (hasRefSelf)
                {
                    //����
                    //ȡ��Ŀ�굥Ԫ���λ��...must
                    while (reRefSelf.match(cellValue))
                    {
                        String refRowName = reRefSelf.getParen(1); //���յ� �� ��
                        String rql = "[" + colName + "].[" + refRowName + "]";
                        String location = (String) dataBlock.getCell(rql).
                                          getLocation();
                        String reStr = "get$" + location + "()";

                        cellValue = StringUtility.replace(cellValue,
                                "[" + refRowName + "]", reStr);
                    }
                } //�滻���ڲ���over

                cell.setValue(cellValue);

            } //end while

        } //end if
        else //ֻ������formula���������,�п�����or,���and...
        {
            if (isSPRow)
            {
                //shit,�������¼�����,���,������Ҫ�����滻��.
                //��������û��formula,ʹ�����ϵ�formula����.

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
                    //������cell
                    Cell cell = (Cell) ((RowData) dataBlock.getRowDataMap().get(
                            rowName)).getCells().get(colName);
//          String cellValue=cell.getValue();
//          if(cellValue==null||"".equals(cellValue))
//            cellValue="0";

                    //����,��Ҫ��������
                    String cellValue = colFormula;

                    Set dsSet = new HashSet();
                    XRRegExp.getAllDS(colFormula, dsSet); //ȡ�е���������Դ

                    //��������Դ
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
//              .append(condition);  //����������Ҫת��.--yang at 2003-4-15 15:28 in MinSheng
                                    .append(tableRelations.getActualCondition(
                                    condition, currentTable))
                                    .append(")");
                        }

                        if (null != colCondition && !"".equals(colCondition))
                        {
                            sb.append(" and (")
//              .append(colCondition);  //ʹ�õ����ϵ�formula,���ϵ���������ת��?
                                    .append(tableRelations.getActualCondition(
                                    colCondition, currentTable)) //ת��!������Ч��
                                    .append(")");
                        }

                        String value = getOneValueFromDB(sb.toString());

//            RE re=new RE("\\b"+dsArr[i]+"\\b");
                        cellValue = StringUtility.replaceWord(cellValue,
                                dsArr[i].toString(), value); //�滻cellValue
                    } //for
                    // ����Դ����ok
                    /////////////////////

                    if (XRRegExp.hasRefOtherReports(colFormula)) //�����մ���
                    {
                        /**@todo: ����ʵ�ʵ�rql����*/
//          ReportDataFactory rdf=ReportDataFactory.getInstance();
//          ReportData reportData=rdf.getReportData();
//            cellValue=replaceRef(cellValue,colName,ROWREF);
//��������û��FORMULAʱʹ�õ������ϵ�FOMULA,��ʵ���в���.
                        cellValue = replaceRef(cellValue, rowName, COLREF); //�в���
                    }

                    if (XRRegExp.hasRefSelf(colFormula))
                    {
                        log.debug(cellValue + ":colName:" + colName +
                                  ":rowName:" + rowName);

                        //����
                        //ȡ��Ŀ�굥Ԫ���λ��...must
                        while (reRefSelf.match(cellValue))
                        {
//              String refRowName=reRefSelf.getParen(1); //���յ� �� ��
//              String rql="["+colName+"].["+refRowName+"]";
                            //�������߼�����,����û��Formula,ʹ�õ������ϵ�Formula,����,ֻ���������ϵĲ���
                            //..........................................
                            //
                            String refColName = reRefSelf.getParen(1); //���յ� �� ��
                            String rql = "[" + refColName + "].[" + rowName +
                                         "]";
                            String location = (String) dataBlock.getCell(rql).
                                              getLocation();
                            String reStr = "get$" + location + "()";

                            cellValue = StringUtility.replace(cellValue,
                                    "[" + refColName + "]", reStr);
                        }
                    } //�滻���ڲ���over

                    log.debug("in recalRow,isSPRow,loc:" + cell.getLocation() +
                              ",value:" + cellValue);

                    cell.setValue(cellValue);

                } //end while
            }
        }

    }


    /**
     * ����������������.
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

        //������Դ
        Set dsSet = new HashSet();
        XRRegExp.getAllDS(formula, dsSet);

        //��������
        boolean isSPCol = !XRRegExp.onlyAndCondition(condition);
        //��ָ���������ȡ��
        boolean hasRefOther = XRRegExp.hasRefOtherReports(formula);
        //���ޱ���ȡ��...?   added by yang at 2003-3-30 17:51
        boolean hasRefSelf = XRRegExp.hasRefSelf(formula); //��ͷ,��ͷֻ������,����[����].[����],�ڵ�Cell��ȥȡֵ.

        //������
        if (!isSPCol && !hasRefOther && !hasRefSelf)
        {
            return;
        }

        Object[] dsArr = dsSet.toArray();

//    for(int i=0;i<dsArr.length;i++)
//    {
//      String currentDS=dsArr[i];
//      String currentTable=StringUtility.getLeftWord(currentDS,currentDS.indexOf("."));

        //condition������
        //����ѭ��
        Iterator itRowKey = defineBlock.getRows().keySet().iterator();
        while (itRowKey.hasNext())
        {
            String rowName = (String) itRowKey.next();
            Map rowHeaderDefineMap = (Map) defineBlock.getRows().get(rowName);

            //ȷ��������cell
            Cell cell = (Cell) ((ColData) dataBlock.getColDataMap().get(colName)).
                        getCells().get(rowName);

            //
            String cellValue = cell.getValue();

            //û��ֵ.very importtant
            if (cellValue == null)
            {
                cellValue = formula;
            }

            if (isSPCol)
            {

                /***************************************************************************/
                /**************************������Դ��Ӧ������*********************************/
                /***************************************************************************/
                cellValue = formula; //��������Դ������...

                for (int i = 0; i < dsArr.length; i++)
                {
                    String currentDS = (String) dsArr[i];
                    String currentTable = StringUtility.getLeftWord(currentDS,
                            currentDS.indexOf("."));

                    //��������
                    String rowCondition = (String) rowHeaderDefineMap.get(
                            BlockElement.CONDITION);
                    //build it
                    StringBuffer sb = new StringBuffer();

                    sb.append("select ")
                            .append(currentDS)
                            .append(" from ")
                            .append(currentTable) //����
                            .append(" ")
                            .append(getGlobalWhere(defineBlock, currentTable));

                    //�����()
                    if (null != condition && !"".equals(condition))
                    {
                        sb.append(" and (")
                                .append(condition) //��������
                                .append(")");
                    }

                    if (null != rowCondition && !"".equals(rowCondition))
                    {
//            //���ϵ�������Ҫת��,ȡ������߱��ʽ,ת��Ϊ�����
//            Set subWCSet=new HashSet();
//            XRRegExp.getWhereClause(rowCondition,subWCSet);
//            Iterator itSubKey=subWCSet.iterator();
//            while(itSubKey.hasNext())
//            {
//              WhereClause wc=(WhereClause) itSubKey.next();
//              String actualField=tableRelations.getActualField(wc.getLeft(),currentTable);
//
//              if(actualField==null) //�Ǳ����ֶ�
//              {
//                rowCondition=StringUtility.replace(rowCondition,wc.context," 1=1 "); //ȡ������,����1=1
//              }
//
//              log.debug("HERE ERROR:"+wc);
//
//              RE re=new RE("\\b"+wc.getLeft()+"\\b");
//              rowCondition=StringUtility.replaceWord(rowCondition,wc.getLeft(),actualField);
//            }
//
//            sb.append(" and (")
//            .append(rowCondition) //��������
//            .append(")");

                        sb.append(" and (")
                                .append(tableRelations.getActualCondition(
                                rowCondition, currentTable)) //��������
                                .append(")");
                    }

                    String value = getOneValueFromDB(sb.toString());

//          RE re=new RE("\\b"+dsArr[i]+"\\b");
                    cellValue = StringUtility.replaceWord(cellValue,
                            dsArr[i].toString(), value); //�滻cellValue
                } //��������ֵok
            }

//      if(hasRefOther)
//      {
//        //���ȡ��...
//        /**@todo: ��λ������*/
//        ReportData otherReport=ReportDataFactory.getInstance().getReportData("");
//        /**@todo:Ϊÿ������ֵ*/
//        //�滻cellValue
//        //�滻Ϊ$bq("[].[].[]")����
//      }

            //������,��ǰ֧��$bq(),$tq(),$sq()
            cellValue = replaceRef(cellValue, rowName, COLREF);

            if (hasRefSelf)
            {
                //����
                //ȡ��Ŀ�굥Ԫ���λ��...must
                while (reRefSelf.match(cellValue))
                {
                    String refColName = reRefSelf.getParen(1); //���յ�����
                    String rql = "[" + refColName + "].[" + rowName + "]";
                    String location = (String) dataBlock.getCell(rql).
                                      getLocation();
                    String reStr = "get$" + location + "()";

                    cellValue = StringUtility.replace(cellValue,
                            "[" + refColName + "]", reStr);
                }

            } //�滻���ڲ���over

            log.debug("after recalCol:" + cell.getLocation() + ":" + cellValue);

            cell.setValue(cellValue); //����ֵ
        }
    }

//  private String getGlobleWhere(String tableName)
//  {
//    return "where 1=1 ";
//  }

    /**
     * �õ�ÿ�����Ӧ��where����
     * @todo: ��������������ĺϲ�����. important.
     * @param selectItems ���ǰ�벿��
     * @param wcSet ����where�Ӿ伯��
     * @return ÿ�����where�Ӿ�
     * @throws Exception
     */
    private void getWhereClause(DefineBlock defineBlock, Map selectItems,
                                Set wcSet) throws Exception
    {
        Map result = new HashMap();
        // ÿ�еĿ���ȡֵ[����.����->ȡֵSet]
        Map colValues = new HashMap();

        //����wcSet,��ÿ���ֶ�ȡֵ��������colValues,�Ա㹹��in () �Ӿ�.
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
            // ��������ݿ�ֻ������ȵ����
            // very important
            ///////////////////////////////////////////////////
            if ("=".equals(wc.cp))
            {
                valueSet.add(wc.right);
            }
        }
        //...ok,�Ѿ����ֶ�ȡֵ��������colValues��


        Iterator itTabs = selectItems.keySet().iterator();

        //ÿ�����������������
        while (itTabs.hasNext())
        {
            String currentTable = (String) itTabs.next();
            //select ��Ŀ
            StringBuffer selectClause = (StringBuffer) selectItems.get(
                    currentTable);
            selectClause.append(getGlobalWhere(defineBlock, currentTable)); //����where��ȫ�ֵ�����

            //�ֶ�ȡֵ����
            Iterator it = colValues.keySet().iterator();
            while (it.hasNext()) //�ֶ� ����
            {
                //����.�ֶ���
                String currentColumn = (String) it.next();
                String actualCon = ""; //�ֶ���ɵ�in�Ӿ�
                String actualField = ""; //ʵ���ڱ���������ֶ�,��ʱ��Ҳ��Ҫ�ŵ�select �м�ȥ

                actualField = tableRelations.getActualField(currentColumn,
                        currentTable);

                if (actualField != null) //��ƥ����ֶ�
                {
                    if (!((Set) colValues.get(currentColumn)).isEmpty()) //������=��ȡֵ, a=b
                    {
                        actualCon = buildInClause(actualField,
                                                  (Set) colValues.
                                                  get(currentColumn));

                        selectClause.append(" and ")
                                .append(actualCon);
                        selectClause.insert("select ".length(),
                                            actualField + ","); //�ֶ�Ҳ�ŵ�selectǰ��.
                    }
                }

            } //end while

        } //end while

        //��selectItmes�е�where����ƴ����.

    }


    ///////////////////
    //�����յ�����
    /**���в���*/
    private static final int COLREF = 0;
    /**���в���*/
    private static final int ROWREF = 1;
    /**��Ԫ�����*/
    private static final int CELLREF = 2;

    /**
     * ���㱾��,ͬ��,���ڵı�����.
     * @param refName �仯�Ĳ��յ�����,��������(when type==COLREF);��������(when type==REFREF);��CELL
     * @param cellValue ��Ԫ���ֵ
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
            String $1 = bqRE.getParen(1); //�������
            String $2 = bqRE.getParen(2); //type==COLREF:������ type==ROWREF:������
            String $4 = bqRE.getParen(4);

            if ($4 != null) //���յ�����
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //���в���,rqlƴ����.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //���в���,rqlƴ����
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //Ŀ�걨��
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
                //���ܼ��ر������,Ҳ���ܲ�ѯ���ݳ���,ֵΪ"0",���뾯��
                log.error(ex.getMessage());
                value = "0";
            }

            //��ֵ�滻
            result = StringUtility.replace(result, $0, value);
        }

        //sq
        while (sqRE.match(result))
        {
            String rql;

            String $0 = sqRE.getParen(0);
            String $1 = sqRE.getParen(1); //�������
            String $2 = sqRE.getParen(2); //type==COLREF:������ type==ROWREF:������
            String $4 = sqRE.getParen(4);

            if ($4 != null) //���յ�����
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //���в���,rqlƴ����.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //���в���,rqlƴ����
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //Ŀ�걨��
            targetDataInfo = reportData.getDataInfo().sq($1);
            try
            {
                ReportData refReport = ReportDataFactory.getInstance().
                                       getReportData(targetDataInfo);
                value = refReport.getValue(rql);
            }
            catch (Exception ex)
            {
                //���ܼ��ر������,Ҳ���ܲ�ѯ���ݳ���,ֵΪ"0",���뾯��
                log.error(ex.getMessage());
            }

            //��ֵ�滻
            result = StringUtility.replace(result, $0, value);
        }

        //tq
        while (tqRE.match(result))
        {
            String rql;

            String $0 = tqRE.getParen(0);
            String $1 = tqRE.getParen(1); //�������
            String $2 = tqRE.getParen(2); //type==COLREF:������ type==ROWREF:������
            String $4 = tqRE.getParen(4);

            if ($4 != null) //���յ�����
            {
                rql = "[" + $2 + "].[" + $4 + "]";
            }
            else if (type == COLREF) //���в���,rqlƴ����.
            {
                rql = "[" + $2 + "].[" + refName + "]";
            }
            else //���в���,rqlƴ����
            {
                rql = "[" + refName + "].[" + $2 + "]";
            }

            String value = "0";
            //Ŀ�걨��
            targetDataInfo = reportData.getDataInfo().tq($1);
            try
            {
                ReportData refReport = ReportDataFactory.getInstance().
                                       getReportData(targetDataInfo);
                value = refReport.getValue(rql);
            }
            catch (Exception ex)
            {
                //���ܼ��ر������,Ҳ���ܲ�ѯ���ݳ���,ֵΪ"0",���뾯��
                log.error(ex.getMessage());
            }

            //��ֵ�滻
            result = StringUtility.replace(result, $0, value);
        }

        return result;

    }


    //GOD....�����к���,�ͱ��ȡ������BeanShellȥ��,��������ҵ������ô?...
    //how dare you do this? Yang!

    /**
     * ���ý�������ȡ������,��λ�����.
     * ���ù���:A1->get$A1()
     * @change log: �����쳣����,ʹ n/0 = 0; ����ж�NaN,����Ϊ0
     * @param defineBlock
     * @param dataBlock
     * @throws Exception
     */
    private void setColRow2Interpreter(DefineBlock defineBlock,
                                       DataBlock dataBlock) throws Exception
    {
        //���ݿ��Զ����ɵĽű�
        StringBuffer sbBlockAutoScript = new StringBuffer();

        //ȷ�ź���,������׼��ok

        //����ȡ�����滻...ok


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
                    value = "0"; //�����ǿ���,����
                }

                value = value.replace('\'', '\"'); // "'" -> """,�Ա���java�д���

                sbBlockAutoScript.append("get$") //get$A1(){ try {value = value; return (value==value)value?:0.0d } catch(Exception ex){return 0;}}
                        .append(loc)
                        .append("(){ try{ value = ")
                        .append(value)
                        .append("; return (value==value)?value:0.0d; }catch(Exception exxxxxxxxxxx){return 0d;} }\n");
            }
        }

        //special cells parse, �ŵ����������.
        Iterator itSPCellsKey = defineBlock.getCells().keySet().iterator();
        while (itSPCellsKey.hasNext())
        {
            String cellName = (String) itSPCellsKey.next();
            Map cellDefine = (Map) defineBlock.getCells().get(cellName);
            String location = (String) cellDefine.get(BlockElement.LOCATION);
            String context = (String) cellDefine.get(BlockElement.CONTEXT);

            context = context.replace('\'', '\"'); // "'" -> """,�Ա���java�д���

            //���ȡ��
            context = replaceRef(context, "", CELLREF);
            //����ȡ��
            context = replaceRefSelf(dataBlock, context);

            //�����쳣����.��ֹ0��
            sbBlockAutoScript.append("get$") //get$A1(){return value;}
                    .append(location)
                    .append("(){ try { value= ")
                    .append(context)
                    .append("; return (value==value)?value:0.0d; }catch(Exception exxxxxxxxxxx){return 0d;} }\n");

        }

        String autoSt = dealSum(sbBlockAutoScript.toString());

        log.debug("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n" + autoSt);

        //������һ���򽫽ű����õ�����,Ӧ���Զ��ĵݹ麯������,��ֵ����,�ű�ִ��.
        interpreter.eval(autoSt);

    }

    RE reRefSelfCell;

    /**
     * �滻���ڲ���.
     * @param dataBlock ��ǰ���ݿ�.
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
                throw new Exception("�����ļ�[" +
                                    reportData.getDataInfo().getDefineFilePath() +
                                    "]����:����--" + find + "--û�ж���.");
            }

            String loc = cell.getLocation();
            String replace = "get$" + loc + "()";

            result = StringUtility.replaceWord(result, find, replace);
        }
        return result;
    }


//  /**��ǰ����Ķ����,Ϊ����������,�ϸ�����ʹ��!!!*/
//  private DefineBlock currentDefineBlock; //no use

    /**
     * ����ȫ���趨�õ��������where����
     * �ں����ж��ֶ���ת��.
     * @param tableName ����
     * @return where����
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
            //p�ж��Ƿ��Ǳ����ֶ�
            String actualField = tableRelations.getActualField(left, tableName);
            if (actualField == null) //����
            {
                continue;
            }

            String right = (String) gMap.get(left);
            String rightContent = "";
            String op = "";

            //�жϵڶ������ʽ�Ƿ��бȽϷ�.
            if (right.startsWith(">")
                || right.startsWith("<")
                || right.startsWith("!")
                || right.startsWith("in")
                || right.startsWith("IN")
                || right.startsWith("=")
                    )
            {
            }
            else //û�бȽϷ�,ƴ��=
            {
                right = "=" + right;
            }

            right = right.replace('\'', '"'); //�滻Ϊһ���ַ�������

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
                /**@todo: in(,,)���ܹ���������*/
//        int leftBPos=right.indexOf("(");
//        int rightBPos=right.lastIndexOf(")");
//        right=
            }
            else //��=,<,>��ͷ
            {
//        right="\""+right.substring(0,1)+"\"'"+right.substring(1)+"'";
                op = right.substring(0, 1);
                rightContent = right.substring(1);
            }

            //ִ����
            interpreter.eval("auto_temp=" + rightContent);
            String value = interpreter.get("auto_temp").toString();

            sb.append(" and ")
                    .append(actualField)
                    .append(op)
                    .append("'")
                    .append(value)
                    .append("'");

            dataGCMap.put(left, op + value); //�Ѽ�����ȫ�������ŵ����ݿ��ȫ��������.
        }

        return sb.toString();
    }

    /**
     * ����ȫ������Map.
     * ����ȫ������.�������ұߵı��ʽ�������,�ŵ�DataBlock��gMap��.
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
     * ����condion�еı���,����.
     * @todo: ��ǰֻ�滻����,��������������.
     * ��condition�еı����ͺ�������:������$��ͷ!!! ��Ϊ�洢����Ҳ���к���,��������.
     * @param colHeader ��ͷ����
     * @param rowHeader ��ͷ����
     */
    private void dealConditionVarFunc(Map colHeaders, Map rowHeaders) throws
            Exception
    {
        //����
        //\$[\w_]*\(.*?\)
        RE reFunc = new RE("\\$[\\w_]*\\(.*?\\)");

        //����
        //\$[\w_]*
        RE reVar = new RE("\\$[\\w_]*");

        //colHeaders
        Iterator itCol = colHeaders.keySet().iterator();
        while (itCol.hasNext())
        {
            Object key = itCol.next();
            Object value = colHeaders.get(key); //һ��ͷ

            //����,��������
            //..ȡcondition
            Map col = (Map) value;
            String condition = (String) col.get(BlockElement.CONDITION);

            //����
            if (condition == null || "".equals(condition))
            {
                continue;
            }

            while (reFunc.match(condition))
            {
                String find = reFunc.getParen(0);
                interpreter.eval("auto_temp=" + find);
                String findValue = interpreter.get("auto_temp").toString();
                //�滻����ִ�н��
                condition = StringUtility.replaceWord(condition, find,
                        findValue);
            }

            //����
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
                    //�滻����ִ�н��
                    condition = StringUtility.replaceWord(condition, find,
                            findValue);
                }
            }
            //�Ż�����������
            col.put(BlockElement.CONDITION, condition);
        }

        //colHeaders
        Iterator itRow = rowHeaders.keySet().iterator();
        while (itRow.hasNext())
        {
            Object key = itRow.next();
            Object value = rowHeaders.get(key); //һ��ͷ

            //����,��������
            //..ȡformula
            Map row = (Map) value;

            String condition = (String) row.get(BlockElement.CONDITION);

            //����
            if (condition == null || "".equals(condition))
            {
                continue;
            }

            while (reFunc.match(condition))
            {
                String find = reFunc.getParen(0);
                interpreter.eval("auto_temp=" + find);
                String findValue = interpreter.get("auto_temp").toString();
                //�滻����ִ�н��
                condition = StringUtility.replaceWord(condition, find,
                        findValue);
            }

            //����
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
                    //�滻����ִ�н��
                    condition = StringUtility.replaceWord(condition, find,
                            findValue);
                }
            }
            //�Ż�����������
            row.put(BlockElement.CONDITION, condition);

        }
    }

    /**
     * ����where�е�in�Ӿ�
     * @param field �ֶ�
     * @param valueSet ֵ����
     * @return in�Ӿ�
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
            if (value.startsWith("'")) //�Ѱ���''
            {
                sb.append(value)
                        .append(",");
            }
            else //û�а���'',ƴ��
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
     * ��������Դ���ϵõ���Ӧ��select a,b from c �Ӿ�.
     * ��Ҫ�����ͬ��,��ͬ�ֶε�һ��ȡ��.
     * @param set ����Դ���� [c.a,c.b]
     * @return sqlǰ�벿��
     */
    private Map getSelectFrom(Set set)
    {
        //��¼�������select clause
        Vector v = new Vector();
        //��¼����λ���ϵı���.
        Vector tableLocation = new Vector();

        Object[] tc = set.toArray();

        //������?
        for (int i = 0; i < tc.length; i++)
        {
            String tableColumn = (String) tc[i];
            String tableName = StringUtility.getLeftWord(tableColumn,
                    tableColumn.indexOf("."));
            String columnName = StringUtility.getRightWord(tableColumn,
                    tableColumn.indexOf("."));

            //�жϸñ��select clause �Ƿ����
            int loc = tableLocation.indexOf(tableName);
            //������...�¼�һ��select
            if (loc == -1)
            {
                StringBuffer sb = new StringBuffer("select ");
                sb.append(columnName)
                        .append(",");
                v.add(sb);
                tableLocation.add(tableName); //��Ӧsql��Ӧ����������ͬ������
            }
            else //����,�� �ֶθ���ĩβ.��Ҫ�����һ����������ݶ�Ӧ������е�����.
            {
                StringBuffer sb = (StringBuffer) v.get(loc);
                sb.append(columnName)
                        .append(",");
            }
        }
        //...������

        Map result = new HashMap();

        //ȥ�����һ��',',���ϱ���
        for (int i = 0; i < v.size(); i++)
        {
            StringBuffer sb = (StringBuffer) v.get(i);

            sb.delete(sb.length() - 1, sb.length());
            sb.append(" from ")
                    .append(tableLocation.get(i));

            //����->sql��select���� ӳ���
            result.put(tableLocation.get(i), sb);
        }

        return result;

    }


    /**
     * �õ�ȫ���趨��where����.
     * @param gMap ȫ������
     * @return where����
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

//����ǰ���л���(��ǰ����/��ǰ����/����)���õ�������...
    private void setInterpreterEnv() throws Exception
    {
        //����˳������...
//    interpreter.set("$branch","330700");
//    interpreter.set("$operator","ya....");
//    System.out.println(interpreter.eval("$branch+$operator"));

        //��ǰ����,��ǰ����(Report)

//    interpreter.set("$branch","330700");
//    interpreter.set("$code","js0207h");
//    interpreter.set("$operator","ya....");
//    interpreter.set("$name","���ݷֹ�˾");
//    interpreter.set("$date","\"'"+getRunEnv().getEnv(Environment.CALCDATE)+"'\");");  //�������ɳ���,���С��.
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
     * �ж��Ƿ���Ҫ����
     * ͨ���Ƿ���'$',���ƺ��������ж�
     * @param txt �ַ���
     * @return
     * @throws Exception
     */
    private boolean needParse(String txt) throws Exception
    {
        //����
        if (txt.indexOf("$") > -1)
        {
            return true;
        }
        else
        {
            //��������?
            String sub = txt;

            for (int i = sub.indexOf("("); i > 0; sub = sub.substring(i + 1))
            {
                if (!"".equals(StringUtility.getLeftWord(sub, i)))
                {
                    return true;
                }
            }

            //����������?
            return txt.indexOf("+") > -1
                    || txt.indexOf("-") > -1
                    || txt.indexOf("*") > -1
                    || txt.indexOf("/") > -1;

        }
    }

    /**
     * ��sum(get$A1():get$A5())�滻��(get$A1()+...+get$A5)
     * @param content ���滻����
     * @return �滻���
     * @throws Exception ����
     */
    private String dealSum(String content) throws Exception
    {
        String result = content;

        //(sum|SUM|sum)\s*\(\s*get\$(\w*)\s*\(\s*\)\s*:\s*get\$(\w*)\s*\(\s*\)\s*\)
        RE re = new RE("([sS][uU][mM])\\s*\\(\\s*get\\$(\\w*)\\s*\\(\\s*\\)\\s*:\\s*get\\$(\\w*)\\s*\\(\\s*\\)\\s*\\)");

        while (re.match(result))
        {
            String replaceStr = re.getParen(0); //�ҵ��ĵ�ʽ
            String startLoc = re.getParen(2); //��ʼλ��
            String endLoc = re.getParen(3); //��ֹλ��
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
            //ȥ�����һ��"+"
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")"); //��()��ֹ����������

            result = StringUtility.replace(result, replaceStr, sb.toString());
        }

        return result;
    }


    /**
     * �õ�����/�����滻���.
     * @return �滻���
     * @todo: ��ϵͳ����,�����û�����,ͨ����������ʵ��
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
        //...������bsh�ܲ�������script�е�object
//  r.setInterpreter(new bsh.Interpreter());
//    r.setInterpreterEnv();
//  r.interpreter.eval("a=this");
//  System.out.println(r.interpreter.get("a"));
        //...pass

        //...����select�����
//  Set set=new HashSet();
//  set.add("a.b");
//  set.add("b.c");
//  set.add("a.c");
//
//  System.out.println(r.getSelectFrom(set));
        //result: [select a.c,a.b from a, select b.c from b]
        //...pass

        //���Թ����ֶβ���
//  Vector v=new Vector();
//  r.tr=new TableRelations(v);
//  v.add("target.branch=sourcetable.con");
//  v.add("sourcetable.con=target.branch");
//  String s = r.tr.getConvertField("sourcetable.con","target");
//  System.out.println(s);
        //...pass

        //���Խ������ݹ麯������
//    Interpreter i=new Interpreter();
//    i.eval("getA1(){return getA2()+2;}");
//    i.eval("getA2(){return 3+3;}");
//    i.eval("i=getA1();");
//    System.out.println(i.get("i"));
        //...pass

//    Report r=new Report("330700","js0207h","20020101");
//    System.err.println(r.dealSum("sum(get$AB1():get$B2())"));


        //2003-3-31 15:54 ģ�����
        long time = Calendar.getInstance().getTime().getTime();

        Environment env = new EnvironmentImpl();
        env.setEnv(Environment.CALCBRANCH, "86");
        env.setEnv(Environment.CALCDATE, "2003-2-25");
        env.setEnv(Environment.OPERATORID, "001");

        Report r = new Report("86", "AgentGroupInfo", "20050105");
        r.setRunEnv(env);

        //����BufferDataSourceʵ��
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
                return value; //������,ֱ�ӷ���
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
     * ��������Դ,����ʹ��bufferData
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
