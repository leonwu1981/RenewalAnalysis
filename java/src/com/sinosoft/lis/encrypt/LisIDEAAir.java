package com.sinosoft.lis.encrypt;

import com.sinosoft.lis.encrypt.security.BlockCipher;
import com.sinosoft.lis.encrypt.security.IDEA;
import java.io.PrintStream;

public final class LisIDEAAir
{

    final String mCipherKeyStr = "89ab427f09a81e293d43db3b2378491d";
    final int mStrLen = 8;

    public LisIDEAAir()
    {
    }

    public static final void main(String argv[])
    {
        String plainStr = "10000511";
        String plainStr1 = "10000512";
        LisIDEAAir tLisIdea = new LisIDEAAir();
        String encryptStr = tLisIdea.encryptString(plainStr);
        String encryptStr1 = tLisIdea.encryptString(plainStr1);
        System.out.println("encryptStr = ".concat(String.valueOf(String.valueOf(encryptStr))));
        System.out.println("encryptStr1 = ".concat(String.valueOf(String.valueOf(encryptStr1))));
        String decryptStr = tLisIdea.decryptString(encryptStr);
        System.out.println(decryptStr);
    }

    public String encryptString(String plainStr)
    {
        String tplainStr = "";
        int len = plainStr.length();
        if(len <= 8)
        {
            for(int i = 0; i < len; i++)
                tplainStr = String.valueOf(tplainStr) + String.valueOf(plainStr.charAt(i));

            for(int i = 0; i < 8 - len; i++)
                tplainStr = String.valueOf(String.valueOf(tplainStr)).concat(" ");

        } else
        {
            for(int i = 0; i < 8; i++)
                tplainStr = String.valueOf(tplainStr) + String.valueOf(plainStr.charAt(i));

        }
        System.out.println("tplainStr len:".concat(String.valueOf(String.valueOf(tplainStr.length()))));
        String hexPlainStr = stringToHexString(tplainStr);
        byte key[] = fromString("89ab427f09a81e293d43db3b2378491d");
        byte plain[] = fromString(hexPlainStr);
        IDEA idea = new IDEA(key);
        byte encP[] = new byte[plain.length];
        idea.encrypt(plain, encP);
        String hexEncryptString = toString(encP);
        return hexEncryptString;
    }

    public String decryptString_pre(String encryptStr)
    {
        String tencryptStr = "";
        int len = encryptStr.length();
        if(len <= 8)
        {
            for(int i = 0; i < len; i++)
                tencryptStr = String.valueOf(tencryptStr) + String.valueOf(encryptStr.charAt(i));

            for(int i = 1; i < 8 - len; i++)
                tencryptStr = String.valueOf(String.valueOf(tencryptStr)).concat(" ");

        } else
        {
            for(int i = 0; i < 8; i++)
                tencryptStr = String.valueOf(tencryptStr) + String.valueOf(encryptStr.charAt(i));

        }
        String hexEncryptStr = stringToHexString(tencryptStr);
        byte key[] = fromString("89ab427f09a81e293d43db3b2378491d");
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
        byte key[] = fromString("89ab427f09a81e293d43db3b2378491d");
        IDEA idea = new IDEA(key);
        byte encP[] = fromString(hexEncryptStr);
        byte decC[] = new byte[encP.length];
        idea.decrypt(encP, decC);
        String hexDecryptStr = toString(decC);
        return hexStringToString(hexDecryptStr);
    }

    private static String stringToHexString(String srcString)
    {
        String resultString = "";
        int srcLen = srcString.length();
        for(int pos = 0; pos < srcLen; pos++)
        {
            byte b = (byte)srcString.charAt(pos);
            int hexValue = b & 0xf;
            resultString = String.valueOf(resultString) + String.valueOf(hexToAscii(hexValue));
            hexValue = b >> 4 & 0xf;
            resultString = String.valueOf(resultString) + String.valueOf(hexToAscii(hexValue));
        }

        return resultString;
    }

    private static String hexStringToString(String hexString)
    {
        String resultString = "";
        int hexLen = hexString.length();
        for(int pos = 0; pos < hexLen; pos += 2)
        {
            char c1 = hexString.charAt(pos);
            char c2 = hexString.charAt(pos + 1);
            int hexvalue1 = asciiToHex(c1);
            int hexvalue2 = asciiToHex(c2);
            char c = (char)(hexvalue1 | hexvalue2 << 4);
            resultString = String.valueOf(resultString) + String.valueOf(c);
        }

        return resultString.trim();
    }

    private static byte[] fromString(String inHex)
    {
        int len = inHex.length();
        int pos = 0;
        byte buffer[] = new byte[(len + 1) / 2];
        if(len % 2 == 1)
        {
            buffer[0] = (byte)asciiToHex(inHex.charAt(0));
            pos = 1;
            len--;
        }
        int ptr = pos;
        for(; len > 0; len -= 2)
            buffer[pos++] = (byte)(asciiToHex(inHex.charAt(ptr++)) << 4 | asciiToHex(inHex.charAt(ptr++)));

        return buffer;
    }

    private static final String toString(byte buffer[])
    {
        StringBuffer returnBuffer = new StringBuffer();
        int pos = 0;
        for(int len = buffer.length; pos < len; pos++)
            returnBuffer.append(hexToAscii(buffer[pos] >>> 4 & 0xf)).append(hexToAscii(buffer[pos] & 0xf));

        return returnBuffer.toString();
    }

    private static final int asciiToHex(char c)
    {
        if(c >= 'a' && c <= 'f')
            return (c - 97) + 10;
        if(c >= 'A' && c <= 'F')
            return (c - 65) + 10;
        if(c >= '0' && c <= '9')
            return c - 48;
        else
            throw new Error("ascii to hex failed");
    }

    private static char hexToAscii(int h)
    {
        if(h >= 10 && h <= 15)
            return (char)(65 + (h - 10));
        if(h >= 0 && h <= 9)
            return (char)(48 + h);
        else
            throw new Error("hex to ascii failed");
    }
}
