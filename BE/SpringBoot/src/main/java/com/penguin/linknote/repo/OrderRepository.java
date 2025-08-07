package com.penguin.linknote.repo;

import com.penguin.linknote.entity.Orders;
import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<Orders, Long> {
}
