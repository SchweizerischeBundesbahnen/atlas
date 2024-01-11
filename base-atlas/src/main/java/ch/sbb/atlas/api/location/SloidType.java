package ch.sbb.atlas.api.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SloidType {
  /** ex. ch:1:sloid:7000:5 */
  AREA("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  },
  /** ex. ch:1:sloid:7000:5:900 */
  EDGE("edge_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix + ":0";
    }
  };

  private final String seqName;

  public abstract String getSloidPrefix(String sloidPrefix);
}
