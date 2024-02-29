package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@IntegrationTest
@Transactional
@Slf4j
class ServicePointImportServiceFromCsvFileTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";
  private static final String SEPARATOR = "/";

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  ServicePointImportServiceFromCsvFileTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
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

}
