package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.ClientRequest;

public interface ClientService {
    LoginResponse findById(Integer id);
    Client registration(ClientRequest clientRequest);
}
