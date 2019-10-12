package com.ghts.player.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ghts.player.activity.PlayerActivity;
import com.ghts.player.bean.EmergBean;
import com.ghts.player.parse.ParseXml;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

/**
 * Created by lijingjing on 17-5-24.
 * 数据请求并解析
 */
public class ParseJson {

    String str = "";
    private String[] split;

    public ParseJson() {

    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<EmergBean> beanArrayList = (ArrayList<EmergBean>) msg.obj;
            if(beanArrayList != null && beanArrayList.size() > 0) {
                ArrayList<String> recList = parse(beanArrayList);

                if (recList != null && recList.size() > 0) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.UPDATE_EMERGENCY);
                    myIntent.putStringArrayListExtra(ConstantValue.EXTRA_OBJ, recList);
                    PlayerActivity.activity.sendBroadcast(myIntent);
                }
            }
        }
    };


    ArrayList<EmergBean> beanArrayList = null;
    //启用获取参数
    public void xGetParam(String url, String linecode, String stationcode) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("line_code", "L001");
        params.addQueryStringParameter("station_code", "S200");
        HttpUtils http = new HttpUtils();
        String newurl=url+"linecode="+linecode+"&"+"stationcode="+stationcode;
        //        http.configCurrentHttpCacheExpiry(1000 * 10); //设置超时时间   10s   params,
        http.send(HttpRequest.HttpMethod.GET, newurl,  new RequestCallBack<Object>() {

            private EmergBean bean;

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
            }
            @Override
            public void onStart() {
                super.onStart();
            }
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                str = responseInfo.result.toString();

                String newstr = jsonTrope(str);
                //        str = "{\"count\": \"3\",\n" +
                //                "    \"list\": [\t\t\t\t\t\t\n" +
                //                "        {\n" +
                //                "            \"Info_id\": \"111\",\t\t\n" +
                //                "            \"Info_name\": \"1\",\t\t\n" +
                //                "            \"Info_cotent\": \"紧急消息测试1\",\t\n" +
                //                "            \"Info_type\": \"1\",\t\t\n" +
                //                "            \"Sub_type\": \"1\",\t\t\t\t\n" +
                //                "            \"Start_date\": \"2018-09-01 00:00:00\",\t\t\n" +
                //                "            \"Stop_date\": \"2018-10-10 00:00:00\",\t\t\t\n" +
                //                "            \"Start_time\": \"1\",\t\n" +
                //                "            \"Stop_time\": \"1\",\t\t\n" +
                //                "            \"Editor_id\": \"1\",\t\t\t\t\n" +
                //                "            \"Edit_time\": \"1\",\t\t\n" +
                //                "            \"Audit\": \"1\",\t\t\n" +
                //                "            \"Device_pos\": \"1\",\t\n" +
                //                "            \"Line_code\": \"1\",\t\t\n" +
                //                "            \"Station_code\": \"1\"\t\t\t\t\t\t\t\n" +
                //                "        }, {\n" +
                //                "            \"Info_id\": \"111\",\t\t\n" +
                //                "            \"Info_name\": \"1\",\t\t\n" +
                //                "            \"Info_cotent\": \"测试二二二\",\t\n" +
                //                "            \"Info_type\": \"1\",\t\t\n" +
                //                "            \"Sub_type\": \"1\",\t\t\t\t\n" +
                //                "            \"Start_date\": \"2018-09-01 00:00:00\",\t\t\n" +
                //                "            \"Stop_date\": \"2018-09-10 00:00:00\",\t\t\t\n" +
                //                "            \"Start_time\": \"1\",\t\n" +
                //                "            \"Stop_time\": \"1\",\t\t\n" +
                //                "            \"Editor_id\": \"1\",\t\t\t\t\n" +
                //                "            \"Edit_time\": \"1\",\t\t\n" +
                //                "            \"Audit\": \"1\",\t\t\n" +
                //                "            \"Device_pos\": \"1\",\t\n" +
                //                "            \"Line_code\": \"1\",\t\t\n" +
                //                "            \"Station_code\": \"1\"\t\t\t\t\t\t\t\n" +
                //                "        }, {\n" +
                //                "            \"Info_id\": \"111\",\t\t\n" +
                //                "            \"Info_name\": \"1\",\t\t\n" +
                //                "            \"Info_cotent\": \"杭州四四四\",\t\n" +
                //                "            \"Info_type\": \"1\",\t\t\n" +
                //                "            \"Sub_type\": \"1\",\t\t\t\t\n" +
                //                "            \"Start_date\": \"2018-09-26 00:00:00\",\t\t\n" +
                //                "            \"Stop_date\": \"2018-09-26 23:59:00\",\t\t\t\n" +
                //                "            \"Start_time\": \"1\",\t\n" +
                //                "            \"Stop_time\": \"1\",\t\t\n" +
                //                "            \"Editor_id\": \"1\",\t\t\t\t\n" +
                //                "            \"Edit_time\": \"1\",\t\t\n" +
                //                "            \"Audit\": \"1\",\t\t\n" +
                //                "            \"Device_pos\": \"1\",\t\n" +
                //                "            \"Line_code\": \"1\",\t\t\n" +
                //                "            \"Station_code\": \"1\"\t\t\t\t\t\t\t\n" +
                //                "        }\n" +
                //                "    ]\n" +
                //                "}";
                try {
                    String aa = "(更新时间:"+getscrollingtime()+")&"+newstr;
                    JSONObject  jsonObject = new JSONObject(newstr);
                    Gson gson = new Gson();
                    EmergBean bean = gson.fromJson(newstr, EmergBean.class);
                    List<String> contents = bean.getContents();
                    beanArrayList = new ArrayList<EmergBean>();
                    beanArrayList.clear();
                    //                    if (num > 0) {
                    //                        String list = jsonObject.getString("list");
                    //                        JSONArray jsonArray = new JSONArray(list);
                    //                        if (jsonArray.length() > 0) {
                    //                            for (int i = 0; i < jsonArray.length(); i++) {
                    //                                bean = new EmergBean();
                    //                                JSONObject json = jsonArray.getJSONObject(i);
                    //
                    //                              //  bean.setContents();
                    ////                                bean.setInfo_id(getString(json, "Info_id"));
                    ////                                bean.setInfo_name(getString(json, "Info_name"));
                    ////                                bean.setInfo_cotent(getString(json, "Info_cotent"));
                    ////                                bean.setInfo_type(getString(json, "Info_type"));
                    ////                                bean.setSub_type(getString(json, "Sub_type"));
                    ////                                bean.setStart_date(getString(json, "Start_date"));
                    ////                                bean.setStop_date(getString(json, "Stop_date"));
                    ////                                bean.setStart_time(getString(json, "Start_time"));
                    ////                                bean.setStop_time(getString(json, "Stop_time"));
                    ////                                bean.setEditor_id(getString(json, "Editor_id"));
                    ////                                bean.setEdit_time(getString(json, "Edit_time"));
                    ////                                bean.setAudit(getString(json, "Audit"));
                    ////                                bean.setDevice_pos(getString(json, "Device_pos"));
                    ////                                bean.setLine_code(getString(json, "Line_code"));
                    ////                                bean.setStation_code(getString(json, "Station_code"));
                    //                            }
                    beanArrayList.add(bean);
                    //                        }
                    //                    }
                    //判断当前字符串是否与Emerg.txt中的相同，如果不同则重新写入
                    String jsonstr = PubUtil.getFileContent(Const.EMERG_PATH);
                    String[] splittime = jsonstr.split("&");
                    String s="";
                    if (splittime.length>1){
                        String strtime = splittime[1];
                        JSONObject object = new JSONObject(strtime);
                        String[] split = object.toString().split("]");
                        s = split[1];
                    }else {
                        s = "";
                    }
                    String[] split1 = jsonObject.toString().split("]");
                    String s1 = split1[1];
                    if(!s.equals(s1)){
                        LogUtil.e("滚动发送","-----------滚动文本更新时间----------");
                        PubUtil.writeTxtToFile(aa);
                        LogUtil.e("滚动发送","-----------执行更新滚动文本----------");
                        Message message = new Message();
                        message.obj = beanArrayList;
                        mHandler.sendMessage(message);
                    }
                    Const.scrollingtime=getTimeDate();

                } catch (Exception e) {
                    Log.i("解析错误", e.toString());
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("获取紧急消息失败", s);
            }
        });
    }

    DateFormat formater = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    ArrayList<String> emergBeen = null;
    public ArrayList<String> parse(ArrayList<EmergBean> list) {
        emergBeen = new ArrayList<String>();
        emergBeen.clear();
        for (int i = 0;i <list.size();i++){
            EmergBean bean = list.get(i);
            for (int j = 0;j <bean.getContents().size();j++){
                emergBeen.add(bean.getContents().get(j));
                LogUtil.e("文本内容",bean.getContents().get(j));
            }
        }
        //        try {
        //            for (int i = 0; i < list.size(); i++) {
        //                EmergBean bean = list.get(i);
        //                Date start = formater.parse(bean.getStart_date());
        //                Date stop = formater.parse(bean.getStop_date());
        //                Date now = VeDate.getNowDate();
        //                if (now.getTime() >= start.getTime() && now.getTime() <= stop.getTime()) {
        //                    emergBeen.add(bean.getInfo_cotent());
        //                }else {
        //                    emergBeen.add(bean.getInfo_cotent());
        //                }
        //             }
        //        } catch (Exception e) {
        //
        //        }
        return emergBeen;
    }

    public String getString(JSONObject object, String key) {
        try {
            String obj = filterSpecialCharOfXml(object.getString(key).toString().trim());
            //            obj = removeBOM(obj);
            return obj;
        } catch (Exception ex) {
            return "";
        }
    }

    //去掉BOM
    public static final String removeBOM(String data) {
        if (isEmpty(data)) {
            return data;
        }
        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    //过滤在xml中不被识别的字符
    public static String filterSpecialCharOfXml(String txt) {
        String res = "";
        for (int i = 0; i < txt.length(); ++i) {
            char ch = txt.charAt(i);
            if (Character.isDefined(ch) && ch != '&' && ch != '<' && ch != '>' && !Character.isHighSurrogate(ch) && !Character.isISOControl(ch) && !Character.isLowSurrogate(ch)) {
                res = res + ch;
            }
        }
        return res;
    }

    //判断XML字符串中是否有非法字符
    public static int checkCharacterData(String text) {
        int errorChar = 0;
        if (text == null) {
            return errorChar;
        }
        char[] data = text.toCharArray();
        for (int i = 0, len = data.length; i < len; i++) {
            char c = data[i];
            int result = c;
            //先判断是否在代理范围（surrogate blocks）
            //增补字符编码为两个代码单元，
            //第一个单元来自于高代理（high surrogate）范围（0xD800 至 0xDBFF），
            //第二个单元来自于低代理（low surrogate）范围（0xDC00 至 0xDFFF）。
            if (result >= 0xD800 && result <= 0xDBFF) {
                //解码代理对（surrogate pair）
                int high = c;
                try {
                    int low = text.charAt(i + 1);

                    if (low < 0xDC00 || low > 0xDFFF) {
                        char ch = (char) low;
                    }
                    //unicode说明定义的算法 计算出增补字符范围0x10000 至 0x10FFFF
                    //即若result是增补字符集，应该在0x10000到0x10FFFF之间，isXMLCharacter中有判断
                    result = (high - 0xD800) * 0x400 + (low - 0xDC00) + 0x10000;
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!isXMLCharacter(result)) {
                errorChar++;
            }
        }
        return errorChar;
    }

    private static boolean isXMLCharacter(int c) {
        //根据xml规范中的Character Range检测xml不支持的字符
        if (c <= 0xD7FF) {
            if (c >= 0x20)
                return true;
            else {
                if (c == '\n')
                    return true;
                if (c == '\r')
                    return true;
                if (c == '\t')
                    return true;
                return false;
            }
        }
        if (c < 0xE000)
            return false;
        if (c <= 0xFFFD)
            return true;
        if (c < 0x10000)
            return false;
        if (c <= 0x10FFFF)
            return true;
        return false;
    }
    public String jsonTrope(String jsonString){
        String[] contents1 = jsonString.split("contents");
        String text = contents1[1];
        String t="\"";
        String replace=text.replace("\"","”");
        StringBuilder stringBuilder = new StringBuilder(replace);
        StringBuilder replace1 = stringBuilder.replace(0, 1, t);
        StringBuilder replace2 = replace1.replace(3, 4, t);
        StringBuilder replace3 = replace2.replace(replace2.length()-3, replace2.length()-2, t);
        String replace4 = replace3.toString();
        String newstrone=contents1[0]+"contents"+replace4;
        LogUtil.e("-----滚动信息-----",str);
        LogUtil.e("-----滚动信息-----",newstrone);
        return newstrone;
    }
    public String getscrollingtime(){
        Date date = new Date();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(date);
        return format;
    }

    public String getTimeDate(){
        String oldtime="";
        String text = null;
        try {
            File file = new File(Const.EMERG_PATH);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((text=bufferedReader.readLine())!=null){
                oldtime+=text;
            }
            if(!TextUtils.isEmpty(oldtime) && oldtime.contains("&")) {
                split = oldtime.split("&");
                LogUtil.e("oldtime" + split.length, split[0]);
                return split[0];
            }
        } catch (IOException e) {
            return "";
        }
        return "";
    }
}
