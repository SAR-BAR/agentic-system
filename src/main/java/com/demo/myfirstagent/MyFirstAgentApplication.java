package com.demo.myfirstagent;

import com.demo.myfirstagent.agent.SupportAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyFirstAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFirstAgentApplication.class, args);
    }

    @Bean
    CommandLineRunner tstAgent(SupportAgent agent) {
        return args -> {

            String[] queries = {
                    "I am Alice. My customer ID is C001. Please look up my order O001.",
                    "Please refund order O002 for me.",
                    "Please verify customer C003 and then look up order O004.",
                    "I want a refund for order O007. The order amount is $99.00.",
                    "Please refund order O006 for $50.00.",
                    "Look up order O9999 for me.",
                    "Please look up my order O003 and refund it for the exact order amount.",
                    "Please verify customer C005 and look up order O001.",
                    "I want to speak to a human agent about my order O008.",
                    "Please refund order O010 for $99.00. If you cannot do it, escalate this to a human."
            };

            for (String query : queries) {
                System.out.println("\n\n========================================");
                System.out.println("USER QUERY:");
                System.out.println(query);
                System.out.println("========================================");
                String response = agent.chat(query);
                System.out.println("\nMODEL RESPONSE:");
                System.out.println(response);
            }
        };
    }
}
