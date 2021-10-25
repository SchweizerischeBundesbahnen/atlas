package ch.sbb.timetable.field.number.versioning.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VersionedObject {

  private final Long objectId;

  private LocalDate validFrom;

  private LocalDate validTo;

  private final List<ObjectProperty> objectProperties;

  private final VersioningAction action;

}
