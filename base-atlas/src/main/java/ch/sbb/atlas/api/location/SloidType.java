package ch.sbb.atlas.api.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SloidType {
  AREA("area_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix;
    }
  }, //ch:1:sloid:9994:5 (area and prm)
  EDGE("edge_seq") {
    @Override
    public String getSloidPrefix(String sloidPrefix) {
      return sloidPrefix + ":0";
    }
  }; // ch:1:sloid:1232:5:2 (haltekante)

  private final String seqName;

  public abstract String getSloidPrefix(String sloidPrefix);
}
