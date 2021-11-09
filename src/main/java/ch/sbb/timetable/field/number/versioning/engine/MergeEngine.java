package ch.sbb.timetable.field.number.versioning.engine;

import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MergeEngine {

  public static List<VersionedObject> mergeVersionedObject(List<VersionedObject> versionedObjects) {

    //1. Sort all VersionedObjects
    versionedObjects.sort(Comparator.comparing(VersionedObject::getValidFrom));
    //2. ForEach VersionedObject
    //    if versionedObject[i] == versionedObject[i-1] && versionedObject[i-1].validTo is just next to versionedObject[i].validFrom
    //    versionedObject[i] and versionedObject[i-1] marked to be merged
    //    iteration i+1
    //    if versionedObject[i+1] == versionedObject[(i+1)-1] && versionedObject[(i+1)-1].validTo is just next to versionedObject[i+1].validFrom
    //    versionedObject[i+1] and versionedObject[(i+1)-1] marked to be merged
    //    iteration i+2
    //    if versionedObject[i+2] == versionedObject[(i+2)-1] && versionedObject[(i+2)-1].validTo is just next to versionedObject[i+2].validFrom
    //    versionedObject[i+2] and versionedObject[(i+2)-1] marked to be merged
    //    iteration i+(n-1)
    //    if versionedObject[i+(n-1)] == versionedObject[(i+(n-1))-1] && versionedObject[(i+(n-1))-1].validTo is just next to versionedObject[i+(n-1)].validFrom
    //    versionedObject[i+(n-1)] and versionedObject[(i+(n-1))-1] marked to be merged

    for (int i = 1; i < versionedObjects.size(); i++) {
      VersionedObject current = versionedObjects.get(i - 1);
      VersionedObject next = versionedObjects.get(i);
      if (current.getEntity().getProperties().equals(next.getEntity().getProperties())) { //TODO: && check that current and next are sequential!!
        if (current.getEntity().getId() != null) {
          current.setAction(VersioningAction.DELETE);
        }
        next.setValidFrom(current.getValidFrom());
        next.setAction(VersioningAction.UPDATE);
      }
    }

    return versionedObjects;
  }


}
