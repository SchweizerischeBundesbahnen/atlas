package ch.sbb.timetable.field.number;

import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.ArrayList;
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
  public static class VersionableObject implements Versionable {

    public static List<VersionableProperty> VERSIONABLE = new ArrayList<>();

    static {
      VERSIONABLE.add(VersionableProperty.builder().fieldName(Fields.property).relationType(
          RelationType.NONE).build());
      VERSIONABLE.add(
          VersionableProperty.builder().fieldName(Fields.oneToManyRelation).relationType(
              RelationType.ONE_TO_MANY).relationsFields(
              List.of(Relation.Fields.value)
          ).build());
    }

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    private String property;
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
