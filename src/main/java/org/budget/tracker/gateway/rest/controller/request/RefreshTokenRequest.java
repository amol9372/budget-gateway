package org.budget.tracker.gateway.rest.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenRequest {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
