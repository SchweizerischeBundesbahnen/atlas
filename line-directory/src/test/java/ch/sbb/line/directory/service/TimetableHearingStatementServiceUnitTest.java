package ch.sbb.line.directory.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.line.directory.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapperV1;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.StatementDocumentFilesValidationService;
import ch.sbb.line.directory.service.hearing.TimetableHearingPdfsAmazonService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class TimetableHearingStatementServiceUnitTest {

  private TimetableHearingStatementService timetableHearingStatementService;

  @Mock
  private TimetableHearingStatementRepository timetableHearingStatementRepositoryMock;
  @Mock
  private TimetableHearingYearRepository timetableHearingYearRepositoryMock;
  @Mock
  private FileService fileServiceMock;
  @Mock
  private TimetableHearingPdfsAmazonService timetableHearingPdfsAmazonServiceMock;
  @Mock
  private StatementDocumentFilesValidationService statementDocumentFilesValidationServiceMock;
  @Mock
  private ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;
   @Mock
   private TimetableHearingStatementMapperV1 timetableHearingStatementMapperV1;
  @Mock
  private TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    timetableHearingStatementService = new TimetableHearingStatementService(
        timetableHearingStatementRepositoryMock,
        timetableHearingYearRepositoryMock,
        fileServiceMock,
        timetableHearingPdfsAmazonServiceMock,
        statementDocumentFilesValidationServiceMock,
        responsibleTransportCompanyMapper,
        timetableHearingStatementMapperV1,
        timetableHearingStatementMapperV2
    );
  }

  @Test
  void shouldCallRepositoryOnDeleteSpamMailFromYear() {
    Long year = 2022L;
    timetableHearingStatementService.deleteSpamMailFromYear(year);
    verify(timetableHearingStatementRepositoryMock).deleteByStatementStatusAndTimetableYear(StatementStatus.JUNK, year);
  }

}
