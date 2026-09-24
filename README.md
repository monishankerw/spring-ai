# Spring AI: Complete Guide

## 1. What is Spring AI?

Spring AI is a Spring project that gives Java developers a consistent, portable abstraction for building AI applications. It applies Spring principles (dependency injection, auto-configuration, portable APIs) to generative AI, so you can swap providers without rewriting your application code.

**Core goals:**
- Provider portability (OpenAI, Anthropic, Azure OpenAI, Google Vertex AI / Gemini, Amazon Bedrock, Ollama, Mistral, and more)
- Spring Boot auto-configuration and starters
- Structured output mapped to POJOs
- Built-in support for RAG, tool calling, memory, and observability

---

## 2. Core Concepts

| Concept | Meaning |
|---|---|
| **Model** | The AI model (chat, embedding, image, audio, moderation) |
| **Prompt** | Input sent to the model (messages + options) |
| **Message** | System, User, Assistant, or Tool message |
| **Embedding** | Numeric vector representing text meaning |
| **Vector Store** | Database storing embeddings for similarity search |
| **RAG** | Retrieval-Augmented Generation |
| **Tool / Function Calling** | Model calls your Java methods |
| **Advisor** | Interceptor that modifies requests/responses |
| **Chat Memory** | Stores conversation history |

---

## 3. Getting Started

### Maven dependency (example: OpenAI)

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>${spring-ai.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

> Starter artifact names have changed across versions (older: `spring-ai-openai-spring-boot-starter`). Check the current docs for the version you use.

### application.properties

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.chat.options.temperature=0.7
```

Never hard-code API keys; use environment variables or a secrets manager.

---

## 4. ChatClient (Main API)

`ChatClient` is a fluent API for talking to models.

```java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("You are a helpful assistant.")
            .build();
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

### Streaming

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .stream()
        .content();
}
```

### Prompt templates

```java
String answer = chatClient.prompt()
    .user(u -> u.text("Explain {topic} in simple words")
                .param("topic", "microservices"))
    .call()
    .content();
```

---

## 5. Structured Output

Convert model responses directly into Java objects.

```java
record MovieInfo(String title, int year, List<String> actors) {}

MovieInfo movie = chatClient.prompt()
    .user("Give me details about the movie Inception")
    .call()
    .entity(MovieInfo.class);

// Lists / generics
List<MovieInfo> movies = chatClient.prompt()
    .user("List 3 sci-fi movies")
    .call()
    .entity(new ParameterizedTypeReference<List<MovieInfo>>() {});
```

Under the hood, Spring AI uses a `BeanOutputConverter` that adds JSON schema instructions to the prompt and parses the result.

---

## 6. Embeddings

```java
@Autowired EmbeddingModel embeddingModel;

float[] vector = embeddingModel.embed("Spring AI is great");
```

Embeddings power semantic search: similar meanings produce vectors that are close together (measured by cosine similarity or dot product).

---

## 7. Vector Stores

Spring AI's `VectorStore` interface has implementations for:

- PostgreSQL **pgvector**
- Redis, MongoDB Atlas, Elasticsearch, OpenSearch
- Pinecone, Weaviate, Qdrant, Milvus, Chroma
- Neo4j, Azure AI Search, Cassandra
- `SimpleVectorStore` (in-memory, good for demos)

```java
@Autowired VectorStore vectorStore;

// Add documents
vectorStore.add(List.of(
    new Document("Spring AI supports RAG", Map.of("source", "docs")),
    new Document("Java 21 introduced virtual threads")
));

// Search
List<Document> results = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("What supports RAG?")
        .topK(3)
        .similarityThreshold(0.7)
        .build()
);
```

---

## 8. RAG (Retrieval-Augmented Generation)

**Why RAG?** LLMs don't know your private or recent data. RAG retrieves relevant chunks and gives them to the model as context, reducing hallucination.

### Pipeline

1. **ETL / Ingestion:** Read → Split → Embed → Store
2. **Query time:** Embed question → Retrieve top-K → Augment prompt → Generate

### Ingestion

```java
// Read
TikaDocumentReader reader = new TikaDocumentReader(resource); // PDF, DOCX, etc.
List<Document> docs = reader.get();

// Split into chunks
TokenTextSplitter splitter = new TokenTextSplitter();
List<Document> chunks = splitter.apply(docs);

// Embed + store
vectorStore.add(chunks);
```

### Query with QuestionAnswerAdvisor

```java
ChatClient client = builder
    .defaultAdvisors(
        QuestionAnswerAdvisor.builder(vectorStore).build()
    )
    .build();

String answer = client.prompt()
    .user("What is our refund policy?")
    .call()
    .content();
