package com.project.HR.Connect.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
//public class JWTUtils {
//
//    @Value("${jwt.expiration}")
//    private int expTime;
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    public String normalizeAuthorizationHeader(String authorizationHeader){
//        String[] authorizationHeaderComponents = authorizationHeader.split(" ");
//        for (int i = 0; i < authorizationHeaderComponents.length; i++) {
//            if (authorizationHeaderComponents[i].equals("Bearer")) return authorizationHeaderComponents[i+1];
//        }
//        return null;
//    }
//
//    public String createJwt(String username, String role) {
//        return JWT.create()
//                .withSubject(username)
//                .withIssuedAt(new Date())
//                .withExpiresAt(new Date((new Date()).getTime() + expTime))
//                .withClaim("role", role)
//                .sign(Algorithm.HMAC256(secret));
//    }
//
//    public String getEmailFromJwtToken(String token) {
//        Algorithm algorithm = Algorithm.HMAC256(secret);
//        JWT.require(algorithm).build().verify(token);
//        return JWT.decode(token).getSubject();
//    }
//    public boolean validateJwtToken(String authToken) {
//        JWT.decode(authToken);
//        return true;
//    }
//}

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expTime;

    public String normalizeAuthorizationHeader(String authorizationHeader){
        String[] authorizationHeaderComponents = authorizationHeader.split(" ");
        for (int i = 0; i < authorizationHeaderComponents.length; i++) {
            if (authorizationHeaderComponents[i].equals("Bearer")) return authorizationHeaderComponents[i+1];
        }
        return null;
    }

    public String createJwt(String username, String role, Integer userId) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expTime))
                .withClaim("role", role)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC256(secret));
    }

    public String getEmailFromJwtToken(String token) {
        JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        return JWT.decode(token).getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(authToken);
            return true;
        } catch (JWTVerificationException e) {
            return false;  // Token is invalid
        }
    }
}
