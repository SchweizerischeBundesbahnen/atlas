package ch.sbb.timetable.field.number.versioning.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Entity {

  private Long id;

  private List<Property> properties;

}
