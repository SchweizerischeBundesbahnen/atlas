package ch.sbb.timetable.field.number.versioning.merge;

import static ch.sbb.timetable.field.number.versioning.date.DateHelper.areDatesSequential;

import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MergeHelper {

  private MergeHelper(){
    throw new IllegalStateException("Utility class");
  }

  public static List<VersionedObject> mergeVersionedObject(List<VersionedObject> versionedObjects) {
    versionedObjects.sort(Comparator.comparing(VersionedObject::getValidFrom));
    for (int i = 1; i < versionedObjects.size(); i++) {
      VersionedObject current = versionedObjects.get(i - 1);
      VersionedObject next = versionedObjects.get(i);
      if (current.getEntity().getProperties().equals(next.getEntity().getProperties())
          && areVersionedObjectsSequential(current,next)) {
        log.info("Following objects marked to be merged: \n1. {} \n2. {}", current,next);
        if (current.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
        }
        next.setValidFrom(current.getValidFrom());
        next.setAction(VersioningAction.UPDATE);
      }
    }
    return versionedObjects;
  }

  static boolean areVersionedObjectsSequential(VersionedObject current, VersionedObject next){
    return areDatesSequential(current.getValidTo(),next.getValidFrom());
  }


}
