package ch.sbb.atlas.versioning.service;

import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ToVersionableMapper {

  private ToVersionableMapper(){
    throw new VersioningException();
  }

  public static <T extends Versionable> T convert(
      VersionedObject versionedObject, Class<T> clazz) {
    try {
      return toVersionable(versionedObject, clazz);
    } catch (Exception e) {
      throw new VersioningException("Could not convert VersionedObject to Versionable", e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Versionable> T toVersionable(VersionedObject versionedObject,
      Class<T> clazz)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    Entity entity = versionedObject.getEntity();

    T versionable = clazz.getConstructor().newInstance();

    if (entity.getId() != null) {
      versionable.setId(entity.getId());
    }
    versionable.setValidFrom(versionedObject.getValidFrom());
    versionable.setValidTo(versionedObject.getValidTo());
    Class<? extends Versionable> versionableClass = versionable.getClass();

    for (Property property : entity.getProperties()) {
      Field field = getAccessibleField(versionableClass, property.getKey());

      if (property.hasOneToOneRelation()) {
        throw new VersioningException("OneToOneRelation not implemented!");
      } else if (property.hasOneToManyRelation()) {
        Collection<Object> relations = (Collection<Object>) field.get(versionable);
        for (Entity entityRelation : property.getOneToMany()) {
          Object relationElement = newInstanceOfCollectionType(versionableClass, property.getKey());

          for (Property propertyRelation : entityRelation.getProperties()) {
            Field relationField = getAccessibleField(relationElement.getClass(),
                propertyRelation.getKey());
            relationField.set(relationElement, propertyRelation.getValue());
          }

          Field versionableReference = getAccessibleField(relationElement.getClass(),
              getPropertyName(versionableClass));
          versionableReference.set(relationElement, versionable);

          relations.add(relationElement);
        }
        field.set(versionable, relations);
      } else {
        field.set(versionable, property.getValue());
      }
    }
    return versionable;
  }

  private static String getPropertyName(Class<? extends Versionable> className) {
    String simpleName = className.getSimpleName();
    String firstChar = simpleName.substring(0, 1).toLowerCase();
    return firstChar + simpleName.substring(1);
  }

  private static Object newInstanceOfCollectionType(Class<?> clazz, String fieldName) {
    try {
      ParameterizedType collectionType = (ParameterizedType) clazz.getDeclaredField(
          fieldName).getGenericType();
      Type[] actualTypeArguments = collectionType.getActualTypeArguments();
      if (actualTypeArguments.length != 1) {
        throw new VersioningException("Expected a generic depth of one");
      }
      String collectionClassName = actualTypeArguments[0].getTypeName();
      return Class.forName(collectionClassName).getConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static Field getAccessibleField(Class<?> clazz, String fieldName)
      throws NoSuchFieldException {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }
}
