/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.*;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ϵͳ�������ӿ�</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Liuqiang
 * @version 1.0
 */

public interface SysMaxNo
{

    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType String Ϊ��Ҫ���ɺ��������
     * @param cNoLength String Ϊ��Ҫ���ɺ������������
     * @param cVData VData ҵ���������
     * @return String ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    String CreateMaxNo(String cNoType, String cNoLength, VData cVData);

    /**
     * ���ܣ�����ָ�����ȵ���ˮ�ţ�һ����������һ����ˮ
     * @param cNoType String ��ˮ�ŵ�����
     * @param cNoLength int ��ˮ�ŵĳ���
     * @return String ���ز�������ˮ����
     */
    String CreateMaxNo(String cNoType, int cNoLength);

    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType String Ϊ��Ҫ���ɺ��������
     * @param cNoLimit String Ϊ��Ҫ���ɺ������������
     * @return String ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    String CreateMaxNo(String cNoType, String cNoLimit);

}
