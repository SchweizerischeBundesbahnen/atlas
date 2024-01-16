package ch.sbb.prm.directory.service.dataimport;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointImportServiceTest {

  private final StopPointRepository stopPointRepository;

  private final VersionableService versionableService;

  private final StopPointImportService stopPointImportService;
  private final SharedServicePointRepository sharedServicePointRepository;


  @Autowired
  StopPointImportServiceTest(StopPointRepository stopPointRepository, VersionableService versionableService,
      StopPointImportService stopPointImportService, SharedServicePointRepository sharedServicePointRepository) {
    this.stopPointRepository = stopPointRepository;
    this.versionableService = versionableService;
    this.stopPointImportService = stopPointImportService;
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldImportWhenStopPointsDoesNotExists() {
    //given
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:12345\",\"sboids\":[\"ch:1:sboid:100602\"],"
            + "\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:12345")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(StopPointCsvTestData.getStopPointCsvModelContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8512345");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

  @Test
  void shouldImportWhenStopPointsDoesNotHaveValidFromMandatoryField() {
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    List<StopPointCsvModel> stopPointCsvModels = stopPointCsvModelContainer.getStopPointCsvModels();
    stopPointCsvModels.get(0).setValidFrom(null);
    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getItemNumber()).isEqualTo("8512345");
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.FAILED);
  }

  @Test
  void shouldImportWhenMeansOfTransportFieldIsNull() {
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    List<StopPointCsvModel> stopPointCsvModels = stopPointCsvModelContainer.getStopPointCsvModels();
    stopPointCsvModels.get(0).setTransportationMeans(null);
    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getItemNumber()).isEqualTo("8512345");
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.FAILED);
  }

  @Test
  void shouldImportWhenStopPointsExists() {
    //then
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:12345\",\"sboids\":[\"ch:1:sboid:100602\"],"
            + "\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:12345")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8512345));
    stopPointRepository.saveAndFlush(stopPointVersion);

    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8512345");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
  }

  @Test
  void shouldReplaceAndMerge(){
    //given
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:4761\",\"sboids\":[\"ch:1:sboid:100602\"],"
            + "\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:4761")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);

    StopPointVersion dbVersion1 = StopPointVersion.builder()
        .sloid("ch:1:sloid:4761")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8504761))
        .freeText("[Shuttle][Shuttle]")
        .validFrom(LocalDate.of(1900,1,1))
        .validTo(LocalDate.of(2023,8,23))
        .creator("123456")
        .creationDate(LocalDateTime.of(1900,1,1,10,10))
        .editor("123456")
        .editionDate(LocalDateTime.of(1900,1,1,10,10))
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();
    StopPointVersion dbVersion2 = StopPointVersion.builder()
        .sloid("ch:1:sloid:4761")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8504761))
        .freeText("[Shuttle]")
        .validFrom(LocalDate.of(2023,8,24))
        .validTo(LocalDate.of(2099,12,31))
        .creator("123456")
        .creationDate(LocalDateTime.of(1900,2,1,10,10))
        .editor("123456")
        .editionDate(LocalDateTime.of(1900,2,1,10,10))
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();
    stopPointRepository.saveAndFlush(dbVersion1);
    stopPointRepository.saveAndFlush(dbVersion2);

    List<StopPointCsvModelContainer> csvModelContainers = new ArrayList<>();
    StopPointCsvModelContainer container = new StopPointCsvModelContainer();

    StopPointCsvModel stopPointCsvModel = StopPointCsvModel.builder()
        .sloid("ch:1:sloid:4761")
        .didokCode(8504761)
        .freeText("[Shuttle]")
        .validFrom(LocalDate.of(1900,1,1))
        .validTo(LocalDate.of(2099, 12, 31))
        .modifiedAt(LocalDateTime.of(1900, 2, 1, 10, 10))
        .createdAt(LocalDateTime.of(1900, 2, 1, 10, 10))
        .transportationMeans("~B~")
        .build();
    container.setStopPointCsvModels(List.of(stopPointCsvModel));
    container.setDidokCode(stopPointCsvModel.getDidokCode());
    csvModelContainers.add(container);

    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(csvModelContainers);
    //then
    assertThat(result).isNotEmpty();
    List<StopPointVersion> updatedVersions = stopPointRepository.findAllByNumberOrderByValidFrom(
        dbVersion1.getNumber());
    assertThat(updatedVersions).hasSize(1);

  }

}