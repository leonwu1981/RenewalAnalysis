/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Vector;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */
public class SSRS
{
    private Vector RData = new Vector();
    public int MaxCol = 0;
    public int MaxRow = 0;
    public int MaxNumber = 0;
    public CErrors mErrors = new CErrors(); // 错误信息

    //false没有错，true有错
    public boolean ErrorFlag = false;

    public SSRS()
    {
    }

    public SSRS(int n)
    {
        MaxCol = n;
    }

    protected void SetText(String strValue)
    {
        RData.addElement(strValue);
        MaxNumber = RData.size();
        if ((MaxNumber % MaxCol) == 0)
        {
            MaxRow = MaxNumber / MaxCol;
        }
        else
        {
            MaxRow = MaxNumber / MaxCol + 1;
        }

    }

    public String GetText(int cRow, int cCol)
    {
        String Result = "";
        int Number = (cRow - 1) * MaxCol + cCol - 1;
        if (Number <= MaxNumber)
        {
            Result = (String) RData.get(Number);
        }
        else
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SSRS";
            tError.functionName = "GetText";
            tError.errorMessage = "指定的位置在结果集中没有数据";
            this.mErrors.addOneError(tError);
            this.ErrorFlag = true;
        }
        return Result;
    }

    public int getMaxCol()
    {
        return MaxCol;
    }

    public int getMaxRow()
    {
        return MaxRow;
    }

    public int getMaxNumber()
    {
        return MaxNumber;
    }

    public void setMaxCol(int aMaxCol)
    {
        MaxCol = aMaxCol;
    }

    public void setMaxRow(int aMaxRow)
    {
        MaxRow = aMaxRow;
    }

    public void setMaxNumber(int aMaxNumber)
    {
        MaxNumber = aMaxNumber;
    }

    public void Clear()
    {
        RData.clear();
        MaxRow = 0;
        MaxCol = 0;
        MaxNumber = 0;
    }

    public String[] getRowData(int cRow)
    {
        String StrArr[] = new String[MaxCol];
        for (int i = 0; i < MaxCol; i++)
        {
            StrArr[i] = this.GetText(cRow, i + 1);
        }
        return StrArr;
    }

    public String[][] getAllData()
    {
        String StrArr[][] = new String[MaxRow][MaxCol];
        for (int j = 0; j < MaxRow; j++)
        {
            StrArr[j] = this.getRowData(j + 1);
        }
        return StrArr;
    }

    public String encode()
    {
        String strReturn = "";
        if (MaxNumber != 0)
        {
            strReturn = "0" + SysConst.PACKAGESPILTER + String.valueOf(MaxRow) +
                        SysConst.RECORDSPLITER;
            for (int i = 1; i <= MaxRow; i++)
            {
                for (int j = 1; j <= MaxCol; j++)
                {
                    if (j != MaxCol)
                    {
                        strReturn = strReturn + GetText(i, j) +
                                    SysConst.PACKAGESPILTER;
                    }
                    else
                    {
                        strReturn += GetText(i, j);
                    }
                }
                if (i != MaxRow)
                {
                    strReturn += SysConst.RECORDSPLITER;
                }
            }
        }
        return strReturn;
    }

    public boolean addCol(SSRS aSSRS)
    {
        if (!(this.MaxRow == aSSRS.getMaxRow()))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SSRS";
            tError.functionName = "addCol";
            tError.errorMessage = "合并的两个结果集行数不等！！！";
            this.mErrors.addOneError(tError);
            return false;
        }
        else
        {
            SSRS bSSRS = null;
            try
            {
                bSSRS = (SSRS)this.clone();
            }
            catch (Exception ex)
            {
                // @@错误处理
                ex.printStackTrace();
                CError tError = new CError();
                tError.moduleName = "SSRS";
                tError.functionName = "addCol";
                tError.errorMessage = "克隆出错！！！";
                this.mErrors.addOneError(tError);
                return false;
            }
            this.Clear();
            this.setMaxCol(bSSRS.getMaxCol() + aSSRS.getMaxCol());
//    this.setMaxRow(bSSRS.getMaxRow());
//    this.setMaxNumber(bSSRS.getMaxNumber()+aSSRS.getMaxNumber());
            for (int row = 1; row <= aSSRS.getMaxRow(); row++)
            {
                for (int bcol = 1; bcol <= bSSRS.getMaxCol(); bcol++)
                {
                    this.SetText(StrTool.cTrim(bSSRS.GetText(row, bcol)));
                }
                for (int acol = 1; acol <= aSSRS.getMaxCol(); acol++)
                {
                    this.SetText(StrTool.cTrim(aSSRS.GetText(row, acol)));
                }
            }

            return true;
        }
    }

    public boolean addRow(SSRS aSSRS)
    {
        if (this.MaxCol == aSSRS.getMaxCol())
        {
            SSRS bSSRS = null;
            try
            {
                bSSRS = (SSRS)this.clone();
            }
            catch (Exception ex)
            {
                // @@错误处理
                ex.printStackTrace();
                CError tError = new CError();
                tError.moduleName = "SSRS";
                tError.functionName = "addRow";
                tError.errorMessage = "克隆出错！！！";
                this.mErrors.addOneError(tError);
                return false;
            }
            this.Clear();
            this.setMaxCol(bSSRS.getMaxCol());

            for (int row = 1; row <= bSSRS.getMaxRow(); row++)
            {
                for (int col = 1; col <= bSSRS.getMaxCol(); col++)
                {
                    this.SetText(StrTool.cTrim(bSSRS.GetText(row, col)));
                }
            }
            for (int row = 1; row <= aSSRS.getMaxRow(); row++)
            {
                for (int col = 1; col <= aSSRS.getMaxCol(); col++)
                {
                    this.SetText(StrTool.cTrim(aSSRS.GetText(row, col)));
                }
            }
            return true;
        }
        else
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SSRS";
            tError.functionName = "addRow";
            tError.errorMessage = "合并的两个结果集列数不等！！！";
            this.mErrors.addOneError(tError);
            return false;
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        Object tObj = null;
        SSRS tSSRS = new SSRS();
        tSSRS.setMaxCol(this.getMaxCol());
        tSSRS.setMaxNumber(this.getMaxNumber());
        tSSRS.setMaxRow(this.getMaxRow());
        tSSRS.ErrorFlag = this.ErrorFlag;
        tSSRS.mErrors = this.mErrors;
        tSSRS.RData = (Vector)this.RData.clone();
        tObj = tSSRS;
        return tObj;
    }

    public static void main(String[] args)
    {
    }
}
