package com.ghts.player.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.ghts.player.enumType.POS;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by lijingjing on 17-9-14.
 */
public class ShellUtils {
    private static final String TAG = "ShellUtils";
    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    public static boolean settime(Context context) {
        try {
            LogUtil.i(TAG, "settime=");
            execShellStr("date -s 20131213.174500");
        } catch (Exception e) {

        }
        return true;
    }

    public static boolean setIp(Context context) {
        try {
            Log.i(TAG, "setIp=");
            execShellStr("busybox ifconfig eth0 192.168.0.197");
        } catch (Exception e) {

        }
        return true;
    }

    public static boolean execSh(Context context) {
        try {
            Log.i(TAG, "setIp=");
            execShellStr("./sata/eth0.sh");
        } catch (Exception e) {

        }
        return true;
    }


    public static void execShellStr(String cmd) {
        Log.i(TAG, "retString=" + cmd);
        //"su", "su", "sh","-c","su", "su","-c",
        String[] cmdStrings = new String[]{"sh", "-c", "su", cmd};
        String retString = "";
        try {
            Process process = Runtime.getRuntime().exec(cmdStrings);
            BufferedReader stdout =
                    new BufferedReader(new InputStreamReader(
                            process.getInputStream()), 7777);
            BufferedReader stderr =
                    new BufferedReader(new InputStreamReader(
                            process.getErrorStream()), 7777);
            String line = null;
            while ((null != (line = stdout.readLine()))
                    || (null != (line = stderr.readLine()))) {
                if (null != line) {
                    retString += line + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "retString=" + retString);
    }

    /**
     * check whether has root permission
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot  whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command list
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command array
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     *
     * @param command         command
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command list
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command array
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     * <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and {@link CommandResult#errorMsg} is
     * null.</li>
     * <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     * </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * result of command,
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
     * linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {

        /**
         * result of command
         **/
        public int result;
        /**
         * success message of command result
         **/
        public String successMsg;
        /**
         * error message of command result
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    /**
     * 静默安装，1-安装成功，或没有升级文件，2-升级安装出现异常，-1-程序异常
     */
    public static int installBySlient(Context context, String filePath) {
        int result = 0;
        try {
            File file = new File(filePath);
            if (filePath == null || filePath.length() == 0
                    || (file = new File(filePath)) == null
                    || file.length() <= 0 || !file.exists() || !file.isFile()) {
                return 1;
            }

            String[] args = {"pm", "install", "-r", filePath};

            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();
            try {
                process = processBuilder.start();
                successResult = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }

                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = 2;
            } catch (Exception e) {
                e.printStackTrace();
                result = 2;
            } finally {
                try {
                    if (successResult != null) {
                        successResult.close();
                    }
                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (process != null) {
                    process.destroy();
                }
            }

            if (successMsg.toString().contains("Success")
                    || successMsg.toString().contains("success")) {
                result = 1;
            } else {
                result = 2;
            }
            LogUtil.e("App升级信息:sucess" + successMsg, "ErrorMsg:" + errorMsg);
        } catch (Exception e) {
            result = -1;
        }
        return result;
    }


    /**
     * 定时开关机
     */
    public static void reboot() {
        try {
            Log.v(TAG, "root Runtime->reboot");
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/reboot"});
            proc.waitFor();
              /*Intent reboot = new Intent(Intent.ACTION_REBOOT);
                        reboot.putExtra("nowait", 1);
                        reboot.putExtra("interval", 1);
                        reboot.putExtra("window", 0);
                        sendBroadcast(reboot); */

//            PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);
//            pManager.reboot("重启");
//            ShellUtils.CommandResult cr = ShellUtils.execCommand("/sata/reboot.sh", ShellUtils.checkRootPermission());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
