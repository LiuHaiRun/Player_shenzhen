package com.ghts.player.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijingjing on 17-17-4-25.
 * 对文件读取的工具类
 */
public class PubUtil {

    private static PubUtil fileUtil;

    // 构造函数私有化
    private PubUtil() {
    }

    // 提供一个全局的静态方法
    public static PubUtil getPubUtil() {
        if (fileUtil == null) {
            fileUtil = new PubUtil();
        }
        return fileUtil;
    }

    //   获取文件后缀名
    public static String getFileExtension(String path) {
        if (null != path) {
            // 后缀点 的位置
            int dex = path.lastIndexOf(".");
            // 截取后缀名
            return path.substring(dex + 1);
        }
        return null;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /*
 * Java文件操作 获取不带扩展名的文件名
 */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取当前时间
     */

    public static String getTime() {
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String str = sdf.format(new Date());
        return str;
    }

    /**
     * 比较两个时间大小
     *
     * @param date1 开始时间
     * @param date2 结束时间
     */
    public static boolean TimeCompare(String date1, String date2) {
        boolean flag = false;
        //格式化时间
        SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss");
        try {
            Date firstTime = CurrentTime.parse(date1);
            Date stopTime = CurrentTime.parse(date2);
            Date currentTime = CurrentTime.parse(getTime());

            if (firstTime.getTime() <= currentTime.getTime() && currentTime.getTime() <= stopTime.getTime()) {
                Log.e("hi", "在外围内");
                flag = true;
            } else {
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * string类型时间转换为date
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static double getScaleX(int width) {
        double scaleX = (double) Const.screenW / width;
        Log.e("-width-" + Const.screenW, width + "");
        return scaleX;
    }

    public static double getScaleY(int height) {
        double scaleY = (double) Const.screenH / height;
        Log.e("-height－" + Const.screenH, height + "");
        return scaleY;
    }


    /**
     * 根据1920，1080屏幕，进行适配
     */
    public static int[] getLocation(int width, int height, int x, int y) {
        width = (int) (width * Const.ScaleX);
        height = (int) (height * Const.ScaleY);
        x = (int) (x * Const.ScaleX);
        y = (int) (y * Const.ScaleY);
        int data[] = {width, height, x, y};
        return data;
    }

    public static int[] getLocation2(int width, int height, int x, int y) {
        //        int data[] = {width, height, x, y};
        width = (int) (width * Const.ScaleX);
        height = (int) (height * Const.ScaleY);
        x = (int) (x * Const.ScaleX);
        y = (int) (y * Const.ScaleY);
        int data[] = {width, height, x, y};
        return data;
    }

    /**
     * 读取.txt文件
     */
    public static String readTxt(String path) {
        String txt = "";
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GBK");
            BufferedReader br = new BufferedReader(isr);
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                txt = txt + mimeTypeLine;
            }
            Log.e("--read---", txt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txt;
    }

    /**
     * 数据类型转换
     *
     * @param s
     */
    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 数据类型转换
     *
     * @param s
     */
    public static float parseFloat(String s, int def) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 字符串转成整型,非整型字符转为0
     *
     * @return 整型
     */
    public static int parseInt(String s) {
        try {
            if (s == null)
                return 0;
            else
                return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }

    }

    public static String getNum(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        System.out.println(m.replaceAll("").trim());
        return m.replaceAll("");
    }

    /**
     * /**
     * 分隔字符串
     *
     * @param allStr String
     * @param split  char
     * @return Vector
     */
    public static Vector SplitIntoVector(String allStr, char split) {
        Vector aVec = new Vector();
        if (allStr == null || allStr.equals("") || allStr.equals("" + split))// 如果是空字符串则直接返回一个包含0个元素的向量
        {
            return aVec;
        }
        int pos = allStr.indexOf(split);
        if (pos == -1)
            aVec.addElement(allStr); // 用消息处理器处理接受到的数据
        else {
            // 分隔处理每条信息
            int len = allStr.length();
            int oldpos = 0;
            while (oldpos < len) {
                aVec.addElement(allStr.substring(oldpos, pos));
                oldpos = pos + 1;
                pos = allStr.indexOf(split, oldpos + 1);
                if (pos == -1)
                    pos = len;
            }
        }
        return aVec;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 获取文本宽度
     *
     * @return
     */
    public static int getTextWidth(String text, float fontsize) {
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(fontsize);
        // Measure the width of the text string.
        return (int) mTextPaint.measureText(text);
    }

    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    //压缩图片
    public static Bitmap fitSizeImg(String path) {
        if (path == null || path.length() < 1)
            return null;
        File file = new File(path);
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 数字越大读出的图片占用的heap越小 不然总是溢出
        if (file.length() < 20480) {       // 0-20k
            opts.inSampleSize = 1;
        } else if (file.length() < 51200) { // 20-50k
            opts.inSampleSize = 1;
        } else if (file.length() < 307200) { // 50-300k
            opts.inSampleSize = 1;
        } else if (file.length() < 819200) { // 300-800k
            opts.inSampleSize = 1;
        } else if (file.length() < 1048576) { // 800-1024k
            opts.inSampleSize = 2;
        } else {
            opts.inSampleSize = 2;
        }
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
        return resizeBmp;
    }


    /**
     * 添加n个空格
     *
     * @param count
     * @return
     */
    public static String getSpaceCount(int count) {
        String st = "";
        if (count < 0) {
            count = 0;
        }
        for (int i = 0; i < count; i++) {
            st = st + " ";
        }
        return st;
    }

    public static boolean isCurrentDay(String str1) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str2 = sdf.format(new Date());
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() == dt2.getTime()) {
            isBigger = true;
        } else {
            isBigger = false;
        }
        return isBigger;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceinfos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : serviceinfos) {
            if (className.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void doStartApplicationWithPackageName(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            LogUtil.e("----启动--watchplayer--", "-----");
        }
    }

    /*功能：读取MyGPIO的高低电平
         * cfd:触发点名称
         * 返回值：int failure -1 success 0
         */
        /*
     * myGPIO(600l) Input array
	 */
    public static int ReadmGpio(String cfd) {
        int Ret = 0;
        String s = "";
        String cmd = "";
        cmd = "/system/bin/cat " + "/sys/kernel/kobj_gpio/" + cfd;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader ISR = new InputStreamReader(p.getInputStream());
            BufferedReader BFR = new BufferedReader(ISR);
            String line = null;
            while ((line = BFR.readLine()) != null) {
                s += line;
            }
            if (ISR != null) {
                ISR.close();
            }
            if (BFR != null) {
                BFR.close();
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // s = 0;通  s = 1;断
        if (s.equals("0")) {
            Ret = 0;
            Log.e("tag", "----------------->" + cfd + " " + s + "false");

        } else if (s.equals("1")) {
            Log.e("tag", "----------------->" + cfd + " " + s + "true");
            Ret = 1;
        } else {
            Ret = -1;
        }
        return Ret;
    }

    public static String getFileContent(String path) {
        String content = "";
        JSONObject jsonObject = null;
        try {
            File file = new File(path);
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line;
                }
                instream.close();//关闭输入流
                jsonObject = new JSONObject(content);
            }
        } catch (Exception e) {
            Log.d("TestFile", e.getMessage());
        }
        return content;
    }

    //    public static JSONObject getFileContent(String path) {
//        String content = "";
//        JSONObject  jsonObject = null;
//        try {
//            File file = new File(path);
//            InputStream instream = new FileInputStream(file);
//            if (instream != null) {
//                InputStreamReader inputreader
//                        = new InputStreamReader(instream, "UTF-8");
//                BufferedReader buffreader = new BufferedReader(inputreader);
//                String line = "";
//                //分行读取
//                while ((line = buffreader.readLine()) != null) {
//                    content += line;
//                }
//                instream.close();//关闭输入流
//                jsonObject  = new JSONObject(content);
//            }
//        } catch (Exception e) {
//            Log.d("TestFile", e.getMessage());
//        }
//        return jsonObject;
//    }
    public static String getContent(String path) {
        String content = "";
        try {
            File file = new File(path);
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line;
                }
                instream.close();//关闭输入流
            }
        } catch (Exception e) {
            Log.d("TestFile", e.getMessage());
        }
        return content;
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent) {
        try {
            BufferedWriter writer = null;
            File file = new File(Const.EMERG_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(Const.EMERG_PATH);
            fileOutputStream.write(strcontent.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    /*
     * 功能：写入MyGPIO的高低电平
	 * 函数名：WriteMyGPIO(int num, int level, int type) num 指哪一个GPIO level指高低电平  type 单向 双向
	 * 返回值：int failure -1 success 0
	 */
    public static int WriteMyGPIO(int level) {
        int Ret = 0;
        try {
            Process su;
            String cmd = "";
            su = Runtime.getRuntime().exec("/system/bin/sh");
            cmd = "echo " + level + " > /sys/kernel/kobj_gpio/OUTPUT_enable" + "\n" + "exit\n";
            OutputStream OS = su.getOutputStream();
            OS.write(cmd.getBytes());
            try {
                if (su.waitFor() != 0) {
//                    throw new SecurityException();
                    LogUtil.e("执行开关机异常", "");
                }
            } catch (InterruptedException e) {
                LogUtil.e("执行开关机异常", e.toString());
            }
        } catch (IOException e) {
            LogUtil.e("执行开关机异常", e.toString());
        }
        return Ret;
    }

    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**  * 用256求余最大是255，即16进制的FF  */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length(); // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }


}



