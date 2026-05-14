package org.unibl.etf.soundflow.services.impl;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.*;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.models.requests.ClientUpdateRequest;
import org.unibl.etf.soundflow.models.requests.auth.ChangePasswordRequest;
import org.unibl.etf.soundflow.repositories.ClientEntityRepository;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.EmailService;
import org.unibl.etf.soundflow.services.JwtClientDetailsService;

import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientEntityRepository clientEntityRepository;
    private final EmailService emailService;
    private final JwtClientDetailsService jwtClientDetailsService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public ClientServiceImpl(ClientEntityRepository clientEntityRepository, EmailService emailService, JwtClientDetailsService jwtClientDetailsService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.clientEntityRepository = clientEntityRepository;
        this.emailService = emailService;
        this.jwtClientDetailsService = jwtClientDetailsService;
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

    @Override
    public Client update(Integer id, ClientUpdateRequest request) {
        ClientEntity entity = findById(id);
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        if(!request.getEmail().equals(entity.getEmail())) {
            emailService.sendVerificationEmail(entity);
            entity.setIsVerified(false);
        }
        entity.setEmail(request.getEmail());
        if(!canUsernameBeUpdated(entity, request.getUsername()))
            throw new DuplicateValueException("Username not available");
        entity.setUsername(request.getUsername());
        clientEntityRepository.saveAndFlush(entity);
        return modelMapper.map(entity, Client.class);
    }

    @Override
    public void changePassword(String token, ChangePasswordRequest request) throws UnauthorizedException {
        Claims claims = jwtClientDetailsService.parseToken(token);
        String username = claims.getSubject();
        ClientEntity entity = findByUsername(username);

        if(!passwordEncoder.matches(request.getOldPassword(), entity.getPassword()))
            throw new UnauthorizedException("Invalid old password");
        setNewPassword(entity, request.getNewPassword());
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return clientEntityRepository.existsByUsername(username);
    }

    private boolean isPasswordInvalid(String password) {
        return password.contains("@");
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

    private boolean canUsernameBeUpdated(ClientEntity client, String newUsername) {
        if(client.getUsername().equals(newUsername))
            return true;
        return clientEntityRepository.findByUsername(newUsername).isEmpty();
    }
}
