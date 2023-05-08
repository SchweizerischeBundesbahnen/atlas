package ch.sbb.line.directory.service.hearing;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import java.util.Collections;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class TimetableHearingStatementExportServiceTest {

  @Mock
  private FileService fileService;
  @Mock
  private MessageSource timetableHearingStatementCsvTranslations;
  @Mock
  private UserAdministrationClient userAdministrationClient;

  private TimetableHearingStatementExportService timetableHearingStatementExportService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableHearingStatementExportService = new TimetableHearingStatementExportService(fileService,
        timetableHearingStatementCsvTranslations, userAdministrationClient);
    when(fileService.getDir()).thenReturn("export/");
  }

  @Test
  void shouldNotCallUserAdministrationOnNoEditors() {
    timetableHearingStatementExportService.getStatementsAsCsv(Collections.emptyList(), Locale.GERMAN);

    verifyNoInteractions(userAdministrationClient);
  }
}