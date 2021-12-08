package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.category.CreateCategoryCommand;
import com.edi.domain.application.commands.category.UpdateCategoryCommand;
import com.edi.domain.model.category.Category;
import com.edi.domain.model.user.UserId;

public interface CategoryService {
  /**
   * Get List from category table
   *
   * getList the List from Category
   * @return an instance of <code>Category</code> if found, null otherwise
   */
  List<Category> getList();

  /**
   * Save a new user or an existing user
   *
   * @param user the user instance to be saved
   */
  Category createCategory(CreateCategoryCommand cg);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  Category delete(UpdateCategoryCommand cg);

  /**
   * 카테고리 갱신
   */
  Category update(UpdateCategoryCommand cg);

  /**
   * Get category
   * @return : Category
   */
  Category copyOne(String categoryId,UserId userid);
}
