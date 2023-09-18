package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementExportService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class TimetableHearingStatementControllerTest {

  @Mock
  private TimetableHearingStatementService timetableHearingStatementService;
  @Mock
  private TimetableHearingYearService timetableHearingYearService;
  @Mock
  private TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  @Mock
  private ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;
  @Mock
  private TimetableHearingStatementExportService timetableHearingStatementExportService;

  private TimetableHearingStatementController timetableHearingStatementController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableHearingStatementController = new TimetableHearingStatementController(timetableHearingStatementService,
        timetableHearingYearService, timetableFieldNumberResolverService, responsibleTransportCompaniesResolverService,
        timetableHearingStatementExportService);
  }

  @Test
  void shouldLoadResponsibleTransportCompaniesByYear() {
    when(responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(any(), any())).thenReturn(
        Collections.emptyList());

    timetableHearingStatementController.getResponsibleTransportCompanies("ttfnid", 2024L);

    verify(responsibleTransportCompaniesResolverService).getResponsibleTransportCompanies("ttfnid", LocalDate.of(2024, 1, 1));
  }

  @Test
  void shouldRequireTimetableHearingYear() {
    TimetableHearingStatementRequestParams params = TimetableHearingStatementRequestParams.builder()
        .build();

    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timetableHearingStatementController.getStatementsAsCsv("de", params));

    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("timetableHearingYear is mandatory here");
  }

  @Test
  void shouldRequireLanguage() {
    TimetableHearingStatementRequestParams params = TimetableHearingStatementRequestParams.builder()
        .timetableHearingYear(2023L)
        .build();

    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> timetableHearingStatementController.getStatementsAsCsv("en", params));

    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Language must be either de,fr,it");
  }
}
