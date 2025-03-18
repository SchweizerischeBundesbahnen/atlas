package ch.sbb.exportservice.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RowMapperUtil {

  public static Integer getInteger(ResultSet resultSet, String column) throws SQLException {
    int intValue = resultSet.getInt(column);
    return resultSet.wasNull() ? null : intValue;
  }

  public static Double getDouble(ResultSet resultSet, String column) throws SQLException {
    double doubleValue = resultSet.getDouble(column);
    return resultSet.wasNull() ? null : doubleValue;
  }

  public static <T> Set<T> stringToSet(String values, Function<String, T> mapping) {
    return Arrays.stream(values.split("\\|")).map(mapping).collect(Collectors.toSet());
  }

  public static String stringsToPipedString(Collection<String> collection) {
    return String.join("|", collection);
  }

  public static <T extends Enum<T>> String enumsToPipedString(Collection<T> collection) {
    return collection.stream().map(Enum::name).sorted().collect(Collectors.joining("|"));
  }

}
