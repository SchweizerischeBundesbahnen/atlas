package ch.sbb.atlas.versioning;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

public abstract class BaseTest {

  @Data
  @AllArgsConstructor
  @Builder
  @FieldNameConstants
  @AtlasVersionable
  public static class VersionableObject implements Versionable {

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    @AtlasVersionableProperty
    private String property;
    @AtlasVersionableProperty
    private String anotherProperty;
    @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY, relationsFields = {Relation.Fields.value})
    private List<Relation> oneToManyRelation;

    @Data
    @AllArgsConstructor
    @Builder
    @FieldNameConstants
    public static class Relation {

      private Long id;
      private String value;
    }
  }
}
