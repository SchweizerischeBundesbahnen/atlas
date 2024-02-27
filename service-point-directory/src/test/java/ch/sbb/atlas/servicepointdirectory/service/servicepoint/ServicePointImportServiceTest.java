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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@IntegrationTest
@Transactional
@Slf4j
class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";
  private static final String SEPARATOR = "/";

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

  /**
   * DB   |-------A--------|-------B-------|
   * CSV  |----------------A---------------|
   * Res  |----------------A---------------|
   */
  @Test
  void shouldMergeServicePointByImportServicePointsWithoutGeolocation() throws IOException {
    //given
    ServicePointNumber servicePointNumber;
    String firstFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230717021649_without_geolocation.csv";
    ServicePointCsvModelContainer firstFileCsvContainer = getContainer(firstFile);
    servicePointImportService.importServicePoints(List.of(firstFileCsvContainer));
    servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(firstFileCsvContainer.getDidokCode());
    List<ServicePointVersion> firstResult = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(firstResult).hasSize(2);
    assertThat(firstResult.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(firstResult.get(0).getValidTo()).isEqualTo(LocalDate.of(2022, 5, 30));
    assertThat(firstResult.get(0).getAbbreviation()).isEqualTo("FIGE");
    assertThat(firstResult.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(firstResult.get(1).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(firstResult.get(1).getAbbreviation()).isEqualTo("FIBE");

    //when
    String secondFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230718021052_without_geolocation.csv";
    ServicePointCsvModelContainer secondFileCsvContainer = getContainer(secondFile);
    servicePointImportService.importServicePoints(List.of(secondFileCsvContainer));
    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(result.get(0).getAbbreviation()).isEqualTo("FIBE");
  }

  /**
   * DB   |-------A--------|-------B-------|
   * CSV          |-------C--------|
   * Res  |---A---|-------C--------|---B---|
   */
  @Test
  void shouldUpdateServicePointByImportServicePoints() throws IOException {
    //given
    ServicePointNumber servicePointNumber;
    String firstFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230717021649_without_geolocation.csv";
    ServicePointCsvModelContainer firstFileCsvContainer = getContainer(firstFile);
    servicePointImportService.importServicePoints(List.of(firstFileCsvContainer));
    servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(firstFileCsvContainer.getDidokCode());
    List<ServicePointVersion> firstResult = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(firstResult).hasSize(2);
    assertThat(firstResult.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(firstResult.get(0).getValidTo()).isEqualTo(LocalDate.of(2022, 5, 30));
    assertThat(firstResult.get(0).getAbbreviation()).isEqualTo("FIGE");
    assertThat(firstResult.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(firstResult.get(1).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(firstResult.get(1).getAbbreviation()).isEqualTo("FIBE");

    //when
    String secondFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230718021052_without_geolocation_third_version.csv";
    ServicePointCsvModelContainer secondFileCsvContainer = getContainer(secondFile);
    servicePointImportService.importServicePoints(List.of(secondFileCsvContainer));

    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(result.get(0).getAbbreviation()).isEqualTo("FIGE");
    assertThat(result.get(1).getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(result.get(1).getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(result.get(1).getAbbreviation()).isEqualTo("FIGA");
    assertThat(result.get(2).getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(result.get(2).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(result.get(2).getAbbreviation()).isEqualTo("FIBE");
  }

  /**
   * * DB   |-------A--------|-------B-------|
   * * CSV  |-------A--------|-------A-------|
   * * Res  |----------------A---------------|
   * <p>
   * 1) The first File has 2 identical sequential ServicePoint versions with different Geolocation versions
   * 2) The second File has 2 identical sequential ServicePoint versions and two identical Geolocation version
   * 3) The Result is one ServicePoint version with one Geolocation version -> the sequential versions are merged
   */
  @Test
  void shouldMergeServicePointByImportServicePointsWithMergeGeolocation() throws IOException {
    //given
    ServicePointNumber servicePointNumber;
    String firstFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230717021649_geo_with_merge.csv";
    ServicePointCsvModelContainer firstFileCsvContainer = getContainer(firstFile);
    servicePointImportService.importServicePoints(List.of(firstFileCsvContainer));
    servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(firstFileCsvContainer.getDidokCode());
    List<ServicePointVersion> firstResult = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(firstResult).hasSize(2);
    assertThat(firstResult.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(firstResult.get(0).getValidTo()).isEqualTo(LocalDate.of(2022, 5, 30));
    assertThat(firstResult.get(0).getAbbreviation()).isEqualTo("FIBE");
    assertThat(firstResult.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(firstResult.get(1).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(firstResult.get(1).getAbbreviation()).isEqualTo("FIBE");

    //when
    String secondFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230718021052_geo_with_merge.csv";
    ServicePointCsvModelContainer secondFileCsvContainer = getContainer(secondFile);
    servicePointImportService.importServicePoints(List.of(secondFileCsvContainer));
    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(result.get(0).getAbbreviation()).isEqualTo("FIBE");
  }

  /**
   * * DB   |-------A--------|-------B-------|
   * * CSV  |----------------A---------------|
   * * Res  |----------------A---------------|
   * <p>
   * 1) The first File has 2 identical sequential ServicePoint versions with different Geolocation versions
   * 2) The second File has 1 ServicePoint version and one Geolocation version, the versions are already be merged
   * 3) The Result should be one ServicePoint version with one Geolocation version like the second file
   */
  @Test
  void shouldMergeServicePointByImportServicePointsWithPremergedGeolocation() throws IOException {
    //given
    ServicePointNumber servicePointNumber;
    String firstFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230717021649_geo.csv";
    ServicePointCsvModelContainer firstFileCsvContainer = getContainer(firstFile);
    servicePointImportService.importServicePoints(List.of(firstFileCsvContainer));
    servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(firstFileCsvContainer.getDidokCode());
    List<ServicePointVersion> firstResult = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(firstResult).hasSize(2);
    assertThat(firstResult.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(firstResult.get(0).getValidTo()).isEqualTo(LocalDate.of(2022, 5, 30));
    assertThat(firstResult.get(0).getAbbreviation()).isEqualTo("FIGE");
    assertThat(firstResult.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(firstResult.get(1).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(firstResult.get(1).getAbbreviation()).isEqualTo("FIBE");

    //when
    String secondFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230718021052_geo.csv";
    ServicePointCsvModelContainer secondFileCsvContainer = getContainer(secondFile);
    servicePointImportService.importServicePoints(List.of(secondFileCsvContainer));

    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(result.get(0).getAbbreviation()).isEqualTo("FIBE");
  }

  /**
   * See https://flow.sbb.ch/browse/ATLAS-1341
   * <p>
   * DB   |-------A--------|-------B-------|
   * CSV  |----------------A---------------|
   * Res  |----------------A---------------|
   * <p>
   * 1) The first File has 2 identical sequential ServicePoint versions with different Geolocation versions
   * 2) The second File has 1 ServicePoint version and one Geolocation version, the versions are already be merged
   * 3) The Result should be one ServicePoint version with one Geolocation version like the second file
   */
  @Test
  void shouldMergeServicePointByImportServicePointsWithPremergedGeolocationDataExample() throws IOException {
    //given
    ServicePointNumber servicePointNumber;
    String firstFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230717021649.csv";
    ServicePointCsvModelContainer firstFileCsvContainer = getContainer(firstFile);
    servicePointImportService.importServicePoints(List.of(firstFileCsvContainer));
    servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(firstFileCsvContainer.getDidokCode());
    List<ServicePointVersion> firstResult = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(firstResult).hasSize(7);
    assertThat(firstResult.get(0).getValidFrom()).isEqualTo(LocalDate.of(1987, 12, 31));
    assertThat(firstResult.get(0).getValidTo()).isEqualTo(LocalDate.of(2007, 12, 8));
    assertThat(firstResult.get(0).getAbbreviation()).isNull();
    assertThat(firstResult.get(1).getValidFrom()).isEqualTo(LocalDate.of(2007, 12, 9));
    assertThat(firstResult.get(1).getValidTo()).isEqualTo(LocalDate.of(2008, 12, 13));
    assertThat(firstResult.get(1).getAbbreviation()).isNull();
    assertThat(firstResult.get(2).getValidFrom()).isEqualTo(LocalDate.of(2008, 12, 14));
    assertThat(firstResult.get(2).getValidTo()).isEqualTo(LocalDate.of(2015, 7, 20));
    assertThat(firstResult.get(2).getAbbreviation()).isNull();
    assertThat(firstResult.get(3).getValidFrom()).isEqualTo(LocalDate.of(2015, 7, 21));
    assertThat(firstResult.get(3).getValidTo()).isEqualTo(LocalDate.of(2018, 2, 17));
    assertThat(firstResult.get(3).getAbbreviation()).isEqualTo("FIBE");
    assertThat(firstResult.get(4).getValidFrom()).isEqualTo(LocalDate.of(2018, 2, 18));
    assertThat(firstResult.get(4).getValidTo()).isEqualTo(LocalDate.of(2020, 8, 31));
    assertThat(firstResult.get(4).getAbbreviation()).isEqualTo("FIBE");
    assertThat(firstResult.get(5).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(firstResult.get(5).getValidTo()).isEqualTo(LocalDate.of(2022, 5, 30));
    assertThat(firstResult.get(5).getAbbreviation()).isEqualTo("FIBE");
    assertThat(firstResult.get(6).getValidFrom()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(firstResult.get(6).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(firstResult.get(6).getAbbreviation()).isEqualTo("FIBE");

    //when
    String secondFile = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230718021052.csv";
    ServicePointCsvModelContainer secondFileCsvContainer = getContainer(secondFile);
    servicePointImportService.importServicePoints(List.of(secondFileCsvContainer));

    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(6);
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(1987, 12, 31));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2007, 12, 8));
    assertThat(result.get(0).getAbbreviation()).isNull();
    assertThat(result.get(1).getValidFrom()).isEqualTo(LocalDate.of(2007, 12, 9));
    assertThat(result.get(1).getValidTo()).isEqualTo(LocalDate.of(2008, 12, 13));
    assertThat(result.get(1).getAbbreviation()).isNull();
    assertThat(result.get(2).getValidFrom()).isEqualTo(LocalDate.of(2008, 12, 14));
    assertThat(result.get(2).getValidTo()).isEqualTo(LocalDate.of(2015, 7, 20));
    assertThat(result.get(2).getAbbreviation()).isNull();
    assertThat(result.get(3).getValidFrom()).isEqualTo(LocalDate.of(2015, 7, 21));
    assertThat(result.get(3).getValidTo()).isEqualTo(LocalDate.of(2018, 2, 17));
    assertThat(result.get(3).getAbbreviation()).isEqualTo("FIBE");
    assertThat(result.get(4).getValidFrom()).isEqualTo(LocalDate.of(2018, 2, 18));
    assertThat(result.get(4).getValidTo()).isEqualTo(LocalDate.of(2020, 8, 31));
    assertThat(result.get(4).getAbbreviation()).isEqualTo("FIBE");
    assertThat(result.get(5).getValidFrom()).isEqualTo(LocalDate.of(2020, 9, 1));
    assertThat(result.get(5).getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(result.get(5).getAbbreviation()).isEqualTo("FIBE");
  }

  @Test
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + CSV_FILE)) {
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

      assertThat(servicePointCsvModels).isNotEmpty();
      ServicePointCsvModel firstServicePointCsvModel = servicePointCsvModels.get(0);
      assertThat(firstServicePointCsvModel.getNummer()).isNotNull();
      assertThat(firstServicePointCsvModel.getLaendercode()).isNotNull();
      assertThat(firstServicePointCsvModel.getDidokCode()).isNotNull();
      assertThat(firstServicePointCsvModel.getCreatedAt()).isNotNull();
      assertThat(firstServicePointCsvModel.getCreatedBy()).isNotNull();
    }
  }

  @Test
  void shouldParseCsvAndAllTheBooleansShouldCorrespond() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + CSV_FILE)) {
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
      ServicePointCsvToEntityMapper servicePointCsvToEntityMapper = new ServicePointCsvToEntityMapper();

      List<Pair<ServicePointCsvModel, ServicePointVersion>> mappingResult = servicePointCsvModels
          .stream()
          .map(i -> Pair.of(i, servicePointCsvToEntityMapper.apply(i)))
          .toList();

      for (Pair<ServicePointCsvModel, ServicePointVersion> mappingPair : mappingResult) {
        ServicePointCsvModel csvModel = mappingPair.getFirst();
        ServicePointVersion atlasModel = mappingPair.getSecond();

        assertThat(csvModel.getIsBetriebspunkt()).isEqualTo(atlasModel.isOperatingPoint());
        assertThat(csvModel.getIsFahrplan()).isEqualTo(atlasModel.isOperatingPointWithTimetable());
        assertThat(csvModel.getIsHaltestelle()).isEqualTo(atlasModel.isStopPoint());
        assertThat(csvModel.getIsBedienpunkt()).isEqualTo(atlasModel.isFreightServicePoint());
        assertThat(csvModel.getIsVerkehrspunkt()).isEqualTo(atlasModel.isTrafficPoint());
        assertThat(csvModel.getIsGrenzpunkt()).isEqualTo(atlasModel.isBorderPoint());
      }
    }
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

  private ServicePointCsvModelContainer getContainer(String filePath) throws IOException {
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + filePath)) {
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
      Integer didokCode = servicePointCsvModels.get(0).getDidokCode();
      container.setServicePointCsvModelList(servicePointCsvModels);
      container.setDidokCode(didokCode);
    }
    return container;
  }

  @Test
  void shouldImportServicePointsWithUicCodes11To14WithAndWithoutSloidAndVerifySloidExistsByAll() {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getContainersForUicCodes11To14WithAndWithoutSloid();
    int didokCode = 1118771;
    String sloid = "ch:1:sloid:" + didokCode;
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode);
    int didokCode1 = 1118772;
    String sloid1 = "ch:1:sloid:" + didokCode1;
    ServicePointNumber servicePointNumber1 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode1);
    int didokCode2 = 1218771;
    String sloid2 = "ch:1:sloid:" + didokCode2;
    ServicePointNumber servicePointNumber2 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode2);
    int didokCode3 = 1218772;
    String sloid3 = "ch:1:sloid:" + didokCode3;
    ServicePointNumber servicePointNumber3 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode3);
    int didokCode4 = 1318771;
    String sloid4 = "ch:1:sloid:" + didokCode4;
    ServicePointNumber servicePointNumber4 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode4);
    int didokCode5 = 1318772;
    String sloid5 = "ch:1:sloid:" + didokCode5;
    ServicePointNumber servicePointNumber5 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode5);
    int didokCode6 = 1418771;
    String sloid6 = "ch:1:sloid:" + didokCode6;
    ServicePointNumber servicePointNumber6 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode6);
    int didokCode7 = 1418772;
    String sloid7 = "ch:1:sloid:" + didokCode7;
    ServicePointNumber servicePointNumber7 = ServicePointNumber.ofNumberWithoutCheckDigit(didokCode7);
    //when
    List<ItemImportResult> itemImportResults = servicePointImportService.importServicePoints(
        servicePointCsvModelContainers);
    LocalDateTime now = LocalDateTime.now();

    //then
    assertThat(itemImportResults).hasSize(8);
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSloid()).isEqualTo(sloid);
    assertThat(result.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result1 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber1);
    assertThat(result1).hasSize(1);
    assertThat(result1.get(0).getSloid()).isEqualTo(sloid1);
    assertThat(result1.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result2 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber2);
    assertThat(result2).hasSize(1);
    assertThat(result2.get(0).getSloid()).isEqualTo(sloid2);
    assertThat(result2.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result3 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber3);
    assertThat(result3).hasSize(1);
    assertThat(result3.get(0).getSloid()).isEqualTo(sloid3);
    assertThat(result3.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result4 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber4);
    assertThat(result4).hasSize(1);
    assertThat(result4.get(0).getSloid()).isEqualTo(sloid4);
    assertThat(result4.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result5 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber5);
    assertThat(result5).hasSize(1);
    assertThat(result5.get(0).getSloid()).isEqualTo(sloid5);
    assertThat(result5.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result6 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber6);
    assertThat(result6).hasSize(1);
    assertThat(result6.get(0).getSloid()).isEqualTo(sloid6);
    assertThat(result6.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
    List<ServicePointVersion> result7 = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber7);
    assertThat(result7).hasSize(1);
    assertThat(result7.get(0).getSloid()).isEqualTo(sloid7);
    assertThat(result7.get(0).getEditionDate().toLocalDate()).isEqualTo(now.toLocalDate());
  }

  private List<ServicePointCsvModelContainer> getContainersForUicCodes11To14WithAndWithoutSloid() {
    LocalDateTime editedAt = LocalDateTime.of(2020, 1, 1, 22, 22);
    int numberForUic11WithoutSloid = 1118771;
    int numberForUic11WithSloid = 1118772;
    String sloidForUic11 = "ch:1:sloid:" + numberForUic11WithSloid;
    int uicCode11 = 11;
    ServicePointCsvModel servicePointWithUicCode11AndSloidNull = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern11NoSloid, Wyleregg")
        .bezeichnungOffiziell("Bern11NoSloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic11WithoutSloid)
        .laendercode(uicCode11)
        .status(0)
        .abkuerzung("TEST1")
        .didokCode(numberForUic11WithoutSloid)
        .sloid(null)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    ServicePointCsvModel servicePointWithUicCode11AndSloid = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern11Sloid, Wyleregg")
        .bezeichnungOffiziell("Bern11Sloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic11WithSloid)
        .laendercode(uicCode11)
        .status(0)
        .abkuerzung("TEST2")
        .didokCode(numberForUic11WithSloid)
        .sloid(sloidForUic11)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    int numberForUic12WithoutSloid = 1218771;
    int numberForUic12WithSloid = 1218772;
    String sloidForUic12 = "ch:1:sloid:" + numberForUic12WithSloid;
    int uicCode12 = 12;
    ServicePointCsvModel servicePointWithUicCode12AndSloidNull = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern12NoSloid, Wyleregg")
        .bezeichnungOffiziell("Bern12NoSloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic12WithoutSloid)
        .laendercode(uicCode12)
        .status(0)
        .abkuerzung("TEST3")
        .didokCode(numberForUic12WithoutSloid)
        .sloid(null)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    ServicePointCsvModel servicePointWithUicCode12AndSloid = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern12Sloid, Wyleregg")
        .bezeichnungOffiziell("Bern12Sloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic12WithSloid)
        .laendercode(uicCode12)
        .status(0)
        .abkuerzung("TEST4")
        .didokCode(numberForUic12WithSloid)
        .sloid(sloidForUic12)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    int numberForUic13WithoutSloid = 1318771;
    int numberForUic13WithSloid = 1318772;
    String sloidForUic13 = "ch:1:sloid:" + numberForUic13WithSloid;
    int uicCode13 = 13;
    ServicePointCsvModel servicePointWithUicCode13AndSloidNull = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern13NoSloid, Wyleregg")
        .bezeichnungOffiziell("Bern13NoSloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic13WithoutSloid)
        .laendercode(uicCode13)
        .status(0)
        .abkuerzung("TEST5")
        .didokCode(numberForUic13WithoutSloid)
        .sloid(null)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    ServicePointCsvModel servicePointWithUicCode13AndSloid = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern13Sloid, Wyleregg")
        .bezeichnungOffiziell("Bern13Sloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic13WithSloid)
        .laendercode(uicCode13)
        .status(0)
        .abkuerzung("TEST6")
        .didokCode(numberForUic13WithSloid)
        .sloid(sloidForUic13)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    int numberForUic14WithoutSloid = 1418771;
    int numberForUic14WithSloid = 1418772;
    String sloidForUic14 = "ch:1:sloid:" + numberForUic14WithSloid;
    int uicCode14 = 14;
    ServicePointCsvModel servicePointWithUicCode14AndSloidNull = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern14NoSloid, Wyleregg")
        .bezeichnungOffiziell("Bern14NoSloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic14WithoutSloid)
        .laendercode(uicCode14)
        .status(0)
        .abkuerzung("TEST7")
        .didokCode(numberForUic14WithoutSloid)
        .sloid(null)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    ServicePointCsvModel servicePointWithUicCode14AndSloid = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern14Sloid, Wyleregg")
        .bezeichnungOffiziell("Bern14Sloid, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(numberForUic14WithSloid)
        .laendercode(uicCode14)
        .status(0)
        .abkuerzung("TEST8")
        .didokCode(numberForUic14WithSloid)
        .sloid(sloidForUic14)
        .comment("BAV-Kommentar")
        .editedAt(editedAt)
        .build();
    List<ServicePointCsvModel> modelList11 = new ArrayList<>();
    modelList11.add(servicePointWithUicCode11AndSloidNull);
    List<ServicePointCsvModel> modelList11Sloid = new ArrayList<>();
    modelList11Sloid.add(servicePointWithUicCode11AndSloid);
    List<ServicePointCsvModel> modelList12 = new ArrayList<>();
    modelList12.add(servicePointWithUicCode12AndSloidNull);
    List<ServicePointCsvModel> modelList12Sloid = new ArrayList<>();
    modelList12Sloid.add(servicePointWithUicCode12AndSloid);
    List<ServicePointCsvModel> modelList13 = new ArrayList<>();
    modelList13.add(servicePointWithUicCode13AndSloidNull);
    List<ServicePointCsvModel> modelList13Sloid = new ArrayList<>();
    modelList13Sloid.add(servicePointWithUicCode13AndSloid);
    List<ServicePointCsvModel> modelList14 = new ArrayList<>();
    modelList14.add(servicePointWithUicCode14AndSloidNull);
    List<ServicePointCsvModel> modelList14Sloid = new ArrayList<>();
    modelList14Sloid.add(servicePointWithUicCode14AndSloid);

    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList11);
    container.setDidokCode(numberForUic11WithoutSloid);
    ServicePointCsvModelContainer container1 = new ServicePointCsvModelContainer();
    container1.setServicePointCsvModelList(modelList11Sloid);
    container1.setDidokCode(numberForUic11WithSloid);
    ServicePointCsvModelContainer container2 = new ServicePointCsvModelContainer();
    container2.setServicePointCsvModelList(modelList12);
    container2.setDidokCode(numberForUic12WithoutSloid);
    ServicePointCsvModelContainer container3 = new ServicePointCsvModelContainer();
    container3.setServicePointCsvModelList(modelList12Sloid);
    container3.setDidokCode(numberForUic12WithSloid);
    ServicePointCsvModelContainer container4 = new ServicePointCsvModelContainer();
    container4.setServicePointCsvModelList(modelList13);
    container4.setDidokCode(numberForUic13WithoutSloid);
    ServicePointCsvModelContainer container5 = new ServicePointCsvModelContainer();
    container5.setServicePointCsvModelList(modelList13Sloid);
    container5.setDidokCode(numberForUic13WithSloid);
    ServicePointCsvModelContainer container6 = new ServicePointCsvModelContainer();
    container6.setServicePointCsvModelList(modelList14);
    container6.setDidokCode(numberForUic14WithoutSloid);
    ServicePointCsvModelContainer container7 = new ServicePointCsvModelContainer();
    container7.setServicePointCsvModelList(modelList14Sloid);
    container7.setDidokCode(numberForUic14WithSloid);

    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(container);
    servicePointCsvModelContainers.add(container1);
    servicePointCsvModelContainers.add(container2);
    servicePointCsvModelContainers.add(container3);
    servicePointCsvModelContainers.add(container4);
    servicePointCsvModelContainers.add(container5);
    servicePointCsvModelContainers.add(container6);
    servicePointCsvModelContainers.add(container7);
    return servicePointCsvModelContainers;
  }

}
