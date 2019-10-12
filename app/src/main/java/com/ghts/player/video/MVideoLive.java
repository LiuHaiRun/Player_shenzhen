package com.ghts.player.video;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ghts.player.enumType.POS;
import com.ghts.player.utils.LogUtil;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-9-21.
 */
public class MVideoLive extends SurfaceView implements IVLCVout.OnNewVideoLayoutListener {

    private static MVideoLive instance;
    private Context mContext;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "MVideoLive";
    private static String SAMPLE_URL = "rtsp://c.itvitv.com/hj.kprkjmf";
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_FILL;

    private final Handler mHandler = new Handler();
    private OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLC = null;
    public static MediaPlayer mMediaPlayer = null;
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
    private AudioManager mAudioManager;

    public static MVideoLive getInstance(Context context) {
        if (instance == null) {
            instance = new MVideoLive(context);
        }
        return instance;
    }

    public MVideoLive(Context context) {
        super(context);
        this.mContext = context;
    }

    public MVideoLive(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MVideoLive(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDestroy() {
        LogUtil.e("--onDestroy---", "--JAVAACTIVITY-");
        mMediaPlayer.release();
        mLibVLC.release();
    }

    public void init(String SAMPLE_URL, POS pos) {
        LogUtil.e("--onStart---", "--直播-");
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        this.pos = pos;
        mVideoSurface = this;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(mSHCallback);

        final Context context = mContext;
        ArrayList<String> options = new ArrayList<String>(50);
        final boolean frameSkip = false;
        String chroma = "RV16";
        if (chroma.equals("YV12"))
            chroma = "";
        final boolean verboseMode = true;
        int deblocking = -1;
        //                try {
        //                    deblocking  = getDeblocking(-1);
        //                } catch (NumberFormatException ignored) {}

        int networkCaching = 3000;
        final boolean freetypeBackground = false;
        final int opengl = 0;
                /* CPU intensive plugin, setting for slow devices */
        options.add("--audio-time-stretch");
        options.add("--avcodec-skiploopfilter");
        //        options.add("" + deblocking);
        options.add("--avcodec-skip-frame");
        options.add(frameSkip ? "2" : "0");
        options.add("--avcodec-skip-idct");
        options.add(frameSkip ? "2" : "0");
        // options.add("--subsdec-encoding");
        // options.add("--stats");
               /* XXX: why can't the default be fine ? #7792 */
        if (networkCaching > 0)
            options.add("--network-caching=" + networkCaching);
        options.add("--android-display-chroma");
        options.add(chroma);
        options.add("--audio-resampler");
        options.add("--sout-mux-caching=" + networkCaching);//输出缓存
        options.add("--codec=mediacodec,iomx,all");
        if (freetypeBackground)
            options.add("--freetype-background-opacity=128");
        else
            options.add("--freetype-background-opacity=0");
        if (opengl == 1) {
            options.add("--vout=gles2,android_display");
            options.add("--aout=opensles");
        } else if (opengl == 0)
            options.add("--vout=android_display,none");

               /* Configure keystore */
        options.add("--keystore");
        options.add("--keystore-file");
        options.add("-vvv");

        //        options.add(":file-caching=1500");//文件缓存
        //        options.add(":network-caching=1500");//网络缓存
        //        options.add(":live-caching=1500");//直播缓存
        //        options.add(":sout-mux-caching=1500");//输出缓存
        //        options.add(":codec=mediacodec,iomx,all");
        mLibVLC = new LibVLC(mContext, options);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.getVLCVout().setWindowSize(pos.getWidth(), pos.getHeight());//宽，高  播放窗口的大小
        mMediaPlayer.setAspectRatio(pos.getWidth() + ":" + pos.getHeight());//宽，高  画面大小
        mMediaPlayer.setScale(0);
        //深圳9修改
        mMediaPlayer.setVolume(100);
        mVideoSurfaceFrame = new FrameLayout(mContext);
        mVideoSurfaceFrame.setBackgroundColor(Color.BLACK);
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        if (mVideoSurface != null)
            vlcVout.setSubtitlesView(mVideoSurface);
        vlcVout.attachViews(this);
        Media media = new Media(mLibVLC, Uri.parse(SAMPLE_URL));
//        media.setHWDecoderEnabled(true, true);
        media.setHWDecoderEnabled(false, false);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();

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
        //深圳9修改
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
        LogUtil.e("####--直播----", "播放完成");
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
            mVideoSurfaceFrame.setLayoutParams(lp);
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
        lp.width = pos.getWidth();
        lp.height = pos.getHeight();
        mVideoSurface.setLayoutParams(lp);
        if (mSubtitlesSurface != null)
            mSubtitlesSurface.setLayoutParams(lp);

        lp = mVideoSurfaceFrame.getLayoutParams();
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
