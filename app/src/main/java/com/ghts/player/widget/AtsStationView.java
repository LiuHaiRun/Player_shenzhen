//package com.ghts.player.widget;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.GradientDrawable;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.text.style.RelativeSizeSpan;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.ghts.player.bean.StationViewBean;
//import com.ghts.player.data.StationBean;
//import com.ghts.player.data.SystemBean;
//import com.ghts.player.data.TrainData;
//import com.ghts.player.data.TrainItem;
//import com.ghts.player.enumType.AlignMode;
//import com.ghts.player.enumType.BGParam;
//import com.ghts.player.enumType.FontParam;
//import com.ghts.player.enumType.GradientColorType;
//import com.ghts.player.enumType.POS;
//import com.ghts.player.enumType.RGBA;
//import com.ghts.player.utils.PubUtil;
//import com.ghts.player.utils.TypeFaceFactory;
//
//import java.util.ArrayList;
//
///**
// * Created by lijingjing on 17-9-8.
// */
//public class AtsStationView extends BaseModuleInfo {
//    private Context mContext;
//    private SystemBean systemBean;
//    private ArrayList<MAtsStrain> atsViewList;
//    FrameLayout view = null;
//
//    @Override
//    public View getView(Context context) {
//        this.mContext = context;
//        if (systemBean != null) {
//            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            view = new FrameLayout(mContext);
//            view.setLayoutParams(lp);//设置布局参数
//
//            initBackground(view, systemBean.getBgParam());
//            initPosition(view, systemBean.getPos());
//            atsViewList = new ArrayList<MAtsStrain>();
//            atsViewList.clear();
//            if (systemBean.getTrain0() != null) {
//                TrainData trainData = systemBean.getTrain0();
//                setTrainData(trainData);
//            }
//            if (systemBean.getTrain1() != null) {
//                TrainData trainData = systemBean.getTrain1();
//                setTrainData(trainData);
//            }
//            if (systemBean.getTrain2() != null) {
//                TrainData trainData = systemBean.getTrain2();
//                setTrainData(trainData);
//            }
//            if (systemBean.getTrain3() != null) {
//                TrainData trainData = systemBean.getTrain3();
//                setTrainData(trainData);
//            }
//        }
//        return view;
//    }
//
//    public void setTrainData(TrainData trainData) {
//        TrainItem timeData = trainData.getTimeData();
//        TrainItem nextData = trainData.getNextData();
//        TrainItem dstData = trainData.getDstData();
//        setTrainItem(timeData);
//        setTrainItem(nextData);
//        setTrainItem(dstData);
//    }
//
//    public void setTrainItem(TrainItem timeData) {
//        if (timeData != null) {
//            StationViewBean titleView = timeData.getTitleView();
//            StationViewBean infoView = timeData.getInfoView();
//            StationViewBean titleEnView = timeData.getTitleEnView();
//            StationViewBean infoEnView = timeData.getInfoEnView();
//            setView(titleView, false);
//            setView(infoView, true);
//            setView(titleEnView, false);
//            setView(infoEnView, true);
//        }
//    }
//
//    public void setView(StationViewBean viewBean, boolean isAdd) {
//        AlignMode alignMode = viewBean.getAlignMode();
//        FontParam fontParam = viewBean.getFontParam();
//        POS rect = viewBean.getRectPos();
//        String txt = viewBean.getTxt();
//
//        MAtsStrain atsView = new MAtsStrain(mContext,fontParam.getSize());
//        atsView.setIds(viewBean.getId());
//        atsView.setmAtsStrain(atsView);
//        atsView.setInfo(txt);
//        initFont(atsView, fontParam);
//        initPosition(atsView, rect);
//        setGravity(alignMode, atsView);
//
//        if (viewBean.isShow()) {
//            view.addView(atsView);
//            if (isAdd) {
//                atsViewList.add(atsView);
//            }
//        }
//    }
//
//    /**
//     * 更新ATS信息
//     * @param bean
//     */
//    public void receiveControlCmd(ArrayList<StationBean> bean,byte type) {
//        //        LogUtil.e("--ATS--"+ Const.atsId, bean.toString());
//        if (bean != null && bean.size() > 0) {
//            for (int i = 0; i < bean.size(); i++) {
//                StationBean stationBean = bean.get(i);
//                String time = stationBean.getTime();
//                String dst = stationBean.getDst();
//                String enTime = stationBean.getEnTime();
//                String enDst = stationBean.getEnDst();
//                int showtime = 0;
//                boolean isNum =PubUtil.isNumeric(time);
//                if (atsViewList != null && atsViewList.size() > 0) {
//                    for (int j = 0; j < atsViewList.size(); j++) {
//                        MAtsStrain atsView = atsViewList.get(j);
//                        String id = atsView.getIds();
//                        String timeV = "Train" + i + "TimeInfo";
//                        if (id.equals(timeV)) {
//                            if(isNum){
//                                if (time != null && !time.equals("")) {
//                                    showtime = PubUtil.parseInt(time);
//                                }
//                                if (showtime > 0) {
//                                    //atsView.setText(showtime + " 分钟");
//                                    String ss = showtime + "分钟";
//                                    String aa = showtime+"";
//                                    String bb = "分钟";
//                                    SpannableString spannableString = new SpannableString(ss);
//                                    RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1.2f);
//                                    RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(1.0f);
//                                    spannableString.setSpan(sizeSpan01, 0, aa.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                    spannableString.setSpan(sizeSpan02, aa.length()+1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                    atsView.setText(spannableString);
//                                } else {
//                                    atsView.setText("");
//                                }
//                            }else{
//                                String aa = PubUtil.getNum(time);
//                                SpannableString spannableString = new SpannableString(time);
//                                RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1.2f);
//                                RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(1.0f);
//                                spannableString.setSpan(sizeSpan01, 0, aa.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                spannableString.setSpan(sizeSpan02, aa.length()+1, time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                atsView.setText(spannableString);
//                            }
//                        }
//
//                        String dstV = "Train" + i + "DstInfo";
//                        if (id.equals(dstV) && !TextUtils.isEmpty(dst)) {
//                            atsView.setText(dst);
//                        }
//                        String timeEn = "Train" + i + "TimeInfoEn";
//                        if (id.startsWith(timeEn) && !TextUtils.isEmpty(enTime)) {
//                            atsView.setText(enTime);
//                        }
//                        String info = "Train" + i + "TimeInfoTitleEn";
//                        if (id.startsWith(info)&& !TextUtils.isEmpty(enDst)) {
//                            atsView.setText(enDst);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 十分钟接收不到车站信息，清空ATS信息
//     */
//    public void clearControlCmd() {
//        if (atsViewList != null && atsViewList.size() > 0) {
//            for (int i = 0; i < atsViewList.size(); i++) {
//                MAtsStrain strainView = atsViewList.get(i);
//                strainView.setText("");
//            }
//        }
//    }
//
//    void initFont(TextView tv, FontParam font) {
//        tv.setTextSize(font.getSize());
//        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
//        tv.setTypeface(typeface);
//        RGBA rgba = font.getFaceColor();
//        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
//                rgba.getBlue());
//        tv.setTextColor(color);
//    }
//
//    void initBackground(View view, BGParam bg) {
//        switch (bg.getType()) {
//            case NOTSHOW: // 背景不显示
//                break;
//            case GRADIENTCOLOR: // 背景类型为渐变色
//                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
//                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
//                            bg.getGradientcolor1().getBlue());
//                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
//                            bg.getGradientcolor2().getBlue());
//                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
//                    view.setBackgroundDrawable(gradientDrawable);
//                } else {
//                    // 垂直渐变
//                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
//                            bg.getGradientcolor1().getBlue());
//                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
//                            bg.getGradientcolor2().getBlue());
//                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
//                    view.setBackgroundDrawable(gradientDrawable);
//                }
//                break;
//            case PICTURE: // 背景类型为图片
//                // 本地图片文件路径生成drawable
//                Bitmap bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
//                Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//                // 设置图片背景
//                view.setBackgroundDrawable(drawable);
//                break;
//            case PURECOLOR: // 背景类型为纯色
//                RGBA purecolor = bg.getPurecolor();
//                view.setBackgroundColor(Color.argb(purecolor.getAlpha(),
//                        purecolor.getRed(), purecolor.getGreen(),
//                        purecolor.getBlue()));
//                break;
//            default: // 其他
//                break;
//        }
//    }
//
//    void initPosition(View view, POS pos) {
//        view.setX(pos.getLeft());
//        view.setY(pos.getTop());
//        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
//    }
//
//    void setGravity(AlignMode align, TextView mText) {
//        switch (align) {
//            case MIDDLELEFT:
//                mText.setGravity(Gravity.LEFT | Gravity.CENTER);
//                break;
//            case CENTER:
//                mText.setGravity(Gravity.CENTER);
//                break;
//            case MIDDLERIGHT:
//                mText.setGravity(Gravity.RIGHT | Gravity.CENTER);
//                break;
//            default:
//                break;
//        }
//    }
//
//
//    public Context getmContext() {
//        return mContext;
//    }
//
//    @Override
//    public int getModule_type() {
//        return module_type;
//    }
//
//    public SystemBean getSystemBean() {
//        return systemBean;
//    }
//
//    public void setSystemBean(SystemBean systemBean) {
//        this.systemBean = systemBean;
//    }
//}



