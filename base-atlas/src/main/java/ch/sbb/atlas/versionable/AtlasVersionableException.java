package ch.sbb.atlas.versionable;

import java.io.Serial;

public class AtlasVersionableException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public AtlasVersionableException(String message) {
    super(message);
  }
}
