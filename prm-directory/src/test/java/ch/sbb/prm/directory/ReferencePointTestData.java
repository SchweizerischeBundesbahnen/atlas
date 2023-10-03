package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointAttributeType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferencePointTestData {

  public static ReferencePointVersion getReferencePointVersion(){
    return ReferencePointVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .designation("designation")
        .mainReferencePoint(true)
        .parentServicePointSloid("ch:1.sloid:12345")
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .build();
  }

}