package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ghts.player.bean.StationViewBean;
import com.ghts.player.data.StationBean;
import com.ghts.player.data.SystemBean;
import com.ghts.player.data.TrainData;
import com.ghts.player.data.TrainItem;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.MTypefaceSpan;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.TypeFaceFactory;
import com.ghts.player.utils.VeDate;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-9-8.
 */
public class AtsStationView extends BaseModuleInfo {
    private Context mContext;
    private SystemBean systemBean;
    private ArrayList<MAtsStrain> atsViewList;
    private ArrayList<MAtsStrain> titleViewList;
    FrameLayout view = null;

    @Override
    public View getView(Context context) {
        this.mContext = context;
        if (systemBean != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view = new FrameLayout(mContext);
            view.setLayoutParams(lp);//设置布局参数

            initBackground(view, systemBean.getBgParam());
            initPosition(view, systemBean.getPos());
            atsViewList = new ArrayList<MAtsStrain>();
            atsViewList.clear();

            titleViewList = new ArrayList<MAtsStrain>();
            titleViewList.clear();

            if (systemBean.getTrain0() != null) {
                TrainData trainData = systemBean.getTrain0();
                setTrainData(trainData);
            }
            if (systemBean.getTrain1() != null) {
                TrainData trainData = systemBean.getTrain1();
                setTrainData(trainData);
            }
            if (systemBean.getTrain2() != null) {
                TrainData trainData = systemBean.getTrain2();
                setTrainData(trainData);
            }
            if (systemBean.getTrain3() != null) {
                TrainData trainData = systemBean.getTrain3();
                setTrainData(trainData);
            }

            if (systemBean.getCurStation() != null) {
                TrainItem trainItem = systemBean.getCurStation();
                setTrainItemCur(trainItem);
            }
        }
        return view;
    }

