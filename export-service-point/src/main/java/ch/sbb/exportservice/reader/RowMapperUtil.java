package ch.sbb.exportservice.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RowMapperUtil {

  public static Integer getInteger(ResultSet resultSet, String column) throws SQLException {
    int intValue = resultSet.getInt(column);
    return resultSet.wasNull() ? null : intValue;
  }

}
