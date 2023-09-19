package ch.sbb.line.directory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import ch.sbb.line.directory.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TimetableFieldNumberServiceTest {

  private static final long ID = 1L;

  @Mock
  private TimetableFieldNumberVersionRepository versionRepository;
  @Mock
  private TimetableFieldNumberRepository timetableFieldNumberRepository;
  @Mock
  private TimetableFieldNumberValidationService timetableFieldNumberValidationService;
  @Mock
  private VersionableService versionableService;

  private TimetableFieldNumberService timetableFieldNumberService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableFieldNumberService = new TimetableFieldNumberService(versionRepository,
      timetableFieldNumberRepository,timetableFieldNumberValidationService, versionableService);
  }

  @Test
   void shouldThrowStaleExceptionOnDifferentVersion() {
    //given
    TimetableFieldNumberVersionBuilder<?, ?> version = TimetableFieldNumberVersion.builder().ttfnid("ttfnid");

    Executable executable = () -> timetableFieldNumberService.updateVersion(
      version.version(1).build(), version.version(0).build());
    assertThrows(StaleObjectStateException.class, executable);
    //then
    verify(versionRepository).incrementVersion("ttfnid");
  }

}
