package ch.sbb.atlas.location.service;

import ch.sbb.atlas.location.repository.SePoDiRepository;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.servicepoint.Country;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SloidService {

  private final SloidRepository sloidRepository;
  private final SePoDiRepository sePoDiRepository;

  public String sync(){
    Set<String> servicePointSloid = sePoDiRepository.getServicePointSloid();
    Set<String> allocatedSloid = sloidRepository.getAllocatedSloid();
    
    log.info("Used ServicePoint sloid: {}",servicePointSloid);
    log.info("Allocated servicePoint sloid: {}",allocatedSloid);
    servicePointSloid.removeAll(allocatedSloid);

    log.info("Used ServicePoint sloid: {}",servicePointSloid);
    log.info("Allocated servicePoint sloid: {}",allocatedSloid);
    return "asd";
  }

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

  public String getNextAvailableSloid(Country country) {
    String nextAvailableSloid;
    int updateCount;
    do {
      nextAvailableSloid = sloidRepository.getNextAvailableSloid(country);
      updateCount = sloidRepository.setAvailableSloidToUsed(nextAvailableSloid, country);
    } while (updateCount == 0);
    return nextAvailableSloid;
  }

  public boolean claimAvailableSloid(String sloid, Country country) {
    int updateCount = sloidRepository.setAvailableSloidToUsed(sloid, country);
    return updateCount != 0;
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
// todo: implement sync endpoint and cleanup endpoint for not_confirmed elements
