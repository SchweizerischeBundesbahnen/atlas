package ch.sbb.exportservice.job.sepodi.sector.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SectorVersionTest {

  @Test
  void sectorSharedEntityIntegrityTest() {
    //given

    //when
    AtomicInteger result = new AtomicInteger();
    Arrays.stream(SectorVersion.class.getClasses()).forEach(c -> result.addAndGet(c.getDeclaredFields().length));

    //then
    String errorDescription = String.format("""
            The %s is used in ServicePointDirectory project
            If this test fail please make sure the entire ATLAS application works properly: import, export, ...
            """,
        SectorVersion.class);
    assertThat(result.get()).as(errorDescription).isEqualTo(34);
  }

}