package com.demo.myfirstagent;

import com.demo.myfirstagent.coordinator.SupportCase;
import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.model.ToolResponse;
import com.demo.myfirstagent.tool.CustomerTools;
import com.demo.myfirstagent.tool.OrderTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

// Calls the tools directly (no LLM), so the interleaving is deterministic and the test runs in seconds.
// The barriers force every request to verify before any request looks up an order: with shared
// singleton state, each thread would see the last customer verified instead of its own.
@SpringBootTest(properties = "agent.demo.enabled=false")
class ConcurrentRefundTest {

    @Autowired
    private SupportCaseHolder supportCaseHolder;

    @Autowired
    private CustomerTools customerTools;

    @Autowired
    private OrderTools orderTools;

    record Request(String customerId, String orderId, double amount, String otherCustomersOrderId) {}

    @Test
    void concurrentRequestsKeepSeparateSessions() throws Exception {
        List<Request> requests = List.of(
                new Request("C001", "O001", 99.00, "O004"),
                new Request("C003", "O004", 99.00, "O005"),
                new Request("C004", "O005", 9.00, "O001"));

        CyclicBarrier allVerified = new CyclicBarrier(requests.size());
        CyclicBarrier allLookedUp = new CyclicBarrier(requests.size());
        ExecutorService pool = Executors.newFixedThreadPool(requests.size());

        List<Future<?>> results = new ArrayList<>();
        for (Request request : requests) {
            results.add(pool.submit(() -> supportCaseHolder.runInCase(SupportCase.open(), () -> {
                try {
                    assertTrue(customerTools.getcustomerRecord(request.customerId()).success());
                    allVerified.await(10, TimeUnit.SECONDS);

                    assertTrue(orderTools.lookUpOrder(request.orderId()).success());
                    allLookedUp.await(10, TimeUnit.SECONDS);

                    SupportCase current = supportCaseHolder.current();
                    assertEquals(request.customerId(), current.session().getCustomerId(), "session leaked another customer");
                    assertEquals(request.orderId(), current.session().getLastLookedUpOrderId(), "session leaked another order");

                    // Another customer's order must be refused, even though another thread looked it up
                    ToolResponse<String> crossRefund = orderTools.processRefund(request.otherCustomersOrderId(), 99.00);
                    assertFalse(crossRefund.success(), "refunded another customer's order");

                    ToolResponse<String> ownRefund = orderTools.processRefund(request.orderId(), request.amount());
                    assertTrue(ownRefund.success(), () -> "own refund failed: " + ownRefund.error());
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })));
        }

        pool.shutdown();
        for (Future<?> result : results) {
            result.get(30, TimeUnit.SECONDS); // rethrows any assertion failure from the worker thread
        }
    }

    @Test
    void toolsRefuseToRunOutsideACase() {
        assertThrows(IllegalStateException.class, () -> orderTools.lookUpOrder("O001"));
    }
}
