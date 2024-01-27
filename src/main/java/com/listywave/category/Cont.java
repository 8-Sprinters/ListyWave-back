package com.listywave.category;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Cont {

    @GetMapping("/categories")
    ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(Category.values())
                .map(Category::getValue)
                .toList();

        return ResponseEntity.ok(categories);
    }

    // ArgumentResolver
    // ~~~~?category=moivafsd

    // category=movie
    @GetMapping("/users/{userId}/lists?category={}&type={}&sort={}")
    void getLists(
            @PathVariable Long userId,
            @RequestParam Category category
            // type
            // sort
    ) {

    }
}
