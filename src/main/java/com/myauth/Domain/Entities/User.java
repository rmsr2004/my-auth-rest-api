package com.myauth.Domain.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;

    public User(String username, String password) {
        this.id = 0L;
        this.username = username;
        this.password = password;
    }
}