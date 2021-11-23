package ch.sbb.timetable.field.number.versioning.model;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
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

  private LocalDate validFrom;

  private LocalDate validTo;

  private final Entity entity;

  private VersioningAction action;

  public static VersionedObject buildVersionedObjectToUpdate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    return buildVersionedObject(validFrom, validTo, entity, VersioningAction.UPDATE);
  }

  public static VersionedObject buildVersionedObjectNotTouched(LocalDate validFrom,
      LocalDate validTo,
      Entity entity) {
    return buildVersionedObject(validFrom, validTo, entity, VersioningAction.NOT_TOUCHED);
  }

  public static VersionedObject buildVersionedObjectToCreate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    //Copy entity and setId=null to ensure that we do not override an existing entity
    Entity entityToCreate = Entity.builder().id(null).properties(entity.getProperties()).build();
    return buildVersionedObject(validFrom, validTo, entityToCreate, VersioningAction.NEW);
  }

  public static List<VersionedObject> fillNotTouchedVersionedObject(
      List<ToVersioning> objectsToVersioning,
      List<ToVersioning> objectToVersioningFound) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    List<ToVersioning> objectsToVersioningNotFound =
        objectsToVersioning.stream()
                           .filter(toVersioning ->
                               objectToVersioningFound.stream()
                                                      .noneMatch(toVersioningFound ->
                                                          toVersioningFound.equals(toVersioning))
                           ).collect(Collectors.toList());
    objectsToVersioningNotFound.forEach(toVersioning -> versionedObjects.add(
        buildVersionedObjectNotTouched(toVersioning.getValidFrom(),
            toVersioning.getValidTo(), toVersioning.getEntity())));
    return versionedObjects;
  }

  private static VersionedObject buildVersionedObject(LocalDate validFrom, LocalDate validTo,
      Entity entity,
      VersioningAction action) {
    if (VersioningAction.NEW.equals(action) && entity.getId() != null) {
      throw new VersioningException(
          "To create a new VersionedObject the entity id must be null, to avoid to override an existing Entity.\n"
              + entity);
    }
    return VersionedObject.builder()
                          .validFrom(validFrom)
                          .validTo(validTo)
                          .entity(entity)
                          .action(action)
                          .build();
  }

}
