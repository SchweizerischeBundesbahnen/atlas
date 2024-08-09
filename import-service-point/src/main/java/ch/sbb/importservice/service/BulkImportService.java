package ch.sbb.importservice.service;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BulkImportService {

  private final BulkImportRepository bulkImportRepository;

  @Async
  public void startBulkImport(BulkImport bulkImport) {
    saveBulkImportMetaData(bulkImport);
  }

  public void saveBulkImportMetaData(BulkImport bulkImport) {
    bulkImportRepository.saveAndFlush(bulkImport);
  }
}
