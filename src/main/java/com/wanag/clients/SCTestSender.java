package com.wanag.clients;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SCTestSender {

	static int n = 1;
	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		Socket sc = new Socket("192.168.31.155", 6000);
		OutputStream out = sc.getOutputStream();
		for(int a=0;a<10000000;a++){
			executorService.submit(new Runnable() {
				public void run() {
					try {
						//也可以在这里开socket连接已测试大量连接
						sengMSG(out);
						System.out.println("线程[" + Thread.currentThread().getId()  + "]发送第["+ n++ + "]条数据");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		System.out.println(executorService.isTerminated());
		System.out.println(executorService.isShutdown());
//		sc.close();
	}

	private static void sengMSG(OutputStream out) throws IOException, InterruptedException {
		String t = "hello! this is a IM framework";
		int msgCount = t.getBytes().length;
		int prefixLength = 4;
		int pkgCount = 40 + msgCount;
		byte[] msgArray = new byte[prefixLength + pkgCount];
		//设置mina底层的数据包总体长度
		msgArray[0] = (byte)(pkgCount>>24);
		msgArray[1] = (byte)(pkgCount>>16);
		msgArray[2] = (byte)(pkgCount>>8);
		msgArray[3] = (byte)pkgCount;
		//设置消息起始标记-1字节
		msgArray[4] = (byte)3;
		//设置逻辑数据包长-2字节
		msgArray[5] = (byte)(pkgCount >> 8);
		msgArray[6] = (byte)pkgCount;
		//设置消息类型-4字节
		String type = "0003";
		type.getBytes(0, 4, msgArray, 7);
		//设置消息id-20字节
		msgArray[11] = (byte)0;
		msgArray[12] = (byte)0;
		msgArray[13] = (byte)0;
		msgArray[14] = (byte)0;
		String id = "2013--11--30--SC";
		id.getBytes(0, 16, msgArray, 15);
		//设置内容
		//用户ID-8字节
		msgArray[31] = (byte) (0l >> 56);
		msgArray[32] = (byte) (0l >> 48);
		msgArray[33] = (byte) (0l >> 40);
		msgArray[34] = (byte) (0l >> 32);
		msgArray[35] = (byte) (0l >> 24);
		msgArray[36] = (byte) (0l >> 16);
		msgArray[37] = (byte) (0l >> 8);
		msgArray[38] = (byte)(0l);
		//消息内容数据长度-4字节
		msgArray[39] = (byte)(msgCount >> 24);
		msgArray[40] = (byte)(msgCount >> 16);
		msgArray[41] = (byte)(msgCount >> 8);
		msgArray[42] = (byte)msgCount;
		//设置消息内容
		t.getBytes(0, msgCount, msgArray, 43);
		//设置包尾-1字节
		msgArray[prefixLength + pkgCount -1] =(byte)7;
		out.write(msgArray);
		out.flush();
		Thread.sleep(10);
	}

}
