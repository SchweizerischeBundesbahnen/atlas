package ch.sbb.timetable.field.number.versioning.service;

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

  public List<VersionedObject> applyVersioning(Versionable current,
      Entity editedEntity, List<ToVersioning> objectsToVersioning) {
    throw new RuntimeException("You have to implement me!!");
  }

  public List<VersionedObject> applyVersioning(Versionable edited, Versionable current,
      List<ToVersioning> objectsToVersioning, Entity editedEntity) {
    throw new RuntimeException("You have to implement me!!");
  }

  protected Entity replaceChangedAttributeWithActualAttribute(Long objectId,
      Entity editedEntity, Entity currentEntity) {

    //Copy currentEntity
    List<Property> properties = new ArrayList<>(currentEntity.getProperties());

    for (Property editedProperty : editedEntity.getProperties()) {
      //find the index of the edited attribute end replace it with the new value
      int index = IntStream.range(0, properties.size())
                           .filter(i -> currentEntity.getProperties().get(i)
                                                     .getKey()
                                                     .equals(editedProperty.getKey()))
                           .findFirst().orElse(-1);
      if (index >= 0) {
        Property replacedProperty = replaceProperty(editedProperty,
            properties.get(index));
        properties.set(index, replacedProperty);
      }

    }
    Entity entity = Entity.builder()
                          .id(objectId)
                          .properties(properties)
                          .build();
    return entity;
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
    return buildVersionedObject(validFrom, validTo, entity, VersioningAction.NEW);
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

  private Property replaceProperty(Property editedProperty,
      Property currentProperty) {
    return Property
        .builder()
        .key(currentProperty.getKey())
        .value(editedProperty.getValue())
        .build();
  }
}
