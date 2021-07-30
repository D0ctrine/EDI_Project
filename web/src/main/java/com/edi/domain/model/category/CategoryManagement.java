package com.edi.domain.model.category;

import com.edi.domain.model.user.UserId;
import com.edi.infrastructure.repository.CategoryRepository;

import org.springframework.stereotype.Component;

@Component
public class CategoryManagement {

  private CategoryRepository categoryRepository;

  public CategoryManagement(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

}
