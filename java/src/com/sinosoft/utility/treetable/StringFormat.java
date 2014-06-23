/**
 * 作者：王育春<br>
 * 日期：2006-7-19<br>
 * 邮件：wangyc@sinosoft.com.cn<br>
 */
package com.sinosoft.utility.treetable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormat {
    private String formatStr;

    private ArrayList arr;

    private ArrayList indexs;

    private String[] params;

    private boolean[] flags;

    private int iAddedCount = 0;

    private static final String regex = "\\{[P|\\d]*?\\}";

    private static Pattern pattern = null;

    private boolean indexFlag = false;

    public StringFormat(String str) {
        this.formatStr = str;
        parseFormat();
    }

    private void parseFormat() {
        if (pattern == null) {
            pattern = Pattern.compile(StringFormat.regex);
        }
        Matcher m = pattern.matcher(formatStr);
        int lastEndIndex = 0;
        arr = new ArrayList();
        indexs = new ArrayList();
        int iFlag = -1;
        while (m.find(lastEndIndex)) {
            int s = m.start();
            int e = m.end();
            arr.add(formatStr.substring(lastEndIndex, s));
            String ph = formatStr.substring(s, e);
            if (iFlag == -1) {
                iFlag = ph.indexOf("P") > 0 ? 0 : 1;
                indexFlag = iFlag == 1;
            } else {
                if ((iFlag == 1 && ph.indexOf("P") > 0) || (iFlag == 0 && ph.indexOf("P") < 0)) {
                    throw new RuntimeException("StringFormat:{P}与{0}两种占位符不能同时使用!");
                }
            }
            if (indexFlag) {
                int index = Integer.parseInt(ph.substring(1, ph.length() - 1));
                indexs.add(new Integer(index));
            } else {
                indexs.add(new Integer(0));
            }
            lastEndIndex = e;
        }
        arr.add(formatStr.substring(lastEndIndex, formatStr.length()));
        params = new String[indexs.size()];
        flags = new boolean[indexs.size()];
    }

    public void addParam(String v) {
        if (indexFlag) {
            throw new RuntimeException("StringFormat:己指定占位符格式为{0}!");
        }
        if (iAddedCount >= indexs.size()) {
            throw new RuntimeException("StringFormat:所有的参数都己填充!");
        }
        params[iAddedCount] = v;
        flags[iAddedCount] = true;
        iAddedCount++;
    }

    public void addParam(String v, int index) {
        if (!indexFlag) {
            throw new RuntimeException("StringFormat:己指定占位符格式为{P}!");
        }
        boolean b = false;
        for (int i = 0; i < indexs.size(); i++) {
            if (indexs.get(i).equals(new Integer(index))) {
                params[i] = v;
                flags[i] = true;
                b = true;
            }
        }
        if (!b) {
            throw new RuntimeException("StringFormat:要格式化的字符串中找不到index值:" + index);
        }
    }

    public void addParam(long v) {
        addParam(String.valueOf(v));
    }

    public void addParam(long v, int index) {
        addParam(String.valueOf(v), index);
    }

    public void addParam(int v) {
        addParam(String.valueOf(v));
    }

    public void addParam(int v, int index) {
        addParam(String.valueOf(v), index);
    }

    public void addParam(float v) {
        addParam(String.valueOf(v));
    }

    public void addParam(float v, int index) {
        addParam(String.valueOf(v), index);
    }

    public void addParam(double v) {
        addParam(String.valueOf(v));
    }

    public void addParam(double v, int index) {
        addParam(String.valueOf(v), index);
    }

    public void addParam(Object v) {
        addParam(String.valueOf(v));
    }

    public void addParam(Object v, int index) {
        addParam(String.valueOf(v), index);
    }

    public String toString() {
        for (int i = 0; i < flags.length; i++) {
            if (!flags[i]) {
                throw new RuntimeException("StringFormat:尚有参数未被填充!");
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            sb.append(arr.get(i));
            sb.append(params[i]);
        }
        sb.append(arr.get(arr.size() - 1));
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            String sql = "{5}update t1 set c1='{13424}',c2={0},c3='{1}' where c4='{2}'{3}'";
            System.out.println(sql);
            StringFormat sf = new StringFormat(sql);
            sf.addParam("V1", 0);
            sf.addParam("V1", 1);
            sf.addParam(342973, 2);
            sf.addParam("V3", 3);
            sf.addParam("V13424", 13424);
            sf.addParam("V4", 5);
            String s = sf.toString();
            System.out.println(s);
        }
        System.out.println(System.currentTimeMillis()-t);
    }

}
