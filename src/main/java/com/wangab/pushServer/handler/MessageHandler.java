package com.wangab.pushServer.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.wangab.pushServer.ptotcol.entity.Message;
import com.wangab.pushServer.ptotcol.entity.impl.LoginMessageContent;
import com.wangab.pushServer.ptotcol.entity.impl.PublicMessageContent;
import com.wangab.pushServer.util.LogUtil;

public class MessageHandler extends IoHandlerAdapter {

    private static Map<Long, List<IoSession>> sessionMaping = new HashMap<Long, List<IoSession>>();
    private static final Logger LOG = LogUtil.getLogger();
    private static List<Map<Long, Long>> templist = new ArrayList<Map<Long, Long>>();

    public void sessionCreated(IoSession session) {
        // 显示客户端的ip和端口
        LOG.info("创建连接：" + session.getRemoteAddress().toString() + " SID:" + session.getId());
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        Message msg = (Message) message;
        if ("0000".equals(msg.getMsgType())) {
            //处理登录消息
            LOG.info("开始标示：" + msg.getStx());
            LOG.info("包总长度：" + msg.getTotalLen());
            LOG.info("消息类型：" + msg.getMsgType());
            LOG.info("流水号：" + msg.getMessageID());
            LOG.info("消息内容：" + ((LoginMessageContent) (msg.getMsgContent())).getUserid() + "-" + ((LoginMessageContent) (msg.getMsgContent())).getExtend());
            LOG.info("包尾标示：" + msg.getEtx());

            Long userid = ((LoginMessageContent) (msg.getMsgContent())).getUserid();
            List<IoSession> sessionList = sessionMaping.get(userid);
            if (sessionList == null) {
                sessionList = new ArrayList<IoSession>();
            }
            if (!sessionList.contains(session)) {
                sessionList.add(session);
            }
            synchronized (sessionMaping) {
                sessionMaping.put(userid, sessionList);
            }
            synchronized (templist) {
                Map<Long, Long> map = new HashMap<Long, Long>();
                map.put(session.getId(), userid);
                templist.add(map);
            }
        } else if ("0003".equals(msg.getMsgType())) {
            //处理推送消息
            LOG.info("开始标示：" + msg.getStx());
            LOG.info("包总长度：" + msg.getTotalLen());
            LOG.info("消息类型：" + msg.getMsgType());
            LOG.info("流水号：" + msg.getMessageID());
            LOG.info("消息内容：" + ((PublicMessageContent) (msg.getMsgContent())).getUserid() + "-" + ((PublicMessageContent) (msg.getMsgContent())).getMsg());
            LOG.info("包尾标示：" + msg.getEtx());

            Long userid = ((PublicMessageContent) (msg.getMsgContent())).getUserid();
//			PublishMessage msgcontent = new PublishMessage();
//			msgcontent.setContent(((PublicMessageContent)(msg.getMsgContent())).getMsg());
            if (userid == 0) {
                Collection<List<IoSession>> mapValus = sessionMaping.values();
                Iterator<List<IoSession>> itmapval = mapValus.iterator();
                while (itmapval.hasNext()) {
                    List<IoSession> list = itmapval.next();
                    Iterator<IoSession> itr = list.iterator();
                    while (itr.hasNext()) {
                        IoSession sion = itr.next();
                        sion.write(msg);
                    }
                }
            } else {
                List<IoSession> userSessionList = sessionMaping.get(userid);
                if (userSessionList != null) {
                    Iterator<IoSession> it = userSessionList.iterator();
                    while (it.hasNext()) {
                        IoSession sion = it.next();
                        sion.write(msg);
                    }
                } else {
                    LOG.warn("User :" + userid + " User Sesion List is null .");
                }
            }

        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOG.warn("关闭连接：" + session.getRemoteAddress().toString() + " SID:" + session.getId());
        Long userid = null;
        Iterator<Map<Long, Long>> it = templist.iterator();
        while (it.hasNext()) {
            Map<Long, Long> uts = (HashMap<Long, Long>) it.next();
            if (uts.containsKey(session.getId())) {
                userid = (Long) (uts.get(session.getId()));
                synchronized (templist) {
                    it.remove();
                }
                break;
            }
        }
        if (userid != null) {
            synchronized (sessionMaping) {
                sessionMaping.get(userid).remove(session);
                if (sessionMaping.get(userid).size() == 0) {
                    sessionMaping.remove(userid);
                }
            }
        } else {
            LOG.warn("Userid is null and session :" + session.getId() + " is closed !");
        }

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        System.out.println("22222222222222" + status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
//			session.close();
        LOG.error("Connetction is exception :" + cause.getMessage());
        cause.printStackTrace();
    }


}