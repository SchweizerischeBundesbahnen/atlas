package ch.sbb.atlas.versioning.service;

import static ch.sbb.atlas.versioning.model.VersioningAction.DELETE;
import static ch.sbb.atlas.versioning.model.VersioningAction.NEW;
import static ch.sbb.atlas.versioning.model.VersioningAction.NOT_TOUCHED;
import static ch.sbb.atlas.versioning.model.VersioningAction.UPDATE;
import static java.util.Collections.unmodifiableList;

import ch.sbb.atlas.versioning.annotation.AtlasAnnotationProcessor;
import ch.sbb.atlas.versioning.engine.VersioningEngine;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionableServiceImpl implements VersionableService {

  private final VersioningEngine versioningEngine;
  private final AtlasAnnotationProcessor atlasAnnotationProcessor;

  public VersionableServiceImpl() {
    this.versioningEngine = new VersioningEngine();
    this.atlasAnnotationProcessor = new AtlasAnnotationProcessor();
  }

  @Override
  public <T extends Versionable> List<VersionedObject> versioningObjects(Versionable current,
      Versionable editedVersion,
      List<T> currentVersions) {

    List<VersionableProperty> versionableProperties = atlasAnnotationProcessor.getVersionableProperties(
        current);

    logStarting(current, editedVersion, currentVersions);
    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(current,
        editedVersion, currentVersions, versionableProperties
    );

    logDone(versionedObjects);
    return unmodifiableList(versionedObjects);
  }

  private <T extends Versionable> void logStarting(Versionable current, Versionable edited,
      List<T> currentVersions) {
    log.info("Versioning start...");
    log.info("Current version: {}", current);
    log.info("Edited version: {}", edited);
    log.info("Got {} versions to process: {}", currentVersions.size(), currentVersions);
  }

  private void logDone(
      List<VersionedObject> versionedObjects) {
    log.info("Got {} Versioned objects: {}", versionedObjects.size(), versionedObjects);
    log.info("Versioning done.");
  }

  @Override
  public <T extends Versionable> void applyVersioning(Class<T> clazz,
      List<VersionedObject> versionedObjects, Consumer<T> save, LongConsumer deleteById) {
    for (VersionedObject versionedObject : versionedObjects) {
      if (NOT_TOUCHED.equals(versionedObject.getAction())) {
        log(versionedObject);
      }
      if (UPDATE.equals(versionedObject.getAction())) {
        log(versionedObject);
        T version = ToVersionableMapper.convert(versionedObject,
            clazz);
        save.accept(version);
      }
      if (NEW.equals(versionedObject.getAction())) {
        log.info("A new Version was added. VersionedObject={}", versionedObject);
        T version = ToVersionableMapper.convert(versionedObject,
            clazz);
        //ensure version.getId() == null to avoid to update a Version
        version.setId(null);
        save.accept(version);
      }
      if (DELETE.equals(versionedObject.getAction())) {
        log(versionedObject);
        deleteById.accept(versionedObject.getEntity().getId());
      }
    }
  }

  private void log(VersionedObject versionedObject) {
    log.info("Version with id={} was {}D. VersionedObject={}",
        versionedObject.getEntity().getId(),
        versionedObject.getAction(),
        versionedObject);
  }
}
