package com.ghts.player.anim;

/**
 * Created by Administrator on 2017/11/1.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ghts.player.R;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SlideShowView extends FrameLayout {

    private Context context;
    private ViewPager viewPager;
    private int imgTypes;
    //当前轮播页
    private int currentItem = 0;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;
    //自动轮播的时间间隔
    private int duration = 1000;
    private List<ImageView> mImageViewList;
    private List<Drawable> ysImageViewList;
    private int currentPosition = 0;
    private int dotPosition = 0;
    private int prePosition = 0;
    private ArrayList<String> fileList;
    FixedSpeedScroller fixedSpeedScroller; // 滚动

    //Handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentPosition, true);
        }
    };

    public SlideShowView(Context context) {
        this(context, null);
        this.context = context;
        fileList = new ArrayList<String>();
        fileList.clear();
    }

    public void setImg(int duration) {
        this.duration = duration;
        initView();
        initData();
        setViewPager();
        //        autoPlay();
        startPlay();
    }

    public void initView() {
        LayoutInflater.from(context).inflate(R.layout.layout, this, true);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        try {
            //设置滚动切换的动画时间
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            fixedSpeedScroller = new FixedSpeedScroller(context,
                    new AccelerateInterpolator());
            field.set(viewPager, fixedSpeedScroller);
            fixedSpeedScroller.setmDuration(100);
        } catch (Exception e) {
            LogUtil.e("Slide出错",e.toString());
            e.printStackTrace();
        }
        imgTypes = 0;
        //        setImgChange();

    }


    private void setImgChange(){
        switch (imgTypes){
            case 0:
                viewPager.setPageTransformer(true, new DepthPageTransformer());
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                break;
        }

    }
    private void initData() {
        mImageViewList = new ArrayList<ImageView>();
        mImageViewList.clear();
        ysImageViewList = new ArrayList<Drawable>();
        ysImageViewList.clear();
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                Bitmap bm = PubUtil.fitSizeImg(fileList.get(i));
                ImageView view = new ImageView(context);
                view.setImageBitmap(bm);
                view.setScaleType(ScaleType.FIT_XY);
                mImageViewList.add(view);
                ysImageViewList.add(view.getDrawable());
            }
        }
        ImageView imageView;
        for(int i=0;i<fileList.size();i++){//0,1,2,3,4
            if(i==0){   //判断当i=0为该处的ImageView设置最后一张图片作为背景
                imageView=new ImageView(context);
                //                imageView.setBackgroundResource(images[images.length-1]);
                imageView.setBackground(ysImageViewList.get(0));
                mImageViewList.add(imageView);
            }else if(i==fileList.size()+1){   //判断当i=images.length+1时为该处的ImageView设置第一张图片作为背景
                imageView=new ImageView(context);
                imageView.setBackground(ysImageViewList.get(0));
                mImageViewList.add(imageView);
            }else{  //其他情况则为ImageView设置images[i-1]的图片作为背景
                imageView=new ImageView(context);
                imageView.setBackground(ysImageViewList.get(i));
                mImageViewList.add(imageView);
            }
        }
    }

    //  设置自动播放
    private void autoPlay() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    SystemClock.sleep(duration);
                    currentPosition++;
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private void setViewPager() {
        MyPagerAdapter adapter = new MyPagerAdapter(mImageViewList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        //页面改变监听
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position==0){    //判断当切换到第0个页面时把currentPosition设置为images.length,即倒数第二个位置，小圆点位置为length-1
                    currentPosition=fileList.size();
                    dotPosition=fileList.size()-1;
                    //                    LogUtil.e("---&&1----",currentPosition+"-------"+dotPosition);
                }else if(position==fileList.size()+1){    //当切换到最后一个页面时currentPosition设置为第一个位置，小圆点位置为0
                    currentPosition=1;
                    dotPosition=0;   //1,0
                    //                    LogUtil.e("---&&2----",currentPosition+"-------"+dotPosition);
                }else{
                    currentPosition=position;
                    dotPosition=position-1; //234512345
                    //                    LogUtil.e("---&&3----",currentPosition+"-------"+dotPosition);
                }
                prePosition = dotPosition; //1234001234

                //设置动画效果
                //                LogUtil.e("---&&4----",prePosition+"-------");
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                //当state为SCROLL_STATE_IDLE即没有滑动的状态时切换页面
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewPager.setCurrentItem(currentPosition, false);
                }
            }
        });
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 开始轮播图切换
     */
    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        duration = 5000;
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), duration, duration, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止轮播图切换
     */
    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }


    private class MyPagerAdapter extends PagerAdapter implements OnPageChangeListener{
        List<ImageView> list;

        public MyPagerAdapter(List<ImageView> list) {
            this.list = list;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {

        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView=list.get(position);
            container.addView(imageView);
            return list.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }

    /**
     * 执行轮播图切换任务
     *
     * @author caizhiming
     */
    private class SlideShowTask implements Runnable {
        @Override
        public void run() {
            synchronized (viewPager) {
                currentPosition++;
                handler.sendEmptyMessage(1);
            }
        }
    }

    /**
     * 销毁ImageView资源，回收内存
     */
    private void destoryBitmaps() {
        for (int i = 0; i < fileList.size(); i++) {
            ImageView imageView = mImageViewList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }

    public ArrayList<String> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<String> fileList) {
        this.fileList = fileList;
    }
}