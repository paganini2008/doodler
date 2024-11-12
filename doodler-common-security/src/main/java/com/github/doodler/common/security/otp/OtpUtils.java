package com.github.doodler.common.security.otp;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import de.taimos.totp.TOTP;
import lombok.experimental.UtilityClass;

/**
 * @Description: OtpUtils
 * @Author: Fred Feng
 * @Date: 04/12/2022
 * @Version 1.0.0
 */
@UtilityClass
public class OtpUtils {

    public String getTotpCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static void main(String[] args) {
        System.out.println(getTotpCode("MZVNHE43PCXNAQMH"));
    }
}
