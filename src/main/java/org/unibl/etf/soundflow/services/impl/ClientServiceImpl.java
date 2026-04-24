package org.unibl.etf.soundflow.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.DuplicateValueException;
import org.unibl.etf.soundflow.exceptions.InternalServerException;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.ValidationException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.repositories.ClientEntityRepository;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.EmailService;

import java.util.Random;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientEntityRepository clientEntityRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public ClientServiceImpl(ClientEntityRepository clientEntityRepository, EmailService emailService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.clientEntityRepository = clientEntityRepository;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    public ClientEntity findById(Integer id) throws NotFoundException {
        return clientEntityRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public ClientEntity findByUsername(String username) throws NotFoundException {
        return clientEntityRepository.findByUsername(username)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public ClientEntity findByEmail(String email) throws NotFoundException {
        return clientEntityRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }

    @Override
    public Client registration(ClientRequest clientRequest, AuthProvider authProvider) throws DuplicateValueException, InternalServerException {
        if(AuthProvider.LOCAL == authProvider) {
            if(doesUsernameExist(clientRequest.getUsername())) {
                throw new DuplicateValueException();
            }
            Client client = setPasswordAndPersistClient(clientRequest, authProvider);
//            emailService.sendVerificationEmail(modelMapper.map(client, ClientEntity.class));
            return client;
        }
        else if(AuthProvider.GOOGLE == authProvider) {
            while(doesUsernameExist(clientRequest.getUsername())) {
                String newUsername = clientRequest.getUsername() + new Random().nextInt(1000);
                clientRequest.setUsername(newUsername);
            }
            return setPasswordAndPersistClient(clientRequest, authProvider);
        }
        throw new InternalServerException("Unsupported auth provider");
    }

    @Override
    public void setIsVerified(int id) {
        ClientEntity client = clientEntityRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);
        client.setIsVerified(true);
        clientEntityRepository.saveAndFlush(client);
    }

    @Override
    public Client setNewPassword(ClientEntity client, String newPassword) {
        if(isPasswordInvalid(newPassword))
            throw new ValidationException("Password contains not allowed characters");
        client.setPassword(passwordEncoder.encode(newPassword));
        clientEntityRepository.saveAndFlush(client);
        return modelMapper.map(client, Client.class);
    }

    private boolean isPasswordInvalid(String password) {
        return password.contains("@");
    }

    private boolean doesUsernameExist(String username) {
        return clientEntityRepository.existsByUsername(username);
    }

    private Client setPasswordAndPersistClient(ClientRequest clientRequest, AuthProvider authProvider) {
        ClientEntity entity = modelMapper.map(clientRequest, ClientEntity.class);
        if(AuthProvider.GOOGLE == authProvider)
            entity.setIsVerified(true);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity = clientEntityRepository.saveAndFlush(entity);
        entityManager.refresh(entity);
        return modelMapper.map(entity, Client.class);
    }
}
