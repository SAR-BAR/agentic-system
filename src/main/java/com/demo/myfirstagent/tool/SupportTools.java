package com.demo.myfirstagent.tool;


import com.demo.myfirstagent.guard.AgentSession;
import com.demo.myfirstagent.model.ToolResponse;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class SupportTools {

    private final AgentSession agentSession;

    public SupportTools(AgentSession agentSession) {
        this.agentSession = agentSession;
    }

    @Tool("""
            Escalate the current customer support case to a human agent.
            Use this immediately when:
            - the customer explicitly asks for a human or manager.
            - a request exceeds the agent's authority.
            - repeated failures require human intervention.
            
            Do not try to resolve a request that exceeds your authority.
            """)
    public ToolResponse<String> escalateTohuman(String reason){
        String customerId = agentSession.getCustomerId();
        String ticketId = "ESC-" + (customerId !=null ? customerId : "UNK");
        System.out.println("===============");
        System.out.println("[ESCALATION]: HUMAN INPUT REQUIRED ");
        System.out.println("Ticket: "+ ticketId);
        System.out.println("Customer: "+ (customerId !=null ? customerId : "UNK"));
        System.out.println("Reason: "+ reason);
        System.out.println("===============");

        return ToolResponse.success("Sent for human input "+ ticketId);
    }
}
