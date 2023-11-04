package org.noah;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class JWTService {
    /**
     * First create a getAllClaims() that return claims.
     * Claims needs a setSigningKey that takes in a method that creates keys
     * create getSignInKey()
     * create a method that allows us extract a single element of a claim from the Claims.
     * this method will return any type that is requested.
     * To do this a Functional Interface that takes in a parameter and returns another is used
     * get username will use the above method
     * Method to generate Token. Two methods one where there is extra claims with userdetails.
     * the other will be just with userDetails.
     * Having generated the token, the next thing is to check the validity of the token generated.
     * Validity will check two things -expiration, if the username from the token matches the username in the user details.
     * so the method will have token and user details as parameters.
     * checking if token is expired needs 1. a method to extract expiration and 2. a method that returns true if the
     * expiration date is before the current date
     */

    private static final long JWT_EXPIRATION = 1000 * 30 * 60 * 1000;

    private static final String JWT_SECRET = "3676537A24432646126A404G635266546A576E5A7234753778214125442A472E";

    public static Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claim = getAllClaims(token);
        return claimsResolver.apply(claim);
    }

    public static String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public static String isTokenValid(String token, String usernameInputted) {
        String usernameGottenAsClaimFromToken = getUsername(token);
        if (usernameGottenAsClaimFromToken.equals(usernameInputted) && !isTokenExpired(token)){
            return "Verification Passed";
        } else {
            return "Verification failed";
        }
    }

    private static boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private static Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
