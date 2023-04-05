package ch.sbb.line.directory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TimetableHearingStatementControllerTest {

  @Mock
  private TimetableHearingStatementService timetableHearingStatementService;
  @Mock
  private TimetableHearingYearService timetableHearingYearService;
  @Mock
  private TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  @Mock
  private ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;

  private TimetableHearingStatementController timetableHearingStatementController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableHearingStatementController = new TimetableHearingStatementController(timetableHearingStatementService,
        timetableHearingYearService, timetableFieldNumberResolverService, responsibleTransportCompaniesResolverService);
  }

  @Test
  void shouldLoadResponsibleTransportCompaniesByYear() {
    when(responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(any(), any())).thenReturn(
        Collections.emptyList());

    timetableHearingStatementController.getResponsibleTransportCompanies("ttfnid", 2024L);

    verify(responsibleTransportCompaniesResolverService).getResponsibleTransportCompanies("ttfnid", LocalDate.of(2024, 1, 1));
  }
}