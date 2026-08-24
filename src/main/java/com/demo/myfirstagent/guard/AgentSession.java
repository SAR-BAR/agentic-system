package com.demo.myfirstagent.guard;

import org.springframework.stereotype.Component;

@Component
public class AgentSession {

    private boolean customerVerified;
    private String customerId;
    private String lastLookedUpOrderId;
    private String lastlookedUporderCustomerId;

    public boolean isCustomerVerified(){
        return customerVerified;
    }

    public String getCustomerId(){
        return customerId;
    }

    public void verifyCustomer(String customerId){
        this.customerVerified = true;
        this.customerId = customerId;
        System.out.println("[SESSION]: Customer Verified "+ customerId);
    }

    public void recordOrderLookup(String orderId, String orderCustomerId){
        this.lastLookedUpOrderId = orderId;
        this.lastlookedUporderCustomerId = orderCustomerId;
        System.out.println("[SESSION]: Order Lookup recorded: "+ orderId);
    }

    public boolean isOrderLookedUpForVerifiedCustomer(String orderId){
        return customerVerified && this.customerId.equals(lastlookedUporderCustomerId) && orderId.equals(lastLookedUpOrderId);
    }

    public void reset(){
        this.customerVerified = false;
        this.customerId = null;
        this.lastLookedUpOrderId = null;
        this.lastlookedUporderCustomerId = null;
    }
}
