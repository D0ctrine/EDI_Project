package com.edi.web.payload.category;

import com.edi.domain.application.commands.category.CreateCategoryCommand;
import com.edi.domain.model.user.UserId;

public class CreateCategoryPayload {
  private String depth;
  private String name;
  private String parent;
  private String file_type;

  public CreateCategoryCommand toCommand(UserId userId) {
    return new CreateCategoryCommand(userId, name, file_type, depth, parent);
  }

  public void setDepth(String depth) {
    this.depth = depth;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public void setFile_type(String file_type) {
    this.file_type = file_type;
  }

}
