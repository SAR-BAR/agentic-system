package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.agent.CoordinationAssistant;
import com.demo.myfirstagent.tool.CoordinatorTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

@Component
public class SupportCoordinator {
   private final CoordinationAssistant coordinationAssistant;
   private final CoordinatorContext coordinatorContext;

    public SupportCoordinator(ChatModel chatModel, CoordinatorTools coordinatorTools, CoordinatorContext context) {
       this.coordinatorContext = context;
        this.coordinationAssistant = AiServices.builder(CoordinationAssistant.class)
                .chatModel(chatModel)
                .tools(coordinatorTools)
                .build();
    }

    public String handleRequest(String userRequest){
        this.coordinatorContext.clear();
        return coordinationAssistant.handle(userRequest);
    }
}
