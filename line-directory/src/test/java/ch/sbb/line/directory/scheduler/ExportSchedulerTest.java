package ch.sbb.line.directory.scheduler;

import static org.mockito.Mockito.verify;

import ch.sbb.line.directory.service.export.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ExportSchedulerTest {

  @Mock
  private ExportService exportService;

  private ExportScheduler exportScheduler;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    exportScheduler = new ExportScheduler(exportService);
  }

  @Test
  public void shouldExportFullLineVersions() {
    //when
    exportScheduler.exportFullLineVersions();
    //then
    verify(exportService).exportFullLineVersionsCsv();
    verify(exportService).exportFullLineVersionsCsvZip();

  }

  @Test
  public void shouldExportActualLineVersions() {
    //when
    exportScheduler.exportActualLineVersions();
    //then
    verify(exportService).exportActualLineVersionsCsv();
    verify(exportService).exportActualLineVersionsCsvZip();

  }

  @Test
  public void shouldExportFutureTimetableLineVersions() {
    //when
    exportScheduler.exportFutureTimetableLineVersions();
    //then
    verify(exportService).exportFutureTimetableLineVersionsCsv();
    verify(exportService).exportFutureTimetableLineVersionsCsvZip();

  }


}