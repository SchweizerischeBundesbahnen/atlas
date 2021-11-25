package ch.sbb.timetable.field.number.service;


import static ch.sbb.atlas.versioning.model.VersioningAction.DELETE;
import static ch.sbb.atlas.versioning.model.VersioningAction.NEW;
import static ch.sbb.atlas.versioning.model.VersioningAction.NOT_TOUCHED;
import static ch.sbb.atlas.versioning.model.VersioningAction.UPDATE;

import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.timetable.field.number.entity.LineRelation;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.exceptions.ConflictException;
import ch.sbb.timetable.field.number.repository.TimetableFieldNumberRepository;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VersionService {

  private final VersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final VersionableService versionableService;

  @Autowired
  public VersionService(VersionRepository versionRepository,
      TimetableFieldNumberRepository timetableFieldNumberRepository,
      VersionableService versionableService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
    this.versionableService = versionableService;
  }

  public List<Version> getAllVersionsVersioned(String ttfnId) {
    return versionRepository.getAllVersionsVersioned(ttfnId);
  }

  public Optional<Version> findById(Long id) {
    return versionRepository.findById(id);
  }

  public Version save(Version newVersion) {
    if (!areNumberAndSttfnUnique(newVersion)) {
      throw new ConflictException("Number or SwissTimeTableFieldNumber are already taken");
    }
    return versionRepository.save(newVersion);
  }

  public boolean existsById(Long id) {
    return versionRepository.existsById(id);
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public Page<TimetableFieldNumber> getOverview(Pageable pageable) {
    return timetableFieldNumberRepository.findAll(pageable);
  }

  public long count() {
    return versionRepository.count();
  }

  public List<VersionedObject> updateVersion(Version currentVersion, Version editedVersion) {
    //1. get all versions from currentVersion.getTtfnid(); and sort it by from asc
    List<Version> currentVersions = getAllVersionsVersioned(currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    for (VersionedObject versionedObject : versionedObjects) {
      if (NOT_TOUCHED.equals(versionedObject.getAction())) {
        //nothing to do
        log(versionedObject);
      }
      if (UPDATE.equals(versionedObject.getAction())) {
        //update existing Version
        log(versionedObject);
        Version version = convertVersionedObjectToVersion(versionedObject);
        version.setStatus(Status.ACTIVE);
        save(version);
      }
      if (NEW.equals(versionedObject.getAction())) {
        //create new version
        log.info("A new Version was added. VersionedObject={}", versionedObject);
        Version version = convertVersionedObjectToVersion(versionedObject);
        //ensure version.getId() == null to avoid to update a Version
        version.setId(null);
        version.setStatus(Status.ACTIVE);
        version.getLineRelations().forEach(lineRelation -> {
          lineRelation.setVersion(version);
        });
        save(version);
      }
      if (DELETE.equals(versionedObject.getAction())) {
        //delete existing version
        log(versionedObject);
        deleteById(versionedObject.getEntity().getId());
      }
    }
    return versionedObjects;
  }

  private boolean areNumberAndSttfnUnique(Version version) {
    String ttfnid = version.getTtfnid() == null ? "" : version.getTtfnid();
    return versionRepository.getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(version.getNumber(), version.getSwissTimetableFieldNumber(),
        version.getValidFrom(), version.getValidTo(), ttfnid).size() == 0;
  }

  private Version convertVersionedObjectToVersion(VersionedObject versionedObject) {
    Entity entity = versionedObject.getEntity();
    Version version = new Version();
    if (entity.getId() != null) {
      version.setId(entity.getId());
    }
    version.setValidFrom(versionedObject.getValidFrom());
    version.setValidTo(versionedObject.getValidTo());
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);
    for (Property property : entity.getProperties()) {
      if (property.hasOneToOneRelation()) {
        //parse and build one to one relation
        throw new VersioningException("OneToOneRelation not implemented!");
      } else if (property.hasOneToManyRelation()) {
        Set<LineRelation> lineRelations = new HashSet<>();
        List<Entity> oneToManyEntities = property.getOneToMany();
        for (Entity entityRelation : oneToManyEntities) {
          LineRelation lineRelation = new LineRelation();
          ConfigurablePropertyAccessor lineRelationPropertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
              lineRelation);
          for (Property propertyRelation : entityRelation.getProperties()) {
            lineRelationPropertyAccessor.setPropertyValue(propertyRelation.getKey(),
                propertyRelation.getValue());
          }
          lineRelations.add(lineRelation);
        }
        version.setLineRelations(lineRelations);
      } else {
        propertyAccessor.setPropertyValue(property.getKey(), property.getValue());
      }
    }
    return version;
  }

  private void log(VersionedObject versionedObject) {
    log.info("Version with id={} was {}D. VersionedObject={}",
        versionedObject.getEntity().getId(),
        versionedObject.getAction(),
        versionedObject);
  }
}
