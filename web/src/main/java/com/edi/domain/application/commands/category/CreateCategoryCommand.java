package com.edi.domain.application.commands.category;

import com.edi.domain.model.user.UserId;

/**
 * CreateCategoryCommand
 */
public class CreateCategoryCommand {

  private UserId userId;
  private String name;
  private String filetype;
  private String depth;
  private String parent;

  public CreateCategoryCommand(UserId userId,String name ,String filetype, String depth, String parent) {
    this.userId = userId;
    this.filetype = filetype;
    this.name = name;
    this.depth = depth;
    this.parent = parent;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getFiletype() {
    return filetype;
  }

  public String getDepth() {
    return depth;
  }

  public String getParent() {
    return parent;
  }

  @Override
  public String toString() {
    return "CreateCategoryCommand [depth=" + depth + ", filetype=" + filetype + ", name=" + name + ", parent=" + parent
        + ", userId=" + userId + "]";
  }
}
