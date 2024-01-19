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
  };

  private final String seqName;

  public abstract String getSloidPrefix(String sloidPrefix);
}
