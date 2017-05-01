package com.wanag.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SCTestReceiver {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Clenit();
//		sc.close();
    }

    private static void Clenit() throws IOException {
        Socket sc = new Socket("192.168.31.155", 6000);
        OutputStream out = sc.getOutputStream();
        byte[] msgByteArray = LoginGenerator();
        out.write(msgByteArray);


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int a = 1;
        while (true) {
            System.out.println("---- >> 接收线程ID:" + Thread.currentThread().getId() + "<< ----");
            InputStream in = sc.getInputStream();
            MessageParser(in, "UTF-8");
            System.out.println("接收数量：" + (a++));
        }
    }

    public static void MessageParser(InputStream in, String charseName) throws IOException {
        //开始标志
        byte[] stx = new byte[1];
        in.read(stx);
        int istx = stx[0];
        System.out.println("开始标志：" + istx);
        //数据包长度
        byte[] totallen = new byte[2];
        in.read(totallen);
        int itotallen = ((totallen[0]) & 0xff << 8)
                | (((totallen[1]) & 0xff));
        System.out.println("消息总长：" + itotallen);
        //消息类型
        byte[] msgtp = new byte[4];
        in.read(msgtp);
        int msgType = ((msgtp[0] & 0xff) << 24)
                | ((msgtp[1] & 0xff) << 16)
                | ((msgtp[2] & 0xff) << 8)
                | (msgtp[3] & 0xff);
        System.out.println("消息类型：" + msgType);
        //消息ID
        byte[] seq = new byte[20];
        in.read(seq);
        System.out.println("消息ID：" + new String(seq).trim());
        //用户ID
        byte[] userid = new byte[8];
        in.read(userid);
        int userID = ((userid[0] & 0xff) << 56)
                | ((userid[1] & 0xff) << 48)
                | ((userid[2] & 0xff) << 40)
                | ((userid[3] & 0xff) << 32)
                | ((userid[4] & 0xff) << 24)
                | ((userid[5] & 0xff) << 16)
                | ((userid[6] & 0xff) << 8)
                | ((userid[7] & 0xff));
        System.out.println("消息发送者ID：" + userID);
        //消息内容长度
        byte[] msgLength = new byte[4];
        in.read(msgLength);
        int msgCount = ((msgLength[0] & 0xff) << 24)
                | ((msgLength[1] & 0xff) << 16)
                | ((msgLength[2] & 0xff) << 8)
                | (msgLength[3] & 0xff);
        System.out.println("消息内容长度：" + msgCount);
        //消息内容
        byte[] chartMSG = new byte[msgCount];
        in.read(chartMSG);
        System.out.println("消息内容：" + new String(chartMSG, charseName));
        //结束标志
        byte[] etx = new byte[1];
        in.read(etx);
        int etxFlag = etx[0];
        System.out.println("结束标志：" + etxFlag);
    }

    public static byte[] LoginGenerator() {
        byte[] msgByteArray = new byte[51];
        //下面是登陆包
        msgByteArray[0] = (byte) (47 >> 24);
        msgByteArray[1] = (byte) (47 >> 16);
        msgByteArray[2] = (byte) (47 >> 8);
        msgByteArray[3] = (byte) 47;

        //设置消息起始标记
        msgByteArray[4] = (byte) 3;
        //设置包长
        msgByteArray[5] = (byte) (47 >> 8);
        msgByteArray[6] = (byte) 47;
        //设置消息类型
        String msgtype = "0000";
        msgtype.getBytes(0, 4, msgByteArray, 7);
        //设置消息id
        msgByteArray[11] = (byte) 0;
        msgByteArray[12] = (byte) 0;
        msgByteArray[13] = (byte) 0;
        msgByteArray[14] = (byte) 0;
        String msgid = "2013--11--30--SC";
        msgid.getBytes(0, 16, msgByteArray, 15);
        //设置内容
        msgByteArray[31] = (byte) (123l >> 56);
        msgByteArray[32] = (byte) (123l >> 48);
        msgByteArray[33] = (byte) (123l >> 40);
        msgByteArray[34] = (byte) (123l >> 32);
        msgByteArray[35] = (byte) (123l >> 24);
        msgByteArray[36] = (byte) (123l >> 16);
        msgByteArray[37] = (byte) (123l >> 8);
        msgByteArray[38] = (byte) (123l);

        String msg = "loginlogin";
        msg.getBytes(0, 10, msgByteArray, 39);
        //	设置包尾
        msgByteArray[50] = (byte) 7;
        return msgByteArray;
    }

    private static void printBinaryByte(byte b) {
        for (int i = 7; i >= 0; --i) {
            if (((1 << i) & b) == 0) {
                System.out.print("0");
            } else {
                System.out.print("1");
            }
        }
        System.out.println();
    }
}
