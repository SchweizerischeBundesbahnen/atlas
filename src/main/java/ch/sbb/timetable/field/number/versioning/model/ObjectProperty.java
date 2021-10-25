package ch.sbb.timetable.field.number.versioning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ObjectProperty {

  private Long objectId;

  private String key;

  private String value;

}
