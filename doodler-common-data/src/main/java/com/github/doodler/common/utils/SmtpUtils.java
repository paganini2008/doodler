package com.github.doodler.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import lombok.experimental.UtilityClass;

/**
 * @Description: SmtpUtils
 * @Author: Fred Feng
 * @Date: 24/10/2023
 * @Version 1.0.0
 */
@UtilityClass
public class SmtpUtils {

	private static final LruMap<String, Boolean> hostCache = new LruMap<>(256);

	public boolean canReach(String emailAddress, int timeout) {
		String host = emailAddress;
		int index = emailAddress.indexOf('@');
		if (index > 0) {
			host = emailAddress.substring(index + 1);
		}
		final String checkedHost = host.intern();
		return MapUtils.getOrCreate(hostCache, checkedHost, () -> {
			try {
				return InetAddress.getByName(checkedHost).isReachable(timeout);
			} catch (IOException ignored) {
				return false;
			}
		});
	}

	public static void main(String[] args) {
		System.out.println(canReach("mozmail.com", 10000));
	}
}