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
 * @Date: 01/03/2023
 * @Version 1.0.0
 */
public class Chatbot {

	public static void main(String[] kwargs) throws Exception {
		kwargs = new String[] {"-u", "ws://localhost:8765/news/ws/user/U2FsdGVkX1%2F5Mfgkgawp%2F5H76nR4ZqUEh0kh2g7lpOCIoSiGDWL47UgeHYqdfrfCawK8Im9ffudQEIQhhn83eA%3D%3D"};
				//"-a",
				//"eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxMDI2OSIsInN1YiI6ImZyZWRAZ2xvYmFsdGxsYy5jb20iLCJhdWQiOiJ3ZWJzaXRlIiwiaWF0IjoxNjc4ODM4MTQ0LCJpc3MiOiJjcnlwdG8tZ2FtZSIsImV4cCI6MTY3ODkyNDU0NH0.AxBO75wu9RL4B45WFwqb55KPWQc6zt5DslGxqR7ccGhqBmeczT4yVcbxI8sQi6h8bzsoIwZD3x5P092ICbqFDw"};

		// kwargs = new String[] {"-u",
		//		"ws://localhost:8899/chat/ws/chat/U2FsdGVkX1%2BBkKaURQ1A4W3Z6Ohm9vwqG0bTa86e4pluvAQPTMvPZ2mHqizppEyfMGl6ErFWhdj3pAWShkSnmQ%3D%3D",
		//		"-a", "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxMDQyNSIsInN1YiI6ImFsdmExMDBAZ2xvYmFsdGxsYy5jb20iLCJhdWQiOiJ3ZWJzaXRlIiwiaWF0IjoxNjc4Nzc4Nzk5LCJpc3MiOiJjcnlwdG8tZ2FtZSIsImV4cCI6MTY3ODg2NTE5OX0.RiyBv2jqjf7l7kn_Fi7Z_ny7rZ7B9zUo4_Z1uy2F5Az23RDI_wDogypAnbqfpgJGUmNcD00uxl8878PNHYSuzA"};
		// kwargs = new String[] {"-h"};
		parseInitialArgs(kwargs);
	}

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