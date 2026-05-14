package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.models.requests.ClientUpdateRequest;
import org.unibl.etf.soundflow.models.requests.auth.ChangePasswordRequest;

public interface ClientService {
    ClientEntity findById(Integer id) throws NotFoundException;
    ClientEntity findByUsername(String username) throws NotFoundException;
//    ClientEntity findByEmailAndAuthProvider(String email, AuthProvider authProvider) throws NotFoundException;
    ClientEntity findByEmail(String email) throws NotFoundException;
    void delete(Integer id);

    Client registration(ClientRequest clientRequest, AuthProvider authProvider);
    void setIsVerified(int id);
    Client setNewPassword(ClientEntity client, String newPassword);

    Client update(Integer id, ClientUpdateRequest client);
    void changePassword(String token, ChangePasswordRequest request);
    boolean doesUsernameExist(String username);
}
