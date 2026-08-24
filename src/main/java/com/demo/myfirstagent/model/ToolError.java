package com.demo.myfirstagent.model;

public record ToolError(
        String errorCategory,
        boolean retryable,
        String description,
        Integer retryAfterMs
){}
