package com.demo.myfirstagent.repository;

import com.demo.myfirstagent.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
