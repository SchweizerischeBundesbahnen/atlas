package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.RelationCsvTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.service.PlatformService;
import ch.sbb.prm.directory.service.StopPointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
@Transactional
class RelationImportServiceTest {

  @MockBean
  private StopPointService stopPointService;

  private final RelationRepository relationRepository;
  @InjectMocks
  private final RelationImportService relationImportService;
  private final SharedServicePointRepository sharedServicePointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final PlatformRepository platformRepository;
  @Mock
  private final PlatformService platformService;


  @Autowired
  RelationImportServiceTest(RelationRepository relationRepository, RelationImportService relationImportService, SharedServicePointRepository sharedServicePointRepository, ReferencePointRepository referencePointRepository, PlatformRepository platformRepository, PlatformService platformService) {
    this.relationRepository = relationRepository;
    this.relationImportService = relationImportService;
    this.sharedServicePointRepository = sharedServicePointRepository;
      this.referencePointRepository = referencePointRepository;
      this.platformRepository = platformRepository;
      this.platformService = platformService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:76646",
        Set.of("ch:1:sboid:100602"), Set.of("ch:1:sloid:76646:0:17"));
    sharedServicePointRepository.saveAndFlush(servicePoint);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setSloid("ch:1:sloid:294:1");
    referencePointRepository.saveAndFlush(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setSloid("ch:1:sloid:294:787306");
    platformRepository.saveAndFlush(platformVersion);

    when(stopPointService.isReduced(any())).thenReturn(false);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldImportWhenReferencePointAndElementExists() {

    //when
    List<ItemImportResult> result = relationImportService.importRelations(
            List.of(RelationCsvTestData.getContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

  @Test
  void shouldImportWhenRelationExists() {
    //given
    RelationCsvModelContainer relationCsvModelContainer = RelationCsvTestData.getContainer();
    RelationVersion relationVersion = RelationTestData.getRelation("", "", ReferencePointElementType.PLATFORM );
    relationVersion.setSloid(relationCsvModelContainer.getSloid());
    relationVersion.setParentServicePointSloid("ch:1:sloid:76646");
    relationVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
    relationRepository.saveAndFlush(relationVersion);

    //when
    List<ItemImportResult> result = relationImportService.importRelations(List.of(relationCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

  @Test
  void testCheckElementExistsForPlatform() {
    String sloid = "ch:1:sloid:294:787306";

    relationImportService.checkElementExists(ReferencePointElementType.PLATFORM, sloid);

    assertDoesNotThrow(() -> relationImportService.checkElementExists(ReferencePointElementType.PLATFORM, sloid));
  }

  @Test
  void testCheckElementExistsForPlatform_DoesNotExist() {
    String sloid = "ch:1:sloid:76646:1";
    assertThrows(ElementTypeDoesNotExistException.class, () -> {
      relationImportService.checkElementExists(ReferencePointElementType.PLATFORM, sloid);
    });
  }
}