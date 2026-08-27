package com.demo.myfirstagent;


import com.demo.myfirstagent.coordinator.SupportCoordinator;
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
    CommandLineRunner tstAgent(SupportCoordinator agent) {
        return args -> {
            String response = agent.handleRequest("""
                    I am customer C001. Please refund my order O001.
                    """);
            System.out.println("FINAL RESPONSE ");
            System.out.println(response);
        };
    }
}
