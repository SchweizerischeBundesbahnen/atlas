package ch.sbb.atlas.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCharacterSetsRegex {

  public static final String ISO_8859_1 = "[\\u0000-\\u00ff]*";
  public static final String NUMERIC_WITH_DOT = "[.0-9]*";
  public static final String ALPHA_NUMERIC = "[0-9a-zA-Z]*";
  public static final String SID4PT = "[-.:_0-9a-zA-Z]*";

  public static final String EMAIL_ADDRESS = "(^$|(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"
          + "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"
          + "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
          + "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|"
          + "\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.)"
          + "{3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:"
          + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|"
          + "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]))";

}
