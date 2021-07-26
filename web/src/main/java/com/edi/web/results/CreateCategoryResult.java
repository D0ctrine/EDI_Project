package com.edi.web.results;

import com.edi.domain.model.category.Category;

import org.springframework.http.ResponseEntity;

public class CreateCategoryResult {
  public static ResponseEntity<ApiResult> build(Category category) {
    ApiResult apiResult = ApiResult.blank()
        .add("id", category.getId())
        .add("file_type", category.getFile_type())
        .add("name", category.getName())
        .add("depth", category.getDepth())
        .add("parent", category.getParent());

    return Result.ok(apiResult);
  }
}
