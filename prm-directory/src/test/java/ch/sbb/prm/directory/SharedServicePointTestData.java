package ch.sbb.prm.directory;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SharedServicePointTestData {

  public static SharedServicePoint getSharedServicePoint() {
    return buildSharedServicePoint("ch:1:sloid:70000", Set.of("ch:1:sboid:100602"), Collections.emptySet());
  }

  public static SharedServicePoint buildSharedServicePoint(String sloid, Set<String> sboids, Set<String> trafficPointSloids) {
    SharedServicePointVersionModel servicePointVersionModel = SharedServicePointVersionModel.builder()
        .servicePointSloid(sloid)
        .sboids(sboids)
        .trafficPointSloids(trafficPointSloids)
        .stopPoint(true)
        .build();

    return SharedServicePoint.builder()
        .servicePoint(getObjectAsString(servicePointVersionModel))
        .sloid(sloid)
        .build();
  }

  private static String getObjectAsString(SharedServicePointVersionModel sharedServicePoint) {
    try {
      return new ObjectMapper().writeValueAsString(sharedServicePoint);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

}
