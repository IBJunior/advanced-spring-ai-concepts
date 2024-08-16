package com.flyintelligent.advancedconcepts.chats;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RestController
@RequestMapping("/api/chat-memory")
public class MemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public MemoryController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .build();
        this.chatMemory = chatMemory;
    }

    @GetMapping
    public String chatWithNoMemory(@RequestParam(defaultValue = "Bonjour je m'appelle John Doe") String message) {
        return this.chatClient
                .prompt()
                .user(message)
                .call().content();

    }

    @GetMapping("/{userId}")
    public String chatWithMemory(@PathVariable String userId,
                                 @RequestParam(defaultValue = "Bonjour je m'appelle John Doe") String message) {

        return this.chatClient
                .prompt()
                .user(message)
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .advisors(a -> {
                    a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                })
                .call().content();
    }
}
