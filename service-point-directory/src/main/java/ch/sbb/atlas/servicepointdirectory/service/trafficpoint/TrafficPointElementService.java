package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;
  private final VersionableService versionableService;

  public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {
    return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<TrafficPointElementVersion> findBySloidOrderByValidFrom(String sloid) {
    return trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  public Optional<TrafficPointElementVersion> findById(Long id) {
    return trafficPointElementVersionRepository.findById(id);
  }

  public boolean isTrafficPointElementExisting(String sloid) {
    return trafficPointElementVersionRepository.existsBySloid(sloid);
  }

  public TrafficPointElementVersion save(TrafficPointElementVersion trafficPointElementVersion) {
    return trafficPointElementVersionRepository.save(trafficPointElementVersion);
  }

  public void deleteById(Long id) {
    trafficPointElementVersionRepository.deleteById(id);
  }

  public void updateTrafficPointElementVersion(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion) {
    trafficPointElementVersionRepository.incrementVersion(currentVersion.getSloid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(currentVersion.getSloid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion, editedVersion,
        dbVersions);
    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save, this::deleteById);
  }
}
