package com.edi.infrastructure.repository;

import java.util.List;

import com.edi.domain.model.category.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  /**
   * 카테고리 리스트
   */
  List<Category> findByDeleteUserIsNull();
}
