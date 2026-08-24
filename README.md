# My First Agentic System with LangChain4j

My first hands-on attempt at building an **agentic system using Java, Spring Boot, LangChain4j, and Ollama**.

The goal is to move from understanding agentic concepts theoretically to actually implementing them.

Currently, this is a **single-agent system**. The customer-support domain is only used as a simple environment to experiment with agent architecture.

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
Single Agent
 |
 v
Local LLM (Ollama)
 |
 +------------+-------------+------------------+
 |            |             |                  |
 v            v             v                  v
getCustomer  lookupOrder  processRefund  escalateToHuman
 |            |             |                  |
 +------------+-------------+------------------+
                         |
                         v
                    Tool Result
                         |
                         v
                        LLM
                         |
                    Continue / Finish
