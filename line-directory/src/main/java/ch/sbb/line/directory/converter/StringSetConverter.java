package ch.sbb.line.directory.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {
  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(Set<String> stringList) {
    return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
  }

  @Override
  public Set<String> convertToEntityAttribute(String string) {
    return string != null ? new HashSet<>(Arrays.asList(string.split(SPLIT_CHAR))) : Collections.emptySet();
  }
}
