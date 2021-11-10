package ch.sbb.timetable.field.number.versioning.merge;

import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MergeHelper {

  public static List<VersionedObject> mergeVersionedObject(List<VersionedObject> versionedObjects) {
    versionedObjects.sort(Comparator.comparing(VersionedObject::getValidFrom));
    for (int i = 1; i < versionedObjects.size(); i++) {
      VersionedObject current = versionedObjects.get(i - 1);
      VersionedObject next = versionedObjects.get(i);
      if (current.getEntity().getProperties().equals(next.getEntity().getProperties())
          && areVersionsSequential(current,next)) {
        if (current.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
        }
        next.setValidFrom(current.getValidFrom());
        next.setAction(VersioningAction.UPDATE);
      }
    }
    return versionedObjects;
  }

  static boolean areVersionsSequential(VersionedObject current, VersionedObject next){
    return current.getValidTo().plusDays(1).equals(next.getValidFrom());
  }


}