    //当前站设置
    public void setTrainItemCur(TrainItem timeData) {
        if (timeData != null) {
            StationViewBean titleView = timeData.getTitleView();
            StationViewBean infoView = timeData.getInfoView();
            StationViewBean titleEnView = timeData.getTitleEnView();
            StationViewBean infoEnView = timeData.getInfoEnView();
            boolean showTitleEn = titleEnView.isShow();
            boolean showTitle = titleView.isShow();
            boolean showInfoEn = infoEnView.isShow();
            boolean showInfo = infoView.isShow();
            if (showTitleEn || showTitle) {
                setViewCur(titleView, false);
                setViewCur(titleEnView, false);
            }
            if (showInfo || showInfoEn) {
                setViewCur(infoView, true);
                setViewCur(infoEnView, true);
            }
        }
    }

    public void setViewCur(StationViewBean viewBean, boolean isAdd) {
        AlignMode alignMode = viewBean.getAlignMode();
        FontParam fontParam = viewBean.getFontParam();
        FontParam FontNum = viewBean.getFontNum();
        FontParam FontTrans = viewBean.getFontTrans();
        FontParam FontTransEn = viewBean.getFontTransEn();

        POS rect = viewBean.getRectPos();
        String txt = viewBean.getTxt();
        MAtsStrain atsView = new MAtsStrain(mContext, fontParam.getSize(),fontParam,FontNum,FontNum,
                FontTransEn);
        atsView.setFont(fontParam);
        atsView.setFontNum(FontNum);
        atsView.setFontTrans(FontTrans);
        atsView.setFontTransEn(FontTransEn);
        atsView.setIds(viewBean.getId());
        atsView.setmAtsStrain(atsView);

        if (viewBean.getId().startsWith("CurStationTitle")) {
            atsView.setInfo(txt);
        } else if (viewBean.getId().equals("CurStationInfo") && !viewBean.getId().equals("CurStationInfoEn")) {
            atsView.setInfo(Const.globalBean.getStationName());
        } else if (viewBean.getId().equals("CurStationInfoEn")) {
            atsView.setInfo(Const.globalBean.getStationEnName());
        }
        initFont(atsView, fontParam);
        initPosition(atsView, rect);
        setGravity(alignMode, atsView);
        if (viewBean.isShow()) {
            view.addView(atsView);
            if (isAdd) {
                atsViewList.add(atsView);
            } else {
                titleViewList.add(atsView);
            }
        } else {
            view.addView(atsView);
            atsView.setVisibility(View.INVISIBLE);
            if (isAdd) {
                atsViewList.add(atsView);
            } else {
                titleViewList.add(atsView);
            }
        }
    }

