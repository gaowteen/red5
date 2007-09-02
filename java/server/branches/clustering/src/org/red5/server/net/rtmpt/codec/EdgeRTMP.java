package org.red5.server.net.rtmpt.codec;

import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.Invoke;

public class EdgeRTMP extends RTMP {
	public static final byte EDGE_CONNECTED = 0x10;
	public static final byte EDGE_CONNECT_ORIGIN_SENT = 0x11;
	public static final byte ORIGIN_CONNECT_FORWARDED = 0x12;
	
	public EdgeRTMP(boolean mode) {
		super(mode);
	}
}