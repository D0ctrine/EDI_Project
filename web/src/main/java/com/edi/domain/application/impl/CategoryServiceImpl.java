package com.edi.domain.application.impl;

import com.edi.domain.application.CategoryService;
import com.edi.domain.application.commands.category.CreateCategoryCommand;
import com.edi.domain.application.commands.category.UpdateCategoryCommand;
import com.edi.domain.model.category.Category;
import com.edi.domain.model.user.UserId;
import com.edi.infrastructure.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
  @Autowired
  private CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<Category> getList() {
    return categoryRepository.findByDeleteUserIsNull();
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
    categoryRepository.delete(category);
    return category;
  }

  @Override
  public Category update(UpdateCategoryCommand command) {
    System.out.println("-----------------UpdateCategoryCommand");
    Category category = categoryRepository.getOne(command.getId());
    Category updateCategory = Category.update(command.getUserId(),command.getId() , command.getNewName(), "update");
    category.setUpdate_date(updateCategory.getUpdate_date());
    category.setUpdate_user(updateCategory.getUpdate_user());
    category.setName(updateCategory.getName());
    return categoryRepository.save(category);
  }

  @Override
  public Category copyOne(String categoryId, UserId userid) {
    Optional<Category> category = categoryRepository.findById(Long.parseLong(categoryId));
    if(!category.isPresent()) throw new IllegalArgumentException();
    Category newCategory = Category.create(userid, category.get().getName()+"_COPY", category.get().getFile_type(), category.get().getDepth(), category.get().getParent());
    return categoryRepository.save(newCategory);
  }

}
