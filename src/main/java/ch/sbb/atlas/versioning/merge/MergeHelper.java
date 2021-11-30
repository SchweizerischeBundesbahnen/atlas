package ch.sbb.atlas.versioning.merge;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
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
      List<Property> notIgnoredCurrentProperties = getNotIgnoredCurrentProperties(current);
      List<Property> notIgnoredNextProperties = getNotIgnoredCurrentProperties(next);
      if (notIgnoredCurrentProperties.equals(notIgnoredNextProperties)
          && areVersionedObjectsSequential(current, next)) {
        log.info("Following objects marked to be merged: \n1. {} \n2. {}", current, next);
        if (current.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
        }
        next.setValidFrom(current.getValidFrom());
        next.setAction(VersioningAction.UPDATE);
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
