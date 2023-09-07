package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
@Transactional
public class LoadingPointServiceTest {

  private final ServicePointVersionRepository servicePointVersionRepository;

  private final LoadingPointVersionRepository loadingPointVersionRepository;

  private final LoadingPointService loadingPointService;

  @MockBean
  private CrossValidationService crossValidationServiceMock;

  @Autowired
  public LoadingPointServiceTest(ServicePointVersionRepository servicePointVersionRepository,
      LoadingPointVersionRepository loadingPointVersionRepository,
      LoadingPointService loadingPointService) {
    this.servicePointVersionRepository = servicePointVersionRepository;
    this.loadingPointVersionRepository = loadingPointVersionRepository;
    this.loadingPointService = loadingPointService;
  }

  @Test
  void shouldSaveWithValidation() {
    // given
    doNothing().when(crossValidationServiceMock).validateServicePointNumberExists(any());
    LoadingPointVersion loadingPointVersion = LoadingPointVersion.builder()
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .validFrom(LocalDate.of(2014, 12, 10))
        .validTo(LocalDate.of(2021, 3, 15))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("Design")
        .number(1)
        .build();

    // when
    loadingPointService.save(loadingPointVersion);

    // then
    verify(crossValidationServiceMock, times(1))
        .validateServicePointNumberExists(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(loadingPointVersionRepository.findAll()).hasSize(1);
  }

  @Test
  public void shouldGetLoadingPointByNumber() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber = 77777;
    LoadingPointVersion loadingPointVersion = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .numbers(List.of(loadingPointNumber)).build();
    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getNumber()).isEqualTo(loadingPointNumber);

  }

  @Test
  public void shouldNotGetLoadingPointByNumber() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber = 77777;
    LoadingPointVersion loadingPointVersion = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .numbers(List.of(66666)).build();
    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);

  }

  @Test
  public void shouldGetLoadingPointByNumbers() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    int loadingPointNumber3 = 77779;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .numbers(List.of(loadingPointNumber1, loadingPointNumber2)).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByServicePointSloid() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointSloids(List.of(servicePointVersion.getSloid())).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
  }

  @Test
  public void shouldNotGetLoadingPointByServicePointSloid() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointSloids(List.of("ch:1:sloid:76237:1")).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  public void shouldGetLoadingPointByServicePointSloids() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);

    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    ServicePointVersion servicePointVersionWithCountryBorder = ServicePointTestData.createServicePointVersion();
    servicePointVersionWithCountryBorder.setValidFrom(LocalDate.of(2014, 12, 14));
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(servicePointVersionWithCountryBorder);
    int loadingPointNumber3 = 77779;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointSloids(List.of(servicePointVersion.getSloid(), servicePointVersion1.getSloid())).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByServicePointUicCountryCode() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointUicCountryCode(servicePointVersion.getCountry().getUicCode()).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).contains(loadingPointVersion1);
  }

  @Test
  public void shouldGetLoadingPointByServicePointUicCountryCodes() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointUicCountryCodes(List.of(servicePointVersion.getCountry().getUicCode()
            , servicePointVersion1.getCountry().getUicCode())
        ).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
  }

  @Test
  public void shouldNotGetLoadingPointByServicePointUicCountryCode() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointUicCountryCode(25).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  public void shouldGetLoadingPointByServicePointNumberShort() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumbersShort(servicePointVersion.getNumber().getNumberShort()).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).contains(loadingPointVersion1);
  }

  @Test
  public void shouldNotGetLoadingPointByServicePointNumberShort() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);

    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumbersShort(80000).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  public void shouldGetLoadingPointByServicePointNumberShorts() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumbersShorts(
            List.of(servicePointVersion.getNumber().getNumberShort()
                , servicePointVersion1.getNumber().getNumberShort()
            )).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByServicePointNumber() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumber(servicePointVersion.getNumber().getNumber()).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).contains(loadingPointVersion1);
  }

  @Test
  public void shouldNotGetLoadingPointByServicePointNumber() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);

    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumber(8000007).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  public void shouldGetLoadingPointByServicePointNumbers() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumbers(
            List.of(loadingPointVersion1.getServicePointNumber().getNumber()
                , loadingPointVersion2.getServicePointNumber().getNumber()
            )).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointBySboid() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .sboid(servicePointVersion.getBusinessOrganisation()).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
  }

  @Test
  public void shouldGetLoadingPointBySboids() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .sboids(List.of("ch:1:sboid:100626")).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByValidFrom() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2013, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDate validFrom = LocalDate.of(2014, 12, 14);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .fromDate(validFrom).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByValidTo() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2013, 12, 14))
        .validTo(LocalDate.of(2022, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDate validTo = LocalDate.of(2021, 3, 31);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .toDate(validTo).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByValidOn() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2016, 12, 14))
        .validTo(LocalDate.of(2022, 3, 31))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDate validOn = LocalDate.of(2015, 12, 14);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .validOn(validOn).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

  @Test
  public void shouldGetLoadingPointByCreateAt() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .creationDate(LocalDateTime.of(2014, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .creationDate(LocalDateTime.of(2015, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2016, 12, 14))
        .validTo(LocalDate.of(2022, 3, 31))
        .creationDate(LocalDateTime.of(2016, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDateTime createdAt = LocalDateTime.of(2015, 12, 14, 10, 10, 10);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .createdAfter(createdAt).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).contains(loadingPointVersion3);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion1);
  }

  @Test
  public void shouldGetLoadingPointByModifiedAt() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .editionDate(LocalDateTime.of(2014, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .editionDate(LocalDateTime.of(2015, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2016, 12, 14))
        .validTo(LocalDate.of(2022, 3, 31))
        .editionDate(LocalDateTime.of(2016, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDateTime modifiedAfter = LocalDateTime.of(2015, 12, 14, 10, 10, 10);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .modifiedAfter(modifiedAfter).build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).contains(loadingPointVersion3);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion1);
  }

  @Test
  public void shouldGetLoadingPointByMultipleFilter() {
    //given
    ServicePointVersion servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
    int loadingPointNumber1 = 77777;
    LoadingPointVersion loadingPointVersion1 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .editionDate(LocalDateTime.of(2014, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber1)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion1);
    ServicePointVersion servicePointVersion1 = servicePointVersionRepository.save(
        ServicePointTestData.createAbroadServicePointVersion());
    int loadingPointNumber2 = 77778;
    LoadingPointVersion loadingPointVersion2 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion1.getNumber())
        .validFrom(LocalDate.of(2015, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .editionDate(LocalDateTime.of(2015, 12, 14, 10, 10, 10))
        .creationDate(LocalDateTime.of(2015, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber2)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion2);
    ServicePointVersion servicePointVersion2 = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersion());
    int loadingPointNumber3 = 77711;
    LoadingPointVersion loadingPointVersion3 = LoadingPointVersion.builder()
        .servicePointNumber(servicePointVersion2.getNumber())
        .validFrom(LocalDate.of(2016, 12, 14))
        .validTo(LocalDate.of(2022, 3, 31))
        .editionDate(LocalDateTime.of(2016, 12, 14, 10, 10, 10))
        .connectionPoint(true)
        .designationLong("DesignLong")
        .designation("design")
        .number(loadingPointNumber3)
        .build();
    loadingPointVersionRepository.saveAndFlush(loadingPointVersion3);

    //when
    LocalDateTime modifiedAfter = LocalDateTime.of(2015, 12, 14, 10, 10, 10);
    LocalDateTime createdAfter = LocalDateTime.of(2015, 12, 14, 10, 10, 10);
    LoadingPointElementRequestParams params = LoadingPointElementRequestParams.builder()
        .servicePointNumber(loadingPointVersion2.getServicePointNumber().getNumber())
        .servicePointSloid(servicePointVersion1.getSloid())
        .number(loadingPointNumber2)
        .sboid(servicePointVersion1.getBusinessOrganisation())
        .servicePointNumbersShort(servicePointVersion1.getNumber().getNumberShort())
        .servicePointUicCountryCode(servicePointVersion1.getNumber().getUicCountryCode())
        .validOn(LocalDate.of(2016, 12, 15))
        .fromDate(LocalDate.of(2015, 12, 14))
        .toDate(LocalDate.of(2021, 3, 31))
        .modifiedAfter(modifiedAfter)
        .createdAfter(createdAfter)
        .build();

    LoadingPointSearchRestrictions restrictions = LoadingPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .loadingPointElementRequestParams(params)
        .build();
    Page<LoadingPointVersion> result = loadingPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).contains(loadingPointVersion2);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion1);
    assertThat(result.getContent()).doesNotContain(loadingPointVersion3);
  }

}
