package com.wangab.pushServer.ptotcol.entity.impl;

import com.wangab.pushServer.ptotcol.entity.IMessageContent;

public class LoginMessageContent implements IMessageContent {
	private long userid;
	private  String extend;
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	
}
