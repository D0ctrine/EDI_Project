package com.edi.web.apis;

import java.util.List;

import com.edi.domain.application.CategoryService;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.category.Category;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.category.CreateCategoryPayload;
import com.edi.web.payload.category.UpdateCategoryPayload;
import com.edi.web.results.ApiResult;
import com.edi.web.results.CreateCategoryResult;
import com.edi.web.results.Result;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CategoryApiController {
  private CategoryService categoryService;

  public CategoryApiController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/api/categories")
  public ResponseEntity<ApiResult> getCGList() {
    List<Category> cgs = categoryService.getList();
    for(int i=0; i<cgs.size(); i++){
      System.out.println(cgs.get(i).toString());
    }
    ApiResult apiResult = ApiResult.blank()
      .add("categories", cgs);

    return Result.ok(apiResult);
  }

  //Create Category / need : file_type
  @PostMapping("/api/categories/create")
  public ResponseEntity<ApiResult> CreateCateGory(@RequestBody CreateCategoryPayload payload,
                                                  @CurrentUser SimpleUser currentUser) {
    System.out.println(payload);
    System.out.println(currentUser.toString());
    Category category = categoryService.createCategory(payload.toCommand(currentUser.getUserId()));
    return CreateCategoryResult.build(category);
  }

  //Update Category
  @PostMapping("/api/categories/update")
  public ResponseEntity<ApiResult> UpdateCateGory(@RequestBody UpdateCategoryPayload payload,
                                                  @CurrentUser SimpleUser currentUser) {
    System.out.println(currentUser.toString());
    System.out.println(payload.toString());
    Category category = categoryService.update(payload.toCommand(currentUser.getUserId()));

    return Result.ok(category.toString());
  }

  //Delete Category
  @PostMapping("/api/categories/delete")
  public ResponseEntity<ApiResult> DeleteCateGory(@RequestBody UpdateCategoryPayload payload,
                                                  @CurrentUser SimpleUser currentUser) {
    System.out.println(payload.toString());
    System.out.println(currentUser.toString());
    Category category = categoryService.delete(payload.toCommand(currentUser.getUserId()));
    return Result.ok(category.toString());
  }

}
