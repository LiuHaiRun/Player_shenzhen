package com.ghts.player.manager;

import com.ghts.player.utils.LogUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class WatchDog {

    public static WatchDog watchDog;
    public void WatchDog(){
       if(watchDog == null){
           watchDog = WatchDog.this;
       }
    }

    public static PlayerTimer playTimer = null;
    private Timer timer = null;
    /**
     * start Timer
     */
    public synchronized void startPlayerTimer() {
        //        stopPlayerTimer();
        LogUtil.e("开启进程","喂狗信息");
        if (timer == null) {
            playTimer = new PlayerTimer();
            Timer m_musictask = new Timer();
            m_musictask.schedule(playTimer,  0, 3 * 1000);
        }
    }

    /**
     * stop Timer
     */
    public synchronized void stopPlayerTimer() {
        try {
            LogUtil.e("停止进程","停止喂狗信息");
            if (playTimer != null) {
                playTimer.cancel();
                playTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int i = 0;
    public class PlayerTimer extends TimerTask {
        public PlayerTimer() {
        }
        public void run() {
            i++;
            check(i);
        }
    }

    public void check(int i) {
        if (i % 2 == 0) {
            LogUtil.e("喂狗1",i+"");

            WriteMyGPIO(1, 11, 2);  //echo 11 > IOCard_State2
            WriteMyGPIO(0, 10, 2);  //echo 11 > IOCard_State1
        } else {
            LogUtil.e("喂狗2",i+"");

            WriteMyGPIO(1, 10, 2);  //echo 11 > IOCard_State2
            WriteMyGPIO(0, 11, 2);  //echo 11 > IOCard_State1
        }
    }

    public static int WriteMyGPIO(int level) {
        int Ret = 0;
        try {
            Process su;
            String cmd = "";
            su = Runtime.getRuntime().exec("/system/bin/sh");
            cmd = "echo " + level + " > /sys/kernel/kobj_gpio/OUTPUT_enable" + "\n" + "exit\n";
            OutputStream OS = su.getOutputStream();
            OS.write(cmd.getBytes());
            try {
                if (su.waitFor() != 0) {
                    throw new SecurityException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SecurityException();
        }
        return Ret;
    }

    private static final String GPIO_output[] = {"GPI_LED3", "GPI_LED1", "GPI_LED4", "GPI_LED2"};  //对应实物     1  2
    private static final String GPIO_IO[] = {"IOCard_State1", "IOCard_State2", "IO_COM1", "IO_COM2", "OUTPUT_enable"};

    /*
    * 功能：写入MyGPIO的高低电平
    * 函数名：WriteMyGPIO(int num, int level, int type) num 指哪一个GPIO level指高低电平  type 单向 双向
    * 返回值：int failure -1 success 0
    */
    public static int WriteMyGPIO(int num, int level, int type){
        int Ret = 0;
        try {
            Process su;
            String cmd = "";
            su = Runtime.getRuntime().exec("/system/bin/sh");
            if(type == 1){
                cmd = "echo " + level + " > /sys/kernel/kobj_gpio/" +
                        GPIO_output[num] + "\n" + "exit\n";
            } else if(type == 2) {
                cmd = "echo " + level + " > /sys/kernel/kobj_gpio/" +
                        GPIO_IO[num] + "\n" + "exit\n";
            }else {
                type = -1;
            }
            OutputStream OS = su.getOutputStream();
            OS.write(cmd.getBytes());
            //OS.flush();
            try {
                if(su.waitFor() != 0){
                    throw new SecurityException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SecurityException();
        }
        return Ret;
    }
}
