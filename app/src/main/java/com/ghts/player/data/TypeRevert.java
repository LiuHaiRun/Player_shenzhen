package com.ghts.player.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lijingjing on 17-8-19.
 */
public class TypeRevert {

    /**
     * 将int值转为低字节byte数组
     */
    public static byte[] intToByte(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * c++ 32位 转int
      */
    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
        return targets;
    }
    public static byte[] intToByte2(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte[] shortToByte(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    //浮点到字节转换
    public static byte[] doubleToBytes(double d)
    {
        byte writeBuffer[]= new byte[8];
        long v = Double.doubleToLongBits(d);
        writeBuffer[0] = (byte)(v >>> 0);
        writeBuffer[1] = (byte)(v >>> 8);
        writeBuffer[2] = (byte)(v >>> 16);
        writeBuffer[3] = (byte)(v >>> 24);
        writeBuffer[4] = (byte)(v >>> 32);
        writeBuffer[5] = (byte)(v >>> 40);
        writeBuffer[6] = (byte)(v >>>  48);
        writeBuffer[7] = (byte)(v >>>  56);
        return writeBuffer;

    }

    /**
     * 将64位的long值放到8字节的byte数组
     */
    public static byte[] longToByte(long num) {
        byte[] result = new byte[8];
        result[0] = (byte) (num);// 取最高8位放到0下标
        result[1] = (byte) (num >>> 8);// 取最高8位放到0下标
        result[2] = (byte) (num >>> 16);// 取最高8位放到0下标
        result[3] = (byte) (num >>> 24);// 取最高8位放到0下标
        result[4] = (byte) (num >>> 32);// 取最高8位放到0下标
        result[5] = (byte) (num >>> 40);// 取次高8为放到1下标
        result[6] = (byte) (num >>> 48); // 取次低8位放到2下标
        result[7] = (byte) (num >>> 56); // 取最低8位放到3下标
        return result;
    }

    public static byte[] longToByte2(long n) {

        byte[] b = new byte[8];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        b[4] = (byte) (n >>> 32 & 0xff);
        b[5] = (byte) (n >>> 40 & 0xff);
        b[6] = (byte) (n >>> 48 & 0xff);
        b[7] = (byte) (n >>> 56 & 0xff);
        return b;
    }
    /**
     * 将低字节在前高字节在后的byte数组转为int，
     */
    public static int bytesToInt(byte[] bArr) {
        int n = 0;
        for(int i=0;i<bArr.length&&i<4;i++){
            int left = i*8;
            n+= (bArr[i] << left);
        }
        return n;
    }

    /**
     * 将低字节在前高字节在后的byte数组转为long，
     */
    public static long bytesToLong(byte[] bArr) {
        long n = 0;
        for(int i=0;i<bArr.length&&i<8;i++){
            int left = i*8;
            n+= (bArr[i] << left);
        }
        return n;
    }

    /**
     * 将低字节在前高字节在后的byte数组转为long，
     */
    public static short bytesToShort(byte[] bArr) {
        short n = 0;
        for(int i=0;i<bArr.length&&i<2;i++){
            int left = i*8;
            n+= (bArr[i] << left);
        }
        return n;
    }

    /**
     * 将short值转为低字节byte数组
     */
    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s << 0);
        b[index + 0] = (byte) (s << 8);
    }


    /**
     * 将float值转为低字节byte数组
     */
    public static void putFloat(byte[] bb, float x, int index) {
        // byte[] b = new byte[4];
        int l = Float.floatToIntBits(x);
        for (int i = 0; i < 4; i++) {
            bb[index + i] = new Integer(l).byteValue();
            l = l >> 8;
        }
    }
    private static String byte2hex(byte [] buffer){
        String h = "";

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + temp;
        }
        return h;
    }

    // 将 UTF-8 编码的字符串转换为 GB2312 编码格式：
    public static String utf8Togb2312(String str){
        StringBuffer sb = new StringBuffer();
        for ( int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '+' :
                    sb.append( ' ' );
                    break ;
                case '%' :
                    try {
                        sb.append(( char )Integer.parseInt (str.substring(i+1,i+3),16));
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    }
                    i += 2;
                    break ;
                default :
                    sb.append(c);
                    break ;
            }
        }
        String result = sb.toString();
        String res= null ;
        try {
            byte [] inputBytes = result.getBytes( "8859_1" );
            res= new String(inputBytes, "UTF-8" );
        }
        catch (Exception e){}
        return res;
    }

// 将 GB2312 编码格式的字符串转换为 UTF-8 格式的字符串：
    public static String gb2312ToUtf8(String str) {
        String urlEncode = "" ;
        try {
            urlEncode = URLEncoder.encode (str, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlEncode;
    }



    public int ByteArrayToInt(byte[] bAttr){
        int n=0;
        int leftmove;
        for(int i=0;i<4&&(i<bAttr.length);i++){
            leftmove = i*8;
            n += bAttr[i]<<leftmove;
        }
        return n;

    }

    public static  byte[] stringToByteArray(String str,int length){
        byte[] temp=new byte[length];
        try {
            temp=str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
        }
        return temp;
    }

    public String ByteArrayToString(byte[] bAttr,int maxLen){
        int index=0;
        while(index <bAttr.length&&index<maxLen){
            if(bAttr[index] == 0){
                break;
            }
            index++;
        }
        byte[] tmp = new byte[index];
        System.arraycopy(bAttr, 0, tmp, 0, index);
        String str=null;
        try {
            str = new String(tmp,"GBK");
        } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
        }
        return str;

    }
//    byte []= str.getBytes("GBK");
//    　　在java里面接收到 byte [] lems后，用创建一个新字符串的方式 String s=new String(lems,"GBK")，就可以得到那些传递过来的字符串。


}
