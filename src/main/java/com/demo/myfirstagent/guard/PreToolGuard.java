package com.demo.myfirstagent.guard;

import org.springframework.stereotype.Component;

@Component
public class PreToolGuard {
    private final AgentSession session;

    public PreToolGuard(AgentSession session) {
        this.session = session;
    }

    public ToolDecision check(String toolName){
        if((toolName.equals("lookUpOrder") || toolName.equals("processRefund")) && !session.isCustomerVerified()){
            return ToolDecision.deny("Customer must be verified before looking up an order", "getcustomerRecord");
        }
        return ToolDecision.allow();
    }

    public ToolDecision checkRefundAmount(double amount){
        if(amount > 500){
            return ToolDecision.deny(String.format("Refund $%.2f exceeds the $500 agent limit. ", amount), "escalateTohuman");
        }
        return ToolDecision.allow();
    }
}
