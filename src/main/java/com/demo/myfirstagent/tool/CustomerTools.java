package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.data.FakeDatabase;
import com.demo.myfirstagent.guard.AgentSession;
import com.demo.myfirstagent.model.ToolError;
import com.demo.myfirstagent.model.ToolResponse;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CustomerTools {

    private final AgentSession agentSession;

    public CustomerTools(AgentSession agentSession) {
        this.agentSession = agentSession;
    }

    @Tool("""
            Retrive a customer profile by customerId.
            Use this tool to verify that a customer exists.
            Customerids have the format C followed by digits, for example C001.
            """)
    public ToolResponse<FakeDatabase.CustomerRecord> getcustomerRecord(String customerid){
        System.out.println("[TOOL]: getcustomer(" + customerid + ")");

        FakeDatabase.CustomerRecord customer = FakeDatabase.CUSTOMERS.get(customerid);
        if(customer == null){
            return ToolResponse.error(new ToolError("validation", false, "No customer found with id "+ customerid, null));
        }
        agentSession.verifyCustomer(customerid);
        return ToolResponse.success(customer);
    }
}
