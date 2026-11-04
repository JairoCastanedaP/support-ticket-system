package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.service.SupportAgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/support-agents")
public class SupportAgentController {

    private final SupportAgentService supportAgentService;

    public SupportAgentController(SupportAgentService supportAgentService) {
        this.supportAgentService = supportAgentService;
    }

    @GetMapping
    public ResponseEntity<List<SupportAgentResponseDto>> findAll() {
        return ResponseEntity.ok(supportAgentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportAgentResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(supportAgentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SupportAgentResponseDto> save(
            @Valid @RequestBody SupportAgentRequestDto requestDto
    ) {
        SupportAgentResponseDto savedSupportAgent = supportAgentService.save(requestDto);
        URI location = URI.create("/api/v1/support-agents/" + savedSupportAgent.id());

        return ResponseEntity.created(location).body(savedSupportAgent);
    }
}