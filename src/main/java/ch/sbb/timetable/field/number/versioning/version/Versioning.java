package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Versioning {

  public List<VersionedObject> applyVersioning(Versionable currentVersion,
      Entity editedEntity, List<ToVersioning> objectsToVersioning) {
    throw new IllegalStateException("You have to implement me!!");
  }

  public List<VersionedObject> applyVersioning(Versionable edited, Versionable current,
      List<ToVersioning> objectsToVersioning, Entity editedEntity) {
    throw new IllegalStateException("You have to implement me!!");
  }

  protected Entity replaceEditedPropertiesWithCurrentProperties(Entity editedEntity,
      Entity currentEntity) {

    //Copy currentProperties
    List<Property> properties = new ArrayList<>(currentEntity.getProperties());

    for (Property editedProperty : editedEntity.getProperties()) {
      //find the index of the edited attribute end replace it with the new value
      int index = IntStream.range(0, properties.size())
                           .filter(i -> currentEntity.getProperties().get(i)
                                                     .getKey()
                                                     .equals(editedProperty.getKey()))
                           .findFirst().orElse(-1);
      if (index >= 0) {
        properties.set(index, editedProperty);
      }
    }
    return Entity.builder().id(currentEntity.getId()).properties(properties).build();
  }

  protected ToVersioning findObjectToVersioning(Versionable currentVersion,
      List<ToVersioning> objectsToVersioning) {
    return objectsToVersioning
        .stream()
        .filter(versioning -> versioning.getEntity().getId().equals(currentVersion.getId()))
        .findFirst()
        .orElse(null);
  }

  protected VersionedObject buildVersionedObjectToUpdate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    return buildVersionedObject(validFrom, validTo, entity, VersioningAction.UPDATE);
  }

  protected VersionedObject buildVersionedObjectToCreate(LocalDate validFrom, LocalDate validTo,
      Entity entity) {
    //Copy entity and setId=null to ensure that we do not override an existing entity
    Entity entityToCreate = Entity.builder().id(null).properties(entity.getProperties()).build();
    return buildVersionedObject(validFrom, validTo, entityToCreate, VersioningAction.NEW);
  }

  protected VersionedObject buildVersionedObject(LocalDate validFrom, LocalDate validTo,
      Entity entity,
      VersioningAction action) {
    return VersionedObject.builder()
                          .validFrom(validFrom)
                          .validTo(validTo)
                          .entity(entity)
                          .action(action)
                          .build();
  }

}
