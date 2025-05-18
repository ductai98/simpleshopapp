package com.taild.simpleshopapp.repositories;

import com.taild.simpleshopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
