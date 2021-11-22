package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.findObjectToVersioning;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.util.List;

public class VersioningWhenValidFromAndValidToAreNotEdited implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    ToVersioning toVersioning = findObjectToVersioning(vd.getCurrentVersion(), vd.getObjectsToVersioning());
    Entity entity = Entity.replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());

    vd.getVersionedObjects().add(buildVersionedObjectToUpdate(vd.getCurrentVersion().getValidFrom(),
        vd.getCurrentVersion().getValidTo(), entity));
    return vd.getVersionedObjects();
  }
}
