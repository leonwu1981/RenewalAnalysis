/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.encrypt;

import com.sinosoft.lis.encrypt.security.IDEA;

public final class LisIDEA
{
    final String mCipherKeyStr = "12dc427f09a81e293d43db3b2378491d";
    final int mStrLen = 8;

    public String encryptString(String plainStr)
    {
        //make sure plainStr is with fix length, if it is short, fill
        //it with " "; if it is longer, cut it
        String tplainStr = "";
        int len = plainStr.length();
        if (len <= mStrLen)
        {
            for (int i = 0; i < len; i++)
            {
                tplainStr += plainStr.charAt(i);
            }
            for (int i = 0; i < mStrLen - len; i++)
            {
                tplainStr += " ";
            }
        }
        else
        {
            for (int i = 0; i < mStrLen; i++)
            {
                tplainStr += plainStr.charAt(i);
            }
        }
//        System.out.println("tplainStr len:" + tplainStr.length());
        String hexPlainStr = stringToHexString(tplainStr);
        byte key[] = fromString(mCipherKeyStr);
        byte plain[] = fromString(hexPlainStr);
        IDEA idea = new IDEA(key);
        byte encP[] = new byte[plain.length];

        idea.encrypt(plain, encP);

        String hexEncryptString = toString(encP);
        return hexEncryptString;
        //        return hexStringToString(hexEncryptString);

    }

    public String decryptString_pre(String encryptStr)
    {
        //make sure encryptStr is with fix length, if it is short, fill
        //it with " "; if it is longer, cut it
        String tencryptStr = "";
        int len = encryptStr.length();
        if (len <= mStrLen)
        {
            for (int i = 0; i < len; i++)
            {
                tencryptStr += encryptStr.charAt(i);
            }

            for (int i = 1; i < mStrLen - len; i++)
            {
                tencryptStr += " ";
            }
        }
        else
        {
            for (int i = 0; i < mStrLen; i++)
            {
                tencryptStr += encryptStr.charAt(i);
            }
        }

        String hexEncryptStr = stringToHexString(tencryptStr);
        byte key[] = fromString(mCipherKeyStr);
        IDEA idea = new IDEA(key);
        byte encP[] = fromString(hexEncryptStr);
        byte decC[] = new byte[encP.length];
        idea.decrypt(encP, decC);
        String hexDecryptStr = toString(decC);
        return hexStringToString(hexDecryptStr);
    }

    public String decryptString(String encryptStr)
    {
        String hexEncryptStr = encryptStr;
        byte key[] = fromString(mCipherKeyStr);
        IDEA idea = new IDEA(key);
        byte encP[] = fromString(hexEncryptStr);
        byte decC[] = new byte[encP.length];
        idea.decrypt(encP, decC);
        String hexDecryptStr = toString(decC);
        return hexStringToString(hexDecryptStr);
    }

    private static String
            stringToHexString(String srcString)
    {
        String resultString = "";
        int srcLen = srcString.length();
        for (int pos = 0; pos < srcLen; pos++)
        {
            byte b = (byte) srcString.charAt(pos);
            int hexValue = (b & 0x0F);
            resultString += hexToAscii(hexValue);
            hexValue = ((b >> 4) & 0x0F);
            resultString += hexToAscii(hexValue);
        }
//		System.out.println(resultString);
        return resultString;
    }

    private static String
            hexStringToString(String hexString)
    {
        String resultString = "";
        int hexLen = hexString.length();
        for (int pos = 0; pos < hexLen; pos += 2)
        {
            char c1 = hexString.charAt(pos);
            char c2 = hexString.charAt(pos + 1);
            int hexvalue1 = asciiToHex(c1);
            int hexvalue2 = asciiToHex(c2);
            char c = (char) (hexvalue1 | hexvalue2 << 4);
            resultString += c;
        }

        return resultString.trim();
    }

    private static byte[]
            fromString(String inHex)
    {
        int len = inHex.length();
        int pos = 0;
        byte buffer[] = new byte[((len + 1) / 2)];
        if ((len % 2) == 1)
        {
            buffer[0] = (byte) asciiToHex(inHex.charAt(0));
            pos = 1;
            len--;
        }

        for (int ptr = pos; len > 0; len -= 2)
        {
            buffer[pos++] = (byte) (
                    (asciiToHex(inHex.charAt(ptr++)) << 4) |
                    (asciiToHex(inHex.charAt(ptr++)))
                            );
        }
        return buffer;

    }

    private static String
            toString(byte buffer[])
    {
        StringBuffer returnBuffer = new StringBuffer();
        for (int pos = 0, len = buffer.length; pos < len; pos++)
        {
            returnBuffer.append(hexToAscii((buffer[pos] >>> 4) & 0x0F))
                    .append(hexToAscii(buffer[pos] & 0x0F));
        }
        return returnBuffer.toString();

    }

    private static int
            asciiToHex(char c)
    {
        if ((c >= 'a') && (c <= 'f'))
        {
            return (c - 'a' + 10);
        }
        if ((c >= 'A') && (c <= 'F'))
        {
            return (c - 'A' + 10);
        }
        if ((c >= '0') && (c <= '9'))
        {
            return (c - '0');
        }
        throw new Error("ascii to hex failed");
    }

    private static char
            hexToAscii(int h)
    {
        if ((h >= 10) && (h <= 15))
        {
            return (char) ('A' + (h - 10));
        }
        if ((h >= 0) && (h <= 9))
        {
            return (char) ('0' + h);
        }
        throw new Error("hex to ascii failed");
    }

    public static void main(String argv[])
    {
//        String plainStr = "test";
//        String ensryptStr = "EBFE69BFE402325E";
//        LisIDEA tLisIdea = new LisIDEA();
//        String plaintoencryptStr = tLisIdea.encryptString(plainStr);
//        //String encryptStr =
//        System.out.println("plaintoencryptStr = " + plaintoencryptStr);
//        String decrypttoplainStr = tLisIdea.decryptString(ensryptStr);
//        System.out.println(decrypttoplainStr);
    }
}
