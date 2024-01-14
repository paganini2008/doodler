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
			case "doodler-common-service":
				return COMMON;
			case "doodler-upms-service":
				return UPMS;
			case "doodler-user-service":
				return USER;
			case "doodler-game-service":
				return GAME;
			case "doodler-gaming-service":
				return GAMING;
			case "doodler-newsletter-service":
				return NEWSLETTER;
			case "doodler-chat-service":
				return CHAT;
			case "doodler-payment-service":
				return PAYMENT;
			case "doodler-promotion-service":
				return PROMOTION;
			case "doodler-aggregation-service":
				return AGGREGATION;
			case "doodler-job-service":
			case "doodler-alert-service":
				return SYSTEM;
		}
		return UNKNOWN;
	}
}