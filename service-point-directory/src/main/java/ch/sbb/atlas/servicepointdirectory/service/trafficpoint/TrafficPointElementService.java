package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.BasePointService;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementService extends BasePointService {

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

  public void updateTrafficPointElementVersionImport(TrafficPointElementVersion edited) {
    List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(edited.getSloid());
    TrafficPointElementVersion current = getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsWithDeleteByNullProperties(current, edited,
        dbVersions);

    versionedObjects.stream().filter(versionedObject -> {
      final VersioningAction action = versionedObject.getAction();
      return action == VersioningAction.UPDATE || action == VersioningAction.NEW;
    }).forEach(versionedObject -> {
      final Property geolocationProp =
          versionedObject.getEntity()
              .getProperties()
              .stream()
              .filter(property -> property.getKey().equals(Fields.trafficPointElementGeolocation))
              .findFirst()
              .orElseThrow();

      if (geolocationProp.getOneToOne() != null) {
        final List<Property> geolocationPropertyList = geolocationProp.getOneToOne().getProperties();
        final List<Property> propertiesToAdd = versionedObject
            .getEntity()
            .getProperties()
            .stream()
            .filter(property -> List.of(
                BaseDidokImportEntity.Fields.creationDate,
                BaseDidokImportEntity.Fields.creator,
                BaseDidokImportEntity.Fields.editor,
                BaseDidokImportEntity.Fields.editionDate
            ).contains(property.getKey()))
            .toList();

        geolocationPropertyList.addAll(propertiesToAdd);
      }
    });

    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save, this::deleteById);
  }

  public void updateTrafficPointElementVersion(TrafficPointElementVersion edited) {
    List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(edited.getSloid());
    TrafficPointElementVersion current = getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(current, edited,
        dbVersions);
    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save, this::deleteById);
  }
}
