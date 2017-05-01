package com.wangab.pushServer.ptotcol.decoder;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.wangab.pushServer.ptotcol.entity.IMessageContent;
import com.wangab.pushServer.ptotcol.entity.impl.LoginMessageContent;
import com.wangab.pushServer.ptotcol.entity.impl.PublicMessageContent;
import com.wangab.pushServer.util.LogUtil;

public class MessageContentDecoder {
    private static final Logger LOG = LogUtil.getLogger();

    public static IMessageContent decod(byte[] copyOfRange, String msgType) {
        if ("0000".equals(msgType)) {
            LOG.info("MessageContentDecoder Resaved Login message.");
            LoginMessageContent lmg = new LoginMessageContent();
            long userid = ((long) copyOfRange[7] & 0xff)
                    | (((long) copyOfRange[6] & 0xff) << 8)
                    | (((long) copyOfRange[5] & 0xff) << 16)
                    | (((long) copyOfRange[4] & 0xff) << 24)
                    | (((long) copyOfRange[3] & 0xff) << 32)
                    | (((long) copyOfRange[2] & 0xff) << 40)
                    | (((long) copyOfRange[1] & 0xff) << 48)
                    | (((long) copyOfRange[0] & 0xff) << 56);
            lmg.setUserid(userid);
            lmg.setExtend(new String(copyOfRange(copyOfRange, 8, 18)));
            return lmg;
        } else if ("0003".equals(msgType)) {
            LOG.info("MessageContentDecoder Resaved Publish message.");
            PublicMessageContent pmg = new PublicMessageContent();
            long userid = ((long) copyOfRange[7] & 0xff)
                    | (((long) copyOfRange[6] & 0xff) << 8)
                    | (((long) copyOfRange[5] & 0xff) << 16)
                    | (((long) copyOfRange[4] & 0xff) << 24)
                    | (((long) copyOfRange[3] & 0xff) << 32)
                    | (((long) copyOfRange[2] & 0xff) << 40)
                    | (((long) copyOfRange[1] & 0xff) << 48)
                    | (((long) copyOfRange[0] & 0xff) << 56);
            pmg.setUserid(userid);
            int d = (copyOfRange[8] & 0xff) << 24;
            int c = (copyOfRange[9] & 0xff) << 16;
            int b = (copyOfRange[10] & 0xff) << 8;
            int a = copyOfRange[11] & 0xff;
            pmg.setMsgLength(a + b + c + d);
            byte[] bt = copyOfRange(copyOfRange, 12, copyOfRange.length);
            pmg.setMsg(new String(bt));
            return pmg;
        }
        return null;
    }

    @SuppressWarnings("unused")
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

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }
}
