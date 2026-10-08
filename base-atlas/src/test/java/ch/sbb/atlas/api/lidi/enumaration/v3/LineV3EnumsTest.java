package ch.sbb.atlas.api.lidi.enumaration.v3;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineV3EnumsTest {

  @Test
  void shouldProvideConcessionTypeValues() {
    //when && then
    assertThat(ConcessionType.values()).containsExactly(ConcessionType.FEDERAL_ZONE_CONCESSION,
        ConcessionType.CANTONALLY_APPROVED_LINE, ConcessionType.RIGHT_FREE_LINE, ConcessionType.FEDERALLY_LICENSED_LINE,
        ConcessionType.FEDERALLY_APPROVED_LINE, ConcessionType.STATE_TREATY);
    assertThat(ConcessionType.valueOf("STATE_TREATY")).isEqualTo(ConcessionType.STATE_TREATY);
  }

  @Test
  void shouldProvideFinancingValues() {
    //when && then
    assertThat(Financing.values()).containsExactly(Financing.RPV, Financing.WBV, Financing.SELF_FINANCED,
        Financing.LONG_DISTANCE_SERVICES);
    assertThat(Financing.valueOf("RPV")).isEqualTo(Financing.RPV);
  }

  @Test
  void shouldProvideLineMeanOfTransportValues() {
    //when && then
    assertThat(LineMeanOfTransport.values()).containsExactly(LineMeanOfTransport.TRAIN, LineMeanOfTransport.RACK_RAILWAY,
        LineMeanOfTransport.METRO, LineMeanOfTransport.TRAM, LineMeanOfTransport.BUS, LineMeanOfTransport.CABLE_RAILWAY,
        LineMeanOfTransport.AERIAL_TRAMWAY, LineMeanOfTransport.GONDOLA_LIFT, LineMeanOfTransport.CHAIRLIFT,
        LineMeanOfTransport.ELEVATOR, LineMeanOfTransport.BOAT, LineMeanOfTransport.FERRY);
    assertThat(LineMeanOfTransport.valueOf("GONDOLA_LIFT")).isEqualTo(LineMeanOfTransport.GONDOLA_LIFT);
  }

  @Test
  void shouldProvideLineObjectTypeValues() {
    //when && then
    assertThat(LineObjectType.values()).containsExactly(LineObjectType.LINE, LineObjectType.SUBLINE);
    assertThat(LineObjectType.valueOf("SUBLINE")).isEqualTo(LineObjectType.SUBLINE);
  }

  @Test
  void shouldProvideLineTypeValues() {
    //when && then
    assertThat(LineType.values()).containsExactly(LineType.ORDERLY, LineType.DISPOSITION, LineType.TEMPORARY,
        LineType.OPERATIONAL, LineType.TECHNICAL, LineType.CONCESSION, LineType.ZONE);
    assertThat(LineType.valueOf("ZONE")).isEqualTo(LineType.ZONE);
  }

}
