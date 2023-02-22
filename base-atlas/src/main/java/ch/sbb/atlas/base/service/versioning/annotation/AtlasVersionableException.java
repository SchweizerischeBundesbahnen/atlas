package ch.sbb.atlas.base.service.versioning.annotation;

public class AtlasVersionableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AtlasVersionableException(String message) {
    super(message);
  }
}
