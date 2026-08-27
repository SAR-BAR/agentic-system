package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.agent.RefundProcessAgent;
import com.demo.myfirstagent.agent.VerifierAgent;
import com.demo.myfirstagent.coordinator.CoordinatorContext;
import com.demo.myfirstagent.model.VerificationFindings;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CoordinatorTools {

    private final VerifierAgent verifierAgent;
    private final RefundProcessAgent refundProcessAgent;
    private final CoordinatorContext coordinatorContext;

    public CoordinatorTools(VerifierAgent verifierAgent, RefundProcessAgent refundProcessAgent, CoordinatorContext coordinatorContext) {
        this.refundProcessAgent = refundProcessAgent;
        this.verifierAgent = verifierAgent;
        this.coordinatorContext = coordinatorContext;
    }

    @Tool("""
            Verify a customer using the customer verification specialist.
            Use this when customer identity needs to be verified.
            
            You must provide the customerId exactly as supplied by user. NEVER call this tool with a null or empty customerId.
            """)
    public VerificationFindings verifyCustomer(String customerId){
        VerificationFindings findings = verifierAgent.verify("Verify customer "+ customerId);
        System.out.println("[COORDINATOR TOOL]: verifyCustomer done: " + customerId);
        coordinatorContext.setVerificationFindings(findings);
        return findings;
    }

    @Tool("""
            Process a refund using the Refund Processor Specialist.
            Before using this tool, the customer must already have been verified.
            A verified customer findings must exist in the coordinator context before this toll can be used.
            
            The refund specialist will look up the order, confirm it belongs to the verified customer, 
            determine the exact amount of refund, and then process the refund if allowed.
            
            Do not invent or guess the refund amount.
            """)
    public String refundProcess(String orderId){
        System.out.println("[COORDINATOR TOOL]: refundRequest in process for order: " + orderId);
        VerificationFindings verification = coordinatorContext.getVerificationFindings();

        if(verification == null || !verification.verified()){
            return "Cannot process refund. Customer has not been verified. ";
        }
        return refundProcessAgent.process("""
                Process a refund for order %s. 
                Verified customer %s. 
                customerName = %s
                plan = %s
                
                Look up te order and refund the exact order amount.
                """.formatted(orderId, verification.customerId(), verification.customerName(), verification.plan()));
    }
}
