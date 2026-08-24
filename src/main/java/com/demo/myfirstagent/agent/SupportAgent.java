package com.demo.myfirstagent.agent;

import com.demo.myfirstagent.tool.CustomerTools;
import com.demo.myfirstagent.tool.OrderTools;
import com.demo.myfirstagent.tool.SupportTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

//Component which uses chatModel created to trigger LLM calls
@Component
public class SupportAgent {

    private final SupportAssitant supportAgent;

    public SupportAgent(ChatModel chatModel, CustomerTools customerTools, OrderTools orderTools, SupportTools supportTools) {
        this.supportAgent = AiServices.builder(SupportAssitant.class)
                .chatModel(chatModel)
                .tools(customerTools, orderTools, supportTools)
                .build();
    }

    public String chat(String message){
        return supportAgent.chat(message);
    }
}
