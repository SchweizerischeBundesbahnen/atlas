package ch.sbb.exportservice.job.prm.ersad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.exportservice.job.prm.ersad.accessibility.ErsadAccessibilityDeriver;
import ch.sbb.exportservice.job.prm.ersad.assembler.ErsadStationAssembler;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointGeolocation;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ErsadStationAssemblerTest {

  private ErsadStationAssembler assembler;

  @Mock
  private ErsadAccessibilityDeriver accessibilityDeriver;

  @BeforeEach
  void setUp() {
    openMocks(this);
    assembler = new ErsadStationAssembler(accessibilityDeriver);
  }

  @Test
  void shouldConvertLv95ToWgs84() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);
    CoordinatePair tavannesLv95 = new CoordinateTransformer().transform(
        CoordinatePair.builder()
            .east(7.20164538962)
            .north(47.21984466031)
            .spatialReference(SpatialReference.WGS84)
            .build(),
        SpatialReference.LV95);
    ServicePointVersion servicePoint = servicePoint("ch:1:sloid:8500100", "Tavannes", tavannesLv95.getEast(), tavannesLv95.getNorth());
    when(accessibilityDeriver.derive(any(), any(), any(), any())).thenReturn(NetexTriState.TRUE);

    // When
    List<ErsadStation> stations = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePoint.getSloid(), servicePoint),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.of(2026, Month.SEPTEMBER, 16));

    // Then
    assertThat(stations).singleElement()
        .satisfies(station -> {
          assertThat(station.longitude()).isCloseTo(7.20164538962, within(1e-7));
          assertThat(station.latitude()).isCloseTo(47.21984466031, within(1e-7));
        });
  }

  @Test
  void shouldSkipStationWithoutServicePoint() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);

    // When
    List<ErsadStation> stations = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.now());

    // Then
    assertThat(stations).isEmpty();
  }

  @Test
  void shouldOmitCoordinatesWhenServicePointHasNoGeolocation() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);
    ServicePointVersion servicePointWithoutGeolocation = ServicePointVersion.builder()
        .sloid("ch:1:sloid:8500100")
        .designationOfficial("Tavannes")
        .servicePointGeolocation(null)
        .build();
    ServicePointVersion servicePointWithoutSpatialReference = ServicePointVersion.builder()
        .sloid("ch:1:sloid:8500100")
        .designationOfficial("Tavannes")
        .servicePointGeolocation(ServicePointGeolocation.builder().east(2596624.4).north(1222062.1).spatialReference(null).build())
        .build();
    when(accessibilityDeriver.derive(any(), any(), any(), any())).thenReturn(NetexTriState.UNKNOWN);

    // When
    List<ErsadStation> stationsWithoutGeolocation = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePointWithoutGeolocation.getSloid(), servicePointWithoutGeolocation),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.now());
    List<ErsadStation> stationsWithoutSpatialReference = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePointWithoutSpatialReference.getSloid(), servicePointWithoutSpatialReference),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.now());

    // Then
    assertThat(stationsWithoutGeolocation).singleElement()
        .satisfies(station -> {
          assertThat(station.longitude()).isNull();
          assertThat(station.latitude()).isNull();
        });
    assertThat(stationsWithoutSpatialReference).singleElement()
        .satisfies(station -> {
          assertThat(station.longitude()).isNull();
          assertThat(station.latitude()).isNull();
        });
  }

  @Test
  void shouldAttachPlatformDesignationFromTrafficPoint() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);
    ServicePointVersion servicePoint = servicePoint("ch:1:sloid:8500100", "Tavannes", 2596624.4, 1222062.1);
    PlatformVersion platform = PlatformVersion.builder()
        .sloid("ch:1:sloid:8500100:0:3")
        .version(0)
        .parentServicePointSloid("ch:1:sloid:8500100")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access info")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(0.0)
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .build();
    when(accessibilityDeriver.derive(any(), any(), any(), any())).thenReturn(NetexTriState.UNKNOWN);

    // When
    List<ErsadStation> stations = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePoint.getSloid(), servicePoint),
        Map.of("ch:1:sloid:8500100", List.of(platform)),
        Map.of("ch:1:sloid:8500100:0:3", "3"),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.now());

    // Then
    assertThat(stations).singleElement()
        .satisfies(station -> assertThat(station.quays()).singleElement()
            .satisfies(quay -> assertThat(quay.designation()).isEqualTo("3")));
  }

  @Test
  void shouldDelegateAccessibilityToDeriver() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);
    ServicePointVersion servicePoint = servicePoint("ch:1:sloid:8500100", "Tavannes", 2596624.4, 1222062.1);
    when(accessibilityDeriver.derive(any(), any(), any(), any())).thenReturn(NetexTriState.PARTIAL);

    // When
    List<ErsadStation> stations = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePoint.getSloid(), servicePoint),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        LocalDate.of(2026, Month.SEPTEMBER, 16));

    // Then
    verify(accessibilityDeriver).derive(stopPoint, List.of(), List.of(), LocalDate.of(2026, Month.SEPTEMBER, 16));
    assertThat(stations).singleElement().extracting(ErsadStation::accessibleStation).isEqualTo(NetexTriState.PARTIAL);
  }

  @Test
  void shouldBuildPathLinksFromRelations() {
    // Given
    StopPointVersion stopPoint = stopPoint("ch:1:sloid:8500100", 8500100);
    ServicePointVersion servicePoint = servicePoint("ch:1:sloid:8500100", "Tavannes", 2596624.4, 1222062.1);
    RelationVersion relation = RelationVersion.builder()
        .sloid("ch:1:sloid:8500100:pathLink:1")
        .parentServicePointSloid("ch:1:sloid:8500100")
        .referencePointSloid("ch:1:sloid:8500100:ref:1")
        .referencePointElementType(ReferencePointElementType.TOILET)
        .stepFreeAccess(StepFreeAccessAttributeType.YES)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .contrastingAreas(StandardAttributeType.YES)
        .version(7)
        .build();
    when(accessibilityDeriver.derive(any(), any(), any(), any())).thenReturn(NetexTriState.TRUE);

    // When
    List<ErsadStation> stations = assembler.assembleStations(
        Map.of(stopPoint.getSloid(), stopPoint),
        Map.of(servicePoint.getSloid(), servicePoint),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of(),
        Map.of("ch:1:sloid:8500100", List.of(relation)),
        Map.of(),
        LocalDate.now());

    // Then
    assertThat(stations).singleElement()
        .satisfies(station -> assertThat(station.pathLinks()).containsExactly(
            new ErsadPathLink("ch:1:sloid:8500100:ref:1", "ch:1:sloid:8500100:pathLink:1", ReferencePointElementType.TOILET,
                StepFreeAccessAttributeType.YES, TactileVisualAttributeType.YES, StandardAttributeType.YES, 8)));
  }

  private StopPointVersion stopPoint(String sloid, int number) {
    return StopPointVersion.builder()
        .sloid(sloid)
        .version(0)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(number))
        .address("Rue de la Gare")
        .zipCode("2710")
        .city("Tavannes")
        .url("https://www.sbb.ch/bahnhof/tavannes")
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("Condition")
        .assistanceService(StandardAttributeType.YES)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.YES)
        .alternativeTransport(StandardAttributeType.NO)
        .alternativeTransportCondition("No replacement")
        .visualInfo(StandardAttributeType.YES)
        .dynamicOpticSystem(StandardAttributeType.YES)
        .dynamicAudioSystem(StandardAttributeType.YES)
        .ticketMachine(BooleanOptionalAttributeType.YES)
        .wheelchairTicketMachine(StandardAttributeType.YES)
        .audioTicketMachine(StandardAttributeType.YES)
        .build();
  }

  private ServicePointVersion servicePoint(String sloid, String designation, double east, double north) {
    ServicePointGeolocation geolocation = ServicePointGeolocation.builder()
        .spatialReference(SpatialReference.LV95)
        .east(east)
        .north(north)
        .build();
    return ServicePointVersion.builder()
        .sloid(sloid)
        .designationOfficial(designation)
        .servicePointGeolocation(geolocation)
        .build();
  }
}
