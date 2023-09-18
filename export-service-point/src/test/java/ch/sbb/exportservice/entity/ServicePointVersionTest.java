package ch.sbb.exportservice.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

 class ServicePointVersionTest {

    @Test
     void servicePointSharedEntityIntegrityTest(){
        //given

        //when
        AtomicInteger result = new AtomicInteger();
        Arrays.stream(ServicePointVersion.class.getClasses()).forEach(c -> result.addAndGet(c.getDeclaredFields().length));

        //then
        String errorDescription = String.format("\nThe %s is used in ServicePointDirectory project. " +
                "If this test fail please make sure the entire ATLAS application works properly: import, export, ...\n", ServicePointVersion.class);
        assertThat(result.get()).as(errorDescription).isEqualTo(66);
    }

}