package ch.sbb.atlas.versioning.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Entity {

  private Long id;

  private List<Property> properties;

  public static Entity replaceEditedPropertiesWithCurrentProperties(Entity editedEntity,
      Entity currentEntity) {

    //Copy currentProperties
    List<Property> properties = new ArrayList<>(currentEntity.getProperties());
    for (Property editedProperty : editedEntity.getProperties()) {
      //find the index of the edited attribute end replace it with the new value
      int editedAttributeIndex = findEditedAttributeIndex(currentEntity, properties,
          editedProperty);
      if (editedAttributeIndex >= 0) {
        properties.set(editedAttributeIndex, editedProperty);
      }
    }
    return Entity.builder().id(currentEntity.getId()).properties(properties).build();
  }

  private static int findEditedAttributeIndex(Entity currentEntity, List<Property> properties,
      Property editedProperty) {
    return IntStream.range(0, properties.size())
                    .filter(i -> currentEntity.getProperties().get(i)
                                              .getKey()
                                              .equals(editedProperty.getKey()))
                    .findFirst().orElse(-1);
  }

}
