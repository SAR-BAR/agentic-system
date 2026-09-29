package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.agent.CoordinationAssistant;
import com.demo.myfirstagent.tool.CoordinatorTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

@Component
public class SupportCoordinator {
   private final CoordinationAssistant coordinationAssistant;
   private final SupportCaseHolder supportCaseHolder;

    public SupportCoordinator(ChatModel chatModel, CoordinatorTools coordinatorTools, SupportCaseHolder supportCaseHolder) {
       this.supportCaseHolder = supportCaseHolder;
        this.coordinationAssistant = AiServices.builder(CoordinationAssistant.class)
                .chatModel(chatModel)
                .tools(coordinatorTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }

    public String handleRequest(String userRequest){
        SupportCase supportCase = SupportCase.open();
        System.out.println("[COORDINATOR]: opened " + supportCase.caseId());
        return supportCaseHolder.runInCase(supportCase, () -> coordinationAssistant.handle(userRequest));
    }

    // Keeps one SupportCase bound for every turn, so verification carries over between messages.
    public void runConversation(Runnable conversation){
        SupportCase supportCase = SupportCase.open();
        System.out.println("[COORDINATOR]: opened " + supportCase.caseId());
        supportCaseHolder.runInCase(supportCase, () -> {
            conversation.run();
            return null;
        });
    }

    public String handleTurn(String userMessage){
        return coordinationAssistant.handle(userMessage);
    }
}
