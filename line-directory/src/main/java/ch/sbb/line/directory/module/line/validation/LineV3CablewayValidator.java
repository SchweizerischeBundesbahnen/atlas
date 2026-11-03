package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import ch.sbb.line.directory.module.line.exception.LineV3CablewayFieldNotAllowedException;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * The cableway fields (Anlagennummer, Anlage-Typ, Anlage-Name) describe a Seilbahn installation. They may only be set if the
 * version carries {@link LineMeanOfTransport#CABLE_RAILWAY}. Applies to lines and members alike, since meansOfTransport is
 * never inherited from the referenced line.
 */
@Service
public class LineV3CablewayValidator {

  public void validateCablewayFields(LineVersionV3 version) {
    if (hasCablewayMeanOfTransport(version)) {
      return;
    }
    Set<String> forbiddenFields = new TreeSet<>();
    if (StringUtils.isNotBlank(version.getCablewayNumber())) {
      forbiddenFields.add(Fields.cablewayNumber);
    }
    if (StringUtils.isNotBlank(version.getCablewayType())) {
      forbiddenFields.add(Fields.cablewayType);
    }
    if (StringUtils.isNotBlank(version.getCablewayName())) {
      forbiddenFields.add(Fields.cablewayName);
    }
    if (!forbiddenFields.isEmpty()) {
      throw new LineV3CablewayFieldNotAllowedException(forbiddenFields);
    }
  }

  private static boolean hasCablewayMeanOfTransport(LineVersionV3 version) {
    Set<LineMeanOfTransport> meansOfTransport = version.getMeansOfTransport();
    return meansOfTransport != null && meansOfTransport.contains(LineMeanOfTransport.CABLE_RAILWAY);
  }

}
