package com.demo.myfirstagent.agent;

import com.demo.myfirstagent.model.VerificationFindings;
import com.demo.myfirstagent.tool.CustomerTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

@Component
public class VerifierAgent {
    private final VerifierAssistant verifierAgent;

    public VerifierAgent(ChatModel chatModel, CustomerTools customerTools) {
        this.verifierAgent = AiServices.builder(VerifierAssistant.class)
                .chatModel(chatModel)
                .tools(customerTools)
                .build();
    }

    public VerificationFindings verify(String message){
        return verifierAgent.verify(message);
    }
}
