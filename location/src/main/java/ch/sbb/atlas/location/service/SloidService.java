package ch.sbb.atlas.location.service;

import ch.sbb.atlas.location.repository.SloidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SloidService {

  private final SloidRepository sloidRepository;

  public String generateNewSloid(String sloidPrefix, String seqName) {
    String generatedSloid = null;
    do {
      final Integer nextSeqValue = sloidRepository.getNextSeqValue(seqName);
      final String sloid = sloidPrefix + ":" + nextSeqValue;
      try {
        sloidRepository.insertSloid(sloid);
        generatedSloid = sloid;
      } catch (DataAccessException e) {
        log.info("{} occupied", sloid);
      }
    } while (generatedSloid == null);
    return generatedSloid;
  }

  public boolean claimSloid(String sloid) {
    try {
      sloidRepository.insertSloid(sloid);
      return true;
    } catch (DataAccessException e) {
      log.info("{} occupied", sloid);
      return false;
    }
  }

}
