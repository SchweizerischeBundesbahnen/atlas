package ch.sbb.atlas.versioning;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

public abstract class MergeBaseTest {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @FieldNameConstants
  @AtlasVersionable
  public static class MergeVersionableObject implements Versionable {

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    @AtlasVersionableProperty
    private String property;
    @AtlasVersionableProperty
    private String anotherProperty;
    @AtlasVersionableProperty(ignoreDiff = true)
    private String propertyToBeIgnored;
    @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY, relationsFields = {
        Relation.Fields.value})
    private List<Relation> oneToManyRelation = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldNameConstants
    public static class Relation {

      private Long id;
      @AtlasVersionableProperty
      private String value;
      private VersionableObject versionableObject;
    }
  }
}
