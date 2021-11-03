package ch.sbb.timetable.field.number;

import ch.sbb.timetable.field.number.versioning.annotation.AtlasVersionable;
import ch.sbb.timetable.field.number.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
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
