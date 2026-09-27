package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.guard.AgentSession;

import java.util.UUID;

// Everything that belongs to a single customer request. A new one is created for every handleRequest call.
public record SupportCase(
        String caseId,
        AgentSession session,
        CoordinatorContext context
){
    public static SupportCase open(){
        String caseId = "CASE-" + UUID.randomUUID().toString().substring(0, 8);
        return new SupportCase(caseId, new AgentSession(caseId), new CoordinatorContext());
    }
}
