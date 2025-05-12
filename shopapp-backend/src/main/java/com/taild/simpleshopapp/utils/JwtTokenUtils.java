package com.taild.simpleshopapp.utils;

import com.taild.simpleshopapp.exceptions.InvalidParamException;
import com.taild.simpleshopapp.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) throws Exception{
        Map<String, Object> claims = new HashMap<>();
        String subject = getSubject(user);
        claims.put("subject", subject);
        claims.put("userId", user.getId());

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
            return token;
        }catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: "+e.getMessage());
        }
    }

    private static String getSubject(User user) {
        String subject = user.getPhoneNumber();
        if (subject == null || subject.isBlank()) {
            subject = user.getEmail();
        }
        return subject;
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

}
