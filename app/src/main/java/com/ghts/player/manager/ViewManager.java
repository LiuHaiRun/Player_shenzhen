package com.ghts.player.manager;

import android.app.Activity;
import android.view.ViewGroup;

import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.video.LocalVideoView;
import com.ghts.player.video.VideoSurfaceInfo;
import com.ghts.player.video.VlcVideo;
import com.ghts.player.widget.AdvertisementView;
import com.ghts.player.widget.AtsStationView;
import com.ghts.player.widget.AtsStrainView;
import com.ghts.player.widget.BaseModuleInfo;
import com.ghts.player.widget.DateView;
import com.ghts.player.widget.ExigentInfo;
import com.ghts.player.widget.ExigentInfo_Full;
import com.ghts.player.widget.MyImgView;
import com.ghts.player.widget.ScrollView;
import com.ghts.player.widget.TimeView;
import com.ghts.player.widget.TitleView;
import com.ghts.player.widget.TrainInfoView;
import com.ghts.player.widget.WeatherView;
import com.ghts.player.widget.WeekView;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by lijingjing on 17-7-27.
 */
public class ViewManager {
    private static final String TAG = "ViewManager";
    private Activity activity;
    public static LocalVideoView videoInfo = null;
//    public static MVideo lVideo = null;
    public static VideoSurfaceInfo surfaceInfo = null;
    public static VlcVideo lVideo = null;
    public static ViewGroup viewGroup = null;
    private DateView dateView = null;
    private TimeView timeView = null;
    private TitleView titleView = null;
    private MyImgView imgView = null;
    private ScrollView scrollInfo = null;
    private WeekView weekView = null;
    public static ExigentInfo exigentInfo = null;
    public static ExigentInfo_Full exigentInfo_full = null;
    private static ViewManager instance = null;
    public AtsStrainView mAtsStrain = null;
    public AtsStationView mAtsStationView = null;
    public AdvertisementView advertisementView = null;
    public TrainInfoView trainInfoView = null;
    public WeatherView weatherView;

    /**
     * 获取本对象的实例
     * @return 本对象的实例
     */
    public static ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    /**
     *
     */
    public void init(Activity activity) {
        this.activity = activity;
    }

