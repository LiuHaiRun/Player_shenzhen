package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;

/**
 * @author ljj
 * @des 2019-1-8
 */
public class WeatherView extends BaseModuleInfo {
    private Context mContext = null;
    private BGParam backgroud;

    private int AlarmLogoNum;// 每页显示的预警数目
    private int SwtichParam; //切换时间
    private int WeatherOffLine, WarningOffLine; // 分钟
    /**
     * 天气页面
     */
    private POS weatherPos;

    private POS temperaturePos;
    private FontParam temperatureFont;
    private String temperatureStr;

    private POS humidityPos;
    private FontParam humidityFont;
    private String humidityStr;

    private POS windPos;
    private FontParam windFont;
    private String windStr;
    /**
     * 预警页面
     */
    private POS zone1Logo, zone2Logo, zone3Logo, zone4Logo;
    private FontParam zoneFont;
    private POS zone1Rect, zone2Rect, zone3Rect, zone4Rect;
    /**
     * 地铁图标
     */
    private POS metroLogo;
    private WeatherModule module;

    @Override
    public View getView(Context context) {
        module = new WeatherModule(context);

        module.setAlarmLogoNum(AlarmLogoNum);
        module.setSwtichParam(SwtichParam);
        module.setWeatherOffLine(WeatherOffLine);
        module.setWarningOffLine(WarningOffLine);

        module.setWeatherPos(weatherPos);
        module.setTemperaturePos(temperaturePos);
        module.setTemperatureFont(temperatureFont);

        module.setHumidityPos(humidityPos);
        module.setHumidityFont(humidityFont);

        module.setWindPos(windPos);
        module.setWindFont(windFont);
        module.setMetroLogo(metroLogo);

        module.setZoneFont(zoneFont);
        module.setZone1Rect(zone1Rect);
        module.setZonePos1(zone1Logo);

        module.setZone2Rect(zone2Rect);
        module.setZonePos2(zone2Logo);

        module.setZone3Rect(zone3Rect);
        module.setZonePos3(zone3Logo);

        module.setZone4Rect(zone4Rect);
        module.setZonePos4(zone4Logo);

        module.setLogo();
        module.setWeatherLayout();
        module.setZoneLayout();
        initBackground(module, backgroud);
        initPosition(module, pos);
        module.startChange();
        return module;
    }

    void initBackground(View view, BGParam bg) {
        switch (bg.getType()) {
            case NOTSHOW: // 背景不显示
                break;
            case GRADIENTCOLOR: // 背景类型为渐变色
                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
                    //水平渐变 参考：url:http://blog.csdn.net/a_large_swan/article/details/7107126
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

    /**
     * 初始化控件的位置
     */
    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public BGParam getBackgroud() {
        return backgroud;
    }

    public void setBackgroud(BGParam backgroud) {
        this.backgroud = backgroud;
    }

    public int getAlarmLogoNum() {
        return AlarmLogoNum;
    }

    public void setAlarmLogoNum(int alarmLogoNum) {
        AlarmLogoNum = alarmLogoNum;
    }

    public int getSwtichParam() {
        return SwtichParam;
    }

    public void setSwtichParam(int swtichParam) {
        SwtichParam = swtichParam;
    }

    public int getWeatherOffLine() {
        return WeatherOffLine;
    }

    public void setWeatherOffLine(int weatherOffLine) {
        WeatherOffLine = weatherOffLine;
    }

    public int getWarningOffLine() {
        return WarningOffLine;
    }

    public void setWarningOffLine(int warningOffLine) {
        WarningOffLine = warningOffLine;
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

    public String getTemperatureStr() {
        return temperatureStr;
    }

    public void setTemperatureStr(String temperatureStr) {
        this.temperatureStr = temperatureStr;
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

    public String getHumidityStr() {
        return humidityStr;
    }

    public void setHumidityStr(String humidityStr) {
        this.humidityStr = humidityStr;
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

    public String getWindStr() {
        return windStr;
    }

    public void setWindStr(String windStr) {
        this.windStr = windStr;
    }

    public POS getZone1Logo() {
        return zone1Logo;
    }

    public void setZone1Logo(POS zone1Logo) {
        this.zone1Logo = zone1Logo;
    }

    public POS getZone2Logo() {
        return zone2Logo;
    }

    public void setZone2Logo(POS zone2Logo) {
        this.zone2Logo = zone2Logo;
    }

    public POS getZone3Logo() {
        return zone3Logo;
    }

    public void setZone3Logo(POS zone3Logo) {
        this.zone3Logo = zone3Logo;
    }

    public POS getZone4Logo() {
        return zone4Logo;
    }

    public void setZone4Logo(POS zone4Logo) {
        this.zone4Logo = zone4Logo;
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
}
