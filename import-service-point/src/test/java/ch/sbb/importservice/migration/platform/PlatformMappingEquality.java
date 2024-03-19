package ch.sbb.importservice.migration.platform;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanIntegerAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.export.StringUtils;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.importservice.migration.MigrationUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    else {
      assertThat(atlasCsvLine.getBoardingDevice()).isNull();
    }
    if(atlasCsvLine.getAdditionalInformation() != null && didokCsvLine.getInfos() != null){
      assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(StringUtils.removeNewLine(didokCsvLine.getInfos()));
    }
    else {
      assertThat(didokCsvLine.getInfos()).isNull();
      assertThat(atlasCsvLine.getAdditionalInformation()).isNull();
    }
    if (atlasCsvLine.getAdviceAccessInfo() != null && didokCsvLine.getAccessInfo() != null) {
      assertThat(atlasCsvLine.getAdviceAccessInfo()).isEqualTo(StringUtils.removeNewLine(didokCsvLine.getAccessInfo()));
    }
    else {
      assertThat(atlasCsvLine.getAdviceAccessInfo()).isNull();
    }
    if (atlasCsvLine.getContrastingAreas() != null && didokCsvLine.getContrastingAreas() != null) {
      assertThat(atlasCsvLine.getContrastingAreas()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getContrastingAreas()).toString());
    }
    else {
      assertThat(atlasCsvLine.getContrastingAreas()).isNull();
    }
    if (atlasCsvLine.getDynamicAudio() != null && didokCsvLine.getDynamicAudio() != null) {
      assertThat(atlasCsvLine.getDynamicAudio()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicAudio()).toString());
    }
    else {
      assertThat(atlasCsvLine.getDynamicAudio()).isNull();
    }
    if (atlasCsvLine.getDynamicVisual() != null && didokCsvLine.getDynamicVisual() != null) {
      assertThat(atlasCsvLine.getDynamicVisual()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getDynamicVisual()).toString());
    }
    else {
      assertThat(atlasCsvLine.getDynamicVisual()).isNull();
    }
    if (atlasCsvLine.getHeight() != null && didokCsvLine.getHeight() != null) {
      assertThat(atlasCsvLine.getHeight()).isEqualTo(didokCsvLine.getHeight());
    }
    else {
      assertThat(atlasCsvLine.getHeight()).isNull();
    }
    if (atlasCsvLine.getInclination() != null && didokCsvLine.getInclination() != null) {
      assertThat(atlasCsvLine.getInclination()).isEqualTo(
          didokCsvLine.getInclination());
    }
    else {
      assertThat(atlasCsvLine.getInclination()).isNull();
    }
    if (atlasCsvLine.getInclinationLongitudinal() != null && didokCsvLine.getInclinationLong() != null) {
      assertThat(atlasCsvLine.getInclinationLongitudinal()).isEqualTo(
          didokCsvLine.getInclinationLong());
    }
    else {
      assertThat(atlasCsvLine.getInclinationLongitudinal()).isNull();
    }
    if (atlasCsvLine.getInclinationWidth() != null && didokCsvLine.getInclinationWidth() != null) {
      assertThat(atlasCsvLine.getInclinationWidth()).isEqualTo(
          didokCsvLine.getInclinationWidth());
    }
    else {
      assertThat(atlasCsvLine.getInclinationWidth()).isNull();
    }
    if (atlasCsvLine.getLevelAccessWheelchair() != null && didokCsvLine.getLevelAccessWheelchair() != null) {
      assertThat(atlasCsvLine.getLevelAccessWheelchair()).isEqualTo(
          StandardAttributeType.from(didokCsvLine.getLevelAccessWheelchair()).toString());
    }
    else {
      assertThat(atlasCsvLine.getLevelAccessWheelchair()).isNull();
    }
    if (atlasCsvLine.getPartialElevation() != null && didokCsvLine.getPartialElev() != null) {
      assertThat(atlasCsvLine.getPartialElevation()).isEqualTo(
              BooleanIntegerAttributeType.of(didokCsvLine.getPartialElev()));
    }
    else {
      assertThat(atlasCsvLine.getPartialElevation()).isNull();
    }
    if (atlasCsvLine.getSuperElevation() != null && didokCsvLine.getSuperelevation() != null) {
      assertThat(atlasCsvLine.getSuperElevation()).isEqualTo(
              didokCsvLine.getSuperelevation());
    }
    else {
      assertThat(atlasCsvLine.getSuperElevation()).isNull();
    }
    if (atlasCsvLine.getTactileSystems() != null && didokCsvLine.getTactileSystems() != null) {
      assertThat(atlasCsvLine.getTactileSystems()).isEqualTo(
              StandardAttributeType.from(didokCsvLine.getTactileSystems()).toString());
    }
    else {
      assertThat(atlasCsvLine.getTactileSystems()).isNull();
    }
    if (atlasCsvLine.getVehicleAccess() != null && didokCsvLine.getVehicleAccess() != null) {
      assertThat(atlasCsvLine.getVehicleAccess()).isEqualTo(
              VehicleAccessAttributeType.of(didokCsvLine.getVehicleAccess()).toString());
    }
    else {
      assertThat(atlasCsvLine.getVehicleAccess()).isNull();
    }
    if (atlasCsvLine.getWheelChairAreaLength() != null && didokCsvLine.getWheelchairAreaLength() != null) {
      assertThat(atlasCsvLine.getWheelChairAreaLength()).isEqualTo(
              didokCsvLine.getWheelchairAreaLength());
    }
    else {
      assertThat(atlasCsvLine.getWheelChairAreaLength()).isNull();
    }
    if (atlasCsvLine.getWheelChairAreaWidth() != null && didokCsvLine.getWheelchairAreaWidth() != null) {
      assertThat(atlasCsvLine.getWheelChairAreaWidth()).isEqualTo(
              didokCsvLine.getWheelchairAreaWidth());
    }
    else {
      assertThat(atlasCsvLine.getWheelChairAreaWidth()).isNull();
    }
    if(atlasCsvLine.getInfoOpportunities() != null && didokCsvLine.getInfoBlinds() != null){
      assertThat(mapPipedInfoOpportunities(atlasCsvLine.getInfoOpportunities())).containsAll(InfoOpportunityAttributeType.fromCode(didokCsvLine.getInfoBlinds()));
    }
    else {
      assertThat(atlasCsvLine.getInfoOpportunities()).isNull();
    }

    assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
    if (didokCsvLine.getModifiedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
      assertThat(localDateFromString(atlasCsvLine.getEditionDate()).toLocalDate())
          .isEqualTo(PlatformMigrationActualDateIntegrationTest.ACTUAL_DATE);
    } else {
      assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());
    }
    assertThat(atlasCsvLine.getStatus()).isEqualTo(Status.VALIDATED);
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
