package ch.sbb.atlas.api.prm.model.platform;

import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.ACOUSTIC_INFORMATION;
import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE;
import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.TEXT_TO_SPEECH_DEPARTURES;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.atlas.model.Status;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlatformVersionModelTest extends BaseValidatorTest {

  @Test
  void shouldReturnInfoOpportunitiesAsList() {
    PlatformVersionModel platformVersionModel = new PlatformVersionModel();
    assertThat(platformVersionModel.getInfoOpportunities()).isNotNull().isEmpty();
  }

  @Test
  void shouldReturnInfoOpportunitiesAsListWithEntries() {
    PlatformVersionModel platformVersionModel = PlatformVersionModel.builder()
        .infoOpportunities(List.of(InfoOpportunityAttributeType.ACOUSTIC_INFORMATION,
            InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE))
        .build();
    assertThat(platformVersionModel.getInfoOpportunities()).isNotNull().hasSize(2);
  }

  @Test
  void shouldOnlyAllowPositiveHeight() {
    PlatformVersionModel platformVersionModel = buildValidPlatform();

    Set<ConstraintViolation<PlatformVersionModel>> result = validator.validate(platformVersionModel);
    assertThat(result).isEmpty();

    platformVersionModel.setHeight(-1.0);
    result = validator.validate(platformVersionModel);
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldOnlyAllowWheelchairAreaLength() {
    PlatformVersionModel platformVersionModel = buildValidPlatform();
    platformVersionModel.setWheelchairAreaLength(-1.0);
    Set<ConstraintViolation<PlatformVersionModel>> result = validator.validate(platformVersionModel);
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldOnlyAllowWheelchairAreaWidth() {
    PlatformVersionModel platformVersionModel = buildValidPlatform();
    platformVersionModel.setWheelchairAreaWidth(-1.0);
    Set<ConstraintViolation<PlatformVersionModel>> result = validator.validate(platformVersionModel);
    assertThat(result).hasSize(1);
  }

  private static PlatformVersionModel buildValidPlatform() {
    return PlatformVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional")
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(List.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();
  }
}