package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghts.player.R;
import com.ghts.player.bean.WeatherBean;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.TypeFaceFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author ljj
 * @des 2019-01-10
 */
public class WeatherModule extends FrameLayout {
    private Context context;
    private int alarmLogoNum;// 每页显示的预警数目
    private int swtichParam; //切换时间
    private int weatherOffLine, warningOffLine; // 分钟

    private POS bgPos;
    /**
     * 天气页面
     */
    private POS weatherPos;

    private POS temperaturePos;
    private FontParam temperatureFont;

    private POS humidityPos;
    private FontParam humidityFont;

    private POS windPos;
    private FontParam windFont;
    /**
     * 预警页面
     */
    private POS zonePos1, zonePos2, zonePos3, zonePos4;
    private FontParam zoneFont;
    private POS zone1Rect, zone2Rect, zone3Rect, zone4Rect;
    private TextView zoneTv1, zoneTv2, zoneTv3, zoneTv4;
    private GifImageView zoneLogo1, zoneLogo2, zoneLogo3, zoneLogo4;
    private GifDrawable zoneDrawable1, zoneDrawable2, zoneDrawable3, zoneDrawable4;
    /**
     * 地铁图标
     */
    private POS metroLogo;

    FrameLayout weatherLayout;
    private GifImageView imageView;
    private GifImageView weather;
    TextView tempTv, humTv, windTv;

    FrameLayout zoneLayout;
    FrameLayout metroLayout;

    View mView;
    ArrayList<WeatherBean> zones;

    public WeatherModule(Context context) {
        super(context, null);
        this.context = context;
        mView = LayoutInflater.from(context).inflate(R.layout.weatherlayout, this, true);
        zones = new ArrayList<WeatherBean>();
        zones.clear();
    }

