# Agentic System

A Java-based multi-agent system built with Spring Boot, LangChain4j, and Ollama, exploring how LLM agents can be combined with deterministic application logic.

This project started as my first hands-on agent experiment and gradually evolved into a small multi-agent system with a lightweight agent harness.

> **Agents propose actions. The harness decides whether those actions are allowed. Tools execute permitted actions.**

---

# Architecture

```text
                         User
                           |
                           v
                    +-------------+
                    | Coordinator |
                    |    Agent    |
                    +------+------+
                           |
             +-------------+-------------+
             |                           |
             v                           v
      +-------------+             +-------------+
      |  Verifier   |             |   Refund    |
      |    Agent    |             |    Agent    |
      +------+------+             +------+------+
             |                           |
             v                           v
      Customer Tools              Order Tools
             |                           |
             +-------------+-------------+
                           |
                           v
                    Guards / State
                           |
                           v
                      Application
```

The coordinator delegates work to specialized agents.

The **Verifier Agent** handles customer verification, while the **Refund Agent** handles order and refund operations.

The agents interact with the application through tools, with deterministic checks sitting between agent decisions and important operations.

---

# Agents

### Coordinator Agent

Acts as the entry point for user requests and coordinates the specialized agents.

It maintains coordinator context and consumes structured results produced by other agents.

### Verifier Agent

Handles customer verification and produces structured `VerificationFindings` that can be consumed by the coordinator and downstream operations.

### Refund Agent

Handles order and refund operations while respecting verification, order ownership, and refund policies.

---

# Harness / Guardrails

A major part of the project is experimenting with **what should be handled by the LLM vs. what should remain deterministic application logic**.

| Component                 | What I implemented                                                                                                   |
| ------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| **Pre-tool Guards**       | Block unsafe tool calls before execution, such as refund operations without customer verification.                   |
| **Prerequisite Gates**    | Enforce required workflow steps, such as verification and order lookup, before a refund.                             |
| **Execution State**       | `AgentSession` tracks workflow facts outside the LLM, so model reasoning is not treated as proof of execution.       |
| **Policy Enforcement**    | Refunds above the `$500` agent limit are blocked and routed toward human escalation.                                 |
| **Tool-level Validation** | Tools validate critical conditions again before executing, providing a second enforcement boundary.                  |
| **Structured Outcomes**   | Tools return explicit `SUCCESS`, `BLOCKED`, or `ERROR` results, including reasons and next actions where applicable. |
| **Human Escalation**      | Operations outside the agent's authority are handed off instead of being decided by the LLM.                         |

### Core Principle

The project deliberately separates **reasoning from enforcement**.

> The LLM can decide *what it wants to do*.
> Deterministic application code decides *whether it is allowed to do it*.

---

# Concepts Implemented

### Agentic Systems

* LangChain4j AI Services
* Tool selection & calling
* Agentic loops
* Specialized agents
* Multi-agent coordination

### State & Coordination

* Execution state
* Coordinator context
* Structured inter-agent results
* Cross-agent state propagation
* Structured tool responses

---

# Tech Stack

| Technology      | Purpose                         |
| --------------- | ------------------------------- |
| **Java**        | Core application                |
| **Spring Boot** | Application framework           |
| **LangChain4j** | LLM / agent integration         |
| **Ollama**      | Local LLM runtime               |
| **Maven**       | Build and dependency management |

---

# Current Limitations

The current implementation is intentionally lightweight: agent state is not yet persistent or distributed, and observability/evaluation are still fairly basic. The harness also does not yet have production-oriented concerns such as execution budgets, retries, timeouts, or durable workflows.

---

I wanted to go beyond simply calling an LLM from a Spring Boot application and understand what is actually required to build a **controlled agentic system**.

The project is therefore evolving around a simple question:

> **How do we give an LLM enough autonomy to be useful without giving it unrestricted authority over the system?**

More experiments will be added as I explore agentic architectures, multi-agent systems, and harness engineering.
