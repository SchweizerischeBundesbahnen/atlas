package ch.sbb.atlas.servicepointdirectory.entity;

import static org.assertj.core.api.Assertions.assertThat;

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

}
