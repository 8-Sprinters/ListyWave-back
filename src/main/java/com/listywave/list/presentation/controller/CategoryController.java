package com.listywave.list.presentation.controller;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.dto.response.CategoryTypeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryTypeResponse>> getCategory() {
        return ResponseEntity.ok()
                .body(
                        Arrays.stream(CategoryType.values())
                                .map(CategoryTypeResponse::fromEnum)
                                .collect(Collectors.toList())
                );
    }
}
