/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

/**
 * <p>
 * Title: Webҵ��ϵͳ
 * </p>
 * <p>
 * Description:ϵͳ��������������ٺ���ҵ��ϵͳ������ϵͳ����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sinosoft
 * </p>
 * 
 * @author Fanym
 * @version 1.0
 */

import java.sql.Connection;
import java.math.BigInteger;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;
import com.sinosoft.utility.treetable.DBUtil;
import com.sinosoft.utility.TransferData;

public class SysMaxNoZhongYi implements com.sinosoft.lis.pubfun.SysMaxNo {

	public SysMaxNoZhongYi() {
	}

	/**
	 * <p>
	 * ������ˮ�ŵĺ���
	 * <p>
	 * <p>
	 * ������򣺻������� ������ У��λ ���� ��ˮ��
	 * <p>
	 * <p>
	 * 1-6 7-10 11 12-13 14-20
	 * <p>
	 * 
	 * @param cNoType
	 *            Ϊ��Ҫ���ɺ��������
	 * @param cNoLimit
	 *            Ϊ��Ҫ���ɺ��������������Ҫô��SN��Ҫô�ǻ������룩
	 * @param cVData
	 *            Ϊ��Ҫ���ɺ����ҵ�������������
	 * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
	 *         ����ú͵ĵ�֤���ͣ���Ҫһ��֤һ�汾��ˮ�Ĺ���LDMaxNo�е��ֶκ������£� NoType:��֤���� NoLimit:��֤�汾
	 */
	public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData) {
		// ��֤�������λ��

		int serialLen = 16;
		String tReturn = null;
		if (cNoType == null || cNoType.equalsIgnoreCase("")) {
			return null;
		}
		// ����ʧ��֪ͨ��
		else if (cNoType.equalsIgnoreCase("TRANSFER")) {
			tReturn = "1019";
		}
		// �ͻ������֪ͨ��
		else if (cNoType.equalsIgnoreCase("CUSISSUE")) {
			tReturn = "1022";
		}
		// �˱�֪ͨ��
		else if (cNoType.equalsIgnoreCase("HEBAOHAN")) {
			tReturn = "1023";
		}
		// ���֪ͨ��
		else if (cNoType.equalsIgnoreCase("TIJIAN")) {
			tReturn = "1028";
		}
		// ����֪ͨ��
		else if (cNoType.equalsIgnoreCase("QIDIAO")) {
			tReturn = "1045";
		}
		// ����֪ͨ��
		else if (cNoType.equalsIgnoreCase("BUFEI")) {
			tReturn = "1048";
		}
		// ҵ��Ա�����
		else if (cNoType.equalsIgnoreCase("AGENTISSUE")) {
			tReturn = "1066";
		}
		// ����������
		else if (cNoType.equalsIgnoreCase("SPECISSUE")) {
			tReturn = "1067";
		}
		// ���屣����
		else if (cNoType.equalsIgnoreCase("GRPCONTNO")) {
			String grpcontno = (String) ((TransferData) cVData
					.getObjectByObjectName("TransferData", 0))
					.getValueByName("GRPCONTNO");
			System.out.println("++++++++" + grpcontno);
			if (grpcontno != null) {
				BigInteger grp = new BigInteger(grpcontno);
				BigInteger t = BigInteger.valueOf(1);
				grp = grp.add(t);
				System.out.println("tttttt " + grp);
				grpcontno = grp + "";
				return grpcontno;
			}
		}
		// �����ְ����
		else if (cNoType.equalsIgnoreCase("CASENO")) {
			TransferData tTransferData = new TransferData();
			tTransferData = (TransferData) cVData.getObjectByObjectName(
					"TransferData", 0);
			String tNoLimit = (String) tTransferData.getValueByName("RGTNO");

			// String prgino = (String)((TransferData)
			// cVData.getObjectByObjectName("TransferData",
			// 0)).getValueByName("RGTNO");
			System.out.println("+++++++prgino= " + tNoLimit);
			if (tNoLimit != null) {
				tReturn = tNoLimit;

				Connection conn = DBConnPool.getConnection();
				if (conn == null) {
					System.out
							.println("CreateMaxNo : fail to get db connection");
					return null;
				}
				// int tMaxNo = 0;
				BigInteger tMaxNo = new BigInteger("0");
				try {
					// ��ʼ����
					// ��ѯ�����3���� -- added by Fanym
					// ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
					// ���û����������������������ѯ�õ������UPDATE��û����INSERT
					conn.setAutoCommit(false);

					String strSQL = "select MaxNo from LDZhongYiMaxNo where notype='"
							+ cNoType
							+ "' and nolimit='"
							+ tNoLimit
							+ "' for update";
					ExeSQL exeSQL = new ExeSQL(conn);
					String result = null;
					result = exeSQL.getOneValue(strSQL);

					// ���Է��ش���
					if (exeSQL.mErrors.needDealError()) {
						System.out.println("��ѯLDZhongYiMaxNo�������Ժ�!");
						conn.rollback();
						conn.close();
						return null;
					}

					if ((result == null) || result.equals("")) {
						strSQL = "insert into ldzhongyimaxno(notype, nolimit, maxno) values('"
								+ cNoType + "', '" + tNoLimit + "', 1)";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = 1;
							tMaxNo = new BigInteger("1");
						}
					} else {
						strSQL = "update ldzhongyimaxno set maxno = maxno + 1 where notype = '"
								+ cNoType
								+ "' and nolimit = '"
								+ tNoLimit
								+ "'";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = Integer.parseInt(result) + 1;
							tMaxNo = new BigInteger(result).add(new BigInteger(
									"1"));
						}
					}
					conn.commit();
					conn.close();
				} catch (Exception Ex) {
					try {
						conn.rollback();
						conn.close();
						return null;
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				}
				// ����Ϊ3λ��������0
				String s = PubFun.LCh(tMaxNo.toString(), "0", 3);
				tReturn = tReturn + s; // �ܰ���+3λ�ְ���
				return tReturn;
			}

		}
		// ������
		else if (cNoType.equalsIgnoreCase("SURVEYNO")) {
			TransferData tTransferData = new TransferData();
			tTransferData = (TransferData) cVData.getObjectByObjectName(
					"TransferData", 0);
			String tNoLimit = (String) tTransferData.getValueByName("SURVEYNO");

			// String prgino = (String)((TransferData)
			// cVData.getObjectByObjectName("TransferData",
			// 0)).getValueByName("RGTNO");
			System.out.println("+++++++prgino= " + tNoLimit);
			if (tNoLimit != null) {
				tReturn = tNoLimit;

				Connection conn = DBConnPool.getConnection();
				if (conn == null) {
					System.out
							.println("CreateMaxNo : fail to get db connection");
					return null;
				}
				// int tMaxNo = 0;
				BigInteger tMaxNo = new BigInteger("0");
				try {
					// ��ʼ����
					// ��ѯ�����3���� -- added by Fanym
					// ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
					// ���û����������������������ѯ�õ������UPDATE��û����INSERT
					conn.setAutoCommit(false);

					String strSQL = "select MaxNo from LDZhongYiMaxNo where notype='"
							+ cNoType
							+ "' and nolimit='"
							+ tNoLimit
							+ "' for update";
					ExeSQL exeSQL = new ExeSQL(conn);
					String result = null;
					result = exeSQL.getOneValue(strSQL);

					// ���Է��ش���
					if (exeSQL.mErrors.needDealError()) {
						System.out.println("��ѯLDZhongYiMaxNo�������Ժ�!");
						conn.rollback();
						conn.close();
						return null;
					}

					if ((result == null) || result.equals("")) {
						strSQL = "insert into ldzhongyimaxno(notype, nolimit, maxno) values('"
								+ cNoType + "', '" + tNoLimit + "', 1)";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = 1;
							tMaxNo = new BigInteger("1");
						}
					} else {
						strSQL = "update ldzhongyimaxno set maxno = maxno + 1 where notype = '"
								+ cNoType
								+ "' and nolimit = '"
								+ tNoLimit
								+ "'";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = Integer.parseInt(result) + 1;
							tMaxNo = new BigInteger(result).add(new BigInteger(
									"1"));
						}
					}
					conn.commit();
					conn.close();
				} catch (Exception Ex) {
					try {
						conn.rollback();
						conn.close();
						return null;
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				}
				// ����Ϊ2λ��������0
				String s = PubFun.LCh(tMaxNo.toString(), "0", 2);
				tReturn = tReturn + s; // �ܰ���+3λ�ְ���
				return tReturn;
			}

		} else {
			return null;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}
		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");
		try {
			// ��ʼ����
			// ��ѯ�����3���� -- added by Fanym
			// ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
			// ���û����������������������ѯ�õ������UPDATE��û����INSERT
			conn.setAutoCommit(false);
			// ��ѯ��֤��������ȡ�õ�֤������Ч�����汾��
			String strSQL = "select max(certifycode) from lmcertifydes where state = '0' and Certifycode like '"
					+ tReturn + "%'";
			ExeSQL exeSQL = new ExeSQL(conn);
			String tVersion = exeSQL.getOneValue(strSQL).substring(4, 6);

			// ����汾�Ų����ڣ���֤��û�д˵�֤��������
			if (tVersion == null || tVersion.equals("")) {
				System.out.println("û�и�" + cNoType + "��֤����������");
				conn.rollback();
				conn.close();
				return null;
			}
			// �Ѱ汾��Ϊ��������������Ϊ2λ��������0
			cNoLimit = PubFun.LCh(tVersion, "0", 2);
			strSQL = "select MaxNo from LDMaxNo where notype='" + cNoType
					+ "' and nolimit='" + cNoLimit + "' for update";
			exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			// ���Է��ش���
			if (exeSQL.mErrors.needDealError()) {
				System.out.println("��ѯLDMaxNo�������Ժ�!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || result.equals("")) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = 1;
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));
				}
			}
			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();
				return null;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		// ��ȡ��ˮ�ţ���λ��-��֤���ͳ���-�汾λ��-�����λ88
		String tStr = PubFun.LCh(tMaxNo.toString(), "0", serialLen
				- tReturn.length() - cNoLimit.length() - 2);

		tReturn = tReturn + cNoLimit + tStr + "88";
		// ���ص�֤��ˮ��
		return tReturn;
	}

	/**
	 * <p>
	 * ������ˮ�ŵĺ���
	 * <p>
	 * <p>
	 * ������򣺻������� ������ У��λ ���� ��ˮ��
	 * <p>
	 * <p>
	 * 1-6 7-10 11 12-13 14-20
	 * <p>
	 * 
	 * @param cNoType
	 *            Ϊ��Ҫ���ɺ��������
	 * @param cNoLimit
	 *            Ϊ��Ҫ���ɺ��������������Ҫô��SN��Ҫô�ǻ������룩
	 * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
	 */
	public String CreateMaxNo(String cNoType, String cNoLimit) {

		String copyNolMit = ""; // ���� ��������ʱ���cNoLimit
		// ����Ĳ�������Ϊ�գ����Ϊ�գ���ֱ�ӷ���
		if ((cNoType == null) || (cNoType.trim().length() <= 0)
				|| (cNoLimit == null)) {
			System.out.println("NoType���ȴ������NoLimitΪ��");
			return null;
		}

		int serialLen = 16; // ���Ǻ����ܳ�����16λ
		String tReturn = null; // �����ַ���
		cNoType = cNoType.toUpperCase(); // ��ˮ����
		// String tBit = "0"; //У��λ

		// cNoLimit�����SN���ͣ���cNoTypeֻ�������������е�һ��
		if (cNoLimit.trim().equalsIgnoreCase("SN")) {
			// modify by yt 2002-11-04
			if (cNoType.equalsIgnoreCase("CUSTOMERNO")
					|| cNoType.equalsIgnoreCase("GRPNO")
					|| cNoType.equalsIgnoreCase("VCUSTOMERNO")) {
				serialLen = 12; // ����Ĭ�Ͽͻ�����12λ
			} else if (cNoType.equalsIgnoreCase("COMMISIONSN")) {
				serialLen = 20; // ���ʱ���ˮ��Ĭ����20λ
			} 
			//liuwd 20070927
			else if (cNoType.equalsIgnoreCase("EDITUSER")) {
				serialLen = 10; // 
			} 
			
			else {
				System.out.println("�����NoLimit");
				return null;
			}
		}
		//added by zhangjq for ����ӱ���
		else if (cNoType.toUpperCase().startsWith("GRPCONTNOSUB")) {

		}
		else if (!cNoType.equalsIgnoreCase("ORGCOMCODE")
				&& !cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			copyNolMit = cNoLimit;
			cNoLimit = "SYS";

		}

		// ���������������ڲ�����,�����cNoLimit����Ϊ�ϼ��������ڲ�����
		if (cNoType.equalsIgnoreCase("ORGCOMCODE")
				|| cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			serialLen = 4;
		}

		// ���˿ͻ���
		if (cNoType.equalsIgnoreCase("CUSTOMERNO")) {
			tReturn = "1"; // 1��9λ��ˮ��88
			serialLen = 12;
		}
		// ����ͻ���
		else if (cNoType.equalsIgnoreCase("GRPNO")) {
			serialLen = 12;
			tReturn = "9"; // 9��9λ��ˮ��88
		}
		// ����ͻ����������������ʻ�
		else if (cNoType.equalsIgnoreCase("VCUSTOMERNO")) {
			tReturn = "0";
		}
		// ����Ͷ����
		else if (cNoType.equalsIgnoreCase("PROPOSALNO")) {
			tReturn = "9010";
		}
		// �����¸���Ͷ����
		else if (cNoType.equalsIgnoreCase("GPROPOSALNO")) {
			tReturn = "9011";
		}
		// ����Ͷ����
		else if (cNoType.equalsIgnoreCase("GRPPROPOSALNO")) {
			tReturn = "9012";
		}
		// �ܵ�Ͷ�������룬ϵͳ����
		else if (cNoType.equalsIgnoreCase("PROPOSALCONTNO")) {
			tReturn = "9015";
		}
		// �����º�ͬͶ��������
		else if (cNoType.equalsIgnoreCase("GPROPOSALCONTNO")) {
			tReturn = "9016";
		}
		// ����Ͷ��������
		else if (cNoType.equalsIgnoreCase("PROGRPCONTNO")) {
			if (copyNolMit.length() != 8) {
				return "�����������,����Ӧ��Ϊ8λ";
			}
			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			if (o != null) {
				tReturn = o.toString(); // 4λbranch code+5λ��ˮ��
				serialLen = 9;
			} else {
				return null;
			}
		}
		// �������ֺ���
		else if (cNoType.equalsIgnoreCase("POLNO")) {
			tReturn = "9020";
		}
		// �����±������ֺ���
		else if (cNoType.equalsIgnoreCase("GPOLNO")) {
			tReturn = "9021";
		}
		// ���屣�����ֺ���
		else if (cNoType.equalsIgnoreCase("GRPPOLNO")) {
			tReturn = "9022";
		}
		// ���˱�������
		else if (cNoType.equalsIgnoreCase("CONTNO")) {
			tReturn = "9025";
		}
		// �����¸��˱�������
		else if (cNoType.equalsIgnoreCase("GCONTNO")) {
			serialLen = 12;
			tReturn = "6"; // 6��9λ��ˮ��88
		}
		// ���屣����
		else if (cNoType.equalsIgnoreCase("GRPCONTNO")) {
			if (copyNolMit.length() != 8) {
				return "�����������,����Ӧ��Ϊ8λ";
			}

			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			if (o != null) {
				tReturn = "8" + o.toString().substring(1, 3); // 8��2λbranchcode+6λ��ˮ�ţ�2λ��������
				serialLen = 11;
			} else {
				return null;
			}
		}
		// ��ͨ�����ձ�����
		else if (cNoType.equalsIgnoreCase("AIRPOLNO")) {
			tReturn = "9029";
		}
		// �˱�֪ͨ���
		// else if (cNoType.equalsIgnoreCase("UWNOTICENO"))
		// {
		// tReturn = "102301";
		// }
		// ����֪ͨ�����
		else if (cNoType.equalsIgnoreCase("PAYNOTICENO")) {
			tReturn = "102101";
		}
		// �����վݺ���
		else if (cNoType.equalsIgnoreCase("PAYNO")) {
			tReturn = "9032";
		}
		// ����֪ͨ�����
		else if (cNoType.equalsIgnoreCase("GETNOTICENO")) {
			tReturn = "9036";
		}
		// ʵ������
		else if (cNoType.equalsIgnoreCase("GETNO")) {
			tReturn = "9038";
		}
		// �����������
		else if (cNoType.equalsIgnoreCase("EDORAPPNO")) {
			tReturn = "9041";
		}
		// ��������
		else if (cNoType.equalsIgnoreCase("EDORNO")) {
			tReturn = "9046";
		}
		// ���������������
		else if (cNoType.equalsIgnoreCase("EDORGRPAPPNO")) {
			tReturn = "9043";
		}
		// ������������
		else if (cNoType.equalsIgnoreCase("EDORGRPNO")) {
			serialLen = 12;
			tReturn = "5"; // 5��9λ��ˮ��88
		}
		// ��ȫ�������
		else if (cNoType.equalsIgnoreCase("EDORACCEPTNO")) {
			tReturn = "9048";
		}
		// �������
		else if (cNoType.equalsIgnoreCase("RPTNO")) {
			tReturn = "9050";
		}
		// �������
		else if (cNoType.equalsIgnoreCase("RGTNO")) {
			if (copyNolMit.length() != 8) {
				return "�����������,����Ӧ��Ϊ8λ";
			}

			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			serialLen = 12;
			String date = PubFun.getCurrentDate();
			if (o != null) {
				tReturn = date.substring(0, 4) + o.toString().substring(1, 3); // ��λ�꣫2λbranch
				// code��6λ��ˮ
				// //
			} else {
				return null;
			}

		}
		// �ⰸ���
		else if (cNoType.equalsIgnoreCase("CLMNO")) {
			tReturn = "9052";
		}
		// �ܰ����
		else if (cNoType.equalsIgnoreCase("DECLINENO")) {
			tReturn = "9053";
		}
		// �����ְ����
		else if (cNoType.equalsIgnoreCase("SUBRPTNO")) {
			tReturn = "9054";
		}
		// �����ְ����
		// else if (cNoType.equalsIgnoreCase("CASENO"))
		// {
		// serialLen = 15;
		// //////////////////�ܰ��ţ�3λ�ְ���
		// tReturn = "9055";
		// }
		// �¼����
		else if (cNoType.equalsIgnoreCase("CASERELANO")) {
			tReturn = "9056";
		}
		// ��ӡ������ˮ��
		else if (cNoType.equalsIgnoreCase("PRTSEQNO")) {
			tReturn = "9066";
		}
		// ��ӡ������ˮ��
		else if (cNoType.equalsIgnoreCase("PRTSEQ2NO")) {
			tReturn = "9068";
		}
		// �������㵥��
		else if (cNoType.equalsIgnoreCase("TAKEBACKNO")) {
			tReturn = "9061";
		}
		// ���д��۴������κ�
		else if (cNoType.equalsIgnoreCase("BATCHNO")) {
			tReturn = "9063";
		}
		// ���ʱ���ˮ��
		else if (cNoType.equalsIgnoreCase("COMMISIONSN")) {
			tReturn = "9071";
		}
		// �н�Э�������
		else if (cNoType.equalsIgnoreCase("PROTOCOLNO")) {
			serialLen = 15;
			tReturn = "907";
		}
		// ���������
		else if (cNoType.equalsIgnoreCase("SUGCODE")) {
			tReturn = "9088";
		}
		// ������ģ�����
		else if (cNoType.equalsIgnoreCase("SUGMODELCODE")) {
			tReturn = "9082";
		}
		// ��������Ŀ����
		else if (cNoType.equalsIgnoreCase("SUGITEMCODE")) {
			tReturn = "9083";
		}
		// ������������Ŀ����
		else if (cNoType.equalsIgnoreCase("SUGDATAITEMCODE")) {
			tReturn = "9085";
		}
		// ��ˮ��
		else if (cNoType.equalsIgnoreCase("SERIALNO")) {
			tReturn = "9090";
		}
		// ������Ҫ��ˮ��
		else if (cNoType.equalsIgnoreCase("FINNO")) {
			tReturn = "212401";
		}
		//ά���û���ˮ��
		else if(cNoType.equalsIgnoreCase("EDITUSER")){
			tReturn="5700";
		}
//		����ӱ��������,zhangjq 2007-11-27
		else if(cNoType.equalsIgnoreCase("GrpContNoSub")){
			tReturn="8800";
		}
		//����ӱ������������,zhangjq 2007-11-27
		else if(cNoType.equalsIgnoreCase("GrpContNoSubBk")){
			tReturn="8700";
		}
		// ����
		else {
			tReturn = "";
			// return null;
		}

		// ���������������ڲ�����,�����cNoLimit����Ϊ�ϼ��������ڲ�����
		if (cNoType.equalsIgnoreCase("ORGCOMCODE")
				|| cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			tReturn = cNoLimit;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}

		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");

		try {
			// ��ʼ����
			// ��ѯ�����3���� -- added by Fanym
			// ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
			// ���û����������������������ѯ�õ������UPDATE��û����INSERT
			conn.setAutoCommit(false);

			String strSQL = "select MaxNo from LDMaxNo where notype='"
					+ cNoType + "' and nolimit='" + cNoLimit + "' for update";
			ExeSQL exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			// ���Է���nullʱ
			if (exeSQL.mErrors.needDealError()) {
				System.out.println("��ѯLDMaxNo�������Ժ�!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || result.equals("")) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();

					return null;
				} else {
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();

					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));

				}
			}
			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();

				return null;
			} catch (Exception e1) {
				e1.printStackTrace();

				return null;
			}
		}

		// String tStr = String.valueOf(tMaxNo);
		String tStr = tMaxNo.toString();
		if (!cNoType.equalsIgnoreCase("ORGCOMCODE")
				&& !cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			if (cNoType.equalsIgnoreCase("GRPCONTNO")) { // 8��2λbranch
				// code+6λ��ˮ�ţ�2λ��������
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2);
				System.out.println("tStr = " + tStr);
				tReturn = tReturn + tStr + "00";
				System.out.println("tReturn 1 = " + tReturn);
			} else if (cNoType.equalsIgnoreCase("GPROPOSALCONTNO")) { // 4λbranch
				// code+5λ��ˮ��
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length());
				tReturn = tReturn + tStr;
				System.out.println("tReturn 2 = " + tReturn);
			} else if (cNoType.equalsIgnoreCase("RGTNO")) { // ��λ�꣫2λbranch
				// code��6λ��ˮ�� //
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length());
				System.out.println("tStr= " + tStr);
				tReturn = tReturn + tStr;
				System.out.println("tReturn 3 = " + tReturn);
			} else if(cNoType.equalsIgnoreCase("ActuGetNo")){
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 6) ;
				tReturn = tReturn + tStr;
			}
