package com.demo.myfirstagent.model;

import com.demo.myfirstagent.domain.Order;

// What the model sees for an order. Amounts are converted from stored cents to dollars
// so the model never mistakes 9900 cents for $9900.
public record OrderDetails(
        String orderId,
        String customerId,
        String item,
        double amountUsd,
        boolean alreadyRefunded
){
    public static OrderDetails from(Order order){
        return new OrderDetails(order.getOrderId(), order.getCustomerId(), order.getItem(),
                order.getAmount() / 100.0, order.getStatus() == Order.STATUS_REFUNDED);
    }
}
