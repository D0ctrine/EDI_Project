package com.edi.domain.application.impl;

import com.edi.domain.application.CategoryService;
import com.edi.domain.application.commands.category.CreateCategoryCommand;
import com.edi.domain.application.commands.category.UpdateCategoryCommand;
import com.edi.domain.model.category.Category;
import com.edi.domain.model.category.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

  private CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<Category> getList() {
    return categoryRepository.getList();
  }

  @Override
  public Category createCategory(CreateCategoryCommand command) {
    Category category = Category.create(command.getUserId(),command.getName() , command.getFiletype(),command.getDepth() , command.getParent());
    categoryRepository.save(category);

    return category;
  }

  @Override
  public Category delete(UpdateCategoryCommand command) {
    Category category = Category.update(command.getUserId(),command.getId() , command.getNewName(), "delete");
    System.out.println("-----------------DeleteCategoryCommand");
    System.out.println(category.toString());
    return categoryRepository.delete(category);
  }

  @Override
  public Category update(UpdateCategoryCommand command) {
    // TODO Auto-generated method stub
    System.out.println("-----------------UpdateCategoryCommand");
    Category category = Category.update(command.getUserId(),command.getId() , command.getNewName(), "update");
    System.out.println(category.toString());
    return categoryRepository.update(category);
  }

}
