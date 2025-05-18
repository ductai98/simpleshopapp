package com.taild.simpleshopapp.controllers;


import com.taild.simpleshopapp.models.Category;
import com.taild.simpleshopapp.dtos.CategoryDTO;
import com.taild.simpleshopapp.dtos.Response;
import com.taild.simpleshopapp.services.categories.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    //Hiện tất cả các categories
    @GetMapping("")
    public ResponseEntity<Response> getAllCategories(
            @RequestParam(value = "page", defaultValue = "1")     int page,
            @RequestParam(value = "limit", defaultValue = "10")    int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(Response.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK)
                .data(categories)
                .build());
    }


    @PostMapping("")
    public ResponseEntity<Response> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {

        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(Response.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());

        }

        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(Response.builder()
                .message("Create category successfully")
                .status(HttpStatus.OK)
                .data(category)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(Response.builder()
                .status(HttpStatus.OK)
                .data(categoryService.getCategoryById(id))
                .message("Update category successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCategory(@PathVariable Long id) throws Exception {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                Response.builder()
                        .status(HttpStatus.OK)
                        .message("Delete category successfully")
                        .build());
    }
}
