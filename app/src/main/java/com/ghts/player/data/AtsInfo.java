package com.ghts.player.data;

import android.content.Context;
import android.content.Intent;
import android.util.Xml;

import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by lijingjing on 17-9-8.
 */
public class AtsInfo implements Runnable {
    private static String TAG = "AtsInfo";
    public static boolean isStop = false;
    public static final String charset = "gb2312";
    private String ip;
    private int port_server;
    private Context mContext;
    private ByteArrayInputStream baInPacketHeader;
    private boolean runFlag = true;
    private DatagramPacket pkt_in;
    private MulticastSocket socket;//车站组播
//    private Thread myThread;
//    private long time_countdown = Const.COUNTDOWNTIME;
    private DatagramSocket dataSocket; //车载单播
    private byte showMode;

    public AtsInfo(Context context, String ip, int serverPort) {
        mContext = context;
        this.ip = ip;
        port_server = serverPort;
//        myThread = new Thread(myRunnable);
//        myThread.start();
    }

    public void run() {
        try {
            byte[] buf_in = new byte[50000];
            if (Const.globalBean.getIsTrain().equals("0")) { //车站
                InetAddress address = InetAddress.getByName(ip);
                socket = new MulticastSocket(port_server);
                socket.joinGroup(address);
                pkt_in = new DatagramPacket(buf_in, buf_in.length);
                LogUtil.i(TAG, "车站组播..." + ip + "--" + port_server);
            } else if (Const.globalBean.getIsTrain().equals("1")) {//车载
                if (dataSocket == null) {
                    dataSocket = new DatagramSocket(null);
                    dataSocket.setReuseAddress(true);
                    dataSocket.bind(new InetSocketAddress(port_server));
                }
                pkt_in = new DatagramPacket(buf_in, buf_in.length);
            }
            while (true) {
                try {
                    if (Const.globalBean.getIsTrain().equals("0")) {//车站
                        socket.receive(pkt_in);
                        socket.getReceiveBufferSize();
                     } else if (Const.globalBean.getIsTrain().equals("1")) {//车载
                        dataSocket.receive(pkt_in);
                    }
                    int recvLength = pkt_in.getLength();
                    byte[] buf_recv = pkt_in.getData();
                    assert (buf_recv.length == recvLength);
                    /**
                     * 根据是车站还是车载，解析不同数据
                     */
                    if (Const.globalBean.getIsTrain().equals("1")) { //车载
                        baInPacketHeader = new ByteArrayInputStream(buf_recv, 0, recvLength);
                        DataInputStream dInHeader = new DataInputStream(baInPacketHeader);
                        short wCRC = dInHeader.readShort();
                        byte[] buf_temp = new byte[6];
                        dInHeader.read(buf_temp, 0, 6);
                        String szTrainCode = new String(buf_temp, 0, 6, GData.charset).trim();
                        byte buType = dInHeader.readByte();
                         int iDataSize = dInHeader.readInt();
                        LogUtil.i(TAG, "Ats显示模式:" + buType+"数据包大小"+iDataSize);
                        byte[] xml = new byte[iDataSize - 14];
                        dInHeader.read(xml, 0, iDataSize - 14);
                        InputStream inputStream = new ByteArrayInputStream(xml);
                        StrainBean strainBean = parseStrainXml(inputStream);
                        if (strainBean != null) {
                            //更新ATS信息
                            Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                            myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_ATSSTRAIN);
                            myIntent.putExtra(ConstantValue.EXTRA_OBJ, strainBean);
                            myIntent.putExtra(ConstantValue.EXTRA_BUNDLE, buType);
                            mContext.sendBroadcast(myIntent);
//                            time_countdown = Const.COUNTDOWNTIME;
                        }
                    } else if (Const.globalBean.getIsTrain().equals("0")) {//车站
                        baInPacketHeader = new ByteArrayInputStream(buf_recv, 0, recvLength);
                        DataInputStream dInHeader = new DataInputStream(baInPacketHeader);
                        short wCRC = dInHeader.readShort();
                        byte[] buf_temp = new byte[4];
                        int num = dInHeader.readInt();
                        showMode = dInHeader.readByte();
                        int iDataSize = dInHeader.readInt();
                        byte[] xml = new byte[iDataSize - 11];
                        dInHeader.read(xml, 0, iDataSize - 11);
                        InputStream inputStream = new ByteArrayInputStream(xml);
                         LogUtil.i(TAG, "Ats显示模式:" + showMode+"数据包大小"+iDataSize);
                        ArrayList<StationBean> beanList = parseStationXml(inputStream);
                        if (beanList != null && beanList.size() > 0) {
                            Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                            myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_ATSSTATION);
                            myIntent.putExtra(ConstantValue.EXTRA_OBJ, beanList);
                            myIntent.putExtra(ConstantValue.EXTRA_BUNDLE, showMode);
                            mContext.sendBroadcast(myIntent);
//                            time_countdown = Const.COUNTDOWNTIME;
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, " --  catch IOException: receive() ", e);
                }
            }
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 60);
                run();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            LogUtil.e(TAG, "-- catch SocketException: ", e);
        }
    }

    /**
     * 解析车载xml数据
     */
    private StrainBean parseStrainXml(InputStream inputStream) {
        StrainBean strainBean = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {//如果开始文档不等于结束标签,type必须赋值，否则会出现死循环
                switch (type) {
                    case XmlPullParser.START_TAG://如果是开始标签
                        //获取开始标签的名字
                        String startname = parser.getName();
                        if ("Time".equals(startname)) {
                            strainBean = new StrainBean();
                            strainBean.setTimeName(parser.getAttributeValue("", "Name"));
                            strainBean.setTineEnName(parser.getAttributeValue("", "EnName"));
                        } else if ("NextStation".equals(startname)) {
                            strainBean.setNextStationName(parser.getAttributeValue("", "Name"));
                            strainBean.setNextStationEnName(parser.getAttributeValue("", "EnName"));
                            strainBean.setNextStationTitle(parser.getAttributeValue("", "Title"));
                            strainBean.setNextStationEnTitle(parser.getAttributeValue("", "EnTitle"));
                        } else if ("DstStation".equals(startname)) {
                            strainBean.setDstStationName(parser.getAttributeValue("", "Name"));
                            strainBean.setDstStationEnName(parser.getAttributeValue("", "EnName"));
                            strainBean.setDstStationTitle(parser.getAttributeValue("", "Title"));
                            strainBean.setDstStationEnTitle(parser.getAttributeValue("", "EnTitle"));
                        }
                        break;
                    case XmlPullParser.END_TAG://如果是结束标签
                        break;
                }
                type = parser.next();//通过解析器拿到下一个的值
            }
        } catch (XmlPullParserException e) {
            LogUtil.e("ats解析失败", e.toString());
        } catch (IOException e) {
            LogUtil.e("ats解析失败", e.toString());
        }
        LogUtil.e(TAG + "--strainBean--", strainBean.toString());
        return strainBean;
    }

    /**
     * 解析车站xml数据
     *
     * @param inputStream
     */
    private ArrayList<StationBean> parseStationXml(InputStream inputStream) {
        StationBean stationBean = null;
        ArrayList<StationBean> beanList = null;
        try {
            XmlPullParser parse = Xml.newPullParser();
            parse.setInput(inputStream, "UTF-8");
            int type = parse.getEventType();
            boolean isId = false;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        //获取开始标签的名字
                        String startname = parse.getName();
                        if ("ATS".equals(startname)) {
                        } else if (startname.equals("P" + Const.atsId)) {
                            beanList = new ArrayList<StationBean>();
                            beanList.clear();
                            isId = true;
                        } else if (startname.startsWith("T") && isId) {
                            stationBean = new StationBean();
                            stationBean.setTime(parse.getAttributeValue("", "Time"));
                            String dst2 = parse.getAttributeValue("", "Dst");
                            stationBean.setDst(dst2);
                            stationBean.setEnTime(parse.getAttributeValue("", "EnTime"));
                            stationBean.setEnDst(parse.getAttributeValue("", "EnDst"));
                            beanList.add(stationBean);
                            LogUtil.e(TAG, stationBean.toString());
                        }
                        break;
                    case XmlPullParser.END_TAG://如果是结束标签
                        String sname = parse.getName();
                         if ("ATS".equals(sname)) {
                        } else if (sname.startsWith("P") && isId) {
                            isId = false;
                        }
                        break;
                }
                type = parse.next();
            }
        } catch (XmlPullParserException e) {
            LogUtil.e("ats解析失败", e.toString());
        } catch (IOException e) {
            LogUtil.e("ats解析失败", e.toString());
        }
        return beanList;
    }

    //倒计时的线程
//    private Runnable myRunnable = new Runnable() {
//        public void run() {
//            while (runFlag) {
//                if (time_countdown > 0) {
//                    // 倒计时时间减一秒。
//                    time_countdown -= 1000;
//                    if (time_countdown <= 0) { // 倒计时为零后重新计时
//                        time_countdown = Const.COUNTDOWNTIME;
//                        Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
//                        myIntent.putExtra("cmd", ConstantValue.CMD_CLEAR_ATSSTATION);
//                        mContext.sendBroadcast(myIntent);
//                    }
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    LogUtil.i(TAG, "InterruptedException...\r\n" + e);
//                }
//            }
//        }
//    };
}
