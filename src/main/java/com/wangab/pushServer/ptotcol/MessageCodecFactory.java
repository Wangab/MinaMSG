package com.wangab.pushServer.ptotcol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.wangab.pushServer.ptotcol.encoder.XELEncoder;
import com.wangab.pushServer.ptotcol.decoder.XELDecoder;

public class MessageCodecFactory implements ProtocolCodecFactory{
	private final XELEncoder encoder;
    private final XELDecoder decoder;
	
	public MessageCodecFactory() {
		this.encoder = new XELEncoder();
		this.decoder = new XELDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
