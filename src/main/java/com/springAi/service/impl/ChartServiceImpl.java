package com.springAi.service.impl;

import com.springAi.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChartServiceImpl implements ChartService {
    private final OpenAiChatModel openAiChatModel;
    @Override
    public String getChart(String message) {
        return openAiChatModel.call(message);
    }
}