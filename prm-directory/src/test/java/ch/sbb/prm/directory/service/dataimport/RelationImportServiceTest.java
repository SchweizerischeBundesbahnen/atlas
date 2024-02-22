package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.PlatformCsvTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.service.StopPointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
@Transactional
class RelationImportServiceTest {

  @MockBean
  private StopPointService stopPointService;

  private final RelationRepository relationRepository;
  private final RelationImportService relationImportService;
  private final SharedServicePointRepository sharedServicePointRepository;


  @Autowired
  RelationImportServiceTest(RelationRepository relationRepository, RelationImportService relationImportService, SharedServicePointRepository sharedServicePointRepository) {
    this.relationRepository = relationRepository;
    this.relationImportService = relationImportService;
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:76646",
        Set.of("ch:1:sboid:100602"), Set.of("ch:1:sloid:76646:0:17"));
    sharedServicePointRepository.saveAndFlush(servicePoint);

    when(stopPointService.isReduced(any())).thenReturn(false);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  //shouldImport
  //  When
  //    Stoppointexists & complete
  //    Referencepoint exists
  //    Element exists
  //shouldnot Import
  //  When
  //    Stopoint reduced
  //    Stoppoint not exists
  //    Referencepoint not exists
  //    Element not exists

}