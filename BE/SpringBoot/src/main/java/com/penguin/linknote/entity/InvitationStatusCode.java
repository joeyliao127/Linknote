package com.penguin.linknote.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "invitation_status_codes")
@Data
public class InvitationStatusCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;
}
