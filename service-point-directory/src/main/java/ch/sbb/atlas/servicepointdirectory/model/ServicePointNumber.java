package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ServicePointNumber {

  private final int value;

  public String asString() {
    return String.valueOf(value);
  }

  @AssertTrue
  boolean isEightDigitsLong() {
    return asString().length() == 8;
  }

  @NotNull(message = "Given Country of ServicePointNumber could not be matched")
  public Country getCountry() {
    return Country.from(getNumericPart(0, 2));
  }

  @NotNull
  public Integer getServicePointId() {
    return getNumericPart(2, 7);
  }

  @NotNull
  public Integer getCheckDigit() {
    return getNumericPart(7, 8);
  }

  private Integer getNumericPart(int from, int to) {
    try {
      return Integer.parseInt(asString().substring(from, to));
    } catch (Exception e) {
      return null;
    }
  }

  public static ServicePointNumber of(int number) {
    return new ServicePointNumber(number);
  }

  public static ServicePointNumber fromString(String number) {
    return ServicePointNumber.of(Integer.parseInt(number));
  }

  public static ServicePointNumber of(Country country, int servicePointId) {
    String formattedId = String.format("%05d", servicePointId);
    return ServicePointNumber.fromString(country.getUicCode() + formattedId + calculateCheckDigit(formattedId));
  }

  private static int calculateCheckDigit(String servicePointId) {
    int initialChecksum = calculateChecksum(getNthDigit(servicePointId, 0) * 2) +
        calculateChecksum(getNthDigit(servicePointId, 1)) +
        calculateChecksum(getNthDigit(servicePointId, 2) * 2) +
        calculateChecksum(getNthDigit(servicePointId, 3)) +
        calculateChecksum(getNthDigit(servicePointId, 4) * 2);
    return (10 - (initialChecksum % 10)) % 10;
  }

  private static int getNthDigit(String value, int n) {
    return Integer.parseInt(value.substring(n, n + 1));
  }

  private static int calculateChecksum(int i) {
    return i % 10 + (i > 0 ? calculateChecksum(i / 10) : 0);
  }

}
