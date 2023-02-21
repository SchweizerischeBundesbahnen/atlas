package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Former DIDOK Code:
 * <p>
 * Format: ## ##### # 2 stelliger Ländercode 5 stellige Nummer/Dienststellen-ID eine Prüfziffer (Checkdigit)
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
  private static final int SEVEN_DIGIT_SPLITTER = 100000;

  @JsonIgnore
  private final int value;

  @JsonIgnore
  @NotNull(message = "Given Country of ServicePointNumber could not be matched")
  public Country getCountry() {
    return Country.from(getNumericPart(0, 2));
  }

  @NotNull
  @JsonInclude
  @Schema(description = "UicCountryCode", example = "85")
  public Integer getUicCountryCode() {
    if (getCountry() == null) {
      return null;
    }
    return getCountry().getUicCode();
  }

  @NotNull
  @JsonInclude
  @Schema(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort. Size: 7",
      example = "8518771")
  public Integer getNumber() {
    if (getCountry() == null || getCountry().getUicCode() == null) {
      return null;
    }
    return getCountry().getUicCode() * SEVEN_DIGIT_SPLITTER + getNumberShort();
  }

  @NotNull
  @Schema(description = "NumberShort - 5 chars identifying number. Range: 1-99.999", example = "18771")
  public Integer getNumberShort() {
    return getNumericPart(2, LENGTH - 1);
  }

  @NotNull
  @Schema(description = "Calculated value formed from the numberShort. Range: 0-9", example = "6")
  public Integer getCheckDigit() {
    return getNumericPart(LENGTH - 1, LENGTH);
  }

  public static ServicePointNumber of(int number) {
    return new ServicePointNumber(number);
  }

  public static ServicePointNumber ofNumberWithoutCheckDigit(int number) {
    if (String.valueOf(number).length() == LENGTH - 1) {
      return of(Country.from(number / SEVEN_DIGIT_SPLITTER), number % SEVEN_DIGIT_SPLITTER);
    }
    return of(number);
  }

  public static ServicePointNumber of(Country country, int servicePointId) {
    String formattedId = String.format("%05d", servicePointId);
    return ServicePointNumber.fromString(country.getUicCode() + formattedId + calculateCheckDigit(formattedId));
  }

  @AssertTrue
  boolean isEightDigitsLong() {
    return asString().length() == LENGTH;
  }

  public String asString() {
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
