package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.model.StopPointRequestParams;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.search.StopPointSearchRestrictions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@Transactional
class StopPointVersionServiceTest {

  private static final String SLOID = "ch:1:sloid:70000";

  private final StopPointService stopPointService;
  private final StopPointRepository stopPointRepository;

  @Autowired
  StopPointVersionServiceTest(StopPointService stopPointService, StopPointRepository stopPointRepository) {
    this.stopPointService = stopPointService;
    this.stopPointRepository = stopPointRepository;
  }

  @Test
  void shouldThrowExceptionWhenStopPointDoesNotExist() {
    assertThrows(StopPointDoesNotExistException.class,
        () -> stopPointService.checkStopPointExists(SLOID)).getLocalizedMessage();
  }

  @Test
  void shouldNotThrowExceptionWhenStopPointExist() {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when && then
    assertDoesNotThrow(() -> stopPointService.checkStopPointExists(version.getSloid()));
  }

  @Test
  void shouldGetStopPointByNumber() {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .numbers(List.of(version.getNumber().getNumber())).build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getNumber()).isEqualTo(version.getNumber());
  }

  @Test
  void shouldNotGetStopPointByNumber() {
    //given
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .numbers(List.of(7654321)).build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  void shouldGetStopPointByNumbers() {
    //given
    StopPointVersion stopPoint1 = stopPointRepository.save(StopPointTestData.builderVersion1().build());
    StopPointVersion stopPoint2 =
        stopPointRepository.save(StopPointTestData.builderVersion2()
            .number(ServicePointNumber.ofNumberWithoutCheckDigit(7654321))
            .build());
    StopPointVersion stopPoint3 =
        stopPointRepository.save(StopPointTestData.builderVersion3()
            .number(ServicePointNumber.ofNumberWithoutCheckDigit(1472583))
            .build());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .numbers(List.of(stopPoint1.getNumber().getNumber(),
            stopPoint2.getNumber().getNumber()))
        .build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(stopPoint1);
    assertThat(result.getContent()).contains(stopPoint2);
    assertThat(result.getContent()).doesNotContain(stopPoint3);
  }

  @Test
  void shouldGetStopPointBySloid() {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .sloids(List.of(version.getSloid())).build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getSloid()).isEqualTo(version.getSloid());
  }

  @Test
  void shouldNotGetStopPointBySloid() {
    //given
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .sloids(List.of("ch:1:sloid:1536842")).build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }

  @Test
  void shouldGetStopPointBySloids() {
    //given
    StopPointVersion stopPoint1 = stopPointRepository.save(StopPointTestData.builderVersion1().build());
    StopPointVersion stopPoint2 =
        stopPointRepository.save(StopPointTestData.builderVersion2()
            .number(ServicePointNumber.ofNumberWithoutCheckDigit(7654321))
            .sloid("ch:1:sloid:7654321")
            .build());
    StopPointVersion stopPoint3 =
        stopPointRepository.save(StopPointTestData.builderVersion3()
            .number(ServicePointNumber.ofNumberWithoutCheckDigit(1472583))
            .sloid("ch:1:sloid:1472583")
            .build());
    //when
    StopPointRequestParams params = StopPointRequestParams.builder()
        .sloids(List.of(stopPoint1.getSloid(),stopPoint2.getSloid()))
        .build();
    StopPointSearchRestrictions restrictions = StopPointSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .stopPointRequestParams(params)
        .build();
    Page<StopPointVersion> result = stopPointService.findAll(restrictions);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).contains(stopPoint1);
    assertThat(result.getContent()).contains(stopPoint2);
    assertThat(result.getContent()).doesNotContain(stopPoint3);
  }

}
