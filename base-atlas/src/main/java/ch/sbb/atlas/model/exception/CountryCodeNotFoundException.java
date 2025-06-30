package ch.sbb.atlas.model.exception;

public class CountryCodeNotFoundException extends NotFoundException {

  public CountryCodeNotFoundException(String value) {
    super("uic", value);
  }
}
