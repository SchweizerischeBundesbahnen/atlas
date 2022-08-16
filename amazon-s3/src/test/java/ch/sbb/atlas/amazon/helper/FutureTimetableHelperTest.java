package ch.sbb.atlas.amazon.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * To check which day of the year should be the Actual Future Timetable
 * see <a href="https://www.fahrplanfelder.ch/en/explanations/timetable-year.html">Timetable year</a>
 */
public class FutureTimetableHelperTest {

  @Test
  public void shouldGetActualFutureTimetableDateForYear2022_12_9() {
    //given
    LocalDate futureTimetableDate2022 = LocalDate.of(2022, 12, 11);
    //when
    LocalDate result = FutureTimetableHelper.getFutureTimetableDate(LocalDate.of(2022, 12, 9));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2022);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2022_12_10() {
    //given
    LocalDate futureTimetableDate2022 = LocalDate.of(2022, 12, 11);
    //when
    LocalDate result = FutureTimetableHelper.getFutureTimetableDate(LocalDate.of(2022, 12, 10));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2022);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2022_12_11() {
    //given
    LocalDate futureTimetableDate2023 = LocalDate.of(2023, 12, 10);
    //when
    LocalDate result = FutureTimetableHelper.getFutureTimetableDate(LocalDate.of(2022, 12, 11));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2023);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2022_12_12() {
    //given
    LocalDate futureTimetableDate2023 = LocalDate.of(2023, 12, 10);
    //when
    LocalDate result = FutureTimetableHelper.getFutureTimetableDate(LocalDate.of(2022, 12, 12));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2023);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2021() {
    //given
    LocalDate futureTimetableDate2021 = LocalDate.of(2021, 12, 12);
    //when
    LocalDate result = FutureTimetableHelper.getActualFutureTimetableDate(
        LocalDate.now().withYear(2021));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2021);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2022() {
    //given
    LocalDate futureTimetableDate2021 = LocalDate.of(2022, 12, 11);
    //when
    LocalDate result = FutureTimetableHelper.getActualFutureTimetableDate(
        LocalDate.now().withYear(2022));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2021);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2023() {
    //given
    LocalDate futureTimetableDate2021 = LocalDate.of(2023, 12, 10);
    //when
    LocalDate result = FutureTimetableHelper.getActualFutureTimetableDate(
        LocalDate.now().withYear(2023));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2021);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2024() {
    //given
    LocalDate futureTimetableDate2021 = LocalDate.of(2024, 12, 15);
    //when
    LocalDate result = FutureTimetableHelper.getActualFutureTimetableDate(
        LocalDate.now().withYear(2024));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2021);
  }

  @Test
  public void shouldGetActualFutureTimetableDateForYear2025() {
    //given
    LocalDate futureTimetableDate2021 = LocalDate.of(2025, 12, 14);
    //when
    LocalDate result = FutureTimetableHelper.getActualFutureTimetableDate(
        LocalDate.now().withYear(2025));
    //then
    assertThat(result).isEqualTo(futureTimetableDate2021);
  }

}