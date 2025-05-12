package com.taild.simpleshopapp.services;

import java.util.List;
import com.taild.simpleshopapp.models.Category;
import com.taild.simpleshopapp.dtos.CategoryDTO;


public interface ICategoryService {

    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO category);
    Category deleteCategory(long id) throws Exception;

}
