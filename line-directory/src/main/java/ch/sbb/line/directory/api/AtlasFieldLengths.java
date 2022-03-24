package ch.sbb.line.directory.api;

public final class AtlasFieldLengths {

  private AtlasFieldLengths() {
    throw new IllegalStateException();
  }

  public static final int SMALL = 50;
  public static final int MID = 255;
  public static final int BUSINESS_IDS = 500;
  public static final int COMMENTS = 1500;
}
