package com.github.doodler.common.utils;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @Description: Markers
 * @Author: Fred Feng
 * @Date: 20/04/2023
 * @Version 1.0.0
 */
public abstract class Markers {

	public static final Marker COMMON = MarkerFactory.getMarker("common");

	public static final Marker UPMS = MarkerFactory.getMarker("upms");

	public static final Marker USER = MarkerFactory.getMarker("user");

	public static final Marker GAME = MarkerFactory.getMarker("game");

	public static final Marker GAMING = MarkerFactory.getMarker("gaming");

	public static final Marker NEWSLETTER = MarkerFactory.getMarker("newsletter");

	public static final Marker CHAT = MarkerFactory.getMarker("chat");

	public static final Marker PAYMENT = MarkerFactory.getMarker("payment");

	public static final Marker PROMOTION = MarkerFactory.getMarker("promotion");

	public static final Marker AGGREGATION = MarkerFactory.getMarker("aggregation");
	
	public static final Marker SYSTEM = MarkerFactory.getMarker("system");
	
	public static final Marker UNKNOWN = MarkerFactory.getMarker("unknown");

	public static Marker forName(String applicationName) {
		switch (applicationName) {
			case "crypto-common-service":
				return COMMON;
			case "crypto-upms-service":
				return UPMS;
			case "crypto-user-service":
				return USER;
			case "crypto-game-service":
				return GAME;
			case "crypto-gaming-service":
				return GAMING;
			case "crypto-newsletter-service":
				return NEWSLETTER;
			case "crypto-chat-service":
				return CHAT;
			case "crypto-payment-service":
				return PAYMENT;
			case "crypto-promotion-service":
				return PROMOTION;
			case "crypto-aggregation-service":
				return AGGREGATION;
			case "crypto-job-service":
			case "crypto-alert-service":
				return SYSTEM;
		}
		return UNKNOWN;
	}
}