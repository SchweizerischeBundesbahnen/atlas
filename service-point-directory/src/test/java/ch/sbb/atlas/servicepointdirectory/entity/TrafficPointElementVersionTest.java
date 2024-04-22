package ch.sbb.atlas.servicepointdirectory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TrafficPointElementVersionTest {

  @Test
   void trafficPointVersionSharedEntityIntegrityTest() {
    //given

    //when
    AtomicInteger result = new AtomicInteger();
    Arrays.stream(TrafficPointElementVersion.class.getClasses()).forEach(c -> result.addAndGet(c.getDeclaredFields().length));

    //then
    String errorDescription = String.format("""
            The %s is used in ServicePointDirectory project. If this test fail please make sure the entire ATLAS application works properly: import, export, ...
            """,
        TrafficPointElementVersion.class);
    assertThat(result.get()).as(errorDescription).isEqualTo(31);
  }


  @Test
  void shouldBeValidTrafficPointArea(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .sloid("ch:1:2311:1233")
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isTrue();
  }

  @Test
  void shouldNotBeValidTrafficPointAreaIfDesignationOperationalIsNotNull(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .designationOperational("Das sollte nicht erlaubt sein für AREA")
       .sloid("ch:1:2311:1233")
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isFalse();
  }

  @Test
  void shouldNotBeValidTrafficPointAreaIfLengthIsNotNull(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .sloid("ch:1:2311:1233")
       .length(24.5)
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isFalse();
  }

  @Test
  void shouldNotBeValidTrafficPointAreaIfCompassDirectionIsNotNull(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .sloid("ch:1:2311:1233")
       .compassDirection(24.5)
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isFalse();
  }

  @Test
  void shouldNotBeValidTrafficPointAreaIfBoardingAreaHeightIsNotNull(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .sloid("ch:1:2311:1233")
       .boardingAreaHeight(24.5)
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isFalse();
  }

  @Test
  void shouldBeValidTrafficPointPlattform(){
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .designationOperational("Das sollte funktionieren")
       .length(35.234)
       .boardingAreaHeight(43.123)
       .compassDirection(11.11)
       .sloid("ch:1:2311:1233")
       .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isTrue();
  }

}
