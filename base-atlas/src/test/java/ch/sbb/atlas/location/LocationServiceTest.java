package ch.sbb.atlas.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.location.LocationClientV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import feign.FeignException.Conflict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocationServiceTest {

  @Mock
  private LocationClientV1 locationClient;

  private LocationService locationService;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    this.locationService = new LocationService(locationClient);
  }

  @Test
  void shouldClaimSloidSuccessfully() {
    // when
    locationService.claimSloid(SloidType.TOILET, "ch:1:sloid:1");

    // then
    verify(locationClient, times(1)).claimSloid(
        argThat(request -> request.sloidType() == SloidType.TOILET && request.sloid() == "ch:1:sloid:1"));
  }

  @Test
  void shouldThrowWhenClaimSloidConflicts() {
    // given
    when(locationClient.claimSloid(any())).thenThrow(Conflict.class);

    // when, then
    assertThrows(SloidAlreadyExistsException.class, () -> locationService.claimSloid(SloidType.TOILET, "ch:1:sloid:1"));
  }

  @Test
  void shouldClaimServicePointSloidWhenNotNull() {
    // when
    locationService.claimServicePointSloid("ch:1:sloid:1");

    // then
    verify(locationClient, times(1)).claimSloid(any());
  }

  @Test
  void shouldNotClaimServicePointSloidWhenNull() {
    // when
    locationService.claimServicePointSloid(null);

    // then
    verify(locationClient, never()).claimSloid(any());
  }

  @Test
  void shouldGetSloidTypeForBoardingArea() {
    assertThat(LocationService.getSloidType(TrafficPointElementType.BOARDING_AREA)).isEqualTo(SloidType.AREA);
  }

  @Test
  void shouldGetSloidTypeForBoardingPlatform() {
    assertThat(LocationService.getSloidType(TrafficPointElementType.BOARDING_PLATFORM)).isEqualTo(SloidType.PLATFORM);
  }

  @Test
  void shouldCallGenerateSloidWithCountry() {
    // when
    locationService.generateSloid(SloidType.SERVICE_POINT, Country.SWITZERLAND);

    // then
    verify(locationClient, times(1)).generateSloid(
        argThat(request -> request.getSloidType() == SloidType.SERVICE_POINT && request.getCountry() == Country.SWITZERLAND));
  }

  @Test
  void shouldCallGenerateSloidWithSloidPrefix() {
    // when
    locationService.generateSloid(SloidType.TOILET, "ch:1:sloid:1");

    // then
    verify(locationClient, times(1)).generateSloid(
        argThat(request -> request.getSloidType() == SloidType.TOILET && request.getSloidPrefix() == "ch:1:sloid:1"));
  }

  @Test
  void shouldGenerateTrafficPointSloidForSwitzerland() {
    // when
    locationService.generateTrafficPointSloid(TrafficPointElementType.BOARDING_PLATFORM,
        ServicePointNumber.of(Country.SWITZERLAND, 1));

    // then
    verify(locationClient, times(1)).generateSloid(
        argThat(request -> request.getSloidType() == SloidType.PLATFORM && "ch:1:sloid:1".equals(request.getSloidPrefix())));
  }

  @Test
  void shouldGenerateTrafficPointSloidForElseThanSwitzerland() {
    // when
    locationService.generateTrafficPointSloid(TrafficPointElementType.BOARDING_PLATFORM,
        ServicePointNumber.of(Country.ITALY_BUS, 5));

    // then
    verify(locationClient, times(1)).generateSloid(
        argThat(
            request -> request.getSloidType() == SloidType.PLATFORM && "ch:1:sloid:1300005".equals(request.getSloidPrefix())));
  }

}
