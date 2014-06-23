/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 * B��---��C��ԭ
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ��ȫȷ���߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Tjj modify by Alex
 * @version 1.0
 */
public class ContCancelBack
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData;

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    private MMap mMap = new MMap();
    /** ���ݲ����ַ��� */
//    private String mOperate;
    /**������*/
    private String mContNo;
    /**��������*/
    private String mContType;
    /**��ز����Ĵ��� */
    private String mOtherNo;
    private String mEdorNo;
    //b��c�������밴˳��һһ��Ӧ

    public static final String[] mPContC = { "LCCont", "LCAppnt",
			"LCCustomerImpart", "LCCustomerImpartParams"};

    public static final String[] mPContB = { "LBCont", "LBAppnt",
			"LBCustomerImpart", "LBCustomerImpartParams" };

	private static final String[] mPPolC = { "LCPol", "LCInsureAcc",
			"LCInsureAccTrace", "LCDuty", "LCPrem", "LCGet", "LCBnf",
			"LCInsuredRelated", "LCPremToAcc", "LCGetToAcc", "LCInsureAccFee",
			"LCInsureAccClassFee", "LCInsureAccClass", "LCInsureAccFeeTrace",
			"LCPerInvestPlan" };

	private static final String[] mPPolB = { "LBPol", "LBInsureAcc",
			"LBInsureAccTrace", "LBDuty", "LBPrem", "LBGet", "LBBnf",
			"LBInsuredRelated", "LBPremToAcc", "LBGetToAcc", "LBInsureAccFee",
			"LBInsureAccClassFee", "LBInsureAccClass", "LBInsureAccFeeTrace",
			"LBPerInvestPlan" };
	ExeSQL exeSql = new ExeSQL();

    /** ȫ������ */
