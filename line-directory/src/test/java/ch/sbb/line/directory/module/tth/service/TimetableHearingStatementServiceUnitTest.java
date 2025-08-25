package ch.sbb.line.directory.module.tth.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.module.tth.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.module.tth.mapper.TimetableHearingStatementMapperV1;
import ch.sbb.line.directory.module.tth.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingYearRepository;
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
  private TimetableFieldNumberRepository timetableFieldNumberRepository;
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
        timetableFieldNumberRepository,
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
