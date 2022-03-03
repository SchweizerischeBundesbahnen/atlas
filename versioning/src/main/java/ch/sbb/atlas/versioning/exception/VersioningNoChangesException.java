package ch.sbb.atlas.versioning.exception;

public class VersioningNoChangesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public VersioningNoChangesException() {
    super("No entities were modified after versioning execution.");
  }

}
