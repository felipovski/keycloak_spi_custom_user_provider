package com.github.felipovski;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserAdapter extends AbstractUserAdapter {

    private final UserApiData userApiData;
    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, UserApiData userApiData) {
        super(session, realm, storageProviderModel);
        this.storageId = new StorageId(storageProviderModel.getId(), userApiData.getUsername());
        this.userApiData = userApiData;
    }

    public String getUsername() {
        return userApiData.getUsername();
    }

    @Override
    public String getFirstName() {
        return userApiData.getFirstName();
    }

    @Override
    public String getLastName() {
        return userApiData.getLastName();
    }

    @Override
    public String getEmail() {
        return userApiData.getEmail();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, this.getUsername());
        attributes.add(UserModel.EMAIL, this.getEmail());
        attributes.add(UserModel.FIRST_NAME, this.getFirstName());
        attributes.add(UserModel.LAST_NAME, this.getLastName());
        return attributes;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        Map<String, List<String>> attributes = getAttributes();
        return attributes.containsKey(name) ? attributes.get(name).stream() : Stream.empty();
    }
    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }


}
