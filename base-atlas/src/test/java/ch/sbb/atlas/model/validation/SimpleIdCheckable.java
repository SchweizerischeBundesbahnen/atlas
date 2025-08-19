package ch.sbb.atlas.model.validation;

import ch.sbb.atlas.model.IdCheckable;

public class SimpleIdCheckable implements IdCheckable {

  private final Long id;

  public SimpleIdCheckable(Long id) {
    this.id = id;
  }

  @Override
  public Long getId() {
    return id;
  }
}
