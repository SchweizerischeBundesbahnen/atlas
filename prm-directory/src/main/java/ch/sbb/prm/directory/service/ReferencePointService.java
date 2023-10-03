package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReferencePointService {

  private final ReferencePointRepository referencePointRepository;

  public List<ReferencePointVersion> getAllReferencePoints() {
   return referencePointRepository.findAll();
  }

}
