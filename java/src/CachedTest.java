import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.vschema.LMCalFactorSet; 
/**

 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: SINOSOFT</p>
 *
 * @author �����
 * @version 1.0
 */
public class CachedTest
{
    public CachedTest()
    {
    }

    public static void main(String[] args)
    {
        CachedRiskInfo mCRI = CachedRiskInfo.getInstance();
        String CalCode = "601300";

        //ȡ�����ݿ��м���Ҫ��
        LMCalFactorSet tLMCalFactorSet1 = mCRI.findCalFactorByCalCodeClone(CalCode);

        LMCalFactorSet tLMCalFactorSet2 = mCRI.findCalFactorByCalCodeClone(CalCode);

        tLMCalFactorSet2.get(1).setCalCode("1");
        System.out.println(tLMCalFactorSet1.get(1).getCalCode());
        System.out.println(tLMCalFactorSet2.get(1).getCalCode());


    }
}
