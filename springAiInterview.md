# Spring AI Interview Questions (0 to 7+ Years)

## Level 1: 0-1 Year (Fresher / Basics)

1. What is Spring AI and why is it needed?
2. What is an LLM? What is a prompt?
   3. Difference between `ChatModel` and `ChatClient`?
4. What are system, user, and assistant messages?
5. What is temperature? What does it change?
6. What is a token? Why does it matter for cost?
7. How do you configure an OpenAI API key in Spring Boot?
8. What is the Spring AI BOM and starter dependency?
9. What is an embedding? (simple explanation)
10. What is hallucination in LLMs?
11. Write a simple REST endpoint that calls `ChatClient`.
12. Why should API keys never be hard-coded?



# Spring AI: Easy Answers to Remember

## 1. What is Spring AI and why is it needed?

**Answer:** Spring AI is a Spring project that helps Java developers connect their apps to AI models (like OpenAI, Anthropic, Ollama) using one common API.

**Why needed:** Without it, you write different code for every AI provider. With it, you change only the config and the same code works.

**Remember:** *"One API, many AI models."*

---

## 3. Difference between `ChatModel` and `ChatClient`?

| | ChatModel | ChatClient |
|---|---|---|
| Level | Low-level | High-level |
| Style | Basic call with a `Prompt` | Fluent builder style |
| Extras | Manual work | Advisors, memory, tools, structured output built in |

**Answer:** `ChatModel` is the basic interface that talks to the model. `ChatClient` is built on top of it and gives an easy fluent API.

**Remember:** *"ChatModel = engine, ChatClient = steering wheel."*

---

## 4. System, User, and Assistant messages?

- **System message:** sets the AI's role and rules. Example: "You are a helpful Java tutor."
- **User message:** the question from the person. Example: "Explain Spring Boot."
- **Assistant message:** the reply from the AI.

**Remember:** *"System = rules, User = question, Assistant = answer."*

---

## 6. What is a token? Why does it matter for cost?

**Answer:** A token is a small piece of text (a word or part of a word). Roughly, 1 token is about 4 characters or ¾ of a word in English.

**Cost:** AI providers charge by tokens, both input (your prompt) and output (the reply). More tokens means higher cost and slower response. Models also have a token limit (context window).

**Remember:** *"More tokens = more money."*

---

## 8. What is the Spring AI BOM and starter dependency?

- **BOM (Bill of Materials):** manages the versions of all Spring AI libraries in one place, so versions never conflict.
- **Starter dependency:** one dependency that adds the provider library plus auto-configuration. Example: `spring-ai-starter-model-openai`.

**Remember:** *"BOM = version manager, Starter = ready-made setup."*

---

## 11. Simple REST endpoint using `ChatClient`

```java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
```

**Remember the flow:** `prompt()` → `user()` → `call()` → `content()`

---

## 12. Why should API keys never be hard-coded?

**Answer:**
- Code goes to Git, so anyone can see and steal the key.
- Stolen keys can run up a big bill or leak data.
- Hard to change or rotate the key later.

**Fix:** use environment variables (`${OPENAI_API_KEY}`), Vault, or a secrets manager.

**Remember:** *"Keys in config, never in code."*

---

## One-Line Cheat Sheet

| # | Question | One-line answer |
|---|---|---|
| 1 | Spring AI | One API for many AI models |
| 3 | ChatModel vs ChatClient | Engine vs steering wheel |
| 4 | Messages | System = rules, User = question, Assistant = answer |
| 6 | Token | Small text piece; you pay per token |
| 8 | BOM / Starter | Version manager / ready-made setup |
| 11 | REST endpoint | `prompt().user().call().content()` |
| 12 | API key | Never in code; use env variables |

Want the same easy answers for the next levels (1-3 years)?
---

## Level 2: 1-3 Years (Working Knowledge)

1. How do you use prompt templates with parameters?
2. How do you get structured output as a POJO? (`.entity()`, `BeanOutputConverter`)
3. How do you stream responses (`Flux<String>`, SSE)?
4. What is a `VectorStore`? Name a few implementations.
5. What is cosine similarity?
6. What is RAG and why use it instead of fine-tuning?
7. Explain the ingestion pipeline: read, split, embed, store.
8. What is chunking? What are chunk size and overlap?
9. How do you switch from OpenAI to Ollama or Anthropic with minimal code change?
10. What is `Document` in Spring AI? What is metadata used for?
11. How do you handle API errors, timeouts, and rate limits (429)?
12. How do you run a local model with Ollama?

---

## Level 3: 3-5 Years (Intermediate)

