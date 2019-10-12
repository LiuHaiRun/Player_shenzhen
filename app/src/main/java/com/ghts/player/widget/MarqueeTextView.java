package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.TypeFaceFactory;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by lijingjing on 17-5-15.
 */
public class MarqueeTextView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MScroll";
    private int module_type; // 模块类型
    private volatile float textWidth;
    private int zOrder; // 模块的ZOrder
    private String file_version;    //文件版本

    private String module_name;    //控件名

    private int module_uid;//控件标识

    private int module_gid;//控件组标识

    private float y_pos;//控件y轴数值;

    private int refresh_time; // 滚动刷新的时间间隔
    private int scroll_pixel; // 滚动刷新的间距
    /***
     * 滚动上一文本和下一文本间距
     **/
    private float scroll_space = 0;

    private volatile Paint paint = null;
    protected SurfaceHolder holder = null;
    private Thread myThread;
    private volatile int currentScrollX;// 当前滚动的位置
    private final String gapString = "               ";
    private volatile int count = 0;

    public int getCurrentScrollX() {
        return currentScrollX;
    }

    public void setCurrentScrollX(int currentScrollX) {
        this.currentScrollX = currentScrollX;
    }

    //	private String tempText="";
    private StringBuffer currentText = new StringBuffer();
    private StringBuffer nextText = new StringBuffer();
    private BGParam backgroud;
    private ArrayList<String> text_list;// 待显示的文本列表

    private boolean isFirst = true;
    private FontParam font;
    /**
     * 是否收到广播 默认false
     */
    private volatile boolean isReceive = false;
    private volatile boolean isChangeNextStation = false;
    /**
     * 是否添加
     ***/
    private volatile boolean isAdd = true;
    private ArrayList<String> temp_list = null;// 待显示的文本列表
    private Rect mScrollRect;
    private Handler mHandler = new Handler();

    public MarqueeTextView(Context context) {
        super(context);
        holder = this.getHolder();
        //调用SurfaceHolder.Callback这个接口,这个接口必须实现3个方法,surfaceCreate,surfaceChanged,surfaceDestory
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT); // 顶层绘制SurfaceView设成透明
        this.setZOrderOnTop(true);
    }

    /* 自定义线程 */
    class MyRunnable implements Runnable {
        private ListIterator<String> listIterator = null;
        private int mWidth = 0;
        private Canvas canvas = null;
        int ret = 0;
        public void run() {
            //整个空间的宽度
            mWidth = MarqueeTextView.this.getWidth();
            LogUtil.e("--滚动字幕1--",mWidth+"");
            mScrollRect = new Rect(0, 0, MarqueeTextView.this.getWidth(), MarqueeTextView.this.getHeight());
            while (!myThread.isInterrupted()) {
                try {
                    synchronized (holder) {
                        if (paint == null || isChangeNextStation == true) {
                            if (isChangeNextStation) {
                                isChangeNextStation = false;
                                paint = null;
                            }
                            initPaint();
                            textWidth = paint.measureText(currentText.toString());// 得到文本的长度。
                            LogUtil.e("--滚动字幕2--",textWidth+"");
                            LogUtil.e("--滚动字幕3--",currentText.toString());
                        }
                        ret = draw();
                        if (ret == 1) {
                            continue;
                        } else if (ret < 0) {
                            paint = null;
                            currentScrollX = MarqueeTextView.this.getWidth();
                            textWidth = 0;
                            currentText.setLength(0);
                            System.gc();
                            return;
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "ScrollSurfaceView：run...\r\n"
                            + e);
                }
            }
        }

        private int draw() {
            currentScrollX -= scroll_pixel;// 滚动速度
            canvas = holder.lockCanvas(mScrollRect); // 获取画布
            if (canvas == null) return -1;
            // 清除画布方法一
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawText(currentText, 0, currentText.length(),
                    currentScrollX, y_pos, paint);

            holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
            if ((currentScrollX + textWidth + scroll_space) <= mWidth) {
//                LogUtil.e("--滚动字幕4--",textWidth+"");
                if (textWidth >= mWidth * 30) {//超过一定长度,不再追加currentText
//                    LogUtil.e("--滚动字幕5--",mWidth*5+"");
                    if (currentScrollX <= -textWidth) {//最后一个字滚完
                        paint = null;
                        currentScrollX = MarqueeTextView.this.getWidth();
                        textWidth = 0;
                        currentText.setLength(0);
                        System.gc();
                        return 1;
                    }
                    return 2;
                } else {
                }
                if (isFirst) {
                    scroll_space = paint.measureText(currentText.toString());
                    currentText.append(gapString);
                    textWidth = paint.measureText(currentText.toString());// 得到文本的长度。
                    scroll_space = textWidth - scroll_space;
                    currentText.append(nextText);
                    isFirst = false;
                } else {
                    currentText.append(gapString).append(nextText);
                    textWidth = paint.measureText(currentText.toString());// 得到文本的长度。
                }
                findNext();
            }

            return 0;
        }

        /**
         * 初始化paint
         */
        public void initPaint() {
            paint = new Paint();
            if (isReceive && null != temp_list) {
                listIterator = temp_list.listIterator();
            } else {
                listIterator = text_list.listIterator();
            }

            if (listIterator.hasNext()) {
                currentText.setLength(0);//清空StringBuffer
                currentText.append(listIterator.next());
                findNext();
            } else if (listIterator.hasPrevious()) {
                currentText.append(listIterator.previous());
            }
            initFont(paint, font);
            y_pos = MarqueeTextView.this.getHeight() - (MarqueeTextView.this.getHeight() - font.getSize()) / 2;
        }

        // 找到下一个文本。
        void findNext() {
            if (listIterator.hasNext()) {
                nextText.setLength(0);
                nextText.append(listIterator.next());
            } else { // 文本集合播完了就再播第一项。
//				isAdd=true;
                if (isReceive && null != temp_list) {
                    listIterator = temp_list.listIterator();
                } else {
                    listIterator = text_list.listIterator();
                }

                nextText.setLength(0);
                nextText.append(listIterator.next());
            }
        }

        /**
         * 设置TextView的字体
         *
         * @param paint textview控件
         * @param font  字体
         */
        void initFont(Paint paint, FontParam font) {
            // paint.setTextAlign(Align.RIGHT);
            // 大小
            paint.setTextSize(font.getSize());
            // 字体
            Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
            paint.setTypeface(typeface);
            // 颜色
            RGBA rgba = font.getFaceColor();
            int color = Color.argb(rgba.getAlpha(), rgba.getRed(),
                    rgba.getGreen(), rgba.getBlue());
            paint.setColor(color);
        }

    }

    @Override
    /**
     * 当控件创建时自动执行的方法
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // 启动自定义线程
        startThread();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isReceive) {
                try {
                    if (count >= 20) {
                        count = 0;
                        isReceive = false;
                        checkThread = null;
                        LogUtil.i(TAG, "runnable/run/stopThread");
                        System.gc();
                    }

                    count++;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LogUtil.e(TAG, "run():" + e);
                }
            }
        }
    };

    Thread checkThread = null;

    public void startThread() {
        currentText.setLength(0);
        LogUtil.i(TAG, "startThread");
        if (myThread != null) {
            stopThread();
            return;
        }
        myThread = new Thread(new MyRunnable());
        myThread.setName("MScrollThread");
        myThread.start();


        /**
         *如果有接收,计时20秒(暂定)，然后切回默认滚动播表 将isReceive 改为false
         */
    }

    public void stopThread() {
        LogUtil.i(TAG, "stopThread");
        // 终止自定义线程
        myThread.interrupt();
        myThread = null;
        System.gc();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    /**
     * 当控件销毁时自动执行的方法
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    public int getScroll_pixel() {
        return scroll_pixel;
    }

    public void setScroll_pixel(int scroll_pixel) {
        this.scroll_pixel = scroll_pixel;
    }

    public String getText() {
        return currentText.toString();
    }

    public void setText(String text) {
        currentText.setLength(0);
        this.currentText.append(text);
    }

    public BGParam getBackgroud() {
        return backgroud;
    }

    public void setBackgroud(BGParam backgroud) {
        this.backgroud = backgroud;
    }


    public FontParam getFont() {
        return font;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }

    public long getRefresh_time() {
        return refresh_time;
    }

    public void setRefresh_time(int refresh_time) {
        this.refresh_time = refresh_time;
    }

    public ArrayList<String> getText_list() {
        return text_list;
    }

    public void setText_list(ArrayList<String> text_list) {
        this.text_list = text_list;
    }

    public int getModule_type() {
        return module_type;
    }

    public void setModule_type(int module_type) {
        this.module_type = module_type;
    }

    public int getzOrder() {
        return zOrder;
    }

    public void setzOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    public String getFile_version() {
        return file_version;
    }

    public void setFile_version(String file_version) {
        this.file_version = file_version;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public int getModule_uid() {
        return module_uid;
    }

    public void setModule_uid(int module_uid) {
        this.module_uid = module_uid;
    }

    public int getModule_gid() {
        return module_gid;
    }

    public void setModule_gid(int module_gid) {
        this.module_gid = module_gid;
    }

    /**
     * @return the isReceive
     */
    public boolean isReceive() {
        return isReceive;
    }

    /**
     * @param isReceive the isReceive to set
     */
    public void setReceive(boolean isReceive) {
        this.isReceive = isReceive;
        if (isReceive) {
            count = 0;
            isChangeNextStation = true;
//			paint = null;
            currentScrollX = MarqueeTextView.this.getWidth();
            textWidth = 0;
            currentText.setLength(0);
//			System.gc();
            if (checkThread == null) {
                checkThread = new Thread(runnable);
                checkThread.setName("MScrollCheckThread");
                checkThread.start();
                LogUtil.i(TAG, "setReceive/checkThread.start()");
            }
        }
    }

    /**
     * @return the temp_list
     */
    public ArrayList<String> getTemp_list() {
        return temp_list;
    }

    /**
     * @param temp_list the temp_list to set
     */
    public void setTemp_list(ArrayList<String> temp_list) {
        this.temp_list = temp_list;
    }


}
