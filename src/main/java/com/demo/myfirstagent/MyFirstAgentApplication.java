package com.demo.myfirstagent;


import com.demo.myfirstagent.coordinator.SupportCoordinator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyFirstAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFirstAgentApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "agent.demo.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner tstAgent(SupportCoordinator agent) {
        return args -> {
            String request = args.length > 0
                    ? String.join(" ", args)
                    : "I am customer C001. Please refund my order O001.";
            System.out.println("USER REQUEST: " + request);
            String response = agent.handleRequest(request);
            System.out.println("FINAL RESPONSE ");
            System.out.println(response);
        };
    }
}
