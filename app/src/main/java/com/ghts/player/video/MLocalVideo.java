package com.ghts.player.video;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ghts.player.bean.LocalPlayItem;
import com.ghts.player.enumType.POS;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.UserInfoSingleton;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by lijingjing on 18-10-11.
 */
public class MLocalVideo extends SurfaceView implements IVLCVout.OnNewVideoLayoutListener {

    private static MLocalVideo instance;
    private Context mContext;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "MVideoLive";
    private static String SAMPLE_URL = "file:///sata/media/jiuzai.mpg";
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_FILL;

    private final Handler mHandler = new Handler();
    private OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLC = null;
    public MediaPlayer mMediaPlayer = null;
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    private SurfaceView mVideoSurface, mSubtitlesSurface;
    private SurfaceHolder surfaceHolder;
    private FrameLayout mVideoSurfaceFrame;
    private POS pos;

    private ArrayList<LocalPlayItem> local_list; // 本地视频播放列表。
    public int volume; // 本地视频播放音量
    private ListIterator<LocalPlayItem> iterator;
    private LocalPlayItem current_item; // 当前播放视频项
    private LocalPlayItem next_item; // 下一个播放视频项
    private int frameRate = 25;
    String path;


    public static MLocalVideo getInstance(Context context) {
        if (instance == null) {
            instance = new MLocalVideo(context);
        }
        return instance;
    }

    public MLocalVideo(Context context) {
        super(context);
        this.mContext = context;
    }

    public MLocalVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MLocalVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDestroy() {
        LogUtil.e("--onDestroy---", "--JAVAACTIVITY-");
        mMediaPlayer.release();
        mLibVLC.release();
    }

