package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Former DIDOK Code:
 * <p>
 * Format: ## ##### #
 * 2 stelliger Ländercode
 * 5 stellige Nummer/Dienststellen-ID
 * eine Prüfziffer (Checkdigit)
 * <p>
 * Die Prüfziffer errechnet sich aus der Dienststellen-ID und soll "Zahlendreher verhindern"
 */
@ToString
@EqualsAndHashCode
@Getter
@Slf4j
@RequiredArgsConstructor
public final class ServicePointNumber {

  private static final int LENGTH = 8;
  private static final int TEN = 10;

  private final int value;

  @NotNull(message = "Given Country of ServicePointNumber could not be matched")
  public Country getCountry() {
    return Country.from(getNumericPart(0, 2));
  }

  @NotNull
  public Integer getServicePointId() {
    return getNumericPart(2, LENGTH - 1);
  }

  @NotNull
  public Integer getCheckDigit() {
    return getNumericPart(LENGTH - 1, LENGTH);
  }

  public static ServicePointNumber of(int number) {
    return new ServicePointNumber(number);
  }

  public static ServicePointNumber of(Country country, int servicePointId) {
    String formattedId = String.format("%05d", servicePointId);
    return ServicePointNumber.fromString(country.getUicCode() + formattedId + calculateCheckDigit(formattedId));
  }

  @AssertTrue
  boolean isEightDigitsLong() {
    return asString().length() == LENGTH;
  }

  private String asString() {
    return String.valueOf(value);
  }

  private Integer getNumericPart(int from, int to) {
    try {
      return Integer.parseInt(asString().substring(from, to));
    } catch (Exception e) {
      log.debug("Could not parse getNumericPart of ServicePointNumber", e);
      return null;
    }
  }

  private static ServicePointNumber fromString(String number) {
    return ServicePointNumber.of(Integer.parseInt(number));
  }

  private static int calculateCheckDigit(String servicePointId) {
    int initialChecksum = 0;
    for (int i = 0; i < servicePointId.length(); i++) {
      int nthDigit = getNthDigit(servicePointId, i);
      if (i % 2 == 0) {
        nthDigit *= 2;
      }
      initialChecksum += calculateChecksum(nthDigit);
    }
    return (TEN - (initialChecksum % TEN)) % TEN;
  }

  private static int getNthDigit(String value, int n) {
    return Integer.parseInt(value.substring(n, n + 1));
  }

  private static int calculateChecksum(int i) {
    return i % TEN + (i > 0 ? calculateChecksum(i / TEN) : 0);
  }

}
