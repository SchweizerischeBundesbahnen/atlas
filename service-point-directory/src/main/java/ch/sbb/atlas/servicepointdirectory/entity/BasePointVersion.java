package ch.sbb.atlas.servicepointdirectory.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public abstract class BasePointVersion extends BaseDidokImportEntity {

  public void setThisAsParentOnRelatingEntities() {
    // default no operation when no relating Entities exist
  }

}