    public void setTrainData(TrainData trainData) {
        TrainItem timeData = trainData.getTimeData();
        TrainItem nextData = trainData.getNextData();
        TrainItem dstData = trainData.getDstData();
        FontParam fontNum = trainData.getFontNum();
        FontParam fontTrans = trainData.getFontTrans();
        FontParam fontTransEn = trainData.getFontTransEn();
        setTrainItem(timeData,fontNum,fontTrans,fontTransEn);
        setTrainItem(nextData,fontNum,fontTrans,fontTransEn);
        setTrainItem(dstData,fontNum,fontTrans,fontTransEn);
    }

    public void setTrainItem(TrainItem timeData,FontParam fontNum,FontParam fontTrans,FontParam fontTransEn) {
        if (timeData != null) {
            StationViewBean titleView = timeData.getTitleView();
            StationViewBean infoView = timeData.getInfoView();
            StationViewBean titleEnView = timeData.getTitleEnView();
            StationViewBean infoEnView = timeData.getInfoEnView();
            boolean showTitleEn = titleEnView.isShow();
            boolean showTitle = titleView.isShow();
            boolean showInfoEn = infoEnView.isShow();
            boolean showInfo = infoView.isShow();
            if (showTitleEn || showTitle) {
                setView(titleView,fontNum,fontTrans,fontTransEn, false);
                setView(titleEnView,fontNum,fontTrans,fontTransEn, false);
            }
            if (showInfo || showInfoEn) {
                setView(infoView,fontNum,fontTrans,fontTransEn, true);
                setView(infoEnView,fontNum,fontTrans,fontTransEn, true);
            }
        }
    }
    public void setView(StationViewBean viewBean,FontParam fontNum,FontParam fontTrans,FontParam fontTransEn, boolean isAdd) {
        AlignMode alignMode = viewBean.getAlignMode();
        FontParam fontParam = viewBean.getFontParam();
        POS rect = viewBean.getRectPos();
        String txt = viewBean.getTxt();
        MAtsStrain atsView = new MAtsStrain(mContext, fontParam.getSize());
        atsView.setIncludeFontPadding(false);
        atsView.setFont(fontParam);
        atsView.setFontNum(fontNum);
        atsView.setFontTrans(fontTrans);
        atsView.setFontTransEn(fontTransEn);
        atsView.setIds(viewBean.getId());
        atsView.setmAtsStrain(atsView);
        atsView.setInfo(txt);
        initFont(atsView, fontParam);
        initPosition(atsView, rect);
        setGravity(alignMode, atsView);

        if (viewBean.isShow()) {
            view.addView(atsView);
             if (isAdd) {
                atsViewList.add(atsView);
            } else {
                titleViewList.add(atsView);
            }
        } else {
            view.addView(atsView);
            atsView.setVisibility(View.INVISIBLE);
            if (isAdd) {
                atsViewList.add(atsView);
            } else {
                titleViewList.add(atsView);
            }
        }
    }

//    public void setView(StationViewBean viewBean, boolean isAdd) {
//        AlignMode alignMode = viewBean.getAlignMode();
//        FontParam fontParam = viewBean.getFontParam();
//        POS rect = viewBean.getRectPos();
//        String txt = viewBean.getTxt();
//        MAtsStrain atsView = new MAtsStrain(mContext, fontParam.getSize());
//        atsView.setIncludeFontPadding(false);
//        atsView.setFont(fontParam);
//        atsView.setFontNum(viewBean.getFontNum());
//        atsView.setFontTrans(viewBean.getFontTrans());
//        atsView.setFontTransEn(viewBean.getFontTransEn());
//        atsView.setIds(viewBean.getId());
//        atsView.setmAtsStrain(atsView);
//        atsView.setInfo(txt);
//        initFont(atsView, fontParam);
//        initPosition(atsView, rect);
//        setGravity(alignMode, atsView);
//
//        if (viewBean.isShow()) {
//            view.addView(atsView);
//            //  atsView.setVisibility(View.INVISIBLE);
//            if (isAdd) {
//                atsViewList.add(atsView);
//            } else {
//                titleViewList.add(atsView);
//            }
//        } else {
//            view.addView(atsView);
//            atsView.setVisibility(View.INVISIBLE);
//            if (isAdd) {
//                atsViewList.add(atsView);
//            } else {
//                titleViewList.add(atsView);
//            }
//        }
//    }

