package ch.sbb.atlas.base.service.versioning.model;

import static ch.sbb.atlas.base.service.versioning.model.VersioningAction.NEW;
import static ch.sbb.atlas.base.service.versioning.model.VersioningAction.NOT_TOUCHED;
import static ch.sbb.atlas.base.service.versioning.model.VersioningAction.UPDATE;

import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VersionedObject {

  private final Entity entity;
  private LocalDate validFrom;
  private LocalDate validTo;
  private VersioningAction action;

  public static VersionedObject buildVersionedObjectToUpdate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    return buildVersionedObject(validFrom, validTo, entity, UPDATE);
  }

  public static VersionedObject buildVersionedObjectNotTouched(LocalDate validFrom,
      LocalDate validTo,
      Entity entity) {
    return buildVersionedObject(validFrom, validTo, entity, NOT_TOUCHED);
  }

  public static VersionedObject buildVersionedObjectToCreate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    //Copy entity and setId=null to ensure that we do not override an existing entity
    Entity entityToCreate = Entity.builder().id(null).properties(entity.getProperties()).build();
    return buildVersionedObject(validFrom, validTo, entityToCreate, NEW);
  }

  public static List<VersionedObject> fillNotTouchedVersionedObject(
      List<ToVersioning> objectsToVersioning,
      List<ToVersioning> objectToVersioningFound) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    List<ToVersioning> objectsToVersioningNotFound =
        objectsToVersioning
            .stream()
            .filter(toVersioning ->
                objectToVersioningFound
                    .stream()
                    .noneMatch(toVersioningFound -> toVersioningFound.equals(toVersioning))
            ).collect(Collectors.toList());

    objectsToVersioningNotFound.forEach(
        toVersioning -> versionedObjects.add(
            buildVersionedObjectNotTouched(toVersioning.getValidFrom(),
                toVersioning.getValidTo(), toVersioning.getEntity())
        )
    );
    return versionedObjects;
  }

  static VersionedObject buildVersionedObject(LocalDate validFrom, LocalDate validTo,
      Entity entity,
      VersioningAction action) {
    if (NEW == action && entity.getId() != null) {
      throw new VersioningException(
          "To create a new VersionedObject the entity id must be null, to avoid to override an existing Entity.\n"
              + entity);
    }
    if (validTo.isBefore(validFrom)) {
      throw new VersioningException(
          "ValidFrom: " + validFrom + " is bigger than validTo: " + validTo);
    }
    return VersionedObject.builder()
                          .validFrom(validFrom)
                          .validTo(validTo)
                          .entity(entity)
                          .action(action)
                          .build();
  }

}
