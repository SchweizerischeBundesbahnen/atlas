package ch.sbb.atlas.base.service.versioning.merge;

import ch.sbb.atlas.base.service.versioning.date.DateHelper;
import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import ch.sbb.atlas.base.service.versioning.model.Property;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.model.VersioningAction;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MergeHelper {

  private MergeHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static List<VersionedObject> mergeVersionedObject(List<VersionedObject> versionedObjects) {
    versionedObjects.sort(Comparator.comparing(VersionedObject::getValidFrom));
    for (int i = 1; i < versionedObjects.size(); i++) {
      VersionedObject current = versionedObjects.get(i - 1);
      VersionedObject next = versionedObjects.get(i);
      List<Property> notIgnoredCurrentVersionProperties = getNotIgnoredCurrentProperties(current);
      List<Property> notIgnoredNextVersionProperties = getNotIgnoredCurrentProperties(next);
      if (notIgnoredCurrentVersionProperties.equals(notIgnoredNextVersionProperties)
          && areVersionedObjectsSequential(current, next)) {
        log.info("Following objects marked to be merged: \n1. {} \n2. {}", current, next);
        if (current.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.UPDATE);
        } else if (current.getEntity().getId() == null && next.getEntity().getId() == null) {
          //After versioning we have 2 new sequential versions. In this case we merge them together,
          //we mark the current version to be deleted and the next to be created.
          current.setAction(VersioningAction.DELETE);
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.NEW);
        } else if (current.getEntity().getId() != null && next.getEntity().getId() != null) {
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.UPDATE);
        } else if (current.getEntity().getId() == null && next.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.UPDATE);
        } else {
          throw new VersioningException(
              "Something went wrong during merge. I'm not able to apply versioning.");
        }
      }
    }
    return versionedObjects;
  }

  private static List<Property> getNotIgnoredCurrentProperties(VersionedObject versionedObject) {
    return versionedObject.getEntity()
                          .getProperties()
                          .stream()
                          .filter(property -> !property.isIgnoreDiff())
                          .collect(
                              Collectors.toList());
  }


  static boolean areVersionedObjectsSequential(VersionedObject current, VersionedObject next) {
    return DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom());
  }

}
