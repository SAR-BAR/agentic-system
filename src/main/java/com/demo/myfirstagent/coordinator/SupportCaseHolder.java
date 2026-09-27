package com.demo.myfirstagent.coordinator;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

// Binds the active SupportCase to the thread handling the request.
// LangChain4j runs tool calls (including the nested agents) on the caller's thread,
// so every tool invoked during one request sees the same case, and parallel requests never share one.
@Component
public class SupportCaseHolder {

    private final ThreadLocal<SupportCase> activeCase = new ThreadLocal<>();

    public <T> T runInCase(SupportCase supportCase, Supplier<T> work){
        if(activeCase.get() != null){
            throw new IllegalStateException("A support case is already active on this thread: " + activeCase.get().caseId());
        }
        activeCase.set(supportCase);
        try {
            return work.get();
        } finally {
            activeCase.remove();
        }
    }

    public SupportCase current(){
        SupportCase supportCase = activeCase.get();
        if(supportCase == null){
            throw new IllegalStateException("No active support case. Tools must be called through SupportCoordinator.handleRequest.");
        }
        return supportCase;
    }
}
