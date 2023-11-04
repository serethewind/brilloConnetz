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

/**
 * This class provides utility methods for working with JSON Web Tokens (JWT).
 * It includes methods for token generation, validation, and extracting claims.
 */
public class JWTService {
    /**
     * The duration, in milliseconds, for which a JWT token remains valid (30 minutes).
     */
    private static final long JWT_EXPIRATION = 1000 * 30 * 60 * 1000;
    /**
     * The secret key used for JWT token signing and verification.
     */
    private static final String JWT_SECRET = "3676537A24432646126A404G635266546A576E5A7234753778214125442A472E";

    /**
     * Parses a JWT token and returns its claims.
     *
     * @param token The JWT token to be parsed.
     * @return The claims from the provided token, represented as a map of key-value pairs.
     */
    public static Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves a specific claim from a JWT token using a provided claims resolver function.
     *
     * @param token          The JWT token from which to extract the claim.
     * @param claimsResolver A function that maps claims to a generic type.
     * @param <T>            The type of the claim to extract.
     * @return The claim extracted from the token.
     */
    public static <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claim = getAllClaims(token);
        return claimsResolver.apply(claim);
    }

    /**
     * Retrieves the username claim from a JWT token.
     *
     * @param token The JWT token from which to extract the username claim.
     * @return The username extracted from the token.
     */
    public static String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Generates a JWT token for the specified username.
     *
     * @param username The username for which to generate the token.
     * @return The generated JWT token, which includes claims for the provided username, issuance time,
     * and expiration time, signed with the secret key.
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifies the validity of a JWT token and checks if it matches the provided username.
     *
     * @param token            The JWT token to be verified.
     * @param usernameInputted The username input against which the token is validated.
     * @return "Verification Passed" if the token is valid and matches the inputted username, otherwise "Verification failed".
     */

    public static String isTokenValid(String token, String usernameInputted) {
        String usernameGottenAsClaimFromToken = getUsername(token);
        if (usernameGottenAsClaimFromToken.equals(usernameInputted) && !isTokenExpired(token)) {
            return "Verification Passed";
        } else {
            return "Verification failed";
        }
    }

    /**
     * Checks if a JWT token has expired by comparing its expiration date to the current time.
     *
     * @param token The JWT token to be checked for expiration.
     * @return true if the token has expired, false otherwise.
     */
    private static boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    /**
     * Retrieves the expiration date claim from a JWT token.
     *
     * @param token The JWT token from which to extract the expiration date claim.
     * @return The expiration date as a Date object.
     */
    private static Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Generates the signing key for JWT token processing based on the predefined secret key.
     *
     * @return The signing key as a cryptographic key.
     */
    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
