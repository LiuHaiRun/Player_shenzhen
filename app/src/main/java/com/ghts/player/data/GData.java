package com.ghts.player.data;

/**
 * Created by lijingjing on 17-8-19.
 */
public class GData {

    public static final String charset = "gb2312";
    public static final String charset_utf = "UTF-8";
    public static short CMD_RESULT_WAIT_EXEC = -100;	//等待处理

    public static int CMD_RESULT_DB_ERROR = -90;		//数据库访问错误

    //状态返回
    public static int CMD_RESULT_LOCAL_ERROR = -2;        //本地错误，例如参数非法
    public static int CMD_RESULT_CONN_ERROR = -1;                //网络故障
    public static int CMD_RESULT_ERROR = 0;            //执行失败，对于状态检测，故障原因由szError描述
    public static short CMD_RESULT_OK = 1;                        //执行成功
    //错误命令序号
    public static int CMD_RESULT_NO_CMD = 2;                    //没有指定的命令
    public static int CMD_RESULT_NO_LOGON = 3;                //没有登录，不使用
    //登录错误序号
    public static int CMD_RESULT_LOGON_FAIL = 4;                //错误的用户名或密码，不使用
    public static int CMD_RESULT_NO_RIGHT = 5;            //没有权限，不使用
    //紧急状态或者播表、版式控制错误序号
    public static int CMD_RESULT_LOW_RIGHT = 6;            //权限低，不能进行紧急状态的控制，或者是当前处于紧急状态，实施其他控制但权限较低
    public static int CMD_RESULT_LAYOUT_NOT_EXIST = 7;        //版式不存在
    public static int CMD_RESULT_LIST_NOT_EXIST = 8;            //播表不存在，不使用
    public static int CMD_RESULT_NOT_EMERGENT = 9;    //取消设置失败，因为没有处于紧急状态
    //TAKE命令错误序号
    public static int CMD_RESULT_NO_PST = 10;                //当前没有PST节目，不能执行TAKE命令
    //开始运行和停止运行错误序号
    public static int CMD_RESULT_NO_LIST = 11;                    //当前没有播表节目，不能执行命令
    public static int CMD_RESULT_NO_LAYOUT = 12;            //当前时间段没有版式，不能执行命令
    public static int CMD_ERSULT_INVALID_INDEX = 13;            //SKIP命令的版式组或者版式节目序号非法，不使用
    //没有板卡
    public static int CMD_RESULT_NO_BOARD = 14;                //不使用
    //直播命令的错误序号
    public static int CMD_RESULT_NO_LIVE_MODULE = 15;            //没有直播模块
    //指令
    public static int CMD_PUBLISH_TASK = 0;           //网管发布操作任务
    public static int CMD_QUERY_TASK_RESULT = 1;            //网管查询操作任务执行结果
    public static int CMD_QUERY_STATUS = 2;                //网管查询设备状态
    public static int CMD_QUERY_SINGLE_STATUS = 3;       //网管查询单个设备状态
    public static int CMD_QUERY_TASK = 4;                    //设备查询自己的操作任务
    public static int CMD_FEEDBACK_TASK_RESULT = 5;        //设备反馈操作任务的执行结果
    public static int CMD_PUBLISH_STATUS = 6;                //设备发布自己的状态
    //屏幕控制DLL的加载状态
    public static int DL_STATUS_OK = 0;          //屏幕控制DLL加载正常
    public static int DL_STATUS_SET_NONE = 1;            //未设置屏幕控制DLL
    public static int DL_STATUS_LOAD_FAIL = 2;        //屏幕控制DLL加载失败
    public static int DL_STATUS_INIT_FAIL = 3;     //屏幕控制DLL初始化失败
    public static int MAX_DL_STATUS = 4;
    //屏幕状态
    public static int SCREEN_STATUS_OFF = 0;            //关闭状态
    public static int SCREEN_STATUS_ON = 1;                    //开启状态
    public static int SCREEN_STATUS_ERROR = 2;                //故障状态
    public static int SCREEN_STATUS_UNKNOWN = 3;                //未知状态
    public static int MAX_SCREEN_STATUS = 4;
    //NET_ES_TYPE
    public static int NET_ES_CONTENT = 0;            //采用紧急内容，通过紧急模块实现，szContent维护紧急内容
    public static int NET_ES_LAYOUT = 1;            //采用紧急版式，szContent维护版式文件名
    public static int MAX_NET_ES_TYPE = 2;
    // NET_ES_SOUND_TYPE
    public static int NET_ES_SOUND_NONE = 0;        //不处理
    public static int NET_ES_SOUND_OFF = 1;        //关闭声音
    public static int NET_ES_SOUND_SET_VOLUME = 2;    //设置音量
    public static int MAX_NET_ES_SOUND_TYPE = 3;

    public static int MT_POWER_ON = 0;    //设备开机命令	网管软件提供该功能
    public static int MT_POWER_OFF = 1;        //设备关机命令	网管软件提供该功能
    public static int MT_RESET_DEVICE = 2;    //重新启动命令	网管软件提供该功能
    public static int MT_SCREEN_ON = 3;    //设备显示屏开机命令	网管软件提供该功能，紧急管理软件提供该功能
    public static int MT_SCREEN_OFF = 4;    //设备显示屏关机命令	网管软件提供该功能，紧急管理软件提供该功能
    public static int MT_PUBLISH_INFO = 5;    //发布紧急信息命令，即实施紧急状态	紧急管理软件提供该功能
    public static int MT_STOP_PUBLISH = 6;    //取消紧急信息命令，即结束紧急状态	紧急管理软件提供该功能
    public static int MT_START_RUNNING = 7;    //开始运行命令	网管软件提供该功能
    public static int MT_STOP_RUNNING = 8;    //停止运行命令	网管软件提供该功能
    public static int MT_TAKE = 9;    //TAKE命令	网管软件提供该功能
    public static int MT_SOUND_ON = 10;        //开启声音命令	网管软件提供该功能，紧急管理软件提供该功能
    public static int MT_SOUND_OFF = 11;    //关闭声音命令	网管软件提供该功能，紧急管理软件提供该功能
    public static int MT_SET_VOLUME = 12;        //设置音量（0～255）	网管软件提供该功能
    public static int MT_LIVE_ON = 13;        //开始直播	网管软件提供该功能
    public static int MT_LIVE_OFF = 14;        //停止直播	网管软件提供该功能
    public static int MAX_MACRO_TYPE = 15;

}
