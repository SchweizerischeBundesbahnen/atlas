package ch.sbb.atlas.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCharacterSetsRegex {

  public static final String ISO_8859_1 = "[\\u0000-\\u00ff]*";
  public static final String NUMERIC_WITH_DOT = "[.0-9]*";
  public static final String ALPHA_NUMERIC = "[0-9a-zA-Z]*";
  public static final String SID4PT = "[-.:_0-9a-zA-Z]*";

  public static final String EMAIL_ADDRESS = "^$|^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

  public static final String ABBREVIATION_PATTERN = "[A-Z0-9]*";

}
