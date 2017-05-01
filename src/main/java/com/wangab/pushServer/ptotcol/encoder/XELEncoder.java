package com.wangab.pushServer.ptotcol.encoder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.wangab.pushServer.ptotcol.entity.Message;
import com.wangab.pushServer.ptotcol.entity.impl.PublicMessageContent;
import com.wangab.pushServer.util.LogUtil;

public class XELEncoder extends ProtocolEncoderAdapter {
    private static final Logger LOG = LogUtil.getLogger();

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {

        LOG.info("Publish message encoding .....");
        Message msg = (Message) message;

        int startFlag = msg.getStx();
        String msgid = msg.getMessageID();
        String pubMGContent = ((PublicMessageContent) (msg.getMsgContent())).getMsg();
        long userid = ((PublicMessageContent) (msg.getMsgContent())).getUserid();
        int ext = msg.getEtx();
        int length = pubMGContent.getBytes().length;
        int totalen = 40 + length;

        IoBuffer variableHeaderBuff = IoBuffer.allocate(totalen).setAutoExpand(true);
        //包开始标志
        variableHeaderBuff.put((byte) startFlag);
        //包长
        variableHeaderBuff.put((byte) (totalen >> 8));
        variableHeaderBuff.put((byte) totalen);
        //消息id
        variableHeaderBuff.put((byte) 0);
        variableHeaderBuff.put((byte) 0);
        variableHeaderBuff.put((byte) 0);
        variableHeaderBuff.put((byte) 3);
        //流水账号
        byte[] mid = new byte[20];
        byte[] tempmid = msgid.getBytes();
        for (int i = 0; i < tempmid.length; i++) {
            mid[i] = tempmid[i];
        }
        variableHeaderBuff.put(mid);
        //推送包内容
        //设置UserID
        variableHeaderBuff.put((byte) (userid >> 56));
        variableHeaderBuff.put((byte) (userid >> 48));
        variableHeaderBuff.put((byte) (userid >> 40));
        variableHeaderBuff.put((byte) (userid >> 32));
        variableHeaderBuff.put((byte) (userid >> 24));
        variableHeaderBuff.put((byte) (userid >> 16));
        variableHeaderBuff.put((byte) (userid >> 8));
        variableHeaderBuff.put((byte) userid);
        //设置消息长度
        variableHeaderBuff.put((byte) (length >> 24));
        variableHeaderBuff.put((byte) (length >> 16));
        variableHeaderBuff.put((byte) (length >> 8));
        variableHeaderBuff.put((byte) length);
        //设置消息内容
        variableHeaderBuff.put(pubMGContent.getBytes());
        //包尾
        variableHeaderBuff.put((byte) ext);

        variableHeaderBuff.flip();
        out.write(variableHeaderBuff);
//		out.flush();
        LOG.info("Publish message encoding end");
    }

}
