package ch.sbb.timetable.field.number.versioning.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VersionedObject {

  private LocalDate validFrom;

  private LocalDate validTo;

  private final Entity entity;

  private VersioningAction action;

}
