package ch.sbb.prm.directory;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.entity.ToiletVersion.ToiletVersionBuilder;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToiletTestData {

  public static ToiletVersion getToiletVersion() {
    return ToiletVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .wheelchairToilet(StandardAttributeType.NO)
        .additionalInformation("Additional information")
        .build();
  }

  public static ToiletVersionBuilder<?, ?> builderVersion1() {
    return ToiletVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .wheelchairToilet(StandardAttributeType.NO)
        .additionalInformation("Additional information");
  }

  public static ToiletVersionBuilder<?, ?> builderVersion2() {
    return ToiletVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation Napoli")
        .wheelchairToilet(StandardAttributeType.NO)
        .additionalInformation("Additional information");
  }

  public static ToiletVersionBuilder<?, ?> builderVersion3() {
    return ToiletVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation bern")
        .wheelchairToilet(StandardAttributeType.NO)
        .additionalInformation("Additional information");
  }

  public static ToiletVersionModel getToiletVersionModel() {
    return ToiletVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .wheelchairToilet(StandardAttributeType.NO)
        .additionalInformation("Additional information")
        .build();
  }

}
