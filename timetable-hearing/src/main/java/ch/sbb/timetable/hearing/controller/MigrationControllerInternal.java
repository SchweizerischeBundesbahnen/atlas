package ch.sbb.timetable.hearing.controller;

import ch.sbb.timetable.hearing.api.MigrationApiInternal;
import ch.sbb.timetable.hearing.repository.MigrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MigrationControllerInternal implements MigrationApiInternal {

  private final MigrationRepository migrationRepository;

  @Override
  public void copyFromLidi() {
    migrationRepository.migrateFromLidi();
  }
}