package com.penguin.linknote.controller;

import com.penguin.linknote.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    private final OrderService orderService;

    public SampleController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }

    @GetMapping("/order/count")
    public Long count() {
        return orderService.countAll();
    }

}
