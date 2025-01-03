package com.github.doodler.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.experimental.UtilityClass;

/**
 * @Description: NetUtils
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@UtilityClass
public class NetUtils {

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    public static final int MIN_PORT = 1024;

    public static final int MAX_PORT = 65535;

    private static final int RANDOM_PORT_TRY_TIMES = 10;

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final String[] PUBLIC_IP_FINDERS =
            new String[] {"http://checkip.amazonaws.com/", "https://ipv4.icanhazip.com/"};

    public static boolean isAnyHost(String host) {
        return "0.0.0.0".equals(host);
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public static String getLocalHostAddress() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    public static InetAddress getLocalAddress() {
        InetAddress localAddress;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if (interfaces != null) {
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                if (addresses != null) {
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (isValidAddress(address)) {
                            return address;
                        }
                    }
                }
            }
        }
        return localAddress;
    }

    public static byte[] getMacAddress() {
        byte[] mac = null;
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            try {
                final NetworkInterface localInterface =
                        NetworkInterface.getByInetAddress(localHost);
                if (isUpAndNotLoopback(localInterface)) {
                    mac = localInterface.getHardwareAddress();
                }
                if (mac == null) {
                    final Enumeration<NetworkInterface> networkInterfaces =
                            NetworkInterface.getNetworkInterfaces();
                    if (networkInterfaces != null) {
                        while (networkInterfaces.hasMoreElements() && mac == null) {
                            final NetworkInterface nic = networkInterfaces.nextElement();
                            if (isUpAndNotLoopback(nic)) {
                                mac = nic.getHardwareAddress();
                            }
                        }
                    }
                }
            } catch (final SocketException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (ArrayUtils.isEmpty(mac) && localHost != null) {
                // Emulate a MAC address with an IP v4 or v6
                final byte[] address = localHost.getAddress();
                // Take only 6 bytes if the address is an IPv6 otherwise will pad with two zero
                // bytes
                mac = Arrays.copyOf(address, 6);
            }
        } catch (final UnknownHostException ignored) {
            // ignored
        }
        return mac;
    }

    private static boolean isUpAndNotLoopback(final NetworkInterface ni) throws SocketException {
        return ni != null && !ni.isLoopback() && ni.isUp();
    }

    public static boolean isLoopbackAddress(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress).isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean canAccess(String hostName, int port, int timeout) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(hostName, port), timeout);
            return socket.isConnected();
        } catch (IOException ignored) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static boolean isAvailablePort(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static int getRandomPort(int from, int to) {
        int port;
        int i = 0;
        from = Math.max(MIN_PORT, from);
        to = Math.min(MAX_PORT, to);
        do {
            port = RandomUtils.nextInt(from, to);
        } while (!isAvailablePort(port) && (i++) < RANDOM_PORT_TRY_TIMES);
        return port;
    }

    public static String getExternalIp() {
        return getExternalIp(LOCALHOST);
    }

    public static String getExternalIp(String defaultValue) {
        return getExternalIp(10000, defaultValue);
    }

    public static String getExternalIp(int timeout, String defaultValue) {
        String[] copy = PUBLIC_IP_FINDERS.clone();
        String ip;
        for (String ipFinder : copy) {
            try {
                ip = UrlUtils.toString(ipFinder, timeout, timeout, Charset.defaultCharset());
            } catch (IOException ignored) {
                continue;
            }
            if (StringUtils.isBlank(ip)) {
                continue;
            }
            ip = ip.trim();
            if (IP_PATTERN.matcher(ip).matches()) {
                return ip;
            }
        }
        return defaultValue;
    }

    public SocketAddress parse(String serviceLocation) {
        int index = serviceLocation.indexOf(":");
        if (index == -1) {
            throw new IllegalArgumentException("Cannot parse: " + serviceLocation);
        }
        String hostName = serviceLocation.substring(0, index);
        int port = Integer.parseInt(serviceLocation.substring(index + 1));
        return new InetSocketAddress(hostName, port);
    }

    public String toExternalString(InetSocketAddress socketAddress) {
        return socketAddress.getHostString() + ":" + socketAddress.getPort();
    }

    public static void main(String[] args) {
        System.out.println(isLoopbackAddress("abc.com"));
    }
}
