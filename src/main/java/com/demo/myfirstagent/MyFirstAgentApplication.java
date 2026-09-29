package com.demo.myfirstagent;


import com.demo.myfirstagent.coordinator.SupportCoordinator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

@SpringBootApplication
public class MyFirstAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFirstAgentApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "agent.demo.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner tstAgent(SupportCoordinator agent) {
        return args -> {
            if (args.length > 0) {
                String request = String.join(" ", args);
                System.out.println("USER REQUEST: " + request);
                String response = agent.handleRequest(request);
                System.out.println("FINAL RESPONSE ");
                System.out.println(response);
                return;
            }

            System.out.println("Support chat started. Type 'quit' to exit.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            agent.runConversation(() -> {
                try {
                    while (true) {
                        System.out.print("> ");
                        String line = reader.readLine();
                        if (line == null) break;
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;
                        System.out.println("AGENT: " + agent.handleTurn(line));
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        };
    }
}
