package com.example.gemini.Entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;


    private String username;

    private String password;
    private String role; // USER or ADMIN

}
