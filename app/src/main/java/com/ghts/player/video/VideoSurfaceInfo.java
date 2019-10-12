package com.ghts.player.video;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.view.ViewGroup;

import com.ghts.player.enumType.POS;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.widget.BaseModuleInfo;

/**
 * Created by lijingjing on 17-9-1.
 */
public class VideoSurfaceInfo extends BaseModuleInfo {
    private static final String TAG = "/VideoSurfaceInfo";
    private MVideoLive surfaceView = null;
    private AudioManager mAudioManager;
    private int max;
    private static VideoSurfaceInfo instance = null;

    public static VideoSurfaceInfo getInstance() {
        if (instance == null) {
            instance = new VideoSurfaceInfo();
        }
        return instance;
    }

    @Override
    public MVideoLive getView(Context context) {
        surfaceView = new MVideoLive(context);
        // 设置控件的位置
        initPosition(surfaceView, pos);
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return surfaceView;
    }

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public void startPlay() {
        try {
            // 设置音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, PubUtil.parseInt(Const.AudioVolume) * max / 255, 0);

            String live_url = LocalVideoView.getLive_url();
            //            live_url ="127.0.0.1:7662";
            live_url = "udp://@" + live_url;
            LogUtil.e(TAG + "音量" + PubUtil.parseInt(Const.AudioVolume) * max / 255, "live_url = " + live_url);
            surfaceView.init(live_url, pos);
        } catch (Exception e) {
            LogUtil.e(TAG, "startPlay fund error ", e);
        }
    }

    public void onstop() {
        try {
            LogUtil.e(TAG+"surfaceView--", "--onstop-");
            surfaceView.onStop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            LogUtil.e(TAG+"surfaceView--", "--onDestroy-");
            surfaceView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
