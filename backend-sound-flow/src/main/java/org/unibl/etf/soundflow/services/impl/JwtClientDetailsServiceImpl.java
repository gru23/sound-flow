package org.unibl.etf.soundflow.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.unibl.etf.soundflow.repositories.ClientEntityRepository;
import org.unibl.etf.soundflow.services.JwtClientDetailsService;

import java.util.Date;

@Service
public class JwtClientDetailsServiceImpl implements JwtClientDetailsService {
    private final ClientEntityRepository clientEntityRepository;
    private final ModelMapper modelMapper;

    @Value("${authorization.token.expiration-time}")
    private String tokenExpirationTime;
    @Value("${authorization.token.secret}")
    private String tokenSecret;

    public JwtClientDetailsServiceImpl(ClientEntityRepository clientEntityRepository, ModelMapper modelMapper) {
        this.clientEntityRepository = clientEntityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JwtClient loadUserByUsername(String username) throws UsernameNotFoundException {
        return modelMapper.map(clientEntityRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException(username)), JwtClient.class);
    }

    @Override
    public String generateJwt(JwtClient client) {
        return Jwts.builder()
                .setId(client.getId().toString())
                .setSubject(client.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(tokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    @Override
    public  Claims parseToken(String accessToken) {
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
