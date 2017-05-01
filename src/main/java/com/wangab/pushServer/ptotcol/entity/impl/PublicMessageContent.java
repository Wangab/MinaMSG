package com.wangab.pushServer.ptotcol.entity.impl;

import com.wangab.pushServer.ptotcol.entity.IMessageContent;

public class PublicMessageContent implements IMessageContent {
	private long userid;
	private int  msgLength;
	private String msg;
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public int getMsgLength() {
		return msgLength;
	}
	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
