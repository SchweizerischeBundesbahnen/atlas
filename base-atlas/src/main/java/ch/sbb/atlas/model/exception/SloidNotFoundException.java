package ch.sbb.atlas.model.exception;

public class SloidNotFoundException extends NotFoundException {

  public SloidNotFoundException(String sloid) {
    super("sloid", sloid);
  }
}


