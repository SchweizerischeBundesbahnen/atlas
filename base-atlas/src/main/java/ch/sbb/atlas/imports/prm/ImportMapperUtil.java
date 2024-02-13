package ch.sbb.atlas.imports.prm;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImportMapperUtil {

  public StandardAttributeType mapStandardAttributeType(Integer standardAttributeTypeCode) {
    return standardAttributeTypeCode != null ? StandardAttributeType.from(standardAttributeTypeCode) : null;
  }

}
