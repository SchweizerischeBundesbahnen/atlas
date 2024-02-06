package ch.sbb.atlas.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.repository.PrmRepository;
import ch.sbb.atlas.location.repository.SePoDiRepository;
import ch.sbb.atlas.location.repository.SloidRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SloidSyncService {

  private final SloidRepository sloidRepository;
  private final SePoDiRepository sePoDiRepository;
  private final PrmRepository prmRepository;

  private static final List<SloidType> SLOID_TYPES = List.of(SloidType.PLATFORM, SloidType.AREA, SloidType.REFERENCE_POINT,
      SloidType.PARKING_LOT, SloidType.INFO_DESK, SloidType.TICKET_COUNTER, SloidType.TOILET);

  public void sync() {
    servicePointSloidSync();
    SLOID_TYPES.forEach(this::sloidSync);
  }

  private void sloidSync(SloidType sloidType) {
    log.info("**** Start Synch {} ****", sloidType);
    Set<String> alreadyDistributedSloid = getAlreadyDistributedSloid(sloidType);
    Set<String> alreadyAllocatedSloid = sloidRepository.getAllocatedSloid(sloidType);
    log.info("Used {} sloid: {}", sloidType, alreadyDistributedSloid.size());
    log.info("Allocated servicePoint sloid: {}", alreadyAllocatedSloid.size());
    addUsedMissingSloidToAllocatedSloid(alreadyDistributedSloid, alreadyAllocatedSloid, sloidType);
    removeUnusedSloidFromAllocatedSloid(alreadyDistributedSloid, alreadyAllocatedSloid, sloidType);
    log.info("**** End Synch {} ****", sloidType);
  }

  private Set<String> getAlreadyDistributedSloid(SloidType sloidType) {
    if (SloidType.PLATFORM == sloidType || SloidType.AREA == sloidType) {
      return sePoDiRepository.getAlreadyDistributedSloid(sloidType);
    }
    return prmRepository.getAlreadyDistributedSloid(sloidType);
  }

  private void servicePointSloidSync() {
    log.info("**** Start Synch SERVICE_POINT ****");
    Set<String> servicePointSePoDiAllocatedSloid = sePoDiRepository.getAlreadyServicePointDistributedSloid();
    Set<String> servicePointLocationAllocatedSloid = sloidRepository.getAllocatedSloid(SloidType.SERVICE_POINT);
    log.info("Used ServicePoint sloid: {}", servicePointSePoDiAllocatedSloid.size());
    log.info("Allocated servicePoint sloid: {}", servicePointLocationAllocatedSloid.size());
    log.info("Diff between Allocated and used servicePoint sloid: {}",
        Math.abs(servicePointSePoDiAllocatedSloid.size() - servicePointLocationAllocatedSloid.size()));
    //case_1: SePoDi ServicePoints has more Sloids then Location-> add SLOID to Location
    addUsedMissingSloidServicePointToAllocatedSloid(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    //case_2: Location has more Sloids then SePoDi ServicePoints-> remove SLOID from Location
    removeUnusedServicePointSloidFromAllocatedSloid(servicePointSePoDiAllocatedSloid, servicePointLocationAllocatedSloid);
    //case_3: delete all available_service_point_sloid already claimed
    deleteAllAvailableServicePointSloidAlreadyClaimed(servicePointSePoDiAllocatedSloid);
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

}
