package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BasePointUtility {

  public <T extends Versionable> T getCurrentPointVersion(List<T> dbVersions, T edited) {
    dbVersions.sort(Comparator.comparing(Versionable::getValidFrom));

    Optional<T> currentVersionMatch = dbVersions.stream()
        .filter(dbVersion -> {
              // match validFrom
              if (edited.getValidFrom().isEqual(dbVersion.getValidFrom())) {
                return true;
              }
              // match validTo
              if (edited.getValidTo().isEqual(dbVersion.getValidTo())) {
                return true;
              }
              // match edited version between dbVersion
              if (edited.getValidFrom().isAfter(dbVersion.getValidFrom()) && edited.getValidTo().isBefore(dbVersion.getValidTo())) {
                return true;
              }
              // match 1 or more dbVersion/s between edited version
              return dbVersion.getValidFrom().isAfter(edited.getValidFrom()) && dbVersion.getValidTo()
                  .isBefore(edited.getValidTo());
            }
        )
        .findFirst();

    if (currentVersionMatch.isEmpty()) {
      // match edited version after last dbVersion
      if (edited.getValidFrom().isAfter(dbVersions.get(dbVersions.size() - 1).getValidTo())) {
        return dbVersions.get(dbVersions.size() - 1);
      }
      // match edited version before first dbVersion
      if (edited.getValidTo().isBefore(dbVersions.get(0).getValidFrom())) {
        return dbVersions.get(0);
      }
    }

    return currentVersionMatch.orElseThrow(() -> new RuntimeException("Not found current point version"));
  }

  public void addCreateAndEditDetailsToGeolocationPropertyFromVersionedObject(
      VersionedObject versionedObject,
      Property geolocationProp) {
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
  }

}
