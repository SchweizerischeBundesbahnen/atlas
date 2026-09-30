package ch.sbb.atlas.api.lidi.enumaration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Locks the Linienmodell 3.0 enum contracts (ATLAS-3573, decisions D-1, D-3, D-9).
 */
class LineV3EnumsTest {

  private static final Path ENUM_SOURCE_DIR = Path.of("src", "main", "java", "ch", "sbb", "atlas", "api", "lidi", "enumaration");

  @Test
  void shouldHaveExactlyTwelveLineMeansOfTransport() {
    assertThat(LineMeanOfTransport.values()).hasSize(12)
        .containsExactly(
            LineMeanOfTransport.TRAIN,
            LineMeanOfTransport.RACK_RAILWAY,
            LineMeanOfTransport.METRO,
            LineMeanOfTransport.TRAM,
            LineMeanOfTransport.BUS,
            LineMeanOfTransport.CABLE_RAILWAY,
            LineMeanOfTransport.AERIAL_TRAMWAY,
            LineMeanOfTransport.GONDOLA_LIFT,
            LineMeanOfTransport.CHAIRLIFT,
            LineMeanOfTransport.ELEVATOR,
            LineMeanOfTransport.BOAT,
            LineMeanOfTransport.FERRY);
  }

  @Test
  void shouldKeepLineMeanOfTransportIndependentOfOtherMeanOfTransportEnums() throws IOException {
    //when
    String source = Files.readString(ENUM_SOURCE_DIR.resolve("LineMeanOfTransport.java"));

    //then
    assertThat(source).doesNotContain("TtfnMeanOfTransport")
        .doesNotContain("ch.sbb.atlas.servicepoint");
    assertThat(LineMeanOfTransport.class).isNotEqualTo(TtfnMeanOfTransport.class);
  }

  @Test
  void shouldHaveExactlySixConcessionTypesV3() {
    assertThat(ConcessionTypeV3.values()).hasSize(6)
        .containsExactly(
            ConcessionTypeV3.FEDERAL_ZONE_CONCESSION,
            ConcessionTypeV3.CANTONALLY_APPROVED_LINE,
            ConcessionTypeV3.RIGHT_FREE_LINE,
            ConcessionTypeV3.FEDERALLY_LICENSED_LINE,
            ConcessionTypeV3.FEDERALLY_APPROVED_LINE,
            ConcessionTypeV3.STATE_TREATY);
  }

  @Test
  void shouldNotCarryLegacyConcessionTypeLiterals() {
    //given
    List<String> legacyLiterals = List.of(
        "LINE_OF_A_ZONE_CONCESSION",
        "VARIANT_OF_A_LICENSED_LINE",
        "COLLECTION_LINE",
        "NOT_LICENSED_UNPUBLISHED_LINE",
        "FEDERALLY_LICENSED_OR_APPROVED_LINE",
        "LINE_ABROAD");

    //when
    List<String> actualLiterals = Arrays.stream(ConcessionTypeV3.values()).map(Enum::name).toList();

    //then
    assertThat(actualLiterals).isNotEmpty();
    assertThat(actualLiterals).doesNotContainAnyElementsOf(legacyLiterals);
  }

  @Test
  void shouldHaveExactlyTwoLineObjectTypes() {
    assertThat(LineObjectType.values()).hasSize(2)
        .containsExactly(LineObjectType.LINE, LineObjectType.SUBLINE);
  }

  @Test
  void shouldHaveExactlyFourFinancingValuesWithoutOwn() {
    //then
    assertThat(Financing.values()).hasSize(4)
        .containsExactly(Financing.RPV, Financing.WBV, Financing.SELF_FINANCED, Financing.LONG_DISTANCE_SERVICES);
    assertThat(Arrays.stream(Financing.values()).map(Enum::name)).doesNotContain("OWN");
  }

  @Test
  void shouldHaveExactlySevenLineTypesV3WithoutArea() {
    //then
    assertThat(LineTypeV3.values()).hasSize(7)
        .containsExactly(
            LineTypeV3.ORDERLY,
            LineTypeV3.DISPOSITION,
            LineTypeV3.TEMPORARY,
            LineTypeV3.OPERATIONAL,
            LineTypeV3.TECHNICAL,
            LineTypeV3.CONCESSION,
            LineTypeV3.ZONE);
    assertThat(Arrays.stream(LineTypeV3.values()).map(Enum::name)).doesNotContain("AREA");
  }

  @Test
  void shouldKeepLegacyConcessionTypeEnumsAvailableForV2() {
    //then
    assertThat(LineConcessionType.values()).isNotEmpty();
    assertThat(SublineConcessionType.values()).isNotEmpty();
    assertThat(LineConcessionType.COLLECTION_LINE).isNotNull();
  }

}
