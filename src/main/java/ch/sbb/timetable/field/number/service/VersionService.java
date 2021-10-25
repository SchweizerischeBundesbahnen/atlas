package ch.sbb.timetable.field.number.service;


import static ch.sbb.timetable.field.number.entity.Version.VERSIONABLE_PROPERTIES;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import ch.sbb.timetable.field.number.versioning.model.ObjectProperty;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import ch.sbb.timetable.field.number.versioning.service.VersionableService;
import java.util.List;
import java.util.Optional;
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
  private final VersionableService versionableService;

  @Autowired
  public VersionService(VersionRepository versionRepository,
      VersionableService versionableService) {
    this.versionRepository = versionRepository;
    this.versionableService = versionableService;
  }

  public List<Version> getAllVersionsVersioned(String ttfnId) {
    return versionRepository.getAllVersionsVersioned(ttfnId);
  }

  public Optional<Version> findById(Long id) {
    return versionRepository.findById(id);
  }

  public Version save(Version newVersion) {
    return versionRepository.save(newVersion);
  }

  public boolean existsById(Long id) {
    return versionRepository.existsById(id);
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public Page<Version> findAll(Pageable pageable) {
    return versionRepository.findAll(pageable);
  }

  public long count() {
    return versionRepository.count();
  }

  public List<VersionedObject> updateVersion(Version currentVersion, Version editedVersion) {
    //1. get all versions from currentVersion.getTtfnid(); and sort it by from asc
    List<Version> currentVersions = versionRepository.getAllVersionsVersioned(
        currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(
        VERSIONABLE_PROPERTIES,currentVersion,
        editedVersion, currentVersions);

    for (VersionedObject versionedObject : versionedObjects) {
      if (VersioningAction.NOT_TOUCHED.equals(versionedObject.getAction())) {
        //nothing to do
        log(versionedObject);
      }
      if (VersioningAction.UPDATE.equals(versionedObject.getAction())) {
        //update existing Version
        log(versionedObject);
        Version version = convertVersionedObjectToVersion(versionedObject);
        System.out.println(version);
        versionRepository.save(version);
      }
      if (VersioningAction.NEW.equals(versionedObject.getAction())) {
        //create new version
        log.info("A new Version was added. VersionedObject={}", versionedObject);
        Version version = convertVersionedObjectToVersion(versionedObject);
        System.out.println(version);
        versionRepository.save(version);
      }
      if (VersioningAction.DELETE.equals(versionedObject.getAction())) {
        //delete existing version
        log(versionedObject);
        versionRepository.deleteById(versionedObject.getObjectId());
      }
    }
    return versionedObjects;
  }

  private Version convertVersionedObjectToVersion(VersionedObject versionedObject) {
    ObjectProperty  objectProperties = versionedObject.getObjectProperties();
    Long objectId = versionedObject.getObjectId();
    Version version = new Version();
    version.setId(objectId);
    version.setValidFrom(versionedObject.getValidFrom());
    version.setValidTo(versionedObject.getValidTo());
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);
    for (Property property : objectProperties.getProperties()) {
      propertyAccessor.setPropertyValue(property.getKey(), property.getValue());
    }
    return version;
  }

  private void log(VersionedObject versionedObject) {
    log.info("Version with id={} was {}D. VersionedObject={}",
        versionedObject.getObjectId(),
        versionedObject.getAction(),
        versionedObject);
  }

}
