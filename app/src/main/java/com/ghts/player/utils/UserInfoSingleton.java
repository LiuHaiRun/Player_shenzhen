package com.ghts.player.utils;

import android.text.TextUtils;

import com.ghts.player.application.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import static com.ghts.player.utils.Const.IsPowerOff;

/**
 * Created by lijingjing on 17-9-22.
 */
public class UserInfoSingleton {


    /**
     * 是否直播 0 紧急 1 正常
     */
    private volatile static String isExigent;

    private static String playPath;


    public volatile static boolean isUpdate = true;

    /**
     * 是否直播 0 本地 1 直播
     */
    private volatile static String islive;
    /**
     * 是否开关机 0 开 1 关
     */
    private volatile static String isPowerOff;
    /**
     //     * 是否开关屏幕 0 开 1 关
     //     */
    private volatile static String isScreenOff;
    /**
     * 紧急消息内容
     */
    private static String emergency;
    /**
     * 是否是全屏紧急消息 全屏:true 局部:false
     */
    private static boolean isEmFull = false;

    public static boolean putStringAndCommit(String key, String value) {
        return MyApplication.sysEdit.putString(key, value).commit();
    }

    public static boolean putIntAndCommit(String key, int value) {
        return MyApplication.sysEdit.putInt(key, value).commit();
    }

    public static boolean putBooleanAndCommit(String key, boolean value) {
        return MyApplication.sysEdit.putBoolean(key, value).commit();
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defValue) {
        return MyApplication.sysShare.getString(key, defValue);
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static int getInt(String key, int defValue) {
        return MyApplication.sysShare.getInt(key, defValue);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return MyApplication.sysShare.getBoolean(key, defValue);
    }

    /**
     * @return the isExigent
     */
    public static String getIsExigent() {
        isExigent = MyApplication.sysShare.getString(Const.ISEXIGENT, "");
        return isExigent;
    }

    /**
     * @param isExigent the isExigent to set
     */
    public static void setIsExigent(String isExigent) {
        UserInfoSingleton.isExigent = isExigent;
        MyApplication.sysEdit.putString(Const.ISEXIGENT, isExigent);
        MyApplication.sysEdit.commit();
    }

    /**
     * @return the islive
     */
    public static String getIslive() {
        islive = MyApplication.sysShare.getString(Const.ISLIVE, "");
        return islive;
    }

    /**
     * @param islive the islive to set
     */
    public static void setIslive(String islive) {
        UserInfoSingleton.islive = islive;
        MyApplication.sysEdit.putString(Const.ISLIVE, islive);
        MyApplication.sysEdit.commit();
    }

    public static String getIsScreenOff() {
        isScreenOff = MyApplication.sysShare.getString(Const.IsScreenOff, "");
        return isScreenOff;
    }
    public static void setIsScreenOff(String isScreenOffOff) {
        UserInfoSingleton.isScreenOff = isScreenOffOff;
        MyApplication.sysEdit.putString(Const.IsScreenOff, isScreenOff);
        MyApplication.sysEdit.commit();
    }
    public static String getIsPowerOff() {
        isPowerOff = MyApplication.sysShare.getString(IsPowerOff, "");
        return isPowerOff;
    }

    /**
     * @param IsPowerOff the IsPowerOff to set
     */
    public static void setIsPowerOff(String IsPowerOff) {
        UserInfoSingleton.isPowerOff = IsPowerOff;
        MyApplication.sysEdit.putString(Const.IsPowerOff, isPowerOff);
        MyApplication.sysEdit.commit();
    }

    /**
     * @return the playPath
     */
    public static String getPlayPath() {
        playPath = MyApplication.sysShare.getString(Const.PLAYPATH, "");
        return playPath;
    }

    /**
     * @param playPath the playPath to set
     */
    public static void setPlayPath(String playPath) {
        UserInfoSingleton.playPath = playPath;
        MyApplication.sysEdit.putString(Const.PLAYPATH, playPath);
        MyApplication.sysEdit.commit();
    }

    /**
     * desc:保存对象
     *
     * @param key
     * @param obj     要保存的对象，只能保存实现了serializable的对象
     *                modified:
     */
    public static void saveObject(String key, Object obj) {
        try {
            // 保存对象
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            MyApplication.sysEdit.putString(key, bytesToHexString);
            MyApplication.sysEdit.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param key
     * @return modified:
     */
    public static Object readObject(String key) {
        try {
            if (MyApplication.sysShare.contains(key)) {
                String string = MyApplication.sysShare.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;

                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    public static String getEmergency() {
        return emergency;
    }

    public static void setEmergency(String emergency) {
        UserInfoSingleton.emergency = emergency;
    }

    public static void setIsEmFull(boolean isEmFull) {
        UserInfoSingleton.isEmFull = isEmFull;
    }

    public static boolean getIsEmFull() {
        return isEmFull;
    }
}
