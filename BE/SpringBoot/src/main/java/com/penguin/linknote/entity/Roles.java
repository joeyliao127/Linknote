package com.penguin.linknote.entity;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Roles {

    private int id;

    private String title;

    private Instant createdAt;

    private Instant updatedAt;


}
