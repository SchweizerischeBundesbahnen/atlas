package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.util.List;

public class VersioningWhenValidFromAndValidToAreNotEdited extends Versioning {

  @Override
  public List<VersionedObject> applyVersioning(Versionable current, Entity editedEntity,
      List<ToVersioning> objectsToVersioning) {
    ToVersioning toVersioning = objectsToVersioning
        .stream()
        .filter(versioning -> versioning.getEntity().getId().equals(current.getId()))
        .findFirst()
        .orElse(null);

    VersionedObject versionedObjectToUpdate =
        VersionedObject.builder()
                       .validFrom(current.getValidFrom())
                       .validTo(current.getValidTo())
                       .entity(
                           replaceChangedAttributeWithActualAttribute(current.getId(),editedEntity,
                               toVersioning.getEntity())
                       )
                       .action(VersioningAction.UPDATE)
                       .build();
    return List.of(versionedObjectToUpdate);
  }
}
