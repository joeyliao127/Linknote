package com.penguin.linknote.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_status_codes")
public class UserStatusCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;
}
