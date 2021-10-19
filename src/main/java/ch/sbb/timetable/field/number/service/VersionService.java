package ch.sbb.timetable.field.number.service;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import ch.sbb.timetable.field.number.versioning.service.VersionableService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  public List<VersionedObject> updateVersion(Version actualVersion, Version editedVersion) {
    //1. get all versions from actualVersion.getTtfnid(); and sort it by from asc
    List<Version> versionsVersioned = versionRepository.getAllVersionsVersioned(
        actualVersion.getTtfnid());

    //2. get edited properties from editedVersion
    List<AttributeObject> editedAttributeObjects = getEditedAttributeObjects(actualVersion,
        editedVersion);

    List<ToVersioning> toVersions = new ArrayList<>();
    for (Version version : versionsVersioned) {
      toVersions.add(new ToVersioning(version.getId(), version, getAttributeObjects(version)));
    }

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(actualVersion,
        editedVersion, editedAttributeObjects, toVersions);

    for (VersionedObject versionedObject : versionedObjects){
      if(VersioningAction.NOT_TOUCHED.equals(versionedObject.getAction())){
        log(versionedObject);
        //nothing to do
      }
      if(VersioningAction.UPDATE.equals(versionedObject.getAction())){
        log(versionedObject);
        //update existing Version
        Version version = convertVersionedObjectToVersion(versionedObject);
        System.out.println(version);
        versionRepository.save(version);
      }
      if(VersioningAction.NEW.equals(versionedObject.getAction())){
        log.info("A new Version was added. VersionedObject={}", versionedObject);
        //create new version
        Version version = convertVersionedObjectToVersion(versionedObject);
        System.out.println(version);
        versionRepository.save(version);
      }
      if(VersioningAction.DELETE.equals(versionedObject.getAction())){
        log(versionedObject);
        //delete existing version
//        versionRepository.deleteById(versionedObject.getObjectId());
      }
    }
    return versionedObjects;
  }

  private void log(VersionedObject versionedObject) {
    log.info("Version with id={} was {}D. VersionedObject={}",
        versionedObject.getObjectId(),
        versionedObject.getAction(),
        versionedObject);
  }

  private Version convertVersionedObjectToVersion(VersionedObject versionedObject){
    List<AttributeObject> attributeObjects = versionedObject.getAttributeObjects();
    Long objectId = versionedObject.getObjectId();
    Version version = new Version();
    version.setId(objectId);
    version.setValidFrom(versionedObject.getValidFrom());
    version.setValidTo(versionedObject.getValidTo());
    for (AttributeObject attributeObject: attributeObjects){
      ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
          version);
      propertyAccessor.setPropertyValue(attributeObject.getKey(),attributeObject.getValue());
    }
    return version;
  }

  private List<AttributeObject> getAttributeObjects(Version version) {

    List<AttributeObject> editedAttributeObjects = new ArrayList<>();
    editedAttributeObjects.add(versionableService.getAttributeObject(
        version.getId(), Fields.name, version.getName()));
    editedAttributeObjects.add(versionableService.getAttributeObject(
        version.getId(), Fields.number, version.getNumber()));
    editedAttributeObjects.add(versionableService.getAttributeObject(
        version.getId(), Fields.swissTimetableFieldNumber, version.getSwissTimetableFieldNumber()));
    editedAttributeObjects.add(versionableService.getAttributeObject(
        version.getId(), Fields.ttfnid, version.getTtfnid()));
    return editedAttributeObjects;
  }


  private List<AttributeObject> getEditedAttributeObjects(Version actualVersion,
      Version editedVersion) {
    List<AttributeObject> editedAttributeObjects = new ArrayList<>();
    if (editedVersion.getName() != null) {
      editedAttributeObjects.add(versionableService.getAttributeObject(
          actualVersion.getId(), Fields.name, editedVersion.getName()));
    }
    if (editedVersion.getNumber() != null) {
      editedAttributeObjects.add(versionableService.getAttributeObject(
          actualVersion.getId(),  Fields.number, editedVersion.getNumber()));
    }
    if (editedVersion.getSwissTimetableFieldNumber() != null) {
      editedAttributeObjects.add(versionableService.getAttributeObject(
          actualVersion.getId(), Fields.swissTimetableFieldNumber,
          editedVersion.getSwissTimetableFieldNumber()));
    }
    return editedAttributeObjects;
  }


}
