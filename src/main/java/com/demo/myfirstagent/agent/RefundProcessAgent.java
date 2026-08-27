package com.demo.myfirstagent.agent;

import com.demo.myfirstagent.tool.OrderTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

@Component
public class RefundProcessAgent {
    private final RefundProcessAssistant refundProcessAgent;

    public RefundProcessAgent(ChatModel chatModel, OrderTools orderTools) {
        this.refundProcessAgent = AiServices.builder(RefundProcessAssistant.class)
                .chatModel(chatModel)
                .tools(orderTools)
                .build();
    }

    public String process(String message){
        return refundProcessAgent.process(message);
    }
}
