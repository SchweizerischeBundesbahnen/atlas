package ch.sbb.atlas.location.module.sloid.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.SloidLocationModel;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.module.sloid.model.SloidLocation;
import ch.sbb.atlas.location.module.sloid.service.SloidService;
import ch.sbb.atlas.location.module.sloid.service.SloidSyncService;
import ch.sbb.atlas.servicepoint.SloidNotValidException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SloidControllerTest {

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

  @Test
  void shouldGetSloid() {
    // given
    when(sloidService.getSloid("ch:1:sloid:1")).thenReturn(List.of(new SloidLocation("ch:1:sloid:1", SloidType.PLATFORM)));

    // when, then
    List<SloidLocationModel> result = sloidController.getSloid("ch:1:sloid:1");
    assertThat(result).hasSize(1);
  }

  @ParameterizedTest
  @EnumSource(names = {"AREA", "TOILET", "REFERENCE_POINT", "PARKING_LOT", "CONTACT_POINT"})
  void shouldThrowWhenSloidNotValidTypeArea(SloidType sloidType) {
    // given
    ClaimSloidRequestModel requestModel = new ClaimSloidRequestModel(sloidType, "ch:1:sloid:7000");

    // when, then
    assertThrows(SloidNotValidException.class, () -> sloidController.claimSloid(requestModel));
  }

}
