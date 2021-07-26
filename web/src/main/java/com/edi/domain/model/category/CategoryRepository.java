package com.edi.domain.model.category;

import java.util.List;

public interface CategoryRepository {
  /**
   * 카테고리 리스트
   */
  List<Category> getList();

  /**
   * 카테고리 생성
   * 엔티티를 통해 생성한다.
   */
  void save(Category cg);

  /**
   * 카테고리 검색
   * ID(PK)를 통한 카테고리에 정보를 엔티티로 반환한다.
   */
  Category findById(String id);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  Category delete(Category cg);

  /**
   * 카테고리 갱신
   */
  Category update(Category cg);

}
