package com.springAi.controller;

import com.springAi.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/charts")
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/{message}")
    public ResponseEntity<String> getChart(@PathVariable String message) {
        return ResponseEntity.ok(chartService.getChart(message));
    }
}