// private GlobalInput mGlobalInput = new GlobalInput();

    /**
     * constructor
     */
    public ContCancelBack()
    {}

    public ContCancelBack(String aContNo)
    {
        mContNo = aContNo;
    }

    /**
     * ������ʽһ
     * @param aContNo String
     * @param aOtherNo String
     */
    public ContCancelBack(String aContNo, String aOtherNo)
    {
        mContNo = aContNo;
        mOtherNo = aOtherNo;
        mContType = "I";
    }

    /**
     * ������ʽ��
     * @param aContNo String
     * @param aEdorNo String
     * @param aContType String
     */
    public ContCancelBack(String aContNo, String aEdorNo, String aContType)
    {
        mContNo = aContNo;
        mEdorNo = aEdorNo;
        mContType = aContType; //G�ŵ���I����
    }

    /**
     * �������ݵĹ�������
     * @return boolean
     */
    public boolean submitData()
    {
        if (dealData())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���ݴ���
     * @return boolean
     */
    public boolean dealData()
    {
        if (mContNo == null || mEdorNo == null || "".equals(mContNo) || "".equals(mContNo))
        {
            mErrors.addOneError("����Ĳ���δ������");
            return false;
        }
        mResult.clear();
        mResult.add(mMap);
        return true;
    }
    /**
     * ׼����Ҫ��������� (���ݺ�ͬ��Ϣ)
     * @param aContNo String
     * @param aEdorNo String
     * @return MMap
     */
    public MMap prepareContData(String aContNo)
    {

        StringBuffer tSBql = null;
        String strCol = "";
        MMap map = new MMap();
        //�����е�c���b�����һһ��Ӧ
        String[] lcTable =mPContC;
                
        String[] lbTable =mPContB;
                

        for (int i = 0; i < lcTable.length; i++)
        {
        	strCol = tableColSql(lcTable[i]);
        	if(!strCol.equals(""))
        	{
        		tSBql = new StringBuffer(128);
        		tSBql.append("insert into ");
        		tSBql.append(lcTable[i]);
        		tSBql.append(" (select ");
        		tSBql.append(strCol);
        		tSBql.append(" from ");
        		tSBql.append(lbTable[i]);
        		tSBql.append(" where ContNo='");
        		tSBql.append(aContNo);
        		tSBql.append("')");
                         
        		//    String sqlStr = "insert into " + lcTable[i] + " (select " + strCol + " from " + lbTable[i] +" where ContNo='" + aContNo + "')";
        		map.put(tSBql.toString(), "INSERT");
        	}
        }
        for (int i = 0; i < lbTable.length; i++)
        {
            tSBql = new StringBuffer(128);
            tSBql.append("delete from ");
            tSBql.append(lbTable[i]);
            tSBql.append(" where ContNo='");
            tSBql.append(aContNo);
            tSBql.append("'");
//            sqlStr = "delete from " + lcTable[i] + " where ContNo='" +
//                    aContNo + "'";
            map.put(tSBql.toString(), "DELETE");
        }


        LBInsuredSet tLBInsuredSet = new LBInsuredSet();
        LBInsuredDB tLBInsuredDB = new LBInsuredDB();
        tLBInsuredDB.setContNo(aContNo);
        tLBInsuredSet = tLBInsuredDB.query();
        if (tLBInsuredDB.mErrors.needDealError())
        {
            CError.buildErr(this, "��ѯ���˺�ͬ" + aContNo + "��������ʧ�ܣ�");
            return null;
        }
        for (int i = 1; i <= tLBInsuredSet.size(); i++)
        {
            MMap tmap = new MMap();
            tmap = this.prepareInsuredData(aContNo, tLBInsuredSet.get(i).getInsuredNo());
            if (mErrors.needDealError())
            {
                CError.buildErr(this, "׼����������" + tLBInsuredSet.get(i).getInsuredNo() + "����ʧ�ܣ�");
                return null;
            }
            map.add(tmap);
        }

        return map;
    }
    /**
     * ׼����Ҫ��������� (���ݱ���������Ϣ)
     * @param aContNo String
     * @param aInsuredNo String
     * @param aEdorNo String
     * @return MMap
     */
    public MMap prepareInsuredData(String aContNo, String aInsuredNo)
    {
//        String sqlStr = "";
        StringBuffer tSBql = null;
        MMap map = new MMap();

        LBPolSet tLBPolSet = new LBPolSet();
        LBPolDB tLBPolDB = new LBPolDB();
        tLBPolDB.setContNo(aContNo);
        tLBPolDB.setInsuredNo(aInsuredNo);
        tLBPolSet = tLBPolDB.query();
        if (tLBPolDB.mErrors.needDealError())
        {
            CError.buildErr(this, "��ѯ��������" + aInsuredNo + "�����ֱ���ʧ�ܣ�");
            return null;
        }
        for (int i = 1; i <= tLBPolSet.size(); i++)
        {
            LBPolSchema tLBPolSchema = new LBPolSchema();
            tLBPolSchema = tLBPolSet.get(i);
            MMap tmap = new MMap();
            tmap = this.preparePolData(tLBPolSchema.getPolNo());
            if (mErrors.needDealError())
            {
                CError.buildErr(this, "׼�����ֱ�����" + tLBPolSchema.getPolNo() + "����ʧ�ܣ�");
                return null;
            }
            map.add(tmap);
        }
        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCInsured (select ");
        tSBql.append(tableColSql("LCInsured"));
        tSBql.append(" from LBInsured where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and InsuredNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("')");
//        sqlStr = "insert into LBInsured (select '" + aEdorNo +
//                "',LCInsured.* from LCInsured where ContNo='" + aContNo +
//                "' and InsuredNo='" + aInsuredNo + "')";
        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append("delete from LBInsured where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and InsuredNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("'");
//        sqlStr = "delete from LCInsured where ContNo='" +
//                aContNo + "' and InsuredNo='" + aInsuredNo + "'";
        map.put(tSBql.toString(), "DELETE");

        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpart (select ");
        tSBql.append(tableColSql("LCCustomerImpart"));
        tSBql.append(" from LBCustomerImpart where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and CustomerNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("' and CustomerNoType='I')");
//        sqlStr = "insert into LBCustomerImpart (select '" + aEdorNo +
//                "',LCCustomerImpart.* from LCCustomerImpart where ContNo='" +
//                aContNo + "' and CustomerNo='" + aInsuredNo +
//                "' and CustomerNoType='I')";
        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append("delete from LBCustomerImpart where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and CustomerNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("' and CustomerNoType='I'");
//        sqlStr = "delete from LCCustomerImpart where ContNo='" +
//                aContNo + "' and CustomerNo='" + aInsuredNo +
//                "' and CustomerNoType='I'";
        map.put(tSBql.toString(), "DELETE");

        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpartParams (select ");
        tSBql.append(tableColSql("LCCustomerImpartParams"));
        tSBql.append(" from LBCustomerImpartParams where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and CustomerNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("' and CustomerNoType='I')");
//        sqlStr = "insert into LBCustomerImpartParams (select '" + aEdorNo +
//                "',LCCustomerImpartParams.* from LCCustomerImpartParams where ContNo='" +
//                aContNo + "' and CustomerNo='" + aInsuredNo +
//                "' and CustomerNoType='I')";
        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append("delete from LBCustomerImpartParams where ContNo='");
        tSBql.append(aContNo);
        tSBql.append("' and CustomerNo='");
        tSBql.append(aInsuredNo);
        tSBql.append("' and CustomerNoType='I'");
//        sqlStr = "delete from LCCustomerImpartParams where ContNo='" +
//                aContNo + "' and CustomerNo='" + aInsuredNo +
//                "' and CustomerNoType='I'";
        map.put(tSBql.toString(), "DELETE");

        return map;
    }
    /**
     * ׼����Ҫ��������� (���ݱ�����Ϣ)
     * @param aPolNo String
     * @param aEdorNo String
     * @return MMap
     */
    public MMap preparePolData(String aPolNo)
    {
        //�������
//        String sqlStr = "";
        StringBuffer tSBql = null;
        String strCol = "";
        MMap map = new MMap();
        String[] lcTable =mPPolC;
                
        String[] lbTable =mPPolB;
               

        for (int i = 0; i < lcTable.length; i++)
        {
        	strCol = tableColSql(lcTable[i]);
        	if(!strCol.equals(""))
        	{
        		tSBql = new StringBuffer(128);
        		tSBql.append("insert into ");
        		tSBql.append(lcTable[i]);
        		tSBql.append(" (select ");
        		tSBql.append(strCol);
        		tSBql.append(" from ");
        		tSBql.append(lbTable[i]);
        		tSBql.append(" where PolNo='");
        		tSBql.append(aPolNo);
        		tSBql.append("')");

            map.put(tSBql.toString(), "INSERT");
        	}
        }
        for (int i = 0; i < lbTable.length; i++)
        {
            tSBql = new StringBuffer(128);
            tSBql.append("delete from ");
            tSBql.append(lbTable[i]);
            tSBql.append(" where PolNo='");
            tSBql.append(aPolNo);
            tSBql.append("'");
            map.put(tSBql.toString(), "DELETE");
        }
        //��������������SQL
        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpart (select ");
        tSBql.append(tableColSql("LCCustomerImpart"));
        tSBql.append(" from LBCustomerImpart where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='3')");
        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append(
                "delete from LBCustomerImpart where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='3'");
        map.put(tSBql.toString(), "DELETE");

        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpartParams (select ");
        tSBql.append(tableColSql("LCCustomerImpartParams"));
        tSBql.append(" from LBCustomerImpartParams where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='I')");
        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append(
                "delete from LBCustomerImpartParams where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='I'");
        map.put(tSBql.toString(), "DELETE");

        //����ɾ�������յĴ��� Alex
        LBPolSet tLBPolSet = new LBPolSet();
        LBPolDB tLBPolDB = new LBPolDB();
        tLBPolDB.setMainPolNo(aPolNo);
        tLBPolSet = tLBPolDB.query();
        if (tLBPolDB.mErrors.needDealError())
        {
            CError.buildErr(this, "��ѯ���պ�" + aPolNo + "�����ֱ���ʧ�ܣ�");
            return null;
        }
        for (int i = 1; i <= tLBPolSet.size(); i++)
        {
            LBPolSchema tLBPolSchema = new LBPolSchema();
            tLBPolSchema = tLBPolSet.get(i);
            MMap tmap = new MMap();
            tmap = this.prepareSubPolData(tLBPolSchema.getPolNo());
            if (mErrors.needDealError())
            {
                CError.buildErr(this, "׼�����ֱ�����" + tLBPolSchema.getPolNo() + "����ʧ�ܣ�");
                return null;
            }
            map.add(tmap);
        }

        return map;
    }
    /**
     * ׼����Ҫ��������� (���ݱ�����Ϣ)
     * @param aPolNo String
     * @param aEdorNo String
     * @return MMap
     */
    public MMap prepareSubPolData(String aPolNo)
    {
        //�������
        StringBuffer tSBql = null;
        String strCol ="";
        MMap map = new MMap();
        String[] lcTable =this.mPPolC;
        String[] lbTable =this.mPPolB;
        for (int i = 0; i < lcTable.length; i++)
        {
//            tSBql = new StringBuffer(128);
//            tSBql.append("delete from ");
//            tSBql.append(lbTable[i]);
//            tSBql.append(" where PolNo='");
//            tSBql.append(aPolNo);
//            tSBql.append("'");
//
//            map.put(tSBql.toString(), "DELETE");
        	strCol = tableColSql(lcTable[i]);
        	if(!strCol.equals(""))
        	{
        		tSBql = new StringBuffer(128);
        		tSBql.append("insert into ");
        		tSBql.append(lcTable[i]);
        		tSBql.append(" (select ");
        		tSBql.append(strCol);
        		tSBql.append(" from ");
        		tSBql.append(lbTable[i]);
        		tSBql.append(" where PolNo='");
        		tSBql.append(aPolNo);
        		tSBql.append("')");

        		map.put(tSBql.toString(), "INSERT");
        	}
        }
        for (int i = 0; i < lbTable.length; i++)
        {
            tSBql = new StringBuffer(128);
            tSBql.append("delete from ");
            tSBql.append(lbTable[i]);
            tSBql.append(" where PolNo='");
            tSBql.append(aPolNo);
            tSBql.append("'");
            map.put(tSBql.toString(), "DELETE");
        }
        //��������������SQL
//        tSBql = new StringBuffer(128);
//        tSBql.append(
//                "delete from LBCustomerImpart where  ContNo in (select ContNO from LCPol where PolNo='");
//        tSBql.append(aPolNo);
//        tSBql.append("') and CustomerNo in (select CustomerNo from LCBnf where PolNo='");
//        tSBql.append(aPolNo);
//        tSBql.append("') and CustomerNoType='3'");
//        
//        map.put(tSBql.toString(), "DELETE");

        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpart (select ");
        tSBql.append(tableColSql("LCCustomerImpart"));
        tSBql.append(" from LBCustomerImpart where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='3')");

        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append(
                "delete from LBCustomerImpart where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='3'");
        
        map.put(tSBql.toString(), "DELETE");

//        tSBql = new StringBuffer(128);
//        tSBql.append(
//                "delete from LBCustomerImpartParams where ContNo in (select ContNO from LCPol where PolNo='");
//        tSBql.append(aPolNo);
//        tSBql.append("') and CustomerNo in (select CustomerNo from LCBnf where PolNo='");
//        tSBql.append(aPolNo);
//        tSBql.append("') and CustomerNoType='I'");
//
//        map.put(tSBql.toString(), "DELETE");

        tSBql = new StringBuffer(128);
        tSBql.append("insert into LCCustomerImpartParams (select ");
        tSBql.append(tableColSql("LCCustomerImpartParams"));
        tSBql.append(" from LBCustomerImpartParams where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='I')");

        map.put(tSBql.toString(), "INSERT");

        tSBql = new StringBuffer(128);
        tSBql.append(
                "delete from LBCustomerImpartParams where ContNo in (select ContNO from LBPol where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNo in (select CustomerNo from LBBnf where PolNo='");
        tSBql.append(aPolNo);
        tSBql.append("') and CustomerNoType='I'");

        map.put(tSBql.toString(), "DELETE");

        return map;
    }
    /**
     * ���ݴ���ı��������ز�ѯ�˱������ֶε�Sql���
     * @param aTable String C����
     * @return String
     */
    public String tableColSql(String aTable)
    {
    	String strSql = "";
    	String strCol = " select a.column_name from cols a where a.table_name='"+aTable.toUpperCase()+"' order by a.column_id ";
    	SSRS colSSRS = exeSql.execSQL(strCol);
    	if(colSSRS.getMaxRow()>0)
    	{
    		for(int i = 1; i < colSSRS.getMaxRow(); i++) {
    			strSql +=colSSRS.GetText(i, 1)+",";
    		}
    		strSql +=colSSRS.GetText(colSSRS.getMaxRow(), 1)+"";
    	}
    	//System.out.println("strSql==="+strSql);
    	return strSql;
    }
    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
    	
    }
}
