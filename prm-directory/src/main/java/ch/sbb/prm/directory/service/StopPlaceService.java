package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPlaceService {

  private final StopPlaceRepository stopPlaceRepository;

  public List<StopPlaceVersion> getAllStopPlaces() {
   return stopPlaceRepository.findAll();
  }

  public void checkStopPlaceExists(String sloid) {
    if (!stopPlaceRepository.existsBySloid(sloid)) {
      throw new StopPlaceDoesNotExistsException(sloid);
    }
  }

  public StopPlaceVersion createStopPlace(StopPlaceVersion stopPlaceVersion) {
    return stopPlaceRepository.saveAndFlush(stopPlaceVersion);
  }
}
