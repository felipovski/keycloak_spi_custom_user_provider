package com.github.felipovski.dto;

public class UserInfoRequest{

    public String username;
    public String system;

    public UserInfoRequest(String username, String system) {
        this.username = username;
        this.system = system;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
