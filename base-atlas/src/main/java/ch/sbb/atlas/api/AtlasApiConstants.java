package ch.sbb.atlas.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasApiConstants {

  public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
  public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final String DATE_TIME_FORMAT_PATTERN_WITH_T = "yyyy-MM-dd'T'HH:mm:ss";

  public static final String ISO_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static final String DATE_TIME_FOR_FILE_FORMAT_PATTERN = "yyyy-MM-dd_HH-mm-ss";

  public static final String DATE_FORMAT_PATTERN_CH = "dd.MM.yyyy";

  public static final int ATLAS_WGS84_MAX_DIGITS = 11;
  public static final int ATLAS_LV_MAX_DIGITS = 5;

  public static final String ZURICH_ZONE_ID = "Europe/Zurich";

}
