package ch.sbb.prm.directory;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion.ReferencePointVersionBuilder;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferencePointTestData {

  public static ReferencePointVersion getReferencePointVersion() {
    return ReferencePointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .designation("designation")
        .additionalInformation("additional")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1:sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .build();
  }

  public static ReferencePointVersionBuilder<?, ?> builderVersion1() {
    return ReferencePointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .designation("designation")
        .additionalInformation("additional")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1:sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM);
  }

  public static ReferencePointVersionBuilder<?, ?> builderVersion2() {
    return ReferencePointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .designation("designation forever")
        .additionalInformation("additional")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1:sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .version(0);
  }

  public static ReferencePointVersionBuilder<?, ?> builderVersion3() {
    return ReferencePointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .designation("designation forever yb")
        .additionalInformation("additional")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1:sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .version(0);
  }

  public static ReferencePointVersionModel getReferencePointVersionModel() {
    return ReferencePointVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .designation("designation")
        .additionalInformation("additional")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1:sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .build();
  }

}
