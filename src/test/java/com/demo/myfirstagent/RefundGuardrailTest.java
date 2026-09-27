package com.demo.myfirstagent;

import com.demo.myfirstagent.agent.VerifierAgent;
import com.demo.myfirstagent.coordinator.SupportCase;
import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.model.OrderDetails;
import com.demo.myfirstagent.model.ToolResponse;
import com.demo.myfirstagent.model.VerificationFindings;
import com.demo.myfirstagent.tool.CoordinatorTools;
import com.demo.myfirstagent.tool.CustomerTools;
import com.demo.myfirstagent.tool.OrderTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// Exercises the deterministic guardrails directly, without the LLM deciding whether to call them.
@SpringBootTest(properties = "agent.demo.enabled=false")
class RefundGuardrailTest {

    @Autowired
    private SupportCaseHolder supportCaseHolder;

    @Autowired
    private CustomerTools customerTools;

    @Autowired
    private OrderTools orderTools;

    @Autowired
    private CoordinatorTools coordinatorTools;

    @MockitoBean
    private VerifierAgent verifierAgent;

    private <T> T inNewCase(Supplier<T> work) {
        return supportCaseHolder.runInCase(SupportCase.open(), work);
    }

    @Test
    void orderAmountIsShownInDollars() {
        OrderDetails order = inNewCase(() -> {
            customerTools.getcustomerRecord("C001");
            return orderTools.lookUpOrder("O001").data();
        });
        assertEquals(99.00, order.amountUsd());
        assertFalse(order.alreadyRefunded());
    }

    @Test
    void refundIsBlockedForAnotherCustomersOrder() {
        ToolResponse<String> result = inNewCase(() -> {
            customerTools.getcustomerRecord("C001");
            orderTools.lookUpOrder("O004"); // belongs to C003
            return orderTools.processRefund("O004", 99.00);
        });
        assertFalse(result.success());
        assertEquals("lookUpOrder", result.requiredTool());
    }

    @Test
    void alreadyRefundedIsRefusedEvenWithAWrongAmount() {
        ToolResponse<String> result = inNewCase(() -> {
            customerTools.getcustomerRecord("C002");
            orderTools.lookUpOrder("O002"); // status refunded
            return orderTools.processRefund("O002", 900.00); // cents mistaken for dollars
        });
        assertFalse(result.success());
        assertEquals("Order is already refunded.", result.error().description());
        assertNull(result.requiredTool(), "must not route an already-refunded order to escalation");
    }

    @Test
    void refundWithWrongAmountIsBlocked() {
        ToolResponse<String> result = inNewCase(() -> {
            customerTools.getcustomerRecord("C001");
            orderTools.lookUpOrder("O001");
            return orderTools.processRefund("O001", 9900.00);
        });
        assertFalse(result.success());
    }

    @Test
    void refundOfExactAmountSucceeds() {
        ToolResponse<String> result = inNewCase(() -> {
            customerTools.getcustomerRecord("C001");
            orderTools.lookUpOrder("O001");
            return orderTools.processRefund("O001", 99.00);
        });
        assertTrue(result.success());
    }

    @Test
    void verifierClaimIsIgnoredWhenTheLookupNeverRan() {
        // The verifier model says "verified" but never called getcustomerRecord
        when(verifierAgent.verify(anyString())).thenReturn(new VerificationFindings(true, "C001", "Made Up", "gold"));

        VerificationFindings findings = inNewCase(() -> coordinatorTools.verifyCustomer("C001"));

        assertFalse(findings.verified());
        assertEquals("Cannot process refund. Customer has not been verified. ",
                inNewCase(() -> {
                    coordinatorTools.verifyCustomer("C001");
                    return coordinatorTools.refundProcess("O001");
                }));
    }

    @Test
    void verifiedFindingsComeFromTheDatabase() {
        // The verifier did call the tool, but reported a wrong name
        when(verifierAgent.verify(anyString())).thenAnswer(invocation -> {
            customerTools.getcustomerRecord("C001");
            return new VerificationFindings(true, "C001", "Made Up", "gold");
        });

        VerificationFindings findings = inNewCase(() -> coordinatorTools.verifyCustomer("C001"));

        assertTrue(findings.verified());
        assertNotEquals("Made Up", findings.customerName());
    }
}
