package com.demo.myfirstagent.repository;

import com.demo.myfirstagent.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
