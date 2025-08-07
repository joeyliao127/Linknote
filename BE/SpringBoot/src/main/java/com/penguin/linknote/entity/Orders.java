package com.penguin.linknote.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Orders", schema = "ec")
public class Orders {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private String orderId;

    public Orders() {
    }

    public Orders(String userId, String orderId) {
    }
}
