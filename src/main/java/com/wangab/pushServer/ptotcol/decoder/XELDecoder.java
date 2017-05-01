package com.wangab.pushServer.ptotcol.decoder;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.wangab.pushServer.ptotcol.entity.IMessageContent;
import com.wangab.pushServer.ptotcol.entity.Message;
import com.wangab.pushServer.util.LogUtil;

public class XELDecoder extends CumulativeProtocolDecoder {
    private static final Logger LOG = LogUtil.getLogger();
    public long num = 1;

    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        boolean d = in.prefixedDataAvailable(4, 2048 * 5000);
        if (d)

        {
            LOG.info("Resaved Legal data number is :" + (num++));
            int remainingLength = in.getInt();
            byte[] remain = new byte[remainingLength];
            in.get(remain);
            //解析起始标志
            int stx = remain[0];
            //解析包长
            int totalLen = ((remain[1]) & 0xff << 8) | (((remain[2]) & 0xff));
            //解析消息类型
            byte[] msgtp = new byte[4];
            msgtp[0] = remain[3];
            msgtp[1] = remain[4];
            msgtp[2] = remain[5];
            msgtp[3] = remain[6];
            String msgType = new String(msgtp).trim();
            //解析消息ID
            byte[] mid = new byte[20];
            for (int i = 0; i < 20; i++) {
                mid[i] = remain[7 + i];
            }
            String messageID = new String(mid).trim();
            //解析内容
            byte[] msg = Arrays.copyOfRange(remain, 27, totalLen - 1);
            IMessageContent msgContent = MessageContentDecoder.decod(msg, msgType);
            //解析包尾
            int etx = remain[remainingLength - 1];

            Message msgobj = new Message();
            msgobj.setEtx(etx);
            msgobj.setMessageID(messageID);
            msgobj.setMsgContent(msgContent);
            msgobj.setMsgType(msgType);
            msgobj.setStx(stx);
            msgobj.setTotalLen(totalLen);
            out.write(msgobj);
            return true;
        } else

        {
            LOG.warn("Incomplete data continue to make a complete cycle");
            return false;
        }
    }

}
