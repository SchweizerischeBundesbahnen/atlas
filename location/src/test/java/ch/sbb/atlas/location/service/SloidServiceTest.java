package ch.sbb.atlas.location.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.LocationSchemaCreation;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@LocationSchemaCreation
class SloidServiceTest {

  private final SloidRepository sloidRepository;
  private final SloidService sloidService;

  @Autowired
  SloidServiceTest(SloidRepository sloidRepository, SloidService sloidService) {
    this.sloidRepository = sloidRepository;
    this.sloidService = sloidService;
  }

  @BeforeEach
  void init() {
    String sloid = "ch:1:sloid:7000";
    sloidService.claimAvailableServicePointSloid(sloid);
  }

  @Test
  void shouldClaimAvailableServicePointSloidWhenAvailable() {
    //given
    String sloid = "ch:1:sloid:7001";
    //when
    boolean result = sloidService.claimAvailableServicePointSloid(sloid);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotClaimAvailableServicePointSloidWhenNotAvailable() {
    //given
    String sloid = "ch:1:sloid:7000";
    sloidService.claimAvailableServicePointSloid(sloid);
    //when
    boolean result = sloidService.claimAvailableServicePointSloid(sloid);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldClaimSloidWhenAvailable() {
    //given
    String sloid = "ch:1:sloid:7001:0:1";
    //when
    boolean result = sloidService.claimSloid(sloid, SloidType.PLATFORM);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotClaimSloidWhenNotAvailable() {
    //given
    String sloid = "ch:1:sloid:7000:0:1";
    sloidService.claimSloid(sloid, SloidType.PLATFORM);
    //when
    boolean result = sloidService.claimSloid(sloid, SloidType.PLATFORM);
    //then
    assertThat(result).isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = SloidType.class, names = {"AREA", "REFERENCE_POINT", "PARKING_LOT", "INFO_DESK",
      "TICKET_COUNTER",
      "TOILET"})
  void shouldGenerateNewSloid(SloidType sloidType) {
    //given
    String sloidPrefix = "ch:1:sloid:7000";
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, sloidType);
    //then
    assertThat(result).isEqualTo("ch:1:sloid:7000:100");
  }

  @Test
  void shouldGenerateNewSloidPlatformCase() {
    //given
    String sloidPrefix = "ch:1:sloid:7000:0";
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, SloidType.PLATFORM);
    //then
    assertThat(result).isEqualTo("ch:1:sloid:7000:0:1");
  }

  @ParameterizedTest
  @EnumSource(value = SloidType.class, names = {"AREA", "REFERENCE_POINT", "PARKING_LOT", "INFO_DESK",
      "TICKET_COUNTER",
      "TOILET"})
  void shouldGenerateNewSloidEvenWhenSloidAlreadyOccupied(SloidType sloidType) {
    //given
    String sloidPrefix = "ch:1:sloid:7000";
    int nextSeqValue = sloidRepository.getNextSeqValue(sloidType) + 1;
    sloidRepository.insertSloid(sloidPrefix + ":" + nextSeqValue, sloidType);
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, sloidType);
    //then
    assertThat(result).isEqualTo("ch:1:sloid:7000:102");
  }

  @Test
  void shouldGenerateNewSloidEvenWhenSloidAlreadyOccupiedPlatformCase() {
    //given
    String sloidPrefix = "ch:1:sloid:7000:0";
    int nextSeqValue = sloidRepository.getNextSeqValue(SloidType.PLATFORM) + 1;
    sloidRepository.insertSloid(sloidPrefix + ":" + nextSeqValue, SloidType.PLATFORM);
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, SloidType.PLATFORM);
    //then
    assertThat(result).isEqualTo("ch:1:sloid:7000:0:3");
  }

  @Test
  void shouldGetNextAvailableServicePointSloid() {
    //given
    sloidRepository.setAvailableSloidToClaimed("ch:1:sloid:1");
    //when
    String result = sloidService.getNextAvailableServicePointSloid(Country.SWITZERLAND);
    //then
    assertThat(result).isNotNull();
  }

}