    /**
     * 改变显示布局
     *
     * @param viewGroup      界面显示的控件容器
     * @param moduleInfoList 容器里的控件信息
     */
    public void changeContentView(ViewGroup viewGroup, ArrayList<BaseModuleInfo> moduleInfoList) {
        this.viewGroup = viewGroup;
        try {
            LogUtil.i(TAG, "changeContentView");
            if (moduleInfoList == null) return;
            ListIterator<BaseModuleInfo> iterator = moduleInfoList.listIterator();
            while (iterator.hasNext()) {
                BaseModuleInfo baseModuleInfo = iterator.next();
                int module_type = baseModuleInfo.getModule_type();
                switch (module_type) { // 根据模块类型，强转。并加入ViewGroup容器
                    case ConstantValue.MODULE_TYPE_TEXT: //文本
                        titleView = (TitleView) baseModuleInfo;
                        viewGroup.addView(titleView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_PIC: //静态图片
                        imgView = (MyImgView) baseModuleInfo;
                        viewGroup.addView(imgView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_DATE: //日期模块
                        dateView = (DateView) baseModuleInfo;
                        viewGroup.addView(dateView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_TIME: //时间模块
                        timeView = (TimeView) baseModuleInfo;
                        viewGroup.addView(timeView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_SCROLL://滚动模块
                        scrollInfo = (ScrollView) baseModuleInfo;
                        viewGroup.addView(scrollInfo.getView(activity));
                         break;
                    case ConstantValue.MODULE_TYPE_WEEK: // 星期模块
                        weekView = (WeekView)baseModuleInfo;
                        viewGroup.addView(weekView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_VIDEO: //视频
                        videoInfo = (LocalVideoView) baseModuleInfo;
                        surfaceInfo = changeLocalToLive(videoInfo);
                        lVideo = videoInfo.getView(activity);
                        viewGroup.addView(lVideo);
                        break;
                    case ConstantValue.MODULE_TYPE_EXIGENT:   //局部紧急消息
                        exigentInfo = (ExigentInfo) baseModuleInfo;
                        viewGroup.addView(exigentInfo.getView(activity));
                        exigentInfo.setVisibility(false);
                        break;
                    case ConstantValue.MODULE_TYPE_EXIGENT_FULL:   //全屏紧急消息
                        exigentInfo_full = (ExigentInfo_Full) baseModuleInfo;
                        viewGroup.addView(exigentInfo_full.getView(activity));
                        exigentInfo_full.setVisibility(false);
                        break;
                    case ConstantValue.MODULE_TYPE_ATS_STRAIN://车载ATS
                        mAtsStrain = (AtsStrainView) baseModuleInfo;
                        viewGroup.addView(mAtsStrain.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_ATS_STATION://车站ATS
                        mAtsStationView = (AtsStationView) baseModuleInfo;
                        viewGroup.addView(mAtsStationView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_ADVERTISEMENT://广告模块
                        advertisementView = (AdvertisementView) baseModuleInfo;
                        viewGroup.addView(advertisementView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_TRAININFO://首末班车模块
                        trainInfoView = (TrainInfoView) baseModuleInfo;
                        viewGroup.addView(trainInfoView.getView(activity));
                        break;
                    case ConstantValue.MODULE_TYPE_WEATHER: // 天气预报模块
                        weatherView = (WeatherView) baseModuleInfo;
                        viewGroup.addView(weatherView.getView(activity));
                        break;
                    default:
                        break;
                }
            }
            this.viewGroup = viewGroup;
            showContentView();
        } catch (Exception e) {
            LogUtil.e(TAG, "changeContentView error ", e);
        }
    }

    public VideoSurfaceInfo changeLocalToLive(LocalVideoView v) {
        VideoSurfaceInfo vv = VideoSurfaceInfo.getInstance();
        if (v == null) return null;
        vv.setModule_type(v.getModule_type());
        vv.setModule_name(v.getModule_name());
        vv.setModule_Pos(v.getModule_Pos());
        return vv;
    }

    /**
     * 显示当前布局
     */
    private void showContentView() {
        if (viewGroup != null) {
            activity.setContentView(viewGroup);
        }
    }

    public VlcVideo getlVideo() {
        return lVideo;
    }

    public void setlVideo(VlcVideo lVideo) {
        this.lVideo = lVideo;
    }


    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public static void setExigentInfo(ExigentInfo exigentInfo) {
        ViewManager.exigentInfo = exigentInfo;
    }

    public static void setExigentInfo_full(ExigentInfo_Full exigentInfo_full) {
        ViewManager.exigentInfo_full = exigentInfo_full;
    }

    public static ExigentInfo getExigentInfo() {
        return exigentInfo;
    }

    public static ExigentInfo_Full getExigentInfo_full() {
        return exigentInfo_full;
    }

    public static LocalVideoView getVideoInfo() {
        return videoInfo;
    }

    public static void setVideoInfo(LocalVideoView videoInfo) {
        ViewManager.videoInfo = videoInfo;
    }

    public ScrollView getScrollInfo() {
        return scrollInfo;
    }

    public void setScrollInfo(ScrollView scrollInfo) {
        this.scrollInfo = scrollInfo;
    }

    public AtsStrainView getmAtsStrain() {
        return mAtsStrain;
    }

    public void setmAtsStrain(AtsStrainView mAtsStrain) {
        this.mAtsStrain = mAtsStrain;
    }

    public AtsStationView getmAtsStationView() {
        return mAtsStationView;
    }

    public void setmAtsStationView(AtsStationView mAtsStationView) {
        this.mAtsStationView = mAtsStationView;
    }

    public AdvertisementView getAdvertisementView() {
        return advertisementView;
    }

    public void setAdvertisementView(AdvertisementView advertisementView) {
        this.advertisementView = advertisementView;
    }

    public WeekView getWeekView() {
        return weekView;
    }

    public void setWeekView(WeekView weekView) {
        this.weekView = weekView;
    }

    public TrainInfoView getTrainInfoView() {
        return trainInfoView;
    }

    public void setTrainInfoView(TrainInfoView trainInfoView) {
        this.trainInfoView = trainInfoView;
    }
}

