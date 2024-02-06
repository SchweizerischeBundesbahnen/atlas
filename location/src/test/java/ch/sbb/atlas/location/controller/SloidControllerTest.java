package ch.sbb.atlas.location.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.service.SloidService;
import ch.sbb.atlas.location.service.SloidSyncService;
import ch.sbb.atlas.servicepoint.SloidNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SloidControllerTest {

  @Mock
  private SloidService sloidService;
  @Mock
  private SloidSyncService sloidSyncService;

  private SloidController sloidController;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    sloidController = new SloidController(sloidService, sloidSyncService);
  }

  @Test
  void shouldThrowWhenSloidNotValidTypeServicePoint() {
    // given
    ClaimSloidRequestModel requestModel = new ClaimSloidRequestModel(SloidType.SERVICE_POINT, "ch:1:sloid:7000:1");

    // when, then
    assertThrows(SloidNotValidException.class, () -> sloidController.claimSloid(requestModel));
  }

  @Test
  void shouldThrowWhenSloidNotValidTypePlatform() {
    // given
    ClaimSloidRequestModel requestModel = new ClaimSloidRequestModel(SloidType.PLATFORM, "ch:1:sloid:7000:1");

    // when, then
    assertThrows(SloidNotValidException.class, () -> sloidController.claimSloid(requestModel));
  }

  @ParameterizedTest
  @EnumSource(names = {"AREA", "TOILET", "REFERENCE_POINT", "PARKING_LOT", "INFO_DESK", "TICKET_COUNTER"})
  void shouldThrowWhenSloidNotValidTypeArea(SloidType sloidType) {
    // given
    ClaimSloidRequestModel requestModel = new ClaimSloidRequestModel(sloidType, "ch:1:sloid:7000");

    // when, then
    assertThrows(SloidNotValidException.class, () -> sloidController.claimSloid(requestModel));
  }

}
