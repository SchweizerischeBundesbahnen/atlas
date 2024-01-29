package ch.sbb.atlas.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SloidService {

  private final SloidRepository sloidRepository;

  public String generateNewSloid(String sloidPrefix, @NotNull SloidType sloidType) {
    if (SloidType.SERVICE_POINT == sloidType) {
      throw new IllegalArgumentException("This method is not allowed to generate sloid for ServicePoint.");
    }
    String sloidToInsert = null;
    do {
      final Integer nextSeqValue = sloidRepository.getNextSeqValue(sloidType);
      final String sloid = createFormattedSloid(sloidPrefix, nextSeqValue);
      if (!sloidRepository.isSloidAllocated(sloid)) {
        sloidToInsert = sloid;
      }
    } while (sloidToInsert == null);
    sloidRepository.insertSloid(sloidToInsert, sloidType);
    return sloidToInsert;
  }

  @Transactional
  public String getNextAvailableServicePointSloid(Country country) {
    final String nextAvailableSloid = sloidRepository.getNextAvailableSloid(country);
    sloidRepository.insertSloid(nextAvailableSloid, SloidType.SERVICE_POINT);
    sloidRepository.setAvailableSloidToClaimed(nextAvailableSloid);
    return nextAvailableSloid;
  }

  @Transactional
  public boolean claimAvailableServicePointSloid(String sloid) {
    if (!sloidRepository.isSloidAllocated(sloid)) {
      sloidRepository.insertSloid(sloid, SloidType.SERVICE_POINT);
      sloidRepository.setAvailableSloidToClaimed(sloid);
      return true;
    } else {
      log.info("Could not claim Service Point sloid {} ", sloid);
      return false;
    }
  }

  public boolean claimSloid(String sloid, @NotNull SloidType sloidType) {
    if (!sloidRepository.isSloidAllocated(sloid)) {
      sloidRepository.insertSloid(sloid, sloidType);
      return true;
    } else {
      log.info("Could not claim {} sloid: {}", sloidType, sloid);
      return false;
    }
  }

  private String createFormattedSloid(String sloidPrefix, Integer nextSeqValue) {
    return sloidPrefix + ":" + nextSeqValue;
  }

}