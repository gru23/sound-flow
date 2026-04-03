package org.unibl.etf.soundflow.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.DuplicateValueException;
import org.unibl.etf.soundflow.exceptions.InternalServerException;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.repositories.ClientEntityRepository;
import org.unibl.etf.soundflow.services.ClientService;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientEntityRepository clientEntityRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public ClientServiceImpl(ClientEntityRepository clientEntityRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.clientEntityRepository = clientEntityRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    public LoginResponse findById(Integer id) {
        ClientEntity entity = clientEntityRepository.findById(id).orElseThrow(NotFoundException::new);
        return modelMapper.map(entity, LoginResponse.class);
    }

    @Override
    public Client registration(ClientRequest clientRequest) throws DuplicateValueException, InternalServerException {
        if(clientRequest.getAuthProvider() == AuthProvider.LOCAL) {
            if(clientEntityRepository.existsByUsername(clientRequest.getUsername())) {
                throw new DuplicateValueException();
            }
            ClientEntity entity = modelMapper.map(clientRequest, ClientEntity.class);
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            entity = clientEntityRepository.saveAndFlush(entity);
            entityManager.refresh(entity);
            return modelMapper.map(entity, Client.class);
        }
        else if(clientRequest.getAuthProvider() == AuthProvider.GOOGLE) {
            ClientEntity entity = modelMapper.map(clientRequest, ClientEntity.class);
            // sta sada? azurirati podatke korisnika? samo azurairt token?
            return null;
        }
        throw new InternalServerException("Unsupported auth provider");
    }
}
