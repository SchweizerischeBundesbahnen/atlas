package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.entity.StopPointVersion;
import ch.sbb.exportservice.entity.StopPointVersion.StopPointVersionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class StopPointVersionRowMapper extends BaseRowMapper implements RowMapper<StopPointVersion> {

  @Override
  public StopPointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    StopPointVersionBuilder<?, ?> builder = StopPointVersion.builder();
    builder.id(rs.getLong("id"));
    builder.number(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.sloid(rs.getString("sloid"));
    builder.freeText(rs.getString("free_text"));
    builder.address(rs.getString("address"));
    builder.zipCode(rs.getString("zip_code"));
    builder.city(rs.getString("city"));
    builder.alternativeTransport(
        rs.getObject("alternative_transport") != null ?
            StandardAttributeType.valueOf(rs.getString("alternative_transport")) : null);
    builder.alternativeTransportCondition(rs.getString("alternative_transport_condition"));
    builder.assistanceAvailability(
        rs.getObject("assistance_availability") != null ?
            StandardAttributeType.valueOf(rs.getString("assistance_availability")) : null);
    builder.assistanceCondition(rs.getString("assistance_condition"));
    builder.assistanceService(
        rs.getObject("assistance_service") != null ?
            StandardAttributeType.valueOf(rs.getString("assistance_service")) : null);
    builder.audioTicketMachine(
        rs.getObject("audio_ticket_machine") != null ?
            StandardAttributeType.valueOf(rs.getString("audio_ticket_machine")) : null);
    builder.additionalInformation(rs.getString("additional_information"));
    builder.dynamicAudioSystem(
        rs.getObject("dynamic_audio_system") != null ?
            StandardAttributeType.valueOf(rs.getString("dynamic_audio_system")) : null);
    builder.dynamicOpticSystem(
        rs.getObject("dynamic_optic_system") != null ?
            StandardAttributeType.valueOf(rs.getString("dynamic_optic_system")) : null);
    builder.infoTicketMachine(rs.getString("info_ticket_machine"));
    builder.interoperable(mapBooleanObject(rs));
    builder.url(rs.getString("url"));
    builder.wheelchairTicketMachine(
        rs.getObject("wheelchair_ticket_machine") != null ?
            StandardAttributeType.valueOf(rs.getString("wheelchair_ticket_machine")) : null);
    builder.assistanceRequestFulfilled(
        rs.getObject("assistance_request_fulfilled") != null ?
            BooleanOptionalAttributeType.valueOf(rs.getString("assistance_request_fulfilled")) : null);
    builder.ticketMachine(
        rs.getObject("ticket_machine") != null ?
                BooleanOptionalAttributeType.valueOf(rs.getString("ticket_machine")) : null);
    setMeansOfTransport(builder, rs.getString("list_of_transports"));
    builder.validFrom(rs.getObject("valid_from", LocalDate.class));
    builder.validTo(rs.getObject("valid_to", LocalDate.class));
    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    builder.creator(rs.getString("creator"));
    builder.editor(rs.getString("editor"));
    builder.version(rs.getInt("version"));
    return builder.build();
  }

  void setMeansOfTransport(StopPointVersionBuilder<?, ?> stopPointVersionBuilder, String listOfMeansOfTransport) {
    if (listOfMeansOfTransport != null) {
      Set<MeanOfTransport> meansOfTransport = RowMapperUtil.stringToSet(listOfMeansOfTransport, MeanOfTransport::valueOf);
      stopPointVersionBuilder.meansOfTransport(meansOfTransport);
      stopPointVersionBuilder.meansOfTransportPipeList(RowMapperUtil.toPipedString(meansOfTransport));
    }
  }

  private String mapBooleanObject(ResultSet rs) throws SQLException {
    if (rs.getObject("interoperable") != null) {
      return String.valueOf(rs.getBoolean("interoperable"));
    }
    return StringUtils.EMPTY;
  }

}
