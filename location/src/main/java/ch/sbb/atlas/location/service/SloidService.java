package ch.sbb.atlas.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.SePoDiRepository;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.NotNull;
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
    allocatedSloid.removeAll(servicePointSloid);
    log.info("Used ServicePoint sloid: {}",servicePointSloid);
    log.info("Allocated but not used servicePoint sloid: {}",allocatedSloid);
    //TODO: sloidRepository.removeUnusedSloidFromAllocatedSloid(allocatedSloid)
    // sloidRepository.setUnclimedNotUsedSloid(allocatedSloid)
    return "asd";
  }

  public String generateNewSloid(String sloidPrefix, String seqName, @NotNull SloidType sloidType) {
    String generatedSloid = null;
    do {
      final Integer nextSeqValue = sloidRepository.getNextSeqValue(seqName);
      final String sloid = sloidPrefix + ":" + nextSeqValue;
      try {
        sloidRepository.insertSloid(sloid,sloidType);
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

  public boolean claimSloid(String sloid, @NotNull SloidType sloidType) {
    try {
      sloidRepository.insertSloid(sloid,sloidType);
      return true;
    } catch (DataAccessException e) {
      log.info("{} occupied", sloid);
      return false;
    }
  }

  public void saveGeneratedToAllocatedSloid(String sloid, SloidType sloidType){
    sloidRepository.insertSloid(sloid,sloidType);
  }

}
// todo: implement sync endpoint and cleanup endpoint for not_confirmed elements
