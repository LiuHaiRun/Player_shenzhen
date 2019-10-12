package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ghts.player.bean.InfoBean;
import com.ghts.player.bean.StationViewBean;
import com.ghts.player.bean.TrainInfoBean;
import com.ghts.player.data.TrainInfoData;
import com.ghts.player.data.TrainInfoItem;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.TypeFaceFactory;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TrainInfoView extends BaseModuleInfo {
    private Context mContext;
    private TrainInfoBean trainInfoBean;
    private ArrayList<MAtsStrain> atsViewList;
    FrameLayout view = null;
    String infoData="";
    private InfoBean infoBean;
    private InfoBean.TrainFLPageBean.TrainFLInfoBean trainFLInfoBean;
    private InfoBean.TrainFLPageBean.TrainFLInfoBean trainFLInfoBean1;
    private int i=0;
    private List<InfoBean.TrainFLPageBean.TrainFLInfoBean> trainFLInfo;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startUpData();
        }
    };

    @Override
    public View getView(Context context) {
        this.mContext = context;
        if (trainInfoBean!=null){
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view = new FrameLayout(mContext);
            view.setLayoutParams(lp);//设置布局参数
            initBackground(view, trainInfoBean.getBgParam());
            initPosition(view, trainInfoBean.getPos());
            atsViewList = new ArrayList<MAtsStrain>();
            atsViewList.clear();
            startUpData();
            if (trainInfoBean.getUp()!=null){
                TrainInfoData up = trainInfoBean.getUp();
                setTrainInfoData(up);
            }
            if (trainInfoBean.getDown()!=null){
                TrainInfoData down = trainInfoBean.getDown();
                setTrainInfoData(down);
            }
        }
            return view;
    }

    public void setTrainInfoData(TrainInfoData trainInfoData) {
        TrainInfoItem firstTrain = trainInfoData.getFirstTrain();
        TrainInfoItem lastTrain = trainInfoData.getLastTrain();
        TrainInfoItem terminate = trainInfoData.getTerminate();
        setTrainItem(terminate);
        setTrainItem(firstTrain);
        setTrainItem(lastTrain);
    }

    public void setTrainItem(TrainInfoItem timeData) {
        if (timeData != null) {
            StationViewBean titleView = timeData.getTitleView();
            StationViewBean titleEnView = timeData.getTitleEnView();
            StationViewBean destView = timeData.getDestView();
            StationViewBean destEnView = timeData.getDestEnView(); 
            setView(titleView, true);
            setView(titleEnView, true);
            setView(destView, true);
            setView(destEnView, true);
        }
    }

    public void setView(StationViewBean viewBean, boolean isAdd) {
        AlignMode alignMode = viewBean.getAlignMode();
        FontParam fontParam = viewBean.getFontParam();
        POS rect = viewBean.getRectPos();
        String txt = viewBean.getTxt();
        MAtsStrain atsView = new MAtsStrain(mContext, fontParam.getSize());
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
            }
        }
        int size = atsViewList.size();
        Log.i("atsViewList",size+"");
    }
    void initFont(TextView tv, FontParam font)  {
        tv.setTextSize(font.getSize());
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);
    }
    public InfoBean initDate(){
            String Path="/sata/config/traininfotext.json";
            BufferedReader reader = null;
            String laststr = "";
            try {
                FileInputStream fileInputStream = new FileInputStream(Path);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
                reader = new BufferedReader(inputStreamReader);
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    laststr += tempString;
                }
                Log.i("fileInputStream",laststr);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        Gson gson = new Gson();
        InfoBean infoBean = gson.fromJson(laststr, InfoBean.class);
        return infoBean;
    }

    public void startUpData(){
        Gson gson = new Gson();
        infoBean = gson.fromJson(infoData, InfoBean.class);
        if (infoBean!=null){
            trainFLInfo = infoBean.getTrainFLPage().get(0).getTrainFLInfo();
            int size = trainFLInfo.size();
            LogUtil.e("atsViewid","集合长度"+size);
            trainFLInfoBean = trainFLInfo.get(i%size);
            i++;
            if (atsViewList!=null&& atsViewList.size() > 0){
                for (int j = 0; j < atsViewList.size(); j++) {
                    MAtsStrain atsView = atsViewList.get(j);
                    String id = atsView.getIds();
                    Log.i("atsViewid",id);
//                    if (size>1){
//                        trainFLInfoBean = trainFLInfo.get(0);
//                        trainFLInfoBean1 = trainFLInfo.get(1);
//                    }else{
//                        trainFLInfoBean = trainFLInfo.get(0);
//                    }
                    if (id.equals("UpTerminateTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getTipCh())) {
                            atsView.setText(trainFLInfoBean.getTipCh());
                        }
                    }else if (id.equals("UpTerminateEnTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getDestCh())) {
                            atsView.setText(trainFLInfoBean.getTipEn());
                        }
                    }else if (id.equals("UpTerminateDest")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getDestCh())) {
                            TextPaint paint = atsView.getPaint();
                            paint.setFakeBoldText(true);
                            atsView.setText(trainFLInfoBean.getDestCh());
                        }
                    }else if (id.equals("UpTerminateEnDest")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getDestCh())) {
                            TextPaint paint = atsView.getPaint();
                            paint.setFakeBoldText(true);
                            atsView.setText(trainFLInfoBean.getDestEn());
                        }
                    }else if (id.equals("UpFirstTrainTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getFristCh())) {
                            atsView.setText(trainFLInfoBean.getFristCh());
                        }
                    }else if (id.equals("UpFirstTrainEnTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getFristCh())) {
                            atsView.setText(trainFLInfoBean.getFristEn());
                        }
                    }else if (id.equals("UpFirstTrainTime")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getFirstTime())) {
                            String firstTime = trainFLInfoBean.getFirstTime();
                            String ft=firstTime.substring(0,5);
                            TextPaint paint = atsView.getPaint();
                            paint.setFakeBoldText(true);
                            atsView.setText(ft);
                        }
                    }else if (id.equals("UpLastTrainTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getLastCh())) {
                            atsView.setText(trainFLInfoBean.getLastCh());
                        }
                    }else if (id.equals("UpLastTrainEnTitle")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getLastCh())) {
                            atsView.setText(trainFLInfoBean.getLastEn());
                        }
                    }else if (id.equals("UpLastTrainTime")){
                        if (!TextUtils.isEmpty(trainFLInfoBean.getLastTime())) {
                            String lastTime = trainFLInfoBean.getLastTime();
                            String lt=lastTime.substring(0,5);
                            TextPaint paint = atsView.getPaint();
                            paint.setFakeBoldText(true);
                            atsView.setText(lt);
                        }
//                        }else if (id.equals("DownTerminateTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getTipCh())) {
//                                atsView.setText(trainFLInfoBean1.getTipCh());
//                            }
//                        }else if (id.equals("DownTerminateEnTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getTipCh())) {
//                                atsView.setText(trainFLInfoBean1.getTipEn());
//                            }
//                        }else if (id.equals("DownTerminateDest")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getDestCh())) {
//                                atsView.setText(trainFLInfoBean1.getDestCh());
//                            }
//                        }else if (id.equals("DownTerminateEnDest")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getDestCh())) {
//                                atsView.setText(trainFLInfoBean1.getDestEn());
//                            }
//                        }else if (id.equals("DownFirstTrainTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getFristCh())) {
//                                atsView.setText(trainFLInfoBean1.getFristCh());
//                            }
//                        }else if (id.equals("DownFirstTrainEnTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getFristCh())) {
//                                atsView.setText(trainFLInfoBean1.getFristEn());
//                            }
//                        }else if (id.equals("DownFirstTrainTime")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getFirstTime())) {
//                                atsView.setText(trainFLInfoBean1.getFirstTime());
//                            }
//                        }else if (id.equals("DownLastTrainTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getLastCh())) {
//                                atsView.setText(trainFLInfoBean1.getLastCh());
//                            }
//                        }else if (id.equals("DownLastTrainEnTitle")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getLastCh())) {
//                                atsView.setText(trainFLInfoBean1.getLastEn());
//                            }
//                        }else if (id.equals("DownLastTrainTime")){
//                            if (!TextUtils.isEmpty(trainFLInfoBean1.getLastTime())) {
//                                atsView.setText(trainFLInfoBean1.getLastTime());
//                            }
                    }
                }
            }
        }
        handler.sendEmptyMessageDelayed(0,7000);
    }

    public void upDataTrainInfo(String s){
        Log.i("atsViewsize",atsViewList.size()+"");
        if (s.equals(infoData)){
            Log.e("infoData","1");
        }else {
            infoData=s;
            Log.e("infoData","0");
            i=0;
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                    upDataTrainInfo(infoData);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

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

    public TrainInfoBean getTrainInfoBean() {
        return trainInfoBean;
    }

    public void setTrainInfoBean(TrainInfoBean trainInfoBean) {
        this.trainInfoBean = trainInfoBean;
    }

}
