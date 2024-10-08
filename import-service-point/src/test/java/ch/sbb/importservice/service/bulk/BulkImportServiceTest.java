package ch.sbb.importservice.service.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BulkImportServiceTest {

  private BulkImportService bulkImportService;
  private BulkImportRepository bulkImportRepository;

  @BeforeEach
  void setUp() {
    bulkImportRepository = Mockito.mock(BulkImportRepository.class);
    BulkImportS3BucketService bulkImportS3BucketService = Mockito.mock(BulkImportS3BucketService.class);
    BulkImportJobService bulkImportJobService = Mockito.mock(BulkImportJobService.class);
    bulkImportService = new BulkImportService(bulkImportRepository, bulkImportS3BucketService, bulkImportJobService);
  }

  @Test
  void getBulkImport() {
    Mockito.when(bulkImportRepository.findById(5L))
        .thenReturn(Optional.of(BulkImport.builder().application(ApplicationType.SEPODI).build()));

    BulkImport bulkImport = bulkImportService.getBulkImport(5L);

    assertThat(bulkImport.getApplication()).isEqualTo(ApplicationType.SEPODI);
  }

  @Test
  void getBulkImportNotFound() {
    Mockito.when(bulkImportRepository.findById(5L)).thenReturn(Optional.empty());
    assertThrows(IdNotFoundException.class, () -> bulkImportService.getBulkImport(5L), "Entity not found");
  }
}
