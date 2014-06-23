package com.sinosoft.utility;

import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Picch_UWGrade
{

    public Picch_UWGrade()
    {
    }

    public String getUWGrade(double tAmnt, String tContflag)
    {
        String tResult = "";
        try
        {

            LDUWGrade_PicchDB tLDUWGrade_PicchDB = new LDUWGrade_PicchDB();
            String tsql = "select * from LDUWGrade_Picch where Amnt>=" + tAmnt
                          + " and Contflag='" + tContflag + "' order by Amnt";
            LDUWGrade_PicchSet tLDUWGrade_PicchSet = tLDUWGrade_PicchDB.
                    executeQuery(
                            tsql);

            if (tLDUWGrade_PicchSet.get(1) != null)
            {
                tResult = tLDUWGrade_PicchSet.get(1).getUWGrade();
            }
            else
            {
                tResult = "08";
            }
            System.out.println("Result=" + tResult);
        }
        catch (Exception ex)
        {
        }
        return tResult;
    }

    public static void main(String[] args)
    {
        Picch_UWGrade tPicch_UWGrade = new Picch_UWGrade();
        tPicch_UWGrade.getUWGrade(40000000, "1");

    }
}
