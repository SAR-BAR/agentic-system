package com.demo.myfirstagent.model;

public record VerificationFindings (
        boolean verified,
        String customerId,
        String customerName,
        String plan
){}
