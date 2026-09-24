package com.springAi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OllamaController {

    private final ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}
/*
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINdm+SLuLLC9/1fkCeM3PyZL1eu6KeB0u8Xsn0EeoYnq
 */