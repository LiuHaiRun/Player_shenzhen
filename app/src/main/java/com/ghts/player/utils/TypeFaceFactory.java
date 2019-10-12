package com.ghts.player.utils;

import android.graphics.Typeface;

import com.ghts.player.bean.FontXml;
import com.ghts.player.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lijingjing on 17-6-14.
 * 创建字体
 */
public class TypeFaceFactory {

    public static final String TAG = "TypeFaceFactory";

    private TypeFaceFactory() {
        super();
    }

    //    static Typeface fzgl = null;
    //    static Typeface fzxz = null;
    //    static Typeface hwxk = null;
    //    static Typeface hwxs = null;
    //    static Typeface wryh = null;
    //    static Typeface mzd = null;
    //    static Typeface arial = null;
    //    static Typeface simyou = null;
    //    static Typeface fzzy = null;
    static HashMap<String, Typeface> mTypefaces;
    //    static ArrayList<Typeface> mTypefaces;

    // 生成对应的字体
    static {
        try {
            //            mTypefaces = new ArrayList<Typeface>();
            mTypefaces = new HashMap<String, Typeface>();
            mTypefaces.clear();
            ArrayList<FontXml> fontList = Const.parseXml.getFonts();
            if (fontList != null && fontList.size() > 0) {
                for (int i = 0; i < fontList.size(); i++) {
                    FontXml fonts = fontList.get(i);
                    Typeface type = Typeface.createFromFile(ConstantValue.ROOT_DIR
                            + "fonts/" + fonts.getValue());
                    mTypefaces.put(fonts.getName(), type);
                 }
            }
        } catch (Exception e) {

        }

        //        fzgl = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/fzgl.ttf");	//古隶
        //        fzxz = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/fzxz.ttf");	//小篆
        //        hwxk = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/hwxk.ttf");	//楷体
        //        wryh = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/wryh.ttf");	//黑体
        //        hwxs = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/hwxs.ttf");	//宋体
        //        mzd =  Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/mzd.ttf"); //毛体
        //        arial= Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/arial.ttf"); //Arial
        //        simyou= Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/simyou.ttf"); //幼圆
        //        fzzy = Typeface.createFromFile(ConstantValue.ROOT_DIR
        //                + "fonts/fzzy.ttf"); //方正准圆
    }

    /**
     * 根据字体名称返回对应的字体
     *
     * @param fontName 字体名称
     * @return 字体
     */
    public static Typeface createTypeface(String fontName) {
        Typeface type = Typeface.createFromFile("/system/"
                + "fonts/DroidSerif-Regular.ttf" );
        if (mTypefaces != null && mTypefaces.size() > 0) {
            Iterator iter = mTypefaces.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                Typeface val = (Typeface)entry.getValue();
                if(key.equals(fontName)){
                     return val;
                }
            }
        }else{
            return type;
        }
        return type;

//        if ("古隶".equals(fontName)) {
//            return fzgl;
//        } else if ("小篆".equals(fontName)) {
//            return fzxz;
//        } else if ("楷体".equals(fontName)) {
//            return hwxk;
//        } else if ("黑体".equals(fontName)) {
//            return wryh;
//        } else if ("宋体".equals(fontName)) {
//            return hwxs;
//        } else if ("毛体".equals(fontName)) {
//            return mzd;
//        } else if ("Arial".equals(fontName)) {
//            return arial;
//        } else if ("幼圆".equals(fontName)) {
//            return simyou;
//        } else if ("方正准圆简体".equals(fontName)) {
//            return fzzy;
//        }
//        return hwxs;
    }


}
