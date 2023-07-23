package io.doodler.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * @Description: SmtpUtils
 * @Author: Isaac Yang
 * @Date: 06/03/2023
 * @Version 1.0.0
 */
@Deprecated
public abstract class SmtpUtils {

    public static void main2(String[] args) {

        String[] testData = {
//                "tvf@tvf.cz",
//                "isaacyaner@gmail.com",
//                "isaac@maxitechnology.com",
//                "bzaarrrr@noExistqwenlmgfnqw.com"
                "1111@gmail.com"
        };
        for (String testDatum : testData) {
            System.out.println(testDatum + " is valid? " +
                    isAddressValid(testDatum));
        }
        return;
    }

    /**
     * Return whether the email address is valid using smtp
     *
     * @param address string: email address
     * @return boolean
     */
    public static boolean isAddressValid(String address) {
        // Get mail exchangers from domain
        String domain = address.substring(address.indexOf('@') + 1);
        ArrayList<String> mxList = null;
        try {
            mxList = getMX(domain);
        } catch (NamingException ex) {
            return false;
        }
        // For each mail exchange, request the validity
        for (String s : mxList) {
            Socket socket = null;
            try {
                socket = new Socket((String) s, 25);
                socket.setSoTimeout(5000);
                // socket.set
                BufferedReader bufferedReader = new BufferedReader
                        (new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(socket.getOutputStream()));
                if (hear(bufferedReader) != 220) {
                    throw new Exception("Invalid header");
                }
                say(bufferedWriter, "EHLO izac.com");
                if (hear(bufferedReader) != 250) {
                    throw new Exception("Not ESMTP");
                }
                // validate the sender address
                say(bufferedWriter, "MAIL FROM: <izac@testmail.com>");
                if (hear(bufferedReader) != 250) {
                    throw new Exception("Sender rejected");
                }
                // Some are banned from spamhaus
                say(bufferedWriter, "RCPT TO: <" + address + ">");
                if (hear(bufferedReader) != 250) {
                    throw new Exception("Address is not valid!");
                }
                say(bufferedWriter, "VRFY " + address);
                int respond = hear(bufferedReader);
                if (respond != 250 && respond != 252) {
                    throw new Exception("VRFY not passed");
                }
                // be polite, ending the requests
                say(bufferedWriter, "RSET");
                hear(bufferedReader);
                say(bufferedWriter, "QUIT");
                hear(bufferedReader);
                bufferedReader.close();
                bufferedWriter.close();
            } catch (Exception ex) {
                continue;
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static int hear(BufferedReader bufferedReader) throws IOException {
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String code = line.substring(0, 3);
            if (line.charAt(3) != '-') {
                try {
                    return Integer.parseInt(code);
                } catch (Exception e) {
                    return -1;
                }
            }
        }
        return 0;
    }

    private static void say(BufferedWriter wr, String text)
            throws IOException {
        wr.write(text + "\r\n");
        wr.flush();
    }

    private static ArrayList<String> getMX(String hostName)
            throws NamingException {
        // Perform a DNS lookup for MX records in the domain
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext initialDirContext = new InitialDirContext(env);
        Attributes attributes = initialDirContext.getAttributes
                (hostName, new String[]{"MX"});
        Attribute attribute = attributes.get("MX");
        // if we don't have an MX record, try the machine itself
        if ((attribute == null) || (attribute.size() == 0)) {
            attributes = initialDirContext.getAttributes(hostName, new String[]{"A"});
            attribute = attributes.get("A");
            if (attribute == null) {
                throw new NamingException("No match for name '" + hostName + "'");
            }
        }
        // Return list of mail exchanges
        ArrayList<String> result = new ArrayList<>();
        NamingEnumeration<?> en = attribute.getAll();
        while (en.hasMore()) {
            String x = (String) en.next();
            String serverName = x.split(" ")[1];
            result.add(serverName.endsWith(".")
                    ? serverName.substring(0, (serverName.length() - 1))
                    : serverName);
        }
        return result;
    }
    
    public static void main(String[] args) {
    	System.out.println(isAddressValid("fred@globaltllc.com"));
    }
}