    /**
     * 更新ATS信息
     *
     * @param bean
     */
    StringBuffer data;
    public void receiveControlCmd(ArrayList<StationBean> bean, byte type) {
        LogUtil.e("--更新ATS--" + Const.atsId, bean.toString());
        data = new StringBuffer();
        data.setLength(0);
        String isshow = "";
        if (titleViewList != null && titleViewList.size() > 0) {
            for (int k = 0; k < titleViewList.size(); k++) {
                MAtsStrain atsView = titleViewList.get(k);
                String id = atsView.getIds();
                switch (type) {
                    case 0:
                        if (id.contains("Title") && !id.contains("TitleEn")) {
                            atsView.setVisibility(View.VISIBLE);
                        } else if (id.contains("TitleEn")) {
                            atsView.setVisibility(View.INVISIBLE);
                        }
                        isshow = "中文显示";
                        break;
                    case 1:
                        if (id.contains("Title") && !id.contains("TitleEn")) {
                            atsView.setVisibility(View.INVISIBLE);
                        } else if (id.contains("TitleEn")) {
                            atsView.setVisibility(View.VISIBLE);
                        }
                        isshow = "英文显示";
                        break;
                    case 2:
                        if (id.contains("Title") && !id.contains("TitleEn")) {
                            atsView.setVisibility(View.VISIBLE);
                        } else if (id.contains("TitleEn")) {
                            atsView.setVisibility(View.VISIBLE);
                        }
                        isshow = "中英文同显";
                        break;
                    default:
                        break;
                }
            }
        }
        if (bean != null && bean.size() > 0) {
            for (int i = 0; i < bean.size(); i++) {
                StationBean stationBean = bean.get(i);
                String time = stationBean.getTime();
                String dst = stationBean.getDst();
                String enTime = stationBean.getEnTime();
                String enDst = stationBean.getEnDst();
                if (i < 2) {
                    data.append("第" + (i + 1) + "列车：" + stationBean.toString());
                }
                int showtime = 0;
                int showtimeEn = 0;
                boolean isNum = PubUtil.isNumeric(time);
                boolean isNumEn = PubUtil.isNumeric(enTime);
                if (atsViewList != null && atsViewList.size() > 0) {
                    for (int j = 0; j < atsViewList.size(); j++) {
                        MAtsStrain atsView = atsViewList.get(j);
                        atsView.setIncludeFontPadding(false);
                        String id = atsView.getIds();
                        String timeV = "Train" + i + "TimeInfo";
                        String timeEn = "Train" + i + "TimeInfoEn";
                        String info = "Train" + i + "DstInfo";
                        String infoen = "Train" + i + "DstInfoEn";
                        if (id.equals(timeV) && !id.equals(timeEn)) {
                            if (!TextUtils.isEmpty(time)) {
                                if (isNum) {
                                    if (time != null && !time.equals("")) {
                                        showtime = PubUtil.parseInt(time);
                                    }
                                    if (showtime > 0) {
                                        String ss = showtime + " 分钟";
                                        String aa = showtime + "";
                                        ss = "分钟";
                                        SpannableString spanStr = new SpannableString(time + ss);
                                        // 日期字符串的大小，字体，颜色
                                        atsView.initSpanFont(spanStr, atsView.getFontNum(), 0, time.length());
                                        // 星期字符串的大小，字体，颜色
                                        atsView.initSpanFont(spanStr, atsView.getFont(),time.length(),
                                                time.length()+ss.length());
                                        atsView.setText(spanStr);
                                    } else {
                                        atsView.setText("");
                                    }
                                } else {
                                    SpannableString spanStr = new SpannableString(time);
                                    // 日期字符串的大小，字体，颜色
                                    atsView.initSpanFont(spanStr, atsView.getFontTrans(), 0, time.length());
                                    atsView.setText(spanStr);
                                }
                            } else {
                                atsView.setText("");
                            }
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (id.equals(timeEn) && !id.equals(timeV) && !TextUtils.isEmpty(enTime)) {
                            if (!TextUtils.isEmpty(enTime)) {
                                if (isNumEn) {
                                    if (enTime != null && !enTime.equals("")) {
                                        showtimeEn = PubUtil.parseInt(enTime);
                                    }
                                    if (showtimeEn > 0) {
                                        String ss = showtimeEn + " ";
                                        String aa = showtimeEn + " ";
                                        if (showtimeEn == 1) {
                                            ss = ss + "min";
                                            ss = "min";
                                        } else {
                                            ss = ss + "mins";
                                            ss = "mins";
                                        }
                                        SpannableString spanStr = new SpannableString(showtimeEn + ss);
                                        // 日期字符串的大小，字体，颜色
                                        atsView.initSpanFont(spanStr, atsView.getFontNum(), 0, enTime.length());
                                        // 星期字符串的大小，字体，颜色
                                        atsView.initSpanFont(spanStr, atsView.getFont(),enTime.length(),
                                                enTime.length()+ss.length());
                                        atsView.setText(spanStr);
                                    } else {
                                        atsView.setText("");
                                    }
                                } else {
                                    SpannableString spanStr = new SpannableString(timeEn);
                                    // 日期字符串的大小，字体，颜色
                                    atsView.initSpanFont(spanStr, atsView.getFontTransEn(), 0, timeEn.length());
                                    atsView.setText(spanStr);
                                }
                            } else {
                                atsView.setText("");
                            }
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (id.equals(info) && !id.equals(infoen) && !TextUtils.isEmpty(dst)) {
                            if (!TextUtils.isEmpty(dst)) {
                                atsView.setText(dst);
                            } else {
                                atsView.setText("");
                            }
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (id.equals(infoen) && !id.equals(info) && !TextUtils.isEmpty(enDst)) {
                            if (!TextUtils.isEmpty(enDst)) {
                                atsView.setText(enDst);
                            } else {
                                atsView.setText("");
                            }
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (id.equals("CurStationInfo") && !id.equals("CurStationInfoEn")) { // 本站信息
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (id.equals("CurStationInfoEn")) { // 本站信息
                            switch (type) {
                                case 0:
                                    atsView.setVisibility(View.INVISIBLE);
                                    break;
                                case 1:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    atsView.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                }
            }
            if (Const.moduleMap.containsKey("atsstation0")) {
                Const.moduleMap.put("atsstation0", "(" + VeDate.getStringDate() + ") " + isshow + ": " + data.toString());
            }
        }
    }

    /**
     * 十分钟接收不到车站信息，清空ATS信息
     */
    public void clearControlCmd() {
//        if (atsViewList != null && atsViewList.size() > 0) {
//            for (int i = 0; i < atsViewList.size(); i++) {
//                MAtsStrain strainView = atsViewList.get(i);
//                String id = strainView.getIds();
//                //清空时本站信息默认中文
//                if (id.equals("Train0TimeInfo") || id.equals("Train0TimeInfoEn") || id.equals("Train0DstInfo") || id.equals("Train0DstInfoEn")) {
//                    strainView.setText("");
//                }
//            }
//        }
    }

    void initFont(TextView tv, FontParam font) {
        tv.setTextSize(font.getSize());
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);
    }

    void initBackground(View view, BGParam bg) {
        switch (bg.getType()) {
            case NOTSHOW: // 背景不显示
                break;
            case GRADIENTCOLOR: // 背景类型为渐变色
                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
                            bg.getGradientcolor1().getBlue());
                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
                            bg.getGradientcolor2().getBlue());
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
                    view.setBackgroundDrawable(gradientDrawable);
                } else {
                    // 垂直渐变
                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
                            bg.getGradientcolor1().getBlue());
                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
                            bg.getGradientcolor2().getBlue());
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
                    view.setBackgroundDrawable(gradientDrawable);
                }
                break;
            case PICTURE: // 背景类型为图片
                // 本地图片文件路径生成drawable
                Bitmap bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
                Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                // 设置图片背景
                view.setBackgroundDrawable(drawable);
                break;
            case PURECOLOR: // 背景类型为纯色
                RGBA purecolor = bg.getPurecolor();
                view.setBackgroundColor(Color.argb(purecolor.getAlpha(),
                        purecolor.getRed(), purecolor.getGreen(),
                        purecolor.getBlue()));
                break;
            default: // 其他
                break;
        }
    }

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    void setGravity(AlignMode align, TextView mText) {
        switch (align) {
            case MIDDLELEFT:
                mText.setGravity(Gravity.LEFT | Gravity.CENTER);
                break;
            case CENTER:
                mText.setGravity(Gravity.CENTER);
                break;
            case MIDDLERIGHT:
                mText.setGravity(Gravity.RIGHT | Gravity.CENTER);
                break;
            default:
                break;
        }
    }

    public Context getmContext() {
        return mContext;
    }

    @Override
    public int getModule_type() {
        return module_type;
    }

    public SystemBean getSystemBean() {
        return systemBean;
    }

    public void setSystemBean(SystemBean systemBean) {
        this.systemBean = systemBean;
    }

}