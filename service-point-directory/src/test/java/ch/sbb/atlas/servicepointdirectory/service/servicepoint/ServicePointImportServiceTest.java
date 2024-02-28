package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@Slf4j
class ServicePointImportServiceTest {

  // required for test functionality
  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;
  @MockBean
  private LocationService locationService;

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;
  private final ServicePointFotCommentService servicePointFotCommentService;

  @Autowired
  ServicePointImportServiceTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository,
      ServicePointFotCommentService servicePointFotCommentService) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
    this.servicePointFotCommentService = servicePointFotCommentService;
  }

  @Test
  void shouldDeleteAvailableServicePointNumbersOnSave() {
    // given
    int didokCode = 85070001;
    final List<ServicePointCsvModel> csvModels = List.of(
        ServicePointCsvModel.builder()
            .validFrom(LocalDate.of(2002, 1, 1))
            .validTo(LocalDate.of(2002, 6, 15))
            .abkuerzung("BWYG")
            .bezeichnungLang("Bern, Wyleregg")
            .bezeichnungOffiziell("Bern, Wyleregg")
            .isBedienpunkt(true)
            .isBetriebspunkt(true)
            .isFahrplan(true)
            .nummer(didokCode)
            .laendercode(85)
            .status(1)
            .didokCode(didokCode)
            .comment("BAV-Kommentar")
            .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(2023, 1, 1, 1, 1))
            .editedBy("fs22222")
            .build()
    );
    final ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setDidokCode(didokCode);
    container.setServicePointCsvModelList(csvModels);
    final List<ServicePointCsvModelContainer> containers = List.of(container);

    // when
    servicePointImportService.importServicePoints(containers);
  }

  @Test
  void shouldImportServicePoints() {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getServicePointCsvModelContainers();
    Integer didokCode = servicePointCsvModelContainers.get(0).getDidokCode();
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode);
    //when
    List<ItemImportResult> itemImportResults = servicePointImportService.importServicePoints(
        servicePointCsvModelContainers);

    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).isNotNull();
    assertThat(itemImportResults).hasSize(5);
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getStatus()).isEqualTo(Status.DRAFT);
    assertThat(result.get(1).getStatus()).isEqualTo(Status.IN_REVIEW);
    assertThat(result.get(2).getStatus()).isEqualTo(Status.VALIDATED);
    for (ServicePointVersion servicePointVersion : result) {
      assertThat(servicePointVersion.getNumber()).isNotNull();
      assertThat(servicePointVersion.getNumber()).isEqualTo(servicePointNumber);
    }

    Optional<ServicePointFotComment> fotComment = servicePointFotCommentService.findByServicePointNumber(didokCode);
    assertThat(fotComment).isPresent();
    assertThat(fotComment.get().getFotComment()).isEqualTo("BAV-Kommentar");
  }

  @Test
  void shouldImportServicePointsAndOverwriteStatusIfOnlyStatusChanged() {
    // given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getServicePointCsvModelContainers();
    Integer didokCode = servicePointCsvModelContainers.get(0).getDidokCode();
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode);
    // when
    List<ItemImportResult> itemImportResults = servicePointImportService.importServicePoints(
            servicePointCsvModelContainers);

    // then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).isNotNull();
    assertThat(itemImportResults).hasSize(5);
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getStatus()).isEqualTo(Status.DRAFT);
    assertThat(result.get(1).getStatus()).isEqualTo(Status.IN_REVIEW);
    assertThat(result.get(2).getStatus()).isEqualTo(Status.VALIDATED);
    for (ServicePointVersion servicePointVersion : result) {
      assertThat(servicePointVersion.getNumber()).isNotNull();
      assertThat(servicePointVersion.getNumber()).isEqualTo(servicePointNumber);
    }

    // given
    ServicePointVersionModel servicePointVersionModel1 = ServicePointVersionMapper.toModel(servicePointVersionRepository.findAll().get(0));
    ServicePointVersionModel servicePointVersionModel2 = ServicePointVersionMapper.toModel(servicePointVersionRepository.findAll().get(1));
    ServicePointVersionModel servicePointVersionModel3 = ServicePointVersionMapper.toModel(servicePointVersionRepository.findAll().get(2));
    // set all statuses to VALIDATED
    servicePointVersionRepository.findAll().forEach(spv -> spv.setStatus(Status.VALIDATED));

    // make all elements in servicePointCsvModelContainers identical like in DB, meaning set edition and creation details like in DB
    ServicePointCsvModel servicePointCsvModel1 = servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0);
    servicePointCsvModel1.setEditedAt(servicePointVersionModel1.getEditionDate());
    servicePointCsvModel1.setEditedBy(servicePointVersionModel1.getEditor());
    servicePointCsvModel1.setCreatedAt(servicePointVersionModel1.getCreationDate());
    servicePointCsvModel1.setCreatedBy(servicePointVersionModel1.getCreator());

    ServicePointCsvModel servicePointCsvModel2 = servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(1);
    servicePointCsvModel2.setEditedAt(servicePointVersionModel2.getEditionDate());
    servicePointCsvModel2.setEditedBy(servicePointVersionModel2.getEditor());
    servicePointCsvModel2.setCreatedAt(servicePointVersionModel2.getCreationDate());
    servicePointCsvModel2.setCreatedBy(servicePointVersionModel2.getCreator());

    ServicePointCsvModel servicePointCsvModel3 = servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(2);
    servicePointCsvModel3.setEditedAt(servicePointVersionModel3.getEditionDate());
    servicePointCsvModel3.setEditedBy(servicePointVersionModel3.getEditor());
    servicePointCsvModel3.setCreatedAt(servicePointVersionModel3.getCreationDate());
    servicePointCsvModel3.setCreatedBy(servicePointVersionModel3.getCreator());

    ServicePointCsvModel servicePointCsvModel4 = servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(3);
    servicePointCsvModel4.setEditedAt(servicePointVersionModel3.getEditionDate());
    servicePointCsvModel4.setEditedBy(servicePointVersionModel3.getEditor());
    servicePointCsvModel4.setCreatedAt(servicePointVersionModel3.getCreationDate());
    servicePointCsvModel4.setCreatedBy(servicePointVersionModel3.getCreator());

    ServicePointCsvModel servicePointCsvModel5 = servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(4);
    servicePointCsvModel5.setEditedAt(servicePointVersionModel3.getEditionDate());
    servicePointCsvModel5.setEditedBy(servicePointVersionModel3.getEditor());
    servicePointCsvModel5.setCreatedAt(servicePointVersionModel3.getCreationDate());
    servicePointCsvModel5.setCreatedBy(servicePointVersionModel3.getCreator());

    // when import again with the same data, only status changed
    List<ItemImportResult> itemImportResults1 = servicePointImportService.importServicePoints(
            servicePointCsvModelContainers);

    // then status updated
    List<ServicePointVersion> result1 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result1).isNotNull();
    assertThat(itemImportResults1).hasSize(5);
    assertThat(result1).hasSize(3);
    assertThat(result1.get(0).getStatus()).isEqualTo(Status.DRAFT);
    assertThat(result1.get(0).getEditionDate()).isNotEqualTo(servicePointVersionModel1.getEditionDate());
    assertThat(result1.get(1).getStatus()).isEqualTo(Status.IN_REVIEW);
    assertThat(result1.get(1).getEditionDate()).isNotEqualTo(servicePointVersionModel2.getEditionDate());
    assertThat(result1.get(2).getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(result1.get(2).getEditionDate()).isEqualTo(servicePointVersionModel3.getEditionDate());

  }

  @Test
  void shouldUpdateValidToAndEditionPropertiesCorrectlyOnSecondRun() {
    // given
    final List<ServicePointCsvModel> servicePointCsvModels = List.of(
        ServicePointCsvModel.builder()
            .validFrom(LocalDate.of(2002, 1, 1))
            .validTo(LocalDate.of(2002, 12, 31))
            .abkuerzung("BWYG")
            .bezeichnungLang("Bern, Wyleregg")
            .bezeichnungOffiziell("Bern, Wyleregg")
            .isBedienpunkt(true)
            .isBetriebspunkt(true)
            .isFahrplan(true)
            .nummer(85070001)
            .laendercode(85)
            .status(1)
            .didokCode(85070001)
            .comment("BAV-Kommentar")
            .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .editedBy("fs11111")
            .build()
    );

    final List<ServicePointCsvModelContainer> servicePointCsvModelContainers = List.of(
        ServicePointCsvModelContainer.builder()
            .servicePointCsvModelList(servicePointCsvModels)
            .didokCode(85070001)
            .build()
    );
    servicePointImportService.importServicePoints(servicePointCsvModelContainers);

    final List<ServicePointCsvModel> servicePointCsvModelsSecondRun = List.of(
        ServicePointCsvModel.builder()
            .validFrom(LocalDate.of(2002, 1, 1))
            .validTo(LocalDate.of(2002, 6, 15))
            .abkuerzung("BWYG")
            .bezeichnungLang("Bern, Wyleregg")
            .bezeichnungOffiziell("Bern, Wyleregg")
            .isBedienpunkt(true)
            .isBetriebspunkt(true)
            .isFahrplan(true)
            .nummer(85070001)
            .laendercode(85)
            .status(1)
            .didokCode(85070001)
            .comment("BAV-Kommentar")
            .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(2023, 1, 1, 1, 1))
            .editedBy("fs22222")
            .build()
    );

    final List<ServicePointCsvModelContainer> servicePointCsvModelContainersSecondRun = List.of(
        ServicePointCsvModelContainer.builder()
            .servicePointCsvModelList(servicePointCsvModelsSecondRun)
            .didokCode(85070001)
            .build()
    );

    // when
    final List<ItemImportResult> servicePointItemImportResults =
        servicePointImportService.importServicePoints(servicePointCsvModelContainersSecondRun);

    // then
    assertThat(servicePointItemImportResults).hasSize(1);

    final List<ServicePointVersion> dbVersions =
        servicePointVersionRepository.findAllByNumberOrderByValidFrom(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));

    assertThat(dbVersions).hasSize(1);
    assertThat(dbVersions.get(0).getEditor()).isEqualTo("fs22222");
    assertThat(dbVersions.get(0).getEditionDate()).isEqualTo(LocalDateTime.of(2023, 1, 1, 1, 1));
    assertThat(dbVersions.get(0).getCreator()).isEqualTo("fs11111");
    assertThat(dbVersions.get(0).getCreationDate()).isEqualTo(LocalDateTime.of(2020, 1, 1, 1, 1));
    assertThat(dbVersions.get(0).getValidFrom()).isEqualTo("2002-01-01");
    assertThat(dbVersions.get(0).getValidTo()).isEqualTo("2002-06-15");
  }

  private List<ServicePointCsvModelContainer> getServicePointCsvModelContainers() {
    ServicePointTestData.getBernWyleregg();
    int didokCode = 80187710;
    ServicePointCsvModel withGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(0)
        .abkuerzung("TEST")
        .didokCode(didokCode)
        .comment("BAV-Kommentar")
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .abkuerzung("TEST1")
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .didokCode(didokCode)
        .comment("BAV-Kommentar")
        .build();
    ServicePointCsvModel virtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("TEST2")
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(2)
        .didokCode(didokCode)
        .comment("BAV-Kommentar")
        .build();
    ServicePointCsvModel virtualWithoutGeolocation2 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("TEST2")
        .bezeichnungLang("Bern, Wankdorf")
        .bezeichnungOffiziell("Bern, Wankdorf")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(3)
        .didokCode(didokCode)
        .comment("BAV-Kommentar")
        .build();
    ServicePointCsvModel virtualWithoutGeolocation3 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("TEST2")
        .bezeichnungLang("Bern, Wankdorf")
        .bezeichnungOffiziell("Bern, Wankdorf")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(4)
        .didokCode(didokCode)
        .comment("BAV-Kommentar")
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withGeolocation);
    modelList.add(notVirtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation2);
    modelList.add(virtualWithoutGeolocation3);
    modelList.sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(didokCode);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(container);
    return servicePointCsvModelContainers;
  }

  @ParameterizedTest
  @ValueSource(strings = {"11", "12", "13", "14"})
  void shouldImportServicePointsWithUicCodes11To14WithAndWithoutSloidAndVerifySloidExistsByAll(String input) {
    //given
    int prefixCountryCode = Integer.parseInt(input);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers =
        getContainersForUicCodes11To14WithAndWithoutSloid(prefixCountryCode);

    String stringWithoutSloid = prefixCountryCode + String.valueOf(18771);
    int didokCode = Integer.parseInt(stringWithoutSloid);
    String sloid = "ch:1:sloid:" + didokCode;
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode);

    String stringWithoutSloid1 = prefixCountryCode + String.valueOf(18772);
    int didokCode1 = Integer.parseInt(stringWithoutSloid1);
    String sloid1 = "ch:1:sloid:" + didokCode1;
    ServicePointNumber servicePointNumber1 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode1);
    //when
    List<ItemImportResult> itemImportResults = servicePointImportService.importServicePoints(
        servicePointCsvModelContainers);
    LocalDateTime now = LocalDateTime.now();

    //then
    assertThat(itemImportResults).hasSize(2);
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSloid()).isEqualTo(sloid);
    assertThat(result.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result1 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber1);
    assertThat(result1).hasSize(1);
    assertThat(result1.get(0).getSloid()).isEqualTo(sloid1);
    assertThat(result1.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
  }

  private List<ServicePointCsvModelContainer> getContainersForUicCodes11To14WithAndWithoutSloid(int prefixCountryCode) {
    LocalDateTime editedAt = LocalDateTime.of(2020, 1, 1, 22, 22);

    String stringWithoutSloid = prefixCountryCode + String.valueOf(18771);
    int numberWithoutSloid = Integer.parseInt(stringWithoutSloid);
    String bezeichnungWithoutSloid = "BernNoSloid, Wyleregg";
    String abkuerzungWithoutSloid = "TES" + prefixCountryCode;

    String stringWithSloid = prefixCountryCode + String.valueOf(18772);
    int numberWithSloid = Integer.parseInt(stringWithSloid);
    String bezeichnungWithSloid = "BernSloid, Wyleregg";
    String abkuerzungWithSloid = "SET" + prefixCountryCode;

    String sloid = "ch:1:sloid:" + numberWithSloid;

    ServicePointCsvModel servicePointWithSloidNull = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang(bezeichnungWithoutSloid)
        .bezeichnungOffiziell(bezeichnungWithoutSloid)
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberWithoutSloid)
        .laendercode(prefixCountryCode)
        .status(0)
        .abkuerzung(abkuerzungWithoutSloid)
        .didokCode(numberWithoutSloid)
        .sloid(null)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    ServicePointCsvModel servicePointWithSloid = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang(bezeichnungWithSloid)
        .bezeichnungOffiziell(bezeichnungWithSloid)
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberWithSloid)
        .laendercode(prefixCountryCode)
        .status(0)
        .abkuerzung(abkuerzungWithSloid)
        .didokCode(numberWithSloid)
        .sloid(sloid)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    List<ServicePointCsvModel> modelListWithoutSloid = new ArrayList<>();
    modelListWithoutSloid.add(servicePointWithSloidNull);
    List<ServicePointCsvModel> modelListWithSloid = new ArrayList<>();
    modelListWithSloid.add(servicePointWithSloid);

    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelListWithoutSloid);
    container.setDidokCode(numberWithoutSloid);
    ServicePointCsvModelContainer container1 = new ServicePointCsvModelContainer();
    container1.setServicePointCsvModelList(modelListWithSloid);
    container1.setDidokCode(numberWithSloid);

    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(container);
    servicePointCsvModelContainers.add(container1);
    return servicePointCsvModelContainers;
  }

}
