package com.derry.tom_client.model;

public class CheckUsernameResponse {
    private boolean exists;

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}