package com.demo.myfirstagent.data;

import java.util.Map;

public class FakeDatabase {
    public record CustomerRecord(
            String customerId,
            String name,
            String email,
            String plan
    ){}

    public record OrderRecord(
            String orderId,
            String customerid,
            String item,
            int amountCents,
            int status,
            long orderedAt
    ){}


    public static final Map<String, CustomerRecord> CUSTOMERS = Map.of(
            "C001", new CustomerRecord("C001", "Alice", "alice@example.com", "pro"),
            "C002", new CustomerRecord("C002", "Bob", "bob@example.com", "free"),
            "C003", new CustomerRecord("C003", "Charlie", "charlie@example.com", "pro"),
            "C004", new CustomerRecord("C004", "Diana", "diana@example.com", "free"),
            "C005", new CustomerRecord("C005", "Ethan", "ethan@example.com", "enterprise"),
            "C006", new CustomerRecord("C006", "Fiona", "fiona@example.com", "pro"),
            "C007", new CustomerRecord("C007", "George", "george@example.com", "free"),
            "C008", new CustomerRecord("C008", "Hannah", "hannah@example.com", "enterprise"),
            "C009", new CustomerRecord("C009", "Ian", "ian@example.com", "pro"),
            "C010", new CustomerRecord("C010", "Julia", "julia@example.com", "free")
    );

    public static final Map<String, OrderRecord> ORDERS = Map.of(
            "O001", new OrderRecord("O001", "C001", "Annual Plan", 9900, 3, 1740009600),
            "O002", new OrderRecord("O002", "C002", "Monthly Plan", 900, 5, 1738886400),
            "O003", new OrderRecord("O003", "C001", "Enterprise Plan(annual)", 14500, 3, 1740614400),
            "O004", new OrderRecord("O004", "C003", "Annual Plan", 9900, 4, 1741219200),
            "O005", new OrderRecord("O005", "C004", "Monthly Plan", 900, 2, 1741824000),
            "O006", new OrderRecord("O006", "C005", "Enterprise Plan(annual)", 14500, 5, 1742428800),
            "O007", new OrderRecord("O007", "C006", "Annual Plan", 9900, 1, 1743033600),
            "O008", new OrderRecord("O008", "C007", "Monthly Plan", 900, 3, 1743638400),
            "O009", new OrderRecord("O009", "C008", "Enterprise Plan(annual)", 14500, 4, 1744243200),
            "O010", new OrderRecord("O010", "C009", "Annual Plan", 9900, 2, 1744848000)
    );
}
