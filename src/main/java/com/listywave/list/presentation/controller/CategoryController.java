package com.listywave.list.presentation.controller;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.presentation.dto.response.CategoryTypeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    @GetMapping
    public ResponseEntity<List<CategoryTypeResponse>> getCategory() {
        return ResponseEntity.ok()
                .body(
                        Arrays.stream(CategoryType.values())
                                .map(CategoryTypeResponse::fromEnum)
                                .collect(Collectors.toList())
                );
    }
}
