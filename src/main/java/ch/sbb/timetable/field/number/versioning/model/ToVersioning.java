package ch.sbb.timetable.field.number.versioning.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToVersioning {

  private Long objectId;

  private Versionable versionable;

  private List<AttributeObject> attributeObjects;
}
