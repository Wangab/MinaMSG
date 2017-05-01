package com.wangab.pushServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.wangab.pushServer.handler.MessageHandler;
import com.wangab.pushServer.ptotcol.MessageCodecFactory;
import com.wangab.pushServer.util.LogUtil;
/**
 * This is a publish Server.
 * @author wanganbang
 *
 */
public class PublishServer {
	private static final Logger LOG = LogUtil.getLogger();
	private static final int	PORT	= 6000;	// 定义监听端口
	private static IoAcceptor acceptor;
	public static void main(String[] args) throws IOException {
		startMinaServer();
	}
	/**
	 * Server start.
	 */
	public static void startMinaServer(){

//		DemuxingProtocolDecoder decoder = new DemuxingProtocolDecoder();//解码器装载类
//		decoder.addMessageDecoder(new MessageProtDecoder());//添加自定义解码器
//		DemuxingProtocolEncoder encoder = new DemuxingProtocolEncoder();//编码器装载类
//		encoder.addMessageEncoder(PublishMessage.class, new PublishMSGEncoder());//添加自定义编码器

		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
//		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(encoder, decoder));//注册编码器和解码器的拦截器到Accepter中去
		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new MessageCodecFactory()));
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());//注册日志拦截器到Accepter中

		((NioSocketAcceptor)acceptor).setReuseAddress(true);//设置的是主服务监听的端口可以重用
		((NioSocketAcceptor)acceptor).getSessionConfig().setReuseAddress(true);//设置每一个非主监听连接的端口可以重用
//		((NioSocketAcceptor)acceptor).getSessionConfig().setMaxReadBufferSize(2048*5000);
//		((NioSocketAcceptor)acceptor).getSessionConfig().setMinReadBufferSize(1024*1000);
//		((NioSocketAcceptor)acceptor).getSessionConfig().setReadBufferSize(2048*5000);//设置接收（输入）缓冲区大小
//		((NioSocketAcceptor)acceptor).getSessionConfig().setSendBufferSize(2048*5000);//设置发送（输出）缓冲区大小
		((NioSocketAcceptor)acceptor).getSessionConfig().setSoLinger(0);//连接关闭，Sockte底层立马关闭返回
		((NioSocketAcceptor)acceptor).getSessionConfig().setTcpNoDelay(true);//不允许延迟发送
//		((NioSocketAcceptor)acceptor).setBacklog(100);//设置主服务监听端口的监听队列的最大值为50，如果当前已经有50个连接，再新的连接来将被服务器拒绝

		acceptor.getSessionConfig().setReadBufferSize(2048*5000);
		acceptor.getStatistics().setThroughputCalculationInterval(10);//设置吞吐量时间间隔
		acceptor.getStatistics().updateThroughput(System.currentTimeMillis());//设置吞吐量大小
		acceptor.setHandler(new MessageHandler());// 指定业务逻辑处理器
//		acceptor.setHandler(new TextHandler());
		acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));// 设置端口号
		try {
			LOG.info("Server binded " + InetAddress.getLocalHost().getHostAddress() + "-" + PORT);
			acceptor.bind();// 启动监听
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获得客户端连接总数
	 * @return
	 */
	public static int getConNum(){

		int num = acceptor.getManagedSessionCount();
		System.out.println("num:" + num);

		return num;
	}

	/**
	 * 向每个客户端发送消息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void sendConMessage(){

		IoSession session;

		Map conMap = acceptor.getManagedSessions();

		Iterator iter = conMap.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			session = (IoSession)conMap.get(key);
			session.write("" + key.toString());
		}


	}
}