package ch.sbb.timetable.field.number.api;

public final class CharacterSets {

  public static final String ISO_8859_1 = "[\\u0000-\\u00ff]*";
  public static final String NUMERIC = "[0-9]*";
  public static final String NUMERIC_WITH_DOT = "[.0-9]*";
  public static final String SID4PT = "[-.:_0-9a-zA-Z]*";

  private CharacterSets() {
    throw new IllegalStateException();
  }
}
