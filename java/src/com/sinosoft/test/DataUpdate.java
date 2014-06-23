package com.sinosoft.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.system;

import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.VData;

public class DataUpdate {
	private FileWriter fw;

	public void dataUpdate() {
		FileWriter file = null;
		try {
			file = new FileWriter("d:/update2.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String organSql = "select distinct  organcomcode,organinnercode from ltsendorgan where"
				+ " grpcontno='99029288' and organcomcode<>'0000' and grouplevel='1' "
				+ " and calmonth>='200806' and calmonth<='200807' order by organcomcode ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("������==" + ssrs.getMaxRow());
		// ����Ҫ������·�
		List yearMonthList = new ArrayList();
		// yearMonthList.add("200801");
		// yearMonthList.add("200802");
		// yearMonthList.add("200803");
		// yearMonthList.add("200804");
		// yearMonthList.add("200805");
		yearMonthList.add("200806");
		yearMonthList.add("200807");
		ExeSQL exeSQL2 = new ExeSQL();
		SSRS ssrs2 = new SSRS();
		int count = 1;
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);
			System.out.println("--------���������ǣ�" + organcomcode + "���������ǣ�"
					+ organinnercode);
			for (int j = 0; j < yearMonthList.size(); j++) {
				String calmonth = (String) yearMonthList.get(j);
				String avUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ " sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ " ',actmoney=actmoney+' ||"
						+ " addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ " ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ " contflag || ''' and proposalstate=''' || proposalstate ||"
						+ " ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ " substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ " '''and calmonth=''' || a.calmonth || ''';'"
						+ "from lcannualedorinfo a, lcsendorgan b "
						+ "where a.sendinnercode like b.organinnercode || '%' "
						+ "and sendinnercode like '"
						+ organinnercode
						+ "%' "
						+ " "
						+ "and a.calmonth = '"
						+ calmonth
						+ "' "
						+ "and b.organcomcode <> '0000' "
						+ "and edortype = 'AV' and (contflag = '1' or contflag='0')";
				System.out.println(avUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����  "
							+ count);
					writeLog(sql);
					count++;
				}

				String avUpdate2 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "   sendmoney || ',supplymoney=supplymoney+' || addmoney || "
						+ "  ',actmoney=actmoney+' || "
						+ " addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "  contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "  ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "   ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and b.organcomcode <> '0000'"
						+ "   and edortype = 'AV'"
						+ "  and contflag = '2'"
						+ " and not exists (select 'x'"
						+ "     from lcannualedorinfo"
						+ "   where insuredno = a.insuredno"
						+ "    and edortype = 'IB')";
				System.out.println(avUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա���� "
							+ count);
					writeLog(sql);
					count++;
				}
				String avUpdate3 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ " addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "   ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ " a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ " and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and edortype = 'AV'"
						+ " and contflag = '2'"
						+ " and exists (select 'x'"
						+ " from lcannualedorinfo"
						+ " where insuredno = a.insuredno"
						+ " and edortype = 'IB')";
				System.out.println(avUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա���� "
							+ count);
					writeLog(sql);
					count++;
				}
				String dvUpdate1 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ " sendmoney || ',deduckmoney=deduckmoney+' || reducemoney ||"
						+ "    ',actmoney=actmoney-' ||"
						+ "    reducemoney || ' where sendcomcode=''' || organcomcode ||"
						+ "    ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "     ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ " and edortype = 'DV'"
						+ " and exists (select 'x'"
						+ "       from lpinsured"
						+ "    where insuredno = a.insuredno"
						+ "     and edorno = a.edorno"
						+ "      and edortype = a.edortype"
						+ "        and insuredstat = '1')"
						+ "  and (contflag = '1' or contflag = '0' or"
						+ "   (contflag = '2' and not exists"
						+ "    (select 'x'"
						+ "   from lcannualedorinfo"
						+ "    where insuredno = a.insuredno"
						+ "       and edortype = 'IB')))";
				System.out.println(dvUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա���� "
							+ count);
					writeLog(sql);
					count++;
				}
				String dvUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' ||"
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "  organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "  ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "  proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "     ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "  and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and edortype = 'DV'"
						+ " and exists (select 'x'"
						+ "        from lpinsured"
						+ "        where insuredno = a.insuredno"
						+ "         and edorno = a.edorno"
						+ "        and edortype = a.edortype"
						+ "        and insuredstat = '1')"
						+ " and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "        from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "        and edortype = 'IB')";
				// System.out.println("------dvUpdate2");
				System.out.println(dvUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
					count++;
				}
				String dvUpdate3 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' ||"
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "     proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DV'"
						+ "  and exists (select 'x'"
						+ "        from lpinsured"
						+ "      where insuredno = a.insuredno"
						+ "         and edorno = a.edorno"
						+ "    and edortype = a.edortype"
						+ "    and insuredstat = '3')";
				// System.out.println("----dvUpdate3");
				System.out.println(dvUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
					count++;
				}
				String drUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ "  sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ " ',actmoney=actmoney+' ||"
						+ "   addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "   contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "   ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ " and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "          from lpinsured"
						+ "         where edorno = (select min(edorno)"
						+ "                  from lpinsured"
						+ "                   where insuredno = a.insuredno"
						+ "                      and grpcontno = a.grpcontno)"
						+ "           and insuredstat = '1')) or"
						+ "   (not exists (select 'x'"
						+ "                  from lpinsured"
						+ "           where insuredno = a.insuredno"
						+ "               and grpcontno = a.grpcontno"
						+ "              and edorno > a.edorno) and"
						+ " exists((select 'x'"
						+ "                  from lcinsured"
						+ "                 where insuredno = a.insuredno"
						+ "                   and grpcontno = a.grpcontno"
						+ "                   and insuredstat = '1'))))"
						+ " and (contflag = '1' or contflag = '0' or "
						+ "   (contflag = '2' and not exists"
						+ "  (select 'x'"
						+ "      from lcannualedorinfo"
						+ "      where insuredno = a.insuredno"
						+ "       and edortype = 'IB')))";
				ssrs2.Clear();
				System.out.println(drUpdate1);
				ssrs2 = exeSQL2.execSQL(drUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
					count++;
				}
				// System.out.println(drUpdate1);
				String drUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "   a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "             from lpinsured"
						+ "             where edorno = (select min(edorno)"
						+ "                from lpinsured"
						+ "                           where insuredno = a.insuredno"
						+ "                   and grpcontno = a.grpcontno)"
						+ "    and insuredstat = '1')) or"
						+ "    (not exists (select 'x'"
						+ "            from lpinsured"
						+ "             where insuredno = a.insuredno"
						+ "               and grpcontno = a.grpcontno"
						+ "               and edorno > a.edorno) and"
						+ "   exists((select 'x'"
						+ "              from lcinsured"
						+ "              where insuredno = a.insuredno"
						+ "            and grpcontno = a.grpcontno"
						+ "               and insuredstat = '1'))))"
						+ " and contflag = '2'"
						+ " and exists (select 'x'"
						+ "     from lcannualedorinfo"
						+ "     where insuredno = a.insuredno"
						+ "    and edortype = 'IB')";
				System.out.println(drUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(drUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
					count++;
				}
				String drUpdate3 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "            from lpinsured"
						+ "             where edorno = (select min(edorno)"
						+ "                        from lpinsured"
						+ "                       where insuredno = a.insuredno"
						+ "                         and grpcontno = a.grpcontno)"
						+ "         and insuredstat = '3')) or"
						+ "    (not exists (select 'x'"
						+ "                 from lpinsured"
						+ "                  where insuredno = a.insuredno"
						+ "                    and grpcontno = a.grpcontno"
						+ "                     and edorno > a.edorno) and"
						+ "     exists((select 'x'"
						+ "                    from lcinsured"
						+ "                     where insuredno = a.insuredno"
						+ "                       and grpcontno = a.grpcontno"
						+ "               and insuredstat = '3'))))";
				System.out.println(drUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(drUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
					count++;
				}
				String spUpdate1 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  sendmoney || ',deduckmoney=deduckmoney+' || reducemoney ||"
						+ "   ',actmoney=actmoney-' ||"
						+ "     reducemoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SP'"
						+ " and exists (select 'x'"
						+ "     from lpinsured"
						+ "      where insuredno = a.insuredno"
						+ "       and edorno = a.edorno"
						+ "        and edortype = a.edortype"
						+ "      and insuredstat = '1')"
						+ " and (contflag = '1' or  contflag = '0' or "
						+ "    (contflag = '2' and not exists"
						+ "   (select 'x'"
						+ "       from lcannualedorinfo"
						+ "     where insuredno = a.insuredno"
						+ "       and edortype = 'IB')))";
				System.out.println(spUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(spUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ");
					writeLog(sql);
					count++;
				}
				String spUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' || "
						+ "    reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "      ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "       ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ "   and edortype = 'SP'"
						+ "   and exists (select 'x'"
						+ "         from lpinsured"
						+ "         where insuredno = a.insuredno"
						+ "          and edorno = a.edorno"
						+ "           and edortype = a.edortype"
						+ "         and insuredstat = '1')"
						+ "  and contflag = '2'"
						+ " and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "         and edortype = 'IB')";
				System.out.println(spUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(spUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ");
					writeLog(sql);
					count++;
				}
				String srUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ " sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ "   ',actmoney=actmoney+' ||"
						+ "     addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "      ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "      contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "       substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SR'"
						+ "  and (contflag = '1' or contflag = '0' or "
						+ "      (contflag = '2' and not exists"
						+ "       (select 'x'"
						+ "           from lcannualedorinfo"
						+ "          where insuredno = a.insuredno"
						+ "            and edortype = 'IB')))";
				System.out.println(srUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(srUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ�ָ�");
					writeLog(sql);
					count++;
				}
				String srUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ " addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "    ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SR'"
						+ "  and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "        where insuredno = a.insuredno"
						+ "         and edortype = 'IB')";
				System.out.println(srUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(srUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ�ָ�");
					writeLog(sql);
					count++;
				}
				String ssUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "  sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ "  ',deduckmoney=deduckmoney+' || reducemoney || ',actmoney=actmoney+' ||"
						+ "   addmoney || '-'||reducemoney|| ' where sendcomcode=''' || organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "   contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "     ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SS'"
						+ "  and (contflag = '1' or contflag = '0' or "
						+ "     (contflag = '2' and not exists"
						+ "      (select 'x'"
						+ "        from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "        and edortype = 'IB')))";
				System.out.println(ssUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
					count++;
				}
				String ssUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "   addmoney || ',deduckmoney=deduckmoney+' || reducemoney || ',actmoney=actmoney+' || addmoney || '-'||reducemoney|| ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "     proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "     ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "     ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "       a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SS'"
						+ "  and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "        where insuredno = a.insuredno"
						+ "          and edortype = 'IB')";
				System.out.println(ssUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
					count++;
				}
				String ssUpdate3 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  c.oldsendmoney || ' where sendcomcode=''' || b.organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "     substr(c.oldsendstandard, -3, 3) || ''' and stockflag=''' ||"
						+ "     stockflag || ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b, lpedorannualconfig c"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  and c.edorno = a.edorno"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and c.edortype = 'SS'"
						+ "  and (contflag = '1' or contflag = '0' or "
						+ "      (contflag = '2' and not exists"
						+ "       (select 'x'"
						+ "         from lcannualedorinfo"
						+ "           where insuredno = a.insuredno"
						+ "        and edortype = 'IB')))";
				System.out.println(ssUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
					count++;
				}
				// תͶ����Ա��ҵ�������ֲ���������������ֻ���벹�۷����ö�ע��
				// String ssUpdate4 = "select 'update lccompersoninfo_bak set
				// reducenum=reducenum+1,reducemoney=reducemoney+' || "
				// + " c.oldsendmoney || ' where sendcomcode=''' ||
				// b.organcomcode ||"
				// + " ''' and retiretype=''' || retiretype || ''' and
				// contflag=''' ||"
				// + " contflag || ''' and proposalstate=''' || proposalstate
				// ||"
				// + " ''' and contnosub=''' || contnosub || ''' and
				// classinfo=''' ||"
				// + " substr(c.oldsendstandard, -3, 3) || ''' and stockflag='''
				// ||"
				// + " stockflag || ''' and calmonth=''' || a.calmonth || ''';'"
				// + " from lcannualedorinfo a, lcsendorgan b,
				// lpedorannualconfig c"
				// + " where a.sendinnercode like b.organinnercode || '%'"
				// + " and sendinnercode like '"
				// + organinnercode
				// + "%'"
				// + " and c.edorno = a.edorno"
				// + " "
				// + " and b.organcomcode <> '0000'"
				// + " and a.calmonth = '"
				// + calmonth
				// + "'"
				// + " and c.edortype = 'SS'"
				// + " and contflag = '2'"
				// + " and exists (select 'x'"
				// + " from lcannualedorinfo"
				// + " where insuredno = a.insuredno"
				// + " and edortype = 'IB')";
				// System.out.println(ssUpdate4);
				// ssrs2.Clear();
				// // ssrs2 = exeSQL2.execSQL(ssUpdate4);
				// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
				// System.out.println("---" + count);
				// writeLog("---" + count);
				// String sql = ssrs2.GetText(k, 1);
				// System.out.println(sql);
				// writeLog("-----------���� " + organcomcode + " ��Ա��׼���");
				// writeLog(sql);
				// count++;
				// }
				String ibUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ "   sendmoney || '  where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "      substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ "  and edortype = 'IB'";
				System.out.println(ibUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ibUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog(sql);
					count++;
				}
				String ibUpdate2 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' ||"
						+ "    sendmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype ||"
						+ "   ''' and contflag=''2'' and proposalstate=''2'' and contnosub=''0'' and classinfo=''' ||"
						+ "   substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'IB'";
				System.out.println(ibUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ibUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog(sql);
					count++;
				}
				String pdUpdate1 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' || "
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "     organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "     ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype like '%PD%'" + "  and addmoney > 0";
				System.out.println(pdUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(pdUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա�����������");
					writeLog(sql);
					count++;
				}
				String pdUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney +' || "
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "    ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and b.organcomcode <> '0000'"
						+ "  and edortype like '%PD%'"
						+ "  and reducemoney > 0";
				System.out.println(pdUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(pdUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա�����������");
					writeLog(sql);
					count++;
				}
				String sdUpdate1 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' || "
						+ "    addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "     organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "      ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SD'" + " and addmoney > 0";
				System.out.println(sdUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(sdUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					//System.out.println("---" + count);
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog(sql);

					count++;
				}
				String sdUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney +' ||"
						+ "  reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "   proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "   a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and edortype = 'SD'"
						+ "   and a.calmonth = '"
						+ calmonth + "'" + "  and reducemoney > 0";
				System.out.println(sdUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(sdUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					//System.out.println("---" + count);
					writeLog(sql);
					count++;
				}
				String itUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "    a.sendmoney || '  where sendcomcode=''' || c.organcomcode ||"
						+ "      ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "     contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "      substr(a.sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lpedorannualconfig b, lcsendorgan c"
						+ " where a.edorno = b.edorno"
						+ "  "
						+ "   and c.organcomcode <> '0000'"
						+ "  and a.edortype in ('IT', 'ST')"
						+ "   and b.sendcomcode <> b.oldsendcomcode"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and a.sendinnercode like c.organinnercode || '%'"
						+ "  and (select organinnercode"
						+ "        from lcsendorgan"
						+ "       where organcomcode = b.oldsendcomcode"
						+ "          and calmonth = a.calmonth) <>"
						+ "      (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = a.sendcomcode"
						+ "      )     and exists(select 'x' from lpinsured where "
						+ "insuredno=a.insuredno and  edorno=a.edorno and edortype=a.edortype "
						+ "and insuredstat='1')";
				System.out.println(itUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(itUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Աת��"
							+ count);
					writeLog(sql);
					count++;
				}
				String itUpdate2 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  a.sendmoney || ' where sendcomcode=''' || c.organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "     substr(a.sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "     ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lpedorannualconfig b, lcsendorgan c"
						+ " where a.edorno = b.edorno"
						+ "   "
						+ "  and c.organcomcode <> '0000'"
						+ "   and a.edortype in ('IT', 'ST')"
						+ "   and b.sendcomcode <> b.oldsendcomcode"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and (select organinnercode"
						+ "        from lcsendorgan"
						+ "       where organcomcode = b.oldsendcomcode"
						+ "       ) like '"
						+ organinnercode
						+ "%'"
						+ "  and (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = b.oldsendcomcode"
						+ "          ) like c.organinnercode || '%'"
						+ "  and (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = b.oldsendcomcode"
						+ "        ) <>"
						+ "       (select organinnercode"
						+ "         from lcsendorgan"
						+ "       where organcomcode = a.sendcomcode"
						+ "         ) and exists(select 'x' from lpinsured where "
						+ "insuredno=a.insuredno and  edorno=a.edorno and edortype=a.edortype "
						+ "and insuredstat='1')";
				System.out.println(itUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(itUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					//System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Աת�� "
							+ count);
					writeLog(sql);
					count++;
				}
				try {
					file.write(itUpdate2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dataUpdatett() {

		FileWriter file = null;
		try {
			file = new FileWriter("d:/update2.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String organSql = "select organcomcode,organinnercode from lcsendorgan where grpcontno='99029288' and organcomcode='3008' and  grouplevel='1'  order by organcomcode ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("������==" + ssrs.getMaxRow());
		// ����Ҫ������·�
		List yearMonthList = new ArrayList();
		yearMonthList.add("200801");
		yearMonthList.add("200802");
		yearMonthList.add("200803");
		yearMonthList.add("200804");
		yearMonthList.add("200805");
		// yearMonthList.add("200806");
		// yearMonthList.add("200807");
		ExeSQL exeSQL2 = new ExeSQL();
		SSRS ssrs2 = new SSRS();
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);
			System.out.println("--------���������ǣ�" + organcomcode + "���������ǣ�"
					+ organinnercode);
			for (int j = 0; j < yearMonthList.size(); j++) {
				String calmonth = (String) yearMonthList.get(j);
				String avUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ " sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ " ',actmoney=actmoney+' ||"
						+ " addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ " ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ " contflag || ''' and proposalstate=''' || proposalstate ||"
						+ " ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ " substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ " '''and calmonth=''' || a.calmonth || ''';'"
						+ "from lcannualedorinfo a, lcsendorgan b "
						+ "where a.sendinnercode like b.organinnercode || '%' "
						+ "and sendinnercode like '"
						+ organinnercode
						+ "%' "
						+ " "
						+ "and a.calmonth = '"
						+ calmonth
						+ "' "
						+ "and b.organcomcode <> '0000' "
						+ "and edortype = 'AV' " + "and contflag = '1'";
				System.out.println(avUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}

				String avUpdate2 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "   sendmoney || ',supplymoney=supplymoney+' || addmoney || "
						+ "  ',actmoney=actmoney+' || "
						+ " addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "  contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "  ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "   ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and b.organcomcode <> '0000'"
						+ "   and edortype = 'AV'"
						+ "  and contflag = '2'"
						+ " and not exists (select 'x'"
						+ "     from lcannualedorinfo"
						+ "   where insuredno = a.insuredno"
						+ "    and edortype = 'IB'"
						+ "  and calmonth >= a.calmonth)";
				System.out.println(avUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}
				String avUpdate3 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ " addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "   ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ " a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ " and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and edortype = 'AV'"
						+ " and contflag = '2'"
						+ " and exists (select 'x'"
						+ " from lcannualedorinfo"
						+ " where insuredno = a.insuredno"
						+ " and edortype = 'IB'"
						+ "and calmonth >= a.calmonth)";
				// System.out.println(avUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(avUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}
				String dvUpdate1 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ " sendmoney || ',deduckmoney=deduckmoney+' || reducemoney ||"
						+ "    ',actmoney=actmoney-' ||"
						+ "    reducemoney || ' where sendcomcode=''' || organcomcode ||"
						+ "    ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "     ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ " and edortype = 'DV'"
						+ " and exists (select 'x'"
						+ "       from lpinsured"
						+ "    where insuredno = a.insuredno"
						+ "     and edorno = a.edorno"
						+ "      and edortype = a.edortype"
						+ "        and insuredstat = '1')"
						+ "  and (contflag = '1' or"
						+ "   (contflag = '2' and not exists"
						+ "    (select 'x'"
						+ "   from lcannualedorinfo"
						+ "    where insuredno = a.insuredno"
						+ "       and edortype = 'IB'"
						+ "         and calmonth >= a.calmonth)))";
				System.out.println("-------dvUpdate1");
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}
				String dvUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' ||"
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "  organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "  ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "  proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "     ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "  and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and edortype = 'DV'"
						+ " and exists (select 'x'"
						+ "        from lpinsured"
						+ "        where insuredno = a.insuredno"
						+ "         and edorno = a.edorno"
						+ "        and edortype = a.edortype"
						+ "        and insuredstat = '1')"
						+ " and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "        from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "        and edortype = 'IB'"
						+ "      and calmonth >= a.calmonth)";
				System.out.println("------dvUpdate2");
				System.out.println(dvUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}
				String dvUpdate3 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' ||"
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "     proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DV'"
						+ "  and exists (select 'x'"
						+ "        from lpinsured"
						+ "      where insuredno = a.insuredno"
						+ "         and edorno = a.edorno"
						+ "    and edortype = a.edortype"
						+ "    and insuredstat = '3')";
				System.out.println("----dvUpdate3");
				System.out.println(dvUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(dvUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����");
					writeLog(sql);
				}
				String drUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ "  sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ " ',actmoney=actmoney+' ||"
						+ "   addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "   contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "   ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ " and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "          from lpinsured"
						+ "         where edorno = (select min(edorno)"
						+ "                  from lpinsured"
						+ "                   where insuredno = a.insuredno"
						+ "                      and grpcontno = a.grpcontno)"
						+ "           and insuredstat = '1')) or"
						+ "   (not exists (select 'x'"
						+ "                  from lpinsured"
						+ "           where insuredno = a.insuredno"
						+ "               and grpcontno = a.grpcontno"
						+ "              and edorno > a.edorno) and"
						+ " exists((select 'x'"
						+ "                  from lcinsured"
						+ "                 where insuredno = a.insuredno"
						+ "                   and grpcontno = a.grpcontno"
						+ "                   and insuredstat = '1'))))"
						+ " and (contflag = '1' or"
						+ "   (contflag = '2' and not exists"
						+ "  (select 'x'"
						+ "      from lcannualedorinfo"
						+ "      where insuredno = a.insuredno"
						+ "       and edortype = 'IB'"
						+ "      and calmonth >= a.calmonth)))";
				ssrs2.Clear();
				System.out.println("----drUpdate1");
				ssrs2 = exeSQL2.execSQL(drUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
				}
				// System.out.println(drUpdate1);
				String drUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "   a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ " and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "             from lpinsured"
						+ "             where edorno = (select min(edorno)"
						+ "                from lpinsured"
						+ "                           where insuredno = a.insuredno"
						+ "                   and grpcontno = a.grpcontno)"
						+ "    and insuredstat = '1')) or"
						+ "    (not exists (select 'x'"
						+ "            from lpinsured"
						+ "             where insuredno = a.insuredno"
						+ "               and grpcontno = a.grpcontno"
						+ "               and edorno > a.edorno) and"
						+ "   exists((select 'x'"
						+ "              from lcinsured"
						+ "              where insuredno = a.insuredno"
						+ "            and grpcontno = a.grpcontno"
						+ "               and insuredstat = '1'))))"
						+ " and contflag = '2'"
						+ " and exists (select 'x'"
						+ "     from lcannualedorinfo"
						+ "     where insuredno = a.insuredno"
						+ "    and edortype = 'IB'"
						+ "    and calmonth >= a.calmonth)";
				// System.out.println(drUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(drUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
				}
				String drUpdate3 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'DR'"
						+ "  and (exists((select 'x'"
						+ "            from lpinsured"
						+ "             where edorno = (select min(edorno)"
						+ "                        from lpinsured"
						+ "                       where insuredno = a.insuredno"
						+ "                         and grpcontno = a.grpcontno)"
						+ "         and insuredstat = '3')) or"
						+ "    (not exists (select 'x'"
						+ "                 from lpinsured"
						+ "                  where insuredno = a.insuredno"
						+ "                    and grpcontno = a.grpcontno"
						+ "                     and edorno > a.edorno) and"
						+ "     exists((select 'x'"
						+ "                    from lcinsured"
						+ "                     where insuredno = a.insuredno"
						+ "                       and grpcontno = a.grpcontno"
						+ "               and insuredstat = '3'))))";
				// System.out.println(drUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(drUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա����ָ�");
					writeLog(sql);
				}
				String spUpdate1 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  sendmoney || ',deduckmoney=deduckmoney+' || reducemoney ||"
						+ "   ',actmoney=actmoney-' ||"
						+ "     reducemoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SP'"
						+ " and exists (select 'x'"
						+ "     from lpinsured"
						+ "      where insuredno = a.insuredno"
						+ "       and edorno = a.edorno"
						+ "        and edortype = a.edortype"
						+ "      and insuredstat = '1')"
						+ " and (contflag = '1' or"
						+ "    (contflag = '2' and not exists"
						+ "   (select 'x'"
						+ "       from lcannualedorinfo"
						+ "     where insuredno = a.insuredno"
						+ "       and edortype = 'IB'"
						+ "     and calmonth >= a.calmonth)))";
				System.out.println("----spUpdate1");
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(spUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ");
					writeLog(sql);
				}
				String spUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney+' || "
						+ "    reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "      ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "       ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ " "
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ "   and edortype = 'SP'"
						+ "   and exists (select 'x'"
						+ "         from lpinsured"
						+ "         where insuredno = a.insuredno"
						+ "          and edorno = a.edorno"
						+ "           and edortype = a.edortype"
						+ "         and insuredstat = '1')"
						+ "  and contflag = '2'"
						+ " and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "         and edortype = 'IB'"
						+ "          and calmonth >= a.calmonth)";
				// System.out.println(spUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(spUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ");
					writeLog(sql);
				}
				String srUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ " sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ "   ',actmoney=actmoney+' ||"
						+ "     addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "      ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "      contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "       substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SR'"
						+ "  and (contflag = '1' or"
						+ "      (contflag = '2' and not exists"
						+ "       (select 'x'"
						+ "           from lcannualedorinfo"
						+ "          where insuredno = a.insuredno"
						+ "            and edortype = 'IB'"
						+ "          and calmonth >= a.calmonth)))";
				// System.out.println(srUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(srUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ�ָ�");
					writeLog(sql);
				}
				String srUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ " addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "    ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ " and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SR'"
						+ "  and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "        where insuredno = a.insuredno"
						+ "         and edortype = 'IB'"
						+ "         and calmonth >= a.calmonth)";
				// System.out.println(srUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(srUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��ͣ�ָ�");
					writeLog(sql);
				}
				String ssUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "  sendmoney || ',supplymoney=supplymoney+' || addmoney ||"
						+ "  ',actmoney=actmoney+' ||"
						+ "   addmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "   contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "     ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "    substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SS'"
						+ "  and contflag = '2'"
						+ "  and (contflag = '1' or"
						+ "     (contflag = '2' and not exists"
						+ "      (select 'x'"
						+ "        from lcannualedorinfo"
						+ "       where insuredno = a.insuredno"
						+ "        and edortype = 'IB'"
						+ "         and calmonth >= a.calmonth)))";
				// System.out.println(ssUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
				}
				String ssUpdate2 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' ||"
						+ "   addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "    organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "     proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "     ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "     ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "       a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype = 'SS'"
						+ "  and contflag = '2'"
						+ "  and exists (select 'x'"
						+ "         from lcannualedorinfo"
						+ "        where insuredno = a.insuredno"
						+ "          and edortype = 'IB'"
						+ "          and calmonth >= a.calmonth)";
				// System.out.println(ssUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
				}
				String ssUpdate3 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  c.oldsendmoney || ' where sendcomcode=''' || b.organcomcode ||"
						+ "  ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "     substr(c.oldsendstandard, -3, 3) || ''' and stockflag=''' ||"
						+ "     stockflag || ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b, lpedorannualconfig c"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  and c.edorno = a.edorno"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and c.edortype = 'SS'"
						+ "  and contflag = '2'"
						+ "  and (contflag = '1' or"
						+ "      (contflag = '2' and not exists"
						+ "       (select 'x'"
						+ "         from lcannualedorinfo"
						+ "           where insuredno = a.insuredno"
						+ "        and edortype = 'IB'"
						+ "         and calmonth >= a.calmonth)))";
				// System.out.println(ssUpdate3);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate3);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
				}
				String ssUpdate4 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "     c.oldsendmoney || ',  where sendcomcode=''' || b.organcomcode ||"
						+ "     ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "     contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "       substr(c.oldsendstandard, -3, 3) || ''' and stockflag=''' ||"
						+ "      stockflag || ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b, lpedorannualconfig c"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   and c.edorno = a.edorno"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and c.edortype = 'SS'"
						+ "   and contflag = '2'"
						+ "   and exists (select 'x'"
						+ "          from lcannualedorinfo"
						+ "        where insuredno = a.insuredno"
						+ "          and edortype = 'IB'"
						+ "         and calmonth >= a.calmonth)";
				// System.out.println(ssUpdate4);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ssUpdate4);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա��׼���");
					writeLog(sql);
				}
				String ibUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' ||"
						+ "   sendmoney || '  where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "      substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and b.organcomcode <> '0000'"
						+ "  and edortype = 'IB'";
				// System.out.println(ibUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ibUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog(sql);
				}
				String ibUpdate2 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' ||"
						+ "    sendmoney || ' where sendcomcode=''' || organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype ||"
						+ "   ''' and contflag=''2'' and proposalstate=''2'' and contnosub=''0'' and classinfo=''' ||"
						+ "   substr(sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "    ''' and calmonth=''' || a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'IB'";
				// System.out.println(ibUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(ibUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog(sql);
				}
				String pdUpdate1 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' || "
						+ "  addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "     organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "     ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "     a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   "
						+ "   and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "   and edortype like '%PD%'" + "  and addmoney > 0";
				// System.out.println(pdUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(pdUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա�����������");
					writeLog(sql);
				}
				String pdUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney +' || "
						+ "   reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "    proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "    ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "    ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and b.organcomcode <> '0000'"
						+ "  and edortype like '%PD%'"
						+ "  and reducemoney > 0";
				// System.out.println(pdUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(pdUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Ա�����������");
					writeLog(sql);
				}
				String sdUpdate1 = "select 'update lccompersoninfo_bak set supplymoney=supplymoney+' || "
						+ "    addmoney || ',actmoney=actmoney+' || addmoney || ' where sendcomcode=''' ||"
						+ "     organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "     ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "      proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "      ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "      ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "    a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "  and b.organcomcode <> '0000'"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and edortype = 'SD'" + " and addmoney > 0";
				// System.out.println(sdUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(sdUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog(sql);
				}
				String sdUpdate2 = "select 'update lccompersoninfo_bak set deduckmoney=deduckmoney +' ||"
						+ "  reducemoney || ',actmoney=actmoney-' || reducemoney || ' where sendcomcode=''' ||"
						+ "   organcomcode || ''' and retiretype=''' || retiretype ||"
						+ "    ''' and contflag=''' || contflag || ''' and proposalstate=''' ||"
						+ "   proposalstate || ''' and contnosub=''' || contnosub ||"
						+ "   ''' and classinfo=''' || substr(sendstandard, -3, 3) ||"
						+ "   ''' and stockflag=''' || stockflag || ''' and calmonth=''' ||"
						+ "   a.calmonth || ''';'"
						+ " from lcannualedorinfo a, lcsendorgan b"
						+ " where a.sendinnercode like b.organinnercode || '%'"
						+ "  and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "  "
						+ "   and b.organcomcode <> '0000'"
						+ "   and edortype = 'SD'"
						+ "   and a.calmonth = '"
						+ calmonth + "'" + "  and reducemoney > 0";
				// System.out.println(sdUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(sdUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog(sql);
				}
				String itUpdate1 = "select 'update lccompersoninfo_bak set addnum=addnum+1,addmoney=addmoney+' || "
						+ "    a.sendmoney || '  where sendcomcode=''' || c.organcomcode ||"
						+ "      ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "     contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "      ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "      substr(a.sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "      ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lpedorannualconfig b, lcsendorgan c"
						+ " where a.edorno = b.edorno"
						+ "  "
						+ "   and c.organcomcode <> '0000'"
						+ "  and a.edortype in ('IT', 'ST')"
						+ "   and b.sendcomcode <> b.oldsendcomcode"
						+ "   and sendinnercode like '"
						+ organinnercode
						+ "%'"
						+ "   and a.calmonth = '"
						+ calmonth
						+ "'"
						+ "  and a.sendinnercode like c.organinnercode || '%'"
						+ "  and (select organinnercode"
						+ "        from lcsendorgan"
						+ "       where organcomcode = b.oldsendcomcode"
						+ "          and calmonth = a.calmonth) <>"
						+ "      (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = a.sendcomcode"
						+ "      )";
				// System.out.println(itUpdate1);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(itUpdate1);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Աת��");
					writeLog(sql);
				}
				String itUpdate2 = "select 'update lccompersoninfo_bak set reducenum=reducenum+1,reducemoney=reducemoney+' || "
						+ "  a.sendmoney || ' where sendcomcode=''' || c.organcomcode ||"
						+ "   ''' and retiretype=''' || retiretype || ''' and contflag=''' ||"
						+ "    contflag || ''' and proposalstate=''' || proposalstate ||"
						+ "    ''' and contnosub=''' || contnosub || ''' and classinfo=''' ||"
						+ "     substr(a.sendstandard, -3, 3) || ''' and stockflag=''' || stockflag ||"
						+ "     ''' and calmonth=''' || a.calmonth || ''';'"
						+ "  from lcannualedorinfo a, lpedorannualconfig b, lcsendorgan c"
						+ " where a.edorno = b.edorno"
						+ "   "
						+ "  and c.organcomcode <> '0000'"
						+ "   and a.edortype in ('IT', 'ST')"
						+ "   and b.sendcomcode <> b.oldsendcomcode"
						+ "  and a.calmonth = '"
						+ calmonth
						+ "'"
						+ " and (select organinnercode"
						+ "        from lcsendorgan"
						+ "       where organcomcode = b.oldsendcomcode"
						+ "       ) like '"
						+ organinnercode
						+ "%'"
						+ "  and (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = b.oldsendcomcode"
						+ "          ) like c.organinnercode || '%'"
						+ "  and (select organinnercode"
						+ "         from lcsendorgan"
						+ "        where organcomcode = b.oldsendcomcode"
						+ "        ) <>"
						+ "       (select organinnercode"
						+ "         from lcsendorgan"
						+ "       where organcomcode = a.sendcomcode"
						+ "         )";
				// System.out.println(itUpdate2);
				ssrs2.Clear();
				ssrs2 = exeSQL2.execSQL(itUpdate2);
				for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					String sql = ssrs2.GetText(k, 1);
					System.out.println(sql);
					writeLog("-----------����  " + organcomcode + "  ��Աת��");
					writeLog(sql);
				}
				try {
					file.write(itUpdate2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void dataCheck() {
		String organSql = "select organcomcode,organinnercode,childflag from lcsendorgan where grpcontno='99029288' and organcomcode='21041718'  and grouplevel='1'  and organcomcode<>'0000'   order by organcomcode desc ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("������==" + ssrs.getMaxRow());
		int total = ssrs.getMaxRow();
		// List yearMonthList = new ArrayList();
		// yearMonthList.add("200805");
		// yearMonthList.add("200806");
		// ����Ҫ������·�

		String[] yearmonnths = new String[] { "200712", "200801", "200802",
				"200803", "200804", "200805", "200806" };
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);

			String sql = "select organcomcode,organinnercode,childflag from lcsendorgan where organinnercode like '"
					+ organinnercode + "%' ";

			ExeSQL exeSQL2 = new ExeSQL();

			SSRS ssrs2 = exeSQL2.execSQL(sql);

			System.out.println("У�鵽һ������  " + organcomcode + "   ��" + i
					+ "�һ��� ����" + (total - i) + "�һ���");
			List errors = new ArrayList();
			for (int m = 1; m <= ssrs2.getMaxRow(); m++) {
				// ����ĩ������
				String sendcomcode = ssrs2.GetText(m, 1);
				String sendinnercode = ssrs2.GetText(m, 2);
				System.out.println("����  " + sendcomcode + " " + sendinnercode);
				String childflag = ssrs2.GetText(m, 3);
				String sendsql = "";
				if (childflag.equals("1")) {
					sendsql = " and sendinnercode like '" + sendinnercode
							+ "%' ";
				}
				// ĩ������
				else if (childflag.equals("0")) {
					sendsql = " and sendcomcode = '" + sendcomcode + "' ";
				}
				for (int j = 0; j < yearmonnths.length; j++) {
					String calmonth = yearmonnths[j];
					// ����ֻ�����򵥵�У��(lccompersoninfo_bak������ݺ�lccompersoninfo�������У��)

					// �������ĩ��������sendinnercode like
					// ��ĩ��������sendcomcode=
					String lcompersoninfosql = " select nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),nvl(sum(getmoney),0) from lccompersoninfo a where a.calmonth = '"
							+ calmonth + "'" + sendsql;
					// + " and sendcomcode like '"
					// + organcomcode + "%' ";
					// + " and sendinnercode like '"
					// + sendinnercode + "%' ";
					// ����lccompersoninfo_bak����sendcomcode=
					String lcompersoninfobaksql = "select  nvl(sum(lastgetnum),  0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),nvl(sum(getmoney),0) from lccompersoninfo_bak a where a.calmonth = '"
							+ calmonth
							+ "'"
							+ " and sendcomcode = '"
							+ sendcomcode + "' ";

					SSRS infoSSRS = exeSQL.execSQL(lcompersoninfosql);
					SSRS infobakSSRS = exeSQL.execSQL(lcompersoninfobaksql); // lccomperoninfo_bak
					double lastgetnum = 0.0;
					double lastgetmoney = 0.0;
					double getnum = 0.0;
					double getmoney = 0.0;
					if (infobakSSRS.getMaxRow() != 0) {
						lastgetnum = Double.parseDouble(infobakSSRS.GetText(1,
								1));
						lastgetmoney = Double.parseDouble(infobakSSRS.GetText(
								1, 2));
						getnum = Double.parseDouble(infobakSSRS.GetText(1, 3));
						getmoney = Double
								.parseDouble(infobakSSRS.GetText(1, 4));
					}
					// lccomperoninfo
					if (infoSSRS.getMaxRow() != 0) {
						double lastgetnumt = Double.parseDouble(infoSSRS
								.GetText(1, 1));
						double lastgetmoneyt = Double.parseDouble(infoSSRS
								.GetText(1, 2));
						double getnumt = Double.parseDouble(infoSSRS.GetText(1,
								3));
						double getmoneyt = Double.parseDouble(infoSSRS.GetText(
								1, 4));
						// if ("200806".equals(calmonth)) {
						if (getnum != getnumt || getmoney != getmoneyt) {
							System.out.println(lcompersoninfosql);
							System.out.println(lcompersoninfobaksql);
							System.out.println("���ִ���  ���� " + sendcomcode + " "
									+ calmonth + "�����ݲ���ȷ   " + sendinnercode);
							errors.add("���ִ���  ���� " + sendcomcode + " "
									+ calmonth + "�����ݲ���ȷ   " + sendinnercode);
							System.out.println("-----------" + sendcomcode
									+ "   " + sendinnercode + "  " + calmonth
									+ "--------------");
						}
						// } // 200805
						// else {
						// if (getnum != getnumt || getmoney != getmoneyt) {
						// System.out.println("���� " + sendcomcode + " "
						// + calmonth + "�����ݲ���ȷ");
						// }
						// }
					}
					// ��У�����

					// ���¿�ʼУ��lccompersoninfo_bak�ı��º����µ�����
					// �������еĻ���У�鱾�µ�lastgetnum��lastgetmoney�����µ�getnum��getmoney�Ƿ�һ��
					// SSRS ssrs2 = new SSRS();
					// if (!"200712".equals(calmonth)) {
					// String lastmonthsql = " select nvl(sum(getnum), 0) num,
					// nvl(sum(getmoney), 0) money from lccompersoninfo_bak
					// where calmonth='"+yearmonnths[j-1]+"' and sendcomcode='"
					// + sendcomcode + "'";
					// System.out.println(lastmonthsql);
					// ssrs2 = exeSQL.execSQL(lastmonthsql);
					// double getnum05 = 0.0;
					// double getmoney05 = 0.0;
					// if (ssrs2.getMaxRow() != 0) {
					// getnum05 = Double.parseDouble(ssrs2.GetText(1, 1));
					// getmoney05 = Double.parseDouble(ssrs2.GetText(1, 2));
					// }
					// ssrs2.Clear();
					// String nowmonthsql = "select nvl(sum(lastgetnum), 0) num,
					// nvl(sum(lastgetmoney), 0) money from lccompersoninfo_bak
					// where calmonth='"+yearmonnths[j]+"' and sendcomcode='"
					// + sendcomcode + "'";
					// ssrs2 = exeSQL.execSQL(nowmonthsql);
					// double getnum06 = 0.0;
					// double getmoney06 = 0.0;
					// System.out.println(nowmonthsql);
					// if (ssrs2.getMaxRow() != 0) {
					// getnum06 = Double.parseDouble(ssrs2.GetText(1, 1));
					// getmoney06 = Double.parseDouble(ssrs2.GetText(1, 2));
					// }
					// if (getnum05 != getnum06 || getmoney05 != getmoney06) {
					// System.out
					// .println("���ִ��� ����"
					// + sendcomcode
					// +yearmonnths[j]+ "
					// �µ�lastgetnum��lastgetmoney��"+yearmonnths[j-1]+"�µ�getnum��getmoney�Բ���");
					// }
					// }

					// У�����

					//
					// String checkSql1 = "select"
					// + " sum(lastgetnum),sum(lastgetmoney),sum(getnum),"
					// + " sum(getmoney),sum(totalmoney),sum(actmoney),calmonth
					// "
					// + " from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney)"
					// + " lastgetmoney,sum(getnum) getnum, sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney, sum(actmoney) actmoney,"
					// + " calmonth "
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' and sendcomcode='"
					// + sendcomcode
					// + "' "
					// + " group by calmonth union all select sum(-lastgetnum)"
					// + " lastgetnum, "
					// + " sum(-lastgetmoney) lastgetmoney, sum(-getnum) getnum,
					// "
					// + " sum(-getmoney) getmoney, sum(-totalmoney) totalmoney,
					// "
					// + " sum(-actmoney) actmoney, calmonth from
					// lccompersoninfo a"
					// + " where a.calmonth = '" + calmonth
					// + "' and sendcomcode = '" + sendcomcode + "' "
					// + "group by calmonth) group by calmonth ";
					// System.out.println(checkSql1);
					// SSRS ssrs2 = new SSRS();
					// if (ssrs2.getMaxRow() != 0) {
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (calmonth.equals("200805")) {
					// if (Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ");
					// }
					// } else {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ");
					// }
					// }
					//
					// }
					// }

					// ִ��sql֮���У���׼
					// a) ����У���±��е������Ƿ�ƽ

					// String checkSql3 = "select nvl(sum(lastgetnum +addnum
					// -reducenum"
					// + " -getnum),0) count ,"
					// + " nvl(sum(lastgetmoney + addmoney -reducemoney
					// -getmoney),0)"
					// + "money,"
					// + " nvl(sum(getmoney +supplymoney -deduckmoney -
					// totalmoney),0) "
					// + " totalmoney,nvl(sum(totalmoney - actmoney),0)
					// actmoney,sendcomcode"
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "'"
					// + " and sendinnercode like '"
					// + sendinnercode
					// + "%' group by sendcomcode"
					// + " having (sum(lastgetnum +addnum -reducenum
					// -getnum)<>0"
					// + " or sum(lastgetmoney + addmoney -reducemoney -"
					// + " getmoney)<>0"
					// + " or sum(getmoney +supplymoney - deduckmoney - "
					// + " totalmoney)<>0 or sum(totalmoney - actmoney)<>0)";
					// System.out.println("У��a===" + checkSql3);
					// ssrs2.Clear();
					// ssrs2 = exeSQL.execSQL(checkSql3);
					// if (ssrs2.getMaxRow() != 0) {
					// for (int m = 1; i <= ssrs2.getMaxRow(); m++) {
					// if (Double.parseDouble(ssrs2.GetText(m, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(m, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(m, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(m, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(m, 5)) != 0) {
					//
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��a");
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ.У��a");
					// }
					// }
					// }
					// //
					// b)����ٴ�У���¾ɱ���getnum��getmoney��lastgetnum��lastgetmoney�Ƿ�һ�£�
					// String checkSql4 = "select "
					// + "
					// nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),"
					// + "
					// nvl(sum(getmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0),retiretype"
					// + "from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney) "
					// + " lastgetmoney,sum(getnum) getnum,sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney,sum(actmoney) "
					// + " actmoney,retiretype"
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' "
					// + " and sendcomcode<>'"
					// + sendcomcode
					// + "' and sendinnercode like '"
					// + sendinnercode
					// + "%' group by retiretype union "
					// + " all select sum(-lastgetnum)
					// lastgetnum,sum(-lastgetmoney) "
					// + " lastgetmoney,"
					// + " sum(-getnum) getnum,sum(-getmoney) "
					// + " getmoney,sum(-totalmoney) totalmoney,sum(-actmoney) "
					// + " actmoney,retiretype from lccompersoninfo a where
					// a.calmonth = "
					// + " '" + calmonth + "' and sendinnercode like '"
					// + sendinnercode + "%' "
					// + " group by retiretype) group by retiretype";
					// System.out.println(checkSql4);
					// ssrs2.Clear();
					// ssrs2 = exeSQL.execSQL(checkSql4);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��b");
					// System.out.println("����" + sendcomcode + " " + calmonth
					// + "�����ݲ���ȷ.У��b");
					// }
					// }
					// // c) У���ĩ������ʵ���Ƿ��������ĩ������֮��
					// String checkSql5 = "select "
					// + "
					// nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),"
					// + "
					// nvl(sum(getmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0),calmonth
					// "
					// + " from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney)"
					// + " lastgetmoney,sum(getnum) getnum,sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney,sum(actmoney)"
					// + " actmoney,calmonth "
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' and sendcomcode = '"
					// + sendcomcode
					// + "' group by calmonth union all select sum(-lastgetnum)"
					// + " lastgetnum, sum(-lastgetmoney) lastgetmoney,
					// sum(-getnum)"
					// + " getnum, sum(-getmoney) getmoney,sum(-totalmoney)
					// totalmoney,"
					// + " sum(-actmoney) actmoney, calmonth from
					// lccompersoninfo_bak a"
					// + " where a.calmonth = '" + calmonth
					// + "' and sendinnercode like '" + sendinnercode
					// + "%' and sendcomcode <> '" + sendcomcode
					// + "' group by calmonth) group by calmonth ";
					// ssrs2.Clear();
					// System.out.println(checkSql5);
					// ssrs2 = exeSQL.execSQL(checkSql5);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// System.out.println("����" + sendcomcode + " " + calmonth
					// + "�����ݲ���ȷ.У��c");
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��c");
					// }
					// }
					// // d) У���ĩ���������������Ƿ��������ĩ������֮��
					// String checkSql6 = "select "
					// + "
					// nvl(sum(addnum),0),nvl(sum(addmoney),0),nvl(sum(reducenum),0),"
					// + "
					// nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),calmonth"
					// + " from (select sum(addnum) addnum,sum(addmoney)
					// addmoney,"
					// + " sum(reducenum) reducenum,sum(reducemoney)
					// reducemoney,"
					// + " sum(supplymoney) supplymoney,sum(deduckmoney) "
					// + "deduckmoney,"
					// + " calmonth from lccompersoninfo_bak a where a.calmonth
					// = '"
					// + calmonth
					// + "'"
					// + " and sendcomcode = '"
					// + sendcomcode
					// + "' group by calmonth union all "
					// + " select nvl(sum(-addnum),0)
					// addnum,nvl(sum(-addmoney),0) addmoney,"
					// + " nvl(sum(-reducenum),0)
					// reducenum,nvl(sum(-reducemoney),0) reducemoney,"
					// + " nvl(sum(-supplymoney),0)
					// supplymoney,nvl(sum(-deduckmoney),0) "
					// + " deduckmoney,"
					// + " calmonth from lccompersoninfo_bak a where a.calmonth
					// = '"
					// + calmonth + "' and sendinnercode like '"
					// + sendinnercode + "%' and sendcomcode <> '"
					// + sendcomcode
					// + "'group by calmonth) group by calmonth";
					// ssrs2.Clear();
					// System.out.println(checkSql6);
					// ssrs2 = exeSQL.execSQL(checkSql6);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// System.out.println("����" + sendcomcode + " " + calmonth
					// + "�����ݲ���ȷ.У��d");
					// // writeLog("����" + organcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��d");
					// }
					// }
					//
				}
			}
			if (errors.size() == 0) {
				System.out.println("һ������  " + organcomcode + " У�����û�д���");
			} else {
				System.out.println("һ������  " + organcomcode + " У����ɷ�������"
						+ errors.size() + "������");
			}

		}

		System.out.println("У�����!!!");
	}

	public void updateLccompersoninfobak() {
		String organSql = "select organcomcode,organinnercode,childflag from lcsendorgan where grpcontno='99029288' and organcomcode='3008'  and grouplevel='1'  and organcomcode<>'0000'   order by organcomcode ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("������==" + ssrs.getMaxRow());
		int total = ssrs.getMaxRow();
		String[] yearmonnths = new String[] { "200712", "200801", "200802",
				"200803", "200804", "200805", "200806" };
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);

			String sql = "select organcomcode,organinnercode,childflag from lcsendorgan where organinnercode like '"
					+ organinnercode + "%' ";

			ExeSQL exeSQL2 = new ExeSQL();

			SSRS ssrs2 = exeSQL2.execSQL(sql);

			System.out.println("--------------���µ�һ������  " + organcomcode
					+ "   ��" + i + "�һ��� ����" + (total - i) + "�һ���");
			for (int m = 1; m <= ssrs2.getMaxRow(); m++) {
				// ����ĩ������
				String sendcomcode = ssrs2.GetText(m, 1);
				String sendinnercode = ssrs2.GetText(m, 2);
				System.out.println("------------����  " + sendcomcode + " "
						+ sendinnercode);
				// writeLog("------һ������" + sendcomcode + " " + sendinnercode);
				for (int j = 0; j < yearmonnths.length; j++) {
					String calmonth = yearmonnths[j];

					// writeLog("------����" + sendcomcode + " " + sendinnercode
					// + " " + yearmonnths[j] + "��");
					if (!calmonth.equals("200806")) {
						String calmontht = yearmonnths[j + 1];
						String lcompersoninfosqlsum = "select  nvl(sum(getnum),0),nvl(sum(getmoney),0) from lccompersoninfo_bak a where a.calmonth = '"
								+ calmonth
								+ "'"
								+ " and sendcomcode = '"
								+ sendcomcode + "' ";
						String lcompersoninfosqlsumt = "select  nvl(sum(lastgetnum),  0),nvl(sum(lastgetmoney),0) from lccompersoninfo_bak a where a.calmonth = '"
								+ calmontht
								+ "'"
								+ " and sendcomcode = '"
								+ sendcomcode + "' ";
						SSRS ssrs3 = new SSRS();
						ssrs3 = exeSQL.execSQL(lcompersoninfosqlsum);

						double getnumsum = Double.parseDouble(ssrs3.GetText(1,
								1));
						double getmoneysum = Double.parseDouble(ssrs3.GetText(
								1, 2));

						ssrs3 = exeSQL.execSQL(lcompersoninfosqlsumt);
						double getnumsumt = Double.parseDouble(ssrs3.GetText(1,
								1));
						double getmoneysumt = Double.parseDouble(ssrs3.GetText(
								1, 2));
						if (getnumsum == getnumsumt
								&& getmoneysum == getmoneysumt) {
							System.out.println("----------����" + sendcomcode
									+ "��������ȷ����Ҫ����");
						}
						if (getnumsum != getnumsumt
								|| getmoneysum != getmoneysumt) {

							String lcompersoninfosql = " select getnum,getmoney,classinfo,contflag,proposalstate,contnosub,stockflag,retiretype,sendcomcode from lccompersoninfo_bak a where a.calmonth = '"
									+ calmonth
									+ "' and sendcomcode='"
									+ sendcomcode + "'";

							// String lcompersoninfosqlt = " select
							// getnum,getmoney,classinfo,contflag,proposalstate,contnosub,stockflag,retiretype,sendcomcode
							// from lccompersoninfo_bak a where a.calmonth = '"
							// + calmontht
							// + "' and sendcomcode='"
							// + sendcomcode + "'";
							SSRS infoSSRS = exeSQL.execSQL(lcompersoninfosql);
							for (int k = 1; k <= infoSSRS.getMaxRow(); k++) {
								String getnum = infoSSRS.GetText(k, 1);
								String getmoney = infoSSRS.GetText(k, 2);
								String classinfo = infoSSRS.GetText(k, 3);
								String contflag = infoSSRS.GetText(k, 4);
								String proposalstate = infoSSRS.GetText(k, 5);
								String contnosub = infoSSRS.GetText(k, 6);
								String stockflag = infoSSRS.GetText(k, 7);
								String retiretype = infoSSRS.GetText(k, 8);
								if (Double.parseDouble(getnum) != 0
										|| Double.parseDouble(getmoney) != 0) {
									String updatesql = "update  lccompersoninfo_bak set lastgetnum='"
											+ getnum
											+ "',lastgetmoney='"
											+ getmoney
											+ "' where classinfo='"
											+ classinfo
											+ "' and contflag='"
											+ contflag
											+ "' and proposalstate='"
											+ proposalstate
											+ "' and contnosub='"
											+ contnosub
											+ "' and stockflag='"
											+ stockflag
											+ "' and retiretype='"
											+ retiretype
											+ "' and calmonth='"
											+ yearmonnths[j + 1]
											+ "' and sendcomcode='"
											+ sendcomcode + "';";

									System.out.println(updatesql);
									// writeLog(updatesql);
									// writeLog("commit;");
									System.out.println("commit;");
								}
							}
						}
					}
				}
			}

		}

	}

	public void unseal(String fileLogPath) {
		try {
			fw = new FileWriter(fileLogPath);
			fw.write("*****\r\n");
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
	}

	public void colse() {
		try {
			fw.write("*****");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
	}

	public void writeLog(String log) {
		try {
			System.out.println(log);
			fw.write(log);
			fw.write("\r\n");
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
	}

	public void deleteData() {
		String organSql = "select organcomcode,organinnercode from lcsendorgan where grpcontno='99029288' and grouplevel='1'  order by organcomcode ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		VData data = new VData();
		PubSubmit pubSubmit = new PubSubmit();
		MMap map = new MMap();
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			String organinnercode = ssrs.GetText(i, 2);
			String sql = "delete from  lccompersoninfo_bak where calmonth='200805' and sendinnercode like '"
					+ organinnercode + "%'";
			map.put(sql, "DELETE");
		}
		data.add(map);
		if (!pubSubmit.submitData(data, "DELETE")) {
			System.out.println("ɾ������ʧ��");
		}
	}

	public void datacheckAfterUpdate() {
		// ִ��sql֮���У���׼
		// a) ����У���±��е������Ƿ�ƽ
		writeLog("����У�鿪ʼ");
		String organSql = "select organcomcode,organinnercode,childflag from lcsendorgan where grpcontno='99029288'  and grouplevel='1'  and organcomcode<>'0000' and organcomcode in ('1208')   order by organcomcode desc ";
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("������==" + ssrs.getMaxRow());
		int total = ssrs.getMaxRow();
		// List yearMonthList = new ArrayList();
		// yearMonthList.add("200805");
		// yearMonthList.add("200806");
		// ����Ҫ������·�

		String[] yearmonnths = new String[] { "200801", "200802", "200803",
				"200804", "200805" };

		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			List errors = new ArrayList();
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);

			String sql = "select organcomcode,organinnercode,childflag from lcsendorgan where organinnercode like '"
					+ organinnercode + "%' ";

			ExeSQL exeSQL2 = new ExeSQL();

			SSRS ssrs2 = exeSQL2.execSQL(sql);

			System.out.println("У�鵽һ������  " + organcomcode + "   ��" + i
					+ "�һ��� ����" + (total - i) + "�һ���");
			writeLog("У�鵽һ������  " + organcomcode + "   ��" + i + "�һ��� ����"
					+ (total - i) + "�һ���");
			for (int m = 1; m <= ssrs2.getMaxRow(); m++) {
				String sendcomcode = ssrs2.GetText(m, 1);
				String sendinnercode = ssrs2.GetText(m, 2);
				System.out.println("����  " + m + " " + sendcomcode + " "
						+ sendinnercode + "  " + ssrs2.getMaxRow());
				writeLog("����  " + m + " " + sendcomcode + " " + sendinnercode
						+ "  " + ssrs2.getMaxRow());

				String childflag = ssrs2.GetText(m, 3);
				// String sendsql = "";
				// if (childflag.equals("1")) {
				// sendsql = " and sendinnercode like '" + sendinnercode
				// + "%' ";
				// }
				// // ĩ������
				// else if (childflag.equals("0")) {
				// sendsql = " and sendcomcode = '" + sendcomcode + "' ";
				// }
				for (int j = 0; j < yearmonnths.length; j++) {
					String calmonth = yearmonnths[j];
					// ���Ⱥ�ԭ���Ļ��ܱ�У��
					// String compersoninfo = "select
					// nvl(sum(addnum),0),nvl(sum(addmoney),0),"
					// +
					// "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
					// + "nvl(sum(totalmoney),0),nvl(sum(actmoney),0) from
					// lccompersoninfo "
					// + "where calmonth = '" + calmonth + "' " + sendsql;
					// String compersoninfobak = "select
					// nvl(sum(addnum),0),nvl(sum(addmoney),0),"
					// +
					// "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
					// + "nvl(sum(totalmoney),0),nvl(sum(actmoney),0) from
					// lccompersoninfo_bak "
					// + "where calmonth = '"
					// + calmonth
					// + "' and sendcomcode='" + sendcomcode + "'";
					// SSRS ssrs3 = new SSRS();
					// ssrs3 = exeSQL2.execSQL(compersoninfo);
					// SSRS ssrs4 = new SSRS();
					// ssrs4 = exeSQL2.execSQL(compersoninfobak);
					// System.out.println(compersoninfo);
					// System.out.println(compersoninfobak);
					// if (ssrs3.getMaxRow() != 0 && ssrs4.getMaxRow() != 0) {
					// if (Double.parseDouble(ssrs3.GetText(1, 1)) != Double
					// .parseDouble(ssrs4.GetText(1, 1))
					// || Double.parseDouble(ssrs3.GetText(1, 2)) != Double
					// .parseDouble(ssrs4.GetText(1, 2))
					// || Double.parseDouble(ssrs3.GetText(1, 3)) != Double
					// .parseDouble(ssrs4.GetText(1, 3))
					// || Double.parseDouble(ssrs3.GetText(1, 4)) != Double
					// .parseDouble(ssrs4.GetText(1, 4))
					// || Double.parseDouble(ssrs3.GetText(1, 5)) != Double
					// .parseDouble(ssrs4.GetText(1, 5))
					// || Double.parseDouble(ssrs3.GetText(1, 6)) != Double
					// .parseDouble(ssrs4.GetText(1, 6))
					// || Double.parseDouble(ssrs3.GetText(1, 7)) != Double
					// .parseDouble(ssrs4.GetText(1, 7))
					// || Double.parseDouble(ssrs3.GetText(1, 8)) != Double
					// .parseDouble(ssrs4.GetText(1, 8))) {
					// if (Double.parseDouble(ssrs3.GetText(1, 7)) != Double
					// .parseDouble(ssrs3.GetText(1, 8))) {
					// System.out
					// .println("��������Ƚ�����ԭ���ܱ��totalmoney��actmoney�����");
					// writeLog("��������Ƚ�����ԭ���ܱ��totalmoney��actmoney�����");
					// }
					// System.out.println("���ִ��� ���� " + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ " + sendinnercode);
					// System.out.println(compersoninfo);
					// System.out.println(compersoninfobak);
					// writeLog("���ִ��� ���� " + sendcomcode + " " + calmonth
					// + "�����ݲ���ȷ " + sendinnercode);
					// writeLog(compersoninfo);
					// writeLog(compersoninfobak);
					// } else {
					// System.out.println("���� " + sendcomcode
					// + " ��ԭ�����ܱ������� " + calmonth);
					// writeLog("���� " + sendcomcode + " ��ԭ�����ܱ������� "
					// + calmonth);
					// }
					// }
					// end ���Ⱥ�ԭ���Ļ��ܱ�У��

					// ��ԭ���Ļ��ܱ�У�� ��ȷ��ԭ�����ܱ��ÿ����Ч�ļ�¼
					if (childflag.equals("0")) {
						String everyRecordSql = "select addnum,addmoney,reducenum,reducemoney,supplymoney,deduckmoney,totalmoney,actmoney,"
								+ "classinfo,contflag,proposalstate,retiretype,"
								+ "sendcomcode "
								+ "from lccompersoninfo where calmonth='"
								+ calmonth
								+ "' and sendcomcode ='"
								+ sendcomcode
								+ "' and ( (proposalstate='1' and contflag='1')or(proposalstate='2' and contflag='2')) and (addnum+reducenum>0 or "
								+ "addmoney+reducemoney+supplymoney+deduckmoney>0 "
								+ "or totalmoney<>0 or actmoney<>0 )";
						SSRS everyRecordSqlRS = exeSQL2.execSQL(everyRecordSql);
						for (int k = 1; k <= everyRecordSqlRS.getMaxRow(); k++) {
							String contflagt = "";
							if (everyRecordSqlRS.GetText(k, 10).equals("1")) {
								contflagt = " and contflag in ('0','1')";
							} else {
								contflagt = "and contflag ='"
										+ everyRecordSqlRS.GetText(k, 10)
										+ "' ";
							}
							String sumLccomperbaksql = "select nvl(sum(addnum),0),nvl(sum(addmoney),0),"
									+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),"
									+ "nvl(sum(deduckmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0) from lccompersoninfo_bak where sendcomcode='"
									+ sendcomcode
									+ "' and calmonth='"
									+ calmonth
									+ "' and classinfo='"
									+ everyRecordSqlRS.GetText(k, 9)
									+ "' and proposalstate='"
									+ everyRecordSqlRS.GetText(k, 11)
									+ "' and retiretype='"
									+ everyRecordSqlRS.GetText(k, 12)
									+ "' "
									+ contflagt;
							SSRS ssrs5 = new SSRS();
							ssrs5 = exeSQL2.execSQL(sumLccomperbaksql);
							if (ssrs5.getMaxRow() != 0) {

								if (Double.parseDouble(everyRecordSqlRS
										.GetText(k, 1)) != Double
										.parseDouble(ssrs5.GetText(1, 1))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 2)) != Double
												.parseDouble(ssrs5
														.GetText(1, 2))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 3)) != Double
												.parseDouble(ssrs5
														.GetText(1, 3))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 4)) != Double
												.parseDouble(ssrs5
														.GetText(1, 4))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 5)) != Double
												.parseDouble(ssrs5
														.GetText(1, 5))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 6)) != Double
												.parseDouble(ssrs5
														.GetText(1, 6))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 7)) != Double
												.parseDouble(ssrs5
														.GetText(1, 7))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 8)) != Double
												.parseDouble(ssrs5
														.GetText(1, 8))) {
									errors
											.add("������ ����" + sendcomcode + " "
													+ calmonth
													+ " ��ԭ���ܱ�����ݲ��� �����ǲ�ѯsql");
									System.out.println("������ ����" + sendcomcode
											+ " " + calmonth
											+ " ��ԭ���ܱ�����ݲ��� �����ǲ�ѯsql");
									errors.add(everyRecordSql);
									errors.add(sumLccomperbaksql);
									System.out.println(everyRecordSql);
									System.out.println(sumLccomperbaksql);
									System.out.println("������ ����" + sendcomcode
											+ " " + calmonth
											+ " ��ԭ���ܱ�����ݲ��� �����ǲ�ѯsql");
									System.out.println(everyRecordSql);
									System.out.println(sumLccomperbaksql);
									writeLog("������ ����" + sendcomcode + " "
											+ calmonth + " ��ԭ���ܱ�����ݲ��� �����ǲ�ѯsql");
									writeLog(everyRecordSql);
									writeLog(sumLccomperbaksql);
								}

							}
						}
					}

					// end ��ԭ���Ļ��ܱ�У�� ��ȷ��ԭ�����ܱ��ÿ����Ч�ļ�¼
					// String checkSql1 = "select"
					// + " sum(lastgetnum),sum(lastgetmoney),sum(getnum),"
					// + " sum(getmoney),sum(totalmoney),sum(actmoney),calmonth
					// "
					// + " from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney)"
					// + " lastgetmoney,sum(getnum) getnum, sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney, sum(actmoney) actmoney,"
					// + " calmonth "
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' and sendcomcode='"
					// + sendcomcode
					// + "' "
					// + " group by calmonth union all select sum(-lastgetnum)"
					// + " lastgetnum, "
					// + " sum(-lastgetmoney) lastgetmoney, sum(-getnum) getnum,
					// "
					// + " sum(-getmoney) getmoney, sum(-totalmoney) totalmoney,
					// "
					// + " sum(-actmoney) actmoney, calmonth from
					// lccompersoninfo a"
					// + " where a.calmonth = '" + calmonth
					// + "' and sendcomcode = '" + sendcomcode + "' "
					// + "group by calmonth) group by calmonth ";
					// System.out.println(checkSql1);
					// SSRS ssrs2 = new SSRS();
					// if (ssrs2.getMaxRow() != 0) {
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (calmonth.equals("200805")) {
					// if (Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ");
					// }
					// } else {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ");
					// }
					// }
					//
					// }
					// }

					// String checkSql3 = "select nvl(sum(lastgetnum
					// +addnum-reducenum"
					// + " -getnum),0) count ,"
					// + " nvl(sum(lastgetmoney + addmoney
					// -reducemoney-getmoney),0)"
					// + "money,"
					// + " nvl(sum(getmoney +supplymoney -deduckmoney -
					// totalmoney),0) "
					// + " totalmoney,nvl(sum(totalmoney - actmoney),0)
					// actmoney,sendcomcode"
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "'"
					// + " and sendinnercode like '"
					// + sendinnercode
					// + "%' group by sendcomcode"
					// + " having (sum(lastgetnum +addnum -reducenum
					// -getnum)<>0"
					// + " or sum(lastgetmoney + addmoney -reducemoney -"
					// + " getmoney)<>0"
					// + " or sum(getmoney +supplymoney - deduckmoney - "
					// + " totalmoney)<>0 or sum(totalmoney - actmoney)<>0)";
					// String checkSql3 = "select nvl(sum(lastgetnum
					// +addnum-reducenum-getnum),0),nvl(sum(lastgetmoney +
					// addmoney -reducemoney-getmoney),0), "
					// + " nvl(sum(totalmoney - actmoney),0) "
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "'"
					// + " and sendcomcode = '"
					// + sendcomcode + "'";
					// System.out.println("У��a===" + checkSql3);
					// SSRS ssrs5=new SSRS();
					// ssrs5 = exeSQL.execSQL(checkSql3);
					// if (ssrs5.getMaxRow() != 0) {
					// for (int k = 1; k <= ssrs5.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs5.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs5.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs5.GetText(k, 3)) != 0) {
					//
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��a");
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ.У��a");
					// }
					// }
					// }
					//
					// b)����ٴ�У���¾ɱ���getnum��getmoney��lastgetnum��lastgetmoney�Ƿ�һ�£�
					// String checkSql4 = "select "
					// + "
					// nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),"
					// + "
					// nvl(sum(getmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0),retiretype
					// "
					// + " from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney) "
					// + " lastgetmoney,sum(getnum) getnum,sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney,sum(actmoney) "
					// + " actmoney,retiretype"
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' "
					// + " and sendcomcode<>'"
					// + sendcomcode
					// + "' and sendinnercode like '"
					// + sendinnercode
					// + "%' group by retiretype union "
					// + " all select sum(-lastgetnum)
					// lastgetnum,sum(-lastgetmoney) "
					// + " lastgetmoney,"
					// + " sum(-getnum) getnum,sum(-getmoney) "
					// + " getmoney,sum(-totalmoney) totalmoney,sum(-actmoney) "
					// + " actmoney,retiretype from lccompersoninfo a where
					// a.calmonth = "
					// + " '" + calmonth + "' and sendinnercode like '"
					// + sendinnercode + "%' "
					// + " group by retiretype) group by retiretype";
					// System.out.println(checkSql4);
					// ssrs2.Clear();
					// ssrs2 = exeSQL.execSQL(checkSql4);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��b");
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ.У��b");
					// }
					// }
					// // c) У���ĩ������ʵ���Ƿ��������ĩ������֮��
					// String checkSql5 = "select "
					// + "
					// nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),"
					// + "
					// nvl(sum(getmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0),calmonth"
					// + " from (select sum(lastgetnum)
					// lastgetnum,sum(lastgetmoney)"
					// + " lastgetmoney,sum(getnum) getnum,sum(getmoney)
					// getmoney,"
					// + " sum(totalmoney) totalmoney,sum(actmoney)"
					// + " actmoney,calmonth "
					// + " from lccompersoninfo_bak a where a.calmonth = '"
					// + calmonth
					// + "' and sendcomcode = '"
					// + sendcomcode
					// + "' group by calmonth union all select sum(-lastgetnum)"
					// + " lastgetnum, sum(-lastgetmoney) lastgetmoney,
					// sum(-getnum)"
					// + " getnum, sum(-getmoney) getmoney,sum(-totalmoney)
					// totalmoney,"
					// + " sum(-actmoney) actmoney, calmonth from
					// lccompersoninfo_bak a"
					// + " where a.calmonth = '" + calmonth
					// + "' and sendinnercode like '" + sendinnercode
					// + "%' and sendcomcode <> '" + sendcomcode
					// + "' group by calmonth) group by calmonth ";
					// ssrs2.Clear();
					// System.out.println(checkSql5);
					// ssrs2 = exeSQL.execSQL(checkSql5);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ.У��c");
					// // writeLog("����" + sendcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��c");
					// }
					// }
					// // d) У���ĩ���������������Ƿ��������ĩ������֮��
					// String checkSql6 = "select "
					// + "
					// nvl(sum(addnum),0),nvl(sum(addmoney),0),nvl(sum(reducenum),0),"
					// + "
					// nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),calmonth"
					// + " from (select sum(addnum) addnum,sum(addmoney)
					// addmoney,"
					// + " sum(reducenum) reducenum,sum(reducemoney)
					// reducemoney,"
					// + " sum(supplymoney) supplymoney,sum(deduckmoney) "
					// + "deduckmoney,"
					// + " calmonth from lccompersoninfo_bak a where a.calmonth
					// = '"
					// + calmonth
					// + "'"
					// + " and sendcomcode = '"
					// + sendcomcode
					// + "' group by calmonth union all "
					// + " select nvl(sum(-addnum),0)
					// addnum,nvl(sum(-addmoney),0) addmoney,"
					// + " nvl(sum(-reducenum),0)
					// reducenum,nvl(sum(-reducemoney),0) reducemoney,"
					// + " nvl(sum(-supplymoney),0)
					// supplymoney,nvl(sum(-deduckmoney),0) "
					// + " deduckmoney,"
					// + " calmonth from lccompersoninfo_bak a where a.calmonth
					// = '"
					// + calmonth + "' and sendinnercode like '"
					// + sendinnercode + "%' and sendcomcode <> '"
					// + sendcomcode
					// + "'group by calmonth) group by calmonth";
					// ssrs2.Clear();
					// System.out.println(checkSql6);
					// ssrs2 = exeSQL.execSQL(checkSql6);
					// for (int k = 1; k <= ssrs2.getMaxRow(); k++) {
					// if (Double.parseDouble(ssrs2.GetText(k, 1)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 2)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 3)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 4)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 5)) != 0
					// || Double.parseDouble(ssrs2.GetText(k, 6)) != 0) {
					// System.out.println("����" + sendcomcode + " "
					// + calmonth + "�����ݲ���ȷ.У��d");
					// // writeLog("����" + organcomcode + " " + calmonth
					// // + "�����ݲ���ȷ.У��d");
					// }
					// }
				}
			}
			if (errors.size() == 0) {
				System.out.println("���� " + organcomcode + "  û�д���");
			} else {
				System.out.println("���� " + organcomcode + "  ���ִ��� ");
				// for (int j = 0; j < errors.size(); j++) {
				// System.out.println(errors.get(j));
				// writeLog((String) errors.get(j));
				// }
			}
		}
	}

	public static void main(String[] args) {
		DataUpdate dataUpdate = new DataUpdate();
		// dataUpdate.datacheckAfterUpdate();
		// dataUpdate.dataCheck();
		// dataUpdate.dataCheck();
		// dataUpdate.updateLccompersoninfobak();
		// dataUpdate.deleteData();
		//		
		// // dataUpdate.dataUpdate();
		System.out.println("��ʼУ������");
		File path = new File(DataUpdate.class.getResource("/").getFile());
		String folderLogPath = path.getParentFile().getParentFile()
				.getParentFile().toString()
				+ "/logs";
		File folderLog = new File(folderLogPath);
		if (!folderLog.exists()) {
			folderLog.mkdirs();
		}
		String fileLogPath = folderLogPath + "/" + "dataUpdate"
				+ PubFun.getCurrentDate() + ".log";
		File fileLog = new File(fileLogPath); // �ж��ļ��Ƿ���ڣ�û���򴴽�
		if (!fileLog.exists()) {
			try {
				fileLog.createNewFile();
			} catch (IOException e) { // TODO �Զ����� catch ��
				// e.printStackTrace();
			}
		}
		dataUpdate.unseal(fileLogPath); // ��ȫ��LPEdorItem����У��
		dataUpdate.dataUpdate(); // ������ܱ����У�� //
		// dataUpdate.dataCheck();
		// dataUpdate.updateLccompersoninfobak();
		// dataUpdate.datacheckAfterUpdate();
		dataUpdate.colse();
		System.out.println("����");
	}

}
