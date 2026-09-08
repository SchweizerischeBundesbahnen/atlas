package ch.sbb.atlas.versioning.helper;

import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class MergeHelper {

  public List<VersionedObject> mergeVersionedObject(List<VersionedObject> versionedObjects) {
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
        } else if (next.getEntity().getId() == null) {
          //After versioning we have 2 new sequential versions. In this case we merge them together,
          //we mark the current version to be deleted and the next to be created.
          current.setAction(VersioningAction.DELETE);
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.NEW);
        } else {
          current.setAction(VersioningAction.DELETE);
          next.setValidFrom(current.getValidFrom());
          next.setAction(VersioningAction.UPDATE);
        }
      }
    }
    return versionedObjects;
  }

  private List<Property> getNotIgnoredCurrentProperties(VersionedObject versionedObject) {
    return versionedObject.getEntity().getPropertiesWithoutIgnore();
  }

  boolean areVersionedObjectsSequential(VersionedObject current, VersionedObject next) {
    return DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom());
  }

}
