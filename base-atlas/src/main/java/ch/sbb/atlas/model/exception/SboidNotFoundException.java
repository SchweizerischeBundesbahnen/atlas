package ch.sbb.atlas.model.exception;

public class SboidNotFoundException extends NotFoundException {

  public SboidNotFoundException(String value) {
    super("sboid", value);
  }
}
