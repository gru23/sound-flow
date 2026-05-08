package org.unibl.etf.soundflow.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.services.OAuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService authService;

    @Value("${google.client.id}")
    private String clientId;// = "48939084992-ah3h7cvtjhp82dc5al8ser9g1h69lbk4.apps.googleusercontent.com";
    @Value("${google.client.secret}")
    private String secret;// = "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";

    public OAuthController(OAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/google/login")
    public LoginResponse googleLogin1(@RequestBody String idToken) {
        return authService.login(idToken);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String REDIRECT_URI = "http://localhost:8080/oauth/google";

        String code = payload.get("code");

        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", secret);
        params.put("redirect_uri", REDIRECT_URI);
        params.put("grant_type", "authorization_code");
        params.put("code", code);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, params, Map.class);
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Povuci podatke o korisniku
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

        String email = (String) userResponse.getBody().get("email");
        String name = (String) userResponse.getBody().get("name");

        // Kreiraj ili pronađi korisnika u bazi
//        String jwt = jwtService.generateToken(email);

        return ResponseEntity.ok(Map.of("jwt", email, "name", name));
    }
}
