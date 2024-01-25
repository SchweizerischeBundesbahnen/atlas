package ch.sbb.importservice.migration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.*;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public record PlatformMappingEquality(PlatformCsvModel didokCsvLine, PlatformVersionCsvModel atlasCsvLine) {

  public static final String PIPE_SEPARATOR = "\\|";

  public void performCheck() {
    assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
    assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
    if (atlasCsvLine.getParentSloidServicePoint() != null && didokCsvLine.getDsSloid() != null) {
      assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(
          didokCsvLine.getDsSloid());
    }
    if (atlasCsvLine.getBoardingDevice() != null && didokCsvLine.getBoardingDevice() != null) {
      assertThat(atlasCsvLine.getBoardingDevice()).isEqualTo(
          BoardingDeviceAttributeType.of(didokCsvLine.getBoardingDevice()).toString());
    }
    if(atlasCsvLine.getAdditionalInformation() != null && didokCsvLine.getInfos() != null){
      String didokInfos = didokCsvLine.getInfos().replaceAll("\r\n|\r|\n", " ");
      assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(didokInfos);
    }
    if (atlasCsvLine.getAdviceAccessInfo() != null && didokCsvLine.getAccessInfo() != null) {
      assertThat(atlasCsvLine.getAdviceAccessInfo()).isEqualTo(didokCsvLine.getAccessInfo());
    }
    if (atlasCsvLine.getContrastingAreas() != null && didokCsvLine.getContrastingAreas() != null) {
      assertThat(atlasCsvLine.getContrastingAreas()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getContrastingAreas()).toString());
    }
    if (atlasCsvLine.getDynamicAudio() != null && didokCsvLine.getDynamicAudio() != null) {
      assertThat(atlasCsvLine.getDynamicAudio()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicAudio()).toString());
    }
    if (atlasCsvLine.getDynamicVisual() != null && didokCsvLine.getDynamicVisual() != null) {
      assertThat(atlasCsvLine.getDynamicVisual()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicVisual()).toString());
    }
    if (atlasCsvLine.getHeight() != null && didokCsvLine.getHeight() != null) {
      assertThat(atlasCsvLine.getHeight()).isEqualTo(didokCsvLine.getHeight());
    }
    if (atlasCsvLine.getInclination() != null && didokCsvLine.getInclination() != null) {
      assertThat(atlasCsvLine.getInclination()).isEqualTo(
          didokCsvLine.getInclination());
    }
    if (atlasCsvLine.getInclinationLongitudal() != null && didokCsvLine.getInclinationLong() != null) {
      assertThat(atlasCsvLine.getInclinationLongitudal()).isEqualTo(
          didokCsvLine.getInclinationLong());
    }
    if (atlasCsvLine.getInclinationWidth() != null && didokCsvLine.getInclinationWidth() != null) {
      assertThat(atlasCsvLine.getInclinationWidth()).isEqualTo(
          didokCsvLine.getInclinationWidth());
    }
    if (atlasCsvLine.getLevelAccessWheelchair() != null && didokCsvLine.getLevelAccessWheelchair() != null) {
      assertThat(atlasCsvLine.getLevelAccessWheelchair()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getLevelAccessWheelchair()).toString());
    }
    if (atlasCsvLine.getPartialElevation() != null && didokCsvLine.getPartialElev() != null) {
      assertThat(atlasCsvLine.getPartialElevation()).isEqualTo(
              BooleanIntegerAttributeType.of(didokCsvLine.getPartialElev()));
    }
    if (atlasCsvLine.getSuperElevation() != null && didokCsvLine.getSuperelevation() != null) {
      assertThat(atlasCsvLine.getSuperElevation()).isEqualTo(
              didokCsvLine.getSuperelevation());
    }
    if (atlasCsvLine.getTactileSystems() != null && didokCsvLine.getTactileSystems() != null) {
      assertThat(atlasCsvLine.getTactileSystems()).isEqualTo(
              StandardAttributeType.from(didokCsvLine.getTactileSystems()).toString());
    }
    if (atlasCsvLine.getVehicleAccess() != null && didokCsvLine.getVehicleAccess() != null) {
      assertThat(atlasCsvLine.getVehicleAccess()).isEqualTo(
              VehicleAccessAttributeType.of(didokCsvLine.getVehicleAccess()).toString());
    }
    if (atlasCsvLine.getWheelchairAreaLength() != null && didokCsvLine.getWheelchairAreaLength() != null) {
      assertThat(atlasCsvLine.getWheelchairAreaLength()).isEqualTo(
              didokCsvLine.getWheelchairAreaLength());
    }
    if (atlasCsvLine.getWheelChairAreaWidth() != null && didokCsvLine.getWheelchairAreaWidth() != null) {
      assertThat(atlasCsvLine.getWheelChairAreaWidth()).isEqualTo(
              didokCsvLine.getWheelchairAreaWidth());
    }
    if(atlasCsvLine.getInfoOpportunities() != null && didokCsvLine.getInfoBlinds() != null){
      assertThat(mapPipedInfoOpportunities(atlasCsvLine.getInfoOpportunities())).containsAll(InfoOpportunityAttributeType.fromCode(didokCsvLine.getInfoBlinds()));
    }

    assertThat(atlasCsvLine.getCreator()).isEqualTo(didokCsvLine.getAddedBy());
    assertThat(atlasCsvLine.getEditor()).isEqualTo(didokCsvLine.getModifiedBy());
    assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
    assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

  }

  private List<InfoOpportunityAttributeType> mapPipedInfoOpportunities(String infoOpportunities){
    if(infoOpportunities != null){
      String[] split = infoOpportunities.split(PIPE_SEPARATOR);
      List<InfoOpportunityAttributeType> meanOfTransports =  new ArrayList<>();
      Arrays.asList(split).forEach(s -> meanOfTransports.add(InfoOpportunityAttributeType.valueOf(s)));
      return meanOfTransports;
    }
    return null;
  }

  public LocalDateTime localDateFromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
  }

}
