package ch.sbb.atlas.api.location;

public enum SloidType {
  AREA {
    @Override
    public void test() {

    }
  }, //ch:1:sloid:9994:5 (area and prm)
  EDGE {
    @Override
    public void test() {

    }
  }; // ch:1:sloid:1232:5:2 (haltekante)

  public abstract void test();
}
