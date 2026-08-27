package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.agent.RefundProcessAgent;
import com.demo.myfirstagent.agent.VerifierAgent;
import com.demo.myfirstagent.model.VerificationFindings;
import org.springframework.stereotype.Component;

@Component
public class SupportCoordinator {
    private final RefundProcessAgent refundProcessAgent;
    private final VerifierAgent verifierAgent;

    public SupportCoordinator(RefundProcessAgent refundProcessAgent, VerifierAgent verifierAgent){
        this.refundProcessAgent = refundProcessAgent;
        this.verifierAgent = verifierAgent;
    }

    public String handleRefund(String customerId, String orderId){
        VerificationFindings verification = verifierAgent.verify("Verify customer "+ customerId + ". Use the customer lookup tool.");
        System.out.println("\n[COORDINATOR] Verification findings: ");
        System.out.println(verification);

        if(!verification.verified()){
            return "Customer verification failed. Refund cannot be processed.";
        }

        String refundTask = """
                Process a refund for order %s.
                
                VERIFIED CUSTOMER:
                Customer Id: %s
                Customer Name: %s
                Plan: %s
                
                The customer has already been verified.
                
                Steps:
                1. Lookup the order
                2. Confirm it belongs to customer %s.
                3. If confirmed, refund the exact full order amount.
                """.formatted(orderId, verification.customerId(), verification.customerName(), verification.plan(), verification.customerId());

        String refundResult = refundProcessAgent.process(refundTask);

        return """
                Customer Support Case
                
                Verification:
               %s
                
                Refund: %s
                """.formatted(verification, refundResult);
    }
}
