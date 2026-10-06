package ch.sbb.exportservice.job.prm.ersad.netex;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class NetexValueMapperTest {

  private static Optional<String> expected(String value) {
    return Optional.ofNullable(value);
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "NO,false", "PARTIALLY,partial", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapEveryStandardAttributeType(StandardAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @Test
  void shouldMapPartiallyToPartial() {
    // Given
    StandardAttributeType value = StandardAttributeType.PARTIALLY;

    // When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).contains("partial");
  }

  @Test
  void shouldOmitToBeCompleted() {
    // Given
    StandardAttributeType value = StandardAttributeType.TO_BE_COMPLETED;

    // When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldOmitNotApplicable() {
    // Given
    StandardAttributeType value = StandardAttributeType.NOT_APPLICABLE;

    // When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldOmitNullValues() {
    // Given / When / Then
    assertThat(NetexValueMapper.limitation((StandardAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.limitation((BooleanOptionalAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.limitation((LevelAccessWheelchairAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.limitation((StepFreeAccessAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.limitation((TactileVisualAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.limitation((VehicleAccessAttributeType) null)).isEmpty();
    assertThat(NetexValueMapper.ramp(null)).isEmpty();
    assertThat(NetexValueMapper.hoist(null)).isEmpty();
    assertThat(NetexValueMapper.assistanceAvailability(null)).isEmpty();
    assertThat(NetexValueMapper.sanitaryFacilityList(null)).contains("toilet");
    assertThat(NetexValueMapper.ticketOffice(null)).isEmpty();
    assertThat(NetexValueMapper.triState(null)).isEmpty();
    assertThat(NetexValueMapper.coordinate(null)).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "NO,false", "TO_BE_COMPLETED,"})
  void shouldMapEveryBooleanOptionalAttributeType(BooleanOptionalAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "YES_WITH_STAFF_ASSISTANCE,partial", "NO,false", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapEveryLevelAccessWheelchairAttributeType(LevelAccessWheelchairAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "YES_WITH_LIFT,true", "YES_WITH_RAMP,true", "NO,false", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapEveryStepFreeAccessAttributeType(StepFreeAccessAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "WITH_REMOTE_CONTROL,true", "PARTIALLY,partial", "NO,false", "TO_BE_COMPLETED,",
      "NOT_APPLICABLE,"})
  void shouldMapEveryTactileVisualAttributeType(TactileVisualAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"PLATFORM_ACCESS_WITHOUT_ASSISTANCE,true", "PLATFORM_ACCESS_WITH_ASSISTANCE,partial",
      "PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED,partial", "PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE,false",
      "TO_BE_COMPLETED,"})
  void shouldMapEveryVehicleAccessAttributeType(VehicleAccessAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.limitation(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"RAMPS,true", "NO,false", "LIFTS,", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapBoardingDeviceToRamp(BoardingDeviceAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.ramp(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"LIFTS,true", "RAMPS,", "NO,false", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapBoardingDeviceToHoist(BoardingDeviceAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.hoist(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"YES,true", "NO,false", "PARTIALLY,", "TO_BE_COMPLETED,", "NOT_APPLICABLE,"})
  void shouldMapBooleanFlagDroppingPartiallyBecauseXsdBooleanHasNoPartialLiteral(StandardAttributeType value,
      String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.booleanFlag(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @Test
  void shouldMapBooleanFlagNullToEmpty() {
    // Given / When
    Optional<String> result = NetexValueMapper.booleanFlag((StandardAttributeType) null);

    // Then
    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({"YES,availableIfBooked", "PARTIALLY,availableAtCertainTimes", "NO,none", "TO_BE_COMPLETED,",
      "NOT_APPLICABLE,"})
  void shouldMapAssistanceAvailability(StandardAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.assistanceAvailability(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"YES,toilet wheelchairAccessToilet", "PARTIALLY,toilet", "NO,toilet", "TO_BE_COMPLETED,toilet",
      "NOT_APPLICABLE,toilet"})
  void shouldMapWheelchairToiletToSanitaryFacilityList(StandardAttributeType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.sanitaryFacilityList(value);

    // Then
    assertThat(result).contains(literal);
  }

  @ParameterizedTest
  @CsvSource({"TICKET_COUNTER,true", "INFORMATION_DESK,"})
  void shouldMapContactPointTypeToTicketOffice(ContactPointType value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.ticketOffice(value);

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @CsvSource({"TRUE,true", "FALSE,false", "PARTIAL,partial", "UNKNOWN,unknown"})
  void shouldMapTriStateToLowerCaseLiteral(NetexTriState value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.triState(value);

    // Then
    assertThat(result).contains(literal);
  }

  @Test
  void shouldReturnEmptyForBlankText() {
    // Given / When / Then
    assertThat(NetexValueMapper.text(null)).isEmpty();
    assertThat(NetexValueMapper.text("  ")).isEmpty();
  }

  @Test
  void shouldTrimText() {
    // Given
    String value = "  Tavannes ";

    // When
    Optional<String> result = NetexValueMapper.text(value);

    // Then
    assertThat(result).contains("Tavannes");
  }

  @ParameterizedTest
  @CsvSource({"TRAIN,rail", "BUS,bus", "TRAM,tram", "BOAT,water", "CABLE_CAR,cableway", "CHAIRLIFT,cableway",
      "CABLE_RAILWAY,funicular", "RACK_RAILWAY,rail", "METRO,metro", "ELEVATOR,lift", "ON_DEMAND,", "UNKNOWN,"})
  void shouldMapEveryMeanOfTransport(MeanOfTransport value, String literal) {
    // Given / When
    Optional<String> result = NetexValueMapper.transportMode(value.name());

    // Then
    assertThat(result).isEqualTo(expected(literal));
  }

  @ParameterizedTest
  @EnumSource(MeanOfTransport.class)
  void shouldCoverEveryMeanOfTransportConstant(MeanOfTransport value) {
    // Given / When
    Optional<String> result = NetexValueMapper.transportMode(" " + value.name().toLowerCase() + " ");

    // Then
    assertThat(result).isEqualTo(NetexValueMapper.transportMode(value.name()));
  }

  @Test
  void shouldOmitInvalidOrBlankTransportMode() {
    // Given / When / Then
    assertThat(NetexValueMapper.transportMode(null)).isEmpty();
    assertThat(NetexValueMapper.transportMode(" ")).isEmpty();
    assertThat(NetexValueMapper.transportMode("SPACESHIP")).isEmpty();
  }

  @Test
  void shouldFormatCoordinateWithElevenDecimals() {
    // Given
    Double longitude = 7.201645389621234;

    // When
    Optional<String> result = NetexValueMapper.coordinate(longitude);

    // Then
    assertThat(result).contains("7.20164538962");
  }

  @Test
  void shouldPadCoordinateToElevenDecimals() {
    // Given
    Double latitude = 47.5;

    // When
    Optional<String> result = NetexValueMapper.coordinate(latitude);

    // Then
    assertThat(result).contains("47.50000000000");
  }
}
