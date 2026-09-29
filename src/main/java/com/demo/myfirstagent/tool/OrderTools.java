package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.domain.Order;
import com.demo.myfirstagent.guard.AgentSession;
import com.demo.myfirstagent.guard.PreToolGuard;
import com.demo.myfirstagent.guard.ToolDecision;
import com.demo.myfirstagent.model.OrderDetails;
import com.demo.myfirstagent.model.ToolError;
import com.demo.myfirstagent.model.ToolResponse;
import com.demo.myfirstagent.repository.OrderRepository;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private final PreToolGuard preToolGuard;
    private final SupportCaseHolder supportCaseHolder;
    private final OrderRepository orderRepository;

    public OrderTools(PreToolGuard preToolGuard, SupportCaseHolder supportCaseHolder, OrderRepository orderRepository) {
        this.preToolGuard = preToolGuard;
        this.supportCaseHolder = supportCaseHolder;
        this.orderRepository = orderRepository;
    }

    @Tool("""
            Fetch the details of an order by orderId.
            Returns the order amount in US dollars (amountUsd), item, customerId, and whether it was already refunded.
            The order Id has the format O followed by digits, for example O100.
            """)
    public ToolResponse<OrderDetails> lookUpOrder(String orderId){
        AgentSession session = supportCaseHolder.current().session();
        ToolDecision decision = preToolGuard.check("lookUpOrder", session);

        //Customer must be verified
        if(!decision.allowed()){
            System.out.println("[PRE-TOOL]: Blocked lookUpOrder");
            return ToolResponse.blocked(decision.reason(), decision.requiredTool());
        }

        System.out.println("[TOOL]: lookUpOrder(" + orderId + ")");
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            return ToolResponse.error(new ToolError("validation", false, "No order found with id "+ orderId, null));
        }
        session.recordOrderLookup(order.getOrderId(), order.getCustomerId(), order.getStatus() == Order.STATUS_REFUNDED);
        return ToolResponse.success(OrderDetails.from(order));
    }

    @Tool("""
            Process a refund for an order.
            The customer must already be verified.
            The order must have been looked up and confirmed to belong to the verified customer.
            The amount must be the exact order amountUsd returned by lookUpOrder.
            Do not guess the refund amount.
            """)
    public ToolResponse<String> processRefund(String orderId, @P("Refund amount in US dollars, e.g. 99.00") double amount){
        AgentSession session = supportCaseHolder.current().session();
        ToolDecision decision = preToolGuard.check("processRefund", session);

        // Customer should be verified
        if(!decision.allowed()){
            System.out.println("[PRE-TOOL]: Blocked processRefund");
            return ToolResponse.blocked(decision.reason(), decision.requiredTool());
        }

        // Order must belong to same customer verification
        if(!session.isOrderLookedUpForVerifiedCustomer(orderId)){
            System.out.println("[PRE-TOOL]: Blocked processRefund - order not verified");
            return ToolResponse.blocked("order not verified for the verified customer", "lookUpOrder");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            return ToolResponse.error(new ToolError("validation", false, "No order found with id "+ orderId, null));
        }

        // Already refunded is a hard stop, checked before anything that could route to escalation
        if(order.getStatus() == Order.STATUS_REFUNDED){
            System.out.println("[PRE-TOOL]: Blocked processRefund - already refunded");
            return ToolResponse.blocked("Order is already refunded. Do not retry or escalate; tell the customer it was already refunded.", null);
        }

        if(Math.round(amount * 100) != order.getAmount()){
            System.out.println("[PRE-TOOL]: Blocked processRefund - amount mismatch");
            return ToolResponse.blocked(String.format("Amount must be equal to order amount $%.2f.", order.getAmount() / 100.0), null);
        }

        //Refund amount check
        ToolDecision refundPolicy = preToolGuard.checkRefundAmount(amount);
        if(!refundPolicy.allowed()){
            System.out.println("[PRE-TOOL]: Blocked processRefund - "+ refundPolicy.reason());
            return ToolResponse.blocked(refundPolicy.reason(), refundPolicy.requiredTool());
        }

        System.out.println("[TOOL]: processRefund(" + orderId + ", " + amount + ")");
        String refundId = "REF-" + orderId;
        return ToolResponse.success("Refund processed successfully. " + "Refund id-> " + refundId + ", amount: $" + String.format("%.2f", amount));
    }
}
