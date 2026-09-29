package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.agent.RefundProcessAgent;
import com.demo.myfirstagent.agent.VerifierAgent;
import com.demo.myfirstagent.coordinator.SupportCase;
import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.domain.Customer;
import com.demo.myfirstagent.model.VerificationFindings;
import com.demo.myfirstagent.repository.CustomerRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CoordinatorTools {

    private final VerifierAgent verifierAgent;
    private final RefundProcessAgent refundProcessAgent;
    private final SupportCaseHolder supportCaseHolder;
    private final CustomerRepository customerRepository;

    public CoordinatorTools(VerifierAgent verifierAgent, RefundProcessAgent refundProcessAgent, SupportCaseHolder supportCaseHolder, CustomerRepository customerRepository) {
        this.refundProcessAgent = refundProcessAgent;
        this.verifierAgent = verifierAgent;
        this.supportCaseHolder = supportCaseHolder;
        this.customerRepository = customerRepository;
    }

    @Tool("""
            Verify a customer using the customer verification specialist.
            Use this when customer identity needs to be verified.

            You must provide the customerId exactly as supplied by user. NEVER call this tool with a null or empty customerId.
            """)
    public VerificationFindings verifyCustomer(String customerId){
        SupportCase supportCase = supportCaseHolder.current();
        VerificationFindings reported = verifierAgent.verify("Verify customer "+ customerId);
        // Small local models sometimes skip or garble the lookup, so give the verifier one more try.
        if(!supportCase.session().isVerified(customerId)){
            System.out.println("[COORDINATOR TOOL]: lookup did not succeed for " + customerId + ", asking verifier again");
            reported = verifierAgent.verify("Verify customer "+ customerId + ". Call getcustomerRecord with customerid = " + customerId + ".");
        }

        // The verifier's answer is only a claim. The session records whether getcustomerRecord actually succeeded.
        VerificationFindings findings;
        Customer customer = supportCase.session().isVerified(customerId)
                ? customerRepository.findById(supportCase.session().getCustomerId()).orElse(null)
                : null;
        if(customer != null){
            findings = new VerificationFindings(true, customer.getCustomerId(), customer.getName(), customer.getPlan());
        } else {
            findings = new VerificationFindings(false, customerId, null, null);
        }

        if(reported != null && reported.verified() != findings.verified()){
            System.out.println("[COORDINATOR TOOL]: verifier reported verified=" + reported.verified() + " but session says verified=" + findings.verified() + " for " + customerId + ". Using session.");
        }
        System.out.println("[COORDINATOR TOOL]: verifyCustomer done: " + customerId + " verified=" + findings.verified());
        supportCase.context().setVerificationFindings(findings);
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
        VerificationFindings verification = supportCaseHolder.current().context().getVerificationFindings();

        if(verification == null || !verification.verified()){
            return "Cannot process refund. Customer has not been verified. ";
        }
        String result = refundProcessAgent.process("""
                Process a refund for order %s.
                Verified customer %s.
                customerName = %s
                plan = %s

                Look up the order and refund the exact order amount only if it is eligible.
                If it is not eligible, explain why.
                """.formatted(orderId, verification.customerId(), verification.customerName(), verification.plan()));

        if(result == null || result.isBlank()){
            System.out.println("[COORDINATOR TOOL]: refund agent returned no result for order: " + orderId);
            return "Refund agent returned no result for order " + orderId + ". The refund was not confirmed; escalate to a human.";
        }
        return result;
    }
}
