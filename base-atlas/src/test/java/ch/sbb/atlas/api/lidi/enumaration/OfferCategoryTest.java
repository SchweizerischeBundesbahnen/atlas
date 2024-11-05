package ch.sbb.atlas.api.lidi.enumaration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.util.List;
import org.junit.jupiter.api.Test;

class OfferCategoryTest {

  @Test
  void shouldGetOfferCategoryByTrain() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.TRAIN);
    //then
    assertThat(result).hasSize(19);
  }

  @Test
  void shouldGetOfferCategoryByTram() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.TRAM);
    //then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldGetOfferCategoryByMetro() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.METRO);
    //then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldGetOfferCategoryByBus() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.BUS);
    //then
    assertThat(result).hasSize(8);
  }

  @Test
  void shouldGetOfferCategoryByCableRailway() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.CABLE_RAILWAY);
    //then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldGetOfferCategoryByCableCar() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.CABLE_CAR);
    //then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldGetOfferCategoryByChairlift() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.CHAIRLIFT);
    //then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldGetOfferCategoryByElevator() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.ELEVATOR);
    //then
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldGetOfferCategoryByBoat() {
    //when
    List<OfferCategory> result = OfferCategory.from(MeanOfTransport.BOAT);
    //then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldThrowExceptionWhenMeanOfTransportUnknown() {
    //when && //then
    assertThrows(IllegalArgumentException.class, () -> OfferCategory.from(MeanOfTransport.UNKNOWN));
  }

}