```

Newer versions also offer a modular RAG architecture (`RetrievalAugmentationAdvisor`) with query transformers, retrievers, and document joiners for more control.

---

## 9. Advisors

Advisors intercept and enrich requests and responses, similar to servlet filters or AOP.

Built-in examples:
- `QuestionAnswerAdvisor` (RAG)
- `MessageChatMemoryAdvisor` (memory)
- `SimpleLoggerAdvisor` (logging)
- Safeguard/guardrail advisors

```java
ChatClient client = builder
    .defaultAdvisors(new SimpleLoggerAdvisor())
    .build();
```

You can write custom advisors for caching, PII masking, rate limiting, or auditing.

---

## 10. Chat Memory

LLMs are stateless; memory re-sends prior context.

```java
ChatMemory memory = MessageWindowChatMemory.builder()
    .maxMessages(20)
    .build();

ChatClient client = builder
    .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
    .build();

client.prompt()
    .user("My name is Amit")
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "user-1"))
    .call().content();
```

Storage options: in-memory, JDBC, Cassandra, Neo4j. Use a conversation ID per user or session.

---

## 11. Tool (Function) Calling

The model decides when to call your Java code, for example to fetch live data or perform actions.

```java
public class WeatherTools {

    @Tool(description = "Get current temperature for a city")
    public String getTemperature(
            @ToolParam(description = "City name") String city) {
        return "30°C in " + city; // call real API here
    }
}

String reply = chatClient.prompt()
    .user("What's the weather in Delhi?")
    .tools(new WeatherTools())
    .call()
    .content();
```

**Flow:** User asks → model requests tool call → Spring executes method → result returned to model → final answer.

**Security note:** Treat tools as an attack surface. Validate arguments, apply authorization, and require confirmation for destructive actions.

---

## 12. Model Context Protocol (MCP)

Spring AI supports MCP, an open protocol for connecting models to external tools and data.

- **MCP Client:** your app consumes tools from MCP servers
- **MCP Server:** expose your tools and resources to other AI apps
- Supports stdio, SSE, and streamable HTTP transports

---

## 13. Other Model Types

| Capability | Interface |
|---|---|
| Image generation | `ImageModel` |
| Text-to-speech | `SpeechModel` |
| Transcription | `TranscriptionModel` |
| Moderation | `ModerationModel` |
| Multimodal input | Media in `UserMessage` (images, etc.) |

---

## 14. Observability and Evaluation

- **Micrometer** metrics and tracing for model calls, token usage, latency
- Works with Prometheus, Grafana, Zipkin, and OpenTelemetry
- **Evaluators** (e.g., `RelevancyEvaluator`, `FactCheckingEvaluator`) to test response quality (LLM-as-judge)

---

## 15. Local Models with Ollama

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3
```

Good for privacy, offline development, and zero API cost.

---

## 16. Architecture Overview

```
Client → Controller → ChatClient
                         │
              ┌──────────┼───────────┐
           Advisors     Tools     Memory
              │
        Vector Store (RAG)
              │
        ChatModel (OpenAI / Anthropic / Ollama ...)
```

---

## 17. Best Practices

- **Keep secrets out of code**; use env vars or Vault.
- **Chunk documents sensibly** (size and overlap affect retrieval quality).
- **Set token limits and timeouts**; add retries and rate limiting.
- **Validate structured output** since models can return malformed data.
- **Guard against prompt injection**, especially with RAG and tools.
- **Log and monitor cost**: track token usage.
- **Test with evaluators** rather than eyeballing outputs.
- **Pin the Spring AI version**; the API evolved quickly (e.g., `FunctionCallback` → `@Tool`, `SearchRequest.defaults()` → builder).

---

## 18. Common Use Cases

- Chatbots and support assistants
- Document Q&A (RAG) over PDFs and wikis
- Text summarization and classification
- Data extraction into POJOs
- AI agents that call internal APIs
- Semantic search and recommendations

---

## 19. Quick Interview Points

1. **What is Spring AI?** A Spring abstraction for integrating LLMs with portable APIs.
2. **ChatClient vs ChatModel?** ChatModel is the low-level interface; ChatClient is the fluent, higher-level API.
3. **What is RAG?** Retrieval of relevant documents to ground model answers.
4. **What is an Advisor?** An interceptor pattern for prompts and responses.
5. **How to get typed output?** `.entity(MyClass.class)`.
6. **How does tool calling work?** The model requests a function call, and Spring executes it and returns the result.

---

## 20. Learning Path

1. Simple `ChatClient` endpoint
2. Prompt templates and structured output
3. Streaming responses
4. Embeddings and a vector store (pgvector)
5. Full RAG app over your own documents
6. Chat memory and tools
7. MCP, observability, and evaluation

---

Want me to go deeper on any part, such as a full RAG project with pgvector, tool calling, or a step-by-step setup with Ollama? I can also put this into a PDF or Word file for study notes.