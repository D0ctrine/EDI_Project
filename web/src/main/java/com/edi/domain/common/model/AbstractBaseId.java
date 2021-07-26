package com.edi.domain.common.model;

import java.io.Serializable;

public abstract class AbstractBaseId implements Serializable {

  private static final long serialVersionUID = 3435210296634626689L;

  private Long id;

  public AbstractBaseId(Long id) {
    this.id = id;
  }

  public Long value() {
    return id;
  }

  public boolean isValid() {
    return id > 0;
  }
}
