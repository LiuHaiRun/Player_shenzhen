package com.ghts.player.utils;

/**
 * Created by lijingjing on 17-6-19.
 */

public interface ConstantValue {

    /**
     * 指定字符集
     */
    String CHARSET = "UTF-8";

    /**
     * 指定字符集
     */
    String CHARSET_GB2312 = "GB2312";
    /**
     * 字体位置
     */
    String ROOT_DIR = "/sata/";

    /**
     * 判断当前播放的是直播，还是本地  播放本地 0
     */
    String PLAY_LOCAL = "0";
    /**
     * 播放直播 1
     */
    String PLAY_LIVE = "1";

    /**
     * 不支持
     */
    String EMERGSTATUS_DEFAULT = "0";
    /**
     * 不是紧急状态
     */
    String EMERGSTATUS_NO = "1";
    /**
     * 紧急状态
     */
    String EMERGSTATUS_YES = "2";

    /**
     * 不支持 0
     */
    int AUDIOSTATUS_DEFAULT = 0;
    /**
     * 静音 1
     */
    int AUDIOSTATUS_NO = 1;
    /**
     * 未静音 2
     */
    int AUDIOSTATUS_YES = 2;

    /**
     * 开屏状态
     */
    String IsScreenOff_NO = "0";
    /**
     * 关屏状态
     */
    String IsScreenOff_YES = "1";

    /**
     * 开机状态
     */
    String IsPowerOff_NO = "0";
    /**
     * 关机状态
     */
    String IsPowerOff_YES = "1";

    /**
     * 类型-----视频模块
     */
    int MODULE_TYPE_VIDEO = 0x0000100C;
    /**
     * 类型-----滚动模块
     */
    int MODULE_TYPE_SCROLL = 0x00001004;
    /**
     * 类型-----广告模块
     */
    int MODULE_TYPE_ADVERTISEMENT = 0x0000100E;
    /**
     * 类型-----日期模块
     */
    int MODULE_TYPE_DATE = 0x00001002;
    /**
     * 类型-----时间模块
     */
    int MODULE_TYPE_TIME = 0x00001003;
    /**
     * 类型-----星期模块
     */
    int MODULE_TYPE_WEEK = 0x0000100A;
    /**
     * 类型-----文本模块
     */
    int MODULE_TYPE_TEXT = 0x00001007;
    /**
     * 类型-----图片模块
     */
    int MODULE_TYPE_PIC = 0x00001008;
    /**
     * 类型-----紧急模块
     */
    int MODULE_TYPE_EXIGENT = 0x0000100F;
    /**
     * 类型-----紧急模块，全屏
     */
    int MODULE_TYPE_EXIGENT_FULL = 0x00001009;
    /**
     * 车站和车载ATS
     **/
    int MODULE_TYPE_ATS_STRAIN = 0x00001005;
    int MODULE_TYPE_ATS_STATION = 0x00001006;

    /**
     * 类型-----天气预报模块
     */
    int MODULE_TYPE_WEATHER = 0x00001012;
    /**
     * 类型-----切换文本模块
     */
    int MODULE_TYPE_SNAPTEXT = 0x00001011;
    /**
     * 类型-----首末班车
     */
    int MODULE_TYPE_TRAININFO = 0x0000100B;

    /**
     * 接收播放控制指令广播action
     ****/
    public static final String ACTION_CMD_CONTROL = "com.ghyf.mplay.cmd_control";

    public static final String ACTION_CMD_LIVE ="com.ghts.player.LiveReceiver";


    String EXTRA_OBJ = "obj";
    String EXTRA_BUNDLE="bundle";
    String EXTRA_type = "type";
    /**
     * 设备查询自己的操作任务
     **/
    String CMD_QUERY_TASK = "com.mplay.cmd.query_task";
    /**
     * 设备上传自己的状态
     */
    String CMD_PUBLISH_STATUS = "com.play.cmd.publish_status";
    /**
     * 直播流
     ***/
    public static final short CMD_CONTROL_PLAYSTREAM = 320;
    /**
     * 播放本地
     ***/
    public static final short CMD_CONTROL_PLAYLOCAL = 321;

    /**
     * 设置声音
     */
    public static final short CMD_CONTROL_SETVOLUME = 322;
    /**
     * 音量控制-静音恢复
     ***/
    public static final short CMD_CONTROL_SOUNDON = 323;
    /**
     * 音量控制-静音
     ***/
    public static final short CMD_CONTROL_SOUNDOFF = 324;

    /**
     * 星期变化监听
     */
    public static final short CMD_CONTROL_WEEK = 325;

    /**
     * 发布紧急消息
     */
    public static final short CMD_TEXT_SETPROIRITY = 327;


    /**
     * 通过GPIO发布紧急消息
     */
    public static final short CMD_GPIO_SETPROIRITY = 336;

    /**
     * /**
     * 取消紧急信息命令
     ***/
    public static final short CMD_TEXT_STOP = 328;
    /**
     * 接收车站ATS信息
     */
    public static final short CMD_TEXT_ATSSTRAIN = 329;

    /**
     * 接收车载ATS信息
     */
    public static final short CMD_TEXT_ATSSTATION = 330;

    /**
     * 接收首末班车信息
     */
    public static final short CMD_TEXT_TRAININFO = 339;

    /**
     * 十分钟接收不到车站ATS信息，清空数据
     */
    public static final short CMD_CLEAR_ATSSTRAIN = 420;

    /**
     * 十分钟接收不到车载ATS信息，清空数据
     */
    public static final short CMD_CLEAR_ATSSTATION = 421;


    /**
     * 网管软件发布的开始直播流命令
     */
    public static final short CMD_MT_LIVE_ON = 331;
    /**
     * 网管软件发布的停止直播流命令
     */
    public static final short CMD_MT_LIVE_OFF = 332;

    /**
     * 网管软件发布的设备显示屏开机命令
     */
    public static final short CMD_MT_SCREEN_ON = 333;
    /**
     * 网管软件发布的设备显示屏关机命令
     */
    public static final short CMD_MT_SCREEN_OFF = 334;

    /**
     * 重启设备命令
     */
    public static final short CMD_MT_RESET_DEVICE = 335;

    /**
     * 设备开机命令
     */
    public static final short MT_POWER_ON = 337;
    /**
     * 设备关机命令
     */
    public static final short MT_POWER_OFF = 338;

    /**
     * 更新数据库紧急消息
     */
    public static final short UPDATE_EMERGENCY = 341;
    /**
     * 切换播表
     */
    public static final short CMD_FILE_SETLIST = 342;


    /**重启应用广播**/
    String RESOFTWARE_RECEIVER="com.mplay.resoftware.receiver";

    String COMMAND_INSTALL_APP=" pm install /sata/MPlay.apk";

}
