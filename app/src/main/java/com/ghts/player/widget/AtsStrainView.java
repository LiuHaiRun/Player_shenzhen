package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ghts.player.bean.StationViewBean;
import com.ghts.player.data.ATSStrain;
import com.ghts.player.data.ATSStrainModel;
import com.ghts.player.data.StrainBean;
import com.ghts.player.data.SystemBean;
import com.ghts.player.data.TrainItem;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.TypeFaceFactory;
import com.ghts.player.utils.VeDate;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-9-5.
 * 车载ATS
 */
public class AtsStrainView extends BaseModuleInfo {
    private Context mContext;
    private SystemBean systemBean;
    private ArrayList<MAtsStrain> atsViewList;
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
            if (systemBean.getStrainNextStation() != null) {
                TrainItem trainItem = systemBean.getStrainNextStation();
                setTrainItem(trainItem);
            }
            if (systemBean.getStrainDstStation() != null) {
                TrainItem trainItem = systemBean.getStrainDstStation();
                setTrainItem(trainItem);
            }

            if (systemBean.getStrainCurStation() != null) {
                TrainItem trainItem = systemBean.getStrainCurStation();
                setTrainItem(trainItem);
            }
            if (systemBean.getStrainStartStation() != null) {
                TrainItem trainItem = systemBean.getStrainStartStation();
                setTrainItem(trainItem);
            }
            if (systemBean.getStrianTrainStatus() != null) {
                TrainItem trainItem = systemBean.getStrianTrainStatus();
                setTrainItem(trainItem);
            }
        }
        return view;
    }

    public void setTrainItem(TrainItem timeData) {
        if (timeData != null) {
            StationViewBean titleView = timeData.getTitleView();
            StationViewBean infoView = timeData.getInfoView();
            StationViewBean titleEnView = timeData.getTitleEnView();
            StationViewBean infoEnView = timeData.getInfoEnView();
            setView(titleView, true);
            setView(infoView, true);
            setView(titleEnView, true);
            setView(infoEnView, true);
        }
    }

    public void setView(StationViewBean viewBean, boolean isAdd) {
        AlignMode alignMode = viewBean.getAlignMode();
        FontParam fontParam = viewBean.getFontParam();
        POS rect = viewBean.getRectPos();
        String txt = viewBean.getTxt();
        MAtsStrain atsView = new MAtsStrain(mContext, fontParam.getSize());
        atsView.setIncludeFontPadding(false);
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
    }

    /**
     * 更新ATS信息-----------杭州最新版本------------------
     * <ATS>
     * <StartStation NameCh="湘湖" NameEn="Xianghu" TipCh=" " TipEn=" " />
     * <CurStation NameCh="西兴" NameEn="Xixing" TipCh=" " TipEn=" " />
     * <NextStation NameCh=" " NameEn=" " TipCh="下一站" TipEn="Next Station" />
     * <DestStation NameCh=" " NameEn=" " TipCh=" " TipEn=" " />
     * <Status NameCh="已到站" NameEn="Train Arrive" TipCh=" " TipEn=" " />
     * </ATS>
     */
    public void receiveControlCmd(ATSStrain bean) {
        if (bean != null) {
            ATSStrainModel nextModel = bean.getNextStation();
            ATSStrainModel desModel = bean.getDestStation();
            ATSStrainModel startModel = bean.getStartStation();
            ATSStrainModel curModel = bean.getCurStation();
            ATSStrainModel statusModel = bean.getStatus();

            if (atsViewList != null && atsViewList.size() > 0) {
                for (int j = 0; j < atsViewList.size(); j++) {
                    MAtsStrain atsView = atsViewList.get(j);
                    String id = atsView.getIds();
                    if (id.equals("NextStationInfo")) {
                        if (!TextUtils.isEmpty(nextModel.getNameCh())) {
                            atsView.setText(nextModel.getNameCh());
                        }
                    } else if (id.equals("NextStationInfoEn")) {
                        if (!TextUtils.isEmpty(nextModel.getNameEn())) {
                            atsView.setText(nextModel.getNameEn());
                        }
                    } else if (id.equals("NextStationTitle")) {
                        if (!TextUtils.isEmpty(nextModel.getTipCh())) {
                            atsView.setText(nextModel.getTipCh());
                        }
                    } else if (id.equals("NextStationTitleEn")) {
                        if (!TextUtils.isEmpty(nextModel.getTipEn())) {
                            atsView.setText(nextModel.getTipEn());
                        }
                    } else if (id.equals("DstStationInfo")) {
                        if (!TextUtils.isEmpty(desModel.getNameCh())) {
                            atsView.setText(desModel.getNameCh());
                        }
                    } else if (id.equals("DstStationInfoEn")) {
                        if (!TextUtils.isEmpty(desModel.getNameEn())) {
                            atsView.setText(desModel.getNameEn());
                        }
                    } else if (id.equals("DstStationTitle")) {
                        if (!TextUtils.isEmpty(desModel.getTipCh())) {
                            atsView.setText(desModel.getTipCh());
                        }
                    } else if (id.equals("DstStationTitleEn")) {
                        if (!TextUtils.isEmpty(desModel.getTipEn())) {
                            atsView.setText(desModel.getTipEn());
                        }
                    } else if (id.equals("StartStationInfo")) {
                        if (!TextUtils.isEmpty(startModel.getNameCh())) {
                            atsView.setText(startModel.getNameCh());
                        }
                    } else if (id.equals("StartStationInfoEn")) {
                        if (!TextUtils.isEmpty(startModel.getNameEn())) {
                            atsView.setText(startModel.getNameEn());
                        }
                    } else if (id.equals("StartStationTitle")) {
                        if (!TextUtils.isEmpty(startModel.getTipCh())) {
                            atsView.setText(startModel.getTipCh());
                        }
                    } else if (id.equals("StartStationTitleEn")) {
                        if (!TextUtils.isEmpty(startModel.getTipEn())) {
                            atsView.setText(startModel.getTipEn());
                        }
                    } else if (id.equals("CurStationInfo")) {
                        if (!TextUtils.isEmpty(curModel.getNameCh())) {
                            atsView.setText(curModel.getNameCh());
                        }
                    } else if (id.equals("CurStationInfoEn")) {
                        if (!TextUtils.isEmpty(curModel.getNameEn())) {
                            atsView.setText(curModel.getNameEn());
                        }
                    } else if (id.equals("CurStationTitle")) {
                        if (!TextUtils.isEmpty(curModel.getTipCh())) {
                            atsView.setText(curModel.getTipCh());
                        }
                    } else if (id.equals("CurStationTitleEn")) {
                        if (!TextUtils.isEmpty(curModel.getTipEn())) {
                            atsView.setText(curModel.getTipEn());
                        }
                    } else if (id.equals("TrainStatusInfo")) {
                        if (!TextUtils.isEmpty(statusModel.getNameCh())) {
                            atsView.setText(statusModel.getNameCh());
                        }
                    } else if (id.equals("TrainStatusInfoEn")) {
                        if (!TextUtils.isEmpty(statusModel.getNameEn())) {
                            atsView.setText(statusModel.getNameEn());
                        }
                    } else if (id.equals("TrainStatusTitle")) {
                        if (!TextUtils.isEmpty(statusModel.getTipCh())) {
                            atsView.setText(statusModel.getTipCh());
                        }
                    } else if (id.equals("TrainStatusTitleEn")) {
                        if (!TextUtils.isEmpty(statusModel.getTipEn())) {
                            atsView.setText(statusModel.getTipEn());
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新ATS信息-------------南京既有版本-------------------------
     */
     public void receiveControlCmd(StrainBean bean, byte type) {
        LogUtil.e("--ATS--", bean.toString());

        if (bean != null) {
            if (atsViewList != null && atsViewList.size() > 0) {
                for (int j = 0; j < atsViewList.size(); j++) {
                    MAtsStrain atsView = atsViewList.get(j);
                    String id = atsView.getIds();
                    if (id.equals("NextStationInfo")) {
                        if (!TextUtils.isEmpty(bean.getNextStationName())) {
                            atsView.setText(bean.getNextStationName());
                        }
                    } else if (id.equals("NextStationInfoEn")) {
                        if (!TextUtils.isEmpty(bean.getNextStationEnName())) {
                            atsView.setText(bean.getNextStationEnName());
                        }
                    } else if (id.equals("DstStationInfo")) {
                        if (!TextUtils.isEmpty(bean.getDstStationName())) {
                            atsView.setText(bean.getDstStationName());
                        }
                    } else if (id.equals("DstStationInfoEn")) {
                        if (!TextUtils.isEmpty(bean.getDstStationEnName())) {
                            atsView.setText(bean.getDstStationEnName());
                        }
                    } else if (id.equals("NextStationTitle")) {
                        if (!TextUtils.isEmpty(bean.getNextStationTitle())) {
                            atsView.setText(bean.getNextStationTitle());
                        }
                    } else if (id.equals("NextStationTitleEn")) {
                        if (!TextUtils.isEmpty(bean.getNextStationEnTitle())) {
                            atsView.setText(bean.getNextStationEnTitle());
                        }
                    } else if (id.equals("DstStationTitle")) {
                        if (!TextUtils.isEmpty(bean.getDstStationTitle())) {
                            atsView.setText(bean.getDstStationTitle());
                        }
                    } else if (id.equals("DstStationTitleEn")) {
                        if (!TextUtils.isEmpty(bean.getDstStationEnTitle())) {
                            atsView.setText(bean.getDstStationEnTitle());
                        }
                    }
                }
            }
            if(Const.moduleMap.containsKey("atstrain0")){
                Const.moduleMap.put("atstrain0","(" + VeDate.getStringDate()+") "+bean.toString());
            }
        }
    }

    /**
     * 十分钟接收不到车载信息，清空ATS信息
     */
    public void clearControlCmd() {
        if (atsViewList != null && atsViewList.size() > 0) {
            for (int i = 0; i < atsViewList.size(); i++) {
                MAtsStrain strainView = atsViewList.get(i);
                strainView.setText("");
            }
        }
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

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public SystemBean getSystemBean() {
        return systemBean;
    }

    public void setSystemBean(SystemBean systemBean) {
        this.systemBean = systemBean;
    }

}
