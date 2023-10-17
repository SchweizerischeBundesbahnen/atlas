package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BasePointUtility {

  /**
   * Sets the values for the properties {@link BaseDidokImportEntity.Fields.creationDate},
   * {@link BaseDidokImportEntity.Fields.creator}, {@link BaseDidokImportEntity.Fields.editor} and
   * {@link BaseDidokImportEntity.Fields.editionDate} on the child PointGeolocation from the parent
   * PointVersion.
   */
  public void addCreateAndEditDetailsToGeolocationPropertyFromVersionedObjects(
      List<VersionedObject> versionedObjects,
      String geolocationField) {
    versionedObjects.stream().filter(versionedObject -> {
      final VersioningAction action = versionedObject.getAction();
      return action == VersioningAction.UPDATE || action == VersioningAction.NEW;
    }).forEach(versionedObject -> {
      final Property geolocationProp = getPropertyFromFieldOnVersionedObject(geolocationField, versionedObject);
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
  }

  public <T extends BaseDidokImportEntity> void overrideEditionDateAndEditorOnVersionedObjects(
      T version,
      List<VersionedObject> versionedObjects) {
    versionedObjects.stream().filter(versionedObject -> {
      final VersioningAction action = versionedObject.getAction();
      return action == VersioningAction.UPDATE || action == VersioningAction.NEW;
    }).forEach(versionedObject -> {
      final Property editionDate = getPropertyFromFieldOnVersionedObject(
          BaseDidokImportEntity.Fields.editionDate,
          versionedObject
      );
      final Property editor = getPropertyFromFieldOnVersionedObject(
          BaseDidokImportEntity.Fields.editor,
          versionedObject
      );
      editionDate.setValue(version.getEditionDate());
      editor.setValue(version.getEditor());
    });
  }

  public <T extends Versionable> List<T> findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<T> versions) {
    List<T> collected = versions.stream()
        .filter(toVersioning -> !toVersioning.getValidFrom().isAfter(editedValidTo))
        .filter(toVersioning -> !toVersioning.getValidTo().isBefore(editedValidFrom))
        .collect(Collectors.toList());
    if (!collected.isEmpty() &&
        (collected.get(0).getValidFrom().equals(editedValidFrom) && collected.get(collected.size() - 1).getValidTo()
            .equals(editedValidTo))) {
      return collected;
    }
    return Collections.emptyList();
  }

  private Property getPropertyFromFieldOnVersionedObject(String fieldName, VersionedObject versionedObject) {
    return versionedObject
        .getEntity()
        .getProperties()
        .stream()
        .filter(property -> property.getKey().equals(fieldName))
        .findFirst().orElseThrow();
  }

}
