package com.github.doodler.common.ws;

import java.io.IOException;

/**
 * 
 * @Description: WsEncoder
 * @Author: Fred Feng
 * @Date: 10/01/2020
 * @Version 1.0.0
 */
public interface WsEncoder {

	Object encode(String channel, WsUser user, String text) throws IOException;
	
}
