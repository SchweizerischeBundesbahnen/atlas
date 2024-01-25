package ch.sbb.prm.directory.service.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.service.StopPointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class PlatformImportServiceTest {

  @MockBean
  private StopPointService stopPointService;

  private final PlatformRepository platformRepository;
  private final PlatformImportService platformImportService;
  private final SharedServicePointRepository sharedServicePointRepository;


  @Autowired
  PlatformImportServiceTest(PlatformRepository platformRepository, PlatformImportService platformImportService, SharedServicePointRepository sharedServicePointRepository) {
    this.platformRepository = platformRepository;
    this.platformImportService = platformImportService;
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

  @Test
  void shouldImportWhenPlatformDoesNotExists() {
    //when
    List<ItemImportResult> result = platformImportService.importPlatforms(List.of(PlatformCsvTestData.getContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8576646");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

  @Test
  void shouldImportWhenPlatformExists() {
    //given
    PlatformCsvModelContainer platformCsvModelContainer = PlatformCsvTestData.getContainer();
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setSloid(platformCsvModelContainer.getSloid());
    platformVersion.setParentServicePointSloid("ch:1:sloid:76646");
    platformVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
    platformRepository.saveAndFlush(platformVersion);

    //when
    List<ItemImportResult> result = platformImportService.importPlatforms(List.of(platformCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8576646");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

}