package com.github.doodler.common.ws.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * @Description: WsClientConsole
 * @Author: Fred Feng
 * @Date: 23/02/2023
 * @Version 1.0.0
 */
public class WsClientConsole {

    private String url;
    private String bearerToken;
    private Map<String, String> httpHeaders = new HashMap<>();
    private long connectTimeout = 10000L;
    private List<WsClientListener> listeners = new CopyOnWriteArrayList<>();
    private WsConnection wsConnection;
    private final AtomicBoolean running = new AtomicBoolean();

    public WsClientConsole() {
        listeners.add(new StdOutMessageListener());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void start() throws IOException {
        this.wsConnection = WsClientUtils.openConnection(url, bearerToken, httpHeaders, connectTimeout,
                listeners.toArray(new WsClientListener[0]));
        Terminal terminal = TerminalBuilder.builder().system(true).color(true).build();
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        String prompt = String.format("[%s]-ws-client > ", wsConnection.getSessionId());
        running.set(true);
        while (running.get()) {
            String line = lineReader.readLine(prompt);
            if (StringUtils.isBlank(line)) {
                continue;
            }
            System.out.println("[input] " + line);
            ParsedLine parsedLine = lineReader.getParsedLine();
            List<String> intructions = parsedLine.words();
            if (intructions.size() > 0) {
                try {
                    exec(intructions);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        System.out.println("bye");
    }

    protected void exec(List<String> instructions) {
        final String cmd = instructions.get(0);
        if (StringUtils.isBlank(cmd)) {
            return;
        }
        switch (cmd.toLowerCase()) {
            case "exit":
            case "quit":
                doQuit();
                break;
            case "batchsend":
                doBatchSend(instructions);
                break;
            case "send":
                doSend(instructions);
                break;
            default:
                System.out.println("[output] Unknown instruction: " + StringUtils.join((instructions)));
                break;
        }
    }

    private void doQuit() {
        wsConnection.close(true);
        running.set(false);
    }

    private void doBatchSend(List<String> instructions) {
        if (instructions.size() < 3) {
            throw new IllegalArgumentException("Bad format of 'batchSend' instruction");
        }
        int n = Integer.parseInt(instructions.get(1));
        String msg = StringUtils.join(instructions.subList(2, instructions.size()), " ");
        for (int i = 0; i < n; i++) {
            wsConnection.send(msg);
        }
    }

    private void doSend(List<String> instructions) {
        if (instructions.size() < 2) {
            throw new IllegalArgumentException("Bad format of 'send' instruction");
        }
        String msg = StringUtils.join(instructions.subList(1, instructions.size()), " ");
        wsConnection.send(msg);
    }

    private static class StdOutMessageListener implements WsClientListener {

        @Override
        public void onMessage(String msg) {
            if (StringUtils.isNotBlank(msg) && !"ping".equals(msg)) {
                System.out.println("[output] " + msg);
            }
        }
    }

}