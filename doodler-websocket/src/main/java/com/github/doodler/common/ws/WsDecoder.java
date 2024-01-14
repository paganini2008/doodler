package com.github.doodler.common.ws;

import java.io.IOException;

/**
 * 
 * @Description: WsDecoder
 * @Author: Fred Feng
 * @Date: 12/01/2020
 * @Version 1.0.0
 */
public interface WsDecoder {

	String decode(String channel, WsUser user, Object payload, long timestamp) throws IOException;
	
}
