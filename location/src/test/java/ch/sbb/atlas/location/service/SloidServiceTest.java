package ch.sbb.atlas.location.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.LocationSchemaCreation;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@LocationSchemaCreation
@Transactional
class SloidServiceTest extends BaseControllerApiTest {

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
  void shouldClaimAvailableSloidWhenAvailable() {
    //given
    String sloid = "ch:1:sloid:7001:0:1";
    //when
    boolean result = sloidService.claimSloid(sloid, SloidType.PLATFORM);
    //then
    assertThat(result).isTrue();

  }

  @Test
  void shouldNotClaimAvailableeWhenNotAvailable() {
    //given
    String sloid = "ch:1:sloid:7000:0:1";
    sloidService.claimSloid(sloid, SloidType.PLATFORM);
    //when
    boolean result = sloidService.claimAvailableServicePointSloid(sloid);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldGenerateNewSloid() {
    //given
    String sloidPrefix = "ch:1:sloid:";
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, SloidType.PLATFORM);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGenerateNewSloidEvenWhenSloidAlreadyOccupied() {
    //given
    String sloidPrefix = "ch:1:sloid:";
    int nextSeqValue = sloidRepository.getNextSeqValue(SloidType.PLATFORM) + 1;
    sloidRepository.insertSloid(sloidPrefix + ":" + nextSeqValue, SloidType.PLATFORM);
    //when
    String result = sloidService.generateNewSloid(sloidPrefix, SloidType.PLATFORM);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGenerateNewServicePointSloid() {
    //when
    String result = sloidService.getNextAvailableServicePointSloid(Country.SWITZERLAND);
    //then
    assertThat(result).isNotNull();
  }


}