    /**
     * 天气模块
     */
    public void setWeatherLayout() {
        weatherLayout = (FrameLayout) mView.findViewById(R.id.weather);
        try {
            weather = new GifImageView(context);

            Bitmap bitmap = BitmapFactory.decodeFile("/sata/img/shorttimeicon/01.png");
            weather.setScaleType(ImageView.ScaleType.FIT_XY);
            // 获取软引用中的位图变量
            if (bitmap != null) {
                weather.setImageBitmap(bitmap);
                initPositionLogo(weather, weatherPos);
                weatherLayout.addView(weather);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempTv = new TextView(context);
        initFont(tempTv, temperatureFont);
        initPosition(tempTv, temperaturePos);

        humTv = new TextView(context);
        initFont(humTv, humidityFont);
        initPosition(humTv, humidityPos);

        windTv = new TextView(context);
        initFont(windTv, windFont);
        initPosition(windTv, windPos);

        weatherLayout.addView(tempTv);
        weatherLayout.addView(humTv);
        weatherLayout.addView(windTv);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int m = msg.arg1;
            show(m);
        }
    };

    public void show(int msg) {
        try {
            if (Const.getWeatherBean() == null) {
                metroLayout.setVisibility(View.VISIBLE);
                weatherLayout.setVisibility(View.GONE);
                zoneLayout.setVisibility(View.GONE);
            } else if (Const.getWeatherBean() != null && Const.alarmBean == null) {
                tempTv.setText(Const.weatherBean.getTemperature());
                humTv.setText(Const.weatherBean.getHumidity());
                windTv.setText(Const.weatherBean.getWind());
                Bitmap bitmap = BitmapFactory.decodeFile(Const.weatherBean.getIcon());
                if (bitmap != null) {
                    weather.setImageBitmap(bitmap);
                }
                metroLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);
                zoneLayout.setVisibility(View.GONE);
            } else if (Const.getWeatherBean() != null && Const.getAlarmBean() != null) {
                if (msg == 0) {
                    tempTv.setText(Const.weatherBean.getTemperature());
                    humTv.setText(Const.weatherBean.getHumidity());
                    windTv.setText(Const.weatherBean.getWind());
                    Bitmap bitmap = BitmapFactory.decodeFile(Const.weatherBean.getIcon());
                    if (bitmap != null) {
                        weather.setImageBitmap(bitmap);
                    }
                    metroLayout.setVisibility(View.GONE);
                    weatherLayout.setVisibility(View.VISIBLE);
                    zoneLayout.setVisibility(View.GONE);
                } else {
                    WeatherBean bean = Const.getAlarmBean().getZones().get(msg - 1);
                    zoneTv1.setText(bean.getDistrict());
                    zoneDrawable1 = new GifDrawable(new File(bean.getIcon()));
                    zoneLogo1.setImageDrawable(zoneDrawable1);

                    metroLayout.setVisibility(View.GONE);
                    weatherLayout.setVisibility(View.GONE);
                    zoneLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startChange() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (Const.getAlarmBean() != null) {
                            int num = Const.getAlarmBean().getNum();
                            for (int i = 0; i < num + 1; i++) {
//                                swtichParam = 1000 * 30;
                                Thread.sleep(swtichParam);
//                                LogUtil.e("######切换", "-----");
                                Message msg = new Message();
                                msg.what = 0;
                                msg.arg1 = i;
                                handler.sendMessage(msg);
                            }
                        } else {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.arg1 = 1000;
                            handler.sendMessage(msg);
                            Thread.sleep(swtichParam);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 默认地铁图标
     */
    public void setLogo() {
        metroLayout = (FrameLayout) mView.findViewById(R.id.logo);
        imageView = new GifImageView(context);
        Bitmap bitmap = BitmapFactory.decodeFile("/sata/img/metro.jpg");
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        // 获取软引用中的位图变量
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            initPositionLogo2(imageView, metroLogo);
            metroLayout.addView(imageView);
        }
    }

    /**
     * 设置区域模块
     */
    public void setZoneLayout() {
        zoneLayout = (FrameLayout) mView.findViewById(R.id.zone);
        switch (alarmLogoNum) {
            case 1:
                setZone1();
                break;
            case 2:
                setZone1();
                setZone2();
                break;
            case 3:
                setZone1();
                setZone2();
                setZone3();
                break;
            case 4:
                setZone1();
                setZone2();
                setZone3();
                setZone4();
                break;
        }
    }

    private void setZone1() {
        try {
            zoneTv1 = new TextView(context);
            initFont(zoneTv1, zoneFont);
            initPosition(zoneTv1, zone1Rect);
            zoneLayout.addView(zoneTv1);

            zoneLogo1 = new GifImageView(context);
            zoneDrawable1 = new GifDrawable(new File("/sata/img/alarmicon/A2.gif"));
            zoneLogo1.setImageDrawable(zoneDrawable1);
            initPositionLogo(zoneLogo1, zonePos1);
            zoneLayout.addView(zoneLogo1);
        } catch (Exception e) {
        }
    }

    private void setZone2() {
        try {
            zoneTv2 = new TextView(context);
            initFont(zoneTv2, zoneFont);
            initPosition(zoneTv2, zone2Rect);
            zoneTv2.setText("区域2");
            zoneLayout.addView(zoneTv2);

            zoneLogo2 = new GifImageView(context);
            zoneDrawable2 = new GifDrawable(new File("/sata/img/alarmicon/A2.gif"));
            zoneLogo2.setImageDrawable(zoneDrawable2);
            initPositionLogo(zoneLogo2, zonePos2);
            zoneLayout.addView(zoneLogo2);
        } catch (IOException e) {
        }
    }

    private void setZone3() {
        try {
            zoneTv3 = new TextView(context);
            initFont(zoneTv3, zoneFont);
            initPosition(zoneTv3, zone3Rect);
            zoneTv3.setText("区域3");
            zoneLayout.addView(zoneTv3);

            zoneLogo3 = new GifImageView(context);
            zoneDrawable3 = new GifDrawable(new File("/sata/img/alarmicon/A2.gif"));
            zoneLogo3.setImageDrawable(zoneDrawable3);
            initPositionLogo(zoneLogo3, zonePos3);
            zoneLayout.addView(zoneLogo3);
        } catch (IOException e) {
        }
    }

    private void setZone4() {
        try {
            zoneTv4 = new TextView(context);
            initFont(zoneTv4, zoneFont);
            initPosition(zoneTv4, zone4Rect);
            zoneTv4.setText("区域4");
            zoneLayout.addView(zoneTv4);

            zoneLogo4 = new GifImageView(context);
            zoneDrawable4 = new GifDrawable(new File("/sata/img/alarmicon/A2.gif"));
            zoneLogo4.setImageDrawable(zoneDrawable4);
            initPositionLogo(zoneLogo4, zonePos4);
            zoneLayout.addView(zoneLogo4);
        } catch (IOException e) {
        }
    }

    void initFont(TextView tv, FontParam font) {
        // 大小
        tv.setTextSize(font.getSize());
        // 字体
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        // 颜色
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);
    }

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    void initPositionLogo(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(60, 48));
    }

    void initPositionLogo2(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public void setWarningOffLine(int warningOffLine) {
        this.warningOffLine = warningOffLine;
    }

    public POS getWeatherPos() {
        return weatherPos;
    }

    public void setWeatherPos(POS weatherPos) {
        this.weatherPos = weatherPos;
    }

    public POS getTemperaturePos() {
        return temperaturePos;
    }

    public void setTemperaturePos(POS temperaturePos) {
        this.temperaturePos = temperaturePos;
    }

    public FontParam getTemperatureFont() {
        return temperatureFont;
    }

    public void setTemperatureFont(FontParam temperatureFont) {
        this.temperatureFont = temperatureFont;
    }

    public POS getHumidityPos() {
        return humidityPos;
    }

    public void setHumidityPos(POS humidityPos) {
        this.humidityPos = humidityPos;
    }

    public FontParam getHumidityFont() {
        return humidityFont;
    }

    public void setHumidityFont(FontParam humidityFont) {
        this.humidityFont = humidityFont;
    }

    public POS getWindPos() {
        return windPos;
    }

    public void setWindPos(POS windPos) {
        this.windPos = windPos;
    }

    public FontParam getWindFont() {
        return windFont;
    }

    public void setWindFont(FontParam windFont) {
        this.windFont = windFont;
    }

    public FontParam getZoneFont() {
        return zoneFont;
    }

    public void setZoneFont(FontParam zoneFont) {
        this.zoneFont = zoneFont;
    }

    public POS getZone1Rect() {
        return zone1Rect;
    }

    public void setZone1Rect(POS zone1Rect) {
        this.zone1Rect = zone1Rect;
    }

    public POS getZone2Rect() {
        return zone2Rect;
    }

    public void setZone2Rect(POS zone2Rect) {
        this.zone2Rect = zone2Rect;
    }

    public POS getZone3Rect() {
        return zone3Rect;
    }

    public void setZone3Rect(POS zone3Rect) {
        this.zone3Rect = zone3Rect;
    }

    public POS getZone4Rect() {
        return zone4Rect;
    }

    public void setZone4Rect(POS zone4Rect) {
        this.zone4Rect = zone4Rect;
    }

    public POS getMetroLogo() {
        return metroLogo;
    }

    public void setMetroLogo(POS metroLogo) {
        this.metroLogo = metroLogo;
    }

    public POS getBgPos() {
        return bgPos;
    }

    public void setBgPos(POS bgPos) {
        this.bgPos = bgPos;
    }

    public POS getZonePos1() {
        return zonePos1;
    }

    public void setZonePos1(POS zonePos1) {
        this.zonePos1 = zonePos1;
    }

    public POS getZonePos2() {
        return zonePos2;
    }

    public void setZonePos2(POS zonePos2) {
        this.zonePos2 = zonePos2;
    }

    public POS getZonePos3() {
        return zonePos3;
    }

    public void setZonePos3(POS zonePos3) {
        this.zonePos3 = zonePos3;
    }

    public POS getZonePos4() {
        return zonePos4;
    }

    public void setZonePos4(POS zonePos4) {
        this.zonePos4 = zonePos4;
    }

    public int getAlarmLogoNum() {
        return alarmLogoNum;
    }

    public void setAlarmLogoNum(int alarmLogoNum) {
        this.alarmLogoNum = alarmLogoNum;
    }

    public int getSwtichParam() {
        return swtichParam;
    }

    public void setSwtichParam(int swtichParam) {
        this.swtichParam = swtichParam;
    }

    public int getWeatherOffLine() {
        return weatherOffLine;
    }

    public void setWeatherOffLine(int weatherOffLine) {
        this.weatherOffLine = weatherOffLine;
    }

    public int getWarningOffLine() {
        return warningOffLine;
    }
}
