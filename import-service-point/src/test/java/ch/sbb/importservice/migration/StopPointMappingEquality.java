package ch.sbb.importservice.migration;

import static ch.sbb.atlas.imports.util.CsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record StopPointMappingEquality(StopPointCsvModel didokCsvLine, StopPointVersionCsvModel atlasCsvLine) {

  public static final String PIPE_SEPARATOR = "\\|";

  public void performCheck() {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
    assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
    if (atlasCsvLine.getAssistanceAvailability() != null && didokCsvLine.getAssistanceAvailability() != null) {
      assertThat(atlasCsvLine.getAssistanceAvailability()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceAvailability()).toString());
    }
    assertThat(atlasCsvLine.getAddress()).isEqualTo(didokCsvLine.getAddress());
    assertThat(atlasCsvLine.getZipCode()).isEqualTo(didokCsvLine.getZipCode());
    assertThat(atlasCsvLine.getCity()).isEqualTo(didokCsvLine.getCity());
    assertThat(atlasCsvLine.getFreeText()).isEqualTo(didokCsvLine.getFreeText());
    if (atlasCsvLine.getAlternativeTransport() != null && didokCsvLine.getAlternativeTransport() != null) {
      assertThat(atlasCsvLine.getAlternativeTransport()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAlternativeTransport()).toString());
    }
    assertThat(atlasCsvLine.getAlternativeTransportCondition()).isEqualTo(didokCsvLine.getAlternativeTransportCondition());
    if (atlasCsvLine.getAssistanceAvailability() != null && didokCsvLine.getAssistanceAvailability() != null) {
      assertThat(atlasCsvLine.getAssistanceAvailability()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceAvailability()).toString());
    }
    assertThat(atlasCsvLine.getAssistanceCondition()).isEqualTo(didokCsvLine.getAssistanceCondition());
    if (atlasCsvLine.getAssistanceService() != null && didokCsvLine.getAssistanceService() != null) {
      assertThat(atlasCsvLine.getAssistanceService()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceService()).toString());
    }
    if (atlasCsvLine.getAudioTicketMachine() != null && didokCsvLine.getAudioTickMach() != null) {
      assertThat(atlasCsvLine.getAudioTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAudioTickMach()).toString());
    }
    assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(didokCsvLine.getCompInfos());
    if (atlasCsvLine.getDynamicAudioSystem() != null && didokCsvLine.getDynamicAudioSys() != null) {
      assertThat(atlasCsvLine.getDynamicAudioSystem()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicAudioSys()).toString());
    }
    if (atlasCsvLine.getDynamicOpticSystem() != null && didokCsvLine.getDynamicOpticSys() != null) {
      assertThat(atlasCsvLine.getDynamicOpticSystem()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicOpticSys()).toString());
    }
    assertThat(atlasCsvLine.getInfoTicketMachine()).isEqualTo(didokCsvLine.getInfoTickMach());
    assertThat(atlasCsvLine.getInteroperable()).isEqualTo(mapInteroperableFromDidok(didokCsvLine.getInteroperable()));
    assertThat(atlasCsvLine.getUrl()).isEqualTo(didokCsvLine.getUrl());
    if (atlasCsvLine.getVisualInfo() != null && didokCsvLine.getVisualInfos() != null) {
      assertThat(atlasCsvLine.getVisualInfo()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getVisualInfos()).toString());
    }
    if (atlasCsvLine.getWheelchairTicketMachine() != null && didokCsvLine.getWheelchairTickMach() != null) {
      assertThat(atlasCsvLine.getWheelchairTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getWheelchairTickMach()).toString());
    }
    if (atlasCsvLine.getAssistanceRequestFulfilled() != null && didokCsvLine.getAssistanceReqsFulfilled() != null) {
      assertThat(atlasCsvLine.getAssistanceRequestFulfilled()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceReqsFulfilled()).toString());
    }
    if (atlasCsvLine.getTicketMachine() != null && didokCsvLine.getTicketMachine() != null) {
      assertThat(atlasCsvLine.getTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getTicketMachine()).toString());
    }

    assertThat(mapPipedMeansOfTransport(atlasCsvLine.getMeansOfTransport())).containsAll(MeanOfTransport.fromCode(didokCsvLine.getTransportationMeans()));
    assertThat(dateFromString(atlasCsvLine.getValidFrom())).isEqualTo(didokCsvLine.getValidFrom());
    assertThat(dateFromString(atlasCsvLine.getValidTo())).isEqualTo(didokCsvLine.getValidTo());
    assertThat(atlasCsvLine.getCreator()).isEqualTo(didokCsvLine.getAddedBy());
    assertThat(atlasCsvLine.getEditor()).isEqualTo(didokCsvLine.getModifiedBy());
    assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
    assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

  }

  String mapInteroperableFromDidok(Integer integer){
    if(integer == null){
      return null;
    }
    if(integer.equals(0)){
      return "false";
    }
    return "true";
  }

  private List<MeanOfTransport> mapPipedMeansOfTransport(String meansOfTransport){
    String[] split = meansOfTransport.split(PIPE_SEPARATOR);
    List<MeanOfTransport> meanOfTransports =  new ArrayList<>();
    Arrays.asList(split).forEach(s -> meanOfTransports.add(MeanOfTransport.valueOf(s)));
    return meanOfTransports;
  }

  public LocalDateTime localDateFromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
  }

}
