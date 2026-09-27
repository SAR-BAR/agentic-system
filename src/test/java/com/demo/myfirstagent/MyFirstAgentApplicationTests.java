package com.demo.myfirstagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "agent.demo.enabled=false")
class MyFirstAgentApplicationTests {

    @Test
    void contextLoads() {
    }

}
