# My First Agentic System with LangChain4j

My first hands-on attempt at building an agentic system using Java, Spring Boot, LangChain4j, and Ollama.

## Tech Stack

- Java
- Spring Boot
- LangChain4j
- Ollama
- Maven
- Local LLM


## Concepts Implemented

- LangChain4j AI Services
- Tool calling
- Agentic loop
- Tool selection
- Agent execution state
- Pre-tool guards
- Prerequisite gates
- Customer verification
- Order/customer validation
- Refund policy enforcement
- Structured tool responses
- Human escalation

## Current Architecture

```text
User
 |
 v
Coordinator
 |
 +-------------------+
 |                   |
 v                   v
Verifier Agent    Refund Agent
 |                   |
 v                   v
Customer Tools     Order Tools
 |
 v
VerificationFindings
 |
 v
Coordinator Context
```

The project started as a single-agent system and is now being extended into a multi-agent system, one concept at a time.

The goal is not to build a production customer-support system, but to get hands-on with how agentic systems are actually built.

More things to come as I learn.

