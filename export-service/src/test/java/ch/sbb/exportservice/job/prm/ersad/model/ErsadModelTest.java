package ch.sbb.exportservice.job.prm.ersad.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErsadModelTest {

  @Test
  void shouldExposeImmutableQuayList() {
    // Given
    List<ErsadQuay> mutableQuays = new ArrayList<>();
    mutableQuays.add(new ErsadQuay("ch:1:sloid:100:0:3", "3", null, null, null, null, null, null, null));
    ErsadStation station = new ErsadStation(
        "ch:1:sloid:100", 8500100, "Tavannes", 7.20164538962, 47.21984466031,
        null, null, null, null, null, null,
        mutableQuays, null, null, null, null);

    // When
    mutableQuays.add(new ErsadQuay("ch:1:sloid:100:0:4", "4", null, null, null, null, null, null, null));

    // Then
    assertThat(station.quays()).hasSize(1);
    ErsadQuay ersadQuay = new ErsadQuay("x", "x", null, null, null, null, null, null, null);
    assertThatThrownBy(() -> station.quays().add(ersadQuay))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldDefaultNullListsToEmptyImmutableLists() {
    // Given
    // When
    ErsadStation station = new ErsadStation(
        "ch:1:sloid:100", 8500100, "Tavannes", 7.20164538962, 47.21984466031,
        null, null, null, null, null, null,
        null, null, null, null, null);

    // Then
    assertThat(station.meansOfTransport()).isEmpty();
    assertThat(station.quays()).isEmpty();
    assertThat(station.contactPoints()).isEmpty();
    assertThat(station.toilets()).isEmpty();
    assertThat(station.parkings()).isEmpty();
    assertThat(station.pathLinks()).isEmpty();
  }

  @Test
  void shouldExposeAllFieldsViaAccessors() {
    // Given
    ErsadAddress address = new ErsadAddress("Bahnhofplatz 1", "2710", "Tavannes");

    // When
    ErsadStation station = new ErsadStation(
        "ch:1:sloid:100", 8500100, "Tavannes", 7.20164538962, 47.21984466031,
        address, "https://www.sbb.ch", List.of("TRAIN"), NetexTriState.TRUE,
        null, null, List.of(), List.of(), List.of(), List.of(), List.of());

    // Then
    assertThat(station.sloid()).isEqualTo("ch:1:sloid:100");
    assertThat(station.number()).isEqualTo(8500100);
    assertThat(station.address()).isEqualTo(address);
    assertThat(station.accessibleStation()).isEqualTo(NetexTriState.TRUE);
  }

  @Test
  void shouldBeTrainStationWhenMeansOfTransportContainsTrainIgnoringCaseAndWhitespace() {
    // Given
    ErsadStation station = stationWith(List.of("BUS", " train "), null);

    // When
    boolean trainStation = station.isTrainStation();

    // Then
    assertThat(trainStation).isTrue();
  }

  @Test
  void shouldNotBeTrainStationWhenMeansOfTransportLacksTrain() {
    // Given
    ErsadStation station = stationWith(List.of("BUS", "RACK_RAILWAY", " "), null);

    // When
    boolean trainStation = station.isTrainStation();

    // Then
    assertThat(trainStation).isFalse();
    assertThat(stationWith(null, null).isTrainStation()).isFalse();
  }

  @Test
  void shouldFilterTicketCountersAndInformationDesksPreservingOrder() {
    // Given
    ErsadContactPoint counter1 = contactPoint("cp1", ContactPointType.TICKET_COUNTER);
    ErsadContactPoint desk1 = contactPoint("cp2", ContactPointType.INFORMATION_DESK);
    ErsadContactPoint counter2 = contactPoint("cp3", ContactPointType.TICKET_COUNTER);
    ErsadContactPoint desk2 = contactPoint("cp4", ContactPointType.INFORMATION_DESK);
    ErsadStation station = stationWith(null, List.of(counter1, desk1, counter2, desk2));

    // When
    List<ErsadContactPoint> ticketCounters = station.ticketCounters();
    List<ErsadContactPoint> informationDesks = station.informationDesks();

    // Then
    assertThat(ticketCounters).containsExactly(counter1, counter2);
    assertThat(informationDesks).containsExactly(desk1, desk2);
  }

  @Test
  void shouldReturnEmptyContactPointFiltersWhenNoContactPoints() {
    // Given
    ErsadStation station = stationWith(null, null);

    // When
    // Then
    assertThat(station.ticketCounters()).isEmpty();
    assertThat(station.informationDesks()).isEmpty();
  }

  @Test
  void shouldOfferAssistanceOnlyWhenAvailabilityIsYesOrPartially() {
    // Given
    // When
    // Then
    assertThat(assistance(StandardAttributeType.YES).isAssistanceOffered()).isTrue();
    assertThat(assistance(StandardAttributeType.PARTIALLY).isAssistanceOffered()).isTrue();
    assertThat(assistance(StandardAttributeType.NO).isAssistanceOffered()).isFalse();
    assertThat(assistance(StandardAttributeType.TO_BE_COMPLETED).isAssistanceOffered()).isFalse();
    assertThat(assistance(StandardAttributeType.NOT_APPLICABLE).isAssistanceOffered()).isFalse();
    assertThat(assistance(null).isAssistanceOffered()).isFalse();
  }

  private static ErsadStation stationWith(List<String> meansOfTransport, List<ErsadContactPoint> contactPoints) {
    return new ErsadStation(
        "ch:1:sloid:100", 8500100, "Tavannes", null, null,
        null, null, meansOfTransport, null, null, null,
        null, contactPoints, null, null, null);
  }

  private static ErsadContactPoint contactPoint(String sloid, ContactPointType type) {
    return new ErsadContactPoint(sloid, type, null, null, null, null);
  }

  private static ErsadAssistance assistance(StandardAttributeType availability) {
    return new ErsadAssistance(availability, null, null, null, null, null);
  }
}
