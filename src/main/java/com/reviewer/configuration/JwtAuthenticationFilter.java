package com.reviewer.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.reviewer.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtService.isTokenValid(jwt)) {
                log.info(jwt);
                DecodedJWT decodedJWT = JWT.decode(jwt);
                log.info("token decodeado");
                String role = decodedJWT.getClaim("auth").asString();
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                log.info("Pillamos autoridades");
                // Creamos un UserDetails completo con los roles directamente del token
                UserDetails userDetails = new User(
                        username,
                        "",
                        authorities
                );
                log.info("Hacemos details");
                log.info(userDetails.getAuthorities().stream().toString());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

                log.info("Hacemos authentication");

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                log.info("Ponemos el details");

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("JWT token is invalid for user: {}", username);
                log.warn(jwt);
            }
        }
        filterChain.doFilter(request, response);
    }
}
