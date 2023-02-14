package ch.sbb.atlas.servicepointdirectory.geodata.transformer;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Command {

  /**
   * MoveTo: 1. (2 parameters follow)
   */
  public static final int MOVE_TO = 1;

  /**
   * LineTo: 2. (2 parameters follow)
   */
  public static final int LINE_TO = 2;

}
