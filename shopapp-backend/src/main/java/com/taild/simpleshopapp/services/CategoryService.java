package com.taild.simpleshopapp.services;


import com.taild.simpleshopapp.models.Category;
import com.taild.simpleshopapp.dtos.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    @Override
    public Category createCategory(CategoryDTO category) {
        return null;
    }

    @Override
    public Category getCategoryById(long id) {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDTO category) {
        return null;
    }

    @Override
    public Category deleteCategory(long id) throws Exception {
        return null;
    }
}
