package ch.sbb.timetable.field.number.versioning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Property {

  private String key;

  private String value;
}
