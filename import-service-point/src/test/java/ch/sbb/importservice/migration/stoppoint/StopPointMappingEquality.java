package ch.sbb.importservice.migration.stoppoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.importservice.migration.MigrationUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.sbb.atlas.imports.util.CsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

public record StopPointMappingEquality(StopPointCsvModel didokCsvLine, StopPointVersionCsvModel atlasCsvLine) {

  public static final String PIPE_SEPARATOR = "\\|";

  public void performCheck() {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
    assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
    if (atlasCsvLine.getAssistanceAvailability() != null) {
      assertThat(atlasCsvLine.getAssistanceAvailability()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceAvailability()).toString());
    } else {
      assertThat(didokCsvLine.getAssistanceAvailability()).isNull();
    }

    assertThat(atlasCsvLine.getAddress()).isEqualTo(didokCsvLine.getAddress());
    assertThat(atlasCsvLine.getZipCode()).isEqualTo(didokCsvLine.getZipCode());
    assertThat(atlasCsvLine.getCity()).isEqualTo(didokCsvLine.getCity());
    assertThat(atlasCsvLine.getFreeText()).isEqualTo(didokCsvLine.getFreeText());
    if (atlasCsvLine.getAlternativeTransport() != null) {
      assertThat(atlasCsvLine.getAlternativeTransport()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAlternativeTransport()).toString());
    } else {
      assertThat(didokCsvLine.getAlternativeTransport()).isNull();
    }
    assertThat(atlasCsvLine.getAlternativeTransportCondition()).isEqualTo(didokCsvLine.getAlternativeTransportCondition());
    if (atlasCsvLine.getAssistanceAvailability() != null) {
      assertThat(atlasCsvLine.getAssistanceAvailability()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceAvailability()).toString());
    } else {
      assertThat(didokCsvLine.getAssistanceAvailability()).isNull();
    }
    assertThat(atlasCsvLine.getAssistanceCondition()).isEqualTo(didokCsvLine.getAssistanceCondition());
    if (atlasCsvLine.getAssistanceService() != null) {
      assertThat(atlasCsvLine.getAssistanceService()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceService()).toString());
    } else {
      assertThat(didokCsvLine.getAssistanceService()).isNull();
    }
    if (atlasCsvLine.getAudioTicketMachine() != null) {
      assertThat(atlasCsvLine.getAudioTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAudioTickMach()).toString());
    } else {
      assertThat(didokCsvLine.getAudioTickMach()).isNull();
    }
    assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(didokCsvLine.getCompInfos());
    if (atlasCsvLine.getDynamicAudioSystem() != null) {
      assertThat(atlasCsvLine.getDynamicAudioSystem()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicAudioSys()).toString());
    } else {
      assertThat(didokCsvLine.getDynamicAudioSys()).isNull();
    }
    if (atlasCsvLine.getDynamicOpticSystem() != null) {
      assertThat(atlasCsvLine.getDynamicOpticSystem()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicOpticSys()).toString());
    } else {
      assertThat(didokCsvLine.getDynamicOpticSys()).isNull();
    }
    assertThat(atlasCsvLine.getInfoTicketMachine()).isEqualTo(didokCsvLine.getInfoTickMach());
    assertThat(atlasCsvLine.getInteroperable()).isEqualTo(mapInteroperableFromDidok(didokCsvLine.getInteroperable()));
    assertThat(atlasCsvLine.getUrl()).isEqualTo(didokCsvLine.getUrl());
    if (atlasCsvLine.getVisualInfo() != null) {
      assertThat(atlasCsvLine.getVisualInfo()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getVisualInfos()).toString());
    } else {
      assertThat(didokCsvLine.getVisualInfos()).isNull();
    }
    if (atlasCsvLine.getWheelchairTicketMachine() != null) {
      assertThat(atlasCsvLine.getWheelchairTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getWheelchairTickMach()).toString());
    } else {
      assertThat(didokCsvLine.getWheelchairTickMach()).isNull();
    }
    if (atlasCsvLine.getAssistanceRequestFulfilled() != null) {
      assertThat(atlasCsvLine.getAssistanceRequestFulfilled()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getAssistanceReqsFulfilled()).toString());
    } else {
      assertThat(didokCsvLine.getAssistanceReqsFulfilled()).isNull();
    }
    if (atlasCsvLine.getTicketMachine() != null) {
      assertThat(atlasCsvLine.getTicketMachine()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getTicketMachine()).toString());
    } else {
      assertThat(didokCsvLine.getTicketMachine()).isNull();
    }

    assertThat(mapPipedMeansOfTransport(atlasCsvLine.getMeansOfTransport())).containsAll(
        MeanOfTransport.fromCode(didokCsvLine.getTransportationMeans()));
    assertThat(dateFromString(atlasCsvLine.getValidFrom())).isEqualTo(didokCsvLine.getValidFrom());
    assertThat(dateFromString(atlasCsvLine.getValidTo())).isEqualTo(didokCsvLine.getValidTo());

    assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
    assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

  }

  String mapInteroperableFromDidok(Integer integer) {
    if (integer == null) {
      return null;
    }
    if (integer.equals(0)) {
      return "false";
    }
    return "true";
  }

  private List<MeanOfTransport> mapPipedMeansOfTransport(String meansOfTransport) {
    String[] split = meansOfTransport.split(PIPE_SEPARATOR);
    List<MeanOfTransport> meanOfTransports = new ArrayList<>();
    Arrays.asList(split).forEach(s -> meanOfTransports.add(MeanOfTransport.valueOf(s)));
    return meanOfTransports;
  }

  public LocalDateTime localDateFromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
  }

}
