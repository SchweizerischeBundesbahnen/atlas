package ch.sbb.line.directory.module.ttfn.service.quovadis;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisCsvReader.QuoVadisDataRow;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisDataMapper.TimetableFieldNumberV2.TimetableFieldNumberV2Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class QuoVadisDataMapper {

  static List<TimetableFieldNumberV2> mapToTimetableFieldNumber(List<QuoVadisDataRow> quoVadisData) {
    List<String> occurredErrors = new ArrayList<>();

    Map<String, List<QuoVadisDataRow>> dataPerNumber = quoVadisData.stream()
        .collect(Collectors.groupingBy(QuoVadisDataRow::getNumber));

    List<TimetableFieldNumberV2> timetableFieldNumbers = new ArrayList<>();
    dataPerNumber.forEach((number, data) -> {
      boolean hasDifferentMoT = data.stream().map(QuoVadisDataRow::getMeanOfTransport).distinct().count() != 1;
      if (hasDifferentMoT) {
        log.error("{} has different mot in different lines!", number);
        occurredErrors.add(number + " has different mot in different lines!");
        return;
      }

      List<String> descriptionOnward = getDescription(data, "H");
      List<String> descriptionReturn = getDescription(data, "R");

      Optional<MeanOfTransport> meanOfTransport = Arrays.stream(MeanOfTransport.values())
          .filter(i -> i.getDesignationDe().equals(data.getFirst().getMeanOfTransport())).findFirst();
      if (meanOfTransport.isEmpty()) {
        log.error("{} has invalid mot!", number);
        occurredErrors.add(number + " has invalid mot!");
        return;
      }
      TimetableFieldNumberV2Builder timetableFieldNumber = TimetableFieldNumberV2.builder()
          .number(number)
          .meanOfTransport(meanOfTransport.get());

      if (!descriptionOnward.isEmpty()) {
        timetableFieldNumber.descriptionOutwardLine1(descriptionOnward.getFirst().trim());
      }
      if (descriptionOnward.size() > 1) {
        timetableFieldNumber.descriptionOutwardLine2(descriptionOnward.get(1).trim());
      }
      if (descriptionOnward.size() > 2) {
        timetableFieldNumber.descriptionOutwardLine3(descriptionOnward.get(2).trim());
      }

      if (!descriptionReturn.isEmpty()) {
        timetableFieldNumber.descriptionReturnLine1(descriptionReturn.getFirst().trim());
      }
      if (descriptionReturn.size() > 1) {
        timetableFieldNumber.descriptionReturnLine2(descriptionReturn.get(1).trim());
      }
      if (descriptionReturn.size() > 2) {
        timetableFieldNumber.descriptionReturnLine3(descriptionReturn.get(2).trim());
      }

      timetableFieldNumbers.add(timetableFieldNumber.build());
    });

    if (!occurredErrors.isEmpty()) {
      throw new IllegalStateException("There were " + occurredErrors.size() +
          " errors during mapping! Canceling further processing");
    }
    return timetableFieldNumbers;
  }

  static List<String> getDescription(List<QuoVadisDataRow> data, String direction) {
    List<QuoVadisDataRow> dataRows = data.stream().filter(i -> direction.equals(i.getDirection())).toList();
    if (dataRows.size() != 1) {
      return Collections.emptyList();
    }
    QuoVadisDataRow dataRow = dataRows.getFirst();
    return getDescription(dataRow);
  }

  static List<String> getDescription(QuoVadisDataRow dataRow) {
    int expectedAmountOfDescriptions = dataRow.getRowCount().split("\\|").length;
    String[] descriptions = dataRow.getDescription().split("\\|");

    // only split on first expectedAmount of pipes
    if (expectedAmountOfDescriptions == 1) {
      return List.of(dataRow.getDescription());
    }
    List<String> descriptionList = new ArrayList<>(Arrays.asList(descriptions).subList(0, expectedAmountOfDescriptions - 1));
    descriptionList.add(String.join("|", Arrays.asList(descriptions).subList(expectedAmountOfDescriptions - 1,
        descriptions.length)));
    return descriptionList;
  }

  @Builder
  @Data
  static class TimetableFieldNumberV2 {

    private String number;

    private MeanOfTransport meanOfTransport;

    private String descriptionOutwardLine1;

    private String descriptionOutwardLine2;

    private String descriptionOutwardLine3;

    private String descriptionReturnLine1;

    private String descriptionReturnLine2;

    private String descriptionReturnLine3;

  }

}
