package org.unibl.etf.soundflow.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.unibl.etf.soundflow.repositories.ClientEntityRepository;
import org.unibl.etf.soundflow.services.JwtClientDetailsService;

@Service
public class JwtClientDetailsServiceImpl implements JwtClientDetailsService {
    private final ClientEntityRepository clientEntityRepository;
    private final ModelMapper modelMapper;

    public JwtClientDetailsServiceImpl(ClientEntityRepository clientEntityRepository, ModelMapper modelMapper) {
        this.clientEntityRepository = clientEntityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JwtClient loadUserByUsername(String username) throws UsernameNotFoundException {
        return modelMapper.map(clientEntityRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException(username)), JwtClient.class);
    }
}
