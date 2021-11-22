package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Versioning {

  public List<VersionedObject> applyVersioning(Versionable currentVersion,
      Entity editedEntity, List<ToVersioning> objectsToVersioning) {
    throw new VersioningException("You have to implement me!!");
  }

  public List<VersionedObject> applyVersioning(Versionable edited, Versionable current,
      Entity editedEntity, List<ToVersioning> objectsToVersioning) {
    throw new VersioningException("You have to implement me!!");
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





}
