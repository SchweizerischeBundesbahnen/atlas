package ch.sbb.atlas.base.service.versioning.model;

import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ToVersioning {

  private LocalDate validFrom;

  private LocalDate validTo;

  private Versionable versionable;

  private Entity entity;

  public ToVersioning(Versionable versionable, Entity entity) {
    if (versionable == null) {
      throw new VersioningException("Versionable object is null.");
    }
    this.versionable = versionable;
    this.entity = entity;
    this.validFrom = versionable.getValidFrom();
    this.validTo = versionable.getValidTo();
  }

  public static class ToVersioningBuilder {

    public ToVersioning build() {
      return new ToVersioning(this.versionable, this.entity);
    }
  }
}
