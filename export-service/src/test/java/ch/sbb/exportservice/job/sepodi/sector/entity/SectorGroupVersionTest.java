package ch.sbb.exportservice.job.sepodi.sector.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SectorGroupVersionTest {

  @Test
  void sectorGroupSharedEntityIntegrityTest() {
    //given

    //when
    AtomicInteger result = new AtomicInteger();
    Arrays.stream(SectorGroupVersion.class.getClasses()).forEach(c -> result.addAndGet(c.getDeclaredFields().length));

    //then
    String errorDescription = String.format("""
            The %s is used in ServicePointDirectory project
            If this test fail please make sure the entire ATLAS application works properly: import, export, ...
            """,
        SectorGroupVersion.class);
    assertThat(result.get()).as(errorDescription).isEqualTo(26);
  }

}
