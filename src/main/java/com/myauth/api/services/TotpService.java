package com.myauth.api.services;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;

@Service
public class TotpService {
    final private TimeBasedOneTimePasswordGenerator totp;

    public TotpService() {
        this.totp = new TimeBasedOneTimePasswordGenerator();
    }

    public String generateToken(String secret) {
        try {
            Base32 base32 = new Base32();
            byte[] secretBytes = base32.decode(secret);
            Key key = new SecretKeySpec(secretBytes, "HmacSHA1");

            int code = totp.generateOneTimePassword(key, Instant.now());
            return String.format("%06d", code);
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
}