    private MediaPlayer.EventListener eventListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            try {
                if (mMediaPlayer.getPlayerState() == Media.State.Ended) {
                    mMediaPlayer.setTime(0);
                    completion();
                    LogUtil.e("-播放结束-", mMediaPlayer.getPosition() + "--");
                }
            } catch (Exception e) {
                Log.d("vlc-event", e.toString());
            }
        }
    };

    public void init(ArrayList<LocalPlayItem> local_list, POS pos) {
        this.local_list = local_list;
        this.pos = pos;
        mVideoSurface = this;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(mSHCallback);
        final Context context = mContext;
        ArrayList<String> options = new ArrayList<String>(50);
        mLibVLC = new LibVLC(mContext, options);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.getVLCVout().setWindowSize(pos.getWidth(), pos.getHeight());//宽，高  播放窗口的大小
        mMediaPlayer.setAspectRatio(pos.getWidth() + ":" + pos.getHeight());//宽，高  画面大小
        mMediaPlayer.setScale(0);
         mVideoSurfaceFrame = new FrameLayout(mContext);
        mVideoSurfaceFrame.setBackgroundColor(Color.BLACK);
        mMediaPlayer.setEventListener(eventListener);
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        if (mVideoSurface != null)
            vlcVout.setSubtitlesView(mVideoSurface);
        vlcVout.attachViews(this);
        startPlayLocal();
        updateVideoSurfaces();
        if (mOnLayoutChangeListener == null) {
            final Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    updateVideoSurfaces();
                }
            };
            mOnLayoutChangeListener = new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
        mVideoSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    /**
     * 播放本地视频<br>
     * 记录上一次切播本地的视频的位置,在这里恢复继续播
     */
    private void playLocalVideo() {
        if (local_list == null)
            return;
        if (local_list.size() <= 0)
            return;
        iterator = local_list.listIterator();
        // 如果有下个元素
        while (iterator.hasNext()) {
            // 获取第一个播放项
            LocalPlayItem item = iterator.next();
            // 获取当前时间毫秒值
            long currentTimeMillis = System.currentTimeMillis();
            Time current_time = new Time();
            current_time.set(currentTimeMillis);
            if (timeToMillis(current_time) >= item.getStart_frame()
                    .toMillisecond(frameRate)) { // 如果当前时间晚于或等于本视频开始播放时间
                current_item = item;
                break; // 找到后退出循环
            }
        }
        if (current_item == null) { // 如果一个都没找到，则播放第一个
            iterator = local_list.listIterator();
            // 视频列表项播完了就再播第一项。
            current_item = iterator.next();
        }
        findNext();
        playVideo();
        System.gc();
    }

    /**
     * 找到下一个播放项。
     */
    void findNext() {
        try {
            if (iterator == null) {
                startPlayLocal();
                return;
            }
            if (iterator.hasNext()) {
                next_item = iterator.next();
            } else {
                while (iterator.hasPrevious()) {
                    iterator.previous();
                }
                // 视频列表项播完了就再播第一项。
                next_item = iterator.next();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "find next error", e);
        }
    }

    private void completion() {
        // 播放完当前视频项，停止播放以释放资源。
        // 将下一项设为当前项，
        current_item = next_item;
        // 并找到另一个下一项
        findNext();
        // 播放
        playVideo();
    }

    public void startPlayLocal() {
        playLocalVideo();
    }

    public void playVideo() {
        path = current_item.getFile().getPath();
        Log.e("--本地path--", current_item.getFile().getPath());
        Media media = new Media(mLibVLC, path);
        media.setHWDecoderEnabled(true, true);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();
    }

    private int timeToMillis(Time time) {
        return time.hour * 60 * 60 * 1000 + time.minute * 60 * 1000
                + time.second * 1000;
    }

    public void pause() {
        LogUtil.e("视频暂停",mMediaPlayer.getPosition()+"*****"+mMediaPlayer.getLength());
        mMediaPlayer.pause();
    }

    public void restart() {
        LogUtil.e("视频重播",mMediaPlayer.getPosition()+"*****"+mMediaPlayer.getLength());
        mMediaPlayer.play();
    }

    private void updateVideoSurfaces() {
        int sw = pos.getWidth();
        int sh = pos.getHeight();
        // sanity check
        if (sw * sh == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }
        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);

        mMediaPlayer.setAspectRatio(pos.getWidth() + ":" + pos.getHeight());//宽，高  画面大小
        mVideoSurface.setMinimumWidth(sw);
        mVideoSurface.setMinimumHeight(sh);
        ViewGroup.LayoutParams lp = mVideoSurface.getLayoutParams();
        if (mVideoWidth * mVideoHeight == 0) {
            /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
            lp.width = sw;
            lp.height = sh;
            mVideoSurface.setLayoutParams(lp);
            //            lp = mVideoSurfaceFrame.getLayoutParams();
            //            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            //            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoSurfaceFrame.setLayoutParams(lp);
            //            changeMediaPlayerLayout(sw, sh);
            return;
        }

        if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            /* We handle the placement of the video using Android View LayoutParams */
            mMediaPlayer.setAspectRatio(null);
            mMediaPlayer.setScale(0);
        }

        double dw = sw, dh = sh;
        final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mVideoSarDen == mVideoSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
            ar = 1;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_SCREEN:
                if (dar >= ar)
                    dh = dw / ar; /* horizontal */
                else
                    dw = dh * ar; /* vertical */
                break;
            case SURFACE_FILL:
                dh = mVideoVisibleHeight;
                dw = mVideoVisibleWidth;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = mVideoVisibleWidth;
                break;
        }

        // set display size
        //        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        //        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);

        lp.width = pos.getWidth();
        lp.height = pos.getHeight();
        mVideoSurface.setLayoutParams(lp);
        if (mSubtitlesSurface != null)
            mSubtitlesSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = mVideoSurfaceFrame.getLayoutParams();
        //        lp.width = (int) Math.floor(dw);
        //        lp.height = (int) Math.floor(dh);
        lp.width = pos.getWidth();
        lp.height = pos.getHeight();
        mVideoSurfaceFrame.setLayoutParams(lp);

        mVideoSurface.invalidate();
        if (mSubtitlesSurface != null)
            mSubtitlesSurface.invalidate();
    }

    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mVideoSarNum = sarNum;
        mVideoSarDen = sarDen;
        updateVideoSurfaces();
    }


    public void onStop() {
        if (mOnLayoutChangeListener != null) {
            mVideoSurfaceFrame.removeOnLayoutChangeListener(mOnLayoutChangeListener);
            mOnLayoutChangeListener = null;
        }
        mMediaPlayer.stop();
        LogUtil.e("--onStop---", "--JAVAACTIVITY-");
        mMediaPlayer.getVLCVout().detachViews();
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {

        }

        public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                                   int width, int height) {

        }

        public void surfaceDestroyed(SurfaceHolder surfaceholder) {

        }
    };


}

