//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Formula.java

package com.sinosoft.xreport.bl;


public class Formula
{

    private String context;

    public Formula()
    {
        this("");
    }

    public Formula(String context)
    {
        this.context = context;
    }

    public boolean equals(Formula other)
    {
        return equals(other.context);

    }

    /**
     * �ж������ַ���������Ƿ����
     * e.g. "A=a and B=b or not (C<c and D>d)"
     *  <=> "not (C<c and D>d) or (B=b and A=a)"
     *
     * @param context
     * @return
     */
    public boolean equals(String context)
    {
        if (this.context.equals(context))
        {
            return true;
        }
        else
        {
            return false; //must reimplement
        }

    }


}