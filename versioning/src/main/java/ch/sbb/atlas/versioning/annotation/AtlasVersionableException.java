package ch.sbb.atlas.versioning.annotation;

public class AtlasVersionableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AtlasVersionableException(String message) {
    super(message);
  }
}
