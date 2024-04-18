package ch.sbb.atlas.imports.util;

import ch.sbb.atlas.exception.CsvException;
import ch.sbb.atlas.imports.ImportDataModifier;
import ch.sbb.atlas.imports.Importable;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
@Deprecated(since = "2.0.0")
public class ImportUtils {

  private static final String EDITION_DATE_FIELD_NAME = "editionDate";
  private static final String EDITOR_FIELD_NAME = "editor";
  public static final LocalDate DIDOK_HIGEST_DATE = LocalDate.of(2099, 12, 31);
  public static final LocalDate ATLAS_HIGHEST_DATE = LocalDate.of(9999, 12, 31);

  public static <T extends ImportDataModifier> void replaceNewLinesAndReplaceToDateWithHighestDate(List<T> csvModels) {
    try {
      replaceNewLines(csvModels);
    } catch (IllegalAccessException e) {
      throw new CsvException(e);
    }
    replaceToDateWithHighestDate(csvModels);
  }

  static <T extends ImportDataModifier> void replaceNewLines(List<T> csvModels) throws IllegalAccessException {
    Pattern pattern = Pattern.compile("\\$newline\\$");
    for (T csvModel : csvModels) {
      for (Field field : csvModel.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        Object value = field.get(csvModel);
        if (value instanceof String) {
          Matcher matcher = pattern.matcher((String) value);
          if (matcher.find()) {
            field.set(csvModel, matcher.replaceAll("\r\n"));
            csvModel.setLastModifiedToNow();
          }
        }
      }
    }
  }

  static <T extends ImportDataModifier> void replaceToDateWithHighestDate(List<T> csvModels) {
    for (T csvModel : csvModels) {
      if(DIDOK_HIGEST_DATE.isEqual(csvModel.getValidTo())){
        csvModel.setValidTo(ATLAS_HIGHEST_DATE);
        csvModel.setLastModifiedToNow();
      }
    }
  }


  public <T extends Versionable> T getCurrentPointVersion(List<T> dbVersions, T edited) {
    dbVersions.sort(Comparator.comparing(Versionable::getValidFrom));

    Optional<T> currentVersionMatch = dbVersions.stream()
        .filter(dbVersion -> {
              // match validFrom
              if (edited.getValidFrom().isEqual(dbVersion.getValidFrom())) {
                return true;
              }
              // match validTo
              if (edited.getValidTo().isEqual(dbVersion.getValidTo())) {
                return true;
              }
              // match edited version between dbVersion
              if (edited.getValidFrom().isAfter(dbVersion.getValidFrom()) && edited.getValidTo().isBefore(dbVersion.getValidTo())) {
                return true;
              }
              if (edited.getValidFrom().isAfter(dbVersion.getValidFrom()) && edited.getValidFrom().isBefore(dbVersion.getValidTo())) {
                return true;
              }
              if (edited.getValidTo().isAfter(dbVersion.getValidFrom()) && edited.getValidTo().isBefore(dbVersion.getValidTo())) {
                return true;
              }
              // match 1 or more dbVersion/s between edited version
              return dbVersion.getValidFrom().isAfter(edited.getValidFrom()) && dbVersion.getValidTo()
                  .isBefore(edited.getValidTo());
            }
        )
        .findFirst();

    if (currentVersionMatch.isEmpty()) {
      // match edited version after last dbVersion
      if (edited.getValidFrom().isAfter(dbVersions.get(dbVersions.size() - 1).getValidTo())) {
        return dbVersions.get(dbVersions.size() - 1);
      }
      // match edited version before first dbVersion
      if (edited.getValidTo().isBefore(dbVersions.get(0).getValidFrom())) {
        return dbVersions.get(0);
      }
    }

    return currentVersionMatch.orElseThrow(() -> new RuntimeException("Not found current point version"));
  }

  public <T extends Versionable> List<T> findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<T> versions) {
    List<T> collected = versions.stream()
        .filter(toVersioning -> !toVersioning.getValidFrom().isAfter(editedValidTo))
        .filter(toVersioning -> !toVersioning.getValidTo().isBefore(editedValidFrom))
        .toList();
    if (!collected.isEmpty() &&
        (collected.get(0).getValidFrom().equals(editedValidFrom) && collected.get(collected.size() - 1).getValidTo()
            .equals(editedValidTo))) {
      return collected;
    }
    return Collections.emptyList();
  }

  public <T extends Importable> void overrideEditionDateAndEditorOnVersionedObjects(
      T version,
      List<VersionedObject> versionedObjects) {
    versionedObjects.stream().filter(versionedObject -> {
      final VersioningAction action = versionedObject.getAction();
      return action == VersioningAction.UPDATE || action == VersioningAction.NEW;
    }).forEach(versionedObject -> {
      final Property editionDate = getPropertyFromFieldOnVersionedObject(
          EDITION_DATE_FIELD_NAME,
          versionedObject
      );
      final Property editor = getPropertyFromFieldOnVersionedObject(
          EDITOR_FIELD_NAME,
          versionedObject
      );
      editionDate.setValue(version.getEditionDate());
      editor.setValue(version.getEditor());
    });
  }

  private Property getPropertyFromFieldOnVersionedObject(String fieldName, VersionedObject versionedObject) {
    return versionedObject
        .getEntity()
        .getProperties()
        .stream()
        .filter(property -> property.getKey().equals(fieldName))
        .findFirst().orElseThrow();
  }


}
