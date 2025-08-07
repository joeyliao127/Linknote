package com.penguin.linknote.service;

import com.penguin.linknote.entity.Orders;
import com.penguin.linknote.repo.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Iterable<Orders> index() {
        return orderRepository.findAll();
    }

    public long countAll() {
        return orderRepository.count();
    }
}
