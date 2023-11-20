package ch.sbb.atlas.servicepoint;

import static ch.sbb.atlas.api.AtlasFieldLengths.SERVICE_POINT_NUMBER_LENGTH;

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

  private static final int TEN = 10;
  private static final int SEVEN_DIGIT_SPLITTER = 100000;
  public static final String EMPTY_STRING = "";

  @JsonIgnore
  private final int value;

  public static ServicePointNumber ofNumberWithoutCheckDigit(int number) {
    if (String.valueOf(number).length() != SERVICE_POINT_NUMBER_LENGTH) {
      throw new IllegalArgumentException("The number size must be 7![" + number + "]");
    }
    return new ServicePointNumber(number);
  }

  public static ServicePointNumber of(Country country, int servicePointId) {
    if (country.getUicCode() == null) {
      throw new IllegalArgumentException("Country " + country + " does not provide any uicCountryCode!");
    }
    String formattedId = String.format("%05d", servicePointId);
    return ServicePointNumber.fromString(country.getUicCode() + formattedId);
  }

  public static String calculateSloid(ServicePointNumber servicePointNumber) {
    if (Country.SWITZERLAND.getUicCode().equals(servicePointNumber.getUicCountryCode())) {
      return SloidValidation.SLOID_PREFIX + servicePointNumber.getNumberShort();
    }
    if (Country.SLOID_COMPATIBLE_COUNTRY_CODES.contains(servicePointNumber.getUicCountryCode())) {
      return SloidValidation.SLOID_PREFIX + servicePointNumber.getNumber();
    }
    return null;
  }

  /**
   * @deprecated used until Didok CSV File are imported.
   */
  @Deprecated
  public static Integer removeCheckDigit(Integer didokCode) {
    String didokCodeAsString = Integer.toString(didokCode);
    if (didokCodeAsString.length() == SERVICE_POINT_NUMBER_LENGTH) {
      return didokCode;
    }
    return Integer.parseInt(didokCodeAsString.substring(0, didokCodeAsString.length() - 1));
  }

  private static ServicePointNumber fromString(String number) {
    return ServicePointNumber.ofNumberWithoutCheckDigit(Integer.parseInt(number));
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

  @JsonIgnore
  @NotNull(message = "Given Country of ServicePointNumber could not be matched")
  public Country getCountry() {
    return Country.from(getNumericPart(0, 2));
  }

  @NotNull
  @JsonInclude
  @Schema(description = "UicCountryCode, Indicates which country allocated the service point number and is to be interpreted "
      + "organisationally, not territorially.", example = "85")
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
    return getNumericPart(2, SERVICE_POINT_NUMBER_LENGTH);
  }

  @NotNull
  @Schema(description = "Calculated value formed from the numberShort. Range: 0-9", example = "6")
  public Integer getCheckDigit() {
    return calculateCheckDigit(String.format("%05d", getNumberShort()));
  }

  @AssertTrue
  boolean isEightDigitsLong() {
    return asString().length() == SERVICE_POINT_NUMBER_LENGTH;
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

}
