package ch.sbb.atlas.imports.prm.stoppoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StopPointUtilTest {

  @Test
  void shouldReturnSortedTransportationMeansBT(){
    //given
    String transportationMeans = "~T~B~";
    //when
    String result = StopPointUtil.sortTransportationMeans(transportationMeans);
    //then
    assertThat(result).isEqualTo("~B~T~");
  }

  @Test
  void shouldReturnSortedTransportationMeansT(){
    //given
    String transportationMeans = "~T~";
    //when
    String result = StopPointUtil.sortTransportationMeans(transportationMeans);
    //then
    assertThat(result).isEqualTo("~T~");
  }
}