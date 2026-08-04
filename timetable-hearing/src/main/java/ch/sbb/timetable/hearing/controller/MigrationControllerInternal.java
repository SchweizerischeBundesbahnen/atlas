package ch.sbb.timetable.hearing.controller;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.timetable.hearing.repository.MigrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MigrationControllerInternal {

  private final MigrationRepository migrationRepository;

  @AdminOnly
  @PostMapping("/internal/migrate-from-lidi")
  void copyFromLidi() {
    migrationRepository.migrateFromLidi();
  }
}