package ch.sbb.atlas.api.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SloidType {
  /** ex. ch:1:sloid:7000 */
  SERVICE_POINT(null) {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      throw new IllegalStateException("This method should not be called on type SERVICE_POINT!");
    }
  },
  /** ex. ch:1:sloid:7000:5 */
  AREA("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /** ex. ch:1:sloid:7000:5:900 */
  PLATFORM("edge_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix + ":0";
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  REFERENCE_POINT("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  TOILETTE("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  RELATION("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  PARKING_LOT("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  INFO_DESK("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /**PRM ex. ch:1:sloid:7000:5 */
  TICKET_COUNTER("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  };

  private final String seqName;

  public abstract String getSloidPrefix(String sloidPrefix);

}
