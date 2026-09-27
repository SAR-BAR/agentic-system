package com.demo.myfirstagent.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    public static final int STATUS_REFUNDED = 5;

    @Id
    private String orderId;
    private String customerId;
    private String item;
    private int amount;
    private int status;
    private long orderedAt;

    protected Order() {
    }

    public Order(String orderId, String customerId, String item, int amount, int status, long orderedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.item = item;
        this.amount = amount;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public int getStatus() {
        return status;
    }

    public long getOrderedAt() {
        return orderedAt;
    }
}
