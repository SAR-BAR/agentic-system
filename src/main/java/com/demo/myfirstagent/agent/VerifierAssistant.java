package com.demo.myfirstagent.agent;


import com.demo.myfirstagent.model.VerificationFindings;
import dev.langchain4j.service.SystemMessage;

public interface VerifierAssistant {

    @SystemMessage("""
            You verify customers. Always call getcustomerRecord with the customer ID you were given.
            Report verified = true only if getcustomerRecord succeeded. If it failed, report verified = false.
            Copy the customer's name and plan from the tool result. Never guess them.
            """)
    VerificationFindings verify(String task);

}
