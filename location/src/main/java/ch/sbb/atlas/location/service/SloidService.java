package ch.sbb.atlas.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SloidService {

  private final SloidRepository sloidRepository;

  public String generateNewSloid(String sloidPrefix, @NotNull SloidType sloidType) {
    if(SloidType.SERVICE_POINT == sloidType){
      throw new IllegalArgumentException("This method is not allowed to generate sloid for ServicePoint.");
    }
    String generatedSloid = null;
    do {
      final Integer nextSeqValue = sloidRepository.getNextSeqValue(sloidType);
      final String sloid = sloidPrefix + ":" + nextSeqValue;
      try {
        sloidRepository.insertSloid(sloid, sloidType);
        generatedSloid = sloid;
      } catch (DataAccessException e) {
        log.info("{} occupied", sloid);
      }
    } while (generatedSloid == null);
    return generatedSloid;
  }

  public String getNextAvailableServicePointSloid(Country country) {
    //TODO: check only swiss like countries
    String nextAvailableSloid;
    boolean insertDone = false;
    do {
      nextAvailableSloid = sloidRepository.getNextAvailableSloid(country);
      try {
        sloidRepository.insertSloid(nextAvailableSloid, SloidType.SERVICE_POINT);
      } catch (DataAccessException e) {
        //Possible loop?
        continue;
      }
      insertDone = true;
      sloidRepository.setAvailableSloidToClaimed(nextAvailableSloid);
    } while (!insertDone);
    return nextAvailableSloid;
  }

  @Transactional
  public boolean claimAvailableServicePointSloid(String sloid) {
    try {
      sloidRepository.insertSloid(sloid, SloidType.SERVICE_POINT);
      sloidRepository.setAvailableSloidToClaimed(sloid);
    } catch (DataAccessException e) {
      return false;
    }
    return true;
  }

  public boolean claimSloid(String sloid, @NotNull SloidType sloidType) {
    try {
      sloidRepository.insertSloid(sloid, sloidType);
      return true;
    } catch (DataAccessException e) {
      log.info("{} occupied", sloid);
      return false;
    }
  }

}
