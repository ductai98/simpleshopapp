package com.taild.simpleshopapp.controller;


import com.taild.simpleshopapp.models.Category;
import com.taild.simpleshopapp.dtos.CategoryDTO;
import com.taild.simpleshopapp.dtos.ResponseDTO;
import com.taild.simpleshopapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final CategoryService categoryService;

    //Hiện tất cả các categories
    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAllCategories(
            @RequestParam("page")     int page,
            @RequestParam("limit")    int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK)
                .data(categories)
                .build());
    }


    @PostMapping("")
    public ResponseEntity<ResponseDTO> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {

        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseDTO.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());

        }

        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Create category successfully")
                .status(HttpStatus.OK)
                .data(category)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(HttpStatus.OK)
                .data(categoryService.getCategoryById(id))
                .message("Update category successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable Long id) throws Exception {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .message("Delete category successfully")
                        .build());
    }
}
