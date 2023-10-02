package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.repository.StopPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPlaceService {

  private final StopPlaceRepository stopPlaceRepository;

}
