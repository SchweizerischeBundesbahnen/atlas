package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ServicePointSearchVersionRepositoryTest {

  private final ServicePointSearchVersionRepository servicePointSearchVersionRepository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  ServicePointSearchVersionRepositoryTest(ServicePointSearchVersionRepository servicePointSearchVersionRepository,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointSearchVersionRepository = servicePointSearchVersionRepository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void initData() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    bernWyleregg.setDesignationLong("Wyleregg Napoli");
    servicePointVersionRepository.save(bernWyleregg);
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setDesignationLong("Bern Napoli");
    servicePointVersionRepository.save(bern);
    ServicePointVersion bernOst = ServicePointTestData.getBernOst();
    bernOst.setDesignationLong("Ost Napol");
    servicePointVersionRepository.save(bernOst);
    servicePointVersionRepository.save(ServicePointTestData.createServicePointVersionWithCountryBorder());
  }

  @AfterEach
  void tearDown() {
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldThrowExceptionWhenSearchWithLessThanTwoDigits() {
    //when & then
    assertThrows(IllegalArgumentException.class, () -> servicePointSearchVersionRepository.searchServicePoints("b"));
  }

  @Test
  void shouldReturnAllServicePointWithDesignationOfficialBern() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("bern");
    //then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(3);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern");
    assertThat(results.get(1).getDesignationOfficial()).isEqualTo("Bern Ost (Spw)");
    assertThat(results.get(2).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldReturnAllServicePointWithDesignationOfficialBernAndRouteNetworkTrue() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePointsWithRouteNetworkTrue("bern");
    //then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(2);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern Ost (Spw)");
    assertThat(results.get(1).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldReturnAllServicePointWithDesignationOfficialContainsOstAndRouteNetworkTrue() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePointsWithRouteNetworkTrue("ost");
    //then
    assertThat(results).isNotNull().hasSize(1);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern Ost (Spw)");
  }

  @Test
  void shouldReturnAllServicePointWithDesignationOfficialEndsWithEgg() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("egg");
    //then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldReturnOnlyAllSwissOnlyServicePoint() {
    //given
    servicePointVersionRepository.save(ServicePointTestData.createAbroadServicePointVersion());
    servicePointVersionRepository.save(ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion());

    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchSwissOnlyStopPointServicePoints("egg");
    //then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldReturnEmptyListWhenNoMatch() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("milan");
    //then
    assertThat(results).isNotNull();
    assertThat(results).isEmpty();
  }

  @Test
  void shouldReturnAllServicePointWithNumberStartWith85() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("85");
    //then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(4);
    assertThat(results.get(0).getNumber()).isEqualTo(8500925);
    assertThat(results.get(1).getNumber()).isEqualTo(8507000);
    assertThat(results.get(2).getNumber()).isEqualTo(8519761);
    assertThat(results.get(3).getNumber()).isEqualTo(8589008);
  }

  @Test
  void shouldReturnAllServicePointWithNumberEndsWith85() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("61");
    //then
    assertThat(results).isNotNull().hasSize(1);
    assertThat(results.get(0).getNumber()).isEqualTo(8519761);
  }

  @Test
  void shouldReturnAllServicePointWithNumberContains7000() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("7000");
    //then
    assertThat(results).isNotNull().hasSize(1);
    assertThat(results.get(0).getNumber()).isEqualTo(8507000);
  }

  @Test
  void shouldReturnAllServicePointWithDesignationLongNapoli() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("napoli");
    //then
    assertThat(results).isNotNull().hasSize(2);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern");
    assertThat(results.get(1).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldReturnAllServicePointWithDesignationLongNapol() {
    //when
    List<ServicePointSearchResult> results = servicePointSearchVersionRepository.searchServicePoints("napol");
    //then
    assertThat(results).isNotNull().hasSize(3);
    assertThat(results.get(0).getDesignationOfficial()).isEqualTo("Bern");
    assertThat(results.get(1).getDesignationOfficial()).isEqualTo("Bern Ost (Spw)");
    assertThat(results.get(2).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldEscapePercent() {
    //when
    String results = servicePointSearchVersionRepository.sanitizeValue("Be%rn");
    //then
    assertThat(results).isNotNull();
    assertThat(results).isEqualTo("Be\\%rn");
  }

  @Test
  void shouldRemoveIfStringContainsOnlyDigits() {
    //when
    String results = servicePointSearchVersionRepository.sanitizeValue("85 07000");
    //then
    assertThat(results).isNotNull();
    assertThat(results).isEqualTo("8507000");
  }

}