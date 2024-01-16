package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public class Sloid {

  private final String value;
  private final ServicePointNumber servicePointNumber;

  public Sloid(String value) {
    this.value = value;

    try {
      List<String> identifiers = Stream.of(this.value.split(":")).skip(3).toList();
      String servicePointPart = identifiers.get(0);
      int number = Integer.parseInt(servicePointPart);
      if (servicePointPart.length() >= 7) {
        this.servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(number);
      } else {
        this.servicePointNumber = ServicePointNumber.of(Country.SWITZERLAND, number);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Not a valid sloid, could not determine servicePointNumber of sloid=" + value, e);
    }
  }

}
