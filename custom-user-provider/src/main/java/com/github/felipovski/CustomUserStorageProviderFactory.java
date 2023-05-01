package com.github.felipovski;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomStorageProvider> {

    private static final String PROVIDER_ID = "custom-provider";

    @Override
    public CustomStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
