package com.demo.myfirstagent.tool;


import com.demo.myfirstagent.coordinator.SupportCase;
import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.model.ToolResponse;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class SupportTools {

    private final SupportCaseHolder supportCaseHolder;

    public SupportTools(SupportCaseHolder supportCaseHolder) {
        this.supportCaseHolder = supportCaseHolder;
    }

    @Tool("""
            Escalate the current customer support case to a human agent.
            Use this only when:
            - the customer explicitly asks for a human or manager.
            - a tool result says requiredTool = escalateTohuman (for example, a refund over the agent limit).

            Do not escalate when an order is already refunded, not found, or belongs to another customer.
            In those cases, tell the customer the outcome instead.
            """)
    public ToolResponse<String> escalateTohuman(String reason){
        SupportCase supportCase = supportCaseHolder.current();
        String customerId = supportCase.session().getCustomerId();

        // An already-refunded order needs no human action, whatever the model decides
        if(supportCase.session().isLastLookedUpOrderRefunded()){
            System.out.println("[PRE-TOOL]: Blocked escalateTohuman - order already refunded");
            return ToolResponse.blocked("Order " + supportCase.session().getLastLookedUpOrderId()
                    + " was already refunded. No escalation needed; tell the customer it was already refunded.", null);
        }

        String ticketId = "ESC-" + supportCase.caseId();
        System.out.println("===============");
        System.out.println("[ESCALATION]: HUMAN INPUT REQUIRED ");
        System.out.println("Ticket: "+ ticketId);
        System.out.println("Case: "+ supportCase.caseId());
        System.out.println("Customer: "+ (customerId !=null ? customerId : "UNK"));
        System.out.println("Reason: "+ reason);
        System.out.println("===============");

        return ToolResponse.success("Sent for human input "+ ticketId);
    }
}