1. What is an Advisor? How does it compare to a servlet filter or AOP?
2. Explain `QuestionAnswerAdvisor` and how it builds the augmented prompt.
3. How does chat memory work? Why are LLMs stateless?
4. `MessageWindowChatMemory` vs JDBC-backed memory: when to use which?
5. How does tool (function) calling work end to end?
6. What do `@Tool` and `@ToolParam` do?
7. How do you filter vector search by metadata (for example, per tenant or user)?
8. `topK` vs `similarityThreshold`: how do they affect results?
9. How do you use pgvector with Spring AI? Explain the index types (HNSW vs IVFFlat).
10. How do you handle multimodal input (images, PDFs)?
11. How do you write a custom advisor (logging, PII masking, caching)?
12. How do you test AI code, given non-deterministic outputs?
13. How do you track token usage and cost?

---

## Level 4: 5-7 Years (Advanced / Senior)

1. Design a document Q&A system for 10,000 PDFs. Cover ingestion, storage, retrieval, and updates.
2. Naive RAG vs modular RAG (`RetrievalAugmentationAdvisor`): query transformers, expanders, retrievers, joiners?
3. How do you improve RAG quality? (hybrid search, re-ranking, query rewriting, better chunking)
4. What is prompt injection? How does it affect RAG and tools? How do you mitigate it?
5. How do you secure tool calls (authorization, argument validation, human confirmation for destructive actions)?
6. How do you make multi-tenant RAG safe so one user never sees another's data?
7. How do you implement observability (Micrometer, OpenTelemetry, traces per LLM call)?
8. How do you evaluate answer quality? (`RelevancyEvaluator`, `FactCheckingEvaluator`, LLM-as-judge)
9. Sync `.call()` vs reactive `.stream()`: threading, backpressure, and virtual threads?
10. How do you reduce latency and cost? (caching, smaller models, prompt trimming, semantic cache)
11. What is MCP (Model Context Protocol)? How do you build an MCP server and client in Spring AI?
12. How do you handle failover between model providers (retry, circuit breaker with Resilience4j)?
13. How do you version and manage prompts across environments?

---

## Level 5: 7+ Years (Architect / Lead)

1. Design an enterprise AI platform on Spring AI for many teams. Cover gateway, model routing, quotas, and governance.
2. Build vs buy: Spring AI vs LangChain4j vs a direct provider SDK. How do you decide?
3. How do you design an AI agent that calls internal microservices safely? (tool boundaries, idempotency, audit logs)
4. How do you handle data privacy and compliance (PII, GDPR)? When do you choose local models over cloud APIs?
5. Explain your strategy for guardrails: input validation, output validation, moderation, and policy enforcement.
6. How do you scale ingestion and vector search? (batching, async pipelines with Kafka, sharding, index tuning)
7. How do you migrate embeddings when you change the embedding model? (re-index strategy, dual-write, versioning)
8. How do you build CI/CD, regression testing, and evaluation gates for prompts and RAG pipelines?
9. Describe failure modes in production: hallucination, timeouts, cost spikes, model deprecation. How do you design for them?
10. How do you design conversation memory at scale (long-term vs short-term, summarization, storage choice)?
11. Multi-agent vs single-agent workflows: trade-offs, orchestration, and loop/cost control?
12. How do you keep the codebase portable and the Spring AI version upgrade-safe, given fast API changes?
13. How do you explain AI risks and limits to business stakeholders?

---

## Full Stack Specific Questions (Java + Frontend)

1. How do you stream LLM tokens to a React or Angular UI? (SSE vs WebSocket, `EventSource`, `fetch` with `ReadableStream`)
2. How do you render Markdown and code blocks from streamed responses?
3. How do you handle cancel/stop generation, retry, and loading states in the UI?
4. How do you design a chat API? (conversation ID, history endpoint, pagination)
5. How do you upload files (PDF) from the UI to the backend and trigger ingestion asynchronously with progress status?
6. How do you secure the AI endpoint? (JWT/OAuth2, rate limiting per user, CORS)
7. Why should the frontend never call the LLM provider directly?
8. How do you show source citations from RAG in the UI?
9. How do you store chat history (PostgreSQL, MongoDB, Redis) and design the schema?
10. How do you deploy with Docker/Kubernetes, and how do you handle secrets and health checks?
11. How do you handle long-running responses behind proxies and load balancers (timeouts, sticky sessions, buffering)?

---

## Scenario / Coding Questions (Commonly Asked)

- Build a chatbot endpoint with memory per user.
- Extract invoice data from text into a Java record.
- Build a RAG API over uploaded PDFs with pgvector.
- Create a tool that fetches order status from a database and lets the model call it.
- Add a custom advisor that masks emails and phone numbers before the prompt is sent.
- The chatbot gives wrong answers from your documents: how do you debug it? (check chunks, retrieval scores, prompt, model)

---

## Quick Tips

- **0-3 years:** expect concepts plus a simple coding task.
- **3-5 years:** expect RAG, tools, memory, and advisors in depth.
- **5-7+ years:** expect system design, security, cost, observability, and trade-off discussion.
- Always mention **security, cost, testing, and observability**. Interviewers at senior level look for this.

Would you like model answers for any level, or a mock interview with follow-up questions? I can also put these into a PDF or Word file.