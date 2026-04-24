package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;

public interface ClientService {
    ClientEntity findById(Integer id) throws NotFoundException;
    ClientEntity findByUsername(String username) throws NotFoundException;
//    ClientEntity findByEmailAndAuthProvider(String email, AuthProvider authProvider) throws NotFoundException;
    ClientEntity findByEmail(String email) throws NotFoundException;
    Client registration(ClientRequest clientRequest, AuthProvider authProvider);
    void setIsVerified(int id);
    Client setNewPassword(ClientEntity client, String newPassword);
}
