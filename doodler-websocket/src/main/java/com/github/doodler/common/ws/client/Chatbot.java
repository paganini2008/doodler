package com.github.doodler.common.ws.client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: Chatbot
 * @Author: Fred Feng
 * @Date: 01/01/2020
 * @Version 1.0.0
 */
public class Chatbot {

	public static void parseInitialArgs(String[] args) throws Exception {
		WsClientConsole console = new WsClientConsole();
		console.setConnectTimeout(10000L);
		Options options = getOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = parser.parse(options, args);
		if (commandLine.hasOption('h')) {
			HelpFormatter helpFormatter = new HelpFormatter();
			int width = 110;
			String header = "title";
			String footer = "Welcome to use light Websocket client console";
			boolean autoUsage = true;
			helpFormatter.printHelp(width, "ws-client-tester", header, options, footer, autoUsage);
		} else {
			if (commandLine.hasOption("u")) {
				String url = commandLine.getOptionValue("u");
				if (StringUtils.isBlank(url)) {
					throw new IllegalArgumentException("Ws url must not be null");
				}
				console.setUrl(url);
			}
			if (commandLine.hasOption("a")) {
				String authorization = commandLine.getOptionValue("a");
				if (StringUtils.isNotBlank(authorization)) {
					console.setBearerToken(authorization);
				}
			}
			if (commandLine.hasOption("T")) {
				String headers = commandLine.getOptionValue("T");
				if (StringUtils.isNotBlank(headers)) {
					console.setHttpHeaders(MapUtils.splitAsMap(headers, ",", "="));
				}
			}
		}
		console.start();
	}

	public static Options getOptions() {

		Options options = new Options();
		Option opt = new Option("h", "help", false, "Print help");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("a", "authorzation", true, "Authorzation token, eg: Authorzation Bearer xxx");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("u", "url", true, "ws url, eg: wss://xxx.xxx.xxx/ws/test");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("T", "header", true, "http header, eg: a=1,b=2");
		opt.setRequired(false);
		options.addOption(opt);
		return options;
	}
}