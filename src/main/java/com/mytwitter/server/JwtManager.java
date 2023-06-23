package com.mytwitter.server;

import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class JwtManager {
    private final static String secret = Config.getDecryptionPass();

    private static final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(Config.getDecryptionPass()),
            SignatureAlgorithm.HS256.getJcaName());


    public static Jws<Claims> parseJwt(String jwtString) {

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }

    public static Key getHmacKey() {
        return hmacKey;
    }
}
