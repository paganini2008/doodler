package com.github.doodler.common.utils;

import java.io.IOException;
import lombok.experimental.UtilityClass;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

/**
 * @Description: EmailUtils
 * @Author: Fred Feng
 * @Date: 10/05/2023
 * @Version 1.0.0
 */
@UtilityClass
public class EmailUtils {

    public boolean checkEmail(String email) {
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            return false;
        }
        String host = "";
        String hostName = email.split("@")[1];
        Record[] result = null;
        SMTPClient client = new SMTPClient();
        try {
            Lookup lookup = new Lookup(hostName, Type.MX);
            lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL) {
                return false;
            } else {
                result = lookup.getAnswers();
            }
            for (int i = 0; i < result.length; i++) {
                host = result[i].getAdditionalName().toString();
                client.connect(host);
                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
                    client.disconnect();
                    continue;
                } else {
                    break;
                }
            }

            client.login("globaltllc.com");
            client.setSender("fred@globaltllc.com");
            client.addRecipient(email);
            if (250 == client.getReplyCode()) {
                return true;
            }
        } catch (Exception ignored) {
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
            }
        }
        return false;
    }
}