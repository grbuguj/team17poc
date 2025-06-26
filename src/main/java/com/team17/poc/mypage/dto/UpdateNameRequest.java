package com.team17.poc.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateNameRequest {
    @JsonProperty("name")
    private String newName;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
