package ch.sbb.timetable.hearing.controller;

import static org.mockito.Mockito.verify;

import ch.sbb.timetable.hearing.repository.MigrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MigrationControllerInternalUnitTest {

  @Mock
  private MigrationRepository migrationRepository;

  @InjectMocks
  private MigrationControllerInternal migrationControllerInternal;

  @Test
  void shouldDelegateCopyFromLidiToRepository() {
    migrationControllerInternal.copyFromLidi();
    verify(migrationRepository).migrateFromLidi();
  }
}

