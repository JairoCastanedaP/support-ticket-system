package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.service.RequesterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requesters")
public class RequesterController {

    private final RequesterService requesterService;

    public RequesterController(RequesterService requesterService) {
        this.requesterService = requesterService;
    }

    @GetMapping
    public ResponseEntity<List<RequesterResponseDto>> findAll() {
        return ResponseEntity.ok(requesterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequesterResponseDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(requesterService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RequesterResponseDto> save(
            @Valid @RequestBody RequesterRequestDto requesterRequestDto
    ) {
        RequesterResponseDto savedRequester =
                requesterService.save(requesterRequestDto);

        URI location = URI.create(
                "/api/v1/requesters/" + savedRequester.id()
        );

        return ResponseEntity.created(location).body(savedRequester);
    }
}