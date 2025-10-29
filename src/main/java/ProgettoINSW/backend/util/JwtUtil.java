package ProgettoINSW.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 🔐 Chiave segreta fissa — cambiala con una tua personale, lunga almeno 32 caratteri
    private static final String SECRET_KEY = "ProgettoINSW2025SecretKeyForJWT_is_MoltoLungaEForte!";
    private static final long EXPIRATION_TIME = 86400000; // 24h

    private static final Key key = new SecretKeySpec(
            SECRET_KEY.getBytes(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.getJcaName()
    );

    // 🔸 Genera token
    public static String generateToken(String email, String ruolo) {
        return Jwts.builder()
                .setSubject(email)
                .claim("ruolo", ruolo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔸 Estrae email dal token
    public static String extractMail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 🔸 Estrae ruolo dal token
    public static String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("ruolo", String.class);
    }

    // 🔸 Verifica validità (scadenza + firma)
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
