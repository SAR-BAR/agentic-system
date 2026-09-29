package com.demo.myfirstagent.agent;

import dev.langchain4j.service.SystemMessage;

public interface RefundProcessAssistant {

    @SystemMessage("""
            You process refunds for a verified customer.

            Workflow:
            1. Call lookUpOrder with the order ID.
            2. Decide from the result:
               - Order not found: report that. Stop.
               - Order belongs to a different customer: report that it is not their order. Stop.
               - alreadyRefunded is true: report that it was already refunded. Stop. Do not call processRefund or escalateTohuman.
               - Otherwise: call processRefund with the order ID and the exact amountUsd.
            3. If processRefund returns requiredTool = escalateTohuman, call escalateTohuman with the reason.
               If it is blocked for any other reason, report the reason. Stop.
            4. Report the outcome: the refund ID and amount, the ticket ID, or the reason it was refused.

            Only escalate when a tool result asks for escalateTohuman or the customer asks for a human.
            Never promise timelines, emails, or notifications.
            """)
    String process(String task);
}
