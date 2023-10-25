package ch.sbb.exportservice.reader;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.entity.StopPointVersion;
import ch.sbb.exportservice.entity.StopPointVersion.StopPointVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.RowMapper;

public class StopPointVersionRowMapper extends BaseRowMapper implements RowMapper<StopPointVersion> {

  @Override
  public StopPointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    StopPointVersionBuilder<?, ?> builder = StopPointVersion.builder();
    builder.id(rs.getLong("id"));
    builder.number(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.sloid(rs.getString("sloid"));
    builder.validFrom(rs.getObject("valid_from", LocalDate.class));
    builder.validTo(rs.getObject("valid_to", LocalDate.class));
    builder.designation(rs.getString("designation"));

    setMeansOfTransport(builder, rs.getString("list_of_transports"));

    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    builder.creator(rs.getString("creator"));
    builder.editor(rs.getString("editor"));
    builder.version(rs.getInt("version"));
    return builder.build();
  }

  void setMeansOfTransport(StopPointVersionBuilder<?, ?> stopPointVersionBuilder, String listOfMeansOfTransport) {
    if (listOfMeansOfTransport != null) {
      Set<MeanOfTransport> meansOfTransport = stringToSet(listOfMeansOfTransport, MeanOfTransport::valueOf);

      stopPointVersionBuilder.meansOfTransport(meansOfTransport);
      stopPointVersionBuilder.meansOfTransportPipeList(toPipedString(meansOfTransport));
    }
  }

  //TODO: remove duplication
  private <T> Set<T> stringToSet(String values, Function<String, T> enumType) {
    return Arrays.stream(values.split("\\|")).map(enumType).collect(Collectors.toSet());
  }

  private String toPipedString(Collection<? extends Enum<?>> collection) {
    return collection.stream().map(Enum::name).sorted().collect(Collectors.joining("|"));
  }

}
