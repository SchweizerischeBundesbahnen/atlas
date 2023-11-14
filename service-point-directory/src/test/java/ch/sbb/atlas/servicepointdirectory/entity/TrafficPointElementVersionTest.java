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
    String errorDescription = String.format("\nThe %s is used in ServicePointDirectory project. " +
            "If this test fail please make sure the entire ATLAS application works properly: import, export, ...\n",
        TrafficPointElementVersion.class);
    assertThat(result.get()).as(errorDescription).isEqualTo(36);
  }


  @Test
  public void shouldCreateTrafficPointIfValid(){
   //given
   TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
       .designation("Test")
       .sloid("ch:1:2311:1233")
       .trafficPointElementType(TrafficPointElementType.BOARDING_AREA)
       .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(7283913))
       .validFrom(LocalDate.of(2022, 1, 1))
       .validTo(LocalDate.of(2022, 12, 31))
       .build();

   //then

   assertThat(trafficPointElementVersion.isValidForBoardingArea()).isTrue();
  }


  //TODO: Should not create

}
