package ch.sbb.atlas.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.PrmRepository;
import ch.sbb.atlas.location.repository.SePoDiRepository;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
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
  private final PrmRepository prmRepository;

  public void sync() {
    servicePointSloidSync();
    sloidSync(SloidType.PLATFORM);
    sloidSync(SloidType.AREA);
    sloidSync(SloidType.REFERENCE_POINT);
    sloidSync(SloidType.PARKING_LOT);
    sloidSync(SloidType.INFO_DESK);
    sloidSync(SloidType.TICKET_COUNTER);
    sloidSync(SloidType.TOILET);
  }
  private void sloidSync(SloidType sloidType) {
    log.info("**** Start Synch {} ****", sloidType);
    Set<String> alreadyDistributedSloid = getAlreadyDistributedSloid(sloidType);
    Set<String> alreadyAllocatedSloid = sloidRepository.getAllocatedSloid(sloidType);
    log.info("Used {} sloid: {}", sloidType, alreadyDistributedSloid);
    log.info("Allocated servicePoint sloid: {}", alreadyAllocatedSloid);
    addUsedMissingSloidToAllocatedSloid(alreadyDistributedSloid, alreadyAllocatedSloid, sloidType);
    removeUnusedSloidFromAllocatedSloid(alreadyDistributedSloid, alreadyAllocatedSloid, sloidType);
    log.info("**** End Synch {} ****", sloidType);
  }

  private Set<String> getAlreadyDistributedSloid(SloidType sloidType){
    if(SloidType.PLATFORM == sloidType || SloidType.AREA == sloidType){
      return sePoDiRepository.getAlreadyDistributedSloid(sloidType);
    }
    return prmRepository.getAlreadyDistributedSloid(sloidType);
  }

  private void servicePointSloidSync() {
    log.info("**** Start Synch SERVICE_POINT ****");
    Set<String> servicePointSePoDiAllocatedSloid = sePoDiRepository.getAlreadyServicePointDistributedSloid();
    Set<String> servicePointLocationAllocatedSloid = sloidRepository.getAllocatedSloid(SloidType.SERVICE_POINT);
    log.info("Used ServicePoint sloid: {}", servicePointSePoDiAllocatedSloid);
    log.info("Allocated servicePoint sloid: {}", servicePointLocationAllocatedSloid);
    //case_1: SePoDi ServicePoints has more Sloids then Location-> add SLOID to Location
    addUsedMissingSloidServicePointToAllocatedSloid(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    //case_2: Location has more Sloids then SePoDi ServicePoints-> remove SLOID from Location
    removeUnusedServicePointSloidFromAllocatedSloid(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    //case_3: delete all available_service_point_sloid already claimed
    deleteAllAvailableServicePointSloidAlreadyClaimed(servicePointSePoDiAllocatedSloid);//TODO: fixme
    log.info("**** End Synch SERVICE_POINT ****");
  }

  private void deleteAllAvailableServicePointSloidAlreadyClaimed(Set<String> servicePointSePoDiAllocatedSloid) {
    log.info("Delete already claimed availableServicePointSloids: {}", servicePointSePoDiAllocatedSloid);
    sloidRepository.deleteAvailableServicePointSloidAlreadyClaimed(servicePointSePoDiAllocatedSloid);
  }

  private void removeUnusedServicePointSloidFromAllocatedSloid(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid) {
    Set<String> sloidToRemove = getSloidToRemove(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    if (!sloidToRemove.isEmpty()) {
      sloidRepository.deleteAllocatedSloid(sloidToRemove, SloidType.SERVICE_POINT);
      sloidRepository.setAvailableSloidToUnclaimedAllocatedSloid(sloidToRemove);
    }
  }

  private void removeUnusedSloidFromAllocatedSloid(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid, SloidType sloidType) {
    Set<String> sloidToRemove = getSloidToRemove(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    if (!sloidToRemove.isEmpty()) {
      sloidRepository.deleteAllocatedSloid(sloidToRemove, sloidType);
    }
  }

  private static Set<String> getSloidToRemove(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid) {
    Set<String> sloidToRemove = new HashSet<>(servicePointLocationAllocatedSloid);
    sloidToRemove.removeAll(servicePointSePoDiAllocatedSloid);
    log.info("Sloid to remove from allocatedSloid:{}", sloidToRemove);
    return sloidToRemove;
  }

  private void addUsedMissingSloidServicePointToAllocatedSloid(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid) {
    Set<String> sloidToAdd = getSloidToAdd(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    if (!sloidToAdd.isEmpty()) {
      sloidRepository.addMissingAllocatedSloid(sloidToAdd, SloidType.SERVICE_POINT);
      sloidRepository.setAvailableSloidToClaimed(sloidToAdd);
    }
  }
  private void addUsedMissingSloidToAllocatedSloid(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid, SloidType sloidType) {
    Set<String> sloidToAdd = getSloidToAdd(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    if (!sloidToAdd.isEmpty()) {
      sloidRepository.addMissingAllocatedSloid(sloidToAdd, sloidType);
    }
  }

  private Set<String> getSloidToAdd(Set<String> servicePointSePoDiAllocatedSloid,
      Set<String> servicePointLocationAllocatedSloid) {
    Set<String> sloidToAdd = new HashSet<>(servicePointSePoDiAllocatedSloid);
    sloidToAdd.removeAll(servicePointLocationAllocatedSloid);
    log.info("Sloid not present on Location:{}", sloidToAdd);
    return sloidToAdd;
  }

  public String generateNewSloid(String sloidPrefix, @NotNull SloidType sloidType) {
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

  public String getNextAvailableSloid(Country country) {
    String nextAvailableSloid;
    boolean insertDone = false;
    do {
      nextAvailableSloid = sloidRepository.getNextAvailableSloid(country);
      try {
        sloidRepository.insertSloid(nextAvailableSloid, SloidType.SERVICE_POINT);
      } catch (DataAccessException e) {
        continue;
      }
      insertDone = true;
      int rowsAffected = sloidRepository.setAvailableSloidToClaimed(nextAvailableSloid);
      if (rowsAffected != 1) {
        throw new IllegalStateException("Row needs to be found after select from available sloids");
      }
    } while (!insertDone);
    return nextAvailableSloid;
  }

  public boolean claimAvailableSloid(String sloid) {
    boolean sloidAvailable = sloidRepository.isSloidAvailable(sloid);
    if (!sloidAvailable) {
      return false;
    }
    try {
      sloidRepository.insertSloid(sloid, SloidType.SERVICE_POINT);
    } catch (DataAccessException e) {
      return false;
    }
    int rowsAffected = sloidRepository.setAvailableSloidToClaimed(sloid);
    if (rowsAffected != 1) {
      throw new IllegalStateException("Row needs to be found after select statement");
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

  public void saveGeneratedToAllocatedSloid(String sloid, SloidType sloidType) {
    sloidRepository.insertSloid(sloid, sloidType);
  }

}
