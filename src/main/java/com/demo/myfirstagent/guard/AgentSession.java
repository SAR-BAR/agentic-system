package com.demo.myfirstagent.guard;

// Facts recorded by tools during one request. Owned by a SupportCase, never a Spring bean.
public class AgentSession {

    private final String caseId;
    private boolean customerVerified;
    private String customerId;
    private String lastLookedUpOrderId;
    private String lastlookedUporderCustomerId;
    private boolean lastLookedUpOrderRefunded;

    public AgentSession(String caseId) {
        this.caseId = caseId;
    }

    public boolean isCustomerVerified(){
        return customerVerified;
    }

    public String getCustomerId(){
        return customerId;
    }

    public String getLastLookedUpOrderId(){
        return lastLookedUpOrderId;
    }

    public boolean isVerified(String customerId){
        return customerVerified && this.customerId.equalsIgnoreCase(customerId);
    }

    public void verifyCustomer(String customerId){
        this.customerVerified = true;
        this.customerId = customerId;
        System.out.println("[SESSION " + caseId + "]: Customer Verified "+ customerId);
    }

    public boolean isLastLookedUpOrderRefunded(){
        return lastLookedUpOrderRefunded;
    }

    public void recordOrderLookup(String orderId, String orderCustomerId, boolean refunded){
        this.lastLookedUpOrderId = orderId;
        this.lastlookedUporderCustomerId = orderCustomerId;
        this.lastLookedUpOrderRefunded = refunded;
        System.out.println("[SESSION " + caseId + "]: Order Lookup recorded: "+ orderId);
    }

    public boolean isOrderLookedUpForVerifiedCustomer(String orderId){
        return customerVerified && this.customerId.equalsIgnoreCase(lastlookedUporderCustomerId) && orderId.equalsIgnoreCase(lastLookedUpOrderId);
    }
}
