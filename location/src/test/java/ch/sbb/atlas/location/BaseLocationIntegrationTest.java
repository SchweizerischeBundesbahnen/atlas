package ch.sbb.atlas.location;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;

public abstract class BaseLocationIntegrationTest extends BaseControllerApiTest {

  private final SloidRepository sloidRepository;

  protected BaseLocationIntegrationTest(SloidRepository sloidRepository) {
    this.sloidRepository = sloidRepository;
  }

  @AfterEach
  void cleanUp(){
    Arrays.stream(SloidType.values()).toList().forEach(sloidType -> {
      Set<String> allocatedSloid = sloidRepository.getAllocatedSloids(sloidType);
      if(sloidType == SloidType.SERVICE_POINT){
        sloidRepository.setAvailableSloidsToUnclaimed(allocatedSloid);
        sloidRepository.deleteAllocatedSloids(allocatedSloid,SloidType.SERVICE_POINT);
      }else {
        sloidRepository.deleteAllocatedSloids(allocatedSloid,sloidType);
      }

    });
  }

}