//liuwd 20070927
			else if(cNoType.equalsIgnoreCase("EDITUSER")){
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 6) ;
				tReturn = tReturn + tStr;
			}
//			����ӱ��������,zhangjq 2007-11-27
			else if(cNoType.equalsIgnoreCase("GrpContNoSub")){
				tStr = PubFun.LCh(tStr, "0", 4) ;
				tReturn = tReturn + tStr;
			}
			//����ӱ������������,zhangjq 2007-11-27
			else if(cNoType.equalsIgnoreCase("GrpContNoSubBk")){
				tStr = PubFun.LCh(tStr, "0", 4) ;
				tReturn = tReturn + tStr;
			}
			else { // ��Ҫ����λ��88 
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2);
				tReturn = tReturn + tStr + "88";
				System.out.println("tReturn4 =  " + tReturn);
			}
		} else {
			tStr = PubFun.LCh(tStr, "0", serialLen);
			tReturn = tReturn + tStr;
		}

		return tReturn;
	}

	/**
	 * ���ܣ�����ָ�����ȵ���ˮ�ţ�һ����������һ����ˮ
	 * 
	 * @param cNoType
	 *            String ��ˮ�ŵ�����
	 * @param cNoLength
	 *            int ��ˮ�ŵĳ���
	 * @return String ���ز�������ˮ����
	 */
	public String CreateMaxNo(String cNoType, int cNoLength) {
		if ((cNoType == null) || (cNoType.trim().length() <= 0)
				|| (cNoLength <= 0)) {
			System.out.println("NoType���ȴ����NoLength����");
			return null;
		}

		cNoType = cNoType.toUpperCase();
		String tReturn = "";
		String cNoLimit = "SN";
		// ����������cNoLimitΪSN��������һ��У�飬����ᵼ�����ݸ���
		if (cNoType.equalsIgnoreCase("COMMISIONSN")
				|| cNoType.equalsIgnoreCase("GRPNO")
				|| cNoType.equalsIgnoreCase("CUSTOMERNO")
				|| cNoType.equalsIgnoreCase("SUGDATAITEMCODE")
				|| cNoType.equalsIgnoreCase("SUGITEMCODE")
				|| cNoType.equalsIgnoreCase("SUGMODELCODE")
				|| cNoType.equalsIgnoreCase("SUGCODE")) {
			System.out.println("��������ˮ�ţ������CreateMaxNo('" + cNoType
					+ "','SN')��ʽ����");
			return null;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}

		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");
		tReturn = cNoLimit;

		try {
			// ��ʼ����
			conn.setAutoCommit(false);

			String strSQL = "select MaxNo from LDMaxNo where notype='"
					+ cNoType + "' and nolimit='" + cNoLimit + "' for update";

			ExeSQL exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			if (exeSQL.mErrors.needDealError()) {
				System.out.println("CreateMaxNo ��ѯʧ�ܣ����Ժ�!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || (result.equals(""))) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = 1;
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));
				}
			}

			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();
				return null;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}

		// String tStr = String.valueOf(tMaxNo);
		String tStr = tMaxNo.toString();
		tStr = PubFun.LCh(tStr, "0", cNoLength);
		tReturn = tStr;

		return tReturn;
	}

	public static void main(String[] args) {
		/*
		 * System.out.println(PubFun1.CreateMaxNo("ORGCOMCODE","86"));
		 * System.out.println(PubFun1.CreateMaxNo("GRPCONTNO", "86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("GPROPOSALCONTNO","86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("RGTNO", "86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("EDORGRPNO", "86"));
		 * System.out.println(PubFun1.CreateMaxNo("GCONTNO","86"));
		 * System.out.println(PubFun1.CreateMaxNo("GRPNO", "86"));
		 * System.out.println(PubFun1.CreateMaxNo("PROGRPCONTNO","86010101"));
		 * String a = "8611"; System.out.println(a.substring(0, 4));
		 * System.out.println(PubFun1.CreateMaxNo("CUSTOMERNO", "86"));
		 * 
		 * 
		 * VData tVData = new VData(); TransferData tTransferData =new
		 * TransferData(); tTransferData.setNameAndValue("RGTNO",
		 * "200630000003");
		 * tTransferData.setNameAndValue("GRPCONTNO","83012345700" );
		 * tVData.addElement(tTransferData);
		 * 
		 * System.out.println("++++++++GRTCONTNO=
		 * "+PubFun1.CreateMaxNo("GRPCONTNO", "86",tVData));
		 * System.out.println("+++++++++++CASENO=
		 * "+PubFun1.CreateMaxNo("CASENO", "86",tVData));
		 */

	}
}
