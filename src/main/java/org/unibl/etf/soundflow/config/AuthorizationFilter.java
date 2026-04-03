package org.unibl.etf.soundflow.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Value("${authorization.token.header.name}")
    private String authorizationHeaderName;
    @Value("${authorization.token.header.prefix}")
    private String authorizationHeaderPrefix;
    @Value("${authorization.token.secret}")
    private String authorizationSecret;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String authorizationHeader = httpServletRequest.getHeader(authorizationHeaderName);
        if (authorizationHeader == null || !authorizationHeader.startsWith(authorizationHeaderPrefix)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String token = authorizationHeader.replace(authorizationHeaderPrefix, "");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(authorizationSecret)
                    .parseClaimsJws(token)
                    .getBody();
            JwtClient jwtUser = new JwtClient(Integer.valueOf(claims.getId()), claims.getSubject(), null);
//            Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("JWT Authentication failed from: " + httpServletRequest.getRemoteHost(), e);
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "JWT Authentication failed");
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
