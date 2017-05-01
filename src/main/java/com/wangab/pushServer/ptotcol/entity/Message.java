package com.wangab.pushServer.ptotcol.entity;

public class Message {
	private int stx;
	private int totalLen;
	private String msgType;
	private String messageID;
	private IMessageContent msgContent;
	private int etx;
	public int getStx() {
		return stx;
	}
	public void setStx(int stx) {
		this.stx = stx;
	}
	public int getTotalLen() {
		return totalLen;
	}
	public void setTotalLen(int totalLen) {
		this.totalLen = totalLen;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public IMessageContent getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(IMessageContent msgContent) {
		this.msgContent = msgContent;
	}
	public int getEtx() {
		return etx;
	}
	public void setEtx(int etx) {
		this.etx = etx;
	}
	
}
