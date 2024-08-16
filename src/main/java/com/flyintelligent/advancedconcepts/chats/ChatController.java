package com.flyintelligent.advancedconcepts.chats;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    public static final String DEFAULT_SYSTEM_MESSAGE = """
            Tu es un assistant utile qui ne répond qu’aux questions sur la compagnie aérienne fly intelligent ou sur les voyages en général.
            Tu dois t'en tenir au contexte, si la réponse n’est pas présente dans ton contexte, tu dois répondre par "désolé je ne connais pas la réponse".
            De plus, si la réponse que tu obtiens du contexte fait plus de deux phrases, tu dois la résumer en deux phrases avant de donner la réponse à l’utilisateur.
            """;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/without-rag")
    public String chatWithoutRag(@RequestParam(defaultValue = "Qu'est-ce que fly intelligent ?")
                                 String quest) {
        return this.chatClient
                .prompt()
                .user(quest)
                .call().content();
    }

    @GetMapping("/with-rag")
    public String chatWithRag(@RequestParam(defaultValue = "Qu'est-ce que fly intelligent ?")
                              String quest) {
        return this.chatClient
                .prompt()
                .user(quest)
                .advisors(new QuestionAnswerAdvisor(this.vectorStore, SearchRequest.defaults()))
                .call().content();
    }

    @GetMapping("/with-rag-with-system-message")
    public String chatWithRagAndSystemMessage(@RequestParam(defaultValue = "Qu'est-ce que fly intelligent ?")
                                              String quest) {

        return this.chatClient
                .prompt()
                .user(quest)
                .system(DEFAULT_SYSTEM_MESSAGE)
                .advisors(new QuestionAnswerAdvisor(this.vectorStore, SearchRequest.defaults()))
                .call().content();
    }
}
