package com.github.felipovski;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.felipovski.dto.AuthRequest;
import com.github.felipovski.dto.UserInfoRequest;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class CustomStorageProvider
        implements UserStorageProvider, UserLookupProvider,
        CredentialInputValidator, UserRegistrationProvider {

    private final KeycloakSession session;

    private final ComponentModel componentModel;

    private static final Logger log = LoggerFactory.getLogger(CustomStorageProvider.class);

    public CustomStorageProvider(KeycloakSession session, ComponentModel componentModel) {
        this.session = session;
        this.componentModel = componentModel;
    }


    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {

        if (!supportsCredentialType(credentialInput.getType())) {
            return false;
        }

        var authRequest = new AuthRequest(userModel.getUsername(), credentialInput.getChallengeResponse());
        var mapper = new ObjectMapper();
        try {
            var json = mapper.writeValueAsString(authRequest);

            var uri = new URI("http://mock-auth-service:8081/auth");
            log.info("\n\nPrinting URI> {} \n\n", uri.toString());
            var httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            var response = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return Boolean.parseBoolean(response.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {

        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();

        UserModel usermodel = null;

        synchronized (session) {
            usermodel = session
                    .users()
                    .getUsersStream(realm)
                    .filter(userModel -> username.equals(userModel.getUsername()))
                    .findFirst()
                    .orElse(null);
        }
        if (Objects.nonNull(usermodel)) {

            var userApiData = new UserApiData();
            userApiData.setUsername(username);
            userApiData.setEmail(usermodel.getEmail());
            userApiData.setFirstName(usermodel.getFirstName());
            userApiData.setLastName(usermodel.getLastName());

            return new UserAdapter(session, realm, componentModel, userApiData);
        }
        return null;
    }

    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {

        return null;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {

        UserModel userModel = getUserById(realmModel, username);

        if (Objects.nonNull(userModel)) return userModel;

        var userInfoRequest = new UserInfoRequest("felipe", "IT");
        var mapper = new ObjectMapper();
        try {
            var json = mapper.writeValueAsString(userInfoRequest);

            var uri = new URI("http://mock-auth-service:8081/userInfo");
            log.info("\n\nPrinting URI> {} \n\n", uri.toString());
            var httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            var response = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            var userApiData = mapper.readValue(response.body(), UserApiData.class);

            var userAdapter = new UserAdapter(session, realmModel, componentModel, userApiData);

            synchronized (session) {
                var savedUser = session.users().addUser(realmModel, username);
                savedUser.setEnabled(true);
                savedUser.setFirstName(userAdapter.getFirstName());
                savedUser.setLastName(userAdapter.getLastName());
                savedUser.setEmail(userAdapter.getEmail());
                savedUser.setFederationLink(componentModel.getId());
            }

            return userAdapter;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String s) {
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        return false;
    }
}
