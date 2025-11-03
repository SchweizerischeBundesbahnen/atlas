package ch.sbb.line.directory.module.ttfn.service.quovadis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisCsvReader.QuoVadisDataRow;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisDataMapper.TimetableFieldNumberV2;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuoVadisDataMapperTest {

  @Test
  void shouldExtractDescriptionBySplittingOnPipes() {
    // given
    QuoVadisDataRow row = new QuoVadisDataRow();
    row.setNumber("203");
    row.setDescription("Lausanne - Palézieux - Romont - Fribourg/Freiburg  | (RER Fribourg | Freiburg, Lignes S40, S41)");
    row.setRowCount("0 | 1");

    // when & then
    List<String> description = QuoVadisDataMapper.getDescription(row);
    assertThat(description).hasSize(2);
    assertThat(description.getFirst()).isEqualTo("Lausanne - Palézieux - Romont - Fribourg/Freiburg  ");
    assertThat(description.get(1)).isEqualTo(" (RER Fribourg | Freiburg, Lignes S40, S41)");
  }

  @Test
  void shouldReportErrorOnInvalidNumber() {
    // given
    QuoVadisDataRow rowh = new QuoVadisDataRow();
    rowh.setNumber("10.203 et 204");
    rowh.setMeanOfTransport("Bus");
    rowh.setDescription("Bern - Wohlen");
    rowh.setRowCount("0");
    rowh.setDirection("H");

    QuoVadisDataRow rowr = new QuoVadisDataRow();
    rowr.setNumber("10.203 et 204");
    rowr.setMeanOfTransport("Bus");
    rowr.setDescription("Wohlen - Bern");
    rowr.setRowCount("0");
    rowr.setDirection("R");

    List<QuoVadisDataRow> quoVadisTtfn = List.of(rowh, rowr);

    // when & then
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisTtfn));
  }

  @Test
  void shouldReportErrorOnInvalidMot() {
    // given
    QuoVadisDataRow rowh = new QuoVadisDataRow();
    rowh.setNumber("10.203");
    rowh.setMeanOfTransport("1");
    rowh.setDescription("Bern - Wohlen");
    rowh.setRowCount("0");
    rowh.setDirection("H");

    List<QuoVadisDataRow> quoVadisTtfn = List.of(rowh);

    // when & then
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisTtfn));
  }

  @Test
  void shouldReportDifferentMoT() {
    // given
    QuoVadisDataRow rowh = new QuoVadisDataRow();
    rowh.setNumber("10.203");
    rowh.setMeanOfTransport("Bus");
    rowh.setDescription("Bern - Wohlen");
    rowh.setRowCount("0");
    rowh.setDirection("H");

    QuoVadisDataRow rowr = new QuoVadisDataRow();
    rowr.setNumber("10.203");
    rowr.setMeanOfTransport("Zug");
    rowr.setDescription("Wohlen - Bern");
    rowr.setRowCount("0");
    rowr.setDirection("R");

    List<QuoVadisDataRow> quoVadisTtfn = List.of(rowh, rowr);

    // when & then
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisTtfn));
  }

  @Test
  void shouldMapMinimalData() {
    // given
    QuoVadisDataRow rowh = new QuoVadisDataRow();
    rowh.setNumber("10.203");
    rowh.setMeanOfTransport("Bus");
    rowh.setDescription("Bern - Wohlen");
    rowh.setRowCount("0");
    rowh.setDirection("H");
    rowh.setBusinessOrganisation("11");

    List<QuoVadisDataRow> quoVadisTtfn = List.of(rowh);

    // when & then
    List<TimetableFieldNumberV2> timetableFieldNumberV2 = QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisTtfn);
    assertThat(timetableFieldNumberV2).hasSize(1);

    TimetableFieldNumberV2 expected = TimetableFieldNumberV2.builder()
        .number("10.203")
        .meanOfTransport(TtfnMeanOfTransport.BUS)
        .descriptionOutwardLine1("Bern - Wohlen")
        .businessOrganisationNumber(11)
        .build();
    assertThat(timetableFieldNumberV2.getFirst()).isEqualTo(expected);
  }

  @Test
  void shouldMapMaximalData() {
    // given
    QuoVadisDataRow rowh = new QuoVadisDataRow();
    rowh.setNumber("10.203");
    rowh.setMeanOfTransport("Bus");
    rowh.setDescription("Bern - Wohlen | Line 1 | Wichtige");
    rowh.setRowCount("0 | 1 | 2");
    rowh.setDirection("H");
    rowh.setBusinessOrganisation("11");

    QuoVadisDataRow rowr = new QuoVadisDataRow();
    rowr.setNumber("10.203");
    rowr.setMeanOfTransport("Bus");
    rowr.setDescription("Wohlen - Bern | Line 1 | Wichtige");
    rowr.setRowCount("0 | 1 | 2");
    rowr.setDirection("R");
    rowh.setBusinessOrganisation("11");

    List<QuoVadisDataRow> quoVadisTtfn = List.of(rowh, rowr);

    // when & then
    List<TimetableFieldNumberV2> timetableFieldNumberV2 = QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisTtfn);
    assertThat(timetableFieldNumberV2).hasSize(1);

    TimetableFieldNumberV2 expected = TimetableFieldNumberV2.builder()
        .number("10.203")
        .meanOfTransport(TtfnMeanOfTransport.BUS)
        .descriptionOutwardLine1("Bern - Wohlen")
        .descriptionOutwardLine2("Line 1")
        .descriptionOutwardLine3("Wichtige")
        .descriptionReturnLine1("Wohlen - Bern")
        .descriptionReturnLine2("Line 1")
        .descriptionReturnLine3("Wichtige")
        .businessOrganisationNumber(11)
        .build();
    assertThat(timetableFieldNumberV2.getFirst()).isEqualTo(expected);
  }
}