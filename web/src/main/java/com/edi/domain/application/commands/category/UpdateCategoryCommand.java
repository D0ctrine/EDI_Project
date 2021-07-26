package com.edi.domain.application.commands.category;

import com.edi.domain.model.user.UserId;

/**
 * CreateCategoryCommand
 */
public class UpdateCategoryCommand {

  private UserId userId;
  private String newName;
  private Long id;

  public UpdateCategoryCommand(UserId userId,Long id ,String newName) {
    this.userId = userId;
    this.newName = newName;
    this.id = id;
    toString();
  }

  public UserId getUserId() {
    return userId;
  }

  public String getNewName() {
    return newName;
  }

  public Long getId() {
    return id;
  }

  @Override
  public String toString() {
    return "UpdateCategoryCommand [id=" + id + ", newName=" + newName + ", userId=" + userId + "]";
  }

}
