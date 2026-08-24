package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.data.FakeDatabase;
import com.demo.myfirstagent.guard.AgentSession;
import com.demo.myfirstagent.guard.PreToolGuard;
import com.demo.myfirstagent.guard.ToolDecision;
import com.demo.myfirstagent.model.ToolError;
import com.demo.myfirstagent.model.ToolResponse;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private final PreToolGuard preToolGuard;
    private final AgentSession agentSession;

    public OrderTools(PreToolGuard preToolGuard, AgentSession agentSession) {
        this.preToolGuard = preToolGuard;
        this.agentSession = agentSession;
    }

    @Tool("""
            Fetch the details of an order by orderId.
            Returns the order amount, item, customerId, and status.
            The order Id has the format O followed by digits, for example O100.
            """)
    public ToolResponse<FakeDatabase.OrderRecord> lookUpOrder(String orderId){
        ToolDecision decision = preToolGuard.check("lookUpOrder");

        //Customer must be verified
        if(!decision.allowed()){
            System.out.println("[PRE-TOOL]: Blocked lookUpOrder");
            return ToolResponse.blocked(decision.reason(), decision.requiredTool());
        }

        System.out.println("[TOOL]: lookUpOrder(" + orderId + ")");
        FakeDatabase.OrderRecord order = FakeDatabase.ORDERS.get(orderId);
        if(order == null){
            return ToolResponse.error(new ToolError("validation", false, "No order found with id "+ orderId, null));
        }
        agentSession.recordOrderLookup(order.orderId(), order.customerid());
        return ToolResponse.success(order);
    }

    @Tool("""
            Process a refund for an order.
            The customer must already be verified.
            The order must have been looked up and confirmed to belong to the verified customer.
            The amount must be the exact order amount.
            Do not guess the refund amount.
            """)
    public ToolResponse<String> processRefund(String orderId, double amount){
        ToolDecision decision = preToolGuard.check("processRefund");

        // Customer should be verified
        if(!decision.allowed()){
            System.out.println("[PRE-TOOL]: Blocked processRefund");
            return ToolResponse.blocked(decision.reason(), decision.requiredTool());
        }

        // Order must belong to same customer verification
        if(!agentSession.isOrderLookedUpForVerifiedCustomer(orderId)){
            System.out.println("[PRE-TOOL]: locked processRefund - order not verified");
            return ToolResponse.blocked("order not verified for the verified customer", "lookUpOrder");
        }

        //Refund amount check
        ToolDecision refundPolicy = preToolGuard.checkRefundAmount(amount);
        if(!refundPolicy.allowed()){
            System.out.println("[PRE-TOOL]: Blocked processRefund - "+ refundPolicy.reason());
            return ToolResponse.blocked(refundPolicy.reason(), refundPolicy.requiredTool());
        }


        System.out.println("[TOOL]: processRefund(" + orderId + ", " + amount + ")");
        FakeDatabase.OrderRecord order = FakeDatabase.ORDERS.get(orderId);
        if(order == null){
            return ToolResponse.error(new ToolError("validation", false, "No order found with id "+ orderId, null));
        }
        if(order.status() == 5){
            return ToolResponse.blocked("Order is already refunded.", null);
        }

        double orderAmount = order.amountCents() / 100.0;
        if(Double.compare(amount, orderAmount) != 0){
            return ToolResponse.blocked("Amount must be equal to order amount.", null);
        }

        String refundId = "REF-" + orderId;
        return ToolResponse.success("Refund processed successfully. " + "Rwfund id-> " + refundId + ", amount: $" + String.format("%.2f", amount));
    }
}
