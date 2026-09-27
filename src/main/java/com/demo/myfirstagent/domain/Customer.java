package com.demo.myfirstagent.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private String customerId;
    private String name;
    private String email;
    private String plan;

    protected Customer() {
    }

    public Customer(String customerId, String name, String email, String plan) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.plan = plan;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPlan() {
        return plan;
    }
}
