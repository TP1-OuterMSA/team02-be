package com.example.community_cr.diet.controller;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.service.DietService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet")
public class DietController {

    private final DietService dietService;

    @PostMapping
    public ResponseEntity<String> createDiet(@RequestBody DietRequest dto) {
        dietService.saveDiet(dto);
        return ResponseEntity.ok("식단 저장 완료");
    }


}
