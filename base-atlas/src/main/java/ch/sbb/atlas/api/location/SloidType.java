package ch.sbb.atlas.api.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SloidType {
  /** ex. ch:1:sloid:7000 */
  SERVICE_POINT,
  /** ex. ch:1:sloid:7000:5 */
  AREA,
  /** ex. ch:1:sloid:7000:5:900 */
  PLATFORM,
  /** PRM ex. ch:1:sloid:7000:5 */
  REFERENCE_POINT,
  /** PRM ex. ch:1:sloid:7000:5 */
  TOILET,
  /** PRM ex. ch:1:sloid:7000:5 */
  PARKING_LOT,
  /** PRM ex. ch:1:sloid:7000:5 */
  CONTACT_POINT;

  public static String transformSloidPrefix(SloidType sloidType, String sloidPrefix) {
    return sloidType == PLATFORM ? sloidPrefix + ":0" : sloidPrefix;
  }

}
