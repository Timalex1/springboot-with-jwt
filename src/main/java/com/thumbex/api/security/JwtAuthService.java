package com.thumbex.api.security;

import java.security.Signature;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.thumbex.api.service.UserDetailsServiceImpl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthService {
    private static final String SECRETKEY = Base64.getEncoder().encodeToString("AuthServXKey".getBytes());

    private static final String PREFIX = "Bearer";
    private static final String EMPTY = "";
    private static final long EXPIRATIONTIME = 86400000;
    private static final String AUTHORIZATION = "Authorization";

    private UserDetailsServiceImpl userDetailsService;

    public String createToken(String username, List roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + EXPIRATIONTIME);

        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRETKEY).compact();
    }

    public Authentication getAuthentication(HttpServletRequest req) {

        String token = resolveToken(req);
        if (token != null && validateToken(token)) {
            String username = getUsername(token);

            if (username != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
        }

        return null;
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(PREFIX)) {
            return bearerToken.replace(PREFIX, EMPTY).trim();
        }

        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jws claims = Jwts.parser().setSigningKey(SECRETKEY).parseClaimsJws(token);

            if (((Claims) claims.getBody()).getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Expired or invalid JWT token");
        }
    }

}
